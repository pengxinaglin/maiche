package com.haoche51.sales.hctransaction;

import android.content.Intent;

import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.webview.WebViewActivity;

/**
 * 展厅车源详情页
 * Created by PengXianglin on 16/8/26.
 */
public class StoreDetailActivity extends WebViewActivity {

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TaskConstants.PREPAY_TRANSACTION) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}
