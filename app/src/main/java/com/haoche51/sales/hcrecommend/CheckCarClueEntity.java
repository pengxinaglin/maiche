package com.haoche51.sales.hcrecommend;

/**
 * 验车车源线索实体类
 * Created by wufx on 2016/1/20.
 */
public class CheckCarClueEntity {
	/**
	 * 车主电话
	 */
	private String seller_phone;
	/**
	 * 车主姓名
	 */
	private String seller_name;
	/**
	 * 品牌id
	 */
	private int brand_id;
	/**
	 * 品牌名称
	 */
	private String brand_name;
	/**
	 * 车系id
	 */
	private int class_id;
	/**
	 * 车系名称
	 */
	private String class_name;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 登录用户id：对应crm_user_id
	 */
	private int id;
	/**
	 * 登录用户名：crm_user_name
	 */
	private String name;

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

	public int getClass_id() {
		return class_id;
	}

	public void setClass_id(int class_id) {
		this.class_id = class_id;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getSeller_phone() {
		return seller_phone;
	}

	public void setSeller_phone(String seller_phone) {
		this.seller_phone = seller_phone;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
}
