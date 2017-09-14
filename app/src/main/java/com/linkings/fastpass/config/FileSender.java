package com.linkings.fastpass.config;

import android.content.Context;

import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.LogUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Lin on 2017/9/8.
 * Time: 10:21
 * Description: TOO
 */

public class FileSender implements Runnable {
    public static final int BYTE_SIZE_HEADER = 1024 * 10;
    public static final int BYTE_SIZE_DATA = 1024 * 4; //字节数组长度
    public static final String UTF_8 = "UTF-8";//传输字节类型
    private Context mContext;
    private FileInfo mFileInfo;//待发送的文件数据
    private Socket mSocket;//传送文件的Socket输入输出流
    private OutputStream mOutputStream;
    private OnSendListener mOnSendListener;//文件传送监听事件
    private boolean mIsStop;//设置未执行线程的不执行标识
    private boolean mIsFinish;//该线程是否执行完毕
    //用来控制线程暂停、恢复
    private final Object LOCK = new Object();
    private boolean mIsPause;

    public FileSender(Context context, Socket socket, FileInfo fileInfo, OnSendListener onSendListener) {
        mContext = context;
        mSocket = socket;
        mFileInfo = fileInfo;
        mOnSendListener = onSendListener;
    }

    public boolean isPause() {
        return mIsPause;
    }

    public void setPause(boolean pause) {
        mIsPause = pause;
    }

    @Override
    public void run() {
        if (mIsStop) return;
        try {
            //初始化
            if (mOnSendListener != null) mOnSendListener.onStart();
            init();
            //发送文件实体数据
            parseBody();
            //文件传输完毕
            finishTransfer();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(e.toString());
            if (mOnSendListener != null) mOnSendListener.onFailure(e, mFileInfo);
        }
    }

    public void init() throws Exception {
        mSocket.setSoTimeout(30 * 1000);
        OutputStream os = mSocket.getOutputStream();
        mOutputStream = new BufferedOutputStream(os);
    }

    private void parseBody() throws Exception {
        long fileSize = mFileInfo.getSize();
        File file = new File(mFileInfo.getFilePath());
        InputStream fis = new FileInputStream(file);
        int len = 0;
        long total = 0;
        byte[] bytes = new byte[BYTE_SIZE_DATA];
        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while ((len = fis.read(bytes)) != -1) {
            synchronized (LOCK) {
                if (mIsPause) {
                    LOCK.wait();
                }
                //写入文件
                mOutputStream.write(bytes, 0, len);
                total += len;
                //每隔200毫秒返回一次进度
                eTime = System.currentTimeMillis();
                if (eTime - sTime > 200) {
                    sTime = eTime;
                    if (mOnSendListener != null) mOnSendListener.onProgress(total, fileSize);
                }
            }
        }
        if (fileSize - total > 1000) {
            throw new Exception();
        }
        //关闭Socket输入输出流
        mOutputStream.flush();
        mOutputStream.close();
        //文件发送成功
        if (mOnSendListener != null) mOnSendListener.onSuccess(mFileInfo);
        mIsFinish = true;
    }

    private void finishTransfer() throws Exception {
        if (mOutputStream != null) mOutputStream.close();
        if (mSocket != null && mSocket.isConnected()) mSocket.close();
    }

    /**
     * 暂停发送线程
     */
    public void pause() {
        synchronized (LOCK) {
            mIsPause = true;
            LOCK.notifyAll();
        }
    }

    /**
     * 恢复发送线程
     */
    public void resume() {
        synchronized (LOCK) {
            mIsPause = false;
            LOCK.notifyAll();
        }
    }

    /**
     * 设置当前的发送任务不执行
     */
    public void stop() {
        mIsStop = true;
    }

    /**
     * 文件是否在发送中
     */
    public boolean isRunning() {
        return !mIsFinish;
    }

    public interface OnSendListener {
        void onStart();

        void onProgress(long progress, long total);

        void onSuccess(FileInfo fileInfo);

        void onFailure(Throwable throwable, FileInfo fileInfo);
    }
}
