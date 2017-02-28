package com.haoche51.settlement.onlinepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.cashiers.SettlementDetailActivity;
import com.haoche51.settlement.cashiers.SettlementIntent;
import com.haoche51.settlement.utils.AlertDialog;
import com.haoche51.settlement.utils.ToastUtil;

/**
 * 添加收款
 */
public class AddPayActivity extends Activity {

    private String mCrmUserId;
    private String mCrmUserName;
    private String mAppToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrmUserId = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID);
        mCrmUserName = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME);
        mAppToken = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN);
        if (TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName) || TextUtils.isEmpty(mAppToken)) {
            finish();
            return;
        }

        setContentView(R.layout.pay_add_pay_activity);

        // 返回
        View backButton = findViewById(R.id.back_image_view);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 设置标题
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText(getString(R.string.pay_add_pay));

        final EditText phoneView = (EditText) findViewById(R.id.phone_edit_text);
        final EditText priceView = (EditText) findViewById(R.id.price_edit_text);
        final EditText commentView = (EditText) findViewById(R.id.comment_edit_text);

        TextView payTextView = (TextView) findViewById(R.id.tv_settlement_commit);
        payTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phoneView.getText().toString().trim();
                final String price = priceView.getText().toString().trim();
                final String comment = commentView.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_phone));
                    return;
                }
                if (phone.length() != 11) {
                    ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_right_phone));
                    return;
                }
                if (TextUtils.isEmpty(price)) {
                    ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_price));
                    return;
                }
                if (TextUtils.isEmpty(comment)) {
                    ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_comment));
                    return;
                }

                AlertDialog.showConfirmPhoneDialog(AddPayActivity.this, getString(R.string.again_confirm_phone), phone,
                   getString(R.string.again_input), getString(R.string.confirm_no_error), new AlertDialog.OnDismissListener() {
                        @Override
                        public void onDismiss(Bundle data) {
                            SettlementIntent settlementIntent = new SettlementIntent(AddPayActivity.this);
                            settlementIntent.setCrmUserId(mCrmUserId);
                            settlementIntent.setCrmUserName(mCrmUserName);
                            settlementIntent.setAppToken(mAppToken);
                            settlementIntent.setCustomerPhone(phone);
                            settlementIntent.setPrice(price);
                            settlementIntent.setComment(comment);
                            startActivityForResult(settlementIntent, PayListActivity.REQUEST_CODE_REFRESH);
                        }
                   });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayListActivity.REQUEST_CODE_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }
}
