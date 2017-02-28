package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.widget.CheckBox;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 复活买家
 */
public class BuyerReviveAdapter extends HCCommonAdapter<TransactionTaskShortEntity> {

    private int selectePosition = -1;

    public int getSelectePosition() {
        return selectePosition;
    }

    public void setSelectePosition(int selectePosition) {
        this.selectePosition = selectePosition;
    }

    public BuyerReviveAdapter(Context context, List<TransactionTaskShortEntity> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void fillViewData(HCCommonViewHolder holder, int position) {
        TransactionTaskShortEntity item = getItem(position);
        holder.setTextViewText(R.id.tv_item_transaction_series,item.getVehicle_name());
        holder.setTextViewText(R.id.tv_number_math,item.getVehicle_source_id().toString());
        holder.setTextViewText(R.id.tv_item_date, UnixTimeUtil.format(item.getAppointment_starttime()));
        CheckBox checkbox= holder.findTheView(R.id.cb_select);
        if (position == selectePosition) {
            checkbox.setChecked(true);
        }else {
            checkbox.setChecked(false);
        }
    }
}
