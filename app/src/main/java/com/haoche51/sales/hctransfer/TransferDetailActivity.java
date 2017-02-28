package com.haoche51.sales.hctransfer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.TagsLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCArithUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.lang.reflect.Type;

/**
 * 过户详情
 */

public class TransferDetailActivity extends CommonStateActivity {

	public final static String KEY_INTENT_EXTRA_ID = "transfer_id";
	private final static int REQUEST_CODE_TRANSFER = 0x101;
	private final static int REQUEST_CODE_MODIFY = 0x102;
	/**
	 * 订单状态
	 */
	@ViewInject(R.id.ll_customer_state)
	private LinearLayout ll_refuse_transfer;
	@ViewInject(R.id.tv_audit_status)
	private TextView tv_refuse_status;
	@ViewInject(R.id.tv_audit_des)
	private TextView tv_refuse_reason;

	/**
	 * 过户信息
	 */
	@ViewInject(R.id.tv_transfer_info)
	private TextView tv_transfer_info;

	/**
	 * 预定时间
	 */
	@ViewInject(R.id.tv_transfer_order_time)
	private TextView tv_transfer_order_time;

	/**
	 * 订单编号
	 */
	@ViewInject(R.id.tv_transfer_order_number)
	private TextView tv_transfer_order_number;

	/**
	 * 车源编号
	 */
	@ViewInject(R.id.tv_transfer_vc_id)
	private TextView tv_transfer_vc_id;

	/**
	 * 车牌号
	 */
	@ViewInject(R.id.tv_transfer_plate_number)
	private TextView tv_transfer_plate_number;

	/**
	 * 车型
	 */
	@ViewInject(R.id.tv_transfer_vehicle)
	private TextView tv_transfer_vehicle;

	/**
	 * 车源来源
	 */
	@ViewInject(R.id.tv_vehicle_type_text)
	private TextView tv_vehicle_type_text;

	/**
	 * 备注
	 */
	@ViewInject(R.id.tv_transfer_remark)
	private TextView tv_transfer_remark;

	/**
	 * 公司包过户
	 */
	@ViewInject(R.id.tv_transfer_mode)
	private TextView tv_transfer_mode;

	/**
	 * 买家给公司定金
	 */
	@ViewInject(R.id.tv_transaction_buyer_company)
	private TextView tv_transaction_buyer_company;

	/**
	 * 买家付公司车款
	 */
	@ViewInject(R.id.tv_transaction_buyer_owner)
	private TextView tv_transaction_buyer_owner;

	/**
	 * 车主给公司定金
	 */
	@ViewInject(R.id.tv_transaction_owner_company)
	private TextView tv_transaction_owner_company;

	/**
	 * 成交价
	 */
	@ViewInject(R.id.tv_final_price)
	private TextView tv_final_price;

	/**
	 * 成本价
	 */
	@ViewInject(R.id.rl_transfer_detail_deal_price)
	private RelativeLayout rl_transfer_detail_deal_price;
	@ViewInject(R.id.tv_transfer_detail_deal_price)
	private TextView tv_transfer_detail_deal_price;
	@ViewInject(R.id.tv_transfer_detail_deal_price_change_lable)
	private TextView tv_transfer_detail_deal_price_change_lable;

	/**
	 * 服务费
	 */
	@ViewInject(R.id.tv_transfer_detail_service_price)
	private TextView tv_transfer_detail_service_price;
	@ViewInject(R.id.rl_owner_commission)
	private RelativeLayout rl_owner_commission;
	@ViewInject(R.id.tv_owner_commission)
	private TextView tv_owner_commission;

	/**
	 * 车主
	 */
	@ViewInject(R.id.tv_seller_name)
	private TextView tv_seller_name;
	@ViewInject(R.id.tv_seller_phone)
	private TextView tv_seller_phone;

	/**
	 * 买家
	 */
	@ViewInject(R.id.tv_buyer_name)
	private TextView tv_buyer_name;
	@ViewInject(R.id.tv_buyer_phone)
	private TextView tv_buyer_phone;

	/**
	 * 评估师
	 */
	@ViewInject(R.id.tv_checker_name)
	private TextView tv_checker_name;
	@ViewInject(R.id.tv_checker_phone)
	private TextView tv_checker_phone;

	/**
	 * 地销
	 */
	@ViewInject(R.id.tv_local_saler_name)
	private TextView tv_local_saler_name;
	@ViewInject(R.id.tv_local_saler_phone)
	private TextView tv_local_saler_phone;

