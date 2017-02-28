package com.haoche51.sales.hctransaction;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.HCConst;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.custom.PlayerView;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCStatistic;
import com.haoche51.sales.util.HCUtils;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 车主录音、买家录音
 * Created by mac on 15/11/11.
 */
public class PhoneRecordListFragment extends CommonBaseFragment implements View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private PlayerView playerView;

    private List<PhoneRecordEntity> mList = new ArrayList<>();
    private PhoneRecordAdapter mAdapter;
    private boolean mLoadEnd = false;
    private int pageIndex = 0;//当前页面索引
    private String phone;//根据电话查询
    private String taskId;//根据电话查询

    private int taskType;

    public PhoneRecordListFragment() {
    }

    public static PhoneRecordListFragment newInstance(String phone, String taskId, int taskType) {
        PhoneRecordListFragment fragment = new PhoneRecordListFragment();
        fragment.setPhoneNumber(phone);
        fragment.setTaskId(taskId);
        fragment.setTaskType(taskType);
        return fragment;
    }

    public void setPhoneNumber(String phone) {
        this.phone = phone;
    }

    public String getPhoneNumber() {
        return this.phone;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_phone_record_list;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showInfo(getString(R.string.lack_parameter));
        }

        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.hc_self_gray_bg)));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));

        playerView = (PlayerView) view.findViewById(R.id.play_view_activity_listen_record);

        //初始化播放器
        playerView.setMediaPlayer(new MediaPlayer());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mAdapter = new PhoneRecordAdapter(GlobalData.mContext, mList, new PhoneRecordAdapter.PlayListener() {
            @Override
            public void play(String url) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (getTaskType() == TaskConstants.MESSAGE_VIEW_TASK) {// crm 接口只接受看车任务统计，验车任务会报错
                            HCStatistic.readyClick(StringUtil.parseInt(getTaskId(), 0), HCConst.ReadyInfo.READYINFO_TYPE_VOICE_BS
                                    , HCUtils.getNetType(), System.currentTimeMillis(), 1);
                        }
                    }
                });

                playerView.playUrl(url);
                playerView.setVisibility(View.VISIBLE);
            }
        });
        mListView.setAdapter(mAdapter);

        dismissResultView(true);
        showLoadingView(true);
        loadData();
    }

    /**
     * 加载list数据
     */
    private void loadData() {
        pageIndex = 0;
        mLoadEnd = false;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getPhoneRecordList(pageIndex, TaskConstants.DEFAULT_SHOWTASK_COUNT,
                phone, null), this, HttpConstants.GET_LIST_REFRESH);
    }

    /**
     * 加载下一页list数据
     */
    private void loadMore() {
        //当前页面索引值++
        pageIndex += 1;
        mLoadEnd = true;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getPhoneRecordList(pageIndex, TaskConstants.DEFAULT_SHOWTASK_COUNT,
                phone, null), this, HttpConstants.GET_LIST_LOADMORE);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        dismissLoadingView();
        switch (response.getErrno()) {
            case 0:
                List<PhoneRecordEntity> entities = HCJsonParse.parseRecordLists(response.getData());
                //刷新
                if (requestId == HttpConstants.GET_LIST_REFRESH) {
                    mList.clear();
                    if (entities != null) {
                        mList.addAll(entities);
                    }
                } else {//加载更多
                    //把后来加载的添加进来
                    if (entities != null)
                        mList.addAll(entities);
                }

                //是否还有更多
                boolean isNoMoreData = false;
                if (entities == null || entities.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT) {
                    isNoMoreData = true;
                }
                mPullToRefresh.setFooterStatus(isNoMoreData);

                if (mList.size() == 0) {
                    //没有数据
                    mPullToRefresh.hideFooter();
                    showNoDataView(false, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
                }
                //停止刷新
                mPullToRefresh.finishRefresh();
                mAdapter.notifyDataSetChanged();
                break;
            default:
                //停止刷新
                mPullToRefresh.finishRefresh();
                ToastUtil.showInfo(response.getErrmsg());
                if (requestId == HttpConstants.GET_LIST_REFRESH) {
                    showErrorView(true, "网络异常", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadData();
                        }
                    });
                }
                break;
        }
    }

    public void pausePlay() {
        if (playerView != null) {
            playerView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playerView != null) {
            playerView.playerDestory();
        }
    }

    @Override
    public void onClick(View view) {
        //当空数据列表时会回调此方法
        if (mPullToRefresh != null) {
            dismissResultView(true);
            showLoadingView(false);
            onPullDownRefresh();//刷新重新拉取数据
        }
    }

    @Override
    public void onPullDownRefresh() {
        if (!TextUtils.isEmpty(phone)) {
            //条件判断通过，允许刷新，重置参数，请求刷新；
            loadData();
        }
        //重置录音播放器
        playerView.stop();
        playerView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadMoreRefresh() {
        loadMore();
    }
}
