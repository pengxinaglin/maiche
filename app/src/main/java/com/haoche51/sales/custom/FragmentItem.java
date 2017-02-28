package com.haoche51.sales.custom;

import com.haoche51.sales.hcbasefragment.CommonBaseFragment;

/**
 * Created by mac on 15/11/19.
 */
public class FragmentItem {
	private String title;
	private CommonBaseFragment fragment;

	public FragmentItem(String title, CommonBaseFragment fragment) {
		this.title = title;
		this.fragment = fragment;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CommonBaseFragment getFragment() {
		return fragment;
	}

	public void setFragment(CommonBaseFragment fragment) {
		this.fragment = fragment;
	}
}
