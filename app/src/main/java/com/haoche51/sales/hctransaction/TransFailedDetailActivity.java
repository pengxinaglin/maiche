package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.custom.TagsLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hccustomer.CustomerDetailActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * 带看失败 ——》 失败详情
 * Created by mac on 15/11/3.
 */
public class TransFailedDetailActivity extends CommonStateActivity {

	@ViewInject(R.id.tv_audit_status)
	private TextView tv_audit_status;//状态

	@ViewInject(R.id.tv_transaction_suc_buyer_name)
	private TextView tv_buyer_name;//买家

	@ViewInject(R.id.tv_vehicle_store)
	private TextView tv_vehicle_store;//车商

	@ViewInject(R.id.tv_transaction_suc_buyer_phone)
	private TextView tv_buyer_phone;//买家电话

	@ViewInject(R.id.tv_transfer_order_number)
	private TextView tv_order_number;//订单号

	@ViewInject(R.id.tv_transaction_vehicle)
	private TextView tv_vehicle;//车型

	@ViewInject(R.id.tv_transfer_vc_id)
	private TextView tv_vehicle_id;//车源编号

	@ViewInject(R.id.tv_vehicle_plate_number)
	private TextView tv_plate_number;//车牌号

	@ViewInject(R.id.tv_vehicle_type_text)
	private TextView tv_type_text;//车源标签

	@ViewInject(R.id.ll_vehicle_tag)
	private TagsLayout ll_vehicle_tag;//车源标签

	@ViewInject(R.id.tv_seller_name)
	private TextView tv_seller_name;//车主姓名

	@ViewInject(R.id.tv_seller_phone)
	private TextView tv_seller_phone;//车主电话

	@ViewInject(R.id.tv_trans_fail_reason)
	private TextView tv_fail_reason;//失败原因

	@ViewInject(R.id.btn_commit)
	private Button btn_commit;

	TransactionTaskEntity mTaskEntity;
	int trans_id;

	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_fail_detail;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_trans_task_activity_title);
		btn_commit.setText("查看客户详情");
		tv_audit_status.setText(getString(R.string.see_failure));
		tv_audit_status.setVisibility(View.VISIBLE);
	}

	@Override
	protected void initData() {
		this.trans_id = getIntent().getIntExtra("trans_id", 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (trans_id > 0) {
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id), this, 0);
		}
	}

	/**
	 * 设置数据
	 */
	private void setData() {
		try {
			tv_buyer_name.setText(mTaskEntity.getBuyer_name());
			tv_buyer_phone.setText(mTaskEntity.getBuyer_phone());
			if (mTaskEntity.getCar_dealer() == 1) {
				tv_vehicle_store.setVisibility(View.VISIBLE);
			}
			if (!TextUtils.isEmpty(mTaskEntity.getOrder_number())) {
				tv_order_number.setText(mTaskEntity.getOrder_number());
			}
			tv_vehicle.setText(mTaskEntity.getVehicle_name());//客户所看车辆
			tv_vehicle_id.setText(mTaskEntity.getVehicle_source_id());//所看车辆id
			tv_plate_number.setText(mTaskEntity.getPlate_number());//车牌号
			if (!TextUtils.isEmpty(mTaskEntity.getVehicle_type_text())) {
				tv_type_text.setText(mTaskEntity.getVehicle_type_text());//车源类型
			}
			//车源更多标签
			if (mTaskEntity.getVehicle_tag() != null && !mTaskEntity.getVehicle_tag().isEmpty()) {
				ll_vehicle_tag.removeAllViews();
				ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.setMargins(20, 0, 0, 0);
				for (int i = 0; i < mTaskEntity.getVehicle_tag().size(); i++) {
					TextView textView = new TextView(this);
					textView.setTextAppearance(this, R.style.vehicle_lable);
					Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_vehicle_tag);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					textView.setCompoundDrawables(drawable, null, null, null);
					textView.setCompoundDrawablePadding(10);
					textView.setText(mTaskEntity.getVehicle_tag().get(i));
					ll_vehicle_tag.addView(textView, lp);
				}
			}
			tv_seller_name.setText(mTaskEntity.getSeller_name());//车主姓名
			tv_seller_phone.setText(mTaskEntity.getSeller_phone());
			tv_fail_reason.setText(mTaskEntity.getAbort_reason());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.tv_seller_phone)
	private void callSeller(View view) {
		//用intent启动拨打电话
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_seller_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 查看客户详情
	 */
	@OnClick(R.id.btn_commit)
	private void toDetail(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("phone", mTaskEntity.getBuyer_phone());
			HCActionUtil.launchActivity(this, CustomerDetailActivity.class, map);
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		dismissLoadingView();
		ProgressDialogUtil.closeProgressDialog();

		switch (response.getErrno()) {
			case 0:
				Gson gson = new Gson();
				mTaskEntity = gson.fromJson(response.getData(), TransactionTaskEntity.class);
				if (mTaskEntity != null)
					setData();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				showErrorView(false, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismissResultView(true);
						showLoadingView(false);
						AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id),
								TransFailedDetailActivity.this, 0);
					}
				});
				break;
		}
	}
}
