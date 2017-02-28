package com.haoche51.sales.hcshare;

/**
 * Created by PengXianglin on 16/8/17.
 */
public class ImageInfoEntity {
	private String url;
	private boolean isChecked;

	public ImageInfoEntity(String url, boolean isChecked) {
		this.url = url;
		this.isChecked = isChecked;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return url;
	}

	@Override
	public boolean equals(Object o) {
		return url.equals(o.toString());
	}
}
