package com.haoche51.sales.helper;

import java.io.InputStream;

import android.content.Context;

public class ResourceHelper {
	private Context context;

	public ResourceHelper(Context context) {
		this.context = context;
	}

	public String getString(int res) {
		return context.getResources().getString(res);
	}

	public String getString(int res, java.lang.Object... formatArgs) {
		return context.getResources().getString(res, formatArgs);
	}

	public int getColor(int res) {
		return context.getResources().getColor(res);
	}

	public InputStream getRawResource(int res) {
		return context.getResources().openRawResource(res);
	}

	public String[] getArray(int res) {
		return context.getResources().getStringArray(res);
	}

	public float getFontSize(int res) {
		return context.getResources().getDimension(res);
	}

	public int getDimenPx(int res) {
		return context.getResources().getDimensionPixelOffset(res);
	}
}
