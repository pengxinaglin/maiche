package com.haoche51.sales.ImageDisplay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by yangming on 2016/5/30.
 */
public class ImageDisplayFragment extends CommonBaseFragment {

    private ImageView mImageView;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutImage;

    private String mImageUrl;
    private ImageLoader imageLoader;

    public static ImageDisplayFragment newInstance(String imageUrl) {
        final ImageDisplayFragment f = new ImageDisplayFragment();
        final Bundle bundle = new Bundle();
        bundle.putString("url", imageUrl);
        f.setArguments(bundle);
        return f;
    }

    @Override
    protected int getContentView() {
        return R.layout.image_display_fragment;
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        mImageView = (ImageView) view.findViewById(R.id.image);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        linearLayoutImage = (LinearLayout) view.findViewById(R.id.ll_image_desplay_fragment_image);

        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        linearLayoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.feedback_list_screen_error_image)          // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_template)  // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_template)       // 设置图片加载或解码过程中发生错误显示的图片
                .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
                .delayBeforeLoading(1000)
                .cacheInMemory(false)           // default 不缓存至内存
                .cacheOnDisc(false)             // default 不缓存至手机SDCard
                .bitmapConfig(Bitmap.Config.RGB_565) //设置图片的解码类型
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .handler(new Handler())                             // default
                .build();
        if (!TextUtils.isEmpty(mImageUrl)) {
            if (mImageUrl.startsWith("http")) {
                imageLoader.displayImage(mImageUrl, mImageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                       // Picasso.with(getActivity()).load(R.drawable.default_template).centerCrop().into(mImageView);
                        String message = null;
                        switch (failReason.getType()) {
                            case IO_ERROR:
                                message = "下载错误";
                                break;
                            case DECODING_ERROR:
                                message = "图片无法显示";
                                break;
                            case NETWORK_DENIED:
                                message = "网络有问题，无法下载";
                                break;
                            case OUT_OF_MEMORY:
                                message = "图片太大，无法显示";
                                break;
                            case UNKNOWN:
                                message = "未知错误";
                                break;
                        }
                        ToastUtil.showText(message);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                mImageView.setImageBitmap(getLoacalBitmap(mImageUrl));//缩略图
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageLoader.clearMemoryCache();
        imageLoader.clearDiscCache();
    }

    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}
