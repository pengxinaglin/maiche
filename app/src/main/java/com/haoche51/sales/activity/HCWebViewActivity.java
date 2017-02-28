package com.haoche51.sales.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.util.DownloadUtil;
import com.haoche51.sales.util.HCStatistic;
import com.haoche51.sales.util.HCUtils;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.webview.WebInterface;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class HCWebViewActivity extends CommonStateActivity {

	public static String KEY_INTENT_EXTRA_DOWNLOAD_ENABLE = "key_intent_extra_download_enable";
	public static String KEY_INTENT_EXTRA_DOWNLOAD_URL = "key_intent_extra_download_url";
	public static String KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD = "key_intent_extra_download_checker_report_or_cjd";
	public static String KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT = "key_intent_extra_download_checker_report";
	public static String KEY_INTENT_EXTRA_DOWNLOAD_CJD = "key_intent_extra_download_cjd";
	public static String KEY_INTENT_EXTRA_URL = "url";
	public static String KEY_INTENT_EXTRA_ID = "id";
	public static String KEY_INTENT_EXTRA_SHOW_TAKE_LOOK = "key_intent_extra_show_take_look";

	private String EVALUATION_URL = "";//报告url
	private String DOWNLOAD_URL = "";//下载url
	private String filename = "";//报告文件名
	private boolean downloadEnable = false;//是否下载页面的标识
	private String title = "";

	@ViewInject(R.id.tv_common_title)
	private TextView tv_common_title;

	@ViewInject(R.id.right_text_view)
	private TextView right_text_view;

	@ViewInject(R.id.view_for_progress)
	private View mProgressView;
	private FrameLayout.LayoutParams mProgParams;

	@ViewInject(R.id.web_content)
	private WebView webView;

	@ViewInject(R.id.ll_activity_evaluation_download_path)
	private LinearLayout ll_activity_evaluation_download_path;

	@ViewInject(R.id.tv_activity_evaluation_download_path)
	private TextView tv_activity_evaluation_download_path;

	private String str;
	private String taskID;

	@Override
	protected int getTitleView() {
		return R.layout.title_bar;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_evaluation;
	}


	/**
	 * @param downloadUrl
	 * @param flag        0:车检报告  1：车鉴定报告
	 */
	private void showDownloadTip(String downloadUrl, Integer flag) {
		if (downloadEnable) {
			filename = DownloadUtil.getFileName(downloadUrl, flag);
			StringBuilder path = new StringBuilder("保存路径:");
			path.append(DownloadUtil.MRECORDDIR_NAME);
			path.append("/");
			path.append(DownloadUtil.getFileName(downloadUrl, flag));
			tv_activity_evaluation_download_path.setText(path.toString());//+ filename
			ll_activity_evaluation_download_path.setVisibility(View.VISIBLE);
		} else {
			ll_activity_evaluation_download_path.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initView() {
		super.initView();
		EVALUATION_URL = getIntent().getStringExtra(KEY_INTENT_EXTRA_URL);
		if (getIntent().hasExtra(KEY_INTENT_EXTRA_ID)) {
			taskID = getIntent().getStringExtra(KEY_INTENT_EXTRA_ID);
		}
		if (getIntent().hasExtra(KEY_INTENT_EXTRA_DOWNLOAD_URL)) {
			DOWNLOAD_URL = getIntent().getStringExtra(KEY_INTENT_EXTRA_DOWNLOAD_URL);
		}
		if (getIntent().hasExtra(KEY_INTENT_EXTRA_DOWNLOAD_ENABLE)) {
			downloadEnable = getIntent().getBooleanExtra(KEY_INTENT_EXTRA_DOWNLOAD_ENABLE, false);
		}

		if (getIntent().hasExtra(KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD)) {
			str = getIntent().getStringExtra(KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD);
			if (str.equals(KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT)) {
				title = "车检报告";
				showDownloadTip(DOWNLOAD_URL, 0);
			} else if (str.equals(KEY_INTENT_EXTRA_DOWNLOAD_CJD)) {
				title = "车鉴定报告";
				showDownloadTip(DOWNLOAD_URL, 1);
			}
		} else {
			title = "车源详情";
		}

		registerTitleBack();
		tv_common_title.setText(title);
		// 标题栏关闭按钮
		right_text_view.setText(getString(R.string.hc_title_close));
		right_text_view.setVisibility(View.VISIBLE);

		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.addJavascriptInterface(new WebInterface(this), WebInterface.INTERFACE_NAME);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 调用拨号程序
				if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				} else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
			//for support https url request

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
		});

		final int screenWidth = GlobalData.mContext.getResources().getDisplayMetrics().widthPixels;

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (mProgParams == null) {
					mProgParams = (FrameLayout.LayoutParams) mProgressView.getLayoutParams();
				}
				mProgParams.width = screenWidth * newProgress / 100;
				mProgressView.setLayoutParams(mProgParams);
				if (newProgress == 100) {
					mProgressView.setVisibility(View.GONE);
				} else {
					mProgressView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				result.confirm();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}
		});
		webView.loadUrl(EVALUATION_URL);
	}

	@OnClick(R.id.btn_report_download)
	private void downloadReport(View v) {
		if (TextUtils.isEmpty(DOWNLOAD_URL)) {
			ToastUtil.showText("未生成可下载的报告，无法下载");
			return;
		}

		if (!TextUtils.isEmpty(taskID)) {
			int id = StringUtil.parseInt(taskID, 0);
			if (KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT.equals(str)) {
				HCStatistic.readyClick(id, HCConst.ReadyInfo.READYINFO_TYPE_CHECKER_REPORT_DOWNLOAD
						, HCUtils.getNetType(), System.currentTimeMillis(), 1);
			} else if (KEY_INTENT_EXTRA_DOWNLOAD_CJD.equals(str)) {
				HCStatistic.readyClick(id, HCConst.ReadyInfo.READYINFO_TYPE_CJD_REPORT_DOWNLOADD
						, HCUtils.getNetType(), System.currentTimeMillis(), 1);
			}
		}

		int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
		if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
				state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
				|| state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
			//无法调用download manager ,需要启用它
			ToastUtil.showText("下载管理器已停用，请启用");
			String packageName = "com.android.providers.downloads";
			try {
				//Open the specific App Info page:
				Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				intent.setData(Uri.parse("package:" + packageName));
				startActivity(intent);
			} catch (Exception e) {
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
				startActivity(intent);
			}
		} else {
			ToastUtil.showText("开始下载");
			DownloadUtil.downloadUseDownloadManager(HCWebViewActivity.this, DOWNLOAD_URL, title);
		}
	}

	/**
	 * 返回按钮
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
		registerReceiver(DownloadUtil.mReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver(DownloadUtil.mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void registerTitleBack() {
		View backBtn = findViewById(R.id.tv_common_back);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (webView != null && webView.canGoBack()) {
						webView.goBack();
					} else {
						finish();
					}
				}
			});
		}
	}

	/**
	 * 返回
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null && webView.canGoBack()) {
				webView.goBack();
				return true;
			} else {
				finish();
			}
		}
		return false;
	}

	/**
	 * 关闭
	 */
	@OnClick(R.id.right_text_view)
	private void onClose(View v) {
		finish();
	}
}
