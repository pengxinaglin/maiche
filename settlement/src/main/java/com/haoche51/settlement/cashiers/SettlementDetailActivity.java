package com.haoche51.settlement.cashiers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.net.HCJsonParse;
import com.haoche51.settlement.net.HttpConstants;
import com.haoche51.settlement.utils.AlertDialog;
import com.haoche51.settlement.utils.DisplayUtils;
import com.haoche51.settlement.utils.HCActionUtil;
import com.haoche51.settlement.utils.HCArithUtil;
import com.haoche51.settlement.utils.ProgressDialogUtil;
import com.haoche51.settlement.utils.QueryLianLianUtil;
import com.haoche51.settlement.utils.SharedPreferencesUtils;
import com.haoche51.settlement.utils.TimerCount;
import com.haoche51.settlement.utils.ToastUtil;
import com.jungly.gridpasswordview.GridPasswordView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结算模块收款详情页面
 * 请求验证接口：需要验证，发送验证码；不需验证，关闭对话框，跳过验证；
 * 验证通过，通知服务端结果
 * <p/>
 * 0、现金支付，必传任务类型和任务编号（没有任务编号不现实现金支付）
 * 1、计算剩余应收，剩余应收款=应收-本次可使用金额，
 * 4、剩余应收款未收齐时，点击返回有弹框提示
 * 5、有现金收款时，点击完成弹框提示，确定后完成提交；取消留在当前页
 * 6、本次可使用，，展示全部客户可用余额，本次可使用小于等于客户可用余额，同时小于等于应收款
 * 7、pos机收款成功后，将相应的金额加到客户可用余额和本此可使用上（不含手续费）
 * 8、快速收款和意向金，本次可使用为0，后面每次刷卡按次累加
 * <p/>
 * <p/>
 * 返回结果：
 * 收款完成 result_OK , 返回 余额，本次可使用，现金金额 三部分；
 * 收款为完成 result_CANCEL
 * <p/>
 * Created by yangming on 2016/3/30.
 */
public class SettlementDetailActivity extends Activity implements HCHttpCallback, View.OnClickListener {


    public final static String KEY_EXTRA_CHECK_USER_ID = "check_user_id";
    public final static String KEY_EXTRA_CHECK_USER_NAME = "check_user_name";
    public final static String KEY_EXTRA_TOKEN = "key_extra_token";
    public final static String KEY_EXTRA_TASK_ID = "mTaskId";
    public final static String KEY_EXTRA_TASK_TYPE = "mTaskType";
    public final static String KEY_EXTRA_PRICE = "key_extra_price";
    public final static String KEY_EXTRA_PHONE = "phone";
    public final static String KEY_EXTRA_NAME = "name";
    public final static String KEY_EXTRA_FROM_BUSINESS = "key_extra_from_business";
    public final static String KEY_EXTRA_CASH_ENABLE = "key_extra_cash_enable";
    public final static String KEY_EXTRA_COMMENT = "key_extra_comment";
    public final static String KEY_EXTRA_BALANCE = "key_extra_balance";

    public final static String RESULT_KEY_EXTRA_BALANCE = "balance";
    public final static String RESULT_KEY_EXTRA_USABLE = "usable";

    public final static int REQUEST_CODE_POS_PAY = 0;

    private TextView tv_settlement_commit;//
    private TextView tv_settlement_total;//
    private TextView tv_settlement_usage;//
    private TextView tv_settlement_remaind;//
    private TextView tv_settlement_order_info;//
    private TextView tv_settlement_receivable;//
    private GridPasswordView gridPasswordView;
    private LinearLayout bottom;

    String mCrmUserId;          //必传，mCrmUserId 接口参数中使用
    String mCrmUserName;        //必传，mCrmUserName 接口参数中使用
    String mAppToken = "";      //必传，apptoken，接口参数，加密解密使用
    String mTaskId = "";    //非必，业务订单id，可以没有不传
    int mTaskType;          //非必，任务类型，1c2c交易 2回购 3金融
    String price = "0";     //必传，应收款
    String name = "";       //非必，客户名字
    String phone = "";      //必传，客户电话号码
    String comment = "";      //非必，备注
    private boolean fromBusiness = false;//从业务方调用（非线上收款和意向金收款）

    double balance = 0; //余额
    double usable = 0;  //本次可用
    double remaind;     //剩余应收

