package com.haoche51.settlement.onlinepay;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.cashiers.PaymentEntity;
import com.haoche51.settlement.cashiers.SettlementDetailActivity;
import com.haoche51.settlement.custom.HCPullToRefresh;
import com.haoche51.settlement.net.AppHttpServer;
import com.haoche51.settlement.net.HCHttpCallback;
import com.haoche51.settlement.net.HCHttpRequestParam;
import com.haoche51.settlement.net.HCHttpResponse;
import com.haoche51.settlement.net.HCJsonParse;
import com.haoche51.settlement.net.HttpConstants;
import com.haoche51.settlement.utils.ProgressDialogUtil;
import com.haoche51.settlement.utils.QueryLianLianUtil;
import com.haoche51.settlement.utils.ToastUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 收款账单
 */
public class PayListActivity extends Activity implements HCHttpCallback, HCPullToRefresh.OnRefreshCallback, AdapterView.OnItemClickListener {

    public static final String EXTRA_PAY_LIST_ENTITY = "pay_list_entity";
    public static final int REQUEST_CODE_REFRESH = 1000;

    // 每页加载数
    public static final int LIMIT = 10;

    private String mCrmUserId;
    private String mCrmUserName;
    private String mAppToken;
    private int mPage = 0;
    private String mUserPhone;

    private View mTitleBarLayout;
    private EditText mSearchEditText;
    private View mMaskView;
    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private PayListAdapter mAdapter;
    private boolean mUnconfirmList = false;

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

        setContentView(R.layout.pay_list_activity);

        mTitleBarLayout = findViewById(R.id.titlebar_layout);
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
        titleTextView.setText(getString(R.string.pay_pay_list));

