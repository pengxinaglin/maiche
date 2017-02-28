package com.haoche51.record.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by mac on 15/10/26.
 */
public class SupportUtil {

	/**
	 * 获取手机号
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		if (NativePhoneNumber != null && !"".equals(NativePhoneNumber) && NativePhoneNumber.length() > 11) {
			NativePhoneNumber = NativePhoneNumber.substring(NativePhoneNumber.length() - 11, NativePhoneNumber.length());
		} else {
			NativePhoneNumber = null;
		}
		return NativePhoneNumber;
	}

	/**
	 * 获取VersionCode，用于更新版本使用
	 *
	 * @param context
	 * @return pi.versionCode
	 */
	public static int getVersionCode(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi.versionCode;
	}

	/**
	 * 获取VersionName，用于显示给用户看
	 *
	 * @param context
	 * @return pi.versionName
	 */
	public static String getVersionName(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi.versionName;
	}

	/**
	 * 判断当前网络状态是否为wifi 并且连接
	 */
	public static boolean isCurrentNetWifi(Context mContext) {
		ConnectivityManager mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (wifiInfo != null && wifiInfo.isConnected() && NetworkInfo.State.CONNECTED.equals(wifiInfo.getState()));
	}

	/**
	 * 移动3G(中国制式)
	 */
	private static final String NEWWORK_TD_SCDMA = "TD-SCDMA";
	/**
	 * 联通3G(欧洲制式)
	 */
	private static final String NEWWORK_WCDMA = "WCDMA";
	/**
	 * 电信3G(美国制式)
	 */
	private static final String NEWWORK_CDMA2000 = "CDMA2000";

	/**
	 * 判断当前网络状态是否为WIFI/3G/4G 并且已连接
	 */
	public static boolean isCurrentNetWifiOr3gOr4g(Context mContext) {
		boolean available = false;
		NetworkInfo networkInfo = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
				//WIFI
				return true;
				//其他网络类型
			else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				//网络名称
				String _strSubTypeName = networkInfo.getSubtypeName();
				switch (networkInfo.getSubtype()) {
					case TelephonyManager.NETWORK_TYPE_UMTS:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_HSDPA:
					case TelephonyManager.NETWORK_TYPE_HSUPA:
					case TelephonyManager.NETWORK_TYPE_HSPA:
					case TelephonyManager.NETWORK_TYPE_EVDO_B:
					case TelephonyManager.NETWORK_TYPE_EHRPD:
					case TelephonyManager.NETWORK_TYPE_HSPAP:
						available = true;//3G
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:
						available = true;//4G
						break;
					default:
						//中国移动/联通/电信 3G
						if (_strSubTypeName.equalsIgnoreCase(NEWWORK_TD_SCDMA) || _strSubTypeName.equalsIgnoreCase(NEWWORK_WCDMA)
							|| _strSubTypeName.equalsIgnoreCase(NEWWORK_CDMA2000)) {
							available = true;
						}
						break;
				}
			}
		}
		//网络状态为WiFi/3G/4G 并且已连接
		return available && (networkInfo != null && networkInfo.isConnected() && NetworkInfo.State.CONNECTED.equals(networkInfo.getState()));
	}

	public static boolean isNetAvailable(Context mContext) {
		ConnectivityManager mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectManager.getActiveNetworkInfo();
		if (info != null)
			return info.isAvailable();

		return false;
	}

}
