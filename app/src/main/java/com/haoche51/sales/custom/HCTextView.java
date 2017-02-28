package com.haoche51.sales.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.TextView;

public class HCTextView extends TextView {

  public HCTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HCTextView(Context context) {
    super(context);
    init();
  }

  /**
   * 显示出来的颜色
   */
  private String showBgColor = "#C2C9CF";

  /**
   * 透明色 用于隐藏
   */
  private String hideBgColor = "#00000000";

  /**
   * 绘制三角形的画笔
   */
  private Paint mPaint;
  /**
   * path构成一个三角形
   */
  private Path mPath;
  /**
   * 三角形的宽度
   */
  private int mTriangleW = 30;
  /**
   * 三角形的高度
   */
  private int mTriangleH = 30;

  /***
   * 在指定位置显示三角形 X 坐标
   */
  private float locationX;

  /***
   * 在指定位置显示三角形 X 坐标
   */
  private float locationY;

  private void init() {
    // 初始化画笔
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setColor(Color.parseColor(hideBgColor));
    mPaint.setStyle(Style.FILL);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    initTriangle();
  }

  /**
   * 初始化三角形
   */
  private void initTriangle() {
    int w = getWidth();
    int h = getHeight();
    // a(w / 2 - tw/2,h)
    // b(w / 2, h-th/2)
    // c(w /2 + tw / 2, h)
    mPath = new Path();
    mTriangleH = mTriangleW;
    mPath.moveTo((w / 2 - mTriangleW / 2), h);
    mPath.lineTo(w / 2, h - mTriangleH / 2);
    mPath.lineTo(w / 2 + mTriangleW / 2, h);
    mPath.close();
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.save();
    // TODO: 判断要不要移动画笔
    if (locationX > 0 && locationY > 0) {
      canvas.translate(locationX, locationY);
    }
    canvas.drawPath(mPath, mPaint);
    canvas.restore();
    super.dispatchDraw(canvas);
  }

  public void showTriangle() {
    mPaint.setColor(Color.parseColor(showBgColor));
    invalidate();

  }

  public void showAtLeft() {
    changeTriangle();
    showTriangle();
  }

  private void changeTriangle() {

    int height = getHeight();
    int width = (int) (getWidth() / 15.0);

    mPath = new Path();
    mTriangleH = mTriangleW;
    mPath.moveTo(width, height);
    mPath.lineTo(mTriangleW + width, height);
    mPath.lineTo(mTriangleW / 2 + width, height - mTriangleH / 2);
    mPath.close();
  }

  public boolean isShowTriangle() {
    return mPaint.getColor() == Color.parseColor(showBgColor);
  }

  public void hideTriangle() {
    mPaint.setColor(Color.parseColor(hideBgColor));
    invalidate();
  }
}
