package com.haoche51.sales.hctransfer;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/***
 * 底部tab 过户 列表 适配器
 */
public class TransferAdapter extends BaseAdapter {

	private List<TransferSimpleEntity> mData;
	private Resources mResources;

	public TransferAdapter(List<TransferSimpleEntity> data) {
		this.mData = data;
		mResources = GlobalData.mContext.getResources();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mData.size() == 0) {
			TextView textView = new TextView(GlobalData.mContext);
			textView.setText(R.string.hc_transfer_list_empty);
			textView.setTextColor(Color.BLACK);
			textView.setHeight(120);
			textView.setWidth(-1);
			textView.setGravity(0x11);
			return textView;
		}

		TViewHolder mHolder = null;
		if (convertView == null || TextView.class.isInstance(convertView)) {
			mHolder = new TViewHolder();
			convertView = LayoutInflater.from(GlobalData.mContext).inflate(R.layout.item_transfer_list, parent, false);
			mHolder.vehicle_name = (TextView) convertView.findViewById(R.id.vehicle_name);
			mHolder.vehicle_number = (TextView) convertView.findViewById(R.id.vehicle_number);
			mHolder.status = (TextView) convertView.findViewById(R.id.status);
			mHolder.transfer_time = (TextView) convertView.findViewById(R.id.transfer_time);
			convertView.setTag(mHolder);
		} else {
			mHolder = (TViewHolder) convertView.getTag();
		}

		if (position < mData.size()) {
			TransferSimpleEntity entity = mData.get(position);
			setItemdata(mHolder, entity);
		}
		return convertView;
	}

	private void setItemdata(TViewHolder mHolder, TransferSimpleEntity transfer) {
		// mHolder.vehicle_name.setText(transfer.get);
		String str = transfer.getBrand_name() + transfer.getClass_name();
		mHolder.vehicle_name.setText(str);
		mHolder.vehicle_number.setText(transfer.getPlate_number());

		int unixtime = transfer.getTransfer_time();
		String mTransferTime = UnixTimeUtil.formatTransferTime(unixtime);
		mHolder.transfer_time.setText(mTransferTime);
		mHolder.status.setText(transfer.getAudit_text());

		switch (transfer.getAudit_status()) {
			case TaskConstants.STATUS_WAIT_TRANSFER: //待过户
			case TaskConstants.STATUS_TRANSFER_CONTRACT: //过户合同审核
				mHolder.status.setTextColor(mResources.getColor(R.color.hc_indicator));
				break;
			case TaskConstants.STATUS_TRANSFER_CONTRACT_FAIL://过户合同审核拒绝
			case TaskConstants.STATUS_CANCEL_FINISH://已毁约
				mHolder.status.setTextColor(mResources.getColor(R.color.hc_self_red));
				break;
			default:
				mHolder.status.setTextColor(mResources.getColor(R.color.hc_self_black));
				break;
		}
	}


	private static class TViewHolder {
		TextView vehicle_name;
		TextView vehicle_number;
		TextView status;
		TextView transfer_time;
	}

	public void resetAdapterData(List<TransferSimpleEntity> data) {
		// TDOO: 这里得考虑一下怎么通知基类里的list变化
		this.mData = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();//此处没有数据返回0
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}