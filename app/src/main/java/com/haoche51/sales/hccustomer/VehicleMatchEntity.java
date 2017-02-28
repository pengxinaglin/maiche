package com.haoche51.sales.hccustomer;

import java.util.List;

/**
 * Created by PengXianglin on 16/11/24.
 */
public class VehicleMatchEntity {
	private int vehicle_source_id;// 123, //车源id
	private String vehicle_name;//  宝马 ,   //车源名称
	private double seller_price;// 13, //报价13万
	private double cheap_price;// 10, //底价
	private int register_time;//  1362067200 ,//上牌时间
	private int online_time;//  1445593080 ,//上线时间
	private double miles;// 10,              //公里数
	private String gearbox;//     //变速箱
	private String image_url;//  http://image1.haoche51.com/11848ba321a-1e5b-4b1d-938a-47672e2fe4b7.jpg , //图片
	private int vehicle_type;// 1, // 车源类型
	private String vehicle_type_text;//  回购 ,   // 车源类型显示的文案
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

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
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

	public double getSeller_price() {
		return seller_price;
	}

	public void setSeller_price(double seller_price) {
		this.seller_price = seller_price;
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

	public int getVehicle_type() {
		return vehicle_type;
	}

	public void setVehicle_type(int vehicle_type) {
		this.vehicle_type = vehicle_type;
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
