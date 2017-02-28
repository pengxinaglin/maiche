package com.haoche51.sales.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.haoche51.sales.BuildConfig;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.service.LocationService;
import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.util.HashMap;

/**
 * Created by lightman_mac on 11/2/15.
 * 统计,不直接调用umeng提供的API,以防被测试环境数据污染
 * <p/>
 * 在这里统一第三方集成
 */
public class ThirdPartInjector {

	/**
	 * 百度push key
	 */
	public final static String BD_PUSH_KEY = "LkTaE7pAZedTjtPCbFcm7TX8";

	/**
	 * 听云 key
	 */
	public final static String TINGYUN_KEY_TEST = "acdb6680fc6a4115b91d3c49587d0f2a";//测试
	/**
	 * 听云 key
	 */
	public final static String TINGYUN_KEY = "2b1a626e9469481eb861a20d38b1d422";//正式

	/**
	 * 集成听云监控
	 *
	 * @param mAct activity
	 */
	public static void startTingyun(Activity mAct) {
		if (BuildConfig.DEBUG_TINGYUN) {//统计正式版本
			NBSAppAgent.setLicenseKey(TINGYUN_KEY).withLocationServiceEnabled(true).start(mAct);
		} else {//统计测试版本
			NBSAppAgent.setLicenseKey(TINGYUN_KEY_TEST).withLocationServiceEnabled(true).start(mAct);
		}
		NBSAppAgent.setUserCrashMessage("userName", GlobalData.userDataHelper.getUser().getName());
		NBSAppAgent.setUserCrashMessage("userPhone", GlobalData.userDataHelper.getUser().getPhone());
	}

	/**
	 * 启动百度推送
	 */
	public static void startBaiduPush() {
		PushManager.startWork(GlobalData.mContext, PushConstants.LOGIN_TYPE_API_KEY, BD_PUSH_KEY);
		HCLogUtil.d(" start baidu push now ....");
	}

	/**
	 * 关闭百度推送
	 */
	public static void stopBaiduPush() {
		PushManager.stopWork(GlobalData.mContext);
		HCLogUtil.d(" stop baidu push now ....");
	}

	/**
	 * 开启百度定位
	 */
	public static void startBaiduLocation() {
		HCLogUtil.d(" start baidu location now ....");
		GlobalData.mContext.startService(new Intent(GlobalData.mContext, LocationService.class));
	}

	/**
	 * 关闭百度定位
	 */
	public static void stopBaiduLocation() {
		HCLogUtil.d(" stop baidu location now ....");
		GlobalData.mContext.stopService(new Intent(GlobalData.mContext, LocationService.class));
	}

	/**
	 * 友盟 页面统计
	 *
	 * @param tag
	 */
	public static void onPageStart(String tag) {
		if (TextUtils.isEmpty(tag)) return;

		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onPageStart(tag);
		}
	}

	/**
	 * 友盟 页面统计
	 *
	 * @param tag
	 */
	public static void onPageEnd(String tag) {
		if (TextUtils.isEmpty(tag)) return;

		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onPageEnd(tag);
		}
	}

	/**
	 * 友盟 页面统计
	 *
	 * @param mAct
	 */
	public static void onResume(Activity mAct) {
		if (mAct == null) return;

		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onResume(mAct);
		}
	}

	/**
	 * 友盟 页面统计
	 *
	 * @param mAct
	 */
	public static void onPause(Activity mAct) {
		if (mAct == null) return;

		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onPause(mAct);
		}
	}

	/**
	 * umeng 页面统计
	 */
//    public static void enableUMUpdateOnlineConfig() {
//        MobclickAgent.updateOnlineConfig(GlobalData.mContext);
//    }

	//禁止默认的页面统计方式，这样将不会再自动统计Activity。 false

	/**
	 * 关闭友盟自动页面统计(基于Activity)
	 */
	public static void disableUMAutoActivityAnalysis() {
		MobclickAgent.openActivityDurationTrack(false);
	}

	/**
	 * 关闭友盟错误统计
	 */
	public static void disableUMCrashAnalysis() {
		MobclickAgent.setCatchUncaughtExceptions(false);
	}

//    public static void setUMUpdateOnlineConfig(Activity mAct) {
//        MobclickAgent.updateOnlineConfig(mAct);
//    }

	/**
	 * 事件统计 带参数
	 *
	 * @param act
	 * @param event
	 * @param map
	 */
	public static void onEvent(Activity act, String event, HashMap map) {
		if (act == null || TextUtils.isEmpty(event)) return;
		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onEvent(act, event, map);
		}
	}

	/**
	 * 事件统计不带参数
	 *
	 * @param act
	 * @param event
	 */
	public static void onEvent(Context act, String event) {
		if (act == null || TextUtils.isEmpty(event)) return;
		if (BuildConfig.ENABLE_UMENG_ANALYTICS) {
			MobclickAgent.onEvent(act, event);
		}
	}


}
