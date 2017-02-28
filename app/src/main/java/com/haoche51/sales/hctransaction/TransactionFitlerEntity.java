package com.haoche51.sales.hctransaction;

/**
 * Created by xuhaibo on 15/9/25.
 *
 * 看车列表页过滤条件
 */
public class TransactionFitlerEntity {
	private int type;//0带看任务，1看车失败 2预定成功
	private int start_time; //开始时间
	private int end_time; // 结束时间
	private String search_field;// 文本搜索条件，电话还是姓名
	private String buyer_phone; //买家电话搜索

	public String getBuyer_phone() {
		return buyer_phone;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}
	public void setType(int type) {
		this.type = type;
	}

	public void setStart_time(int start_time) {
		this.start_time = start_time;
	}

	public void setEnd_time(int end_time) {
		this.end_time = end_time;
	}

	public void setSearch_field(String search_field) {
		this.search_field = search_field;
	}

	public String getSearch_field() {
		return search_field;
	}

	public int getEnd_time() {
		return end_time;
	}

	public int getStart_time() {
		return start_time;
	}

	public int getType() {
		return type;
	}

}
