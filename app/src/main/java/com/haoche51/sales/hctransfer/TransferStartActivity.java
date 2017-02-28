package com.haoche51.sales.hctransfer;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.custom.switchbutton.SwitchButton;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCArithUtil;
import com.haoche51.sales.util.PopupWindowUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * 开始过户，上传过户信息页面
 */
public class TransferStartActivity extends CommonStateActivity implements TransferPhotoViewAdapter.OnPhotoListChangedListener, CompoundButton.OnCheckedChangeListener {

	/**
	 * 剩余服务费
	 */
	@ViewInject(R.id.tv_transfer_start_remaind_service)
	private TextView tv_transfer_start_remaind_service;

	/**
	 * 剩余车价
	 */
	@ViewInject(R.id.tv_transfer_start_remaind_price)
	private TextView tv_transfer_start_remaind_price;

	/**
	 * 复检后车辆成交价
	 */
	@ViewInject(R.id.et_transfer_start_price)
	private EditText et_transfer_start_price;

	/**
	 * 复检减免服务费
	 */
	@ViewInject(R.id.et_transfer_start_reduce)
	private EditText et_transfer_start_reduce;

	/**
	 * 过户费用
	 */
	@ViewInject(R.id.ll_transfer_start_fee)
	private LinearLayout ll_transfer_start_fee;
	@ViewInject(R.id.et_transfer_start_fee)
	private EditText et_transfer_start_fee;

	/**
	 * 是否贷款
	 */
	@ViewInject(R.id.swb_is_loan)
	private SwitchButton swb_is_loan;

	/**
	 * 车主定金转服务费
	 */
	@ViewInject(R.id.swb_owner_earnest_to_service)
	private SwitchButton switchButton;

	/**
	 * 应付车款/担保车款
	 */
	boolean isBackConsignment;
	@ViewInject(R.id.tv_transfer_start_pay_for_car)
	private TextView tv_transfer_start_pay_for_car;
	@ViewInject(R.id.et_transfer_start_pay_for_car)
	private EditText et_transfer_start_pay_for_car;

	//其他支出
	@ViewInject(R.id.et_other_cost)
	private EditText et_other_cost;

	/**
	 * 刷卡手续费
	 */
	@ViewInject(R.id.et_transfer_start_pos_fee)
	private EditText et_transfer_start_pos_fee;

	/**
	 * 悟空
	 */
	@ViewInject(R.id.et_transaction_prepay_wukong)
	private EditText et_transaction_prepay_wukong;

	/**
	 * 备注
	 */
	@ViewInject(R.id.et_transfer_start_comment)
	private EditText et_transfer_start_comment;

	/**
	 * 合同及转账照片
	 */
	@ViewInject(R.id.recyclerview_contract)
	private RecyclerView recyclerview_contract;

	/**
	 * 应收
	 */
	@ViewInject(R.id.tv_transfer_start_receivable)
	private TextView tv_transfer_start_receivable;

	private final int REQUEST_CODE_POSPAY = 2;

	private TransferEntity entity;

	ArrayList<String> transferPhotoPath = new ArrayList<>();         //全部照片的list
	ArrayList<String> transferPhotoPathOrigin = new ArrayList<>();   //全部照片的list
	ArrayList<String> transferPhotoSelectedPath = new ArrayList<>(); //选择照片的list
	private TransferPhotoViewAdapter transferPhotoViewAdapter;
	private int ContractPicOriginCount = 0;//已有照片的数量

	private String originPrice = "0";  //预定时填写的成交价
	private String receiveable = "0";  //应收

	private boolean ownerEarnestToService;

	@Override
	protected int getContentView() {
		return R.layout.activity_transfer_start;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_transfer_start_title);

