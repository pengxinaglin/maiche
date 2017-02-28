package com.haoche51.sales.hctransaction;

import java.util.List;

/**
 * Created by yangming on 2015/10/30.
 */
public class VehicleSubscribeRuleEntity {

  /**
   * subscribe_series : [{"id":"577","name":"蒙迪欧-致胜"},{"id":"657","name":"科鲁兹"},{"id":"982","name":"英朗"}]
   * emission : 1
   * gearbox : 2
   * year : [3,5]
   * price : [12,15]
   */

  private SubscribeRuleEntity subscribe_rule;
  /**
   * subscribe_rule : {"subscribe_series":[{"id":"577","name":"蒙迪欧-致胜"},{"id":"657","name":"科鲁兹"},{"id":"982","name":"英朗"}],"emission":1,"gearbox":2,"year":[3,5],"price":[12,15]}
   * comment : 上门带看失败，自动加入地销客户列表
   */

  private String comment;
  private int level;

  public void setSubscribe_rule(SubscribeRuleEntity subscribe_rule) {
    this.subscribe_rule = subscribe_rule;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public SubscribeRuleEntity getSubscribe_rule() {
    return subscribe_rule;
  }

  public String getComment() {
    return comment;
  }

  public static class SubscribeRuleEntity {
    private int emission;
    private int gearbox;
    /**
     * id : 577
     * name : 蒙迪欧-致胜
     */

    private List<VehicleSeriesEntity> subscribe_series;
    private List<Integer> year;
    private List<Float> price;
    private List<Integer> vehicle_structure;
    private List<Integer> vehicle_color_type;
    private List<Float> emission_value;

    public List<Integer> getVehicle_structure() {
      return vehicle_structure;
    }

    public void setVehicle_structure(List<Integer> vehicle_structure) {
      this.vehicle_structure = vehicle_structure;
    }

    public List<Integer> getVehicle_color_type() {
      return vehicle_color_type;
    }

    public void setVehicle_color_type(List<Integer> vehicle_color_type) {
      this.vehicle_color_type = vehicle_color_type;
    }

    public List<Float> getEmission_value() {
      return emission_value;
    }

    public void setEmission_value(List<Float> emission_value) {
      this.emission_value = emission_value;
    }

    public void setEmission(int emission) {
      this.emission = emission;
    }

    public void setGearbox(int gearbox) {
      this.gearbox = gearbox;
    }

    public void setSubscribe_series(List<VehicleSeriesEntity> subscribe_series) {
      this.subscribe_series = subscribe_series;
    }

    public void setYear(List<Integer> year) {
      this.year = year;
    }

    public void setPrice(List<Float> price) {
      this.price = price;
    }

    public int getEmission() {
      return emission;
    }

    public int getGearbox() {
      return gearbox;
    }

    public List<VehicleSeriesEntity> getSubscribe_series() {
      return subscribe_series;
    }

    public List<Integer> getYear() {
      return year;
    }

    public List<Float> getPrice() {
      return price;
    }
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
