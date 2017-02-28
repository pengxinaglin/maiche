package com.haoche51.sales.hcvehiclerecommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.ThirdPartyConst;
import com.haoche51.sales.util.ThirdPartInjector;
import com.haoche51.sales.util.UnixTimeUtil;
import com.haoche51.sales.util.HCActionUtil;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 15/10/30.
 */
public class VehicleRecommentAdapter extends BaseAdapter {

	private Context mContext;
	private List<VehicleRecommentEntity> adapterTask;

	public VehicleRecommentAdapter(Context context, List<VehicleRecommentEntity> adapterTask) {
		this.mContext = context;
		this.adapterTask = adapterTask;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		if (adapterTask == null) return 0;
		if (adapterTask.size() == 0) {
			return 1;
		}
		return adapterTask.size();
	}

	@Override
	public VehicleRecommentEntity getItem(int i) {
		return adapterTask.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (this.adapterTask.size() == 0) {
			TextView textView = new TextView(mContext);
			textView.setText("任务列表为空");
			textView.setTextSize(GlobalData.resourceHelper.getFontSize(R.dimen.px_18));
			textView.setTextColor(Color.BLACK);
			textView.setHeight(GlobalData.resourceHelper.getDimenPx(R.dimen.px_120));
			textView.setWidth(-1);
			textView.setGravity(0x11);
			return textView;
		}

		VehicleRecommentEntity item = getItem(position);
		if (convertView == null || TextView.class.isInstance(convertView)) {
			convertView = View.inflate(this.mContext, R.layout.layout_vehicle_recommend_item, null);
		}
		ViewHolder holder = getHolder(convertView);
		setData(item, holder);
		return convertView;
	}

	/**
	 * 设置数据
	 */
	private void setData(final VehicleRecommentEntity item, ViewHolder holder) {
		try {
			holder.vehicle_name.setText(item.getVehicle_name());
			holder.describe.setText(UnixTimeUtil.getYear(item.getRegister_time()) + "." + UnixTimeUtil.getMonth(item.getRegister_time())
					+ "上牌，" + item.getMiles() + "万公里，" + item.getGearbox());
			holder.online_time.setText(UnixTimeUtil.format(item.getOnline_time()));
			holder.price.setText(GlobalData.resourceHelper.getString(R.string.hc_wan, item.getSeller_price()));
			holder.match_count.setText(String.valueOf(item.getMatched_buyer_count()));


			String[] cover_image_urls = item.getCover_image_urls();
			if (cover_image_urls != null && cover_image_urls.length == 3) {
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, cover_image_urls[0])).
						placeholder(R.drawable.vc_image_default).into(holder.image1);
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, cover_image_urls[1])).
						placeholder(R.drawable.vc_image_default).into(holder.image2);
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, cover_image_urls[2])).
						placeholder(R.drawable.vc_image_default).into(holder.image3);
			} else {
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, item.getImage_url())).
						placeholder(R.drawable.vc_image_default).into(holder.image1);
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, item.getImage_url())).
						placeholder(R.drawable.vc_image_default).into(holder.image2);
				Picasso.with(this.mContext).load(GlobalData.resourceHelper.getString(R.string.rc_icon_url, item.getImage_url())).
						placeholder(R.drawable.vc_image_default).into(holder.image3);
			}

			holder.vehicle_name.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Map<String, Object> map = new HashMap<>();
					map.put("url", "http://m.haoche51.com/details/" + item.getVehicle_source_id() + ".html");
					ThirdPartInjector.onEvent((Activity) mContext, ThirdPartyConst.RECOMMEND_VEHICLE_DETAIL);
					HCActionUtil.launchActivity(mContext, HCWebViewActivity.class, map);

				}
			});
			holder.llMatchCount.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//跳转到匹配客户
					Intent intent = new Intent(mContext, MatchCustomerListActivity.class);
					intent.putExtra(MatchCustomerListActivity.KEY_INTENT_EXTRA_VEHICLE, item);
					ThirdPartInjector.onEvent((Activity) mContext, ThirdPartyConst.RECOMMEND_VEHICLE_CUSTOMER);
					mContext.startActivity(intent);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回Holder
	 */
	private ViewHolder getHolder(View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		return holder;
	}

	/**
	 * 控件索引存储器
	 */
	class ViewHolder {
		ImageView image1, image2, image3;
		TextView vehicle_name, describe, price, match_count, online_time;
		LinearLayout llMatchCount;

		public ViewHolder(View convertView) {
			image1 = (ImageView) convertView.findViewById(R.id.iv_vehicle_img1);
			image2 = (ImageView) convertView.findViewById(R.id.iv_vehicle_img2);
			image3 = (ImageView) convertView.findViewById(R.id.iv_vehicle_img3);
			vehicle_name = (TextView) convertView.findViewById(R.id.tv_vehicle_model);
			describe = (TextView) convertView.findViewById(R.id.tv_vehicle_desc);
			price = (TextView) convertView.findViewById(R.id.tv_seller_price);
			match_count = (TextView) convertView.findViewById(R.id.tv_match_cus_count);
			online_time = (TextView) convertView.findViewById(R.id.tv_online_time);
			llMatchCount = (LinearLayout) convertView.findViewById(R.id.ll_match_customer);

		}
	}
}

