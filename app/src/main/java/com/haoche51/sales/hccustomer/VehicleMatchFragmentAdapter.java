package com.haoche51.sales.hccustomer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.TagsLayout;
import com.haoche51.sales.custom.tagtext.TagTextView;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.helper.ImageLoaderHelper;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * Created by PengXianglin on 16/11/24.
 */
public class VehicleMatchFragmentAdapter extends HCCommonAdapter<VehicleMatchEntity> {

	public VehicleMatchFragmentAdapter(Context context, List<VehicleMatchEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		VehicleMatchEntity item = getItem(position);

		ImageView imageView = holder.findTheView(R.id.iv_photo);
		ImageLoaderHelper.displayImage(item.getImage_url(), imageView);

		final TagTextView tv_model = holder.findTheView(R.id.tv_model);
		try {
			tv_model.setText((TextUtils.isEmpty(item.getVehicle_name()) ? "" : " " + item.getVehicle_name())
					+ (TextUtils.isEmpty(item.getVehicle_type_text()) ? "" : "@" + item.getVehicle_type_text()));
			tv_model.post(new Runnable() {
				@Override
				public void run() {
					tv_model.render();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuilder info = new StringBuilder(UnixTimeUtil.format(item.getRegister_time(), UnixTimeUtil.YEAR_MONTH_PATTERN));
		info.append("上牌");
		info.append(" · ");
		info.append(item.getMiles());
		info.append("万公里");
		info.append(TextUtils.isEmpty(item.getGearbox()) ? "" : " · " + item.getGearbox());
		holder.setTextViewText(R.id.tv_info, info.toString());
		holder.setTextViewText(R.id.tv_intend_seller_price, item.getSeller_price() + "万");

		TagsLayout tagsLayout = holder.findTheView(R.id.ll_tags);
		tagsLayout.removeAllViews();
		tagsLayout.setPadding(0, 0, 0, 0);
		//车源更多标签
		if (item.getVehicle_tag() != null && !item.getVehicle_tag().isEmpty()) {
			tagsLayout.setPadding(DisplayUtils.dip2px(mContext, 15), 0, DisplayUtils.dip2px(mContext, 15), DisplayUtils.dip2px(mContext, 15));
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(35, 0, 0, 0);
			for (int i = 0; i < item.getVehicle_tag().size(); i++) {
				TextView textView = new TextView(mContext);
				textView.setTextAppearance(mContext, R.style.vehicle_lable);
				Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_vehicle_tag);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				textView.setCompoundDrawables(drawable, null, null, null);
				textView.setCompoundDrawablePadding(5);
				textView.setText(item.getVehicle_tag().get(i));
				tagsLayout.addView(textView, lp);
			}
		}
	}
}
