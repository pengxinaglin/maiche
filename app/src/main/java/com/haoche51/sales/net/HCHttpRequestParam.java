package com.haoche51.sales.net;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.haoche51.record.entity.CallLogEntity;
import com.haoche51.record.entity.PhoneRecordEntity;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.User;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.entity.ExtInfo;
import com.haoche51.sales.entity.HCBDloactionEntity;
import com.haoche51.sales.hcrecommend.CheckCarClueEntity;
import com.haoche51.sales.hcrecommend.PurchaseClueEntity;
import com.haoche51.sales.hctransaction.RefundInfoEntity;
import com.haoche51.sales.hctransaction.TransactionFitlerEntity;
import com.haoche51.sales.hctransaction.TransactionReadyInfo;
import com.haoche51.sales.hctransaction.TransactionTaskEntity;
import com.haoche51.sales.hctransaction.VehicleSeriesEntity;
import com.haoche51.sales.hctransaction.VehicleSubscribeRuleEntity;
import com.haoche51.sales.util.DESUtil;
import com.haoche51.sales.util.DeviceInfoUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.NetInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络请求参数配置
 * Created by xuhaibo on 15/8/25.
 */
public class HCHttpRequestParam {
	public static final String TAG = "HCHttpRequestParam";
	public static final String TOKEN = "sales_app_20160517";
	private static Gson mGson = new Gson();


	/**
	 * login
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public static Map<String, Object> loginForEncode(String username, String password
			, String device_id, String device_name, String os_v, String app_v) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("pwd", password);
//    params.put("password", password);
		params.put("device_id", device_id);
		params.put("device_name", device_name);
		params.put("os_v", os_v);
		params.put("app_v", app_v);
		return getRequest(HttpConstants.ACTION_LOGIN_ENCODE, params);
	}

	/**
	 * 获取地销薪资信息
	 *
	 * @return
	 */
	public static Map<String, Object> getSubsidyInfo() {
		Map<String, Object> params = new HashMap<String, Object>();

		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> user_info = new HashMap<>();
		user_info.put("id", user.getId());
		user_info.put("name", user.getName());
		params.put("user_info", user_info);

		params.put("username", user.getUsername());
		params.put("pwd", GlobalData.userDataHelper.getLoginPwd());
		return getRequest(HttpConstants.ACTION_GETSUBSIDYINFO, params);
	}

	/**
	 * 获取在线城市
	 *
	 * @return
	 */
	public static Map<String, Object> getOnlineCity() {
		Map<String, Object> params = new HashMap<String, Object>();

		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> user_info = new HashMap<>();
		user_info.put("id", user.getId());
		user_info.put("name", user.getName());

		params.put("user_info", user_info);
		return getRequest(HttpConstants.ACTION_GET_ONLINE_CITY, params);
	}

	/**
	 * 修改密码
	 *
	 * @param oldPwd
	 * @param newPwd
	 * @return
	 */
	public static Map<String, Object> changePwd(String oldPwd, String newPwd) {
		Map<String, Object> params = new HashMap<String, Object>();
		int checkerId = GlobalData.userDataHelper.getUser().getId();
		params.put("employee_id", checkerId);
		params.put("old_passwd", oldPwd);
		params.put("new_passwd", newPwd);
		return getRequest(HttpConstants.ACTION_CHANGE_PWD, params);
	}

	/**
	 * 获取登录用户权限
	 *
	 * @return
	 */
	public static Map<String, Object> getUserRight() {
		Map<String, Object> params = new HashMap<>();
		params.put("employee_id", GlobalData.userDataHelper.getUser().getId());//user ID
		return getRequest(HttpConstants.ACTION_GET_USER_RIGHT, params);
	}

	/**
	 * 绑定百度推送
	 */
	public static Map<String, Object> bind(String userId, String channelId) {
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", checker_id);
		params.put("bd_user_id", userId);
		params.put("bd_channel_id", channelId);
		return getRequest(HttpConstants.ACTION_BIND_BAIDU_PUSH, params);
	}

	/**
	 * 取消百度推送绑定
	 *
	 * @return
	 */
	public static Map<String, Object> unbind() {
		int pushId = GlobalData.userDataHelper.getPushId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("push_id", pushId);
		return getRequest(HttpConstants.ACTION_UNBIND_BAIDUPUSH, params);
	}


