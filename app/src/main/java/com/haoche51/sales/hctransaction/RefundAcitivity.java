package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import okhttp3.Call;

/**
 * 毁约退款转账信息页面
 * Created by PengXianglin on 16/6/29.
 */
public class RefundAcitivity extends CommonStateActivity {

	@ViewInject(R.id.tv_buyer_reason)
	private TextView tv_buyer_reason;
	@ViewInject(R.id.tv_buyer_prepay)
	private TextView tv_buyer_prepay;//买家转账金额
	@ViewInject(R.id.et_refund_buyer_name)
	private TextView et_refund_buyer_name;//买家开户姓名
	@ViewInject(R.id.et_refund_buyer_bank)
	private TextView et_refund_buyer_bank;//买家开户银行
	@ViewInject(R.id.et_refund_buyer_card_number)
	private TextView et_refund_buyer_card_number;//买家开户卡号
	@ViewInject(R.id.ll_buyer)
	LinearLayout ll_buyer;//买家信息

	@ViewInject(R.id.tv_seller_reason)
	private TextView tv_seller_reason;
	@ViewInject(R.id.tv_seller_prepay)
	private TextView tv_seller_prepay;//车主转账金额
	@ViewInject(R.id.et_refund_seller_name)
	private TextView et_refund_seller_name;//车主开户姓名
	@ViewInject(R.id.et_refund_seller_bank)
	private TextView et_refund_seller_bank;//车主开户银行
	@ViewInject(R.id.et_refund_seller_card_number)
	private TextView et_refund_seller_card_number;//车主开户卡号
	@ViewInject(R.id.ll_seller)
	LinearLayout ll_seller;//买家信息

	@ViewInject(R.id.btn_commit)
	private Button btn_commit;

	RefundInfoEntity buyerRefundInfo, sellerRefundInfo;//买家、车主退款信息
	TransactionTaskEntity.FinFeeInfoEntity finBuyerInfo, finSellerInfo;//买家、车主退款信息
	String transferId;

	@Override
	protected int getContentView() {
		return R.layout.acitivity_change_promises_refund;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle("提交转账");
	}

