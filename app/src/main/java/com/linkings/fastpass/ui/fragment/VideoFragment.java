package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.VideoPresenter;
import com.linkings.fastpass.ui.interfaces.IVideoView;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:34
 * Description: TOO
 */

public class VideoFragment extends BaseFragment implements IVideoView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private VideoPresenter mVideoPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_video;
    }

    @Override
    public void initPresenter() {
        mVideoPresenter = new VideoPresenter(this);
    }

    @Override
    public void initView() {
        mVideoPresenter.init(mRecyclerview);
    }
}
