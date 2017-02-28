package com.haoche51.sales.workreport;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.photoview.PhotoView;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ImageUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FullscreenImageActivity extends CommonStateFragmentActivity {

    public static final String KEY_NOT_SHOW_RIGHT_TITLE = "is_show_right_title";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_IMAGE_FILE_PATH = "image_file_path";

    @ViewInject(R.id.right_text_view)
    private TextView right_text_view;
    private AlbumViewPager mViewPager;

    private boolean mIsShowRightTitle = false;
    private String mImageUrl;
    private String mImagePath;
    private Vector<String> mUriArray;

    @Override
    protected int getTitleView() {
        return R.layout.title_bar;
    }

    @Override
    protected int getContentView() {
        return R.layout.workreport_activity_photo_image;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.inject(this);
        registerTitleBack();
        setScreenTitle("");

        mViewPager = (AlbumViewPager) findViewById(R.id.view_pager);
    }

    @Override
    public void initData() {
        super.initData();
        mIsShowRightTitle = getIntent().getBooleanExtra(KEY_NOT_SHOW_RIGHT_TITLE, false);
        mImageUrl = getIntent().getStringExtra(KEY_IMAGE_URL);
        mImagePath = getIntent().getStringExtra(KEY_IMAGE_FILE_PATH);
        mUriArray = new Vector<String>();
        mUriArray.add(mImagePath);

        if (mIsShowRightTitle) {
            right_text_view.setText(getString(R.string.p_finish));
            right_text_view.setVisibility(View.VISIBLE);
        }
        MyPagerAdapter adapter = new MyPagerAdapter(mUriArray);
        mViewPager.setAdapter(adapter);
    }

    /**
     * 图片页面适配器
     */
    class MyPagerAdapter extends PagerAdapter {

        private List<String> mDataList = new ArrayList<String>();

        public MyPagerAdapter(Vector<String> dataList) {
            super();
            mDataList = dataList;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(FullscreenImageActivity.this);
            View view = inflater.inflate(R.layout.layout_fullscreen_image, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.loadImg = (ImageView) view.findViewById(R.id.loadImg);
            viewHolder.photoView = (PhotoView) view.findViewById(R.id.imgShow);
            viewHolder.progressLay = view.findViewById(R.id.progressLay);
            view.setTag(viewHolder);
            viewHolder.photoView.setVisibility(View.GONE);
            viewHolder.loadImg.setVisibility(View.GONE);
            viewHolder.progressLay.setVisibility(View.VISIBLE);
            viewHolder.url = mDataList.get(position);
            showData(view, viewHolder.url);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    static final class ViewHolder {
        String url;
        ImageView loadImg;
        PhotoView photoView;
        View progressLay;
    }


    private void showData(View view, String url) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            Object obj = view.getTag();
            if (obj != null && obj instanceof ViewHolder) {
                ViewHolder vh = (ViewHolder) obj;
                if (!TextUtils.isEmpty(mImagePath)) {
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(mImagePath,
                            DisplayUtils.getScreenWidthInPixels(),
                            DisplayUtils.getScreenHeightPixels() - DisplayUtils.dip2px(FullscreenImageActivity.this, 87));
                    if (bitmap != null) {
                        vh.progressLay.setVisibility(View.GONE);
                        vh.loadImg.setVisibility(View.GONE);
                        vh.photoView.setVisibility(View.VISIBLE);
                        vh.photoView.setImageBitmap(bitmap);
                    }
                } else if (!TextUtils.isEmpty(mImageUrl)) {
                    vh.progressLay.setVisibility(View.GONE);
                    vh.loadImg.setVisibility(View.GONE);
                    vh.photoView.setVisibility(View.VISIBLE);
                    Picasso picasso = Picasso.with(FullscreenImageActivity.this);
                    RequestCreator requestCreator = picasso.load(mImageUrl);
                    requestCreator.placeholder(R.drawable.image_default_load);
                    requestCreator.into(vh.photoView);
                } else {
                    vh.progressLay.setVisibility(View.GONE);
                    vh.photoView.setVisibility(View.GONE);
                    vh.loadImg.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 点击发表
     */
    @OnClick(R.id.right_text_view)
    private void onAddClick(View view) {
        Intent intent = new Intent(FullscreenImageActivity.this, WorkReportSendActivity.class);
        intent.putExtra(KEY_IMAGE_FILE_PATH, mImagePath);
        startActivityForResult(intent, WorkReportActivity.REQUEST_CODE_SEND_WORK_REPORT);
        finish();
    }
}
