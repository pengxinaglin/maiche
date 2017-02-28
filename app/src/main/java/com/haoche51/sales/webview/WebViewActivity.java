package com.haoche51.sales.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.util.NetInfoUtil;
import com.haoche51.sales.util.ToastUtil;

/**
 * 通用WebView，子类有额外的逻辑，需要继承这个类，而不要加在这个类里面
 */
public class WebViewActivity extends CommonStateActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";

    protected String mUrl;
    protected String mTitle;

    protected View mWebViewLayout;
    protected View mNetworkLayout;
    protected TextView mTitleTextView;
    protected TextView mRightTextView;
    protected WebView mWebView;
    protected View mProgressView;
    protected FrameLayout.LayoutParams mProgressParams;
    protected int mScreenWidth = GlobalData.mContext.getResources().getDisplayMetrics().widthPixels;

    @Override
    protected int getTitleView() {
        return R.layout.title_bar;
    }

    @Override
    protected int getContentView() {
        return R.layout.web_view;
    }

    @Override
    protected void initView() {
        super.initView();
        // 获得传进来的数据
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mUrl)) {
            mUrl = Debug.WAP_SERVER;
        }
        mTitle = intent.getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(mTitle)) {
            mTitle = getString(R.string.app_name);
        }

        // 获得View
        mWebViewLayout = findViewById(R.id.web_view_layout);
        mNetworkLayout = findViewById(R.id.network_error_layout);
        mTitleTextView = (TextView) findViewById(R.id.tv_common_title);
        mRightTextView = (TextView) findViewById(R.id.right_text_view);
        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressParams = (FrameLayout.LayoutParams) mProgressView.getLayoutParams();

        // 执行显示
        mTitleTextView.setText(mTitle);
        mRightTextView.setText(getString(R.string.hc_title_close));
        mRightTextView.setVisibility(View.VISIBLE);
        mRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        mWebView.addJavascriptInterface(new WebInterface(this), WebInterface.INTERFACE_NAME);
        setWebViewClient();
        setWebChromeClient();
    }

    @Override
    protected void initData() {
        loadWapUrl(mUrl);
    }

    /**
     * 加载url
     */
    protected void loadWapUrl(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!NetInfoUtil.isNetAvaliable()) {
            mWebViewLayout.setVisibility(View.GONE);
            mNetworkLayout.setVisibility(View.VISIBLE);
            ToastUtil.showInfo(getString(R.string.network_error));
            mNetworkLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadWapUrl(url);
                }
            });
        } else {
            mWebViewLayout.setVisibility(View.VISIBLE);
            mNetworkLayout.setVisibility(View.GONE);
            mWebView.loadUrl(url);
        }
    }

    /**
     * 设置WebViewClient
     */
    protected void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:") || url.startsWith("mailto:") || url.startsWith("geo:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    loadWapUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * 设置WebChromeClient
     */
    protected void setWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressParams.width = mScreenWidth * newProgress / 100;
                mProgressView.setLayoutParams(mProgressParams);
                if (newProgress == 100) {
                    mProgressView.setVisibility(View.GONE);
                } else {
                    mProgressView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }

    /**
     * 注册标题返回
     */
    protected void registerTitleBack() {
        View backButton = findViewById(R.id.tv_common_back);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mWebView != null && mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                }
            });
        }
    }

    /**
     * 返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                finish();
            }
        }
        return false;
    }
}
