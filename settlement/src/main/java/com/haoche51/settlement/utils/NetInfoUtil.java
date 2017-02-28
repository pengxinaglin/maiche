package com.haoche51.settlement.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

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
  public static long getTotalTxBytes(Context context) {
    return TrafficStats.getUidTxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() / 1024);//转为KB
  }

  /**
   * 检测网络连接是否可用
   */
  public static boolean isNetAvaliable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null) {
      return networkInfo.isAvailable();
    }
    return false;
  }

  public static boolean isNetConnected(Context ctx) {
    boolean netSataus = false;
    ConnectivityManager mConnectManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

    if (mConnectManager.getActiveNetworkInfo() != null) {
      netSataus = mConnectManager.getActiveNetworkInfo().isConnected();
    }

    return netSataus;
  }

  /**
   * 获取当前网络类型 Wifi ，4G ,3G, 2G
   *
   * @return String netType网络类型
   */
  public static String getNetworkType(Context context) {
    String netType = "unknow";
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
