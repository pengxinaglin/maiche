package com.haoche51.sales.util;

import android.content.Context;
import android.preference.PreferenceManager;

public class PreferenceUtil {

    public static String LAST_CLEAR_VIEWTASTKDB_DATE = "";

	public static String getString(Context context, String key, String defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(key, defValue);
	}

	public static void putString(Context context, String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, value).commit();
	}
	
	public static void delete(Context context, String key) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.remove(key).commit();
	}

	public static int getInt(Context context, String key, int defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				key, defValue);
	}

	public static void putInt(Context context, String key, int value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(key, value).commit();
	}

	public static long getLong(Context context, String key, long defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getLong(
				key, defValue);
	}

	public static void putLong(Context context, String key, long value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putLong(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(key, defValue);
	}

	public static void putBoolean(Context context, String key, boolean value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(key, value).commit();
	}

}
