package com.haoche51.sales.hctransfer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.haoche51.sales.ImageDisplay.ImageDisplayActivity;
import com.haoche51.sales.R;
import com.haoche51.sales.util.BitmapUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 15/9/11.
 */
public class TransferPhotoViewAdapter extends RecyclerView.Adapter<TransferPhotoViewAdapter.PhotoViewHolder> {

    private List<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;
    private Context mContext;
    public OnPhotoListChangedListener onPhotoListChangedListener;
    public boolean isView;
    public int viewCount;

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public TransferPhotoViewAdapter(Context mContext, List<String> photoPaths, OnPhotoListChangedListener onPhotoListChangedListener) {
        if (photoPaths != null) {
            this.photoPaths = photoPaths;
        }
        this.mContext = mContext;
        this.onPhotoListChangedListener = onPhotoListChangedListener;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_transfer_selected_photo_view, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (position == this.photoPaths.size()) {//显示一个“+”
//      ImageLoaderHelper.displayImage("drawable://" + R.drawable.transaction_append, holder.ivPhoto);
            Picasso.with(mContext)
                    .load(R.drawable.transaction_append)
                    .placeholder(R.drawable.transaction_append)
                    .error(R.drawable.transaction_append)
                    .into(holder.ivPhoto);

            holder.ivDelete.setVisibility(View.GONE);

        } else {//加载普通照片
            String path = photoPaths.get(position);
            if (!TextUtils.isEmpty(path)) {
                if (path.startsWith("http")) {
//          ImageLoaderHelper.displayNormalImage(path, holder.ivPhoto);
                    Picasso.with(mContext)
                            .load(DisplayUtils.getImageUrl(path, 150, 150))
                            .placeholder(R.drawable.default_template)
                            .error(R.drawable.default_template)
                            .into(holder.ivPhoto);
                } else {
                    Uri uri = Uri.fromFile(new File(path));
//          ImageLoaderHelper.displayNormalImage("uri://" + path, holder.ivPhoto);
                    holder.ivPhoto.setImageBitmap(BitmapUtil.decodeSampledBitmapFromFd(path, 150, 150));//缩略图
//          Picasso.with(mContext)
//            .load(uri)
//            .placeholder(R.drawable.default_template)
//            .error(R.drawable.default_template)
//            .into(holder.ivPhoto);
                }

                if (isView) {
                    holder.ivDelete.setVisibility(View.GONE);
                } else {
                    if (position < viewCount) {
                        holder.ivDelete.setVisibility(View.GONE);
                    } else {
                        holder.ivDelete.setVisibility(View.VISIBLE);
                    }
                }

            }
        }

        //按看大图
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != photoPaths.size()) {//不是“+”这张图
//          Intent intent = new Intent(mContext, PhotoPagerActivity.class);
//          intent.putExtra(PhotoPagerActivity.EXTRA_PHOTO_PATH, photoPaths.get(position));
//          mContext.startActivity(intent);
                    Intent intent = new Intent(mContext, ImageDisplayActivity.class);
                    intent.putExtra(ImageDisplayActivity.EXTRA_IMAGE_URLS, (String[]) photoPaths.toArray(new String[photoPaths.size()]));
                    intent.putExtra(ImageDisplayActivity.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                } else if (position == photoPaths.size()) {
                    //选择图片
                    if (onPhotoListChangedListener != null) {
                        onPhotoListChangedListener.onSelectPhoto(view);
                    }
                }
            }
        });

        //点击红色“-”删除当前这张图片
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != photoPaths.size()) {//不是“+”这张图
                    if (onPhotoListChangedListener != null) {
                        onPhotoListChangedListener.onDeletePhoto(position, "");
                    }
                    //刷新界面
                    TransferPhotoViewAdapter.this.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return isView ? this.photoPaths.size() : this.photoPaths.size() + 1;//如果是删除，不显示最后的“+”号图片
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private ImageView ivDelete;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_transfer_photo);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_transfer_photo_delete);
        }
    }


    public void refreshData(List<String> photoPaths) {
        this.photoPaths = photoPaths;
        notifyDataSetChanged();
    }

    public interface OnPhotoListChangedListener {
        void onSelectPhoto(View view);

        void onViewPhoto(int position);

        void onDeletePhoto(int position, String tag);

    }
}