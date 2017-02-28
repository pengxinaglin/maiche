package com.haoche51.sales.hccustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hctransaction.*;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看车失败
 * Created by mac on 15/11/20.
 */
public class BuyerListFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

	//	private TextView tv_titlebar_totalcount;//显示总数量
	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;
	private RelativeLayout rl_trans_task_filter_sort, rl_trans_task_filter;
	private TransactionFitlerEntity fitlerEntity;//根据条件加载数据
	private List<BuyerEntity> mTasks = new ArrayList<BuyerEntity>();
	private BuyerListAdapter mAdapter = null;
	private ImageButton floatingActionButton;
	private int pageIndex = 0;//当前页面索引

	@Override
	protected int getContentView() {
		return R.layout.fragment_trans_buyer_task;
	}

	@Override
	protected void initView(View view) {
//		tv_titlebar_totalcount = (TextView) view.findViewById(R.id.tv_titlebar_totalcount);
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);
		mListView = mPullToRefresh.getListView();
		mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
		mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
		mListView.setOnItemClickListener(this);
		floatingActionButton = (ImageButton) view.findViewById(R.id.floatingactionbutton);
		floatingActionButton.setOnClickListener(this);
		//排序
		rl_trans_task_filter_sort = (RelativeLayout) view.findViewById(R.id.rl_trans_task_filter_sort);
		rl_trans_task_filter_sort.setOnClickListener(this);
		//筛选
		rl_trans_task_filter = (RelativeLayout) view.findViewById(R.id.rl_trans_task_filter);
		rl_trans_task_filter.setOnClickListener(this);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		if (this.fitlerEntity == null) {
			//用于塞选时间用 不做区分成功还是失败
			this.fitlerEntity = new TransactionFitlerEntity();
		}
		if (mAdapter == null)
			mAdapter = new BuyerListAdapter(getActivity(), this.mTasks, R.layout.item_customer);
		//设置数据
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		//自动刷新
		mPullToRefresh.setFirstAutoRefresh();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("phone", this.mTasks.get(position).getPhone());
			HCActionUtil.launchActivity(getActivity(), CustomerDetailActivity.class, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (getActivity() != null && getActivity().isFinishing()) {
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
	public void doingFilter(Bundle where) {
		//加载网络筛选
		if (fitlerEntity != null)
			fitlerEntity.setSearch_field(where.getString("search_field", null));
		if (mPullToRefresh != null)
			mPullToRefresh.setFirstAutoRefresh();//刷新
	}

	int sort = -1, where = -1, filter = -1;
	int search_cond = -1;
	String order = null;

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.floatingactionbutton) {
			HCActionUtil.launchActivity(getActivity(), BuyerAddActivity.class, null);
		} else if (view.getId() == R.id.rl_trans_task_filter_sort) {
			//排序
			HCDialog.TransactionFilterSort(getActivity(), sort, where, new HCDialog.OnSortConfirmListener() {
				@Override
				public void isClick(int sort, int where) {
					BuyerListFragment.this.sort = sort;
					BuyerListFragment.this.where = where;

					switch (where) {
						case TaskConstants.LEVEL:
							if (sort == TaskConstants.ASCENDING)
								order = "level asc";
							else
								order = "level desc";
							break;
						case TaskConstants.REVISIT:
							if (sort == TaskConstants.ASCENDING)
								order = "contact_time asc";
							else
								order = "contact_time desc";
							break;
					}

					mPullToRefresh.setFirstAutoRefresh();
				}
			});

		} else if (view.getId() == R.id.rl_trans_task_filter) {
			//筛选
			HCDialog.TransactionFilter(getActivity(), filter, new HCDialog.OnLevelConfirmListener() {
				@Override
				public void isClick(int where) {
					BuyerListFragment.this.filter = where;

					//筛选条件 1：H级 2:A级 3:B级 4：今日回访 5：今日失败带看
					switch (where) {
						case TaskConstants.LEVELH:
							search_cond = 1;
							break;
						case TaskConstants.LEVELA:
							search_cond = 2;
							break;
						case TaskConstants.LEVELB:
							search_cond = 3;
							break;
						case TaskConstants.TODAY_VISIT:
							search_cond = 4;
							break;
						case TaskConstants.TODAY_FAIL:
							search_cond = 5;
							break;
						case TaskConstants.ALL:
							search_cond = -1;
							order = null;
							break;
					}

					mPullToRefresh.setFirstAutoRefresh();
				}
			});
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
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransFailBuyerList(pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity, search_cond, order, 0), BuyerListFragment.this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
	}

	@Override
	public void onLoadMoreRefresh() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransFailBuyerList(++pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity, search_cond, order, 0), BuyerListFragment.this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TransactionMainActivity.REQUEST_CODE_AUTO_REFRESH) {
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
