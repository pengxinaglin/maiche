package com.haoche51.sales.hctransaction;

/**
 * Created by 车主回访 on 15/8/27.
 */
public class VehicleRevisitEntity {

	private long id;
	private long vehicle_source_id;//车源id
	private String vehicle_info;//车源信息
	private String vehicle_owner_phone;//车主电话
	private String vehicle_owner;//车主姓名
	private String custom_service_id;//客服id
	private String custom_service;//客服姓名
	private int type;//回访对象 1: 卖家 2: 买家
	private String visitor_phone;//回访者电话
	private String visitor_name;//回访者姓名
	private String record;//回访记录
	private int status; //当时的回访状态
	private int time;//回访时间
	private String group_name;//管理员

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getVehicle_source_id() {
		return vehicle_source_id;
	}

	public void setVehicle_source_id(long vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}

	public String getVehicle_info() {
		return vehicle_info;
	}

	public void setVehicle_info(String vehicle_info) {
		this.vehicle_info = vehicle_info;
	}

	public String getVehicle_owner_phone() {
		return vehicle_owner_phone;
	}

	public void setVehicle_owner_phone(String vehicle_owner_phone) {
		this.vehicle_owner_phone = vehicle_owner_phone;
	}

	public String getVehicle_owner() {
		return vehicle_owner;
	}

	public void setVehicle_owner(String vehicle_owner) {
		this.vehicle_owner = vehicle_owner;
	}

	public String getCustom_service_id() {
		return custom_service_id;
	}

	public void setCustom_service_id(String custom_service_id) {
		this.custom_service_id = custom_service_id;
	}

	public String getCustom_service() {
		return custom_service;
	}

	public void setCustom_service(String custom_service) {
		this.custom_service = custom_service;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getVisitor_phone() {
		return visitor_phone;
	}

	public void setVisitor_phone(String visitor_phone) {
		this.visitor_phone = visitor_phone;
	}

	public String getVisitor_name() {
		return visitor_name;
	}

	public void setVisitor_name(String visitor_name) {
		this.visitor_name = visitor_name;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
}
