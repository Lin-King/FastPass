package com.linkings.fastpass.base;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/2/21.
 * Time: 16:56
 * Description: TOO Activity基类
 */

public abstract class BaseActivity extends RxAppCompatActivity implements IBaseView {
    public Context context;
    private Toast toast;
    private ProgressDialog mProgressDialog;

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 初始化控制中心
     */
    public abstract void initPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getContentResId());
        // 初始化View注入
        ButterKnife.bind(this);
        context = this;
        initPresenter();
        initView();
    }

    protected abstract int getContentResId();


    @Override
    public <T> LifecycleTransformer<T> bind() {
        return bindToLifecycle();
    }

    @Override
    public void showProgress(boolean flag, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(flag);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    @Override
    public void showProgress(String message) {
        showProgress(true, message);
    }

    @Override
    public void setProgressCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        if (mProgressDialog != null) {
            mProgressDialog.setOnCancelListener(onCancelListener);
        }
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog == null)
            return;

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showToast(String msg) {
        if (!isFinishing()) {
            if (toast == null) {
                toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }

            toast.show();
        }
    }


    /**
     * User: Lin
     * Date: 2016/9/23 16:07
     * Description: 将R.strings.xml中的转化成字符串
     */
    public String intoString(int Rstrings) {
        return getResources().getString(Rstrings);
    }

    /**
     * User: Lin
     * Date: 2016/9/23 16:18
     * Description: 将R.arrays.xml中的转化成数组
     */
    public String[] intoArray(int Rarray) {
        return getResources().getStringArray(Rarray);
    }


    /**
     * User: Lin
     * Date: 2016/9/28 15:36
     * Description: 获得EditText的内容
     */
    public String getEditTextString(EditText view) {
        return view.getText().toString();
    }

    /**
     * User: Lin
     * Date: 2016/9/28 17:41
     * Description:  获得CheckBox的内容
     */
    public String getCheckBoxString(CheckBox view) {
        if (view.isChecked()) {
            return "1";
        }
        return "0";
    }

    public String getChexkBoxIdString(String from, String need) {
        if (TextUtils.isEmpty(from)) {
            return "";
        }
        return from + ",";
    }

}
