package com.haoche51.sales.helper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.util.DisplayUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;
import java.util.Set;

public class ImageLoaderHelper {

	protected static ImageLoader mImageLoader;

	static {
		mImageLoader = ImageLoader.getInstance();
		//TODO: 研究合适的memoryCache方案和大小
		//mImageLoader.init();
	}

	/**
	 * 有memoryCache的option
	 */
	private static DisplayImageOptions normalOpts =
			new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_template)
					.showImageForEmptyUri(R.drawable.default_template)
					.showImageOnFail(R.drawable.default_template)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.build();

	private static DisplayImageOptions noMemoryOpts =
			new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_template)
					.showImageForEmptyUri(R.drawable.default_template)
					.showImageOnFail(R.drawable.default_template)
					.cacheInMemory(false)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.build();

	public static void displayImage(String url, ImageView imageView) {
		mImageLoader.displayImage(url, imageView, normalOpts);
	}

	public static void displayNormalImage(String url, ImageView imageView) {
		url = processPre(url, imageView);
		mImageLoader.displayImage(url, imageView, normalOpts);
	}

	public static void displayNoMemoryImage(String url, ImageView imageView) {
		if (imageView != null && !TextUtils.isEmpty(url)) {
			url = processPre(url, imageView);
			mImageLoader.displayImage(url, imageView, noMemoryOpts);
		}
	}

	private static String processPre(String url, ImageView imageView) {
		if (imageView != null && !TextUtils.isEmpty(url)) {
			int preW = imageView.getLayoutParams().width;
			int preH = imageView.getLayoutParams().height;

			String splitor = "?imageView2/";
			if (url.contains(splitor)) {
				splitor = "\\" + splitor;
				url = url.split(splitor)[0];
			}
			if (preW >= 0 && preH >= 0) {
				url = DisplayUtils.convertImageURL(url, preW, preH);
			}
		}
		return url;
	}

	public static DisplayImageOptions justDiskCache = new DisplayImageOptions.Builder().cacheInMemory(false)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

	public static void loadEnterIcons(final TextView tv, String url,
	                                  final HashMap<TextView, Drawable> mTvMaps) {

		if (tv == null || TextUtils.isEmpty(url)) return;

		final Resources mRes = GlobalData.mContext.getResources();

		DisplayMetrics metrics = mRes.getDisplayMetrics();
		int mCurrentDensity = metrics.densityDpi;

		Drawable[] drawables = tv.getCompoundDrawables();
		Drawable topDrawable = drawables[1];

		if (topDrawable == null) return;
		final Rect rect = topDrawable.getBounds();

		int picW = topDrawable.getIntrinsicWidth();
		int picH = topDrawable.getIntrinsicHeight();

		if (mCurrentDensity > DisplayMetrics.DENSITY_XHIGH
				&& mCurrentDensity <= DisplayMetrics.DENSITY_XXHIGH) {
			//xxh
		} else if (mCurrentDensity > DisplayMetrics.DENSITY_HIGH
				&& mCurrentDensity <= DisplayMetrics.DENSITY_XHIGH) {
			//xh
		} else if (mCurrentDensity <= DisplayMetrics.DENSITY_HIGH) {
			//h
			picW = (int) (picW / 2.0F * 1.5F);
			picH = (int) (picH / 2.0F * 1.5F);
		}

		url = url + "?imageView2/2/w/" + picW + "/h/" + picH;

		ImageLoader.getInstance().loadImage(url, justDiskCache, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				if (loadedImage != null) {
					Drawable bmDrawable = new BitmapDrawable(mRes, loadedImage);
					bmDrawable.setBounds(rect);
					mTvMaps.put(tv, bmDrawable);
					seeIfNeedShowTvDrawable(mTvMaps);
				}
			}
		});
	}

	private static void seeIfNeedShowTvDrawable(HashMap<TextView, Drawable> mTvMaps) {

		if (mTvMaps == null) return;

		Set<TextView> mSets = mTvMaps.keySet();
		for (TextView tv : mSets) {
			if (tv == null || mTvMaps.get(tv) == null) {
				return;
			}
		}

		for (TextView tv : mSets) {
			Drawable d = mTvMaps.get(tv);
			tv.setCompoundDrawables(null, d, null, null);
		}
	}

	/**
	 * Banner的option,不进行内存缓存
	 */
	public static DisplayImageOptions getBannerOptions() {
		DisplayImageOptions bannerOptions =
				new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_template)
						.showImageOnFail(R.drawable.default_template)
						.resetViewBeforeLoading(false)
						.cacheOnDisk(true)
						.cacheInMemory(false)
						.imageScaleType(ImageScaleType.EXACTLY)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.build();
		return bannerOptions;
	}

	public static DisplayImageOptions getBannerOptions(int defaultImg) {
		DisplayImageOptions bannerOptions =
				new DisplayImageOptions.Builder().showImageForEmptyUri(defaultImg)
						.showImageOnFail(defaultImg)
						.resetViewBeforeLoading(false)
						.cacheOnDisk(true)
						.cacheInMemory(false)
						.imageScaleType(ImageScaleType.EXACTLY)
						.bitmapConfig(Bitmap.Config.RGB_565)
						.build();
		return bannerOptions;
	}

	public static Bitmap loadImageSync(String url, int width, int height) {
		ImageSize imageSize = new ImageSize(width, height);
		return mImageLoader.loadImageSync(url, imageSize);
	}
}
