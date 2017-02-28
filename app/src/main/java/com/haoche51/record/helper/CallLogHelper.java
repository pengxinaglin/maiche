package com.haoche51.record.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.text.TextUtils;


import com.haoche51.record.entity.CallLogEntity;

import java.util.ArrayList;
import java.util.List;

public class CallLogHelper {

	private static int ITEM_COUNT = 10;

	public static List<CallLogEntity> getCallLog(Context context, int last_id) {
		String where = " _id " + ">" + last_id;
		String order = " _id ASC " + " limit " + ITEM_COUNT;// CallLog.Calls.DEFAULT_SORT_ORDER
		ContentResolver mContentResolver = context.getContentResolver();
		final Cursor mCursor = mContentResolver.query(CallLog.Calls.CONTENT_URI, new String[] { CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION, "_id" }, where, null, order);
		if (mCursor == null) {
			return null;
		}
		List<CallLogEntity> mCallLogs = new ArrayList<CallLogEntity>();
		while (mCursor.moveToNext()) {
			CallLogEntity entity = new CallLogEntity();
			entity.setCallNumber(mCursor.getString(0));
			String callName = mCursor.getString(1);
			if (TextUtils.isEmpty(callName)) {
				callName = "";
			}
			entity.setCallName(callName);
			entity.setCallType(mCursor.getInt(2));
			entity.setCallDate(mCursor.getLong(3));
			entity.setCallDuratioin(mCursor.getLong(4));
			entity.set_id(mCursor.getInt(5));
			mCallLogs.add(entity);
		}
		mCursor.close();
		return mCallLogs;
	}

}
