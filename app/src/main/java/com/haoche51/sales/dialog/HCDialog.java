package com.haoche51.sales.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hctransaction.CouponEntity;
import com.haoche51.sales.hctransaction.UserCouponsListAdapter;
import com.haoche51.sales.util.ToastUtil;

import java.util.List;

public class HCDialog {

	private static AlertDialog alertDialogCouponList;
	private static AlertDialog alertDialogCouponExchange;

	private static ProgressDialog progressDialog;

	public static void showProgressDialog(Activity activity) {
		showProgressDialog(activity, null);
	}

	public static void showProgressDialog(Activity activity, AlertDialog.OnDismissListener dismissListener) {
		if (activity == null || activity.isFinishing()) {
			return;
		}

		progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("处理中...");
		if (dismissListener != null) {
			progressDialog.setOnDismissListener(dismissListener);
		}
		progressDialog.show();
	}

	public static void dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 显示dialog
	 *
	 * @param context  环境
	 * @param strTitle 标题
	 * @param strText  内容
	 * @param icon     图标
	 */
	public static void showDialog(Activity context, String strTitle,
	                              String strText, int icon, DialogInterface.OnClickListener clickListener) {
		try {
			AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
			tDialog.setIcon(icon);
			tDialog.setTitle(strTitle);
			tDialog.setMessage(strText);
			tDialog.setPositiveButton("确定", clickListener);
			tDialog.show();
		} catch (Exception e) {

		}
	}


	/**
	 * 显示dialog
	 */
	public static void showDialog(Context context, final View.OnClickListener clickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		View view = flater.inflate(R.layout.revisit_add_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		TextView textView = (TextView) view.findViewById(R.id.confirm);
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				clickListener.onClick(v);
			}
		});

		dialog.show();
	}


	/**
	 * 选择优惠券对话框
	 *
	 * @param context
	 * @param data
	 * @param inputCouponClickListener
	 * @param couponClickListener
	 */
	public static void UserCunponsDialog(Context context, List<CouponEntity> data
			, View.OnClickListener inputCouponClickListener, final OnCouponClickListener couponClickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		View view = flater.inflate(R.layout.layout_user_coupons_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		alertDialogCouponList = builder.create();
		alertDialogCouponList.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		TextView textViewInputCoupon = (TextView) view.findViewById(R.id.tv_coupon_input);
		textViewInputCoupon.setOnClickListener(inputCouponClickListener);

		ListView listView = (ListView) view.findViewById(R.id.lv_coupons);
		listView.setDividerHeight(0);
		if (data != null) {
			UserCouponsListAdapter adapter = new UserCouponsListAdapter(context, data, R.layout.item_user_coupon_list);
			listView.setAdapter(adapter);
		}
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				alertDialogCouponList.dismiss();
				couponClickListener.couponClick(position);
			}
		});

		alertDialogCouponList.show();

	}


	/**
	 * 关闭选择优惠券对话框
	 */
	public static void dissmissCouponListDialog() {
		if (alertDialogCouponList != null) {
			alertDialogCouponList.dismiss();
		}
	}


	/**
	 * 兑换优惠券对话框
	 *
	 * @param context
	 * @param onCouponExchangeClickListener
	 */
	public static void UserCunponExchangeDialog(Context context, final View.OnClickListener backClickListener, final OnInputDialogClickListener onCouponExchangeClickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.layout_user_coupon_exchange_dialog, null);

