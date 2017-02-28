package com.haoche51.settlement.cashiers;

import java.io.Serializable;

/**
 * 未确定支付结果的支付订单
 * Created by yangming on 2016/1/14.
 */
public class PaymentEntity implements Serializable {

    private String trade_number;
    private String name_goods;

    public String getTrade_number() {
        return trade_number;
    }

    public void setTrade_number(String trade_number) {
        this.trade_number = trade_number;
    }

    public String getName_goods() {
        return name_goods;
    }

    public void setName_goods(String name_goods) {
        this.name_goods = name_goods;
    }
}
