package com.haoche51.settlement.cashiers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class SettlementIntent extends Intent {


    public SettlementIntent() {
    }

    private SettlementIntent(Intent o) {
        super(o);
    }

    private SettlementIntent(String action) {
        super(action);
    }

    private SettlementIntent(String action, Uri uri) {
        super(action, uri);
    }

    private SettlementIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public SettlementIntent(Context packageContext) {
        super(packageContext, SettlementDetailActivity.class);
    }

    /**
     * 必传，网络请求的appToken
     *
     * @param token
     */
    public void setAppToken(String token) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, token);
    }


    /**
     * 必传，crm_user_id
     *
     * @param crm_user_id
     */
    public void setCrmUserId(String crm_user_id) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, crm_user_id);
    }


    /**
     * 必传，crm_user_name
     *
     * @param crm_user_name
     */
    public void setCrmUserName(String crm_user_name) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, crm_user_name);
    }

    /**
     * 必传 客户电话号码
     *
     * @param phone phone
     */
    public void setCustomerPhone(String phone) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_PHONE, phone);
    }

    /**
     * 非必传 客户电话号码
     *
     * @param name name
     */
    public void setCustomerName(String name) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_NAME, name);
    }

    /**
     * 非必传 业务订单号，没有不传
     *
     * @param taskId taskId
     */
    public void setTaskId(String taskId) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_TASK_ID, taskId);
    }

    /**
     * 任务类型，1c2c交易 2回购 3金融
     *
     * @param taskType
     */
    public void setTaskType(int taskType) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_TASK_TYPE, taskType);
    }

    /**
     * 应收金额，单位元
     */
    public void setPrice(String price) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_PRICE, price);
    }

    /**
     * 是否从业务方调用
     * true ： 初始进入页面，本次可使用金额为可用余额（如果可用余额小于应收），或者本次可用金额为应收（如果应收小于可用余额），以后每次收款后累加
     * false ： 初始进入页面，本次可用金额为0；以后每次收款收累加
     *
     * @param fromBusiness true （非线上收款和意向金收款）
     */
    public void setFromBusiness(boolean fromBusiness) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_FROM_BUSINESS, fromBusiness);
    }

    /**
     * 是否可以使用现金收款，不传默认false，不使用现金
     */
    public void setCashEnable(boolean cashEnable) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_CASH_ENABLE, cashEnable);
    }

    /**
     * 设置备注
     */
    public void setComment(String comment) {
        this.putExtra(SettlementDetailActivity.KEY_EXTRA_COMMENT, comment);
    }
}