    private PopupWindow popupView;
    private TextView mValidateSuccessView;
    private boolean mValidateSuccess = false;

    private DetailEntity detailEntity;
    private FrameLayout mContentViewContainer;
    private View mResultView;
    private boolean mUnconfirmList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppToken = getIntent().getStringExtra(KEY_EXTRA_TOKEN);
        mCrmUserName = getIntent().getStringExtra(KEY_EXTRA_CHECK_USER_NAME);
        mCrmUserId = getIntent().getStringExtra(KEY_EXTRA_CHECK_USER_ID);
        mTaskId = getIntent().getStringExtra(KEY_EXTRA_TASK_ID);
        mTaskType = getIntent().getIntExtra(KEY_EXTRA_TASK_TYPE, -1);
        price = getIntent().getStringExtra(KEY_EXTRA_PRICE);
        phone = getIntent().getStringExtra(KEY_EXTRA_PHONE);
        name = getIntent().getStringExtra(KEY_EXTRA_NAME);
        comment = getIntent().getStringExtra(KEY_EXTRA_COMMENT);
        fromBusiness = getIntent().getBooleanExtra(KEY_EXTRA_FROM_BUSINESS, false);

        if (TextUtils.isEmpty(mAppToken) || TextUtils.isEmpty(mCrmUserId) || TextUtils.isEmpty(mCrmUserName)
                || TextUtils.isEmpty(price) || TextUtils.isEmpty(phone)) {
            ToastUtil.showTextLong(getApplicationContext(), "参数错误");
            finish();
            return;
        }
        setContentView(R.layout.pay_detail_activity);

        //back
        registerTitleBack();
        //title
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("收款详情");

        //订单信息
        tv_settlement_order_info = (TextView) findViewById(R.id.tv_settlement_order_info);
        //应收款
        tv_settlement_receivable = (TextView) findViewById(R.id.tv_settlement_receivable);

