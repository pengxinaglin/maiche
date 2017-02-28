package com.haoche51.sales.hcvehiclerecommend;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;


/**
 * Created by Administrator on 2015/10/27.
 */
public class WeiXinShareDialog extends Dialog {

  private Context context;
  private int layoutRes;//布局文件
  private boolean isWeiXinCopy;
  private TextView textViewTitle;
  private TextView textViewCommit;
  private View.OnClickListener onClickListener;

  public WeiXinShareDialog(Context context, int resLayout, boolean isWeixinCopy, View.OnClickListener onClickListener) {
    super(context, R.style.shareDialog);
    this.context = context;
    this.layoutRes = resLayout;
    this.isWeiXinCopy = isWeixinCopy;
    this.onClickListener = onClickListener;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(layoutRes);
    textViewTitle = (TextView) findViewById(R.id.text_view_share_dialog_title);
    textViewCommit = (TextView) findViewById(R.id.text_view_share_dialog_commit);

    if (isWeiXinCopy) {
      textViewTitle.setText(context.getResources().getString(R.string.share_dialog_text_weixin));
    } else {
      textViewTitle.setText(context.getResources().getString(R.string.share_dialog_text_phone));
    }

    this.setCancelable(false);// 设置点击屏幕Dialog不消失

    textViewCommit.setOnClickListener(onClickListener);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      dismiss();
      return true;
    } else {
      return super.onKeyDown(keyCode, event);
    }
  }

}
