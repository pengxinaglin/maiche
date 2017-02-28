package com.haoche51.sales.hctransaction;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 车主录音、买家录音
 * Created by mac on 15/11/11.
 */
public class PhoneRecorListActivity extends CommonStateFragmentActivity {

	public static final String KEY_INTENT_EXTRA_BUYER_PHONE = "buyer_phone";
	public static final String KEY_INTENT_EXTRA_SALER_PHONE = "saler_phone";
	public static final String KEY_TASK_TYPE = "task_type";
	@ViewInject(R.id.hc_smarttab_layout_content_view)
	private HCSmartTabLayout hcSmartTabLayout;

	private String salerphone, buyerPhone;
	private String taskId;

	private int taskType;
	private boolean isSalerRecordShow = true;

	private PhoneRecordListFragment salerFragment;
	private PhoneRecordListFragment buyerFragment;

	@Override
	protected int getTitleView() {
		return R.layout.layout_common_titlebar;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_common_smarttab;
	}

	@Override
	protected void initView() {
		super.initView();
		registerTitleBack();
		setScreenTitle(getString(R.string.hc_phone_record_list_activity_title));

		taskId = getIntent().getStringExtra("id");
		taskType = getIntent().getIntExtra(KEY_TASK_TYPE, 0);

		//车主信息
		if (getIntent().hasExtra(KEY_INTENT_EXTRA_SALER_PHONE)) {
			salerphone = getIntent().getStringExtra(KEY_INTENT_EXTRA_SALER_PHONE);
			salerFragment = PhoneRecordListFragment.newInstance(salerphone, taskId, taskType);
		}

		//买家信息
		if (getIntent().hasExtra(KEY_INTENT_EXTRA_BUYER_PHONE)) {
			buyerPhone = getIntent().getStringExtra(KEY_INTENT_EXTRA_BUYER_PHONE);
			buyerFragment = PhoneRecordListFragment.newInstance(buyerPhone, taskId, taskType);
		}
	}

	@Override
	protected void initData() {
		super.initData();
		if (salerFragment != null && buyerFragment != null) {
			List<FragmentItem> list = new ArrayList<>();
			list.add(new FragmentItem(getString(R.string.hc_phone_record_list_saler), salerFragment));
			list.add(new FragmentItem(getString(R.string.hc_phone_record_list_buyer), buyerFragment));
			//设置界面
			hcSmartTabLayout.setContentFragments(this, list);

			hcSmartTabLayout.addOnHCPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				}

				@Override
				public void onPageSelected(int position) {
					//tab 切换，播放停止
					if (salerFragment != null)
						salerFragment.pausePlay();

					if (buyerFragment != null)
						buyerFragment.pausePlay();
				}

				@Override
				public void onPageScrollStateChanged(int state) {
				}
			});

		} else {
			//只显示一个录音列表
			hcSmartTabLayout.setVisibility(View.GONE);
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (salerFragment != null) {
				ft.add(R.id.fl_activity_common_smarttab, PhoneRecordListFragment.newInstance(salerphone, taskId, taskType));
			} else if (buyerFragment != null) {
				ft.add(R.id.fl_activity_common_smarttab, PhoneRecordListFragment.newInstance(buyerPhone, taskId, taskType));
			}
			ft.commitAllowingStateLoss();
		}
	}


}
