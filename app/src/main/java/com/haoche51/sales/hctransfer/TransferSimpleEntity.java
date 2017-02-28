package com.haoche51.sales.hctransfer;

import java.io.Serializable;

/**
 * 过户列表item对象
 * Created by yangming on 2016/1/23.
 */
public class TransferSimpleEntity implements Serializable {

	private String transaction_id;           //交易表id
	private String vehicle_source_id;        //车源编号
	private String status;                      //订单状态:3 待过户，4 待转账 5 交易完成
	private int transfer_time;               //过户时间
	private String brand_name;               //品牌名称
	private String class_name;               //车系
	private String plate_number;             //车牌号
	private String audit_text;              // 列表页展示文案 // 待过户，拒绝过户，过户合同审核中，过户合同审核失败，已过户；
	private int audit_status;    // 1待过户，2拒绝过户，3过户合同审核中，4过户合同审核失败，5已过户

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getVehicle_source_id() {
		return vehicle_source_id;
	}

	public void setVehicle_source_id(String vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTransfer_time() {
		return transfer_time;
	}

	public void setTransfer_time(int transfer_time) {
		this.transfer_time = transfer_time;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getPlate_number() {
		return plate_number;
	}

	public void setPlate_number(String plate_number) {
		this.plate_number = plate_number;
	}

	public int getAudit_status() {
		return audit_status;
	}

	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}

	public String getAudit_text() {
		return audit_text;
	}

	public void setAudit_text(String audit_text) {
		this.audit_text = audit_text;
	}
}
