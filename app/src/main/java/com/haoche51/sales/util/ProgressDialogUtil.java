package com.haoche51.sales.util;

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

	public static void showProgressDialog(Activity activity) {
		showProgressDialog(activity, null, null);
	}

	public static void showProgressDialog(Activity activity, String msg) {
		showProgressDialog(activity, msg, null);
	}

	public static void showProgressDialog(Activity activity, String msg, DialogInterface.OnDismissListener dismissListener) {
		if (activity == null && activity.isFinishing()) {
			return;
		}
		//先关闭 避免重复
		if (isShowProgressDialog) {
			closeProgressDialog();
		}
		progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		if (dismissListener != null) {
			progressDialog.setOnDismissListener(dismissListener);
		}
		if (TextUtils.isEmpty(msg)) {
			progressDialog.setMessage("处理中...");
		} else {
			progressDialog.setMessage(msg);
		}
		if (!progressDialog.isShowing()) {
			//判断Activity状态
			if (!activity.isFinishing() && !activity.isDestroyed()) {
				progressDialog.show();
				isShowProgressDialog = true;
			}
		}
	}

	public static void showProgressDialogWithProgress(Activity activity, String title, int maxProgress) {
		if (activity == null) {
			return;
		}
		//先关闭 避免重复
		if (isShowProgressDialog) {
			closeProgressDialog();
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
		//先关闭 避免重复
		if (isShowProgressDialog) {
			closeProgressDialog();
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


	public static void setProgress(int progress) {
		if (progressDialog != null) {
			progressDialog.setProgress(progress);
		}
	}

	public static void closeProgressDialog() {
		try {
			if (progressDialog != null) {
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
