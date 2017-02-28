package com.haoche51.sales.hcbaseactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.BroadcastConstants;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.ThirdPartInjector;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

/**
 * Created by xuhaibo on 15/9/11.
 */
public abstract class CommonBaseActivity extends Activity implements Validator.ValidationListener, HCHttpCallback {

  public String className;

  //	public Context mContext;
  /* titlebar return*/
  private TextView mReturn;
  /* title content */
  private TextView mTitle;
  /* titleBar rightFaction */
  private TextView mRightFaction;
  /* haoche51 content view*/
  private FrameLayout mContentViewContainer;
  /*表单校验*/
  public Validator validator;

  public CommonBaseActivity() {
    this.className = this.getClass().getCanonicalName();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //透明状态栏
//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    //透明导航栏
//    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    //注册广播，用于退出时关闭页面
    IntentFilter finishIntent = new IntentFilter();
    finishIntent.addAction(BroadcastConstants.Broadcast.ACTION_FINISH_MAIN);
    registerReceiver(finishReceiver, finishIntent);

    // 初始化Validator
    validator = new Validator(this);
    validator.setValidationListener(this);

    initView();
    initData();

  }

  /**
   * 广播接收器--接收关闭页面的广播--finsh
   */
  private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      try {
        finish();
      } catch (Exception e) {
      }
    }
  };

  @Override
  public void setContentView(final int layoutResId) {
    super.setContentView(layoutResId);
  }

  protected void initView() {
  }

  protected void initData() {
  }

  @Override
  protected void onResume() {
    super.onResume();
    String name = "UMENG_STATISTIC_" + this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    String name = "UMENG_STATISTIC_" + this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }

  @Override
  protected void onDestroy() {
    unregisterReceiver(finishReceiver);
    super.onDestroy();
  }


  /**
   * 校验失败
   *
   * @param failedView
   * @param failedRule
   */
  @Override
  public void onValidationFailed(View failedView, Rule<?> failedRule) {
    // 验证失败
    String message = failedRule.getFailureMessage();
    validation(failedView, message);
  }

  /**
   * 校验成功
   */
  @Override
  public void onValidationSucceeded() {
    // 验证成功
  }

  /**
   * 显示校验失败错误消息
   *
   * @param failedView
   * @param message
   */
  public void showErrorMsg(View failedView, String message) {
    validation(failedView, message);
  }

  /**
   * 显示校验失败消息
   *
   * @param failedView
   * @param message
   */
  private void validation(View failedView, String message) {
    if (failedView instanceof EditText || !failedView.isEnabled()) {
      failedView.requestFocus();
      ((EditText) failedView).setError(message);
    } else {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * 设置title
   *
   * @param titleRes
   */
  protected void setScreenTitle(int titleRes) {
    TextView titleTextView = (TextView) findViewById(R.id.tv_common_title);
    if (titleTextView != null) {
      titleTextView.setText(titleRes);
    }
  }

  /**
   * 设置title
   *
   * @param title
   */
  protected void setScreenTitle(String title) {
    TextView titleTextView = (TextView) findViewById(R.id.tv_common_title);
    titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    titleTextView.setSingleLine(true);
    if (titleTextView != null) {
      titleTextView.setText(title);
    }
  }

  /**
   * 注册back功能
   */
  protected void registerTitleBack() {
    View backBtn = findViewById(R.id.tv_common_back);
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
   * 网络请求开始
   *
   * @param action 当前请求action
   */
  @Override
  public void onHttpStart(String action, int requestId) {

  }

  /**
   * 网络请求结束,请求服务器成功，请求服务器失败
   *
   * @param action    当前请求action
   * @param requestId
   * @param response  hc 请求结果
   * @param error     网络问题造成failed 的error
   */
  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {

  }

  /**
   * 网络请求进度
   *
   * @param action       当前请求action
   * @param bytesWritten
   * @param totalSize
   */
  @Override
  public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

  }

  /**
   * 重试次数回调
   *
   * @param action  当前请求action
   * @param retryNo 重试次数
   */
  @Override
  public void onHttpRetry(String action, int requestId, int retryNo) {

  }

  /**
   * 请求完成
   *
   * @param action
   * @param requestId
   */
  @Override
  public void onHttpFinish(String action, int requestId) {

  }
}
