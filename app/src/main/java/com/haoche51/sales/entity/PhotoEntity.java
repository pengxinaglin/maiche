package com.haoche51.sales.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 相机 拍摄照片
 */
public class PhotoEntity implements Parcelable{
	private int mode; // 模式。1，标准， 2 自由图，3 瑕疵。
	private int index; // 默认上传照片顺序
	private int enumeration;// 枚举值
	private String name;// 名称
	private int type;//PictureConstants.type
	private String path;// 存储路径
	private float position_x;
	private float position_y;
	private String temp_path; //上传时压缩临时存储位置
	private String url; //上传后的url
	private String unid;

	private int create_time;//数码相机拍摄照片的实际时间

	public PhotoEntity() {
		mode = 0;
		index = 0;
		path = null;
	}

	public PhotoEntity(int mode, int index, String path) {
		this.mode = mode;
		this.index = index;
		this.path = path;
	}

	public PhotoEntity(int mode, int index, int enumeration, String path) {
		this.mode = mode;
		this.index = index;
		this.path = path;
		this.enumeration = enumeration;
	}

	/**
	 * 相册选择照片时，只记住类型，名字和 path
	 */
	public PhotoEntity(int type, String name, String path) {
		this.type = type;
		this.name = name;
		this.path = path;
	}

	public PhotoEntity(int mode, int index, int enumeration, int type, String name, String path) {
		this.name = name;
		this.path = path;
		this.type = type;
		this.index = index;
		this.mode = mode;
		this.enumeration = enumeration;
	}


	public void setPosition(String path) {
		Pattern mPattern = Pattern.compile(".*scratch_[0-9]+_([0-9]+)_([0-9]+).*");
		Matcher mMatcher = mPattern.matcher(path);
		if (mMatcher.find() && mMatcher.groupCount() == 2) {
			position_x = Float.parseFloat(mMatcher.group(1)) / 10000;
			position_y = Float.parseFloat(mMatcher.group(2)) / 10000;
		}
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getEnumeration() {
		return enumeration;
	}

	public void setEnumeration(int enumeration) {
		this.enumeration = enumeration;
	}

	public float getPosition_x() {
		return position_x;
	}

	public void setPosition_x(float position_x) {
		this.position_x = position_x;
	}

	public float getPosition_y() {
		return position_y;
	}

	public void setPosition_y(float position_y) {
		this.position_y = position_y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTemp_path() {
		return temp_path;
	}

	public void setTemp_path(String temp_path) {
		this.temp_path = temp_path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public int getCreate_time() {
		return create_time;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mode);
		dest.writeInt(index);
		dest.writeInt(enumeration);
		dest.writeString(name);
		dest.writeInt(type);
		dest.writeString(path);
		dest.writeFloat(position_x);
		dest.writeFloat(position_y);
		dest.writeString(temp_path);
		dest.writeString(url);
		dest.writeString(unid);
		dest.writeInt(create_time);
	}

	public static final Parcelable.Creator<PhotoEntity> CREATOR=new Parcelable.Creator<PhotoEntity>(){

		@Override
		public PhotoEntity createFromParcel(Parcel source) {
			return new PhotoEntity(source);
		}

		@Override
		public PhotoEntity[] newArray(int size) {
			return new PhotoEntity[size];
		}
	};

	public PhotoEntity(Parcel source){
		mode=source.readInt();
		index=source.readInt();
		enumeration=source.readInt();
		name=source.readString();
		type=source.readInt();
		path=source.readString();
		position_x=source.readFloat();
		position_y=source.readFloat();
		temp_path=source.readString();
		url=source.readString();
		unid=source.readString();
		create_time=source.readInt();
	}
}
