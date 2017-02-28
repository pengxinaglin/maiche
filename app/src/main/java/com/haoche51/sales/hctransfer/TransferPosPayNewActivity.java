package com.haoche51.sales.hctransfer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.entity.CommonBooleanEntity;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransaction.RefundInfoEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.TransactionQiNiuUploadUtil;
import com.haoche51.settlement.cashiers.SettlementIntent;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;


public class TransferPosPayNewActivity extends CommonStateActivity {

	private final int REQUEST_CODE_POSPAY = 2;

	//买家
	@ViewInject(R.id.tv_pospay_title)
	private TextView tv_pospay_title;
	//需支付定金[元]
	@ViewInject(R.id.tv_pospay_lable)
	private TextView tv_pospay_lable;
	//定金金额
	@ViewInject(R.id.tv_pospay_price)
	private TextView tv_pospay_price;
	//支付按钮
	@ViewInject(R.id.ll_pospay_pay_btu)
	private LinearLayout ll_pospay_pay_btu;

	/**
	 * 去提交审核
	 */
	@ViewInject(R.id.tv_pospay_tocommit)
	private TextView tv_pospay_tocommit;

	private String receiveable = "0";
	private TransferEntity entity;
	ArrayList<String> transferPhotoPathOrigin = new ArrayList<>();   //全部照片的list
	ArrayList<String> transferPhotoSelectedPath = new ArrayList<>(); //选择照片的list

	public final static String KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT = "owner_out_payment";//公司需转车主金额
	public final static String KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY = "seller_company_prepay";//车主定金
	public final static String KEY_INTENT_EXTRA_SELLER_BANK = "seller_bank_info";//车主银行信息
	private final static int REQUEST_CODE_BANKINFO = 10;

	boolean inputRefundInfo = true;

	@Override
	protected int getContentView() {
		return R.layout.transfer_pos_pay_new_activity;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle("支付尾款");
		entity = (TransferEntity) getIntent().getSerializableExtra("transfer");
		receiveable = getIntent().getStringExtra("receiveable");
		transferPhotoPathOrigin = getIntent().getStringArrayListExtra("originPhoto");
		transferPhotoSelectedPath = getIntent().getStringArrayListExtra("selectPhoto");

	}

	@Override
	protected void initData() {
		super.initData();
		if (entity == null) {
			ToastUtil.showText("参数错误");
			return;
		}

		tv_pospay_price.setText(receiveable);
		if ("0".equals(receiveable)) {
			tv_pospay_tocommit.setVisibility(View.VISIBLE);
			buyerPaySuccess(true);
		}
	}


	/**
	 * 去支付
	 *
	 * @param view view
	 */
	@OnClick(R.id.ll_pospay_pay_btu)
	private void onPayClick(View view) {

		SettlementIntent intent = new SettlementIntent(this);
		intent.setFromBusiness(true);
		intent.setAppToken(GlobalData.userDataHelper.getLastAppToken());
		intent.setCrmUserId("" + GlobalData.userDataHelper.getUser().getId());
		intent.setCrmUserName(GlobalData.userDataHelper.getUser().getName());
		intent.setTaskId(entity.getTransaction_number());
		intent.setTaskType(1);
		intent.setPrice(receiveable);
		intent.setCustomerPhone(entity.getBuyer_phone());
		startActivityForResult(intent, REQUEST_CODE_POSPAY);

	}

	@OnClick(R.id.tv_pospay_tocommit)
	private void commit(View view) {
		//去填写转账信息
		if (inputRefundInfo) {

			//剩余服务费 = 剩余服务费 - 复检减免
			double rest_commission = Double.valueOf(entity.getRest_commission()) - Double.valueOf(entity.getTransfer_remission());
			//使用车主定金 = 使用车主定金 ？min(剩余服务费，车主定金）: 0；
			double seller_company_prepay = entity.getUse_owner() == 1 ? Math.min(rest_commission, Double.valueOf(entity.getSeller_company_prepay()))
					: 0;
			seller_company_prepay = Double.valueOf(entity.getSeller_company_prepay()) - seller_company_prepay;

			//预定时担保车款
			int prepay_vehicle_price = StringUtil.parseInt(entity.getPrepay_vehicle_price(), 0);
			//过户时担保车款
			int transfer_vehicle_price = StringUtil.parseInt(entity.getTransfer_vehicle_price(), 0);
			//需转账金额
			int owner_out_payment = prepay_vehicle_price + transfer_vehicle_price;

			//成本价
			int deal_price = entity.getDeal_price();

			//不是贷款车（贷款车不用添加转账信息）
			if (entity.getIs_loan() == 0) {
				//普通车源 担保车款（需转账金额）>0/车主定金>0
				if ((entity.getVehicle_type() == 0 || entity.getVehicle_type() == 2) && (owner_out_payment > 0 || seller_company_prepay > 0)) {
					//跳转到提交转账信息
					Intent intent = new Intent(this, RemittanceCommitActivity.class);
					intent.putExtra(KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, owner_out_payment);
					intent.putExtra(KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY, (int) seller_company_prepay);
					RefundInfoEntity infoEntity = new RefundInfoEntity();
					infoEntity.setName(entity.getSeller_name());
					infoEntity.setBank(entity.getSeller_bank());
					infoEntity.setCard(entity.getSeller_card());
					intent.putExtra(KEY_INTENT_EXTRA_SELLER_BANK, infoEntity);
					startActivityForResult(intent, REQUEST_CODE_BANKINFO);
					return;
				}
				//实体寄售 成本价>0（需转账金额）/车主定金>0
				else if (entity.isConsignment() && (deal_price > 0 || seller_company_prepay > 0)) {
					//跳转到提交转账信息
					Intent intent = new Intent(this, RemittanceCommitActivity.class);
					intent.putExtra(KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, deal_price);
					intent.putExtra(KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY, (int) seller_company_prepay);
					RefundInfoEntity infoEntity = new RefundInfoEntity();
					infoEntity.setName(entity.getSeller_name());
					infoEntity.setBank(entity.getSeller_bank());
					infoEntity.setCard(entity.getSeller_card());
					intent.putExtra(KEY_INTENT_EXTRA_SELLER_BANK, infoEntity);
					startActivityForResult(intent, REQUEST_CODE_BANKINFO);
					return;
				}
			}
		}

		//上传照片或直接提交
		if (transferPhotoSelectedPath != null && transferPhotoSelectedPath.size() > 0) {//有选择新的照片
			TransactionQiNiuUploadUtil qiniuUploadImageUtil = new TransactionQiNiuUploadUtil(this, this.transferPhotoSelectedPath);
			qiniuUploadImageUtil.startUpload(new TransactionQiNiuUploadUtil.QiniuUploadListener() {
				@Override
				public void onSuccess(List<String> keys) {
					//上传完成，调用取消任务
					if (transferPhotoPathOrigin != null && transferPhotoPathOrigin.size() > 0) {//还存在原有照片
						for (String key : transferPhotoPathOrigin) {
							keys.add(key);
						}
					}

					requestCommitTransferData(keys);
				}

				@Override
				public void onFailed() {
				}
			});
		} else {
			requestCommitTransferData(transferPhotoPathOrigin);
		}
	}

