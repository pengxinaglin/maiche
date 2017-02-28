package com.haoche51.settlement.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @File:SharedPreferencesUtils.java
 * @Package:com.haoche51.checker.tools
 * @desc:本地保存数据工具类
 * @author:zhuzuofei
 * @date:2015-1-26 上午10:51:03
 * @version:V1.0
 */
public class SharedPreferencesUtils {

    private static final String SHAREDNAME = "haoche51App";
    public static final String KEY_POSPAY_LOGIN_NAME = "key_pospay_login_name";

    public static void saveData(Context context, String key, Object value) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, Float.parseFloat(value.toString()));
        } else if (value instanceof Integer) {
            editor.putInt(key, Integer.parseInt(value.toString()));
        } else if (value instanceof Long) {
            editor.putLong(key, Long.parseLong(value.toString()));
        } else {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void removeData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        if (sp.contains(key))
            sp.edit().remove(key).commit();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        return sp.getString(key, defaultValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        return sp.getBoolean(key, defaultValue);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        return sp.getFloat(key, defaultValue);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        return sp.getInt(key, defaultValue);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SHAREDNAME, 0);
        return sp.getLong(key, defaultValue);
    }

}
