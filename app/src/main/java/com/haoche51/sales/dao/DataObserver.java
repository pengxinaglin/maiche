package com.haoche51.sales.dao;

import android.os.Bundle;
import android.os.Handler;

public abstract class DataObserver {
	protected Handler handler = null;

	public DataObserver(Handler handler) {
		this.handler = handler;
	}

	public abstract void onChanged();

	public void onChanged(Bundle data) {

	}
}