package com.linkings.fastpass.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.linkings.fastpass.utils.LogUtil;

/**
 * Created by Lin on 2017/9/4.
 * Time: 17:40
 * Description: TOO
 */

public abstract class WifiAPBroadcastReceiver extends BroadcastReceiver {
    //WIFI AP state action
    public static final String ACTION_WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_WIFI_AP_STATE_CHANGED)) { //Wifi AP state changed
            // get Wi-Fi Hotspot state here
            //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启 
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            LogUtil.i("Wifi Ap state--->>>" + state);
            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                // Wifi is enabled
                onWifiApEnabled();
            } else if (WifiManager.WIFI_STATE_ENABLING == state % 10) {
                // Wifi is enabling
            } else if (WifiManager.WIFI_STATE_DISABLED == state % 10) {
                // Wifi is disable
            } else if (WifiManager.WIFI_STATE_DISABLING == state % 10) {
                // Wifi is disabling
            }
        }
    }

    /**
     * User: Lin
     * Date: 2017/9/4 17:41
     * Description: 热点开启回调
     */
    public abstract void onWifiApEnabled();
}
