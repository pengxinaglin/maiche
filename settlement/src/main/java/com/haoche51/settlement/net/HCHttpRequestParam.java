package com.haoche51.settlement.net;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.haoche51.settlement.utils.DESUtil;
import com.haoche51.settlement.utils.DeviceInfoUtil;
import com.haoche51.settlement.utils.HCLogUtil;
import com.haoche51.settlement.utils.NetInfoUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求参数配置
 * Created by xuhaibo on 15/8/25.
 */
public class HCHttpRequestParam {
    public static final String TAG = "HCHttpRequestParam";
    public static final String TOKEN = "sales_app_20160226";
    public static final String VCODE_TOKEN = "haoche51@572_cashier_app_server";
    private static Gson mGson = new Gson();


    ///////////////////////////////////// 支付接口 开始 /////////////////////////////////////////////////////

    /**
     * 创建POS机充值订单/newPosPay
     *
     * @param context       context
     * @param crm_user_id   crm_user_id
     * @param crm_user_name crm_user_name
     * @param buyer_phone   买家电话
     * @param trade_amount  必传，充值金额，单位分
     * @param task_type     非必，任务类型，1c2c交易 2回购 3金融
     * @param task_number   非必，任务编号
     * @param comment       非必，备注
     * @return map
     */
    public static Map<String, Object> newPosPay(Context context, String crm_user_id, String crm_user_name
            , String buyer_phone, String trade_amount, int task_type, String task_number, String comment, String appToken) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> user_info = new HashMap<>();
        user_info.put("id", crm_user_id);
        user_info.put("name", crm_user_name);

        params.put("user_info", user_info);//

