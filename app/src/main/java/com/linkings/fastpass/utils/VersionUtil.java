package com.linkings.fastpass.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Lin on 2016/9/21.
 * Time: 17:22
 * Description: TOO app版本工具
 */

public class VersionUtil {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 版本比较
     *
     * @param nowVersion    app版本
     * @param serverVersion 服务器版本
     * @return
     */
    public static boolean compareVersion(String nowVersion, String serverVersion) {

        if (nowVersion != null && serverVersion != null) {
            String[] nowVersions = nowVersion.split("\\.");
            String[] serverVersions = serverVersion.split("\\.");
            if (nowVersions != null && serverVersions != null && nowVersions.length > 1 && serverVersions.length > 1) {
                int nowVersionsFirst = Integer.parseInt(nowVersions[0]);
                int serverVersionFirst = Integer.parseInt(serverVersions[0]);
                int nowVersionsSecond = Integer.parseInt(nowVersions[1]);
                int serverVersionSecond = Integer.parseInt(serverVersions[1]);
                int nowVersionsThree = Integer.parseInt(nowVersions[2]);
                int serverVersionThree = Integer.parseInt(serverVersions[2]);
                if (nowVersionsFirst < serverVersionFirst) {
                    return true;
                } else if (nowVersionsFirst == serverVersionFirst && nowVersionsSecond < serverVersionSecond) {
                    return true;
                } else if (nowVersionsFirst == serverVersionFirst && nowVersionsSecond == serverVersionSecond
                        && nowVersionsThree < serverVersionThree) {
                    return true;
                }
            }
        }
        return false;
    }
}
