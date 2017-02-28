package com.haoche51.sales.dao;

import com.haoche51.sales.entity.BaseEntity;

public class VehicleBrandEntity extends BaseEntity {
    private int id = 0;
    private String name = "";
    private String pinyin = "";
    private String first_char = "";
    private String img_url = "";

    public static VehicleBrandEntity parseFromJson(String jsonString) {
        return gson.fromJson(jsonString, VehicleBrandEntity.class);
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

	public String getFirst_char() {
		return first_char;
	}

	public void setFirst_char(String first_char) {
		this.first_char = first_char;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

}
