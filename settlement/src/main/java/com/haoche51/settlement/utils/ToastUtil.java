package com.haoche51.settlement.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    static Toast toast;

    /**
     * 去掉context参数，，，因为会导致内存泄漏
     *
     * @param text text
     */
    public static void showInfo(Context context, String text) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
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


    public static void showTextLong(Context context, String text) {
        if (toast == null)
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        else
            toast.setText(text);

        toast.show();
    }

}
