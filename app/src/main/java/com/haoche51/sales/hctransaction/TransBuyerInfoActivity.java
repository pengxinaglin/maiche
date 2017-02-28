package com.haoche51.sales.hctransaction;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.activity.HCWebViewActivity;
import com.haoche51.sales.custom.TagsLayout;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hctransfer.RemittanceCommitActivity;
import com.haoche51.sales.hctransfer.TransferPhotoViewAdapter;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hctransfer.TransferPosPayNewActivity;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ControlDisplayUtil;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCArithUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户看车成功--》任务详情页
 * Created by mac on 15/12/4.
 */
public class TransBuyerInfoActivity extends CommonStateActivity {

	@ViewInject(R.id.recyclerview_contract)
	public RecyclerView recyclerview_contract;//合同照片的展示
	@ViewInject(R.id.recycler_transfer_photo)
	public RecyclerView recycler_transfer_photo;//转账照片的展示
	@ViewInject(R.id.tv_transaction_suc_buyer_name)
	private TextView tv_transaction_suc_buyer_name;//客户姓名
	@ViewInject(R.id.tv_item_today_task)
	private TextView tv_transaction_car_dealer;
	@ViewInject(R.id.tv_transaction_suc_buyer_phone)
	private TextView tv_transaction_suc_buyer_phone;//客户电话
	@ViewInject(R.id.tv_transfer_type)
	private TextView tv_transfer_type;//过户方式
	@ViewInject(R.id.rl_transfer_time)
	private RelativeLayout rl_transfer_time;
	@ViewInject(R.id.tv_transfer_time)
	private TextView tv_transfer_time;//过户时间
	@ViewInject(R.id.rl_transfer_address)
	private RelativeLayout rl_transfer_address;
	@ViewInject(R.id.tv_transfer_add)
	private TextView tv_transfer_add;//过户地点
	@ViewInject(R.id.tv_transfer_service_pay)
	private TextView tv_transfer_service_pay;//服务费
	@ViewInject(R.id.rl_owner_commission)
	private RelativeLayout rl_owner_commission;//车商服务费
	@ViewInject(R.id.tv_owner_commission)
	private TextView tv_owner_commission;//车商服务费
	@ViewInject(R.id.tv_transfer_status)
	private TextView tv_transfer_status;//支付状态
	@ViewInject(R.id.tv_audit_status)
	private TextView tv_audit_contract;//支付成功状态
	@ViewInject(R.id.tv_transaction_vehicle)
	private TextView tv_transaction_vehicle;//客户所看车辆
	@ViewInject(R.id.tv_transfer_vc_id)
	private TextView tv_transfer_vc_id;//所看车辆id
	@ViewInject(R.id.tv_vehicle_plate_number)
	private TextView tv_vehicle_plate_number;//车牌号
	@ViewInject(R.id.tv_vehicle_type_text)
	private TextView tv_vehicle_type_text;//车牌号
	@ViewInject(R.id.tv_final_price)
	private TextView tv_final_price;//成交价
	@ViewInject(R.id.tv_seller_name)
	private TextView tv_seller_name;//车主姓名
	@ViewInject(R.id.tv_seller_phone)
	private TextView tv_seller_phone;//车主电话
	@ViewInject(R.id.tv_transaction_buyer_company)
	private TextView tv_transaction_buyer_company;//成交价
	@ViewInject(R.id.tv_transaction_buyer_owner)
	private TextView tv_transaction_buyer_owner;//成交价
	@ViewInject(R.id.tv_transaction_owner_company)
	private TextView tv_transaction_owner_company;//成交价
	@ViewInject(R.id.btn_transaction_dropbuy)
	private TextView btn_transaction_dropbuy;//毁约
	@ViewInject(R.id.btn_transaction_fail)
	private LinearLayout btn_transaction_fail;//转失败
	@ViewInject(R.id.btn_transaction_prepay_commit)
	private LinearLayout btn_transaction_prepay;//修改过户信息
	@ViewInject(R.id.ll_upload_transfer_pic)
	private LinearLayout ll_upload_transfer_pic;//上传转账截图
	@ViewInject(R.id.btn_transfer_seller)
	private LinearLayout btn_transfer_seller;//自行过户交易完成转车主
	@ViewInject(R.id.btn_transfer_other_cost)
	private LinearLayout btn_transfer_other_cost;//交易完成其他支出
	@ViewInject(R.id.tv_audit_des)
	private TextView tv_audit_des;

