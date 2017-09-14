package com.linkings.fastpass.config;

import android.content.Context;

import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Lin on 2017/9/8.
 * Time: 13:40
 * Description: TOO
 */

public class FileReceiver implements Runnable {

    public static final int BYTE_SIZE_HEADER = 1024 * 10;
    private static final int BYTE_SIZE_DATA = 1024 * 4; //字节数组长度
    public static final String UTF_8 = "UTF-8";//传输字节类型
    private Context mContext;
    private FileInfo mFileInfo;//待发送的文件数据
    private Socket mSocket;//传送文件的Socket输入输出流
    private InputStream mInputStream;
    private OnReceiveListener mOnReceiveListener;//文件接收监听事件
    private boolean mIsStop;//设置未执行线程的不执行标识
    private boolean mIsFinish;//该线程是否执行完毕
    //用来控制线程暂停、恢复
    private final Object LOCK = new Object();
    private boolean mIsPause;

    public FileReceiver(Context context, Socket socket, FileInfo fileInfo, OnReceiveListener onReceiveListener) {
        mContext = context;
        mSocket = socket;
        mFileInfo = fileInfo;
        mOnReceiveListener = onReceiveListener;
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
            if (mOnReceiveListener != null) {
                mOnReceiveListener.onStart();
            }
            init();
            //发送文件实体数据
            parseBody();
            //文件传输完毕
            finishTransfer();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(e.toString());
            if (mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }
    }

    private void init() throws Exception {
        if (mSocket != null) {
            mInputStream = mSocket.getInputStream();
        }
    }

    private void parseBody() throws Exception {
        if (mFileInfo == null) return;
        long fileSize = mFileInfo.getSize();
        OutputStream fos = new FileOutputStream(gerateLocalFile(mFileInfo));
        byte[] bytes = new byte[BYTE_SIZE_DATA];
        long total = 0;
        int len = 0;
        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while ((len = mInputStream.read(bytes)) != -1) {
            synchronized (LOCK) {
                if (mIsPause) {
                    LOCK.wait();
                }
                //写入文件
                fos.write(bytes, 0, len);
                total = total + len;
                //每隔200毫秒返回一次进度
                eTime = System.currentTimeMillis();
                if (eTime - sTime > 200) {
                    sTime = eTime;
                    if (mOnReceiveListener != null) {
                        mOnReceiveListener.onProgress(mFileInfo, total, fileSize);
                    }
                }
            }
        }
//        if (fileSize - total > 1000) {
//            if (mOnReceiveListener != null)
//                mOnReceiveListener.onFailure(new Exception(), mFileInfo);
//        }

        if (fileSize - total > 1000) {
            throw new Exception();
        }
        //关闭Socket输入输出流
        mInputStream.close();
        //文件接收成功
        if (mOnReceiveListener != null) mOnReceiveListener.onSuccess(mFileInfo);
        mIsFinish = true;
    }

    private void finishTransfer() throws Exception {
        if (mInputStream != null) mInputStream.close();
        if (mSocket != null && mSocket.isConnected()) mSocket.close();
    }

    /**
     * 生成本地文件路径
     */
    public static File gerateLocalFile(FileInfo fileInfo) {
        LogUtil.i("fileInfo.getFileType() " + fileInfo.getFileType());
        String path;
        switch (fileInfo.getFileType()) {
            case "mp4":
                path = Constant.ROOT_PATH_MP4;
                break;
            case "mp3":
                path = Constant.ROOT_PATH_MP3;
                break;
            case "apk":
                path = Constant.ROOT_PATH_APK;
                break;
            case "jpg":
            case "jpeg":
            case "png":
                path = Constant.ROOT_PATH_PIC;
                break;
            default:
                path = Constant.ROOT_PATH_FILE;
                break;
        }
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
//        if (!TextUtils.isEmpty(fileInfo.getFileType())) {
//            if (fileInfo.getFileType().equals("apk"))
//                return new File(dirFile, fileInfo.getFileName() + "." + fileInfo.getFileType());
//        }
        return new File(dirFile, fileInfo.getFileName());
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

    /**
     * 文件接收监听事件
     */
    public interface OnReceiveListener {
        void onStart();

        void onProgress(FileInfo fileInfo, long progress, long total);

        void onSuccess(FileInfo fileInfo);

        void onFailure(Throwable throwable, FileInfo fileInfo);
    }
}
