package com.haoche51.settlement.o2outils;


import android.text.TextUtils;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 对签名进行验�?
 */
public class ResultChecker {

  public static final int RESULT_INVALID_PARAM = 0;
  public static final int RESULT_CHECK_SIGN_FAILED = 1;
  public static final int RESULT_CHECK_SIGN_TYPE_FAILED = -1;
  public static final int RESULT_CHECK_SIGN_SUCCEED = 2;

  String mContent;

  public ResultChecker(String content) {
    this.mContent = content;
  }

  /**
   * 对签名进行验�?
   *
   * @return
   */
  public int checkSign() {
    int retVal = RESULT_CHECK_SIGN_SUCCEED;

    try {
      JSONObject objContent = BaseHelper.string2JSON(this.mContent);
      String signContent = "";
      // 获取待签名数�?
      if (TextUtils.isEmpty(objContent.optString("result_sign", ""))) {
        signContent = getSignContent(objContent);
      } else {
        signContent = getSignContentForSignCard(objContent);
      }

      Log.i("ResultChecker", "支付结果待签名数据：" + signContent);
      // 获取签名类型
      String signType = objContent.optString("sign_type");
      // 获取签名
      String sign = objContent.optString("sign");
      // 进行验签 返回验签结果
      if (signType.equalsIgnoreCase("RSA")) {
        if (!Rsa.doCheck(signContent, sign, "")) {// TODO 公钥
          retVal = RESULT_CHECK_SIGN_FAILED;
          Log.e("ResultChecker", "RESULT_CHECK_SIGN_FAILED");
        }
      } else {
        Log.e("ResultChecker", "RESULT_CHECK_SIGN_TYPE_FAILED");
        retVal = RESULT_CHECK_SIGN_TYPE_FAILED;
      }
    } catch (Exception e) {
      retVal = RESULT_INVALID_PARAM;
      e.printStackTrace();
    }
    return retVal;
  }

  private String getSignContent(JSONObject objResult) {
    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    for (Iterator<?> it = objResult.keys(); it.hasNext(); ) {
      String key = (String) it.next();
      // ret_code、ret_msg、sign不参与签�?
      if ("ret_code".equalsIgnoreCase(key)
        || "ret_msg".equalsIgnoreCase(key)
        || "sign".equalsIgnoreCase(key)
        || "agreementno".equalsIgnoreCase(key)) {
        continue;
      }
      parameters
        .add(new BasicNameValuePair(key, objResult.optString(key)));
    }
    String content = BaseHelper.sortParam(parameters);
    return content;
  }


  private String getSignContentForSignCard(JSONObject objResult) {
    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    for (Iterator<?> it = objResult.keys(); it.hasNext(); ) {
      String key = (String) it.next();
      // ret_code、ret_msg、sign不参与签�?
      if ("ret_code".equalsIgnoreCase(key)
        || "ret_msg".equalsIgnoreCase(key)
        || "sign".equalsIgnoreCase(key)
        ) {
        continue;
      }
      parameters
        .add(new BasicNameValuePair(key, objResult.optString(key)));
    }
    String content = BaseHelper.sortParamForSignCard(parameters);
    return content;
  }


}