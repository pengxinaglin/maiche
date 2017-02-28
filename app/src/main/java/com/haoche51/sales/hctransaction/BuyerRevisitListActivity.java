package com.haoche51.sales.hctransaction;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.custom.HCPullToRefresh;
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
 * 买家回访记录
 * * Created by mac on 15/12/3.
 */
public class BuyerRevisitListActivity extends CommonStateActivity implements AdapterView.OnItemClickListener, HCPullToRefresh.OnRefreshCallback, View.OnClickListener {


	@ViewInject(R.id.pull_to_refresh)
	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;

	private List<BuyerRevisitEntity> buyerRevisitEntityInfos;
	private BuyerRevisitAdapter mAdapter;

	private String buyer_phone, vehicle_source_id;
	private int level;
	private String changeLevel;

	private int pageIndex = 0;//当前页面索引

	@Override
	protected int getTitleView() {
		return R.layout.layout_common_titlebar_add;
	}

	@Override
	protected int getContentView() {
		return R.layout.activity_buyer_revisit;
	}

	@Override
	protected void initView() {
		super.initView();
		registerTitleRightFunction();//设置titleBar右侧点击
		setScreenTitle(R.string.buyer_revisit);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setOnItemClickListener(this);
	}

	/**
	 * 注册titleBar右侧点击
	 */
	private void registerTitleRightFunction() {
		View view = findViewById(R.id.tv_right_fuction);
		if (view != null) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					addRevisit();//添加回访
				}
			});
		}
	}

	@Override
	protected void initData() {
		level = getIntent().getIntExtra("level", 3);
		changeLevel = getIntent().getStringExtra("changeLevel");
		vehicle_source_id = getIntent().getStringExtra("vehicle_source_id");
		buyer_phone = getIntent().getStringExtra("buyer_phone");

		if (buyerRevisitEntityInfos == null)
			buyerRevisitEntityInfos = new ArrayList<>();

		if (mAdapter == null)
			mAdapter = new BuyerRevisitAdapter(this, buyerRevisitEntityInfos, R.layout.item_buyer_revisit);

		mListView.setAdapter(mAdapter);

		showLoadingView(false);
		mPullToRefresh.autoRefresh();
	}

	/**
	 * 添加回访
	 */
	public void addRevisit() {
		Map<String, Object> map = new HashMap<>();
		map.put("buyer_phone", this.buyer_phone);
		if (TextUtils.isEmpty(vehicle_source_id))
			vehicle_source_id = "0";//默认为0
		map.put("vehicle_source_id", vehicle_source_id);
		if (!TextUtils.isEmpty(changeLevel)) {
			map.put("level", level);
			map.put("changeLevel", changeLevel);
		}
		HCActionUtil.launchActivity(this, RevisitAddActivity.class, map);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing() || isDestroyed()) {
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
			if (!TextUtils.isEmpty(buyerRevisitEntityInfos.get(i).getVehicle_name())) {
				Map<String, Object> map = new HashMap<>();
				map.put("url", "http://m.haoche51.com/details/" + buyerRevisitEntityInfos.get(i).getVehicle_source_id() + ".html");
				HCActionUtil.launchActivity(this, HCWebViewActivity.class, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void onClick(View view) {
		//当空数据列表时会回调此方法
		if (mPullToRefresh != null) {
			dismissResultView(true);
			showLoadingView(false);
			onPullDownRefresh();//刷新重新拉取数据
		}
	}
}
