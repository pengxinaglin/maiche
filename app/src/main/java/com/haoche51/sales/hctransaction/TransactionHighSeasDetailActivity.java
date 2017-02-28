package com.haoche51.sales.hctransaction;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.hccustomer.CustomerRevisitFragment;
import com.haoche51.sales.hccustomer.IntendedSourceFragment;
import com.haoche51.sales.hccustomer.TransRecordFragment;
import com.haoche51.sales.hccustomer.VehicleMatchFragment;
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
 * 看车失败列表 --> 失败客户详情页
 * Created by mac on 15/10/30.
 */
public class TransactionHighSeasDetailActivity extends CommonStateFragmentActivity {

	@ViewInject(R.id.tv_customer)
	private TextView tv_customer;//客户名字
	@ViewInject(R.id.tv_level)
	private TextView tv_level;//客户级别
	@ViewInject(R.id.tv_arrival)
	private TextView tv_arrival;//到店时间

	@ViewInject(R.id.content_view)
	public HCSmartTabLayout hcSmartTabLayout;
	private List<FragmentItem> mFragmentItemList;

	private String phone;
	private BuyerEntity mBuyerInfo;

	@Override
	protected int getTitleView() {
		return R.layout.layout_customer_detail_activity_titlebar;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_high_seas_detail;
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
		} else if (action.equals(HttpConstants.ACTION_FETCH_BUYER)) {
			doFetchBuyerResponse(response);
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
							AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerByPhone(phone), TransactionHighSeasDetailActivity.this, 0);
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
					AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerByPhone(phone), TransactionHighSeasDetailActivity.this, 0);
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
				mFragmentItemList.add(new FragmentItem(getString(R.string.hc_trans_history_activity_title), new TransRecordFragment(mBuyerInfo.getPhone())));
				mFragmentItemList.add(new FragmentItem(getString(R.string.intended_source), new IntendedSourceFragment(mBuyerInfo.getPhone())));
				//设置界面
				hcSmartTabLayout.setContentFragments(this, mFragmentItemList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 领取客户
	 *
	 * @param response
	 */
	private void doFetchBuyerResponse(HCHttpResponse response) {
		ProgressDialogUtil.closeProgressDialog();
		if (response.getErrno() == 0) {
			//领取成功
			HCDialog.showDialog(TransactionHighSeasDetailActivity.this, "提示", "领取成功", android.R.drawable.ic_dialog_alert, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//关闭页面，刷新列表
					setResult(RESULT_OK);
					finish();
				}
			});
		} else {
			//领取失败
			HCDialog.showDialog(TransactionHighSeasDetailActivity.this, "提示", response.getErrmsg(), android.R.drawable.ic_dialog_alert, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//关闭页面，刷新列表
					dialog.dismiss();
				}
			});
		}
	}

	@OnClick(R.id.tv_transaction_high_seas_detail_receive)
	private void onFetchBuyerClick(View view) {
		if (mBuyerInfo != null) {
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			AppHttpServer.getInstance().post(HCHttpRequestParam.fetchBuyer(mBuyerInfo.getName(), mBuyerInfo.getPhone()), this, 0);
		}
	}
}
