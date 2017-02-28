package com.haoche51.sales.hcshare;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.WeChatUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by PengXianglin on 16/8/17.
 */
public class SelectImageActivity extends CommonStateActivity implements ImageAdapter.CheckedImageListener, WeChatUtil.ShareCallback {

	@ViewInject(R.id.recycler_contract_photo)
	public RecyclerView recycler;
	@ViewInject(R.id.btn_share)
	public Button btnShare;

	public ImageAdapter mAdapter;
	private List<ImageInfoEntity> data;

	WxShareEntity wxShareEntity;
	public static String EXTRA_KEY_DATA = "key_data";
	public int MAX_SIZE = 8;

	@Override
	protected int getContentView() {
		return R.layout.activity_selectimage;
	}

	@Override
	protected void initView() {
		super.initView();
		initRecyclerView();
	}

	private void initRecyclerView() {
		GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		this.recycler.setLayoutManager(layoutManager);
		this.recycler.setHasFixedSize(true);
	}

	@Override
	protected void initData() {
		setScreenTitle("车辆图片");

		wxShareEntity = getIntent().getParcelableExtra(EXTRA_KEY_DATA);
		if (wxShareEntity == null) {
			ToastUtil.showInfo("没有图片");
			finish();
		}

		final List<String> dataList = wxShareEntity.getImages();
		if (dataList == null || dataList.isEmpty()) {
			ToastUtil.showInfo("没有图片");
			finish();
		} else {
			data = new ArrayList<ImageInfoEntity>();
			mAdapter = new ImageAdapter(this, data, MAX_SIZE, this);
			recycler.setAdapter(mAdapter);
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (String url : dataList) {
						data.add(new ImageInfoEntity(url, false));
						mAdapter.notifyDataSetChanged();
					}
				}
			}).start();
		}
	}

	@OnClick(R.id.btn_share)
	private void onClickShare(View v) {
		List<String> checkeds = mAdapter.getCheckeds();
		//分享
		WeChatUtil.share(this, checkeds, wxShareEntity.getQrcode(), wxShareEntity.getContent(), 100, this);
	}

	@Override
	public void onChecked(int currentCount, int totalCount) {
		btnShare.setText(currentCount + "/" + totalCount);
	}

	@Override
	public void start() {
		ProgressDialogUtil.showProgressDialog(this, null);
	}

	@Override
	public void end() {
		ProgressDialogUtil.closeProgressDialog();
		finish();
	}
}
