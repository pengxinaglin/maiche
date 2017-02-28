package com.haoche51.sales.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.helper.ResourceHelper;

/**
 * Created by mac on 15/11/6.
 */
public class ControlDisplayUtil {
	private static Context mContext;
	private static ResourceHelper resourceHelper = null;
	private static ControlDisplayUtil mControlDisplayUtil;

	private ControlDisplayUtil() {
	}

	public static ControlDisplayUtil getInstance() {
		synchronized (ControlDisplayUtil.class) {
			if (mControlDisplayUtil == null) {
				mControlDisplayUtil = new ControlDisplayUtil();
				mContext = GlobalData.mContext;
				resourceHelper = new ResourceHelper(mContext);
			}
		}
		return mControlDisplayUtil;
	}

	/**
	 * 根据线索状态设置文案及字色
	 */
	public void setTextAndColorByCluesStatus(int status, TextView tv_state) {
		//状态 0：跟进中 1：正在处理 2：已处理 3：无效
		tv_state.setTextColor(mContext.getResources().getColor(R.color.white_B0));
		switch (status) {
			case TaskConstants.CLUESSTATUS_DEFAULT:
				tv_state.setText(resourceHelper.getString(R.string.cluesstatus_default));
				break;
			case TaskConstants.CLUESSTATUS_PROCESSING:
				tv_state.setText(resourceHelper.getString(R.string.cluesstatus_processing));
				break;
			case TaskConstants.CLUESSTATUS_SUCCESS:
				tv_state.setTextColor(mContext.getResources().getColor(R.color.success_green));
				tv_state.setText(resourceHelper.getString(R.string.cluesstatus_success));
				break;
			case TaskConstants.CLUESSTATUS_SUBMIT_INVALID:
				tv_state.setText(resourceHelper.getString(R.string.cluesstatus_invalid));
				break;
			default:
				break;
		}
	}

	/**
	 * 获得毁约状态
	 */
	public String getTransCancelStatus(int status) {
		String result = "";
		switch (status) {
			case TaskConstants.CANCEL_TRANS_STATUS_NOTCONFIRM:
				result = resourceHelper.getString(R.string.breach_of_promise_to_confirm);
				break;
			case TaskConstants.CANCEL_TRANS_STATUS_CONFIRMED:
				result = resourceHelper.getString(R.string.already_breach_of_promise);
				break;
		}
		return result;
	}

	/**
	 * 获得车源操作状态
	 */
	public String getVehicleStatus(int vehicleStatus) {
		//1:待检 2:审核 3: 上线 4: 预定 5:售出 6:公司回购 7:车主售出
		String result = "";
		switch (vehicleStatus) {
			case 0:
				result = "下线";
				break;
			case 1:
				result = resourceHelper.getString(R.string.waiting_check);
				break;
			case 2:
				result = resourceHelper.getString(R.string.audit);
				break;
			case 3:
				result = resourceHelper.getString(R.string.online);
				break;
			case 4:
				result = resourceHelper.getString(R.string.reservation);
				break;
			case 5:
				result = resourceHelper.getString(R.string.sold);
				break;
			case 6:
				result = resourceHelper.getString(R.string.company_repurchase);
				break;
			case 7:
				result = resourceHelper.getString(R.string.seller_sold);
				break;
			default:
				result = "下线";
				break;
		}
		return result;
	}


	public String getCheJinDingStatus(int cjd_status) {
		String result = "";
		switch (cjd_status) {
			case TaskConstants.DID_NOT_GET:
				result = resourceHelper.getString(R.string.did_not_get);
				break;
			case TaskConstants.OBTAING:
				result = resourceHelper.getString(R.string.obtaing);
				break;
			case TaskConstants.GET_SUCC:
				result = resourceHelper.getString(R.string.get_succ);
				break;
			case TaskConstants.GET_FAIL:
				result = resourceHelper.getString(R.string.get_fail);
				break;
		}
		return result;
	}

