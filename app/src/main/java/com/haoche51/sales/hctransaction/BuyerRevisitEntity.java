package com.haoche51.sales.hctransaction;

/**
 * Created by 买家回访 on 15/8/27.
 */
public class BuyerRevisitEntity {

	String buyer_phone;//
	String buyer_name;//
	int custom_service_id;//
	String custom_service_name;//
	int time;//
	String content;//
	String vehicle_name;
	int vehicle_source_id;

	public String getBuyer_phone() {
		return buyer_phone;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCustom_service_id() {
		return custom_service_id;
	}

	public void setCustom_service_id(int custom_service_id) {
		this.custom_service_id = custom_service_id;
	}

	public String getCustom_service_name() {
		return custom_service_name;
	}

	public void setCustom_service_name(String custom_service_name) {
		this.custom_service_name = custom_service_name;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getVehicle_name() {
		return vehicle_name;
	}

	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}

	public int getVehicle_source_id() {
		return vehicle_source_id;
	}

	public void setVehicle_source_id(int vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}
}
