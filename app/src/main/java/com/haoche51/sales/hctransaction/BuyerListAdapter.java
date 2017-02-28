package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 看车失败
 * Created by mac on 15/9/24.
 */
public class BuyerListAdapter extends HCCommonAdapter<BuyerEntity> {

	public BuyerListAdapter(Context context, List<BuyerEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		BuyerEntity item = getItem(position);
		holder.setTextViewText(R.id.tv_item_buyer_name, item.getName());
		if (!TextUtils.isEmpty(DisplayUtils.getCustomerLevel(item.getLevel()))) {
			holder.setTextViewText(R.id.tv_item_customer_level, DisplayUtils.getCustomerLevel(item.getLevel()));
		} else {
			holder.findTheView(R.id.tv_item_customer_level).setVisibility(View.GONE);
		}
		TextView tv_item_today_task = holder.findTheView(R.id.tv_item_today_task);
		tv_item_today_task.setVisibility(item.getToday_task() == 1 ? View.VISIBLE : View.GONE);
		holder.setTextViewText(R.id.tv_item_buyer_phone, item.getPhone());
		holder.setTextViewText(R.id.tv_item_revisit_content, item.getRevisit_content());
		holder.setTextViewText(R.id.tv_item_revisit_time, item.getRevisit_time() > 0 ?
				UnixTimeUtil.format(item.getRevisit_time()) : "-----------");
		int onsite_trans_count = item.getOnsite_trans_count();
		holder.setTextViewText(R.id.tv_item_onsite_count, item.getCar_dealer() == 1 ? "车商 · " + "已上门" + onsite_trans_count + "次"
				: "已上门" + onsite_trans_count + "次");
	}
}
