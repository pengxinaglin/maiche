package com.haoche51.sales.hcrecommend;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加车源推荐
 * Created by wufx on 2016/3/28.
 */
public class CheckAddCluesActivity extends CommonStateActivity {


	/**
	 * 车主姓名
	 */
	@ViewInject(R.id.ed_seller_name)
	@Required(order = 1, message = "车主姓名不能为空")
	private EditText ed_seller_name;

	/**
	 * 车主电话
	 */
	@ViewInject(R.id.ed_seller_phone)
	@Required(order = 2, message = "车主电话不能为空")
	private EditText ed_seller_phone;

	/**
	 * 品牌车系
	 */
	@ViewInject(R.id.tv_vehicle_model)
	@Required(order = 3, message = "品牌车系不能为空")
	private TextView tv_vehicle_model;

	@ViewInject(R.id.spn_city)
	private Spinner spn_city;

	/**
	 * 品牌车系实体类
	 */
	private VehicleSeriesEntity vehicleSeries;

	private List<CityInfo> cityInfoList = new ArrayList<>();

	@Override
	protected int getContentView() {
		return R.layout.activity_add_vehicle_recommend;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_addbuyer_lead_activity_title);
		ed_seller_name.requestFocus();
	}

	@Override
	protected void initData() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getOnlineCity(), this, 0);
	}

	/**
	 * 确认添加
	 */
	@OnClick(R.id.btn_commit)
	private void commit(View v) {
		validator.validate();
	}

	/**
	 * 校验成功
	 */
	@Override
	public void onValidationSucceeded() {
		super.onValidationSucceeded();
		String sellerPhone = ed_seller_phone.getText().toString().trim();

		if (!PhoneNumberUtil.isPhoneNumberValid(sellerPhone)) {
			onValidateFailed(ed_seller_phone, "车主电话不正确");
			return;
		}

		CheckCarClueEntity purchaseClue = new CheckCarClueEntity();
		User mUser = GlobalData.userDataHelper.getUser();
		purchaseClue.setId(mUser.getId());
		purchaseClue.setName(mUser.getName());
		//设置品牌和车系信息
		if (vehicleSeries != null) {
			purchaseClue.setBrand_id(vehicleSeries.getBrand_id());
			purchaseClue.setBrand_name(vehicleSeries.getBrand_name());
			purchaseClue.setClass_id(vehicleSeries.getId());
			purchaseClue.setClass_name(vehicleSeries.getName());
		}

		purchaseClue.setSeller_name(ed_seller_name.getText().toString().trim());
		purchaseClue.setSeller_phone(sellerPhone);
		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.addVehicleRecom(purchaseClue, cityInfoList.isEmpty() ? 0
				: ((CityInfo) spn_city.getSelectedItem()).getId()), this, 0);
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

		if (HttpConstants.ACTION_ADD_VEHICLE_RECOM.equals(action)) {
			switch (response.getErrno()) {
				case 0://0：表示接口请求成功
					ToastUtil.showInfo("添加车源推荐成功！");
					setResult(RESULT_OK);
					finish();
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

}