	/**
	 * 开始过户
	 */
	@ViewInject(R.id.ll_transfer_detail_start)
	private LinearLayout ll_transfer_detail_start;

	@ViewInject(R.id.ll_transfer_detail_modify)
	private LinearLayout ll_transfer_detail_modify;

	@ViewInject(R.id.tv_transfer_detail_modify)
	private TextView tv_transfer_detail_modify;

	@ViewInject(R.id.ll_transfer_fee)
	private LinearLayout ll_transfer_fee;//跳转过户费

	@ViewInject(R.id.ll_transfer_seller)
	private LinearLayout ll_transfer_seller;//跳转转车主

	@ViewInject(R.id.ll_transfer_detail_payment)
	private LinearLayout ll_transfer_detail_payment;

	@ViewInject(R.id.ll_vehicle_tag)
	private TagsLayout ll_vehicle_tag;//车源标签

	private TransferEntity entity;
	private String transferID;

	@Override
	protected int getContentView() {
		return R.layout.activity_transfer_detail;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.action_transfer);
		transferID = getIntent().getStringExtra(KEY_INTENT_EXTRA_ID);

		if (TextUtils.isEmpty(transferID)) {
			ToastUtil.showText(R.string.parameters_error);
			finish();
		}

		tv_refuse_status.setVisibility(View.VISIBLE);//头部拒绝过户状态

	}

	@Override
	protected void onStart() {
		super.onStart();

		ProgressDialogUtil.showProgressDialog(TransferDetailActivity.this, null);
		AppHttpServer.getInstance().post(HCHttpRequestParam.transferDetail(transferID), this, 0);
	}

	private void setViewData(TransferEntity entity) {
		if (entity == null) {
			return;
		}

		// 设置头部状态
		updateRefuseStatus(entity);
		//过户信息
		String transferInfo = new StringBuffer().append(UnixTimeUtil.formatTransferTime(entity.getTransfer_time())).append("  ").append(entity.getTransfer_place()).toString();
		tv_transfer_info.setText(transferInfo);
		//预定时间
		tv_transfer_order_time.setText(UnixTimeUtil.format(entity.getPrepay_time()));
		//订单编号
		tv_transfer_order_number.setText(entity.getTransaction_number());
		//车源编号
		tv_transfer_vc_id.setText(entity.getVehicle_source_id() + "");
		//车牌号码
		tv_transfer_plate_number.setText(entity.getPlate_number());
		//车型
		tv_transfer_vehicle.setText(entity.getVehicle_name());
		// 车源来源
		if (!TextUtils.isEmpty(entity.getVehicle_type_text())) {
			tv_vehicle_type_text.setText(entity.getVehicle_type_text());
		}
		//备注
		if (entity.getPrepay_comment() != null) {
			tv_transfer_remark.setText(entity.getPrepay_comment());
		}
		//是否公司包过户
		if (String.valueOf(1).equals(entity.getTransfer_free())) {
			tv_transfer_mode.setText("是");
		} else {
			tv_transfer_mode.setText("否");
		}

		//定金
		tv_transaction_buyer_company.setText(String.valueOf(entity.getPrepay()));
		//预定时担保车款
		int prepay_vehicle_price = StringUtil.parseInt(entity.getPrepay_vehicle_price(), 0);
		//过户时担保车款
		int transfer_vehicle_price = StringUtil.parseInt(entity.getTransfer_vehicle_price(), 0);
		tv_transaction_buyer_owner.setText(String.valueOf(prepay_vehicle_price + transfer_vehicle_price));
		tv_transaction_owner_company.setText(String.valueOf(entity.getSeller_company_prepay()));

		//成交价
		tv_final_price.setText(DisplayUtils.parseMoney("###.###", HCArithUtil.div(Double.valueOf(entity.getPrice()), 10000d)));
		//实体寄售车显示成本价
		if (entity.isConsignment()) {
			rl_transfer_detail_deal_price.setVisibility(View.VISIBLE);
			tv_transfer_detail_deal_price_change_lable.setVisibility(View.VISIBLE);
			tv_transfer_detail_deal_price.setText(String.valueOf(entity.getDeal_price()));
			rl_owner_commission.setVerticalGravity(View.VISIBLE);
			tv_owner_commission.setText(entity.getOwner_commission());
		}
		//服务费
		tv_transfer_detail_service_price.setText(DisplayUtils.parseMoney("###", Double.valueOf(entity.getCommission())));


		tv_seller_name.setText(entity.getSeller_name());
		DisplayUtils.getNoUnderlineSpan(tv_seller_phone, entity.getSeller_phone());

		tv_buyer_name.setText(entity.getBuyer_name());
		DisplayUtils.getNoUnderlineSpan(tv_buyer_phone, entity.getBuyer_phone());

		tv_checker_name.setText(entity.getChecker_name());
		DisplayUtils.getNoUnderlineSpan(tv_checker_phone, entity.getChecker_phone());

		tv_local_saler_name.setText(entity.getAccompany_name());//带看人
		DisplayUtils.getNoUnderlineSpan(tv_local_saler_phone, entity.getAccompany_phone());

		//车源更多标签
		if (entity.getVehicle_tag() != null && !entity.getVehicle_tag().isEmpty()) {
			ll_vehicle_tag.removeAllViews();
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(20, 0, 0, 0);
			for (int i = 0; i < entity.getVehicle_tag().size(); i++) {
				TextView textView = new TextView(this);
				textView.setTextAppearance(this, R.style.vehicle_lable);
				Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_vehicle_tag);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				textView.setCompoundDrawables(drawable, null, null, null);
				textView.setCompoundDrawablePadding(10);
				textView.setText(entity.getVehicle_tag().get(i));
				ll_vehicle_tag.addView(textView, lp);
			}
		}
	}


	/**
	 * 修改过户信息
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_transfer_detail_modify)
	private void onModifyClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, TransferModifyActivity.class);
		intent.putExtra(TransferModifyActivity.KEY_INTENT_EXTRA_ID, entity.getId());
		intent.putExtra(TransferModifyActivity.KEY_INTENT_EXTRA_TIME, entity.getTransfer_time());
		intent.putExtra(TransferModifyActivity.KEY_INTENT_EXTRA_ADDRESS, entity.getTransfer_place());
		startActivityForResult(intent, REQUEST_CODE_MODIFY);
	}

	/**
	 * 开始过户
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_transfer_detail_start)
	private void onStartClick(View view) {
		Intent intent = new Intent(GlobalData.mContext, TransferStartActivity.class);
		intent.putExtra("transferEntity", entity);
		startActivityForResult(intent, REQUEST_CODE_TRANSFER);
	}

	/**
	 * 过户费
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_transfer_fee)
	private void onTransferFinClick(View view) {
		Intent intent = new Intent(GlobalData.mContext, FinFeeInfoActivity.class);
		intent.putExtra("finEntity", entity.getFin_fee_info());
		intent.putExtra("transfer_amount", entity.getTransfer_amount());
		intent.putExtra("transferId", entity.getId());
		startActivity(intent);
	}

	private int getOwnerOutPayment() {
		//预定时担保车款
		int prepay_vehicle_price = StringUtil.parseInt(entity.getPrepay_vehicle_price(), 0);
		//过户时担保车款
		int transfer_vehicle_price = StringUtil.parseInt(entity.getTransfer_vehicle_price(), 0);
		//需转账金额
		return prepay_vehicle_price + transfer_vehicle_price;
	}

	private int getSellerCompanyPrepay() {
		//剩余服务费 = 剩余服务费 - 复检减免
		double rest_commission = Double.valueOf(entity.getRest_commission()) - Double.valueOf(entity.getTransfer_remission());
		//使用车主定金 = 使用车主定金 ？min(剩余服务费，车主定金）: 0；
		double seller_company_prepay = entity.getUse_owner() == 1 ? Math.min(rest_commission, Double.valueOf(entity.getSeller_company_prepay()))
				: 0;
		seller_company_prepay = Double.valueOf(entity.getSeller_company_prepay()) - seller_company_prepay;
		return (int) seller_company_prepay;
	}

	/**
	 * 转款
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_transfer_seller)
	private void onll_transfer_sellerClick(View view) {
		int owner_out_payment = getOwnerOutPayment();
		int seller_company_prepay = getSellerCompanyPrepay();
		int deal_price = entity.getDeal_price();
		//跳转到提交转账信息
		Intent intent = new Intent(this, RemittanceCommitActivity.class);
		//普通车源
		if (entity.getVehicle_type() == 0 || entity.getVehicle_type() == 2) {
			intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, owner_out_payment);
		}
		//实体寄售
		else if (entity.isConsignment()) {
			intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, deal_price);
		}
		intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY, seller_company_prepay);
		intent.putExtra("finEntity", entity.getFin_seller_transfer());
		intent.putExtra("transferId", entity.getId());
		startActivity(intent);
	}

	/**
	 * 收款历史
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_transfer_detail_payment)
	private void onPaymentClick(View view) {
		Intent intent = new Intent();
		intent.putExtra("transferEntity", entity);
		intent.setClass(this, TransferPaymentActivity.class);
		startActivity(intent);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK != resultCode) return;

		if (REQUEST_CODE_TRANSFER == requestCode) {
			// 隐藏修改时间按钮,开始过户按钮 //改变过户状态
			ll_transfer_detail_modify.setVisibility(View.GONE);
			ll_transfer_detail_start.setVisibility(View.GONE);
			finish();
		} else if (REQUEST_CODE_MODIFY == requestCode) {
			if (data != null) {
				int transferTime = data.getIntExtra("time", -1);
				String transferAdress = data.getStringExtra("address");
				entity.setTransfer_time(transferTime);
				entity.setTransfer_place(transferAdress);
				String transferInfo = new StringBuffer().append(UnixTimeUtil.formatTransferTime(transferTime))
						.append("  ").append(transferAdress).toString();
				tv_transfer_info.setText(transferInfo);
			}
		}
	}


	/**
	 * 设置头部拒绝过户状态,如果为拒绝过户， 禁用开始过户button
	 * 底部按钮控制  由于数据问题，暂时按钮全部显示
	 *
	 * @param entity
	 */
	private void updateRefuseStatus(TransferEntity entity) {
		//底部按钮
		switch (entity.getAudit_status()) {
			case TaskConstants.STATUS_WAIT_TRANSFER://待过户
			case TaskConstants.STATUS_TRANSFER_CONTRACT_FAIL://过户合同审核失败
				ll_transfer_detail_modify.setVisibility(View.VISIBLE);
				ll_transfer_detail_start.setVisibility(View.VISIBLE);
				ll_transfer_detail_payment.setVisibility(View.GONE);
				ll_transfer_fee.setVisibility(View.GONE);
				ll_transfer_seller.setVisibility(View.GONE);
				break;
			case TaskConstants.STATUS_MAN_AUDIT://拒绝过户
				ll_transfer_detail_modify.setVisibility(View.VISIBLE);
				ll_transfer_detail_modify.setBackground(getResources().getDrawable(R.drawable.color_red_turn_deep));
				tv_transfer_detail_modify.setTextColor(Color.WHITE);
				ll_transfer_detail_start.setVisibility(View.GONE);
				ll_transfer_detail_payment.setVisibility(View.GONE);
				ll_transfer_fee.setVisibility(View.GONE);
				ll_transfer_seller.setVisibility(View.GONE);
				break;
			case TaskConstants.STATUS_FINISH://交易完成
				ll_transfer_detail_modify.setVisibility(View.GONE);
				ll_transfer_detail_start.setVisibility(View.GONE);
				if (StringUtil.parseInt(entity.getTransfer_amount(), 0) > 0) {
					ll_transfer_fee.setVisibility(View.VISIBLE);
				} else {
					ll_transfer_fee.setVisibility(View.GONE);
				}
				//不是贷款车（贷款车不用添加转账信息）
				if (entity.getIs_loan() == 0) {
					int owner_out_payment = getOwnerOutPayment();
					int seller_company_prepay = getSellerCompanyPrepay();
					int deal_price = entity.getDeal_price();
					//普通车源 担保车款（需转账金额）>0/车主定金>0
					if ((entity.getVehicle_type() == 0 || entity.getVehicle_type() == 2) && (owner_out_payment > 0 || seller_company_prepay > 0)) {
						ll_transfer_seller.setVisibility(View.VISIBLE);
					}
					//实体寄售 成本价>0（需转账金额）/车主定金>0
					else if (entity.isConsignment() && (deal_price > 0 || seller_company_prepay > 0)) {
						ll_transfer_seller.setVisibility(View.VISIBLE);
					} else {
						ll_transfer_seller.setVisibility(View.GONE);
					}
				} else {
					ll_transfer_seller.setVisibility(View.GONE);
				}
				ll_transfer_detail_payment.setVisibility(View.VISIBLE);
				break;
			default:
				ll_transfer_detail_modify.setVisibility(View.GONE);
				ll_transfer_detail_start.setVisibility(View.GONE);
				ll_transfer_fee.setVisibility(View.GONE);
				ll_transfer_seller.setVisibility(View.GONE);
				ll_transfer_detail_payment.setVisibility(View.VISIBLE);
				break;
		}

		// 头部过户状态
		tv_refuse_status.setText(entity.getAudit_text());
		if (!TextUtils.isEmpty(entity.getAudit_desc())) {
			tv_refuse_reason.setVisibility(View.VISIBLE);
			tv_refuse_reason.setText(entity.getAudit_desc());
		} else {
			tv_refuse_reason.setVisibility(View.GONE);
		}
	}

	/**
	 * 附加信息
	 */
	@OnClick(R.id.rb_transfer_detail_extinfo)
	private void onExtInfoClick(View view) {
		if (entity == null || TextUtils.isEmpty(entity.getExt_info())) {
			ToastUtil.showInfo("附加信息为空");
			return;
		}
		Intent intent = new Intent(TransferDetailActivity.this, TransferExtInfoActivity.class);
		intent.putExtra(TransferExtInfoActivity.KEY_INTENT_EXTRA_EXTINFO, entity.getExt_info());
		startActivity(intent);
	}

	/**
	 * 4s店保养记录
	 *
	 * @param view
	 */
	@OnClick(R.id.rb_transfer_detail_4sinfo)
	private void on4sInfoClick(View view) {
		// 获取到了，查看，下载；没有报告，toast提示
		//0:订单未获取 1:订单获取中 2:订单获取成功 3:订单获取失败
		if (entity == null) {
			ToastUtil.showText(ControlDisplayUtil.getInstance().getCheJinDingStatus(TaskConstants.GET_FAIL));
			return;
		}
		if (!"2".equals(entity.getCjd_status())) {
			ToastUtil.showText(ControlDisplayUtil.getInstance().getCheJinDingStatus(Integer.valueOf(entity.getCjd_status())));
			return;
		}

		//先确认token 是否过期
		LoginToken token = GlobalData.userDataHelper.getLoginToken();
		boolean timeout = false;
		if (token != null) {
			int current_time = (int) System.currentTimeMillis() / 1000;
			timeout = (current_time - token.getTimeOut()) > token.getCreate_time() ? true : false;
		}
		if (token == null || TextUtils.isEmpty(token.getToken()) || timeout == true) {
			ProgressDialogUtil.showProgressDialog(this, null);
			AppHttpServer.getInstance().post(HCHttpRequestParam.getLoginToken("crm"), this, 0);
		} else {
			startChejianding(token.getToken());
		}
	}

	/**
	 * 跳转到车鉴定页面
	 *
	 * @param token
	 */

	private void startChejianding(String token) {
		if (entity != null && !TextUtils.isEmpty(entity.getCjd_url())) {
			StringBuffer sb = new StringBuffer();
			sb.append(entity.getCjd_url()).append("?").append("token=").append(token);
			Intent intent = new Intent(this, HCWebViewActivity.class);
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_URL, sb.toString());
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_ENABLE, true);
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_URL, entity.getCjd_report_pdf());
			intent.putExtra(HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CHECKER_REPORT_OR_CJD, HCWebViewActivity.KEY_INTENT_EXTRA_DOWNLOAD_CJD);
			startActivity(intent);
		} else {
			ToastUtil.showInfo("车鉴定数据为空");
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		super.onHttpComplete(action, requestId, response, error);
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_TRANSFERAPI_GET_DETAIL_BY_ID)) {
			onDetailResponse(response, requestId);
		} else if (action.equals(HttpConstants.ACTION_GET_LOGIN_TOKEKN)) {
			onLoginTokenReturn(response);
		}
	}

	private void onLoginTokenReturn(HCHttpResponse response) {
		dismissLoadingView();
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();
		switch (response.getErrno()) {
			case 0:
				Type type = new TypeToken<LoginToken>() {
				}.getType();
				LoginToken token = response.getData(type);
				if (token != null) {
					startChejianding(token.getToken());
				}
				break;
			default:
				Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	/**
	 * 详情接口数据处理
	 *
	 * @param response  response
	 * @param requestId requestId
	 */
	private void onDetailResponse(HCHttpResponse response, int requestId) {
		switch (response.getErrno()) {
			case 0:
				entity = HCJsonParse.parseTransferDetail(response.getData());
				if (entity != null) {
					dismissLoadingView();
					if (!isFinishing())
						ProgressDialogUtil.closeProgressDialog();
					setViewData(entity);
				} else {
					if (!isFinishing())
						ProgressDialogUtil.closeProgressDialog();
					setErrorView();
				}
				break;
			default:
				ToastUtil.showText(response.getErrmsg());
				setErrorView();
				break;
		}
	}

	private void setErrorView() {
		dismissLoadingView();
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();
		showErrorView(false, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissResultView(false);
				showLoadingView(false);
				ProgressDialogUtil.showProgressDialog(TransferDetailActivity.this, null);
				AppHttpServer.getInstance().post(HCHttpRequestParam.transferDetail(transferID), TransferDetailActivity.this, 0);
			}
		});
	}
}