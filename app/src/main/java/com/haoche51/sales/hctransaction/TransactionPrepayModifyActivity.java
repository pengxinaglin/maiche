package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.entity.ExtInfo;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.hctransfer.TransferPhotoViewAdapter;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.CalculationUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCArithUtil;
import com.haoche51.sales.util.PopupWindowUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.TransactionQiNiuUploadUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * 修改过户信息
 * <p/>
 * 如果订单状态是 2 合同未通过------修改所有内容
 * 如果订单状态是 6 预定成功  ------有限制修改
 * 如果订单状态是 7 交易成功  ------只能自行改公司
 * <p/>
 * Created by mac on 15/9/27.
 */
public class TransactionPrepayModifyActivity extends CommonBaseActivity implements TransferPhotoViewAdapter.OnPhotoListChangedListener {

	@ViewInject(R.id.spn_transfer_type)
	private Spinner spn_transfer_type;

	@ViewInject(R.id.et_transfer_final_price)
	@Required(order = 1, message = "成交价不能为空")
	private EditText et_transfer_final_price;

	@ViewInject(R.id.et_transfer_service_price)
	@Required(order = 2, message = "服务费不能为空")
	private EditText et_transfer_service_price;

	@ViewInject(R.id.ll_transfer_company_layout)
	private LinearLayout ll_transfer_company_layout;

	@ViewInject(R.id.et_transfer_buyer_prepay)
	@Required(order = 3, message = "买家给公司定金不能为空")
	private EditText et_transfer_buyer_prepay;

	@ViewInject(R.id.et_transfer_seller_prepay)
	@Required(order = 4, message = "买家给车主定金不能为空")
	private EditText et_transfer_seller_prepay;

	@ViewInject(R.id.et_transfer_seller_company_prepay)
	@Required(order = 5, message = "车主给公司定金不能为空")
	private EditText et_transfer_seller_company_prepay;

	@ViewInject(R.id.ll_transaction_buyer_owner)
	private LinearLayout ll_transaction_buyer_owner;
	@ViewInject(R.id.et_transaction_buyer_owner)
	private EditText et_transaction_buyer_owner;//已付车款

	@ViewInject(R.id.rg_transfer_buyer_emigrate)
	private RadioGroup rg_transfer_buyer_emigrate;

	@ViewInject(R.id.rg_transfer_buyer_resident)
	private RadioGroup rg_transfer_buyer_resident;

	@ViewInject(R.id.rg_transfer_buyer_temp_resident)
	private RadioGroup rg_transfer_buyer_temp_resident;

	@ViewInject(R.id.rg_transfer_company_seller)
	private RadioGroup rg_transfer_company_seller;

	@ViewInject(R.id.rg_transfer_company_buyer)
	private RadioGroup rg_transfer_company_buyer;

	@ViewInject(R.id.rg_transfer_seller_present)
	private RadioGroup rg_transfer_seller_present;

	@ViewInject(R.id.rg_transfer_buyer_present)
	private RadioGroup rg_transfer_buyer_present;

	@ViewInject(R.id.rg_transfer_loan)
	private RadioGroup rg_transfer_loan;

	@ViewInject(R.id.ll_transfer_time)
	private LinearLayout ll_transfer_time;//过户地点
	@ViewInject(R.id.tv_transfer_time)
	private TextView tv_transfer_time;//过户时间

	@ViewInject(R.id.ll_transfer_address)
	private LinearLayout ll_transfer_address;//过户地点
	@ViewInject(R.id.et_transfer_address)
	private EditText et_transfer_address;

	@ViewInject(R.id.ll_transfer_deal_price)
	private LinearLayout ll_transfer_deal_price;//成本价
	@ViewInject(R.id.et_transfer_deal_price)
	private EditText et_transfer_deal_price;

	@ViewInject(R.id.tv_buyer_pay_company_moeny)
	private TextView tv_buyer_pay_company_moeny;//应付车款/担保车款
	@ViewInject(R.id.et_buyer_pay_company_moeny)
	private EditText et_buyer_pay_company_moeny;

	//其他支出
	@ViewInject(R.id.et_other_cost)
	private EditText et_other_cost;

	@ViewInject(R.id.ll_modify_transfer_diff)
	private LinearLayout ll_modify_transfer_diff;

	@ViewInject(R.id.ll_transfer_contract_photo)
	private LinearLayout ll_transfer_contract_photo;
	@ViewInject(R.id.ll_transfer_photo)
	private LinearLayout ll_transfer_photo;

	@ViewInject(R.id.ll_transfer_modify_preferential)
	private LinearLayout ll_transfer_modify_preferential;
	@ViewInject(R.id.ll_transfer_ext_info)
	private LinearLayout ll_transfer_ext_info;

	@ViewInject(R.id.ll_transfer_company_free)
	private LinearLayout ll_transfer_company_free;
	@ViewInject(R.id.ll_transfer_cash_reduction_money)
	private LinearLayout ll_transfer_cash_reduction_money;
	@ViewInject(R.id.ll_transfer_remark)
	private LinearLayout ll_transfer_remark;

	//买家姓名
	@ViewInject(R.id.et_buyer_name)
	private EditText et_buyer_name;
	//买家身份证号码
	@ViewInject(R.id.et_buyer_id)
	private EditText et_buyer_id;
	//车辆VIN码
	@ViewInject(R.id.et_vehicle_vin)
	private EditText et_vehicle_vin;

