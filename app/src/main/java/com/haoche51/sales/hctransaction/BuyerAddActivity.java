package com.haoche51.sales.hctransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoche51.sales.R;
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
import com.loopj.android.http.RequestHandle;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.Map;

import okhttp3.Call;

/**
 * 买家列表--添加买家
 * Created by yangming on 2016/5/18.
 */
public class BuyerAddActivity extends CommonStateActivity {

	@Required(order = 1, message = "填写姓名")
	@ViewInject(R.id.et_name)
	private EditText et_name;

	@Required(order = 2, message = "填写电话号码")
	@ViewInject(R.id.et_phone)
	private EditText et_phone;

	@Required(order = 4, message = "填写微信")
	@ViewInject(R.id.et_weixin)
	private EditText et_weixin;
	@ViewInject(R.id.checkbox)
	private CheckBox checkbox;

	@Required(order = 5, message = "填写备注")
	@ViewInject(R.id.et_comment)
	private EditText et_comment;

	@ViewInject(R.id.sp_buyer_level)
	private Spinner sp_buyer_level;

	@ViewInject(R.id.sp_buyer_revisit)
	private Spinner sp_buyer_revisit;

	@ViewInject(R.id.rg_car_dealer)
	private RadioGroup rg_car_dealer;

	@ViewInject(R.id.rg_arrival)
	private RadioGroup rg_arrival;

	@ViewInject(R.id.tv_arrival_time)
	private TextView tv_arrival_time;

	@Override
	protected int getContentView() {
		return R.layout.add_buyer_activity;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle("添加买家");
	}

	@Override
	protected void initData() {
		super.initData();

		sp_buyer_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(BuyerAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_n)));
						break;
					case 1:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(BuyerAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_b)));
						break;
					case 2:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(BuyerAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_a)));
						break;
					case 3:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(BuyerAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_h)));
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		sp_buyer_level.setSelection(1);

		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					et_weixin.setText(et_phone.getText().toString());
				} else {
					et_weixin.setText("");
				}
			}
		});

		et_phone.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (checkbox.isChecked()) {
					et_weixin.setText(s.toString());
				}
			}
		});

	}

	@OnClick(R.id.tv_commit)
	private void commit(View view) {
		validator.validate();
	}

	@OnClick(R.id.tv_arrival_time)
	private void onClickArrivalTime(View view) {
		if (rg_arrival.getCheckedRadioButtonId() == -1) {
			ToastUtil.showInfo("请先选择是否到店");
			return;
		}
		if (rg_arrival.getCheckedRadioButtonId() % 2 == 0) {
			ToastUtil.showInfo("未到店，无需选择此项");
			return;
		}
		DisplayUtils.displayTimeWhellYMD(this, tv_arrival_time, R.string.select_time, true, true);
	}

	Call post;
	@Override
	public void onValidationSucceeded() {
		super.onValidationSucceeded();
		if (rg_car_dealer.getCheckedRadioButtonId() == -1) {
			showErrorMsg(rg_car_dealer, "选择是否车商");
			return;
		}
		if (rg_arrival.getCheckedRadioButtonId() == -1) {
			showErrorMsg(rg_arrival, "选择是否到店");
			return;
		}
		int lastArriveTime = 0;
		//已选择到店
		if (rg_arrival.getCheckedRadioButtonId() % 2 == 1){
			lastArriveTime = UnixTimeUtil.getUnixTime(tv_arrival_time.getText().toString());
			//没有选择到店时间
			if (lastArriveTime == 0){
				//默认提交时间
				lastArriveTime = (int) (System.currentTimeMillis() / 1000L);
			}
		}
		ProgressDialogUtil.showProgressDialog(this);
		int due_time;
		//N级
		if (sp_buyer_level.getSelectedItemPosition() == 0) {
			due_time = sp_buyer_revisit.getSelectedItemPosition();
		}
		//B.A.H级
		else {
			due_time = sp_buyer_revisit.getSelectedItemPosition() + 1;
		}
		Map<String, Object> params = HCHttpRequestParam.createBuyer(et_name.getText().toString()
				, et_phone.getText().toString(), rg_car_dealer.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1
				, sp_buyer_level.getSelectedItemPosition() + 2, due_time, et_weixin.getText().toString(),
				et_comment.getText().toString(), lastArriveTime);
		post = AppHttpServer.getInstance().post(params, this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		super.onHttpComplete(action, requestId, response, error);
		ProgressDialogUtil.closeProgressDialog();
		if (HttpConstants.ACTION_OFFLINESALERAPI_CREATEBUYER.equals(action)) {
			doResponseCreateBuyer(response);
		}
	}

	private void doResponseCreateBuyer(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				finish();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppHttpServer.getInstance().cancelRequest(post);
	}
}
