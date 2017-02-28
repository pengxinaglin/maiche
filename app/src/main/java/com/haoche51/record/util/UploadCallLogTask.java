package com.haoche51.record.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;

import com.haoche51.record.entity.CallLogEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.ArrayList;

/***
 * 上传通讯录
 */
public class UploadCallLogTask {
    private final static String TAG = "UploadCallLogTask";

    /**
     * 每次读多少条
     */
    private final static int EVERYUPLOADCOUNTS = 15;

    /***
     * 获取最新一条时长大于5秒的记录的时间
     *
     * @param mContext
     * @return
     */
    public long getNewestCallLog(Context mContext) {
        long result = 0L;
        String where = CallLog.Calls.DURATION + " > " + 5;
        String sortOrder = CallLog.Calls.DEFAULT_SORT_ORDER;
        ContentResolver mContentResolver = mContext.getContentResolver();
        final Cursor mCursor = mContentResolver.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.DATE}, where, null, sortOrder);
        if (mCursor != null && mCursor.getCount() > 0) {
            HCLogUtil.d(TAG, "mCursor count= " + mCursor.getCount());
            mCursor.moveToFirst();
            result = mCursor.getLong(0);
            mCursor.close();
        }
        return result;
    }

    public synchronized void startUploadCalls(final Context mContext, int lastUploadID) {
        new AsyncTask<Integer, Void, ArrayList<CallLogEntity>>() {
            @Override
            protected ArrayList<CallLogEntity> doInBackground(Integer... params) {
                Integer mSearchId = params[0];
                HCLogUtil.d(TAG, " startUPloadCalls doInbg mSearchId= " + mSearchId);
                ArrayList<CallLogEntity> mCallLists;
                String callNumber, callName;
                int callType;
                long callDuration, callDate = 0L;
                int _id;
                String where = " _id " + " > " + mSearchId;
                String sortOrder = " _id ASC " + " limit " + EVERYUPLOADCOUNTS;// CallLog.Calls.DEFAULT_SORT_ORDER

                ContentResolver mContentResolver = mContext.getContentResolver();
                final Cursor mCursor = mContentResolver.query(CallLog.Calls.CONTENT_URI
                        , new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME
                                , CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION
                                , "_id"}, where, null, sortOrder);

                if (mCursor != null && mCursor.getCount() > 0 && mCursor.moveToFirst()) {
                    mCallLists = new ArrayList<CallLogEntity>(EVERYUPLOADCOUNTS);
                    do {
                        callNumber = mCursor.getString(0); // 呼叫号码
                        callName = mCursor.getString(1); // 联系人姓名
                        callType = mCursor.getInt(2); // 来电:1,拨出:2,未接:3
                        callDate = mCursor.getLong(3);// 通话时间
                        callDuration = mCursor.getLong(4);// 通话时长
                        callName = callName == null ? "" : callName;
                        _id = mCursor.getInt(5);

                        CallLogEntity entity = new CallLogEntity();
                        entity.setCallNumber(callNumber);
                        entity.setCallName(callName);
                        entity.setCallType(callType);
                        entity.setCallDate(callDate);
                        entity.setCallDuratioin(callDuration);
                        entity.set_id(_id);
                        mCallLists.add(entity);

                        HCLogUtil.d(TAG, "_id = " + _id + ",callType=" + callType + ", callNumber=" + callNumber + ",  callDate " + UnixTimeUtil.format((int) callDate));
                    } while (mCursor.moveToNext());
                } else {
                    return null;
                }
                HCLogUtil.d(TAG, "startUPloadCalls query lists = " + mCallLists.size());
                mCursor.close();
                return mCallLists;
            }

            protected void onPostExecute(ArrayList<CallLogEntity> result) {
                if (result != null && result.size() >= 1) {
                    final int mSize = result.size();
                    CallLogEntity entity = result.get(mSize - 1);
                    final int mId = entity.get_id();
                    AppHttpServer.getInstance().post(HCHttpRequestParam.uploadCallLogs(result), new HCHttpCallback() {
                        @Override
                        public void onHttpStart(String action, int requestId) {

                        }

                        @Override
                        public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
                            try {
                                HCLogUtil.d(TAG, "upload cal log 上传成功-------");
                                if (0 == response.getErrno()) {
                                    // 记录最后的id
                                    RecordUtil.putLastUploadRecordID(mId);
                                    if (mSize == EVERYUPLOADCOUNTS)// 还没上传完
                                    {
                                        startUploadCalls(mContext, mId);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                HCLogUtil.d(TAG, "pload cal log 上传异常-------- ");
                            }
                        }

                        @Override
                        public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

                        }

                        @Override
                        public void onHttpRetry(String action, int requestId, int retryNo) {

                        }

                        @Override
                        public void onHttpFinish(String action, int requestId) {

                        }
                    }, 0);
                }
            }

        }.execute(lastUploadID);
    }
}
