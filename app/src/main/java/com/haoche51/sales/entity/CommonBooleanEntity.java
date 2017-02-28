package com.haoche51.sales.entity;

/**
 * 公共的返回true false的实体 返回实体
 */
public class CommonBooleanEntity {
	// {"errno":0,"errmsg":"","data":true}
	private boolean data;
	private int errno;
	private String errmsg;

	public boolean isData() {
		return data;
	}

	public void setData(boolean data) {
		this.data = data;
	}

	public int getErrno() {
		return errno;
	}

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}