//    if (alertDialogCouponExchange == null) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		alertDialogCouponExchange = builder.create();
//    }

		alertDialogCouponExchange.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		TextView textViewBack = (TextView) view.findViewById(R.id.tv_user_coupon_exchange_back);
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialogCouponExchange.dismiss();
				backClickListener.onClick(v);
			}
		});

		final EditText editText = (EditText) view.findViewById(R.id.et_user_coupon_exchange_no);

		Button button = (Button) view.findViewById(R.id.btn_user_coupon_exchange_search);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(editText.getText().toString())) {
					ToastUtil.showText(R.string.hc_transfer_coupon_please_input_code);
					return;
				}
				onCouponExchangeClickListener.onClick(true, editText.getText().toString());
			}
		});

		alertDialogCouponExchange.show();

	}


	/**
	 * 关闭兑换优惠券对话框
	 */
	public static void dissmissCouponExchangeDialog() {
		if (alertDialogCouponExchange != null) {
			alertDialogCouponExchange.dismiss();
		}
	}


	/**
	 * 兑换优惠券成功对话框
	 *
	 * @param context
	 * @param onCouponExchangeSucsessClickListener
	 */
	public static void UserCunponExchangeSucsessDialog(Context context, CouponEntity coupon, final OnCouponExchangeSucsessClickListener onCouponExchangeSucsessClickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.layout_user_coupon_succsess_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		TextView textViewTitle = (TextView) view.findViewById(R.id.tv_user_coupon_title);
		textViewTitle.setText(coupon.getTitle() + coupon.getAmount() + "元");

		Button buttonUse = (Button) view.findViewById(R.id.btn_user_coupon_use);
		Button buttonUnUse = (Button) view.findViewById(R.id.btn_user_coupon_unuse);

		buttonUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				dissmissCouponExchangeDialog();
				dissmissCouponListDialog();
				onCouponExchangeSucsessClickListener.isUseClick(true);
			}
		});
		buttonUnUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				dissmissCouponExchangeDialog();
				dissmissCouponListDialog();
				onCouponExchangeSucsessClickListener.isUseClick(false);
			}
		});

		alertDialog.show();

	}


	/**
	 * 兑换优惠券成功对话框
	 *
	 * @param context
	 */
	public static void UserCunponExchangeFailureDialog(Context context, final View.OnClickListener backClickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.layout_user_coupon_failure_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		Button buttonBack = (Button) view.findViewById(R.id.btn_user_coupon_back);

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				backClickListener.onClick(v);
			}
		});

		alertDialog.show();
	}


	/**
	 * 优惠券选择回调
	 */
	public interface OnCouponClickListener {
		void couponClick(int position);
	}


	/**
	 * 优惠券查询回调
	 */
	public interface OnInputDialogClickListener {
		void onClick(boolean isOK, String code);
	}


	/**
	 * 优惠券查询回调
	 */
	public interface OnCouponExchangeSucsessClickListener {
		void isUseClick(boolean isUser);
	}


	/**
	 * 现金支付确认
	 *
	 * @param context                 context
	 * @param price                   price
	 * @param isEarnest               true 定金； false 服务费
	 * @param onDialogConfirmListener 回调
	 */
	public static void pospayCahsConfirmDialog(Context context, String price, boolean isEarnest, final OnDialogConfirmListener onDialogConfirmListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.dialog_pospay_cash_confirm, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		TextView textViewTitle = (TextView) view.findViewById(R.id.tv_pospay_cash_confirm_price);
		String source;
		if (isEarnest) {
			source = "请确保可当场收取<font color=\"#ff3333\">"
					+ price + "元</font>定金！";
		} else {
			source = "请确保可当场收取<font color=\"#ff3333\">"
					+ price + "元</font>服务费！";
		}

		textViewTitle.setText(Html.fromHtml(source));

		Button buttonUse = (Button) view.findViewById(R.id.btn_pospay_cash_confirm);
		Button buttonUnUse = (Button) view.findViewById(R.id.btn_pospay_cash_cancle);

		buttonUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				onDialogConfirmListener.isClick(true);
			}
		});
		buttonUnUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				onDialogConfirmListener.isClick(false);
			}
		});

		alertDialog.show();

	}


	/**
	 * 刷卡输入操作员的对话框
	 *
	 * @param context
	 * @param onInputDialogClickListener
	 */
	public static void posInputLoginNameDialog(Context context, final OnInputDialogClickListener onInputDialogClickListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		final View view = flater.inflate(R.layout.dialog_pospay_input_login_name, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();

		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		final EditText editText = (EditText) view.findViewById(R.id.et_pospay_login_name);
		editText.setText(GlobalData.userDataHelper.getPosLoginName());

		Button buttonUse = (Button) view.findViewById(R.id.btn_pospay_login_name_confirm);
		Button buttonUnUse = (Button) view.findViewById(R.id.btn_pospay_login_name_cancle);

		buttonUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				onInputDialogClickListener.onClick(true, editText.getText().toString().trim());
			}
		});
		buttonUnUse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				onInputDialogClickListener.onClick(false, "");
			}
		});

		alertDialog.show();

	}

	/**
	 * 优惠券查询回调
	 */
	public interface OnDialogConfirmListener {
		void isClick(boolean confirm);
	}


	public static void TransactionFilterLevel(Context context, int checked, final OnLevelConfirmListener onLevelConfirmListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		View view = flater.inflate(R.layout.transaction_high_seas_filter_level_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_customer_level);

		switch (checked) {
			case TaskConstants.INTERESTED:
				radioGroup.check(radioGroup.getChildAt(0).getId());
				break;
			case TaskConstants.NOINTERESTED:
				radioGroup.check(radioGroup.getChildAt(1).getId());
				break;
		}

		/** 取消 */
		Button cancle = (Button) view.findViewById(R.id.cancel);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				onLevelConfirmListener.isClick(-1);
			}
		});
		/** 确定 */
		Button confirm = (Button) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				int level = -1;
				switch (radioGroup.getCheckedRadioButtonId()) {
					case R.id.rb_customer_level_normal:
						level = TaskConstants.INTERESTED;
						break;
					case R.id.rb_customer_level_no:
						level = TaskConstants.NOINTERESTED;
						break;
				}
				onLevelConfirmListener.isClick(level);
			}
		});

		alertDialog.show();

	}

	/**
	 * 买家排序
	 */
	public static void TransactionFilterSort(Context context, int sort, int where, final OnSortConfirmListener onSortConfirmListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		View view = flater.inflate(R.layout.transaction_filter_sort_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		final RadioGroup sortRadioGroup = (RadioGroup) view.findViewById(R.id.rg_customer_sort);
		final RadioGroup whereRadioGroup = (RadioGroup) view.findViewById(R.id.rg_customer_where);

		switch (sort) {
			case TaskConstants.ASCENDING:
				sortRadioGroup.check(sortRadioGroup.getChildAt(0).getId());
				break;
			case TaskConstants.DESCENDING:
				sortRadioGroup.check(sortRadioGroup.getChildAt(1).getId());
				break;
		}
		switch (where) {
			case TaskConstants.LEVEL:
				whereRadioGroup.check(whereRadioGroup.getChildAt(0).getId());
				break;
			case TaskConstants.REVISIT:
				whereRadioGroup.check(whereRadioGroup.getChildAt(1).getId());
				break;
		}

		/** 取消 */
		Button cancle = (Button) view.findViewById(R.id.cancel);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		/** 确定 */
		Button confirm = (Button) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				int sort = -1;
				switch (sortRadioGroup.getCheckedRadioButtonId()) {
					case R.id.rg_customer_asc:
						sort = TaskConstants.ASCENDING;
						break;
					case R.id.rg_customer_desc:
						sort = TaskConstants.DESCENDING;
						break;
				}

				int where = -1;
				switch (whereRadioGroup.getCheckedRadioButtonId()) {
					case R.id.rb_customer_level:
						where = TaskConstants.LEVEL;
						break;
					case R.id.rb_customer_revisit:
						where = TaskConstants.REVISIT;
						break;
				}
				if (onSortConfirmListener != null)
					onSortConfirmListener.isClick(sort, where);
			}
		});
		alertDialog.show();
	}

	/**
	 * 买家筛选
	 */
	public static void TransactionFilter(Context context, int where, final OnLevelConfirmListener onLevelConfirmListener) {

		LayoutInflater flater = LayoutInflater.from(context);
		View view = flater.inflate(R.layout.transaction_filter_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		final AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_customer_filter);
		switch (where) {
			case TaskConstants.ALL:
				radioGroup.check(radioGroup.getChildAt(0).getId());
				break;
			case TaskConstants.LEVELH:
				radioGroup.check(radioGroup.getChildAt(1).getId());
				break;
			case TaskConstants.LEVELA:
				radioGroup.check(radioGroup.getChildAt(2).getId());
				break;
			case TaskConstants.LEVELB:
				radioGroup.check(radioGroup.getChildAt(3).getId());
				break;
			case TaskConstants.TODAY_VISIT:
				radioGroup.check(radioGroup.getChildAt(4).getId());
				break;
			case TaskConstants.TODAY_FAIL:
				radioGroup.check(radioGroup.getChildAt(5).getId());
				break;
		}

		/** 取消 */
		Button cancle = (Button) view.findViewById(R.id.cancel);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		/** 确定 */
		Button confirm = (Button) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				int where = -1;
				switch (radioGroup.getCheckedRadioButtonId()) {
					case R.id.rb_customer_all:
						where = TaskConstants.ALL;
						break;
					case R.id.rb_customer_h:
						where = TaskConstants.LEVELH;
						break;
					case R.id.rb_customer_a:
						where = TaskConstants.LEVELA;
						break;
					case R.id.rb_customer_b:
						where = TaskConstants.LEVELB;
						break;
					case R.id.rb_customer_revisit:
						where = TaskConstants.TODAY_VISIT;
						break;
					case R.id.rb_customer_fail:
						where = TaskConstants.TODAY_FAIL;
						break;
				}
				if (onLevelConfirmListener != null)
					onLevelConfirmListener.isClick(where);
			}
		});
		alertDialog.show();
	}

	/**
	 * 客户级别
	 */
	public interface OnLevelConfirmListener {
		void isClick(int level);
	}

	/**
	 * 客户排序
	 */
	public interface OnSortConfirmListener {
		void isClick(int sort, int where);
	}

	/**
	 * 弹出选择回访类型对话框
	 */
	public static void revisitFilter(Activity activity, int source, final OnRevisitSourceConfirmListener sourceConfirmListener) {
		LayoutInflater flater = LayoutInflater.from(activity);
		View view = flater.inflate(R.layout.revisit_filter_dialog, null);

		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
		builder.setView(view);
		final android.app.AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

		final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_customer_filter);
		switch (source) {
			case TaskConstants.REQUEST_REVISIT_SELF:
				radioGroup.check(radioGroup.getChildAt(0).getId());
				break;
			case TaskConstants.REQUEST_REVISIT_PENDING:
				radioGroup.check(radioGroup.getChildAt(1).getId());
				break;
			case TaskConstants.REQUEST_REVISIT_UNVIEW:
				radioGroup.check(radioGroup.getChildAt(2).getId());
				break;
		}

		/** 取消 */
		Button cancle = (Button) view.findViewById(R.id.cancel);
		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		/** 确定 */
		Button confirm = (Button) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				int where = -1;
				switch (radioGroup.getCheckedRadioButtonId()) {
					case R.id.rb_source_self:
						where = TaskConstants.REQUEST_REVISIT_SELF;
						break;
					case R.id.rb_source_huilu:
						where = TaskConstants.REQUEST_REVISIT_PENDING;
						break;
					case R.id.rb_source_weituichu:
						where = TaskConstants.REQUEST_REVISIT_UNVIEW;
						break;
				}
				if (sourceConfirmListener != null)
					sourceConfirmListener.isClick(where);
			}
		});
		alertDialog.show();
	}

	/**
	 * 回访类型
	 */
	public interface OnRevisitSourceConfirmListener {
		void isClick(int source);
	}
}
