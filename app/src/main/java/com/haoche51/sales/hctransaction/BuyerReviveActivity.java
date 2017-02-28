package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 复活买家
 */
public class BuyerReviveActivity extends CommonStateActivity implements AdapterView.OnItemClickListener, View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

    @ViewInject(R.id.lv_buyer_resurgence)
    HCPullToRefresh mPullToRefresh; //买家车型列表

    ListView mListView;
    @ViewInject(R.id.tv_activation_buyer)
    TextView tv_revive; // 激活买家button


    private List<TransactionTaskShortEntity> mTransList = new ArrayList<TransactionTaskShortEntity>();
    private int pageIndex = 0; //分页button

    private BuyerReviveAdapter mAdapter;

    private TransactionFitlerEntity filterEntity;


    @Override
    protected int getContentView() {
        return R.layout.layout_buyer_resurgence;
    }

    @Override
    protected void initView() {
        super.initView();

        setScreenTitle("复活买家");

        String buyer_phone = this.getIntent().getStringExtra("buyer_phone");
        if (buyer_phone == null) {
            Toast.makeText(this, "买家电话为空！", Toast.LENGTH_LONG).show();
            return;
        }

        mListView = mPullToRefresh.getListView();
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
        mListView.setDividerHeight(DisplayUtils.dip2px(BuyerReviveActivity.this, 10));
        mListView.setOnItemClickListener(this);
        tv_revive.setOnClickListener(this);
        //自动刷新
        mPullToRefresh.setFirstAutoRefresh();
        mAdapter = new BuyerReviveAdapter(this, mTransList, R.layout.item_please_select_vehicle_model);
        mListView.setAdapter(mAdapter);
        //设置单选
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //初始化数据
        filterEntity = new TransactionFitlerEntity();
        filterEntity.setType(TaskConstants.TRANSACTION_FAILED); //带看失败的任务
        filterEntity.setBuyer_phone(buyer_phone);
        //设置单选

    }


    @Override
    public void onClick(View v) {
        if (mAdapter.getSelectePosition() < 0) {
            Toast.makeText(this, "请选择激活车源！", Toast.LENGTH_LONG).show();
            return;
        }
        TransactionTaskShortEntity entity = mTransList.get(mAdapter.getSelectePosition());
        AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(entity.getId()), this, 0);
        ProgressDialogUtil.showProgressDialog(this, "请稍后");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setSelectePosition(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPullDownRefresh() {
        pageIndex = 0;
        AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, filterEntity), BuyerReviveActivity.this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
    }

    @Override
    public void onLoadMoreRefresh() {
        AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(++pageIndex,
                TaskConstants.DEFAULT_SHOWTASK_COUNT, filterEntity), BuyerReviveActivity.this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
    }

    /**
     * 网络请求返回
     *
     * @param action    当前请求action
     * @param requestId
     * @param response  hc 请求结果
     * @param error     网络问题造成failed 的error
     */
    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }
        super.onHttpComplete(action, requestId, response, error);
        if (action.equals(HttpConstants.ACTION_TRANSACTION_FILTER)) {
            onListRefreshResponse(response, requestId);
        }
        if (action.equals(HttpConstants.ACTION_GET_TRANSACTION_BY_ID)) {
            onRequestDetail(response);
        }
    }

    /**
     * 获取详情，并跳转
     *
     * @param response
     */
    private void onRequestDetail(HCHttpResponse response) {
        ProgressDialogUtil.closeProgressDialog();

        if (response.getErrno() != 0) {
            Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_LONG).show();
            return;
        }

        TransactionTaskEntity entity;
        try {
            Gson mGson = new Gson();
            entity = mGson.fromJson(response.getData(), TransactionTaskEntity.class);
        } catch (Exception e) {
            entity = null;
        }
        if (entity == null) {
            Toast.makeText(this, "错误的带看信息", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(BuyerReviveActivity.this, TransactionPrepayActivity.class);
        intent.putExtra("taskEntity", entity);
        startActivityForResult(intent, TaskConstants.PREPAY_TRANSACTION);
    }

    /**
     * 列表刷新页面
     *
     * @param response
     * @param requestId
     */
    private void onListRefreshResponse(HCHttpResponse response, int requestId) {
        switch (response.getErrno()) {
            case 0:
                List<TransactionTaskShortEntity> temps = HCJsonParse.parseGetTransTaskShortEntitys(response.getData());
                if (requestId == HttpConstants.GET_LIST_REFRESH) {
                    if (temps != null) {
                        mTransList.clear();
                        mTransList.addAll(temps);
                        mAdapter.setSelectePosition(-1);
                    }
                } else {
                    if (temps != null) {
                        mTransList.addAll(temps);
                    }
                }
                for (int i = 0; i < mTransList.size(); i++) {
                    if (mTransList.get(i).getVehicle_status() != 3) {
                        mTransList.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
                //是否还有更多
                boolean isNoMoreData = mTransList.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
                mPullToRefresh.setFooterStatus(isNoMoreData);
                if (mTransList.size() == 0) {
                    mPullToRefresh.hideFooter();
                } else {
                }
                break;
            default:
                mPullToRefresh.setFooterStatus(false);
                ToastUtil.showInfo(response.getErrmsg());
                break;
        }
        mPullToRefresh.finishRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onPullDownRefresh();
        }
    }
}
