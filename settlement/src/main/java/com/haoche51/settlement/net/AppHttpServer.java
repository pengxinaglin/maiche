package com.haoche51.settlement.net;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.haoche51.settlement.PayDebug;
import com.haoche51.settlement.utils.DESUtil;
import com.haoche51.settlement.utils.HCLogUtil;
import com.haoche51.settlement.utils.ToastUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by xuhaibo on 15/8/26.
 */
public class AppHttpServer {

    public static final String KEY_IS_CANCEL = "is_cancel";

    private static final String LOG_TAG = "AppHttpServer";
    private static final String SERVER_URL = PayDebug.VCODE_SERVER;
    private static final String NORMAL_REQEUST_PARAMS = "req";
    private static final int TIMEOUT_MSECOND = 10 * 1000;
    private static AsyncHttpClient client;
    private static AppHttpServer mInstance = new AppHttpServer();
    private static Gson mGson = new Gson();
    private static String appToken;


    /**
     * 初始化Async httpclient
     */
    private AppHttpServer() {
        synchronized (AsyncHttpClient.class) {
            if (client == null) {
                client = new AsyncHttpClient();
                client.setMaxRetriesAndTimeout(2, TIMEOUT_MSECOND);
            }
        }

    }

    /**
     * 获取实例
     */
    public static AppHttpServer getInstance(String token) {
        if (mInstance == null) {
            mInstance = new AppHttpServer();
        }
        appToken = token;
        return mInstance;
    }

    /**
     * @param request  请求参数
     * @param callback 回调
     */
    public RequestHandle post(Context context, final Map<String, Object> request, final HCHttpCallback callback, final int requestId) {
        return post(context, null, request, callback, requestId);
    }


    /**
     * 传入Context参数，后面可以用于取消Content相关的请求
     * 传入Object tag参数，后面可以用于取消tag相关的请求
     *
     * @param context
     * @param tag
     * @param request
     * @param callback
     * @param requestId
     * @return 返回请求对象
     */
    public RequestHandle post(final Context context, Object tag, final Map<String, Object> request, final HCHttpCallback callback, final int requestId) {
        String tempAction = "";
        if (request != null && request.containsKey("action")) {
            //取出action
            try {
                tempAction = request.get("action").toString();
                //去掉外层的action的键值对
                request.remove("action");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String action = tempAction;
        RequestParams params = new RequestParams();
        params.put(NORMAL_REQEUST_PARAMS, mGson.toJson(request));

        HCLogUtil.d(LOG_TAG, "request-action:" + action);
        HCLogUtil.d(LOG_TAG, "request:" + SERVER_URL + "params:" + params.toString());

        return client.post(context, SERVER_URL, params, new AsyncHttpResponseHandler() {

            /**
             * Fired when the request progress, override to handle in your own code
             *
             * @param bytesWritten offset from start of file
             * @param totalSize    total size of file
             */
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                if (callback != null) {
                    callback.onHttpProgress(action, requestId, bytesWritten, totalSize);
                }
            }

            /**
             * Fired when the request is started, override to handle in your own code
             */
            @Override
            public void onStart() {
                super.onStart();
                if (callback != null) {
                    callback.onHttpStart(action, requestId);
                }
            }

            /**
             * Fired in all cases when the request is finished, after both success and failure, override to
             * handle in your own code
             */
            @Override
            public void onFinish() {
                super.onFinish();
                if (callback != null) {
                    callback.onHttpFinish(action, requestId);
                }
            }

            /**
             * Fired when a request returns successfully, override to handle in your own code
             *
             * @param statusCode   the status code of the response
             * @param headers      return headers, if any
             * @param responseBody the body of the HTTP response from the server
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String log = new String(responseBody);
                HCLogUtil.d(LOG_TAG, "response:" + SERVER_URL + "action:" + action + "--return:" + new String(responseBody));
                HCHttpResponse response = new HCHttpResponse();
                String responseStr = "";

                try {
                    responseStr = DESUtil.decryptDES(new String(responseBody), appToken);
                } catch (Exception e) {
                    e.printStackTrace();
//                    response.set
                    if (callback != null) {
                        callback.onHttpComplete(action, requestId, response, null);
                    }
                    return;
                }
                HCLogUtil.d(LOG_TAG, "response:" + SERVER_URL + "action:" + action + "--return--decryptDES:" + responseStr);


                response = new HCHttpResponse(responseStr);
                if (response.getErrno() == -101) {
                    // erroNo = -101 异常登录，无效的app token 退出登录
                    ToastUtil.showInfo(context, response.getErrmsg());
                } else {
                    if (callback != null) {
                        if (request.get(KEY_IS_CANCEL) != null && (boolean) request.get(KEY_IS_CANCEL) == true) {
                            response.setCancel(true);
                        }
                        callback.onHttpComplete(action, requestId, response, null);
                    }
                }
            }

            /**
             * Fired when a request fails to complete, override to handle in your own code
             *
             * @param statusCode   return HTTP status code
             * @param headers      return headers, if any
             * @param responseBody the response body, if any
             * @param error        the underlying cause of the failure
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                HCHttpResponse response = new HCHttpResponse();
                if (callback != null) {
                    if (request.get(KEY_IS_CANCEL) != null && (boolean) request.get(KEY_IS_CANCEL) == true) {
                        response.setCancel(true);
                    }
                    callback.onHttpComplete(action, requestId, response, error);
                }
            }

            /**
             * Fired when a retry occurs, override to handle in your own code
             *
             * @param retryNo number of retry
             */
            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
                if (callback != null) {
                    callback.onHttpRetry(action, requestId, retryNo);
                }
            }

        }).setTag(tag);
    }

    /**
     * 取消Content相关的请求
     *
     * @param context
     */
    public void cancel(Context context) {
        client.cancelRequests(context, true);
    }

    /**
     * 取消tag相关的请求
     *
     * @param tag
     */
    public void cancelByTag(Object tag) {
        client.cancelRequestsByTAG(tag, true);
    }

    /**
     * 取消所有的请求
     *
     * @param tag
     */
    public void cancelAll(Object tag) {
        client.cancelAllRequests(true);
    }


    /**
     * 查询连连订单
     * 传入Context参数，后面可以用于取消Content相关的请求
     * 传入Object tag参数，后面可以用于取消tag相关的请求
     *
     * @param request
     * @param callback
     * @param requestId
     * @return 返回请求对象
     */
    public RequestHandle lianlianQueryPost(Context context, Map<String, Object> request, final AsyncHttpResponseHandler callback, final int requestId) {

        JsonObject jo = new JsonObject();
        jo.addProperty("no_order", (String) request.get("no_order"));
        jo.addProperty("oid_partner", (String) request.get("oid_partner"));
        jo.addProperty("sign_type", "RSA");
        jo.addProperty("sign", (String) request.get("sign"));

        StringEntity entity = null;
        try {
            entity = new StringEntity(jo.toString(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
        }

        return client.post(context, "https://yintong.com.cn/offline_api/queryOrder.htm", entity, "application/json", callback);
    }

}
