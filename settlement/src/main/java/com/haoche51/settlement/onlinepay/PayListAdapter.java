package com.haoche51.settlement.onlinepay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haoche51.settlement.R;

import java.util.ArrayList;
import java.util.List;

public class PayListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<PayListEntity> mPayListEntities;

    public PayListAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据
     */
    public void setData(List<PayListEntity> payListEntities) {
        mPayListEntities = payListEntities;
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<PayListEntity> payListEntities) {
        if (mPayListEntities == null) {
            mPayListEntities = new ArrayList<PayListEntity>();
        }
        mPayListEntities.addAll(payListEntities);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mPayListEntities == null || mPayListEntities.size() == 0) {
            return 0;
        }
        return mPayListEntities.size();
    }

    @Override
    public Object getItem(int position) {
        if (mPayListEntities == null || mPayListEntities.size() == 0) {
            return null;
        }
        return mPayListEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.pay_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.dayView = (TextView) convertView.findViewById(R.id.day_text_view);
            viewHolder.timeView = (TextView) convertView.findViewById(R.id.time_text_view);
            viewHolder.moneyView = (TextView) convertView.findViewById(R.id.money_text_view);
            viewHolder.phoneView = (TextView) convertView.findViewById(R.id.phone_text_view);
            viewHolder.showDescView = (TextView) convertView.findViewById(R.id.show_status_desc_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mPayListEntities != null && mPayListEntities.size() > 0) {
            PayListEntity entity = mPayListEntities.get(position);
            viewHolder.dayView.setText(entity.getDayTimes()[0]);
            viewHolder.timeView.setText(entity.getDayTimes()[1]);

            if (entity.getShow_status() == 1) {
                viewHolder.moneyView.setText("+" + entity.getTradeAmount());
            } else {
                viewHolder.moneyView.setText("-" + entity.getTradeAmount());
            }

            viewHolder.phoneView.setText(mContext.getString(R.string.pay_custom_phone) + "-" + entity.getBuyer_phone());
            viewHolder.showDescView.setText(entity.getShow_status_desc());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView dayView;
        TextView timeView;
        TextView moneyView;
        TextView phoneView;
        TextView showDescView;
    }
}
