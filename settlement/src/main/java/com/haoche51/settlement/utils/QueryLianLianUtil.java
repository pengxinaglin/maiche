package com.haoche51.settlement.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.haoche51.settlement.cashiers.PaymentEntity;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.o2outils.Rsa;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangming on 2016/4/18.
 */
public class QueryLianLianUtil {

	private static QueryLianLianUtil mQueryLianLianUtil;


	private Context mContext;
	private String mCrmUserId;
	private String mCrmUserName;
	private String mAppToken;

	private List<PaymentEntity> failPaymentList = new ArrayList<>();//支付失败list
	private int mQueryLianlianIndex = 0;//查询连连订单的index
	private String mOidTrader;
	private String mOrderNumber;
	private String mRsaPrivate;
	private String sign;
	private ResultListener resultListener;

	private QueryLianLianUtil() {
	}


	public static QueryLianLianUtil getInstance() {
		synchronized (QueryLianLianUtil.class) {
			if (mQueryLianLianUtil == null) {
				mQueryLianLianUtil = new QueryLianLianUtil();
			}
		}
		return mQueryLianLianUtil;
	}

	public void query(Context context, String crmUserId, String crmUserName, String appToken
			, List<PaymentEntity> data, String oidTrader, String rsaPrivate, ResultListener listener) {

		mContext = context;
		mAppToken = appToken;
		mCrmUserId = crmUserId;
		mCrmUserName = crmUserName;

		failPaymentList = data;
		mOidTrader = oidTrader;
		mRsaPrivate = rsaPrivate;
		mQueryLianlianIndex = 0;

		resultListener = listener;

		queryLianlian(mQueryLianlianIndex);
	}

	private void queryLianlian(final int index) {

		mOrderNumber = failPaymentList.get(index).getTrade_number();//商家订单号
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("no_order=").append(mOrderNumber)
				.append("&oid_partner=").append(mOidTrader)
				.append("&sign_type=RSA");
		String content = stringBuffer.toString();
		sign = Rsa.sign(content, mRsaPrivate);

		AppHttpServer.getInstance(mAppToken).lianlianQueryPost(mContext,
				HCHttpRequestParam.getQueryLianlian(mOrderNumber
						, mOidTrader, sign), new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
						//{"ret_code":"910003","ret_msg":"该单不存在"}
						//{"ret_code":"000000","ret_msg":"交易成功"}
						String result = new String(responseBody);
						HCLogUtil.d(result);
						if (statusCode == 200) {
							try {
								JSONObject jsonObject = new JSONObject(result);

								String retCode = jsonObject.optString("ret_code");
								String cardType = jsonObject.optString("card_type");
								String retMsg = jsonObject.optString("ret_msg");
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("ret_code", retCode);
								map.put("ret_msg", retMsg);
								map.put("mCrmUserId", mCrmUserId);
								map.put("mCrmUserName", mCrmUserName);
								map.put("trade_number", mOrderNumber);
								ThirdPartInjector.onEvent((Activity) mContext, "pospay", map);

								if (jsonObject.has("ret_code") && "910003".equals(jsonObject.getString("ret_code"))
										&& index < failPaymentList.size()) {//无效
									confirmPosPay(mOrderNumber, 40, "1", cardType, retCode, retMsg, 0, jsonObject.toString());
								} else if (jsonObject.has("ret_code") && "400107".equals(jsonObject.getString("ret_code"))
										&& index < failPaymentList.size()) {//该单不存在
									confirmPosPay(mOrderNumber, 40, "1", cardType, retCode, retMsg, 0, jsonObject.toString());
								} else if (jsonObject.has("ret_code") && "000000".equals(jsonObject.getString("ret_code"))
										&& index < failPaymentList.size()) {//交易成功
									String type_order = jsonObject.optString("type_order");
									if (jsonObject.has("pay_status") && "0".equals(jsonObject.getString("pay_status"))
											&& index < failPaymentList.size()) {//支付成功--pay_status =0
										confirmPosPay(mOrderNumber, 20, type_order, cardType, retCode, retMsg, 0, jsonObject.toString());
									} else if (index < failPaymentList.size() && jsonObject.has("money_order")) {//支付失败
										confirmPosPay(mOrderNumber, 30, type_order, cardType, retCode, retMsg, 0, jsonObject.toString());
									} else {
										confirmPosPay(mOrderNumber, 30, type_order, cardType, retCode, retMsg, 0, jsonObject.toString());
									}

								} else if (jsonObject.has("ret_code") && "400102".equals(jsonObject.getString("ret_code"))) {//接口通信异常
									resultListener.isComPlete(false);
								} else if (jsonObject.has("ret_code") && "600107".equals(jsonObject.getString("ret_code"))) {//非法商户
									resultListener.isComPlete(false);
								} else if (jsonObject.has("ret_code") && "600103".equals(jsonObject.getString("ret_code"))) {//商户签名验证失败
									resultListener.isComPlete(false);
								} else if (index < failPaymentList.size()) {//交易失败
									confirmPosPay(mOrderNumber, 30, "1", cardType, retCode, retMsg, 0, jsonObject.toString());
								}
								//TODO 未处理 600109、000000
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							resultListener.isComPlete(false);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
						resultListener.isComPlete(false);
					}
				}, index);
	}

	private void confirmPosPay(String trade_number, int status, String payType, String cardType, String retCode, String retMsg, long fee, String extJson) {

		if (TextUtils.isEmpty(payType)) {
			payType = "1";
		}

		Map<String, Object> request = HCHttpRequestParam.confirmPosPay(mContext,
				mCrmUserId, mCrmUserName, trade_number, status, payType, cardType, retCode, retMsg, mAppToken, fee, extJson);

		AppHttpServer.getInstance(mAppToken).post(mContext, null, request
				, new HCHttpCallback() {
					@Override
					public void onHttpStart(String action, int requestId) {

					}

					@Override
					public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
						switch (response.getErrno()) {
							case 0:
								if (mQueryLianlianIndex < failPaymentList.size() - 1) {
									queryLianlian(++mQueryLianlianIndex);
								} else {
									resultListener.isComPlete(true);
								}
								break;
							default:
								resultListener.isComPlete(false);
								break;
						}
					}

					@Override
					public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

					}

					@Override
					public void onHttpRetry(String action, int requestId, int retryNo) {

					}

					@Override
					public void onHttpFinish(String action, int requestId) {

					}
				}, 0);
	}


	public interface ResultListener {
		void isComPlete(boolean isComPlete);
	}

}
