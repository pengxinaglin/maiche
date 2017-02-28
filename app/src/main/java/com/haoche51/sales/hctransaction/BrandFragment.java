package com.haoche51.sales.hctransaction;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.dao.VehicleBrandEntity;
import com.haoche51.sales.hcbasefragment.CommonBaseFragment;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.HCThreadUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.custom.SideBar;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.RequestHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 品牌 Fragment
 * Created by yangming on 2015/12/4.
 */
public class BrandFragment extends CommonBaseFragment implements View.OnClickListener {

  private final String SERIES_FRAGMENT_TAG = "series_fragment";

  @ViewInject(R.id.lv_brand_main)
  ListView mBrandLv;
  @ViewInject(R.id.tv_toast)
  TextView mTvToast;
  @ViewInject(R.id.sidrbar)
  SideBar mSideBar;

  View headView;

  BrandAdapter mBrandAdapter;
  List<VehicleBrandEntity> mBrandsData = new ArrayList<>();

  Map<String, Integer> mapBrandName2Position = new HashMap<>();
//  List<String> mHotBrandNames = new ArrayList<>();

  private List<VehicleBrandEntity> brandList;

  /**
   * 每行多少个品牌
   */
//  private int countsPerRow = 5;

  /**
   * 标记车系选择是否显示
   **/
  public static boolean isCarSeriesShowing = false;
  private VehicleSubscribeSeriesListFragment seriesListFragment;

  @Override
  protected int getContentView() {
    return R.layout.fragment_brand;
  }

  @Override
  protected void initView(View view) {
    super.initView(view);
    mSideBar = (SideBar) view.findViewById(R.id.sidrbar);
    mBrandLv = (ListView) view.findViewById(R.id.lv_brand_main);
    mTvToast = (TextView) view.findViewById(R.id.tv_toast);
    mSideBar.setTextView(mTvToast);

    AppHttpServer.getInstance().post(HCHttpRequestParam.getAllBrands(), this, 0);

    mBrandAdapter = new BrandAdapter(mBrandsData, this);
    mBrandLv.setAdapter(mBrandAdapter);

    // 设置右侧触摸监听
    mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
      @Override
      public void onTouchingLetterChanged(String s) {
        // 该字母首次出现的位置
        int position = mBrandAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
          mBrandLv.setSelection(position);
        }
      }
    });
  }

  @Override
  protected void initData(Bundle savedInstanceState) {
    super.initData(savedInstanceState);
  }


  private void fillMapData(final List<VehicleBrandEntity> brands) {
    Runnable command = new Runnable() {
      @Override
      public void run() {
        int size = brands.size();
        for (int i = 0; i < size; i++) {
          VehicleBrandEntity b = brands.get(i);
          String name = b.getName();
          mapBrandName2Position.put(name, i);
        }
      }
    };
    HCThreadUtil.execute(command);
  }

  @Override
  public void onClick(View v) {
    int vid = v.getId();
    switch (vid) {
      case R.id.rel_brand_parent:
        VehicleBrandEntity mEntity = (VehicleBrandEntity) v.getTag(R.id.brand_convert_tag);
        // 取消点击品牌滚动
        int position = (Integer) v.getTag(R.id.for_brand_pos);
        int offset;
        if (0 == position) {
          offset = 0;
        } else {
          offset = -DisplayUtils.getDimenPixels(R.dimen.margin_45dp);
        }
        mBrandLv.smoothScrollToPositionFromTop(position, offset);
        showSeriesFragment(mEntity.getId());
        break;
    }
  }

  /**
   * 显示车系fragment
   *
   * @param brandId
   */
  public void showSeriesFragment(int brandId) {
    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction ft = getActivity().getSupportFragmentManager()
      .beginTransaction();
    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
    if (fragmentManager.findFragmentByTag(SERIES_FRAGMENT_TAG) == null) {
      seriesListFragment = VehicleSubscribeSeriesListFragment
        .newInstance(getActivity(), brandId);
    } else {
      seriesListFragment = (VehicleSubscribeSeriesListFragment) fragmentManager.findFragmentByTag(SERIES_FRAGMENT_TAG);
    }

    if (!seriesListFragment.isAdded()) {
      ft.add(R.id.fl_vehicle_sub_series_list, seriesListFragment, SERIES_FRAGMENT_TAG);
    }

    ft.show(seriesListFragment);
    ft.commitAllowingStateLoss();
    fragmentManager.executePendingTransactions();
    seriesListFragment.reloadData(brandId);
    isCarSeriesShowing = true;
  }

  /**
   * 隐藏车系fragment
   */
  public void hideSeriesFragment() {
    if (getActivity().getSupportFragmentManager().findFragmentByTag(SERIES_FRAGMENT_TAG) != null && seriesListFragment != null) {
      android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
      FragmentTransaction ft = getActivity().getSupportFragmentManager()
        .beginTransaction();
      ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
      ft.hide(seriesListFragment);
      ft.commitAllowingStateLoss();
      fragmentManager.executePendingTransactions();
      isCarSeriesShowing = false;
    }
  }

  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
    if (HttpConstants.ACTION_ALL_BRANDS.equals(action)) {
      switch (response.getErrno()) {
        case 0://0：表示接口请求成功
          doRequestSuccess(response.getData());
          break;
        default://非0：发生错误
          ToastUtil.showInfo(response.getErrmsg());
          break;
      }
    }
  }

  /**
   * 请求成功的处理
   *
   * @param jsonStr json串
   */
  private void doRequestSuccess(String jsonStr) {
    brandList = HCJsonParse.parseVehicleBrandList(jsonStr);
    if (brandList == null) {
      return;
    }

    List<VehicleBrandEntity> bList = new ArrayList<>();
    VehicleBrandEntity brand;
    for (VehicleBrandEntity brandEntity : brandList) {
      brand = new VehicleBrandEntity();
      brand.setId(brandEntity.getId());
      brand.setName(brandEntity.getName());
      brand.setFirst_char(brandEntity.getFirst_char());
      bList.add(brand);
    }
    mBrandsData = DisplayUtils.sortBrand(bList);
    fillMapData(mBrandsData);

    mBrandAdapter.setBrands(mBrandsData);
    mBrandAdapter.notifyDataSetChanged();
  }

}
