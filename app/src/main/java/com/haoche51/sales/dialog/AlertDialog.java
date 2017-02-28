package com.haoche51.sales.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.haoche51.hctimepickerlibrary.CalendarCellDecorator;
import com.haoche51.hctimepickerlibrary.CalendarPickerView;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.util.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @File:AlertDialogUtil.java
 * @Package:com.haoche51.checker.util
 * @desc:对话框Util
 * @author:zhuzuofei
 * @date:2015-4-30 下午4:09:25
 */
public class AlertDialog {

	//确定Listener
	public interface OnClickYesListener {
		void onClickYes();
	}

	/**
	 * 创建一个标题、消息、确定、取消的对话框
	 */
	public static void showStandardTitleMessageDialog(final Activity context, String title, String msg, String cancelStr, String determineStr, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_standard, null);
		((TextView) root.findViewById(R.id.title)).setText(title);
		if (!TextUtils.isEmpty(msg))
			((TextView) root.findViewById(R.id.msg)).setText(msg);
		/** 取消 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(cancelStr);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(determineStr);
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//回调关闭
				if (onDismissListener != null) {
					onDismissListener.onDismiss(null);
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 创建标准对话框
	 *
	 * @param context
	 * @param message
	 * @param yesListener
	 */
	public static void createDialog(Activity context, String message, final OnClickYesListener yesListener) {
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setNegativeButton(GlobalData.resourceHelper.getString(R.string.cancel), null);
		builder.setPositiveButton(GlobalData.resourceHelper.getString(R.string.action_ok), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (yesListener != null) {
					yesListener.onClickYes();
				}
			}
		});
		builder.show();
	}

	/**
	 * 弹出一个输入VIN码的对话框
	 */
	public static void createInputVinCodeDialog(Activity context, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_input_vincode, null);
		final EditText et_content = (EditText) root.findViewById(R.id.et_content);

		/** 取消 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(GlobalData.resourceHelper.getString(R.string.cancel));
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onDismissListener.onDismiss(null);
				dialog.dismiss();
			}
		});

		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(GlobalData.resourceHelper.getString(R.string.action_ok));
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//VIN码必须是17位
				if (TextUtils.isEmpty(et_content.getText()) || et_content.getText().toString().trim().length() != 17) {
					et_content.setText(null);
					et_content.setHint("VIN码不能为空且必须是17位");
					return;
				}

				if (onDismissListener != null) {
					Bundle data = new Bundle();
					data.putString("vinCode", et_content.getText().toString());
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 弹出一个修改VIN码的对话框
	 */
	public static void createModifyVinCodeDialog(final Activity context, final String vin, final boolean flag, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_modify_vincode, null);
		((TextView) root.findViewById(R.id.et_content)).setText("您输入的VIN码为：\n" + vin + "，\n输入后不可修改，请确定");
		/** 修改 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(GlobalData.resourceHelper.getString(R.string.modify));
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!flag)
					createInputVinCodeDialog(context, onDismissListener);
				else {
					Bundle data = new Bundle();
					data.putBoolean("determine", false);
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});

		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(GlobalData.resourceHelper.getString(R.string.action_ok));
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onDismissListener != null) {
					Bundle data = new Bundle();
					data.putString("vinCode", vin);
					data.putBoolean("determine", true);
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 弹出一个修改VIN码的对话框
	 */
	public static void createNormalDialog(final Activity context, final String content, final String cancelContent, final String okContent, final boolean flag, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_modify_vincode, null);
		((TextView) root.findViewById(R.id.et_content)).setText(content);
		/** 修改 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(cancelContent);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!flag)
					createInputVinCodeDialog(context, onDismissListener);
				else {
					Bundle data = new Bundle();
					data.putBoolean("determine", false);
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});

		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(okContent);
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onDismissListener != null) {
					Bundle data = new Bundle();
					data.putBoolean("determine", true);
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 请求VIN码鉴定报告成功对话框
	 */
	public static void createRequestVinSuccessAutoDismissDialog(final Activity context) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_vin_success, null);
		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.65, 0);
		setDialogBackground(dialog, R.drawable.shape_toast);
		dialog.show();

		root.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null != context && !context.isFinishing())
					dialog.dismiss();
			}
		}, 2000);
	}

	/**
	 * 未检索到VIN报告对话框
	 */
	public static void createNotFoundReportDialog(final Activity context, String vin, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_notfound_report, null);
		((TextView) root.findViewById(R.id.et_content)).setText("您输入的VIN码为：\n" + (TextUtils.isEmpty(vin) ? "" : vin) + "，\n未检索到车鉴定报告，请确认是否\n输入正确，若正确则该车没有报\n告，若错误则点击修改");
		/** 修改 */
		Button cancel = (Button) root.findViewById(R.id.cancel);
		cancel.setText(GlobalData.resourceHelper.getString(R.string.modify));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createInputVinCodeDialog(context, onDismissListener);
				dialog.dismiss();
			}
		});

		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(GlobalData.resourceHelper.getString(R.string.action_ok));
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 弹出一个添加”我的备注“的对话框
	 */
	public static void createCheckerCommentDialog(final Activity context, boolean flag, final String comment, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_input_remark, null);
		TextView title = (TextView) root.findViewById(R.id.title);
		title.setText(flag ? GlobalData.resourceHelper.getString(R.string.add_remark) : GlobalData.resourceHelper.getString(R.string.modify_remark));
		final EditText et_content = (EditText) root.findViewById(R.id.et_content);
		if (!TextUtils.isEmpty(comment)) {
			et_content.setText(comment);
			et_content.setSelection(comment.length());
		}
		/** 取消 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(GlobalData.resourceHelper.getString(R.string.cancel));
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(GlobalData.resourceHelper.getString(R.string.action_ok));
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(et_content.getText().toString()) || et_content.getText().toString().length() > 50) {
					ToastUtil.showInfo(GlobalData.resourceHelper.getString(R.string.tip_input_remark));
					return;
				}

				//回传数据
				if (onDismissListener != null) {
					Bundle data = new Bundle();
					//防止故意输一大堆换行
					data.putString("comment", et_content.getText().toString().replace("\n", ""));
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	/**
	 * 弹出选择时间区段的对话框
	 */
	public static void showSelectDateDialog(final Activity context, long startTime, long endTime, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_select_date, null);
		//日历控件
		final CalendarPickerView calendar = (CalendarPickerView) root.findViewById(R.id.calendar_view);
		List<Date> dates = new ArrayList<Date>();
		if (startTime > 0)
			dates.add(new Date(startTime));//设置范围开始时间
		if (endTime > 0)
			dates.add(new Date(endTime));//设置范围结束时间
		calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
		//设置日历最多可以看到之前两年
		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -2);
		//设置日历最多可以看到之后两年
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 2);
		//初始化控件
		calendar.init(lastYear.getTime(), nextYear.getTime())
				.inMode(CalendarPickerView.SelectionMode.RANGE)//设置控件的模式（选择范围）
				.withSelectedDates(dates);//设置已经选择的时间范围
		/** 取消 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onDismissListener != null)
					onDismissListener.onDismiss(null);
				dialog.dismiss();
			}
		});
		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//没有选时间 或 只选了一个时间
				if (calendar.getSelectedDates() == null || calendar.getSelectedDates().size() < 2) {
					ToastUtil.showInfo(GlobalData.resourceHelper.getString(R.string.hc_trans_task_filter_time_tip));
					return;
				}
				//传递数据
				if (onDismissListener != null) {
					Bundle data = new Bundle();
					data.putSerializable("selectedDates", (Serializable) calendar.getSelectedDates());
					onDismissListener.onDismiss(data);
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.9, (float) 0.85);
		dialog.show();
	}

	/**
	 * 弹出是否确定请求财务支付尾款对话框
	 */
	public static void createpayBackBalanceDialog(final Activity context, final OnDismissListener onDismissListener) {
		if (context == null || context.isFinishing()) return;
		final Dialog dialog = new Dialog(context, R.style.shareDialog);
		View root = View.inflate(context, R.layout.dialog_pay_back_balance, null);
		/** 关闭 */
		root.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		/** 取消 */
		Button cancle = (Button) root.findViewById(R.id.cancel);
		cancle.setText(GlobalData.resourceHelper.getString(R.string.cancel));
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		/** 确定 */
		Button determine = (Button) root.findViewById(R.id.determine);
		determine.setText(GlobalData.resourceHelper.getString(R.string.action_ok));
		determine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//回传数据
				if (onDismissListener != null) {
					onDismissListener.onDismiss(null);
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(root);
		setDialogSize(context, dialog, (float) 0.85, 0);
		dialog.show();
	}

	public static void setDialogSize(Activity context, Dialog dialog, float scaleW, float scaleH) {
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		if (scaleW > 0)
			lp.width = (int) (context.getWindowManager().getDefaultDisplay().getWidth() * scaleW);
		else
			lp.width = WindowManager.LayoutParams.WRAP_CONTENT;

		if (scaleH > 0)
			lp.height = (int) (context.getWindowManager().getDefaultDisplay().getHeight() * scaleH);
		else
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	}

	private static void setDialogBackground(Dialog dialog, int res) {
		Window window = dialog.getWindow();
		window.setBackgroundDrawableResource(res);
	}

	//确定Listener
	public interface OnDismissListener {
		void onDismiss(Bundle data);
	}
}
