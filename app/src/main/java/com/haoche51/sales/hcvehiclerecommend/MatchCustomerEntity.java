package com.haoche51.sales.hcvehiclerecommend;

import java.util.List;

/**
 * Created by yangming on 2015/11/5.
 */
public class MatchCustomerEntity {

  /**
   * buyer_name : 张先生
   * buyer_phone : 13520173050
   * subscribe_rule : {"subscribe_series":[{"id":"577","name":"蒙迪欧-致胜"},{"id":"657","name":"科鲁兹"},{"id":"982","name":"英朗"}],"emission":1,"gearbox":2,"year":[3,5],"price":[12,15]}
   * comment : xxxxxxx
   */

  private String buyer_name;
  private String buyer_phone;
  /**
   * subscribe_series : [{"id":"577","name":"蒙迪欧-致胜"},{"id":"657","name":"科鲁兹"},{"id":"982","name":"英朗"}]
   * emission : 1
   * gearbox : 2
   * year : [3,5]
   * price : [12,15]
   */

  private SubscribeRuleEntity subscribe_rule;
  private String comment;

  public void setBuyer_name(String buyer_name) {
    this.buyer_name = buyer_name;
  }

  public void setBuyer_phone(String buyer_phone) {
    this.buyer_phone = buyer_phone;
  }

  public void setSubscribe_rule(SubscribeRuleEntity subscribe_rule) {
    this.subscribe_rule = subscribe_rule;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getBuyer_name() {
    return buyer_name;
  }

  public String getBuyer_phone() {
    return buyer_phone;
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

    private List<SubscribeSeriesEntity> subscribe_series;
    private List<Integer> year;
    private List<Float> price;

    public void setEmission(int emission) {
      this.emission = emission;
    }

    public void setGearbox(int gearbox) {
      this.gearbox = gearbox;
    }

    public void setSubscribe_series(List<SubscribeSeriesEntity> subscribe_series) {
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

    public List<SubscribeSeriesEntity> getSubscribe_series() {
      return subscribe_series;
    }

    public List<Integer> getYear() {
      return year;
    }

    public List<Float> getPrice() {
      return price;
    }

    public static class SubscribeSeriesEntity {
      private String id;
      private String name;

      public void setId(String id) {
        this.id = id;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getId() {
        return id;
      }

      public String getName() {
        return name;
      }
    }
  }
}
