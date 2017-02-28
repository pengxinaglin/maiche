package com.haoche51.sales.hctransaction;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ListView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车源（车主）回访记录
 * * Created by mac on 15/12/2.
 */
public class VehicleRevisitListActivity extends CommonStateActivity {//implements AdapterView.OnItemClickListener {

    List<VehicleRevisitEntity> vehicleRevisitinfoEntities = new ArrayList<>();
    VehicleRevisitAdapter mAdapter;

    @ViewInject(R.id.pull_to_refresh)
    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;

    boolean onlyViewId;//是否只查看一个id车源
    private String vehicle_source_id, seller_name, seller_phone;//车源ID、买家、车主电话


    @Override
    protected int getContentView() {
        return R.layout.activity_vehicle_revisit;
    }

    @Override
    protected int getTitleView() {
        return R.layout.layout_common_titlebar_add;
    }

    @Override
    protected void initView() {
        super.initView();
        registerTitleRightFunction();//设置titleBar右侧点击
        setScreenTitle(R.string.hc_vehicle_revisit_activity_title);

        mPullToRefresh.setCanPull(false);//不能下拉刷新，接口暂不支持分页加载
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.hc_self_gray_bg)));
        mListView.setDividerHeight(DisplayUtils.dip2px(this, 10));
    }

    /**
     * 注册titleBar右侧点击
     */
    private void registerTitleRightFunction() {
        View view = findViewById(R.id.tv_right_fuction);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    addRevisit();//添加回访
                }
            });
        }
    }

    @Override
    protected void initData() {
        if (vehicleRevisitinfoEntities == null)
            vehicleRevisitinfoEntities = new ArrayList<>();

        if (mAdapter == null)
            mAdapter = new VehicleRevisitAdapter(this, vehicleRevisitinfoEntities);

        mListView.setAdapter(mAdapter);
//    mListView.setOnItemClickListener(this);

        this.seller_phone = getIntent().getStringExtra("seller_phone");
        this.vehicle_source_id = getIntent().getStringExtra("vehicle_source_id");
        seller_name = getIntent().getStringExtra("seller_name");
        onlyViewId = getIntent().getBooleanExtra("onlyViewId", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
//    ProgressDialogUtil.showProgressDialog(this, getString(R.string.hc_loading));
        showLoadingView(false);
        dismissResultView(true);
        AppHttpServer.getInstance().post(HCHttpRequestParam.getSellerRevisit(seller_phone, onlyViewId ? vehicle_source_id : ""), this, 0);
    }

    /**
     * 添加回访
     */
    private void addRevisit() {
        Map<String, Object> map = new HashMap<>();
        map.put("seller_phone", this.seller_phone);
        map.put("vehicle_source_id", vehicle_source_id);
        map.put("seller_name", seller_name);
        HCActionUtil.launchActivity(this, RevisitAddActivity.class, map);
    }


    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }
        dismissLoadingView();
        switch (response.getErrno()) {
            case 0:
                if (response.getData() != null && HCJsonParse.parseVehicleRevisitResult(response.getData()) != null) {
                    vehicleRevisitinfoEntities.clear();
                    vehicleRevisitinfoEntities.addAll(HCJsonParse.parseVehicleRevisitResult(response.getData()));
                }
                if (vehicleRevisitinfoEntities.size() == 0) {
                    showNoDataView(false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLoadingView(false);
                            dismissResultView(true);
                            AppHttpServer.getInstance().post(HCHttpRequestParam.getSellerRevisit(seller_phone,
                                    onlyViewId ? vehicle_source_id : ""), VehicleRevisitListActivity.this, 0);
                        }
                    });
                }
                mPullToRefresh.setFooterStatus(true);
                mAdapter.notifyDataSetChanged();
                break;
            default:
                ToastUtil.showInfo(response.getErrmsg());
                showErrorView(false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingView(false);
                        dismissResultView(true);
                        AppHttpServer.getInstance().post(HCHttpRequestParam.getSellerRevisit(seller_phone,
                                onlyViewId ? vehicle_source_id : ""), VehicleRevisitListActivity.this, 0);
                    }
                });
                break;
        }
    }

//	@Override
//	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//		try {
//			Map<String, Object> map = new HashMap<>();
//			map.put("url", "http://m.haoche51.com/details/" + vehicleRevisitinfoEntities.get(i).getVehicle_source_id() + ".html");
//			UtilsHelper.launchActivity(this, HCWebViewActivity.class, map);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
