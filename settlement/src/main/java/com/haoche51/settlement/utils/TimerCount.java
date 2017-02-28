package com.haoche51.settlement.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.haoche51.settlement.R;

/**
 * 时间倒计时
 */
public class TimerCount extends CountDownTimer {

    private Context mContext;
    private TextView mTextView;

    public TimerCount(Context context, long millisInFuture, long countDownInterval, TextView textView) {
        super(millisInFuture, countDownInterval);
        mContext = context;
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false);
        mTextView.setText(millisUntilFinished / 1000 + mContext.getString(R.string.pay_again_send_code));
    }

    @Override
    public void onFinish() {
        mTextView.setClickable(true);
        mTextView.setText("获取验证码");
    }
}
