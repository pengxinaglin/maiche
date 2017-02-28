package com.haoche51.sales.hcbasefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpResponse;

/**
 * Created by yangming on 2015/11/2.
 */
public abstract class CommonBaseFragment extends Fragment implements HCHttpCallback {

  public final int RESULT_NODATA = 0;
  public final int RESULT_ERROR = 1;

  private RelativeLayout mRootView;
  private View mContentView;
  private View mLoadingView;
  private View mResultView;

  protected abstract int getContentView();

  protected void initView(View view) {
  }

  protected void initData(Bundle savedInstanceState) {
  }

  public void doingFilter(Bundle where) {
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (mRootView != null) {
      ViewGroup parent = (ViewGroup) mRootView.getParent();
      if (parent != null) {
        parent.removeView(mRootView);
      }
    } else {
      mRootView = new RelativeLayout(getActivity());
      final int layoutId = getContentView();
      if (layoutId == 0) {
        mContentView = super.onCreateView(inflater, container,
          savedInstanceState);
      }
      mContentView = inflater.inflate(getContentView(), container, false);
      mRootView.addView(mContentView, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    return mRootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(mRootView);
    initData(savedInstanceState);
  }

  public void showLoadingView(boolean contentVisible) {
    if (getActivity() == null)
      return;
    showLoadingView(contentVisible, GlobalData.mContext.getString(R.string.loading));
  }

  public void showLoadingView(boolean contentVisible, String text) {
    if (getActivity() == null)
      return;
    if (mLoadingView == null) {
      mLoadingView = LayoutInflater.from(getActivity()).inflate(
        R.layout.layout_common_loading_view, null);
    }
    final TextView textView = (TextView) mLoadingView
      .findViewById(R.id.loading_txt);
    textView.setText(text);
    mRootView.removeView(mLoadingView);
    mRootView.addView(mLoadingView, new RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    if (!contentVisible) {
      mContentView.setVisibility(View.INVISIBLE);
    }
  }

  public void dismissLoadingView() {
    if (getActivity() == null)
      return;
    if (mLoadingView != null) {
      mRootView.removeView(mLoadingView);
    }
    mContentView.setVisibility(View.VISIBLE);
  }

  public void showNoDataView(boolean contentVisible, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_NODATA, GlobalData.mContext.getString(R.string.hc_common_result_nodata), l);
  }

  public void showNoDataView(boolean contentVisible, String text, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_NODATA, text, l);
  }

  public void showErrorView(boolean contentVisible, String text, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_ERROR, text, l);
  }

  public void showErrorView(boolean contentVisible, View.OnClickListener l) {
    showResultView(contentVisible, RESULT_ERROR, GlobalData.mContext.getString(R.string.hc_common_result_offline), l);
  }

  public void showResultView(boolean contentVisible, int result, String text, View.OnClickListener l) {
    if (getActivity() == null)
      return;
    if (mResultView == null) {
      mResultView = LayoutInflater.from(getActivity()).inflate(
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
    mRootView.removeView(mResultView);
    mRootView.addView(mResultView, new RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    if (!contentVisible) {
      mContentView.setVisibility(View.INVISIBLE);
    }

    textView.setOnClickListener(l);
  }

  public void dismissResultView(boolean contentVisible) {
    if (getActivity() == null)
      return;
    if (mResultView != null) {
      mRootView.removeView(mResultView);
    }
    mContentView.setVisibility(View.VISIBLE);
  }

  public void refreshData(int action, Object data, OnRefreshListener l) {

  }

  public interface OnRefreshListener {
    void onRefreshComplete(Object data);
  }

  @Override
  public void onHttpStart(String action, int requestId) {

  }

  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {

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
}
