package com.linkings.fastpass.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.AcceptAdapter;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.presenter.AcceptPresenter;
import com.linkings.fastpass.ui.interfaces.IAcceptView;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:44
 * Description: TOO
 */

@RuntimePermissions
public class AcceptActivity extends BaseActivity implements IAcceptView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private AcceptPresenter mAcceptPresenter;
    private WifiMgr mWifiMgr;

    /**
     * 与 文件发送方 通信的 线程
     */
    Runnable mUdpServerRuannable;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            if (msg.what == MSG_TO_FILE_SENDER_UI) {
//                ToastUtils.show(getContext(), "进入文件发送列表");
//                NavigatorUtils.toFileSenderListUI(getContext());
//                finishNormal();
//            } else if (msg.what == MSG_TO_SHOW_SCAN_RESULT) {
//                getOrUpdateWifiScanResult();
//                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TO_SHOW_SCAN_RESULT), 1000);
//            }
        }
    };

    @Override
    public void initView() {
        AcceptActivityPermissionsDispatcher.needsWithCheck(this);
    }

    private void init() {
        mWifiMgr = WifiMgr.getInstance(context);
        if (!mWifiMgr.isWifiEnabled()) {//wifi未打开的情况
            mWifiMgr.openWifi();
        }
        getWifiList();
    }

    private void getWifiList() {
        try {
            final List<ScanResult> wifiScanList = mWifiMgr.getWifiScanList();
            AcceptAdapter acceptAdapter = new AcceptAdapter(wifiScanList);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerview.setAdapter(acceptAdapter);
            acceptAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ScanResult scanResult = wifiScanList.get(position);
                    if (mWifiMgr.isNoPasswordWifi(scanResult)) {

                    } else {

                    }

                    //1.连接网络
                    String ssid = Constant.DEFAULT_SSID;
                    ssid = scanResult.SSID;
//                    mWifiMgr.openWifi();
                    //WifiMgr.getInstance(getContext()).addNetwork(WifiMgr.createWifiCfg(ssid, null, WifiMgr.WIFICIPHER_NOPASS));
                    try {
                        mWifiMgr.connectWifi(ssid, "", scanResult);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    //2.发送UDP通知信息到 文件接收方 开启ServerSocketRunnable
//                    mUdpServerRuannable = createSendMsgToServerRunnable(mWifiMgr.getIpAddressFromHotspot());
//                    MyApplication.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initPresenter() {
        mAcceptPresenter = new AcceptPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_accept;
    }


    /**
     * 创建发送UDP消息到 文件接收方 的服务线程
     *
     * @param serverIP
     */
    private Runnable createSendMsgToServerRunnable(final String serverIP) {
        LogUtil.i("receiver serverIp ----->>>" + serverIP);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileSenderServer(serverIP, Constant.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 开启 文件发送方 通信服务 (必须在子线程执行)
     *
     * @param targetIpAddr
     * @param serverPort
     * @throws Exception
     */
    DatagramSocket mDatagramSocket;

    private void startFileSenderServer(String targetIpAddr, int serverPort) throws Exception {
//        Thread.sleep(3*1000);
        // 确保Wifi连接上之后获取得到IP地址
        int count = 0;
        while (targetIpAddr.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(1000);
            targetIpAddr = mWifiMgr.getIpAddressFromHotspot();
            count++;
        }

        // 即使获取到连接的热点wifi的IP地址也是无法连接网络 所以采取此策略
        count = 0;
        while (!WifiMgr.pingIpAddress(targetIpAddr) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(500);
            count++;
        }
        if (mDatagramSocket == null)
            mDatagramSocket = new DatagramSocket(serverPort);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        InetAddress ipAddress = InetAddress.getByName(targetIpAddr);

        //0.发送 即将发送的文件列表 到文件接收方
        sendFileInfoListToFileReceiverWithUdp(serverPort, ipAddress);

        //1.发送 文件接收方 初始化
//        sendData = Constant.MSG_FILE_RECEIVER_INIT.getBytes(BaseTransfer.UTF_8);
//        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);
//        mDatagramSocket.send(sendPacket);

//        sendFileInfoListToFileReceiverWithUdp(serverPort, ipAddress);

        //2.接收 文件接收方 初始化 反馈
//        while (true) {
//            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//            mDatagramSocket.receive(receivePacket);
//            String response = new String(receivePacket.getData(), BaseTransfer.UTF_8).trim();
//            if (response != null && response.equals(Constant.MSG_FILE_RECEIVER_INIT_SUCCESS)) {
//                // 进入文件发送列表界面 （并且通知文件接收方进入文件接收列表界面）
//                mHandler.obtainMessage(MSG_TO_FILE_SENDER_UI).sendToTarget();
//            }
//        }
    }

    /**
     * 发送即将发送的文件列表到文件接收方
     *
     * @param serverPort
     * @param ipAddress
     * @throws IOException
     */
    private void sendFileInfoListToFileReceiverWithUdp(int serverPort, InetAddress ipAddress) throws IOException {
        //1.1将发送的List<FileInfo> 发送给 文件接收方
        //如何将发送的数据列表封装成JSON
//        Map<String, FileInfo> sendFileInfoMap = MyApplication.getInstance().getApplicationContext().getFileInfoMap();
//        List<Map.Entry<String, FileInfo>> fileInfoMapList = new ArrayList<Map.Entry<String, FileInfo>>(sendFileInfoMap.entrySet());
//        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
//        //排序
//        Collections.sort(fileInfoMapList, Constant.DEFAULT_COMPARATOR);
//        for (Map.Entry<String, FileInfo> entry : fileInfoMapList) {
//            if (entry.getValue() != null) {
//                FileInfo fileInfo = entry.getValue();
//                String fileInfoStr = FileInfo.toJsonStr(fileInfo);
//                DatagramPacket sendFileInfoListPacket =
//                        new DatagramPacket(fileInfoStr.getBytes(), fileInfoStr.getBytes().length, ipAddress, serverPort);
//                try {
//                    mDatagramSocket.send(sendFileInfoListPacket);
//                    Log.i(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Success!");
//                } catch (Exception e) {
//                    Log.i(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Failure!");
//                }
//
//            }
//        }
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, AcceptActivity.class);
        srcActivity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AcceptActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needs() {
        LogUtil.i("needs");
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void rationale(final PermissionRequest request) {
        LogUtil.i("rationale");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void denied() {
        LogUtil.i("denied");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAgain() {
        LogUtil.i("neverAgain");
    }
}
