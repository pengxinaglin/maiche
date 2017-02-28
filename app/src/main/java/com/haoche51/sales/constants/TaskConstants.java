package com.haoche51.sales.constants;

public class TaskConstants {

	//地销级别
	public static final int SALE_LEVEL_BJ = 10;//宝骏
	public static final int SALE_LEVEL_VW = 20;//大众
	public static final int SALE_LEVEL_BMW = 30;//宝马
	public static final int SALE_LEVEL_PS = 40;//保时捷

	/**
	 * 推送消息中任务类型
	 */
	public static final int MESSAGE_CHECK_TASK = 0;//车检任务
	public static final int MESSAGE_VIEW_TASK = 1;//看车任务
	public static final int MESSAGE_TRANSFER_TASK = 2;//过户通知
	public static final int MESSAGE_TRANSATION_CUSTOMER = 3;//客户通知
	public static final int MESSAGE_BACK_TASK = 4;//收车任务
	public static final int MESSAGE_REVISIT_TASK = 5;//回访任务

	// 看车任务状态码
	public static final int VIEW_TASK_PENDING = 0; // 待看
	public static final int VIEW_TASK_BCANCEL = 1; // 试驾前取消
	public static final int VIEW_TASK_ACANCEL = 2; // 试驾后取消
	public static final int VIEW_TASK_PREPAY = 3;//预定成功
	public static final int VIEW_TASK_FULLPAY = 4;// 支付全款
	public static final int VIEW_TASK_FINISH = 5;// 交易完成
	public static final int VIEW_TASK_BREACH = 6;// 毁约

	//看车预定状态
	public static final int STATUS_PRE_PAY = 0;    //看车任务为预定状态
	public static final int STATUS_CONTRACT_AUDIT = 1;    // 合同审核 跳过
	public static final int STATUS_MAN_AUDIT = 2;    // 预定审核
	public static final int STATUS_FIN_PREPAY = 3;    // 预定收款 跳过
	public static final int STATUS_WAIT_TRANSFER = 4;    // 待过户
	public static final int STATUS_FIN_TRANSFER = 5;    // 过户经理审核
	public static final int STATUS_FINISH = 6;    // 交易完成
	public static final int STATUS_MAN_CANCEL = 7;    // 毁约经理审核
	public static final int STATUS_FIN_CANCEL = 8;    // 毁约财务审核 跳过
	public static final int STATUS_CANCEL_FINISH = 9;    // 已毁约
	public static final int STATUS_MAN_TRANSFER = 10;   // 过户审核 废弃忽略
	public static final int STATUS_CONTRACT_FAIL = 11;   // 合同审核未通过
	public static final int STATUS_TRANSFER_CONTRACT = 12;   // 过户合同审核
	public static final int STATUS_TRANSFER_CONTRACT_FAIL = 13;   // 过户合同审核失败


	// 看车任务请求码：
	public static final int QUIT_BUY_TRANSACTION = 0x666; // 放弃购买
	public static final int REQUEST_CODE_CONDITION_MODIFY = 0x671; // 修改订阅条件
	public static final int TRANSACTION_MODIFY_TRANSFER_CONTRACT_AUDIT_FAIL = 0x667; // 修改过户信息--合同审核失败时，重新预定
	public static final int TRANSACTION_MODIFY_TRANSFER = 0x670; // 修改过户信息--修改过户方式
	public static final int TRANSACTION_UP_TRANSFER_PIC = 0x668; // 上传转账照片
	public static final int TRANSACTION_CHANGE_PROMISES = 0x669; // 毁约
	public static final int PREPAY_TRANSACTION = 0x888;//支付定金
	public static final int TRANSACTION_STORE_TAKE = 0x672;//展厅车添加带看
	public static final int TRANSACTION_STORE_ARRIVAL = 0x673;//展厅车添加带看

	//默认显示的任务条数
	public static final int DEFAULT_SHOWTASK_COUNT = 10;

	public static final int TRANSACTION_PENDING = 0;//待看
	public static final int TRANSACTION_FAILED = 1;//失败
	public static final int TRANSACTION_SUCCESS = 2;//成功

	public static final String ACACHE_TRANSACTION_DEFAULT = "transaction_default";//带看任务
	public static final String ACACHE_TRANSACTION_SUCCESS = "transaction_success";//预定成功任务
	public static final String ACACHE_TRANSACTION_FAILED = "transaction_failed";//看车失败任务
	public static final String ACACHE_RECOMMEND_BUYER = "recommend_buyer";//买家推荐
	public static final String ACACHE_RECOMMEND_PURCHASE = "recommend_purchase";//回购推荐
	public static final String ACACHE_RECOMMEND_CHECK = "recommend_check";//验车推荐
	public static final String ACACHE_CUSTOMER_DEFAULT = "customer_default";//客户列表
	public static final String ACACHE_CUSTOMER_HIGH = "customer_high";//客户公海列表

