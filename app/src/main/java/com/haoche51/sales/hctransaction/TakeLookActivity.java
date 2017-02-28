package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.time.ScreenInfo;
import com.haoche51.sales.custom.time.WheelMain;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Calendar;
import java.util.Map;

/**
 * 相似车源加带看
 */
public class TakeLookActivity extends CommonStateFragmentActivity {

	public static final String EXTRA_IS_SIMILAR = "is_similar";
	public static final String EXTRA_BUYER_NAME = "buyer_name";
	public static final String EXTRA_BUYER_PHONE = "buyer_phone";
	public static final String EXTRA_SOURCE_ID = "source_id";

	private String mBuyerName;
	private String mBuyerPhone;
	private String mSourceId;
	private int mTakeLookTime;
	private boolean isSimilar = true;

	@ViewInject(R.id.transaction_code_edit_text)
	private EditText transaction_code_edit_text;

	@ViewInject(R.id.take_look_time_view)
	private TextView take_look_time_view;

	@ViewInject(R.id.ll_buyer_phone)
	private LinearLayout ll_buyer_phone;
	@ViewInject(R.id.transaction_buyer_phone_edit_text)
	private EditText transaction_buyer_phone_edit_text;

	@ViewInject(R.id.ll_buyer_name)
	private LinearLayout ll_buyer_name;
	@ViewInject(R.id.transaction_buyer_name_edit_text)
	private EditText transaction_buyer_name_edit_text;

	@ViewInject(R.id.tv_task_type)
	private TextView tv_task_type;

	@Override
	protected int getTitleView() {
		return R.layout.title_bar;
	}

	@Override
	protected int getContentView() {
		return R.layout.transaction_activity_take_look;
	}

	@Override
	protected void initView() {
		super.initView();
		isSimilar = getIntent().getBooleanExtra(EXTRA_IS_SIMILAR, true);
		mBuyerName = getIntent().getStringExtra(EXTRA_BUYER_NAME);
		mBuyerPhone = getIntent().getStringExtra(EXTRA_BUYER_PHONE);
		mSourceId = getIntent().getStringExtra(EXTRA_SOURCE_ID);
		if (isSimilar && (TextUtils.isEmpty(mBuyerName) || TextUtils.isEmpty(mBuyerPhone))) {
			finish();
		}

		ViewUtils.inject(this);
		registerTitleBack();
		setScreenTitle(getString(R.string.take_look));

		if (!isSimilar) {
			ll_buyer_phone.setVisibility(View.VISIBLE);
			ll_buyer_name.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(mSourceId)) {
			transaction_code_edit_text.setText(mSourceId);
			transaction_code_edit_text.setEnabled(false);
		}
	}

	/**
	 * 点击带看时间
	 */
	@OnClick(R.id.take_look_time_view)
	private void onTakeLookTime(View view) {
		String sourceId = transaction_code_edit_text.getText().toString();
		if (TextUtils.isEmpty(sourceId)) {
			ToastUtil.showInfo("请填写车源编号");
			return;
		} else {
			//查询一下车源类型
			ProgressDialogUtil.showProgressDialog(this);
			AppHttpServer.getInstance().post(HCHttpRequestParam.getVehicleInfoById(StringUtil.parseInt(sourceId, 0)), this, 0);
		}

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.select_take_look_time));

		final View timerView = LayoutInflater.from(this).inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(this);
		final WheelMain wheelMain = new WheelMain(timerView, true);
		wheelMain.screenheight = screenInfo.getHeight();

		Calendar calendar = Calendar.getInstance();
		if (mTakeLookTime > 0) {
			long time = mTakeLookTime * 1000L;
			calendar.setTimeInMillis(time);
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);

		wheelMain.initDateTimePicker(year, month, day, hour, min);
		builder.setView(timerView);
		builder.setNegativeButton(getString(R.string.soft_update_cancel), null);
		builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTakeLookTime = UnixTimeUtil.getUnixTime(wheelMain.getTime());
				if (mTakeLookTime < System.currentTimeMillis() / 1000) {
					ToastUtil.showInfo(getString(R.string.no_choose));
				} else {
					take_look_time_view.setText(UnixTimeUtil.format(mTakeLookTime));
				}
			}
		});
		builder.show();
	}

	/**
	 * 提交
	 */
	@OnClick(R.id.take_look_button)
	private void onCommit(View view) {

		if (!isSimilar) {
			mBuyerName = transaction_buyer_name_edit_text.getText().toString();
			mBuyerPhone = transaction_buyer_phone_edit_text.getText().toString();
		}
		String code = transaction_code_edit_text.getText().toString();

		if (TextUtils.isEmpty(mBuyerPhone)) {
			ToastUtil.showInfo("请填写买家电话");
			return;
		}
		if (TextUtils.isEmpty(mBuyerName)) {
			ToastUtil.showInfo("请填写买家姓名");
			return;
		}
		if (TextUtils.isEmpty(code)) {
			ToastUtil.showInfo(getString(R.string.please_write_transaction_code));
			return;
		}
		if (mTakeLookTime <= 0) {
			ToastUtil.showInfo(getString(R.string.please_select_take_look_time));
			return;
		}

		//车源id被修改了，重新查询一下
		if (vehicleInfo != null && !vehicleInfo.getVehicle_source_id().equals(code)) {
			//查询一下车源类型
			ProgressDialogUtil.showProgressDialog(this);
			AppHttpServer.getInstance().post(HCHttpRequestParam.getVehicleInfoById(StringUtil.parseInt(code, 0)), this, 0);
			return;
		}

		ProgressDialogUtil.showProgressDialog(TakeLookActivity.this, getString(R.string.commiting));
		Map<String, Object> params = HCHttpRequestParam.takeLook(mBuyerPhone, mBuyerName, code, mTakeLookTime);
		AppHttpServer.getInstance().post(params, TakeLookActivity.this, HttpConstants.GET_LIST_REFRESH);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_TRANSACTION_GETVEHICLEINFOBYID)) {
			responseGetTaskType(response);
		} else if (action.equals(HttpConstants.ACTION_MAKE_LOOK)) {
			responseMakeLook(response);
		}
	}

	private void responseMakeLook(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				ToastUtil.showInfo(getString(R.string.take_look_success));
				setResult(Activity.RESULT_OK);
				finish();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	TransactionTaskEntity vehicleInfo;

	private void responseGetTaskType(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				TransactionTaskEntity vehicleInfo = HCJsonParse.parseTransTaskEntity(response.getData());
				if (vehicleInfo == null) {
					ToastUtil.showInfo("任务类型解析出错");
					finish();
				} else {
					switch (vehicleInfo.getVehicle_type()) {
						case 1:
						case 3:
						case 4:
							tv_task_type.setText("展厅任务");
							break;
						default:
							tv_task_type.setText("户外任务");
							break;
					}
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}
}
