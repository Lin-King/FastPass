package com.linkings.fastpass.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;

import com.linkings.fastpass.R;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.SendPresenter;
import com.linkings.fastpass.ui.interfaces.ISendView;
import com.linkings.fastpass.utils.LogUtil;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Lin on 2017/9/4.
 * Time: 13:42
 * Description: TOO
 */

@RuntimePermissions
public class SendActivity extends BaseActivity implements ISendView {

    private SendPresenter mSendPresenter;

    @Override
    public void initView() {
        // if (MPermission.requestSettingActivity(this, MPermission.CODE_WRITE_SETTINGS)) init();
        SendActivityPermissionsDispatcher.needsWithCheck(this);
    }

    @Override
    public void initPresenter() {
        mSendPresenter = new SendPresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_send;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSendPresenter.unregisterHotSpotReceiver();
    }

    public static void startActivity(Activity srcActivity) {
        Intent intent = new Intent(srcActivity, SendActivity.class);
        srcActivity.startActivity(intent);
    }

    @NeedsPermission(Manifest.permission.WRITE_SETTINGS)
    void needs() {
        LogUtil.i("needs");
        mSendPresenter.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SendActivityPermissionsDispatcher.onActivityResult(this, requestCode);
    }

    @OnShowRationale(Manifest.permission.WRITE_SETTINGS)
    void rationale(final PermissionRequest request) {
        LogUtil.i("rationale");
    }

    @OnPermissionDenied(Manifest.permission.WRITE_SETTINGS)
    void denied() {
        LogUtil.i("denied");
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_SETTINGS)
    void neverAskAgain() {
        LogUtil.i("neverAskAgain");
    }

//测试机小米7.0，无法永久添加系统设置权限，需每次设置，原因不明
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MPermission.CODE_WRITE_SETTINGS) {
//            if (Build.VERSION.SDK_INT >= M) {
//                if (Settings.System.canWrite(this)) {
//                    String[] strings = {
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_WIFI_STATE,
//                            Manifest.permission.ACCESS_COARSE_LOCATION};
//                    if (MPermission.requestMultiPermissions(this, strings, MPermission.CODE_MULTI_PERMISSION)) {
//                        init();
//                    }
//                } else {
//                    LogUtil.i("CODE_WRITE_SETTINGS");
//                    ToastUtil.show(context, "请开启该权限");
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case MPermission.CODE_MULTI_PERMISSION: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    init();
//                } else {
//                    LogUtil.i("CODE_MULTI_PERMISSION");
//                    ToastUtil.show(context, "请开启该权限");
//                }
//                break;
//            }
//        }
//    }
}
