package com.haoche51.sales.hctransaction;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 转账信息
 * Created by PengXianglin on 16/6/30.
 */
public class RefundInfoEntity implements Parcelable {
	private int payMoney;//转账金额
	private String name;//开户姓名
	private String bank;//银行
	private String card;//卡号
	private boolean isToBalance;//是否转账到余额

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public boolean isToBalance() {
		return isToBalance;
	}

	public void setToBalance(boolean toBalance) {
		isToBalance = toBalance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(int payMoney) {
		this.payMoney = payMoney;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.payMoney);
		dest.writeString(this.name);
		dest.writeString(this.bank);
		dest.writeString(this.card);
		dest.writeByte(this.isToBalance ? (byte) 1 : (byte) 0);
	}

	public RefundInfoEntity() {
	}

	protected RefundInfoEntity(Parcel in) {
		this.payMoney = in.readInt();
		this.name = in.readString();
		this.bank = in.readString();
		this.card = in.readString();
		this.isToBalance = in.readByte() != 0;
	}

	public static final Creator<RefundInfoEntity> CREATOR = new Creator<RefundInfoEntity>() {
		@Override
		public RefundInfoEntity createFromParcel(Parcel source) {
			return new RefundInfoEntity(source);
		}

		@Override
		public RefundInfoEntity[] newArray(int size) {
			return new RefundInfoEntity[size];
		}
	};
}
