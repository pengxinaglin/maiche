package com.haoche51.sales.hcrecommend;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 回购线索列表适配器
 * Created by PengXianglin on 16/6/14.
 */
public class PurchaseCluesAdapter extends HCCommonAdapter<PurchaseCluesEntity> {

	public PurchaseCluesAdapter(Context context, List<PurchaseCluesEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		PurchaseCluesEntity entity = mList.get(position);

		holder.setTextViewText(R.id.tv_title, "[" + entity.getVehicle_source_id() + "]" + entity.getTitle());
		holder.setTextViewText(R.id.tv_remark, entity.getRemark());
		holder.setTextViewText(R.id.tv_createtime, UnixTimeUtil.format(entity.getCreate_time(), "MM-dd HH:mm"));
		holder.setTextViewText(R.id.tv_state, entity.getStatus_text());
	}
}
