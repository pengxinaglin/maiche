package com.haoche51.sales.hcrecommend;

/**
 * Created by PengXianglin on 16/6/14.
 */
public class PurchaseCluesEntity {
	/**
	 'vehicle_source_id' => int, //内网车源id
	 'title' => '奥迪 A4', //任务名称，即为标题
	 'remark' => '备注', //备注
	 'status_text' => '状态', //已预约/待付定金 等状态文本值
	 'create_time' => int, //提交时间
	 * */

	private int vehicle_source_id;
	private String title;
	private String remark;
	private String status_text;
	private int create_time;

	public int getCreate_time() {
		return create_time;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus_text() {
		return status_text;
	}

	public void setStatus_text(String status_text) {
		this.status_text = status_text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getVehicle_source_id() {
		return vehicle_source_id;
	}

	public void setVehicle_source_id(int vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}
}