		entity = (TransferEntity) getIntent().getSerializableExtra("transferEntity");

	}

	@Override
	protected void initData() {
		super.initData();

		if (entity == null) {
			ToastUtil.showText(getString(R.string.parameters_error));
			finish();
			return;
		}

		// 是否包过户显示过户费用
		if (String.valueOf(1).equals(entity.getTransfer_free())) {
			ll_transfer_start_fee.setVisibility(View.VISIBLE);
		} else {
			ll_transfer_start_fee.setVisibility(View.GONE);
		}

		switch (entity.getVehicle_type()) {
			case 1:
			case 3:
			case 4:
				isBackConsignment = true;
				tv_transfer_start_pay_for_car.setText(getString(R.string.hc_transaction_guarantee_amount2));
				break;
			default:
				isBackConsignment = false;
				tv_transfer_start_pay_for_car.setText(getString(R.string.hc_transaction_guarantee_amount));
				break;
		}

		setData(entity);
	}

	private void setData(TransferEntity entity) {

		originPrice = entity.getPrice();

		//剩余服务费
		if (TextUtils.isEmpty(entity.getRest_commisson())) {
			tv_transfer_start_remaind_service.setText("0");
		} else {
			tv_transfer_start_remaind_service.setText(entity.getRest_commisson());
		}

		//剩余车款--车价按照输入框里面的计算
		tv_transfer_start_remaind_price.setText(entity.getRest_vehicle_price());
		//担保车款/应付车款
		et_transfer_start_pay_for_car.setText(entity.getRest_vehicle_price());
		//回购/寄售车并且不贷款
		if ((entity.getVehicle_type() == 1 || entity.isConsignment()) && entity.getIs_loan() == 0) {
			//回购/寄售车过户不能修改担保车款/应付车款
			et_transfer_start_pay_for_car.setEnabled(false);
		}

		//复检收车辆成交价
		String vehiclePrice = originPrice;
		if (entity.getAudit_status() == TaskConstants.STATUS_TRANSFER_CONTRACT_FAIL) {
			//过户合同审核失败，重新提交过户
			et_transfer_start_price.setText(entity.getTransfer_price());
		} else {
			et_transfer_start_price.setText(vehiclePrice);
		}

		//如果是被驳回，展示上次提交的复检减免
		if (!TextUtils.isEmpty(entity.getTransfer_remission())) {
			et_transfer_start_reduce.setText(entity.getTransfer_remission());
		}

		//是否贷款 0不贷款 1贷款
		swb_is_loan.setOnCheckedChangeListener(this);
		swb_is_loan.setChecked(entity.getIs_loan() == 0 ? false : true);
		//如果是已经贷款，不可再修改是否贷款
		swb_is_loan.setEnabled(entity.getIs_loan() == 1 ? false : true);

		// 车主定金转服务费
		if (entity.getUse_owner() == 1) {
			switchButton.setChecked(true);
			ownerEarnestToService = true;
		} else {
			switchButton.setChecked(false);
			ownerEarnestToService = false;
		}

		//刷卡手续费
		if (!TextUtils.isEmpty(entity.getTransfer_fee())) {
			if (StringUtil.parseInt(entity.getTransfer_fee(), 0) > 0)
				et_transfer_start_pos_fee.setText(entity.getTransfer_fee());
		}
		et_other_cost.setText(String.valueOf(entity.getOther_cost()));

		//悟空
		if (!TextUtils.isEmpty(entity.getInsurance())) {
			et_transaction_prepay_wukong.setText(entity.getInsurance());
		}

		//备注
		if (!TextUtils.isEmpty(entity.getTransfer_comment())) {
			et_transfer_start_comment.setText(entity.getTransfer_comment());
		}

		//复检后车辆成交价输入监听
		et_transfer_start_price.addTextChangedListener(textWatcherPrice);
		//复检后减免服务费输入监听
		et_transfer_start_reduce.addTextChangedListener(textWatcherReceivable);
		//应付车款/担保车款输入监听
		et_transfer_start_pay_for_car.addTextChangedListener(textWatcherReceivable);
		//刷卡手续费输入监听
		et_transfer_start_pos_fee.addTextChangedListener(textWatcherReceivable);
		//悟空卡
		et_transaction_prepay_wukong.addTextChangedListener(textWatcherReceivable);

		switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ownerEarnestToService = isChecked;
				calulationReceiveable();
			}
		});

		//应收
		calulationReceiveable();

		//照片
		if (entity.getReceipt_pic() != null && entity.getReceipt_pic().size() > 0) {
			transferPhotoPath = entity.getReceipt_pic();
			transferPhotoPathOrigin = entity.getReceipt_pic_origin();
			ContractPicOriginCount = entity.getReceipt_pic().size();
		}

		// 照片
		GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerview_contract.setLayoutManager(layoutManager);
		recyclerview_contract.setHasFixedSize(true);

		// 照片
		transferPhotoViewAdapter = new TransferPhotoViewAdapter(TransferStartActivity.this, transferPhotoPath, TransferStartActivity.this);
		transferPhotoViewAdapter.setView(false);
		recyclerview_contract.setAdapter(transferPhotoViewAdapter);

		DisplayUtils.reSizeRecyclerView(this, recyclerview_contract, transferPhotoPath, false);


	}


	/**
	 * 剩余车款
	 * 剩余车款=成交价-卖家给车主定金-预收车款
	 *
	 * @return 剩余车款
	 */
	private String calculationPriceRemaind() {
		String vehiclePrice = "0";

		try {
			//预定时的成交车价
			if (!TextUtils.isEmpty(et_transfer_start_price.getText().toString().trim())) {
				vehiclePrice = et_transfer_start_price.getText().toString().trim();
			}
			//减去买家给车主定金
			if (!TextUtils.isEmpty(entity.getSeller_prepay())) {
				vehiclePrice = DisplayUtils.parseMoney("###",
						HCArithUtil.sub(Double.valueOf(vehiclePrice)
								, Double.valueOf(entity.getSeller_prepay())));
			}
			//减去预收车款
			if (!TextUtils.isEmpty(entity.getPrepay_vehicle_price())) {
				vehiclePrice = DisplayUtils.parseMoney("###",
						HCArithUtil.sub(Double.valueOf(vehiclePrice)
								, Double.valueOf(entity.getPrepay_vehicle_price())));
			}

			//计算结果小于0时，归零
			if (Double.valueOf(vehiclePrice) < 0) {
				vehiclePrice = DisplayUtils.parseMoney("###", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vehiclePrice;
	}


	/**
	 * 计算应收(剩余服务费-复检减免服务费+买家付车款+刷卡手续费)
	 *
	 * @return receiveable
	 */
	private String calulationReceiveable() {
		double temp = 0;
		//加try 防止运算发生错误
		try {
			//剩余服务费
			temp = Double.valueOf(tv_transfer_start_remaind_service.getText().toString());
			//减去复检减免服务费
			if (!TextUtils.isEmpty(et_transfer_start_reduce.getText().toString())) {
				temp = HCArithUtil.sub(temp, Double.valueOf(et_transfer_start_reduce.getText().toString()));
			}
			//加上应付车款/担保车款
			if (!TextUtils.isEmpty(et_transfer_start_pay_for_car.getText().toString())) {
				temp = HCArithUtil.add(temp, Double.valueOf(et_transfer_start_pay_for_car.getText().toString()));
			}
			//加上刷卡手续费
			if (!TextUtils.isEmpty(et_transfer_start_pos_fee.getText().toString())) {
				temp = HCArithUtil.add(temp, Double.valueOf(et_transfer_start_pos_fee.getText().toString()));
			}
			if (ownerEarnestToService) {
				//剩余服务费 = 剩余服务费 - 复检减免
				double rest_commission = Double.valueOf(entity.getRest_commission()) - Double.valueOf(et_transfer_start_reduce.getText().toString());
				//车主定金 = min(剩余服务费，车主定金）
				double seller_prepay = Math.min(rest_commission, Double.valueOf(entity.getSeller_company_prepay()));
				//减去车主定金
				temp = HCArithUtil.sub(temp, seller_prepay);
			}
			//加上悟空费
			if (!TextUtils.isEmpty(et_transaction_prepay_wukong.getText().toString())) {
				temp = HCArithUtil.add(temp, Double.valueOf(et_transaction_prepay_wukong.getText().toString()));
			}
			if (temp < 0) {
				temp = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		receiveable = DisplayUtils.parseMoney("###", temp);
		tv_transfer_start_receivable.setText(receiveable);
		return receiveable;
	}

	/**
	 * 收款
	 *
	 * @param view view
	 */
	@OnClick(R.id.btn_transfer_pos_pay)
	private void posPayClick(View view) {
		if (entity == null) {
			ToastUtil.showInfo("过户数据错误，请返回列表页刷新数据，重新提交过户");
			return;
		}
		if (validateInput()) {

			TransferEntity transferEntity = entity;

			transferEntity.setTransfer_price(et_transfer_start_price.getText().toString().trim());//复检后车辆成交价
			transferEntity.setTransfer_remission(et_transfer_start_reduce.getText().toString().trim());//复检减免服务费
			// 是否包过户
			if (String.valueOf(1).equals(entity.getTransfer_free())) {
				transferEntity.setTransfer_amount(et_transfer_start_fee.getText().toString().trim());//过户费用
			} else {
				transferEntity.setTransfer_amount("0");//过户费用
			}
			transferEntity.setComment(et_transfer_start_comment.getText().toString());
			//是否贷款
			transferEntity.setIs_loan(swb_is_loan.isChecked() ? 1 : 0);
			//车主定金转服务费
			if (ownerEarnestToService) {
				transferEntity.setUse_owner(1);
			} else {
				transferEntity.setUse_owner(0);
			}
			//应付车款/担保车款
			transferEntity.setTransfer_vehicle_price(et_transfer_start_pay_for_car.getText().toString().trim());
			//刷卡手续费
			transferEntity.setTransfer_fee(et_transfer_start_pos_fee.getText().toString().trim());
			//悟空卡
			transferEntity.setInsurance(et_transaction_prepay_wukong.getText().toString().trim());
			//其他支出
			transferEntity.setOther_cost(StringUtil.parseInt(et_other_cost.getText().toString(), 0));
			Intent intent = new Intent(this, TransferPosPayNewActivity.class);
			intent.putExtra("transfer", transferEntity);//
			intent.putExtra("receiveable", receiveable);//应收
			intent.putStringArrayListExtra("originPhoto", transferPhotoPathOrigin);//原照片
			if (transferPhotoSelectedPath != null && transferPhotoSelectedPath.size() > 0) {
				intent.putExtra("selectPhoto", transferPhotoSelectedPath);//新选照片
			}
			startActivityForResult(intent, REQUEST_CODE_POSPAY);
		}
	}

	/**
	 * 用户输入校验
	 * 0、复检后车辆成交价大于0（忠旭要求）
	 * 1、选择通过公司转账---需填写转账金额
	 * 2、通过公司转账金额应大于（剩余服务费-复检收服务费减免）
	 * 3、通过公司转账金额应小于（剩余服务费-复检收服务费减免+剩余车款）
	 * 4、通过公司转账金额应大于 收款历史总额（除手续费）
	 */
	private boolean validateInput() {
		boolean result = true;

		if (TextUtils.isEmpty(et_transfer_start_price.getText().toString())
				|| Double.valueOf(et_transfer_start_price.getText().toString()) <= 0) {
			ToastUtil.showText("请填写复检后车辆成交价，并且复检后车辆成交价应大于0");
			return false;
		}

		if (TextUtils.isEmpty(et_transfer_start_reduce.getText().toString())) {
			ToastUtil.showText("请填写复检减免服务费，无减免请填0");
			return false;
		}

		if (String.valueOf(1).equals(entity.getTransfer_free())//包过户
				&& TextUtils.isEmpty(et_transfer_start_fee.getText().toString())) {
			ToastUtil.showText("请填写过户费用");
			return false;
		}

		if (TextUtils.isEmpty(et_transfer_start_pay_for_car.getText().toString())) {
			if (isBackConsignment) {
				ToastUtil.showText("请填写应付车款，没有请填0");
			} else {
				ToastUtil.showText("请填写担保车款，没有请填0");
			}
			return false;
		}
		//担保/应付金额不能大于剩余车款
		if (Double.valueOf(et_transfer_start_pay_for_car.getText().toString()) > Double.valueOf(calculationPriceRemaind())) {
			if (isBackConsignment) {
				ToastUtil.showText("应付车款金额不能大于剩余车款");
			} else {
				ToastUtil.showText("担保车款金额不能大于剩余车款");
			}
			return false;
		}
		//担保/应付金额不能为负数
		if (Double.valueOf(et_transfer_start_pay_for_car.getText().toString()) < 0) {
			if (isBackConsignment) {
				ToastUtil.showText("应付车款金额不合法");
			} else {
				ToastUtil.showText("担保车款金额不合法");
			}
			return false;
		}

		if (TextUtils.isEmpty(et_transaction_prepay_wukong.getText().toString())) {
			ToastUtil.showText("请填写悟空服务卡");
			return false;
		}

		//没有选择小票或合同图片，显示提示
		if (transferPhotoPath == null || this.transferPhotoPath.size() == 0) {
			ToastUtil.showTextLong("没有上传合同及转账照片");
			return false;
		}

		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == TaskConstants.PREPAY_SELECT_PHOTO) { // 选择照片界面
			if (data != null) {
				List<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
				for (String photo : photos) {
					transferPhotoSelectedPath.add(photo);
					transferPhotoPath.add(photo);
				}
			}
			if (transferPhotoViewAdapter == null) {
				transferPhotoViewAdapter = new TransferPhotoViewAdapter(TransferStartActivity.this, transferPhotoPath, TransferStartActivity.this);
				recyclerview_contract.setAdapter(transferPhotoViewAdapter);
			}
			transferPhotoViewAdapter.refreshData(transferPhotoPath);
			DisplayUtils.reSizeRecyclerView(this, recyclerview_contract, transferPhotoPath, false);
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_POSPAY) {//收款---直接finish
			setResult(RESULT_OK);
			finish();
		}
	}


	@Override
	public void onSelectPhoto(View view) {
		PopupWindowUtil.UploadPicturePopup(TransferStartActivity.this, view);
	}

	@Override
	public void onViewPhoto(int position) {

	}

	@Override
	public void onDeletePhoto(int position, String tag) {
		if (position < ContractPicOriginCount) {
			transferPhotoPathOrigin.remove(position);
			transferPhotoPath.remove(position);
			ContractPicOriginCount--;
		} else {
			transferPhotoPath.remove(position);
			transferPhotoSelectedPath.remove(position - ContractPicOriginCount);
		}
		transferPhotoViewAdapter.refreshData(transferPhotoPath);
		DisplayUtils.reSizeRecyclerView(this, recyclerview_contract, transferPhotoPath, false);
	}


	/**
	 * 复检后成交价
	 */
	private TextWatcher textWatcherPrice = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s.toString())) {
				tv_transfer_start_remaind_price.setText(calculationPriceRemaind());
				if (!swb_is_loan.isChecked())
					et_transfer_start_pay_for_car.setText(calculationPriceRemaind());
			}
		}
	};


	/**
	 * 复检后减免服务费
	 */
	private TextWatcher textWatcherReceivable = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			calulationReceiveable();
		}
	};

	/***
	 * @param isChecked true 贷款 false 非贷款
	 */
	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
		//贷款  禁用 然后显示0
		if (isChecked) {
			et_transfer_start_pay_for_car.setEnabled(false);
			et_transfer_start_pay_for_car.setText("0");
		}
		//不贷款
		else {
			et_transfer_start_pay_for_car.setEnabled(true);
			//担保/应付金额为剩余车款全款
			et_transfer_start_pay_for_car.setText(calculationPriceRemaind());
			//回购车/寄售车
			if (entity.getVehicle_type() == 1 || entity.isConsignment()) {
				et_transfer_start_pay_for_car.setEnabled(false);
			}
		}
	}
}