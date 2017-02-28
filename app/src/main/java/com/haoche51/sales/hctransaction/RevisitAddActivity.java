package com.haoche51.sales.hctransaction;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.haoche51.sales.R;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 添加回访记录
 * Created by mac on 15/9/26.
 */
public class RevisitAddActivity extends CommonBaseActivity {

	@ViewInject(R.id.ll_transaction_buyer_info_level)
	private LinearLayout ll_transaction_buyer_info_level;
	@ViewInject(R.id.sp_edit_buyer_level)
	private Spinner sp_edit_buyer_level;
	@ViewInject(R.id.sp_buyer_revisit)
	private Spinner sp_buyer_revisit;

	@ViewInject(R.id.et_content)
	private EditText et_content;

	private String vehicle_source_id, seller_name;
	private String buyer_phone, seller_phone;//买家、车主电话

	private int level;
	private String changeLevel;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_add_revisit);
		setScreenTitle(R.string.add_revisit);
		registerTitleBack();//设置返回
		ViewUtils.inject(this);
	}

	@Override
	protected void initData() {
		vehicle_source_id = getIntent().getStringExtra("vehicle_source_id");
		seller_name = getIntent().getStringExtra("seller_name");
		buyer_phone = getIntent().getStringExtra("buyer_phone");
		seller_phone = getIntent().getStringExtra("seller_phone");

		sp_edit_buyer_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(RevisitAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_n)));
						break;
					case 1:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(RevisitAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_b)));
						break;
					case 2:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(RevisitAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_a)));
						break;
					case 3:
						sp_buyer_revisit.setAdapter(new ArrayAdapter<>(RevisitAddActivity.this,
								android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_h)));
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		changeLevel = getIntent().getStringExtra("changeLevel");
		if (!TextUtils.isEmpty(changeLevel) && "true".equals(changeLevel)) {
			ll_transaction_buyer_info_level.setVisibility(View.VISIBLE);
			level = getIntent().getIntExtra("level", 3);
			if (level == 1) {
				level = 2;
			}
			sp_edit_buyer_level.setSelection(level - 2);
		}

	}

	@OnClick(R.id.rl_modify_transfer_commit)
	private void onClick(View view) {
		String revisit = et_content.getText().toString();
		if (TextUtils.isEmpty(revisit)) {
			ToastUtil.showInfo(getString(R.string.tip_input_content));
			return;
		}
		ProgressDialogUtil.showProgressDialog(RevisitAddActivity.this, getString(R.string.later));
		//添加买家回访
		if (TextUtils.isEmpty(this.seller_phone)) {
			AppHttpServer.getInstance().post(HCHttpRequestParam.addBuyerRevisit(vehicle_source_id, buyer_phone, revisit), this, 0);

		} else {//添加车主回访
			AppHttpServer.getInstance().post(HCHttpRequestParam.addSellerRevisit(vehicle_source_id, seller_name, seller_phone, revisit), this, 0);
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_ADD_BUYER_REVISIT)) {
			doResponseBuyerRevisit(response);
		} else if (action.equals(HttpConstants.ACTION_ADD_SELLER_REVISIT)) {
			doResponseSellerRevisit(response);
		} else if (action.equals(HttpConstants.ACTION_SET_BUYER_COMMENT_LEVEL)) {
			doResponseLevel(response);
		}
	}

	private void doResponseLevel(HCHttpResponse response) {
		ProgressDialogUtil.closeProgressDialog();
		switch (response.getErrno()) {
			case 0:
				//添加成功
				HCDialog.showDialog(this, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setResult(RESULT_OK);
						finish();
					}
				});
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	private void doResponseSellerRevisit(HCHttpResponse response) {
		ProgressDialogUtil.closeProgressDialog();
		switch (response.getErrno()) {
			case 0:
				//添加成功
				ToastUtil.showInfo(getString(R.string.add_success));
				finish();
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	private void doResponseBuyerRevisit(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//添加成功
				if (!TextUtils.isEmpty(changeLevel) && "true".equals(changeLevel)) {
					int due_time;
					//N级
					if (sp_edit_buyer_level.getSelectedItemPosition() == 0) {
						due_time = sp_buyer_revisit.getSelectedItemPosition();
					}
					//B.A.H级
					else {
						due_time = sp_buyer_revisit.getSelectedItemPosition() + 1;
					}
					AppHttpServer.getInstance().post(HCHttpRequestParam.setBuyerCommentLvel(
							buyer_phone, null, sp_edit_buyer_level.getSelectedItemPosition() + 2
							, due_time, null, null, 0), this, 0);
				} else {
					ProgressDialogUtil.closeProgressDialog();
					ToastUtil.showInfo(getString(R.string.add_success));
					finish();
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}
}
