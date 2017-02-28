package com.haoche51.sales.hctransfer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hctransaction.TransactionTaskShortEntity;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

public class TransTasksAdapter extends HCCommonAdapter<TransactionTaskShortEntity> {

	private List<TransactionTaskShortEntity> adapterTask;

	public TransTasksAdapter(Context context, List<TransactionTaskShortEntity> data, int layoutId) {
		super(context, data, layoutId);
		this.adapterTask = data;
	}

	public void setAdapterTask(List<TransactionTaskShortEntity> adapterTask) {
		this.adapterTask = adapterTask;
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		TransactionTaskShortEntity vTask = getItem(position);
		holder.setTextViewText(R.id.tv_item_buyer_name, vTask.getBuyer_name());
		holder.setTextViewTextNoLink(R.id.tv_item_buyer_phone, vTask.getBuyer_phone());
		holder.setTextViewText(R.id.tv_item_transaction_time, UnixTimeUtil.format(vTask.getAppointment_starttime()));
		holder.setTextViewText(R.id.tv_item_transaction_address, vTask.getPlace());
		holder.setTextViewText(R.id.tv_item_transaction_vehicle, vTask.getVehicle_name());

		TextView vehicleType = holder.findTheView(R.id.tv_item_transaction_source);

		if (vTask.getTask_type() == 1) {
			vehicleType.setSelected(true);
			vehicleType.setText(mContext.getString(R.string.hc_transaction_store));
		} else {
			vehicleType.setSelected(false);
			vehicleType.setText(mContext.getString(R.string.hc_transaction_outdoors));
		}

		TextView textView = holder.findTheView(R.id.tv_item_today_task);
		if (vTask.getCar_dealer() == 1) {
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.GONE);
		}
	}

}