	/**
	 * 向服务器提交过户数据
	 */
	private void requestCommitTransferData(List<String> keys) {
		ProgressDialogUtil.showProgressDialog(TransferPosPayNewActivity.this, null);
		AppHttpServer.getInstance().post(HCHttpRequestParam.commitTransferPicAndComment(
				entity.getId(), entity.getTransfer_price(), entity.getTransfer_remission(), entity.getTransfer_amount()
				, entity.getUse_owner(), entity.getTransfer_vehicle_price(), entity.getTransfer_fee(), entity.getInsurance()
				, entity.getComment(), keys, receiveable, entity.getIs_loan(), entity.getSeller_name(), entity.getSeller_bank(), entity.getSeller_card(), entity.getOther_cost()), this, 0);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) return;
		if (requestCode == REQUEST_CODE_POSPAY) {
			tv_pospay_tocommit.setVisibility(View.VISIBLE);
			buyerPaySuccess(false);
		} else if (requestCode == REQUEST_CODE_BANKINFO) {
			if (data == null || data.getExtras().isEmpty()) {
				ToastUtil.showInfo("传递参数错误");
				return;
			}
			//设置车主姓名、开户银行、卡号
			RefundInfoEntity infoEntity = data.getParcelableExtra(KEY_INTENT_EXTRA_SELLER_BANK);
			this.entity.setSeller_name(infoEntity.getName());
			this.entity.setSeller_bank(infoEntity.getBank());
			this.entity.setSeller_card(infoEntity.getCard());

			inputRefundInfo = false;

			//提交server
			commit(null);
		}
	}

	/**
	 * 买家支付定金支付成功
	 */
	private void buyerPaySuccess(boolean none) {
		tv_pospay_title.setText("");
		Drawable drawable = getResources().getDrawable(R.drawable.ic_hc_pospay_success);
		//这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv_pospay_title.setCompoundDrawables(drawable, null, null, null);

		tv_pospay_lable.setTextAppearance(this, R.style.HCTheme_Pospay_SuccessLable);
		if (none) {
			tv_pospay_lable.setText("买家无需支付");
		} else {
			tv_pospay_lable.setText("支付成功");
		}
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(15, 0, 0, 0);//4个参数按顺序分别是左上右下
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		tv_pospay_lable.setLayoutParams(layoutParams);


		ll_pospay_pay_btu.setVisibility(View.INVISIBLE);
	}


	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		super.onHttpComplete(action, requestId, response, error);
		if (isFinishing()) {
			return;
		}
		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_UPLOAD_TRANSFER_INFO)) {
			if (!TextUtils.isEmpty(response.getResult())) {
				CommonBooleanEntity entity = HCJsonParse.parseTransferModifyData(response.getResult());
				if (entity != null) {
					int res = entity.getErrno() == 0 && entity.isData() ? R.string.hc_succ : R.string.hc_error;
					ToastUtil.showText(res);
					if (entity.isData()) {
						// 如果处理成功,关闭当前Activity,发送广播更新过户列表中该条数据状态
						doTransferSuccess();
					}
				}
			}
		}

		if (response.getErrno() != 0)
			ToastUtil.showInfo(response.getErrmsg());

	}

	/**
	 * 数据提交成功后的处理
	 */
	private void doTransferSuccess() {
		// //  2016/1/26 广播列表更新过户列表
		Intent broadcast = new Intent(HCConst.ACTION_TRANSFER_CHANGED);
		sendBroadcast(broadcast);

		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
