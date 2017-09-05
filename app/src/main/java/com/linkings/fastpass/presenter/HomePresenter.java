package com.linkings.fastpass.presenter;

import android.support.v4.app.Fragment;

import com.linkings.fastpass.R;
import com.linkings.fastpass.ui.activity.HomeActivity;
import com.linkings.fastpass.ui.fragment.ApkFragment;
import com.linkings.fastpass.ui.fragment.MediaFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/2.
 * Time: 17:46
 * Description: TOO
 */

public class HomePresenter {
    HomeActivity mHomeActivity;

    public HomePresenter(HomeActivity homeActivity) {
        mHomeActivity = homeActivity;
    }

    public void initToolbar() {
        mHomeActivity.initToolbar();
    }

    public void init() {
        List<Fragment> mList = new ArrayList<>();
        mList.add(new ApkFragment());
        mList.add(new MediaFragment());
        mList.add(new MediaFragment());
        mList.add(new MediaFragment());
        mList.add(new MediaFragment());
        String[] title = {
                mHomeActivity.intoString(R.string.application),
                mHomeActivity.intoString(R.string.video),
                mHomeActivity.intoString(R.string.music),
                mHomeActivity.intoString(R.string.picture),
                mHomeActivity.intoString(R.string.file)
        };
        mHomeActivity.init(mList, title);
    }
    
}
