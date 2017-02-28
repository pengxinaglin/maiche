package com.haoche51.settlement.net;

/**
 * 接口action常量
 */
public class HttpConstants {

    public static final int GET_LIST_REFRESH = 5;//刷新
    public static final int GET_LIST_LOADMORE = 6;//加载更多
    public static final int REQUEST_PAGESIZE = 10;//page size

    /**
     * 创建POS机充值订单/newPosPay
     */
    public static final String ACTION_NEW_POS_PAY = "accountpayapi_newpospay";

    /**
     * 确认POS机支付/confirmPosPay
     */
    public static final String ACTION_CONFIRM_POSPAY = "accountpayapi_confirmpospay";

    /**
     * 获取账户详情/getAccountDetail
     */
    public static final String ACTION_GETACCOUNTDETAIL = "accountpayapi_getaccountdetail";

    /**
     * 获取交易明细列表/getBalanceList
     */
    public static final String ACTION_GET_BALANCE_LIST = "accountpayapi_getbalancelist";

    /**
     * 申请退款/newReturn
     */
    public static final String ACTION_NEW_RETURN = "accountpayapi_newreturn";

    /**
     * 发送验证码
     */
    public static final String ACTION_SEND_VCODE = "send_vcode";

    /**
     * 校验验证码
     */
    public static final String ACTION_CHECK_VCODE = "check_vcode";

}
