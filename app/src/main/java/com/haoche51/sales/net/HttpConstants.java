package com.haoche51.sales.net;

/**
 * 接口action常量
 * Created by xuhaibo on 15/8/25.
 */
public class HttpConstants {
	public static final int GET_LIST_REFRESH = 5;//刷新
	public static final int GET_LIST_LOADMORE = 6;//加载更多
	public static final int REQUEST_PAGESIZE = 10;//page size
	/**
	 * 登录action 带加密
	 */
	public static final String ACTION_LOGIN_ENCODE = "login";
	/**
	 * 修改密码action
	 */
	public static final String ACTION_CHANGE_PWD = "change_passwd";
	/**
	 * 获取工作汇报详情
	 */
	public static final String ACTION_GET_REPORT_DETAIL = "offlinesalerapi_getreportdetail";

	/**
	 * 获取新增报告
	 */
	public static final String ACTION_ADD_REPORT = "offlinesalerapi_addreport";
	/**
	 * 加带看
	 */
	public static final String ACTION_MAKE_LOOK = "exhibitionapi_makeappointment";
	/**
	 * 获取增量车型信息
	 */
	public static final String ACTION_FECTH_INCREMENTMODEL = "get_increment_vehicle";
	/**
	 * 绑定百度推送
	 */
	public static final String ACTION_BIND_BAIDU_PUSH = "bind_baidu_push";
	/**
	 * 取消绑定action
	 */
	public static final String ACTION_UNBIND_BAIDUPUSH = "unbind_baidu_push";
	/**
	 * 支付定金action
	 */
	public static final String ACTION_PREPAY_TRANSACTION = "transaction_prepay";
	/**
	 * 新增买家线索
	 */
	public static final String ACTION_ADD_BUYERLEAD = "add_buyerlead";
	/**
	 * 获取评估师线索列表
	 */
	public static final String ACTION_GET_BUYERLEAD_LIST = "new_get_buyerlead_list";
	/**
	 * 上传评估师地理位置信息
	 */
	public static final String ACTION_UPLOAD_CHECKER_POSITION = "update_checker_position";

	//过户相关
	/**
	 * 获取过户信息列表（新）
	 */
	public static final String ACTION_TRANSFERAPI_GET_LIST = "transferapi_get_list";
	/**
	 * 修改过户信息请求（新）
	 */
	public static final String ACTION_TRANSFERAPI_GET_DETAIL_BY_ID = "transferapi_get_detail_by_id";
	/**
	 * 获取过户信息列表（旧）
	 */
	public static final String ACTION_GET_TRANSFER_LIST = "get_transfer_list";
	/**
	 * 修改过户信息请求（旧）
	 */
	public static final String ACTION_MODIFY_TRANSFER_INFO = "modify_transfer_info";
	/**
	 * 修改过户信息
	 */
	public static final String ACTION_MODIFY_TRANSFER_MESSAGE = "modify_transfer_message";
	/**
	 * 上传过户信息
	 */
	public static final String ACTION_UPLOAD_TRANSFER_INFO = "upload_transfer_info";
	/**
	 * 毁约（支付定金后取消）
	 */
	public static final String ACTION_CANCEL_TRANSFER_AFTER_PREPAY = "cancel_transfer_alter_prepay";

	//看车相关
	/**
	 * 获取回购车源
	 */
	public static final String ACTION_HUIGOU_GET_VEHICLE_SOURCE = "crmcheckerapi_getwaplisturl";
	/**
	 * 取消看车任务接口
	 */
	public static final String ACTION_CANCEL_VIEW_TASK = "cancel_view_task";
	/**
	 * 获取个人分享数据
	 */
	public static final String ACTION_GET_SELF_SHARE_STAT = "get_self_share_stat";
	/**
	 * 获取分享总排行
	 */
	public static final String ACTION_GET_SHARE_STAT = "get_share_stat";
	/**
	 * 获取七牛token
	 */
	public static final String ACTION_GET_QINIU_TOKEN = "get_qiniu_token";

	/**
	 * 获取礼品接口
	 */
	public static final String ACTION_GET_PREPAY_COUPON = "get_prepay_coupon";

	/**
	 * 获取看车任务列表
	 */
	public static final String ACTION_TRANSACTION_FILTER = "transactions_filter";
	/**
	 * 获取待回访任务列表/getRevisitList
	 */
	public static final String ACTION_GET_REVISIT_LIST = "offlinesalerapi_getrevisitlist";