	/**
	 * 上传百度定位
	 *
	 * @param entity
	 * @return
	 */
	public static Map<String, Object> getBaiduUploadParams(HCBDloactionEntity entity) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", GlobalData.userDataHelper.getLastCheckerId());
		params.put("check_user_name", GlobalData.userDataHelper.getLastCheckerName());
		params.put("longitude", entity.getLongitude());
		params.put("latitude", entity.getLatitude());
		long time = System.currentTimeMillis() / 1000;
		params.put("create_time", time);
		return getRequest(HttpConstants.ACTION_UPLOAD_CHECKER_POSITION, params);
	}

	/**
	 * 获取报告详情
	 */
	public static Map<String, Object> getReportDetail(int pageIndex, int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", checker_id);
		String crm_user_name = GlobalData.userDataHelper.getUser().getName();
		params.put("crm_user_name", crm_user_name);
		params.put("page", pageIndex);
		params.put("limit", pageSize);
		return getRequest(HttpConstants.ACTION_GET_REPORT_DETAIL, params);
	}

	/**
	 * 添加汇报
	 */
	public static Map<String, Object> addReport(long createTime,
	                                            String content, String place, String placeLng, String placeLat, String photo) {
		Map<String, Object> params = new HashMap<String, Object>();
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", checker_id);
		String crm_user_name = GlobalData.userDataHelper.getUser().getName();
		params.put("crm_user_name", crm_user_name);
		params.put("create_time", createTime);
		params.put("content", content);
		params.put("place", place);
		params.put("place_lng", placeLng);
		params.put("place_lat", placeLat);
		params.put("photo", photo);
		return getRequest(HttpConstants.ACTION_ADD_REPORT, params);
	}

	/**
	 * 根据条件获取地销列表
	 */
	public static Map<String, Object> getUserListByFilter() {
		Map<String, Object> params = new HashMap<String, Object>();
		User user = GlobalData.userDataHelper.getUser();
		params.put("groups", user.getGroup());
		params.put("city", user.getCity());

		return getRequest(HttpConstants.ACTION_GET_USERLIST_BYFILTER, params);
	}

	/**
	 * 加带看
	 */
	public static Map<String, Object> takeLook(String buyer_phone,
	                                           String buyer_name, String vehicle_source_id, long appointment_starttime) {
		Map<String, Object> params = new HashMap<String, Object>();
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", checker_id);
		params.put("buyer_phone", buyer_phone);
		params.put("buyer_name", buyer_name);
		params.put("vehicle_source_id", vehicle_source_id);
		params.put("appointment_starttime", appointment_starttime);
		return getRequest(HttpConstants.ACTION_MAKE_LOOK, params);
	}

	/**
	 * 展厅带看
	 */
	public static Map<String, Object> makeAppointment(String exhibitionTaskId, int accompanyUserId, String buyerPhone
			, String buyerName, String vehicleSourceId, int appointmentStarttime, int transSource, String address) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("exhibition_task_id", exhibitionTaskId);
		params.put("crm_user_id", user.getId());
		params.put("accompany_user_id", accompanyUserId);
		params.put("buyer_phone", buyerPhone);
		params.put("buyer_name", buyerName);
		params.put("vehicle_source_id", vehicleSourceId);
		params.put("appointment_starttime", appointmentStarttime);
		params.put("trans_source", transSource);
		params.put("address", address);

		return getRequest(HttpConstants.ACTION_MAKE_APPOINTMENT, params);
	}

	/**
	 * 添加到店记录
	 */
	public static Map<String, Object> addExhibitionRecordNew(String taskId, String content, int level
			, int arrive_status, int arrive_time, int due_time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("task_id", taskId);
		params.put("content", content);
		params.put("level", level);
		params.put("arrive_status", arrive_status);
		params.put("arrive_time", arrive_time);
		if (due_time == 0) {
			params.put("due_time", -1);
		} else {
			params.put("due_time", due_time);
		}

		Map<String, Object> userInfo = new HashMap<String, Object>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());

		params.put("user_info", userInfo);

		return getRequest(HttpConstants.ACTION_EDITEXHIBITIONTRANS, params);
	}

	/**
	 * 获取个人每月分享统计接口
	 */
	public static Map<String, Object> getSelfShareStat(int check_user_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", check_user_id);
		return getRequest(HttpConstants.ACTION_GET_SELF_SHARE_STAT, params);
	}

	/**
	 * 获取分享总量
	 */
	public static Map<String, Object> getShareStat() {
		Map<String, Object> params = new HashMap<String, Object>();
		return getRequest(HttpConstants.ACTION_GET_SHARE_STAT, params);
	}

	/**
	 * 获取七牛upload token
	 */
	public static Map<String, Object> getUploadToken() {
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", checker_id);
		return getRequest(HttpConstants.ACTION_GET_QINIU_TOKEN, params);
	}


	/**
	 * 获取增量车型信息
	 *
	 * @return
	 */
	public static Map<String, Object> fetchIncrementModel(int model_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicle_model_id", model_id);
		return getRequest(HttpConstants.ACTION_FECTH_INCREMENTMODEL, params);
	}


	/**
	 * 获取消息列表信息
	 */
	public static Map<String, Object> getMessageList(int page, int pageSize) {
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", checker_id);
		params.put("page", page);
		params.put("page_size", pageSize);
		return getRequest(HttpConstants.ACTION_GET_MESSAGE, params);
	}

	/**
	 * 获取消息列表信息
	 */
	public static Map<String, Object> readMsg(int id) {
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", checker_id);
		params.put("id", id);
		return getRequest(HttpConstants.ACTION_READ_MSG, params);
	}

	/**
	 * 获取消息列表信息
	 */
	public static Map<String, Object> getUrmsgCount() {
		int checker_id = GlobalData.userDataHelper.getUser().getId();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", checker_id);
		return getRequest(HttpConstants.ACTION_GET_URMSG_COUNT, params);
	}

	/**
	 * 获得Wap登录所需要的Token
	 * appCode
	 * “store”  获取登录门店系统的 token
	 * “crm“    获取登录crm 的token (默认)
	 */
	public static Map<String, Object> getLoginToken(String name) {
		String username = GlobalData.userDataHelper.getLoginName();
		String pwd = GlobalData.userDataHelper.getLoginPwd();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("pwd", pwd);
		if (!TextUtils.isEmpty(name)) {
			params.put("appCode", name);
		}
		return getRequest(HttpConstants.ACTION_GET_LOGIN_TOKEKN, params);
	}

	/**
	 * 通知，推荐车源
	 */
	public static Map<String, Object> getSubVehicleList(int pageIndex, int pageSize, String order) {

		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<>();
		where.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		params.put("where", where);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		if (TextUtils.isEmpty(order))
			order = "create_time desc";
		params.put("order", order);
		return getRequest(HttpConstants.ACTION_GET_SUB_VEHICLE_LIST, params);
	}


	/**
	 * 获取匹配客户列表
	 *
	 * @return
	 */
	public static Map<String, Object> getMatchCusotmerList(int pageIndex, int pageSize, String order, int vehicleSourceId) {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> where = new HashMap<>();
		where.put("vehicle_source_id", vehicleSourceId);
		where.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		params.put("where", where);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		if (TextUtils.isEmpty(order))
			order = "create_time desc";
		params.put("order", order);
		return getRequest(HttpConstants.ACTION_GET_VEHICLE_MATCHED_BUYER_LIST, params);
	}


	///////////////////////////////////// 过户 开始 /////////////////////////////////////////////////////

	/**
	 * 获取过户列表（新版）/getList
	 *
	 * @param tab     1, 2, 3分别代表3个tab;1:待过户；2：审核中；3：已过户；
	 * @param keyword 关键字查询
	 * @param page    拉获取第几页数据，从0开始
	 * @param limit   每页获取多少数据，默认10条
	 * @return map
	 */
	public static Map<String, Object> transferList(int tab, String keyword, int page, int limit) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("page", page);
		params.put("limit", limit);
		params.put("tab", tab);
		params.put("keyword", keyword);
		return getRequest(HttpConstants.ACTION_TRANSFERAPI_GET_LIST, params);
	}

	/**
	 * 获取过户详情/getDetailById
	 *
	 * @param transferID //交易ID
	 * @return map
	 */
	public static Map<String, Object> transferDetail(String transferID) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("id", transferID);
		return getRequest(HttpConstants.ACTION_TRANSFERAPI_GET_DETAIL_BY_ID, params);
	}

	/**
	 * 申请报销过户费/transferFeeApply
	 * $params = array(
	 * 'crm_user_id'    => 118,               // 过户专员的 crm_user_id
	 * 'id'             => 1304,              // 需要更新的transaction记录id
	 * 'card_name'      => 张三,               // 开户姓名
	 * 'card_bank'      => 中国工商银行XX支行,   // 开户银行
	 * 'card_number'    => 100004333,         // 银行卡号
	 * )
	 *
	 * @return map
	 */
	public static Map<String, Object> transferFeeApply(String id, String card_name, String card_bank, String card_number) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("id", id);
		params.put("card_name", card_name);
		params.put("card_bank", card_bank);
		params.put("card_number", card_number);
		return getRequest(HttpConstants.ACTION_TRANSFER_TRANSFERFEEAPPLY, params);
	}

	/**
	 * 过户完成后转车主/sellerTransferApply
	 * $params = array(
	 * 'crm_user_id'    => 118,               // 过户专员的 crm_user_id
	 * 'id'             => 1304,              // 需要更新的transaction记录id
	 * 'card_name'      => 张三,               // 开户姓名
	 * 'card_bank'      => 中国工商银行XX支行,   // 开户银行
	 * 'card_number'    => 100004333,         // 银行卡号
	 * )
	 *
	 * @return map
	 */
	public static Map<String, Object> sellerTransferApply(String id, String card_name, String card_bank, String card_number) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("id", id);
		params.put("card_name", card_name);
		params.put("card_bank", card_bank);
		params.put("card_number", card_number);
		return getRequest(HttpConstants.ACTION_TRANSFER_SELLERTRANSFERAPPLY, params);
	}

	/**
	 * 申请转其他支出
	 */
	public static Map<String, Object> otherFinApply(String id, String card_name, String card_bank, String card_number) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("id", id);
		params.put("card_name", card_name);
		params.put("card_bank", card_bank);
		params.put("card_number", card_number);
		return getRequest(HttpConstants.ACTION_TRANSACTION_OTHERFINAPPLY, params);
	}

	/**
	 * 毁约完成后转账/sellerTransferApply
	 * $params = array(
	 * 'crm_user_id'           => 118,              // 过户专员的 crm_user_id
	 * 'id'                    => 1304,             // 订单id
	 * 'card_name_seller'      => "奶奶",    // 车主开户姓名
	 * 'card_bank_seller'      => "不不不",  // 车主开户银行
	 * 'card_number_seller'    => '656',     // 车主银行卡号
	 * 'card_name_buyer'       => '观后感',  // 买家开户姓名
	 * 'card_bank_buyer'       => 'ghh',     // 买家开户银行
	 * 'card_number_buyer'     => '555',     // 买家银行卡号
	 * )
	 *
	 * @return map
	 */
	public static Map<String, Object> cancelTransferApply(String id, RefundInfoEntity seller, RefundInfoEntity buyer) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("crm_user_id", user.getId());
		params.put("id", id);

		if (seller != null) {
			params.put("card_name_seller", seller.getName());
			params.put("card_bank_seller", seller.getBank());
			params.put("card_number_seller", seller.getCard());
		}

		if (buyer != null) {
			params.put("card_name_buyer", buyer.getName());
			params.put("card_bank_buyer", buyer.getBank());
			params.put("card_number_buyer", buyer.getCard());
		}
		return getRequest(HttpConstants.ACTION_TRANSFER_CANCELTRANSFERAPPLY, params);
	}


	/**
	 * 修改过户时间,过户地点
	 *
	 * @param transaction_id
	 * @param transfer_place 过户地点
	 * @param transfer_time  过户时间
	 */
	public static Map<String, Object> modifyTransferInfo(String transaction_id, String transfer_place, int transfer_time) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", transaction_id);

		User user = GlobalData.userDataHelper.getUser();
		params.put("crm_user_id", user.getId());

		params.put("transfer_place", transfer_place);
		params.put("transfer_time", transfer_time);
		return getRequest(HttpConstants.ACTION_MODIFY_TRANSFER_INFO, params);
	}

	/**
	 * 修改过户信息
	 *
	 * @param entity 看车任务
	 * @return
	 */
	public static Map<String, Object> modifyTransferMessage(TransactionTaskEntity entity) {

		Map<String, Object> params = new HashMap<String, Object>();

		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		String crm_user_name = GlobalData.userDataHelper.getUser().getName();
		params.put("crm_user_id", crm_user_id);
		params.put("crm_user_name", crm_user_name);
		params.put("trans_id", entity.getId());

		params.put("id", entity.getId());//看车任务id
		params.put("price", String.valueOf(entity.getPrice()));//成交价
		params.put("theory_commission", entity.getTheory_commission());//理论服务费
		params.put("commission", String.valueOf(entity.getCommission()));//最终服务费
		params.put("commission_info", String.valueOf(entity.getCommission_info()));//差价
		params.put("transfer_time", entity.getTransfer_time());//过户时间
		params.put("transfer_place", entity.getTransfer_place());//过户地点
		params.put("warranty_type", entity.getWarranty_type());//质保方式
		//有质保时才有质保到期时间
		if (entity.getWarranty_type() != 1) {
			params.put("warranty_time", entity.getWarranty_time());//质保到期时间
		}
		params.put("transfer_mode", entity.getTransfer_mode());//过户方式
		params.put("transfer_free", entity.getTransfer_free());//公司包过户 1 包过户，0 不报过户
//        params.put("transfer_amount", entity.getTransfer_amount());//公司包过户金额

		if (!TextUtils.isEmpty(entity.getManager_remission())) {
			params.put("manager_remission", entity.getManager_remission());//城市经理调价(减免金额)
		}
		params.put("prepay_comment", entity.getTransfer_comment());//备注(默认展示看车任务备注，地销可以修改，添加)

		if (entity.getContract_pic() != null) {
			params.put("contract_pic", entity.getContract_pic());//合同和买卖双方图片json 字段
		}

		if (entity.getPrepay_transfer_pic() != null) {
			params.put("prepay_transfer_pic", entity.getPrepay_transfer_pic());//合同和买卖双方图片json 字段
		}

		if (entity.getPrepay() != null) {
			params.put("prepay", String.valueOf(entity.getPrepay()));//定金金额(元)--买家给公司定金
		}
		if (entity.getSeller_prepay() != null) {
			params.put("seller_prepay", String.valueOf(entity.getSeller_prepay()));//买家给车主定金
		}
		if (entity.getSeller_company_prepay() != null) {
			params.put("seller_company_prepay", String.valueOf(entity.getSeller_company_prepay()));//车主给公司定金
		}


		params.put("award_info", entity.getAward_info());//大礼包


		params.put("coupon_info", entity.getCoupon_info());//优惠券


		ExtInfo extInfo = mGson.fromJson(entity.getExt_info(), ExtInfo.class);
		params.put("ext_info", extInfo);

		params.put("prepay_payment", entity.getPrepay_payment());
		params.put("prepay_vehicle_price", entity.getPrepay_vehicle_price());
		params.put("prepay_fee", entity.getPrepay_fee());

		params.put("buyer_name", entity.getBuyer_name());
		params.put("buyer_identify", entity.getBuyer_identify());
		params.put("vin_code", entity.getVin_code());

		params.put("buyer_name", entity.getBuyer_name());
		params.put("buyer_identify", entity.getBuyer_identify());
		params.put("vin_code", entity.getVin_code());

		params.put("seller_name", entity.getSeller_name());
		params.put("seller_bank", entity.getSeller_bank());
		params.put("seller_card", entity.getSeller_card());

		params.put("deal_price", entity.getDeal_price());
		params.put("other_cost", entity.getOther_cost());
		params.put("diff_prepay_vehicle_price", entity.getDiff_prepay_vehicle_price());

		return getRequest(HttpConstants.ACTION_MODIFY_TRANSFER_MESSAGE, params);
	}

	/**
	 * 上传过户
	 *
	 * @param transaction_id         transaction_id
	 * @param urls                   照片
	 * @param transfer_amount        过户费用
	 * @param transfer_comment       备注
	 * @param transfer_price         复检后车辆成交价
	 * @param transfer_remission     复检后减免服务费
	 * @param final_payment          应收金额
	 * @param userSellerPrepay       车主定金转服务费
	 * @param transfer_vehicle_price 买家付车款
	 * @param transfer_fee           刷卡手续费
	 * @param insurance              悟空保
	 * @return map
	 */
	public static Map<String, Object> commitTransferPicAndComment(String transaction_id, String transfer_price
			, String transfer_remission, String transfer_amount, int userSellerPrepay, String transfer_vehicle_price
			, String transfer_fee, String insurance, String transfer_comment, List<String> urls, String final_payment
			, int is_loan, String seller_name, String seller_bank, String seller_card, int other_cost) {

		Map<String, String> receipt_url = new HashMap<>();
		if (urls != null && !urls.isEmpty()) {
			for (int i = 0; i < urls.size(); i++) {
				receipt_url.put(String.valueOf(i), urls.get(i));//老接口兼容Object
			}
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", transaction_id);
		params.put("receipt_url", mGson.toJson(receipt_url));
		params.put("transfer_amount", transfer_amount);
		params.put("transfer_comment", transfer_comment);
		int user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("user_id", user_id);
		params.put("crm_user_id", user_id);

		params.put("transfer_price", transfer_price);
		params.put("transfer_remission", transfer_remission);
		params.put("final_payment", final_payment);
		params.put("transfer_fee", transfer_fee);
		params.put("transfer_vehicle_price", transfer_vehicle_price);
		params.put("use_owner", userSellerPrepay);
		params.put("insurance", insurance);
		params.put("is_loan", is_loan);
		params.put("seller_name", seller_name);
		params.put("seller_bank", seller_bank);
		params.put("seller_card", seller_card);

		params.put("other_cost", other_cost);

		return getRequest(HttpConstants.ACTION_UPLOAD_TRANSFER_INFO, params);
	}

	///////////////////////////////////// 过户 结束 /////////////////////////////////////////////////////


	///////////////////////////////////// 地销 带看 开始 /////////////////////////////////////////////////////

	/**
	 * 获取回购车源列表
	 */
	public static Map<String, Object> getHuiGouVehicleSource() {
		Map<String, Object> params = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		params.put("username", user.getUsername());
		params.put("pwd", GlobalData.userDataHelper.getLoginPwd());
		params.put("city_id", user.getCity());
		params.put("src", "huigou");

		Map<String, Object> user_info = new HashMap<>();
		user_info.put("id", user.getId());
		params.put("user_info", user_info);
		return getRequest(HttpConstants.ACTION_HUIGOU_GET_VEHICLE_SOURCE, params);
	}

	/**
	 * 新增买家线索
	 *
	 * @param phone
	 * @param content
	 * @return
	 */
	public static Map<String, Object> addBuyerLead(String phone, String content) {
		Map<String, Object> params = new HashMap<String, Object>();
		User user = GlobalData.userDataHelper.getUser();
		params.put("check_user_id", user.getId());
		params.put("check_user_name", user.getName());
		params.put("buyer_phone", phone);
		params.put("content", content);
		return getRequest(HttpConstants.ACTION_ADD_BUYERLEAD, params);
	}

	/**
	 * 统一取消看车接口
	 *
	 * @param id
	 * @param type
	 * @param level                   crm枚举 N=2 B=3 A=4 H=5
	 * @param due_time                crm枚举 1天后=1 2天后=2 3天后=3 4天后=4 5天后=5
	 * @param reason
	 * @param is_vehicle_match        车型是否匹配 0:匹配 1：不匹配
	 * @param vehicle_dismatch_detail 车型不匹配详情
	 * @return
	 */
	public static Map<String, Object> cancelTransaction(int id, int type, int level, int due_time, String reason
			, int is_vehicle_match, String vehicle_dismatch_detail) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action", "cancel_view_task");
		params.put("check_user_id", user.getId());
		params.put("id", id);
		params.put("type", type);
		params.put("level", level);
		if (due_time == 0) {
			params.put("due_time", -1);
		} else {
			params.put("due_time", due_time);
		}
		params.put("is_vehicle_match", is_vehicle_match);
		if (is_vehicle_match == 1) {
			params.put("vehicle_dismatch_detail", vehicle_dismatch_detail);
		}
		params.put("reason", reason);
		return getRequest(HttpConstants.ACTION_CANCEL_VIEW_TASK, params);
	}

	/**
	 * 添加买家线索
	 *
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> addBuyerClues(String phone, String content, int cityId) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("check_user_id", user.getId());
		params.put("check_user_name", user.getName());
		params.put("buyer_phone", phone);
		params.put("content", content);
		params.put("city_id", cityId);
		return getRequest(HttpConstants.ACTION_ADD_BUYERLEAD, params);
	}

	/**
	 * 获取买家回访
	 */
	public static Map<String, Object> getBuyerRevisit(String buyer_phone, int pageIndex, int pageSize) {
		Map<String, Object> params = new HashMap<>();
		params.put("buyer_phone", buyer_phone);
		params.put("page", pageIndex);
		params.put("limit", pageSize);
		return getRequest(HttpConstants.ACTION_GET_BUYER_REVISIT, params);
	}

	/**
	 * 看车列表页
	 *
	 * @param page
	 * @param page_size
	 * @param entity    see TransFilterEntity ;
	 * @return
	 */
	public static Map<String, Object> transactionFilter(int page, int page_size, TransactionFitlerEntity entity) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", crm_user_id);
		params.put("page", page);
		params.put("page_size", page_size);
		params.put("where", entity);
		return getRequest(HttpConstants.ACTION_TRANSACTION_FILTER, params);
	}

	/**
	 * 根据transaction 获取任务信息
	 *
	 * @param trans_id
	 * @return
	 */
	public static Map<String, Object> getTransactionById(int trans_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", crm_user_id);
		params.put("id", trans_id);
		return getRequest(HttpConstants.ACTION_GET_TRANSACTION_BY_ID, params);
	}

	/**
	 * 根据车源id获取车源信息
	 *
	 * @param sourceId 车源编号
	 * @return
	 */
	public static Map<String, Object> getVehicleInfoById(int sourceId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicle_source_id", sourceId);

		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);
		return getRequest(HttpConstants.ACTION_TRANSACTION_GETVEHICLEINFOBYID, params);
	}

	/**
	 * 根据电话获取买家看车历史记录
	 *
	 * @param phone
	 * @return
	 */
	public static Map<String, Object> getBuyerTransHistory(String phone, int pageIndex, int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", crm_user_id);
		params.put("phone", phone);
		params.put("page", pageIndex);
		params.put("limit", pageSize);
		return getRequest(HttpConstants.ACTION_GET_BUYER_TRANS_HISTORY, params);
	}

	/**
	 * 新增买家回访记录
	 *
	 * @param buyer_phone
	 * @param revisit
	 * @return
	 */
	public static Map<String, Object> addBuyerRevisit(String vehicle_source_id, String buyer_phone, String revisit) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", crm_user_id);
		params.put("vehicle_source_id", vehicle_source_id);
		params.put("buyer_phone", buyer_phone);
		params.put("revisit", revisit);
		return getRequest(HttpConstants.ACTION_ADD_BUYER_REVISIT, params);
	}

	/**
	 * 毁约接口
	 *
	 * @param id               交易id
	 * @param type             类型毁约方 "owner":车主 "buyer":买家
	 * @param owner_prepay     返还给车主定金
	 * @param buyer_prepay     返还给买家定金
	 * @param vehicle_status   车源状态 0 下线 3 上线
	 * @param reason           原因
	 * @param owner_to_account 1退到余额，0不进
	 * @param buyer_to_account 1退到余额，0不进
	 * @param //seller_bank    车主开户行
	 * @param //seller_card    车主银行卡号
	 * @param //buyer_bank     买家开户行
	 * @param //buyer_card     买家银行卡号
	 * @return
	 */
	public static Map<String, Object> cancelTransAfterPrepay(int id, String type, int owner_prepay
			, int buyer_prepay, int vehicle_status, String reason, boolean owner_to_account, boolean buyer_to_account
			, RefundInfoEntity seller, RefundInfoEntity buyer) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("crm_user_id", crm_user_id);
		params.put("trans_id", id);
		if ("0".equals(type)) {
			params.put("type", "buyer");
		} else {
			params.put("type", "owner");
		}
		params.put("owner_prepay", owner_prepay);
		params.put("buyer_prepay", buyer_prepay);
		params.put("vehicle_status", vehicle_status);
		params.put("reason", reason);
		params.put("owner_to_account", owner_to_account ? "1" : "0");
		params.put("buyer_to_account", buyer_to_account ? "1" : "0");

		if (seller == null) {
			params.put("seller_name", "");
			params.put("seller_bank", "");
			params.put("seller_card", "");
		} else {
			params.put("seller_name", seller.getName());
			params.put("seller_bank", seller.getBank());
			params.put("seller_card", seller.getCard());
		}

		if (buyer == null) {
			params.put("buyer_name", "");
			params.put("buyer_bank", "");
			params.put("buyer_card", "");
		} else {
			params.put("buyer_name", buyer.getName());
			params.put("buyer_bank", buyer.getBank());
			params.put("buyer_card", buyer.getCard());
		}

		return getRequest(HttpConstants.ACTION_CANCEL_TRANSFER_AFTER_PREPAY, params);
	}

	/**
	 * 获取车主回访记录 new
	 *
	 * @param seller_phone 车主电话
	 * @return
	 */
	public static Map<String, Object> getSellerRevisit(String seller_phone, String vehicle_source_id) {
		Map<String, Object> params = new HashMap<String, Object>();
		int check_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("check_user_id", check_user_id);
		params.put("vehicle_source_id", vehicle_source_id);//查看指定的车
		params.put("phone", seller_phone);
		return getRequest(HttpConstants.ACTION_GET_SELLER_REVISIT, params);
	}

	/**
	 * 新增车主回访记录 new
	 *
	 * @param vehicle_source_id 车源id
	 * @param seller_name       车主姓名
	 * @param seller_phone      车主电话
	 * @param record            回访记录
	 * @return
	 */
	public static Map<String, Object> addSellerRevisit(String vehicle_source_id, String seller_name, String seller_phone, String record) {
		Map<String, Object> params = new HashMap<String, Object>();
		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		String name = GlobalData.userDataHelper.getUser().getName();
		params.put("crm_user_id", crm_user_id);
		params.put("crm_user_name", name);
		params.put("vehicle_source_id", vehicle_source_id);
		params.put("name", seller_name);
		params.put("phone", seller_phone);
		params.put("record", record);
		return getRequest(HttpConstants.ACTION_ADD_SELLER_REVISIT, params);
	}

	/**
	 * 获取预订成功礼品列表接口
	 *
	 * @return
	 */
	public static Map<String, Object> getPrepayCoupon() {
		Map<String, Object> params = new HashMap<String, Object>();
		int check_user_id = GlobalData.userDataHelper.getUser().getId();
		params.put("check_user_id", check_user_id);
		return getRequest(HttpConstants.ACTION_GET_PREPAY_COUPON, params);
	}

	/**
	 * 根据买家电话获取买家订阅车源的条件
	 *
	 * @param buyerPhone
	 * @return
	 */
	public static Map<String, Object> getSubscribeRule(String buyerPhone) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", buyerPhone);
		int id = GlobalData.userDataHelper.getUser().getId();
		String name = GlobalData.userDataHelper.getUser().getName();
		Map<String, Object> customServiceInfo = new HashMap<String, Object>();
		customServiceInfo.put("id", id);
		customServiceInfo.put("name", name);
		params.put("custom_service_info", customServiceInfo);
		return getRequest(HttpConstants.ACTION_GET_SUBSCRIBE_RULE, params);
	}

	/**
	 * 设置车源订阅条件
	 *
	 * @param buyerPhone
	 * @param subscribeRuleEntity
	 * @param comment
	 * @return
	 */
	public static Map<String, Object> setSubscribeRule(String buyerPhone, VehicleSubscribeRuleEntity.SubscribeRuleEntity subscribeRuleEntity, String comment) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", buyerPhone);
		int id = GlobalData.userDataHelper.getUser().getId();
		String name = GlobalData.userDataHelper.getUser().getName();
		Map<String, Object> customServiceInfo = new HashMap<String, Object>();
		customServiceInfo.put("id", id);
		customServiceInfo.put("name", name);
		params.put("custom_service_info", customServiceInfo);
		Map<String, Object> subscribeRule = new HashMap<String, Object>();
		List<Integer> listSeries = new ArrayList<>();
		for (VehicleSeriesEntity seriesEntity : subscribeRuleEntity.getSubscribe_series()) {
			listSeries.add(seriesEntity.getId());
		}
		subscribeRule.put("subscribe_series", listSeries);
		subscribeRule.put("emission", subscribeRuleEntity.getEmission());
		subscribeRule.put("gearbox", subscribeRuleEntity.getGearbox());
		if (subscribeRuleEntity.getPrice() != null) {
			subscribeRule.put("price", subscribeRuleEntity.getPrice());
		}
		if (subscribeRuleEntity.getEmission_value() != null) {
			subscribeRule.put("emission_value", subscribeRuleEntity.getEmission_value());
		}
		if (subscribeRuleEntity.getVehicle_color_type() != null && subscribeRuleEntity.getVehicle_color_type().size() > 0) {
			subscribeRule.put("vehicle_color_type", subscribeRuleEntity.getVehicle_color_type());
		} else {
			List<Integer> list = new ArrayList<>();
			list.add(-1);
			subscribeRule.put("vehicle_color_type", list);
		}
		if (subscribeRuleEntity.getVehicle_structure() != null && subscribeRuleEntity.getVehicle_structure().size() > 0) {
			subscribeRule.put("vehicle_structure", subscribeRuleEntity.getVehicle_structure());
		} else {
			List<Integer> list = new ArrayList<>();
			list.add(-1);
			subscribeRule.put("vehicle_structure", list);
		}
		subscribeRule.put("year", subscribeRuleEntity.getYear());
		params.put("subscribe_rule", subscribeRule);
		params.put("comment", comment);
		return getRequest(HttpConstants.ACTION_SET_SUBSCRIBE_RULE, params);
	}

	/**
	 * 通过手机号查询车辆带看失败客户详情
	 */
	public static Map<String, Object> getBuyerByPhone(String phone) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phone", phone);
		Map<String, Object> custom_service_info = new HashMap<String, Object>();
		custom_service_info.put("id", GlobalData.userDataHelper.getUser().getId());
		custom_service_info.put("name", GlobalData.userDataHelper.getUser().getName());
		params.put("custom_service_info", custom_service_info);
		return getRequest(HttpConstants.ACTION_GET_BUYER_BY_PHONE, params);
	}

	/**
	 * 获取意向车源列表信息
	 */
	public static Map<String, Object> getDemandList(String phone, int pageIndex, int pageSize, String order) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("phone", phone);
		params.put("where", where);
		params.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		if (TextUtils.isEmpty(order))
			order = "create_time desc";
		params.put("order", order);
		return getRequest(HttpConstants.ACTION_GET_DEMAND_LIST, params);
	}

	/**
	 * 设置备注
	 */
	public static Map<String, Object> setBuyerComment(String phone, String comment, int level) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", phone);
		params.put("comment", comment);
		params.put("level", level);
		Map<String, Object> custom_service_info = new HashMap<>();
		custom_service_info.put("id", GlobalData.userDataHelper.getUser().getId());
		custom_service_info.put("name", GlobalData.userDataHelper.getUser().getName());
		params.put("custom_service_info", custom_service_info);
		return getRequest(HttpConstants.ACTION_SET_BUYER_COMMENT, params);
	}

	/**
	 * 设置备注和级别
	 */
	public static Map<String, Object> setBuyerCommentLvel(String phone, String comment, int level, int due_time, String weixin_account, String buyer_name, int car_dealer) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", phone);

		Map<String, Object> custom_service_info = new HashMap<>();
		custom_service_info.put("id", GlobalData.userDataHelper.getUser().getId());
		custom_service_info.put("name", GlobalData.userDataHelper.getUser().getName());
		params.put("custom_service_info", custom_service_info);

		Map<String, Object> buyer_info = new HashMap<>();
		buyer_info.put("weixin_account", weixin_account);
		buyer_info.put("buyer_name", buyer_name);
		buyer_info.put("comment", comment);
		buyer_info.put("level", level);
		if (due_time == 0) {
			buyer_info.put("due_time", -1);
		} else {
			buyer_info.put("due_time", due_time);
		}
		buyer_info.put("car_dealer", car_dealer);
		params.put("buyer_info", buyer_info);
		return getRequest(HttpConstants.ACTION_SET_BUYER_COMMENT_LEVEL, params);
	}

	/**
	 * 设置我的买家
	 */
	public static Map<String, Object> updateBuyerInfo(String phone, String weixin_account, String buyer_name, int car_dealer) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", phone);

		Map<String, Object> custom_service_info = new HashMap<>();
		custom_service_info.put("id", GlobalData.userDataHelper.getUser().getId());
		custom_service_info.put("name", GlobalData.userDataHelper.getUser().getName());
		params.put("custom_service_info", custom_service_info);

		Map<String, Object> buyer_info = new HashMap<>();
		buyer_info.put("weixin_account", weixin_account);
		buyer_info.put("buyer_name", buyer_name);
		buyer_info.put("car_dealer", car_dealer);
		params.put("buyer_info", buyer_info);
		return getRequest(HttpConstants.ACTION_OFFLINESALER_UPDATEBUYERINFO, params);
	}

	/**
	 * 获取带看失败客户列表
	 * 'search_cond' => 1, //筛选条件 1：H级 2:A级 3:B级 4：今日回访 5：今日失败带看
	 * 'search_type' => 1, // 筛选类型：1:我的回访 2:回炉回访 3:未退出带看 4:失败未上门 5:失败已上门 6:新车推荐买家
	 */
	public static Map<String, Object> getTransFailBuyerList(int pageIndex, int pageSize, TransactionFitlerEntity fitler, int search_cond, String order, int search_type) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		if (fitler != null) {
			where.put("search_field", fitler.getSearch_field());//手机号/姓名
		}
		if (search_cond > 0) {
			where.put("search_cond", search_cond);
		}
		where.put("search_type", search_type);
		params.put("where", where);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		params.put("order", order);

		return getRequest(HttpConstants.ACTION_GET_TRANS_FAIL_BUYER_LIST, params);
	}

	/**
	 * 公海客户列表
	 */
	public static Map<String, Object> getTransHighSeasList(int pageIndex, int pageSize, int level, long start_time
			, long end_time, String search_field) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		where.put("city_id", GlobalData.userDataHelper.getUser().getCity());//城市
		if (level != -1) {
			where.put("level", level);//级别
		}
		if (start_time > 0) {
			where.put("start_time", start_time);//开始时间
		}
		if (end_time > 0) {
			where.put("end_time", end_time);//截止时间
		}
		if (!TextUtils.isEmpty(search_field)) {
			where.put("search_field", search_field);
		}
		params.put("where", where);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
