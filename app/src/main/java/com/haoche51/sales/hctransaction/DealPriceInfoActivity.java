package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 填写成本价转账信息
 * Created by PengXianglin on 16/12/1.
 */
public class DealPriceInfoActivity extends CommonStateActivity {

	public static final String EXTRA_SELLER_PREPAY = "extra_seller_prepay";
	public static final String EXTRA_DEAL_PRICE = "extra_deal_price";

	@ViewInject(R.id.tv_deal_price)
	private TextView tv_deal_price;//成本价/担保车款
	@ViewInject(R.id.tv_seller_prepay)
	private TextView tv_seller_prepay;//车主定金
	@ViewInject(R.id.et_refund_seller_name)
	private TextView et_refund_seller_name;//车主开户姓名
	@ViewInject(R.id.et_refund_seller_bank)
	private TextView et_refund_seller_bank;//车主开户银行
	@ViewInject(R.id.et_refund_seller_card_number)
	private TextView et_refund_seller_card_number;//车主开户卡号

	@Override
	protected int getContentView() {
		return R.layout.acitivity_deal_price_info;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle("转账信息");
	}

	@Override
	protected void initData() {
		tv_deal_price.setText(String.valueOf(getIntent().getIntExtra(EXTRA_DEAL_PRICE, 0)));
		tv_seller_prepay.setText(String.valueOf(getIntent().getIntExtra(EXTRA_SELLER_PREPAY, 0)));
	}

	@OnClick(R.id.btn_commit)
	private void commit(View v) {
		if (validation()) {
			Intent intent = new Intent();
			RefundInfoEntity seller = new RefundInfoEntity();
			seller.setName(et_refund_seller_name.getText().toString());
			seller.setBank(et_refund_seller_bank.getText().toString());
			seller.setCard(et_refund_seller_card_number.getText().toString());
			intent.putExtra(EXTRA_DEAL_PRICE, seller);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	/**
	 * 校验信息
	 */
	private boolean validation() {
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
		return true;
	}
}
