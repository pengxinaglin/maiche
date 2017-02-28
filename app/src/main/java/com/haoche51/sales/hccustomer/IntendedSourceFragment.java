package com.haoche51.sales.hccustomer;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.IntendedVehicleAdapter;
import com.haoche51.sales.hctransaction.IntendedVehicleEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意向车源
 * Created by PengXianglin on 16/11/22.
 */
public class IntendedSourceFragment extends CommonBaseFragment implements HCPullToRefresh.OnRefreshCallback, AdapterView.OnItemClickListener, View.OnClickListener {

	@ViewInject(R.id.pull_to_refresh)
	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;

	private String phone;
	private List<IntendedVehicleEntity> mList;
	private IntendedVehicleAdapter mAdapter;
	private int pageIndex = 0;//当前页面索引

	public IntendedSourceFragment() {
	}

	public IntendedSourceFragment(String phone) {
		this.phone = phone;
	}

	@Override
	protected int getContentView() {
		return R.layout.fragment_intended_source;
	}

	@Override
	protected void initView(View view) {
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setOnItemClickListener(this);
		mPullToRefresh.setFirstAutoRefresh();//获取数据

		if (mList == null)
			mList = new ArrayList<>();
		if (mAdapter == null) {
			mAdapter = new IntendedVehicleAdapter(this.getActivity(), mList, R.layout.item_intended_vehicle);
			mListView.setAdapter(mAdapter);
		}

		if (mList.isEmpty())
			showLoadingView(false);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (getActivity().isFinishing() || getActivity().isDestroyed()) {
			return;
		}
		dismissLoadingView();
		switch (response.getErrno()) {
			case 0:
				List<IntendedVehicleEntity> entities = HCJsonParse.parseDemandList(response.getData());
				//刷新
				if (requestId == HttpConstants.GET_LIST_REFRESH && entities != null) {
					mList.clear();
					mList.addAll(entities);
				} else if (requestId == HttpConstants.GET_LIST_LOADMORE && entities != null) {//加载更多
					//把后来加载的添加进来
					mList.addAll(entities);
				} else {
					showErrorView(false, this);
				}
				//是否还有更多
				if (entities != null) {
					boolean isNoMoreData = entities.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
					mPullToRefresh.setFooterStatus(isNoMoreData);
				}
				//停止刷新
				mPullToRefresh.finishRefresh();
				//重置数据
				mAdapter.notifyDataSetChanged();
				//当没有数据时显示默认空白页
				if (mList.size() == 0) {
					mPullToRefresh.hideFooter();
					showNoDataView(false, getString(R.string.hc_common_result_nodata), this);
				} else {
					dismissResultView(true);//有数据，显示出来数据列表页面
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
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		if (mList != null && mList.size() > 0 && i >= 0 && i < mList.size()) {
			Map<String, Object> map = new HashMap<>();
			StringBuilder url = new StringBuilder("http://m.haoche51.com/details/");
			url.append(mList.get(i).getVehicle_source_id());
			url.append(".html");
			map.put("url", url.toString());
			HCActionUtil.launchActivity(this.getActivity(), HCWebViewActivity.class, map);
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

	@Override
	public void onPullDownRefresh() {
		pageIndex = 0;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getDemandList(phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, null), this, HttpConstants.GET_LIST_REFRESH);
	}

	@Override
	public void onLoadMoreRefresh() {
		//当前页面索引值++
		pageIndex += 1;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getDemandList(phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, null), this, HttpConstants.GET_LIST_LOADMORE);
	}
}
