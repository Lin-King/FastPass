package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.ApkPresenter;
import com.linkings.fastpass.ui.interfaces.IApkView;

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
        mApkPresenter.init(mRecyclerview);
    }

}