        // 显示搜索
        ImageView showSearchView = (ImageView) findViewById(R.id.right_image_1);
        showSearchView.setImageResource(R.drawable.pay_top_search);
        showSearchView.setVisibility(View.VISIBLE);
        showSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleBarLayout.setVisibility(View.INVISIBLE);
                showAnimator(getResources().getDisplayMetrics().widthPixels, 0, true);
            }
        });

        // 搜索
        ImageView searchView = (ImageView) findViewById(R.id.search_image_view);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPhone();
            }
        });

        // 搜索输入框
        mSearchEditText = (EditText) findViewById(R.id.input_edit_text);
        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mMaskView != null && mMaskView.getVisibility() == View.GONE) {
                    mMaskView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchPhone();
                }
                return true;
            }
        });

        // 取消
        TextView cancelView = (TextView) findViewById(R.id.cancel_text_view);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSearch();
            }
        });

        // 搜索蒙板
        mMaskView = findViewById(R.id.mask_view);
        mMaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // ListView
        mPullToRefresh = (HCPullToRefresh) findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(getResources().getDrawable(R.color.pay_transparent));
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener(this);
        mAdapter = new PayListAdapter(PayListActivity.this.getApplication());
        mListView.setAdapter(mAdapter);

        // 查余额
        Button checkMoney = (Button) findViewById(R.id.check_money);
        checkMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayListActivity.this, BalanceQueryActivity.class);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, mCrmUserId);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, mCrmUserName);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, mAppToken);
                startActivityForResult(intent, REQUEST_CODE_REFRESH);
            }
        });

        // 添加收款
        Button addPayButton = (Button) findViewById(R.id.add_pay_button);
        addPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayListActivity.this, AddPayActivity.class);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_ID, mCrmUserId);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_CHECK_USER_NAME, mCrmUserName);
                intent.putExtra(SettlementDetailActivity.KEY_EXTRA_TOKEN, mAppToken);
                startActivityForResult(intent, REQUEST_CODE_REFRESH);
            }
        });

        loadData();
    }

    /**
     * 显示动画
     */
    private void showAnimator(int targetWidth, int startWdith, final boolean enable) {
        final ViewGroup.LayoutParams layoutParams = mTitleBarLayout.getLayoutParams();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(targetWidth, startWdith);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                layoutParams.width = (Integer) animator.getAnimatedValue();
                mTitleBarLayout.setLayoutParams(layoutParams);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (enable) {
                    // 弹出搜索界面
                    mSearchEditText.setFocusable(true);
                    mSearchEditText.setFocusableInTouchMode(true);
                    mSearchEditText.requestFocus();

                    mSearchEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.showSoftInput(mSearchEditText, 0);
                        }
                    }, 100);

                    mMaskView.setVisibility(View.VISIBLE);
                } else {
                    // 隐藏搜索界面
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                    mSearchEditText.setText("");
                    mSearchEditText.clearFocus();
                    mTitleBarLayout.setVisibility(View.VISIBLE);
                    mMaskView.setVisibility(View.GONE);
                }
            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    /**
     * 搜索手机号
     */
    private void searchPhone() {
        String phone = mSearchEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_phone));
            return;
        }
        if (phone.length() < 11) {
            ToastUtil.showInfo(getApplicationContext(), getString(R.string.pay_please_input_right_phone));
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);

        mUserPhone = phone;
        mSearchEditText.clearFocus();
        mMaskView.setVisibility(View.GONE);
        mPullToRefresh.setFirstAutoRefresh();
    }

    /**
     * 取消搜索
     */
    private void cancelSearch() {
        showAnimator(0, getResources().getDisplayMetrics().widthPixels, false);
        mUserPhone = null;
        mPullToRefresh.setFirstAutoRefresh();
    }

    /**
     * 加载第一页数据
     */
    private void loadData() {
        if (!ProgressDialogUtil.isShowProgress()) {
            ProgressDialogUtil.showProgressDialog(PayListActivity.this,
                    new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mUnconfirmList) {
                                finish();
                            }
                        }
                    });
        }

        mPage = 0;
        Map params = HCHttpRequestParam.getBalanceList(PayListActivity.this,
                mCrmUserId, mCrmUserName, mUserPhone, mPage, LIMIT, mAppToken);
        AppHttpServer.getInstance(mAppToken).post(this, params, PayListActivity.this, HttpConstants.GET_LIST_REFRESH);
    }

    @Override
    public void onPullDownRefresh() {
        mPage = 0;
        Map params = HCHttpRequestParam.getBalanceList(PayListActivity.this,
                mCrmUserId, mCrmUserName, mUserPhone, mPage, LIMIT, mAppToken);
        AppHttpServer.getInstance(mAppToken).post(this, params, PayListActivity.this, HttpConstants.GET_LIST_REFRESH);
    }

    @Override
    public void onLoadMoreRefresh() {
        mPage++;
        Map params = HCHttpRequestParam.getBalanceList(PayListActivity.this,
                mCrmUserId, mCrmUserName, mUserPhone, mPage, LIMIT, mAppToken);
        AppHttpServer.getInstance(mAppToken).post(this, params, PayListActivity.this, HttpConstants.GET_LIST_LOADMORE);
    }

    @Override
    public void onHttpStart(String action, int requestId) {
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
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }

        switch (response.getErrno()) {
            case 0:
                PayDataEntity payDataEntity = HCJsonParse.parsePayDataEntity(response.getData());
                if (payDataEntity == null) {
                    payDataEntity = new PayDataEntity();
                }

                List<PaymentEntity> paymentEntities = payDataEntity.getUnconfirm_list();
                if (paymentEntities != null && paymentEntities.size() > 0 && !TextUtils.isEmpty(payDataEntity.getOid_trader())
                    && payDataEntity.getYt_auth() != null && !TextUtils.isEmpty(payDataEntity.getYt_auth().getRSA_PRIVATE())) {
                    // 有没有确认的账单
                    mUnconfirmList = true;
                    QueryLianLianUtil.getInstance().query(PayListActivity.this, mCrmUserId, mCrmUserName, mAppToken,
                            payDataEntity.getUnconfirm_list(), payDataEntity.getOid_trader(), payDataEntity.getYt_auth().getRSA_PRIVATE(),
                            new QueryLianLianUtil.ResultListener() {
                                @Override
                                public void isComPlete(boolean isComPlete) {
                                    loadData();
                                }
                            });
                } else {
                    // 已经确认的账单
                    mUnconfirmList = false;
                    ProgressDialogUtil.closeProgressDialog();

                    List<PayListEntity> payListEntities = payDataEntity.getList();
                    if (payListEntities == null) {
                        payListEntities = new ArrayList<PayListEntity>(0);
                    }
                    if (requestId == HttpConstants.GET_LIST_REFRESH) {
                        mAdapter.setData(payListEntities);
                    } else {
                        mAdapter.addData(payListEntities);
                    }

                    boolean isNoMoreData = false;
                    if (payListEntities.size() < LIMIT) {
                        isNoMoreData = true;
                    }
                    if (mPage == 0 && isNoMoreData && payListEntities.size() > 0) {
                        mPullToRefresh.hideFooter();
                    } else {
                        mPullToRefresh.setFooterStatus(isNoMoreData);
                    }
                }
                break;
            default:
                mUnconfirmList = false;
                ProgressDialogUtil.closeProgressDialog();
                ToastUtil.showInfo(getApplicationContext(), response.getErrmsg());
                break;
        }
        mPullToRefresh.finishRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() != null) {
            Object object = parent.getAdapter().getItem(position);
            if (object != null && object instanceof PayListEntity) {
                PayListEntity entity = (PayListEntity) object;
                Intent intent = new Intent(PayListActivity.this, PayInfoActivity.class);
                intent.putExtra(EXTRA_PAY_LIST_ENTITY, entity);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REFRESH) {
            if (resultCode == Activity.RESULT_OK) {
                mPullToRefresh.setFirstAutoRefresh();
            }
        }
    }

    /**
     * 返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMaskView != null && mMaskView.getVisibility() == View.VISIBLE) {
                cancelSearch();
                return true;
            } else {
                finish();
            }
        }
        return false;
    }
}
