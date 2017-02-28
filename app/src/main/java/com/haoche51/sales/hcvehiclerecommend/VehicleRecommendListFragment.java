package com.haoche51.sales.hcvehiclerecommend;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 15/11/19.
 */
public class VehicleRecommendListFragment extends CommonBaseFragment implements HCHttpCallback,
        HCPullToRefresh.OnRefreshCallback, View.OnClickListener {

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;

    private List<VehicleRecommentEntity> mTasks;
    private VehicleRecommentAdapter mAdapter;
    private int pageIndex = 0;


    @Override
    protected int getContentView() {
        return R.layout.pager_base_list;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.hc_self_gray_bg)));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (this.mTasks == null)
            mTasks = new ArrayList<>();
        if (mAdapter == null) {
            mAdapter = new VehicleRecommentAdapter(getActivity(), mTasks);
            mListView.setAdapter(mAdapter);
        }
        dismissResultView(true);
        showLoadingView(false);
        //加载数据
        mPullToRefresh.setFirstAutoRefresh();
    }

    @Override
    public void onPullDownRefresh() {
        pageIndex = 0;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getSubVehicleList(pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, null), this, HttpConstants.GET_LIST_REFRESH);
    }

    @Override
    public void onLoadMoreRefresh() {
        pageIndex += 1;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getSubVehicleList(pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, null), this, HttpConstants.GET_LIST_LOADMORE);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }

        dismissLoadingView();
        switch (response.getErrno()) {
            case 0:
                List<VehicleRecommentEntity> list = HCJsonParse.parseSubVehicleList(response.getData());
                if (requestId == HttpConstants.GET_LIST_REFRESH) {
                    mTasks.clear();
                }

                // 停止刷新
                mPullToRefresh.finishRefresh();

                // 是否还有更多
                boolean isNoMoreData = false;
                if (list == null || list.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT) {
                    isNoMoreData = true;
                }
                mPullToRefresh.setFooterStatus(isNoMoreData);

                if (list != null && list.size() > 0) {
                    mTasks.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    dismissResultView(true);
                } else {
                    if (mTasks == null || mTasks.size() == 0) {
                        mPullToRefresh.hideFooter();
                        showNoDataView(false, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
                    }
                }
                break;
            default:
                ToastUtil.showInfo(response.getErrmsg());
                if (requestId == HttpConstants.GET_LIST_REFRESH) {
                    showErrorView(false, this);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        //当空数据列表时会回调此方法
        if (mPullToRefresh != null) {
            dismissResultView(true);
            showLoadingView(false);
            onPullDownRefresh();//刷新重新拉取数据
        }
    }
}
