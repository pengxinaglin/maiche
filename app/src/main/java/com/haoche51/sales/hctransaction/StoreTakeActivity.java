package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hcrecommend.BuyerCluesEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 展厅车添加带看
 * Created by PengXianglin on 16/9/7.
 */
public class StoreTakeActivity extends CommonStateActivity {

	public static String EXTAR_KEY_TASKID = "exhibition_task_id";
	public static String EXTAR_KEY_SOURCE = "trans_source";
	public static String EXTAR_KEY_PHONE = "buyer_phone";
	public static String EXTAR_KEY_NAME = "buyer_name";
	public static String EXTAR_KEY_PLACE = "trans_place";

	@Required(order = 1, message = "填写买家电话")
	@ViewInject(R.id.et_buyer_phone)
	private EditText et_buyer_phone;

	@Required(order = 2, message = "填写买家姓名")
	@ViewInject(R.id.et_buyer_name)
	private EditText et_buyer_name;

	@ViewInject(R.id.spn_select_sale)
	private Spinner spn_select_sale;

	@Required(order = 3, message = "填写车源编号")
	@ViewInject(R.id.et_vehicl_source_id)
	private EditText et_vehicl_source_id;

	@Required(order = 4, message = "填写看车地点")
	@ViewInject(R.id.et_task_address)
	private EditText et_task_address;

	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_store_take;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.activity_store_take_title);

		et_buyer_phone.setText(getIntent().getStringExtra(EXTAR_KEY_PHONE));
		et_buyer_name.setText(getIntent().getStringExtra(EXTAR_KEY_NAME));
		et_task_address.setText(getIntent().getStringExtra(EXTAR_KEY_PLACE));
	}

	@Override
	protected void initData() {
		ProgressDialogUtil.showProgressDialog(this);
		AppHttpServer.getInstance().post(HCHttpRequestParam.getUserListByFilter(), this, 0);
	}

	@OnClick(R.id.take_look_button)
	private void onClickCommit(View v) {
		if (!getIntent().hasExtra(EXTAR_KEY_TASKID) || !getIntent().hasExtra(EXTAR_KEY_SOURCE)) {
			ToastUtil.showInfo("缺少参数");
			return;
		}

		validator.validate();
	}

	@Override
	public void onValidationSucceeded() {
		SalesInfoEntity selectedItem = (SalesInfoEntity) spn_select_sale.getSelectedItem();
		if (selectedItem == null || selectedItem.getId() == 0) {
			ToastUtil.showInfo("请选择地销");
			return;
		} else if (selectedItem.getId() == GlobalData.userDataHelper.getUser().getId()) {
			ToastUtil.showInfo("地销不能选择自己");
			return;
		}

		String phone = et_buyer_phone.getText().toString();
		String name = et_buyer_name.getText().toString();
		String sourceId = et_vehicl_source_id.getText().toString();
		String address = et_task_address.getText().toString();

		String taskId = getIntent().getStringExtra(EXTAR_KEY_TASKID);
		int accompanyUserId = selectedItem.getId();
		int appointmentStarttime = (int) (System.currentTimeMillis() / 1000L);
		int transSource = getIntent().getIntExtra(EXTAR_KEY_SOURCE, 0);

		ProgressDialogUtil.showProgressDialog(this);
		AppHttpServer.getInstance().post(HCHttpRequestParam.makeAppointment(taskId, accompanyUserId, phone
				, name, sourceId, appointmentStarttime, transSource, address), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) return;

		ProgressDialogUtil.closeProgressDialog();

		if (action.equals(HttpConstants.ACTION_MAKE_APPOINTMENT)) {
			responseAddTake(response);
		} else if (action.equals(HttpConstants.ACTION_GET_USERLIST_BYFILTER)) {
			responseGetSales(response);
		}
	}

	/**
	 * 处理获取地销列表
	 */
	private void responseGetSales(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				List<SalesInfoEntity> data = new ArrayList<SalesInfoEntity>();
				SalesInfoEntity entity = new SalesInfoEntity();
				entity.setName("选择地销");
				data.add(entity);

				List<SalesInfoEntity> salesInfoEntities = HCJsonParse.parseGeSalesInfoEntitys(response.getData());
				if (salesInfoEntities != null) {
					data.addAll(salesInfoEntities);
				}
				spn_select_sale.setAdapter(new ArrayAdapter<SalesInfoEntity>(this, android.R.layout.simple_spinner_dropdown_item, data));
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 处理加带看
	 */
	private void responseAddTake(HCHttpResponse response) {
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
}
