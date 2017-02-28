package com.haoche51.sales.util;


import com.haoche51.sales.dao.TransactionReadyInfoDAO;
import com.haoche51.sales.hctransaction.TransactionReadyInfo;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计
 * 1.点击事件统计
 */
public class HCStatistic {

  //在sharedPreference中记录最后一次操作(home_brand,home_price,home_search)

  /**
   * 地销点击统计
   *
   * @param transaction_id 带看任务ID
   * @param type           点击类型
   * @param netType        网络类型
   * @param time           操作时间
   * @param requestID      请求id， 0：四个电话的点击，回传失败了要保存到本地；1：其他点击监控，回传失败就失败了
   */
  public static void readyClick(int transaction_id, int type, int netType, long time, int requestID) {

    final TransactionReadyInfo readyInfo = new TransactionReadyInfo();
    readyInfo.setTransaction_id(transaction_id);
    TransactionReadyInfo.OperationEntity operationEntity = new TransactionReadyInfo.OperationEntity();
    operationEntity.setType(type);
    operationEntity.setNetwork_env(netType);
    operationEntity.setOperate_time(time / 1000L);
    readyInfo.setOperation(operationEntity);

    List<TransactionReadyInfo> readyInfos = new ArrayList<>();
    readyInfos.add(readyInfo);

    //判断是否有网络
    if (HCUtils.isNetAvailable()) {//有网络，提交
      AppHttpServer.getInstance().post(HCHttpRequestParam.addReadyInfo(transaction_id, readyInfos), new HCHttpCallback() {
        @Override
        public void onHttpStart(String action, int requestId) {

        }

        @Override
        public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
          if (response != null && requestId == 0 && response.getErrno() != 0) {
            // 电话号码，统计回传失败，保存至本地
            readyInfo.setUpload_status("false");
            TransactionReadyInfoDAO.getInstance().insert(readyInfo);
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
      }, requestID);
    } else {//无网络，缓存
      if (requestID == 0) {
        readyInfo.setUpload_status("false");
        TransactionReadyInfoDAO.getInstance().insert(readyInfo);
      }
    }
  }
}
