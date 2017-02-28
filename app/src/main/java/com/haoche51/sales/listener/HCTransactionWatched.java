package com.haoche51.sales.listener;

import android.os.Bundle;

import com.haoche51.sales.dao.DataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 看车任务列表观察者
 * Created by mac on 15/9/29.
 */
public class HCTransactionWatched {

    private static HCTransactionWatched taskWatched = new HCTransactionWatched();

    public static HCTransactionWatched getInstance() {
        if (taskWatched == null) {
            synchronized (HCTransactionWatched.class) {
                if (taskWatched == null) {
                    taskWatched = new HCTransactionWatched();
                }
            }
        }
        return taskWatched;
    }

    private HCTransactionWatched() {
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
