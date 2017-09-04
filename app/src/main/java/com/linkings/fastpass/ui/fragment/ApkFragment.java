package com.linkings.fastpass.ui.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.ApkAdapter;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.model.Apk;
import com.linkings.fastpass.presenter.ApkPresenter;
import com.linkings.fastpass.ui.interfaces.IApkView;
import com.linkings.fastpass.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/3.
 * Time: 11:48
 * Description: TOO
 */

public class ApkFragment extends BaseFragment implements IApkView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private ApkPresenter mApkPresenter;
    private List<Apk> mApks;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_apk;
    }

    @Override
    public void initPresenter() {
        mApkPresenter = new ApkPresenter(this);
    }

    @Override
    public void initView() {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> mAllPackages = packageManager.getInstalledPackages(0);
        mApks = new ArrayList<>();
        for (int i = 0; i < mAllPackages.size(); i++) {
            PackageInfo packageInfo = mAllPackages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                Log.i("Lin", "package path : " + packageInfo.applicationInfo.sourceDir);
//                Log.i("Lin", "apk name : " + packageInfo.applicationInfo.loadLabel(packageManager));
                Apk mApk = new Apk();
                mApk.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                mApk.setPath(packageInfo.applicationInfo.sourceDir);
                File file = new File(packageInfo.applicationInfo.sourceDir);
                mApk.setSize(file.length());
                mApk.setPic(packageInfo.applicationInfo.loadIcon(packageManager));
//                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
//                fileInfo.setBitmap(drawableToBitmap(drawable));
                mApks.add(mApk);
            }
            LinearLayoutManager linearLayoutManager = new GridLayoutManager(context, 4);
            mRecyclerview.setLayoutManager(linearLayoutManager);
            ApkAdapter mApkAdapter = new ApkAdapter(mApks);
            mRecyclerview.setAdapter(mApkAdapter);
            mApkAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    ToastUtil.show(context, mApks.get(position).getPath());
                }
            });
        }
    }
    
    
}
