package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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
    @BindView(R.id.tv_num)
    TextView mTvNum;
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
        if (initializeUI) mMediaPresenter.init(mRecyclerview);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!initializeUI && initializeFragment) {
                initializeUI = true;
                initView();
                return;
            }
            if (!initializeUI) initializeUI = true;
        }
    }

    @Override
    public void setNum(int size) {
        String s = "本地音频（" + size + "）";
        mTvNum.setText(s);
    }
}
