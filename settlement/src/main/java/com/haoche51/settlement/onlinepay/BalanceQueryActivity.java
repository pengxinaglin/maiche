package com.haoche51.settlement.onlinepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.cashiers.DetailEntity;
import com.haoche51.settlement.cashiers.PaymentEntity;
import com.haoche51.settlement.cashiers.SettlementDetailActivity;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.net.HCJsonParse;
import com.haoche51.settlement.net.HttpConstants;
import com.haoche51.settlement.utils.HCArithUtil;
import com.haoche51.settlement.utils.ProgressDialogUtil;
import com.haoche51.settlement.utils.QueryLianLianUtil;
import com.haoche51.settlement.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 查余额
 * Created by yangming on 2016/4/11.
 */
public class BalanceQueryActivity extends Activity implements HCHttpCallback {

    private EditText editTextPhone;

    String mCrmUserId = "";
    String mCrmUserName = "";
    private String mAppToken;

    private DetailEntity detailEntity;

    private FrameLayout mContentViewContainer;
    private View mResultView;

    private List<PaymentEntity> failPaymentList = new ArrayList<>();
    private int queryLianlianIndex;

    private double balance;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrmUserId = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID);
        mCrmUserName = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME);
        mAppToken = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN);

        if (TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName) || TextUtils.isEmpty(mAppToken)) {
            ToastUtil.showInfo(getApplicationContext(), "参数错误");
            finish();
            return;
        }

        setContentView(R.layout.pay_balance_query_activity);
        //back
        registerTitleBack();
        //title
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("查余额");

        editTextPhone = ((EditText) findViewById(R.id.et_balance_phone));

        //查询按钮
        findViewById(R.id.btn_balance_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = editTextPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_phone));
                    return;
                } else {
                    ProgressDialogUtil.showProgressDialog(BalanceQueryActivity.this, "");
                    getAccountDetail();
                }
            }
        });
    }

    /**
     * 标题栏返回
     */
    protected void registerTitleBack() {
        View backBtn = findViewById(R.id.back_image_view);
        if (backBtn != null) {
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
        }
    }

    /**
     * 获取详情
     */
    private void getAccountDetail() {
        AppHttpServer.getInstance(mAppToken).post(this,
                HCHttpRequestParam.getAccountDetail(this, editTextPhone.getText().toString().trim(), 0, mAppToken),
                this, 0);
    }

    private void setErrorView() {
        ProgressDialogUtil.closeProgressDialog();
        mContentViewContainer = (FrameLayout) findViewById(R.id.ll_common_content_container);
        mResultView = LayoutInflater.from(this).inflate(
                R.layout.pay_result_view, null);

        final TextView textView = (TextView) mResultView
                .findViewById(R.id.result_txt);
        textView.setText("你掉线了哦~");
        textView.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.pay_button_offline, 0, 0);

        mContentViewContainer.removeView(mResultView);
        mContentViewContainer.addView(mResultView, new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissResultView();
                ProgressDialogUtil.showProgressDialog(BalanceQueryActivity.this, "");
                getAccountDetail();
            }
        });
    }

    public void dismissResultView() {
        mContentViewContainer.removeView(mResultView);
    }

    @Override
    public void onHttpStart(String action, int requestId) {

    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (HttpConstants.ACTION_GETACCOUNTDETAIL.equals(action)) {
            responseDetail(response);
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

    /**
     * 获取账户详情/getAccountDetail
     */
    private void responseDetail(HCHttpResponse response) {

        switch (response.getErrno()) {
            case 0:
                detailEntity = HCJsonParse.parseDetail(response.getData());

                if (detailEntity == null) {
                    ToastUtil.showTextLong(getApplicationContext(), "请求余额失败，请重试");
                    setErrorView();
                    return;
                }

                failPaymentList.clear();
                failPaymentList = detailEntity.getUnconfirm_list();
                if (failPaymentList != null && failPaymentList.size() > 0) {
                    //　查询连连　
                    QueryLianLianUtil.getInstance()
                            .query(this, mCrmUserId, mCrmUserName, mAppToken, failPaymentList, detailEntity.getOid_trader()
                                    , detailEntity.getYt_auth().getRSA_PRIVATE(), new QueryLianLianUtil.ResultListener() {
                                        @Override
                                        public void isComPlete(boolean isComPlete) {
                                            if (isComPlete) {
                                                //重新查询详情
                                                getAccountDetail();
                                            } else {
                                                setErrorView();
                                            }
                                        }
                                    });
                } else {
                    if (!isFinishing())
                        ProgressDialogUtil.closeProgressDialog();
                    if (TextUtils.isEmpty(detailEntity.getUser_amount())) {
                        balance = 0;
                    } else {
                        balance = HCArithUtil.div(Double.valueOf(detailEntity.getUser_amount()), 100);//
                    }
                    // 跳转
                    Intent intent = new Intent(BalanceQueryActivity.this, BalanceDetailActivity.class);
                    intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, mCrmUserId);
                    intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, mCrmUserName);
                    intent.putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, mAppToken);
                    intent.putExtra(SettlementDetailActivity.KEY_EXTRA_PHONE, phone);
                    intent.putExtra(SettlementDetailActivity.KEY_EXTRA_PRICE, balance);
                    BalanceQueryActivity.this.startActivityForResult(intent, 0);
                }

                break;
            default:
                if (!isFinishing())
                    ProgressDialogUtil.closeProgressDialog();
                ToastUtil.showInfo(getApplicationContext(), response.getErrmsg());
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
