package com.haoche51.sales.hctransaction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.TransactionQiNiuUploadUtil;
import com.haoche51.settlement.cashiers.SettlementIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付定金
 */
public class PayDepositActivity extends CommonBaseActivity {

	public static final String EXTRA_TRANS_TASK_ENTITY = "extra_trans_task_entity";
	public static final String EXTRA_IS_COMAPNY_TRANS = "extra_company_rans";
	public static final String EXTRA_IS_PREPAY = "extra_is_prepay";
	public static final String EXTRA_BUYER_PAY_MONEY = "extra_buyer_pay_money";
	public static final String EXTRA_GUARANTEE_MONEY = "extra_guarantee_money";
	public static final String EXTRA_PHOTO_PATH = "extra_photo_path";
	public static final String EXTRA_ORIGIN_PHOTO_PATH = "extra_origin_photo_path";

	public static final int REQUEST_CODE_BUYTER_PAY = 1000;
	public static final int REQUEST_CODE_SELLER_PAY = 1001;
	public static final int REQUEST_CODE_DEAL_PRICE = 1002;

	private TransactionTaskEntity mTask;
	// 是否公司过户
	private boolean mIsCompanyTrans;
	// 是否预定 true 预定；false 修改
	private boolean mIsPrepay;
	// 买家需付金额
	private int mBuyerPayMoney;
	private int mGuaranteeMoney;
	private ArrayList<String> mPhotoPaths;
	private ArrayList<String> mOriginPhotoPaths;

	private TextView mBuyerView;
	private ImageView mBuyerImageView;
	private TextView mBuyerPayView;
	private TextView mBuyerMoneyView;
	private View mBuyerGoPayView;
	private boolean mIsBuyerPaySuccess = false;

	private View mDividerLine;

	private View mSellerPayMoneyLayout;
	private TextView mSellerView;
	private ImageView mSellerImageView;
	private TextView mSellerPayView;
	private TextView mSellerMoneyView;
	private View mSellerGoPayView;
	private boolean mIsSellerPaySuccess = false;

	private View ll_deal_price;
	private View mCommitLayout;

