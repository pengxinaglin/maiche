package com.haoche51.sales.webview;

import android.text.TextUtils;
import android.view.View;

import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.R;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.util.ToastUtil;

public class StoreWebViewActivity extends WebViewActivity {

    @Override
    protected void initData() {
        // 先确认门店Token是否过期
        LoginToken loginToken = GlobalData.userDataHelper.getStoreToken();
        boolean isTimeout = false;
        if (loginToken != null) {
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            isTimeout = (currentTime - loginToken.getTimeOut()) > loginToken.getCreate_time() ? true : false;
        }
        if (loginToken == null || TextUtils.isEmpty(loginToken.getToken()) || isTimeout) {
            HCDialog.showProgressDialog(StoreWebViewActivity.this);
            AppHttpServer.getInstance().post(HCHttpRequestParam.getLoginToken("store"), StoreWebViewActivity.this, 0);
        } else {
            mUrl = Debug.STORE_SERVER + "/?token=" + loginToken.getToken();
            loadWapUrl(mUrl);
        }
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }

        HCDialog.dismissProgressDialog();
        if (response == null) {
            return;
        }

        int errno = response.getErrno();
        if (errno == 0) {
            // 正常返回
            LoginToken loginToken = HCJsonParse.parseStoreLoginToken(response.getData());
            if (loginToken != null) {
                mUrl = Debug.STORE_SERVER + "/?token=" + loginToken.getToken();
                loadWapUrl(mUrl);
            }
        } else {
            String errorMessage = response.getErrmsg();
            if (TextUtils.isEmpty(errorMessage)) {
                errorMessage = getString(R.string.network_error);
            }
            ToastUtil.showInfo(errorMessage);

            mWebViewLayout.setVisibility(View.GONE);
            mNetworkLayout.setVisibility(View.VISIBLE);
            mNetworkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebViewLayout.setVisibility(View.VISIBLE);
                    mNetworkLayout.setVisibility(View.GONE);
                    HCDialog.showProgressDialog(StoreWebViewActivity.this);
                    AppHttpServer.getInstance().post(HCHttpRequestParam.getLoginToken("store"), StoreWebViewActivity.this, 0);
                }
            });
        }
    }
}
