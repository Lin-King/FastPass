package com.linkings.fastpass.config;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Lin on 2017/9/5.
 * Time: 9:43
 * Description: TOO
 */

public class MPermission {
    public static final int CODE_RECORD_AUDIO = 0;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_CALL_PHONE = 3;
    public static final int CODE_CAMERA = 4;
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final int CODE_ACCESS_WIFI_STATE = 9;
    public static final int CODE_WRITE_SETTINGS = 10;
    public static final int CODE_STORAGE = 11;
    public static final int CODE_MULTI_PERMISSION = 100;

    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_SETTINGS = Manifest.permission.WRITE_SETTINGS;
    public static final String PERMISSION_ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;

    public static final String PERMISSION_GROUP_STORAGE = Manifest.permission_group.STORAGE;

    public static boolean requestPermission(Activity activity, String permission, int result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, result);
                return false;
            }
        }
        return true;
    }

    /**
     * 一次申请多个权限
     */
    public static boolean requestMultiPermissions(Activity activity, String[] permissions, int result) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean check = true;
            for (String permission : permissions) {
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    check = false;
                }
            }
            if (!check) {
                ActivityCompat.requestPermissions(activity, permissions, result);
                return false;
            }
        }
        return true;
    }

    public static boolean requestSettingActivity(Activity activity, int result) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                Intent intent = new Intent();
////                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
////                intent.setData(uri);
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                intent.setData(Uri.parse("package:" + activity.getPackageName()));
//                activity.startActivityForResult(intent, result);
//                return false;
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (activity.checkSelfPermission(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_SETTINGS}, result);
//                    return false;
//                }
//            }
//        }

        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(activity);
        } else {
            permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            return true;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, result);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_SETTINGS}, result);
            }
            return false;
        }

    }

}
