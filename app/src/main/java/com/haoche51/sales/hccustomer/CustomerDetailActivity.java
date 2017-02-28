package com.haoche51.sales.hccustomer;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.hctransaction.BuyerEntity;
import com.haoche51.sales.hctransaction.BuyerReviveActivity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * 客户详情页
 * Created by PengXianglin on 16/11/17.
 */
public class CustomerDetailActivity extends CommonStateFragmentActivity {

	@ViewInject(R.id.tv_customer)
	private TextView tv_customer;//客户名字
	@ViewInject(R.id.tv_level)
	private TextView tv_level;//客户级别
	@ViewInject(R.id.tv_arrival)
	private TextView tv_arrival;//到店时间

	@ViewInject(R.id.content_view)
	public HCSmartTabLayout hcSmartTabLayout;
	private List<FragmentItem> mFragmentItemList;
	private VehicleMatchFragment vehicleMatchFragment;

	private String phone;
	private BuyerEntity mBuyerInfo;

	@Override
	protected int getTitleView() {
		return R.layout.layout_customer_detail_activity_titlebar;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_customer_detail;
	}

	@Override
	protected void initData() {
		phone = getIntent().getStringExtra("phone");

		mFragmentItemList = new ArrayList<>();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//请求详情信息
		if (!TextUtils.isEmpty(phone)) {
			requestInfo();
		} else {
			ToastUtil.showInfo(getString(R.string.lack_phone));
			finish();
			return;
		}
	}

	private void requestInfo() {
		ProgressDialogUtil.showProgressDialog(this);
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerByPhone(phone), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_GET_BUYER_BY_PHONE)) {//详情
			responseBuyerInfo(response);
		}
	}

	private void responseBuyerInfo(HCHttpResponse response) {
		try {
			switch (response.getErrno()) {
				case 0:
					Gson gson = new Gson();
					mBuyerInfo = gson.fromJson(response.getData(), BuyerEntity.class);
					dismissLoadingView();
					if (!isFinishing()) {
						ProgressDialogUtil.closeProgressDialog();
					}
					setData(mBuyerInfo);
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					showErrorView(false, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dismissResultView(true);
							showLoadingView(false);
							AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerByPhone(phone), CustomerDetailActivity.this, 0);
						}
					});
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			dismissLoadingView();
			if (!isFinishing())
				ProgressDialogUtil.closeProgressDialog();
			showErrorView(false, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissResultView(true);
					showLoadingView(false);
					AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerByPhone(phone), CustomerDetailActivity.this, 0);
				}
			});
		}
	}

	/**
	 * 设置界面数据
	 */
	public void setData(BuyerEntity data) {
		try {
			//姓名
			tv_customer.setText(data.getName());
			//级别
			if (!TextUtils.isEmpty(DisplayUtils.getCustomerLevel(data.getLevel()))) {
				tv_level.setText(DisplayUtils.getCustomerLevel(data.getLevel()));
			}
			//到店时间
			tv_arrival.setText(data.getLast_arrive_time() > 0 ? UnixTimeUtil.format(data.getLast_arrive_time()) : "-----------");

			if (mFragmentItemList.isEmpty()) {
				mFragmentItemList.add(new FragmentItem(getString(R.string.buyer_revisit),
						new CustomerRevisitFragment(mBuyerInfo.getPhone(), mBuyerInfo.getLevel(), "true")));
				vehicleMatchFragment = new VehicleMatchFragment(mBuyerInfo.getPhone(), mBuyerInfo.getName(),
						mBuyerInfo.getSubscribe_rule() == null ? null : mBuyerInfo.getSubscribe_rule().getSubscribe_series(), mBuyerInfo.getSubscribe_rule() == null ? null : mBuyerInfo.getSubscribe_rule().getPrice());
				mFragmentItemList.add(new FragmentItem(getString(R.string.hc_right_vehicle_recommend), vehicleMatchFragment));
				mFragmentItemList.add(new FragmentItem(getString(R.string.hc_trans_history_activity_title), new TransRecordFragment(mBuyerInfo.getPhone())));
				mFragmentItemList.add(new FragmentItem(getString(R.string.intended_source), new IntendedSourceFragment(mBuyerInfo.getPhone())));
				//设置界面
				hcSmartTabLayout.setContentFragments(this, mFragmentItemList);
			} else {
				if (mBuyerInfo.getSubscribe_rule() != null)
					vehicleMatchFragment.setSubscribeSeries(mBuyerInfo.getSubscribe_rule().getSubscribe_series(), mBuyerInfo.getSubscribe_rule().getPrice());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拨号
	 */
	@OnClick(R.id.iv_call)
	private void onClickPhone(View v) {
		if (mBuyerInfo != null && !TextUtils.isEmpty(mBuyerInfo.getPhone())) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBuyerInfo.getPhone()));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			ToastUtil.showInfo("数据异常，请刷新页面重试");
		}
	}

	/**
	 * 修改信息
	 */
	@OnClick(R.id.iv_editeinfo)
	private void onClickEditeInfo(View v) {
		if (mBuyerInfo != null) {
			Intent intent = new Intent(this, EditeCustomerInfoActivity.class);
			intent.putExtra(EditeCustomerInfoActivity.INTENT_EXTRA_PHONE, mBuyerInfo.getPhone());
			intent.putExtra(EditeCustomerInfoActivity.INTENT_EXTRA_NAME, mBuyerInfo.getName());
			intent.putExtra(EditeCustomerInfoActivity.INTENT_EXTRA_WX, mBuyerInfo.getWeixin_account());
			intent.putExtra(EditeCustomerInfoActivity.INTENT_EXTRA_DEALER, mBuyerInfo.getCar_dealer());
			this.startActivity(intent);
		} else {
			ToastUtil.showInfo("数据异常，请刷新页面重试");
		}
	}

	/**
	 * 复活
	 */
	@OnClick(R.id.iv_resurrection)
	private void onClickResurrection(View v) {
		if (mBuyerInfo != null && !TextUtils.isEmpty(mBuyerInfo.getPhone())) {
			Intent intent = new Intent(this, BuyerReviveActivity.class);
			intent.putExtra("buyer_phone", mBuyerInfo.getPhone());
			this.startActivity(intent);
		} else {
			ToastUtil.showInfo("数据异常，请刷新页面重试");
		}
	}
}