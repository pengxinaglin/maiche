package com.haoche51.sales.hccustomer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户主页
 * Created by mac on 16/11/15.
 */
public class CustomerMainActivity extends CommonStateFragmentActivity implements TextView.OnEditorActionListener, ViewPager.OnPageChangeListener {

	public static final int REQUEST_CODE_ADD_REVISIT = 1;
	public static final int REQUEST_CODE_HIGH_SEAS = 2;

	@ViewInject(R.id.content_view)
	public HCSmartTabLayout hcSmartTabLayout;
	List<FragmentItem> mFragmentItemList;

	@ViewInject(R.id.rl_titlebar)
	private RelativeLayout rl_titlebar;

	@ViewInject(R.id.et_trans_search)
	private EditText et_trans_search;//搜索内容

	@ViewInject(R.id.view_mask)
	private View view_mask;//遮罩

	@ViewInject(R.id.tv_title_add)
	private TextView addBtn;

	@ViewInject(R.id.tv_right_fuction)
	private TextView tv_right_fuction;

	private OverViewFragment overViewFragment;
	private BuyerListFragment mBuyerListFragment;
	private TransactionHighSeasFragment mTransactionHighSeasFragment;


	@Override
	protected int getTitleView() {
		return R.layout.layout_common_titlebar_search_more;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_customer_main;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_customer_mian_title);
		//监听键盘点击搜索
		et_trans_search.setOnEditorActionListener(this);
		addBtn.setVisibility(View.GONE);
		tv_right_fuction.setVisibility(View.GONE);
		hcSmartTabLayout.addOnHCPageChangeListener(this);
	}

	@Override
	protected void initData() {
		mFragmentItemList = new ArrayList<>();
		overViewFragment = new OverViewFragment();
		mBuyerListFragment = new BuyerListFragment();
		mTransactionHighSeasFragment = new TransactionHighSeasFragment();
		mFragmentItemList.add(new FragmentItem(getString(R.string.hc_overview), overViewFragment));
		mFragmentItemList.add(new FragmentItem(getString(R.string.hc_my_customer), mBuyerListFragment));
		mFragmentItemList.add(new FragmentItem(getString(R.string.hc_high_seas), mTransactionHighSeasFragment));
		//设置界面
		hcSmartTabLayout.setContentFragments(this, mFragmentItemList);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (overViewFragment != null) {
			overViewFragment.refreshData(0, null, null);
		}
	}

	/**
	 * 显示搜索框
	 */
	@OnClick(R.id.tv_right_fuction)
	private void showSearchBox(final View v) {
		int width = getWindowManager().getDefaultDisplay().getWidth();
		doAnimator(width, 0, true);
	}

	/**
	 * 隐藏搜索框
	 */
	@OnClick(R.id.view_mask)
	private void clickMask(View v) {
		hideSearchBox(v);
	}

	/**
	 * 隐藏搜索框
	 */
	@OnClick(R.id.tv_search_cancel)
	private void hideSearchBox(View v) {
		//清空搜索
		if (et_trans_search != null)
			et_trans_search.setText("");
		if (mFragmentItemList != null)
			for (FragmentItem item : mFragmentItemList) {
				item.getFragment().doingFilter(new Bundle());
			}
		//隐藏搜索框
		int width = getWindowManager().getDefaultDisplay().getWidth();
		doAnimator(0, width, false);
	}

	@OnClick(R.id.iv_search)
	private void onSearchClick(View v) {
		if (!TextUtils.isEmpty(et_trans_search.getText().toString().trim())) {
			commit(et_trans_search.getText().toString());
		}
	}

	private void doAnimator(int targetWidth, int startWdith, final boolean enable) {
		final ViewGroup.LayoutParams layoutParams = rl_titlebar.getLayoutParams();
		ValueAnimator va = ValueAnimator.ofInt(targetWidth, startWdith);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator va) {
				layoutParams.width = (Integer) va.getAnimatedValue();
				rl_titlebar.setLayoutParams(layoutParams);
			}
		});
		va.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {

			}

			@Override
			public void onAnimationEnd(Animator animator) {
				//激活输入框
				if (enable) {
					//弹出键盘
					et_trans_search.setFocusable(true);
					et_trans_search.setFocusableInTouchMode(true);
					et_trans_search.requestFocus();
					//让软键盘延时弹出，以更好的加载Activity
					et_trans_search.postDelayed(new Runnable() {
						@Override
						public void run() {
							InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							inputManager.showSoftInput(et_trans_search, 0);
						}
					}, 100);
					//弹出半透明效果
					view_mask.setVisibility(View.VISIBLE);
				} else {
					//关闭遮罩
					view_mask.setVisibility(View.GONE);
					//关闭键盘
					et_trans_search.clearFocus();
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(et_trans_search.getWindowToken(), 0);
				}
			}

			@Override
			public void onAnimationCancel(Animator animator) {

			}

			@Override
			public void onAnimationRepeat(Animator animator) {

			}
		});
		va.setDuration(400);
		va.start();
	}

	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
		//监听点击输入法的“搜索”
		if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
			//当前页面进行搜索
			commit(et_trans_search.getText().toString());
			return true;
		}
		return false;
	}

	private void commit(String search_field) {
		//当前页面进行搜索
		if (mFragmentItemList != null) {
			FragmentItem item = mFragmentItemList.get(hcSmartTabLayout.getCurrentPagerPosition());
			Bundle where = new Bundle();
			where.putString("search_field", search_field);
			item.getFragment().doingFilter(where);
		}
		//关闭遮罩
		view_mask.setVisibility(View.GONE);
		//关闭键盘
		et_trans_search.clearFocus();
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(et_trans_search.getWindowToken(), 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == REQUEST_CODE_HIGH_SEAS) {
			if (mTransactionHighSeasFragment != null) {
				mTransactionHighSeasFragment.onActivityResult(requestCode, resultCode, data);
			}
			if (mBuyerListFragment != null) {
				mBuyerListFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
			case 0:
				tv_right_fuction.setVisibility(View.GONE);
				break;
			default:
				tv_right_fuction.setVisibility(View.VISIBLE);
				et_trans_search.setHint(getString(R.string.hc_search_hint));
				break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}

