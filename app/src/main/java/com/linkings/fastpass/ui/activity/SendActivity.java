package com.linkings.fastpass.ui.activity;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.linkings.fastpass.R;
import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.presenter.SendPresenter;
import com.linkings.fastpass.ui.interfaces.ISendView;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.widget.WifiAPBroadcastReceiver;
import com.linkings.fastpass.wifitools.ApMgr;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:42
 * Description: TOO
 */

public class SendActivity extends BaseActivity implements ISendView {

    private SendPresenter mSendPresenter;
    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;
    /**
     * 与 文件发送方 通信的 线程
     */
    Runnable mUdpServerRuannable;
    boolean mIsInitialized = false;
    private DatagramSocket mDatagramSocket;

    public static final int MSG_TO_FILE_RECEIVER_UI = 0X88;

    @Override
    public void initView() {
        //Android 6.0 扫描wifi 需要开启定位
        if (Build.VERSION.SDK_INT >= 23) { //Android 6.0 扫描wifi 需要开启定位
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 获取wifi连接需要定位权限,没有获取权限
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                }, 0);
                return;
            }
        }
        if (WifiMgr.getInstance(context).isWifiEnabled()) {//wifi未打开的情况
            WifiMgr.getInstance(context).closeWifi();
        }
        //1.初始化热点
        WifiMgr.getInstance(context).closeWifi();
        if (ApMgr.isApOn(context)) {
            ApMgr.closeAp(context);
        }

        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApEnabled() {
                LogUtil.i("======>>>onWifiApEnabled !!!");
                if (!mIsInitialized) {
                    mUdpServerRuannable = createSendMsgToFileSenderRunnable();
                    MyApplication.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                    mIsInitialized = true;
//                    tv_desc.setText(getResources().getString(R.string.tip_now_init_is_finish));
//                    tv_desc.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            tv_desc.setText(getResources().getString(R.string.tip_is_waitting_connect));
//                        }
//                    }, 2 * 1000);
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        registerReceiver(mWifiAPBroadcastReceiver, filter);
    }

    @Override
    public void initPresenter() {
        mSendPresenter = new SendPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_send;
    }

    /**
     * 创建发送UDP消息到 文件发送方 的服务线程
     */
    private Runnable createSendMsgToFileSenderRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileReceiverServer(Constant.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TO_FILE_RECEIVER_UI) {
//                IpPortInfo ipPortInfo = (IpPortInfo) msg.obj;
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constant.KEY_IP_PORT_INFO, ipPortInfo);
//                NavigatorUtils.toFileReceiverListUI(getContext(), bundle);
//                finishNormal();
            }
        }
    };

    private void startFileReceiverServer(int serverPort) throws Exception {

        //网络连接上，无法获取IP的问题
        int count = 0;
        String localAddress = WifiMgr.getInstance(context).getIpAddressFromHotspot();
        while (localAddress.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(1000);
            localAddress = WifiMgr.getInstance(context).getIpAddressFromHotspot();
            LogUtil.i("receiver get local Ip ----->>>" + localAddress);
            count++;
        }

        mDatagramSocket = new DatagramSocket(serverPort);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        while (true) {
            //1.接收 文件发送方的消息
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            String msg = new String(receivePacket.getData()).trim();
            InetAddress inetAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            if (msg != null && msg.startsWith(Constant.MSG_FILE_RECEIVER_INIT)) {
                LogUtil.i("Get the msg from FileReceiver######>>>" + Constant.MSG_FILE_RECEIVER_INIT);
                // 进入文件接收列表界面 (文件接收列表界面需要 通知 文件发送方发送 文件开始传输UDP通知)
//                mHandler.obtainMessage(MSG_TO_FILE_RECEIVER_UI, new IpPortInfo(inetAddress, port)).sendToTarget();
            } else { //接收发送方的 文件列表
                if (msg != null) {
//                    FileInfo fileInfo = FileInfo.toObject(msg);
                    LogUtil.i("Get the FileInfo from FileReceiver######>>>" + msg);
//                    parseFileInfo(msg);
                }
            }

            //2.反馈 文件发送方的消息
//            sendData = Constant.MSG_FILE_RECEIVER_INIT_SUCCESS.getBytes(BaseTransfer.UTF_8);
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, port);
//            serverSocket.send(sendPacket);
        }
    }

}
