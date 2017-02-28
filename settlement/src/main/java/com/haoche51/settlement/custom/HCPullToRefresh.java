package com.haoche51.settlement.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haoche51.settlement.R;
import com.haoche51.settlement.utils.HCLogUtil;

import java.lang.reflect.Method;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * Created by lightman_mac on 8/6/15.
 * <p/>
 * 怎么在这个类里设置footer的状态  而不用每次都在使用的时候设置?
 */
public class HCPullToRefresh extends LinearLayout implements AbsListView.OnScrollListener, View.OnTouchListener {
    public HCPullToRefresh(Context context) {
        this(context, null, 0);
    }

    public HCPullToRefresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HCPullToRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doInitViews();
    }

    private final static String TAG = "HCPULL";

    private PtrClassicFrameLayout mPtrFrame;
    private ListView mInnerLv;
    private OnRefreshCallback mOnRefreshCallback;
    private onUIRefreshCompleteCallback mOnUIRefreshCompleteCallback;

    private View footerView;
    private TextView mFooterTv;
    private ProgressBar mFooterPb;
    /**
     * 标识是否正在加载更多
     */
    private boolean isLoadingMore = false;

    /**
     * 标识是否  没有更多数据了
     */
    private boolean isNoMoreData = false;

    /**
     * 控制是否可以下拉 默认可以
     */
    private boolean canPull = true;

    private View emptyView;

    /***
     * 控制当可见条目数等于总条目数,不能下拉
     */
    private boolean isVisibleLessTotal = false;

    /**
     * 控制当有筛选栏隐藏时,不能下拉
     */
    private boolean isFilterBarVisible = true;

    private void doInitViews() {
        int res = R.layout.pay_pull_to_refresh;
        View rootView = LayoutInflater.from(getContext()).inflate(res, this);
        mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_frame_base);
        mPtrFrame.addPtrUIHandler(new PtrClassicDefaultHeader(getContext()) {
            public void onUIRefreshComplete(PtrFrameLayout frame) {
                super.onUIRefreshComplete(frame);
                if (mInnerLv.getEmptyView() == null && emptyView != null) {
                    mInnerLv.setEmptyView(emptyView);
                }

                if (mOnUIRefreshCompleteCallback != null) {
                    mOnUIRefreshCompleteCallback.onUIRefreshComplete();
                }
            }
        });

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {

               /* if(isVisibleLessTotal){
                    return false;
                }*/

                if (!isFilterBarVisible) {
                    return false;
                }

                if (isLvOnTop()) {
                    return canPull;
                } else {
                    return false;
                }
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                if (mOnRefreshCallback != null) {
                    mOnRefreshCallback.onPullDownRefresh();
                }
            }
        });

        mInnerLv = (ListView) rootView.findViewById(R.id.ptr_lv_base);
        //移出默认holder
        try {
            Method method = AbsListView.class.getDeclaredMethod("setOverScrollMode", int.class);
            method.setAccessible(true);
            method.invoke(mInnerLv, 2);// View.OVER_SCROLL_NEVER
        } catch (Exception e) {
            e.printStackTrace();
        }
        mInnerLv.setOnScrollListener(this);

        initFooterView();
    }

    private void initFooterView() {
        int footerRes = R.layout.pay_pulldown_footer;
        footerView = LayoutInflater.from(getContext()).inflate(footerRes, null);
        footerView.setVisibility(View.GONE);
        mFooterTv = (TextView) footerView.findViewById(R.id.pulldown_footer_text);
        mFooterPb = (ProgressBar) footerView.findViewById(R.id.pulldown_footer_loading);

        mInnerLv.addFooterView(footerView);

        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //底部加载更多点击
                seeIfNeedLoadMore();
            }
        });
    }

    public void setFirstAutoRefresh() {
        autoRefresh();
    }

    public void setCanPull(boolean canPull) {
        this.canPull = canPull;
    }

    public ListView getListView() {
        return mInnerLv;
    }

    public PtrClassicFrameLayout getPtrClassicFrameLayout() {
        return mPtrFrame;
    }

    public void autoRefresh() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);
    }

    public void setFooterStatus(boolean isNoMoreData) {
        this.isNoMoreData = isNoMoreData;
        if (footerView.getVisibility() != View.VISIBLE) {
            footerView.setVisibility(View.VISIBLE);
        }
        mFooterPb.setVisibility(View.GONE);
        int textRes = isNoMoreData ? R.string.pay_nomore_data : R.string.pay_loadmore;
        mFooterTv.setText(textRes);
        mFooterTv.setTextSize(14);
        mFooterTv.setTextColor(getResources().getColor(R.color.pay_edit_text_hint));
        isLoadingMore = false;
    }

    public void hideFooter() {
        if (footerView.getVisibility() != View.GONE) {
            footerView.setVisibility(View.GONE);
        }
    }

    public void finishRefresh() {
        mPtrFrame.refreshComplete();
    }

    public void setEmptyView(View view) {
        this.emptyView = view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                // ImageLoader.getInstance().pause();
                break;

            case SCROLL_STATE_IDLE:
                // ImageLoader.getInstance().resume();
                //在idle时候检查是否到了最底部.
                seeIfNeedLoadMore();
                break;

            case SCROLL_STATE_TOUCH_SCROLL:
                break;
        }
    }

    private boolean isLvOnTop() {
        if (mInnerLv.getChildCount() == 0) return true;
        return mInnerLv.getChildAt(0).getTop() == 0;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isVisibleLessTotal = visibleItemCount == totalItemCount;
    }

    private void seeIfNeedLoadMore() {
        if (mInnerLv.getAdapter() == null || isLoadingMore || isNoMoreData)
            return;
        if (mInnerLv.getLastVisiblePosition() == mInnerLv.getAdapter().getCount() - 1
                && mInnerLv.getChildAt(mInnerLv.getChildCount() - 1).getBottom() <= mInnerLv.getHeight()) {
            if (mOnRefreshCallback != null) {
                mOnRefreshCallback.onLoadMoreRefresh();
                mFooterPb.setVisibility(View.VISIBLE);
                mFooterTv.setText(R.string.pay_loading);
                isLoadingMore = true;
            }
        }
    }

    public void setFilterBarVisible(boolean isFilterBarVisible) {
        this.isFilterBarVisible = isFilterBarVisible;
    }

    public void setOnRefreshCallback(OnRefreshCallback callback) {
        this.mOnRefreshCallback = callback;
    }

    public void setmOnUIRefreshCompleteCallback(onUIRefreshCompleteCallback calback) {
        this.mOnUIRefreshCompleteCallback = calback;
    }

    public interface OnRefreshCallback {
        void onPullDownRefresh();

        void onLoadMoreRefresh();
    }

    public interface onUIRefreshCompleteCallback {
        void onUIRefreshComplete();
    }


    //-----------------------touch listener ---------------------------------//

    public interface OnPullUpDetector {
        void isPullUp(boolean isMovingUp);
    }

    private OnPullUpDetector mPullDetector;

    public void setPullUpDetector(OnPullUpDetector detector) {
        mPullDetector = detector;
        this.mInnerLv.setOnTouchListener(this);
    }


    private float downRawY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                HCLogUtil.d(TAG, "------ACTION_CANCEL  getRawY::::::" + event.getY());
                mPullDetector.isPullUp(false);
                break;
            case MotionEvent.ACTION_UP:
                HCLogUtil.d(TAG, "------ACTION_UP  getRawY::::::" + event.getY());
                float currentRawY = event.getY();

                float diffY = currentRawY - downRawY;
                HCLogUtil.d(TAG, "------ACTION_UP  diffY::::::" + diffY);
                if (Math.abs(diffY) > 15) {
                    if (diffY > 0) { //下拉
                        mPullDetector.isPullUp(false);
                        return true;
                    } else {    //上拉
                        mPullDetector.isPullUp(true);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                HCLogUtil.d(TAG, "------ACTION_DOWN  getRawY::::::" + event.getY());
                downRawY = event.getY();
                break;
        }

        return false;
    }

    //-----------------------touch listener ---------------------------------//
}
