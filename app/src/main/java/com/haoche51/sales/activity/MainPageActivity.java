package com.haoche51.sales.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.haoche51.record.service.RecordService;
import com.haoche51.sales.Debug;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.HCApplication;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.user.LoginActivity;
import com.haoche51.sales.activity.user.SettingActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.constants.ThirdPartyConst;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.hccustomer.CustomerMainActivity;
import com.haoche51.sales.hcmessage.MessageMainActivity;
import com.haoche51.sales.hcrecommend.RecommendMainActivity;
import com.haoche51.sales.hcshare.HuiGouVehicleSourceActivity;
import com.haoche51.sales.hctransaction.TransactionMainActivity;
import com.haoche51.sales.hctransfer.TransferMainActivity;
import com.haoche51.sales.hcvehiclerecommend.VehicleRecommendListActivity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.service.AutoUpdateVersionService;
import com.haoche51.sales.service.UploadTransactionReadyService;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.ServiceInfoUtil;
import com.haoche51.sales.util.StringUtil;
import com.haoche51.sales.util.ThirdPartInjector;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.webview.WebViewActivity;
import com.haoche51.sales.workreport.WorkReportActivity;
import com.haoche51.settlement.onlinepay.OnlinePayIntent;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class MainPageActivity extends CommonBaseActivity implements ViewPager.OnPageChangeListener {

	/**
	 * 用户信息
	 */
	@ViewInject(R.id.iv_level)
	private ImageView ivlevel;//当月级别
	@ViewInject(R.id.iv_user_photo)
	private ImageView imageViewUserPhoto;//用户头像
	@ViewInject(R.id.tv_layout_user_welcome_name)
	private TextView textVieName;//欢迎语
	@ViewInject(R.id.tv_login_user)
	private TextView textUser;//欢迎语第二行
	@ViewInject(R.id.tv_premonth)
	private TextView textPremonth;//上个月
	@ViewInject(R.id.tv_currmonth)
	private TextView textCurrmonth;//本月个月
	@ViewInject(R.id.ll_point)
	private LinearLayout ll_point;//工资标点
	@ViewInject(R.id.vp_wage)
	private ViewPager viewPagerWage;//工资
	@ViewInject(R.id.iv_message)
	private ImageView iv_message;//信息

	/**
	 * 工作汇报方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_work_report)
//	private RelativeLayout relativeLayoutWorkReport;
//	@ViewInject(R.id.tv_work_report_tip)
//	private TextView textViewWorkReportTip;//提醒数
	@ViewInject(R.id.iv_work_report)
	private ImageView iv_work_report;//图标
	@ViewInject(R.id.tv_work_report)
	private TextView tv_work_report;
	/**
	 * 任务方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_transaction)
//	private RelativeLayout relativeLayoutTransaction;
	@ViewInject(R.id.tv_transaction_tip)
	private TextView textViewTransactionTip;//提醒数
	@ViewInject(R.id.iv_transaction)
	private ImageView iv_transaction;//图标
	@ViewInject(R.id.tv_transaction)
	private TextView tv_transaction;//项目名称
	/**
	 * 客户方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_customer)
//	private RelativeLayout rl_layout_right_customer;
//	@ViewInject(R.id.tv_customer_tip)
//	private TextView tv_custom_tip;//提醒数
	@ViewInject(R.id.iv_customer)
	private ImageView iv_customer;//图标
	@ViewInject(R.id.tv_customer)
	private TextView tv_customer;//项目名称
	/**
	 * 车源匹配方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_vehicle_recommend)
//	private RelativeLayout relativeLayoutVehicleRecommend;
//	@ViewInject(R.id.tv_vehicle_recommend_tip)
//	private TextView textViewVehicleRecommendTip;//提醒数
	@ViewInject(R.id.iv_vehicle_recommend)
	private ImageView iv_vehicle_recommend;//图标
	@ViewInject(R.id.tv_vehicle_recommend)
	private TextView tv_vehicle_recommend;//项目名称
	/**
	 * 过户任务方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_transfer)
//	private RelativeLayout relativeLayoutTransfer;
//	@ViewInject(R.id.tv_transfer_tip)
//	private TextView textViewTransferTip;//提醒数
	@ViewInject(R.id.iv_transfer)
	private ImageView iv_transfer;//图标
	@ViewInject(R.id.tv_transfer)
	private TextView tv_transfer;//项目名称
	/**
	 * 推荐有奖方块区域
	 */
//	@ViewInject(R.id.rl_layout_right_buyer_recommend)
//	private RelativeLayout relativeLayoutBuyerRecommend;
//	@ViewInject(R.id.tv_buyer_recommend_tip)
//	private TextView textViewBuyerRecommendTip;//提醒数
	@ViewInject(R.id.iv_buyer_recommend)
	private ImageView iv_buyer_recommend;//图标
	@ViewInject(R.id.tv_buyer_recommend)
	private TextView tv_buyer_recommend;//项目名称

	/**
	 * 权限标识
	 */
	private boolean workReportRight = false;
	private boolean transactionRight = false;
	private boolean transferRight = false;
	private boolean vehicleRecommendRight = false;
	private boolean buyerRecommendRight = false;
	private boolean messageRight = false;

	private PopupWindow popupWindow;
	private View viewPopupWindow;
	private LinkedList<Call> requestCalls = new LinkedList<Call>();

	/**
	 * 标识debug 信息
	 */
	private void showDebugInfo() {
		StringBuilder user = new StringBuilder(textUser.getText().toString());
		switch (Debug.EVIROMENT) {
			case 0:
				break;
			case 1:
				user.append("[线上测试]");
				break;
			case 2:
				user.append("[线下测试]");
				break;
			case 3:
				user.append("[线下测试外网]");
				break;
		}
		textUser.setText(user.toString());
	}

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		//默认显示当前月
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		textPremonth.setText(month - 1 + "月总收入");
		textCurrmonth.setText(month + "月收入预测");
		//默认显示第二个标点
		ll_point.getChildAt(1).setSelected(true);
	}

	@Override
	protected void initData() {
		super.initData();
		if (!GlobalData.userDataHelper.isLogin()) {
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
			return;
		} else {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					//获取缓存权限
					List<UserRightEntity> userRights = GlobalData.userDataHelper.getUserRight();
					if (userRights != null && userRights.size() > 0) {
						//有缓存的权限数据
						setUserRight(userRights);
					}
					DisplayUtils.setWelcome(textVieName);
					DisplayUtils.setWelcomeUser(textUser);
					showDebugInfo();
					//启动录音service
					startService(new Intent(MainPageActivity.this, RecordService.class));
					//add tingyun analytics
					ThirdPartInjector.startTingyun(MainPageActivity.this);
					//start baidu location
					ThirdPartInjector.startBaiduLocation();
					//start baidu push
					ThirdPartInjector.startBaiduPush();
					Call userRight = AppHttpServer.getInstance().post(HCHttpRequestParam.getUserRight(), MainPageActivity.this, 0);
					requestCalls.add(userRight);
				}
			});
		}
		if (!TextUtils.isEmpty(GlobalData.userDataHelper.getUser().getPic())) {
			DisplayUtils.setUserFace(imageViewUserPhoto, GlobalData.userDataHelper.getUser().getPic());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		HCLogUtil.e(this.className, "onStart ==========================");
		startUpdateService();

		Call subsidyInfo = AppHttpServer.getInstance().post(HCHttpRequestParam.getSubsidyInfo(), MainPageActivity.this, 0);
		Call urmsg = AppHttpServer.getInstance().post(HCHttpRequestParam.getUrmsgCount(), MainPageActivity.this, 0);
		requestCalls.add(subsidyInfo);
		requestCalls.add(urmsg);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//上传地销监控的service
		Intent intent = new Intent(GlobalData.mContext, UploadTransactionReadyService.class);
		//启动service
		if (!ServiceInfoUtil.isServiceRunning(GlobalData.mContext, UploadTransactionReadyService.class.getCanonicalName())) {
			//服务停止时，开启服务
			GlobalData.mContext.startService(intent);
		}
	}

	/**
	 * 启动更新服务
	 */
	private void startUpdateService() {
		HCLogUtil.e(this.className, "startUpdateService  service start==========================");
		Intent intent = new Intent(this, AutoUpdateVersionService.class);
		startService(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (requestCalls != null && !requestCalls.isEmpty()) {
			for (Call call : requestCalls) {
				AppHttpServer.getInstance().cancelRequest(call);
			}
			requestCalls.clear();
		}
	}

	/**
	 * 左上角头像点击事件
	 *
	 * @param v
	 */
	@OnClick(R.id.iv_user_photo)
	private void titleLeftFaction(View v) {
		HCActionUtil.launchActivity(MainPageActivity.this, SettingActivity.class, null);
	}

	/**
	 * 工作汇报
	 */
	@OnClick(R.id.rl_layout_right_work_report)
	private void onReportClick(View v) {
		if (workReportRight) {
			HCActionUtil.launchActivity(this, WorkReportActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 任务
	 */
	@OnClick(R.id.rl_layout_right_transaction)
	private void onTransactionCLick(View v) {
		if (transactionRight) {
			HCActionUtil.launchActivity(this, TransactionMainActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 过户
	 */
	@OnClick(R.id.rl_layout_right_transfer)
	private void onTransferClick(View v) {
		if (transferRight) {
			HCActionUtil.launchActivity(this, TransferMainActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 车源匹配
	 */
	@OnClick(R.id.rl_layout_right_vehicle_recommend)
	private void onVehicleRecommendClick(View v) {
		if (vehicleRecommendRight) {
			HCActionUtil.launchActivity(this, VehicleRecommendListActivity.class, null);
			ThirdPartInjector.onEvent(this, ThirdPartyConst.RECOMMEND_VEHICLE_ENTRY);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 推荐有奖
	 */
	@OnClick(R.id.rl_layout_right_buyer_recommend)
	private void onBuyerRFecommendClick(View v) {
		if (buyerRecommendRight) {
			HCActionUtil.launchActivity(this, RecommendMainActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 消息
	 */
	@OnClick(R.id.tv_message)
	private void onMessageClick(View v) {
		if (messageRight) {
			HCActionUtil.launchActivity(this, MessageMainActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 估价
	 */
	@OnClick(R.id.tv_appraisal)
	private void onAppraisalClick(View v) {
		Intent intent = new Intent(MainPageActivity.this, WebViewActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_URL, "http://m.haoche51.com/vehicle_valuation?platform=checker_app");
		intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.hc_popup_menu_titlebar_appraisal));
		startActivity(intent);
	}

	/**
	 * 客户
	 */
	@OnClick(R.id.rl_layout_right_customer)
	private void onCustomClick(View v) {
		if (transactionRight) {
//			HCActionUtil.launchActivity(this, RevisitMainActivity.class, null);
			HCActionUtil.launchActivity(this, CustomerMainActivity.class, null);
		} else {
			ToastUtil.showInfo(getString(R.string.hc_right_refuse));
		}
	}

	/**
	 * 收款
	 */
	@OnClick(R.id.rl_layout_right_online_pay)
	private void onOnlinePayClick(View v) {
		OnlinePayIntent intent = new OnlinePayIntent(MainPageActivity.this);
		intent.setCrmUserId("" + GlobalData.userDataHelper.getUser().getId());
		intent.setCrmUserName(GlobalData.userDataHelper.getUser().getName());
		intent.setAppToken(GlobalData.userDataHelper.getLastAppToken());
		startActivity(intent);
	}

	/**
	 * 车源
	 */
	@OnClick(R.id.rl_layout_right_back_source)
	private void onBackSourceClick(View v) {
		Intent intent = new Intent(MainPageActivity.this, HuiGouVehicleSourceActivity.class);
		intent.putExtra(WebViewActivity.EXTRA_TITLE, "车源");
		startActivity(intent);
	}

	/**
	 * HTTP 请求结果
	 *
	 * @param action    当前请求action
	 * @param requestId
	 * @param response  hc 请求结果
	 * @param error     网络问题造成failed 的error
	 */
	@Override
	public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
		if (isFinishing()) {
			return;
		}

		if (action.equals(HttpConstants.ACTION_GET_USER_RIGHT)) {
			doGetUserRightResponse(response);
		} else if (action.equals(HttpConstants.ACTION_GETSUBSIDYINFO)) {
			doGetSubsidyInfoResponse(response);
		} else if (action.equals(HttpConstants.ACTION_GET_URMSG_COUNT)) {
			doGetUrmsgCount(response);
		}
	}

	/**
	 * 获取消息未读条数
	 */
	private void doGetUrmsgCount(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				int count = StringUtil.parseInt(response.getData(), 0);
				iv_message.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				iv_message.setVisibility(View.GONE);
				break;
		}
	}

	/**
	 * 获取薪资
	 */
	private void doGetSubsidyInfoResponse(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				SubsidyInfoEntity entity = HCJsonParse.parseSubsidyInfoEntity(response.getData());
				if (entity != null) {
					if (transactionRight) {
						if (entity.getTask_count() > 0) {
							textViewTransactionTip.setVisibility(View.VISIBLE);
							textViewTransactionTip.setText(String.valueOf(entity.getTask_count()));
						} else {
							textViewTransactionTip.setVisibility(View.GONE);
						}
					}

					viewPagerWage.setAdapter(new MainPagerAdapter(entity));
					//默认显示第二页
					viewPagerWage.setCurrentItem(1);
					viewPagerWage.addOnPageChangeListener(this);
					switch (entity.getLevel()) {
						case TaskConstants.SALE_LEVEL_BJ:
							ivlevel.setImageResource(R.drawable.ic_level_bj);
							break;
						case TaskConstants.SALE_LEVEL_VW:
							ivlevel.setImageResource(R.drawable.ic_level_vw);
							break;
						case TaskConstants.SALE_LEVEL_BMW:
							ivlevel.setImageResource(R.drawable.ic_level_bmw);
							break;
						case TaskConstants.SALE_LEVEL_PS:
							ivlevel.setImageResource(R.drawable.ic_level_porsche);
							break;
						default:
							ivlevel.setImageResource(R.drawable.ic_level_bj);
							break;
					}
				}
				break;
			default:
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 获取用户权限成功
	 *
	 * @param response
	 */
	private void doGetUserRightResponse(HCHttpResponse response) {
		switch (response.getErrno()) {
			case 0:
				//解析用户权限，保存到SP文件中
				Type type = new TypeToken<List<UserRightEntity>>() {
				}.getType();
				try {
					List<UserRightEntity> userRights = response.getData(type);
					GlobalData.userDataHelper.setUserRight(response.getData()).commit();
					if (userRights != null && userRights.size() > 0) {
						setUserRight(userRights);
					}
				} catch (Exception e) {
					doGetUserRightFailure(response);
				}
				break;
			default:
				//网络请求失败。1、获取tips失败，则不现实tips；2、获取权限失败，读取缓存权限，
				// 如果是第一次登陆就读取权限失败，退出登录，让用户重新登陆
				doGetUserRightFailure(response);
				ToastUtil.showInfo(response.getErrmsg());
				break;
		}
	}

	/**
	 * 获取用户权限失败
	 * 首次登录成功后校验权限(获取权限失败，则自动退出，让用户重新登录)，
	 * 其他情况启动主页时，网络请求权限，请求成功使用服务端返回权限，请求失败仍然使用缓存权限
	 *
	 * @param response
	 */
	private void doGetUserRightFailure(HCHttpResponse response) {
		if (response.getErrno() == -7) {
			// -7  无效用户
			HCApplication.logout();
			return;
		}
		//判断是否首次登陆（使用权限判断，有权限缓存，不是首次登陆；没有权限缓存，是首次登陆，需退出重新登陆）
		List<UserRightEntity> userRights = GlobalData.userDataHelper.getUserRight();
		if (userRights != null && userRights.size() > 0) {
			//有缓存的权限数据
			setUserRight(userRights);
		} else {
			//无缓存的权限数据，退出重新登陆
			HCApplication.logout();
		}
	}

	/**
	 * 设置主页权限
	 */
	private void setUserRight(List<UserRightEntity> userRights) {
		//根据权限设置页面规则
		//权限编码：101工作汇报，201过户，301车源推荐，401看车，501买家推荐，601消息，701门店
		for (UserRightEntity userRight : userRights) {
			switch (userRight.getCode()) {
				case 101://工作汇报
					workReportRight = userRight.getRight() > 0;
					tv_work_report.setTextColor(getResources().getColor(workReportRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_work_report.setAlpha(workReportRight ? 1f : 0.2f);
					break;
				case 201://过户
					transferRight = userRight.getRight() > 0;
					tv_transfer.setTextColor(getResources().getColor(transferRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_transfer.setAlpha(transferRight ? 1f : 0.2f);
					break;
				case 301://车源推荐
					vehicleRecommendRight = userRight.getRight() > 0;
					tv_vehicle_recommend.setTextColor(getResources().getColor(vehicleRecommendRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_vehicle_recommend.setAlpha(vehicleRecommendRight ? 1f : 0.2f);
					break;
				case 401://看车
				case 701://门店
					//是否已经有权限
					if (!transactionRight)
						transactionRight = userRight.getRight() > 0;
					tv_transaction.setTextColor(getResources().getColor(transactionRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_transaction.setAlpha(transactionRight ? 1f : 0.2f);

					tv_customer.setTextColor(getResources().getColor(transactionRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_customer.setAlpha(transactionRight ? 1f : 0.2f);
					break;
				case 501://推荐买家
					buyerRecommendRight = userRight.getRight() > 0;
					tv_buyer_recommend.setTextColor(getResources().getColor(buyerRecommendRight ?
							R.color.self_black : R.color.hc_self_white_noright));
					iv_buyer_recommend.setAlpha(buyerRecommendRight ? 1f : 0.2f);
					break;
				case 601://消息
					messageRight = userRight.getRight() > 0;
					break;
			}
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;

		switch (position) {
			case 0:
				textPremonth.setVisibility(View.GONE);
				textCurrmonth.setVisibility(View.VISIBLE);
				textCurrmonth.setText(month + "月当前状况");
				break;
			case 1:
				textPremonth.setVisibility(View.VISIBLE);
				textCurrmonth.setVisibility(View.VISIBLE);
				textPremonth.setText(month - 1 + "月总收入");
				textCurrmonth.setText(month + "月收入预测");
				break;
			case 2:
				textPremonth.setVisibility(View.VISIBLE);
				textCurrmonth.setVisibility(View.GONE);
				textPremonth.setText(month + "月当前状况");
				break;
		}

		for (int i = 0; i < ll_point.getChildCount(); i++) {
			ll_point.getChildAt(i).setSelected(false);
		}
		ll_point.getChildAt(position).setSelected(true);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	private class MainPagerAdapter extends PagerAdapter {

		private SubsidyInfoEntity entity;
		private Map<Integer, SoftReference<View>> views;

		public MainPagerAdapter(SubsidyInfoEntity entity) {
			this.entity = entity;
			views = new HashMap<>(3);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		//每次滑动的时候生成的组件
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = null;
			if (!views.isEmpty() && position < views.size()) {
				SoftReference<View> viewSoftReference = views.get(position);
				if (viewSoftReference != null) {
					view = viewSoftReference.get();
				}
			}

			if (view == null) {
				int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
				switch (position) {
					case 0:
						view = View.inflate(MainPageActivity.this, R.layout.layout_pre_income, null);
						TextView textViewMoth = (TextView) view.findViewById(R.id.tv_month);
						textViewMoth.setText(month - 1 + "月总收入");
						TextView textViewInCome = (TextView) view.findViewById(R.id.tv_income);
						textViewInCome.setText(StringUtil.getFormatMoney(this.entity.getLast_total_income()));
						view.findViewById(R.id.tv_detail).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								if (TextUtils.isEmpty(entity.getPrev_month_url())) {
									ToastUtil.showInfo("暂未获取");
									return;
								}
								Intent intent = new Intent(MainPageActivity.this, WebViewActivity.class);
								intent.putExtra(WebViewActivity.EXTRA_URL, entity.getPrev_month_url());
								intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.hc_income_title));
								startActivity(intent);
							}
						});
						break;
					case 1:
						view = View.inflate(MainPageActivity.this, R.layout.layout_cur_income, null);
						TextView transFee = (TextView) view.findViewById(R.id.tv_trans_fee);
						transFee.setText(StringUtil.getFormatMoney(entity.getTrans_fee()));
						TextView finLoan = (TextView) view.findViewById(R.id.tv_fin_loan);
						finLoan.setText(StringUtil.getFormatMoney(entity.getFin_loan()));
						TextView finInsurance = (TextView) view.findViewById(R.id.tv_fin_insurance);
						finInsurance.setText(StringUtil.getFormatMoney(entity.getFin_insurance()));
						TextView oilFee = (TextView) view.findViewById(R.id.tv_oil_fee);
						oilFee.setText(StringUtil.getFormatMoney(entity.getOil_fee()));
						TextView bonus = (TextView) view.findViewById(R.id.tv_bonus);
						bonus.setText(StringUtil.getFormatMoney(entity.getBonus()));
						break;
					case 2:
						view = View.inflate(MainPageActivity.this, R.layout.layout_next_income, null);
						textViewMoth = (TextView) view.findViewById(R.id.tv_month);
						textViewMoth.setText(month + "月预测收入");
						textViewInCome = (TextView) view.findViewById(R.id.tv_income);
						textViewInCome.setText(StringUtil.getFormatMoney(this.entity.getCurrent_expect_income()));
						view.findViewById(R.id.tv_detail).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								if (TextUtils.isEmpty(entity.getCur_month_url())) {
									ToastUtil.showInfo("暂未获取");
									return;
								}
								Intent intent = new Intent(MainPageActivity.this, WebViewActivity.class);
								intent.putExtra(WebViewActivity.EXTRA_URL, entity.getCur_month_url());
								intent.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.hc_income_title));
								startActivity(intent);
							}
						});
						break;
				}
				try {
					views.put(position, new SoftReference<View>(view));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			container.addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
}
