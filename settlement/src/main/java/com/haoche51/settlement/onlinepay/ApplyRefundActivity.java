package com.haoche51.settlement.onlinepay;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.cashiers.SettlementDetailActivity;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.net.HttpConstants;
import com.haoche51.settlement.utils.DisplayUtils;
import com.haoche51.settlement.utils.HCArithUtil;
import com.haoche51.settlement.utils.ProgressDialogUtil;
import com.haoche51.settlement.utils.SharedPreferencesUtils;
import com.haoche51.settlement.utils.TimerCount;
import com.haoche51.settlement.utils.ToastUtil;
import com.jungly.gridpasswordview.GridPasswordView;

import java.util.Map;

/**
 * 申请退款页面
 * Created by yangming on 2016/4/12.
 */
public class ApplyRefundActivity extends Activity implements HCHttpCallback, View.OnClickListener {

    private TextView tv_refund_blance;
    private EditText et_refund_price;
    private EditText et_refund_name;
    private EditText et_refund_bank;
    private EditText et_refund_card_number;
    private EditText mCommentEditText;

    String mCrmUserId = "";
    String mCrmUserName = "";
    String mAppToken = "";
    double balance = 0;//可用余额
    String phone = "";//客户电话号码
    private PopupWindow popupView;
    private boolean mValidateSuccess = false;
    private GridPasswordView gridPasswordView;
    private TextView mValidateSuccessView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrmUserId = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID);
        mCrmUserName = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME);
        mAppToken = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN);
        balance = getIntent().getDoubleExtra(SettlementDetailActivity.KEY_EXTRA_BALANCE, -1);
        phone = getIntent().getStringExtra(SettlementDetailActivity.KEY_EXTRA_PHONE);

        if (TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName)
                || TextUtils.isEmpty(mAppToken) || TextUtils.isEmpty(phone)) {
            ToastUtil.showInfo(getApplicationContext(), "参数错误");
            finish();
            return;
        }

        setContentView(R.layout.pay_apply_refund_activity);
        //back
        registerTitleBack();
        //title
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("申请退款");

        //可用余额
        tv_refund_blance = (TextView) findViewById(R.id.tv_refund_balance);
        tv_refund_blance.setText(DisplayUtils.parseMoney("#", balance));

        //姓名
        et_refund_name = (EditText) findViewById(R.id.et_refund_name);
        //退款金额
        et_refund_price = (EditText) findViewById(R.id.et_refund_price);
        //开户银行
        et_refund_bank = (EditText) findViewById(R.id.et_refund_bank);
        //银行卡号
        et_refund_card_number = (EditText) findViewById(R.id.et_refund_card_number);
        // 备注
        mCommentEditText = (EditText) findViewById(R.id.comment_edit_text);

        findViewById(R.id.tv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_commit).setOnClickListener(this);


        // 判断是否需要发送验证码
        boolean isNeedValidate = true;
