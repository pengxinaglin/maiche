package com.haoche51.sales.workreport;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ImageUrlUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class WorkReportAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<WorkReportContentEntity> mContentList;

    public WorkReportAdapter(Context context, List<WorkReportContentEntity> contentEntities) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mContentList = contentEntities;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (mContentList == null) {
            return 0;
        }
        if (mContentList.size() == 0) {
            return 1;
        }

        return mContentList.size();
    }

    @Override
    public WorkReportContentEntity getItem(int i) {
        if (mContentList == null || mContentList.size() == 0) {
            return null;
        }
        if (i < 0) {
            i = 0;
        }
        if (i >= mContentList.size()) {
            i = mContentList.size() - 1;
        }

        return mContentList.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 没有数据时
        if (mContentList == null || mContentList.size() == 0) {
            TextView textView = (TextView) View.inflate(mContext, R.layout.list_view_no_data, null);
            return textView;
        }

        WorkReportContentEntity contentEntity = getItem(position);
        if (convertView == null || TextView.class.isInstance(convertView)) {
            convertView = View.inflate(mContext, R.layout.work_report_message_item, null);
        }
        ViewHolder viewHolder = getHolder(convertView);
        setData(contentEntity, viewHolder);

        return convertView;
    }

    /**
     * 返回Holder
     */
    private ViewHolder getHolder(View convertView) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        return viewHolder;
    }

    /**
     * 控件索引存储器
     */
    class ViewHolder {

        TextView timeView;
        TextView contentView;
        TextView placeView;
        ImageView imageView;
        View dividerLine;
        LinearLayout containerLayout;

        public ViewHolder(View convertView) {
            timeView = (TextView) convertView.findViewById(R.id.time_view);
            contentView = (TextView) convertView.findViewById(R.id.content_view);
            placeView = (TextView) convertView.findViewById(R.id.place_view);
            imageView = (ImageView) convertView.findViewById(R.id.reply_image_view);
            dividerLine = convertView.findViewById(R.id.divider_line);
            containerLayout = (LinearLayout) convertView.findViewById(R.id.work_report_item_container);
        }
    }

    /**
     * 设置数据
     */
    private void setData(final WorkReportContentEntity entity, ViewHolder holder) {
        try {
            holder.timeView.setText(entity.getCreate_time());
            holder.contentView.setText(entity.getContent());
            holder.placeView.setText(entity.getPlace());

            String imageUrl = ImageUrlUtil.getNewUrl(entity.getPhoto_url(),
                    DisplayUtils.dip2px(mContext, 80), DisplayUtils.dip2px(mContext, 80));
            Picasso picasso = Picasso.with(this.mContext);
            RequestCreator requestCreator = picasso.load(imageUrl);
            requestCreator.placeholder(R.drawable.image_default_load);
            requestCreator.into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FullscreenImageActivity.class);
                    intent.putExtra(FullscreenImageActivity.KEY_IMAGE_URL, entity.getPhoto_url());
                    mContext.startActivity(intent);
                }
            });

            holder.dividerLine.setVisibility(View.INVISIBLE);
            holder.containerLayout.setVisibility(View.GONE);
            holder.containerLayout.removeAllViews();
            List list = entity.getReply();
            if (list != null && list.size() > 0) {
                holder.dividerLine.setVisibility(View.VISIBLE);
                holder.containerLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < list.size(); i++) {
                    WorkReportReplyEntity replyEntity = (WorkReportReplyEntity) list.get(i);
                    TextView replyView = (TextView) mLayoutInflater.inflate(R.layout.work_report_reply_item, null);
                    String repleyStr = addHtmlText(replyEntity.getCrm_user_name(), ": " + replyEntity.getContent());
                    replyView.setText(Html.fromHtml(repleyStr));
                    holder.containerLayout.addView(replyView);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 添加Html文本
     */
    public String addHtmlText(String key, String text) {
        return "<font color='#FF3333'>" + key + "</font>" + text;
    }
}

