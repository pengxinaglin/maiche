package com.haoche51.sales.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.constants.HCConst;

public class HCUtils {


  /**
   * 判断当前是否有可用网络
   **/
  public static boolean isNetAvailable() {
    ConnectivityManager mConnectManager =
      (ConnectivityManager) GlobalData.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = mConnectManager.getActiveNetworkInfo();
    if (info != null) return info.isAvailable();

    return false;
  }

  /**
   * server服务是否可用
   */
  public static boolean isServerAvailable() {
    if (isNetAvailable()) {
      Process p;
      try {
        p = Runtime.getRuntime().exec("ping -c 1 " + Debug.APP_SERVER);
        int status = p.waitFor(); //status 只能获取是否成功，无法获取更多的信息
        //成功
//失败
        return status == 0;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    } else {
      return false;
    }

  }


//  public static int getUserDeviceId() {
//    return GlobalData.userDataHelper.getUserDiviceId();
//  }

  /**
   * 获取网络环境
   *
   * @return 网络环境3种 0：默认空 1：wifi 2:4G/3G/2G 3：网络异常
   */
  public static int getNetType() {

    ConnectivityManager connectMgr = (ConnectivityManager) GlobalData.mContext
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectMgr.getActiveNetworkInfo();
    if (info == null) {
      return HCConst.ReadyInfo.READYINFO_NET_TYPE_EXCEPTION;
    }
    if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
      return HCConst.ReadyInfo.READYINFO_NET_TYPE_WIFI;
    }
    if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
      return HCConst.ReadyInfo.READYINFO_NET_TYPE_MOBILE;
    }
    return HCConst.ReadyInfo.READYINFO_NET_TYPE_NONE;
  }

}
