package com.haoche51.sales.hctransfer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.hctransaction.TransactionTaskShortEntity;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * Created by mac on 15/9/24.
 */
public class TransSuccessAdapter extends HCCommonAdapter<TransactionTaskShortEntity> {

	public TransSuccessAdapter(Context context, List<TransactionTaskShortEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		TransactionTaskShortEntity item = getItem(position);
		holder.setTextViewText(R.id.tv_item_buyer_name, item.getBuyer_name());
		holder.setTextViewTextNoLink(R.id.tv_item_buyer_phone, item.getBuyer_phone());
		holder.setTextViewText(R.id.tv_item_transaction_time, UnixTimeUtil.format(item.getAppointment_starttime()));
		holder.setTextViewText(R.id.tv_item_transaction_address, item.getPlace());
		holder.setTextViewText(R.id.tv_item_transaction_customer_see_vehicle, item.getVehicle_name());
		holder.setTextViewText(R.id.tv_item_transaction_status, item.getAudit_text());
		ControlDisplayUtil.getInstance().setTransSuccessItemStatus((TextView) holder.findTheView(R.id.tv_item_transaction_status), item.getAudit_status());

		TextView textView = holder.findTheView(R.id.tv_item_today_task);
		if (item.getCar_dealer() == 1) {
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.GONE);
		}
	}
}