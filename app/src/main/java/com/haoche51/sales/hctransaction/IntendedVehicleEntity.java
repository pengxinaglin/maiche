package com.haoche51.sales.hctransaction;

import java.util.List;

/**
 * Created by mac on 15/11/2.
 */
public class IntendedVehicleEntity {


	/**
	 * vehicle_source_id : 11464
	 * vehicle_name : 金杯 海星T20 2011款 1.0L基本型DL465Q5
	 * seller_name : 韩女士
	 * seller_phone : 18656957015
	 * seller_price : 120
	 * cheap_price : 0
	 * register_time : 1170259200
	 * online_time : 1435119614
	 * miles : 10000
	 * gearbox : 未知
	 * url : http://192.168.1.106:9999/06e897e0119ac1137435a3e375640268c9c4b639.jpg
	 */

	private int vehicle_source_id;
	private String vehicle_name;
	private String seller_name;
	private String seller_phone;
	private double seller_price;
	private double cheap_price;
	private int register_time;
	private int online_time;
	private double miles;
	private String gearbox;
	private String vehicle_type_text;
	private String url;
	private List<String> vehicle_tag;

	public double getCheap_price() {
		return cheap_price;
	}

	public void setCheap_price(double cheap_price) {
		this.cheap_price = cheap_price;
	}

	public String getGearbox() {
		return gearbox;
	}

	public void setGearbox(String gearbox) {
		this.gearbox = gearbox;
	}

	public double getMiles() {
		return miles;
	}

	public void setMiles(double miles) {
		this.miles = miles;
	}

	public int getOnline_time() {
		return online_time;
	}

	public void setOnline_time(int online_time) {
		this.online_time = online_time;
	}

	public int getRegister_time() {
		return register_time;
	}

	public void setRegister_time(int register_time) {
		this.register_time = register_time;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public String getSeller_phone() {
		return seller_phone;
	}

	public void setSeller_phone(String seller_phone) {
		this.seller_phone = seller_phone;
	}

	public double getSeller_price() {
		return seller_price;
	}

	public void setSeller_price(double seller_price) {
		this.seller_price = seller_price;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getVehicle_type_text() {
		return vehicle_type_text;
	}

	public void setVehicle_type_text(String vehicle_type_text) {
		this.vehicle_type_text = vehicle_type_text;
	}

	public List<String> getVehicle_tag() {
		return vehicle_tag;
	}

	public void setVehicle_tag(List<String> vehicle_tag) {
		this.vehicle_tag = vehicle_tag;
	}
}
