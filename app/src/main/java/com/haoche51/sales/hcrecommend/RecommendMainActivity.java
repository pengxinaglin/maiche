package com.haoche51.sales.hcrecommend;

import android.content.Intent;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐主页
 * Created by pengxianglin on 16/6/8.
 */
public class RecommendMainActivity extends CommonStateFragmentActivity {

	@ViewInject(R.id.content_view)
	private HCSmartTabLayout hcSmartTabLayout;
	List<FragmentItem> mFragmentItemList;

	@Override
	protected int getContentView() {
		return R.layout.activity_recommend_main;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.buyer_clues);
	}

	@Override
	protected void initData() {
		mFragmentItemList = new ArrayList<>();

		mFragmentItemList.add(new FragmentItem(getString(R.string.p_buyer), new BuyerRecommendFragment()));
		mFragmentItemList.add(new FragmentItem(getString(R.string.hc_repurchase), new PurchaseRecommendFragment()));
		mFragmentItemList.add(new FragmentItem(getString(R.string.hc_check_car), new CheckCarFragment()));

		//设置界面
		hcSmartTabLayout.setContentFragments(this, mFragmentItemList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) return;
		switch (requestCode) {
			case BuyerRecommendFragment.REQUEST_CODE_ADD_CLUES:
				//添加买家线索
				mFragmentItemList.get(0).getFragment().onActivityResult(requestCode, resultCode, data);
				break;
			case PurchaseRecommendFragment.REQUEST_CODE_ADD_CLUES:
				//添加回购线索
				mFragmentItemList.get(1).getFragment().onActivityResult(requestCode, resultCode, data);
				break;
			case CheckCarFragment.REQUEST_CODE_ADD_CLUES:
				//添加验车线索
				mFragmentItemList.get(2).getFragment().onActivityResult(requestCode, resultCode, data);
				break;
		}
	}
}