package com.haoche51.sales.hctransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 待过户列表
 * Created by yangming on 2016/1/19.
 */
public class TransferTaskFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

    /**
     * 记录当前上拉刷新页码 *
     */
    private int mCurrentPage = 0;

    /**
     * 监听过户状态广播
     */
    private BroadcastReceiver mReceiver;

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private TransferAdapter mAdater;
    private List<TransferSimpleEntity> mTransferData = new ArrayList<TransferSimpleEntity>();

    @Override
    protected int getContentView() {
        return R.layout.fragment_transfer_list;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
        mListView.setOnItemClickListener(this);
        doRegistTransferStatusChanged();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mAdater = new TransferAdapter(mTransferData);
        mListView.setAdapter(mAdater);

        //自动刷新
        mPullToRefresh.setFirstAutoRefresh();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        doUnRegistTransferStatusChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
        //当空数据列表时会回调此方法
        if (mPullToRefresh != null) {
            onPullDownRefresh();//刷新重新拉取数据
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position - 1 < mAdater.getCount()) {
            TransferSimpleEntity clickedEntity = (TransferSimpleEntity) mAdater.getItem(position);
            Intent intent = new Intent(GlobalData.mContext, TransferDetailActivity.class);
            intent.putExtra(TransferDetailActivity.KEY_INTENT_EXTRA_ID, clickedEntity.getTransaction_id());
            startActivity(intent);
        }
    }


    @Override
    public void onPullDownRefresh() {
        mCurrentPage = 0;
        requestFilterTransferData(HCConst.Transfer.HTTP_POST_TAB_TRANFER, ""
                , mCurrentPage, HttpConstants.REQUEST_PAGESIZE, HttpConstants.GET_LIST_REFRESH);
    }

    @Override
    public void onLoadMoreRefresh() {
        requestFilterTransferData(HCConst.Transfer.HTTP_POST_TAB_TRANFER, ""
                , ++mCurrentPage, HttpConstants.REQUEST_PAGESIZE, HttpConstants.GET_LIST_LOADMORE);
    }


    private void doUnRegistTransferStatusChanged() {
        if (getActivity() != null) {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
            }
        }
    }

    private void doRegistTransferStatusChanged() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mReceiver == null) {
                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent != null) {// 刷新列表
                            requestFilterTransferData(HCConst.Transfer.HTTP_POST_TAB_TRANFER, ""
                                    , 0, HttpConstants.REQUEST_PAGESIZE * (mCurrentPage + 1), HttpConstants.GET_LIST_REFRESH);
                            mCurrentPage = 0;
                        }
                    }
                };
            }
            IntentFilter filter = new IntentFilter(HCConst.ACTION_TRANSFER_CHANGED);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }


    private void requestFilterTransferData(int tab, String keyword, int page, int limit, int requestId) {
        AppHttpServer.getInstance().post(HCHttpRequestParam.transferList(tab, keyword, page, limit), this, requestId);
    }


    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        if (action.equals(HttpConstants.ACTION_TRANSFERAPI_GET_LIST)) {
            onListHttpResponse(response, requestId);
        }

    }

    /**
     * 列表页刷新返回解析
     *
     * @param response
     * @param requestId
     */
    private void onListHttpResponse(HCHttpResponse response, int requestId) {
        try {
            switch (response.getErrno()) {
                case 0:
                    List<TransferSimpleEntity> resultData = HCJsonParse.parseTransferListData(response.getData());
                    //刷新
                    if (requestId == HttpConstants.GET_LIST_REFRESH && mTransferData != null) {
                        mTransferData.clear();
                    }
                    //把后来加载的添加进来
                    if (resultData != null) {
                        mTransferData.addAll(resultData);
                    }
                    //是否还有更多
                    boolean isNoMoreData = false;
                    if (resultData == null || resultData.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT) {
                        isNoMoreData = true;
                    }
                    mPullToRefresh.setFooterStatus(isNoMoreData);
                    //重置数据
                    mAdater.notifyDataSetChanged();
                    //当没有数据时显示默认空白页
                    if (mTransferData.size() == 0) {
                        mPullToRefresh.hideFooter();
                        showNoDataView(false, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
                    } else {
                        dismissResultView(true);//有数据，显示出来数据列表页面
                    }
                    break;
                default:
                    mPullToRefresh.setFooterStatus(false);
                    ToastUtil.showInfo(response.getErrmsg());
                    //无网络视图
                    if (mTransferData.size() == 0) {
                        showErrorView(false, this);
                    }
                    break;
            }
            //停止刷新
            mPullToRefresh.finishRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doingFilter(Bundle where) {
        super.doingFilter(where);
        String key = where.getString("search_field");
        mCurrentPage = 0;
        requestFilterTransferData(HCConst.Transfer.HTTP_POST_TAB_TRANFER, key
                , mCurrentPage, HttpConstants.REQUEST_PAGESIZE, HttpConstants.GET_LIST_REFRESH);
    }
}
