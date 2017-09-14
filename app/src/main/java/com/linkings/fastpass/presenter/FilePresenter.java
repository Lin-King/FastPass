package com.linkings.fastpass.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.linkings.fastpass.adapter.FileAdapter;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.activity.HomeActivity;
import com.linkings.fastpass.ui.fragment.FileFragment;
import com.linkings.fastpass.utils.LogUtil;
import com.linkings.fastpass.utils.SDCardUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:40
 * Description: TOO
 */

public class FilePresenter {

    private FileFragment fileFragment;
    private List<FileInfo> mList;
    private FileAdapter mFileAdapter;
    private String rootPath;
    private String mPath;
    private MyHandler mMyHandler;

    public FilePresenter(FileFragment fileFragment) {
        this.fileFragment = fileFragment;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<FilePresenter> activityWeakReference;

        MyHandler(FilePresenter mFilePresenter) {
            activityWeakReference = new WeakReference<>(mFilePresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            FilePresenter mFilePresenter = activityWeakReference.get();
            if (mFilePresenter != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        if (mFilePresenter.mFileAdapter != null) {
                            mFilePresenter.mFileAdapter.notifyDataSetChanged();
                        }
                        mFilePresenter.fileFragment.hideProgress();
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerView) {
        rootPath = SDCardUtil.getRootPath();
        mPath = SDCardUtil.getRootPath();
        mList = new ArrayList<>();
        mFileAdapter = new FileAdapter(mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(fileFragment.getContext()));
        recyclerView.setAdapter(mFileAdapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = mList.get(position);
                if (fileInfo.getFileName().equals("...")) {
                    mPath = new File(mPath).getParent();
                    if (rootPath.equals(mPath)) {
                        toRefresh(mPath, false);
                    } else {
                        toRefresh(mPath, true);
                    }
                    return;
                }
                File fl = new File(fileInfo.getFilePath());
                if (fl.isDirectory()) {
                    toRefresh(fl.getPath(), true);
                    return;
                }
                if (fl.isFile()) {
                    fileInfo.setOK(!fileInfo.isOK());
                    if (fileInfo.isOK()) FileInfoMG.getInstance().addFileInfo(fileInfo);
                    else FileInfoMG.getInstance().removeFileInfo(fileInfo);
                    mFileAdapter.notifyDataSetChanged();
                    ((HomeActivity) fileFragment.getActivity()).setSendNum();
//                    if (isPicture(fl.getName())) {
//                        showPic(fl);
//                    } else {
//                        ToastUtil.show(fileFragment.getContext(), "该文件不是图片类型！");
//                    }
                }
            }
        });
        toRefresh(mPath, false);
    }

    private void showPic(File fl) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(fl);
        intent.setDataAndType(uri, "image/*");
        fileFragment.startActivity(intent);
    }

    private void toRefresh(final String path, final boolean state) {
        fileFragment.showProgress("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mList.clear();
                if (state) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName("...");
                    fileInfo.setFilePath("...");
                    mList.add(fileInfo);
                    mPath = new File(path).getPath();
                }
                File[] files = new File(path).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return !new File(dir, name).isHidden();
                    }
                });
                for (File file : files) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(file.getName());
                    fileInfo.setFilePath(file.getPath());
                    mList.add(fileInfo);
                }
                Collections.sort(mList, new CustomComparator());
                mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
            }
        }).start();
    }

    private static boolean isPicture(String fileName) {
        if (TextUtils.isEmpty(fileName) || !fileName.contains(".")) {
            return false;
        }
        String tmpName = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        LogUtil.i("tmpName ：" + tmpName);
        if (TextUtils.isEmpty(tmpName)) {
            return false;
        }
        String imgeArray[] = {"bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico"};
        for (String anImgeArray : imgeArray) {
            if (anImgeArray.equals(tmpName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private class CustomComparator implements Comparator<FileInfo> {
        @Override
        public int compare(FileInfo s1, FileInfo s2) {
            File pFile1 = new File(s1.getFilePath());
            File pFile2 = new File(s2.getFilePath());
            if (pFile1.isDirectory() && pFile2.isDirectory()) {
                return pFile1.getName().compareToIgnoreCase(pFile2.getName());
            } else {
                if (pFile1.isDirectory() && pFile2.isFile()) {
                    return -1;
                } else if (pFile1.isFile() && pFile2.isDirectory()) {
                    return 1;
                } else {
                    return pFile1.getName().compareToIgnoreCase(pFile2.getName());
                }
            }
        }
    }

    public void setNoOK() {
        if (FileInfoMG.getInstance().isClear()) {
            for (FileInfo fileInfo : mList) {
                fileInfo.setOK(false);
            }
            mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
        }
    }
}
