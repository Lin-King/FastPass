package com.linkings.fastpass.presenter;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.model.FileInfoJson;
import com.linkings.fastpass.ui.activity.SendActivity;
import com.linkings.fastpass.ui.activity.SendListActivity;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;
import com.linkings.fastpass.utils.TypeConvertUtil;
import com.linkings.fastpass.widget.WifiAPBroadcastReceiver;
import com.linkings.fastpass.wifitools.ApMgr;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import static com.linkings.fastpass.config.Constant.MSG_FILE_RECEIVER_INIT_SUCCESS;
import static com.linkings.fastpass.config.Constant.UTF8;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:43
 * Description: TOO
 */

public class SendPresenter {

    private WifiAPBroadcastReceiver mWifiAPBroadcastReceiver;
    private SendActivity sendActivity;
    private Runnable mUdpServerRuannable;//与 文件发送方 通信的 线程
    private boolean mIsInitialized;
    private DatagramSocket mDatagramSocket;
    private MyHandler mMyHandler;

    private static class MyHandler extends Handler {
        private WeakReference<SendActivity> activityWeakReference;

        private MyHandler(SendActivity mSendActivity) {
            activityWeakReference = new WeakReference<>(mSendActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            SendActivity mSendActivity = activityWeakReference.get();
            if (mSendActivity != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        SendListActivity.startActivity(mSendActivity);
                        break;
                }
            }
        }
    }

    public SendPresenter(SendActivity sendActivity) {
        this.sendActivity = sendActivity;
        mMyHandler = new MyHandler(sendActivity);
    }

    public void init() {
        if (ApMgr.isApOn(sendActivity)) {
            ApMgr.closeAp(sendActivity);
        }
        mWifiAPBroadcastReceiver = new WifiAPBroadcastReceiver() {
            @Override
            public void onWifiApDisabled() {
                LogUtil.i("======>>>onWifiApDisabled !!!");
                String ssid = TextUtils.isEmpty(Build.DEVICE) ? Constant.DEFAULT_SSID : Build.DEVICE;
                ApMgr.openAp(sendActivity, ssid, "");
            }

            @Override
            public void onWifiApEnabled() {
                LogUtil.i("======>>>onWifiApEnabled !!!");
                if (!mIsInitialized) {
                    mUdpServerRuannable = createSendMsgToFileSenderRunnable();
                    MyApplication.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                    mIsInitialized = true;
                }
            }
        };
        IntentFilter filter = new IntentFilter(WifiAPBroadcastReceiver.ACTION_WIFI_AP_STATE_CHANGED);
        sendActivity.registerReceiver(mWifiAPBroadcastReceiver, filter);
//        if (!ApMgr.isApOn(sendActivity)) {
////            ApMgr.closeAp(sendActivity);
//            mWifiAPBroadcastReceiver.onWifiApDisabled();
//        }
    }

    /**
     * 创建发送UDP消息到 文件发送方 的服务线程
     */
    private Runnable createSendMsgToFileSenderRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileSendServer(Constant.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startFileSendServer(int serverPort) throws Exception {
        //网络连接上，无法获取IP的问题
        int count = 0;
//        String localAddress = intToIp(WifiMgr.getInstance(sendActivity).getWifiManager().getConnectionInfo().getIpAddress());
        String localAddress = WifiMgr.getInstance(sendActivity).getHotspotLocalIpAddress();
        LogUtil.i("receiver get local Ip ----->>>" + localAddress);
        while (localAddress.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(1000);
            localAddress = WifiMgr.getInstance(sendActivity).getHotspotLocalIpAddress();
            LogUtil.i("receiver get local Ip ----->>>" + localAddress);
            count++;
        }
        final String finalLocalAddress = localAddress;
        sendActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(sendActivity, finalLocalAddress);
            }
        });
        if (localAddress.equals("192.168.1.1")) {
            localAddress = "192.168.43.1";
        }
        //这里使用UDP发送和接收指令 
        mDatagramSocket = new DatagramSocket(serverPort);
        while (true) {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            InetAddress ipAddress = InetAddress.getByName(localAddress);
            receivePacket.setAddress(ipAddress);
            mDatagramSocket.receive(receivePacket);
            String response = new String(receivePacket.getData(), UTF8).trim();
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i("接收到的消息 -------->>>" + response);
                switch (response) {
                    case MSG_FILE_RECEIVER_INIT_SUCCESS:
                        //发送文件列表
                        InetAddress inetAddress = receivePacket.getAddress();
                        int port = receivePacket.getPort();
                        //通过UDP发送文件列表给接收端
                        sendFileInfoList(inetAddress, port);
                        break;
                    case Constant.MSG_START_SEND:
                        //开始发送指令
//                    initSenderServer();
                        break;
                    default:
                        //接收端发来的待发送文件列表
//                    parseFileInfo(response);
                        break;
                }
            }
        }
    }

    /**
     * 通过UDP发送文件列表给接收端
     */
    private void sendFileInfoList(InetAddress ipAddress, int serverPort) {
        if (FileInfoMG.getInstance().getListSize() > 0) {
            List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
            List<FileInfo> fileInfos = TypeConvertUtil.toObject(TypeConvertUtil.toJsonStr(new FileInfoJson(fileInfoList)), FileInfoJson.class).getFileInfos();
            for (FileInfo fileInfo : fileInfos) fileInfo.setPic("");
            try {
                FileInfoJson fileInfoJson = new FileInfoJson(fileInfos);
                String jsonStr = TypeConvertUtil.toJsonStr(fileInfoJson);
                DatagramPacket sendFileInfoPacket = new DatagramPacket(jsonStr.getBytes(), jsonStr.getBytes().length, ipAddress, serverPort);
                mDatagramSocket.send(sendFileInfoPacket);
                LogUtil.i("发送消息 --------->>>" + jsonStr + " === Success!");
                mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
//                mMyHandler.obtainMessage(MSG_SET_STATUS, "成功发送文件列表...").sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.i(e.toString());
                LogUtil.i("发送消息 --------->>> 失败！");
            }
        }
    }

    /**
     * 反注册便携热点状态接收器
     */
    public void unregisterHotSpotReceiver() {
        if (mWifiAPBroadcastReceiver != null) {
            sendActivity.unregisterReceiver(mWifiAPBroadcastReceiver);
            mWifiAPBroadcastReceiver = null;
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
