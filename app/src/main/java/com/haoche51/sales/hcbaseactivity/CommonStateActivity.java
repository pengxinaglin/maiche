package com.haoche51.sales.hcbaseactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.BroadcastConstants;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.ThirdPartInjector;
import com.lidroid.xutils.ViewUtils;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

/**
 * Created by yangming on 2015/11/30.
 */
public abstract class CommonStateActivity extends Activity implements Validator.ValidationListener, HCHttpCallback {

  public final int RESULT_NODATA = 0;
  public final int RESULT_ERROR = 1;
  //  private RelativeLayout stateLayout;
  private FrameLayout mContentViewTitle;
  private FrameLayout mContentViewContainer;
  private View mContentView;
  private View mLoadingView;
  private View mResultView;

  public String className;
  /*表单校验*/
  public Validator validator;

  public CommonStateActivity() {
    this.className = this.getClass().getCanonicalName();
  }

  protected abstract int getContentView();

  protected int getTitleView() {
    return 0;
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

  protected void initView() {
//    stateLayout = (RelativeLayout) findViewById(R.id.state_layout);
    setContentView(R.layout.activity_common_status);

    mContentViewTitle = (FrameLayout) findViewById(R.id.fl_common_content_title);
    mContentViewContainer = (FrameLayout) findViewById(R.id.fl_common_content_container);

    if (getTitleView() == 0) {
      mContentViewTitle.addView(View.inflate(this, R.layout.layout_common_titlebar, null));
    } else {
      mContentViewTitle.addView(View.inflate(this, getTitleView(), null));
    }

    mContentView = View.inflate(this, getContentView(), null);
    mContentViewContainer.addView(mContentView);
    ViewUtils.inject(this);
    registerTitleBack();
  }

  protected void initData() {
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


  @Override
  public void setContentView(final int layoutResId) {
    super.setContentView(layoutResId);
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
   * 页面中显示加载进度圈圈
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   */
  public void showLoadingView(boolean contentVisible) {
    showLoadingView(contentVisible, getString(R.string.loading));
  }

  /**
   * 页面中显示加载进度圈圈
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param text
   */
  public void showLoadingView(boolean contentVisible, String text) {
    if (mLoadingView == null) {
      mLoadingView = LayoutInflater.from(this).inflate(
        R.layout.layout_common_loading_view, null);
    }
    final TextView textView = (TextView) mLoadingView.findViewById(R.id.loading_txt);
    textView.setText(text);
    mContentViewContainer.removeView(mLoadingView);
    mContentViewContainer.addView(mLoadingView, new LayoutParams(
      LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    if (!contentVisible) {
      mContentView.setVisibility(View.INVISIBLE);
    }
  }

  /**
   * 显示页面结果视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param result         结果类型
   * @param text           显示文案
   * @param l              点击监听
   */
  public void showResultView(boolean contentVisible, int result, String text, View.OnClickListener l) {
    if (mResultView == null) {

      mResultView = LayoutInflater.from(this).inflate(
        R.layout.layout_common_result_view, null);
    }
    final TextView textView = (TextView) mResultView
      .findViewById(R.id.result_txt);
    textView.setText(text);

    if (result == RESULT_NODATA) {
      textView.setCompoundDrawablesWithIntrinsicBounds(0,
        R.drawable.common_button_nodata, 0, 0);

      TextView textViewNoData = (TextView) mResultView
        .findViewById(R.id.click_txt);
      textViewNoData.setVisibility(View.GONE);
    } else if (result == RESULT_ERROR) {
      textView.setCompoundDrawablesWithIntrinsicBounds(0,
        R.drawable.common_button_offline, 0, 0);
    }
    mContentViewContainer.removeView(mResultView);
    mContentViewContainer.addView(mResultView, new LayoutParams(
      LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    if (!contentVisible) {
      mContentView.setVisibility(View.INVISIBLE);
    }

    textView.setOnClickListener(l);
  }

  /**
   * 显示无数据视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param l              点击监听
   */
  public void showNoDataView(boolean contentVisible, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_NODATA, getString(R.string.hc_common_result_nodata), l);
  }

  /**
   * 显示无数据视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param text           显示文案
   * @param l              点击事件
   */
  public void showNoDataView(boolean contentVisible, String text, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_NODATA, text, l);
  }

  /**
   * 显示页面错误视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param text           显示文案
   * @param l              点击事件
   */
  public void showErrorView(boolean contentVisible, String text, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_ERROR, text, l);
  }

  /**
   * 显示页面错误视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   * @param l              点击事件
   */
  public void showErrorView(boolean contentVisible, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_ERROR, getString(R.string.hc_common_result_offline), l);
  }

  /**
   * 取消页面加载视图
   */
  public void dismissLoadingView() {
    if (mLoadingView != null) {
      mContentViewContainer.removeView(mLoadingView);
    }
    mContentView.setVisibility(View.VISIBLE);
  }

  /**
   * 取消页面结果视图
   *
   * @param contentVisible 页面原内容是否显示 true 显示原页面内容； false 隐藏原页面内容
   */
  public void dismissResultView(boolean contentVisible) {
    if (mResultView != null) {
      mContentViewContainer.removeView(mResultView);
    }
    if (!contentVisible) {
      mContentView.setVisibility(View.INVISIBLE);
    } else {
      mContentView.setVisibility(View.VISIBLE);
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
