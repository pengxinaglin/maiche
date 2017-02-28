package com.haoche51.sales.hccustomer;

import java.util.List;

/**
 * Created by yangming on 2016/5/12.
 */
public class RevisitEntity {

    private String phone;
    private String name;
    private int create_time;//创建时间
    private String comment;
    private SubscribeRuleEntity subscribe_rule;
    private int status;//预约状态，0,提交预约; 1,取消试驾订单; 2,试驾完成后取消; 3,试驾完成后并支付定金 4,试驾完成后并全额支付 5,交易完成
    private int level;

    private int car_dealer = -1;//是否是车商 1是 0否

    public int getCar_dealer() {
        return car_dealer;
    }

    public void setCar_dealer(int car_dealer) {
        this.car_dealer = car_dealer;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setSubscribe_rule(SubscribeRuleEntity subscribe_rule) {
        this.subscribe_rule = subscribe_rule;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public int getCreate_time() {
        return create_time;
    }

    public String getComment() {
        return comment;
    }

    public SubscribeRuleEntity getSubscribe_rule() {
        return subscribe_rule;
    }

    public int getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }


    public static class SubscribeRuleEntity {
        /**
         * subscribe_series : [{"id":"577","name":"蒙迪欧-致胜"},{"id":"657","name":"科鲁兹"},{"id":"982","name":"英朗"}]
         * emission : 1
         * gearbox : 2
         * year : [3,5]
         * price : [12,15]
         */

        private int emission;//排放标准
        private int gearbox;//变数箱
        private List<SubscribeSeriesEntity> subscribe_series;//订阅车系信息包含id 和name
        private List<Integer> year;//年份区间[low,high]
        private List<Float> price;//价格区间[low,high]

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
            /**
             * id : 577
             * name : 蒙迪欧-致胜
             */

            private String id;//车系id
            private String name;//车系名称

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
