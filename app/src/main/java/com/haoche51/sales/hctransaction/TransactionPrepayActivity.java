package com.haoche51.sales.hctransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.entity.ExtInfo;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.hctransfer.TransferPhotoViewAdapter;
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
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import okhttp3.Call;

public class TransactionPrepayActivity extends CommonBaseActivity implements TransferPhotoViewAdapter.OnPhotoListChangedListener, View.OnFocusChangeListener {

	private LinkedList<Call> netList = new LinkedList<>();

	private final int KEY_REQUEST_POSPAY = 2;
	@ViewInject(R.id.recycler_contract_photo)
	public RecyclerView recycler_contract_photo;
	public ArrayList<String> photoPaths = new ArrayList<String>();//新选择照片list
	public ArrayList<String> originPhotoPaths = new ArrayList<String>();//原有照片list
	public List<String> adapterList = new ArrayList<String>();//用于展示的照片list（包含原有的和新选择的）
	List<Coupon> mCouponList;//礼物
	ArrayAdapter<Coupon> adapter;//礼物适配器
	@ViewInject(R.id.spn_transfer_type)
	private Spinner spn_transfer_type;
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
	/**
	 * 车源编号
	 */
	@ViewInject(R.id.et_vehicle_source_id)
	private EditText et_vehicle_source_id;
	/**
	 * 车型
	 */
	@ViewInject(R.id.et_vehicle_name)
	private TextView et_vehicle_name;
	/**
	 * 成交价
	 */
	@ViewInject(R.id.et_transfer_final_price)
	private EditText et_transfer_final_price;
	/**
	 * 成本价
	 */
	boolean isConsigned;//是否是寄售
	@ViewInject(R.id.ll_transfer_deal_price)
	private LinearLayout ll_transfer_deal_price;
	@ViewInject(R.id.et_transfer_deal_price)
	private EditText et_transfer_deal_price;
	/**
	 * 理论服务费
	 */
	@ViewInject(R.id.et_transfer_service_price)
	private EditText et_transfer_service_price;
	/**
	 * 减免金额
	 */
	@ViewInject(R.id.et_transfer_cash_reduction_money)
	private EditText et_transfer_cash_reduction_money;

	/**
	 * 优惠券
	 */
	@ViewInject(R.id.tv_transfer_coupons_type)
	private TextView tv_transfer_coupons_type;
	/**
	 * 选择礼品
	 */
	@ViewInject(R.id.spn_transfer_gift_type)
	private Spinner spn_transfer_gift_type;
	/**
	 * 买家给车主定金
	 */
	@ViewInject(R.id.et_transfer_seller_prepay)
	private EditText et_transfer_seller_prepay;

