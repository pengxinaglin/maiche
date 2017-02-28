package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;

/**
 * Created by PengXianglin on 16/9/7.
 */
public class StoreArrivalActivity extends CommonStateActivity {

	@ViewInject(R.id.rg_isarrival)
	private RadioGroup rg_isarrival;//是否到店

	@ViewInject(R.id.tv_arrival_time)
	private TextView tv_arrival_time;//到店时间

	@ViewInject(R.id.sp_buyer_level)
	private Spinner sp_buyer_level;//客户级别

	@ViewInject(R.id.sp_buyer_revisit)
	private Spinner sp_buyer_revisit;//下次回访时间

	@ViewInject(R.id.et_revisit)
	private EditText et_revisit;//回访记录

	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_store_arrival;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.see_failure);
	}

	@Override
	protected void initData() {
		sp_buyer_level.setSelection(1);
	}

	@OnClick(R.id.tv_arrival_time)
	private void onClickArrivalTime(View v) {

		if (rg_isarrival.getCheckedRadioButtonId() == -1) {
			ToastUtil.showInfo("请先选择是否到店");
			return;
		}

		if (rg_isarrival.getCheckedRadioButtonId() % 2 == 1) {
			ToastUtil.showInfo("未到店，无需选择此项");
			return;
		}

		DisplayUtils.displayTimeWhellYMD(this, (TextView) v, R.string.hc_transaction_customer_arrival, true, true);
	}

	/**
	 * 选择客户级别
	 */
	@OnItemSelected(R.id.sp_buyer_level)
	private void onClickBuyerLevel(AdapterView<?> adapterView, View view, int position, long l) {
		switch (position) {
			case 0:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(StoreArrivalActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_n)));
				break;
			case 1:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(StoreArrivalActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_b)));
				break;
			case 2:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(StoreArrivalActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_a)));
				break;
			case 3:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(StoreArrivalActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_h)));
				break;
		}
	}

	@OnClick(R.id.revisit_button)
	private void onClickCommit(View v) {
		if (!getIntent().hasExtra(StoreTakeActivity.EXTAR_KEY_TASKID)) {
			ToastUtil.showInfo("缺少参数");
			return;
		}

		if (rg_isarrival.getCheckedRadioButtonId() == -1) {
			ToastUtil.showInfo("请选择是否到店");
			return;
		}

		if (TextUtils.isEmpty(et_revisit.getText().toString())) {
			ToastUtil.showInfo("请填写回访记录");
			return;
		}

		String taskId = getIntent().getStringExtra(StoreTakeActivity.EXTAR_KEY_TASKID);
		String content = et_revisit.getText().toString();
		int level = sp_buyer_level.getSelectedItemPosition() + 2;
		int arriveStatus = rg_isarrival.getCheckedRadioButtonId() % 2 == 0 ? 20 : 30;// 是否到店 20:到店 30：未到店
		int unixTime = UnixTimeUtil.getUnixTime(tv_arrival_time.getText().toString());
		int arrive_time = unixTime == 0 ? (int) (System.currentTimeMillis() / 1000L) : unixTime;
		int due_time;
		//N级
		if (sp_buyer_level.getSelectedItemPosition() == 0) {
			due_time = sp_buyer_revisit.getSelectedItemPosition();
		}
		//B.A.H级
		else {
			due_time = sp_buyer_revisit.getSelectedItemPosition() + 1;
		}

		ProgressDialogUtil.showProgressDialog(this);
		AppHttpServer.getInstance().post(HCHttpRequestParam.addExhibitionRecordNew(taskId, content, level
				, arriveStatus, arrive_time, due_time), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) return;

		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_EDITEXHIBITIONTRANS)) {
			responseAddRevisit(response);
		}
	}

	/**
	 * 处理添加到店回访
	 */
	private void responseAddRevisit(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//修改订阅条件
				if (rg_isarrival.getCheckedRadioButtonId() == R.id.rb_meet) {
					ProgressDialogUtil.closeProgressDialog();
					//更改订阅
					changeSubscribeCondition();
				} else {
					this.setResult(RESULT_OK);
					this.finish();
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 跳转修改车源订阅条件页面
	 */
	private void changeSubscribeCondition() {
		Intent intent = new Intent(this, VehicleSubScribeConditionActivity.class);
		intent.putExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_PHONE, getIntent().getStringExtra(StoreTakeActivity.EXTAR_KEY_PHONE));
		intent.putExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_NAME, getIntent().getStringExtra(StoreTakeActivity.EXTAR_KEY_NAME));
		startActivityForResult(intent, TaskConstants.REQUEST_CODE_CONDITION_MODIFY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
}
