package com.haoche51.sales.hccustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.dialog.AlertDialog;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.TransactionHighSeasAdapter;
import com.haoche51.sales.hctransaction.TransactionHighSeasDetailActivity;
import com.haoche51.sales.hctransaction.TransactionHighSeasEntity;
import com.haoche51.sales.hctransaction.TransactionMainActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 公海客户
 * Created by mac on 15/11/20.
 */
public class TransactionHighSeasFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, AlertDialog.OnDismissListener, HCPullToRefresh.OnRefreshCallback {

    private RelativeLayout rl_trans_task_filter_level;//几倍搜索
    private TextView tv_trans_task_filter_level;//显示搜索的级别
    List<Date> selectedDates;//保存已经选择的时间

    private RelativeLayout rl_trans_task_filter_time;//时间搜索
    private TextView tv_trans_task_filter_time;//显示搜索的时间
    private int selectLvel = -1;
    private int start_time = 0;
    private int end_time = 0;
    private String search_field = "";

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private List<TransactionHighSeasEntity> mTasks = new ArrayList<TransactionHighSeasEntity>();
    private TransactionHighSeasAdapter mAdapter = null;

    private int pageIndex = 0;//当前页面索引
    private ACache mAcache;
    private int mClickIndex;

    @Override
    protected int getContentView() {
        return R.layout.transaction_high_seas_fragment;
    }

    @Override
    protected void initView(View view) {
//		tv_titlebar_totalcount = (TextView) view.findViewById(R.id.tv_titlebar_totalcount);
        rl_trans_task_filter_time = (RelativeLayout) view.findViewById(R.id.rl_trans_task_filter_time);
        rl_trans_task_filter_time.setOnClickListener(this);
        tv_trans_task_filter_time = (TextView) view.findViewById(R.id.tv_trans_task_filter_time);

        rl_trans_task_filter_level = (RelativeLayout) view.findViewById(R.id.rl_trans_task_filter_level);
        rl_trans_task_filter_level.setOnClickListener(this);
        tv_trans_task_filter_level = (TextView) view.findViewById(R.id.tv_trans_task_filter_level);

        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);

