package com.linkings.fastpass.ui.activity;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.AcceptPresenter;
import com.linkings.fastpass.ui.interfaces.IAcceptView;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:44
 * Description: TOO
 */

public class AcceptActivity extends BaseActivity implements IAcceptView {

    private AcceptPresenter mAcceptPresenter;

    @Override
    public void initView() {
        
    }

    @Override
    public void initPresenter() {
        mAcceptPresenter = new AcceptPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_accept;
    }
}
