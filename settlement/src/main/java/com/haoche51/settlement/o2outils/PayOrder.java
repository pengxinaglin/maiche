package com.haoche51.settlement.o2outils;

import java.io.Serializable;

public class PayOrder implements Serializable {
  public static final String SIGN_TYPE_RSA = "RSA";
  public static final String SIGN_TYPE_MD5 = "MD5";
  private static final long serialVersionUID = 1L;
  private String oid_trader; // 商户号
  private String notify_url; // 连连钱包支付平台在用户支付成功后通知商户服务端的地址
  private String outside_goodsorder; // 商家订单号
  private String name_trader;// 商户名称
  private String price_goods; // 该笔订单的资金总额

  private String user_login;// 操作员号
  private String name_goods;// 商品名称
  private String pos_id;// pos机器
  private String type_pay;//订单类型

  private String sign_type; // 参与签名
  private String sign;

  public String getOid_trader() {
    return oid_trader;
  }

  public void setOid_trader(String oid_trader) {
    this.oid_trader = oid_trader;
  }

  public String getNotify_url() {
    return notify_url;
  }

  public void setNotify_url(String notify_url) {
    this.notify_url = notify_url;
  }

  public String getOutside_goodsorder() {
    return outside_goodsorder;
  }

  public void setOutside_goodsorder(String outside_goodsorder) {
    this.outside_goodsorder = outside_goodsorder;
  }

  public String getName_trader() {
    return name_trader;
  }

  public void setName_trader(String name_trader) {
    this.name_trader = name_trader;
  }

  public String getPrice_goods() {
    return price_goods;
  }

  public void setPrice_goods(String price_goods) {
    this.price_goods = price_goods;
  }

  public String getSign_type() {
    return sign_type;
  }

  public void setSign_type(String sign_type) {
    this.sign_type = sign_type;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getUser_login() {
    return user_login;
  }

  public void setUser_login(String user_login) {
    this.user_login = user_login;
  }

  public String getName_goods() {
    return name_goods;
  }

  public void setName_goods(String name_goods) {
    this.name_goods = name_goods;
  }

  public String getPos_id() {
    return pos_id;
  }

  public void setPos_id(String pos_id) {
    this.pos_id = pos_id;
  }

  public String getType_pay() {
    return type_pay;
  }

  public void setType_pay(String type_pay) {
    this.type_pay = type_pay;
  }

}
