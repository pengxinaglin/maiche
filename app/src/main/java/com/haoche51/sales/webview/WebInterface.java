package com.haoche51.sales.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hctransaction.BuyerRevisitListActivity;
import com.haoche51.sales.hctransaction.PhoneRecorListActivity;
import com.haoche51.sales.hctransaction.StoreDetailActivity;
import com.haoche51.sales.hctransaction.TransactionMainActivity;
import com.haoche51.sales.hctransaction.TransactionPrepayActivity;
import com.haoche51.sales.hctransaction.TransactionTaskDetailActivity;
import com.haoche51.sales.hctransaction.TransactionTaskEntity;
import com.haoche51.sales.hctransaction.VehicleRevisitListActivity;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCStatistic;
import com.haoche51.sales.util.HCUtils;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * WebView交互接口
 */
public class WebInterface {

	// 接口名
	public static final String INTERFACE_NAME = "APIForJS";

	private Context mContext;

	public WebInterface(Context context) {
		mContext = context;
	}

	/**
	 * 点击预约看车
	 */
	@JavascriptInterface
	public void onClickTransactionTask(String idStr) {
		if (!TextUtils.isEmpty(idStr)) {
			int id = StringUtil.parseInt(idStr, 0);
			if (id != 0) {
				Intent intent = new Intent(mContext, TransactionTaskDetailActivity.class);
				intent.putExtra(TransactionTaskDetailActivity.KEY_ID, id);
				mContext.startActivity(intent);
			} else {
				ToastUtil.showInfo(mContext.getString(R.string.id_error));
			}
		} else {
			ToastUtil.showInfo(mContext.getString(R.string.id_error));
		}
	}

	/**
	 * 点击看展厅车源详情
	 */
	@JavascriptInterface
	public void onClickStoreDetail(String url) {
		if (!TextUtils.isEmpty(url)) {
			Intent intent = new Intent(mContext, StoreDetailActivity.class);
			intent.putExtra(WebViewActivity.EXTRA_URL, url);
			intent.putExtra(WebViewActivity.EXTRA_TITLE, mContext.getString(R.string.hc_trans_task_detail_activity_title));
			((Activity) mContext).startActivityForResult(intent, TransactionMainActivity.REQUEST_CODE_RELOAD);
		} else {
			ToastUtil.showInfo(mContext.getString(R.string.id_error));
		}
	}

	/**
	 * 点击看展厅车源预定
	 */
	@JavascriptInterface
	public void onClickStoreYD(String json) {
		if (!TextUtils.isEmpty(json)) {
			TransactionTaskEntity entity = HCJsonParse.parseTransTaskEntity(json);
			if (entity == null) {
				ToastUtil.showInfo(mContext.getString(R.string.id_error));
				return;
			}
			entity.setType(1);//若type=0，则id为C2C带看任务id；若type=1，则id为展厅任务id
			Intent prepayIntent = new Intent(mContext, TransactionPrepayActivity.class);
			prepayIntent.putExtra("taskEntity", entity);
			prepayIntent.putExtra("isStore", true);
			((Activity) mContext).startActivityForResult(prepayIntent, TaskConstants.PREPAY_TRANSACTION);
		} else {
			ToastUtil.showInfo(mContext.getString(R.string.id_error));
		}
	}

	/**
	 * 展厅车源详情页点击添加到店
	 */
	@JavascriptInterface
	public void onClickDaodian() {
		((Activity) mContext).finish();
	}

	/**
	 * 点击看展厅车源买家记录
	 */
	@JavascriptInterface
	public void onClickRevisit(int transId, int vehicleSourceId, String phone) {
		HCStatistic.readyClick(transId, HCConst.ReadyInfo.READYINFO_TYPE_BUYER_VISIT_RECORD
				, HCUtils.getNetType(), System.currentTimeMillis(), 1);

		Map<String, Object> map = new HashMap<>();
		map.put("vehicle_source_id", vehicleSourceId);
		map.put("buyer_phone", phone);
		HCActionUtil.launchActivity(mContext, BuyerRevisitListActivity.class, map);
	}

	/**
	 * 点击看展厅车源听录音
	 */
	@JavascriptInterface
	public void onClickRecord(int transId, String phone) {
		HCStatistic.readyClick(transId, HCConst.ReadyInfo.READYINFO_TYPE_VOICE
				, HCUtils.getNetType(), System.currentTimeMillis(), 1);
		Map<String, Object> map = new HashMap<>();
		map.put("id", transId);
		map.put(PhoneRecorListActivity.KEY_INTENT_EXTRA_BUYER_PHONE, phone);
		map.put(PhoneRecorListActivity.KEY_TASK_TYPE, TaskConstants.MESSAGE_VIEW_TASK);
		HCActionUtil.launchActivity(mContext, PhoneRecorListActivity.class, map);
	}
}
