package com.linkings.fastpass.ui.interfaces;

import android.support.v4.app.Fragment;

import com.linkings.fastpass.base.IBaseView;

import java.util.List;

/**
 * Created by Lin on 2017/9/2.
 * Time: 17:48
 * Description: TOO
 */

public interface IHomeView extends IBaseView {

    void initToolbar();

    void init(List<Fragment> mList, String[] title);

    void toSendActivity();

    void toAcceptActivity();

    void setSendNum();
}
