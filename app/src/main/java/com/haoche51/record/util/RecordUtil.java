package com.haoche51.record.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.haoche51.record.service.RecordService;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.util.HCLogUtil;

import java.io.File;
import java.util.Calendar;

/**
 * Created by mac on 15/10/26.
 */
public class RecordUtil {

	/**
	 * 重启服务条件 查看时间间隔(秒) 当最近一条通话记录和最新一条录音记录差值大于这个值, 认为有不可知的异常发生,重启录音服务
	 */
	public final static int CHECK_INTERVAL = 60 * 60;

	public final static int WORK_START_HOUR = 8;
	public final static int WORK_START_MINUTE = 0;
	public final static int WORK_START_SECOND = 0;

	public final static int WORK_END_HOUR = 22;
	public final static int WORK_END_MINUTE = 59;
	public final static int WORK_END_SECOND = 59;

	/**
	 * 记录最后一条通话记录的id
	 *
	 * @param _id
	 */
	public static void putLastUploadRecordID(int _id) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalData.mContext);
		sp.edit().putInt("lastUploadId", _id).apply();
	}

	/**
	 * 记录最后一条通话记录的id
	 *
	 * @return
	 */
	public static int getLastUploadRecordID() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalData.mContext);
		return sp.getInt("lastUploadId", 0);
	}

	public static void saveFileUploaded(Context context, File file) {
		SharedPreferences sp = context.getSharedPreferences("file_upload", Context.MODE_PRIVATE);
		sp.edit().putBoolean(file.getAbsolutePath(), true).commit();
	}

	public static boolean getFileUploadStatus(Context context, File file) {
		SharedPreferences sp = context.getSharedPreferences("file_upload", Context.MODE_PRIVATE);
		return sp.getBoolean(file.getAbsolutePath(), false);
	}

	public static void setRecordingFile(Context context, String fileName) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putString("recordingFile", fileName).apply();
	}

	public static String getRecordingFile(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString("recordingFile", "");
	}

	public static void reLaunchService(final Context mContext) {
		new Thread() {
			public void run() {
				HCLogUtil.d("CheckReLaunchService", "reLaunchService pid" + android.os.Process.myPid());
				Intent service = new Intent(mContext, RecordService.class);
				mContext.stopService(service);
			}

		}.start();
	}

	public static boolean isCurrentWorkTime(long currentTimeMillis) {
		if (currentTimeMillis == 0)
			currentTimeMillis = System.currentTimeMillis();
		boolean result = false;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DATE);
		cal.set(year, month, day, WORK_START_HOUR, WORK_START_MINUTE, WORK_START_SECOND);
		long start = cal.getTimeInMillis();
		cal.set(year, month, day, WORK_END_HOUR, WORK_END_MINUTE, WORK_END_SECOND);
		long end = cal.getTimeInMillis();
		result = currentTimeMillis > start && currentTimeMillis < end;
		return result;
	}

	public static String getVisibleToday() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		StringBuilder sb = new StringBuilder();
		sb.append(year).append("_").append(month).append("_").append(day);
		return sb.toString();
	}


	public static final String REBOOT_SERVICE_ACTION = "reboot_service";

	public static void sendStartServiceBroadCast(Context mContext) {
		Intent mIntent = new Intent();
		mIntent.setAction(REBOOT_SERVICE_ACTION);
		mContext.sendBroadcast(mIntent);
	}
}
