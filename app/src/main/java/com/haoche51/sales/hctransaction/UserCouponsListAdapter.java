package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;

import java.util.List;

/**
 * Created by yangming on 2015/12/28.
 */
public class UserCouponsListAdapter extends HCCommonAdapter<CouponEntity> {

  public UserCouponsListAdapter(Context context, List<CouponEntity> data, int layoutId) {
    super(context, data, layoutId);
  }

  @Override
  public int getCount() {
    return super.getCount() + 1;
  }

  @Override
  public void fillViewData(HCCommonViewHolder holder, int position) {
    if (position == 0) {
      holder.setTextViewText(R.id.tv_item_user_coupon_name, GlobalData.mContext.getString(R.string.hc_transfer_coupon_no_use));
    } else {
      holder.setTextViewText(R.id.tv_item_user_coupon_name,
        TextUtils.isEmpty(mList.get(position - 1).getTitle()) ? "" : mList.get(position - 1).getTitle()
          + mList.get(position - 1).getAmount() + "元");
    }
  }
}