//        if (!TextUtils.isEmpty(mCrmUserId) && !TextUtils.isEmpty(phone)) {
//            long validateTime = SharedPreferencesUtils.getLong(this, mCrmUserId + phone, 0);
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - validateTime < 60 * 60 * 1000) {
//                isNeedValidate = false;
//            }
//        }
        if (isNeedValidate) {
            // 发送验证码
            AppHttpServer.getInstance(mAppToken).post(this,
                    HCHttpRequestParam.sendVCode(this, mCrmUserId, phone, 2, mAppToken),
                    this, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showValidateDialog();
                }
            }, 100);
        }

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
     * 显示输入验证码对话框
     */
    private void showValidateDialog() {
        if (ApplyRefundActivity.this.isFinishing()) {
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pay_validate_dialog_layout, null);

        popupView = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupView.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pay_half_transparent)));
        popupView.setFocusable(true);
        popupView.showAtLocation(findViewById(R.id.bottom), Gravity.BOTTOM, 0, 0);
        popupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!mValidateSuccess) {
                    hideSoftInput();
                    finish();
                }
            }
        });

        // 返回
        ImageView backButton = (ImageView) view.findViewById(R.id.back_image_view);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });

        // 输入验证码提示
        String hint = getResources().getString(R.string.pay_input_vcode_2);
        hint = String.format(hint, phone);
        TextView hintView = (TextView) view.findViewById(R.id.input_hint_text_view);
        hintView.setText(hint);

        // 输入验证码框
        gridPasswordView = (GridPasswordView) view.findViewById(R.id.grid_password_view);
        gridPasswordView.setPasswordVisibility(true);
        gridPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String password) {
            }

            @Override
            public void onInputFinish(String password) {
                ProgressDialogUtil.showProgressDialog(ApplyRefundActivity.this, getString(R.string.pay_validate_loading));
                AppHttpServer.getInstance(mAppToken).post(ApplyRefundActivity.this
                        , HCHttpRequestParam.checkVCode(ApplyRefundActivity.this, password, phone, mAppToken)
                        , ApplyRefundActivity.this, 0);
            }
        });

        // 60秒后重新发送
        TextView againSendView = (TextView) view.findViewById(R.id.again_send_text_view);
        final TimerCount timerCount = new TimerCount(ApplyRefundActivity.this, 60000, 1000, againSendView);
        timerCount.start();
        againSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerCount.start();
                Map<String, Object> params = HCHttpRequestParam.sendVCode(ApplyRefundActivity.this, mCrmUserId, phone, 2, mAppToken);
                AppHttpServer.getInstance(mAppToken).post(ApplyRefundActivity.this, params, ApplyRefundActivity.this, 0);
            }
        });

        // 验证成功view
        mValidateSuccessView = (TextView) view.findViewById(R.id.validata_success_text_view);
    }

    /**
     * 隐藏输入法
     */
    private void hideSoftInput() {
        if (gridPasswordView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(gridPasswordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void applyRefund() {
        ProgressDialogUtil.showProgressDialog(this, "");
        String tempPrice = DisplayUtils.parseMoney("#"
                , HCArithUtil.mul(Double.valueOf(et_refund_price.getText().toString().trim()), 100));
        String name = et_refund_name.getText().toString().trim();
        String bank = et_refund_bank.getText().toString().trim();
        String cardNumber = et_refund_card_number.getText().toString().trim();
        String comment = mCommentEditText.getText().toString().trim();
        AppHttpServer.getInstance(mAppToken).post(this,
                HCHttpRequestParam.newReturn(this, mCrmUserId, mCrmUserName, phone
                        , tempPrice, name, bank, cardNumber, comment, mAppToken), this, 0);
    }

    @Override
    public void onHttpStart(String action, int requestId) {

    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (HttpConstants.ACTION_CHECK_VCODE.equals(action)) {
            responseVCode(response);
        } else if (HttpConstants.ACTION_NEW_RETURN.equals(action)) {
            responseNewReturn(response);
        }

    }


    /**
     * 校验验证码
     *
     * @param response
     */
    private void responseVCode(HCHttpResponse response) {
        ProgressDialogUtil.closeProgressDialog();
        switch (response.getErrno()) {
            case 0:
                hideSoftInput();
                if (mValidateSuccessView != null) {
                    mValidateSuccessView.setVisibility(View.VISIBLE);
                }
                // 保存验证状态
                SharedPreferencesUtils.saveData(ApplyRefundActivity.this, mCrmUserId + phone, System.currentTimeMillis());
                mValidateSuccess = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (popupView != null) {
                            popupView.dismiss();
                        }
                    }
                }, 1000);
                break;
            default:
                ToastUtil.showTextLong(getApplicationContext(), response.getErrmsg());
                if (gridPasswordView != null) {
                    gridPasswordView.clearPassword();
                }
                break;
        }
    }


    /**
     * 申请退款
     *
     * @param response
     */
    private void responseNewReturn(HCHttpResponse response) {
        ProgressDialogUtil.closeProgressDialog();
        switch (response.getErrno()) {
            case 0:
                setResult(RESULT_OK);
                finish();
                break;
            default:
                ToastUtil.showInfo(getApplicationContext(), response.getErrmsg());
                break;
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            finish();
        } else if (v.getId() == R.id.tv_commit) {

            if (TextUtils.isEmpty(et_refund_price.getText().toString().trim())) {
                ToastUtil.showInfo(getApplicationContext(), "请填写退款金额");
                return;
            }
            if (Double.valueOf(et_refund_price.getText().toString().trim()) > Double.valueOf(balance)) {
                ToastUtil.showInfo(getApplicationContext(), "退款金额不能大于可用余额");
                return;
            }
            if (TextUtils.isEmpty(et_refund_name.getText().toString().trim())) {
                ToastUtil.showInfo(getApplicationContext(), "请填写开户姓名");
                return;
            }
            if (TextUtils.isEmpty(et_refund_bank.getText().toString().trim())) {
                ToastUtil.showInfo(getApplicationContext(), "请填写开户银行");
                return;
            }
            if (TextUtils.isEmpty(et_refund_card_number.getText().toString().trim())) {
                ToastUtil.showInfo(getApplicationContext(), "请填写银行卡号");
                return;
            }
            applyRefund();
        }

    }

    @Override
    public void finish() {
        super.finish();
        if (popupView.isShowing()) {
            popupView.dismiss();
        }
    }
}
