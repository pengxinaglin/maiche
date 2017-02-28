package com.haoche51.sales;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.baidu.location.LocationClient;
import com.haoche51.sales.activity.user.LoginActivity;
import com.haoche51.sales.constants.BroadcastConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;
import com.haoche51.sales.util.ThirdPartInjector;
import com.haoche51.settlement.PayDebug;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.study.sangerzhong.studyapp.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

public class HCApplication extends Application {

	private static Context instance;
	/**
	 * 百度定位 核心类
	 */
	public static LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        PayDebug.initEviroment(Debug.EVIROMENT);
        HCLogUtil.e("HCApplication", "------Application------onCreate------");
        instance = getApplicationContext();
        //必要的初始化资源操作
        mLocationClient = new LocationClient(getApplicationContext());
        GlobalData.init(getApplicationContext());
        initImageLoader(getApplicationContext());
        intallLeakCanary();
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }

	public static Context getContext() {
		return instance;
	}

	@SuppressWarnings("deprecation")
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "checker/Cache");//获取到缓存的目录地址
		HCLogUtil.d("cacheDir", cacheDir.getPath());
		//创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				.memoryCacheExtraOptions(1200, 900) // max width, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(5)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				//.denyCacheImageMultipleSizesInMemory() // 默认行为允许缓存多个大小的文件
				.memoryCache(new UsingFreqLimitedMemoryCache(12 * 1024 * 1024)) // You can pass your own memory cache implementation
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(100 * 1024 * 1024)
				.discCacheFileCount(100) //缓存的File数量
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				//.discCache(new LimitedAgeDiscCache(cacheDir,3600))//自定义缓存1个小时，1个小时后删除
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				//.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				//.writeDebugLogs() // Remove for release app
				.build();

		ImageLoader.getInstance().init(config);//全局初始化此配置
	}

	public static void logout() {
		GlobalData.userDataHelper.clearPushId().clearPushUserId().clearPushChannelId().unbindBaiduPush().clearLogin().clearUser().clearLastCheckerId().clearUserRight().commit();
		ACache aCache = ACache.get(getContext());
		aCache.clear();
		SharedPreferencesUtils.removeData("hc_share_qrcode");
		String myQRCodePath = Environment.getExternalStorageDirectory() + "/myqrcode.png";
		if (new File(myQRCodePath).exists()) {
			new File(myQRCodePath).delete();
		}
		//关闭服务
		ThirdPartInjector.stopBaiduLocation();
		ThirdPartInjector.stopBaiduPush();
		//清除所有的页面
		Intent intentFinish = new Intent();
		intentFinish.setAction(BroadcastConstants.Broadcast.ACTION_FINISH_MAIN);
		HCApplication.getContext().sendBroadcast(intentFinish);
		HCActionUtil.launchActivity(getContext(), LoginActivity.class, null);
	}

	private RefWatcher intallLeakCanary() {
		if (BuildConfig.DEBUG_LEAK_CANARY) {
			return LeakCanary.install(this);
		}
		return null;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	/**
	 * @return null may be returned if the specified process not found
	 */
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
			if (procInfo.pid == pid) {
				return procInfo.processName;
			}
		}
		return null;
	}
}