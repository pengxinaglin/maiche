package com.haoche51.sales.hcmessage;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.hccustomer.CustomerDetailActivity;
import com.haoche51.sales.hccustomer.CustomerListActivity;
import com.haoche51.sales.hctransaction.TaskBreachDetailActivity;
import com.haoche51.sales.hctransaction.TransBuyerInfoActivity;
import com.haoche51.sales.hctransaction.TransactionTaskDetailActivity;
import com.haoche51.sales.hctransaction.TransactionTaskEntity;
import com.haoche51.sales.hctransfer.TransferDetailActivity;
import com.haoche51.sales.helper.UserDataHelper;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCCacheUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.IntentExtraMap;
import com.haoche51.sales.util.PreferenceUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mac on 15/11/19.
 */
public class MessageListFragment extends CommonBaseFragment {

	private HCPullToRefresh mPullToRefresh;
	private List<MessageEntity> mListView;
	private int pageSize = 10;
	private int mPage = 0;
	private MessageAdapter mAdapter;
	private Gson mGson = new Gson();

	@Override
	protected int getContentView() {
		return R.layout.activity_message;
	}

	@Override
	protected void initView(View view) {
		mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
		mPullToRefresh.setCanPull(true);
		mPullToRefresh.setFirstAutoRefresh();

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
		mPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
			@Override
			public void onPullDownRefresh() {
				mPage = 0;
				AppHttpServer.getInstance().post(HCHttpRequestParam.getMessageList(mPage, pageSize), MessageListFragment.this, 0); //下拉刷新
			}

			@Override
			public void onLoadMoreRefresh() {
				AppHttpServer.getInstance().post(HCHttpRequestParam.getMessageList(++mPage, pageSize), MessageListFragment.this, 1);//上划刷新
			}
		});
		final ListView mListView = mPullToRefresh.getListView();
		mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
		mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
		this.mListView = HCCacheUtil.getCacheMessages(); //
		mAdapter = new MessageAdapter(getActivity(), this.mListView, R.layout.message_item);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (MessageListFragment.this.mListView == null || MessageListFragment.this.mListView.isEmpty())
					return;

				MessageEntity messageEntity = MessageListFragment.this.mListView.get(position);

				if (messageEntity == null) return;
				//标记消息已读
				AppHttpServer.getInstance().post(HCHttpRequestParam.readMsg(messageEntity.getId()), MessageListFragment.this, 3);

				MessageCustomContentEntity task;
				try {
					task = mGson.fromJson(messageEntity.getCustom_content(), MessageCustomContentEntity.class);
					if (task == null) return;
				} catch (Exception e) {
					return;
				}
				int type = -1;
				if (messageEntity != null) {
					type = messageEntity.getType();
				}
				switch (type) {
					case TaskConstants.MESSAGE_VIEW_TASK:
						ProgressDialogUtil.showProgressDialog(getActivity(), GlobalData.resourceHelper.getString(R.string.hc_loading));
						Map<String, Object> params = HCHttpRequestParam.getTransactionById(task.getTask_id());
						AppHttpServer.getInstance().post(params, MessageListFragment.this, 0);
						break;
					case TaskConstants.MESSAGE_TRANSFER_TASK:
						Intent transferIntent = new Intent(GlobalData.mContext, TransferDetailActivity.class);
						transferIntent.putExtra(TransferDetailActivity.KEY_INTENT_EXTRA_ID, String.valueOf(task.getTask_id()));
						startActivity(transferIntent);
						break;
					case TaskConstants.MESSAGE_TRANSATION_CUSTOMER://跳转客户
						if (!TextUtils.isEmpty(task.getPhone())) {
							Map<String, Object> map = new HashMap<>();
							//带看失败页面
							map.put("phone", task.getPhone());
							HCActionUtil.launchActivity(getActivity(), CustomerDetailActivity.class, map);
						}
						break;
					case TaskConstants.MESSAGE_REVISIT_TASK://跳转未推出带看回访
						Intent revisitIntent = new Intent(GlobalData.mContext, CustomerListActivity.class);
						revisitIntent.putExtra(CustomerListActivity.INTENT_EXTRA_LIST_TYPE, TaskConstants.REQUEST_CUSTOMER_LIST_NOPUSH);
						startActivity(revisitIntent);
						break;
				}
			}
		});
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (getActivity() != null && getActivity().isFinishing()) {
			return;
		}

		if (action.equals(HttpConstants.ACTION_GET_MESSAGE)) {
			//记录更新最后一次刷新成功的时间
			try {
				PreferenceUtil.putInt(GlobalData.mContext, UserDataHelper.LAST_UPDATE_TIME, (int) (new Date().getTime() / 1000));
				onListHttpResponse(response, requestId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (action.equals(HttpConstants.ACTION_GET_TRANSACTION_BY_ID)) {
			onTransactionResponse(response);
		}
		if (ProgressDialogUtil.isShowProgress()) {
			ProgressDialogUtil.closeProgressDialog();
		}
	}

	/**
	 * 列表页刷新返回解析
	 *
	 * @param response
	 * @param requestId
	 */
	private void onListHttpResponse(HCHttpResponse response, int requestId) {
		if (getActivity() != null && getActivity().isFinishing()) {
			return;
		}

		switch (response.getErrno()) {
			case 0:
				List<MessageEntity> list = HCJsonParse.parseMessageLists(response.getData());
				//解析返回值，缓存首屏
				if (requestId == 0) { //下拉刷新
					if (mListView != null && list != null) {
						mListView.clear();
						mListView.addAll(list);
						HCCacheUtil.saveCacheMessages(mListView);
					}
				} else { //上拉加载更多
					if (mListView != null) {
						mListView.addAll(mergeConflict(mListView, list));
					}
					if (list != null) {
						boolean isNoMoreData = list.size() < pageSize;
						mPullToRefresh.setFooterStatus(isNoMoreData);
					}
				}
				mAdapter.notifyDataSetChanged();
				int count = mAdapter.getCount();
				HCLogUtil.e("response message", "result count:" + count);
				break;
			default:
				mPullToRefresh.setFooterStatus(false);
				if (getActivity() != null && !getActivity().isFinishing())
					Toast.makeText(getActivity(), response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
		mPullToRefresh.finishRefresh();
	}

	/**
	 * 解析返回的单个看车任务任务信息
	 */
	private void onTransactionResponse(HCHttpResponse response) {
		if (getActivity() == null || getActivity().isFinishing()) {
			return;
		}

		switch (response.getErrno()) {
			case 0:
				if (response.getData() != null) {
					Gson gson = new Gson();
					TransactionTaskEntity mTaskEntity;
					try {
						mTaskEntity = gson.fromJson(response.getData(), TransactionTaskEntity.class);
					} catch (Exception e) {
						e.printStackTrace();
						ToastUtil.showTextLong("数据错误");
						mTaskEntity = null;
					}

					if (mTaskEntity != null) {
						// 新的看车任务
						if (mTaskEntity.getStatus() == 0) {
							String strId = mTaskEntity.getId();
							if (!TextUtils.isEmpty(strId)) {
								int id = StringUtil.parseInt(strId, 0);
								HCActionUtil.launchActivity(getActivity(), TransactionTaskDetailActivity.class, IntentExtraMap.putId(id));
							} else {
								ToastUtil.showTextLong("数据错误");
							}
						} else if (mTaskEntity.getStatus() == 1 || mTaskEntity.getStatus() == 2) {
							// 看车失败
							try {
								Map<String, Object> map = new HashMap<>();
								map.put("phone", mTaskEntity.getBuyer_phone());
								HCActionUtil.launchActivity(getActivity(), CustomerDetailActivity.class, map);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							// 成功的看车任务
							String strId = mTaskEntity.getId();
							if (!TextUtils.isEmpty(strId)) {
								int id = StringUtil.parseInt(strId, 0);
								if (mTaskEntity.getAudit_status() == 10) {
									// 已毁约
									HCActionUtil.launchActivity(getActivity(), TaskBreachDetailActivity.class, IntentExtraMap.putTransactionId(id));
								} else {
									HCActionUtil.launchActivity(getActivity(), TransBuyerInfoActivity.class, IntentExtraMap.putTransactionId(id));
								}
							} else {
								ToastUtil.showTextLong("数据错误");
							}
						}
					}
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 本地合并message 列表
	 */
	private List<MessageEntity> mergeConflict(List<MessageEntity> mMessages, List<MessageEntity> newMessages) {
		List<MessageEntity> result = new ArrayList<>();
		if (mMessages == null || newMessages == null) {
			return result;
		}
		for (MessageEntity pushEntity : newMessages) {
			if (!mMessages.contains(pushEntity)) {
				result.add(pushEntity);
			}
		}
		return result;
	}
}
