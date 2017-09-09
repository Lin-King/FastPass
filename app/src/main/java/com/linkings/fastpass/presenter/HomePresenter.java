package com.linkings.fastpass.presenter;

import android.support.v4.app.Fragment;

import com.linkings.fastpass.R;
import com.linkings.fastpass.ui.activity.HomeActivity;
import com.linkings.fastpass.ui.fragment.ApkFragment;
import com.linkings.fastpass.ui.fragment.FileFragment;
import com.linkings.fastpass.ui.fragment.MediaFragment;
import com.linkings.fastpass.ui.fragment.PicFragment;
import com.linkings.fastpass.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lin on 2017/9/2.
 * Time: 17:46
 * Description: TOO
 */

public class HomePresenter {
    private HomeActivity mHomeActivity;

    public HomePresenter(HomeActivity homeActivity) {
        mHomeActivity = homeActivity;
    }

    public void initToolbar() {
        mHomeActivity.initToolbar();
    }

    public void init() {
        List<Fragment> mList = new ArrayList<>();
        mList.add(new ApkFragment());
        mList.add(new VideoFragment());
        mList.add(new MediaFragment());
        mList.add(new PicFragment());
        mList.add(new FileFragment());
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
