package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;
import com.mobsandgeeks.saripaar.annotation.Required;

/**
 * 看车任务-->看车失败
 * Created by mac on 15/12/3.
 */
public class TransactionTaskFailActivity extends CommonStateActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener,
		TextWatcher {

	@ViewInject(R.id.rg_quit_buy_reason)
	private RadioGroup rg_quit_buy_reason;

	@ViewInject(R.id.tv_content_max_input_count)
	private TextView tv_content_max_input_count;//监听输入字数

	@ViewInject(R.id.sp_buyer_level)
	private Spinner sp_buyer_level;//客户级别

	@ViewInject(R.id.sp_buyer_revisit)
	private Spinner sp_buyer_revisit;//下次回访时间

	@ViewInject(R.id.rg_vehicle_equal_sys)
	private RadioGroup rg_vehicle_equal_sys;//车型相符

	@ViewInject(R.id.et_unequal_reason)
	private EditText et_unequal_reason;//不符原因

	@ViewInject(R.id.ll_vehicle_unequal_reason)
	private LinearLayout ll_vehicle_unequal_reason;

	@ViewInject(R.id.et_quit_buy_reason_content)
	@Required(order = 1, message = "详细原因不能为空")
	private EditText et_quit_buy_reason_content;//失败具体原因

	private final int MAX_REASON_TEXT_LENGTH = 100;//具体原因最大长度

	private int type;//选择放弃购买原因类型
	private TransactionTaskEntity vTask;

	@Override
	protected int getContentView() {
		return R.layout.activity_quit_buy;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.see_failure);
		vTask = (TransactionTaskEntity) getIntent().getSerializableExtra("taskEntity");
		if (vTask == null) { //异常退出 
			return;
		}
		//switch 放弃购买原因
		rg_quit_buy_reason.setOnCheckedChangeListener(this);
		//点击保存
		findViewById(R.id.ll_quit_buy_commit).setOnClickListener(this);

		tv_content_max_input_count.setText(MAX_REASON_TEXT_LENGTH + "");
		//监听具体失败原因文本
		et_quit_buy_reason_content.addTextChangedListener(this);
		//默认是B级客户
		sp_buyer_level.setSelection(1);
		//监听是否车型相符
		rg_vehicle_equal_sys.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		if (radioGroup.getId() == rg_vehicle_equal_sys.getId()) {
			switch (checkedId) {
				case R.id.rb_equal_sys:
					//关闭
					ControlDisplayUtil.displayOpenAnimation(ll_vehicle_unequal_reason, false, 400);
					break;
				case R.id.rb_unequal_sys:
					//展开
					ControlDisplayUtil.displayOpenAnimation(ll_vehicle_unequal_reason, true, 400);
					break;
			}
		} else {
			switch (checkedId) {
				case R.id.rb_quit_buy_nomeet: // 未上门
					type = 1;
					break;
				case R.id.rb_quit_buy_lookbad: // 车没看上
					type = 2;
					break;
				case R.id.rb_quit_buy_pricebad:// 价格没谈拢
					type = 3;
					break;
			}
		}
	}

	@Override
	public void onClick(View view) {
		validator.validate();
	}

	@Override
	public void onValidationSucceeded() {
		//判断是否选择了失败原因
		if (type > 0) {
			//上传失败原因
			updateFailReason();
		} else {
			ToastUtil.showInfo(getString(R.string.select_failed_type));
		}
		super.onValidationSucceeded();
	}

	/**
	 * 跳转修改车源订阅条件页面
	 */
	private void changeSubscribeCondition() {
		Intent intent = new Intent(this, VehicleSubScribeConditionActivity.class);
		intent.putExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_PHONE, vTask.getBuyer_phone());
		intent.putExtra(VehicleSubScribeConditionActivity.KEY_INTENT_EXTRA_BUYER_NAME, vTask.getBuyer_name());
		startActivityForResult(intent, TaskConstants.REQUEST_CODE_CONDITION_MODIFY);
	}

	/**
	 * 上传失败原因
	 */
	private void updateFailReason() {
		ProgressDialogUtil.showProgressDialog(TransactionTaskFailActivity.this, null);
		String reason = et_quit_buy_reason_content.getText().toString(); // 回传json 。后续数据统计
		int is_vehicle_match = rg_vehicle_equal_sys.getCheckedRadioButtonId() == R.id.rb_equal_sys ? 0 : 1;
		String dismatch_detail = et_unequal_reason.getText().toString();
		int due_time;
		//N级
		if (sp_buyer_level.getSelectedItemPosition() == 0) {
			due_time = sp_buyer_revisit.getSelectedItemPosition();
		}
		//B.A.H级
		else {
			due_time = sp_buyer_revisit.getSelectedItemPosition() + 1;
		}
		AppHttpServer.getInstance().post(HCHttpRequestParam.cancelTransaction(Integer.parseInt(vTask.getId()), type,
				sp_buyer_level.getSelectedItemPosition() + 2, due_time, reason, is_vehicle_match, dismatch_detail), this, 0);
	}

	/**
	 * 上传失败原因结果
	 */
	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_CANCEL_VIEW_TASK)) {
			onFailReasonResult(response);
		}
		ProgressDialogUtil.closeProgressDialog();
	}

	private void onFailReasonResult(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//修改订阅条件
				if (type > 1) {
					ProgressDialogUtil.closeProgressDialog();
					//更改订阅
					changeSubscribeCondition();
				} else {
					TransactionTaskFailActivity.this.setResult(RESULT_OK);
					TransactionTaskFailActivity.this.finish();
				}
				break;
			default:
				Toast.makeText(getApplicationContext(), response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		try {
			//已经超出长度了，输入无效
			if (s.toString().length() > MAX_REASON_TEXT_LENGTH) {
				tv_content_max_input_count.setText("0");
				et_quit_buy_reason_content.removeTextChangedListener(this);//避免死循环
				et_quit_buy_reason_content.setText(s.toString().substring(0, MAX_REASON_TEXT_LENGTH));
				et_quit_buy_reason_content.setSelection(et_quit_buy_reason_content.getText().toString().length());//将光标移动到最后
				et_quit_buy_reason_content.addTextChangedListener(this);
			} else {
				//更新剩余可输入字数
				tv_content_max_input_count.setText(String.valueOf(MAX_REASON_TEXT_LENGTH - s.length()));
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 选择客户级别
	 *
	 * @param adapterView
	 * @param view
	 * @param position
	 * @param l
	 */
	@OnItemSelected(R.id.sp_buyer_level)
	private void onBuyerLevelCLick(AdapterView<?> adapterView, View view, int position, long l) {
		switch (position) {
			case 0:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(TransactionTaskFailActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_n)));
				break;
			case 1:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(TransactionTaskFailActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_b)));
				break;
			case 2:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(TransactionTaskFailActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_a)));
				break;
			case 3:
				sp_buyer_revisit.setAdapter(new ArrayAdapter<>(TransactionTaskFailActivity.this,
						android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.revisit_h)));
				break;
		}
	}

	public void onChooseReason(View v) {
		Button button = (Button) v;
		boolean checked = false;
		if (button.getTag() != null)
			checked = (boolean) button.getTag();

		String reason = button.getText().toString();
		if (!checked) {
			button.setTextColor(getResources().getColor(R.color.hc_self_white));
			button.setBackgroundResource(R.drawable.shape_vehicle_unequal_reason_checked);
			et_unequal_reason.setText(et_unequal_reason.getText().toString() + reason);
		} else {
			button.setTextColor(getResources().getColor(R.color.hc_self_black));
			button.setBackgroundResource(R.drawable.shape_vehicle_unequal_reason_normal);
			et_unequal_reason.setText(et_unequal_reason.getText().toString().replace(reason, ""));
		}

		button.setTag(!checked);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			vTask.setStatus(TaskConstants.VIEW_TASK_ACANCEL);
			TransactionTaskFailActivity.this.setResult(RESULT_OK);
			TransactionTaskFailActivity.this.finish();
		}
	}
}
