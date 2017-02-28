package com.haoche51.settlement.onlinepay;

import com.haoche51.settlement.utils.DisplayUtils;
import com.haoche51.settlement.utils.HCArithUtil;
import com.haoche51.settlement.utils.UnixTimeUtil;

import java.io.Serializable;

public class PayListEntity implements Serializable {

    // 创建时间，时间戳
    private int create_time;
    private String[] mDayTimes = new String[2];

    // 交易金额
    private long trade_amount;
    private String mTradeAmount;

    // 用户手机
    private String buyer_phone;

    // 状态：1收款完成  2退款中   3退款完成
    private int show_status;

    // 状态对应显示的文案
    private String show_status_desc;

    // 用户可用余额，单位分
    private long unused_amount;
    private String mUnusedAmount;

    // 备注
    private String comment;

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getCreate_time() {
        return create_time;
    }

    public String[] getDayTimes() {
        if (UnixTimeUtil.isToday(create_time)) {
            mDayTimes[0] = "今天";
            mDayTimes[1] = UnixTimeUtil.format(create_time, UnixTimeUtil.HOURS_MINUTES);
        } else if (UnixTimeUtil.isYesterday(create_time)) {
            mDayTimes[0] = "昨天";
            mDayTimes[1] = UnixTimeUtil.format(create_time, UnixTimeUtil.HOURS_MINUTES);
        } else {
            mDayTimes[0] = UnixTimeUtil.getWeekDay(create_time);
            mDayTimes[1] = UnixTimeUtil.format(create_time, UnixTimeUtil.MONTH_DAY);
        }
        return mDayTimes;
    }

    public void setTrade_amount(long trade_amount) {
        this.trade_amount = trade_amount;
    }

    public long getTrade_amount() {
        return trade_amount;
    }

    public String getTradeAmount() {
        double money = HCArithUtil.div(Double.valueOf(trade_amount), 100);
        mTradeAmount = DisplayUtils.parseMoney("###", money);
        return mTradeAmount;
    }

    public void setBuyer_phone(String buyer_phone) {
        this.buyer_phone = buyer_phone;
    }

    public String getBuyer_phone() {
        return buyer_phone;
    }

    public void setShow_status(int show_status) {
        this.show_status = show_status;
    }

    public long getUnused_amount() {
        return unused_amount;
    }

    public String getUnusedAmount() {
        double money = HCArithUtil.div(Double.valueOf(unused_amount), 100);
        mUnusedAmount = DisplayUtils.parseMoney("###", money);
        return mUnusedAmount;
    }

    public void setShow_status_desc(String show_status_desc) {
        this.show_status_desc = show_status_desc;
    }

    public String getShow_status_desc() {
        return show_status_desc;
    }

    public void setUnused_amount(long unused_amount) {
        this.unused_amount = unused_amount;
    }

    public int getShow_status() {
        return show_status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}