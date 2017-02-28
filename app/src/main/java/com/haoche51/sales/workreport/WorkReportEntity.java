package com.haoche51.sales.workreport;

import java.io.Serializable;
import java.util.List;

/**
 * {
 * "total": {
 * "trans_all": "5",
 * "trans_succ": "1"
 * "trans_fail": "1",
 * "reborn": "1",
 * "revisit": "7",
 * "succ_rate": "17.20",
 * },
 * "reply": [{
 * "create_time": "1457678687",
 * "content": "今天天气很好！",
 * "place": "北京市海淀区西二旗-地铁站",
 * "photo": "2363a300783-dac2-4af0-a8c1-5faccfb949b8.jpg"
 * }, {
 * "create_time": "1457678687",
 * "content": "今天天气很好！",
 * "place": "西二旗",
 * "photo": "2363a300783-dac2-4af0-a8c1-5faccfb949b8.jpg"
 * "reply": [{
 * "crm_user_name": "敖忠旭",
 * "content": "领导很满意！"
 * }, {
 * "crm_user_name": "敖忠旭"
 * "content": "继续加油！"
 * }]
 * }]
 * }
 */
public class WorkReportEntity implements Serializable {

    private WorkReportCountEntity total;
    private List<WorkReportContentEntity> reply;

    public void setTotal(WorkReportCountEntity total) {
        this.total = total;
    }

    public WorkReportCountEntity getTotal() {
        return total;
    }

    public void setReply(List<WorkReportContentEntity> reply) {
        this.reply = reply;
    }

    public List<WorkReportContentEntity> getReply() {
        return reply;
    }
}