        params.put("buyer_phone", buyer_phone);//操作员
        params.put("trade_amount", trade_amount);
        if (task_type != -1) {
            params.put("task_type", task_type);
        }
        if (!TextUtils.isEmpty(task_number)) {
            params.put("task_number", task_number);
        }
        params.put("comment", comment);
        return getRequest(context, HttpConstants.ACTION_NEW_POS_PAY, params, appToken);
    }


    /**
     * 确认POS机支付/confirmPosPay
     *
     * @param context       context
     * @param crm_user_id   crm_user_id
     * @param crm_user_name crm_user_name
     * @param trade_number  必传，交易编号
     * @param trade_status  必传，交易状态，20已确认 30支付失败 40无效
     * @param pay_type      必传，支付方式，1刷卡 2微信 3支付宝
     * @param card_type     必传，卡类型，1借记卡 2信用卡
     * @param fee           手续费，单位：分
     * @param extJson       string, 连连返回的Json
     * @return
     */
    public static Map<String, Object> confirmPosPay(Context context, String crm_user_id, String crm_user_name
            , String trade_number, int trade_status, String pay_type,String card_type, String ret_code, String ret_msg, String appToken, long fee, String extJson) {

        Map<String, Object> params = new HashMap<>();
        Map<String, Object> user_info = new HashMap<>();
        user_info.put("id", crm_user_id);
        user_info.put("name", crm_user_name);

        params.put("user_info", user_info);//

        params.put("trade_number", trade_number);
        params.put("trade_status", trade_status);

        String payType = "1";
        if ("0".equals(pay_type)) {
            payType = "2";
        } else if ("1".equals(pay_type)) {
            payType = "1";
        } else if ("5".equals(pay_type)) {
            payType = "3";
        }
        params.put("pay_type", payType);

        String cardType = "0";
        if ("0".equals(card_type)) {
            cardType = "1";
        } else if ("1".equals(card_type)) {
            cardType = "2";
        }
        params.put("card_type", cardType);

        if (!TextUtils.isEmpty(ret_code)) {
            params.put("ret_code", ret_code);
        }
        if (!TextUtils.isEmpty(ret_msg)) {
            params.put("ret_msg", ret_msg);
        }
        params.put("fee", fee);
        params.put("ext_json", extJson);

        return getRequest(context, HttpConstants.ACTION_CONFIRM_POSPAY, params, appToken);
    }


    /**
     * 获取账户详情/getAccountDetail
     *
     * @param context     context
     * @param buyer_phone buyer_phone
     * @param and_create  非必，1如果不存在账户那么创建 0如果不存在账户那么返回错误码
     * @param appToken    appToken
     * @return map
     */
    public static Map<String, Object> getAccountDetail(Context context, String buyer_phone, int and_create, String appToken) {

        Map<String, Object> params = new HashMap<>();

        params.put("buyer_phone", buyer_phone);//
        params.put("and_create", and_create);//


        return getRequest(context, HttpConstants.ACTION_GETACCOUNTDETAIL, params, appToken);
    }

    /**
     * 连连订单查询
     *
     * @param no_order    商家订单号
     * @param oid_partner 商户号
     * @param sign        全参数加签
     * @return
     */
    public static Map<String, Object> getQueryLianlian(String no_order, String
            oid_partner, String sign) {

        Map<String, Object> params = new HashMap<>();
        params.put("no_order", no_order);
        params.put("oid_partner", oid_partner);
        params.put("sign_type", "RSA");
        params.put("sign", sign);

        return params;
    }

    ///////////////////////////////////// 支付接口 结束/////////////////////////////////////////////////////


    /**
     * 发送短信授权码
     *
     * @param check_user_id crm 登录账户id
     * @param phone         接受授权码的手机
     * @param type          1 收款验证码，2 退款验证码
     * @return map
     */
    public static Map<String, Object> sendVCode(Context context, String check_user_id, String
            phone, int type, String appToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("crm_user_id", check_user_id);
        params.put("phone", phone);
        params.put("type", type);

        return getRequest(context, HttpConstants.ACTION_SEND_VCODE, params, appToken);
    }


    /**
     * 验证授权码
     *
     * @param vcode 验证码
     * @param phone 接受授权码的手机
     * @return map
     */
    public static Map<String, Object> checkVCode(Context context, String vcode, String
            phone, String appToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("vcode", vcode);
        params.put("phone", phone);

        return getRequest(context, HttpConstants.ACTION_CHECK_VCODE, params, appToken);
    }

    /**
     * 获取交易明细列表
     */
    public static Map<String, Object> getBalanceList(Context context, String crm_user_id, String crm_user_name,
                                                     String buyer_phone, int page, int limit, String appToken) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> user_info = new HashMap<>();
        user_info.put("id", crm_user_id);
        user_info.put("name", crm_user_name);
        params.put("user_info", user_info);
        params.put("page", page);
        params.put("limit", limit);
        if (!TextUtils.isEmpty(buyer_phone)) {
            params.put("buyer_phone", buyer_phone);
        }
        return getRequest(context, HttpConstants.ACTION_GET_BALANCE_LIST, params, appToken);
    }

    /**
     * 创建现金充值订单/newOfflinePay
     *
     * @param context       context
     * @param crm_user_id   crm_user_id
     * @param crm_user_name crm_user_name
     * @param buyer_phone   买家电话
     * @param trade_amount  必传，退款金额，单位分
     * @param card_name     必传，持卡人姓名
     * @param card_bank     必传，开户银行
     * @param card_number   非必传，
     * @return map
     */
    public static Map<String, Object> newReturn(Context context, String crm_user_id, String crm_user_name
            , String buyer_phone, String trade_amount, String card_name, String card_bank, String card_number, String comment, String appToken) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> user_info = new HashMap<>();
        user_info.put("id", crm_user_id);
        user_info.put("name", crm_user_name);

        params.put("user_info", user_info);//

        params.put("buyer_phone", buyer_phone);//操作员
        params.put("trade_amount", trade_amount);
        params.put("card_name", card_name);
        params.put("card_bank", card_bank);
        params.put("card_number", card_number);
        if (!TextUtils.isEmpty(comment)) {
            params.put("comment", comment);
        }
        return getRequest(context, HttpConstants.ACTION_NEW_RETURN, params, appToken);
    }

    /**
     * 封装请求参数String
     *
     * @param action
     * @param params
     * @return
     */
    private static Map<String, Object> getRequest(Context context, String action, Map<String, Object> params, String appToken) {
        Map<String, Object> msg = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        msg.put("action", action);
        msg.put("params", params);

        request.put("action", action);
        request.put("web_token", VCODE_TOKEN);
        request.put("stat", getRequestStat(context));
        String msgEncode = "";
        try {
            msgEncode = DESUtil.encryptDES(new Gson().toJson(msg), appToken);
            HCLogUtil.d(TAG, "msg---msgEncode -->" + msgEncode);
            String msgDecode = DESUtil.decryptDES(msgEncode, appToken);
            HCLogUtil.d(TAG, "msg---msgDecode -->" + msgDecode);

        } catch (Exception e) {
            e.printStackTrace();
        }
        request.put("msg", msgEncode);
        request.put("app_token", appToken);


        return request;
    }

    /**
     * 获取手机当前状态信息
     *
     * @return
     */
    private static Map<String, Object> getRequestStat(Context context) {
        Map<String, Object> stat = new HashMap<>();
        stat.put("a_v", DeviceInfoUtil.getAppVersion(context));
        stat.put("s_v", DeviceInfoUtil.getOSVersion());
        stat.put("p_t", DeviceInfoUtil.getPhoneType());
        stat.put("n_s", NetInfoUtil.getNetworkType(context));
        return stat;
    }
}
