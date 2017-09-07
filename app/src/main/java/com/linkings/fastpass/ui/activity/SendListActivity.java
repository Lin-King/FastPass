package com.linkings.fastpass.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.SendListPresenter;
import com.linkings.fastpass.ui.interfaces.ISendListView;

import butterknife.BindView;

/**
 * Created by Lin on 2017/9/7.
 * Time: 15:46
 * Description: TOO
 */

public class SendListActivity extends BaseActivity implements ISendListView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private SendListPresenter mSendListPresenter;

    @Override
    public void initView() {
        mSendListPresenter.init(mRecyclerview);
    }

    @Override
    public void initPresenter() {
        mSendListPresenter = new SendListPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_sendlist;
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, SendListActivity.class);
        srcActivity.startActivity(intent);
    }
    
}
