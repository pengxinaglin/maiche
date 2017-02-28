package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * 车源回访（车主回访）
 * Created by mac on 15/9/26.
 */
public class VehicleRevisitAdapter extends ArrayAdapter<VehicleRevisitEntity> {

	public VehicleRevisitAdapter(Context context, List<VehicleRevisitEntity> mlist) {
		super(context, 0, mlist);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		if (convertView == null)
			convertView = View.inflate(this.getContext(),
				R.layout.layout_vehicle_revisit_item, null);

		ViewHolder holder = getHolder(convertView);

		setData(getItem(position), holder);
		return convertView;
	}

	private void setData(VehicleRevisitEntity item, ViewHolder holder) {
		holder.tv_item_vehicle_saler_name.setText(item.getCustom_service()+ "["+ item.getGroup_name() +"]");//TODO 角色暂时未知
		holder.tv_item_vehicle_revisit_time.setText(UnixTimeUtil.format(item.getTime()));
		String content = item.getRecord();
		if (!TextUtils.isEmpty(content) && content.length() > 50)//最多显示50个字
			content = content.substring(0, 50) + "...";//截取50个字
		holder.tv_item_vehicle_content.setText(content);
	}


	private ViewHolder getHolder(View convertView) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null)
			holder = new ViewHolder(convertView);
		convertView.setTag(holder);
		return holder;
	}


	class ViewHolder {
		TextView tv_item_vehicle_saler_name, tv_item_vehicle_revisit_time, tv_item_vehicle_content;

		public ViewHolder(View convertView) {
			tv_item_vehicle_saler_name = (TextView) convertView.findViewById(R.id.tv_item_vehicle_saler_name);
			tv_item_vehicle_revisit_time = (TextView) convertView.findViewById(R.id.tv_item_vehicle_revisit_time);
			tv_item_vehicle_content = (TextView) convertView.findViewById(R.id.tv_item_vehicle_content);
		}
	}
}
