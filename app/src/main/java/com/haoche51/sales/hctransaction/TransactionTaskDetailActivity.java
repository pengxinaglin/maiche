package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.TagsLayout;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCStatistic;
import com.haoche51.sales.util.HCUtils;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.haoche51.sales.webview.SimilarCarWebViewActivity;
import com.haoche51.sales.webview.WebViewActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 看车任务详情
 * Created by mac on 15/12/1.
 */
public class TransactionTaskDetailActivity extends CommonStateActivity {

	public static final String KEY_ID = "id";
	public static final int REQUEST_CODE_TAKE_LOOK = 1;

	@ViewInject(R.id.tv_transaction_buyer_name)
	private TextView tv_transaction_buyer_name;

	@ViewInject(R.id.tv_item_today_task)
	private TextView tv_transaction_car_dealer;

	@ViewInject(R.id.tv_transaction_buyer_phone)
	private TextView tv_transaction_buyer_phone;

	@ViewInject(R.id.tv_transaction_time)
	private TextView tv_transaction_time;

	@ViewInject(R.id.tv_transaction_address)
	private TextView tv_transaction_address;

	@ViewInject(R.id.tv_transaction_vehicle)
	private TextView tv_transaction_vehicle;

	@ViewInject(R.id.tv_transaction_seller_price)
	private TextView tv_transaction_seller_price;

	@ViewInject(R.id.tv_transaction_cheap_price)
	private TextView tv_transaction_cheap_price;

	@ViewInject(R.id.tv_transaction_eval_price)
	private TextView tv_transaction_eval_price;

	@ViewInject(R.id.tv_transaction_eval_price_21)
	private TextView tv_transaction_eval_price_21;

	@ViewInject(R.id.tv_transaction_dealer_price)
	private TextView tv_transaction_dealer_price;

	@ViewInject(R.id.tv_vehicl_source_id)
	private TextView tv_vehicl_source_id;

	@ViewInject(R.id.tv_vehicle_plate_number)
	private TextView tv_vehicle_plate_number;

	@ViewInject(R.id.tv_online_time)
	private TextView tv_online_time;

	@ViewInject(R.id.tv_saler_comment)
	private TextView tv_saler_comment;

	@ViewInject(R.id.tv_seller_name)
	private TextView tv_seller_name;

	@ViewInject(R.id.tv_seller_phone)
	private TextView tv_seller_phone;

	@ViewInject(R.id.tv_saler_name)
	private TextView tv_saler_name;

	@ViewInject(R.id.tv_saler_phone)
	private TextView tv_saler_phone;

	@ViewInject(R.id.tv_checker_name)
	private TextView tv_checker_name;

	@ViewInject(R.id.tv_checker_phone)
	private TextView tv_checker_phone;

	@ViewInject(R.id.btn_transaction_dropbuy)
	private LinearLayout btn_transaction_dropbuy;//看车失败（放弃购买）

	@ViewInject(R.id.btn_transaction_prepay_commit)
	private LinearLayout btn_transaction_prepay_commit;//支付定金

	@ViewInject(R.id.btn_transaction_take)
	private LinearLayout btn_transaction_take;//展厅车带看任务

	@ViewInject(R.id.btn_transaction_arrival)
	private LinearLayout btn_transaction_arrival;//展厅车到店记录

	@ViewInject(R.id.btn_transaction_prepay)
	private LinearLayout btn_transaction_prepay;//展厅车预定

	@ViewInject(R.id.tv_vehicle_type_text)
	private TextView tv_vehicle_type_text;//车源标签

	@ViewInject(R.id.ll_vehicle_tag)
	private TagsLayout ll_vehicle_tag;//车源标签

	private TransactionTaskEntity vTask;
	private int trans_id;

