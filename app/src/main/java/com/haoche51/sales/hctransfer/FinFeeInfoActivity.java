package com.haoche51.sales.hctransfer;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.loopj.android.http.RequestHandle;
import com.mobsandgeeks.saripaar.annotation.Required;

import okhttp3.Call;

/**
 * Created by PengXianglin on 16/7/27.
 */
public class FinFeeInfoActivity extends CommonStateActivity {

	@ViewInject(R.id.tv_audit_status)
	private TextView tv_refuse_status;
	@ViewInject(R.id.tv_audit_des)
	private TextView tv_refuse_reason;

	@ViewInject(R.id.et_transfer_fee)
	private TextView et_transfer_fee;//过户费

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

	@Override
	protected int getContentView() {
		return R.layout.activity_fintransfer;
	}

	@Override
	protected void initData() {
		setScreenTitle(getString(R.string.hc_transfer_fee));

		transferId = getIntent().getStringExtra("transferId");
		if (TextUtils.isEmpty(transferId)) {
			ToastUtil.showInfo("参数有误");
			finish();
		}

		et_transfer_fee.setText(getIntent().getStringExtra("transfer_amount"));

		TransferEntity.FinFeeInfoEntity entity = (TransferEntity.FinFeeInfoEntity) getIntent().getSerializableExtra("finEntity");
		if (entity != null) {
			//头部状态
			if (!TextUtils.isEmpty(entity.getStatus_text())) {
				tv_refuse_status.setVisibility(View.VISIBLE);
				tv_refuse_status.setText(entity.getStatus_text());
			} else {
				tv_refuse_status.setVisibility(View.GONE);
			}
			//失败原因
			if (!TextUtils.isEmpty(entity.getReason())) {
				tv_refuse_reason.setVisibility(View.VISIBLE);
				tv_refuse_reason.setText(entity.getReason());
			} else {
				tv_refuse_reason.setVisibility(View.GONE);
			}

			et_refund_name.setText(entity.getCard_name());
			et_refund_bank.setText(entity.getCard_bank());
			et_refund_card_number.setText(entity.getCard_number());

			//0未提交 40拒绝
			if (entity.getTrade_status() == 0 || entity.getTrade_status() == 40) {
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

	Call requestHandle;

	@Override
	public void onValidationSucceeded() {
		//校验成功  回传参数
		String name = et_refund_name.getText().toString();
		String bank = et_refund_bank.getText().toString();
		String card = et_refund_card_number.getText().toString();

		ProgressDialogUtil.showProgressDialog(this, null);
		requestHandle = AppHttpServer.getInstance().post(HCHttpRequestParam.transferFeeApply(transferId, name, bank, card), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) return;

		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_TRANSFER_TRANSFERFEEAPPLY)) {
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
