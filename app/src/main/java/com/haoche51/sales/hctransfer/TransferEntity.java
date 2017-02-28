package com.haoche51.sales.hctransfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 过户详情
 * Created by yangming on 2016/1/23.
 */
public class TransferEntity implements Serializable {

	private String id;  //交易id
	private String vehicle_source_id; //车源id
	private String vehicle_name; //车源标题
	private int appointment_starttime; //预约试驾开始时间
	private String place; //试驾地点
	private String buyer_phone; //买家电话
	private String buyer_name; //买家姓名
	private String seller_name; //车主姓名
	private String seller_phone; //车主电话

	private String accompany_name; // 带看人姓名
	private String accompany_phone; // 带看人电话

	private String comment; //备注
	private String transfer_mode; //过户方式 0公司过户 1自行过户
	private String prepay; //买家给公司定金(元)
	private String seller_prepay; //买家给车主定金(元)
	private String seller_company_prepay; //车主给公司定金
	private String price; //成交价(元)
	private String commission; //佣金价格(元)
	private String owner_commission;//车商服务费
	private int transfer_time;//过户时间
	private String transfer_place; //过户地点

	private String ext_info; //APP操作预订时提交的附加信息,json
	private String transfer_amount;  //过户费,为0表示不包过户
	private String transfer_free;  // 是否包过户。0：无、1：有
	private String checker_name;  //检测室姓名
	private String checker_phone; //检测师电话

	private String cjd_url;
	private String cjd_status;
	private String cjd_report_pdf;

	private String transfer_price;// 复检后成交价，单位元
	private String transfer_remission;// 复检后减免金额，单位元
	private String final_payment; // 过户应收金额，单位元

	private String prepay_comment; // // 地销备注

	private String audit_text;              //  当前状态 // 待过户，拒绝过户，过户合同审核中，过户合同审核失败，已过户；
	private int audit_status;    // 1待过户，2拒绝过户，3过户合同审核中，4过户合同审核失败，5已过户
	private String audit_desc;    // 拒绝原因
	private String plate_number;//车牌号
	private String rest_commission; // 剩余服务费，单位元
	private String rest_vehicle_price; // 剩余车款，单位元

	private String transfer_by_company; // 1通过公司转账，0否

	private ArrayList<String> receipt_pic_origin; // 过户照片，不带url
	private ArrayList<String> receipt_pic;  // 过户照片，带url

	private String transaction_number; // 订单编号

	private String transfer_comment; // 过户备注

	private int prepay_time;


	private String prepay_payment_detail;//已收款项json
	private String prepay_vehicle_price;//预定时的预收车款

	private String transfer_vehicle_price;//应付/担保车款
	private String transfer_fee;//手续费，单位元
	private int use_owner = 0;//是否使用车主定金

	private String insurance; // 悟空保
	private String owner_out_payment; // 公司需转车主金额，单位元
	private String vehicle_type_text; // 车源来源
	private int vehicle_type;//// 0 2 普通车 1回购车 3，4寄售车
	private boolean isConsignment;//是否寄售车 本地字段，非crm字段
	private int is_loan;//0不贷款 1贷款
	private int deal_price;//成本价
	private String seller_bank;//开户银行
	private String seller_card;//银行卡号
	private FinFeeInfoEntity fin_fee_info;//过户费信息
	private FinFeeInfoEntity fin_seller_transfer;//转车主信息
	private int other_cost;//其他支出
	private List<String> vehicle_tag;//任务标签（独家、好车好价...）

	public FinFeeInfoEntity getFin_seller_transfer() {
		return fin_seller_transfer;
	}

	public void setFin_seller_transfer(FinFeeInfoEntity fin_seller_transfer) {
		this.fin_seller_transfer = fin_seller_transfer;
	}

	public String getVehicle_type_text() {
		return vehicle_type_text;
	}

	public void setVehicle_type_text(String vehicle_type_text) {
		this.vehicle_type_text = vehicle_type_text;
	}

	public String getOwner_out_payment() {
		return owner_out_payment;
	}

