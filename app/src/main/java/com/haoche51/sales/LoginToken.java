package com.haoche51.sales;


public class LoginToken {

	private String token;
	private int timeOut;
	private int create_time;

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public int getCreate_time() {
		return create_time;
	}
}
