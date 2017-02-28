package com.haoche51.sales.custom.time;

import android.util.Log;
import android.view.View;

import com.haoche51.sales.R;

import java.util.Arrays;
import java.util.List;


public class WheelTime {

	private View view;
	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private WheelView wv_hours;
	private WheelView wv_mins;
	private int year;
	private int month;
	private int day;
	public int screenheight;
	private boolean hasSelectTime;
	private static int START_YEAR = 1990, END_YEAR = 2100;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
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

	public WheelTime(View view) {
		super();
		this.view = view;
		hasSelectTime = false;
		setView(view);
	}
	public WheelTime(View view,boolean hasSelectTime) {
		super();
		this.view = view;
		this.hasSelectTime = hasSelectTime;
		setView(view);
	}
	public void initDateTimePicker(int year ,int month,int day){
		this.initDateTimePicker(year, month, day, 0, 0);
	}
	/**
	 * 
	 */
	public void initDateTimePicker(int year ,int month ,int day,int h,int m) {
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(year,year));
		wv_year.setCyclic(false);
		wv_year.setLabel("年");
		wv_year.setCurrentItem(year-year);
		this.year = year;

		//
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(month+1,month+1));
		wv_month.setCyclic(false);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);
		this.month = month;
		wv_day = (WheelView) view.findViewById(R.id.day);
		
		wv_day.setCyclic(false);
		wv_day.setAdapter(new NumericWheelAdapter(day,day));
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day);
		this.day = day;

		wv_hours = (WheelView)view.findViewById(R.id.hour);
		wv_mins = (WheelView)view.findViewById(R.id.min);
		if(hasSelectTime){
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);
			
			wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
			wv_hours.setCyclic(true);
			wv_hours.setLabel("时");
			wv_hours.setCurrentItem(h);
			
			
			wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
			wv_mins.setCyclic(true);
			wv_mins.setLabel("分");
			wv_mins.setCurrentItem(m);
		}else{
			wv_hours.setVisibility(View.GONE);
			wv_mins.setVisibility(View.GONE);
		}
		
		// 娣诲��"骞�"������
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// ��ゆ��澶у����������������板勾,��ㄦ�ョ‘瀹�"���"�����版��
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
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
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		int textSize = 0;
		if(hasSelectTime)
			textSize = (screenheight / 100) * 3;
		else
			textSize = (screenheight / 100) * 4;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		if(!hasSelectTime)
			sb.append(String.valueOf(year)).append("-")
				.append(month+1).append("-")
				.append(day);
		else
			sb.append(String.valueOf(year)).append("-")
			.append(month+1).append("-")
			.append(day).append(" ")
			.append(wv_hours.getCurrentItem()).append(":")
			.append(wv_mins.getCurrentItem());
		Log.e("com.haoche51.checker","final changed time"+sb.toString());
		return sb.toString();
	}
}
