package com.haoche51.sales.hccustomer;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.TransactionRecordAdapter;
import com.haoche51.sales.hctransaction.TransactionRecordEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 看车记录
 * Created by PengXianglin on 16/11/22.
 */
public class TransRecordFragment extends CommonBaseFragment implements HCPullToRefresh.OnRefreshCallback, View.OnClickListener {


	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;

	private List<TransactionRecordEntity> historyInfos;
	private TransactionRecordAdapter mAdapter;

	private String buyer_phone;
	private int pageIndex = 0;//当前页面索引

	public TransRecordFragment() {
	}

	public TransRecordFragment(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	@Override
	protected int getContentView() {
		return R.layout.fragment_trans_record;
	}

	@Override
	protected void initView(View view) {
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setDivider(null);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		if (historyInfos == null)
			historyInfos = new ArrayList<>();

		if (mAdapter == null)
			mAdapter = new TransactionRecordAdapter(this.getActivity(), historyInfos, R.layout.item_trans_history);
		//设置数据
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		//自动刷新
		mPullToRefresh.setFirstAutoRefresh();

		if (historyInfos.isEmpty())
			showLoadingView(false);
	}

	@Override
	public void onPullDownRefresh() {
		pageIndex = 0;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerTransHistory(buyer_phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT), this, HttpConstants.GET_LIST_REFRESH);
	}

	@Override
	public void onLoadMoreRefresh() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerTransHistory(buyer_phone, pageIndex++,
				TaskConstants.DEFAULT_SHOWTASK_COUNT), this, HttpConstants.GET_LIST_LOADMORE);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (null != getActivity() && (getActivity().isFinishing() || getActivity().isDestroyed())) {
			return;
		}
		onListHttpResponse(response, requestId);
	}

	private void onListHttpResponse(HCHttpResponse response, int requestId) {
		try {
			dismissLoadingView();
			switch (response.getErrno()) {
				case 0:
					List<TransactionRecordEntity> transHistoryList = HCJsonParse.parseBuyerTransHistoryInfos(response.getData());
					//刷新
					if (requestId == HttpConstants.GET_LIST_REFRESH && transHistoryList != null) { //刷新
						historyInfos.clear();
						historyInfos.addAll(transHistoryList);
					} else if (requestId == HttpConstants.GET_LIST_LOADMORE && transHistoryList != null) {//加载更多
						//把后来加载的添加进来
						historyInfos.addAll(transHistoryList);
					} else {//数据解析出错
						showErrorView(true, this);
					}
					//是否还有更多
					if (transHistoryList != null) {
						boolean isNoMoreData = transHistoryList.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
						mPullToRefresh.setFooterStatus(isNoMoreData);
					}
					//重置数据
					mAdapter.notifyDataSetChanged();
					//当没有数据时显示默认空白页
					if (historyInfos.size() == 0) {
						mPullToRefresh.hideFooter();
						showNoDataView(true, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
					} else {
						dismissResultView(true);//有数据，显示出来数据列表页面
					}
					break;
				default:
					mPullToRefresh.setFooterStatus(false);
					ToastUtil.showInfo(response.getErrmsg());
					if (historyInfos.size() == 0) {
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
	public void onClick(View view) {
		//当空数据列表时会回调此方法
		if (mPullToRefresh != null) {
			dismissResultView(true);
			showLoadingView(false);
			onPullDownRefresh();//刷新重新拉取数据
		}
	}
}
