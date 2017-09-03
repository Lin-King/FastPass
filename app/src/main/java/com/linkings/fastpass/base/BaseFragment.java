package com.linkings.fastpass.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;

/**
 * Created by Lin on 2017/2/21.
 * Time: 16:56
 * Description: TOO Fragment基类
 */

public abstract class BaseFragment extends RxFragment implements IBaseView {
    public boolean initializeFragment = false;//true表示已初始化
    protected View mLayoutView;
    protected Context context;

    /**
     * 初始化布局
     */
    public abstract int getLayoutRes();

    /**
     * 初始化控制中心
     */
    public abstract void initPresenter();

    /**
     * 初始化视图
     */
    public abstract void initView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        initializeFragment = true;
        if (mLayoutView != null) {
            ViewGroup parent = (ViewGroup) mLayoutView.getParent();
            if (parent != null) {
                parent.removeView(mLayoutView);
            }
        } else {
            mLayoutView = getCreateView(inflater, container);
            ButterKnife.bind(this, mLayoutView);
            initPresenter();
            initView();     //初始化布局
        }

        return mLayoutView;
    }

    /**
     * 获取Fragment布局文件的View
     *
     * @param inflater
     * @param container
     * @return
     */
    private View getCreateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    /**
     * 获取当前Fragment状态
     *
     * @return true为正常 false为未加载或正在删除
     */
    private boolean getStatus() {
        return (isAdded() && !isRemoving());
    }

    /**
     * 获取Activity
     *
     * @return
     */
    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    @Override
    public <T> LifecycleTransformer<T> bind() {
        return bindToLifecycle();
    }

    @Override
    public void setProgressCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        getBaseActivity().setProgressCancelListener(onCancelListener);
    }


    @Override
    public void showProgress(boolean flag, String message) {
        if (getStatus()) {
            getBaseActivity().showProgress(flag, message);
        }
    }

    @Override
    public void showProgress(String message) {
        showProgress(true, message);
    }

    @Override
    public void hideProgress() {
        if (getStatus()) {
            getBaseActivity().hideProgress();
        }
    }

    @Override
    public void showToast(String msg) {
        if (getStatus()) {
            getBaseActivity().showToast(msg);
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

    public String getEditTextString(TextView view) {
        return view.getText().toString();
    }

    /**
     * User: Lin
     * Date: 2016/9/28 17:41
     * Description:  获得CheckBox的内容
     */
    public String getCheckBoxString(CheckBox view) {
        if (view != null) {
            return view.getText().toString();
        }
        return "";
    }

    public String getChexkBoxIdString(String from, String need) {
        if (TextUtils.isEmpty(from)) {
            return "";
        }
        return from + ",";
    }

    public void saveData() {

    }

    public void refreshData() {

    }

    public void end() {

    }
}
