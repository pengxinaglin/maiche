package com.haoche51.sales.hctransaction;

import java.io.Serializable;

/**
 * 优惠券
 * Created by mingzheng on 2015/12/24.
 */
public class CouponEntity implements Serializable {

  /**
   * id : 46572
   * coupon_id : c743e2c7827eeef57f5d3bbd7cca5a89
   * phone : 13810281143
   * type : 13
   * code :
   * amount : 1111
   * from_time : 1447174588
   * expire_time : 1448553599
   * create_time : 1447174588
   * status : 0
   * update_time : 2015-11-11 00:56:28
   * car_id : 0
   * bak : 双十一活动
   * title : 好车无忧购车券
   * url : http://m.haoche51.com/coupon/detail?coupon_id=c743e2c7827eeef57f5d3bbd7cca5a89
   */

  private String id;
  private String coupon_id;
  private String phone;
  private String type;
  private String code;
  private String amount;
  private String from_time;
  private String expire_time;
  private String create_time;
  private String status;
  private String update_time;
  private String car_id;
  private String bak;
  private String title;
  private String url;

  public void setId(String id) {
    this.id = id;
  }

  public void setCoupon_id(String coupon_id) {
    this.coupon_id = coupon_id;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public void setFrom_time(String from_time) {
    this.from_time = from_time;
  }

  public void setExpire_time(String expire_time) {
    this.expire_time = expire_time;
  }

  public void setCreate_time(String create_time) {
    this.create_time = create_time;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUpdate_time(String update_time) {
    this.update_time = update_time;
  }

  public void setCar_id(String car_id) {
    this.car_id = car_id;
  }

  public void setBak(String bak) {
    this.bak = bak;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public String getCoupon_id() {
    return coupon_id;
  }

  public String getPhone() {
    return phone;
  }

  public String getType() {
    return type;
  }

  public String getCode() {
    return code;
  }

  public String getAmount() {
    return amount;
  }

  public String getFrom_time() {
    return from_time;
  }

  public String getExpire_time() {
    return expire_time;
  }

  public String getCreate_time() {
    return create_time;
  }

  public String getStatus() {
    return status;
  }

  public String getUpdate_time() {
    return update_time;
  }

  public String getCar_id() {
    return car_id;
  }

  public String getBak() {
    return bak;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }
}
