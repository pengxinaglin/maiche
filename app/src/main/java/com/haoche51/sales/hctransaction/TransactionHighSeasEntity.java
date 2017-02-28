package com.haoche51.sales.hctransaction;

/**
 * Created by yangming on 2016/3/15.
 */
public class TransactionHighSeasEntity {
	private String id;
	private String buyer_phone;
	private String buyer_name;
	private int level;
	private String custom_service_name;//最后跟进人
	private int revisit_time;//最后跟进时间
	private String revisit_content;
	private int onsite_trans_count;
	private int car_dealer;//是否是车商 1是 0否

	public int getCar_dealer() {
		return car_dealer;
	}

	public void setCar_dealer(int car_dealer) {
		this.car_dealer = car_dealer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCustom_service_name() {
		return custom_service_name;
	}

	public void setCustom_service_name(String custom_service_name) {
		this.custom_service_name = custom_service_name;
	}

	public int getRevisit_time() {
		return revisit_time;
	}

	public void setRevisit_time(int revisit_time) {
		this.revisit_time = revisit_time;
	}

	public int getOnsite_trans_count() {
		return onsite_trans_count;
	}

	public void setOnsite_trans_count(int onsite_trans_count) {
		this.onsite_trans_count = onsite_trans_count;
	}

	public String getRevisit_content() {
		return revisit_content;
	}

	public void setRevisit_content(String revisit_content) {
		this.revisit_content = revisit_content;
	}
}