	public void setOwner_out_payment(String owner_out_payment) {
		this.owner_out_payment = owner_out_payment;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getTransfer_vehicle_price() {
		return transfer_vehicle_price;
	}

	public void setTransfer_vehicle_price(String transfer_vehicle_price) {
		this.transfer_vehicle_price = transfer_vehicle_price;
	}

	public String getTransfer_fee() {
		return transfer_fee;
	}

	public void setTransfer_fee(String transfer_fee) {
		this.transfer_fee = transfer_fee;
	}

	public int getUse_owner() {
		return use_owner;
	}

	public void setUse_owner(int use_owner) {
		this.use_owner = use_owner;
	}

	public String getPrepay_vehicle_price() {
		return prepay_vehicle_price;
	}

	public void setPrepay_vehicle_price(String prepay_vehicle_price) {
		this.prepay_vehicle_price = prepay_vehicle_price;
	}

	public String getPrepay_comment() {
		return prepay_comment;
	}

	public void setPrepay_comment(String prepay_comment) {
		this.prepay_comment = prepay_comment;
	}

	public int getPrepay_time() {
		return prepay_time;
	}

	public void setPrepay_time(int prepay_time) {
		this.prepay_time = prepay_time;
	}

	public String getRest_commisson() {
		return rest_commission;
	}

	public void setRest_commisson(String rest_commisson) {
		this.rest_commission = rest_commisson;
	}

	public String getRest_vehicle_price() {
		return rest_vehicle_price;
	}

	public void setRest_vehicle_price(String rest_vehicle_price) {
		this.rest_vehicle_price = rest_vehicle_price;
	}

	public String getPlate_number() {
		return plate_number;
	}

	public void setPlate_number(String plate_number) {
		this.plate_number = plate_number;
	}

	public String getAudit_desc() {
		return audit_desc;
	}

	public void setAudit_desc(String audit_desc) {
		this.audit_desc = audit_desc;
	}

	public int getAudit_status() {
		return audit_status;
	}

	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}

	public String getAudit_text() {
		return audit_text;
	}

	public void setAudit_text(String audit_text) {
		this.audit_text = audit_text;
	}

