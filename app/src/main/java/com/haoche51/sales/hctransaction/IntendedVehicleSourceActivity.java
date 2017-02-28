package com.haoche51.sales.hctransaction;

import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.hccustomer.IntendedSourceFragment;
import com.haoche51.sales.util.ToastUtil;

/**
 * 意向车源
 * Created by mac on 15/10/30.
 */
public class IntendedVehicleSourceActivity extends CommonStateFragmentActivity {

	private String phone;

	@Override
	protected int getContentView() {
		return R.layout.activity_intended_vehicle;
	}


	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.intended_source);
		phone = getIntent().getStringExtra("phone");
		if (TextUtils.isEmpty(phone)) {
			ToastUtil.showInfo("参数不足!");
			finish();
			return;
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.fl_activity_message_list_container, new IntendedSourceFragment(phone));
		ft.commitAllowingStateLoss();

	}
}
