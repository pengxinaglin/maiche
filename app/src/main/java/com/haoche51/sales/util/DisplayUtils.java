package com.haoche51.sales.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.User;
import com.haoche51.sales.custom.CircleTransform;
import com.haoche51.sales.custom.NoUnderlineSpan;
import com.haoche51.sales.custom.time.ScreenInfo;
import com.haoche51.sales.custom.time.WheelMain;
import com.haoche51.sales.dao.VehicleBrandEntity;
import com.nineoldandroids.animation.ValueAnimator;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DisplayUtils {
	private static final String TAG = "DisplayUtils";


	private static PinyinComparator pinyinComparator;
	private static NoUnderlineSpan noUnderlineSpan;

	static {
		pinyinComparator = new PinyinComparator();
	}


	/**
	 * dipתpx
	 *
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * pxתdip
	 *
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取当前屏幕宽度
	 */
	public static int getScreenWidthInPixels() {
		return GlobalData.mContext.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取当前屏幕高度
	 */
	public static int getScreenHeightPixels() {
		return GlobalData.mContext.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取屏幕metrics
	 *
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		Log.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen + " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);

	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}


	/**
	 * 获取屏幕DPI
	 *
	 * @param context
	 * @return
	 */
	public static int getDisplayDensity(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metric = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metric);
		return metric.densityDpi;
	}


	public static int getDimenPixels(int resDimen) {
		return GlobalData.mContext.getResources().getDimensionPixelOffset(resDimen);
	}

	/**
	 * 获取评估长宽比
	 *
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context) {
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H / W);
	}

	/**
	 * 获取状态栏的高度
	 *
	 * @return
	 */
	public static int getStatusBarHeight() {
		int result = 0;
		int resourceId = GlobalData.mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = GlobalData.mContext.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 首页和设置页欢迎语设置，返回上午或者下午或者晚上
	 *
	 * @param textView
	 */
	public static void setWelcome(TextView textView) {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour >= 0 && hour <= 12) {
			textView.setText("上午好");
		} else if (hour > 12 && hour <= 19) {
			textView.setText("下午好");
		} else if (hour > 19 && hour <= 23) {
			textView.setText("晚上好");
		}
	}

	public static void setWelcomeUser(TextView textView) {
		User user = GlobalData.userDataHelper.getUser();
		textView.setText(user.getName());
	}

	public static void setUserFace(ImageView userFace, String url) {
		Picasso.with(GlobalData.mContext).load(url).placeholder(R.drawable.ic_login_face_defult)
				.error(R.drawable.ic_login_face_defult).resize(dip2px(GlobalData.mContext, 150)
				, dip2px(GlobalData.mContext, 150)).transform(new CircleTransform()).into(userFace);
	}

	@SuppressLint("DefaultLocale")
	public static List<VehicleBrandEntity> sortBrand(List<VehicleBrandEntity> brands) {
		Collections.sort(brands, pinyinComparator);
		return brands;
	}

	public static class PinyinComparator implements Comparator<VehicleBrandEntity> {

		public int compare(VehicleBrandEntity o1, VehicleBrandEntity o2) {
			if (o1.getFirst_char().equals("@")
					|| o2.getFirst_char().equals("#")) {
				return -1;
			} else if (o1.getFirst_char().equals("#")
					|| o2.getFirst_char().equals("@")) {
				return 1;
			} else {
				return o1.getFirst_char().compareTo(o2.getFirst_char());
			}
		}

	}

	public static void animateLayout(final View view, int from, int to) {
		if (view == null) return;
		final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		ValueAnimator animator = ValueAnimator.ofInt(from, to);
		animator.setDuration(300);
		animator.setInterpolator(new LinearInterpolator());
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int now = (int) animation.getAnimatedValue();
				lp.topMargin = now;
				view.requestLayout();
			}
		});
		animator.start();
	}

	/***
	 * view展开效果
	 */
	public static void expand(final View v) {
		v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration(400);
		v.startAnimation(a);
	}

	/***
	 * 收缩效果
	 */
	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();

		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		// 1dp/ms
		a.setDuration(400);
		v.startAnimation(a);
	}

	public static String parseMoney(String pattern, double bd) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(bd);
	}


	/**
	 * 限制输入两个小数,四位整数,,,用于成交价
	 */
	public static TextWatcher textWatcherPrice = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if ("".equals(editable.toString())) {
				return;
			}

			String temp = editable.toString();
			int posDot = temp.indexOf(".");
			if (posDot < 0) {//整数
				if (temp.length() > 3) {
					editable.delete(3, 4);
				}
			} else if (posDot >= 0 && posDot < 4) {
				if (temp.length() - posDot > 4) {
					editable.delete(posDot + 4, posDot + 5);
				}
			} else if (posDot > 3) {
				editable.delete(posDot - 1, posDot);
			}
		}
	};


	/**
	 * 限制输入一个小数,一位整数,,,用于排量
	 */
	public static TextWatcher textWatcherDisplacement = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if ("".equals(editable.toString())) {
				return;
			}

			String temp = editable.toString();
			int posDot = temp.indexOf(".");
			if (posDot < 0) {//整数
				if (temp.length() > 1) {
					editable.delete(1, 2);
				}
			} else if (posDot > 0 && posDot < 2) {
				if (temp.length() - posDot > 2) {
					editable.delete(posDot + 2, posDot + 3);
				}
			} else if (posDot > 1) {
				editable.delete(posDot - 1, posDot);
			}
		}
	};

	/**
	 * 根据客户级别枚举值，获取客户级别
	 *
	 * @param level
	 * @return
	 */
	public static String getCustomerLevel(int level) {
		String levelStr = "";
		switch (level) {
			case 1:
				levelStr = "N";
				break;
			case 2:
				levelStr = "N";
				break;
			case 3:
				levelStr = "B";
				break;
			case 4:
				levelStr = "A";
				break;
			case 5:
				levelStr = "H";
				break;
		}
		return levelStr;
	}

	/**
	 * 意向金状态
	 * 0:未使用 10：已使用 20：申请退款中 30：已退款
	 *
	 * @param status
	 * @return
	 */
	public static String getIntentionStatus(int status) {
		String levelStr = "";
		switch (status) {
			case 0:
				levelStr = "申请退款";
				break;
			case 10:
				levelStr = "已使用";
				break;
			case 20:
				levelStr = "申请退款中";
				break;
			case 30:
				levelStr = "已退款";
				break;

		}
		return levelStr;
	}

	/**
	 * 根据颜色枚举值获取颜色
	 *
	 * @param color
	 * @return
	 */
	public static String getVehicleColor(int color) {
		String levelStr = "";
		switch (color) {
			case -1:
				levelStr = GlobalData.mContext.getString(R.string.nomarl_select_unlimited);
				break;
			case 1:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_black);
				break;
			case 2:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_white);
				break;
			case 3:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_silver);
				break;
			case 4:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_gray);
				break;
			case 5:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_red);
				break;
			case 6:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_blue);
				break;
			case 7:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_yellow);
				break;
			case 8:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_orange);
				break;
			case 9:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_golden);
				break;
			case 10:
				levelStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_color_green);
				break;

		}
		return levelStr;
	}


	/**
	 * 拼装颜色字串
	 *
	 * @param colorMap
	 * @return
	 */
	public static String getVehicleColorString(Map<Integer, String> colorMap) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String v : colorMap.values()) {
			if (TextUtils.isEmpty(stringBuffer.toString())) {
				stringBuffer.append(v);
			} else {
				stringBuffer.append("·").append(v);
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 拼装颜色字串
	 *
	 * @param colorList
	 * @return
	 */
	public static String getVehicleColorString(List<Integer> colorList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (Integer v : colorList) {
			if (TextUtils.isEmpty(stringBuffer.toString())) {
				stringBuffer.append(getVehicleColor(v));
			} else {
				stringBuffer.append("·").append(getVehicleColor(v));
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 拼装车身结构字串
	 *
	 * @param bodyMap
	 * @return
	 */
	public static String getVehicleStructureString(Map<Integer, String> bodyMap) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String v : bodyMap.values()) {
			if (TextUtils.isEmpty(stringBuffer.toString())) {
				stringBuffer.append(v);
			} else {
				stringBuffer.append("·").append(v);
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 拼装车身结构字串
	 *
	 * @param bodyList
	 * @return
	 */
	public static String getVehicleStructureString(List<Integer> bodyList) {
		StringBuffer stringBuffer = new StringBuffer();
		for (Integer v : bodyList) {
			if (TextUtils.isEmpty(stringBuffer.toString())) {
				stringBuffer.append(getVehicleStructure(v));
			} else {
				stringBuffer.append("·").append(getVehicleStructure(v));
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 根据车身结构枚举值获得车身结构字串
	 *
	 * @param carbody
	 * @return
	 */
	public static String getVehicleStructure(int carbody) {
		String carStr = "";
		switch (carbody) {
			case -1:
				carStr = GlobalData.mContext.getString(R.string.nomarl_select_unlimited);
				break;
			case 1:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_two_front);
				break;
			case 2:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_three_front);
				break;
			case 3:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_suv);
				break;
			case 4:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_mpv);
				break;
			case 5:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_wagon);
				break;
			case 6:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_sports);
				break;
			case 7:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_pickup);
				break;
			case 8:
				carStr = GlobalData.mContext.getString(R.string.hc_vehicle_sub_minibus);
				break;

		}
		return carStr;
	}

	//取消autolink下划线
	public static void getNoUnderlineSpan(TextView textView, String phone) {
		if (noUnderlineSpan == null) {
			noUnderlineSpan = new NoUnderlineSpan();
		}
		textView.setText(phone);
		if (textView.getText() instanceof Spannable) {
			Spannable spannable = (Spannable) textView.getText();
			try {
				spannable.setSpan(noUnderlineSpan, 0, spannable.length(), Spanned.SPAN_MARK_MARK);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public static String getImageUrl(String url, int width, int hight) {
		String result;
		if (url.endsWith("?")) {
			result = url + "imageView2/2/w/" + width + "/h/" + hight;
		} else {
			result = url + "?imageView2/2/w/" + width + "/h/" + hight;
		}
		return result;
	}

	public static String convertImageURL(String url, int w, int h) {
		url = TextUtils.isEmpty(url) ? "" : url;
		String str = new StringBuilder(url).append("?imageView2")
				.append("/1/w/")
				.append(w)
				.append("/h/")
				.append(h)
				.toString();
		return str;
	}

	public static String averageImageURL(String url, int w, int h) {
		url = TextUtils.isEmpty(url) ? "" : url;
		String str = new StringBuilder(url).append("?imageView2")
				.append("/0/w/")
				.append(w)
				.append("/h/")
				.append(h)
				.toString();
		return str;
	}


	/**
	 * 设置recyclerView的高度
	 */
	public static void reSizeRecyclerView(Context context, RecyclerView recyclerView, List<String> list, boolean isView) {
		int lineHight = dip2px(context, 85);
		int listSize = 0;
		if (list != null) {
			listSize = list.size();
		}
		if (!isView) {
			listSize++;
		}
		if (listSize > 0) {
			int viewHeight;
			if (listSize % 4 == 0) {
				viewHeight = lineHight * (listSize / 4);
			} else {
				viewHeight = lineHight * (listSize / 4 + 1);
			}
			recyclerView.getLayoutParams().height = viewHeight;
		} else if (list == null || listSize == 0) {
			recyclerView.getLayoutParams().height = 0;
		}
	}

	/**
	 * 选择年月日控件
	 *
	 * @param context     控件所在的Activity
	 * @param textView    显示时间的TextView
	 * @param titleResId  标题资源id
	 * @param isOnlyAfter 是否只选当前时间之后的时间
	 */
	public static void displayTimeWhellYMD(final Activity context, final TextView textView, int titleResId, final boolean detail, final boolean isOnlyAfter) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
		builder.setTitle(context.getString(titleResId));
		final View timerView = LayoutInflater.from(context).inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(context);
		final WheelMain wheelMain = new WheelMain(timerView, true);
		wheelMain.screenheight = screenInfo.getHeight();
		final Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		if (detail)
			wheelMain.initDateTimePicker(year, month, day, hour, min, false);
		else
			wheelMain.initDateTimePicker(year, month, day, 0, 0, true);
		builder.setView(timerView);
		builder.setNegativeButton(context.getString(R.string.soft_update_cancel), null);
		builder.setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int transferTime = UnixTimeUtil.getUnixTime(wheelMain.getTime());
				if (transferTime < calendar.getTime().getTime() / 1000L && isOnlyAfter) {
					ToastUtil.showInfo("不能选择之前的时间");
					return;
				}

				if (detail)
					textView.setText(UnixTimeUtil.format(transferTime));
				else
					textView.setText(UnixTimeUtil.formatYearMonthDay(transferTime));
			}
		});
		builder.show();
	}

	/**
	 * 返回byte对应的数据文本
	 *
	 * @param size
	 * @return
	 * @author:wfx
	 */
	public static String getDataSize(long size) {
		long normalSize = 1024;
		if (size >= 0 && size < normalSize) {
			return size + "B";
		}

		DecimalFormat formater = new DecimalFormat("####0.00");
		normalSize *= 1024;
		if (size < normalSize) {
			float kbSize = size / 1024f;
			return formater.format(kbSize) + "KB";
		}
		normalSize *= 1024;
		if (size < normalSize) {
			float mbSize = size / 1024f / 1024f;
			return formater.format(mbSize) + "MB";
		}
		normalSize *= 1024;
		if (size < normalSize) {
			float mbSize = size / 1024f / 1024f / 1024f;
			return formater.format(mbSize) + "GB";
		}
		return "size: error";
	}

}
