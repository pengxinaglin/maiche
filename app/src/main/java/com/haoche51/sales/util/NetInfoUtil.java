package com.haoche51.sales.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

import com.haoche51.sales.GlobalData;

/**
 * 网络信息工具类
 * Created by wufx on 2016/1/5.
 */
public class NetInfoUtil {
    /**
     * 获取应用程序所在网络的总上传字节数，单位:KB
     *
     * @return
     */
    public static long getTotalTxBytes() {
        return TrafficStats.getUidTxBytes(GlobalData.mContext.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() / 1024);//转为KB
    }

    /**
     * 检测网络连接是否可用
     */
    public static boolean isNetAvaliable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断网络是否已经连接正常
     */
    public static boolean isNetConnected(Context context) {
        boolean networkSataus = false;
        if (context == null) {
            return networkSataus;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                networkSataus = networkInfo.isConnected();
            }
        }

        return networkSataus;
    }

    /**
     * 获取当前网络类型 Wifi ，4G ,3G, 2G
     *
     * @return String netType网络类型
     */
    public static String getNetworkType() {
        String netType = "unknow";
        ConnectivityManager connectivityManager = (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = networkInfo.getTypeName();
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                netType = networkInfo.getSubtypeName();
            }
        }
        return netType;
    }
}
