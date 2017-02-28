package com.haoche51.sales.util;

import android.content.Context;
import android.widget.Toast;

import com.haoche51.sales.GlobalData;

public class ToastUtil {

  static Toast toast;

  /**
   * 去掉context参数，，，因为会导致内存泄漏
   *
   * @param text text
   */
  public static void showInfo(String text) {
    if (toast == null)
      toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_SHORT);
    else
      toast.setText(text);
    toast.show();
  }

  public static void showNetError(Context context) {
    if (context == null) return;
    if (toast == null)
      toast = Toast.makeText(context, "网络不给力,请稍后再试！", Toast.LENGTH_SHORT);
    else
      toast.setText("网络不给力,请稍后再试！");
    toast.show();
  }

  public static void showServerError(Context context) {
    if (context == null) return;
    if (toast == null)
      toast = Toast.makeText(context, "服务器错误,请重试!", Toast.LENGTH_SHORT);
    else
      toast.setText("服务器错误,请重试!");

    toast.show();
  }

  public static void showText(String text) {
    if (toast == null)
      toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_SHORT);
    else
      toast.setText(text);

    toast.show();
  }

  public static void showTextLong(String text) {
    if (toast == null)
      toast = Toast.makeText(GlobalData.mContext, text, Toast.LENGTH_LONG);
    else
      toast.setText(text);

    toast.show();
  }

  public static void showText(int stringRes) {
    if (toast == null)
      toast = Toast.makeText(GlobalData.mContext, GlobalData.mContext.getResources().getString(stringRes), Toast.LENGTH_SHORT);
    else
      toast.setText(GlobalData.mContext.getResources().getString(stringRes));

    toast.show();
  }
}
