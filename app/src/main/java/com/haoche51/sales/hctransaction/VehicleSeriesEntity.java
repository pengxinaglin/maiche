package com.haoche51.sales.hctransaction;

import com.haoche51.sales.entity.BaseEntity;

import java.io.Serializable;

public class VehicleSeriesEntity extends BaseEntity implements Serializable {
	private int id = 0;
    private String name = "";
    private String pinyin="";
    private int brand_id = 0;
    private String brand_name = "";
    private int subbrand_id = 0;
    private String subbrand_name = "";
 
    public static VehicleSeriesEntity parseFromJson(String jsonString) {
        return gson.fromJson(jsonString, VehicleSeriesEntity.class);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public int getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}

	public String getBrand_name() {
		return brand_name;
	}

	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}

	public int getSubbrand_id() {
		return subbrand_id;
	}

	public void setSubbrand_id(int subbrand_id) {
		this.subbrand_id = subbrand_id;
	}

	public String getSubbrand_name() {
		return subbrand_name;
	}

	public void setSubbrand_name(String subbrand_name) {
		this.subbrand_name = subbrand_name;
	}

}
