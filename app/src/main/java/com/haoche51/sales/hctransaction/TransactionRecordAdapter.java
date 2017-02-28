package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 看车记录
 * Created by mac on 15/9/26.
 */
public class TransactionRecordAdapter extends HCCommonAdapter<TransactionRecordEntity> {

	public TransactionRecordAdapter(Context context, List<TransactionRecordEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(final HCCommonViewHolder holder, int position) {
		TransactionRecordEntity item = getItem(position);

		holder.setTextViewText(R.id.tv_item_vehicle_model, item.getVehicle_name());
		holder.setTextViewText(R.id.tv_item_transaction_time, UnixTimeUtil.format(item.getAppointment_starttime()));
		holder.setTextViewText(R.id.tv_item_transaction_add, item.getPlace());

		TextView status = holder.findTheView(R.id.tv_item_transaction_result);
		TextView reason = holder.findTheView(R.id.tv_item_failed_reason);

		StringBuilder reasontext = new StringBuilder("原因:");
		reasontext.append((TextUtils.isEmpty(item.getAbort_reason()) ? "未知" : item.getAbort_reason()));
		reason.setText(reasontext.toString());
		ControlDisplayUtil.getInstance().setTextAndColor(status, reason, item.getCancel_trans_status(), item.getStatus());
		View theView = holder.findTheView(R.id.stepline);
		theView.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
		RelativeLayout relativeLayout = holder.findTheView(R.id.rl_info);
		relativeLayout.measure(0, 0);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DisplayUtils.dip2px(mContext, 0.3f), relativeLayout.getMeasuredHeight() - DisplayUtils.dip2px(mContext, 5));
		params.setMargins(DisplayUtils.dip2px(mContext, 23), DisplayUtils.dip2px(mContext, 5), 0, 0);
		theView.setLayoutParams(params);
	}
}

