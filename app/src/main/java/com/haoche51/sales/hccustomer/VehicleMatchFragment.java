package com.haoche51.sales.hccustomer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.BuyerEntity;
import com.haoche51.sales.hctransaction.IntendedVehicleAdapter;
import com.haoche51.sales.hctransaction.IntendedVehicleEntity;
import com.haoche51.sales.hctransaction.VehicleSubScribeConditionActivity;
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
 * 车源匹配
 * Created by PengXianglin on 16/11/22.
 */
public class VehicleMatchFragment extends CommonBaseFragment implements HCPullToRefresh.OnRefreshCallback, AdapterView.OnItemClickListener, View.OnClickListener {

	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;
	private TextView tv_subscribe, tv_modify_subscribe;

	private String phone, name;
	private List<VehicleMatchEntity> mList;
	private VehicleMatchFragmentAdapter mAdapter;
	private List<BuyerEntity.SubscribeRuleEntity.SubscribeSeriesEntity> subscribeSeries;
	private List<Float> price;
	private int pageIndex = 0;//当前页面索引

	public VehicleMatchFragment() {
	}

	public VehicleMatchFragment(String phone, String name, List<BuyerEntity.SubscribeRuleEntity.SubscribeSeriesEntity> subscribeSeries, List<Float> price) {
		this.phone = phone;
		this.name = name;
		this.subscribeSeries = subscribeSeries;
		this.price = price;
	}

	@Override
	protected int getContentView() {
		return R.layout.fragment_vehicle_match;
	}

	@Override
	protected void initView(View view) {
		tv_subscribe = (TextView) view.findViewById(R.id.tv_subscribe);
		tv_modify_subscribe = (TextView) view.findViewById(R.id.tv_modify_subscribe);
		tv_modify_subscribe.setOnClickListener(this);
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setOnItemClickListener(this);

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
		mPullToRefresh.setFirstAutoRefresh();//获取数据

		if (mList == null)
			mList = new ArrayList<>();
		if (mAdapter == null) {
			mAdapter = new VehicleMatchFragmentAdapter(this.getActivity(), mList, R.layout.item_match_vehicle);
			mListView.setAdapter(mAdapter);
		}
		if (mList.isEmpty())
			showLoadingView(false);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		setSubscribeSeries(this.subscribeSeries, this.price);
	}

	public void setSubscribeSeries(List<BuyerEntity.SubscribeRuleEntity.SubscribeSeriesEntity> subscribeSeries, List<Float> price) {
		this.subscribeSeries = subscribeSeries;
		this.price = price;

		if (this.subscribeSeries != null && !this.subscribeSeries.isEmpty()) {
			StringBuilder str = new StringBuilder();
			for (BuyerEntity.SubscribeRuleEntity.SubscribeSeriesEntity subscribeSery : this.subscribeSeries) {
				str.append(subscribeSery.getName());
				str.append(";");
			}
			tv_subscribe.setText(str.toString());
		}

		StringBuilder str = new StringBuilder(tv_subscribe.getText().toString());
		if (this.price != null && this.price.size() == 2) {
			str.append(this.price.get(0));
			str.append("~");
			str.append(this.price.get(1));
			str.append("万");
			tv_subscribe.setText(str.toString());
		}

		if (TextUtils.isEmpty(str)) {
			tv_subscribe.setText("---暂无订阅,可点击下方添加点阅车辆---");
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (null != getActivity() && (getActivity().isFinishing() || getActivity().isDestroyed())) {
			return;
		}
		dismissLoadingView();
		switch (response.getErrno()) {
			case 0:
				List<VehicleMatchEntity> entities = HCJsonParse.parseVehicleMatchList(response.getData());
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
		if (v.getId() == tv_modify_subscribe.getId()) {
			//修改订阅
			Map<String, Object> map = new HashMap<>();
			if (!TextUtils.isEmpty(phone)) {
				map.put(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_PHONE, phone);
				map.put(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_NAME, name);
				HCActionUtil.launchActivity(this.getActivity(), VehicleSubScribeConditionActivity.class, map);
			} else {
				ToastUtil.showInfo(getString(R.string.lack_phone));
			}
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
		AppHttpServer.getInstance().post(HCHttpRequestParam.getMatchedVehicleList(phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT), this, HttpConstants.GET_LIST_REFRESH);
	}

	@Override
	public void onLoadMoreRefresh() {
		//当前页面索引值++
		pageIndex += 1;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getMatchedVehicleList(phone, pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT), this, HttpConstants.GET_LIST_LOADMORE);
	}
}
