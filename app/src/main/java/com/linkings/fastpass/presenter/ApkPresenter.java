package com.linkings.fastpass.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.ApkAdapter;
import com.linkings.fastpass.config.Constant;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.ApkFragment;
import com.linkings.fastpass.utils.BitmapUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/3.
 * Time: 11:49
 * Description: TOO
 */

public class ApkPresenter {

    private List<FileInfo> mApks;
    private Context context;
    private ApkAdapter mMApkAdapter;
    private ApkFragment apkFragment;
    private MyHandler mMyHandler;

    public ApkPresenter(ApkFragment apkFragment) {
        this.context = apkFragment.getActivity();
        this.apkFragment = apkFragment;
        mMyHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler {
        private WeakReference<ApkPresenter> activityWeakReference;

        public MyHandler(ApkPresenter mApkPresenter) {
            activityWeakReference = new WeakReference<ApkPresenter>(mApkPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            ApkPresenter mApkPresenter = activityWeakReference.get();
            if (mApkPresenter != null) {
                switch (msg.what) {
                    case Constant.MSG_UPDATE_ADAPTER:
                        if (mApkPresenter.mMApkAdapter != null) {
                            mApkPresenter.mMApkAdapter.notifyDataSetChanged();
                            mApkPresenter.apkFragment.hideProgress();
                        }
                        break;
                }
            }
        }
    }

    public void init(RecyclerView recyclerview) {
        mApks = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(context, 4);
        recyclerview.setLayoutManager(linearLayoutManager);
        mMApkAdapter = new ApkAdapter(mApks);
        recyclerview.setAdapter(mMApkAdapter);
        mMApkAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileInfo fileInfo = mApks.get(position);
                fileInfo.setOK(!fileInfo.isOK());
                List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
                fileInfoList.add(mApks.get(position));
                mMApkAdapter.notifyDataSetChanged();
            }
        });
        readAPK();
    }

    private void readAPK() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager packageManager = context.getPackageManager();
                List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
                for (int i = 0; i < mAllPackages.size(); i++) {
                    PackageInfo packageInfo = mAllPackages.get(i);
                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                Log.i("Lin", "package path : " + packageInfo.applicationInfo.sourceDir);
//                Log.i("Lin", "apk name : " + packageInfo.applicationInfo.loadLabel(packageManager));
                        FileInfo mApk = new FileInfo();
                        mApk.setFileName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                        mApk.setFilePath(packageInfo.applicationInfo.sourceDir);
                        File file = new File(packageInfo.applicationInfo.sourceDir);
                        mApk.setSize(file.length());
                        String s = BitmapUtil.bitmapToBase64(BitmapUtil.drawable2Bitmap(packageInfo.applicationInfo.loadIcon(packageManager)));
                        mApk.setPic(s);
                        mApk.setFileType("apk");
//                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
//                fileInfo.setBitmap(drawableToBitmap(drawable));
                        mApks.add(mApk);
                    }
                }
                mMyHandler.sendEmptyMessage(Constant.MSG_UPDATE_ADAPTER);
            }
        }).start();
    }

}
