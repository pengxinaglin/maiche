package com.haoche51.settlement.net;

import com.google.gson.Gson;
import com.haoche51.settlement.cashiers.DetailEntity;
import com.haoche51.settlement.cashiers.PospayEntity;
import com.haoche51.settlement.onlinepay.PayDataEntity;

/**
 * 解析json 在界面中只操作javabean
 *
 * @author lightman_mac
 */
public class HCJsonParse {

    public static Gson mGson = new Gson();

    /**
     * 解析看车任务
     */
    public static PospayEntity parseNewPosPay(String result) {
        PospayEntity entity = null;
        try {
            entity = mGson.fromJson(result, PospayEntity.class);
        } catch (Exception e) {
            return null;
        }
        return entity;
    }


    public static DetailEntity parseDetail(String result) {
        DetailEntity entity = null;
        try {
            entity = mGson.fromJson(result, DetailEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    /**
     * 解析收款账单数据
     */
    public static PayDataEntity parsePayDataEntity(String result) {
        PayDataEntity payDataEntity = null;
        try {
            payDataEntity = mGson.fromJson(result, PayDataEntity.class);
        } catch (Exception e) {
            payDataEntity = null;
        }
        return payDataEntity;
    }
}
