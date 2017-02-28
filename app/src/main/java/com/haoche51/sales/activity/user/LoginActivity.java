package com.haoche51.sales.activity.user;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.User;
import com.haoche51.sales.activity.MainPageActivity;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.helper.UserDataHelper;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.util.DeviceInfoUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;
import com.haoche51.sales.util.ToastUtil;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends CommonBaseActivity {
	private Button loginButton;
	@Required(order = 1, message = "用户名不能为空")
	private EditText userEditText;
	@Required(order = 2, message = "密码不能为空")
	private EditText pwdEditText;
	private ImageView imageViewUserPhoto;
	private UserDataHelper mUserDataHelper = null;

	private static final int KEY_HTTP_REQUEST_ID_LOGIN = 0;

	public void initView() {
		mUserDataHelper = GlobalData.userDataHelper;

		setContentView(R.layout.activity_login);
		loginButton = (Button) findViewById(R.id.login);
		userEditText = (EditText) findViewById(R.id.user_name);
		pwdEditText = (EditText) findViewById(R.id.password);
		imageViewUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
		userEditText.setText(mUserDataHelper.getLoginName());
		pwdEditText.setText(mUserDataHelper.getLoginPwd());

		if (!TextUtils.isEmpty(SharedPreferencesUtils.getString(HCConst.SharePreferences.SP_USER_FACE, ""))) {
			DisplayUtils.setUserFace(imageViewUserPhoto, SharedPreferencesUtils.getString(HCConst.SharePreferences.SP_USER_FACE, ""));
		}

		setLoginClick();
	}

	private void setLoginClick() {
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				validator.validate();
			}

		});

		pwdEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND ||
						(event != null) && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					validator.validate();
					return true;
				} else {
					return false;
				}
			}
		});
	}

	@Override
	public void onValidationSucceeded() {
		ProgressDialogUtil.showProgressDialog(LoginActivity.this, getString(R.string.loging));
		String username = userEditText.getText().toString().trim();
		String password = pwdEditText.getText().toString().trim();
		mUserDataHelper.setLoginName(username)
				.setLoginPwd(password)
				.commit();
		// 每次登录后清除门店Token
		GlobalData.userDataHelper.clearStoreToken();
		AppHttpServer.getInstance().post(HCHttpRequestParam.loginForEncode(username, password
				, DeviceInfoUtil.getDeviceUniqueId(), DeviceInfoUtil.getPhoneType(), DeviceInfoUtil.getOSVersion()
				, DeviceInfoUtil.getAppVersion()), LoginActivity.this, KEY_HTTP_REQUEST_ID_LOGIN);
		super.onValidationSucceeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LoginActivity");
		MobclickAgent.onResume(getApplicationContext());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LoginActivity");
		MobclickAgent.onPause(getApplicationContext());
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
		ProgressDialogUtil.closeProgressDialog();
		switch (requestId) {
			case KEY_HTTP_REQUEST_ID_LOGIN:
				doGetLoginRequest(response);
				break;
		}
	}

	private void doGetLoginRequest(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				User user = HCJsonParse.parseLogin(response.getData());

				if (user == null) {
					mUserDataHelper.clearLogin().clearUser().commit();
					Toast.makeText(LoginActivity.this, getString(R.string.server_no_response), Toast.LENGTH_SHORT).show();
					break;
				}
				mUserDataHelper.setUser(user).setLogin().setLastCheckerId(user.getId()).commit();
				mUserDataHelper.setUser(user).setLogin().setLastCheckerName(user.getName()).commit();
				mUserDataHelper.setUser(user).setLogin().setLastAppToken(user.getApp_token()).commit();
				SharedPreferencesUtils.saveData(HCConst.SharePreferences.SP_USER_FACE, user.getPic());

				//跳转主页
				Intent intent = new Intent();
				intent.setClass(this, MainPageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
				break;
			case -7:
				showErrorMsg(userEditText, response.getErrmsg());
				mUserDataHelper.clearLogin().clearUser().commit();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				mUserDataHelper.clearLogin().clearUser().commit();
				break;
		}
	}
}
