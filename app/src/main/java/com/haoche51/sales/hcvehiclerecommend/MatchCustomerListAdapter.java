package com.haoche51.sales.hcvehiclerecommend;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.constants.ThirdPartyConst;
import com.haoche51.sales.util.ThirdPartInjector;
import com.haoche51.sales.util.WeiXinShareUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangming on 2015/11/5.
 */
public class MatchCustomerListAdapter extends HCCommonAdapter<MatchCustomerEntity> {

    private String vehicleId;

    public MatchCustomerListAdapter(Context context, List<MatchCustomerEntity> data, int layoutId, String vehicleId) {
        super(context, data, layoutId);
        this.vehicleId = vehicleId;
    }

    @Override
    public void fillViewData(HCCommonViewHolder holder, int position) {

        final MatchCustomerEntity customerEntity = getItem(position);

        holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_name, customerEntity.getBuyer_name());
        holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_phone, customerEntity.getBuyer_phone());
        TextView customerPhone = holder.findTheView(R.id.text_view_match_customer_list_item_layout_phone);
        customerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdPartInjector.onEvent((Activity) mContext, ThirdPartyConst.CUSTOMER_PHONE_CLICK);
                //用intent启动拨打电话
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + customerEntity.getBuyer_phone()));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                }
            }
        });

        if (customerEntity.getSubscribe_rule() != null) {
            List<String> listVehicleName = new ArrayList<>();
            if (customerEntity.getSubscribe_rule().getSubscribe_series() != null && customerEntity.getSubscribe_rule().getSubscribe_series().size() > 0)
                for (MatchCustomerEntity.SubscribeRuleEntity.SubscribeSeriesEntity seriesEntity : customerEntity.getSubscribe_rule().getSubscribe_series()) {
                    listVehicleName.add(seriesEntity.getName());
                }
            if (listVehicleName.size() > 0) {
                holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_vehicle, listVehicleName.toString().replace("[", "").replace("]", ""));
            }
            List<Float> listPrice = customerEntity.getSubscribe_rule().getPrice();
            if (listPrice != null && listPrice.size() >= 1) {
                if (listPrice.size() == 1) {
                    holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_price, listPrice.get(0) + "- -" + "万");
                }
                if (listPrice.size() == 2) {
                    holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_price, listPrice.get(0) + "-" + listPrice.get(1) + "万");
                }
            }
        }
        holder.setTextViewText(R.id.text_view_match_customer_list_item_layout_remark, "备注:" + customerEntity.getComment());
        LinearLayout button = holder.findTheView(R.id.button_match_customer_list_item_layout_shares);
        button.setOnClickListener(getOnClickListenerToShare(vehicleId, customerEntity.getBuyer_phone()));
    }

    private View.OnClickListener getOnClickListenerToShare(final String vehicleId, final String buyerPhone) {
        return new View.OnClickListener() {
            WeiXinShareDialog weiXinShareDialog;

            @Override
            public void onClick(View v) {
                weiXinShareDialog = new WeiXinShareDialog(mContext, R.layout.weixin_share_dialog, false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 得到剪贴板管理器
                        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(buyerPhone);
                        WeiXinShareUtils.wechatShare(0, mContext, vehicleId, GlobalData.userDataHelper.getUser().getPhone());
                        ThirdPartInjector.onEvent((Activity) mContext, ThirdPartyConst.VEHICLE_SHARE_CLICK);
                        weiXinShareDialog.dismiss();
                    }
                });
                weiXinShareDialog.show();
            }
        };
    }
}
