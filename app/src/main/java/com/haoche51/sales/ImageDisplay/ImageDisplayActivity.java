package com.haoche51.sales.ImageDisplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.custom.HackyViewPager;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by yangming on 2016/5/30.
 */
public class ImageDisplayActivity extends CommonStateFragmentActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    @ViewInject(R.id.pager)
    private HackyViewPager mPager;
    private int pagerPosition;

    @Override
    protected int getContentView() {
        return R.layout.image_display_activity;
    }

    @Override
    protected void initView() {
        super.initView();
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
                .getAdapter().getCount());
//        indicator.setText(text);
        setScreenTitle(text.toString());
        // 更新下标
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
//                indicator.setText(text);
                setScreenTitle(text.toString());
            }
        });
        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public String[] fileList;

        public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.length;
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList[position];
            return ImageDisplayFragment.newInstance(url);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
