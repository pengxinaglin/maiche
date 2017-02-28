package com.haoche51.settlement.utils;


import com.haoche51.settlement.BuildConfig;

/**
 * @File:Log.java
 * @Package:com.haoche51.checker.tools
 * @desc:自定义Log接口
 * @author:zhuzuofei
 * @date:2015-1-24 下午2:11:33
 * @version:V1.0
 */
public class HCLogUtil {

    public static final String TAG = "HC-HaoChe51@572";

    public static void d(String tag, String msg) {
        android.util.Log.d(tag, msg);
//		LogUtils.setLogFile(tag);
//		LogUtils.writeLog2File(msg);
    }

    public static void log(String msg) {
        android.util.Log.d("hclogtag", msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void d(String msg) {
        android.util.Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (BuildConfig.LOG_DEBUG) {
            android.util.Log.w(TAG, msg);
        }
    }
}

