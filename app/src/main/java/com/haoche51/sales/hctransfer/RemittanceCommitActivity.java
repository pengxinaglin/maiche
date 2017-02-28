package com.haoche51.sales.hctransfer;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransaction.RefundInfoEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.loopj.android.http.RequestHandle;
import com.mobsandgeeks.saripaar.annotation.Required;

import okhttp3.Call;

/**
 * 提交转账信息
 * Created by PengXianglin on 16/6/25.
 */
public class RemittanceCommitActivity extends CommonStateActivity {

	public final static String EXTRA_TRANSFER_OTHER = "extra_transfer_other";//转其它支出

	@ViewInject(R.id.tv_audit_status)
	private TextView tv_refuse_status;
	@ViewInject(R.id.tv_audit_des)
	private TextView tv_refuse_reason;

	@ViewInject(R.id.tv_owner_out_payment)
	private TextView tv_owner_out_payment;//转账金额

	@ViewInject(R.id.tv_seller_company_prepay)
	private TextView tv_seller_company_prepay;//车主定金

	@Required(order = 1, message = "填写开户姓名")
	@ViewInject(R.id.et_refund_name)
	private EditText et_refund_name;//开户姓名

	@Required(order = 2, message = "填写开户银行")
	@ViewInject(R.id.et_refund_bank)
	private EditText et_refund_bank;//开户银行

	@Required(order = 3, message = "填写银行卡号")
	@ViewInject(R.id.et_refund_card_number)
	private EditText et_refund_card_number;//银行卡号

	@ViewInject(R.id.btn_commit)
	private Button btn_commit;//提交按钮
	private String transferId;//需要更新的transaction记录id

	private int owner_out_payment;//公司需转车主金额
	private int seller_company_prepay;//车主定金

	RefundInfoEntity refundInfoEntity;
	TransferEntity.FinFeeInfoEntity finFeeInfoEntity;

	@Override
	protected int getContentView() {
		return R.layout.activity_remittance_commit;
	}

	@Override
	protected void initData() {
		setScreenTitle("转账信息");
		owner_out_payment = getIntent().getIntExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, 0);
		seller_company_prepay = getIntent().getIntExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY, 0);

		//两个金额都<=0
		if (owner_out_payment <= 0 && seller_company_prepay <= 0) {
			ToastUtil.showInfo("传递参数错误");
			finish();
		}

		tv_owner_out_payment.setText(String.valueOf(owner_out_payment));
		tv_seller_company_prepay.setText(String.valueOf(seller_company_prepay));
		if (getIntent().getBooleanExtra(EXTRA_TRANSFER_OTHER, false)) {
			((View) tv_seller_company_prepay.getParent()).setVisibility(View.GONE);
		}

		//驳回后自动补全上一次提交的信息
		refundInfoEntity = getIntent().getParcelableExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_SELLER_BANK);
		if (refundInfoEntity != null) {
			et_refund_name.setText(refundInfoEntity.getName());
			et_refund_bank.setText(refundInfoEntity.getBank());
			et_refund_card_number.setText(refundInfoEntity.getCard());
			return;
		}

		//过户完成后转车主/转其他支出
		transferId = getIntent().getStringExtra("transferId");
		finFeeInfoEntity = (TransferEntity.FinFeeInfoEntity) getIntent().getSerializableExtra("finEntity");
		if (finFeeInfoEntity != null) {
			//头部状态
			if (!TextUtils.isEmpty(finFeeInfoEntity.getStatus_text())) {
				tv_refuse_status.setVisibility(View.VISIBLE);
				tv_refuse_status.setText(finFeeInfoEntity.getStatus_text());
			} else {
				tv_refuse_status.setVisibility(View.GONE);
			}
			//失败原因
			if (!TextUtils.isEmpty(finFeeInfoEntity.getReason())) {
				tv_refuse_reason.setVisibility(View.VISIBLE);
				tv_refuse_reason.setText(finFeeInfoEntity.getReason());
			} else {
				tv_refuse_reason.setVisibility(View.GONE);
			}

			et_refund_name.setText(finFeeInfoEntity.getCard_name());
			et_refund_bank.setText(finFeeInfoEntity.getCard_bank());
			et_refund_card_number.setText(finFeeInfoEntity.getCard_number());

			//0未提交 40拒绝
			if (finFeeInfoEntity.getTrade_status() == 0 || finFeeInfoEntity.getTrade_status() == 40) {
				btn_commit.setVisibility(View.VISIBLE);
			} else {
				btn_commit.setVisibility(View.GONE);
				et_refund_name.setEnabled(false);
				et_refund_bank.setEnabled(false);
				et_refund_card_number.setEnabled(false);
			}
		}
	}

	@OnClick(R.id.btn_commit)
	private void commit(View v) {
		validator.validate();
	}

	@Override
	public void onValidationSucceeded() {
		if (refundInfoEntity != null) {
			//校验成功  回传参数
			String name = et_refund_name.getText().toString();
			String bank = et_refund_bank.getText().toString();
			String card = et_refund_card_number.getText().toString();

			RefundInfoEntity entity = new RefundInfoEntity();
			entity.setName(name);
			entity.setBank(bank);
			entity.setCard(card);

			Intent intent = new Intent();
			intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_SELLER_BANK, entity);

			setResult(RESULT_OK, intent);
			finish();
		} else {
			//校验成功  回传参数
			String name = et_refund_name.getText().toString();
			String bank = et_refund_bank.getText().toString();
			String card = et_refund_card_number.getText().toString();

			ProgressDialogUtil.showProgressDialog(this, null);
			if (getIntent().getBooleanExtra(EXTRA_TRANSFER_OTHER, false)) {
				// 转其他支出
				requestHandle = AppHttpServer.getInstance().post(HCHttpRequestParam.otherFinApply(transferId, name, bank, card), this, 0);
			} else {
				//转车主
				requestHandle = AppHttpServer.getInstance().post(HCHttpRequestParam.sellerTransferApply(transferId, name, bank, card), this, 0);
			}
		}
	}

	Call requestHandle;

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) return;

		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_TRANSFER_SELLERTRANSFERAPPLY) || action.equals(HttpConstants.ACTION_TRANSACTION_OTHERFINAPPLY)) {
			switch (response.getErrno()) {
				case 0:
					ToastUtil.showInfo("提交成功");
					setResult(RESULT_OK);
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
		AppHttpServer.getInstance().cancelRequest(requestHandle);
	}
}
