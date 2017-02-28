package com.haoche51.sales.listener;

import android.os.Bundle;

import com.haoche51.sales.dao.DataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 公海客户列表观察者
 * Created by mac on 15/9/29.
 */
public class HCTransactionHighSeasWatched {

    private static HCTransactionHighSeasWatched taskWatched = new HCTransactionHighSeasWatched();

    public static HCTransactionHighSeasWatched getInstance() {
        if (taskWatched == null) {
            synchronized (HCTransactionHighSeasWatched.class) {
                if (taskWatched == null) {
                    taskWatched = new HCTransactionHighSeasWatched();
                }
            }
        }
        return taskWatched;
    }

    private HCTransactionHighSeasWatched() {
    }

    private List<DataObserver> mObList = null;

    public void registerDataObserver(DataObserver mDataObserver) {
        if (mObList == null) mObList = new ArrayList<DataObserver>();
        if (!mObList.contains(mDataObserver))
            mObList.add(mDataObserver);
    }

    public void UnRegisterDataObserver(DataObserver mDataObserver) {
        if (mObList != null && mDataObserver != null) {
            mObList.remove(mDataObserver);
        }
    }

    public void notifyWatchers() {
        if (mObList == null || mObList.size() == 0) return;
        for (DataObserver observer : mObList) {
            observer.onChanged();
        }
    }

    public void notifyWatchers(Bundle data) {
        if (mObList == null || mObList.size() == 0) return;
        for (DataObserver observer : mObList) {
            observer.onChanged(data);
        }
    }
}