	public void setTextAndColor(TextView textView, TextView reason, int cancel_trans_status, int status) {
		Drawable drawable;
		//是否是毁约
		if (cancel_trans_status > TaskConstants.CANCEL_TRANS_STATUS_DEFAULT) {
			switch (cancel_trans_status) {
				case TaskConstants.CANCEL_TRANS_STATUS_NOTCONFIRM:
					textView.setText(resourceHelper.getString(R.string.breach_of_promise_to_confirm));
					break;
				case TaskConstants.CANCEL_TRANS_STATUS_CONFIRMED:
					textView.setText(resourceHelper.getString(R.string.already_breach_of_promise));
					break;
			}
			textView.setTextColor(mContext.getResources().getColor(R.color.red));
			drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_trans_failed);
			reason.setVisibility(View.GONE);
		} else {
			//0未知12失败345成功6毁约
			switch (status) {
				case TaskConstants.VIEW_TASK_BCANCEL:
				case TaskConstants.VIEW_TASK_ACANCEL:
					textView.setText(resourceHelper.getString(R.string.with_the_failure));
					textView.setTextColor(mContext.getResources().getColor(R.color.red));
					drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_trans_failed);
					reason.setVisibility(View.VISIBLE);
					break;
				case TaskConstants.VIEW_TASK_BREACH:
					textView.setText(resourceHelper.getString(R.string.breach_of_promise));
					textView.setTextColor(mContext.getResources().getColor(R.color.red));
					drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_trans_failed);
					reason.setVisibility(View.GONE);
					break;
				case TaskConstants.VIEW_TASK_PREPAY:
				case TaskConstants.VIEW_TASK_FULLPAY:
				case TaskConstants.VIEW_TASK_FINISH:
					textView.setText(resourceHelper.getString(R.string.see_succ));
					textView.setTextColor(mContext.getResources().getColor(R.color.hc_task_fullpay));
					drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_trans_success);
					reason.setVisibility(View.GONE);
					break;
				case TaskConstants.VIEW_TASK_PENDING:
				default:
					textView.setText(resourceHelper.getString(R.string.the_unknown));
					textView.setTextColor(mContext.getResources().getColor(R.color.self_yellow));
					drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_trans_failed);
					reason.setVisibility(View.VISIBLE);
					break;
			}
		}
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			textView.setCompoundDrawables(drawable, null, null, null);
		}
	}

	/**
	 * 设置看车成功列表任务状态
	 *
	 * @param textView
	 * @param audit_status
	 */
	public void setTransSuccessItemStatus(TextView textView, int audit_status) {
		if (audit_status == TaskConstants.STATUS_CANCEL_FINISH || audit_status == TaskConstants.STATUS_TRANSFER_CONTRACT_FAIL)
			textView.setTextColor(mContext.getResources().getColor(R.color.red));
		else
			textView.setTextColor(mContext.getResources().getColor(R.color.GREEN));
	}


	public static String getHighSeasLevel(int level) {
		String result = "";
		switch (level) {
			case TaskConstants.NOINTERESTED:
				result = GlobalData.mContext.getString(R.string.hc_transaction_level_nointerested);
				break;
			case TaskConstants.INTERESTED:
				result = GlobalData.mContext.getString(R.string.hc_transaction_level_interested);
				break;

		}
		return result;
	}

	/**
	 * 做一个滑动关闭打开动画
	 */
	public static void displayOpenAnimation(final View view, boolean open, int targetTime) {
		final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		//测量view的宽高
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		ValueAnimator va;
		if (open) {
			va = ValueAnimator.ofInt(0, view.getMeasuredHeight());
		} else {
			va = ValueAnimator.ofInt(view.getMeasuredHeight(), 0);
		}
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator va) {
				layoutParams.height = (Integer) va.getAnimatedValue();
				view.setLayoutParams(layoutParams);
			}
		});
		va.setDuration(targetTime);
		va.start();

	}
}