	/**
	 * 质保方式
	 */
	@ViewInject(R.id.spn_warranty_type)
	private Spinner spn_warranty_type;
	/**
	 * 质保到期时间
	 */
	@ViewInject(R.id.tv_warranty_time)
	private TextView tv_warranty_time;

	/**
	 * 备注
	 */
	@ViewInject(R.id.tv_transfer_remark_count)
	private TextView tv_transfer_remark_count;
	@ViewInject(R.id.et_transfer_remark)
	private EditText et_transfer_remark;
	/**
	 * 减免金额
	 */
	@ViewInject(R.id.et_transfer_cash_reduction_money)
	private EditText et_transfer_cash_reduction_money;
	/**
	 * 减免金额
	 */
	@ViewInject(R.id.et_transaction_wukong)
	private EditText et_transaction_wukong;

	/**
	 * 优惠券
	 */
	@ViewInject(R.id.tv_transfer_coupons_type)
	private TextView tv_transfer_coupons_type;
	/**
	 * 选择礼品
	 */
	@ViewInject(R.id.spn_transfer_gift_type)
	private TextView spn_transfer_gift_type;

	@ViewInject(R.id.tv_transfer_service_final_price)
	private EditText tv_transfer_service_final_price;

	@ViewInject(R.id.recycler_contract_photo)
	private RecyclerView recycler_contract_photo;

	@ViewInject(R.id.recycler_transfer_photo)
	private RecyclerView recycler_transfer_photo;

	/**
	 * 支付差价金额
	 */
	@ViewInject(R.id.tv_layout_transfer_final_payment)
	private TextView tv_layout_transfer_final_payment;

	/**
	 * 支付差价
	 */
	@ViewInject(R.id.ll_modify_transfer_diff_line)
	private View ll_modify_transfer_diff_line;
	@ViewInject(R.id.tv_modify_transfer_diff_pay)
	private TextView tv_modify_transfer_diff_pay;

	@ViewInject(R.id.tv_bottom_commit)
	private TextView tv_bottom_commit;

