package com.haoche51.sales.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 服务信息工具类
 * Created by wufx on 2016/1/5.
 */
public class ServiceInfoUtil {
  /**
   * 判断某个服务是否正在运行
   *
   * @param mContext    上下文对象
   * @param serviceName 是包名+服务的类名（例如：com.haoche51.checker.service.UploadCheckTaskService）
   * @return true代表正在运行，false代表服务不再运行
   */

  public static final String TAG = "UtilsHelper";

  public static boolean isServiceRunning(Context mContext, String serviceName) {
    boolean isRunning = false;
    ActivityManager myAM = (ActivityManager) mContext
      .getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(Integer.MAX_VALUE);
    if (myList.size() <= 0) {
      return false;
    }

    for (int i = 0; i < myList.size(); i++) {
      if (myList.get(i).service.getClassName().equals(serviceName)) {
        isRunning = true;
        break;
      }
    }
    return isRunning;
  }
}
