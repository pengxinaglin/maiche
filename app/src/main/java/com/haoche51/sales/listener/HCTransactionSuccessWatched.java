package com.haoche51.sales.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 15/9/29.
 */
public class HCTransactionSuccessWatched {

    private static HCTransactionSuccessWatched taskWatched;

    public static HCTransactionSuccessWatched getInstance() {
        if (taskWatched == null) {
            synchronized (HCTransactionSuccessWatched.class) {
                if (taskWatched == null) {
                    taskWatched = new HCTransactionSuccessWatched();
                }
            }
        }
        return taskWatched;
    }

    private HCTransactionSuccessWatched() {
    }

    private List<TaskDataObserver> mObList = null;

    public void registerDataObserver(TaskDataObserver mDataObserver) {
        if (mObList == null) mObList = new ArrayList<TaskDataObserver>();
        mObList.add(mDataObserver);
    }

    public void UnRegisterDataObserver(TaskDataObserver mDataObserver) {
        mObList.remove(mDataObserver);
    }

    public void notifyWatchers(Object o) {
        if (mObList == null) return;
        for (TaskDataObserver observer : mObList) {
            observer.onChanged(o);
        }
    }

}

