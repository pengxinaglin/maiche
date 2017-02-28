package com.haoche51.record.entity;

public class PhoneRecordEntity extends BaseEntity {

	private int id =0;
	private int employee_number = 0;
	private int call_type =0; //电话类型，0,呼入,1呼出
	private int call_duration = 0 ;//秒级 s
	private long create_time = 0;//来点或者去电时间
	private String customer_phone ="";//客户电话
	private String saler_phone = ""; //自己电话
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEmployee_number() {
		return employee_number;
	}
	public void setEmployee_number(int employee_number) {
		this.employee_number = employee_number;
	}
	public int getCall_type() {
		return call_type;
	}
	public void setCall_type(int call_type) {
		this.call_type = call_type;
	}
	public int getCall_duration() {
		return call_duration;
	}
	public void setCall_duration(int call_duration) {
		this.call_duration = call_duration;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public String getCustomer_phone() {
		return customer_phone;
	}
	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}
	public String getSaler_phone() {
		return saler_phone;
	}
	public void setSaler_phone(String saler_phone) {
		this.saler_phone = saler_phone;
	}
	@Override
	public String toString()
	{
		return "PhoneRecordEntity [id=" + id + ", employee_number=" + employee_number + ", call_type=" + call_type
				+ ", call_duration=" + call_duration + ", create_time=" + create_time + ", customer_phone="
				+ customer_phone + ", saler_phone=" + saler_phone + "]";
	}

}
