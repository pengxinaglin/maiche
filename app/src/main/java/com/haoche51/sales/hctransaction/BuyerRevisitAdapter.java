package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 买家回访
 * Created by mac on 15/9/26.
 */
public class BuyerRevisitAdapter extends HCCommonAdapter<BuyerRevisitEntity> {

	public BuyerRevisitAdapter(Context context, List<BuyerRevisitEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		BuyerRevisitEntity item = getItem(position);
		holder.setTextViewText(R.id.tv_item_vehicle_saler_name, item.getCustom_service_name());
		holder.setTextViewText(R.id.tv_item_vehicle_revisit_time, UnixTimeUtil.format(item.getTime()));
		String content = item.getContent();
		if (!TextUtils.isEmpty(content) && content.length() > 50)//最多显示50个字
			content = content.substring(0, 50) + "...";//截取50个字
		holder.setTextViewText(R.id.tv_item_vehicle_content, content);
	}
}
