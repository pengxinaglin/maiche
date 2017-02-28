package com.haoche51.sales.entity;

/***
 * 百度定位实体
 */
public class HCBDloactionEntity {
	private double latitude;
	private double longitude;
	private String time;

	public HCBDloactionEntity() {

	}

	public HCBDloactionEntity(double latitude, double longitude, String time) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HCBDloactionEntity other = (HCBDloactionEntity) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		return Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);

	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
