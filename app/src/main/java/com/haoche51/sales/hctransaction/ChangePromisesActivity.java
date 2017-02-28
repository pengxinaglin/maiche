package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.switchbutton.SwitchButton;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.HCArithUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * 已支付定金 毁约页面
 * Created by mac on 15/9/27.
 */
public class ChangePromisesActivity extends CommonBaseActivity {

	@ViewInject(R.id.tv_trans_buyer_prepay)
	private TextView tv_trans_buyer_prepay;

	@ViewInject(R.id.tv_seller_company_prepay)
	private TextView tv_seller_company_prepay;

	@ViewInject(R.id.et_trans_company_refund_buyer)
	@Required(order = 1, message = "公司退买家的金额不能为空")
	private EditText et_trans_company_refund_buyer;

	@ViewInject(R.id.et_trans_company_refund_seller)
	@Required(order = 2, message = "公司退车主的金额不能为空")
	private EditText et_trans_company_refund_seller;

	@ViewInject(R.id.swb_refund_buyer_to_balance)
	private SwitchButton swb_refund_buyer_to_balance;

	@ViewInject(R.id.swb_refund_seller_to_balance)
	private SwitchButton swb_refund_seller_to_balance;

	@ViewInject(R.id.spn_cancel_convention)
	private Spinner spn_cancel_convention;

	@ViewInject(R.id.spn_vehicle_source_online)
	private Spinner spn_vehicle_source_online;

	@ViewInject(R.id.et_quit_buy_reason_content)
	@Required(order = 3, message = "请输入毁约原因")
	private EditText et_quit_buy_reason_content;

	TransactionTaskEntity mTaskEntity;
	RefundInfoEntity buyerRefundInfo, sellerRefundInfo;//买家、车主退款信息

	private final int REQUEST_CODE_TRANSFER = 20;
	public static final String KEY_INTENT_EXTRA_SELLER = "seller_bank_info";
	public static final String KEY_INTENT_EXTRA_BUYER = "buyer_bank_info";

	@Override
	protected void initView() {
		setContentView(R.layout.change_promises_activity);
		ViewUtils.inject(this);
		registerTitleBack();
		setScreenTitle(R.string.breach_of_promise);
	}

	@Override
	protected void initData() {
		mTaskEntity = (TransactionTaskEntity) getIntent().getSerializableExtra("mTaskEntity");
		if (mTaskEntity == null) {
			finish();
			return;
		}

		// 买家已支付金额
		tv_trans_buyer_prepay.setText(mTaskEntity.getBuyer_payment());
		// 车主已支付金额
		tv_seller_company_prepay.setText(mTaskEntity.getSeller_company_prepay());
	}

	/**
	 * 确定更改信息
	 */
	@OnClick(R.id.btn_positive)
	private void saveInfo(View v) {
		if (mTaskEntity == null)
			return;
		validator.validate();
	}

	/**
	 * 取消
	 */
	@OnClick(R.id.btn_negative)
	private void cancel(View v) {
		finish();
	}