        final LinearLayout relEmpty = (LinearLayout) view.findViewById(R.id.ll_list_empty);
        TextView textView = (TextView) relEmpty.findViewById(R.id.result_txt);
        textView.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.common_button_nodata, 0, 0);
        textView.setText(getString(R.string.hc_common_result_nodata));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPullToRefresh.setFirstAutoRefresh();//重新拉取数据
            }
        });
        mPullToRefresh.setEmptyView(relEmpty);

        mListView = mPullToRefresh.getListView();
        mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        //初始化ACache
        if (mAcache == null)
            mAcache = ACache.get(getActivity());
        //读取ACache中的缓存记录
        String json = mAcache.getAsString(TaskConstants.ACACHE_CUSTOMER_HIGH);
        if (!TextUtils.isEmpty(json)) {
            this.mTasks.clear();
            this.mTasks.addAll(HCJsonParse.parseGetTransHighSeasEntitys(json));
        }
        if (mAdapter == null)
            mAdapter = new TransactionHighSeasAdapter(getActivity(), mTasks, R.layout.item_customer);
        //设置数据
        if (mListView != null)
            mListView.setAdapter(mAdapter);
        //自动刷新
        mPullToRefresh.setFirstAutoRefresh();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mTasks == null || mTasks.size() == 0 || id == -1) {
            return;
        }

        Intent intent = new Intent(getActivity(), TransactionHighSeasDetailActivity.class);
        intent.putExtra("phone", this.mTasks.get(position).getBuyer_phone());
        startActivityForResult(intent, CustomerMainActivity.REQUEST_CODE_HIGH_SEAS);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (getActivity() != null && getActivity().isFinishing()) {
            return;
        }
        if (action.equals(HttpConstants.ACTION_GET_TRANS_HIGH_SEAS_LIST)) {
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
            dismissLoadingView();
            switch (response.getErrno()) {
                case 0:
                    List<TransactionHighSeasEntity> viewTaskList = HCJsonParse.parseGetTransHighSeasEntitys(response.getData());
                    //刷新
                    if (requestId == HttpConstants.GET_LIST_REFRESH && viewTaskList != null) { //刷新
                        if (!TextUtils.isEmpty(response.getData())) {
                        }
                        mAcache.put(TaskConstants.ACACHE_CUSTOMER_HIGH, response.getData());//只缓存刷新的数据
                        mTasks.clear();
                        mTasks.addAll(viewTaskList);
                    } else if (requestId == HttpConstants.GET_LIST_LOADMORE && viewTaskList != null) {//加载更多
                        //把后来加载的添加进来
                        mTasks.addAll(viewTaskList);
                    } else {//数据解析出错
                        showErrorView(false, this);
                    }
                    //是否还有更多
                    if (viewTaskList != null) {
                        boolean isNoMoreData = viewTaskList.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
                        mPullToRefresh.setFooterStatus(isNoMoreData);
                    }
                    if (mTasks.size() == 0) {
                        mPullToRefresh.hideFooter();
                    }
                    //重置数据
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    mPullToRefresh.setFooterStatus(false);
                    ToastUtil.showInfo(response.getErrmsg());
                    if (mTasks.size() == 0) {
                        showErrorView(false, this);
                    }
                    break;
            }
            //停止刷新
            mPullToRefresh.finishRefresh();
        } catch (Exception e) {
            e.printStackTrace();
            if (mTasks.size() == 0) {
                showErrorView(false, this);
            }
        }
    }

    @Override
    public void doingFilter(Bundle where) {
        //加载网络筛选
        if (where != null) {
            search_field = where.getString("search_field", null);
        } else {
            search_field = "";
        }
        if (mPullToRefresh != null)
            mPullToRefresh.setFirstAutoRefresh();//刷新
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_trans_task_filter_time:
                //弹出选择时间对话框
                if (selectedDates == null || selectedDates.size() == 0)
                    AlertDialog.showSelectDateDialog(getActivity(), 0, 0, this);
                else
                    AlertDialog.showSelectDateDialog(getActivity(), selectedDates.get(0).getTime(),
                            selectedDates.get(selectedDates.size() - 1).getTime(), this);
                break;
            case R.id.rl_trans_task_filter_level:
                HCDialog.TransactionFilterLevel(getActivity(), selectLvel, new HCDialog.OnLevelConfirmListener() {
                    @Override
                    public void isClick(int level) {
                        selectLvel = level;
                        if (level == -1) {
                            tv_trans_task_filter_level.setText(GlobalData.resourceHelper.getString(R.string.hc_trans_task_filter_level_hint));
                        } else {
                            tv_trans_task_filter_level.setText(ControlDisplayUtil.getHighSeasLevel(level));
                        }
                        if (mPullToRefresh != null) {
                            mPullToRefresh.setFirstAutoRefresh();//刷新
                        }
                    }
                });
                break;
            default:
                //当空数据列表时会回调此方法
                if (mPullToRefresh != null) {
                    showLoadingView(false);
                    dismissResultView(true);
                    mPullToRefresh.setFirstAutoRefresh();//重新拉取数据
                }
                break;
        }
    }

    /**
     * 选择时间会回调此方法
     */
    @Override
    public void onDismiss(Bundle data) {
        if (data != null) {
            //更新时间
            selectedDates = (List<Date>) data.getSerializable("selectedDates");
            long startTimeTemp = 0;
            long endTimeTemp = 0;
            if (selectedDates != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDates.get(0));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                startTimeTemp = calendar.getTime().getTime();

                calendar.setTime(selectedDates.get(selectedDates.size() - 1));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                endTimeTemp = calendar.getTime().getTime();
            }
            if (selectedDates != null && tv_trans_task_filter_time != null) {
                tv_trans_task_filter_time.setText(UnixTimeUtil.formatYearMonthDay((int) (startTimeTemp / 1000L))
                        + " - " + UnixTimeUtil.formatYearMonthDay((int) (endTimeTemp / 1000L)));
            }
            start_time = (int) (startTimeTemp / 1000L);
            end_time = (int) (endTimeTemp / 1000L);

        } else {
            //清掉时间
            if (tv_trans_task_filter_time != null)
                tv_trans_task_filter_time.setText(GlobalData.resourceHelper.getString(R.string.hc_trans_task_filter_time));
            if (selectedDates != null) {
                selectedDates.clear();
            }
            start_time = 0;
            end_time = 0;
        }
        if (mPullToRefresh != null)
            mPullToRefresh.setFirstAutoRefresh();//刷新
    }

    @Override
    public void onPullDownRefresh() {
        pageIndex = 0;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getTransHighSeasList(pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, selectLvel, start_time, end_time, search_field)
                , TransactionHighSeasFragment.this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
    }

    @Override
    public void onLoadMoreRefresh() {
        AppHttpServer.getInstance().post(HCHttpRequestParam.getTransHighSeasList(++pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, selectLvel, start_time, end_time, search_field)
                , TransactionHighSeasFragment.this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CustomerMainActivity.REQUEST_CODE_HIGH_SEAS) {
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