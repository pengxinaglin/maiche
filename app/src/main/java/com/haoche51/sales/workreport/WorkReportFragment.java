package com.haoche51.sales.workreport;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkReportFragment extends CommonBaseFragment implements HCHttpCallback,
        HCPullToRefresh.OnRefreshCallback, View.OnClickListener {

    public static final String ACACHE_WOKR_REPORT = "work_report";

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private View mHeaderView;
    private TextView trans_all_text;
    private TextView trans_succ_text;
    private TextView trans_fail_text;
    private TextView reborn_text;
    private TextView revisit_text;
    private TextView succ_rate_text;

    private ACache mAcache;
    private List<WorkReportContentEntity> mContentList;
    private WorkReportAdapter mAdapter;
    private int mPageIndex = 0;

    @Override
    protected int getContentView() {
        return R.layout.workreport_fragment_main;
    }

    /**
     * 初始化界面
     */
    @Override
    protected void initView(View view) {
        super.initView(view);
        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.hc_self_gray_bg)));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));

        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.workreport_list_header, null);
        trans_all_text = (TextView) mHeaderView.findViewById(R.id.trans_all_text);
        trans_succ_text = (TextView) mHeaderView.findViewById(R.id.trans_succ_text);
        trans_fail_text = (TextView) mHeaderView.findViewById(R.id.trans_fail_text);
        reborn_text = (TextView) mHeaderView.findViewById(R.id.reborn_text);
        revisit_text = (TextView) mHeaderView.findViewById(R.id.revisit_text);
        succ_rate_text = (TextView) mHeaderView.findViewById(R.id.succ_rate_text);
        mListView.addHeaderView(mHeaderView);
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData(Bundle savedInstanceState) {
        // 初始化ACache
        if (mAcache == null) {
            mAcache = ACache.get(getActivity());
        }
        if (mContentList == null) {
            mContentList = new ArrayList<>();
        }
        //读取ACache中的缓存记录
        String json = mAcache.getAsString(ACACHE_WOKR_REPORT);
        WorkReportEntity workReportEntity = null;
        if (!TextUtils.isEmpty(json)) {
            workReportEntity = HCJsonParse.parseWorkReportDetail(json);
            showWorkReportCount(workReportEntity);
            if (workReportEntity.getReply() != null && workReportEntity.getReply().size() > 0) {
                mContentList.addAll(workReportEntity.getReply());
            }
        }

        if (mAdapter == null) {
            mAdapter = new WorkReportAdapter(getActivity(), mContentList);
            mListView.setAdapter(mAdapter);
        }

        if (!TextUtils.isEmpty(json)) {
            mPullToRefresh.setFirstAutoRefresh();
        } else {
            dismissResultView(true);
            showLoadingView(false);
            onPullDownRefresh();
        }
    }

    @Override
    public void onPullDownRefresh() {
        mPageIndex = 0;
        Map<String, Object> params = HCHttpRequestParam.getReportDetail(mPageIndex, TaskConstants.DEFAULT_SHOWTASK_COUNT);
        AppHttpServer.getInstance().post(params, this, HttpConstants.GET_LIST_REFRESH);
    }

    @Override
    public void onLoadMoreRefresh() {
        mPageIndex += 1;
        Map<String, Object> params = HCHttpRequestParam.getReportDetail(mPageIndex, TaskConstants.DEFAULT_SHOWTASK_COUNT);
        AppHttpServer.getInstance().post(params, this, HttpConstants.GET_LIST_LOADMORE);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }

        dismissLoadingView();
        try {
            switch (response.getErrno()) {
                case 0:
                    WorkReportEntity entity = HCJsonParse.parseWorkReportDetail(response.getData());
                    if (requestId == HttpConstants.GET_LIST_REFRESH) {
                        showWorkReportCount(entity);

                        // 只缓存刷新的数据
                        if (response.getData() != null) {
                            mAcache.put(ACACHE_WOKR_REPORT, response.getData());
                        }

                        mContentList.clear();
                    }

                    // 停止刷新
                    mPullToRefresh.finishRefresh();

                    // 是否还有更多
                    boolean isNoMoreData = false;
                    if (entity == null || entity.getReply() == null || entity.getReply().size() < TaskConstants.DEFAULT_SHOWTASK_COUNT) {
                        isNoMoreData = true;
                    }
                    mPullToRefresh.setFooterStatus(isNoMoreData);

                    if (entity != null && entity.getReply() != null && entity.getReply().size() > 0) {
                        mContentList.addAll(entity.getReply());
                        mAdapter.notifyDataSetChanged();
                        dismissResultView(true);
                    } else {
                        if (mContentList == null || mContentList.size() == 0) {
                            mPullToRefresh.hideFooter();
                        }
                    }
                    break;
                default:
                    if (requestId == HttpConstants.GET_LIST_REFRESH) {
                        showErrorView(false, this);
                    }
                    break;
            }
        } catch (Exception e) {
            if (requestId == HttpConstants.GET_LIST_REFRESH) {
                showErrorView(false, this);
            }
        }
    }

    /**
     * 显示工作汇报对应的数据
     */
    private void showWorkReportCount(WorkReportEntity entity) {
        if (entity == null) {
            return;
        }

        WorkReportCountEntity countEntity = entity.getTotal();
        if (countEntity == null) {
            return;
        }

        trans_all_text.setText(countEntity.getTrans_all());
        trans_succ_text.setText(countEntity.getTrans_succ());
        trans_fail_text.setText(countEntity.getTrans_fail());
        reborn_text.setText(countEntity.getReborn());
        revisit_text.setText(countEntity.getRevisit());
        succ_rate_text.setText(countEntity.getSucc_rate() + "%");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WorkReportActivity.REQUEST_CODE_SEND_WORK_REPORT) {
            // 发送工作汇报后，回到首页刷新数据
            mListView.setSelection(0);
            // mElasticListView.smoothScrollToPosition(0);
            onPullDownRefresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (mPullToRefresh != null) {
            dismissResultView(true);
            showLoadingView(false);
            onPullDownRefresh();
        }
    }
}
