package com.haoche51.sales.hcshare;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haoche51.sales.util.ToastUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by PengXianglin on 16/8/17.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.PhotoViewHolder> {

	private LayoutInflater inflater;
	private Context mContext;
	private List<ImageInfoEntity> data;
	private List<String> checkeds;
	private int maxCount;
	private CheckedImageListener checkedImageListener;

	public ImageAdapter(Context mContext, List<ImageInfoEntity> data, int maxCount, CheckedImageListener checkedImageListener) {
		this.mContext = mContext;
		this.data = data;
		this.maxCount = maxCount;
		this.checkedImageListener = checkedImageListener;
		inflater = LayoutInflater.from(mContext);
		checkeds = new LinkedList<>();
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	@Override
	public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = inflater.inflate(me.iwf.photopicker.R.layout.item_photo, parent, false);
		PhotoViewHolder holder = new PhotoViewHolder(itemView);
		return holder;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

		if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
			return;
		}

		Glide.with(mContext)
				.load(data.get(position).getUrl())
				.centerCrop()
				.thumbnail(0.1f)
				.placeholder(me.iwf.photopicker.R.drawable.ic_photo_black_48dp)
				.error(me.iwf.photopicker.R.drawable.ic_broken_image_black_48dp)
				.into(holder.ivPhoto);

		holder.vSelected.setSelected(data.get(position).isChecked());

		holder.vSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ImageInfoEntity infoEntity = data.get(position);
				boolean checked = !infoEntity.isChecked();
				if (checked && checkeds.size() >= maxCount) {
					ToastUtil.showInfo("最多可以选8张~");
					return;
				}

				if (checked) {
					checkeds.add(infoEntity.getUrl());
				} else {
					checkeds.remove(infoEntity.getUrl());
				}

				if (checkedImageListener != null) {
					checkedImageListener.onChecked(checkeds.size(), maxCount);
				}

				infoEntity.setChecked(checked);
				notifyItemChanged(position);
			}
		});
	}

	public static class PhotoViewHolder extends RecyclerView.ViewHolder {
		private ImageView ivPhoto;
		private View vSelected;

		public PhotoViewHolder(View itemView) {
			super(itemView);
			ivPhoto = (ImageView) itemView.findViewById(me.iwf.photopicker.R.id.iv_photo);
			vSelected = itemView.findViewById(me.iwf.photopicker.R.id.v_selected);
		}
	}

	public List<String> getCheckeds() {
		return checkeds;
	}

	public interface CheckedImageListener {
		void onChecked(int currentCount, int totalCount);
	}
}

