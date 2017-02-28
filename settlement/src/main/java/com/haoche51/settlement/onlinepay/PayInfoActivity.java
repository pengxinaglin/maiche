package com.haoche51.settlement.onlinepay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.utils.UnixTimeUtil;

/**
 * 账单明细
 */
public class PayInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        PayListEntity payListEntity = (PayListEntity) intent.getSerializableExtra(PayListActivity.EXTRA_PAY_LIST_ENTITY);
        if (payListEntity == null) {
            finish();
            return;
        }

        setContentView(R.layout.pay_info_activity);
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
        titleTextView.setText(getString(R.string.pay_pay_info));

        // 收款状态
        TextView showStatusView = (TextView) findViewById(R.id.show_status_desc_view);
        showStatusView.setText(payListEntity.getShow_status_desc());

        // 交易金额
        TextView tradeAmountView = (TextView) findViewById(R.id.trade_amount_view);
        if (payListEntity.getShow_status() == 1) {
            tradeAmountView.setText("+" + payListEntity.getTradeAmount() + ".00");
        } else {
            tradeAmountView.setText("-" + payListEntity.getTradeAmount() + ".00");
        }

        // 客户手机
        TextView buyerPhoneView = (TextView) findViewById(R.id.buyer_phone_view);
        buyerPhoneView.setText(payListEntity.getBuyer_phone());

        // 可用余额
        TextView unusedAmountView = (TextView) findViewById(R.id.unused_amount_view);
        unusedAmountView.setText(payListEntity.getUnusedAmount() + "元");

        // 创建时间
        TextView createTimeView = (TextView) findViewById(R.id.create_time_view);
        createTimeView.setText(UnixTimeUtil.format(payListEntity.getCreate_time(), UnixTimeUtil.ALL_PATTERN));

        // 备注
        TextView commentView = (TextView) findViewById(R.id.comment_view);
        if (!TextUtils.isEmpty(payListEntity.getComment())) {
            commentView.setText(payListEntity.getComment());
        }
    }
}
