package com.linkings.fastpass.ui.activity;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.linkings.fastpass.R;
import com.linkings.fastpass.adapter.HomeAdapter;
import com.linkings.fastpass.base.BaseActivity;
import com.linkings.fastpass.presenter.HomePresenter;
import com.linkings.fastpass.ui.interfaces.IHomeView;
import com.linkings.fastpass.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IHomeView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tl_home)
    TabLayout mTlHome;
    @BindView(R.id.vp_home)
    ViewPager mVpHome;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.bt_select)
    Button mBtSelect;
    @BindView(R.id.tv_num)
    TextView mTvNum;
    @BindView(R.id.bt_send)
    Button mBtSend;
    private HomePresenter mHomePresenter;

    @Override
    public void initView() {
        mHomePresenter.initToolbar();
        HomeActivityPermissionsDispatcher.needsWithCheck(this);
//        if (MPermission.requestPermission(this, MPermission.PERMISSION_READ_EXTERNAL_STORAGE, CODE_READ_EXTERNAL_STORAGE)) {
//            mHomePresenter.init();
//        }
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
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSendNum();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void init(List<Fragment> mList, String[] title) {
        HomeAdapter mHomeAdapter = new HomeAdapter(getSupportFragmentManager(), mList, title);
        mVpHome.setAdapter(mHomeAdapter);
        mTlHome.setupWithViewPager(mVpHome);
    }

    @OnClick({R.id.bt_select, R.id.bt_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_select:
                break;
            case R.id.bt_send:
                mHomePresenter.checkType();
                break;
        }
    }

    @Override
    public void toSendActivity() {
        SendActivity.startActivity(HomeActivity.this);
    }

    @Override
    public void toAcceptActivity() {
        AcceptActivity.startActivity(HomeActivity.this);
    }

    @Override
    public void setSendNum() {
        mHomePresenter.setSendNum(mTvNum);
        mHomePresenter.setNoOK();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case CODE_READ_EXTERNAL_STORAGE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mHomePresenter.init();
//                } else {
//                    ToastUtil.show(context, "请开启该权限");
//                }
//                break;
//            }
//        }
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needs() {
        mHomePresenter.init();
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
