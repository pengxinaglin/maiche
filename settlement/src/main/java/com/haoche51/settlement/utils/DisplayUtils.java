package com.haoche51.settlement.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.text.DecimalFormat;

public class DisplayUtils {
    private static final String TAG = "DisplayUtils";


    /**
     * dipתpx
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * pxתdip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



    /**
     * 获取屏幕metrics
     *
     * @param context
     * @return
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
        return new Point(w_screen, h_screen);

    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }


    /**
     * 获取屏幕DPI
     *
     * @param context
     * @return
     */
    public static int getDisplayDensity(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.densityDpi;
    }



    /**
     * 获取评估长宽比
     *
     * @param context
     * @return
     */
    public static float getScreenRate(Context context) {
        Point P = getScreenMetrics(context);
        float H = P.y;
        float W = P.x;
        return (H / W);
    }


    /***
     * view展开效果
     */
    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(
                (int) (targetHeight * 5 / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /***
     * 收缩效果
     */
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration(
                (int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static String parseMoney(String pattern, double bd) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(bd);
    }


    public static String getImageUrl(String url, int width, int hight) {
        String result = "";
        if (url.endsWith("?")) {
            result = url + "imageView2/2/w/" + width + "/h/" + hight;
        } else {
            result = url + "?imageView2/2/w/" + width + "/h/" + hight;
        }
        return result;
    }

    public static String convertImageURL(String url, int w, int h) {
        url = TextUtils.isEmpty(url) ? "" : url;
        String str = new StringBuilder(url).append("?imageView2")
                .append("/1/w/")
                .append(w)
                .append("/h/")
                .append(h)
                .toString();
        return str;
    }

    public static String averageImageURL(String url, int w, int h) {
        url = TextUtils.isEmpty(url) ? "" : url;
        String str = new StringBuilder(url).append("?imageView2")
                .append("/0/w/")
                .append(w)
                .append("/h/")
                .append(h)
                .toString();
        return str;
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void setActivityBackgroundAlpha(Activity mActivity, float bgAlpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        mActivity.getWindow().setAttributes(lp);
    }

}
