package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransfer.FinFeeInfoActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户看车记录 ———》毁约 ——》 毁约详情
 * Created by mac on 15/11/3.
 */
public class TaskBreachDetailActivity extends CommonStateActivity {

	@ViewInject(R.id.tv_buyer_name)
	private TextView tv_buyer_name;

	@ViewInject(R.id.tv_buyer_phone)
	private TextView tv_buyer_phone;

	/**
	 * 任务状态
	 */
	@ViewInject(R.id.status)
	private TextView status;

	/**
	 * 毁约方
	 */
	@ViewInject(R.id.cancel_type)
	private TextView cancel_type;

	/**
	 * 车源操作
	 */
	@ViewInject(R.id.vehicle_status)
	private TextView vehicle_status;

	/**
	 * 赠送礼物
	 */
	@ViewInject(R.id.award_info)
	private TextView award_info;

	/**
	 * 买家已付
	 */
	@ViewInject(R.id.tv_payed_buyer)
	private TextView tv_payed_buyer;

	/**
	 * 车主已付
	 */
	@ViewInject(R.id.tv_payed_seller)
	private TextView tv_payed_seller;

	/**
	 * 公司退买家
	 */
	@ViewInject(R.id.tv_refund_buyer)
	private TextView tv_refund_buyer;

	/**
	 * 公司退车主
	 */
	@ViewInject(R.id.tv_refund_seller)
	private TextView tv_refund_seller;


	@ViewInject(R.id.vehicle_source_id)
	private TextView vehicle_source_id;

	@ViewInject(R.id.vehicle_name)
	private TextView vehicle_name;

	@ViewInject(R.id.seller_info)
	private TextView seller_info;

	@ViewInject(R.id.seller_phone)
	private TextView seller_phone;

	@ViewInject(R.id.cancel_reason)
	private TextView cancel_reason;

	@ViewInject(R.id.tv_refund_way_buyer)
	private TextView tv_refund_way_buyer;

	@ViewInject(R.id.tv_refund_way_seller)
	private TextView tv_refund_way_seller;

	@ViewInject(R.id.btn_commit)
	private Button btn_commit;

	TransactionTaskEntity mTaskEntity;
	int trans_id;


	@Override
	protected int getContentView() {
		return R.layout.breach_detail_activity;
	}

	@Override
	protected void initView() {
		super.initView();
		this.trans_id = getIntent().getIntExtra("trans_id", 0);
		setScreenTitle(getString(R.string.hc_trans_task_activity_title));
		btn_commit.setVisibility(View.GONE);
		btn_commit.setText("毁约转款");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (trans_id != 0) {
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id), this, 0);
		}
	}


	private void setData() {
		try {
			tv_buyer_name.setText(mTaskEntity.getBuyer_name());
			tv_buyer_phone.setText(mTaskEntity.getBuyer_phone());

			status.setText(ControlDisplayUtil.getInstance().getTransCancelStatus(mTaskEntity.getCancel_trans_status()));

			if (mTaskEntity.getCancel_trans_info() != null && !TextUtils.isEmpty(mTaskEntity.getCancel_trans_info().getType())) {
				cancel_type.setText(mTaskEntity.getCancel_trans_info().getType().equals("buyer") ? getString(R.string.p_buyer) : getString(R.string.p_seller));
			}
			if (mTaskEntity.getAward_info() != null) {
				award_info.setText(mTaskEntity.getAward_info().getTitle());
			}

			// 买家已付、车主已付
			tv_payed_buyer.setText(mTaskEntity.getBuyer_payment());
			tv_payed_seller.setText(mTaskEntity.getSeller_company_prepay());

			// 车辆相关
			vehicle_source_id.setText(mTaskEntity.getVehicle_source_id());
			vehicle_name.setText(mTaskEntity.getVehicle_name());
			seller_info.setText(mTaskEntity.getSeller_name());
			seller_phone.setText(mTaskEntity.getSeller_phone());

			TransactionTaskEntity.CancelTransInfoEntity cancel_trans_info = mTaskEntity.getCancel_trans_info();
			if (cancel_trans_info != null) {
				int status = StringUtil.parseInt(cancel_trans_info.getVehicle_status(), 0);
				vehicle_status.setText(ControlDisplayUtil.getInstance().getVehicleStatus(status));

				cancel_reason.setText(cancel_trans_info.getReason());
				// 公司退买家
				tv_refund_buyer.setText(cancel_trans_info.getBuyer_prepay());
				// 公司退车主
				tv_refund_seller.setText(cancel_trans_info.getOwner_prepay());
				// 退款方式
				tv_refund_way_buyer.setText(cancel_trans_info.getComment());
				tv_refund_way_seller.setText(cancel_trans_info.getOwner_comment());
			}

			if (mTaskEntity.getFin_cancel_seller() != null || mTaskEntity.getFin_cancel_buyer() != null) {
				btn_commit.setVisibility(View.VISIBLE);
			} else {
				btn_commit.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 跳转web页看车详情
	 */
	@OnClick(R.id.rb_trans_vehicle)
	private void toVehicleDetail(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("url", "http://m.haoche51.com/details/" + mTaskEntity.getVehicle_source_id() + ".html");
			HCActionUtil.launchActivity(this, HCWebViewActivity.class, map);
		}
	}

	/**
	 * 跳转买家看车记录
	 */
	@OnClick(R.id.rb_buyer_trans_history)
	private void toTransRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("buyer_phone", mTaskEntity.getBuyer_phone());
			HCActionUtil.launchActivity(this, TransactionRecordListActivity.class, map);
		}
	}

	/**
	 * 跳转到买家回访记录
	 */
	@OnClick(R.id.rb_buyer_trans_revisit)
	private void toBuyerRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", mTaskEntity.getVehicle_source_id());
			map.put("buyer_phone", mTaskEntity.getBuyer_phone());
			HCActionUtil.launchActivity(this, BuyerRevisitListActivity.class, map);
		}
	}

	/**
	 * 跳转到车主回访记录
	 */
	@OnClick(R.id.rb_trans_vehicle_revisit)
	private void toSellerRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", mTaskEntity.getVehicle_source_id());
			map.put("onlyViewId", true);
			map.put("seller_phone", mTaskEntity.getSeller_phone());
			map.put("seller_name", mTaskEntity.getSeller_name());
			HCActionUtil.launchActivity(this, VehicleRevisitListActivity.class, map);
		}
	}


	/**
	 * 跳转到h5详情页
	 */
	public void toWebDetail(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("url", "http://m.haoche51.com/details/" + mTaskEntity.getVehicle_source_id() + ".html");
			HCActionUtil.launchActivity(this, HCWebViewActivity.class, map);
		}
	}

	/**
	 * 跳转到毁约转款
	 */
	@OnClick(R.id.btn_commit)
	public void toTransfer(View v) {
		if (mTaskEntity != null) {
			Intent intent = new Intent(GlobalData.mContext, RefundAcitivity.class);
			intent.putExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_BUYER, mTaskEntity.getFin_cancel_buyer());
			intent.putExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_SELLER, mTaskEntity.getFin_cancel_seller());
			intent.putExtra("transferId", mTaskEntity.getId());
			startActivity(intent);
		}
	}


	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
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
				break;
		}
	}
}
