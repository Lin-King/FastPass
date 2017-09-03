package com.linkings.fastpass.app;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.orhanobut.logger.Logger;

/**
 * Created by Lin on 2017/2/21.
 * Time: 13:29
 * Description: TOO
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public MyApplication() {
    }

    // 单例模式中获取唯一的LBSApp 实例
    public static MyApplication getInstance() {
        if (null == mInstance) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder bd = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(bd.build());
        }
        Logger.init("Lin");
    }
}
