package com.haoche51.sales.hcrecommend;

import com.haoche51.sales.entity.BaseEntity;

public class BuyerCluesEntity extends BaseEntity {
	private int id=0;
	private String buyer_phone="";
	private String comment=""; // 备注 线索描述
	private int create_time=0;
	private int processed_time=0;
	private int status =0; //状态 0：跟进中 1：正在处理 2：已处理 3：无效

	public BuyerCluesEntity() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBuyer_phone() {
		return buyer_phone;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	public int getCreate_time() {
		return create_time;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public int getProcessed_time() {
		return processed_time;
	}

	public void setProcessed_time(int processed_time) {
		this.processed_time = processed_time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
