package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransfer.TransTasksAdapter;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 看车列表
 * Created by mac on 15/12/1.
 */
public class TransactionTaskFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private TransactionFitlerEntity fitlerEntity;//根据条件加载数据
    private List<TransactionTaskShortEntity> mTasks = new ArrayList<TransactionTaskShortEntity>();
    private TransTasksAdapter mAdapter = null;
    private ImageButton floatingActionButton;
    private int pageIndex = 0;//当前页面索引
    private ACache mAcache;

    @Override
    protected int getContentView() {
        return R.layout.fragment_trans_task;
    }

    @Override
    protected void initView(View view) {
        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
        mListView.setOnItemClickListener(this);
        floatingActionButton = (ImageButton) view.findViewById(R.id.floatingactionbutton);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (this.fitlerEntity == null) {
            this.fitlerEntity = new TransactionFitlerEntity();
            this.fitlerEntity.setType(TaskConstants.TRANSACTION_PENDING);//0待看车1失败 2成功
        }
        //初始化ACache
        if (mAcache == null) {
            mAcache = ACache.get(getActivity());
        }
        //读取ACache中的缓存记录
        String json = mAcache.getAsString(TaskConstants.ACACHE_TRANSACTION_DEFAULT);
        if (!TextUtils.isEmpty(json)) {
            this.mTasks.clear();
            this.mTasks.addAll(HCJsonParse.parseGetTransTaskShortEntitys(json));
        }
        if (mAdapter == null) {
            mAdapter = new TransTasksAdapter(getActivity(), mTasks, R.layout.item_transaction);
        }
        //设置数据
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
        }
        //自动刷新
        mPullToRefresh.setFirstAutoRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mTasks == null || mTasks.size() == 0 || id == -1) {
            return;
        }

        Intent intent = new Intent(getActivity(), TransactionTaskDetailActivity.class);
        intent.putExtra(TransactionTaskDetailActivity.KEY_ID, mTasks.get(position).getId());
        startActivityForResult(intent, TransactionMainActivity.REQUEST_CODE_AUTO_REFRESH);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        if (HttpConstants.ACTION_TRANSACTION_FILTER.equals(action)) {
            dismissLoadingView();
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
        switch (response.getErrno()) {
            case 0://请求成功
                List<TransactionTaskShortEntity> viewTaskList = HCJsonParse.parseGetTransTaskShortEntitys(response.getData());
                if (requestId == HttpConstants.GET_LIST_REFRESH && viewTaskList != null) { //刷新
                    if (!TextUtils.isEmpty(response.getData())) {
                        mAcache.put(TaskConstants.ACACHE_TRANSACTION_DEFAULT, response.getData());//只缓存刷新的数据
                    }
                    mTasks.clear();
                    mTasks.addAll(viewTaskList);
                } else if (requestId == HttpConstants.GET_LIST_LOADMORE && viewTaskList != null) {//加载更多
                    //把后来加载的添加进来
                    mTasks.addAll(viewTaskList);
                } else {//数据解析出错
                    showErrorView(true, this);
                }
                //是否还有更多
                if (viewTaskList != null) {
                    boolean isNoMoreData = viewTaskList.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
                    mPullToRefresh.setFooterStatus(isNoMoreData);
                }
                //重置数据
                mAdapter.notifyDataSetChanged();
                //当没有数据时显示默认空白页
                if (mTasks.size() == 0) {
                    mPullToRefresh.hideFooter();
                    showNoDataView(true, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
                } else {
                    dismissResultView(true);//有数据，显示出来数据列表页面
                }
                break;
            default:
                mPullToRefresh.setFooterStatus(false);
                ToastUtil.showInfo(response.getErrmsg());
                //无网络视图
                if (mTasks.size() == 0) {
                    showErrorView(true, this);
                }
                break;
        }
        //停止刷新
        mPullToRefresh.finishRefresh();
    }

    @Override
    public void doingFilter(Bundle where) {
        //加载网络筛选
        if (fitlerEntity != null)
            fitlerEntity.setSearch_field(where.getString("search_field", null));
        if (mPullToRefresh != null)
            mPullToRefresh.setFirstAutoRefresh();//刷新
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatingactionbutton) {
            // 加带看
            Intent intent = new Intent(getActivity(), TakeLookActivity.class);
            intent.putExtra(TakeLookActivity.EXTRA_IS_SIMILAR, false);
            startActivityForResult(intent, TransactionTaskDetailActivity.REQUEST_CODE_TAKE_LOOK);
//            HCActionUtil.startActivity(getActivity(), WebPermissoinBaseActivity.class, HCHttpRequestParam.addTransactionTask());
        } else {
            //当空数据列表时会回调此方法
            if (mPullToRefresh != null) {
                dismissResultView(true);
                showLoadingView(false);
                onPullDownRefresh();//刷新重新拉取数据
            }
        }
    }

    @Override
    public void onPullDownRefresh() {
        pageIndex = 0;
        AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity), this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
    }

    @Override
    public void onLoadMoreRefresh() {
        AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(++pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity), this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TransactionMainActivity.REQUEST_CODE_AUTO_REFRESH
                || requestCode == TransactionTaskDetailActivity.REQUEST_CODE_TAKE_LOOK) {
            // 重新刷新数据
            if (resultCode == Activity.RESULT_OK) {
                if (mListView != null) {
                    mListView.setSelection(0);
                }
                if (mPullToRefresh != null) {
                    mPullToRefresh.setFirstAutoRefresh();
                }
            }
        }
    }
}