package com.linkings.fastpass.presenter;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.ReceiveListAdapter;
import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.config.FileReceiver;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.activity.ReceiveListActivity;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.ToastUtil;
import com.linkings.fastpass.wifitools.WifiMgr;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.linkings.fastpass.config.Constant.MSG_SET_STATUS;
import static com.linkings.fastpass.config.Constant.MSG_UPDATE_PROGRESS;

/**
 * Created by Lin on 2017/9/8.
 * Time: 11:29
 * Description: TOO
 */

public class ReceiveListPresenter {

    private ReceiveListActivity receiveListActivity;
    private MyHandler mMyHandler;
    /**
     * 接收文件线程列表数据
     */
    private List<FileReceiver> mFileReceiverList = new ArrayList<>();
    private WifiMgr mWifiMgr;
    private ReceiveListAdapter mReceiveListAdapter;
    private Socket mClientSocket;

    public ReceiveListPresenter(ReceiveListActivity receiveListActivity) {
        this.receiveListActivity = receiveListActivity;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<ReceiveListPresenter> activityWeakReference;

        MyHandler(ReceiveListPresenter mReceiveListPresenter) {
            activityWeakReference = new WeakReference<>(mReceiveListPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            ReceiveListPresenter mReceiveListPresenter = activityWeakReference.get();
            if (mReceiveListPresenter != null) {
                switch (msg.what) {
                    case MSG_SET_STATUS:
                        ToastUtil.show(mReceiveListPresenter.receiveListActivity, msg.obj.toString());
                        break;
                    case MSG_UPDATE_PROGRESS:
                        if (mReceiveListPresenter.mReceiveListAdapter != null) {
                            mReceiveListPresenter.mReceiveListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        }
    }

    public void setTitle(Toolbar toolbar) {
        toolbar.setTitle("接收文件");
//        toolbar.setTitle(acceptActivity.intoString(R.string.music));
        toolbar.setNavigationIcon(R.mipmap.back);
        receiveListActivity.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveListActivity.toBack();
            }
        });
    }

    public void init(RecyclerView recyclerview) {
        List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
        for (int i = 0; i < fileInfoList.size(); i++) {
            fileInfoList.get(i).setProgress(0);
        }
        mReceiveListAdapter = new ReceiveListAdapter(fileInfoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(receiveListActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(mReceiveListAdapter);
        recyclerview.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.btn_operation:
                        FileReceiver fileReceiver = mFileReceiverList.get(position);
                        if (fileReceiver != null) {
                            if (!fileReceiver.isPause()) fileReceiver.pause();
                            else fileReceiver.resume();
                        }
                        break;
                }
            }
        });
        initReceiveServer();
    }

    private void initReceiveServer() {
        mWifiMgr = WifiMgr.getInstance(receiveListActivity);
        ReceiveServerRunnable receiveServerRunnable = new ReceiveServerRunnable();
        new Thread(receiveServerRunnable).start();
    }

    private class ReceiveServerRunnable implements Runnable {

        @Override
        public void run() {
            String serverIp = mWifiMgr.getIpAddressFromHotspot();
            List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
            try {
                for (int i = 0; i < fileInfoList.size(); i++) {
                    final FileInfo fileinfo = fileInfoList.get(i);
                    mClientSocket = new Socket(serverIp, Constant.DEFAULT_SERVER_COM_PORT);
                    final int finalI = i;
                    FileReceiver mFileReceiver = new FileReceiver(receiveListActivity, mClientSocket, fileinfo, new FileReceiver.OnReceiveListener() {
                        @Override
                        public void onStart() {
                            mMyHandler.obtainMessage(MSG_SET_STATUS, "开始接受：" + fileinfo.getFileName()).sendToTarget();
                        }

                        @Override
                        public void onProgress(FileInfo fileInfo, long progress, long total) {
                            int i_progress = (int) (progress * 100 / total);
                            LogUtil.i("正在接收：" + fileInfo.getFilePath() + "\n当前进度：" + i_progress);
                            fileinfo.setProgress(i_progress);

                            Message msg = new Message();
                            msg.what = MSG_UPDATE_PROGRESS;
                            msg.arg1 = finalI;
                            msg.arg2 = i_progress;
                            mMyHandler.sendMessage(msg);
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo) {
                            mMyHandler.obtainMessage(MSG_SET_STATUS, "文件：" + fileInfo.getFileName() + "接收成功").sendToTarget();
                            fileinfo.setResult(FileInfo.FLAG_SUCCESS);
                            fileinfo.setProgress(100);

                            Message msg = new Message();
                            msg.what = MSG_UPDATE_PROGRESS;
                            msg.arg1 = finalI;
                            msg.arg2 = 100;
                            mMyHandler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(Throwable throwable, FileInfo fileInfo) {
                            if (fileInfo != null) {
                                mMyHandler.obtainMessage(MSG_SET_STATUS, "文件：" + fileInfo.getFileName() + "接收失败").sendToTarget();
                                fileInfo.setResult(FileInfo.FLAG_FAILURE);

                                Message msg = new Message();
                                msg.what = MSG_UPDATE_PROGRESS;
                                msg.arg1 = finalI;
                                msg.arg2 = -1;
                                mMyHandler.sendMessage(msg);
                            }
                        }
                    });
                    //加入线程池执行
                    mFileReceiverList.add(mFileReceiver);
                    MyApplication.MAIN_EXECUTOR.execute(mFileReceiver);
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.i(e.toString());
            }
        }

    }

    /**
     * 是否还有文件在接收
     */
    public boolean hasFileReceiving() {
        for (FileReceiver fileReceiver : mFileReceiverList) {
            if (fileReceiver != null && fileReceiver.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 停止所有文件发送任务
     */
    public void stopAllFileReceivingTask() {
        for (FileReceiver fileReceiver : mFileReceiverList) {
            if (fileReceiver != null) {
                fileReceiver.stop();
            }
        }
    }

    /**
     * 断开接收文件的Socket
     */
    public void closeClientSocket() {
        if (mClientSocket != null) {
            try {
                if (!mClientSocket.isClosed()) {
                    mClientSocket.close();
                    mClientSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
