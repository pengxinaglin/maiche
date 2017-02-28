package com.haoche51.sales.hccustomer;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransaction.BuyerEntity;
import com.haoche51.sales.hctransaction.BuyerListAdapter;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户列表
 * Created by PengXianglin on 16/11/21.
 */

public class CustomerListActivity extends CommonStateActivity implements HCPullToRefresh.OnRefreshCallback, AdapterView.OnItemClickListener, View.OnClickListener {

	public static final String INTENT_EXTRA_LIST_TYPE = "intent_extra_list_type";

	@ViewInject(R.id.pull_to_refresh)
	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;
	private List<BuyerEntity> mTasks = new ArrayList<BuyerEntity>();
	private BuyerListAdapter mAdapter = null;
	private int pageIndex = 0;//当前页面索引
	private int search_type;
	private ACache mAcache;

	@Override
	protected int getContentView() {
		return R.layout.acitivity_customer_list;
	}

	@Override
	protected void initView() {
		super.initView();
		search_type = getIntent().getIntExtra(INTENT_EXTRA_LIST_TYPE, 0);
		switch (search_type) {
			case TaskConstants.REQUEST_CUSTOMER_LIST_MINE:
				setScreenTitle(getString(R.string.hc_customer_title_mine));
				break;
			case TaskConstants.REQUEST_CUSTOMER_LIST_RECYCLED:
				setScreenTitle(getString(R.string.hc_customer_title_recycled));
				break;
			case TaskConstants.REQUEST_CUSTOMER_LIST_NOPUSH:
				setScreenTitle(getString(R.string.hc_customer_title_nopush));
				break;
			case TaskConstants.REQUEST_CUSTOMER_LIST_NODOOR:
				setScreenTitle(getString(R.string.hc_customer_title_nodoor));
				break;
			case TaskConstants.REQUEST_CUSTOMER_LIST_DOOR:
				setScreenTitle(getString(R.string.hc_customer_title_door));
				break;
			case TaskConstants.REQUEST_CUSTOMER_LIST_RECOMMENDED:
				setScreenTitle(getString(R.string.hc_customer_title_recommended));
				break;
		}

		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
		mListView.setDividerHeight(DisplayUtils.dip2px(this, 10));
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected void initData() {
		//初始化ACache
		if (mAcache == null)
			mAcache = ACache.get(this);
		//读取ACache中的缓存记录
		String json = mAcache.getAsString(TaskConstants.ACACHE_CUSTOMER_DEFAULT);
		if (!TextUtils.isEmpty(json)) {
			this.mTasks.clear();
			this.mTasks.addAll(HCJsonParse.parseGetTransFailBuyerEntitys(json));
		}
		if (mAdapter == null)
			mAdapter = new BuyerListAdapter(this, this.mTasks, R.layout.item_customer);
		//设置数据
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		//自动刷新
		mPullToRefresh.setFirstAutoRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		try {
			Map<String, Object> map = new HashMap<>();
			//带看失败页面
			map.put("phone", this.mTasks.get(position).getPhone());
			HCActionUtil.launchActivity(this, CustomerDetailActivity.class, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownRefresh() {
		pageIndex = 0;
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransFailBuyerList(pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, null, 0, null, search_type), this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
	}

	@Override
	public void onLoadMoreRefresh() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransFailBuyerList(++pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, null, 0, null, search_type), this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (this.isDestroyed() && this.isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_GET_TRANS_FAIL_BUYER_LIST)) {
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
					List<BuyerEntity> viewTaskList = HCJsonParse.parseGetTransFailBuyerEntitys(response.getData());
					//刷新
					if (requestId == HttpConstants.GET_LIST_REFRESH && viewTaskList != null) { //刷新
						if (!TextUtils.isEmpty(response.getData())) {
							mAcache.put(TaskConstants.ACACHE_CUSTOMER_DEFAULT, response.getData());//只缓存刷新的数据
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
					if (mTasks.size() == 0) {
						showErrorView(true, this);
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
