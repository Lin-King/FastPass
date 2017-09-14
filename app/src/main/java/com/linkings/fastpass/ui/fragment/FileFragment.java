package com.linkings.fastpass.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.FilePresenter;
import com.linkings.fastpass.ui.interfaces.IFileView;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:34
 * Description: TOO
 */

public class FileFragment extends BaseFragment implements IFileView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private FilePresenter mFilePresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_file;
    }

    @Override
    public void initPresenter() {
        mFilePresenter = new FilePresenter(this);
    }

    @Override
    public void initView() {
        if (initializeUI) mFilePresenter.init(mRecyclerview);
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
    public void setNoOK() {
        super.setNoOK();
        if (initializeUI) mFilePresenter.setNoOK();
    }
}
