package com.haoche51.sales.hccustomer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.BuyerRevisitAdapter;
import com.haoche51.sales.hctransaction.BuyerRevisitEntity;
import com.haoche51.sales.hctransaction.RevisitAddActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回访记录
 * Created by PengXianglin on 16/11/22.
 */
public class CustomerRevisitFragment extends CommonBaseFragment implements HCPullToRefresh.OnRefreshCallback, AdapterView.OnItemClickListener, View.OnClickListener {

	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;
	private ImageButton floatingActionButton;

	private List<BuyerRevisitEntity> buyerRevisitEntityInfos;
	private BuyerRevisitAdapter mAdapter;

	private String buyer_phone;
	private int level;
	private String changeLevel;

	private int pageIndex = 0;//当前页面索引

	public CustomerRevisitFragment() {
	}

	public CustomerRevisitFragment(String buyer_phone, int level, String changeLevel) {
		this.level = level;
		this.changeLevel = changeLevel;
		this.buyer_phone = buyer_phone;
	}

	@Override
	protected int getContentView() {
		return R.layout.fragment_customer_revisit;
	}

	@Override
	protected void initView(View view) {
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setOnItemClickListener(this);
		mListView.setDivider(null);
		floatingActionButton = (ImageButton) view.findViewById(R.id.floatingactionbutton);
		floatingActionButton.setOnClickListener(this);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		if (buyerRevisitEntityInfos == null)
			buyerRevisitEntityInfos = new ArrayList<>();

		if (mAdapter == null)
			mAdapter = new BuyerRevisitAdapter(this.getActivity(), buyerRevisitEntityInfos, R.layout.item_buyer_revisit);
		//设置数据
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		//自动刷新
		mPullToRefresh.setFirstAutoRefresh();

		if (buyerRevisitEntityInfos.isEmpty())
			showLoadingView(false);
	}

	@Override
	public void onPullDownRefresh() {
		pageIndex = 0;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerRevisit(buyer_phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT), this, HttpConstants.GET_LIST_REFRESH);
	}

	@Override
	public void onLoadMoreRefresh() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerRevisit(buyer_phone, pageIndex++,
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
					List<BuyerRevisitEntity> buyerRevisitEntities = HCJsonParse.parseBuyerCallbackResult(response.getData());
					//刷新
					if (requestId == HttpConstants.GET_LIST_REFRESH && buyerRevisitEntities != null) { //刷新
						buyerRevisitEntityInfos.clear();
						buyerRevisitEntityInfos.addAll(buyerRevisitEntities);
					} else if (requestId == HttpConstants.GET_LIST_LOADMORE && buyerRevisitEntities != null) {//加载更多
						//把后来加载的添加进来
						buyerRevisitEntityInfos.addAll(buyerRevisitEntities);
					} else {//数据解析出错
						showErrorView(true, this);
					}
					//是否还有更多
					if (buyerRevisitEntities != null) {
						boolean isNoMoreData = buyerRevisitEntities.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
						mPullToRefresh.setFooterStatus(isNoMoreData);
					}
					//重置数据
					mAdapter.notifyDataSetChanged();
					//当没有数据时显示默认空白页
					if (buyerRevisitEntityInfos.size() == 0) {
						mPullToRefresh.hideFooter();
						showNoDataView(true, GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata), this);
					} else {
						dismissResultView(true);//有数据，显示出来数据列表页面
					}
					break;
				default:
					mPullToRefresh.setFooterStatus(false);
					ToastUtil.showInfo(response.getErrmsg());
					if (buyerRevisitEntityInfos.size() == 0) {
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
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		try {
			if (buyerRevisitEntityInfos.get(i).getVehicle_source_id() > 0) {
				Map<String, Object> map = new HashMap<>();
				StringBuilder url = new StringBuilder("http://m.haoche51.com/details/");
				url.append(buyerRevisitEntityInfos.get(i).getVehicle_source_id());
				url.append(".html");
				map.put("url", url.toString());
				HCActionUtil.launchActivity(this.getActivity(), HCWebViewActivity.class, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == floatingActionButton.getId()) {
			Map<String, Object> map = new HashMap<>();
			map.put("buyer_phone", this.buyer_phone);
			if (!TextUtils.isEmpty(changeLevel)) {
				map.put("level", level);
				map.put("changeLevel", changeLevel);
			}
			HCActionUtil.launchActivity(this.getActivity(), RevisitAddActivity.class, map);
		} else {
			//当空数据列表时会回调此方法
			if (mPullToRefresh != null) {
				dismissResultView(true);
				showLoadingView(false);
				onPullDownRefresh();//刷新重新拉取数据
			}
		}
	}
}
