package com.linkings.fastpass.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.config.FileInfoMG;
import com.linkings.fastpass.presenter.SendListPresenter;
import com.linkings.fastpass.ui.interfaces.ISendListView;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Lin on 2017/9/7.
 * Time: 15:46
 * Description: TOO
 */

@RuntimePermissions
public class SendListActivity extends BaseActivity implements ISendListView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private SendListPresenter mSendListPresenter;

    @Override
    public void initView() {
        SendListActivityPermissionsDispatcher.needsWithCheck(this);
    }

    @Override
    public void initPresenter() {
        mSendListPresenter = new SendListPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_sendlist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSendListPresenter.closeServerSocket();
        if (mSendListPresenter.hasFileSending()) mSendListPresenter.stopAllFileSendingTask();
        FileInfoMG.getInstance().cleanFileInfoList();
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, SendListActivity.class);
        srcActivity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SendListActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needs() {
        mSendListPresenter.init(mRecyclerview);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void rationale(final PermissionRequest request) {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void denied() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAskAgain() {
    }
}
