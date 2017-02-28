package com.haoche51.sales.hccustomer;

/**
 * 概述
 * Created by PengXianglin on 16/11/21.
 */

public class OverViewEntity {
	private int my_revisit_count;// 我的回访
	private int reuse_revisit_count;// 回炉回访
	private int notrans_revisit_count;// 未推出带看
	private int nosite_fail_count;// 失败未上门
	private int onsite_fail_count;// 失败已上门
	private int subscribe_count;// 新车推荐买家

	public int getMy_revisit_count() {
		return my_revisit_count;
	}

	public void setMy_revisit_count(int my_revisit_count) {
		this.my_revisit_count = my_revisit_count;
	}

	public int getNosite_fail_count() {
		return nosite_fail_count;
	}

	public void setNosite_fail_count(int nosite_fail_count) {
		this.nosite_fail_count = nosite_fail_count;
	}

	public int getNotrans_revisit_count() {
		return notrans_revisit_count;
	}

	public void setNotrans_revisit_count(int notrans_revisit_count) {
		this.notrans_revisit_count = notrans_revisit_count;
	}

	public int getOnsite_fail_count() {
		return onsite_fail_count;
	}

	public void setOnsite_fail_count(int onsite_fail_count) {
		this.onsite_fail_count = onsite_fail_count;
	}

	public int getReuse_revisit_count() {
		return reuse_revisit_count;
	}

	public void setReuse_revisit_count(int reuse_revisit_count) {
		this.reuse_revisit_count = reuse_revisit_count;
	}

	public int getSubscribe_count() {
		return subscribe_count;
	}

	public void setSubscribe_count(int subscribe_count) {
		this.subscribe_count = subscribe_count;
	}
}
