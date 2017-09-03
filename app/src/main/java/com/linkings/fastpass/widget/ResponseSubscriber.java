package com.linkings.fastpass.widget;

import android.content.DialogInterface;

import com.linkings.fastpass.app.MyApplication;
import com.linkings.fastpass.base.IBaseView;
import com.linkings.fastpass.utils.ToastUtil;

import rx.Subscriber;

/**
 * Created by Lin on 2017/3/9.
 * Time: 11:01
 * Description: TOO
 */

public abstract class ResponseSubscriber<T> extends Subscriber<T> {
    private IBaseView mBaseView;
    private boolean state;

    public ResponseSubscriber(IBaseView baseView) {
        mBaseView = baseView;
        mBaseView.setProgressCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ResponseSubscriber.this.unsubscribe();
            }
        });

    }

    public ResponseSubscriber(IBaseView baseView, boolean state) {
        this(baseView);
        this.state = state;
    }

    @Override
    public void onStart() {
        mBaseView.showProgress("");
    }

    @Override
    public void onCompleted() {
        if (state) {
            return;
        }
        mBaseView.hideProgress();
    }

    @Override
    public void onError(Throwable e) {
        mBaseView.hideProgress();
//        LogUtil.i(e.toString());
        ToastUtil.show(MyApplication.getInstance().getApplicationContext(), "网络连接错误");
//        ToastUtil.show(MyApplication.getInstance().getApplicationContext(), e.toString());
    }
}
