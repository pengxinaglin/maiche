package com.haoche51.sales.hcvehiclerecommend;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.R;
import com.haoche51.sales.custom.HCPullToRefresh;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.hccustomer.CustomerDetailActivity;
import com.haoche51.sales.helper.UserDataHelper;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCActionUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.PreferenceUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangming on 2015/11/5.
 */
public class MatchCustomerListActivity extends CommonStateActivity {

  public static final String KEY_INTENT_EXTRA_VEHICLE = "key_intent_extra_vehicle";
  public static final int KEY_REQUEST_ID_REFRESH = 0;
  public static final int KEY_REQUEST_ID_MORE = 1;

  //  @ViewInject(R.id.image_view_match_customer_list_activity_vehicle_image)
  private ImageView imageViewVehicleImage;
  //  @ViewInject(R.id.text_view_match_customer_list_activity_vehicle_name)
  private TextView textViewVehicleName;
  //  @ViewInject(R.id.text_view_match_customer_list_activity_describe)
  private TextView textViewDescribe;
  //  @ViewInject(R.id.text_view_match_customer_list_activity_online_time)
  private TextView textViewOnlineTime;
  //  @ViewInject(R.id.text_view_match_customer_list_activity_price)
  private TextView textViewPrice;
  //  @ViewInject(R.id.text_view_match_customer_list_activity_match_count)
  private TextView textViewMatchCount;
  @ViewInject(R.id.hc_pull_to_refresh_match_customer_list_activity_list)
  private HCPullToRefresh hcPullToRefresh;

  View headView;

  private ListView listViewCustomers;
  private int pageSize = 10;
  private int mPage = 0;

  private MatchCustomerListAdapter adapter;
  private int mOriginMargin;

  private VehicleRecommentEntity vehicleRecommentEntity;
  private List<MatchCustomerEntity> listMatchCustomers = new ArrayList<>();


  @Override
  protected int getContentView() {
    return R.layout.activity_match_cutomer_list;
  }

  @Override
  protected int getTitleView() {
    return R.layout.layout_common_titlebar;
  }

  @Override
  protected void initView() {
    super.initView();
    setScreenTitle(R.string.match_customer_list_activity_title);

    vehicleRecommentEntity = (VehicleRecommentEntity) getIntent().getSerializableExtra(KEY_INTENT_EXTRA_VEHICLE);
    if (vehicleRecommentEntity == null) {
      ToastUtil.showText(getString(R.string.parameters_error));
    }

    View emptyView = findViewById(R.id.linear_layout_match_customer_list_activity_empty_view);
    hcPullToRefresh.setEmptyView(emptyView);
    hcPullToRefresh.setCanPull(true);
    hcPullToRefresh.setFirstAutoRefresh();
    listViewCustomers = hcPullToRefresh.getListView();
    listViewCustomers.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
    listViewCustomers.setDividerHeight(DisplayUtils.dip2px(this, 10));


    // 设置headerView必须在setAdapter之前
    listViewCustomers.addHeaderView(generateHeaderView());


    hcPullToRefresh.setOnRefreshCallback(new HCPullToRefresh.OnRefreshCallback() {
      @Override
      public void onPullDownRefresh() {
        mPage = 0;
        AppHttpServer.getInstance().post(HCHttpRequestParam.getMatchCusotmerList(mPage, pageSize
          , null, vehicleRecommentEntity.getVehicle_source_id()), MatchCustomerListActivity.this, KEY_REQUEST_ID_REFRESH);
      }

      @Override
      public void onLoadMoreRefresh() {
        AppHttpServer.getInstance().post(HCHttpRequestParam.getMatchCusotmerList(++mPage, pageSize
          , null, vehicleRecommentEntity.getVehicle_source_id()), MatchCustomerListActivity.this, KEY_REQUEST_ID_MORE);
      }
    });

    listViewCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        try {
          HCLogUtil.e("onItemClick-->" + i);
          Map<String, Object> map = new HashMap<>();
          //带看失败页面
          map.put("phone", listMatchCustomers.get(i - 1).getBuyer_phone());
          HCActionUtil.launchActivity(MatchCustomerListActivity.this, CustomerDetailActivity.class, map);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
//    setVehicleData();

  }

  @Override
  protected void initData() {
    super.initData();
  }


  private View generateHeaderView() {
    headView = LayoutInflater.from(this).inflate(R.layout.layout_vehicle_rec_vehicle_info, null);
    imageViewVehicleImage = (ImageView) headView.findViewById(R.id.image_view_match_customer_list_activity_vehicle_image);
    textViewVehicleName = (TextView) headView.findViewById(R.id.text_view_match_customer_list_activity_vehicle_name);
    textViewOnlineTime = (TextView) headView.findViewById(R.id.text_view_match_customer_list_activity_online_time);
    textViewDescribe = (TextView) headView.findViewById(R.id.text_view_match_customer_list_activity_describe);
    textViewPrice = (TextView) headView.findViewById(R.id.text_view_match_customer_list_activity_price);
    textViewMatchCount = (TextView) headView.findViewById(R.id.text_view_match_customer_list_activity_match_count);
    setVehicleData();
    return headView;
  }


  /**
   * 设置页面显示车源信息
   */
  private void setVehicleData() {

    if (vehicleRecommentEntity.getImage_url() != null) {
      int sw = DisplayUtils.getScreenWidth(this);
      int sh = (int) (sw * (436F / 580));
      Picasso.with(this).load(vehicleRecommentEntity.getImage_url() + "?imageView2/2/w/" + sw + "/h/" + sh).placeholder(R.drawable.login_logo).into(imageViewVehicleImage);
    }
    mOriginMargin = imageViewVehicleImage.getLayoutParams().height;

    textViewVehicleName.setText(vehicleRecommentEntity.getVehicle_name() == null ? "" : vehicleRecommentEntity.getVehicle_name());
    textViewOnlineTime.setText(UnixTimeUtil.formatYearDotMounth(vehicleRecommentEntity.getOnline_time()));
    textViewDescribe.setText(UnixTimeUtil.getYear(vehicleRecommentEntity.getRegister_time()) + "." + UnixTimeUtil.getMonth(vehicleRecommentEntity.getRegister_time())
      + "上牌 · " + vehicleRecommentEntity.getMiles() + "万公里 · " + vehicleRecommentEntity.getGearbox());

    textViewPrice.setText("报价：" + vehicleRecommentEntity.getSeller_price() + "万");

  }

  private void loadData() {
    dismissResultView(true);
    showLoadingView(false);

    if (vehicleRecommentEntity.getImage_url() != null) {
      int sw = DisplayUtils.getScreenWidth(this);
      int sh = (int) (sw * (436F / 580));
      Picasso.with(this).load(vehicleRecommentEntity.getImage_url() + "?imageView2/2/w/" + sw + "/h/" + sh).placeholder(R.drawable.login_logo).into(imageViewVehicleImage);
    }

    mPage = 0;
    AppHttpServer.getInstance().post(HCHttpRequestParam.getMatchCusotmerList(mPage, pageSize
      , null, vehicleRecommentEntity.getVehicle_source_id()), MatchCustomerListActivity.this, KEY_REQUEST_ID_REFRESH);
  }

  /**
   * 接口请求返回
   *
   * @param action    当前请求action
   * @param requestId
   * @param response  hc 请求结果
   * @param error     网络问题造成failed 的error
   */
  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
    super.onHttpComplete(action, requestId, response, error);
    if (isFinishing()) {
      return;
    }
    dismissLoadingView();
    hcPullToRefresh.finishRefresh();
    //记录更新最后一次刷新成功的时间
    PreferenceUtil.putInt(MatchCustomerListActivity.this, UserDataHelper.LAST_UPDATE_TIME, (int) (new Date().getTime() / 1000));
    doGetMatchCustomerResponse(requestId, response);
  }

