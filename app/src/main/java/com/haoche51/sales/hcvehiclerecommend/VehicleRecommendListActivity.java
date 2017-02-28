package com.haoche51.sales.hcvehiclerecommend;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.hcbaseactivity.WebPermissoinBaseActivity;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.util.HCActionUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class VehicleRecommendListActivity extends CommonStateFragmentActivity {

    @Override
    protected int getTitleView() {
        return R.layout.title_bar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_recommend_vehicle;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.inject(this);
        registerTitleBack();
        setScreenTitle(R.string.hc_vehicle_recommend_activity_title);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.recommend_vehicle_container, new VehicleRecommendListFragment());
        ft.commitAllowingStateLoss();
    }

}
