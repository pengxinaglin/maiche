package com.haoche51.sales.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.haoche51.sales.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.List;

/**
 * Created by mac on 15/11/19.
 */
public class HCSmartTabLayout extends LinearLayout {

	private SmartTabLayout mSmartTabLayout;
	private ViewPager mViewPager;

	public HCSmartTabLayout(Context context) {
		super(context);
		init(context);
	}

	public HCSmartTabLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	public HCSmartTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public HCSmartTabLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(Context context) {
		//把有smartlayout和viewpager的布局添加进来
		LayoutInflater.from(context).inflate(R.layout.hc_smart_tab_layout, this, true);
		mSmartTabLayout = (SmartTabLayout) findViewById(R.id.smart_vp_tab);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
	}

	/**
	 * 在FragmentActivity中放Fragment
	 */
	public void setContentFragments(FragmentActivity context, List<FragmentItem> list) {
		setAdapter(context.getSupportFragmentManager(), list);
	}

	/**
	 * 在Fragment嵌套中放Fragment
	 */
	public void setContentFragments(Fragment context, List<FragmentItem> list) {
		setAdapter(context.getChildFragmentManager(), list);
	}

	/**
	 * 当前页面的索引
	 */
	public int getCurrentPagerPosition() {
		return mViewPager.getCurrentItem();
	}

	/**
	 * 设置页面的索引
	 */
	public void setCurrentPagerPosition(int position) {
		if (position < mViewPager.getAdapter().getCount()) {
			mViewPager.setCurrentItem(position);
		}
	}

	/**
	 * 设置页面监听
	 */
	public void addOnHCPageChangeListener(ViewPager.OnPageChangeListener l) {
		mViewPager.addOnPageChangeListener(l);
	}

	private void setAdapter(FragmentManager mManager, List<FragmentItem> list) {
		//创建用于存放Fragment的适配器
		SmartTabFragmentPagerAdapter mAdapter = new SmartTabFragmentPagerAdapter(mManager, list);
		//设置viewpager适配器
		mViewPager.setAdapter(mAdapter);
		//绑定viewpager
		mSmartTabLayout.setViewPager(mViewPager);
	}

	private class SmartTabFragmentPagerAdapter extends FragmentPagerAdapter {

		private List<FragmentItem> fragments;

		public SmartTabFragmentPagerAdapter(FragmentManager fm, List<FragmentItem> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position).getFragment();
		}

		//设置标题 不能为null
		public CharSequence getPageTitle(int position) {
			return fragments.get(position).getTitle();
		}

	}

	/**
	 * 获得ViewPager
	 *
	 * @return
	 */
	public ViewPager getViewPager() {
		return mViewPager;
	}
}
