package com.haoche51.sales.custom;

import android.text.TextPaint;
import android.text.style.UnderlineSpan;

/**
 * Created by mingzheng on 2015/12/22.
 */
public class NoUnderlineSpan extends UnderlineSpan {

  @Override
  public void updateDrawState(TextPaint textPaint) {
    textPaint.setColor(textPaint.linkColor);
    textPaint.setUnderlineText(false);
  }
}