	@ViewInject(R.id.ll_vehicle_tag)
	private TagsLayout ll_vehicle_tag;//车源标签

	@ViewInject(R.id.tv_transfer_order_number)
	private TextView tv_transfer_order_number;

	@ViewInject(R.id.tv_transaction_buyer_info_comment)
	private TextView tv_transaction_buyer_info_comment;

	@ViewInject(R.id.tv_transfer_commit)
	private TextView tv_transfer_commit;

	/**
	 * 成本价
	 */
	@ViewInject(R.id.rl_transfer_detail_deal_price)
	private RelativeLayout rl_transfer_detail_deal_price;
	@ViewInject(R.id.tv_transfer_detail_deal_price)
	private TextView tv_transfer_detail_deal_price;
	@ViewInject(R.id.tv_transfer_detail_deal_price_change_lable)
	private TextView tv_transfer_detail_deal_price_change_lable;

	private TransactionTaskEntity mTaskEntity;
	private int trans_id;
	private TransferPhotoViewAdapter mAdapter;//合同照片的adapter
	private TransferPhotoViewAdapter transferPhotoViewAdapter;//过户照片的addapter


	@Override
	protected int getContentView() {
		return R.layout.activity_transaction_buyer_info;
	}

	@Override
	protected void initView() {
		super.initView();
		setScreenTitle(R.string.hc_trans_task_activity_title);
	}

