package com.haoche51.sales.hccustomer;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.haoche51.sales.R;
import com.haoche51.sales.custom.switchbutton.SwitchButton;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 修改客户信息
 * Created by PengXianglin on 16/11/22.
 */
public class EditeCustomerInfoActivity extends CommonStateActivity {

	public static final String INTENT_EXTRA_PHONE = "intent_extra_phone";
	public static final String INTENT_EXTRA_NAME = "intent_extra_name";
	public static final String INTENT_EXTRA_WX = "intent_extra_wx";
	public static final String INTENT_EXTRA_DEALER = "intent_extra_dealer";

	@ViewInject(R.id.et_name)
	private EditText et_name;//姓名

	@ViewInject(R.id.et_wx)
	private EditText et_wx;//微信

	@ViewInject(R.id.rb_is_dealer)
	private RadioButton rb_is_dealer;//是车商
	@ViewInject(R.id.rb_no_dealer)
	private RadioButton rb_no_dealer;//否车商

	@ViewInject(R.id.btn_commit)
	private Button btn_commit;//确定

	private String buyer_phone;

	@Override
	protected int getContentView() {
		return R.layout.acitivity_edite_customer_info;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(getString(R.string.hc_customer_edite_info));

		buyer_phone = getIntent().getStringExtra(INTENT_EXTRA_PHONE);
		if (TextUtils.isEmpty(buyer_phone)) {
			ToastUtil.showInfo("缺少参数");
			finish();
		}

		if (getIntent().getIntExtra(INTENT_EXTRA_DEALER, 0) == 1) {
			rb_is_dealer.setChecked(true);
		} else {
			rb_no_dealer.setChecked(true);
		}

		btn_commit.setText(getString(R.string.action_ok));
		et_name.setText(getIntent().getStringExtra(INTENT_EXTRA_NAME));
		et_wx.setText(getIntent().getStringExtra(INTENT_EXTRA_WX));
	}

	@OnClick(R.id.btn_commit)
	private void onClickCommit(View v) {
		String name = et_name.getText().toString();
		String wx = et_wx.getText().toString();
		boolean checked = rb_is_dealer.isChecked();

		ProgressDialogUtil.showProgressDialog(this);
		AppHttpServer.getInstance().post(HCHttpRequestParam.updateBuyerInfo(buyer_phone, wx, name, checked ? 1 : 0), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isDestroyed() || isFinishing()) {
			return;
		}
		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_OFFLINESALER_UPDATEBUYERINFO)) {
			responseBuyerInfo(response);
		}
	}

	private void responseBuyerInfo(HCHttpResponse response) {
		try {
			switch (response.getErrno()) {
				case 0:
					ToastUtil.showInfo("修改成功");
					finish();
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
