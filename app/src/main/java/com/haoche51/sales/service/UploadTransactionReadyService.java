package com.haoche51.sales.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.haoche51.sales.dao.TransactionReadyInfoDAO;
import com.haoche51.sales.hctransaction.TransactionReadyInfo;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangming on 2016/2/23.
 */
public class UploadTransactionReadyService extends Service implements HCHttpCallback {


  public MyBinder myBinder = new MyBinder();

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return myBinder;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    startUpload();
  }


  public void startUpload() {
    List<TransactionReadyInfo> transactionReadyInfoList = TransactionReadyInfoDAO.getInstance().get(null);

    if (transactionReadyInfoList != null && transactionReadyInfoList.size() > 0) {
      for (TransactionReadyInfo readyInfo : transactionReadyInfoList) {
        List<TransactionReadyInfo> readyInfos = new ArrayList<>();
        readyInfos.add(readyInfo);
        AppHttpServer.getInstance().post(HCHttpRequestParam.addReadyInfo(readyInfo.getTransaction_id(), readyInfos), UploadTransactionReadyService.this, readyInfo.getId());
      }
    }
    stopSelf();
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return super.onUnbind(intent);
  }

  public class MyBinder extends Binder {
    public UploadTransactionReadyService getService() {
      return UploadTransactionReadyService.this;
    }
  }

  @Override
  public void onHttpStart(String action, int requestId) {

  }

  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
    if (response.getErrno() == 0) {
      TransactionReadyInfoDAO.getInstance().deleteByTaskId(requestId);
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
}
