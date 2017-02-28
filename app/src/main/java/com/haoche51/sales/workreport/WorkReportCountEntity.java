package com.haoche51.sales.workreport;

import java.io.Serializable;

public class WorkReportCountEntity implements Serializable {

    // 看车任务
    private String trans_all;
    // 看车成功
    private String trans_succ;
    // 看车失败
    private String trans_fail;
    // 复活买家
    private String reborn;
    // 回访记录
    private String revisit;
    // 客户成交率
    private String succ_rate;

    public void setTrans_all(String trans_all) {
        this.trans_all = trans_all;
    }

    public String getTrans_all() {
        return trans_all;
    }

    public void setTrans_succ(String trans_succ) {
        this.trans_succ = trans_succ;
    }

    public String getTrans_succ() {
        return trans_succ;
    }

    public void setTrans_fail(String trans_fail) {
        this.trans_fail = trans_fail;
    }

    public String getTrans_fail() {
        return trans_fail;
    }

    public void setReborn(String reborn) {
        this.reborn = reborn;
    }

    public String getReborn() {
        return reborn;
    }

    public void setRevisit(String revisit) {
        this.revisit = revisit;
    }

    public String getRevisit() {
        return revisit;
    }

    public void setSucc_rate(String succ_rate) {
        this.succ_rate = succ_rate;
    }

    public String getSucc_rate() {
        return succ_rate;
    }
}
