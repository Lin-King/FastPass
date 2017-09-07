package com.linkings.fastpass.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.adapter.ApkAdapter;
import com.linkings.fastpass.model.FileInfo;
import com.linkings.fastpass.ui.fragment.ApkFragment;
import com.linkings.fastpass.utils.BitmapUtil;
import com.linkings.fastpass.utils.FileInfoMG;
import com.linkings.fastpass.utils.ToastUtil;

import java.io.File;
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

    public ApkPresenter(ApkFragment apkFragment) {
        this.context = apkFragment.getActivity();
    }

    public void init(RecyclerView recyclerview) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
        mApks = new ArrayList<>();
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
//                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
//                fileInfo.setBitmap(drawableToBitmap(drawable));
                mApks.add(mApk);
            }
            LinearLayoutManager linearLayoutManager = new GridLayoutManager(context, 4);
            recyclerview.setLayoutManager(linearLayoutManager);
            ApkAdapter mApkAdapter = new ApkAdapter(mApks);
            recyclerview.setAdapter(mApkAdapter);
            mApkAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ToastUtil.show(context, mApks.get(position).getFilePath());
                    List<FileInfo> fileInfoList = FileInfoMG.getInstance().getFileInfoList();
                    fileInfoList.add(mApks.get(position));
                }
            });
        }
    }
}