	//毁约操作状态 0初始 1待确认 2已确认
	public static final int CANCEL_TRANS_STATUS_DEFAULT = 0;
	public static final int CANCEL_TRANS_STATUS_NOTCONFIRM = 1;
	public static final int CANCEL_TRANS_STATUS_CONFIRMED = 2;

	// 线索状态 0：跟进中 1：正在处理 2：已处理 3：无效
	public static final int CLUESSTATUS_DEFAULT = 0;
	public static final int CLUESSTATUS_PROCESSING = 1;
	public static final int CLUESSTATUS_SUCCESS = 2;
	public static final int CLUESSTATUS_SUBMIT_INVALID = 3;

	//车源状态 0 下线 3 上线
	public static final int VEHICLE_STATUS_OFFLINE = 0;
	public static final int VEHICLE_STATUS_ONLINE = 3;

	//0:订单未获取 1:订单获取中 2:订单获取成功 3:订单获取失败
	public static final int DID_NOT_GET = 0;
	public static final int OBTAING = 1;
	public static final int GET_SUCC = 2;
	public static final int GET_FAIL = 3;

	public static final int PREPAY_SELECT_PHOTO = 0x889;//支付定金选择照片
	public static final int PREPAY_TAKE_PHOTO = 0X899;//支付定金拍摄照片

	//公海客户级别  1:无效 2:不跟进N 3:普通B
	public static final int NOINTERESTED = 2;
	public static final int INTERESTED = 3;

	//买家排序  1:无效 2:升序 3:降序
	public static final int ASCENDING = 2;
	public static final int DESCENDING = 3;
	//买家排序规则  1:无效 2:级别 3:回访时间
	public static final int LEVEL = 2;
	public static final int REVISIT = 3;
	//买家筛选  1:无效 2:H级 3:A级 4:B级 5:今日回访 6:今日失败 7:全部
	public static final int LEVELH = 2;
	public static final int LEVELA = 3;
	public static final int LEVELB = 4;
	public static final int TODAY_VISIT = 5;
	public static final int TODAY_FAIL = 6;
	public static final int ALL = 7;

	//获取品牌车系请求code
	public static final int REQUEST_GET_BRAND_CLASS = 100;

	//回访任务来源  0：我的回访   10：待回访列表 20：未推带看买家列表
	public static final int REQUEST_REVISIT_SELF = 0;
	public static final int REQUEST_REVISIT_PENDING = 10;
	public static final int REQUEST_REVISIT_UNVIEW = 20;

	//根据客户列表类型查看数据
	public static final int REQUEST_CUSTOMER_LIST_MINE = 1;//我的回访
	public static final int REQUEST_CUSTOMER_LIST_RECYCLED = 2;//回炉回访
	public static final int REQUEST_CUSTOMER_LIST_NOPUSH = 3;//未推出带看
	public static final int REQUEST_CUSTOMER_LIST_NODOOR = 4;//未上门
	public static final int REQUEST_CUSTOMER_LIST_DOOR = 5;//已上门
	public static final int REQUEST_CUSTOMER_LIST_RECOMMENDED = 6;//新车推荐

	/**
	 * 车源推荐状态
	 * 0待审核 1线索无效 2待处理 3成功返利
	 */
	public static final String VEHICLE_STATUS_CHECK_PENDING = "待审核";
	public static final String VEHICLE_STATUS_CLUE_INVALID = "线索无效";
	public static final String VEHICLE_STATUS_TO_OPERATE = "待处理";
	public static final String VEHICLE_STATUS_SUCCESS_RETURN = "成功返利";

	/*---------------自动更新 start ---------------------------*/
	//fins页面的广播
	public static final String ACTION_FINISH_MAIN = "action_finish_main";
	/**
	 * 1、关闭对话框
	 */
	public static final int OP_DIS_DOWN_DIALOG = 1;

	/**
	 * 1、卖车神器
	 */
	public static final int APP_MCSQ = 1;

	/**
	 * 2、好车邦
	 */
	public static final int APP_HCB = 2;

	/**
	 * 1、普通更新
	 */
	public static final int UPDATE_TYPE_NORMAL = 1;

	/**
	 * 2、紧急更新
	 */
//    public static final int UPDATE_TYPE_FORCE = 2;

	/**
	 * 下载id
	 */
	public static final String KEY_DOWNLOAD_ID = "download_id";

	public static final String SP_UPDATE_TYPE = "update_type";

	public static final String BINDLE_VERSION_NAME = "version_name";

	public static final String BINDLE_UPDATE_CONTENT = "update_content";
	/*---------------自动更新 end ---------------------------*/
	public static final String LOG_TAG = "OKHttpManager";
	public static final String NORMAL_REQUEST_PARAMS = "req";

}