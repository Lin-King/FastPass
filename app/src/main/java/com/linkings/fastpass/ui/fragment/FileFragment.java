package com.linkings.fastpass.ui.fragment;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseFragment;
import com.linkings.fastpass.presenter.FilePresenter;
import com.linkings.fastpass.ui.interfaces.IFileView;

/**
 * Created by Lin on 2017/9/9.
 * Time: 23:34
 * Description: TOO
 */

public class FileFragment extends BaseFragment implements IFileView {

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

    }
}
