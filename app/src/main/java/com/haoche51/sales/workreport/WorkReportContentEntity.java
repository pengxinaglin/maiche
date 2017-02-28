package com.haoche51.sales.workreport;

import com.haoche51.sales.util.UnixTimeUtil;

import java.io.Serializable;
import java.util.List;

public class WorkReportContentEntity implements Serializable {

    // 创建时间
    private int create_time;
    // 内容
    private String content;
    // 地点
    private String place;

    // 照片
    private String photo_url;
    // 回复
    private List<WorkReportReplyEntity> reply;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public String getCreate_time() {
        return UnixTimeUtil.format(create_time, "HH:mm:ss");
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setReply(List<WorkReportReplyEntity> reply) {
        this.reply = reply;
    }

    public List<WorkReportReplyEntity> getReply() {
        return reply;
    }
}