	@Override
	protected void initView() {
		// 处理数据
		mTask = (TransactionTaskEntity) getIntent().getSerializableExtra(EXTRA_TRANS_TASK_ENTITY);
		if (mTask == null) {
			finish();
			return;
		}
		mIsCompanyTrans = getIntent().getBooleanExtra(EXTRA_IS_COMAPNY_TRANS, false);
		mIsPrepay = getIntent().getBooleanExtra(EXTRA_IS_PREPAY, true);
		mBuyerPayMoney = getIntent().getIntExtra(EXTRA_BUYER_PAY_MONEY, 0);
		mGuaranteeMoney = getIntent().getIntExtra(EXTRA_GUARANTEE_MONEY, 0);
		mPhotoPaths = getIntent().getStringArrayListExtra(EXTRA_PHOTO_PATH);
		mOriginPhotoPaths = getIntent().getStringArrayListExtra(EXTRA_ORIGIN_PHOTO_PATH);

		setContentView(R.layout.activity_pay_deposit);

		// 返回
		View backButton = findViewById(R.id.tv_common_back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		// 标题
		TextView titleTextView = (TextView) findViewById(R.id.tv_common_title);
		titleTextView.setText(getString(R.string.hc_transaction_prepay_pay));

		// 买家
		mBuyerView = (TextView) findViewById(R.id.buyer);
		mBuyerImageView = (ImageView) findViewById(R.id.buyer_success);
		mBuyerPayView = (TextView) findViewById(R.id.buyer_pay);
		mBuyerMoneyView = (TextView) findViewById(R.id.buyer_money);
		mBuyerMoneyView.setText("" + mBuyerPayMoney);
		mBuyerGoPayView = findViewById(R.id.buyer_go_pay_layout);
		mBuyerGoPayView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buyerGoPay();
			}
		});

		// 分割线
		mDividerLine = findViewById(R.id.divider_line);

		// 车主
		mSellerPayMoneyLayout = findViewById(R.id.seller_pay_money_layout);
		if (!mIsCompanyTrans) {
			mDividerLine.setVisibility(View.GONE);
			mSellerPayMoneyLayout.setVisibility(View.GONE);
		}
		mSellerView = (TextView) findViewById(R.id.seller);
		mSellerImageView = (ImageView) findViewById(R.id.seller_success);
		mSellerPayView = (TextView) findViewById(R.id.seller_pay);
		mSellerMoneyView = (TextView) findViewById(R.id.seller_money);
		mSellerMoneyView.setText(mTask.getSeller_company_prepay());
		mSellerGoPayView = findViewById(R.id.seller_go_pay_layout);
		mSellerGoPayView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sellerGoPay();
			}
		});

		//成本价
		ll_deal_price = findViewById(R.id.ll_deal_price);
		ll_deal_price.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(PayDepositActivity.this, DealPriceInfoActivity.class);
				if (mTask.isConsignment() && mTask.getDeal_price() > 0){
					intent.putExtra(DealPriceInfoActivity.EXTRA_DEAL_PRICE, mTask.getDeal_price());
				}else {
					intent.putExtra(DealPriceInfoActivity.EXTRA_DEAL_PRICE, mGuaranteeMoney);
				}
				intent.putExtra(DealPriceInfoActivity.EXTRA_SELLER_PREPAY, StringUtil.parseInt(mTask.getSeller_company_prepay(), 0));
				startActivityForResult(intent, REQUEST_CODE_DEAL_PRICE);
			}
		});

		// 去提交审核
		mCommitLayout = findViewById(R.id.commit_layout);
		mCommitLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCommitLayout.setEnabled(false);
				ProgressDialogUtil.showProgressDialog(PayDepositActivity.this, null, new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						mCommitLayout.setEnabled(true);
					}
				});
				if (mPhotoPaths != null && mPhotoPaths.size() > 0) {
					uploadImage();
				} else {
					// 上传完成，调用取消任务
					if (mTask.getAudit_status() == TaskConstants.STATUS_PRE_PAY) {
						AppHttpServer.getInstance().post(HCHttpRequestParam.transactionPrepay(mTask), PayDepositActivity.this, 0);
					} else {
						AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferMessage(mTask), PayDepositActivity.this, 0);
					}
				}
			}
		});

		// 买家需支付金额传进来为0的情况
		if (mBuyerPayMoney == 0) {
			mBuyerPayView.setText(getString(R.string.no_need_pay));
			mBuyerGoPayView.setVisibility(View.GONE);
			mIsBuyerPaySuccess = true;
		}

		//车主需支付金额传进来为0的情况
		String seller_company_prepay = mTask.getSeller_company_prepay();
		if (TextUtils.isEmpty(seller_company_prepay) || Integer.parseInt(seller_company_prepay) == 0) {
			mSellerPayView.setText(getString(R.string.no_need_pay));
			mSellerGoPayView.setVisibility(View.GONE);
			mIsSellerPaySuccess = true;
		}

		if ((mIsBuyerPaySuccess && mIsSellerPaySuccess) || (mIsBuyerPaySuccess && !mIsCompanyTrans)) {
			mCommitLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 买家去支付
	 */
	private void buyerGoPay() {
		SettlementIntent settlementIntent = new SettlementIntent(PayDepositActivity.this);
		settlementIntent.setAppToken(GlobalData.userDataHelper.getLastAppToken());
		settlementIntent.setCrmUserId("" + GlobalData.userDataHelper.getUser().getId());
		settlementIntent.setCrmUserName(GlobalData.userDataHelper.getUser().getName());
		settlementIntent.setCustomerName(mTask.getBuyer_name());
		settlementIntent.setCustomerPhone(mTask.getBuyer_phone());
		settlementIntent.setPrice("" + mBuyerPayMoney);
		settlementIntent.setTaskId(mTask.getOrder_number());
		settlementIntent.setTaskType(1);
		settlementIntent.setFromBusiness(true);
		startActivityForResult(settlementIntent, REQUEST_CODE_BUYTER_PAY);
	}

	/**
	 * 车主去支付
	 */
	private void sellerGoPay() {
		SettlementIntent settlementIntent = new SettlementIntent(PayDepositActivity.this);
		settlementIntent.setAppToken(GlobalData.userDataHelper.getLastAppToken());
		settlementIntent.setCrmUserId("" + GlobalData.userDataHelper.getUser().getId());
		settlementIntent.setCrmUserName(GlobalData.userDataHelper.getUser().getName());
		settlementIntent.setCustomerName(mTask.getSeller_name());
		settlementIntent.setCustomerPhone(mTask.getSeller_phone());
		settlementIntent.setPrice(mTask.getSeller_company_prepay());
		settlementIntent.setTaskId(mTask.getOrder_number());
		settlementIntent.setTaskType(1);
		settlementIntent.setFromBusiness(true);
		startActivityForResult(settlementIntent, REQUEST_CODE_SELLER_PAY);
	}

	// 上传图片
	private void uploadImage() {
		TransactionQiNiuUploadUtil qiniuUploadImageUtil = new TransactionQiNiuUploadUtil(this, mPhotoPaths);
		qiniuUploadImageUtil.startUpload(new TransactionQiNiuUploadUtil.QiniuUploadListener() {
			@Override
			public void onSuccess(List<String> keys) {
				// 上传完成，调用取消任务
				if (mOriginPhotoPaths != null && mOriginPhotoPaths.size() > 0) {
					if (keys == null) {
						keys = new ArrayList<>();
					}
					for (String key : mOriginPhotoPaths) {
						keys.add(key);
					}
				}
				if (mIsPrepay) {
					mTask.setContract_pic((ArrayList<String>) keys);
				} else {
					mTask.setPrepay_transfer_pic((ArrayList<String>) keys);
					mTask.setContract_pic(null);
				}

				if (mTask.getAudit_status() == TaskConstants.STATUS_PRE_PAY) {
					AppHttpServer.getInstance().post(HCHttpRequestParam.transactionPrepay(mTask), PayDepositActivity.this, 0);
				} else {
					AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferMessage(mTask), PayDepositActivity.this, 0);
				}
			}

			@Override
			public void onFailed() {
				// 取消上传，重新上传
				ProgressDialogUtil.closeProgressDialog();
			}
		});
	}

	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		ProgressDialogUtil.closeProgressDialog();
		mCommitLayout.setEnabled(true);
		if (action.equals(HttpConstants.ACTION_PREPAY_TRANSACTION)) {
			// 提交定金
			responsePay(response);
		} else if (action.equals(HttpConstants.ACTION_MODIFY_TRANSFER_MESSAGE)) {
			// 修改订单信息
			responseModify(response);
		}
	}

	/**
	 * 处理支付定金
	 */
	private void responsePay(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				Toast.makeText(this, getString(R.string.pay_deposit_succ), Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				break;
			default:
				Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
	}

	/**
	 * 修改
	 */
	private void responseModify(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				Toast.makeText(this, getString(R.string.hc_modify_succ), Toast.LENGTH_SHORT).show();
				HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
				setResult(RESULT_OK);
				break;
			default:
				Toast.makeText(this, response.getErrmsg(), Toast.LENGTH_SHORT).show();
				break;
		}
		finish();
	}

	RefundInfoEntity dealPrice;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_BUYTER_PAY) {
			// 买家支付成功
			if (resultCode == Activity.RESULT_OK) {
				mBuyerView.setVisibility(View.GONE);
				mBuyerImageView.setVisibility(View.VISIBLE);
				mBuyerPayView.setText(getString(R.string.buyer_pay_success));
				mBuyerGoPayView.setVisibility(View.GONE);
				mIsBuyerPaySuccess = true;
				if (!mIsCompanyTrans) {
					//不是回购车
					//成本价>0 / 担保车款>0 / 车主定金>0
					if (mTask.getVehicle_type() != 1 &&
							(mTask.getDeal_price() > 0 || mGuaranteeMoney > 0 || StringUtil.parseInt(mTask.getSeller_company_prepay(), 0) > 0)) {
						//填写转账信息
						ll_deal_price.setVisibility(View.VISIBLE);
					} else {
						ll_deal_price.setVisibility(View.GONE);
						mCommitLayout.setVisibility(View.VISIBLE);
					}
				} else {
					if (mIsSellerPaySuccess) {
						mCommitLayout.setVisibility(View.VISIBLE);
					}
				}
			}
		} else if (requestCode == REQUEST_CODE_SELLER_PAY) {
			// 车主支付成功
			if (resultCode == Activity.RESULT_OK) {
				mSellerView.setVisibility(View.GONE);
				mSellerImageView.setVisibility(View.VISIBLE);
				mSellerPayView.setText(getString(R.string.seller_pay_success));
				mSellerGoPayView.setVisibility(View.GONE);
				mIsSellerPaySuccess = true;
				if (mIsBuyerPaySuccess) {
					mCommitLayout.setVisibility(View.VISIBLE);
				}
			}
		} else if (requestCode == REQUEST_CODE_DEAL_PRICE) {
			// 成本价
			if (resultCode == Activity.RESULT_OK) {
				dealPrice = data.getParcelableExtra(DealPriceInfoActivity.EXTRA_DEAL_PRICE);
				if (dealPrice != null) {
					mTask.setSeller_name(dealPrice.getName());
					mTask.setSeller_bank(dealPrice.getBank());
					mTask.setSeller_card(dealPrice.getCard());

					ll_deal_price.setVisibility(View.GONE);
					mCommitLayout.setVisibility(View.VISIBLE);
				}
			}
		}
	}
}