	//回访相关
	/**
	 * 买家回访
	 */
	public static final String ACTION_GET_BUYER_REVISIT = "get_buyer_revisit";
	/**
	 * 车主回访
	 */
	public static final String ACTION_GET_SELLER_REVISIT = "get_seller_revisit";//new
	public static final String ACTION_ADD_SELLER_REVISIT = "add_seller_revisit";//new

	/**
	 * 获取买家看车历史记录
	 */
	public static final String ACTION_GET_BUYER_TRANS_HISTORY = "get_buyer_trans_history";
	/**
	 * 新增买家回访记录
	 */
	public static final String ACTION_ADD_BUYER_REVISIT = "add_buyer_revisit";


	/**
	 * 绑定二维码
	 */
	/*获取消息列表信息(new)*/
	public static final String ACTION_GET_MESSAGE = "get_messages";
	/**
	 * 标记消息已读
	 */
	public static final String ACTION_READ_MSG = "read_msg";
	/**
	 * 获取未读消息个数
	 */
	public static final String ACTION_GET_URMSG_COUNT = "get_urmsg_count";

	/*获取单个看车任务 */
	public static final String ACTION_GET_TRANSACTION_BY_ID = "get_transaction_by_id";

	/*获取登录crm 使用token,*/
	public static final String ACTION_GET_LOGIN_TOKEKN = "get_login_token";

	/**
	 * 获取订阅条件/getSubscribeRule
	 */
	public static final String ACTION_GET_SUBSCRIBE_RULE = "get_sub_rules";
	/**
	 * 设置订阅条件/setSubscribeRule
	 */
	public static final String ACTION_SET_SUBSCRIBE_RULE = "set_sub_rules";

	/**
	 * 通知车源推荐
	 */
	public static final String ACTION_GET_SUB_VEHICLE_LIST = "get_sub_vehicle_list";

	/**
	 * 通过手机号查询车辆带看失败客户详情
	 */
	public static final String ACTION_GET_BUYER_BY_PHONE = "get_buyer_by_phone";

	/**
	 * 获取意向车源列表信息
	 */
	public static final String ACTION_GET_DEMAND_LIST = "get_demand_list";

	/**
	 * 设置客户备注　//　TODO(后期废除)
	 */
	public static final String ACTION_SET_BUYER_COMMENT = "set_buyer_comment";
	/**
	 * 设置客户备注和级别
	 */
	public static final String ACTION_SET_BUYER_COMMENT_LEVEL = "set_buyer_info";

	/**
	 * 获取带看失败客户列表
	 */
	public static final String ACTION_GET_TRANS_FAIL_BUYER_LIST = "get_trans_fail_buyer_list";

	/**
	 * 获取公海客户列表
	 */
	public static final String ACTION_GET_TRANS_HIGH_SEAS_LIST = "offlinesalerapi_getallbuyer";
	/**
	 * 领取公海客户
	 */
	public static final String ACTION_FETCH_BUYER = "offlinesalerapi_fetchbuyer";

	/**
	 * 2.	获取推荐车源匹配到的客户列表接口
	 */
	public static final String ACTION_GET_VEHICLE_MATCHED_BUYER_LIST = "get_vehicle_matched_buyer_list";

	/**
	 * 根据电话和登陆id 获取通话记录
	 */
	public static final String ACTION_GET_PHONE_RECORD_LIST = "get_phone_record_list";

	/**
	 * 获取登陆用户的权限
	 */
	public static final String ACTION_GET_USER_RIGHT = "get_userright";
	/**
	 * 根据用户手机号或者车源id 获取相关优惠劵
	 */
	public static final String ACTION_GET_USER_COUPONS = "get_user_coupons";
	/**
	 * 根据兑换码兑换优惠劵
	 */
	public static final String ACTION_EXCHANGE_COUPONS = "exchange_coupon";

	/**
	 * 添加操作记录/addReadyInfo
	 */
	public static final String ACTION_ADD_READY_INFO = "transaction_add_ready_info";

	/**
	 * 根据车系获取年份信息
	 */
	public static final String ACTION_GET_YEAR_BY_CLASS = "vehicleapi_get_year_by_class";

	/**
	 * 根据年份获取车型信息
	 */
	public static final String ACTION_GET_TYPE_BY_YEAR = "vehicleapi_get_vehicle_by_year";

