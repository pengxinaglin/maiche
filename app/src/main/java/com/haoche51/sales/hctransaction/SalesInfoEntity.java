package com.haoche51.sales.hctransaction;

/**
 * Created by PengXianglin on 16/9/8.
 */
public class SalesInfoEntity {

	private int id;//用户id
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
