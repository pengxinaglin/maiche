package com.haoche51.sales.hctransaction;

import java.util.List;

/**
 * Created by mac on 15/11/2.
 */
public class BuyerEntity {
	/**
	 * phone : 18515040755
	 * name : 张先生
	 * city_id : 12
	 * weixin_account :
	 * city_name : 北京
	 * subscribe_rule : {"subscribe_series":[{"id":"66","name":"宝马3系"},{"id":"314","name":"本田CR-V"}],"emission":2,"gearbox":0,"year":[1,2],"price":[20,22]}
	 * demand_count : 63
	 * trans_count : 65
	 * revisit_count : 263
	 * comment : 上门带看失败，自动加入地销客户列表
	 */

	private String phone;
	private String name;
	private String city_id;
	private String weixin_account;
	private String city_name;
	private SubscribeRuleEntity subscribe_rule;
	private int demand_count;
	private int trans_count;
	private int revisit_count;
	private String comment;
	private int level;
	private int buyer_level = -1;

	private int car_dealer = -1;//是否是车商 1是 0否

	private int create_time;//创建时间
	private int contact_time;
	private int status;//预约状态，0,提交预约; 1,取消试驾订单; 2,试驾完成后取消; 3,试驾完成后并支付定金 4,试驾完成后并全额支付 5,交易完成
	private int abort_type;//abort_type：取消交易类型 1未上门 2车没看上 3价格没谈成 4其他原因
	private int last_arrive_time;//最后到店时间

	private String revisit_content;//最后回访内容
	private int revisit_time;//最后回访时间
	private int today_task;//今日需回访，1是  0否
	private int onsite_trans_count;//已上门次数

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

	public int getRevisit_time() {
		return revisit_time;
	}

	public void setRevisit_time(int revisit_time) {
		this.revisit_time = revisit_time;
	}

	public int getToday_task() {
		return today_task;
	}

	public void setToday_task(int today_task) {
		this.today_task = today_task;
	}

	public int getLast_arrive_time() {
		return last_arrive_time;
	}

	public void setLast_arrive_time(int last_arrive_time) {
		this.last_arrive_time = last_arrive_time;
	}

	public int getContact_time() {
		return contact_time;
	}

	public void setContact_time(int contact_time) {
		this.contact_time = contact_time;
	}

	public int getCreate_time() {
		return create_time;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAbort_type() {
		return abort_type;
	}

	public void setAbort_type(int abort_type) {
		this.abort_type = abort_type;
	}

	public int getCar_dealer() {
		return car_dealer;
	}

	public void setCar_dealer(int car_dealer) {
		this.car_dealer = car_dealer;
	}

	public int getBuyer_level() {
		return buyer_level;
	}

	public void setBuyer_level(int buyer_level) {
		this.buyer_level = buyer_level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public void setWeixin_account(String weixin_account) {
		this.weixin_account = weixin_account;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public void setSubscribe_rule(SubscribeRuleEntity subscribe_rule) {
		this.subscribe_rule = subscribe_rule;
	}

	public void setDemand_count(int demand_count) {
		this.demand_count = demand_count;
	}

	public void setTrans_count(int trans_count) {
		this.trans_count = trans_count;
	}

	public void setRevisit_count(int revisit_count) {
		this.revisit_count = revisit_count;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPhone() {
		return phone;
	}

	public String getName() {
		return name;
	}

	public String getCity_id() {
		return city_id;
	}

	public String getWeixin_account() {
		return weixin_account;
	}

	public String getCity_name() {
		return city_name;
	}

	public SubscribeRuleEntity getSubscribe_rule() {
		return subscribe_rule;
	}

	public int getDemand_count() {
		return demand_count;
	}

	public int getTrans_count() {
		return trans_count;
	}

	public int getRevisit_count() {
		return revisit_count;
	}

	public String getComment() {
		return comment;
	}

	public static class SubscribeRuleEntity {
		/**
		 * subscribe_series : [{"id":"66","name":"宝马3系"},{"id":"314","name":"本田CR-V"}]
		 * emission : 2
		 * gearbox : 0
		 * year : [1,2]
		 * price : [20,22]
		 */

		private int emission;
		private int gearbox;
		private List<SubscribeSeriesEntity> subscribe_series;
		private List<Integer> year;
		private List<Float> price;
		private List<Float> emission_value;
		private List<Integer> vehicle_structure;
		private List<Integer> vehicle_color_type;

		public List<Float> getEmission_value() {
			return emission_value;
		}

		public void setEmission_value(List<Float> emission_value) {
			this.emission_value = emission_value;
		}

		public List<Integer> getVehicle_structure() {
			return vehicle_structure;
		}

		public void setVehicle_structure(List<Integer> vehicle_structure) {
			this.vehicle_structure = vehicle_structure;
		}

		public List<Integer> getVehicle_color_type() {
			return vehicle_color_type;
		}

		public void setVehicle_color_type(List<Integer> vehicle_color_type) {
			this.vehicle_color_type = vehicle_color_type;
		}


		public void setEmission(int emission) {
			this.emission = emission;
		}

		public void setGearbox(int gearbox) {
			this.gearbox = gearbox;
		}

		public void setSubscribe_series(List<SubscribeSeriesEntity> subscribe_series) {
			this.subscribe_series = subscribe_series;
		}

		public void setYear(List<Integer> year) {
			this.year = year;
		}

		public void setPrice(List<Float> price) {
			this.price = price;
		}

		public int getEmission() {
			return emission;
		}

		public int getGearbox() {
			return gearbox;
		}

		public List<SubscribeSeriesEntity> getSubscribe_series() {
			return subscribe_series;
		}

		public List<Integer> getYear() {
			return year;
		}

		public List<Float> getPrice() {
			return price;
		}

		public static class SubscribeSeriesEntity {
			/**
			 * id : 66
			 * name : 宝马3系
			 */

			private String id;
			private String name;

			public void setId(String id) {
				this.id = id;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getId() {
				return id;
			}

			public String getName() {
				return name;
			}
		}
	}
}