	@Override
	public void onValidationSucceeded() {
		super.onValidationSucceeded();

		if (spn_cancel_convention.getSelectedItemPosition() == 0) {
			showErrorMsg(spn_cancel_convention, getString(R.string.select_cancel_party));
			return;
		}

		if (spn_vehicle_source_online.getSelectedItemPosition() == 0) {
			showErrorMsg(spn_vehicle_source_online, getString(R.string.select_vehicle_status));
			return;
		}

		if (TextUtils.isEmpty(et_trans_company_refund_buyer.getText().toString())) {
			ToastUtil.showInfo("请填写退买家金额");
			return;
		}

		if (TextUtils.isEmpty(et_trans_company_refund_seller.getText().toString())) {
			ToastUtil.showInfo("请填写退车主金额");
			return;
		}

		// 校验退款金额总和要小于等于已付金额总和
		int buyerPayed = StringUtil.parseInt(mTaskEntity.getBuyer_payment(), 0);
		int sellerPayed = StringUtil.parseInt(mTaskEntity.getSeller_company_prepay(), 0);
		int refundBuyer = StringUtil.parseInt(et_trans_company_refund_buyer.getText().toString(), 0);
		int refundSeller = StringUtil.parseInt(et_trans_company_refund_seller.getText().toString(), 0);

		if (HCArithUtil.add(refundBuyer, refundSeller) > HCArithUtil.add(buyerPayed, sellerPayed)) {
			ToastUtil.showInfo("退款金额总和能不能大于已付金额总和");
			return;
		}

		//公司退买家金额
		int buyerPrepay = Integer.parseInt(this.et_trans_company_refund_buyer.getText().toString());
		//公司退车主金额
		int ownerPrepay = Integer.parseInt(this.et_trans_company_refund_seller.getText().toString());
		//毁约方
		String type = spn_cancel_convention.getSelectedItemPosition() == 1 ? getString(R.string.one) : getString(R.string.zero);
		//车源上下线
		int vehicleStatus = spn_vehicle_source_online.getSelectedItemPosition() == 1 ? TaskConstants.VEHICLE_STATUS_ONLINE : TaskConstants.VEHICLE_STATUS_OFFLINE;
		//原因
		String reason = this.et_quit_buy_reason_content.getText().toString();

		//退买家金额>0且不退到余额/退车主金额>0且不退到余额
		if ((buyerPrepay > 0 && !swb_refund_buyer_to_balance.isChecked()) || (ownerPrepay > 0 && !swb_refund_seller_to_balance.isChecked())) {
			//跳转到毁约转账信息页面
			Intent intent = new Intent(this, RefundAcitivity.class);

			if (buyerRefundInfo == null){
				buyerRefundInfo = new RefundInfoEntity();
			}
			if (sellerRefundInfo == null){
				sellerRefundInfo = new RefundInfoEntity();
			}

			buyerRefundInfo.setPayMoney(buyerPrepay);
			buyerRefundInfo.setToBalance(swb_refund_buyer_to_balance.isChecked());
			sellerRefundInfo.setPayMoney(ownerPrepay);
			sellerRefundInfo.setToBalance(swb_refund_seller_to_balance.isChecked());

			intent.putExtra(KEY_INTENT_EXTRA_BUYER, buyerRefundInfo);
			intent.putExtra(KEY_INTENT_EXTRA_SELLER, sellerRefundInfo);
			startActivityForResult(intent, REQUEST_CODE_TRANSFER);
			return;
		}

		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.cancelTransAfterPrepay(Integer.parseInt(mTaskEntity.getId()), type,
				ownerPrepay, buyerPrepay, vehicleStatus, reason, swb_refund_seller_to_balance.isChecked(), swb_refund_buyer_to_balance.isChecked(), null, null), this, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) return;
		if (data == null || data.getExtras().isEmpty()) return;

		buyerRefundInfo = data.getParcelableExtra(KEY_INTENT_EXTRA_BUYER);
		sellerRefundInfo = data.getParcelableExtra(KEY_INTENT_EXTRA_SELLER);

		//公司退买家金额
		int buyerPrepay = Integer.parseInt(this.et_trans_company_refund_buyer.getText().toString());
		//公司退车主金额
		int ownerPrepay = Integer.parseInt(this.et_trans_company_refund_seller.getText().toString());
		//毁约方
		String type = spn_cancel_convention.getSelectedItemPosition() == 1 ? getString(R.string.one) : getString(R.string.zero);
		//车源上下线
		int vehicleStatus = spn_vehicle_source_online.getSelectedItemPosition() == 1 ? TaskConstants.VEHICLE_STATUS_ONLINE : TaskConstants.VEHICLE_STATUS_OFFLINE;
		//原因
		String reason = this.et_quit_buy_reason_content.getText().toString();

		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.cancelTransAfterPrepay(Integer.parseInt(mTaskEntity.getId()), type,
				ownerPrepay, buyerPrepay, vehicleStatus, reason, swb_refund_seller_to_balance.isChecked(), swb_refund_buyer_to_balance.isChecked(), sellerRefundInfo, buyerRefundInfo), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		ProgressDialogUtil.closeProgressDialog();
		switch (response.getErrno()) {
			case 0:
				ToastUtil.showInfo(getString(R.string.successful));
				HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
				setResult(RESULT_OK);
				finish();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}
}
