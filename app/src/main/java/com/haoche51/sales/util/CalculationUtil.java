package com.haoche51.sales.util;

import android.text.TextUtils;

import com.haoche51.sales.hctransaction.CouponEntity;

/**
 * 服务费等的计算
 * Created by yangming on 2016/1/28.
 */
public class CalculationUtil {

    /**
     * 看车--预定--计算最后服务费
     *
     * @param servicePrice 填写的理论服务费
     * @param couponEntity 优惠券
     * @param reduce       减免金额
     * @return 实际服务费
     */
    public static double transactionPrepayCalculationServicePrice(String servicePrice, CouponEntity couponEntity, String reduce) {
//填写的理论服务费
        double etServicePeice = 0;
        if (!TextUtils.isEmpty(servicePrice)) {
            etServicePeice = Double.valueOf(servicePrice);
        }

        double finalServicePrice = etServicePeice;
        //减去优惠券金额
        if (couponEntity != null) {
            finalServicePrice = HCArithUtil.sub(finalServicePrice, StringUtil.parseDouble(couponEntity.getAmount(), 0));
        }
        //减去减免金额
        if (!TextUtils.isEmpty(reduce)) {
            finalServicePrice = HCArithUtil.sub(finalServicePrice, StringUtil.parseDouble(reduce, 0));
        }
        return finalServicePrice;
    }


    /**
     * 计算理论服务费
     *
     * @param finalPrice 成交价 单位万元
     * @return 理论服务费
     */
    public static double transactionPrepayCalculationTheServicePrice(String finalPrice) {
        //计算理论服务费
        double dealPrice = 0;
        try {
            if (!TextUtils.isEmpty(finalPrice)) {
                dealPrice = HCArithUtil.mul(Double.valueOf(finalPrice), 10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        double servicePrice = HCArithUtil.mul(dealPrice, 0.03);
        //服务费最低是2000
        if (servicePrice < 2000) {
            servicePrice = 2000;
        }

        if (servicePrice > 9000) {
            servicePrice = 9000;
        }

        return servicePrice;
    }
}
