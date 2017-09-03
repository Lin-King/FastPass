package com.linkings.fastpass.utils;

import com.orhanobut.logger.Logger;

/**
 * Created by Lin on 2017/2/21.
 * Time: 10:49
 * Description: TOO 日志打印
 */
public class LogUtil {
    /**
     * 是否将Log输出到控制台
     */
    private static boolean enable = true;


    public static void v(String logTag, String logContent) {
        if (enable) {
            Logger.t(logTag).v(logContent);
        }
    }

    public static void v(String logContent) {
        if (enable) {
            Logger.v(logContent);
        }
    }

    public static void d(String logTag, String logContent) {
        if (enable) {
            Logger.t(logTag).d(logContent);
        }
    }

    public static void d(String logContent) {
        if (enable) {
            Logger.d(logContent);
        }
    }

    public static void i(String logTag, String logContent) {
        if (enable) {
            Logger.t(logTag).i(logContent);
        }
    }

    public static void i(String logContent) {
        if (enable) {
            Logger.i(logContent);
        }
    }

    public static void w(String logTag, String logContent) {
        if (enable) {
            Logger.t(logTag).w(logContent);
        }
    }

    public static void w(String logContent) {
        if (enable) {
            Logger.w(logContent);
        }
    }

    public static void e(String logTag, String logContent) {
        if (enable) {
            Logger.t(logTag).e(logContent);
        }
    }

    public static void e(String logContent) {
        if (enable) {
            Logger.e(logContent);
        }
    }

    public static void json(String logContent) {
        if (enable) {
            Logger.json(logContent);
        }
    }
}
