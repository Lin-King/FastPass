package com.linkings.fastpass.adapter;

import android.net.wifi.ScanResult;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.linkings.fastpass.R;

import java.util.List;

import static com.linkings.fastpass.wifitools.WifiMgr.NO_PASSWORD;
import static com.linkings.fastpass.wifitools.WifiMgr.NO_PASSWORD_WPS;

/**
 * Created by Lin on 2017/9/3.
 * Time: 15:50
 * Description: TOO
 */

public class AcceptAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {

    public AcceptAdapter(@Nullable List<ScanResult> data) {
        super(R.layout.item_wifilist, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScanResult scanResult) {
        helper.setText(R.id.tv_name, scanResult.SSID);
        if (scanResult.capabilities != null && scanResult.capabilities.equals(NO_PASSWORD) || scanResult.capabilities != null && scanResult.capabilities.equals(NO_PASSWORD_WPS)) {
            helper.setImageResource(R.id.iv_icon, R.mipmap.ic_signal_wifi_4_bar_red_100_24dp);
        } else {
            helper.setImageResource(R.id.iv_icon, R.mipmap.ic_signal_wifi_4_bar_lock_red_100_24dp);
        }
    }
}
