package com.linkings.fastpass.presenter;

import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.AcceptAdapter;
import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.model.FileInfoJson;
import com.linkings.fastpass.ui.activity.AcceptActivity;
import com.linkings.fastpass.ui.activity.ReceiveListActivity;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;
import com.linkings.fastpass.widget.WifiBroadcaseReceiver;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.linkings.fastpass.config.Constant.MSG_FILE_RECEIVER_INIT_SUCCESS;
import static com.linkings.fastpass.config.Constant.MSG_UPDATE_ADAPTER;
import static com.linkings.fastpass.config.Constant.UTF8;

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

    private List<ScanResult> mWifiScanList;
    private AcceptAdapter mAcceptAdapter;
    private boolean firstConnect;
    private String mSelectedSSID = "";

    private MyHandler mMyHandler;

    public AcceptPresenter(AcceptActivity acceptActivity, RecyclerView recyclerview) {
        this.acceptActivity = acceptActivity;
        this.recyclerview = recyclerview;
        mMyHandler = new MyHandler(acceptActivity);
    }


    private static class MyHandler extends Handler {
        private WeakReference<AcceptActivity> activityWeakReference;

        MyHandler(AcceptActivity mAcceptPresenter) {
            activityWeakReference = new WeakReference<>(mAcceptPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            AcceptActivity acceptActivity = activityWeakReference.get();
            if (acceptActivity != null) {
                switch (msg.what) {
                    case MSG_UPDATE_ADAPTER:
                        ReceiveListActivity.startActivity(acceptActivity);
                        break;
                }
            }
        }
    }

    public void init() {
        //开启WiFi，监听WiFi广播
        registerWifiReceiver();
        mWifiMgr = WifiMgr.getInstance(acceptActivity);
        if (mWifiMgr.isWifiEnabled()) clearWifiConfig();
        else mWifiMgr.openWifi();
        mWifiScanList = new ArrayList<>();
        mAcceptAdapter = new AcceptAdapter(mWifiScanList);
        recyclerview.setLayoutManager(new LinearLayoutManager(acceptActivity));
        recyclerview.setAdapter(mAcceptAdapter);
        mAcceptAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final ScanResult scanResult = mWifiScanList.get(position);
                final String[] passwordSSID = {""};
                mSelectedSSID = scanResult.SSID;
                if (!WifiMgr.isNoPasswordWifi(scanResult)) {
                    showDialogWithEditText(mSelectedSSID, new OnWifiPasswordConfirmListener() {
                        @Override
                        public void onConfirm(String password) {
                            if (TextUtils.isEmpty(password)) {
                                ToastUtil.show(acceptActivity, "密码不能为空");
                                return;
                            }
                            passwordSSID[0] = password;
                            try {
                                ToastUtil.show(acceptActivity, "正在连接Wifi...");
                                mWifiMgr.connectWifi(scanResult, passwordSSID[0]);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    try {
                        ToastUtil.show(acceptActivity, "正在连接Wifi...");
                        mWifiMgr.connectWifi(scanResult, passwordSSID[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
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
                    int tryCount = 0;
                    String serverIp = mWifiMgr.getIpAddressFromHotspot();
                    while (serverIp.equals(Constant.DEFAULT_UNKOWN_IP) && tryCount < Constant.DEFAULT_TRY_TIME) {
                        Thread.sleep(1000);
                        serverIp = mWifiMgr.getIpAddressFromHotspot();
                        LogUtil.i("receiver serverIp ----->>>" + serverIp);
                        tryCount++;
                    }
                    tryCount = 0;
                    while (!WifiMgr.pingIpAddress(serverIp) && tryCount < Constant.DEFAULT_TRY_TIME) {
                        Thread.sleep(500);
                        LogUtil.i("Try to ping ------" + serverIp + " - " + tryCount);
                        tryCount++;
                    }

                    final String finalServerIp = serverIp;
                    acceptActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(acceptActivity, finalServerIp);
                        }
                    });

                    //创建UDP通信
                    if (mDatagramSocket == null) {
                        //解决：java.net.BindException: bind failed: EADDRINUSE (Address already in use)
                        mDatagramSocket = new DatagramSocket(null);
                        mDatagramSocket.setReuseAddress(true);
                        mDatagramSocket.bind(new InetSocketAddress(Constant.DEFAULT_SERVER_COM_PORT));
                    }
                    //发送初始化完毕指令
                    InetAddress ipAddress = InetAddress.getByName(serverIp);
                    byte[] sendData = MSG_FILE_RECEIVER_INIT_SUCCESS.getBytes(UTF8);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, Constant.DEFAULT_SERVER_COM_PORT);
                    mDatagramSocket.send(sendPacket);
                    LogUtil.i("发送消息 ------->>>" + MSG_FILE_RECEIVER_INIT_SUCCESS);

                    FileInfoMG.getInstance().cleanFileInfoList();
                    //接收文件列表
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        mDatagramSocket.receive(receivePacket);
                        String response = new String(receivePacket.getData()).trim();
                        if (!TextUtils.isEmpty(response)) {
                            LogUtil.i("接收到的消息 -------->>>" + response);
                            parseFileInfoList(response);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void parseFileInfoList(String jsonStr) {
        if (!TextUtils.isEmpty(jsonStr)) {
            FileInfoJson fileInfoJson = new Gson().fromJson(jsonStr, FileInfoJson.class);
            List<FileInfo> fileInfos = fileInfoJson.getFileInfos();
            if (fileInfos.size() > 0) {
                for (FileInfo fileInfo : fileInfos) {
                    if (fileInfo != null && !TextUtils.isEmpty(fileInfo.getFilePath())) {
//                        FileInfoMG.getInstance().getFileInfoList().add(fileInfo);
                        FileInfoMG.getInstance().addFileInfo(fileInfo);
                    }
                }
                //更新适配器
                mMyHandler.sendEmptyMessage(MSG_UPDATE_ADAPTER);
            }
        }
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
//        mWifiMgr.clearWifiConfig();
        if (mWifiMgr.isWifi(acceptActivity)) mWifiMgr.disconnectWifi(mWifiMgr.getConnectedSSID());
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
            mWifiMgr.startScan();
        }

        @Override
        public void onWifiDisabled() {
            //WiFi已关闭，清除可用WiFi列表
            LogUtil.i("WiFi已关闭，清除可用WiFi列表");
            mWifiMgr.openWifi();
        }

        @Override
        public void onScanResultsAvailable(List<ScanResult> scanResults) {
            //扫描周围可用WiFi成功，设置可用WiFi列表
            LogUtil.i("扫描周围可用WiFi成功，设置可用WiFi列表");
            refresh(scanResults);
        }

        @Override
        public void onWifiConnected(String connectedSSID) {
            if (connectedSSID.equals(mSelectedSSID) && !firstConnect) {
                LogUtil.i("WiFi连接成功");
                ToastUtil.show(acceptActivity, "Wifi连接成功...");
                //发送UDP通知信息到 文件接收方 开启ServerSocketRunnable
                mUdpServerRuannable = createSendMsgToServerRunnable(mWifiMgr.getIpAddressFromHotspot());
                MyApplication.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                firstConnect = true;
            }
        }

        @Override
        public void onWifiDisconnected() {
            firstConnect = false;
        }
    };

    private void refresh(List<ScanResult> scanResults) {
        if (mWifiScanList != null && mAcceptAdapter != null) {
            mWifiScanList.clear();
            mWifiScanList.addAll(scanResults);
            mAcceptAdapter.notifyDataSetChanged();
        }
    }

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

    /**
     * 关闭UDP Socket
     */
    public void closeUdpSocket() {
        if (mDatagramSocket != null) {
            if (!mDatagramSocket.isClosed()) {
                mDatagramSocket.close();
            }
            mDatagramSocket.disconnect();
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
        if (mUdpServerRuannable != null) mUdpServerRuannable = null;
    }

}
