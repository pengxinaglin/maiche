package com.haoche51.sales;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.haoche51.sales.constants.ThirdPartyConst;
import com.haoche51.sales.hcmessage.MessageMainActivity;
import com.haoche51.sales.helper.UserDataHelper;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.ThirdPartInjector;

import java.util.List;

/**
 * Created by xuhaibo on 15/9/9.
 */
public class HCPushMessageReceiver extends PushMessageReceiver implements HCHttpCallback {

	@Override
	public void onBind(Context context, int errorCode, String appId, String userId, String channelId, String requestId) {
		//百度绑定会有返回null 的情况
		String logString = "OnBind: errorCode=" + errorCode + " appid=" + appId + " userId=" + userId + " changnelId=" + channelId + " requestId =" + requestId;
		HCLogUtil.e("push", logString);
		if (errorCode == 0 && userId != null && channelId != null) {
			/**1.服务器未绑定
			 * 2.本地没有存储的channelid，没有userid，
			 * 3. 本地的channelid ，userid 和 百度返回的不一致。
			 * 都要重新绑定**/

			if (GlobalData.userDataHelper.getPushId() == 0 ||
					TextUtils.isEmpty(GlobalData.userDataHelper.getPushChannelId()) ||
					!GlobalData.userDataHelper.getPushChannelId().equals(channelId) ||
					TextUtils.isEmpty(GlobalData.userDataHelper.getPushUserId()) ||
					!GlobalData.userDataHelper.getPushUserId().equals(userId)
					) { // 没有发送过
				GlobalData.userDataHelper.setPushUserId(userId).setPushChannelId(channelId).commit();
				AppHttpServer.getInstance().post(HCHttpRequestParam.bind(userId, channelId), HCPushMessageReceiver.this, 0);
			}
		}
	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String logString = "onUnbind: errorCode=" + errorCode + " requestId=" + requestId;
		HCLogUtil.e("push", logString);
		UserDataHelper mUserDataHelper = new UserDataHelper(context.getApplicationContext());
		mUserDataHelper.clearPushUserId().clearPushChannelId().unbindBaiduPush().commit();
	}

	@Override
	public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

	}

	@Override
	public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

	}

	@Override
	public void onListTags(Context context, int i, List<String> list, String s) {

	}

	@Override
	public void onMessage(Context context, String message, String customContentString) {

	}

	@Override
	public void onNotificationClicked(Context context, String title, String description, String content) {
		HCLogUtil.e("push", "onNotificationClick " + title);
		Intent intent = new Intent();
		intent.setClass(context.getApplicationContext(), MessageMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.getApplicationContext().startActivity(intent);
		ThirdPartInjector.onEvent(context, ThirdPartyConst.MESSAGE_RECEIVER_CLICK);
	}

	@Override
	public void onNotificationArrived(Context context, String s, String s1, String s2) {

	}

	/**
	 * 网络请求结束,请求服务器成功，请求服务器失败
	 *
	 * @param action   当前请求action
	 * @param response hc 请求结果
	 * @param error    网络问题造成failed 的error
	 */
	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		switch (response.getErrno()) {
			case 0:
				if (!TextUtils.isEmpty(response.getData())) {
					int pushId = Integer.parseInt(response.getData());
					HCLogUtil.e("push", "response from server！");
					GlobalData.userDataHelper.setPushId(pushId).bindBaiduPush().commit();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void onHttpFinish(String action, int requestId) {

	}

	/**
	 * 网络请求开始
	 *
	 * @param action 当前请求action
	 */
	@Override
	public void onHttpStart(String action, int requestId) {

	}

	/**
	 * 网络请求进度
	 *
	 * @param action       当前请求action
	 * @param bytesWritten
	 * @param totalSize
	 */
	@Override
	public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

	}

	/***
	 * 重试次数回调
	 *
	 * @param action  当前请求action
	 * @param retryNo 重试次数
	 */
	@Override
	public void onHttpRetry(String action, int requestId, int retryNo) {

	}
}
