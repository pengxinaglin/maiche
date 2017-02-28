package com.haoche51.sales.hcmessage;

import android.content.Context;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseadapter.HCCommonAdapter;
import com.haoche51.sales.hcbaseadapter.HCCommonViewHolder;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.List;

/**
 * Created by xuhaibo on 15/9/12.
 */
public class MessageAdapter extends HCCommonAdapter<MessageEntity> {
	public MessageAdapter(Context context, List<MessageEntity> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void fillViewData(HCCommonViewHolder holder, int position) {
		MessageEntity entity = getItem(position);

		holder.findTheView(R.id.ll_msg_item).setBackgroundColor(mContext.getResources().getColor(entity.getStatus() == 0
				? R.color.self_white : R.color.gray));
		TextView message_title = holder.findTheView(R.id.message_title);
		if (entity.getStatus() == 0) {
			message_title.setTextColor(mContext.getResources().getColor(R.color.hc_indicator));
		}
		holder.setTextViewText(R.id.message_title, entity.getTitle());
		holder.setTextViewText(R.id.message_detail, entity.getDescription());
		String create_time = UnixTimeUtil.relativeStyle(entity.getCreate_time());
		holder.setTextViewText(R.id.message_date, create_time);
	}
}
