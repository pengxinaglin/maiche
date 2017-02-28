package com.haoche51.sales.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import com.haoche51.sales.R;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.service.UpdateVersionEntity;
import com.haoche51.sales.util.FileDownloadUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 展示下载进度
 * Created by wfx on 2016/9/7.
 */
public class DownloadDialogActivity extends Activity {
    private String tag = "DownloadDialogActivity";
    /**
     * 广播接收者--接收关闭页面的广播--finsh
     */
    private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(TaskConstants.ACTION_FINISH_MAIN.equals(intent.getAction())){
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_progress);
        registerReceiver(finishReceiver, new IntentFilter(TaskConstants.ACTION_FINISH_MAIN));
        HCLogUtil.e(tag, "onCreate==========================");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDownloadStatus();
        HCLogUtil.e(tag, "onStart==========================");
    }

    @Override
    protected void onResume() {
        super.onResume();
        HCLogUtil.e(tag, "onResume==========================");
    }

    /**
     * 检查下载任务的状态
     */
    private void checkDownloadStatus() {
        long downloadId = SharedPreferencesUtils.getLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
        if (downloadId != -1L) {
            int status = FileDownloadUtil.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Uri uri = FileDownloadUtil.getDownloadUri(downloadId);
                if (uri == null) {
                    FileDownloadUtil.resetDownloadId(downloadId);
                } else {
                    PackageInfo downApkInfo = FileDownloadUtil.getApkInfo(uri.getPath());
                    if (FileDownloadUtil.isNewAPK(downApkInfo)) {
                        FileDownloadUtil.installApk(uri);
                        try{
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        FileDownloadUtil.resetDownloadId(downloadId);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(UpdateVersionEntity entity) {
        if (entity != null && entity.getOperate() == TaskConstants.OP_DIS_DOWN_DIALOG) {
            disDownloadDialog();
        }
    }

    /**
     * 关闭下载对话框
     */
    private void disDownloadDialog() {
        try{
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 点击返回键不关闭activity
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        HCLogUtil.e(tag, "onStop==========================");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        HCLogUtil.e(tag, "onDestroy==========================");
        unregisterReceiver(finishReceiver);
        super.onDestroy();
    }
}
