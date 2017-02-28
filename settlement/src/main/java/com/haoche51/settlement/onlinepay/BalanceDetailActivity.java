package com.haoche51.settlement.onlinepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.cashiers.SettlementDetailActivity;
import com.haoche51.settlement.utils.DisplayUtils;
import com.haoche51.settlement.utils.ToastUtil;

/**
 * 查余额结果
 * Created by yangming on 2016/4/11.
 */
public class BalanceDetailActivity extends Activity implements View.OnClickListener {


    String mCrmUserId = "";
    String mCrmUserName = "";
    String mAppToken = "";
    String phone = "";//客户电话号码
    private double balance;//余额

//    private TextView tv_balance_phone;
//    private TextView tv_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrmUserId = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID);
        mCrmUserName = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME);
        mAppToken = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN);
        phone = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_PHONE);
        balance = getIntent().getDoubleExtra(SettlementDetailActivity.KEY_EXTRA_PRICE, -1);

        if (TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName)
                || TextUtils.isEmpty(mAppToken) || TextUtils.isEmpty(phone)) {
            ToastUtil.showInfo(getApplicationContext(), "参数错误");
            finish();
            return;
        }

        setContentView(R.layout.pay_balance_detail_activity);
        //back
        registerTitleBack();
        //title
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("查余额");

        //客户手机号
        TextView tv_balance_phone = (TextView) findViewById(R.id.tv_balance_phone);
        tv_balance_phone.setText(phone);
        //客户余额
        TextView tv_balance = (TextView) findViewById(R.id.tv_balance);
        tv_balance.setText(DisplayUtils.parseMoney("#", balance));
        //申请退款
        findViewById(R.id.btn_balance_apply_refund).setOnClickListener(this);


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


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ApplyRefundActivity.class);
        intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, mCrmUserId);
        intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, mCrmUserName);
        intent.putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, mAppToken);
        intent.putExtra(SettlementDetailActivity.KEY_EXTRA_PHONE, phone);
        intent.putExtra(SettlementDetailActivity.KEY_EXTRA_BALANCE, balance);
        startActivityForResult(intent, 0);
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

