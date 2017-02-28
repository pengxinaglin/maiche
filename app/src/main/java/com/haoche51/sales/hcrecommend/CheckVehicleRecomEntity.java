package com.haoche51.sales.hcrecommend;

/**
 * 车源推荐实体类
 * Created by wfx on 2016/3/28.
 */
public class CheckVehicleRecomEntity {
  private int id;

  /**
   * 车源状态
   * 0待审核 1线索无效(中介) 2线索正常 3线索无效(其他原因)
   */
  private int status;

  /**
   * 车主姓名
   */
  private String name;

  /**
   * 车主电话
   */
  private String phone;

  /**
   * 品牌名称
   */
  private String brand_name;

  /**
   * 车系名称
   */
  private String class_name;

  /**
   * 时间
   */
  private int create_time;


  /**
   * 当月奖金
   */
  private int month;

  /**
   * 历史奖金
   */
  private int total;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getBrand_name() {
    return brand_name;
  }

  public void setBrand_name(String brand_name) {
    this.brand_name = brand_name;
  }

  public String getClass_name() {
    return class_name;
  }

  public void setClass_name(String class_name) {
    this.class_name = class_name;
  }

  public int getCreate_time() {
    return create_time;
  }

  public void setCreate_time(int create_time) {
    this.create_time = create_time;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
