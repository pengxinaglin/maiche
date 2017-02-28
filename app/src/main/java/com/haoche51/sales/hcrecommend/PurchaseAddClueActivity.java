package com.haoche51.sales.hcrecommend;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.User;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransaction.VehicleSeriesEntity;
import com.haoche51.sales.hctransaction.VehicleSubBrandAddActivity;
import com.haoche51.sales.hctransaction.VehicleSubScribeConditionActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.PhoneNumberUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;


/**
 * 回购添加线索
 * Created by pengxianglin on 16/6/12.
 */
public class PurchaseAddClueActivity extends CommonStateActivity implements RadioGroup.OnCheckedChangeListener, View.OnFocusChangeListener {

	/**
	 * 选择车源类型
	 */
	@ViewInject(R.id.rg_clue_type)
	private RadioGroup rg_clue_type;
	/**
	 * 内网车源
	 */
	@ViewInject(R.id.rb_inside)
	private RadioButton rb_inside;
	/**
	 * 外网车源输入信息
	 */
	@ViewInject(R.id.ll_outside_source)
	private LinearLayout ll_outside_source;
	/**
	 * 内网车源输入信息
	 */
	@ViewInject(R.id.ll_inside_source)
	private LinearLayout ll_inside_source;
	/**
	 * 车源编号
	 */
	@ViewInject(R.id.et_vehicle_source_id)
	private EditText et_vehicle_source_id;
	/**
	 * 车主姓名
	 */
	@ViewInject(R.id.ed_seller_name)
	private EditText ed_seller_name;
	/**
	 * 车主电话
	 */
	@ViewInject(R.id.ed_seller_phone)
	private EditText ed_seller_phone;
	/**
	 * 品牌车系
	 */
	@ViewInject(R.id.tv_vehicle_model)
	private TextView tv_vehicle_model;
	/**
	 * 任务备注
	 */
	@ViewInject(R.id.ed_remark)
	private EditText ed_remark;

	@ViewInject(R.id.spn_city)
	private Spinner spn_city;

	/**
	 * 品牌车系实体类
	 */
	private VehicleSeriesEntity vehicleSeries;

	private List<CityInfo> cityInfoList = new ArrayList<>();

