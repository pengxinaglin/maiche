package com.haoche51.settlement.cashiers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.haoche51.settlement.PayDebug;
import com.haoche51.settlement.R;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.net.HCJsonParse;
import com.haoche51.settlement.net.HttpConstants;
import com.haoche51.settlement.o2outils.BaseHelper;
import com.haoche51.settlement.o2outils.Constants;
import com.haoche51.settlement.o2outils.MobileSecurePayer;
import com.haoche51.settlement.o2outils.PayOrder;
import com.haoche51.settlement.o2outils.Rsa;
import com.haoche51.settlement.utils.DisplayUtils;
import com.haoche51.settlement.utils.HCArithUtil;
import com.haoche51.settlement.utils.ProgressDialogUtil;
import com.haoche51.settlement.utils.SharedPreferencesUtils;
import com.haoche51.settlement.utils.ThirdPartInjector;
import com.haoche51.settlement.utils.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * POS机收款
 * Created by yangming on 2016/3/30.
 */
public class SettlementPospayActivity extends Activity implements HCHttpCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher {

//    public static final String KEY_EXTRA_CRM_USER_ID = "key_extra_crm_user_id";
//    public static final String KEY_EXTRA_CRM_USER_NAME = "key_extra__crm_user_name";
//    public static final String KEY_EXTRA_TOKEN = "key_extra_token";
//    public static final String KEY_EXTRA_PRICE = "key_extra_price";
//    public static final String KEY_EXTRA_PHONE = "key_extra_phone";
//    public static final String KEY_EXTRA_TASK_ID = "key_extra_task_id";
//    public static final String KEY_EXTRA_TASK_TYPE = "key_extra_task_type";

	private EditText et_settlement_pospay_price;
	private EditText et_settlement_pospay_operator;

	private RadioGroup rg_pay_type;
	private TextView tv_poundage;

	private PospayEntity pospay;     //请求支付订单
	private String type_pay = "1";   //消费

	private String mCrmUserId = "";    //必传，登陆用户ID
	private String mCrmUserName = "";  //必传，登陆用户name
	private String mAppToken = "";       //必传，apptoken
	private String phone = "";          //必传，用户电话
	private String task_id = "";        //非必，业务订单编号
	private int task_type;              //非必，任务类型
	private String comment;              //非必，任务类型

	private double remaind = 0;       //必传，剩余应收款

