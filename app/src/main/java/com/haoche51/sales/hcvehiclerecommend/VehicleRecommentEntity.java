package com.haoche51.sales.hcvehiclerecommend;

import java.io.Serializable;

/**
 * Created by mac on 15/10/30.
 */
public class VehicleRecommentEntity implements Serializable {

  /**
   * matched_buyer_count : 1
   * vehicle_source_id : 10914
   * vehicle_name : 阿尔法罗密欧 ALFA 156 2004款 2.0 AT
   * seller_price : 14.5
   * register_time : 1280592000
   * miles : 7.7
   * gearbox : 手自一体
   * image_url : http://image1.haoche51.com/0ad6f36e6c3a64e48bab3eebb048aac6c79269f2.jpg
   */

  private int matched_buyer_count;
  private int vehicle_source_id;
  private String vehicle_name;
  private float seller_price;
  private int register_time;
  private float miles;
  private String gearbox;
  private String image_url;
  private int online_time;

  public String[] getCover_image_urls() {
    return cover_image_urls;
  }

  public void setCover_image_urls(String[] cover_image_urls) {
    this.cover_image_urls = cover_image_urls;
  }

  private String[] cover_image_urls=null;

  public void setMatched_buyer_count(int matched_buyer_count) {
    this.matched_buyer_count = matched_buyer_count;
  }

  public void setVehicle_source_id(int vehicle_source_id) {
    this.vehicle_source_id = vehicle_source_id;
  }

  public void setVehicle_name(String vehicle_name) {
    this.vehicle_name = vehicle_name;
  }

  public void setSeller_price(float seller_price) {
    this.seller_price = seller_price;
  }

  public void setRegister_time(int register_time) {
    this.register_time = register_time;
  }

  public void setMiles(float miles) {
    this.miles = miles;
  }

  public void setGearbox(String gearbox) {
    this.gearbox = gearbox;
  }

  public void setImage_url(String image_url) {
    this.image_url = image_url;
  }

  public int getMatched_buyer_count() {
    return matched_buyer_count;
  }

  public int getVehicle_source_id() {
    return vehicle_source_id;
  }

  public String getVehicle_name() {
    return vehicle_name;
  }

  public float getSeller_price() {
    return seller_price;
  }

  public int getRegister_time() {
    return register_time;
  }

  public float getMiles() {
    return miles;
  }

  public String getGearbox() {
    return gearbox;
  }

  public String getImage_url() {
    return image_url;
  }

  public int getOnline_time() {
    return online_time;
  }

  public void setOnline_time(int online_time) {
    this.online_time = online_time;
  }
}
