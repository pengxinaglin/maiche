package com.haoche51.sales.hcbaseadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by xuhaibo on 15/9/11.
 */
public abstract class HCCommonAdapter<T> extends BaseAdapter {

  protected Context mContext;
  protected List<T> mList;
  protected int mLayoutId;


  public HCCommonAdapter(Context context, List<T> data, int layoutId) {
    this.mContext = context;
    this.mLayoutId = layoutId;
    this.mList = data;

  }

  public List<T> getmList() {
    return mList;
  }

  public void setmList(List<T> mList) {
    this.mList = mList;
  }

  @Override
  public int getCount() {
    return mList.size();
  }


  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public T getItem(int position) {
    return mList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    HCCommonViewHolder holder = HCCommonViewHolder.getInstance(mContext, convertView, parent, mLayoutId, position);
    fillViewData(holder, position);
    return holder.getConvertView();

  }

  public abstract void fillViewData(HCCommonViewHolder holder, int position);

}
