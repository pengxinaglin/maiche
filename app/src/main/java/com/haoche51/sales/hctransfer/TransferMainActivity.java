package com.haoche51.sales.hctransfer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.FragmentItem;
import com.haoche51.sales.custom.HCSmartTabLayout;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 过户任务主页
 * Created by mac on 15/12/1.
 */
public class TransferMainActivity extends CommonStateFragmentActivity implements TextView.OnEditorActionListener {

  @ViewInject(R.id.content_view)
  private HCSmartTabLayout hcSmartTabLayout;
  List<FragmentItem> mFragmentItemList;

  @ViewInject(R.id.rl_titlebar)
  private RelativeLayout rl_titlebar;

  @ViewInject(R.id.et_trans_search)
  private EditText et_trans_search;//搜索内容

  @ViewInject(R.id.view_mask)
  private View view_mask;//遮罩

  @Override
  protected int getTitleView() {
    return R.layout.layout_common_titlebar_search_more;
  }

  @Override
  protected int getContentView() {
    return R.layout.activity_transfer_main;
  }

  @Override
  protected void initView() {
    super.initView();
    setScreenTitle(R.string.hc_transfer_main_title);
    //监听键盘点击搜索
    et_trans_search.setOnEditorActionListener(this);
    et_trans_search.setHint(R.string.hc_transfer_search_hint);
    et_trans_search.setHintTextColor(getResources().getColor(R.color.hc_self_white));
  }

  @Override
  protected void initData() {
    mFragmentItemList = new ArrayList<>();
    mFragmentItemList.add(new FragmentItem(getString(R.string.hc_transfer_main_transfer), new TransferTaskFragment()));
    mFragmentItemList.add(new FragmentItem(getString(R.string.hc_transfer_main_audit), new TransferAuditFragment()));
    mFragmentItemList.add(new FragmentItem(getString(R.string.hc_transfer_main_finish), new TransferFinishFragment()));
    //设置界面
    hcSmartTabLayout.setContentFragments(this, mFragmentItemList);
  }

  /**
   * 显示搜索框
   */
  @OnClick(R.id.tv_right_fuction)
  private void showSearchBox(final View v) {
    int width = getWindowManager().getDefaultDisplay().getWidth();
    doAnimator(width, 0, true);
  }

  /**
   * 隐藏搜索框
   */
  @OnClick(R.id.view_mask)
  private void clickMask(View v) {
    hideSearchBox(v);
  }

  /**
   * 隐藏搜索框
   */
  @OnClick(R.id.tv_search_cancel)
  private void hideSearchBox(View v) {
    //清空搜索
    if (et_trans_search != null)
      et_trans_search.setText("");
    if (mFragmentItemList != null)
      for (FragmentItem item : mFragmentItemList) {
        item.getFragment().doingFilter(new Bundle());
      }
    //隐藏搜索框
    int width = getWindowManager().getDefaultDisplay().getWidth();
    doAnimator(0, width, false);
  }

  @OnClick(R.id.iv_search)
  private void onSearchClick(View v) {
    if (!TextUtils.isEmpty(et_trans_search.getText().toString().trim())) {
      commit(et_trans_search.getText().toString());
    }
  }

  private void doAnimator(int targetWidth, int startWdith, final boolean enable) {
    final ViewGroup.LayoutParams layoutParams = rl_titlebar.getLayoutParams();
    ValueAnimator va = ValueAnimator.ofInt(targetWidth, startWdith);
    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator va) {
        layoutParams.width = (Integer) va.getAnimatedValue();
        rl_titlebar.setLayoutParams(layoutParams);
      }
    });
    va.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animator) {

      }

      @Override
      public void onAnimationEnd(Animator animator) {
        //激活输入框
        if (enable) {
          //弹出键盘
          et_trans_search.setFocusable(true);
          et_trans_search.setFocusableInTouchMode(true);
          et_trans_search.requestFocus();
          //让软键盘延时弹出，以更好的加载Activity
          et_trans_search.postDelayed(new Runnable() {
            @Override
            public void run() {
              InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
              inputManager.showSoftInput(et_trans_search, 0);
            }
          }, 100);
          //弹出半透明效果
          view_mask.setVisibility(View.VISIBLE);
        } else {
          //关闭遮罩
          view_mask.setVisibility(View.GONE);
          //关闭键盘
          et_trans_search.clearFocus();
          InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
          inputManager.hideSoftInputFromWindow(et_trans_search.getWindowToken(), 0);
        }
      }

      @Override
      public void onAnimationCancel(Animator animator) {

      }

      @Override
      public void onAnimationRepeat(Animator animator) {

      }
    });
    va.setDuration(400);
    va.start();
  }

  @Override
  public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
    //监听点击输入法的“搜索”
    if (actionId == EditorInfo.IME_ACTION_SEARCH || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
      //当前页面进行搜索
      commit(et_trans_search.getText().toString());
      return true;
    }
    return false;
  }

  private void commit(String search_field) {
    //当前页面进行搜索
    if (mFragmentItemList != null) {
      FragmentItem item = mFragmentItemList.get(hcSmartTabLayout.getCurrentPagerPosition());
      Bundle where = new Bundle();
      where.putString("search_field", search_field);
      item.getFragment().doingFilter(where);
    }
    //关闭遮罩
    view_mask.setVisibility(View.GONE);
    //关闭键盘
    et_trans_search.clearFocus();
    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(et_trans_search.getWindowToken(), 0);
  }
}