	public void setConsignment(boolean consignment) {
		isConsignment = consignment;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVehicle_source_id() {
		return vehicle_source_id;
	}

	public void setVehicle_source_id(String vehicle_source_id) {
		this.vehicle_source_id = vehicle_source_id;
	}

	public String getVehicle_name() {
		return vehicle_name;
	}

	public void setVehicle_name(String vehicle_name) {
		this.vehicle_name = vehicle_name;
	}

	public int getAppointment_starttime() {
		return appointment_starttime;
	}

	public void setAppointment_starttime(int appointment_starttime) {
		this.appointment_starttime = appointment_starttime;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTransfer_mode() {
		return transfer_mode;
	}

	public void setTransfer_mode(String transfer_mode) {
		this.transfer_mode = transfer_mode;
	}

	public String getPrepay() {
		return prepay;
	}

	public void setPrepay(String prepay) {
		this.prepay = prepay;
	}

	public String getSeller_prepay() {
		return seller_prepay;
	}

	public void setSeller_prepay(String seller_prepay) {
		this.seller_prepay = seller_prepay;
	}

	public String getSeller_company_prepay() {
		return seller_company_prepay;
	}

	public void setSeller_company_prepay(String seller_company_prepay) {
		this.seller_company_prepay = seller_company_prepay;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public int getTransfer_time() {
		return transfer_time;
	}

	public void setTransfer_time(int transfer_time) {
		this.transfer_time = transfer_time;
	}

	public String getTransfer_place() {
		return transfer_place;
	}

	public void setTransfer_place(String transfer_place) {
		this.transfer_place = transfer_place;
	}

	public String getExt_info() {
		return ext_info;
	}

	public void setExt_info(String ext_info) {
		this.ext_info = ext_info;
	}

	public String getTransfer_amount() {
		return transfer_amount;
	}

	public void setTransfer_amount(String transfer_amount) {
		this.transfer_amount = transfer_amount;
	}

	public String getTransfer_free() {
		return transfer_free;
	}

	public void setTransfer_free(String transfer_free) {
		this.transfer_free = transfer_free;
	}

	public String getChecker_name() {
		return checker_name;
	}

	public void setChecker_name(String checker_name) {
		this.checker_name = checker_name;
	}

	public String getChecker_phone() {
		return checker_phone;
	}

	public void setChecker_phone(String checker_phone) {
		this.checker_phone = checker_phone;
	}

	public String getCjd_url() {
		return cjd_url;
	}

	public void setCjd_url(String cjd_url) {
		this.cjd_url = cjd_url;
	}

	public String getCjd_status() {
		return cjd_status;
	}

	public void setCjd_status(String cjd_status) {
		this.cjd_status = cjd_status;
	}

	public String getCjd_report_pdf() {
		return cjd_report_pdf;
	}

	public void setCjd_report_pdf(String cjd_report_pdf) {
		this.cjd_report_pdf = cjd_report_pdf;
	}

	public String getTransfer_price() {
		return transfer_price;
	}

	public void setTransfer_price(String transfer_price) {
		this.transfer_price = transfer_price;
	}

	public String getTransfer_remission() {
		return transfer_remission;
	}

	public void setTransfer_remission(String transfer_remission) {
		this.transfer_remission = transfer_remission;
	}

	public String getFinal_payment() {
		return final_payment;
	}

	public void setFinal_payment(String final_payment) {
		this.final_payment = final_payment;
	}

	public String getTransfer_by_company() {
		return transfer_by_company;
	}

	public void setTransfer_by_company(String transfer_by_company) {
		this.transfer_by_company = transfer_by_company;
	}

	public ArrayList<String> getReceipt_pic_origin() {
		return receipt_pic_origin;
	}

	public void setReceipt_pic_origin(ArrayList<String> receipt_pic_origin) {
		this.receipt_pic_origin = receipt_pic_origin;
	}

	public ArrayList<String> getReceipt_pic() {
		return receipt_pic;
	}

	public void setReceipt_pic(ArrayList<String> receipt_pic) {
		this.receipt_pic = receipt_pic;
	}

	public String getTransaction_number() {
		return transaction_number;
	}

	public void setTransaction_number(String transaction_number) {
		this.transaction_number = transaction_number;
	}

	public String getAccompany_name() {
		return accompany_name;
	}

	public void setAccompany_name(String accompany_name) {
		this.accompany_name = accompany_name;
	}

	public String getAccompany_phone() {
		return accompany_phone;
	}

	public void setAccompany_phone(String accompany_phone) {
		this.accompany_phone = accompany_phone;
	}

	public String getTransfer_comment() {
		return transfer_comment;
	}

	public void setTransfer_comment(String transfer_comment) {
		this.transfer_comment = transfer_comment;
	}

	public String getPrepay_payment_detail() {
		return prepay_payment_detail;
	}

	public void setPrepay_payment_detail(String prepay_payment_detail) {
		this.prepay_payment_detail = prepay_payment_detail;
	}

	public String getRest_commission() {
		return rest_commission;
	}

	public void setRest_commission(String rest_commission) {
		this.rest_commission = rest_commission;
	}

	public int getVehicle_type() {
		return vehicle_type;
	}

	public void setVehicle_type(int vehicle_type) {
		this.vehicle_type = vehicle_type;
	}

	public int getIs_loan() {
		return is_loan;
	}

	public void setIs_loan(int is_loan) {
		this.is_loan = is_loan;
	}

	public boolean isConsignment() {
		isConsignment = false;
		switch (getVehicle_type()) {
			case 3:
			case 4:
				isConsignment = true;
				break;
		}
		return isConsignment;
	}

	public int getDeal_price() {
		return deal_price;
	}

	public void setDeal_price(int deal_price) {
		this.deal_price = deal_price;
	}

	public String getSeller_bank() {
		return seller_bank;
	}

	public void setSeller_bank(String seller_bank) {
		this.seller_bank = seller_bank;
	}

	public String getSeller_card() {
		return seller_card;
	}

	public void setSeller_card(String seller_card) {
		this.seller_card = seller_card;
	}

	public class FinFeeInfoEntity implements Serializable {
		private String trade_amount;//转账金额
		private String card_name;//过户费开户姓名
		private String card_bank;        // 过户费开户行
		private String card_number;        // 过户费银行卡号
		private int trade_status;     // 过户费转款状态：0未提交  10未确认  20通过  40拒绝
		private String status_text;      // '过户费转款拒绝'
		private String reason;           // 财务拒绝原因

		public String getTrade_amount() {
			return trade_amount;
		}

		public void setTrade_amount(String trade_amount) {
			this.trade_amount = trade_amount;
		}

		public String getCard_bank() {
			return card_bank;
		}

		public void setCard_bank(String card_bank) {
			this.card_bank = card_bank;
		}

		public String getCard_number() {
			return card_number;
		}

		public void setCard_number(String card_card) {
			this.card_number = card_card;
		}

		public String getCard_name() {
			return card_name;
		}

		public void setCard_name(String card_name) {
			this.card_name = card_name;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public String getStatus_text() {
			return status_text;
		}

		public void setStatus_text(String status_text) {
			this.status_text = status_text;
		}

		public int getTrade_status() {
			return trade_status;
		}

		public void setTrade_status(int trade_status) {
			this.trade_status = trade_status;
		}
	}

	public FinFeeInfoEntity getFin_fee_info() {
		return fin_fee_info;
	}

	public void setFin_fee_info(FinFeeInfoEntity fin_transfer_info) {
		this.fin_fee_info = fin_transfer_info;
	}

	public int getOther_cost() {
		return other_cost;
	}

	public void setOther_cost(int other_cost) {
		this.other_cost = other_cost;
	}

	public List<String> getVehicle_tag() {
		return vehicle_tag;
	}

	public void setVehicle_tag(List<String> vehicle_tag) {
		this.vehicle_tag = vehicle_tag;
	}

	public String getOwner_commission() {
		return owner_commission;
	}

	public void setOwner_commission(String owner_commission) {
		this.owner_commission = owner_commission;
	}
}
