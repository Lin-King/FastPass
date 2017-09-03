package com.linkings.fastpass.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Lin on 2017/9/3.
 * Time: 12:02
 * Description: TOO
 */

public class HomeAdapter extends FragmentPagerAdapter {

    private List<Fragment> baseFragments;
    private String[] title;

    public HomeAdapter(FragmentManager fm, List<Fragment> baseFragments, String[] title) {
        super(fm);
        this.baseFragments = baseFragments;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return baseFragments.get(position);
    }

    @Override
    public int getCount() {
        return baseFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title != null ? title[position] : "";
    }
}
