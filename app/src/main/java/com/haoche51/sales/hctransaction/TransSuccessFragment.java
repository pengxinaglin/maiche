package com.haoche51.sales.hctransaction;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.hctransfer.TransSuccessAdapter;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.dialog.AlertDialog;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.listener.TaskDataObserver;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ACache;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看车成功
 * Created by mac on 15/11/20.
 */
public class TransSuccessFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, AlertDialog.OnDismissListener, HCPullToRefresh.OnRefreshCallback {

	//	private TextView tv_titlebar_totalcount;//显示总数量
	private RelativeLayout rl_trans_task_filter_time;//时间搜索
	private TextView tv_trans_task_filter_time;//显示搜索的时间
	List<Date> selectedDates;//保存已经选择的时间
	private HCPullToRefresh mPullToRefresh;
	private ListView mListView;
	private TransactionFitlerEntity fitlerEntity;//根据条件加载数据
	private List<TransactionTaskShortEntity> mTasks = new ArrayList<TransactionTaskShortEntity>();
	private TransSuccessAdapter mAdapter = null;

	private int pageIndex = 0;//当前页面索引
	private ACache mAcache;
	private int mClickIndex;

	@Override
	protected int getContentView() {
		return R.layout.fragment_trans_filter_task;
	}

	@Override
	protected void initView(View view) {
//		tv_titlebar_totalcount = (TextView) view.findViewById(R.id.tv_titlebar_totalcount);
		rl_trans_task_filter_time = (RelativeLayout) view.findViewById(R.id.rl_trans_task_filter_time);
		rl_trans_task_filter_time.setOnClickListener(this);
		tv_trans_task_filter_time = (TextView) view.findViewById(R.id.tv_trans_task_filter_time);
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setOnRefreshCallback(this);

		final LinearLayout relEmpty = (LinearLayout) view.findViewById(R.id.ll_list_empty);
		TextView textView = (TextView) relEmpty.findViewById(R.id.result_txt);
		textView.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.common_button_nodata, 0, 0);
		textView.setText(GlobalData.resourceHelper.getString(R.string.hc_common_result_nodata));
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
		if (this.fitlerEntity == null) {
			this.fitlerEntity = new TransactionFitlerEntity();
			this.fitlerEntity.setType(TaskConstants.TRANSACTION_SUCCESS);//1失败 2成功
		}
		//初始化ACache
		if (mAcache == null)
			mAcache = ACache.get(getActivity());
		//读取ACache中的缓存记录
		String json = mAcache.getAsString(TaskConstants.ACACHE_TRANSACTION_SUCCESS);
		if (!TextUtils.isEmpty(json)) {
			this.mTasks.clear();
			this.mTasks.addAll(HCJsonParse.parseGetTransTaskShortEntitys(json));
		}
		if (mAdapter == null)
			mAdapter = new TransSuccessAdapter(getActivity(), mTasks, R.layout.item_transaction_success);
		//设置数据
		if (mListView != null)
			mListView.setAdapter(mAdapter);
		//自动刷新
		mPullToRefresh.setFirstAutoRefresh();
		//设置监听数据变化
		HCTransactionSuccessWatched.getInstance().registerDataObserver(taskDataObserver);
	}

	TaskDataObserver taskDataObserver = new TaskDataObserver(new Handler()) {
		@Override
		public void onChanged(Object o) {
			try {
				//自动刷新
				mPullToRefresh.setFirstAutoRefresh();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		try {
			Map<String, Object> map = new HashMap<>();
			mClickIndex = position;
			map.put("trans_id", this.mTasks.get(position).getId());
			map.put("cancel_trans_status", this.mTasks.get(position).getCancel_trans_status());
			if (this.mTasks.get(position).getAudit_status() == TaskConstants.STATUS_CANCEL_FINISH) {
				HCActionUtil.launchActivity(getActivity(), TaskBreachDetailActivity.class, map);
			} else {
				HCActionUtil.launchActivity(getActivity(), TransBuyerInfoActivity.class, map);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (getActivity() != null && getActivity().isFinishing()) {
			return;
		}
		if (HttpConstants.ACTION_TRANSACTION_FILTER.equals(action)) {
			dismissLoadingView();
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
		switch (response.getErrno()) {
			case 0://请求成功
				List<TransactionTaskShortEntity> viewTaskList = HCJsonParse.parseGetTransTaskShortEntitys(response.getData());
				if (requestId == HttpConstants.GET_LIST_REFRESH && viewTaskList != null) {//刷新
					if (!TextUtils.isEmpty(response.getData())) {
						mAcache.put(TaskConstants.ACACHE_TRANSACTION_SUCCESS, response.getData());//只缓存刷新的数据
					}
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
	}

	@Override
	public void doingFilter(Bundle where) {
		//加载网络筛选
		if (fitlerEntity != null)
			fitlerEntity.setSearch_field(where.getString("search_field", null));
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
			if (selectedDates != null && tv_trans_task_filter_time != null)
				tv_trans_task_filter_time.setText(UnixTimeUtil.formatYearMonthDay((int) (startTimeTemp / 1000L))
						+ " - " + UnixTimeUtil.formatYearMonthDay((int) (endTimeTemp / 1000L)));
			if (fitlerEntity != null) {
				fitlerEntity.setStart_time((int) (startTimeTemp / 1000L));//设置请求的开始时间
				fitlerEntity.setEnd_time((int) (endTimeTemp / 1000L));//设置请求的结束时间
			}
		} else {
			//清掉时间
			if (tv_trans_task_filter_time != null)
				tv_trans_task_filter_time.setText("");
			if (selectedDates != null)
				selectedDates.clear();
			if (fitlerEntity != null) {
				fitlerEntity.setStart_time(0);
				fitlerEntity.setEnd_time(0);
			}
		}
		if (mPullToRefresh != null)
			mPullToRefresh.setFirstAutoRefresh();//刷新
	}

	@Override
	public void onPullDownRefresh() {
		pageIndex = 0;
		AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity), TransSuccessFragment.this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
	}

	@Override
	public void onLoadMoreRefresh() {
		AppHttpServer.getInstance().post(HCHttpRequestParam.transactionFilter(++pageIndex,
				TaskConstants.DEFAULT_SHOWTASK_COUNT, fitlerEntity), TransSuccessFragment.this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
	}

	@Override
	public void onDestroyView() {
		HCTransactionSuccessWatched.getInstance().UnRegisterDataObserver(taskDataObserver);
		super.onDestroyView();
	}
}