        //客户余额
        tv_settlement_total = (TextView) findViewById(R.id.tv_settlement_total);
        tv_settlement_usage = (TextView) findViewById(R.id.tv_settlement_usage);
        findViewById(R.id.iv_detail_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.createRuleDialog(SettlementDetailActivity.this);
            }
        });

        //剩余应收款
        tv_settlement_remaind = (TextView) findViewById(R.id.tv_settlement_remaind);
        //收款提交
        tv_settlement_commit = (TextView) findViewById(R.id.tv_settlement_commit);
        tv_settlement_commit.setOnClickListener(this);

        bottom = (LinearLayout) findViewById(R.id.bottom);
        setData();
    }

    private void setData() {
        tv_settlement_order_info.setText(mTaskId);
        tv_settlement_receivable.setText(price);

        ProgressDialogUtil.showProgressDialog(this, "", false);
        getAccountDetail();

        // 判断是否需要发送验证码
//        boolean isNeedValidate = true;
//        if (!TextUtils.isEmpty(mCrmUserId) && !TextUtils.isEmpty(phone)) {
//            long validateTime = SharedPreferencesUtils.getLong(this, mCrmUserId + phone, 0);
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - validateTime < 60 * 60 * 1000) {
//                isNeedValidate = false;
//            }
//        }
//        if (isNeedValidate) {
//            // 发送验证码
//            AppHttpServer.getInstance(mAppToken).post(SettlementDetailActivity.this,
//                    HCHttpRequestParam.sendVCode(SettlementDetailActivity.this, mCrmUserId, phone, 1, mAppToken),
//                    SettlementDetailActivity.this, 0);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showValidateDialog();
//                }
//            }, 100);
//        } else {
//            ProgressDialogUtil.showProgressDialog(this, null);
//            getAccountDetail();
//        }
    }

    /**
     * 显示输入验证码对话框
     */
    private void showValidateDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pay_validate_dialog_layout, null);

        popupView = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupView.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.pay_half_transparent)));
        popupView.setFocusable(true);
        popupView.showAtLocation(bottom, Gravity.BOTTOM, 0, 0);
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
        String hint;
        if (TextUtils.isEmpty(name)) {
            hint = getResources().getString(R.string.pay_input_vcode_2);
            hint = String.format(hint, phone);
        } else {
            hint = getResources().getString(R.string.pay_input_vcode);
            hint = String.format(hint, name, phone);
        }

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
                ProgressDialogUtil.showProgressDialog(SettlementDetailActivity.this, getString(R.string.pay_validate_loading), false);
                AppHttpServer.getInstance(mAppToken).post(SettlementDetailActivity.this
                        , HCHttpRequestParam.checkVCode(SettlementDetailActivity.this, password, phone, mAppToken)
                        , SettlementDetailActivity.this, 0);
            }
        });

        // 60秒后重新发送
        TextView againSendView = (TextView) view.findViewById(R.id.again_send_text_view);
        final TimerCount timerCount = new TimerCount(SettlementDetailActivity.this, 60000, 1000, againSendView);
        timerCount.start();
        againSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerCount.start();
                Map<String, Object> params = HCHttpRequestParam.sendVCode(SettlementDetailActivity.this, mCrmUserId, phone, 1, mAppToken);
                AppHttpServer.getInstance(mAppToken).post(SettlementDetailActivity.this, params, SettlementDetailActivity.this, 0);
            }
        });

        // 验证成功view
        mValidateSuccessView = (TextView) view.findViewById(R.id.validata_success_text_view);
    }


    /**
     * 获取详情接口
     */
    private void getAccountDetail() {
        AppHttpServer.getInstance(mAppToken).post(SettlementDetailActivity.this,
                HCHttpRequestParam.getAccountDetail(SettlementDetailActivity.this, phone, 1, mAppToken),
                SettlementDetailActivity.this, 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_settlement_commit) {
            if (detailEntity == null) {
                return;
            }
            if (remaind > 0) {//收款
                Map<String, Object> map = new HashMap<>();
                map.put(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, mCrmUserId);
                map.put(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, mCrmUserName);
                map.put(SettlementDetailActivity.KEY_EXTRA_TOKEN, mAppToken);
                map.put(SettlementDetailActivity.KEY_EXTRA_PRICE, remaind);// 传剩余应收
                map.put(SettlementDetailActivity.KEY_EXTRA_PHONE, phone);
                if (!TextUtils.isEmpty(mTaskId)) {
                    map.put(SettlementDetailActivity.KEY_EXTRA_TASK_ID, mTaskId);
                }
                map.put(SettlementDetailActivity.KEY_EXTRA_TASK_TYPE, mTaskType);
                if (!TextUtils.isEmpty(comment)) {
                    map.put(SettlementDetailActivity.KEY_EXTRA_COMMENT, comment);
                }

                HCActionUtil.launchActivityForResult(this, SettlementPospayActivity.class, map, REQUEST_CODE_POS_PAY);
            } else if (remaind <= 0) {//完成，没有现金收款
                returnResult(true);
            }
        }

    }


    protected void registerTitleBack() {
        View backBtn = findViewById(R.id.back_image_view);
        if (backBtn != null) {
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    doBack();
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
            doBack();
        }
        return false;
    }


    private void doBack() {
        if (detailEntity == null) {
            finish();
            return;
        }
        if (remaind > 0) {//剩余应收款没有收起，弹出提示对话框
            showBackConfirmDialog();
        } else {//收款完成，需判断剩余应收款是否有现金部分
            //完成的逻辑
            //没有现金，返回结果
            hideSoftInput();
            returnResult(true);
        }
    }

    /**
     * 剩余应收款未收齐时，点击返回有弹框提示
     */
    private void showBackConfirmDialog() {
        AlertDialog.createNormalDialog(this, "应收款没有收齐，是否确认离开", new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss(Bundle data) {
                if (data != null && !data.getBoolean("determine")) {
                    //确定离开
                    hideSoftInput();
                    returnResult(false);
                }
            }
        });
    }


    /**
     * 计算剩余应收
     */
    private void calculationRemaind() {
        remaind = HCArithUtil.sub(Double.valueOf(price), usable);
        if (remaind < 0) {
            remaind = 0;
        }
        tv_settlement_remaind.setText(DisplayUtils.parseMoney("###", remaind));
    }


    /**
     * 置底部按钮状态
     */
    private void reSetBottomButton() {
        if (remaind <= 0) {
            //剩余应收款已经收齐，按钮变成完成，点击完成收款
            tv_settlement_commit.setText("完成");
        } else {
            tv_settlement_commit.setText("支付");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_POS_PAY && resultCode == RESULT_OK && data != null) {
            // 本次可用增加, data中带回刷卡金额
            String tempPrice = data.getStringExtra("price");
            usable = HCArithUtil.add(usable, Double.valueOf(tempPrice));
            tv_settlement_usage.setText(DisplayUtils.parseMoney("###", usable));
            //计算剩余应收，赋值，
            calculationRemaind();
            //置底部按钮状态
            reSetBottomButton();
        }
        //请求详情接口，刷新余额
        ProgressDialogUtil.showProgressDialog(this, "", false);
        getAccountDetail();
    }

    @Override
    public void onHttpStart(String action, int requestId) {

    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (HttpConstants.ACTION_CHECK_VCODE.equals(action)) {
            responseVCode(response);
        } else if (HttpConstants.ACTION_GETACCOUNTDETAIL.equals(action)) {
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
     * 校验验证码
     */
    private void responseVCode(final HCHttpResponse response) {
        ProgressDialogUtil.closeProgressDialog();
        switch (response.getErrno()) {
            case 0:
                hideSoftInput();
                if (mValidateSuccessView != null) {
                    mValidateSuccessView.setVisibility(View.VISIBLE);
                }
                // 保存验证状态
                SharedPreferencesUtils.saveData(SettlementDetailActivity.this, mCrmUserId + phone, System.currentTimeMillis());
                mValidateSuccess = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (popupView != null) {
                            popupView.dismiss();
                        }
                    }
                }, 1000);

                // 请求详情接口
                ProgressDialogUtil.showProgressDialog(this, "", false);
                getAccountDetail();
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
     * 隐藏输入法
     */
    private void hideSoftInput() {
        if (gridPasswordView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(gridPasswordView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

                List<PaymentEntity> paymentEntities = detailEntity.getUnconfirm_list();
                if (paymentEntities != null && paymentEntities.size() > 0) {
                    //　查询连连　
                    mUnconfirmList = true;
                    QueryLianLianUtil.getInstance().query(this, mCrmUserId, mCrmUserName, mAppToken
                            , paymentEntities, detailEntity.getOid_trader(), detailEntity.getYt_auth().getRSA_PRIVATE()
                            , new QueryLianLianUtil.ResultListener() {
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
                    mUnconfirmList = false;
                    ProgressDialogUtil.closeProgressDialog();
                    if (TextUtils.isEmpty(detailEntity.getUser_amount())) {
                        balance = 0;
                    } else {
                        balance = HCArithUtil.div(Double.valueOf(detailEntity.getUser_amount()), 100);//
                    }
                    tv_settlement_total.setText(DisplayUtils.parseMoney("###", balance));
                    //本次可使用，
                    if (fromBusiness && balance <= Double.valueOf(price)) {
                        usable = balance;
                    } else if (fromBusiness && balance > Double.valueOf(price)) {
                        usable = Double.valueOf(price);
                    }
                    tv_settlement_usage.setText(DisplayUtils.parseMoney("###", usable));
                    //计算剩余应收
                    calculationRemaind();
                    reSetBottomButton();
                }

                break;
            default:
                mUnconfirmList = false;
                ProgressDialogUtil.closeProgressDialog();
                ToastUtil.showInfo(getApplicationContext(), response.getErrmsg());
                break;
        }
    }

//    /**
//     * 创建现金充值订单/newOfflinePay
//     */
//    private void responseOffLinePay(HCHttpResponse response) {
//        ProgressDialogUtil.closeProgressDialog();
//        switch (response.getErrno()) {
//            case 0:
//                returnResult(true);
//                break;
//            default:
//                ToastUtil.showInfo(getApplicationContext(), "网络请求失败，请重新提交");
//                break;
//        }
//
//    }


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
                ProgressDialogUtil.showProgressDialog(SettlementDetailActivity.this, "", false);
                getAccountDetail();
            }
        });
    }

    public void dismissResultView() {
        mContentViewContainer.removeView(mResultView);
    }


    /**
     * 返回收款结果
     */
    private void returnResult(boolean isComplete) {

        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY_EXTRA_BALANCE, balance);//余额
        intent.putExtra(RESULT_KEY_EXTRA_USABLE, usable);  //本次可使用
//        intent.putExtra(RESULT_KEY_EXTRA_CASH, cash);      //现金
        if (isComplete) {//收款完成
            setResult(RESULT_OK, intent);
        } else {//收款未完成返回
            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }
}