	/**
	 * 获取当前用户对应的所有品牌
	 */
	public static final String ACTION_ALL_BRANDS = "vehicleapi_get_brand";
	/**
	 * 根据品牌获取车系
	 */
	public static final String ACTION_GET_ALL_CLASS_BY_BRAND = "vehicleapi_get_class_by_brand";

	/**
	 * 自己创建看车任务页面
	 */
	public static final String ACTION_TRANSACTIONAPI_ADD_TASK = "transactionapi_add_task";

	/**
	 * 创建买家/createBuyer
	 */
	public static final String ACTION_OFFLINESALERAPI_CREATEBUYER = "offlinesalerapi_createbuyer";
	/**
	 * 添加回购线索
	 */
	public static final String ACTION_ADD_BACK_TASK = "backtaskapi_addtask";
	/**
	 * 根据车源id查询车源标题
	 */
	public static final String ACTION_GET_SOURCETITLE = "backtaskapi_getsourcetitle";
	/**
	 * 获某个人添加的收车任务列表
	 */
	public static final String ACTION_GET_ONES_BACK_TASK = "backtaskapi_getonestask";

	/**
	 * 车源推荐——获取验车车源推荐奖金
	 */
	public static final String ACTION_GET_VEHICLE_RECOM_BONUS = "crmcheckerapi_getmysubsidy";

	/**
	 * 车源推荐——获取验车车源推荐列表
	 */
	public static final String ACTION_GET_VEHICLE_RECOM_LIST = "crmcheckerapi_getmyleadlist";

	/**
	 * 车源推荐——添加验车车源推荐
	 */
	public static final String ACTION_ADD_VEHICLE_RECOM = "crmcheckerapi_addvehiclelead";
	/**
	 * 过户专员——申请报销过户费
	 */
	public static final String ACTION_TRANSFER_TRANSFERFEEAPPLY = "transferapi_transferfeeapply";
	/**
	 * 过户完成后转车主
	 */
	public static final String ACTION_TRANSFER_SELLERTRANSFERAPPLY = "transferapi_sellertransferapply";
	/**
	 * 毁约完成后转账
	 */
	public static final String ACTION_TRANSFER_CANCELTRANSFERAPPLY = "transferapi_canceltransferapply";
	/**
	 * 根据车源id获取车源信息
	 */
	public static final String ACTION_TRANSACTION_GETVEHICLEINFOBYID = "get_vehicle_info_by_id";
	/**
	 * 获取地销薪资信息
	 */
	public static final String ACTION_GETSUBSIDYINFO = "offlinesalerapi_getsubsidyinfo";
	/**
	 * 获取在线城市
	 */
	public static final String ACTION_GET_ONLINE_CITY = "get_online_city";
	/**
	 * 校验app版本是否为最新
	 */
	public static final String ACTION_CHECK_VERSION = "crmcommonapi_validversion";
	/**
	 * 展厅带看
	 */
	public static final String ACTION_MAKE_APPOINTMENT = "exhibitionapi_makeappointment";
	/**
	 * 根据条件获取地销列表
	 */
	public static final String ACTION_GET_USERLIST_BYFILTER = "get_userlist_by_filter";
	/**
	 * 展厅添加到店记录
	 */
	public static final String ACTION_EDITEXHIBITIONTRANS = "exhibitionapi_editexhibitiontrans";
	/**
	 * 买家/getBuyerTotal
	 */
	public static final String ACTION_GETBUYERTOTAL = "offlinesalerapi_getbuyertotal";
	/**
	 * 获取买家匹配到的车源
	 */
	public static final String ACTION_GET_MATCHED_VEHICLE_LIST = "get_matched_vehicle_list";
	/**
	 * 过户完成后转车主
	 */
	public static final String ACTION_TRANSACTION_OTHERFINAPPLY = "transactionapi_otherfinapply";
	/**
	 * 设置我的买家
	 */
	public static final String ACTION_OFFLINESALER_UPDATEBUYERINFO = "update_buyer_purpose";

	/////////////////////////////////////////////////// 录音部分 开始 ///////////////////////////////////////////////

	public final static String ACTION_UPLOADTOSERVER = "add_telephony_record";//上传记录
	public final static String ACTION_GET_UPLOAD_RECORD_TOKEN = "get_qiniu_tel_token";//获取上传文件所需token
	public final static String ACTION_UPLOADCALLLOGS = "add_call_record";//增加记录

	/////////////////////////////////////////////////// 录音部分 结束 ///////////////////////////////////////////////

}
