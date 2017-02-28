package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.util.HCLogUtil;

/**
 * Created by yangming on 2015/12/4.
 */
public class VehicleSubBrandAddActivity extends CommonStateFragmentActivity implements VehicleSubscribeSeriesListFragment.OnSeriesListItemClickListener, VehicleSubscribeSeriesListFragment.OnBackClickListener {

    private BrandFragment brandFragment;

    @Override
    protected int getContentView() {
        return R.layout.activity_vehicle_sub_brand_add;
    }

    @Override
    protected void initView() {
        super.initView();
        registerTitleBack();
        setScreenTitle(R.string.vehicle_sube_brand_add__title);

        FragmentTransaction ft = this.getSupportFragmentManager()
                .beginTransaction();
        brandFragment = new BrandFragment();
        ft.add(R.id.fl_vehicle_sub_brand_add_container, brandFragment);
        ft.commitAllowingStateLoss();

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (BrandFragment.isCarSeriesShowing) {
                brandFragment.hideSeriesFragment();
            } else {
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onSeriesClick(VehicleSeriesEntity seriesEntity) {
        // TODO
        brandFragment.hideSeriesFragment();
        HCLogUtil.e("onSeriesClick");
        Intent intent = new Intent();
        intent.putExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_SERIES, seriesEntity);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onbackClick() {
        brandFragment.hideSeriesFragment();
    }
}
