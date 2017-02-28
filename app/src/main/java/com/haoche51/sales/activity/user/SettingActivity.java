package com.haoche51.sales.activity.user;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoche51.record.entity.CallLogEntity;
import com.haoche51.record.helper.CallLogHelper;
import com.haoche51.record.util.RecordUtil;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.HCApplication;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.dialog.AlertDialog;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.service.AutoUpdateVersionService;
import com.haoche51.sales.service.UpdateVersionEntity;
import com.haoche51.sales.util.DeviceInfoUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.FileDownloadUtil;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.NetInfoUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;

/**
 * 设置页面
 * Created by yangming on 2015/11/25.
 */
public class SettingActivity extends CommonStateActivity {

	@ViewInject(R.id.iv_user_photo)
	private ImageView imageViewFace;
	@ViewInject(R.id.tv_layout_user_setting_version)
	private TextView textViewVersion;

	@Override
	protected int getContentView() {
		return R.layout.activity_seting;
	}

	@Override
	protected void initView() {
		super.initView();

		setScreenTitle("设置");

		if (!TextUtils.isEmpty(GlobalData.userDataHelper.getUser().getPic())) {
			DisplayUtils.setUserFace(imageViewFace, GlobalData.userDataHelper.getUser().getPic());
		}

		try {
			String versionString = getPackageManager().getPackageInfo("com.haoche51.sales", 0).versionName.toString();
			textViewVersion.setText(versionString);
		} catch (PackageManager.NameNotFoundException e) {
			textViewVersion.setText("Unknow");
			e.printStackTrace();
		}
	}


	@OnClick(R.id.tv_layout_user_setting_qrcode)
	private void qrcodeClick(View view) {
		HCActionUtil.launchActivity(this, QRShareActivity.class, null);
	}

	@OnClick(R.id.tv_layout_user_setting_pwd)
	private void pwdClick(View view) {
		HCActionUtil.launchActivity(this, ChangePwdActivity.class, null);
	}

	@OnClick(R.id.ll_layout_user_setting_update)
	private void checkVersionClick(View view) {
		checkUpdate();
	}

	@OnClick(R.id.tv_layout_user_setting_logout)
	private void logoutClick(View view) {
		AlertDialog.createNormalDialog(this, "确认退出登录？", "取消", "确认", true, new AlertDialog.OnDismissListener() {
			@Override
			public void onDismiss(Bundle data) {
				if (data != null) {
					boolean determine = data.getBoolean("determine");
					if (determine) {
						//确认 TODO
						ProgressDialogUtil.showProgressDialog(SettingActivity.this, getString(R.string.cancellation));
						uploadCallLog();
					} else {
						//取消
					}
				}
			}
		});
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		super.onHttpComplete(action, requestId, response, error);
		if (action.equals(HttpConstants.ACTION_UNBIND_BAIDUPUSH)) {//退出，取消百度推送
			onLogoutResult(response);
			ProgressDialogUtil.closeProgressDialog();
		} else if (action.equals(HttpConstants.ACTION_UPLOADCALLLOGS)) {//上传通话记录
			doUploadCallLogsResponse(response, requestId);
		} else if (HttpConstants.ACTION_CHECK_VERSION.equals(action)) {
			switch (response.getErrno()) {
				case 0:
					parseUpdateVersion(response.getData());
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					break;
			}
		}
	}

	private void doUploadCallLogsResponse(HCHttpResponse response, int requestId) {
		switch (response.getErrno()) {
			case 0:
				RecordUtil.putLastUploadRecordID(requestId);
				mHandler.sendEmptyMessage(UPLOAD_SUCCESS); // 发送成功
				break;
			default:
				HCLogUtil.d("----response from app server error----" + response.getData());
				mHandler.sendEmptyMessage(UPLOAD_FAILED);
				break;
		}
	}

	/**
	 * 退出登录,，取消百度推送
	 */
	private void onLogoutResult(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				HCApplication.logout();
				break;
			default:
				ToastUtil.showText(response.getErrmsg());
				break;
		}
	}


	public void checkUpdate() {
		if (!NetInfoUtil.isNetAvaliable()) {
			ToastUtil.showInfo("当前网络不可用，请检查网络设置");
			return;
		}
		AppHttpServer.getInstance().post(HCHttpRequestParam.checkVersion(DeviceInfoUtil.getAppCurrVersion()), this, 0);
	}

	/**
	 * upload Call Log ；
	 */
	private void uploadCallLog() {
		int mlastID = RecordUtil.getLastUploadRecordID();
		ArrayList<CallLogEntity> list = (ArrayList<CallLogEntity>) CallLogHelper.getCallLog(getApplicationContext(), mlastID);
		if (list == null || list.size() == 0) { // 此时认为已传完
			mHandler.sendEmptyMessage(UPLOAD_FINISH);
			return;
		}
		final int _id = list.get(list.size() - 1).get_id();
		AppHttpServer.getInstance().post(HCHttpRequestParam.uploadCallLogs(list), this, _id);

	}

	private static final int UPLOAD_FAILED = 0x1011;
	private static final int UPLOAD_SUCCESS = 0x1022;
	private static final int UPLOAD_FINISH = 0x1033;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case UPLOAD_SUCCESS: // 上传成功 读取下一段记录
					uploadCallLog();
					break;
				case UPLOAD_FAILED: // 上传失败 提示
					ToastUtil.showNetError(getApplicationContext()); //
					ProgressDialogUtil.closeProgressDialog();
					break;
				case UPLOAD_FINISH:// 上传完成
					unBindPush();
//					logout();
					break;
			}
		}

	};

	private void unBindPush() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.unbind(), SettingActivity.this, 0);
	}


	/**
	 * 解析更新版本的json串
	 *
	 * @param json
	 */
	private void parseUpdateVersion(String json) {
		UpdateVersionEntity mVersionEntity = new HCJsonParse().parseUpdateVersion(json);
		//0：不是最新包，需要更新 1：不需要更新
		if (mVersionEntity != null && mVersionEntity.getIs_new() == 0) {
			checkDownloadStatus();
		} else {
			ToastUtil.showInfo(getString(R.string.app_version_isnew));
		}
	}

	/**
	 * 检查下载任务的状态
	 */
	private void checkDownloadStatus() {
		//第一次下载的 downloadManager.enqueue(req)会返回一个downloadId，把downloadId保存到本地，
		// 用户下次进来的时候，取出保存的downloadId，然后通过downloadId来获取下载的状态信息
		long downloadId = SharedPreferencesUtils.getLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
		if (downloadId == -1) {
			startUpdateService();
			return;
		}
		int status = FileDownloadUtil.getDownloadStatus(downloadId);
		switch (status) {
			case DownloadManager.STATUS_RUNNING:
				ToastUtil.showInfo(getString(R.string.version_downloading));
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				Uri uri = FileDownloadUtil.getDownloadUri(downloadId);
				if (uri == null) {
					FileDownloadUtil.resetDownloadId(downloadId);
					startUpdateService();
				} else {
					PackageInfo downApkInfo = FileDownloadUtil.getApkInfo(uri.getPath());
					if (FileDownloadUtil.isNewAPK(downApkInfo)) {
						FileDownloadUtil.installApk(uri);
						finish();
					} else {
						FileDownloadUtil.resetDownloadId(downloadId);
						startUpdateService();
					}
				}
				break;
			default:
				FileDownloadUtil.resetDownloadId(downloadId);
				startUpdateService();
				break;
		}
	}

	/**
	 * 开启更新服务
	 */
	private void startUpdateService() {
		Intent intent = new Intent(this, AutoUpdateVersionService.class);
		startService(intent);
	}
}
