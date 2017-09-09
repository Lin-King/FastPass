package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.PicPresenter;
import com.linkings.fastpass.ui.interfaces.IPicView;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:34
 * Description: TOO
 */

public class PicFragment extends BaseFragment implements IPicView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private PicPresenter mPicPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_pic;
    }

    @Override
    public void initPresenter() {
        mPicPresenter = new PicPresenter(this);
    }

    @Override
    public void initView() {
        mPicPresenter.init(mRecyclerview);
    }
}
