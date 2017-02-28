package com.haoche51.sales.hcshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.Debug;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.R;
import com.haoche51.sales.hctransaction.TakeLookActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.WeChatUtil;
import com.haoche51.sales.webview.WebViewActivity;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by PengXianglin on 16/8/16.
 */
public class HuiGouVehicleSourceActivity extends WebViewActivity implements View.OnClickListener {

	private ImageView iv_more;
	private Button btn_commit;
	List<Call> reqCalls = new LinkedList<>();

	@Override
	protected int getContentView() {
		return R.layout.activity_hgsource;
	}

	@Override
	protected void initView() {
		super.initView();
		btn_commit = (Button) findViewById(R.id.btn_commit);
		btn_commit.setText("加带看");
		btn_commit.setVisibility(View.GONE);
		iv_more = (ImageView) findViewById(R.id.right_image_1);
		iv_more.setImageResource(R.drawable.ic_top_more_white);
		mRightTextView.setVisibility(View.GONE);
	}


	@Override
	protected void initData() {
		showLoadingView(true, "请稍后...");
		Call call = AppHttpServer.getInstance().post(HCHttpRequestParam.getLoginToken("crm"), this, 0);
		reqCalls.add(call);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reqCalls != null && !reqCalls.isEmpty())
			for (Call call : reqCalls) {
				AppHttpServer.getInstance().cancelRequest(call);
			}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		super.onHttpComplete(action, requestId, response, error);
		if (isFinishing()) {
			return;
		}

