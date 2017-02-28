package me.iwf.photopicker.entity;

import java.io.Serializable;

/**
 * 已经压缩过的照片的实体类
 * Created by wfx on 2015/12/29.
 */
public class CompressedPhotoEntity implements Serializable{
	/**
	 * 主键id
	 */
	private int id;
	/**
	 * sd卡上照片名称
	 */
	private String sd_photo_name;
	/**
	 * 本地（手机）存储上压缩后的照片名称
	 */
	private String local_photo_name;
	/**
	 * 照片压缩时的时间戳
	 */
	private long create_mills;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSd_photo_name() {
		return sd_photo_name;
	}

	public void setSd_photo_name(String sd_photo_name) {
		this.sd_photo_name = sd_photo_name;
	}

	public String getLocal_photo_name() {
		return local_photo_name;
	}

	public void setLocal_photo_name(String local_photo_name) {
		this.local_photo_name = local_photo_name;
	}

	public long getCreate_mills() {
		return create_mills;
	}

	public void setCreate_mills(long create_mills) {
		this.create_mills = create_mills;
	}
}