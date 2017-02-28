package com.haoche51.sales.hccustomer;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Type;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ToastUtil;

/**
 * Created by PengXianglin on 16/11/16.
 */
public class OverViewFragment extends CommonBaseFragment implements View.OnClickListener {

	private TextView tv_mine;//我的回访
	private LinearLayout ll_mine;//我的回访
	private TextView tv_recycled;//回炉回访
	private LinearLayout ll_recycled;//回炉回访
	private TextView tv_nopush;//未推出带看
	private LinearLayout ll_nopush;//未推出带看
	private TextView tv_nodoor;//未上门
	private LinearLayout ll_nodoor;//未上门
	private TextView tv_door;//已上门
	private LinearLayout ll_door;//已上门
	private TextView tv_recommended;//新车推荐
	private LinearLayout ll_recommended;//新车推荐

	private OverViewEntity overViewEntity;

	@Override
	protected int getContentView() {
		return R.layout.customer_overview_fragment;
	}

	@Override
	protected void initView(View view) {
		tv_mine = (TextView) view.findViewById(R.id.tv_mine);
		ll_mine = (LinearLayout) view.findViewById(R.id.ll_mine);
		ll_mine.setOnClickListener(this);

		tv_recycled = (TextView) view.findViewById(R.id.tv_recycled);
		ll_recycled = (LinearLayout) view.findViewById(R.id.ll_recycled);
		ll_recycled.setOnClickListener(this);

		tv_nopush = (TextView) view.findViewById(R.id.tv_nopush);
		ll_nopush = (LinearLayout) view.findViewById(R.id.ll_nopush);
		ll_nopush.setOnClickListener(this);

		tv_nodoor = (TextView) view.findViewById(R.id.tv_nodoor);
		ll_nodoor = (LinearLayout) view.findViewById(R.id.ll_nodoor);
		ll_nodoor.setOnClickListener(this);

		tv_door = (TextView) view.findViewById(R.id.tv_door);
		ll_door = (LinearLayout) view.findViewById(R.id.ll_door);
		ll_door.setOnClickListener(this);

		tv_recommended = (TextView) view.findViewById(R.id.tv_recommended);
		ll_recommended = (LinearLayout) view.findViewById(R.id.ll_recommended);
		ll_recommended.setOnClickListener(this);
	}

	@Override
	protected void initData(Bundle savedInstanceState) {
		showLoadingView(true);
		//加载信息
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerTotal(), this, 0);
	}

	@Override
	public void refreshData(int action, Object data, OnRefreshListener l) {
		//加载信息
		AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerTotal(), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (null != getActivity() && getActivity().isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_GETBUYERTOTAL)) {
			onResponse(response, requestId);
		}
	}

	private void onResponse(HCHttpResponse response, int requestId) {
		try {
			dismissLoadingView();
			switch (response.getErrno()) {
				case 0:
					overViewEntity = HCJsonParse.parseOverViewEntity(response.getData());
					if (overViewEntity != null) {
						tv_mine.setText(String.valueOf(overViewEntity.getMy_revisit_count()));
						tv_recycled.setText(String.valueOf(overViewEntity.getReuse_revisit_count()));
						tv_nopush.setText(String.valueOf(overViewEntity.getNotrans_revisit_count()));
						tv_nodoor.setText(String.valueOf(overViewEntity.getNosite_fail_count()));
						tv_door.setText(String.valueOf(overViewEntity.getOnsite_fail_count()));
						tv_recommended.setText(String.valueOf(overViewEntity.getSubscribe_count()));
					}
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					showErrorView(true, this);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_mine:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getMy_revisit_count(), TaskConstants.REQUEST_CUSTOMER_LIST_MINE);
				break;
			case R.id.ll_recycled:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getReuse_revisit_count(), TaskConstants.REQUEST_CUSTOMER_LIST_RECYCLED);
				break;
			case R.id.ll_nopush:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getNotrans_revisit_count(), TaskConstants.REQUEST_CUSTOMER_LIST_NOPUSH);
				break;
			case R.id.ll_nodoor:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getNosite_fail_count(), TaskConstants.REQUEST_CUSTOMER_LIST_NODOOR);
				break;
			case R.id.ll_door:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getOnsite_fail_count(), TaskConstants.REQUEST_CUSTOMER_LIST_DOOR);
				break;
			case R.id.ll_recommended:
				if (overViewEntity != null)
					jumpRevisit(overViewEntity.getSubscribe_count(), TaskConstants.REQUEST_CUSTOMER_LIST_RECOMMENDED);
				break;
			default:
				dismissResultView(true);
				showLoadingView(false);
				//加载信息
				AppHttpServer.getInstance().post(HCHttpRequestParam.getBuyerTotal(), this, 0);
				break;
		}
	}

	/**
	 * 跳转到回访
	 */
	private void jumpRevisit(int count, int type) {
		if (overViewEntity != null && count > 0) {
			Intent intent = new Intent(this.getActivity(), CustomerListActivity.class);
			intent.putExtra(CustomerListActivity.INTENT_EXTRA_LIST_TYPE, type);
			startActivity(intent);
		}
	}
}