		if (action.equals(HttpConstants.ACTION_GET_LOGIN_TOKEKN)) {
			onLoginTokenReturn(response);
		}
	}

	private void onLoginTokenReturn(HCHttpResponse response) {
		dismissLoadingView();
		dismissResultView(true);
		switch (response.getErrno()) {
			case 0:
				Type type = new TypeToken<LoginToken>() {
				}.getType();
				LoginToken token = response.getData(type);
				if (token != null) {
					loadWapUrl(Debug.WAP_SERVER + "/bj/ershouche.html?channel=app&token=" + token.getToken());
				}
				break;
			default:
				showErrorView(true, this);
				Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@OnClick(R.id.right_image_1)
	private void onMoreClick(final View v) {
		LayoutInflater inflater = LayoutInflater.from(HuiGouVehicleSourceActivity.this);
		// 引入窗口配置文件 - 即弹窗的界面
		View view = inflater.inflate(R.layout.layout_hg_source_more, null);
		final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		pop.setFocusable(true); //设置PopupWindow可获得焦点
		pop.setTouchable(true); //设置PopupWindow可触摸
		pop.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		pop.setAnimationStyle(R.style.anim_popup_dir);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.alpha = 0.5f;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//让窗口背景后面的任何东西变暗
		getWindow().setAttributes(params);
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 弹出窗口显示内容视图,默认以锚定视图的左下角为起点，这里为点击按钮
		pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.alpha = 1f;
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//让窗口背景后面的任何东西变暗
				getWindow().setAttributes(params);
			}
		});

		//发送到单个好友
		view.findViewById(R.id.btn_message).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ProgressDialogUtil.showProgressDialog(HuiGouVehicleSourceActivity.this, "请稍后...");
				Call call = AppHttpServer.getInstance().getWxShareData(Debug.WAP_SERVER + "/api/getWxShareData?id=" + getSourceId(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								ToastUtil.showInfo("请求失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, final Response response) throws IOException {
						final WxShareEntity entity = HCJsonParse.parseWxShareEntity(new String(response.body().bytes()));
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								if (entity != null) {
									String picUrl = null;
									if (entity.getImages() != null) {
										//取第一张图片
										picUrl = entity.getImages().get(0);
									}
									WeChatUtil.share(HuiGouVehicleSourceActivity.this, entity.getTitle(), entity.getContent(), entity.getUrl(), picUrl, false);
									pop.dismiss();
								} else {
									ToastUtil.showInfo("请求失败");
								}
							}
						});
					}
				});
				reqCalls.add(call);
			}
		});
		//发送到朋友圈
		view.findViewById(R.id.btn_friends).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ProgressDialogUtil.showProgressDialog(HuiGouVehicleSourceActivity.this, "请稍后...");
				Call call = AppHttpServer.getInstance().getWxShareData(Debug.WAP_SERVER + "/api/getWxShareData?id=" + getSourceId(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								ToastUtil.showInfo("请求失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						final WxShareEntity entity = HCJsonParse.parseWxShareEntity(new String(response.body().bytes()));
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								if (entity != null) {
									String picUrl = null;
									if (entity.getImages() != null) {
										//取第一张图片
										picUrl = entity.getImages().get(0);
									}
									WeChatUtil.share(HuiGouVehicleSourceActivity.this, entity.getTitle(), entity.getContent(), entity.getUrl(), picUrl, true);
									pop.dismiss();
								} else {
									ToastUtil.showInfo("请求失败");
								}
							}
						});
					}
				});
				reqCalls.add(call);
			}
		});
		//发送多图到朋友圈
		view.findViewById(R.id.btn_image).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ProgressDialogUtil.showProgressDialog(HuiGouVehicleSourceActivity.this, "请稍后...");
				Call call = AppHttpServer.getInstance().getWxShareData(Debug.WAP_SERVER + "/api/getWxShareData?id=" + getSourceId(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								ToastUtil.showInfo("请求失败");
							}
						});
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						final WxShareEntity entity = HCJsonParse.parseWxShareEntity(new String(response.body().bytes()));
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialogUtil.closeProgressDialog();
								if (entity != null) {
									Intent intent = new Intent(HuiGouVehicleSourceActivity.this, SelectImageActivity.class);
									intent.putExtra(SelectImageActivity.EXTRA_KEY_DATA, entity);
									startActivity(intent);
									pop.dismiss();
								} else {
									ToastUtil.showInfo("请求失败");
								}
							}
						});
					}
				});
				reqCalls.add(call);
			}
		});
	}

	@OnClick(R.id.btn_commit)
	private void onLookClick(View v) {
		// 加带看
		Intent intent = new Intent(this, TakeLookActivity.class);
		intent.putExtra(TakeLookActivity.EXTRA_IS_SIMILAR, false);
		intent.putExtra(TakeLookActivity.EXTRA_SOURCE_ID, getSourceId());
		startActivity(intent);
	}

	public String getSourceId() {
		return StringUtil.subStirng(mWebView.getUrl(), "/", ".");
	}

	/**
	 * 设置WebViewClient
	 */
	protected void setWebViewClient() {
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				iv_more.setVisibility(View.GONE);
				btn_commit.setVisibility(View.GONE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("geo:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				} else {
					loadWapUrl(url);
				}
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (url.contains("ershouche")) {
					setScreenTitle("车源");
					iv_more.setVisibility(View.GONE);
					btn_commit.setVisibility(View.GONE);
				} else if (url.contains("details") && !mWebView.getUrl().contains("pics")) {
					setScreenTitle("车源详情");
					iv_more.setVisibility(View.VISIBLE);
					btn_commit.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 设置WebChromeClient
	 */
	protected void setWebChromeClient() {
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				mProgressParams.width = mScreenWidth * newProgress / 100;
				mProgressView.setLayoutParams(mProgressParams);
				if (newProgress == 100) {
					mProgressView.setVisibility(View.GONE);
				} else {
					mProgressView.setVisibility(View.VISIBLE);
				}
				if (newProgress > 85) {
					if (mWebView.getUrl().contains("details") && !mWebView.getUrl().contains("pics")) {
						setScreenTitle("车源详情");
						iv_more.setVisibility(View.VISIBLE);
						btn_commit.setVisibility(View.VISIBLE);
					}
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
	}

	@Override
	public void onClick(View view) {
		//重新加载
		initData();
	}
}
