package com.haoche51.sales.hctransaction;

/**
 * Created by mac on 15/9/29.
 */
public class TransactionTaskShortEntity {

	/**
	 * id : 1088
	 * buyer_name : 何先生
	 * buyer_phone : 13800333333
	 * appointment_starttime : 1442457420
	 * place : 北京市朝阳区朝阳门-地铁站
	 * vehicle_source_id : 20014
	 * vehicle_name : 巴博斯 CLS级 2012款 35GC
	 * status : 4
	 * cancel_trans_status : 0
	 */

	private int id;
	private String buyer_name;
	private String buyer_phone;
	private int appointment_starttime;
	private String place;
	private String vehicle_source_id;
	private String vehicle_name;
	private int status;
	private int cancel_trans_status;
	private int level;
	private int audit_status;
	private String audit_text;//文案
	private int vehicle_status; //车源状态. 1:待检 2:审核 3: 上线 4: 预定 5:售出 6:公司回购 7:车主售出
	private int car_dealer = -1;//是否是车商 1是 0否
	private int task_type;//0：户外，1：展厅

	public int getCar_dealer() {
		return car_dealer;
	}

	public void setCar_dealer(int car_dealer) {
		this.car_dealer = car_dealer;
	}

	public int getVehicle_status() {
		return vehicle_status;
	}

	public void setVehicle_status(int vehicle_status) {
		this.vehicle_status = vehicle_status;
	}

	public int getAudit_status() {
		return audit_status;
	}

	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	public void setAppointment_starttime(int appointment_starttime) {
		this.appointment_starttime = appointment_starttime;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public void setVehicle_source_id(String vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setCancel_trans_status(int cancel_trans_status) {
		this.cancel_trans_status = cancel_trans_status;
	}

	public int getId() {
		return id;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public String getBuyer_phone() {
		return buyer_phone;
	}

	public int getAppointment_starttime() {
		return appointment_starttime;
	}

	public String getPlace() {
		return place;
	}

	public String getVehicle_source_id() {
		return vehicle_source_id;
	}

	public String getVehicle_name() {
		return vehicle_name;
	}

	public int getStatus() {
		return status;
	}

	public int getCancel_trans_status() {
		return cancel_trans_status;
	}

	public int getLevel() {
		return level;
	}

	public String getAudit_text() {
		return audit_text;
	}

	public void setAudit_text(String audit_text) {
		this.audit_text = audit_text;
	}

	public int getTask_type() {
		return task_type;
	}

	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
}
