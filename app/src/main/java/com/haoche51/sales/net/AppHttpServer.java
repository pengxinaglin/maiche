package com.haoche51.sales.net;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.HCApplication;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.util.DESUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.ToastUtil;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by xuhaibo on 15/8/26.
 */
public class AppHttpServer {

	private static final AppHttpServer mInstance = new AppHttpServer();
	private Handler mHandler;
	private OkHttpClient mOkHttpClient;
	private Gson mGson;

	private AppHttpServer() {
		mHandler = new Handler(Looper.getMainLooper());
		mGson = new Gson();
		HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
		mOkHttpClient = new OkHttpClient.Builder()
				.connectTimeout(20L, TimeUnit.SECONDS)
				.readTimeout(20L, TimeUnit.SECONDS)
				.writeTimeout(20L, TimeUnit.SECONDS)
				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				.build();
	}

	public static AppHttpServer getInstance() {
		return mInstance;
	}

	/**
	 * 取消指定的请求
	 *
	 * @param call
	 */
	public void cancelRequest(Call call) {
		if (call != null && !call.isCanceled()) {
			try {
				call.cancel();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * @param params
	 * @param callback
	 * @param requestId
	 * @return
	 */
	public Call post(Map<String, Object> params, final HCHttpCallback callback, final int requestId) {
		final String action = params.get("action").toString();
		String paramJson = mGson.toJson(params);
		FormBody reqBody = new FormBody.Builder().add(TaskConstants.NORMAL_REQUEST_PARAMS, paramJson).build();
		Request request = new Request.Builder().url(Debug.APP_SERVER).post(reqBody).build();
		HCLogUtil.d(TaskConstants.LOG_TAG, "request:" + Debug.APP_SERVER + "params:req=" + paramJson);
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, final IOException e) {
				sendFailedCallBack(action, requestId, callback, e);
			}


			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (call.isCanceled()) {
					sendFailedCallBack(action, requestId, callback, new IOException("Request is canceled!"));
					return;
				}

				if (!response.isSuccessful()) {
					sendFailedCallBack(action, requestId, callback, new IOException("Request failed , reponse's code is : " + response.code()));
					return;
				}

				try {
					sendSuccessCallBack(action, requestId, callback, response.body());
				} catch (IOException e) {
					sendFailedCallBack(action, requestId, callback, e);
				}
			}
		});
		return call;
	}


	/**
	 * 发送请求失败后的回调
	 */
	private void sendFailedCallBack(final String action, final int requestId, final HCHttpCallback callback, final Exception e) {
		if (callback == null) {
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				callback.onHttpComplete(action, requestId, new HCHttpResponse(-100, e.getMessage()), e);
			}
		});
	}

	/**
	 * 发送请求成功后的回调
	 *
	 * @param action
	 * @param requestId
	 * @param callback
	 * @param responseBody
	 * @throws IOException
	 */
	private void sendSuccessCallBack(final String action, final int requestId, final HCHttpCallback callback, ResponseBody responseBody) throws IOException {
		String respBodyStr = responseBody.string();
		HCLogUtil.d(TaskConstants.LOG_TAG, "response:" + Debug.APP_SERVER + "return:" + respBodyStr);
		final HCHttpResponse response;
		String responseStr = respBodyStr;

		if (!HttpConstants.ACTION_LOGIN_ENCODE.equals(action)) {//解密
			try {
				responseStr = DESUtil.decryptDES(respBodyStr, GlobalData.userDataHelper.getUser().getApp_token());
				HCLogUtil.d(TaskConstants.LOG_TAG, "response:" + Debug.APP_SERVER + "return--decryptDES:" + responseStr);
			} catch (final Exception e) {
				HCLogUtil.e("登录解密失败：" + e.getMessage());
				//解密失败，用户登录异常，需退出登录
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						logout("登录解密失败，退出登录:" + e.getMessage());
					}
				});
				return;
			}

		}

		response = new HCHttpResponse(responseStr);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (response.getErrno() == -101) {
					// erroNo = -101 异常登录，无效的app token 退出登录
					logout(response.getErrmsg());
				} else if (callback != null) {
					callback.onHttpComplete(action, requestId, response, null);
				}
			}
		});
	}


	/**
	 * 退出登录
	 *
	 * @param errMsg
	 */
	private void logout(String errMsg) {
		ToastUtil.showText(errMsg);
		HCApplication.logout();
	}


	/**
	 * 根据tag取消请求
	 *
	 * @param tag
	 */
	public void cancelTag(Object tag) {
		for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}
		for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}
	}

	public Call getWxShareData(String url, final Callback callback) {
		FormBody reqBody = new FormBody.Builder().build();
		Request request = new Request.Builder().url(url).post(reqBody).build();
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(callback);
		return call;
	}
}