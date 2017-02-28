package com.haoche51.record.service;

import android.app.IntentService;
import android.content.Intent;

import com.haoche51.record.dao.PhoneRecordDAO;
import com.haoche51.record.entity.PhoneRecordEntity;
import com.haoche51.record.util.RecordUtil;
import com.haoche51.record.util.UploadCallLogTask;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.UnixTimeUtil;


/***
 * 检查是否需要重启录音service 在RecordService的onCreate方法中注册一个Alarm,在其onDestroy中cancel掉
 * 每隔一定时间执行一次. 执行的内容是检查最新的录音记录和最新的通话记录相差是否为一个小时 如果不是则跳过本地检查,等待执行下一次检查
 * 如果是再去RecordService里检查当前是否在录音, 如果在录音,跳过本地检查,等待执行下一次检查 如果不在录音状态,重启RecordService
 */
public class CheckReLaunchService extends IntentService {

	private final static String TAG = "CheckReLaunchService";

	/***
	 * 查看时间间隔(秒)
	 */
	private final static int CHECK_INTERVAL = RecordUtil.CHECK_INTERVAL;

	public CheckReLaunchService(String name) {
		super(name);
	}

	public CheckReLaunchService() {
		this("CheckReLaunchService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!RecordUtil.isCurrentWorkTime(0)) {
			HCLogUtil.d(TAG, "checkService is not work time ");
			return;
		} else {

			// 从RecordService带是否在录音的状态回来
			if (!intent.getBooleanExtra("isRecording", true)) {
				HCLogUtil.d(TAG, "checkService is not recording reLauch it ");
				RecordUtil.reLaunchService(this);
				return;
			}

			UploadCallLogTask mCallLog = new UploadCallLogTask();
			PhoneRecordEntity mEntity = PhoneRecordDAO.getInstance().getLastRecord();
			if (mEntity != null) {
				long lastCallDate = mCallLog.getNewestCallLog(this);
				long lastRecordDate = mEntity.getCreate_time();
				if (lastCallDate > 0 && lastRecordDate > 0) {
					lastCallDate = lastCallDate / 1000L;
					long diff = lastCallDate - lastRecordDate;
					String rDate = UnixTimeUtil.format((int)lastRecordDate);
					String cDate = UnixTimeUtil.format((int)lastCallDate);
					HCLogUtil.d(TAG, "callDate = " + lastCallDate + " , cDate=" + cDate + ", lastRecordDate = " + lastRecordDate + ", rDate=" + rDate + ", diff = " + diff);
					if (diff > CHECK_INTERVAL) {
						// 前往RecordService查看是否在录音状态
						Intent mIntent = new Intent(this, RecordService.class);
						mIntent.setAction(RecordUtil.REBOOT_SERVICE_ACTION);
						startService(mIntent);
					}
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		HCLogUtil.d(TAG, "CheckRelaunchService   ondestory ");
		super.onDestroy();
	}
}