	@Override
	protected void initData() {
		this.trans_id = getIntent().getIntExtra("trans_id", 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		ProgressDialogUtil.showProgressDialog(this, getString(R.string.later));
		AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id), this, 0);
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}
		if (action.equals(HttpConstants.ACTION_MODIFY_TRANSFER_MESSAGE)) {
			responseModifyTransferInfo(requestId, response, error);
		} else if (action.equals(HttpConstants.ACTION_GET_TRANSACTION_BY_ID)) {
			responseDetail(requestId, response, error);
		}
	}


	/**
	 * 处理请求修改过户信息
	 */
	private void responseModifyTransferInfo(int requestId, HCHttpResponse response, Throwable error) {
		dismissLoadingView();
		ProgressDialogUtil.closeProgressDialog();
		switch (response.getErrno()) {
			case 0:
				ToastUtil.showInfo(getString(R.string.hc_modify_succ));
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 请求详情信息
	 */
	private void responseDetail(int requestId, HCHttpResponse response, Throwable error) {
		switch (response.getErrno()) {
			case 0:
				if (response.getData() != null) {
					mTaskEntity = HCJsonParse.parseTransTaskEntity(response.getData());
					if (mTaskEntity != null) {
						dismissLoadingView();
						if (!isFinishing())
							ProgressDialogUtil.closeProgressDialog();
						HCDialog.dismissProgressDialog();
						updateUI();

					} else {
						setErrorView();
					}
				}
				break;
			default:
				setErrorView();
				break;
		}
	}

	private void setErrorView() {
		dismissLoadingView();
		if (!isFinishing())
			ProgressDialogUtil.closeProgressDialog();
		showErrorView(false, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissResultView(true);
				showLoadingView(false);
				AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id),
						TransBuyerInfoActivity.this, 0);
			}
		});
	}


	/**
	 * 更新界面
	 */
	private void updateUI() {
		if (mTaskEntity == null)
			return;

		if (mTaskEntity.getCar_dealer() == 1) {
			tv_transaction_car_dealer.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(mTaskEntity.getOrder_number())) {
			tv_transfer_order_number.setText(mTaskEntity.getOrder_number());
		}

		//实体寄售车显示成本价
		if (mTaskEntity.isConsignment()) {
			rl_transfer_detail_deal_price.setVisibility(View.VISIBLE);
			tv_transfer_detail_deal_price_change_lable.setVisibility(View.VISIBLE);
			tv_transfer_detail_deal_price.setText(String.valueOf(mTaskEntity.getDeal_price()));
			rl_owner_commission.setVerticalGravity(View.VISIBLE);
			tv_owner_commission.setText(mTaskEntity.getOwner_commission());
		}

		switch (mTaskEntity.getTransfer_mode()) {
			case 0://公司过户

				if (mTaskEntity.getTransfer_free() == 0) {
					tv_transfer_type.setText(getString(R.string.transfer_company) + "[不包过户]");
				} else if (mTaskEntity.getTransfer_free() == 1) {
					tv_transfer_type.setText(getString(R.string.transfer_company) + "[包过户]");
				} else {
					tv_transfer_type.setText(getString(R.string.transfer_company));
				}

				rl_transfer_time.setVisibility(View.VISIBLE);
				rl_transfer_address.setVisibility(View.VISIBLE);
				tv_transfer_time.setText(UnixTimeUtil.format(mTaskEntity.getTransfer_time()));//过户时间
				tv_transfer_add.setText(mTaskEntity.getTransfer_place());//过户地点
				break;
			case 1://自行过户
				tv_transfer_type.setText(getString(R.string.transfer_own));
				rl_transfer_time.setVisibility(View.GONE);
				rl_transfer_address.setVisibility(View.GONE);
				break;
		}

		tv_transfer_service_pay.setText(mTaskEntity.getCommission());

		tv_transaction_suc_buyer_name.setText(mTaskEntity.getBuyer_name());//客户姓名
		DisplayUtils.getNoUnderlineSpan(tv_transaction_suc_buyer_phone, mTaskEntity.getBuyer_phone());//客户电话

		//根据毁约状态和任务状态设置详情状态文本及颜色和底部按钮
		setStatusAndBottomButton();

		tv_transaction_vehicle.setText(mTaskEntity.getVehicle_name());//客户所看车辆
		tv_transfer_vc_id.setText(mTaskEntity.getVehicle_source_id());//所看车辆id
		tv_vehicle_plate_number.setText(mTaskEntity.getPlate_number());//车牌号
		if (!TextUtils.isEmpty(mTaskEntity.getVehicle_type_text())) {
			tv_vehicle_type_text.setText(mTaskEntity.getVehicle_type_text());//车源类型
		}
		//车源更多标签
		if (mTaskEntity.getVehicle_tag() != null && !mTaskEntity.getVehicle_tag().isEmpty()) {
			ll_vehicle_tag.removeAllViews();
			ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.setMargins(20, 0, 0, 0);
			for (int i = 0; i < mTaskEntity.getVehicle_tag().size(); i++) {
				TextView textView = new TextView(this);
				textView.setTextAppearance(this, R.style.vehicle_lable);
				Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_vehicle_tag);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				textView.setCompoundDrawables(drawable, null, null, null);
				textView.setCompoundDrawablePadding(10);
				textView.setText(mTaskEntity.getVehicle_tag().get(i));
				ll_vehicle_tag.addView(textView, lp);
			}
		}
		String price = mTaskEntity.getPrice();
		double bigDecimal = 0;
		if (!TextUtils.isEmpty(price)) {
			bigDecimal = HCArithUtil.div(Double.valueOf(price), 10000);
		}
		String priceOfWan = DisplayUtils.parseMoney("###.###", bigDecimal);
		tv_final_price.setText(priceOfWan);//成交价( 转换成单位 万元 )
		tv_seller_name.setText(mTaskEntity.getSeller_name());//车主姓名
		DisplayUtils.getNoUnderlineSpan(tv_seller_phone, mTaskEntity.getSeller_phone());//车主电话

		initRecyclerView();

		if (mAdapter == null) {
			//合同照片展示
			mAdapter = new TransferPhotoViewAdapter(TransBuyerInfoActivity.this, mTaskEntity.getContract_pic(), null);
//      mAdapter = new TransferPhotoViewAdapter(TransBuyerInfoActivity.this, picStr, null);
			mAdapter.setView(true);
			recyclerview_contract.setAdapter(mAdapter);
		} else {
			mAdapter.setView(true);
			mAdapter.notifyDataSetChanged();
		}

		transferPhotoViewAdapter = new TransferPhotoViewAdapter(this, mTaskEntity.getPrepay_transfer_pic(), null);
