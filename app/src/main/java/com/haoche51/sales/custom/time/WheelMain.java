package com.haoche51.sales.custom.time;

import android.view.View;

import com.haoche51.sales.R;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.Arrays;
import java.util.List;

public class WheelMain {

	private View mWheelRootView;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_hours;
	private WheelView wv_mins;
	public int screenheight;
	private boolean hasSelectTime;
	private static int START_YEAR = 1990, END_YEAR = 2100;

	public View getView() {
		return mWheelRootView;
	}

	public void setView(View view) {
		this.mWheelRootView = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMain(View view) {
		super();
		this.mWheelRootView = view;
		hasSelectTime = false;
		setView(view);
	}

	public WheelMain(View view, boolean hasSelectTime) {
		super();
		this.mWheelRootView = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}

	public void initDateTimePicker(int year, int month) {
		this.initDateTimePicker(year, month, 1, 0, 0);
		//不显示日、时、分
		wv_day.setVisibility(View.GONE);
		wv_hours.setVisibility(View.GONE);
		wv_mins.setVisibility(View.GONE);
	}

	public void initDateTimePicker(int year, int month, int day) {
		this.initDateTimePicker(year, month, day, 0, 0);
	}

	public void initDateTimePicker(int year, int month, int day, int h, int m, boolean noDetail) {
		this.initDateTimePicker(year, month, day, h, m);
		if (noDetail) {
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
		}
	}

	public void initDateTimePicker(int year, int month, int day, int h, int m, boolean isNotSelectDay, boolean noDetail) {
		this.initDateTimePicker(year, month, day, h, m);
		if (noDetail) {
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
		}

		if (isNotSelectDay) {
			wv_day.setVisibility(View.GONE);
			wv_month.setVisibility(View.GONE);
			wv_year.setVisibility(View.GONE);
		}
	}

	/**
	 * @Description: TODO 寮瑰�烘�ユ����堕�撮����╁��
	 */
	public void initDateTimePicker(int year, int month, int day, int h, int m) {
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DATE);
		// 娣诲��澶у��������浠藉苟灏���惰浆���涓�list,��逛究涔���������ゆ��
		String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
		String[] months_little = {"4", "6", "9", "11"};

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 骞�
		wv_year = (WheelView) mWheelRootView.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 璁剧疆"骞�"�����剧ず��版��
		wv_year.setCyclic(true);// ���寰����婊����
		wv_year.setLabel("年");// 娣诲�����瀛�
		wv_year.setCurrentItem(year - START_YEAR);// ���濮������舵�剧ず�����版��

		// ���
		wv_month = (WheelView) mWheelRootView.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// ���
		wv_day = (WheelView) mWheelRootView.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// ��ゆ��澶у����������������板勾,��ㄦ�ョ‘瀹�"���"�����版��
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// ��板勾
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		wv_hours = (WheelView) mWheelRootView.findViewById(R.id.hour);
		wv_mins = (WheelView) mWheelRootView.findViewById(R.id.min);
		if (hasSelectTime) {
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);

			wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);// ���寰����婊����
			wv_hours.setLabel("时");// 娣诲�����瀛�
			wv_hours.setCurrentItem(h);

			wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);// ���寰����婊����
			wv_mins.setLabel("分");// 娣诲�����瀛�
			wv_mins.setCurrentItem(m);
		} else {
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
		}

		// 娣诲��"骞�"������
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// ��ゆ��澶у����������������板勾,��ㄦ�ョ‘瀹�"���"�����版��
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 娣诲��"���"������
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// ��ゆ��澶у����������������板勾,��ㄦ�ョ‘瀹�"���"�����版��
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0) || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// ��规��灞�骞�瀵�搴���ユ��瀹������╁�ㄥ��浣����澶у��(涓����灞�骞������戒�����)
		int textSize = 0;
		if (hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

	}


	/**
	 * @Description: TODO 寮瑰�烘�ユ����堕�撮����╁��
	 */
	public void initHourPicker(int h) {

		//设置时
		wv_hours = (WheelView) mWheelRootView.findViewById(R.id.hour);
		wv_hours.setVisibility(View.VISIBLE);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);// ���寰����婊����
		wv_hours.setLabel("时");// 娣诲�����瀛�
		wv_hours.setCurrentItem(h);


		// ��规��灞�骞�瀵�搴���ユ��瀹������╁�ㄥ��浣����澶у��(涓����灞�骞������戒�����)
		int textSize = 0;
		if (hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;
		wv_hours.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		if (!hasSelectTime)
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1));
		else
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1)).append(" ").append(wv_hours.getCurrentItem()).append(":").append(wv_mins.getCurrentItem());
		return sb.toString();
	}

	/**
	 * 获取小时
	 * @return
	 */
	public String getHour() {
		return String.valueOf(wv_hours.getCurrentItem());
	}

	/**
	 * 获取年和月
	 *
	 * @return
	 */
	public String getYearAndMonth() {
		StringBuffer sb = new StringBuffer();
		int time = 0;
		if (!hasSelectTime) {
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1));
			time = UnixTimeUtil.getModifyUnixTime(sb.toString());
		} else {
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1)).append(" ").append(wv_hours.getCurrentItem()).append(":").append(wv_mins.getCurrentItem());
			time = UnixTimeUtil.getUnixTime(sb.toString());
		}
		return UnixTimeUtil.format(time, UnixTimeUtil.YEAR_MONTH_PATTERN);
	}

	public long getLongTime() {
		StringBuffer sb = new StringBuffer();
		long time = 0;
		if (!hasSelectTime) {
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1));
			time = UnixTimeUtil.getModifyUnixTime(sb.toString());
		} else {
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1)).append("-").append((wv_day.getCurrentItem() + 1)).append(" ").append(wv_hours.getCurrentItem()).append(":").append(wv_mins.getCurrentItem());
			time = UnixTimeUtil.getUnixTime(sb.toString());
		}
		return time;
	}

	public String getMyTime() {
		int monthItem = wv_month.getCurrentItem() + 1;
		String month = monthItem < 10 ? "0" + monthItem : monthItem + "";
		int dayItem = wv_day.getCurrentItem() + 1;
		String day = dayItem < 10 ? "0" + dayItem : dayItem + "";
		StringBuffer sb = new StringBuffer();
		if (!hasSelectTime)
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append(month).append("-").append(day);
		else
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append(month).append("-").append(day).append(" ").append(wv_hours.getCurrentItem()).append(":").append(wv_mins.getCurrentItem());
		return sb.toString();
	}

	public String getChineseTime() {
		StringBuffer sb = new StringBuffer();
		if (!hasSelectTime)
			sb.append((wv_month.getCurrentItem() + 1)).append("月").append((wv_day.getCurrentItem() + 1)).append("日");
		else
			sb.append((wv_month.getCurrentItem() + 1)).append("月").append((wv_day.getCurrentItem() + 1)).append("日").append(wv_hours.getCurrentItem()).append("时").append(wv_mins.getCurrentItem()).append("分");
		return sb.toString();
	}
}