	private String price = "0";         //本次实际收款金额
	private Handler mHandler = createHandler();
	private String retMsg = "";
	private TextView tv_settlement_pospay_lianlian;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCrmUserId = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID);
		mCrmUserName = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME);
		mAppToken = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN);
		phone = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_PHONE);
		task_id = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TASK_ID);
		task_type = getIntent().getIntExtra(SettlementDetailActivity.KEY_EXTRA_TASK_TYPE, -1);
		remaind = getIntent().getDoubleExtra(SettlementDetailActivity.KEY_EXTRA_PRICE, -1);
		comment = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_COMMENT);

		if (TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName)
				|| TextUtils.isEmpty(mAppToken) || remaind == -1) {
			ToastUtil.showInfo(getApplicationContext(), "参数错误");
			finish();
			return;
		}

		setContentView(R.layout.pos_pay_activity);
		registerTitleBack();
		//title
		TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
		titleTextView.setText("POS机收款");

		//刷卡金额
		et_settlement_pospay_price = (EditText) findViewById(R.id.et_settlement_pospay_price);
		//操作员
		et_settlement_pospay_operator = (EditText) findViewById(R.id.et_settlement_pospay_operator);

		//支付方式
		rg_pay_type = (RadioGroup) findViewById(R.id.rg_pay_type);
		((RadioButton) rg_pay_type.getChildAt(0)).setChecked(true);
		rg_pay_type.setOnCheckedChangeListener(this);
		//手续费
		tv_poundage = (TextView) findViewById(R.id.tv_poundage);

		//链接POS机
		tv_settlement_pospay_lianlian = (TextView) findViewById(R.id.tv_settlement_pospay_lianlian);
		tv_settlement_pospay_lianlian.setOnClickListener(this);

		//刷卡金额默认值
		et_settlement_pospay_price.setText(DisplayUtils.parseMoney("#", remaind));
		et_settlement_pospay_price.addTextChangedListener(this);
		//操作员
		String posName = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.KEY_POSPAY_LOGIN_NAME, "");
		et_settlement_pospay_operator.setText(posName);
	}

	@SuppressLint("StringFormatInvalid")
	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int id) {
		//信用卡、微信支、付宝
		if (id == R.id.rb_pay_type_other) {
			String price = et_settlement_pospay_price.getText().toString();
			if (!TextUtils.isEmpty(price)) {
				int poundage = Integer.parseInt(price);
				tv_poundage.setText(String.valueOf((int) (poundage * 0.006)) + "元（交易金额的0.6%）");
			} else {
				tv_poundage.setText("----");
			}
		}
		//借记卡
		else {
			tv_poundage.setText("----");
		}
	}

	/**
	 * 注册back功能
	 */
	protected void registerTitleBack() {
		View backBtn = findViewById(R.id.back_image_view);
		if (backBtn != null) {
			backBtn.setVisibility(View.VISIBLE);
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		tv_settlement_pospay_lianlian.setEnabled(false);
		if (TextUtils.isEmpty(et_settlement_pospay_price.getText().toString().trim())) {
			ToastUtil.showTextLong(getApplicationContext(), "请填写刷卡金额");
			tv_settlement_pospay_lianlian.setEnabled(true);
			return;
		}

		if (Double.valueOf(et_settlement_pospay_price.getText().toString().trim()) > remaind) {
			ToastUtil.showTextLong(getApplicationContext(), "刷卡金额不能大于剩余应收款金额：" + remaind + "元");
			tv_settlement_pospay_lianlian.setEnabled(true);
			return;
		}

		price = et_settlement_pospay_price.getText().toString();


		if (Double.valueOf(price) <= 0) {
			ToastUtil.showTextLong(getApplicationContext(), "请确认最终刷卡金额大于0");
			tv_settlement_pospay_lianlian.setEnabled(true);
			return;
		}

		SharedPreferencesUtils.saveData(this, SharedPreferencesUtils.KEY_POSPAY_LOGIN_NAME, et_settlement_pospay_operator.getText().toString());
		getOutSideOrder();
	}

	/**
	 * 请求我们的订单信息
	 * 请求中的price要包含手续费
	 */
	private void getOutSideOrder() {

		final Map<String, Object> params = HCHttpRequestParam.newPosPay(SettlementPospayActivity.this, mCrmUserId, mCrmUserName
				, phone, DisplayUtils.parseMoney("###", HCArithUtil.mul(Double.valueOf(price), 100))
				, task_type, task_id, comment, mAppToken);

		ProgressDialogUtil.showProgressDialog(this, new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				AppHttpServer.getInstance(mAppToken).cancel(SettlementPospayActivity.this);
				if (params != null) {
					params.put(AppHttpServer.KEY_IS_CANCEL, true);
				}
				tv_settlement_pospay_lianlian.setEnabled(true);
				dialog.dismiss();
			}
		});
		AppHttpServer.getInstance(mAppToken).post(SettlementPospayActivity.this
				, params
				, SettlementPospayActivity.this, 3);
	}


	private PayOrder constructGesturePayOrder() {
		PayOrder order = new PayOrder();
		order.setSign_type(PayOrder.SIGN_TYPE_RSA);
		order.setOid_trader(pospay.getOid_trader());//商户号

		//使用信用卡、微信、支付宝
		if (rg_pay_type.getCheckedRadioButtonId() == R.id.rb_pay_type_other) {
			//收取手续费
			int money = Integer.parseInt(price);
			int fee = (int) (money * 0.006);
			order.setPrice_goods(String.valueOf(money + fee));//商品价格
		}
		//使用普通刷卡
		else {
			order.setPrice_goods(price);//商品价格
		}

		//测试为1分钱
		if (PayDebug.EVIROMENT != 0) {
			order.setPrice_goods("0.01");//商品价格
		}

		order.setOutside_goodsorder(pospay.getTrade_number());//商家订单号
		order.setUser_login(et_settlement_pospay_operator.getText().toString());//操作员 GlobalData.userDataHelper.getPosLoginName()

		order.setName_goods(pospay.getName_goods());
		order.setType_pay(type_pay);//支付方式：1->消费
		String content = BaseHelper.sortParam(order);
		String sign = Rsa.sign(content, pospay.getYt_auth().getRSA_PRIVATE());
		order.setSign(sign);
		return order;
	}


	/**
	 * 确认POS机支付/confirmPosPay
	 */
	private void responseConfirmPosPay(HCHttpResponse response, int requestId) {
		switch (response.getErrno()) {
			case 0:
				if (requestId == 0) {
					BaseHelper.showDialog(SettlementPospayActivity.this, "提示",
							"支付成功", card_type, android.R.drawable.ic_dialog_alert, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									Intent intent = new Intent();
									intent.putExtra("price", price);
									setResult(RESULT_OK, intent);
									finish();
								}
							});
				}
				break;
			default:
				BaseHelper.showDialog(SettlementPospayActivity.this, "提示", response.getErrmsg(),
						android.R.drawable.ic_dialog_alert);
				break;
		}
	}

	/**
	 * 创建支付请求回调处理
	 */
	private void responseNewPosPay(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				pospay = HCJsonParse.parseNewPosPay(response.getData());
				if (pospay == null) {
					ToastUtil.showTextLong(getApplicationContext(), "请求订单失败，请重试");
					tv_settlement_pospay_lianlian.setEnabled(true);
					return;
				}

				PayOrder order = constructGesturePayOrder();

				String content4Pay = BaseHelper.toJSONString(order);
				MobileSecurePayer msp = new MobileSecurePayer();

				msp.pay1(content4Pay, mHandler, Constants.RQF_PAY,
						SettlementPospayActivity.this, true);
				tv_settlement_pospay_lianlian.setEnabled(true);
				break;
			default:
				ToastUtil.showTextLong(getApplicationContext(), response.getErrmsg());
				break;
		}
	}


	@Override
	public void onHttpStart(String action, int requestId) {

	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}

		if (action.equals(HttpConstants.ACTION_NEW_POS_PAY)) {
			if (response.isCancel()) {
				tv_settlement_pospay_lianlian.setEnabled(true);
				return;
			}
			ProgressDialogUtil.closeProgressDialog();
			responseNewPosPay(response);
		} else if (action.equals(HttpConstants.ACTION_CONFIRM_POSPAY)) {
			ProgressDialogUtil.closeProgressDialog();
			responseConfirmPosPay(response, requestId);
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

	//必传，交易状态，20已确认 30支付失败 40无效
	//必传，支付方式，1刷卡 2微信 3支付宝

	/**
	 * {
	 * "json": {
	 * "ret_code": "0000",
	 * "dt_finish": "2016/04/19 10:23:01",
	 * "type_pay": "1",                        //1-消费 5-预授权
	 * "price_goods": "0.01",
	 * "oid_goodsorder": "20160419010057116",
	 * "ret_msg": "交易成功!",
	 * "pos_id": "04289838",                  //用于此交易的终端序列号
	 * "type_order": "1"                      //0-微信支付 1-刷卡支付 5-支付宝支付 7-applepay
	 * }
	 * }
	 */
	String card_type;

	private Handler createHandler() {
		return new Handler() {
			public void handleMessage(Message msg) {
				if (isFinishing()) {
					return;
				}
				String strRet = (String) msg.obj;
				switch (msg.what) {
					case Constants.RQF_PAY: {
						JSONObject objContent = BaseHelper.string2JSON(strRet);
						JSONObject json = BaseHelper.string2JSON(objContent.optString("json"));
						String retCode = json.optString("ret_code");
						String retMsg = json.optString("ret_msg");
						String type_order = json.optString("type_order");
						card_type = json.optString("card_type");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("ret_code", retCode);
						map.put("ret_msg", retMsg);
						map.put("mCrmUserId", mCrmUserId);
						map.put("mCrmUserName", mCrmUserName);
						map.put("trade_number", pospay.getTrade_number());
						ThirdPartInjector.onEvent(SettlementPospayActivity.this, "pospay", map);

						// 成功
						if (Constants.RET_CODE_SUCCESS.equals(retCode)) {
							//计算信用卡、微信、支付宝支付手续费
							long fee = 0;
							if (rg_pay_type.getCheckedRadioButtonId() == R.id.rb_pay_type_other) {
								fee = ((int) (Integer.parseInt(price) * 0.006)) * 100;
							}
							//确认POS机支付
							ProgressDialogUtil.showProgressDialog(SettlementPospayActivity.this, "");
							AppHttpServer.getInstance(mAppToken).post(SettlementPospayActivity.this, null,
									HCHttpRequestParam.confirmPosPay(SettlementPospayActivity.this,
											mCrmUserId, mCrmUserName, pospay.getTrade_number(), 20
											, type_order, card_type, retCode, retMsg, mAppToken, fee, json.toString())
									, SettlementPospayActivity.this, 0);

						} else if (Constants.RET_CODE_PROCESS.equals(retCode)) {
							// 处理中，掉单的情形
							String resulPay = objContent.optString("result_pay");
							if (Constants.RESULT_PAY_PROCESSING
									.equalsIgnoreCase(resulPay)) {
								BaseHelper.showDialog(SettlementPospayActivity.this, "提示",
										objContent.optString("ret_msg"),
										android.R.drawable.ic_dialog_alert);
							}

						} else {
							BaseHelper.showDialog(SettlementPospayActivity.this, "提示", retMsg,
									android.R.drawable.ic_dialog_alert);
						}
					}
					break;
				}
				super.handleMessage(msg);
			}
		};

	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void afterTextChanged(Editable editable) {
		//信用卡、微信、支付宝
		if (rg_pay_type.getCheckedRadioButtonId() == R.id.rb_pay_type_other) {
			String price = et_settlement_pospay_price.getText().toString();
			if (!TextUtils.isEmpty(price)) {
				int poundage = Integer.parseInt(price);
				tv_poundage.setText(String.valueOf((int) (poundage * 0.006)) + "元（交易金额的0.6%）");
			} else {
				tv_poundage.setText("----");
			}
		}
	}
}
