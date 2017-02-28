package com.haoche51.record.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;


import com.haoche51.record.service.CheckReLaunchService;
import com.haoche51.sales.util.HCLogUtil;

import java.util.List;

/**
 * Created by mac on 15/10/26.
 */
public class AlarmUtil {

	/**
	 * 检查广播requestCode
	 */
	public final static int CHECK_ALARM_REUQEST_CODE = 111;
	public final static int CHECK_IMPOR_REUQEST_CODE = 112;

	/**
	 * 是否开启轮询检查
	 */
	public final static boolean IS_HOUR_CHECKING_ON = true;

	/**
	 * alarm间隔时间(毫秒) 检查是否需要重启服务
	 */
	private final static long INTERVAL_MILLIS = 1000 * 60 * 60;

	/**
	 * 检查录音是否异常
	 *
	 * @param mContext
	 */
	public static void startCheckAlarm(Context mContext) {
		if (!IS_HOUR_CHECKING_ON)
			return;

		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		long triggerAtMillis = SystemClock.elapsedRealtime() + INTERVAL_MILLIS;
		long intervalMillis = INTERVAL_MILLIS;
		Intent rebootIntent = new Intent(mContext, CheckReLaunchService.class);
		PendingIntent operation = PendingIntent.getService(mContext, CHECK_ALARM_REUQEST_CODE, rebootIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setRepeating(type, triggerAtMillis, intervalMillis, operation);
		HCLogUtil.d("CheckReLaunchService", "Utils startCheckAlarm---");
	}

	public static void cancelCheckAlarm(Context mContext) {

		if (!IS_HOUR_CHECKING_ON)
			return;

		AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent rebootIntent = new Intent(mContext, CheckReLaunchService.class);
		PendingIntent operation = PendingIntent.getService(mContext, CHECK_ALARM_REUQEST_CODE, rebootIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.cancel(operation);
		HCLogUtil.d("CheckReLaunchService", "Utils cancel CheckAlarm+++++");
	}




	public static final int RETRIVE_SERVICE_COUNT = 50;
	/** 检测服务是否在运行，是否需要重启 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(RETRIVE_SERVICE_COUNT);

		if(null == serviceInfos || serviceInfos.size() < 1) {
			return false;
		}

		for(int i = 0; i < serviceInfos.size(); i++) {
			if(serviceInfos.get(i).service.getClassName().contains(className)) {
				isRunning = true;
				break;
			}
		}
		HCLogUtil.e("ServiceUtil-AlarmManager", className + " isRunning =  " + isRunning);
		return isRunning;
	}
}
