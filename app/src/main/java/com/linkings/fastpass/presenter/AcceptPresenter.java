package com.linkings.fastpass.presenter;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.AcceptAdapter;
import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.ui.activity.AcceptActivity;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;
import com.linkings.fastpass.widget.WifiBroadcaseReceiver;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:45
 * Description: TOO
 */

public class AcceptPresenter {
    private AcceptActivity acceptActivity;
    private RecyclerView recyclerview;
    private WifiMgr mWifiMgr;
    private Runnable mUdpServerRuannable; //与 文件发送方 通信的 线程
    private DatagramSocket mDatagramSocket; //开启 文件发送方 通信服务 (必须在子线程执行)

    public AcceptPresenter(AcceptActivity acceptActivity, RecyclerView recyclerview) {
        this.acceptActivity = acceptActivity;
        this.recyclerview = recyclerview;
    }

    public void init() {
        //开启WiFi，监听WiFi广播
        registerWifiReceiver();
        mWifiMgr = WifiMgr.getInstance(acceptActivity);
        if (!mWifiMgr.isWifiEnabled()) {//wifi未打开的情况
            mWifiMgr.openWifi();
        }
    }

    private void getWifiList() {
        try {
            final List<ScanResult> wifiScanList = mWifiMgr.getWifiScanList();
            AcceptAdapter acceptAdapter = new AcceptAdapter(wifiScanList);
            recyclerview.setLayoutManager(new LinearLayoutManager(acceptActivity));
            recyclerview.setAdapter(acceptAdapter);
            acceptAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    final ScanResult scanResult = wifiScanList.get(position);
                    final String mSelectedSSID = scanResult.SSID;
                    if (!WifiMgr.isNoPasswordWifi(scanResult)) {
                        //弹出密码输入框
                        showDialogWithEditText(mSelectedSSID, new OnWifiPasswordConfirmListener() {
                            @Override
                            public void onConfirm(String password) {
                                //使用密码连接WiFi
                                if (!TextUtils.isEmpty(password)) {
                                    try {
                                        ToastUtil.show(acceptActivity, "正在连接Wifi...");
                                        mWifiMgr.connectWifi(mSelectedSSID, password, scanResult);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ToastUtil.show(acceptActivity, "密码不能为空");
                                }
                            }
                        });
                    } else {
                        //连接免密码WiFi
                        try {
                            ToastUtil.show(acceptActivity, "正在连接Wifi...");
                            mWifiMgr.connectWifi(mSelectedSSID, "", scanResult);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //发送UDP通知信息到 文件接收方 开启ServerSocketRunnable
                    mUdpServerRuannable = createSendMsgToServerRunnable(mWifiMgr.getIpAddressFromHotspot());
                    MyApplication.MAIN_EXECUTOR.execute(mUdpServerRuannable);

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建发送UDP消息到 文件接收方 的服务线程
     */
    private Runnable createSendMsgToServerRunnable(final String serverIP) {
        LogUtil.i("receiver serverIp ----->>>" + serverIP);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    //告知发送端，接收端初始化完毕
                    sendInitSuccessToFileSender();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 告知发送端初始化完毕
     */
    private void sendInitSuccessToFileSender() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //确保WiFi连接后获取正确IP地址
                    int tryCount = 0;
                    String serverIp = mWifiMgr.getIpAddressFromHotspot();
                    while (serverIp.equals(Constant.DEFAULT_UNKOWN_IP) && tryCount < Constant.DEFAULT_TRY_TIME) {
                        Thread.sleep(1000);
                        serverIp = mWifiMgr.getIpAddressFromHotspot();
                        tryCount++;
                    }

                    //是否可以ping通指定IP地址
                    tryCount = 0;
                    while (!WifiMgr.pingIpAddress(serverIp) && tryCount < Constant.DEFAULT_TRY_TIME) {
                        Thread.sleep(500);
                        LogUtil.i("Try to ping ------" + serverIp + " - " + tryCount);
                        tryCount++;
                    }

                    //创建UDP通信
                    if (mDatagramSocket == null) {
                        //解决：java.net.BindException: bind failed: EADDRINUSE (Address already in use)
                        mDatagramSocket = new DatagramSocket(null);
                        mDatagramSocket.setReuseAddress(true);
                        mDatagramSocket.bind(new InetSocketAddress(Constant.DEFAULT_SERVER_COM_PORT));
                    }
                    //发送初始化完毕指令
                    InetAddress ipAddress = InetAddress.getByName(serverIp);
                    byte[] sendData = "MSG_FILE_RECEIVER_INIT_SUCCESS".getBytes("UTF-8");
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, Constant.DEFAULT_SERVER_COM_PORT);
                    mDatagramSocket.send(sendPacket);
                    LogUtil.i("发送消息 ------->>>MSG_FILE_RECEIVER_INIT_SUCCESS");

                    //接收文件列表
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        mDatagramSocket.receive(receivePacket);
                        String response = new String(receivePacket.getData()).trim();
                        if (!TextUtils.isEmpty(response)) {
                            //发送端发来的文件列表
                            LogUtil.i("接收到的消息 -------->>>" + response);
//                            parseFileInfoList(response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 显示WiFi密码输入框
     */
    private void showDialogWithEditText(String title, final OnWifiPasswordConfirmListener listener) {
        View dialogView = View.inflate(acceptActivity, R.layout.dialog_with_edittext, null);
        final EditText etPassword = (EditText) dialogView.findViewById(R.id.et_dialog_with_edittext);

        AlertDialog.Builder builder = new AlertDialog.Builder(acceptActivity);
        builder.setTitle(title);
        builder.setView(dialogView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onConfirm(etPassword.getText().toString().trim());
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public void clearWifiConfig() {
        mWifiMgr.clearWifiConfig();
    }

    private interface OnWifiPasswordConfirmListener {
        void onConfirm(String password);
    }

    /**
     * WiFi广播接收器
     */
    private WifiBroadcaseReceiver mWifiBroadcaseReceiver = new WifiBroadcaseReceiver() {
        @Override
        public void onWifiEnabled() {
            //WiFi已开启，开始扫描可用WiFi
            LogUtil.i("WiFi已开启，开始扫描可用WiFi");
            getWifiList();
        }

        @Override
        public void onWifiDisabled() {
            //WiFi已关闭，清除可用WiFi列表
            LogUtil.i("WiFi已关闭，清除可用WiFi列表");
        }

        @Override
        public void onScanResultsAvailable(List<ScanResult> scanResults) {
            //扫描周围可用WiFi成功，设置可用WiFi列表
            LogUtil.i("扫描周围可用WiFi成功，设置可用WiFi列表");
        }

        @Override
        public void onWifiConnected(String connectedSSID) {
            //判断指定WiFi是否连接成功
            LogUtil.i("WiFi已开启，开始扫描可用WiFi");
//            if (connectedSSID.equals(mSelectedSSID) && !mIsSendInitOrder) {
//                //连接成功
//                setStatus("Wifi连接成功...");
//                //显示发送列表，隐藏WiFi选择列表
//                mChooseHotspotRecyclerView.setVisibility(View.GONE);
//                mReceiveFilesRecyclerView.setVisibility(View.VISIBLE);
//
//                //告知发送端，接收端初始化完毕
//                mHandler.sendEmptyMessage(MSG_FILE_RECEIVER_INIT_SUCCESS);
//                mIsSendInitOrder = true;
//            } else {
////                //连接成功的不是设备WiFi，清除该WiFi，重新扫描周围WiFi
////                LogUtils.e("连接到错误WiFi，正在断开重连...");
////                mWifiMgr.disconnectWifi(connectedSSID);
////                mWifiMgr.startScan();
//            }
        }

        @Override
        public void onWifiDisconnected() {

        }
    };

    /**
     * 注册监听WiFi操作的系统广播
     */
    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        acceptActivity.registerReceiver(mWifiBroadcaseReceiver, filter);
    }

    /**
     * 反注册WiFi相关的系统广播
     */
    public void unregisterWifiReceiver() {
        if (mWifiBroadcaseReceiver != null) {
            acceptActivity.unregisterReceiver(mWifiBroadcaseReceiver);
            mWifiBroadcaseReceiver = null;
        }
    }

}