	// 加带看成功提交后返回
	private boolean mIsTakeLookSuccess = false;

	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_detail;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_trans_task_activity_title);
		trans_id = getIntent().getIntExtra(KEY_ID, 0);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_GET_LOGIN_TOKEKN)) {
			onLoginTokenReturn(response);
		} else if (action.equals(HttpConstants.ACTION_GET_TRANSACTION_BY_ID)) {
			responseGetTransaction(response);
		}
	}

	/**
	 * 响应请求看车任务
	 */
	private void responseGetTransaction(HCHttpResponse response) {
		try {
			switch (response.getErrno()) {
				case 0:
					Gson gson = new Gson();
					vTask = gson.fromJson(response.getData(), TransactionTaskEntity.class);
					dismissLoadingView();
					if (!isFinishing())
						ProgressDialogUtil.closeProgressDialog();
					HCDialog.dismissProgressDialog();
					updateUI();
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					setErrorView();
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.showText(getString(R.string.common_erro_http_gson_decode));
			setErrorView();
		}
	}

	private void onLoginTokenReturn(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				Type type = new TypeToken<LoginToken>() {
				}.getType();
				LoginToken token = response.getData(type);
				if (token != null) {
					startChejianding(token.getToken());
				}
				break;
			default:
				Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	private void setErrorView() {
		dismissLoadingView();
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();
		showErrorView(false, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissResultView(true);
				showLoadingView(false);
				AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id),
						TransactionTaskDetailActivity.this, 0);
			}
		});
	}

	/**
	 * 更新界面
	 */
	private void updateUI() {
		if (vTask == null)
			return;
		tv_transaction_buyer_name.setText(vTask.getBuyer_name());
		if (vTask.getCar_dealer() == 1) {
			tv_transaction_car_dealer.setVisibility(View.VISIBLE);
		}
		DisplayUtils.getNoUnderlineSpan(tv_transaction_buyer_phone, vTask.getBuyer_phone());
		if (TextUtils.isEmpty(vTask.getBuyer_phone()))
			tv_transaction_buyer_phone.setVisibility(View.GONE);//没有买家手机号码
		tv_transaction_time.setText(UnixTimeUtil.format(vTask.getAppointment_starttime()));
		tv_transaction_address.setText(vTask.getPlace());
		tv_transaction_vehicle.setText(vTask.getVehicle_name());
		tv_transaction_seller_price.setText(vTask.getSeller_price());
		tv_transaction_cheap_price.setText(vTask.getCheap_price());
		tv_transaction_eval_price.setText(vTask.getEval_price());
		tv_transaction_eval_price_21.setText(vTask.getEval_price_21());
		tv_transaction_dealer_price.setText(vTask.getEval_price_dealer() + "");
		tv_vehicl_source_id.setText(vTask.getVehicle_source_id());
		tv_vehicle_plate_number.setText(vTask.getPlate_number());
		tv_online_time.setText(UnixTimeUtil.format(vTask.getOnline_time()));
		tv_saler_comment.setText(vTask.getComment());
		tv_seller_name.setText(vTask.getSeller_name());
		DisplayUtils.getNoUnderlineSpan(tv_seller_phone, vTask.getSeller_phone());
		if (TextUtils.isEmpty(vTask.getSeller_phone()))
			tv_seller_phone.setVisibility(View.INVISIBLE);//没有卖家手机号码
		tv_saler_name.setText(vTask.getSaler_name());
		DisplayUtils.getNoUnderlineSpan(tv_saler_phone, vTask.getSaler_phone());
		if (TextUtils.isEmpty(vTask.getSaler_phone()))
			tv_saler_phone.setVisibility(View.INVISIBLE);//没有电销手机号码
		tv_checker_name.setText(vTask.getChecker_name());
		DisplayUtils.getNoUnderlineSpan(tv_checker_phone, vTask.getChecker_phone());
		if (TextUtils.isEmpty(vTask.getChecker_phone()))
			tv_checker_phone.setVisibility(View.INVISIBLE);//没有评估师手机号码
		switch (vTask.getStatus()) {
			case TaskConstants.VIEW_TASK_PENDING: // 未开始
				if (vTask.getTask_type() == 1) {
					//显示展厅车操作
					btn_transaction_take.setVisibility(View.VISIBLE);
					btn_transaction_arrival.setVisibility(View.VISIBLE);
					btn_transaction_prepay.setVisibility(View.VISIBLE);
					//隐藏普通车操作
					btn_transaction_prepay_commit.setVisibility(View.GONE);
					btn_transaction_dropbuy.setVisibility(View.GONE);
				} else {
					//显示普通车操作
					btn_transaction_prepay_commit.setVisibility(View.VISIBLE);
					btn_transaction_dropbuy.setVisibility(View.VISIBLE);
					//隐藏展厅车操作
					btn_transaction_take.setVisibility(View.GONE);
					btn_transaction_arrival.setVisibility(View.GONE);
					btn_transaction_prepay.setVisibility(View.GONE);
				}
				break;
			default:
				//无任何操作
				btn_transaction_prepay_commit.setVisibility(View.GONE);
				btn_transaction_dropbuy.setVisibility(View.GONE);
				btn_transaction_take.setVisibility(View.GONE);
				btn_transaction_arrival.setVisibility(View.GONE);
				btn_transaction_prepay.setVisibility(View.GONE);
				break;
		}
		//车源标签
		if (!TextUtils.isEmpty(vTask.getVehicle_type_text())) {
			tv_vehicle_type_text.setText(vTask.getVehicle_type_text());
		}
		//车源更多标签
		if (vTask.getVehicle_tag() != null && !vTask.getVehicle_tag().isEmpty()) {
			ll_vehicle_tag.removeAllViews();
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(20, 0, 0, 0);
			for (int i = 0; i < vTask.getVehicle_tag().size(); i++) {
				TextView textView = new TextView(this);
				textView.setTextAppearance(this, R.style.vehicle_lable);
				Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_vehicle_tag);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				textView.setCompoundDrawables(drawable, null, null, null);
				textView.setCompoundDrawablePadding(10);
				textView.setText(vTask.getVehicle_tag().get(i));
				ll_vehicle_tag.addView(textView, lp);
			}
		}
	}

	@OnClick(R.id.tv_transaction_buyer_phone)
	private void callBuyer(View view) {
		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_CUSTOMER_PHONE
				, HCUtils.getNetType(), System.currentTimeMillis(), 0);

		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_transaction_buyer_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	@OnClick(R.id.tv_seller_phone)
	private void callSeller(View view) {
		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_OWNER_PHONE
				, HCUtils.getNetType(), System.currentTimeMillis(), 0);

		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_seller_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	@OnClick(R.id.tv_saler_phone)
	private void callSaler(View view) {
		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_SALER_PHONE
				, HCUtils.getNetType(), System.currentTimeMillis(), 0);

		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_saler_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	@OnClick(R.id.tv_checker_phone)
	private void callChecker(View view) {
		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_CHECKER_PHONE
				, HCUtils.getNetType(), System.currentTimeMillis(), 0);

		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_checker_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 看车失败
	 */
	@OnClick(R.id.btn_transaction_dropbuy)
	private void onClickQuitBuy(View view) {
		if (vTask != null) {
			Intent dropIntent = new Intent(this, TransactionTaskFailActivity.class);
			dropIntent.putExtra("taskEntity", vTask);
			startActivityForResult(dropIntent, TaskConstants.QUIT_BUY_TRANSACTION);
		}
	}

	/**
	 * 看车成功
	 */
	@OnClick(R.id.btn_transaction_prepay_commit)
	private void onClickPrePay(View view) {
		if (vTask != null) {
			Intent prepayIntent = new Intent(this, TransactionPrepayActivity.class);
			prepayIntent.putExtra("taskEntity", vTask);
			startActivityForResult(prepayIntent, TaskConstants.PREPAY_TRANSACTION);
		}
	}

	/**
	 * 展厅车带看任务
	 */
	@OnClick(R.id.btn_transaction_take)
	private void onClickStoreTake(View view) {
		if (vTask != null) {
			Intent intent = new Intent(this, StoreTakeActivity.class);
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_TASKID, vTask.getId());
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_SOURCE, vTask.getTask_type() == 1 ? 1 : 0);
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_PHONE, vTask.getBuyer_phone());
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_NAME, vTask.getBuyer_name());
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_PLACE, vTask.getPlace());
			startActivityForResult(intent, TaskConstants.TRANSACTION_STORE_TAKE);
		}
	}

	/**
	 * 展厅车到店记录
	 */
	@OnClick(R.id.btn_transaction_arrival)
	private void onClickStoreArrival(View view) {
		if (vTask != null) {
			Intent intent = new Intent(this, StoreArrivalActivity.class);
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_TASKID, vTask.getId());
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_PHONE, vTask.getBuyer_phone());
			intent.putExtra(StoreTakeActivity.EXTAR_KEY_NAME, vTask.getBuyer_name());
			startActivityForResult(intent, TaskConstants.TRANSACTION_STORE_ARRIVAL);
		}
	}

	/**
	 * 展厅车预定
	 */
	@OnClick(R.id.btn_transaction_prepay)
	private void onClickStorePrePay(View view) {
		TransactionTaskEntity entity = vTask;
		entity.setType(1);//若type=0，则id为C2C带看任务id；若type=1，则id为展厅任务id
		Intent prepayIntent = new Intent(this, TransactionPrepayActivity.class);
		prepayIntent.putExtra("taskEntity", entity);
		prepayIntent.putExtra("isStore", true);
		startActivityForResult(prepayIntent, TaskConstants.PREPAY_TRANSACTION);
	}

	/**
	 * 车源详情
	 */
	@OnClick(R.id.rb_transaction_vehicle_detail)
	private void toVehicleDetail(View v) {
		if (vTask != null && !TextUtils.isEmpty(vTask.getVehicle_source_id())) {

			HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_VEHICLE_INFO
					, HCUtils.getNetType(), System.currentTimeMillis(), 1);

			Intent intent = new Intent(this, HCWebViewActivity.class);
			intent.putExtra("url", "http://m.haoche51.com/details/" + vTask.getVehicle_source_id() + ".html?"
					+ "transaction_id=" + trans_id + "&user_id=" + GlobalData.userDataHelper.getUser().getId()
					+ "&user_name=" + GlobalData.userDataHelper.getUser().getName()
					+ "&operation_type=" + HCConst.ReadyInfo.READYINFO_TYPE_VEHICLE_INFO_CHECK_PARAM
					+ "&network_env=" + HCUtils.getNetType()
			);
			startActivity(intent);
		}
	}

	/**
	 * 车检报告
	 */
	@OnClick(R.id.rb_transaction_report)
	private void toReport(View v) {
		//0:订单未获取 1:订单获取中 2:订单获取成功 3:订单获取失败
		if (vTask == null || TextUtils.isEmpty(vTask.getCheck_report_url())) {
			return;
		}
		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_CHECKER_REPORT
				, HCUtils.getNetType(), System.currentTimeMillis(), 1);

		// 获取到了，查看，下载；没有报告，toast提示
		// 测试
		Map<String, Object> map = new HashMap<>();
		map.put(HCWebViewActivity.KEY_INTENT_EXTRA_URL, vTask.getCheck_report_url());
		map.put(HCWebViewActivity.KEY_INTENT_EXTRA_ID, vTask.getId());
		map.put(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_ENABLE, true);
		map.put(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_URL, vTask.getCheck_report_pdf());//  下载地址
		map.put(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD, HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT);
		HCActionUtil.launchActivity(this, HCWebViewActivity.class, map);
	}

	/**
	 * 4s店保养记录
	 */
	@OnClick(R.id.rb_transaction_cjd)
	private void toCjd(View v) {

		HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_CJD_REPORT
				, HCUtils.getNetType(), System.currentTimeMillis(), 1);

		// 获取到了，查看，下载；没有报告，toast提示
		//0:订单未获取 1:订单获取中 2:订单获取成功 3:订单获取失败
		if (vTask == null) {
			ToastUtil.showText(ControlDisplayUtil.getInstance().getCheJinDingStatus(TaskConstants.GET_FAIL));
			return;
		}
		if (vTask.getCjd_status() != 2) {
			ToastUtil.showText(ControlDisplayUtil.getInstance().getCheJinDingStatus(vTask.getCjd_status()));
			return;
		}

		//先确认token 是否过期
		LoginToken token = GlobalData.userDataHelper.getLoginToken();
		boolean timeout = false;
		if (token != null) {
			int current_time = (int) System.currentTimeMillis() / 1000;
			timeout = (current_time - token.getTimeOut()) > token.getCreate_time() ? true : false;
		}
		if (token == null || TextUtils.isEmpty(token.getToken()) || timeout) {
			HCDialog.showProgressDialog(this);
			AppHttpServer.getInstance().post(HCHttpRequestParam.getLoginToken("crm"), this, 0);
		} else {
			startChejianding(token.getToken());
		}
	}

	/**
	 * 跳转到车鉴定页面
	 *
	 * @param token
	 */
	private void startChejianding(String token) {
		if (vTask != null && !TextUtils.isEmpty(vTask.getCjd_url())) {
			StringBuffer sb = new StringBuffer();
			sb.append(vTask.getCjd_url()).append("?").append("token=").append(token);
			Intent intent = new Intent(this, HCWebViewActivity.class);
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_URL, sb.toString());
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_ID, vTask.getId());
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_ENABLE, true);
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_URL, vTask.getCjd_report_pdf());
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD, HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CJD);
			startActivity(intent);
		}
	}

	/**
	 * 车源回访记录
	 */
	@OnClick(R.id.rb_transaction_vehicle_revisit)
	private void toVehicleRevisit(View v) {
		if (vTask != null) {
			HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_VEHICLE_VISIT_RECORD
					, HCUtils.getNetType(), System.currentTimeMillis(), 1);

			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", vTask.getVehicle_source_id());
			map.put("onlyViewId", true);
			map.put("seller_phone", vTask.getSeller_phone());
			map.put("seller_name", vTask.getSeller_name());
			HCActionUtil.launchActivity(this, VehicleRevisitListActivity.class, map);
		}
	}

	/**
	 * 买家回访记录
	 */
	@OnClick(R.id.rb_transaction_buyer_revisit)
	private void toBuyerRevisit(View v) {
		if (vTask != null) {
			HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_BUYER_VISIT_RECORD
					, HCUtils.getNetType(), System.currentTimeMillis(), 1);

			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", vTask.getVehicle_source_id());
			map.put("buyer_phone", vTask.getBuyer_phone());
			HCActionUtil.launchActivity(this, BuyerRevisitListActivity.class, map);
		}
	}

	/**
	 * 听录音
	 */
	@OnClick(R.id.rb_transaction_recording)
	private void toRecording(View v) {
		if (vTask != null) {

			HCStatistic.readyClick(trans_id, HCConst.ReadyInfo.READYINFO_TYPE_VOICE
					, HCUtils.getNetType(), System.currentTimeMillis(), 1);

			Map<String, Object> map = new HashMap<>();
			map.put("id", vTask.getId());
			map.put(PhoneRecorListActivity.KEY_INTENT_EXTRA_SALER_PHONE, vTask.getSeller_phone());
			map.put(PhoneRecorListActivity.KEY_INTENT_EXTRA_BUYER_PHONE, vTask.getBuyer_phone());
			map.put(PhoneRecorListActivity.KEY_TASK_TYPE, TaskConstants.MESSAGE_VIEW_TASK);
			HCActionUtil.launchActivity(this, PhoneRecorListActivity.class, map);
		}
	}

	/**
	 * 相似车源
	 */
	@OnClick(R.id.similar_transaction)
	private void toSimilarTransaction(View v) {
		if (vTask == null) {
			ToastUtil.showInfo("买家数据异常，请刷新页面后重试");
			return;
		}

		// 默认为10万的买家可能比较多
		float sellerPrice = 10f;
		try {
			sellerPrice = Float.parseFloat(vTask.getSeller_price());
		} catch (Exception e) {
			sellerPrice = 10f;
		}
		Intent intent = new Intent(TransactionTaskDetailActivity.this, SimilarCarWebViewActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_URL, Debug.WAP_SERVER + "/vehicle_list/f_" + getWapPrice(sellerPrice) + ".html");
		intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.similar_transaction));
		intent.putExtra(TakeLookActivity.EXTRA_BUYER_NAME, vTask.getBuyer_name());
		intent.putExtra(TakeLookActivity.EXTRA_BUYER_PHONE, vTask.getBuyer_phone());
		startActivityForResult(intent, REQUEST_CODE_TAKE_LOOK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) return;

		switch (requestCode) {
			case TaskConstants.PREPAY_TRANSACTION://预定
			case TaskConstants.QUIT_BUY_TRANSACTION://看车失败
			case TaskConstants.TRANSACTION_STORE_TAKE://展厅车带看
			case TaskConstants.TRANSACTION_STORE_ARRIVAL://展厅车到店记录
				setResult(RESULT_OK);
				finish();
				break;
			case REQUEST_CODE_TAKE_LOOK:// 相似车源
				mIsTakeLookSuccess = true;
				break;
		}
	}

	/**
	 * 界面返回
	 */
	protected void registerTitleBack() {
		View backBtn = findViewById(R.id.tv_common_back);
		if (backBtn != null) {
			backBtn.setVisibility(View.VISIBLE);
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mIsTakeLookSuccess) {
						setResult(Activity.RESULT_OK);
					}
					finish();
				}
			});
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mIsTakeLookSuccess) {
				setResult(Activity.RESULT_OK);
			}
			finish();
			return true;
		}
		return false;
	}

	/**
	 * 根据接口下发的报价映射wap列表页筛选条件的价格
	 */
	private String getWapPrice(float price) {
		String wapPrice = "p0-2";
		if (price <= 2) {
			wapPrice = "p0-2";
		} else if (price > 2 && price <= 3) {
			wapPrice = "p2-3";
		} else if (price > 3 && price <= 5) {
			wapPrice = "p3-5";
		} else if (price > 5 && price <= 7) {
			wapPrice = "p5-7";
		} else if (price > 7 && price <= 9) {
			wapPrice = "p7-9";
		} else if (price > 9 && price <= 12) {
			wapPrice = "p9-12";
		} else if (price > 12 && price <= 15) {
			wapPrice = "p12-15";
		} else if (price > 15 && price <= 20) {
			wapPrice = "p15-20";
		} else if (price > 20 && price <= 30) {
			wapPrice = "p20-30";
		} else if (price > 30 && price <= 10000) {
			wapPrice = "p30-10000";
		}
		return wapPrice;
	}
}
