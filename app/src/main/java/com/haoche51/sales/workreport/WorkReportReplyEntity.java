package com.haoche51.sales.workreport;

import java.io.Serializable;

public class WorkReportReplyEntity implements Serializable {

    // 回复人
    private String crm_user_name;
    // 回复内容
    private String content;

    public void setCrm_user_name(String crm_user_name) {
        this.crm_user_name = crm_user_name;
    }

    public String getCrm_user_name() {
        return crm_user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
