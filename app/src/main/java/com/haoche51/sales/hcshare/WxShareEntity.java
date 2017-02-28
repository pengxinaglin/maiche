package com.haoche51.sales.hcshare;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by PengXianglin on 16/8/16.
 */
public class WxShareEntity implements Parcelable {
	private String title;
	private String content;
	private String url;
	private String qrcode;
	private List<String> images;



	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.content);
		dest.writeString(this.url);
		dest.writeString(this.qrcode);
		dest.writeStringList(this.images);
	}

	public WxShareEntity() {
	}

	protected WxShareEntity(Parcel in) {
		this.title = in.readString();
		this.content = in.readString();
		this.url = in.readString();
		this.qrcode = in.readString();
		this.images = in.createStringArrayList();
	}

	public static final Parcelable.Creator<WxShareEntity> CREATOR = new Parcelable.Creator<WxShareEntity>() {
		@Override
		public WxShareEntity createFromParcel(Parcel source) {
			return new WxShareEntity(source);
		}

		@Override
		public WxShareEntity[] newArray(int size) {
			return new WxShareEntity[size];
		}
	};
}
