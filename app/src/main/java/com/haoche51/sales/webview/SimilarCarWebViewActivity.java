package com.haoche51.sales.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.haoche51.sales.R;
import com.haoche51.sales.hctransaction.TransactionTaskDetailActivity;
import com.haoche51.sales.hctransaction.TakeLookActivity;

/**
 * 相似车源
 */
public class SimilarCarWebViewActivity extends WebViewActivity implements View.OnClickListener{

    private String mBuyerName;
    private String mBuyerPhone;

    private Button mTakeLookButton;

    @Override
    protected int getContentView() {
        return R.layout.web_view_similar_car;
    }

    @Override
    protected void initView() {
        super.initView();
        mBuyerName = getIntent().getStringExtra(TakeLookActivity.EXTRA_BUYER_NAME);
        mBuyerPhone = getIntent().getStringExtra(TakeLookActivity.EXTRA_BUYER_PHONE);
        mTakeLookButton = (Button) findViewById(R.id.take_look_button);
        mTakeLookButton.setOnClickListener(this);
    }

    /**
     * 设置WebViewClient
     */
    @Override
    protected void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 调用拨号程序
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    // 不是相似车源详情页，隐藏底部"加带看"Button
                    if (url == null || !url.contains("/details")) {
                        mTakeLookButton.setVisibility(View.GONE);
                    }
                    loadWapUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.contains("/details")) {
                    // 相似车源显示底部"加带看"
                    mTakeLookButton.setVisibility(View.VISIBLE);
                } else {
                    mTakeLookButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mTakeLookButton) {
            // 加带看
            Intent intent = new Intent(SimilarCarWebViewActivity.this, TakeLookActivity.class);
            intent.putExtra(TakeLookActivity.EXTRA_IS_SIMILAR, true);
            intent.putExtra(TakeLookActivity.EXTRA_BUYER_NAME, mBuyerName);
            intent.putExtra(TakeLookActivity.EXTRA_BUYER_PHONE, mBuyerPhone);
            startActivityForResult(intent, TransactionTaskDetailActivity.REQUEST_CODE_TAKE_LOOK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TransactionTaskDetailActivity.REQUEST_CODE_TAKE_LOOK) {
            // 加带看成功后关闭此界面
            if (resultCode == RESULT_OK) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }
}
