package com.haoche51.settlement.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.haoche51.settlement.PayDebug;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by lightman_mac on 11/2/15.
 * 统计,不直接调用umeng提供的API,以防被测试环境数据污染
 * <p/>
 * 在这里统一第三方集成
 */
public class ThirdPartInjector {

    /**
     * 事件统计 带参数
     *
     * @param act
     * @param event
     * @param map
     */
    public static void onEvent(Activity act, String event, HashMap map) {
        if (act == null || TextUtils.isEmpty(event)) return;
        if (PayDebug.EVIROMENT == 2) {
            MobclickAgent.onEvent(act, event, map);
        }
    }


}
