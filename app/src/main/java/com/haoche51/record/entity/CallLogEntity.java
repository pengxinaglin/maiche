package com.haoche51.record.entity;

public class CallLogEntity {
	/** 这条记录的_id 系统 */
	private int _id;

	/** 对方电话 */
	private String callNumber;
	/** 对方名称 */
	private String callName;
	/** 通话类型 来电:1,拨出:2,未接:3 */
	private int callType;
	/** 通话时长 */
	private long callDuration;
	/** 通话时间 */
	private long callDate;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public long getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(long callDuration) {
		this.callDuration = callDuration;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public void setCallNumber(String callNumber) {
		this.callNumber = callNumber;
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public long getCallDuratioin() {
		return callDuration;
	}

	public void setCallDuratioin(long callDuration) {
		this.callDuration = callDuration;
	}

	public long getCallDate() {
		return callDate;
	}

	public void setCallDate(long callDate) {
		this.callDate = callDate;
	}
}