//        params.put("order", "level desc,create_time asc");
		return getRequest(HttpConstants.ACTION_GET_TRANS_HIGH_SEAS_LIST, params);
	}

	/**
	 * 领取公海客户
	 */
	public static Map<String, Object> fetchBuyer(String buyer_name, String buyer_phone) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("id", GlobalData.userDataHelper.getUser().getId());
		where.put("name", GlobalData.userDataHelper.getUser().getName());

		params.put("user_info", where);
		params.put("buyer_name", buyer_name);
		params.put("buyer_phone", buyer_phone);
		params.put("city_id", GlobalData.userDataHelper.getUser().getCity());//城市
		return getRequest(HttpConstants.ACTION_FETCH_BUYER, params);
	}

	/**
	 * 买家/getBuyerTotal
	 */
	public static Map<String, Object> getBuyerTotal() {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);
		return getRequest(HttpConstants.ACTION_GETBUYERTOTAL, params);
	}

	/**
	 * 获取买家匹配到的车源
	 */
	public static Map<String, Object> getMatchedVehicleList(String buyer_phone, int pageIndex, int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("buyer_phone", buyer_phone);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		return getRequest(HttpConstants.ACTION_GET_MATCHED_VEHICLE_LIST, params);
	}

	/**
	 * 获取回访任务列表
	 */
	public static Map<String, Object> getRevisitList(int source, int pageIndex, int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> where = new HashMap<String, Object>();
		where.put("crm_user_id", GlobalData.userDataHelper.getUser().getId());
		where.put("source", source);// 来源 0：我的回访   10：待回访列表 20：未推带看买家列表
		params.put("where", where);
		params.put("limit", pageSize);
		params.put("page", pageIndex);
		String order = "level desc,contact_time desc";
		params.put("order", order);
		return getRequest(HttpConstants.ACTION_GET_REVISIT_LIST, params);
	}

	/**
	 * 根据电话和登陆id 获取通话记录
	 */
	public static Map<String, Object> getPhoneRecordList(int pageIndex, int pageSize, String
			phone, String order) {
		Map<String, Object> params = new HashMap<>();
		params.put("check_user_id", GlobalData.userDataHelper.getUser().getId());
		params.put("phone", phone);
		params.put("page_size", pageSize);
		params.put("page", pageIndex);
		if (TextUtils.isEmpty(order))
			order = "create_time desc";
		params.put("order", order);
		return getRequest(HttpConstants.ACTION_GET_PHONE_RECORD_LIST, params);
	}

	/**
	 * 根据用户手机号或者车源id 获取相关优惠劵
	 *
	 * @param phone     买家电话
	 * @param page      第几页优惠劵列表 默认第一页
	 * @param page_size 每页显示多少优惠劵 默认每页十条 传入参数-1表示不分页,返回全部优惠劵
	 * @param valid     是否显示用户已经使用过的优惠劵,默认是0,显示,值为1表示不显示
	 * @param vid       车源id  默认是-1,显示所有,值为vid表示显示与车源无关优惠劵和与该车源相关的优惠劵
	 * @param order_by  排序依据字段 默认 id desc
	 * @param is_show   默认-1 不显示砍价优惠劵信息，1 显示砍价优惠劵信息
	 * @return
	 */
	public static Map<String, Object> getCouponList(String phone, int page, int page_size,
	                                                int valid, String vid, String order_by, int is_show) {
		Map<String, Object> params = new HashMap<>();
		params.put("phone", phone);
		params.put("page", page);//第几页优惠券列表 默认第一页
		params.put("page_size", page_size);//每页显示多少优惠券
		params.put("valid", valid);//是否使用过优惠券
		params.put("vid", vid);//默认是-1,显示所有,值为vid表示显示与车源无关优惠劵和与该车源相关的优惠劵
		if (order_by != null) {
			params.put("order_by", order_by);//排序依据字段 默认 id desc
		}
		params.put("is_show", is_show);//默认-1 不显示砍价优惠劵信息，1 显示砍价优惠劵信息
		return getRequest(HttpConstants.ACTION_GET_USER_COUPONS, params);
	}

	/**
	 * 根据兑换码兑换优惠劵：
	 *
	 * @return
	 */
	public static Map<String, Object> convertCoupon(String phone, String code, String vid) {
		Map<String, Object> params = new HashMap<>();
		params.put("phone", phone);//买家电话
		params.put("code", code);//优惠券兑换码
		params.put("vid", vid);//车源id
		return getRequest(HttpConstants.ACTION_EXCHANGE_COUPONS, params);
	}

	/**
	 * 买家支付定金
	 *
	 * @param task
	 * @return
	 */
	public static Map<String, Object> transactionPrepay(TransactionTaskEntity task) {
		Map<String, Object> params = new HashMap<String, Object>();

		int crm_user_id = GlobalData.userDataHelper.getUser().getId();
		String crm_user_name = GlobalData.userDataHelper.getUser().getName();
		params.put("crm_user_id", crm_user_id);
		params.put("crm_user_name", crm_user_name);
		params.put("id", task.getId());//看车任务id
		params.put("deal_price", task.getDeal_price());//成本价
		params.put("price", String.valueOf(task.getPrice()));//成交价
		params.put("theory_commission", task.getTheory_commission());//理论服务费
		params.put("commission", String.valueOf(task.getCommission()));//最终服务费
		params.put("transfer_time", task.getTransfer_time());//过户时间
		params.put("transfer_place", task.getTransfer_place());//过户地点
		params.put("transfer_mode", task.getTransfer_mode());//过户方式
		params.put("transfer_free", task.getTransfer_free());//公司包过户 1 包过户，0 不报过户
		params.put("warranty_type", task.getWarranty_type());//质保方式
		//有质保时才有质保到期时间
		if (task.getWarranty_type() != 1) {
			params.put("warranty_time", task.getWarranty_time());//质保到期时间
		}

		if (!TextUtils.isEmpty(task.getManager_remission())) {
			params.put("manager_remission", task.getManager_remission());//城市经理调价(减免金额)
		}
		params.put("prepay_comment", task.getTransfer_comment());//备注(默认展示看车任务备注，地销可以修改，添加)
		params.put("contract_pic", task.getContract_pic());//合同和买卖双方图片json 字段
		params.put("prepay", String.valueOf(task.getPrepay()));//定金金额(元)--买家给公司定金
		params.put("seller_prepay", String.valueOf(task.getSeller_prepay()));//买家给车主定金
		params.put("seller_company_prepay", String.valueOf(task.getSeller_company_prepay()));//车主给公司定金
		params.put("award_info", task.getAward_info());//大礼包
		params.put("coupon_info", task.getCoupon_info());//优惠券

		ExtInfo extInfo = mGson.fromJson(task.getExt_info(), ExtInfo.class);
		params.put("ext_info", extInfo);

		params.put("prepay_payment", task.getPrepay_payment());
		params.put("prepay_vehicle_price", task.getPrepay_vehicle_price());
		params.put("prepay_fee", task.getPrepay_fee());
		params.put("buyer_name", task.getBuyer_name());
		params.put("buyer_identify", task.getBuyer_identify());
		params.put("vin_code", task.getVin_code());

		/*必传参数*/
		params.put("buyer_phone", task.getBuyer_phone());
		params.put("buyer_name", task.getBuyer_name());
		params.put("vehicle_source_id", task.getVehicle_source_id());
//		params.put("type", task.getType());//若type=0，则id为C2C带看任务id；若type=1，则id为展厅任务id

		params.put("seller_name", task.getSeller_name());
		params.put("seller_bank", task.getSeller_bank());
		params.put("seller_card", task.getSeller_card());

		params.put("other_cost", task.getOther_cost());

		return getRequest(HttpConstants.ACTION_PREPAY_TRANSACTION, params);
	}

	/**
	 * 获取线索列表信息
	 *
	 * @param page
	 * @param page_size
	 * @return
	 */
	public static Map<String, Object> getBuyerleadList(int page, int page_size) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("page_size", page_size);
		params.put("user_id", user.getId());
		params.put("a_v", DeviceInfoUtil.getAppVersion());
		return getRequest(HttpConstants.ACTION_GET_BUYERLEAD_LIST, params);
	}

	/**
	 * 获某个人添加的收车任务列表
	 * limit' => int, //获取多少条。不传默认 10
	 * 'page' => int, //获取第几页。默认为 0
	 * 'user_info' => array( //操作人信息
	 * 'id' => int, //crm_user_id
	 * 'name' => string, //crm_user_name
	 * )
	 */
	public static Map<String, Object> getPurchaseleadList(int page, int page_size) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("limit", page_size);
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);
		params.put("a_v", DeviceInfoUtil.getAppVersion());
		return getRequest(HttpConstants.ACTION_GET_ONES_BACK_TASK, params);
	}

	/**
	 * 添加收车线索
	 *
	 * @return
	 * @author wfx
	 */
	public static Map<String, Object> addPurchaseClue(PurchaseClueEntity purchaseClue, int vehicle_source_id, int cityId) {
		Map<String, Object> params = new HashMap<>();
		params.put("seller_name", purchaseClue.getSeller_name());
		params.put("seller_phone", purchaseClue.getSeller_phone());
		params.put("brand_id", purchaseClue.getBrand_id());
		params.put("brand_name", purchaseClue.getBrand_name());
		params.put("class_id", purchaseClue.getClass_id());
		params.put("class_name", purchaseClue.getClass_name());
		params.put("remark", purchaseClue.getRemark());
		params.put("vehicle_source_id", vehicle_source_id);
		Map<String, Object> userInfo = new HashMap<>();
		userInfo.put("id", purchaseClue.getId());
		userInfo.put("name", purchaseClue.getName());
		params.put("user_info", userInfo);
		params.put("city_id", cityId);
		return getRequest(HttpConstants.ACTION_ADD_BACK_TASK, params);
	}

	/**
	 * 添加验车车源推荐
	 *
	 * @return
	 * @author wfx
	 */
	public static Map<String, Object> addVehicleRecom(CheckCarClueEntity purchaseClue, int cityId) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", purchaseClue.getSeller_name());
		params.put("phone", purchaseClue.getSeller_phone());
		params.put("brand_id", purchaseClue.getBrand_id());
		params.put("class_id", purchaseClue.getClass_id());
		params.put("user_id", purchaseClue.getId());
		params.put("city_id", cityId);
		return getRequest(HttpConstants.ACTION_ADD_VEHICLE_RECOM, params);
	}

	/**
	 * 分页获取验车推荐列表
	 *
	 * @param page     页码
	 * @param pageSize 每页的大小
	 * @return
	 * @author wfx
	 */
	public static Map<String, Object> getVehicleRecomByPage(int page, int pageSize) {
		Map<String, Object> params = new HashMap<>();
		params.put("page", page);
		params.put("limit", pageSize);
		params.put("user_id", GlobalData.userDataHelper.getUser().getId());
		return getRequest(HttpConstants.ACTION_GET_VEHICLE_RECOM_LIST, params);
	}

	/**
	 * 获取车源推荐奖金
	 *
	 * @return
	 * @author wfx
	 */
	public static Map<String, Object> getVehicleRecomBonus() {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", GlobalData.userDataHelper.getUser().getId());
		return getRequest(HttpConstants.ACTION_GET_VEHICLE_RECOM_BONUS, params);
	}

	/**
	 * 根据车源id查询车源标题
	 *
	 * @return
	 */
	public static Map<String, Object> getVehicleSourceTitle(int vehicle_source_id) {
		Map<String, Object> params = new HashMap<>();
		params.put("vehicle_source_id", vehicle_source_id);
		Map<String, Object> userInfo = new HashMap<>();
		User checker = GlobalData.userDataHelper.getUser();
		userInfo.put("id", checker.getId());
		userInfo.put("name", checker.getName());
		params.put("user_info", userInfo);
		return getRequest(HttpConstants.ACTION_GET_SOURCETITLE, params);
	}

	/**
	 * 添加操作记录/addReadyInfo
	 *
	 * @param transactionID
	 * @return map
	 */
	public static Map<String, Object> addReadyInfo(int transactionID, List<
			TransactionReadyInfo> readyInfos) {
		User user = GlobalData.userDataHelper.getUser();
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("transaction_id", transactionID);

		Map<String, Object> user_info = new HashMap<>();
		user_info.put("id", user.getId());
		user_info.put("name", user.getName());
		params.put("user_info", user_info);


		List<TransactionReadyInfo.OperationEntity> operationEntities = new ArrayList<>();
		for (TransactionReadyInfo readyInfo : readyInfos) {
			operationEntities.add(readyInfo.getOperation());
		}
		params.put("operation", operationEntities);

		return getRequest(HttpConstants.ACTION_ADD_READY_INFO, params);
	}


	/**
	 * 地销加带看
	 *
	 * @return
	 */
	public static Map<String, Object> addTransactionTask() {
		Map<String, Object> params = new HashMap<>();
		params.put("username", GlobalData.userDataHelper.getLoginName());
		params.put("pwd", GlobalData.userDataHelper.getLoginPwd());
		return getRequest(HttpConstants.ACTION_TRANSACTIONAPI_ADD_TASK, params);
	}

	public static Map<String, Object> createBuyer(String name, String phone, int carDealer, int level, int due_time
			, String weixin, String revisit, int last_arrive_time) {
		Map<String, Object> params = new HashMap<>();

		Map<String, Object> user_info = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		user_info.put("id", user.getId());
		user_info.put("name", user.getName());
		params.put("user_info", user_info);

		params.put("phone", phone);
		params.put("name", name);
		params.put("city_id", user.getCity());

		params.put("level", level);
		if (due_time == 0) {
			params.put("due_time", -1);
		} else {
			params.put("due_time", due_time);
		}
		params.put("car_dealer", carDealer);//1 车商，0 否
		params.put("weixin_account", weixin);
		params.put("revisit", revisit);
//        params.put("comment", revisit);
		params.put("last_arrive_time", last_arrive_time);

		return getRequest(HttpConstants.ACTION_OFFLINESALERAPI_CREATEBUYER, params);
	}

	///////////////////////////////////// 地销 带看 结束 /////////////////////////////////////////////////////


	//////////////////////////////////////// 品牌车系车型年份车款 开始///////////////////////////////////////////////////////////

	/**
	 * 根据车系获取年份信息
	 *
	 * @param classId 车系id
	 * @author wfx
	 */
	public static Map<String, Object> getYearByClass(int classId) {
		Map<String, Object> params = new HashMap<>();
		params.put("class_id", classId);
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);

		return getRequest(HttpConstants.ACTION_GET_YEAR_BY_CLASS, params);
	}

	/**
	 * 根据年份获取车型信息
	 *
	 * @param classId 车系id
	 * @param year    年份
	 * @author wfx
	 */
	public static Map<String, Object> getTypeByYear(int classId, String year) {
		Map<String, Object> params = new HashMap<>();
		params.put("class_id", classId);
		params.put("year", year);
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);

		return getRequest(HttpConstants.ACTION_GET_TYPE_BY_YEAR, params);
	}


	/**
	 * 获取当前用户下对应的所有品牌
	 *
	 * @author wfx
	 */
	public static Map<String, Object> getAllBrands() {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);

		return getRequest(HttpConstants.ACTION_ALL_BRANDS, params);
	}


	/**
	 * 根据品牌获取车系
	 *
	 * @param brandId 品牌id
	 * @author wfx
	 */
	public static Map<String, Object> getClassByBrand(int brandId) {
		Map<String, Object> params = new HashMap<>();
		params.put("brand_id", brandId);
		Map<String, Object> userInfo = new HashMap<>();
		User user = GlobalData.userDataHelper.getUser();
		userInfo.put("id", user.getId());
		userInfo.put("name", user.getName());
		params.put("user_info", userInfo);

		return getRequest(HttpConstants.ACTION_GET_ALL_CLASS_BY_BRAND, params);
	}

	//////////////////////////////////////// 品牌车系车型年份车款 结束///////////////////////////////////////////////////////////


	/////////////////////////////////////////////////// 录音部分 开始 ///////////////////////////////////////////////

	/**
	 * 上传录音记录
	 *
	 * @param entity     entity
	 * @param record_url record_url
	 * @return map
	 */
	public static Map<String, Object> uploadToServer(PhoneRecordEntity entity, String
			record_url) {
		int call_duration = entity.getCall_duration();
		int call_type = entity.getCall_type();
		String customer_phone = entity.getCustomer_phone();
		int employee_number = entity.getEmployee_number();
		String saler_phone = entity.getSaler_phone();
		long create_time = entity.getCreate_time();


		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("action", HttpConstants.ACTION_UPLOADTOSERVER);
		params.put("employee_number", employee_number);
		params.put("saler_phone", saler_phone);
		params.put("customer_phone", customer_phone);
		params.put("call_type", call_type);
		params.put("create_time", create_time);
		params.put("call_duration", call_duration);
		params.put("record_url", record_url);

		return getRequest(HttpConstants.ACTION_UPLOADTOSERVER, params);
	}

	/**
	 * 获取上传文件所需token
	 *
	 * @return map
	 */
	public static Map<String, Object> getFileToken() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("action", HttpConstants.ACTION_GET_UPLOAD_RECORD_TOKEN);
		return getRequest(HttpConstants.ACTION_GET_UPLOAD_RECORD_TOKEN, params);
	}

	/**
	 * 上传通话记录
	 *
	 * @param result result
	 * @return map
	 */
	public static Map<String, Object> uploadCallLogs(ArrayList<CallLogEntity> result) {
		String employee_number = GlobalData.userDataHelper.getUser().getId() + "";
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("action", HttpConstants.ACTION_UPLOADCALLLOGS);
		params.put("employee_number", employee_number);
		JSONArray jArr = new JSONArray();
		try {  //employee_number | customer_phone | customer_name | call_type | create_time | call_duration
			for (CallLogEntity enty : result) {
				JSONObject json = new JSONObject();
				json.put("customer_phone", enty.getCallNumber());
				json.put("customer_name", enty.getCallName());
				json.put("call_duration", enty.getCallDuratioin());
				json.put("create_time", enty.getCallDate() / 1000);
				json.put("call_type", enty.getCallType());
				jArr.put(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.put("call_list", jArr.toString());

		Log.d("UploadCallParams", params.toString());

		return getRequest(HttpConstants.ACTION_UPLOADCALLLOGS, params);
	}

	/////////////////////////////////////////////////// 录音部分 结束 ///////////////////////////////////////////////

	/**
	 * 检测当前版本是否为最新版本
	 *
	 * @author wfx
	 */
	public static Map<String, Object> checkVersion(int version) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", TaskConstants.APP_MCSQ);//1：卖车神器、2：好车帮
		params.put("version", version);
		Map<String, Object> user_info = new HashMap<>();
		user_info.put("id", GlobalData.userDataHelper.getUser().getId());
		user_info.put("name", GlobalData.userDataHelper.getUser().getName());
		params.put("user_info", user_info);
		return getRequest(HttpConstants.ACTION_CHECK_VERSION, params);
	}

	/**
	 * 封装请求参数String
	 *
	 * @param action
	 * @param params
	 * @return
	 */
	private static Map<String, Object> getRequest(String action, Map<String, Object> params) {
		Map<String, Object> msg = new HashMap<>();
		Map<String, Object> request = new HashMap<>();
		msg.put("action", action);
		msg.put("params", params);

		request.put("action", action);
		request.put("web_token", TOKEN);
		request.put("stat", getRequestStat());
		if (action != HttpConstants.ACTION_LOGIN_ENCODE) {
			String msgEncode = "";
			try {
				msgEncode = DESUtil.encryptDES(new Gson().toJson(msg), GlobalData.userDataHelper.getLastAppToken());
				HCLogUtil.d(TAG, "msg---msgEncode -->" + msgEncode);
				String msgDecode = DESUtil.decryptDES(msgEncode, GlobalData.userDataHelper.getLastAppToken());
				HCLogUtil.d(TAG, "msg---msgDecode -->" + msgDecode);

			} catch (Exception e) {
				e.printStackTrace();
			}
			request.put("msg", msgEncode);
			request.put("app_token", GlobalData.userDataHelper.getLastAppToken());
//      request.put("token", GlobalData.userDataHelper.getUser().getApp_token());
		} else {
			request.put("msg", msg);
		}

		return request;
	}

	/**
	 * 获取手机当前状态信息
	 *
	 * @return
	 */
	private static Map<String, Object> getRequestStat() {
		Map<String, Object> stat = new HashMap<>();
		stat.put("a_v", DeviceInfoUtil.getAppVersion());
		stat.put("s_v", DeviceInfoUtil.getOSVersion());
		stat.put("p_t", DeviceInfoUtil.getPhoneType());
		stat.put("n_s", NetInfoUtil.getNetworkType());
		return stat;
	}

}
