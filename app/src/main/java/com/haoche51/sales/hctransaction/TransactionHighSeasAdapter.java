package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 公海客户
 * Created by mac on 15/9/24.
 */
public class TransactionHighSeasAdapter extends HCCommonAdapter<TransactionHighSeasEntity> {

    public TransactionHighSeasAdapter(Context context, List<TransactionHighSeasEntity> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void fillViewData(HCCommonViewHolder holder, int position) {
        TransactionHighSeasEntity item = getItem(position);
        holder.setTextViewText(R.id.tv_item_buyer_name, item.getBuyer_name());
        if (!TextUtils.isEmpty(DisplayUtils.getCustomerLevel(item.getLevel()))) {
            holder.setTextViewText(R.id.tv_item_customer_level, DisplayUtils.getCustomerLevel(item.getLevel()));
        } else {
            holder.findTheView(R.id.tv_item_customer_level).setVisibility(View.GONE);
        }
        holder.setTextViewText(R.id.tv_item_buyer_phone, item.getBuyer_phone());
        holder.setTextViewText(R.id.tv_item_revisit_content, item.getRevisit_content());
        holder.setTextViewText(R.id.tv_item_revisit_time, item.getRevisit_time() > 0 ?
                UnixTimeUtil.format(item.getRevisit_time()) : "-----------");
        int onsite_trans_count = item.getOnsite_trans_count();
        holder.setTextViewText(R.id.tv_item_onsite_count, item.getCar_dealer() == 1 ? "车商 · " + "已上门" + onsite_trans_count + "次"
                : "已上门" + onsite_trans_count + "次");
    }
}