//    TransferPhotoViewAdapter transferPhotoViewAdapter = new TransferPhotoViewAdapter(this, picStr, null);
		transferPhotoViewAdapter.setView(true);
		recycler_transfer_photo.setAdapter(transferPhotoViewAdapter);

		reSizeRecyclerView();

		if (!TextUtils.isEmpty(mTaskEntity.getPrepay_comment())) {
			tv_transaction_buyer_info_comment.setText(mTaskEntity.getPrepay_comment());
			tv_transaction_buyer_info_comment.setVisibility(View.VISIBLE);
		} else {
			tv_transaction_buyer_info_comment.setVisibility(View.GONE);
		}

		tv_transaction_buyer_company.setText(mTaskEntity.getPrepay());
		//预定时担保车款
		int prepay_vehicle_price = StringUtil.parseInt(mTaskEntity.getPrepay_vehicle_price(), 0);
		//过户时担保车款
		int transfer_vehicle_price = StringUtil.parseInt(mTaskEntity.getTransfer_vehicle_price(), 0);
		tv_transaction_buyer_owner.setText(String.valueOf(prepay_vehicle_price + transfer_vehicle_price));
		tv_transaction_owner_company.setText(mTaskEntity.getSeller_company_prepay());
	}

	/**
	 * 设置recyclerView的高度
	 */
	private void reSizeRecyclerView() {
		DisplayUtils.reSizeRecyclerView(this, recyclerview_contract, mTaskEntity.getContract_pic(), true);
		DisplayUtils.reSizeRecyclerView(this, recycler_transfer_photo, mTaskEntity.getPrepay_transfer_pic(), true);
	}

	private void setStatusAndBottomButton() {
		//根据毁约状态和任务状态设置详情状态文本及颜色
		if (mTaskEntity != null) {
			ControlDisplayUtil.getInstance().setTransSuccessItemStatus(this.tv_audit_contract, mTaskEntity.getAudit_status());  // 顶部支付状态
			this.tv_audit_contract.setText(mTaskEntity.getAudit_text());
			this.tv_audit_contract.setVisibility(View.VISIBLE);
		}
		if (mTaskEntity != null && !TextUtils.isEmpty(mTaskEntity.getAudit_desc())) {
			tv_audit_des.setText(mTaskEntity.getAudit_desc());
			tv_audit_des.setVisibility(View.VISIBLE);
		} else {
			tv_audit_des.setVisibility(View.GONE);
		}

		if (mTaskEntity != null && mTaskEntity.getAudit_status() == TaskConstants.STATUS_WAIT_TRANSFER) {//待过户
			ll_upload_transfer_pic.setVisibility(View.GONE);
			btn_transaction_dropbuy.setVisibility(View.VISIBLE);
			btn_transaction_prepay.setVisibility(View.VISIBLE);
			btn_transaction_fail.setVisibility(View.GONE);
			btn_transfer_seller.setVisibility(View.GONE);
			btn_transfer_other_cost.setVisibility(View.GONE);
		} else if (mTaskEntity != null && mTaskEntity.getAudit_status() == TaskConstants.STATUS_FINISH) {//交易成功
			int owner_out_payment = getOwnerOutPayment();
			//自行过户 并且 担保车款>0 并且 为普通车、寄售车
			if (mTaskEntity.getTransfer_mode() == 1 && owner_out_payment > 0
					&& (mTaskEntity.getVehicle_type() == 0 || mTaskEntity.getVehicle_type() == 2 || mTaskEntity.isConsignment())
					&& mTaskEntity.getFin_seller_transfer() != null) {
				btn_transfer_seller.setVisibility(View.VISIBLE);
			} else {
				btn_transfer_seller.setVisibility(View.GONE);
			}
			//转其他支出
			if (mTaskEntity.getOther_cost() > 0) {
				btn_transfer_other_cost.setVisibility(View.VISIBLE);
			} else {
				btn_transfer_other_cost.setVisibility(View.GONE);
			}

			ll_upload_transfer_pic.setVisibility(View.GONE);
			btn_transaction_prepay.setVisibility(View.GONE);
			btn_transaction_dropbuy.setVisibility(View.GONE);
			btn_transaction_fail.setVisibility(View.GONE);
		} else {
			ll_upload_transfer_pic.setVisibility(View.GONE);
			btn_transaction_prepay.setVisibility(View.GONE);
			btn_transaction_dropbuy.setVisibility(View.GONE);
			btn_transaction_fail.setVisibility(View.GONE);
			btn_transfer_seller.setVisibility(View.GONE);
			btn_transfer_other_cost.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == TaskConstants.TRANSACTION_CHANGE_PROMISES) {//毁约
				finish();
			} else if (requestCode == TaskConstants.QUIT_BUY_TRANSACTION) {//看车失败
				HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
				finish();
			} else if (requestCode == TaskConstants.TRANSACTION_MODIFY_TRANSFER_CONTRACT_AUDIT_FAIL) {//修改过户--合同审核失败重新预定--状态变为合同审核中
				//TODO onStart是否可以更新界面
			} else if (requestCode == TaskConstants.TRANSACTION_MODIFY_TRANSFER) {//修改过户方式--状态变为预定审核中
				//TODO onStart是否可以更新界面
			} else if (requestCode == TaskConstants.TRANSACTION_MODIFY_TRANSFER
					|| requestCode == TaskConstants.TRANSACTION_UP_TRANSFER_PIC) {
				AppHttpServer.getInstance().post(HCHttpRequestParam.getTransactionById(trans_id), this, 0);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@OnClick(R.id.tv_seller_phone)
	private void callSeller(View view) {
		//用intent启动拨打电话
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tv_seller_phone.getText().toString()));
			startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 转失败
	 */
	@OnClick(R.id.btn_transaction_fail)
	private void fail(View v) {
		if (mTaskEntity != null) {
			Intent dropIntent = new Intent(this, TransactionTaskFailActivity.class);
			dropIntent.putExtra("taskEntity", mTaskEntity);
			startActivityForResult(dropIntent, TaskConstants.QUIT_BUY_TRANSACTION);
		}
	}


	/**
	 * 毁约
	 */
	@OnClick(R.id.btn_transaction_dropbuy)
	private void droBuy(View v) {
		if (mTaskEntity != null) {
			Intent intent = new Intent(this, ChangePromisesActivity.class);
			intent.putExtra("mTaskEntity", mTaskEntity);
			startActivityForResult(intent, TaskConstants.TRANSACTION_CHANGE_PROMISES);
		}
	}

	/**
	 * 跳转web页看车详情
	 */
	@OnClick(R.id.rb_trans_vehicle)
	private void toVehicleDetail(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("url", "http://m.haoche51.com/details/" + mTaskEntity.getVehicle_source_id() + ".html");
			HCActionUtil.launchActivity(this, HCWebViewActivity.class, map);
		}
	}

	/**
	 * 跳转买家看车记录
	 */
	@OnClick(R.id.rb_buyer_trans_history)
	private void toTransRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("buyer_phone", mTaskEntity.getBuyer_phone());
			HCActionUtil.launchActivity(this, TransactionRecordListActivity.class, map);
		}
	}

	/**
	 * 跳转到买家回访记录
	 */
	@OnClick(R.id.rb_buyer_trans_revisit)
	private void toBuyerRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", mTaskEntity.getVehicle_source_id());
			map.put("buyer_phone", mTaskEntity.getBuyer_phone());
			HCActionUtil.launchActivity(this, BuyerRevisitListActivity.class, map);
		}
	}

	/**
	 * 跳转到车主回访记录
	 */
	@OnClick(R.id.rb_trans_vehicle_revisit)
	private void toSellerRevisit(View v) {
		if (mTaskEntity != null) {
			Map<String, Object> map = new HashMap<>();
			map.put("vehicle_source_id", mTaskEntity.getVehicle_source_id());
			map.put("onlyViewId", true);
			map.put("seller_phone", mTaskEntity.getSeller_phone());
			map.put("seller_name", mTaskEntity.getSeller_name());
			HCActionUtil.launchActivity(this, VehicleRevisitListActivity.class, map);
		}
	}

	/**
	 * 修改过户信息
	 */
	@OnClick(R.id.btn_transaction_prepay_commit)
	private void toTransactionPrepay(View v) {
		if (mTaskEntity != null && (mTaskEntity.getAudit_status() == TaskConstants.STATUS_MAN_AUDIT
				|| mTaskEntity.getAudit_status() == TaskConstants.STATUS_WAIT_TRANSFER)) {//修改过户方式
			Intent intent = new Intent(this, TransactionPrepayModifyActivity.class);
			intent.putExtra("mTaskEntity", mTaskEntity);
			startActivityForResult(intent, TaskConstants.TRANSACTION_MODIFY_TRANSFER);
		}
//		else if (mTaskEntity != null && mTaskEntity.getAudit_status() == TaskConstants.VIEW_TASK_SUCCES_CONTRACT_AUDIT_FAIL) {//合同审核失败，重新预定
//			Intent intent = new Intent(this, TransactionPrepayActivity.class);
//			intent.putExtra("taskEntity", mTaskEntity);
//			startActivityForResult(intent, TaskConstants.TRANSACTION_MODIFY_TRANSFER_CONTRACT_AUDIT_FAIL);
//		}
	}

	//初始化recyclerview
	private void initRecyclerView() {

		GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		this.recyclerview_contract.setLayoutManager(layoutManager);
		this.recyclerview_contract.setHasFixedSize(true);

		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
		gridLayoutManager.setOrientation(LinearLayout.VERTICAL);
		recycler_transfer_photo.setLayoutManager(gridLayoutManager);
		this.recycler_transfer_photo.setHasFixedSize(true);

	}

	/**
	 * 点击底部上传转账截图
	 */
	@OnClick(R.id.tv_upload_transfer_picture)
	private void toUploadTransferPicture(View v) {
		Intent intent = new Intent(this, UploadPictureActivity.class);
		intent.putExtra("mTaskEntity", mTaskEntity);
		startActivityForResult(intent, TaskConstants.TRANSACTION_UP_TRANSFER_PIC);
	}

	private int getOwnerOutPayment() {
		//预定时担保车款
		int prepay_vehicle_price = StringUtil.parseInt(mTaskEntity.getPrepay_vehicle_price(), 0);
		//过户时担保车款
		int transfer_vehicle_price = StringUtil.parseInt(mTaskEntity.getTransfer_vehicle_price(), 0);
		//需转账金额
		return prepay_vehicle_price + transfer_vehicle_price;
	}

	private int getSellerCompanyPrepay() {
		//剩余服务费 = 剩余服务费 - 复检减免
		double rest_commission = StringUtil.parseDouble(mTaskEntity.getRest_commission(), 0) - StringUtil.parseDouble(mTaskEntity.getTransfer_remission(), 0);
		//使用车主定金 = 使用车主定金 ？min(剩余服务费，车主定金）: 0；
		double seller_company_prepay = mTaskEntity.getUse_owner() == 1 ? Math.min(rest_commission, StringUtil.parseDouble(mTaskEntity.getSeller_company_prepay(), 0))
				: 0;
		seller_company_prepay = StringUtil.parseDouble(mTaskEntity.getSeller_company_prepay(), 0) - seller_company_prepay;
		return (int) seller_company_prepay;
	}

	/**
	 * 转车主
	 *
	 * @param view view
	 */
	@OnClick(R.id.btn_transfer_seller)
	private void btn_transfer_seller(View view) {
		//车款
		int owner_out_payment = getOwnerOutPayment();
		//车主定金
		int seller_company_prepay = getSellerCompanyPrepay();
		//跳转到提交转账信息
		Intent intent = new Intent(this, RemittanceCommitActivity.class);
		intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, owner_out_payment);
		intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_SELLER_COMPANY_PREPAY, seller_company_prepay);
		//转账信息驳回状态
		intent.putExtra("finEntity", mTaskEntity.getFin_seller_transfer());
		intent.putExtra("transferId", mTaskEntity.getId());
		startActivity(intent);
	}

	/**
	 * 转其他支出
	 *
	 * @param view view
	 */
	@OnClick(R.id.btn_transfer_other_cost)
	private void btn_transfer_other_cost(View view) {
		//车款
		int owner_out_payment = mTaskEntity.getOther_cost();
		//跳转到提交转账信息
		Intent intent = new Intent(this, RemittanceCommitActivity.class);
		intent.putExtra(TransferPosPayNewActivity.KEY_INTENT_EXTRA_OWNER_OUT_PAYMENT, owner_out_payment);
		intent.putExtra(RemittanceCommitActivity.EXTRA_TRANSFER_OTHER, true);
		//转账信息驳回状态
		intent.putExtra("finEntity", mTaskEntity.getFin_other_info());
		intent.putExtra("transferId", mTaskEntity.getId());
		startActivity(intent);
	}

}