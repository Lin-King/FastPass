package com.linkings.fastpass.ui.activity;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.SendPresenter;
import com.linkings.fastpass.ui.interfaces.ISendView;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:42
 * Description: TOO
 */

public class SendActivity extends BaseActivity implements ISendView {

    private SendPresenter mSendPresenter;

    @Override
    public void initView() {
        
    }

    @Override
    public void initPresenter() {
        mSendPresenter = new SendPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_send;
    }
}