	@Override
	protected void initData() {
		//***********************毁约退款**************************
		transferId = getIntent().getStringExtra("transferId");
		//退买家
		finBuyerInfo = (TransactionTaskEntity.FinFeeInfoEntity) getIntent().getSerializableExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_BUYER);
		//退车主
		finSellerInfo = (TransactionTaskEntity.FinFeeInfoEntity) getIntent().getSerializableExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_SELLER);
		if (finBuyerInfo != null || finSellerInfo != null) {
			//退买家
			if (finBuyerInfo != null) {
				//失败原因
				if (!TextUtils.isEmpty(finBuyerInfo.getStatus_text())) {
					tv_buyer_reason.setVisibility(View.VISIBLE);
					if (!TextUtils.isEmpty(finBuyerInfo.getReason()))
						tv_buyer_reason.setText("（" + finBuyerInfo.getStatus_text() + "，" + finBuyerInfo.getReason() + "）");
					else
						tv_buyer_reason.setText("（" + finBuyerInfo.getStatus_text() + "）");
				} else {
					tv_buyer_reason.setVisibility(View.GONE);
				}
				setTextColor(tv_buyer_reason, finBuyerInfo.getTrade_status());
				tv_buyer_prepay.setText(finBuyerInfo.getTrade_amount());
				et_refund_buyer_name.setText(finBuyerInfo.getCard_name());
				et_refund_buyer_bank.setText(finBuyerInfo.getCard_bank());
				et_refund_buyer_card_number.setText(finBuyerInfo.getCard_number());
				//10未确认  20通过
				if (finBuyerInfo.getTrade_status() == 10 || finBuyerInfo.getTrade_status() == 20) {
					et_refund_buyer_name.setEnabled(false);
					et_refund_buyer_bank.setEnabled(false);
					et_refund_buyer_card_number.setEnabled(false);
				}
			} else {
				ll_buyer.setVisibility(View.GONE);
			}
			//退车主
			if (finSellerInfo != null) {
				//失败原因
				if (!TextUtils.isEmpty(finSellerInfo.getStatus_text())) {
					tv_seller_reason.setVisibility(View.VISIBLE);
					if (!TextUtils.isEmpty(finSellerInfo.getReason()))
						tv_seller_reason.setText("（" + finSellerInfo.getStatus_text() + "，" + finSellerInfo.getReason() + "）");
					else
						tv_seller_reason.setText("（" + finSellerInfo.getStatus_text() + "）");
				} else {
					tv_seller_reason.setVisibility(View.GONE);
				}
				setTextColor(tv_seller_reason, finSellerInfo.getTrade_status());
				tv_seller_prepay.setText(finSellerInfo.getTrade_amount());
				et_refund_seller_name.setText(finSellerInfo.getCard_name());
				et_refund_seller_bank.setText(finSellerInfo.getCard_bank());
				et_refund_seller_card_number.setText(finSellerInfo.getCard_number());
				//10未确认  20通过
				if (finSellerInfo.getTrade_status() == 10 || finSellerInfo.getTrade_status() == 20) {
					et_refund_seller_name.setEnabled(false);
					et_refund_seller_bank.setEnabled(false);
					et_refund_seller_card_number.setEnabled(false);
				}
			} else {
				ll_seller.setVisibility(View.GONE);
			}

			//0未提交  10未确认  20通过  40拒绝
			if (finSellerInfo != null && (finSellerInfo.getTrade_status() == 0 || finSellerInfo.getTrade_status() == 40)) {
				btn_commit.setVisibility(View.VISIBLE);
			} else if (finBuyerInfo != null && (finBuyerInfo.getTrade_status() == 0 || finBuyerInfo.getTrade_status() == 40)) {
				btn_commit.setVisibility(View.VISIBLE);
			} else {
				btn_commit.setVisibility(View.GONE);
			}
			return;
		}

		//***********************任务失败**************************
		//退买家
		buyerRefundInfo = getIntent().getParcelableExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_BUYER);
		if (buyerRefundInfo != null) {
			tv_buyer_prepay.setText(String.valueOf(buyerRefundInfo.getPayMoney()));
			et_refund_buyer_name.setText(buyerRefundInfo.getName());
			et_refund_buyer_bank.setText(buyerRefundInfo.getBank());
			et_refund_buyer_card_number.setText(buyerRefundInfo.getCard());
			//金额为零不显示该信息
			if (buyerRefundInfo.getPayMoney() == 0 || buyerRefundInfo.isToBalance()) {
				ll_buyer.setVisibility(View.GONE);
			}
		}

		//退车主
		sellerRefundInfo = getIntent().getParcelableExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_SELLER);
		if (sellerRefundInfo != null) {
			tv_seller_prepay.setText(String.valueOf(sellerRefundInfo.getPayMoney()));
			et_refund_seller_name.setText(sellerRefundInfo.getName());
			et_refund_seller_bank.setText(sellerRefundInfo.getBank());
			et_refund_seller_card_number.setText(sellerRefundInfo.getCard());
			//金额为零不显示该信息
			if (sellerRefundInfo.getPayMoney() == 0 || sellerRefundInfo.isToBalance()) {
				ll_seller.setVisibility(View.GONE);
			}
		}
	}


	private void setTextColor(TextView textView, int status) {
		//0未提交  10未确认  20通过  40拒绝
		switch (status) {
			case 0:
				textView.setTextColor(getResources().getColor(R.color.pay_black));
				break;
			case 10:
			case 40:
				textView.setTextColor(getResources().getColor(R.color.hc_self_red));
				break;
			case 20:
				textView.setTextColor(getResources().getColor(R.color.GREEN));
				break;
		}
	}

	Call call;

	@OnClick(R.id.btn_commit)
	private void commit(View v) {
		if (validation()) {

			if (finBuyerInfo != null || finSellerInfo != null) {
				RefundInfoEntity buyer = null;
				if (finBuyerInfo != null && finBuyerInfo.getTrade_status() == 40) {
					buyer = new RefundInfoEntity();
					buyer.setName(et_refund_buyer_name.getText().toString());
					buyer.setBank(et_refund_buyer_bank.getText().toString());
					buyer.setCard(et_refund_buyer_card_number.getText().toString());
				}

				RefundInfoEntity seller = null;
				if (finSellerInfo != null && finSellerInfo.getTrade_status() == 40) {
					seller = new RefundInfoEntity();
					seller.setName(et_refund_seller_name.getText().toString());
					seller.setBank(et_refund_seller_bank.getText().toString());
					seller.setCard(et_refund_seller_card_number.getText().toString());
				}

				call = AppHttpServer.getInstance().post(HCHttpRequestParam.cancelTransferApply(transferId, seller, buyer), this, 0);
				return;
			}

			Intent intent = new Intent();

			if (buyerRefundInfo != null && buyerRefundInfo.getPayMoney() != 0 && !buyerRefundInfo.isToBalance()) {
				RefundInfoEntity buyer = new RefundInfoEntity();
				buyer.setName(et_refund_buyer_name.getText().toString());
				buyer.setBank(et_refund_buyer_bank.getText().toString());
				buyer.setCard(et_refund_buyer_card_number.getText().toString());

				intent.putExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_BUYER, buyer);
			}

			if (sellerRefundInfo != null && sellerRefundInfo.getPayMoney() != 0 && !sellerRefundInfo.isToBalance()) {
				RefundInfoEntity seller = new RefundInfoEntity();
				seller.setName(et_refund_seller_name.getText().toString());
				seller.setBank(et_refund_seller_bank.getText().toString());
				seller.setCard(et_refund_seller_card_number.getText().toString());

				intent.putExtra(ChangePromisesActivity.KEY_INTENT_EXTRA_SELLER, seller);
			}

			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * 校验信息
	 */
	private boolean validation() {
		if (finBuyerInfo != null || (buyerRefundInfo != null && buyerRefundInfo.getPayMoney() != 0 && !buyerRefundInfo.isToBalance())) {
			if (TextUtils.isEmpty(et_refund_buyer_name.getText().toString())) {
				showErrorMsg(et_refund_buyer_name, "请填写买家开户姓名");
				return false;
			}

			if (TextUtils.isEmpty(et_refund_buyer_bank.getText().toString())) {
				showErrorMsg(et_refund_buyer_bank, "请填写买家开户开户行");
				return false;
			}

			if (TextUtils.isEmpty(et_refund_buyer_card_number.getText().toString())) {
				showErrorMsg(et_refund_buyer_card_number, "请填写买家卡号");
				return false;
			}
		}

		if (finSellerInfo != null || (sellerRefundInfo != null && sellerRefundInfo.getPayMoney() != 0 && !sellerRefundInfo.isToBalance())) {
			if (TextUtils.isEmpty(et_refund_seller_name.getText().toString())) {
				showErrorMsg(et_refund_seller_name, "请填写车主开户姓名");
				return false;
			}

			if (TextUtils.isEmpty(et_refund_seller_bank.getText().toString())) {
				showErrorMsg(et_refund_seller_bank, "请填写车主开户开户行");
				return false;
			}

			if (TextUtils.isEmpty(et_refund_seller_card_number.getText().toString())) {
				showErrorMsg(et_refund_seller_card_number, "请填写车主卡号");
				return false;
			}
		}

		return true;
	}


	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) return;

		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_TRANSFER_CANCELTRANSFERAPPLY)) {
			switch (response.getErrno()) {
				case 0:
					ToastUtil.showInfo("提交成功");
					finish();
					break;
				default:
					ToastUtil.showText(response.getErrmsg());
					break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppHttpServer.getInstance().cancelRequest(call);
	}
}
