package com.haoche51.sales.hctransaction;

/**
 * Created by mac on 15/11/11.
 */
public class PhoneRecordEntity {

	/**
	 * call_type : 0
	 * customer_phone : 13321100202
	 * create_time : 2147483647
	 * call_duration : 6712
	 * record_url : Fq-CgBKdjjPWHdGevySSiysRs8iT
	 */

	private int call_type;//电话类型，0,呼入,1呼出
	private String customer_phone;
	private int create_time;//时间
	private int call_duration;//通话时长 秒级
	private String record_url;
	private boolean isPaly;

	public boolean isPaly() {
		return isPaly;
	}

	public void setPaly(boolean isPaly) {
		this.isPaly = isPaly;
	}

	public void setCall_type(int call_type) {
		this.call_type = call_type;
	}

	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public void setCall_duration(int call_duration) {
		this.call_duration = call_duration;
	}

	public void setRecord_url(String record_url) {
		this.record_url = record_url;
	}

	public int getCall_type() {
		return call_type;
	}

	public String getCustomer_phone() {
		return customer_phone;
	}

	public int getCreate_time() {
		return create_time;
	}

	public int getCall_duration() {
		return call_duration;
	}

	public String getRecord_url() {
		return record_url;
	}
}