	/**
	 * 买家给公司定金
	 */
	@ViewInject(R.id.et_transfer_buyer_prepay)
	private EditText et_transfer_buyer_prepay;
	/**
	 * 车主给公司定金
	 */
	@ViewInject(R.id.et_transfer_seller_company_prepay)
	private EditText et_transfer_seller_company_prepay;
	// 应付车款/担保车款
	boolean isBack;//是否是回购
	@ViewInject(R.id.tv_buyer_pay_company_moeny)
	private TextView tv_buyer_pay_company_moeny;
	@ViewInject(R.id.buyer_pay_company_moeny)
	private EditText buyer_pay_company_moeny;
	// 买家刷卡手续费
	@ViewInject(R.id.buyer_pay_company_fee)
	private EditText buyer_pay_company_fee;
	//其他支出
	@ViewInject(R.id.et_other_cost)
	private EditText et_other_cost;
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
	 * 过户时间
	 */
	@ViewInject(R.id.ll_transfer_time)
	private LinearLayout ll_transfer_time;
	@ViewInject(R.id.tv_transfer_time)
	private TextView tv_transfer_time;//过户时间
	@ViewInject(R.id.ll_transfer_address)
	private LinearLayout ll_transfer_address;//过户地点
	@ViewInject(R.id.et_transfer_address)
	private EditText et_transfer_address;
	@ViewInject(R.id.ll_transfer_money)//公司过户，定金
	private LinearLayout ll_transfer_money;
	@ViewInject(R.id.ll_transfer_ext_info)//公司过户，更多
	private LinearLayout ll_transfer_ext_info;
	/**
	 * 公司包过户
	 */
	@ViewInject(R.id.ll_transfer_company_free)
	private LinearLayout ll_transfer_company_free;
	@ViewInject(R.id.rg_transfer_company_free)
	private RadioGroup rg_transfer_company_free;

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
	 * 服务费
	 */
	@ViewInject(R.id.tv_layout_transfer_final_service_pay)
	private TextView tv_layout_transfer_final_service_pay;
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
			if (TextUtils.isEmpty(editable.toString())) {
				et_transfer_service_price.setText("");
			} else {
				double theService = CalculationUtil.transactionPrepayCalculationTheServicePrice(editable.toString());
				et_transfer_service_price.setText(DisplayUtils.parseMoney("###", Math.ceil(theService)));
				//计算服务费
				double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
						(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
				tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));

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
			}

			try {
				if ((isBack || isConsigned) && !companyTrans) {
					if (TextUtils.isEmpty(et_transfer_final_price.getText().toString())) {
						buyer_pay_company_moeny.setText("");
					} else {
						double mul = HCArithUtil.mul(Double.valueOf(et_transfer_final_price.getText().toString()), 10000);
						buyer_pay_company_moeny.setText(String.valueOf((int) mul));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	/**
	 * 限制输入两个小数,四位整数,,,用于成本价
	 */
	public TextWatcher textWatcherDeal = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if (!TextUtils.isEmpty(editable.toString())) {
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
			}
		}
	};
	/**
	 * 理论服务费输入限制
	 */
	public TextWatcher textWatcherServicePrice = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			//计算服务费
			double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
					(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
			tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));
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
			//计算服务费
			double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
					(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
			tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));
		}
	};
	/**
	 * 备注
	 */
	@ViewInject(R.id.tv_transfer_remark_count)
	private TextView tv_transfer_remark_count;
	@ViewInject(R.id.et_transfer_remark)
	private EditText et_transfer_remark;
	@ViewInject(R.id.ll_transfer_company_layout)
	private LinearLayout ll_transfer_company_layout;
	// 支付定金
	@ViewInject(R.id.btn_transaction_prepay_pay)
	private RelativeLayout btn_transaction_prepay_pay;

	@ViewInject(R.id.tv_transaction_prepay_pay)
	private TextView tv_transaction_prepay_pay;

	private boolean companyTrans = true;//是否公司过户
	private Context mContext;
	private TransactionTaskEntity mTask;
	private Gson gson = new Gson();
	private TransferPhotoViewAdapter transferPhotoViewAdapter;
	private List<CouponEntity> listuserCoupons = new ArrayList<>();
	private int ContractPicOriginCount = 0;
	/**
	 * 备注输入限制
	 */
	public TextWatcher textWatcherRemark = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			try {
				int editStart = et_transfer_remark.getSelectionStart();
				int editEnd = et_transfer_remark.getSelectionEnd();
				//已经超出长度了，输入无效
				if (editable.length() > 100) {
					tv_transfer_remark_count.setText("0");
					et_transfer_remark.removeTextChangedListener(textWatcherRemark);
					editable.delete(editStart - 1, editEnd);
					et_transfer_remark.setText(editable);
					et_transfer_remark.setSelection(et_transfer_remark.getText().toString().length());//将光标移动到最后
					et_transfer_remark.addTextChangedListener(textWatcherRemark);
				}
				//更新剩余可输入字数
				tv_transfer_remark_count.setText(String.valueOf(100 - editable.length()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void initView() {
		setContentView(R.layout.activity_paymoney);
		ViewUtils.inject(this);
		setScreenTitle(R.string.pay_deposit);
		registerTitleBack();

		et_transfer_final_price.addTextChangedListener(textWatcherPrice);
		et_transfer_deal_price.addTextChangedListener(textWatcherDeal);
		et_transfer_cash_reduction_money.addTextChangedListener(textWatcherReduction);
		et_transfer_remark.addTextChangedListener(textWatcherRemark);
		et_transfer_service_price.addTextChangedListener(textWatcherServicePrice);

		setBottomBtnPay();
	}

	/**
	 * 底部按钮支付定金状态
	 */
	private void setBottomBtnPay() {
		tv_transaction_prepay_pay.setText("支付定金");
		tv_transaction_prepay_pay.setTextColor(getResources().getColor(R.color.hc_self_white));
		Drawable drawable = getResources().getDrawable(R.drawable.ic_transaction_prepay);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv_transaction_prepay_pay.setCompoundDrawables(drawable, null, null, null);
	}

	private void initRecyclerView() {
		GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		this.recycler_contract_photo.setLayoutManager(layoutManager);
		this.recycler_contract_photo.setHasFixedSize(true);

		if (transferPhotoViewAdapter == null) {
			transferPhotoViewAdapter = new TransferPhotoViewAdapter(this, adapterList, this);
			recycler_contract_photo.setAdapter(this.transferPhotoViewAdapter);
		}
		transferPhotoViewAdapter.notifyDataSetChanged();
		DisplayUtils.reSizeRecyclerView(this, recycler_contract_photo, adapterList, false);
	}

	@Override
	protected void initData() {
		mContext = this;

		mTask = (TransactionTaskEntity) getIntent().getSerializableExtra("taskEntity");

		if (mTask == null) {
			finish();
			return;
		}

		setData(mTask);
		//加载礼物
		if (mCouponList == null) {
			mCouponList = new ArrayList<>();
			adapter = new ArrayAdapter<Coupon>(this, android.R.layout.simple_spinner_dropdown_item, mCouponList);
			spn_transfer_gift_type.setAdapter(adapter);
			ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
			//请求获取礼品
			Call postPrepayCoupon = AppHttpServer.getInstance().post(HCHttpRequestParam.getPrepayCoupon(), this, 0);
			netList.add(postPrepayCoupon);

			if (!TextUtils.isEmpty(mTask.getVehicle_source_id())) {
				//请求获取优惠券
				Call postCouponList = AppHttpServer.getInstance().post(HCHttpRequestParam.getCouponList(mTask.getBuyer_phone(), 1
						, -1, 1, mTask.getVehicle_source_id(), null, 1), this, 0);
				netList.add(postCouponList);
			}
		}

		initRecyclerView();
		if (TextUtils.isEmpty(et_transfer_service_price.getText().toString())) {
			double theService = CalculationUtil.transactionPrepayCalculationTheServicePrice(et_transfer_final_price.getText().toString());
			et_transfer_service_price.setText(DisplayUtils.parseMoney("###", Math.ceil(theService)));
		}
	}

	private void setData(TransactionTaskEntity task) {
		if (getIntent().getBooleanExtra("isStore", false)) {
			((LinearLayout) et_vehicle_source_id.getParent()).setVisibility(View.VISIBLE);
			((LinearLayout) et_vehicle_name.getParent()).setVisibility(View.VISIBLE);

			et_vehicle_source_id.setOnFocusChangeListener(this);

			String vehicle_source_id = task.getVehicle_source_id();
			if (!TextUtils.isEmpty(vehicle_source_id)) {
				et_vehicle_source_id.setText(vehicle_source_id);
			}

			String vehicle_name = task.getVehicle_name();
			if (!TextUtils.isEmpty(vehicle_name)) {
				et_vehicle_name.setText(vehicle_name);
			}
		} else {
			((LinearLayout) et_vehicle_source_id.getParent()).setVisibility(View.GONE);
			((LinearLayout) et_vehicle_name.getParent()).setVisibility(View.GONE);
		}

		switch (task.getTransfer_mode()) {
			case 0:
				companyTrans = true;
				spn_transfer_type.setSelection(0);//公司过户
				break;
			case 1:
				companyTrans = false;
				spn_transfer_type.setSelection(1);//自行过户
				break;
		}

		setCompanyPrePay(companyTrans);

		if (mTask.getCoupon_info() != null) {
			tv_transfer_coupons_type.setText(mTask.getCoupon_info().getTitle() + mTask.getCoupon_info().getAmount() + "元");
			tv_transfer_coupons_type.setTag(mTask.getCoupon_info());
		}

		ExtInfo extInfo = gson.fromJson(task.getExt_info(), ExtInfo.class);
		if (extInfo != null) {
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getEmigrate())))
				((RadioButton) rg_transfer_buyer_emigrate.getChildAt(extInfo.getEmigrate() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getBuyer_present())))
				((RadioButton) rg_transfer_buyer_present.getChildAt(extInfo.getBuyer_present() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_buyer())))
				((RadioButton) rg_transfer_company_buyer.getChildAt(extInfo.getCompany_buyer() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_seller())))
				((RadioButton) rg_transfer_company_seller.getChildAt(extInfo.getCompany_seller() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getResident())))
				((RadioButton) rg_transfer_buyer_resident.getChildAt(extInfo.getResident() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getSeller_present())))
				((RadioButton) rg_transfer_seller_present.getChildAt(extInfo.getSeller_present() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getTemp_resident())))
				((RadioButton) rg_transfer_buyer_temp_resident.getChildAt(extInfo.getTemp_resident() == 1 ? 0 : 1)).setChecked(true);
			if (!TextUtils.isEmpty(String.valueOf(extInfo.getLoan())))
				((RadioButton) rg_transfer_loan.getChildAt(extInfo.getLoan() == 1 ? 0 : 1)).setChecked(true);
		}

		if (task.getTransfer_mode() == 0) {
			((RadioButton) rg_transfer_company_free.getChildAt(task.getTransfer_free() == 1 ? 0 : 1)).setChecked(true);
		}


		if (!TextUtils.isEmpty(task.getPrice()) && Float.parseFloat(task.getPrice()) != 0) {
			et_transfer_final_price.setText(String.valueOf(Float.parseFloat(task.getPrice()) / 10000));
		}

		switch (task.getVehicle_type()) {
			case 1://回购
				isBack = true;
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount2));
				break;
			case 3:
			case 4:
				//实体寄售车
				isConsigned = true;
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount2));
				//显示成本价
				ll_transfer_deal_price.setVisibility(View.VISIBLE);
				if (task.getDeal_price() > 0) {
					et_transfer_deal_price.setText(Float.parseFloat(String.valueOf((double) (task.getDeal_price() / 10000))) + "");//成本价
				}
				break;
			default:
				isBack = false;
				isConsigned = false;
				tv_buyer_pay_company_moeny.setText(getString(R.string.hc_transaction_guarantee_amount));
				break;
		}

        /*理论服务费*/
		if (!TextUtils.isEmpty(task.getTheory_commission())) {//计算
			et_transfer_service_price.setText(String.valueOf(task.getTheory_commission()));
		}

		//质保方式
		if (mTask.getWarranty_type() > 0)
			spn_warranty_type.setSelection(mTask.getWarranty_type());

		//质保到期时间
		if (mTask.getWarranty_time() > 0)
			tv_warranty_time.setText(UnixTimeUtil.format(mTask.getWarranty_time(), UnixTimeUtil.YEAR_MONTH_DAY_PATTERN));

		if (mTask.getCoupon_info() != null) {
			tv_transfer_coupons_type.setText(mTask.getCoupon_info().getTitle() + mTask.getCoupon_info().getAmount() + "元");
			tv_transfer_coupons_type.setTag(mTask.getCoupon_info());
		}

		if (!TextUtils.isEmpty(task.getSeller_prepay()) && Float.parseFloat(task.getSeller_prepay()) != 0) {
			et_transfer_seller_prepay.setText(String.valueOf(task.getSeller_prepay()));
		}
		if (!TextUtils.isEmpty(task.getPrepay()) && Float.parseFloat(task.getPrepay()) != 0) {
			et_transfer_buyer_prepay.setText(String.valueOf(task.getPrepay()));
		}
		if (!TextUtils.isEmpty(task.getSeller_company_prepay()) && Float.parseFloat(task.getSeller_company_prepay()) != 0) {
			et_transfer_seller_company_prepay.setText(String.valueOf(task.getSeller_company_prepay()));
		}
		if (!TextUtils.isEmpty(task.getTransfer_place())) {
			et_transfer_address.setText(task.getTransfer_place());
		}

		//经理减免
		if (!TextUtils.isEmpty(task.getManager_remission())) {
			et_transfer_cash_reduction_money.setText(String.valueOf(task.getManager_remission()));
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

		et_transfer_remark.setText(task.getPrepay_comment());

		if (task.getTransfer_time() == 0) {
			tv_transfer_time.setText("");
		} else {
			tv_transfer_time.setText(UnixTimeUtil.format(task.getTransfer_time()));
		}

		double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
				(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
		tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));

		if (mTask.getContract_pic() != null && mTask.getContract_pic().size() > 0) {
			adapterList = mTask.getContract_pic();
			originPhotoPaths = mTask.getContract_pic_origin();
			ContractPicOriginCount = mTask.getContract_pic().size();
		}
	}

	/**
	 * 选择过户方式
	 *
	 * @param adapterView
	 * @param view
	 * @param position
	 * @param l
	 */
	@OnItemSelected(R.id.spn_transfer_type)
	private void onTransferTypeCLick(AdapterView<?> adapterView, View view, int position, long l) {
		switch (position) {
			case 0://公司包过户
				companyTrans = true;
				setCompanyPrePay(companyTrans);
				buyer_pay_company_moeny.setEnabled(true);
				break;
			case 1://自行过户
				companyTrans = false;
				setCompanyPrePay(companyTrans);
				try {
					//回购/寄售,应付车款 = 成交价
					if (isBack || isConsigned) {
						buyer_pay_company_moeny.setEnabled(false);
						if (TextUtils.isEmpty(et_transfer_final_price.getText().toString())) {
							buyer_pay_company_moeny.setText("");
						} else {
							double mul = HCArithUtil.mul(Double.valueOf(et_transfer_final_price.getText().toString()), 10000);
							buyer_pay_company_moeny.setText(String.valueOf((int) mul));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	/**
	 * 点击过户时间
	 *
	 * @param view
	 */
	@OnClick(R.id.tv_transfer_time)
	private void onTransferTimeClick(View view) {
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
	 * 设置是否是公司过户
	 */
	private void setCompanyPrePay(boolean enabled) {
		et_transfer_seller_prepay.setEnabled(enabled);
		et_transfer_buyer_prepay.setEnabled(enabled);
		et_transfer_seller_company_prepay.setEnabled(enabled);
		et_transfer_address.setEnabled(enabled);
		tv_transfer_time.setEnabled(enabled);
//		et_transfer_info_content.setEnabled(enabled);

		setRadioButtonEnabled(rg_transfer_buyer_emigrate, enabled);
		setRadioButtonEnabled(rg_transfer_buyer_resident, enabled);
		setRadioButtonEnabled(rg_transfer_buyer_temp_resident, enabled);
		setRadioButtonEnabled(rg_transfer_company_seller, enabled);
		setRadioButtonEnabled(rg_transfer_company_buyer, enabled);
		setRadioButtonEnabled(rg_transfer_seller_present, enabled);
		setRadioButtonEnabled(rg_transfer_buyer_present, enabled);
		setRadioButtonEnabled(rg_transfer_loan, enabled);
		//包过户是否可以点击
		setRadioButtonEnabled(rg_transfer_company_free, enabled);

		if (enabled) {//公司过户
			ll_transfer_time.setVisibility(View.VISIBLE);
			ll_transfer_address.setVisibility(View.VISIBLE);
			ll_transfer_money.setVisibility(View.VISIBLE);
			ll_transfer_ext_info.setVisibility(View.VISIBLE);
			ll_transfer_company_free.setVisibility(View.VISIBLE);
		} else {
			ll_transfer_time.setVisibility(View.GONE);
			ll_transfer_address.setVisibility(View.GONE);
			ll_transfer_money.setVisibility(View.GONE);
			ll_transfer_ext_info.setVisibility(View.GONE);
			ll_transfer_company_free.setVisibility(View.GONE);
		}

	}

	private void setRadioButtonEnabled(RadioGroup radioGroup, boolean enabled) {
		if (radioGroup != null)
			for (int i = 0; i < radioGroup.getChildCount(); i++) {
				RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
				radioButton.setEnabled(enabled);
			}
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		ProgressDialogUtil.closeProgressDialog();
		if (action.equals(HttpConstants.ACTION_GET_PREPAY_COUPON)) {//获取礼品
			responseGetCoupon(response);
		} else if (action.equals(HttpConstants.ACTION_GET_USER_COUPONS)) {//获取优惠券
			responseGetUserCoupons(response);
		} else if (action.equals(HttpConstants.ACTION_EXCHANGE_COUPONS)) {//兑换优惠券
			HCDialog.dismissProgressDialog();
			HCDialog.dissmissCouponExchangeDialog();
			responseExchangeCoupons(response);
		} else if (action.equals(HttpConstants.ACTION_TRANSACTION_GETVEHICLEINFOBYID)) {//获取车源信息
			responseVehicleInfo(response);
		}
	}

	/**
	 * 获取车源信息返回
	 *
	 * @param response
	 */
	private void responseVehicleInfo(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				TransactionTaskEntity temp = mTask;
				mTask = HCJsonParse.parseTransTaskEntity(response.getData());
				if (mTask == null) {
					ToastUtil.showInfo("解析出错");
					finish();
				} else {
					mTask.setId(temp.getId());
					mTask.setBuyer_name(temp.getBuyer_name());
					mTask.setBuyer_phone(temp.getBuyer_phone());
					mTask.setWarranty_type(temp.getWarranty_type());
					mTask.setType(temp.getType());
					setData(mTask);
				}
				break;
			default:
				if (!TextUtils.isEmpty(response.getErrmsg())) {
					ToastUtil.showInfo(response.getErrmsg());
				}
				break;
		}
	}

	/**
	 * 兑换优惠券接口返回
	 *
	 * @param response
	 */
	private void responseExchangeCoupons(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//解析优惠券//使用不使用//对话框
				CouponEntity coupon = HCJsonParse.parseUserCoupon(response.getData());
				if (coupon == null) {
					ToastUtil.showText("优惠券获取失败");
					return;
				}
				exchangeCouponSuccessDialog(coupon);
				break;
			case 1821://兑换码无效
			case 1823://兑换码过期
				exchangeCouponFailureDialog();
				break;
			default://失败
				if (!TextUtils.isEmpty(response.getErrmsg())) {
					ToastUtil.showInfo(response.getErrmsg());
				}
				break;
		}
	}

	/**
	 * 获取用户优惠券接口返回
	 *
	 * @param response
	 */
	private void responseGetUserCoupons(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				listuserCoupons = HCJsonParse.parseUserCoupons(response.getData());
				break;
			default:
				if (!TextUtils.isEmpty(response.getErrmsg())) {
					ToastUtil.showInfo(response.getErrmsg());
				}
				break;
		}
	}

	/**
	 * 处理获取礼品
	 */
	private void responseGetCoupon(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//解析礼品
				List<Coupon> list = HCJsonParse.parseCouponResult(response.getData());
				mCouponList.clear();
				if (list != null) {
					mCouponList.addAll(list);
				}
				adapter.notifyDataSetChanged();
				spn_transfer_gift_type.setSelection(mCouponList.size() - 1);
				break;
			default:
				if (!TextUtils.isEmpty(response.getErrmsg())) {
					ToastUtil.showInfo(response.getErrmsg());
				}
				break;
		}
	}

	/**
	 * 点击打开选择优惠券对话框
	 *
	 * @param view view
	 */
	@OnClick(R.id.tv_transfer_coupons_type)
	private void onCouponClick(View view) {
		if (listuserCoupons == null) {
			return;
		}
		HCDialog.UserCunponsDialog(TransactionPrepayActivity.this, listuserCoupons, new OnClickListener() {
			@Override
			public void onClick(View v) {
				HCDialog.dissmissCouponListDialog();
				exchangeCouponDialog();
			}
		}, new HCDialog.OnCouponClickListener() {
			@Override
			public void couponClick(int position) {
				if (position == 0) {
					selectCoupon(null);
				} else {
					selectCoupon(listuserCoupons.get(position - 1));
				}
				//计算服务费
				double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
						(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
				tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));

			}
		});
	}

	/**
	 * 选择优惠券
	 *
	 * @param coupon
	 */
	private void selectCoupon(CouponEntity coupon) {
		if (coupon == null) {
			tv_transfer_coupons_type.setText("不使用优惠券");
			tv_transfer_coupons_type.setTag(null);
		} else {
			tv_transfer_coupons_type.setText(coupon.getTitle() + coupon.getAmount() + "元");
			tv_transfer_coupons_type.setTag(coupon);
		}
	}

	/**
	 * 打开兑换优惠券对话框
	 */
	private void exchangeCouponDialog() {
		HCDialog.UserCunponExchangeDialog(TransactionPrepayActivity.this, new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCouponClick(v);
			}
		}, new HCDialog.OnInputDialogClickListener() {
			@Override
			public void onClick(boolean isOK, String code) {
				exchangeCoupon(code);
			}
		});
	}

	/**
	 * 兑换成功对话框
	 *
	 * @param coupon
	 */
	private void exchangeCouponSuccessDialog(final CouponEntity coupon) {
		HCDialog.UserCunponExchangeSucsessDialog(TransactionPrepayActivity.this, coupon, new HCDialog.OnCouponExchangeSucsessClickListener() {
			@Override
			public void isUseClick(boolean isUser) {
				if (isUser) {
					selectCoupon(coupon);
					//计算服务费
					double service = CalculationUtil.transactionPrepayCalculationServicePrice(et_transfer_service_price.getText().toString().trim(),
							(CouponEntity) tv_transfer_coupons_type.getTag(), et_transfer_cash_reduction_money.getText().toString());
					tv_layout_transfer_final_service_pay.setText(DisplayUtils.parseMoney("###", Math.ceil(service)));
				} else {
					HCDialog.dissmissCouponExchangeDialog();
					HCDialog.dissmissCouponListDialog();
				}
			}
		});
	}

	/**
	 * 兑换失败对话框
	 */
	private void exchangeCouponFailureDialog() {
		HCDialog.UserCunponExchangeFailureDialog(TransactionPrepayActivity.this, new OnClickListener() {
			@Override
			public void onClick(View v) {
				exchangeCouponDialog();
			}
		});
	}

	/**
	 * 兑换优惠券
	 *
	 * @param code
	 */
	private void exchangeCoupon(String code) {
		HCDialog.showProgressDialog(TransactionPrepayActivity.this);
		if (mTask == null || mTask.getBuyer_phone() == null || mTask.getVehicle_source_id() == null) {
			HCDialog.dismissProgressDialog();
			ToastUtil.showText("参数错误，请重新进入该页面");
			return;
		}
		Call post = AppHttpServer.getInstance().post(HCHttpRequestParam.convertCoupon(mTask.getBuyer_phone(), code
				, mTask.getVehicle_source_id()), TransactionPrepayActivity.this, 0);
		netList.add(post);
	}

	@Override
	public void onSelectPhoto(View view) {
		hideSoftInput();
		PopupWindowUtil.UploadPicturePopup(TransactionPrepayActivity.this, view);
	}

	@Override
	public void onViewPhoto(int position) {

	}

	@Override
	public void onDeletePhoto(int position, String tag) {

		if (position < ContractPicOriginCount) {
			originPhotoPaths.remove(position);
			adapterList.remove(position);
			ContractPicOriginCount--;
		} else {
			adapterList.remove(position);
			photoPaths.remove(position - ContractPicOriginCount);
		}
		transferPhotoViewAdapter.refreshData(adapterList);
		DisplayUtils.reSizeRecyclerView(this, recycler_contract_photo, adapterList, false);
	}


	/**
	 * 输入验证
	 *
	 * @return
	 */
	private boolean valationCheck() {

		//车源编号
		if (TextUtils.isEmpty(mTask.getVehicle_source_id())) {
			showErrorMsg(et_vehicle_source_id, "请输入车源编号");
			return false;
		}

		//成交价
		if (TextUtils.isEmpty(et_transfer_final_price.getText().toString().trim())) {
			showErrorMsg(et_transfer_final_price, "成交价不能为空");
			return false;
		}
		if (et_transfer_final_price.getText().toString().startsWith(".")) {
			showErrorMsg(et_transfer_final_price, getString(R.string.price_format_error));
			return false;
		}

		//是寄售车成本价
		if (isConsigned) {
			if (TextUtils.isEmpty(et_transfer_deal_price.getText().toString())) {
				showErrorMsg(et_transfer_deal_price, "成本价不能为空");
				return false;
			}
		}

		//理论服务费
		if (TextUtils.isEmpty(et_transfer_service_price.getText().toString().trim())) {
			showErrorMsg(et_transfer_service_price, "理论服务费不能为空");
			return false;
		}
		if (et_transfer_service_price.getText().toString().startsWith(".")) {
			showErrorMsg(et_transfer_service_price, "理论服务费格式错误");
			return false;
		}
		if (Double.valueOf(et_transfer_service_price.getText().toString())
				< CalculationUtil.transactionPrepayCalculationTheServicePrice(et_transfer_final_price.getText().toString())) {
			showErrorMsg(et_transfer_service_price, "理论服务费填写错误");
			return false;
		}

		//双重校验
		if (spn_warranty_type.getSelectedItemPosition() == 0 || mTask.getWarranty_type() == 0) {
			showErrorMsg(spn_warranty_type, "请选择一种质保方式");
			return false;
		}

		if (spn_warranty_type.getSelectedItemPosition() != 1 && TextUtils.isEmpty(tv_warranty_time.getText().toString())) {
			showErrorMsg(tv_warranty_time, "请选择质保到期时间");
			return false;
		}

		if (TextUtils.isEmpty(et_buyer_name.getText().toString())) {
			showErrorMsg(et_buyer_name, "请填写买家姓名");
			return false;
		}

		if (TextUtils.isEmpty(et_buyer_id.getText().toString())) {
			showErrorMsg(et_buyer_id, "请填写买家身份证号码");
			return false;
		}

		if (TextUtils.isEmpty(et_vehicle_vin.getText().toString())) {
			showErrorMsg(et_vehicle_vin, "请填写车辆VIN号码");
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

		if (TextUtils.isEmpty(et_buyer_name.getText().toString())) {
			showErrorMsg(et_buyer_name, "请填写买家姓名");
			return false;
		}

		if (TextUtils.isEmpty(et_buyer_id.getText().toString())) {
			showErrorMsg(et_buyer_id, "请填写买家身份证号码");
			return false;
		}

		if (TextUtils.isEmpty(et_vehicle_vin.getText().toString())) {
			showErrorMsg(et_vehicle_vin, "请填写车辆VIN号码");
			return false;
		}

		String buyerPayCompanyMoeny = buyer_pay_company_moeny.getText().toString();
		if (TextUtils.isEmpty(buyerPayCompanyMoeny)) {
			if (isBack || isConsigned) {
				ToastUtil.showText("请填写应付车款");
			} else {
				ToastUtil.showText("请填写担保车款");
			}
			return false;
		}

		if (companyTrans) {//公司过户
			//过户时间
			if (TextUtils.isEmpty(tv_transfer_time.getText().toString())) {
				showErrorMsg(tv_transfer_time, getString(R.string.transfer_time_empty));
				return false;
			}
			//过户地点
			if (TextUtils.isEmpty(et_transfer_address.getText().toString())) {
				showErrorMsg(et_transfer_address, getString(R.string.transfer_place_empty));
				return false;
			}
			//是否包过户
			if (rg_transfer_company_free.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_company_free, getString(R.string.hc_company_transfer_free));
				return false;
			}

			//定金相关
			if (TextUtils.isEmpty(et_transfer_seller_prepay.getText().toString())) {
				showErrorMsg(et_transfer_seller_prepay, getString(R.string.buyer_give_seller_money_empty));
				return false;
			}
			if (TextUtils.isEmpty(et_transfer_buyer_prepay.getText().toString())) {
				showErrorMsg(et_transfer_buyer_prepay, getString(R.string.buyer_give_company_money_empty));
				return false;
			}
			//定金不能超过服务费（张艳云于2016-05-16提出）
			if (StringUtil.parseDouble(et_transfer_buyer_prepay.getText().toString(), 0)
					> StringUtil.parseDouble(tv_layout_transfer_final_service_pay.getText().toString(), 0)) {
				showErrorMsg(et_transfer_buyer_prepay, "定金金额不能大于服务费金额");
				return false;
			}

			if (TextUtils.isEmpty(et_transfer_seller_company_prepay.getText().toString())) {
				showErrorMsg(et_transfer_seller_company_prepay, getString(R.string.seller_give_company_money_empty));
				return false;
			}
			//其他信息
			if (rg_transfer_buyer_resident.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_buyer_resident, getString(R.string.resident));
				return false;
			}
			if (rg_transfer_buyer_temp_resident.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_buyer_temp_resident, getString(R.string.temp_resident));
				return false;
			}
			if (rg_transfer_seller_present.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_seller_present, getString(R.string.seller_present));
				return false;
			}
			if (rg_transfer_buyer_present.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_buyer_present, getString(R.string.buyer_present));
				return false;
			}
			if (rg_transfer_buyer_emigrate.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_buyer_emigrate, getString(R.string.emigrate));
				return false;
			}
			if (rg_transfer_company_seller.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_company_seller, getString(R.string.company_seller));
				return false;
			}
			if (rg_transfer_company_buyer.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_company_buyer, getString(R.string.company_buyer));
				return false;
			}
			if (rg_transfer_loan.getCheckedRadioButtonId() == -1) {
				showErrorMsg(rg_transfer_loan, getString(R.string.hc_transfer_ext_loan));
				return false;
			}
		}
		//服务费校验
		if (Double.valueOf(tv_layout_transfer_final_service_pay.getText().toString()) < 0) {
			ToastUtil.showText("服务费金额不能小于0,请修改优惠券或者减免金额");
			return false;
		}
		//合同和照片
		if (adapterList.isEmpty()) {
			ToastUtil.showText("请选择合同和照片");
			return false;
		}
		return true;
	}


	/**
	 * 点击支付定金
	 */
	@OnClick(R.id.btn_transaction_prepay_pay)
	private void onPrePayClick(View view) {

		//检查车源id是否被修改
		if (getIntent().getBooleanExtra("isStore", false)) {
			int vehicleSourceId = StringUtil.parseInt(et_vehicle_source_id.getText().toString(), 0);
			if (!mTask.getVehicle_source_id().equals(String.valueOf(vehicleSourceId))) {
				/*查询详情*/
				ProgressDialogUtil.showProgressDialog(this);
				Call post = AppHttpServer.getInstance().post(HCHttpRequestParam.getVehicleInfoById(vehicleSourceId), this, 0);
				netList.add(post);
				return;
			}
		}

		if (!valationCheck()) {
			return;
		}
		if (mTask == null) {
			return;
		}

		// 成交价
		double bigDecimalPrice = HCArithUtil.mul(Double.valueOf(et_transfer_final_price.getText().toString()), 10000);
		// 担保车款/应付车款
		int buyerPayCompanyMoney = StringUtil.parseInt(buyer_pay_company_moeny.getText().toString(), 0);
		// 买家刷卡手续费
		int buyerPayCompnayFee = StringUtil.parseInt(buyer_pay_company_fee.getText().toString(), 0);
		if (buyerPayCompanyMoney > bigDecimalPrice) {
			if (isBack || isConsigned) {
				ToastUtil.showText("应付车款必须小于等于成交价");
			} else {
				ToastUtil.showText("担保车款必须小于等于成交价");
			}
			return;
		}

		saveData();

		Intent intent = new Intent(this, PayDepositActivity.class);
		intent.putExtra(PayDepositActivity.EXTRA_TRANS_TASK_ENTITY, mTask);
		intent.putExtra(PayDepositActivity.EXTRA_IS_COMAPNY_TRANS, companyTrans);
		intent.putExtra(PayDepositActivity.EXTRA_GUARANTEE_MONEY, StringUtil.parseInt(buyer_pay_company_moeny.getText().toString(), 0));

		// 买家需支付金额
		int commission = StringUtil.parseInt(mTask.getCommission(), 0);
		int prepay = StringUtil.parseInt(mTask.getPrepay(), 0);
		int buyerPayMoney;
		if (companyTrans) {
			buyerPayMoney = prepay + buyerPayCompanyMoney + buyerPayCompnayFee;
		} else {
			buyerPayMoney = commission + buyerPayCompanyMoney + buyerPayCompnayFee;
		}
		intent.putExtra(PayDepositActivity.EXTRA_BUYER_PAY_MONEY, buyerPayMoney);

		if (photoPaths != null && photoPaths.size() > 0) {
			intent.putExtra(PayDepositActivity.EXTRA_PHOTO_PATH, photoPaths);
		}
		if (originPhotoPaths != null && originPhotoPaths.size() > 0) {
			intent.putExtra(PayDepositActivity.EXTRA_ORIGIN_PHOTO_PATH, originPhotoPaths);
		}
		startActivityForResult(intent, KEY_REQUEST_POSPAY);

	}

	private void saveData() {
		//成交价
		double bigDecimalPrice = HCArithUtil.mul(Double.valueOf(et_transfer_final_price.getText().toString()), 10000);
		// 担保车款/应付车款
		int buyerPayCompanyMoney = StringUtil.parseInt(buyer_pay_company_moeny.getText().toString(), 0);
		// 买家刷卡手续费
		int buyerPayCompnayFee = StringUtil.parseInt(buyer_pay_company_fee.getText().toString(), 0);
		//成交价
		mTask.setPrice(bigDecimalPrice + "");
		//是寄售车 成本价
		if (isConsigned) {
			int deal_price = (int) HCArithUtil.mul(Double.valueOf(et_transfer_deal_price.getText().toString()), 10000);
			mTask.setDeal_price(deal_price);
		}
		//理论服务费
		mTask.setTheory_commission(et_transfer_service_price.getText().toString());
		//最终服务费
		mTask.setCommission(tv_layout_transfer_final_service_pay.getText().toString());
		//减免金额
		if (!TextUtils.isEmpty(et_transfer_cash_reduction_money.getText().toString())) {
			mTask.setManager_remission(et_transfer_cash_reduction_money.getText().toString());
		}
		/*礼品*/
		if (mCouponList.size() > 0) {
			//设置礼品
			mTask.setAward_info(mCouponList.get(spn_transfer_gift_type.getSelectedItemPosition()));
		}
		/*优惠券*/
		if (listuserCoupons.size() > 0) {
			//优惠券
			CouponEntity couponEntity = (CouponEntity) tv_transfer_coupons_type.getTag();
			mTask.setCoupon_info(couponEntity);
		}
		/*质保到期时间*/
		mTask.setWarranty_time(UnixTimeUtil.getUnixTime(tv_warranty_time.getText().toString(), UnixTimeUtil.YEAR_MONTH_DAY_PATTERN));
		/*减免金额*/
		if (!TextUtils.isEmpty(et_transfer_cash_reduction_money.getText().toString())) {
			mTask.setManager_remission(et_transfer_cash_reduction_money.getText().toString());
		}
		/*备注*/
		mTask.setTransfer_comment(et_transfer_remark.getText().toString());

		if (companyTrans) {
		    /*过户方式*/
			mTask.setTransfer_mode(0);
			/*买家给车主定金*/
			mTask.setSeller_prepay(et_transfer_seller_prepay.getText().toString());
			/*买家给公司定金*/
			mTask.setPrepay(String.valueOf(DisplayUtils.parseMoney("####", Double.valueOf(et_transfer_buyer_prepay.getText().toString()))));
			/*车主给公司定金*/
			mTask.setSeller_company_prepay(et_transfer_seller_company_prepay.getText().toString());
			/*过户时间*/
			mTask.setTransfer_time(UnixTimeUtil.getUnixTime(tv_transfer_time.getText().toString()));
			/*过户地点*/
			mTask.setTransfer_place(et_transfer_address.getText().toString());
			/*是否包过户*/
			mTask.setTransfer_free(rg_transfer_company_free.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);//是否公司包过户 包过户1 不包过户0
			/*其他信息*/
			ExtInfo extInfo = new ExtInfo();
			extInfo.setEmigrate(rg_transfer_buyer_emigrate.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setResident(rg_transfer_buyer_resident.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setTemp_resident(rg_transfer_buyer_temp_resident.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setCompany_seller(rg_transfer_company_seller.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setCompany_buyer(rg_transfer_company_buyer.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setSeller_present(rg_transfer_seller_present.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setBuyer_present(rg_transfer_buyer_present.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			extInfo.setLoan(rg_transfer_loan.getCheckedRadioButtonId() % 2 == 0 ? 0 : 1);
			mTask.setExt_info(gson.toJson(extInfo));
		} else {
			mTask.setTransfer_mode(1);
			// 服务费
			mTask.setCommission(tv_layout_transfer_final_service_pay.getText().toString());
			mTask.setTransfer_free(0);//是否公司包过户 包过户1 不包过户0
			mTask.setSeller_prepay("0");
			mTask.setSeller_company_prepay("0");
			mTask.setPrepay("0");
			mTask.setTransfer_time(0);
			mTask.setTransfer_place("");
			mTask.setExt_info("");
		}

		// 买家需支付金额
		int commission = StringUtil.parseInt(mTask.getCommission(), 0);
		int prepay = StringUtil.parseInt(mTask.getPrepay(), 0);
		int buyerPayMoney;
		if (companyTrans) {
			buyerPayMoney = prepay + buyerPayCompanyMoney + buyerPayCompnayFee;
		} else {
			buyerPayMoney = commission + buyerPayCompanyMoney + buyerPayCompnayFee;
		}

		mTask.setPrepay_payment(buyerPayMoney + "");
		//担保车款/应付车款
		mTask.setPrepay_vehicle_price(buyer_pay_company_moeny.getText().toString());
		mTask.setPrepay_fee(buyerPayCompnayFee);//买家刷卡手续费

		mTask.setBuyer_name(et_buyer_name.getText().toString());
		mTask.setBuyer_identify(et_buyer_id.getText().toString());
		mTask.setVin_code(et_vehicle_vin.getText().toString());
		//其他支出
		mTask.setOther_cost(StringUtil.parseInt(et_other_cost.getText().toString(), 0));
	}

	/**
	 * 隐藏输入法
	 */
	private void hideSoftInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(btn_transaction_prepay_pay.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 当车源编号失去焦点时查询车源信息
	 */
	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (!hasFocus) {
			int vehicleSourceId = StringUtil.parseInt(et_vehicle_source_id.getText().toString(), 0);
			if (vehicleSourceId == 0) {
				ToastUtil.showInfo("请输入车源编号");
			} else {
				/*查询详情*/
				ProgressDialogUtil.showProgressDialog(this);
				Call post = AppHttpServer.getInstance().post(HCHttpRequestParam.getVehicleInfoById(vehicleSourceId), this, 0);
				netList.add(post);
			}
		}
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
					adapterList.add(photo);
					photoPaths.add(photo);
				}
			}
			transferPhotoViewAdapter.refreshData(adapterList);
			DisplayUtils.reSizeRecyclerView(this, recycler_contract_photo, adapterList, false);
		}

		if (requestCode == KEY_REQUEST_POSPAY) {
			setResult(RESULT_OK);
			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*清除网络请求*/
		if (!netList.isEmpty()) {
			for (Call post : netList) {
				AppHttpServer.getInstance().cancelRequest(post);
			}
		}
	}
}
