package com.haoche51.sales.hctransaction;

import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.hccustomer.TransRecordFragment;
import com.haoche51.sales.util.ToastUtil;

/**
 * 买家看车记录列表
 */
public class TransactionRecordListActivity extends CommonStateFragmentActivity {

	private String buyer_phone;//需要查询的买家手机号

	protected int getContentView() {
		return R.layout.activity_transaction_record_list;
	}

	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_trans_history_activity_title);
		buyer_phone = getIntent().getStringExtra("buyer_phone");
		if (TextUtils.isEmpty(buyer_phone)) {
			ToastUtil.showInfo("参数不足!");
			finish();
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fl_activity_message_list_container, new TransRecordFragment(buyer_phone));
		ft.commitAllowingStateLoss();
	}
}
