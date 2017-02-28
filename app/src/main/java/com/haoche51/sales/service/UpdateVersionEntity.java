package com.haoche51.sales.service;

/**
 * 更新版本实体类
 * Created by wfx on 2016/9/1.
 */
public class UpdateVersionEntity {

    /**
     * 操作：0、初始化更新提示对话框，1、初始化下载对话框，2、更新对话框下载进度 3、关闭对话框
     */
    private int operate;

    /**
     * apk的总大小（byte)
     */
    private long total;

    /**
     * 当前已经下载apk的大小（byte)
     */
    private long current;

    /**
     * 是否最新，0：不是最新包、1：是最新包
     */
    private int is_new;

    /**
     * 更新类型：1：普通更新、2：紧急更新
     */
    private int type;

    /**
     * 包的下载url
     */
    private String url;

    /**
     * 版本名称
     */
    private String version_name;

    /**
     * 更新内容
     */
    private String brief;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getIs_new() {
        return is_new;
    }

    public void setIs_new(int is_new) {
        this.is_new = is_new;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public UpdateVersionEntity(int operate, long total, long current) {
        this.operate = operate;
        this.total = total;
        this.current = current;
    }


    public UpdateVersionEntity(int operate) {
        this.operate = operate;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