  private void doGetMatchCustomerResponse(int requestId, HCHttpResponse response) {
    switch (response.getErrno()) {
      case 0:
        Type type = new TypeToken<List<MatchCustomerEntity>>() {
        }.getType();
        List<MatchCustomerEntity> list = new ArrayList<>();
        try {
          list = response.getData(type);
        } catch (Exception e) {
          e.printStackTrace();
          ToastUtil.showText(getString(R.string.common_erro_http_gson_decode));
          showErrorView(false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              loadData();
            }
          });
        }
        if (list == null) {
          hcPullToRefresh.setFooterStatus(true);

          return;
        }
        if (list != null && list.size() > 0) {
          textViewMatchCount.setText(getString(R.string.match_customer_count, list.size()));
        }
        //解析返回值
        if (requestId == KEY_REQUEST_ID_REFRESH) {//刷新
          listMatchCustomers.clear();
          listMatchCustomers.addAll(list);
          if (listMatchCustomers.size() == 0) {
            showNoDataView(false, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                loadData();
              }
            });
          }
        } else if (requestId == KEY_REQUEST_ID_MORE) {//加载更多
          listMatchCustomers = mergeConflict(listMatchCustomers, list);
        }
        boolean isNoMoreData = list.size() < pageSize;
        hcPullToRefresh.setFooterStatus(isNoMoreData);
        if (adapter == null) {
          adapter = new MatchCustomerListAdapter(MatchCustomerListActivity.this, listMatchCustomers, R.layout.match_customer_list_item_layout, vehicleRecommentEntity.getVehicle_source_id() + "");
        } else {
          adapter.setmList(listMatchCustomers);
        }
        listViewCustomers.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        break;
      default:
        hcPullToRefresh.finishRefresh();
        ToastUtil.showText(response.getErrmsg());
        if (requestId == KEY_REQUEST_ID_REFRESH) {//刷新
          showErrorView(false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              loadData();
            }
          });
        }
        break;
    }
  }

  /**
   * 本地合并 MatchCustomerEntity 列表
   *
   * @param mList
   * @param newList
   * @return
   */
  private List<MatchCustomerEntity> mergeConflict(List<MatchCustomerEntity> mList, List<MatchCustomerEntity> newList) {
    List<MatchCustomerEntity> result = new ArrayList<>();
    if (mList == null || newList == null) {
      return result;
    }
    for (MatchCustomerEntity customerEntity : newList) {
      if (!mList.contains(customerEntity)) {
        result.add(customerEntity);
      }
    }
    return result;
  }

}
