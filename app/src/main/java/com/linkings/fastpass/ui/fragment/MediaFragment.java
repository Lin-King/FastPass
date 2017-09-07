package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.MediaPresenter;
import com.linkings.fastpass.ui.interfaces.IMediaView;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/4.
 * Time: 10:29
 * Description: TOO
 */

public class MediaFragment extends BaseFragment implements IMediaView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private MediaPresenter mMediaPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_media;
    }

    @Override
    public void initPresenter() {
        mMediaPresenter = new MediaPresenter(this);
    }

    @Override
    public void initView() {
        mMediaPresenter.init(mRecyclerview);
    }
}
