package com.haoche51.record.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;

import com.haoche51.record.service.RecordService;
import com.haoche51.record.util.RecordUtil;
import com.haoche51.sales.util.HCLogUtil;


/***
 * 1.监听系统广播事件(开机启动,解锁),启动后台常驻的录音service
 */
public class PhoneSatusReceiver extends BroadcastReceiver {

	private final static String TAG = "PhoneSatusReceiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		HCLogUtil.d(TAG, "Receiver pid = " + android.os.Process.myPid() + " Receive  action " + intent.getAction());
		// SCREEN_ON和SCREEN_OFF 只能通过代码注册,改用ACTION_USER_PRESENT
		if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_USER_PRESENT.equals(action) || Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
			Intent mIntent = new Intent(context, RecordService.class);
			if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
				String dialNum = getResultData();
				if (!TextUtils.isEmpty(dialNum)) {
					mIntent.putExtra("dialNum", dialNum);
					HCLogUtil.d(TAG, "receiver dialNum = " + dialNum);
				}
			}
			context.startService(mIntent);
		}

		if (RecordUtil.REBOOT_SERVICE_ACTION.equals(action)) {
			new Thread() {
				public void run() {
					SystemClock.sleep(100);
					HCLogUtil.d("RecordService", "重启录音服务");
					context.startService(new Intent(context, RecordService.class));
				}
			}.start();
		}
	}
}
