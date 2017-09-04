package com.linkings.fastpass.ui.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.HomeAdapter;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.HomePresenter;
import com.linkings.fastpass.ui.interfaces.IHomeView;
import com.linkings.fastpass.utils.DialogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IHomeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tl_home)
    TabLayout mTlHome;
    @BindView(R.id.vp_home)
    ViewPager mVpHome;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private HomePresenter mHomePresenter;

    @Override
    public void initView() {
        mHomePresenter.initToolbar();
        mHomePresenter.init();

    }

    @Override
    public void initPresenter() {
        mHomePresenter = new HomePresenter(this);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_home;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @OnClick(R.id.fab)
    public void onViewClicked() {
        DialogUtil.showDoubleDialog(context, "", "选择", "发送", "接受", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
//        Snackbar.make(mFab, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mDrawerLayout.setDrawerListener(toggle);
        }
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void init(List<Fragment> mList, String[] title) {
        HomeAdapter mHomeAdapter = new HomeAdapter(getSupportFragmentManager(), mList, title);
        mVpHome.setAdapter(mHomeAdapter);
        mTlHome.setupWithViewPager(mVpHome);
    }
}
