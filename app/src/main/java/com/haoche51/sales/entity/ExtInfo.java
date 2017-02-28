package com.haoche51.sales.entity;

import java.io.Serializable;

/**
 * Created by 彭祥林 on 15/8/28.
 */
public class ExtInfo implements Serializable {

  private int emigrate;//外迁
  private int resident;//本市户口
  private int temp_resident;//有无暂住证
  private int company_seller;//车是否公户
  private int company_buyer;//买家是否公户
  private int seller_present;//车主是否到场过户
  private int buyer_present;//买家是否到场过户
  private int loan = -1;//贷款

  public int getEmigrate() {
    return emigrate;
  }

  public void setEmigrate(int emigrate) {
    this.emigrate = emigrate;
  }

  public int getResident() {
    return resident;
  }

  public void setResident(int resident) {
    this.resident = resident;
  }

  public int getTemp_resident() {
    return temp_resident;
  }

  public void setTemp_resident(int temp_resident) {
    this.temp_resident = temp_resident;
  }

  public int getCompany_seller() {
    return company_seller;
  }

  public void setCompany_seller(int company_seller) {
    this.company_seller = company_seller;
  }

  public int getCompany_buyer() {
    return company_buyer;
  }

  public void setCompany_buyer(int company_buyer) {
    this.company_buyer = company_buyer;
  }

  public int getSeller_present() {
    return seller_present;
  }

  public void setSeller_present(int seller_present) {
    this.seller_present = seller_present;
  }

  public int getBuyer_present() {
    return buyer_present;
  }

  public void setBuyer_present(int buyer_present) {
    this.buyer_present = buyer_present;
  }

  public int getLoan() {
    return loan;
  }

  public void setLoan(int loan) {
    this.loan = loan;
  }
}

