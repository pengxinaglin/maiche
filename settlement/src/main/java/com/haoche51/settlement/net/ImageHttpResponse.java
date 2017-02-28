package com.haoche51.settlement.net;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageHttpResponse {

	private int code ;
	private String msg;
	public ImageHttpResponse() {
		code = -2;
		msg = null;
	}
	public ImageHttpResponse(String result) {
		if (result == null) {
			code = -2;
		}else {
			try {
				JSONObject obj = new JSONObject(result);
				if (obj.has("code")){
					code = obj.getInt("code");
				}
				if (obj.has("msg")){
					msg = obj.getString("msg");
				}
			}catch (JSONException e){
				
			}
		}
		
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}
