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
import com.linkings.fastpass.presenter.ReceiveListPresenter;
import com.linkings.fastpass.ui.interfaces.IReceiveListView;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Lin on 2017/9/8.
 * Time: 11:25
 * Description: TOO
 */

@RuntimePermissions
public class ReceiveListActivity extends BaseActivity implements IReceiveListView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private ReceiveListPresenter mReceiveListPresenter;

    @Override
    public void initView() {
        ReceiveListActivityPermissionsDispatcher.needsWithCheck(this);
    }

    @Override
    public void initPresenter() {
        mReceiveListPresenter = new ReceiveListPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_filereceivelist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiveListPresenter.closeClientSocket();
        if (mReceiveListPresenter.hasFileReceiving())
            mReceiveListPresenter.stopAllFileReceivingTask();
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, ReceiveListActivity.class);
        srcActivity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ReceiveListActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needs() {
        mReceiveListPresenter.init(mRecyclerview);
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
