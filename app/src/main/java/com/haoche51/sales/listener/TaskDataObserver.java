package com.haoche51.sales.listener;

import android.os.Handler;

/**
 * Created by mac on 15/10/8.
 */
public abstract class TaskDataObserver {
	protected Handler handler = null;

	public TaskDataObserver(Handler handler) {
		this.handler = handler;
	}

	public abstract void onChanged(Object o);
}