	TransactionTaskEntity mTask;
	ArrayList<String> transferPhotoPath = new ArrayList<>();
	ArrayList<String> transferPhotoSelectedPath = new ArrayList<>();
	private TransferPhotoViewAdapter transferPhotoViewAdapter;
	private int viewCount = 0;
	private int transferModeChange = -1;
	private static final int companyToSelf = 1;
	private static final int selfToCompany = 2;
	private final int KEY_REQUEST_POSPAY = 2;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_modify_trans_info);
		ViewUtils.inject(this);
		registerTitleBack();
		setScreenTitle(R.string.hc_modify_transfer);
		mTask = (TransactionTaskEntity) getIntent().getSerializableExtra("mTaskEntity");
		if (mTask == null) {
			return;
		}

		if (mTask.getPrepay_transfer_pic() != null) {
			transferPhotoPath = mTask.getPrepay_transfer_pic();
			viewCount = mTask.getPrepay_transfer_pic().size();
		}

		switch (mTask.getTransfer_mode()) {
			case 0:
				companyTrans = true;
				spn_transfer_type.setSelection(0);//公司过户
				ll_transfer_company_layout.setVisibility(View.GONE);
				ll_transfer_modify_preferential.setVisibility(View.GONE);
				ll_transfer_ext_info.setVisibility(View.GONE);
				ll_transfer_contract_photo.setVisibility(View.GONE);
				ll_transfer_photo.setVisibility(View.GONE);
				break;
			case 1:
				companyTrans = false;
				spn_transfer_type.setSelection(1);//自行过户
				ll_transfer_time.setVisibility(View.GONE);
				ll_transfer_address.setVisibility(View.GONE);
				ll_transfer_company_layout.setVisibility(View.GONE);
				ll_transfer_modify_preferential.setVisibility(View.VISIBLE);
				ll_transfer_company_free.setVisibility(View.GONE);
				ll_transfer_company_layout.setVisibility(View.GONE);
				ll_transfer_cash_reduction_money.setVisibility(View.VISIBLE);
				ll_transfer_contract_photo.setVisibility(View.GONE);
				ll_transfer_photo.setVisibility(View.GONE);
				ll_transfer_ext_info.setVisibility(View.GONE);
				ll_transfer_remark.setVisibility(View.VISIBLE);

				setCannotModify(et_transfer_final_price);
				setCannotModify(et_transfer_service_price);
				setCannotModify(tv_transfer_service_final_price);
				setCannotModify(tv_transfer_coupons_type);
				setCannotModify(spn_transfer_gift_type);
				setCannotModify(et_transfer_cash_reduction_money);
				break;
		}
		ll_transaction_buyer_owner.setVisibility(View.VISIBLE);
		setCannotModify(et_transaction_buyer_owner);
	}

	@Override
	protected void initData() {

		if (mTask.getTransfer_time() == 0) {
			tv_transfer_time.setText("");
		} else {
			tv_transfer_time.setText(UnixTimeUtil.format(mTask.getTransfer_time()));
		}

		if (!TextUtils.isEmpty(mTask.getTransfer_place())) {
			et_transfer_address.setText(mTask.getTransfer_place());
		}

		et_other_cost.setText(String.valueOf(mTask.getOther_cost()));

		//预定时担保车款
		int prepay_vehicle_price = StringUtil.parseInt(mTask.getPrepay_vehicle_price(), 0);
		//已付车款
		et_transaction_buyer_owner.setText(String.valueOf(prepay_vehicle_price));

		switch (mTask.getVehicle_type()) {
			case 1://回购
				//不显示成本价
				ll_transfer_deal_price.setVisibility(View.GONE);
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount2));
				setCannotModify(et_buyer_pay_company_moeny);
				break;
			case 3:
			case 4:
				//实体寄售车
				//显示成本价
				ll_transfer_deal_price.setVisibility(View.VISIBLE);
				if (mTask.getDeal_price() > 0) {
					et_transfer_deal_price.setText(String.valueOf(HCArithUtil.div(mTask.getDeal_price(), 10000)));//成本价
				}
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount2));
				//应付车款 = 成交价 - 已付车款
				int r = StringUtil.parseInt(mTask.getPrice(), 0) - StringUtil.parseInt(mTask.getPrepay_vehicle_price(), 0);
				et_buyer_pay_company_moeny.setText(String.valueOf(HCArithUtil.div(r, 10000)));
				setCannotModify(et_buyer_pay_company_moeny);
				break;
			default:
				//不显示成本价
				ll_transfer_deal_price.setVisibility(View.GONE);
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount));
				et_buyer_pay_company_moeny.setEnabled(true);
				et_buyer_pay_company_moeny.addTextChangedListener(textWatcherPrice);
				break;
		}

		//质保方式
		if (mTask.getWarranty_type() > 0)
			spn_warranty_type.setSelection(mTask.getWarranty_type());

		//质保到期时间
		if (mTask.getWarranty_time() > 0)
			tv_warranty_time.setText(UnixTimeUtil.format(mTask.getWarranty_time(), UnixTimeUtil.YEAR_MONTH_DAY_PATTERN));

		if (!TextUtils.isEmpty(mTask.getPrepay_comment())) {
			et_transfer_remark.setText(mTask.getPrepay_comment());
		}

		et_transfer_service_price.setText(DisplayUtils.parseMoney("###", Math.ceil(Double.valueOf(mTask.getTheory_commission()))));


		if (!TextUtils.isEmpty(mTask.getPrepay())) {
			tv_transfer_service_final_price.setText(mTask.getCommission());
		}

		if (!TextUtils.isEmpty(mTask.getManager_remission())) {
			et_transfer_cash_reduction_money.setText(mTask.getManager_remission() + "");
		}


		if (mTask.getCoupon_info() != null) {
			tv_transfer_coupons_type.setText(mTask.getCoupon_info().getTitle() + mTask.getCoupon_info().getAmount() + "元");
			tv_transfer_coupons_type.setTag(mTask.getCoupon_info());
		}


		if (mTask.getAward_info() != null) {
			spn_transfer_gift_type.setText(mTask.getAward_info().getTitle());
		}

		if (!TextUtils.isEmpty(mTask.getInsurance())) {
			et_transaction_wukong.setText(mTask.getInsurance());
		}

		//买家姓名
		if (!TextUtils.isEmpty(mTask.getBuyer_name())) {
			et_buyer_name.setText(mTask.getBuyer_name());
		}
		//买家身份证号码
		if (!TextUtils.isEmpty(mTask.getBuyer_identify())) {
			et_buyer_id.setText(mTask.getBuyer_identify());
		}
		//车辆VIN码
		if (!TextUtils.isEmpty(mTask.getVin_code())) {
			et_vehicle_vin.setText(mTask.getVin_code());
		}

		//合同照片
		GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		this.recycler_contract_photo.setLayoutManager(layoutManager);
		this.recycler_contract_photo.setHasFixedSize(true);

		//合同照片
		TransferPhotoViewAdapter contractPhotoViewAdapter = new TransferPhotoViewAdapter(TransactionPrepayModifyActivity.this, mTask.getContract_pic(), null);
		contractPhotoViewAdapter.setView(true);
		recycler_contract_photo.setAdapter(contractPhotoViewAdapter);

		//转账照片
		GridLayoutManager layoutManager2 = new GridLayoutManager(this, 4);
		layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
		this.recycler_transfer_photo.setLayoutManager(layoutManager2);
		this.recycler_transfer_photo.setHasFixedSize(true);
		//转账照片
		transferPhotoViewAdapter = new TransferPhotoViewAdapter(TransactionPrepayModifyActivity.this, transferPhotoPath, this);
		transferPhotoViewAdapter.setViewCount(viewCount);
		transferPhotoViewAdapter.setView(false);
		recycler_transfer_photo.setAdapter(transferPhotoViewAdapter);

		reSizeRecyclerView();

		Gson gson = new Gson();
		try {
			ExtInfo extInfo = gson.fromJson(mTask.getExt_info(), ExtInfo.class);
			if (extInfo != null) {
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getEmigrate()))) {
					((RadioButton) rg_transfer_buyer_emigrate.getChildAt(extInfo.getEmigrate() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getBuyer_present()))) {
					((RadioButton) rg_transfer_buyer_present.getChildAt(extInfo.getBuyer_present() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_buyer()))) {
					((RadioButton) rg_transfer_company_buyer.getChildAt(extInfo.getCompany_buyer() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_seller()))) {
					((RadioButton) rg_transfer_company_seller.getChildAt(extInfo.getCompany_seller() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getResident()))) {
					((RadioButton) rg_transfer_buyer_resident.getChildAt(extInfo.getResident() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getSeller_present()))) {
					((RadioButton) rg_transfer_seller_present.getChildAt(extInfo.getSeller_present() == 1 ? 0 : 1)).setChecked(true);
				}
				if (!TextUtils.isEmpty(String.valueOf(extInfo.getTemp_resident()))) {
					((RadioButton) rg_transfer_buyer_temp_resident.getChildAt(extInfo.getTemp_resident() == 1 ? 0 : 1)).setChecked(true);
				}
				if (extInfo.getLoan() != -1 && !TextUtils.isEmpty(String.valueOf(extInfo.getLoan()))) {
					((RadioButton) rg_transfer_loan.getChildAt(extInfo.getLoan() == 1 ? 0 : 1)).setChecked(true);
				} else {
					((RadioButton) rg_transfer_loan.getChildAt(1)).setChecked(true);
				}
			}

			if (!TextUtils.isEmpty(mTask.getPrice())) {
				double bigDecimal = HCArithUtil.div(Double.valueOf(mTask.getPrice()), 10000);
				String priceOfWan = DisplayUtils.parseMoney("###.###", bigDecimal);
				et_transfer_final_price.setText(priceOfWan);
			}
			if (!TextUtils.isEmpty(mTask.getSeller_prepay())) {
				et_transfer_seller_prepay.setText(String.valueOf(mTask.getSeller_prepay()));
			}
			if (!TextUtils.isEmpty(mTask.getSeller_company_prepay())) {
				et_transfer_seller_company_prepay.setText(mTask.getSeller_company_prepay());
			}
			if (!TextUtils.isEmpty(mTask.getPrepay())) {
				et_transfer_buyer_prepay.setText(String.valueOf(mTask.getPrepay()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		calculationDiff(false);

		et_transfer_deal_price.addTextChangedListener(textWatcherPrice);
		et_transfer_final_price.addTextChangedListener(textWatcherPrice);
		et_transfer_service_price.addTextChangedListener(textWatcherService);
		et_transfer_cash_reduction_money.addTextChangedListener(textWatcherReduction);
		et_transaction_wukong.addTextChangedListener(textWatcherReduction);
		et_transfer_remark.addTextChangedListener(textWatcherRemark);

		resetBottomBtnStatus();
	}

	/**
	 * 重置底部按钮状态
	 */
	private void resetBottomBtnStatus() {
		if (transferModeChange == -1) {
			//未改变过户方式--底部按钮只有提交审核--可点击
			tv_bottom_commit.setVisibility(View.VISIBLE);
		} else {
			tv_bottom_commit.setVisibility(View.GONE);
		}

	}

	@Override
	public void finish() {
		transferModeChange = -1;
		super.finish();
	}

	/**
	 * 设置recyclerView的高度
	 */
	private void reSizeRecyclerView() {
		DisplayUtils.reSizeRecyclerView(this, recycler_contract_photo, mTask.getContract_pic(), true);
		DisplayUtils.reSizeRecyclerView(this, recycler_transfer_photo, transferPhotoPath, false);
	}

	/**
	 * 设置是否是公司过户
	 */
	private void setCompanyPrePay(boolean enabled) {
		if (enabled) {//公司过户
			companyTrans = true;
			switch (mTask.getTransfer_mode()) {
				case 0:
					//公司过户转公司过户
					transferModeChange = -1;

					ll_transfer_time.setVisibility(View.VISIBLE);
					ll_transfer_address.setVisibility(View.VISIBLE);
					ll_transfer_company_layout.setVisibility(View.GONE);
					ll_transfer_modify_preferential.setVisibility(View.GONE);
					ll_transfer_ext_info.setVisibility(View.GONE);
					ll_transfer_company_free.setVisibility(View.GONE);
					ll_transfer_cash_reduction_money.setVisibility(View.GONE);
					ll_transfer_contract_photo.setVisibility(View.GONE);
					ll_transfer_photo.setVisibility(View.GONE);
					ll_transfer_remark.setVisibility(View.VISIBLE);

					break;
				case 1:
					//自行过户转公司过户
					transferModeChange = selfToCompany;

					ll_transfer_time.setVisibility(View.VISIBLE);
					ll_transfer_address.setVisibility(View.VISIBLE);
					ll_transfer_company_layout.setVisibility(View.VISIBLE);
					ll_transfer_modify_preferential.setVisibility(View.VISIBLE);
					ll_transfer_company_free.setVisibility(View.GONE);
					ll_transfer_cash_reduction_money.setVisibility(View.VISIBLE);
					ll_modify_transfer_diff.setVisibility(View.GONE);
					ll_modify_transfer_diff_line.setVisibility(View.GONE);
					ll_transfer_ext_info.setVisibility(View.VISIBLE);
					ll_transfer_contract_photo.setVisibility(View.VISIBLE);
					ll_transfer_photo.setVisibility(View.VISIBLE);
					ll_transfer_remark.setVisibility(View.VISIBLE);

					setCannotModify(et_transfer_final_price);
					setCannotModify(et_transfer_service_price);
					setCannotModify(tv_transfer_coupons_type);
					setCannotModify(spn_transfer_gift_type);
					setCannotModify(et_transfer_cash_reduction_money);

					et_transfer_buyer_prepay.setText(mTask.getCommission());

					break;
			}
		} else {//个人过户
			companyTrans = false;
			switch (mTask.getTransfer_mode()) {
				case 0:
					//公司过户转自行过户
					transferModeChange = companyToSelf;

					ll_transfer_time.setVisibility(View.GONE);
					ll_transfer_address.setVisibility(View.GONE);
					ll_transfer_company_layout.setVisibility(View.VISIBLE);
					ll_transfer_modify_preferential.setVisibility(View.VISIBLE);
					ll_transfer_company_free.setVisibility(View.GONE);
					ll_transfer_cash_reduction_money.setVisibility(View.VISIBLE);
					ll_transfer_ext_info.setVisibility(View.GONE);
					ll_transfer_contract_photo.setVisibility(View.VISIBLE);
					ll_transfer_photo.setVisibility(View.VISIBLE);
					ll_transfer_remark.setVisibility(View.VISIBLE);

					setCannotModify(et_transfer_buyer_prepay);
					setCannotModify(et_transfer_seller_prepay);
					setCannotModify(et_transfer_seller_company_prepay);
					setCannotModify(tv_transfer_service_final_price);
					setCannotModify(tv_transfer_coupons_type);
					setCannotModify(spn_transfer_gift_type);

					break;
				case 1:
					//自行过户转自行过户
					transferModeChange = -1;

					ll_transfer_time.setVisibility(View.GONE);
					ll_transfer_address.setVisibility(View.GONE);
					ll_transfer_company_layout.setVisibility(View.GONE);
					ll_transfer_modify_preferential.setVisibility(View.VISIBLE);
					ll_transfer_company_free.setVisibility(View.GONE);
					ll_transfer_cash_reduction_money.setVisibility(View.VISIBLE);
					ll_transfer_ext_info.setVisibility(View.GONE);
					ll_transfer_contract_photo.setVisibility(View.GONE);
					ll_transfer_photo.setVisibility(View.GONE);
					ll_transfer_remark.setVisibility(View.VISIBLE);

					setCannotModify(et_transfer_final_price);
					setCannotModify(et_transfer_service_price);
					setCannotModify(tv_transfer_coupons_type);
					setCannotModify(spn_transfer_gift_type);
					setCannotModify(et_transfer_cash_reduction_money);
					break;
			}
		}
		resetBottomBtnStatus();
	}

	private boolean companyTrans = true;//公司过户还是自行过户

	private void setCannotModify(View view) {
		view.setEnabled(false);
		view.setClickable(false);
		view.setBackgroundResource(R.color.hc_self_gray_bg);
	}


	@OnItemSelected(R.id.spn_transfer_type)
	private void onTransferTypeSelect(AdapterView<?> adapterView, View view, int position, long l) {
		switch (position) {
			case 0:
				setCompanyPrePay(true);
				break;
			case 1:
				setCompanyPrePay(false);
				break;
		}
	}

	/**
	 * 选择质保方式
	 *
	 * @param adapterView
	 * @param view
	 * @param position
	 * @param l
	 */
	@OnItemSelected(R.id.spn_warranty_type)
	private void onWarrantyTypeCLick(AdapterView<?> adapterView, View view, int position, long l) {
		//0：未选择 1:无质保 2：公司质保 3：第三方质保
		mTask.setWarranty_type(position);

		//选择了无质保
		if (position == 1)
			tv_warranty_time.setText("");//质保到期时间为空
	}


	@OnClick(R.id.tv_transfer_time)
	private void onTransferTimeClick(View v) {
		DisplayUtils.displayTimeWhellYMD(this, tv_transfer_time, R.string.select_transfer_time, true, true);
	}

	/**
	 * 点击质保到期时间
	 *
	 * @param view
	 */
	@OnClick(R.id.tv_warranty_time)
	private void onWarrantyTimeClick(View view) {
		//选择了无质保 不选择质保到期时间
		if (spn_warranty_type.getSelectedItemPosition() == 1) {
			ToastUtil.showInfo("无质保无需选择质保到期时间");
			return;
		}

		DisplayUtils.displayTimeWhellYMD(this, tv_warranty_time, R.string.hc_select_warranty_time, false, false);
	}

	/**
	 * 修改信息
	 */
	@OnClick(R.id.tv_bottom_commit)
	private void modifyInfo(View v) {
		if (mTask != null && valationCheck()) {
			commitToServer();
		}
	}


	@OnClick(R.id.ll_modify_transfer_diff_pay)
	private void onDiffPayClick(View view) {

		if (mTask == null) {
			return;
		}
		if (!valationCheck()) {
			return;
		}

		saveData(null);

		Intent intent = new Intent(this, PayDepositActivity.class);

		// 买家应付，公司转自行：买家应付=服务费-定金（最小是0）；自行转公司：买家应付=定金-服务费（最小是0）；
		int buyerPayMoney = 0;
		if (transferModeChange == companyToSelf && !TextUtils.isEmpty(tv_layout_transfer_final_payment.getText().toString())) {
			buyerPayMoney = Integer.valueOf(tv_layout_transfer_final_payment.getText().toString());
			mTask.setComment(et_transfer_remark.getText().toString() + "；修改过户方式应补差价：" + tv_layout_transfer_final_payment.getText().toString());
		} else if (transferModeChange == selfToCompany) {
			buyerPayMoney = (int) HCArithUtil.sub(Double.valueOf(et_transfer_buyer_prepay.getText().toString()), Double.valueOf(mTask.getCommission()));
		}
		intent.putExtra(PayDepositActivity.EXTRA_BUYER_PAY_MONEY, buyerPayMoney);
		intent.putExtra(PayDepositActivity.EXTRA_GUARANTEE_MONEY, (int) HCArithUtil.mul(StringUtil.parseDouble(et_buyer_pay_company_moeny.getText().toString(), 0), 10000));

		if (transferPhotoSelectedPath != null && transferPhotoSelectedPath.size() > 0) {
			intent.putExtra(PayDepositActivity.EXTRA_PHOTO_PATH, transferPhotoSelectedPath);
		}
		if (transferPhotoPath != null && transferPhotoPath.size() > 0) {
			intent.putExtra(PayDepositActivity.EXTRA_ORIGIN_PHOTO_PATH, transferPhotoPath);
		}

		intent.putExtra(PayDepositActivity.EXTRA_TRANS_TASK_ENTITY, mTask);
		intent.putExtra(PayDepositActivity.EXTRA_IS_COMAPNY_TRANS, companyTrans);

		startActivityForResult(intent, KEY_REQUEST_POSPAY);
	}


	public boolean valationCheck() {
		if (spn_warranty_type.getSelectedItemPosition() == 0) {
			showErrorMsg(spn_warranty_type, "请选择一种质保方式");
			return false;
		}

		if (spn_warranty_type.getSelectedItemPosition() != 1 && TextUtils.isEmpty(tv_warranty_time.getText().toString())) {
			showErrorMsg(tv_warranty_time, "请选择质保到期时间");
			return false;
		}

		if (companyTrans) {//公司过户才需要设置这些信息validator

			if (TextUtils.isEmpty(tv_transfer_time.getText().toString())) {
				ToastUtil.showText("请填写过户时间");
				return false;
			}
			if (TextUtils.isEmpty(et_transfer_address.getText().toString())) {
				ToastUtil.showText("请填写过户地点");
				return false;
			}

			if (transferModeChange == selfToCompany) {
				if (TextUtils.isEmpty(et_transfer_buyer_prepay.getText().toString())) {
					ToastUtil.showText("请填写买家定金");
					return false;
				}
				if (TextUtils.isEmpty(et_transfer_seller_company_prepay.getText().toString())) {
					ToastUtil.showText("请填写车主定金");
					return false;
				}
				if (Double.valueOf(et_transfer_buyer_prepay.getText().toString()) < Double.valueOf(mTask.getCommission())) {
					ToastUtil.showText("买家定金不能低于" + mTask.getCommission());
					return false;
				}
			}

			//自行转公司的时候做判断
			if (mTask.getTransfer_mode() == 1 && companyTrans == true
					&& Double.valueOf(et_transfer_buyer_prepay.getText().toString()) < Double.valueOf(mTask.getCommission())) {
				ToastUtil.showText("买家定金不能低于" + mTask.getCommission());
				return false;
			}
		} else {
			if (TextUtils.isEmpty(et_transaction_wukong.getText().toString())) {
				ToastUtil.showText("请填写悟空保驾服务卡");
				return false;
			}
			if (et_transfer_final_price.getText().toString().startsWith(".")) {
				showErrorMsg(et_transfer_final_price, getString(R.string.price_format_error));
				return false;
			}
			if (TextUtils.isEmpty(et_transfer_final_price.getText().toString())
					|| Double.valueOf(et_transfer_final_price.getText().toString()) == 0) {
				showErrorMsg(et_transfer_final_price, "填写成交价");
				return false;
			}
			if (et_transfer_service_price.getText().toString().startsWith(".")) {
				showErrorMsg(et_transfer_service_price, getString(R.string.commission_format_error));
				return false;
			}
		}

		if (mTask.isConsignment())
			if (TextUtils.isEmpty(et_transfer_deal_price.getText().toString())
					|| Double.valueOf(et_transfer_deal_price.getText().toString()) == 0) {
				showErrorMsg(et_transfer_deal_price, "填写成本价");
				return false;
			}

		if (TextUtils.isEmpty(et_buyer_name.getText().toString())) {
			ToastUtil.showText("请填写买家姓名");
			return false;
		}

		if (TextUtils.isEmpty(et_buyer_id.getText().toString())) {
			ToastUtil.showText("请填写买家身份证号码");
			return false;
		}

		if (TextUtils.isEmpty(et_vehicle_vin.getText().toString())) {
			ToastUtil.showText("请填写车辆VIN码");
			return false;
		}


		if (et_buyer_id.getText().toString().length() != 18) {
			showErrorMsg(et_buyer_id, "填写身份证号码位数不正确");
			return false;
		}

		if (et_vehicle_vin.getText().toString().length() != 17) {
			showErrorMsg(et_vehicle_vin, "填写车辆VIN号码位数不正确");
			return false;
		}

		return true;
	}

	private void commitToServer() {
		//上传图片
		if (transferPhotoSelectedPath != null && transferPhotoSelectedPath.size() > 0) {
			uploadImage();
		} else {
			saveData(null);
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			//调用接口更改信息
			AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferMessage(mTask), this, 0);
		}
	}

	private void saveData(ArrayList<String> keys) {

		mTask.setTransfer_comment(et_transfer_remark.getText().toString());

        /*理论服务费*/
		mTask.setTheory_commission(et_transfer_service_price.getText().toString());

		if (keys != null) {
			if (mTask.getPrepay_transfer_pic_origin() != null && mTask.getPrepay_transfer_pic_origin().size() > 0) {
				for (String key : mTask.getPrepay_transfer_pic_origin()) {
					keys.add(key);
				}
			}

			mTask.setPrepay_transfer_pic(keys);
		} else {
			mTask.setPrepay_transfer_pic(null);
		}
		mTask.setContract_pic(null);

		if (companyTrans) {//公司过户才需要设置这些信息
		    /*过户方式*/
			mTask.setTransfer_mode(0);
			/*过户时间*/
			mTask.setTransfer_time(UnixTimeUtil.getUnixTime(tv_transfer_time.getText().toString()));
			/*过户地点*/
			mTask.setTransfer_place(et_transfer_address.getText().toString());
			/*买家给车主定金*/
			mTask.setSeller_prepay(et_transfer_seller_prepay.getText().toString());
			/*买家给公司定金*/
			mTask.setPrepay(et_transfer_buyer_prepay.getText().toString());
			/*车主给公司定金*/
			mTask.setSeller_company_prepay(et_transfer_seller_company_prepay.getText().toString());
			/*是否包过户*/
//      mTask.setTransfer_free(rg_transfer_company_free.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);//是否公司包过户 包过户1 不包过户0

			ExtInfo extInfo = new ExtInfo();
			extInfo.setEmigrate(rg_transfer_buyer_emigrate.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setResident(rg_transfer_buyer_resident.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setTemp_resident(rg_transfer_buyer_temp_resident.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setCompany_seller(rg_transfer_company_seller.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setCompany_buyer(rg_transfer_company_buyer.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setSeller_present(rg_transfer_seller_present.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setBuyer_present(rg_transfer_buyer_present.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setLoan(rg_transfer_loan.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			Gson gson = new Gson();
			mTask.setExt_info(gson.toJson(extInfo));
			mTask.setInsurance(null);
		} else {
			// 自行过户
			mTask.setTransfer_mode(1);
			// 成交价
			double bigDecimalPrice = HCArithUtil.mul(Double.valueOf(et_transfer_final_price.getText().toString()), 10000);
			mTask.setPrice(bigDecimalPrice + "");
			// 理论服务费
			mTask.setTheory_commission(et_transfer_service_price.getText().toString());
			// 服务费
			mTask.setCommission(tv_transfer_service_final_price.getText().toString());
			// 已减金额
			if (!TextUtils.isEmpty(et_transfer_cash_reduction_money.getText().toString())) {
				mTask.setManager_remission(et_transfer_cash_reduction_money.getText().toString());
			}
			mTask.setInsurance(et_transaction_wukong.getText().toString());
			// 应补差价
			mTask.setCommission_info(tv_layout_transfer_final_payment.getText().toString());
		}
		//是寄售车 成本价
		if (mTask.isConsignment()) {
			int deal_price = (int) HCArithUtil.mul(Double.valueOf(et_transfer_deal_price.getText().toString()), 10000);
			mTask.setDeal_price(deal_price);
		}
		//质保到期时间
		mTask.setWarranty_time(UnixTimeUtil.getUnixTime(tv_warranty_time.getText().toString(), UnixTimeUtil.YEAR_MONTH_DAY_PATTERN));
		mTask.setBuyer_name(et_buyer_name.getText().toString());
		mTask.setBuyer_identify(et_buyer_id.getText().toString());
		mTask.setVin_code(et_vehicle_vin.getText().toString());
		//其他支出
		mTask.setOther_cost(StringUtil.parseInt(et_other_cost.getText().toString(), 0));
		//公司转自行,计算差价
		if (transferModeChange == companyToSelf) {
			//应付车款差价
			mTask.setDiff_prepay_vehicle_price(String.valueOf(HCArithUtil.mul(StringUtil.parseDouble(et_buyer_pay_company_moeny.getText().toString(), 0), 10000)));
		} else {
			mTask.setDiff_prepay_vehicle_price("0");
		}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_MODIFY_TRANSFER_MESSAGE)) {//修改订单信息
			switch (response.getErrno()) {
				case 0:
					ToastUtil.showInfo(getString(R.string.hc_modify_succ));
					//成功
					HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
					finish();
					break;
				default:
					ToastUtil.showInfo(response.getErrmsg());
					finish();
					break;
			}
		} else if (action.equals(HttpConstants.ACTION_GET_TRANSACTION_BY_ID)) {
			switch (response.getErrno()) {
				case 0:
					TransactionTaskEntity mTaskEntity = HCJsonParse.parseTransTaskEntity(response.getData());
					if (mTaskEntity != null) {
						mTask = mTaskEntity;
					}
					break;
				default:
					break;
			}

		}
	}

	private void uploadImage() {
		TransactionQiNiuUploadUtil qiniuUploadImageUtil = new TransactionQiNiuUploadUtil(this, this.transferPhotoSelectedPath);
		qiniuUploadImageUtil.startUpload(new TransactionQiNiuUploadUtil.QiniuUploadListener() {
			@Override
			public void onSuccess(List<String> keys) {
				//上传完成，调用取消任务
				saveData((ArrayList<String>) keys);
				tv_bottom_commit.setEnabled(false);
				AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferMessage(mTask), TransactionPrepayModifyActivity.this, 0);

			}

			@Override
			public void onFailed() {
				//取消上传，重新上传
			}
		});
	}

	/**
	 * 限制输入两个小数,四位整数,,,用于成交价
	 */
	public TextWatcher textWatcherPrice = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if ("".equals(editable.toString())) {
				return;
			}

			String temp = editable.toString();
			int posDot = temp.indexOf(".");
			if (posDot < 0) {//整数
				if (temp.length() > 3) {
					editable.delete(3, 4);
				}
			} else if (posDot >= 0 && posDot < 4) {
				if (temp.length() - posDot > 4) {
					editable.delete(posDot + 4, posDot + 5);
				}
			} else if (posDot > 3) {
				editable.delete(posDot - 1, posDot);
			}
			calculationDiff(true);
		}
	};

	/**
	 * 减免金额输入限制
	 */
	public TextWatcher textWatcherReduction = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			calculationDiff(false);

		}
	};

	/**
	 * 减免金额输入限制
	 */
	public TextWatcher textWatcherService = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			calculationDiff(false);

		}
	};

	private CharSequence temp;
	private int editStart;
	private int editEnd;
	/**
	 * 备注输入限制
	 */
	public TextWatcher textWatcherRemark = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void afterTextChanged(Editable editable) {
			try {
				editStart = et_transfer_remark.getSelectionStart();
				editEnd = et_transfer_remark.getSelectionEnd();
				//已经超出长度了，输入无效
				if (temp.length() > 100) {
					tv_transfer_remark_count.setText("0");
					editable.delete(editStart - 1, editEnd);
					int tempSelection = editStart;
					et_transfer_remark.removeTextChangedListener(this);
					et_transfer_remark.setText(editable);
					et_transfer_remark.setSelection(tempSelection);
					et_transfer_remark.addTextChangedListener(this);
				}
				//更新剩余可输入字数
				tv_transfer_remark_count.setText(String.valueOf(100 - editable.length()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 理论服务费
	 * 减免
	 * 差价=最终服务费
	 */
	private void calculationDiff(boolean calculation) {
		//计算理论服务费
		double theService = 0;
		if (calculation) {
			theService = CalculationUtil.transactionPrepayCalculationTheServicePrice(et_transfer_final_price.getText().toString());
		} else if (!TextUtils.isEmpty(et_transfer_service_price.getText().toString())) {
			theService = Double.valueOf(et_transfer_service_price.getText().toString());
		}

		//车源为回购或寄售时不可编辑应付车款
		if (!et_buyer_pay_company_moeny.isEnabled()) {
			//应付车款 = 成交价 - 已付车款
			double r = HCArithUtil.mul(StringUtil.parseDouble(et_transfer_final_price.getText().toString(), 0), 10000) - StringUtil.parseInt(mTask.getPrepay_vehicle_price(), 0);
			et_buyer_pay_company_moeny.setText(String.valueOf(HCArithUtil.div(r, 10000)));
		}

		//经理减免
		double reduce = 0;
		if (!TextUtils.isEmpty(et_transfer_cash_reduction_money.getText().toString())) {
			reduce = Double.valueOf(et_transfer_cash_reduction_money.getText().toString());
		}

		//服务费
		double service = HCArithUtil.sub(theService, reduce);

		if (calculation) {
			et_transfer_service_price.removeTextChangedListener(textWatcherService);
			et_transfer_service_price.setText(DisplayUtils.parseMoney("#", theService));
			et_transfer_service_price.addTextChangedListener(textWatcherService);
		}

		if (service < 0) {
			service = 0;
		}

		tv_transfer_service_final_price.setText(DisplayUtils.parseMoney("#", service));
		//差价 服务费 - 定金 + 悟空卡 + 应付/担保
		double diff = service;
		if (!TextUtils.isEmpty(et_transfer_buyer_prepay.getText().toString().trim())) {
			diff = HCArithUtil.sub(diff, Double.valueOf(et_transfer_buyer_prepay.getText().toString().trim()));
		}
		if (!TextUtils.isEmpty(et_transaction_wukong.getText().toString().trim())) {
			diff = HCArithUtil.add(diff, Double.valueOf(et_transaction_wukong.getText().toString().trim()));
		}
		if (!TextUtils.isEmpty(et_buyer_pay_company_moeny.getText().toString().trim())) {
			diff = HCArithUtil.add(diff, Double.valueOf(HCArithUtil.mul(StringUtil.parseDouble(et_buyer_pay_company_moeny.getText().toString(), 0), 10000)));
		}

		if (diff < 0) {
			diff = 0;
		}
		tv_layout_transfer_final_payment.setText(DisplayUtils.parseMoney("#", diff));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == TaskConstants.PREPAY_SELECT_PHOTO) { // 选择照片界面
			if (data != null) {
				List<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
				for (String photo : photos) {
					transferPhotoPath.add(photo);
					transferPhotoSelectedPath.add(photo);
				}
			}
			if (transferPhotoViewAdapter == null) {
				transferPhotoViewAdapter = new TransferPhotoViewAdapter(TransactionPrepayModifyActivity.this, transferPhotoPath, TransactionPrepayModifyActivity.this);
				recycler_contract_photo.setAdapter(transferPhotoViewAdapter);
			}
			transferPhotoViewAdapter.notifyDataSetChanged();
			reSizeRecyclerView();
		}

		if (requestCode == KEY_REQUEST_POSPAY) {
			ToastUtil.showInfo(getString(R.string.hc_modify_succ));
			//成功
			HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onSelectPhoto(View view) {
		PopupWindowUtil.UploadPicturePopup(TransactionPrepayModifyActivity.this, view);
	}

	@Override
	public void onViewPhoto(int position) {
	}

	@Override
	public void onDeletePhoto(int position, String tag) {
		transferPhotoPath.remove(position);
		transferPhotoSelectedPath.remove(position - viewCount);
		transferPhotoViewAdapter.notifyDataSetChanged();
//    transferPhotoViewAdapter.refreshData(transferPhotoPath);
		reSizeRecyclerView();
	}
}
