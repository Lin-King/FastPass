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
import com.linkings.fastpass.presenter.AcceptPresenter;
import com.linkings.fastpass.ui.interfaces.IAcceptView;
import com.linkings.fastpass.utils.LogUtil;

import butterknife.BindView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:44
 * Description: TOO
 */

@RuntimePermissions
public class AcceptActivity extends BaseActivity implements IAcceptView {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    private AcceptPresenter mAcceptPresenter;

    @Override
    public void initView() {
        AcceptActivityPermissionsDispatcher.needsWithCheck(this);
    }

    @Override
    public void initPresenter() {
        mAcceptPresenter = new AcceptPresenter(this, mRecyclerview);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_accept;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAcceptPresenter.unregisterWifiReceiver();
        mAcceptPresenter.closeUdpSocket();
        mAcceptPresenter.clearWifiConfig();
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, AcceptActivity.class);
        srcActivity.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AcceptActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needs() {
        LogUtil.i("needs");
        mAcceptPresenter.init();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void rationale(final PermissionRequest request) {
        LogUtil.i("rationale");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void denied() {
        LogUtil.i("denied");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void neverAgain() {
        LogUtil.i("neverAgain");
    }
}
