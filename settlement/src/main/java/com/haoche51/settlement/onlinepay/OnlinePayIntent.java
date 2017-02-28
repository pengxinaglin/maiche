package com.haoche51.settlement.onlinepay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.haoche51.settlement.cashiers.SettlementDetailActivity;

@SuppressLint("ParcelCreator")
public class OnlinePayIntent extends Intent {

    public OnlinePayIntent(Context packageContext) {
        super(packageContext, PayListActivity.class);
    }

    /**
     * 设置CRM用户id
     */
    public void setCrmUserId(String crmUserId) {
        if (TextUtils.isEmpty(crmUserId)) {
            return;
        }
        putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, crmUserId);
    }

    /**
     * 设置CRM用户名
     */
    public void setCrmUserName(String crmUserName) {
        if (TextUtils.isEmpty(crmUserName)) {
            return;
        }
        putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, crmUserName);
    }

    /**
     * 设置Token
     */
    public void setAppToken(String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, token);
    }
}
