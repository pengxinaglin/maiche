package com.haoche51.sales.hctransaction;

/**
 * Created by mac on 15/9/28.
 */
public class TransactionRecordEntity {


	/**
	 * vehicle_source_id : 10121
	 * vehicle_name : 马自达6 2011款 2.0L 自动时尚型
	 * appointment_starttime : 1444804200
	 * place : 北京市东城区豆瓣胡同-道路
	 * custom_service_name : 彭祥林
	 * abort_reason :
	 * status : 3
	 * cancel_trans_status : 0
	 * seller_name : 马先生
	 * seller_phone : 13311055824
	 * custom_service_phone :
	 */

	private int trans_id;
	private int vehicle_source_id;
	private String vehicle_name;
	private int appointment_starttime;
	private String place;
	private String custom_service_name;
	private String abort_reason;
	private int status;
	private int cancel_trans_status;
	private String seller_name;
	private String seller_phone;
	private String custom_service_phone;

	public void setVehicle_source_id(int vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}

	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}

	public void setAppointment_starttime(int appointment_starttime) {
		this.appointment_starttime = appointment_starttime;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setCustom_service_name(String custom_service_name) {
		this.custom_service_name = custom_service_name;
	}

	public void setAbort_reason(String abort_reason) {
		this.abort_reason = abort_reason;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setCancel_trans_status(int cancel_trans_status) {
		this.cancel_trans_status = cancel_trans_status;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public void setSeller_phone(String seller_phone) {
		this.seller_phone = seller_phone;
	}

	public void setCustom_service_phone(String custom_service_phone) {
		this.custom_service_phone = custom_service_phone;
	}

	public int getVehicle_source_id() {
		return vehicle_source_id;
	}

	public String getVehicle_name() {
		return vehicle_name;
	}

	public int getAppointment_starttime() {
		return appointment_starttime;
	}

	public String getPlace() {
		return place;
	}

	public String getCustom_service_name() {
		return custom_service_name;
	}

	public String getAbort_reason() {
		return abort_reason;
	}

	public int getStatus() {
		return status;
	}

	public int getCancel_trans_status() {
		return cancel_trans_status;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public String getSeller_phone() {
		return seller_phone;
	}

	public String getCustom_service_phone() {
		return custom_service_phone;
	}

	public int getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(int trans_id) {
		this.trans_id = trans_id;
	}
}