	@Override
	protected int getContentView() {
		return R.layout.activity_purchase_add_clue;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_addbuyer_lead_activity_title);
		//监听选择车源类型
		rg_clue_type.setOnCheckedChangeListener(this);
		//监听车源id输入框焦点
		et_vehicle_source_id.setOnFocusChangeListener(this);
	}

	@Override
	protected void initData() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getOnlineCity(), this, 0);
	}

	/**
	 * 提交收车线索
	 */
	@OnClick(R.id.btn_commit_clue)
	private void commit(View v) {
		//内网车
		if (rg_clue_type.getCheckedRadioButtonId() == R.id.rb_inside) {
			if (TextUtils.isEmpty(et_vehicle_source_id.getText().toString())) {
				onValidateFailed(et_vehicle_source_id, "车源编号不能为空");
				return;
			}
		}
		//外网车
		else {
			if (TextUtils.isEmpty(ed_seller_name.getText().toString())) {
				onValidateFailed(ed_seller_name, "车主姓名不能为空");
				return;
			}
			if (TextUtils.isEmpty(ed_seller_phone.getText().toString())) {
				onValidateFailed(ed_seller_phone, "车主电话不能为空");
				return;
			}
			String sellerPhone = ed_seller_phone.getText().toString().trim();
			if (!PhoneNumberUtil.isPhoneNumberValid(sellerPhone)) {
				onValidateFailed(ed_seller_phone, "车主电话不正确");
				return;
			}
			if (TextUtils.isEmpty(tv_vehicle_model.getText().toString()) || vehicleSeries == null) {
				ToastUtil.showInfo("品牌车系不能为空");
				return;
			}
		}

		if (TextUtils.isEmpty(ed_remark.getText().toString())) {
			onValidateFailed(ed_remark, "任务备注不能为空");
			return;
		}

		PurchaseClueEntity purchaseClue = new PurchaseClueEntity();
		User user = GlobalData.userDataHelper.getUser();
		purchaseClue.setId(user.getId());
		purchaseClue.setName(user.getName());
		//设置品牌和车系信息
		if (vehicleSeries != null) {
			purchaseClue.setBrand_id(vehicleSeries.getBrand_id());
			purchaseClue.setBrand_name(vehicleSeries.getBrand_name());
			purchaseClue.setClass_id(vehicleSeries.getId());
			purchaseClue.setClass_name(vehicleSeries.getName());
		}
		purchaseClue.setSeller_name(ed_seller_name.getText().toString().trim());
		purchaseClue.setSeller_phone(ed_seller_phone.getText().toString().trim());
		purchaseClue.setRemark(ed_remark.getText().toString().trim());

		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.addPurchaseClue(purchaseClue,
				rb_inside.isChecked() ? Integer.parseInt(et_vehicle_source_id.getText().toString().trim()) : 0,
				cityInfoList.isEmpty() ? 0 : ((CityInfo) spn_city.getSelectedItem()).getId()), this, 0);
	}

	/**
	 * 显示校验失败消息
	 *
	 * @param failedView
	 * @param message
	 */
	private void onValidateFailed(EditText failedView, String message) {
		failedView.requestFocus();
		failedView.setError(message);
	}

	/**
	 * 选择车系
	 */
	@OnClick(R.id.tv_vehicle_model)
	private void tv_vehicle_model(View v) {
		Intent intent = new Intent(this, VehicleSubBrandAddActivity.class);
		startActivityForResult(intent, TaskConstants.REQUEST_GET_BRAND_CLASS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null || resultCode != Activity.RESULT_OK) {
			return;
		}
		vehicleSeries = (VehicleSeriesEntity) data.getSerializableExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_SERIES);
		if (vehicleSeries != null) {
			tv_vehicle_model.setText(vehicleSeries.getBrand_name() + " " + vehicleSeries.getName());
		}
	}


	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();

		if (HttpConstants.ACTION_ADD_BACK_TASK.equals(action)) {
			switch (response.getErrno()) {
				case 0://0：表示接口请求成功
					ToastUtil.showInfo("添加线索成功！");
					setResult(RESULT_OK);
					finish();
					break;
				default://1：发生错误
					ToastUtil.showInfo(response.getErrmsg());
					break;
			}
		} else if (HttpConstants.ACTION_GET_SOURCETITLE.equals(action)) {
			switch (response.getErrno()) {
				case 0://0：表示接口请求成功
					if (TextUtils.isEmpty(response.getData())) {
						tv_vehicle_model.setText("未查询到结果");
					} else {
						//选择了内网车源 否则不显示
						if (rb_inside.isChecked())
							tv_vehicle_model.setText(response.getData());
					}
					break;
				default://1：发生错误
					ToastUtil.showInfo(response.getErrmsg());
					break;
			}
		} else if (action.equals(HttpConstants.ACTION_GET_ONLINE_CITY)) {
			responseGetOnLineCity(response);
		}
	}

	/**
	 * 返回获得在线城市
	 */
	private void responseGetOnLineCity(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				cityInfoList = HCJsonParse.parseOnLineCityInfos(response.getData());
				if (cityInfoList != null) {
					User user = GlobalData.userDataHelper.getUser();
					spn_city.setAdapter(new ArrayAdapter<CityInfo>(this, android.R.layout.simple_list_item_1, cityInfoList));
					int size = cityInfoList.size();
					for (int i = 0; i < size; i++) {
						if (user.getCity() == cityInfoList.get(i).getId()) {
							spn_city.setSelection(i);
							break;
						}
					}
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int id) {
		switch (id) {
			case R.id.rb_outside://外网
				//隐藏内网表单 可点击选择车系
				ll_outside_source.setVisibility(View.VISIBLE);
				ll_inside_source.setVisibility(View.GONE);
				tv_vehicle_model.setClickable(true);
				tv_vehicle_model.setHint("点击选择");
				break;
			case R.id.rb_inside://内网
				//隐藏外网表单 不可点击选择车系
				ll_inside_source.setVisibility(View.VISIBLE);
				ll_outside_source.setVisibility(View.GONE);
				tv_vehicle_model.setClickable(false);
				tv_vehicle_model.setHint("自动检索");
				break;
		}
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (!hasFocus) {
			if (!rb_inside.isChecked()) return;

			if (TextUtils.isEmpty(et_vehicle_source_id.getText().toString().trim())) {
				onValidateFailed(et_vehicle_source_id, "请输入车源编号");
				return;
			}
			int sourceId = Integer.parseInt(et_vehicle_source_id.getText().toString());
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			AppHttpServer.getInstance().post(HCHttpRequestParam.getVehicleSourceTitle(sourceId), this, 0);
		}
	}
}