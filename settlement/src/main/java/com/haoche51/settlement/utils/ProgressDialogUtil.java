package com.haoche51.settlement.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.TextUtils;


/**
 * ProgressDialog工具类
 */

public class ProgressDialogUtil {
    private static ProgressDialog progressDialog;
    private static boolean isShowProgressDialog;


    public static void showProgressDialog(Activity activity, String msg) {
        showProgressDialog(activity, msg, true, null);
    }

    public static void showProgressDialog(Activity activity, String msg, boolean cancelable) {
        showProgressDialog(activity, msg, cancelable, null);
    }

    public static void showProgressDialog(Activity activity, DialogInterface.OnDismissListener dismissListener) {
        showProgressDialog(activity, null, true, dismissListener);
    }

    public static void showProgressDialog(Activity activity, String msg, boolean cancelable, DialogInterface.OnDismissListener dismissListener) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(cancelable);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (TextUtils.isEmpty(msg)) {
            progressDialog.setMessage("处理中...");
        } else {
            progressDialog.setMessage(msg);
        }
        if (dismissListener != null) {
            progressDialog.setOnDismissListener(dismissListener);
        }
        progressDialog.show();
        isShowProgressDialog = true;
    }

    public static void showProgressDialogWithProgress(Activity activity, String title, int maxProgress) {
        if (activity == null) {
            return;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        if (title == null) {
            progressDialog.setTitle("处理中...");
        } else {
            progressDialog.setTitle(title);
        }
        progressDialog.show();
        isShowProgressDialog = true;
    }

    public static void showProgressDialogWithCancel(Activity activity, String title, int maxProgress) {
        if (activity == null) {
            return;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(maxProgress);
        if (title == null) {
            progressDialog.setTitle("处理中...");
        } else {
            progressDialog.setTitle(title);
        }
        progressDialog.show();
        isShowProgressDialog = true;
    }


    public static void setProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    public static void closeProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                isShowProgressDialog = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isShowProgress() {
        return isShowProgressDialog;
    }
}
