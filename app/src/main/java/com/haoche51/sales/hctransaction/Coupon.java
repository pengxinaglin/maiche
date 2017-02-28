package com.haoche51.sales.hctransaction;

import java.io.Serializable;

/**
 * 大礼包
 * Created by xuhaibo on 15/10/13.
 */
public class Coupon implements Serializable {
  private int id;
  private int type;
  private String title;
  private int cost;

  public int getId() {
    return id;
  }

  public int getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return title;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }
}
