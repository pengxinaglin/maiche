package com.haoche51.sales.hctransaction;

import android.text.TextUtils;

import com.haoche51.sales.hctransfer.TransferEntity;
import com.haoche51.sales.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionTaskEntity implements Serializable {


	/**
	 * id : 1095
	 * vehicle_source_id : 20030
	 * vehicle_name : 福迪 探索者Ⅱ 2010款 2.2L 手动标准型
	 * appointment_starttime : 1443613320
	 * place : 北京市昌平区回龙观东大街-地铁站
	 * buyer_phone : 13520173050
	 * buyer_name : 张先生
	 * seller_name : 何先生
	 * seller_phone : 13810312234
	 * comment :
	 * transfer_mode : 0
	 * trans_free
	 * prepay : 0
	 * seller_prepay : 0
	 * cash_back : 0
	 * price : 0
	 * commission : 0
	 * transfer_time : 0
	 * transfer_place :
	 * ext_info :
	 * city_id : 12
	 * status : 0
	 * seller_company_prepay : 0
	 * cancel_trans_status : 0
	 * saler_phone : null
	 * saler_name : null
	 * seller_price : 14
	 * eval_price : 12
	 * cheap_price : 0
	 * checker_name : 测试评估师
	 * online_time : 1440123450
	 * che300_price :
	 * checker_phone : 18611080804
	 * view_registration : 1
	 * cjd_url :
	 * cjd_status : 0
	 * trans_count : 10
	 * trans_list : [{"vehicle_name":"宝马3系 2010款 320i 时尚型"},{"vehicle_name":"福迪 探索者Ⅱ 2010款 2.2L 手动标准型"}]
	 * buyer_revisit_count : 26
	 * buyer_revisit : [{"content":"123"},{"content":"推荐车源编号1234"}]
	 * seller_revisit_count : 10
	 * seller_revisit : [{"record":"车源重新上线"},{"record":"车源重新上线"}]
	 */
	private String id;
	private String vehicle_source_id;
	private String vehicle_name;
	private int appointment_starttime;
	private String place;
	private String buyer_phone;
	private String buyer_name;
	private String seller_name;
	private String seller_phone;
	private String comment;
	private int transfer_mode;
	private String prepay;/*买家给公司定金*/
	private String seller_prepay;/*买家给车主定金*/
	private String price;
	private String commission;//实际服务费
	private String owner_commission;//车商服务费
	private int transfer_time;
	private String transfer_place;
	private String ext_info;
	private int status;
	private String seller_company_prepay;/*车主给公司定金*/
	private int cancel_trans_status;
	private String saler_phone;
	private String saler_name;
	private String seller_price;
	private String eval_price;
	private String eval_price_21;

	private String cheap_price;
	private String checker_name;
	private int online_time;
	private String checker_phone;
	private String cjd_url;
	private int cjd_status;
	private String abort_reason;
	private String plate_number;//车牌号码
	private int type;
	private String seller_bank;
	private String seller_card;
	private List<String> vehicle_tag;//任务标签（独家、好车好价...）

	/**
	 * new
	 */
	private int transfer_free = 0;//公司包过户1 不包过户0
	private CancelTransInfoEntity cancel_trans_info;
	private Coupon award_info;//大礼包

	private String check_report_url;
	private String check_report_pdf;
	private String cjd_report_pdf;

	private String audit_desc;
	private String prepay_comment;//地销预约备注
	private int eval_price_dealer;//车商价
	private int audit_status = 0;//审核状态(看车成功列展示状态) 审核状态. 1合同审核中, 2合同审核未通过, 3上传转账截图, 4预定审核中, 5预定审核失败, 6预定收款完成, 7交易完成, 8毁约待确认, 9已毁约
	private String audit_text;//审核状态(文案)

	/**
	 * 过户相关
	 */
	private String manager_remission;//减免金额
	private ArrayList<String> contract_pic;//合同和买卖家照片信息
	private ArrayList<String> prepay_transfer_pic;//转账照片信息
	private ArrayList<String> contract_pic_origin;//合同和买卖家照片信息
	private List<String> prepay_transfer_pic_origin;//转账照片信息
	private String transfer_comment;//过户备注信息
	private CouponEntity coupon_info;//优惠券
	private String commission_info;//差价
	private String order_number;
	private String theory_commission;//理论服务费

	private String prepay_payment; //预定买家应收款，单位元
	// 买家付公司车款// 预收车款，单位元 也是担保车款
	private String prepay_vehicle_price;
	private String transfer_vehicle_price;//过户时应付/担保车款
	private String diff_prepay_vehicle_price;//应付车款差价
	// 买家刷卡手续费
	private int prepay_fee;
	private int car_dealer = -1;//是否是车商 1是 0否
	private String insurance;//悟空服务卡

	private String vin_code;//悟空服务卡
	private String buyer_identify;//悟空服务卡

	private String buyer_payment;//买家已付
	private String vehicle_type_text;//车源来源

	private int warranty_type;//质保类型 0:无质保 1：公司质保 2：第三方质保
	private int warranty_time;//质保到期时间，unix时间戳，如：1465889676

	private int vehicle_type;// 0，2正常 1回购 3，4寄售车
	private int task_type;//0：户外，1：展厅
	private boolean isConsignment;//是否是寄售车
	private boolean isCommon;//是否是普通车
	private int deal_price;// 成本价
	private FinFeeInfoEntity fin_cancel_seller;
	private FinFeeInfoEntity fin_cancel_buyer;
	private String rest_commission; // 剩余服务费，单位元
	private String transfer_remission;// 复检后减免金额，单位元
	private int use_owner = 0;//是否使用车主定金
	private TransferEntity.FinFeeInfoEntity fin_seller_transfer;//转车主信息
	private int other_cost;//其他支出
	private TransferEntity.FinFeeInfoEntity fin_other_info;//转其他支出

	public FinFeeInfoEntity getFin_cancel_seller() {
		return fin_cancel_seller;
	}

	public void setFin_cancel_seller(FinFeeInfoEntity fin_cancel_seller) {
		this.fin_cancel_seller = fin_cancel_seller;
	}

	public FinFeeInfoEntity getFin_cancel_buyer() {
		return fin_cancel_buyer;
	}

	public void setFin_cancel_buyer(FinFeeInfoEntity fin_cancel_buyer) {
		this.fin_cancel_buyer = fin_cancel_buyer;
	}

	public String getVehicle_type_text() {
		return vehicle_type_text;
	}

	public void setVehicle_type_text(String vehicle_type_text) {
		this.vehicle_type_text = vehicle_type_text;
	}

	public String getBuyer_payment() {
		return buyer_payment;
	}

	public void setBuyer_payment(String buyer_payment) {
		this.buyer_payment = buyer_payment;
	}

	public String getVin_code() {
		return vin_code;
	}

	public void setVin_code(String vin_code) {
		this.vin_code = vin_code;
	}

	public String getBuyer_identify() {
		return buyer_identify;
	}

	public void setBuyer_identify(String buyer_identify) {
		this.buyer_identify = buyer_identify;
	}

	/**
	 * 悟空服务卡
	 */
	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	/**
	 * 买家刷卡手续费
	 */
	public void setPrepay_fee(int prepay_fee) {
		this.prepay_fee = prepay_fee;
	}

	public int getPrepay_fee() {
		return prepay_fee;
	}

	public int getCar_dealer() {
		return car_dealer;
	}

	public void setCar_dealer(int car_dealer) {
		this.car_dealer = car_dealer;
	}


	public String getPrepay_payment() {
		return prepay_payment;
	}

	public void setPrepay_payment(String prepay_payment) {
		this.prepay_payment = prepay_payment;
	}


	public String getPrepay_vehicle_price() {
		return prepay_vehicle_price;
	}

	public void setPrepay_vehicle_price(String prepay_vehicle_price) {
		this.prepay_vehicle_price = prepay_vehicle_price;
	}

	public String getTheory_commission() {
		return theory_commission;
	}

	public void setTheory_commission(String theory_commission) {
		this.theory_commission = theory_commission;
	}

	public ArrayList<String> getContract_pic_origin() {
		return contract_pic_origin;
	}

	public void setContract_pic_origin(ArrayList<String> contract_pic_origin) {
		this.contract_pic_origin = contract_pic_origin;
	}

	public List<String> getPrepay_transfer_pic_origin() {
		return prepay_transfer_pic_origin;
	}

	public void setPrepay_transfer_pic_origin(List<String> prepay_transfer_pic_origin) {
		this.prepay_transfer_pic_origin = prepay_transfer_pic_origin;
	}

	public String getOrder_number() {
		return order_number;
	}

	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}

	public String getCommission_info() {
		return commission_info;
	}

	public void setCommission_info(String commission_info) {
		this.commission_info = commission_info;
	}

	public String getAudit_desc() {
		return audit_desc;
	}

	public void setAudit_desc(String audit_desc) {
		this.audit_desc = audit_desc;
	}

	public String getPrepay_comment() {
		return prepay_comment;
	}

	public void setPrepay_comment(String prepay_comment) {
		this.prepay_comment = prepay_comment;
	}

	public int getEval_price_dealer() {
		return eval_price_dealer;
	}

	public void setEval_price_dealer(int eval_price_dealer) {
		this.eval_price_dealer = eval_price_dealer;
	}

	public int getAudit_status() {
		return audit_status;
	}

	public void setAudit_status(int audit_status) {
		this.audit_status = audit_status;
	}

	public ArrayList<String> getPrepay_transfer_pic() {
		return prepay_transfer_pic;
	}

	public void setPrepay_transfer_pic(ArrayList<String> prepay_transfer_pic) {
		this.prepay_transfer_pic = prepay_transfer_pic;
	}

	public String getManager_remission() {
		return manager_remission;
	}

	public void setManager_remission(String manager_remission) {
		this.manager_remission = manager_remission;
	}

	public List<String> getContract_pic() {
		return contract_pic;
	}

	public void setContract_pic(ArrayList<String> contract_pic) {
		this.contract_pic = contract_pic;
	}

	public String getTransfer_comment() {
		return transfer_comment;
	}

	public void setTransfer_comment(String transfer_comment) {
		this.transfer_comment = transfer_comment;
	}

	public CouponEntity getCoupon_info() {
		return coupon_info;
	}

	public void setCoupon_info(CouponEntity coupon_info) {
		this.coupon_info = coupon_info;
	}

	public String getCheck_report_url() {
		return check_report_url;
	}

	public void setCheck_report_url(String check_report_url) {
		this.check_report_url = check_report_url;
	}

	public String getCheck_report_pdf() {
		return check_report_pdf;
	}

	public void setCheck_report_pdf(String check_report_pdf) {
		this.check_report_pdf = check_report_pdf;
	}

	public String getCjd_report_pdf() {
		return cjd_report_pdf;
	}

	public void setCjd_report_pdf(String cjd_report_pdf) {
		this.cjd_report_pdf = cjd_report_pdf;
	}

	public String getEval_price_21() {
		return eval_price_21;
	}

	public void setEval_price_21(String eval_price_21) {
		this.eval_price_21 = eval_price_21;
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

	public int getTransfer_mode() {
		return transfer_mode;
	}

	public void setTransfer_mode(int transfer_mode) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSeller_company_prepay() {
		return seller_company_prepay;
	}

	public void setSeller_company_prepay(String seller_company_prepay) {
		if (!TextUtils.isEmpty(seller_company_prepay))
			this.seller_company_prepay = String.valueOf(Integer.parseInt(seller_company_prepay));//防止输入00X、000X、000X...
		else
			this.seller_company_prepay = seller_company_prepay;
	}

	public int getCancel_trans_status() {
		return cancel_trans_status;
	}

	public void setCancel_trans_status(int cancel_trans_status) {
		this.cancel_trans_status = cancel_trans_status;
	}

	public String getSaler_phone() {
		return saler_phone;
	}

	public void setSaler_phone(String saler_phone) {
		this.saler_phone = saler_phone;
	}

	public String getSaler_name() {
		return saler_name;
	}

	public void setSaler_name(String saler_name) {
		this.saler_name = saler_name;
	}

	public String getSeller_price() {
		return seller_price;
	}

	public void setSeller_price(String seller_price) {
		this.seller_price = seller_price;
	}

	public String getEval_price() {
		return eval_price;
	}

	public void setEval_price(String eval_price) {
		this.eval_price = eval_price;
	}

	public String getCheap_price() {
		return cheap_price;
	}

	public void setCheap_price(String cheap_price) {
		this.cheap_price = cheap_price;
	}

	public String getChecker_name() {
		return checker_name;
	}

	public void setChecker_name(String checker_name) {
		this.checker_name = checker_name;
	}

	public int getOnline_time() {
		return online_time;
	}

	public void setOnline_time(int online_time) {
		this.online_time = online_time;
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

	public int getCjd_status() {
		return cjd_status;
	}

	public void setCjd_status(int cjd_status) {
		this.cjd_status = cjd_status;
	}

	public String getAbort_reason() {

		return abort_reason;
	}

	public void setAbort_reason(String abort_reason) {
		this.abort_reason = abort_reason;
	}

	public CancelTransInfoEntity getCancel_trans_info() {
		return cancel_trans_info;
	}

	public void setCancel_trans_info(CancelTransInfoEntity cancel_trans_info) {
		this.cancel_trans_info = cancel_trans_info;
	}

	public Coupon getAward_info() {
		return award_info;
	}

	public void setAward_info(Coupon award_info) {
		this.award_info = award_info;
	}

	public int getTransfer_free() {
		return transfer_free;
	}

	public void setTransfer_free(int transfer_free) {
		this.transfer_free = transfer_free;
	}

	public String getPlate_number() {
		return plate_number;
	}

	public void setPlate_number(String plate_number) {
		this.plate_number = plate_number;
	}

	public static class CancelTransInfoEntity implements Serializable {
		/**
		 * type : buyer
		 * owner_prepay : 1000
		 * buyer_prepay : 1000
		 * reason : rrr
		 * comment : vvvv
		 * vehicle_status : 0
		 */

		private String type;
		private String owner_prepay;
		private String buyer_prepay;
		private String reason;
		private String comment;//'买家退款方式',
		private String vehicle_status;//车源操作
		private String owner_comment;//'车主退款方式',

		public String getOwner_comment() {
			return owner_comment;
		}

		public void setOwner_comment(String owner_comment) {
			this.owner_comment = owner_comment;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getOwner_prepay() {
			return owner_prepay;
		}

		public void setOwner_prepay(String owner_prepay) {
			this.owner_prepay = owner_prepay;
		}

		public String getBuyer_prepay() {
			return buyer_prepay;
		}

		public void setBuyer_prepay(String buyer_prepay) {
			this.buyer_prepay = buyer_prepay;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getVehicle_status() {
			return vehicle_status;
		}

		public void setVehicle_status(String vehicle_status) {
			this.vehicle_status = vehicle_status;
		}
	}

	public int getWarranty_type() {
		return warranty_type;
	}

	public void setWarranty_type(int warranty_type) {
		this.warranty_type = warranty_type;
	}

	public int getWarranty_time() {
		return warranty_time;
	}

	public void setWarranty_time(int warranty_time) {
		this.warranty_time = warranty_time;
	}

	public int getDeal_price() {
		return deal_price;
	}

	public void setDeal_price(int deal_price) {
		this.deal_price = deal_price;
	}

	public int getVehicle_type() {
		return vehicle_type;
	}

	public void setVehicle_type(int vehicle_type) {
		this.vehicle_type = vehicle_type;
	}

	public boolean isConsignment() {
		switch (getVehicle_type()) {
			case 3:
			case 4:
				setConsignment(true);
				break;
			default:
				setConsignment(false);
				break;
		}
		return isConsignment;
	}

	public boolean isCommon() {
		switch (getVehicle_type()) {
			case 0:
			case 2:
				setCommon(true);
				break;
			default:
				setCommon(false);
				break;
		}
		return isCommon;
	}

	public void setConsignment(boolean consignment) {
		isConsignment = consignment;
	}

	public void setCommon(boolean common) {
		isCommon = common;
	}

	public String getAudit_text() {
		return audit_text;
	}

	public void setAudit_text(String audit_text) {
		this.audit_text = audit_text;
	}

	public String getTransfer_vehicle_price() {
		return transfer_vehicle_price;
	}

	public void setTransfer_vehicle_price(String transfer_vehicle_price) {
		this.transfer_vehicle_price = transfer_vehicle_price;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTask_type() {
		return task_type;
	}

	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}

	public static class FinFeeInfoEntity implements Serializable {
		private String trade_amount;//转账金额（单位分）
		private String card_name;//过户费开户姓名
		private String card_bank;// 过户费开户行
		private String card_number;// 过户费银行卡号
		private int trade_status;// 过户费转款状态：0未提交  10未确认  20通过  40拒绝
		private String status_text;// '过户费转款拒绝'
		private String reason;// 财务拒绝原因

		public String getTrade_amount() {
			try {
				return String.valueOf(StringUtil.parseInt(trade_amount, 0) / 100);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public String getRest_commission() {
		return rest_commission;
	}

	public void setRest_commission(String rest_commission) {
		this.rest_commission = rest_commission;
	}

	public String getTransfer_remission() {
		return transfer_remission;
	}

	public void setTransfer_remission(String transfer_remission) {
		this.transfer_remission = transfer_remission;
	}

	public int getUse_owner() {
		return use_owner;
	}

	public void setUse_owner(int use_owner) {
		this.use_owner = use_owner;
	}

	public TransferEntity.FinFeeInfoEntity getFin_seller_transfer() {
		return fin_seller_transfer;
	}

	public void setFin_seller_transfer(TransferEntity.FinFeeInfoEntity fin_seller_transfer) {
		this.fin_seller_transfer = fin_seller_transfer;
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

	public int getOther_cost() {
		return other_cost;
	}

	public void setOther_cost(int other_cost) {
		this.other_cost = other_cost;
	}

	public TransferEntity.FinFeeInfoEntity getFin_other_info() {
		return fin_other_info;
	}

	public void setFin_other_info(TransferEntity.FinFeeInfoEntity fin_other_info) {
		this.fin_other_info = fin_other_info;
	}

	public String getDiff_prepay_vehicle_price() {
		return diff_prepay_vehicle_price;
	}

	public void setDiff_prepay_vehicle_price(String diff_prepay_vehicle_price) {
		this.diff_prepay_vehicle_price = diff_prepay_vehicle_price;
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
