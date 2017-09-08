package com.linkings.fastpass.presenter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.adapter.SendListAdapter;
import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.config.FileSender;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.activity.SendListActivity;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.linkings.fastpass.config.Constant.MSG_SET_STATUS;
import static com.linkings.fastpass.config.Constant.MSG_UPDATE_PROGRESS;

/**
 * Created by Lin on 2017/9/7.
 * Time: 15:47
 * Description: TOO
 */

public class SendListPresenter {

    private SendListActivity sendListActivity;
    private WifiMgr mWifiMgr;
    private MyHandler mMyHandler;

    /**
     * 发送文件线程列表数据
     */
    private List<FileSender> mFileSenderList = new ArrayList<>();
    private SendListAdapter mSendListAdapter;
    private SenderServerRunnable mSenderServerRunnable;

    public SendListPresenter(SendListActivity sendListActivity) {
        this.sendListActivity = sendListActivity;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<SendListPresenter> activityWeakReference;

        MyHandler(SendListPresenter mSendListPresenter) {
            activityWeakReference = new WeakReference<>(mSendListPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            SendListPresenter mSendListPresenter = activityWeakReference.get();
            if (mSendListPresenter != null) {
                switch (msg.what) {
                    case MSG_SET_STATUS:
                        ToastUtil.show(mSendListPresenter.sendListActivity, msg.obj.toString());
                        break;
                    case MSG_UPDATE_PROGRESS:
                        if (mSendListPresenter.mSendListAdapter != null) {
                            mSendListPresenter.mSendListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
        mSendListAdapter = new SendListAdapter(fileInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(sendListActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(mSendListAdapter);
        initSendServer();
    }

    private void initSendServer() {
        mWifiMgr = WifiMgr.getInstance(sendListActivity);
        mSenderServerRunnable = new SenderServerRunnable();
        new Thread(mSenderServerRunnable).start();
    }

    private class SenderServerRunnable implements Runnable {

        private ServerSocket mServerSocket;

        @Override
        public void run() {
            String serverIp = mWifiMgr.getIpAddressFromHotspot();
            List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
            try {
                mServerSocket = new ServerSocket(Constant.DEFAULT_SERVER_COM_PORT);
                for (int i = 0; i < fileInfoList.size(); i++) {
                    final FileInfo fileInfo = fileInfoList.get(i);
                    Socket socket = mServerSocket.accept();
//                    Socket socket = new Socket(serverIp, Constant.DEFAULT_SERVER_COM_PORT);
                    final int finalI = i;
                    FileSender fileSender = new FileSender(sendListActivity, socket, fileInfo, new FileSender.OnSendListener() {
                        @Override
                        public void onStart() {
                            mMyHandler.obtainMessage(Constant.MSG_SET_STATUS, "开始发送：" + fileInfo.getFileName()).sendToTarget();
                        }

                        @Override
                        public void onProgress(long progress, long total) {
                            //更新发送进度视图
                            int i_progress = (int) (progress * 100 / total);
                            LogUtil.i("正在发送：" + fileInfo.getFilePath() + "\n当前进度：" + i_progress);
                            fileInfo.setProgress(i_progress);

                            Message msg = new Message();
                            msg.what = MSG_UPDATE_PROGRESS;
                            msg.arg1 = finalI;
                            msg.arg2 = i_progress;
                            mMyHandler.sendMessage(msg);
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo) {
                            //发送成功
                            mMyHandler.obtainMessage(MSG_SET_STATUS, "文件：" + fileInfo.getFileName() + "发送成功").sendToTarget();
                            fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_PROGRESS;
                            msg.arg1 = finalI;
                            msg.arg2 = 100;
                            mMyHandler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(Throwable throwable, FileInfo fileInfo) {
                            //发送失败
                            mMyHandler.obtainMessage(MSG_SET_STATUS, "文件：" + fileInfo.getFileName() + "发送失败").sendToTarget();
                            fileInfo.setResult(FileInfo.FLAG_FAILURE);
                            Message msg = new Message();
                            msg.what = MSG_UPDATE_PROGRESS;
                            msg.arg1 = finalI;
                            msg.arg2 = -1;
                            mMyHandler.sendMessage(msg);
                        }
                    });
                    //添加到线程池执行
                    mFileSenderList.add(fileSender);
                    MyApplication.FILE_SENDER_EXECUTOR.execute(fileSender);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.i(e.toString());
            }
        }

        /**
         * 关闭Socket连接
         */
        void closeServerSocket() {
            if (mServerSocket != null) {
                try {
                    mServerSocket.close();
                    mServerSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 是否还有文件在发送
     */
    public boolean hasFileSending() {
        for (FileSender fileSender : mFileSenderList) {
            if (fileSender != null && fileSender.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 停止所有文件发送任务
     */
    public void stopAllFileSendingTask() {
        for (FileSender fileSender : mFileSenderList) {
            if (fileSender != null) {
                fileSender.stop();
            }
        }
    }

    public void cleanSocket() {
        if (mSenderServerRunnable != null) {
            mSenderServerRunnable.closeServerSocket();
            mSenderServerRunnable = null;
        }
    }
}
