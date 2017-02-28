package com.haoche51.sales.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.activity.DownloadDialogActivity;
import com.haoche51.sales.activity.UpdateConfirmDialogActivity;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DeviceInfoUtil;
import com.haoche51.sales.util.FileDownloadUtil;
import com.haoche51.sales.util.HCLogUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;
import com.haoche51.sales.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 版本自动更新服务
 * Created by wfx on 2016/9/2.
 */
public class AutoUpdateVersionService extends Service {

    private DownloadManager mDownloadManager;
    private long downloadId;
    private UpdateVersionEntity mVersionEntity;
    private String tag = "UpdateVersionService";
    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        HCLogUtil.e(tag, "onCreate==========================");
        EventBus.getDefault().register(this);
        mDownloadManager = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    long cacheDownLoadId = SharedPreferencesUtils.getLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
                    if (cacheDownLoadId == downLoadId) {
                        HCLogUtil.e(tag, "接收到下载完成的广播======================");
                        Uri uri = FileDownloadUtil.getDownloadUri(downLoadId);
                        if (uri != null) {
                            FileDownloadUtil.installApk(uri);
                        }
                        HCLogUtil.e(tag, "处理完毕======================");
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HCLogUtil.e(tag, "onStartCommand==========================");
        checkDownloadStatus();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 这个时候用户可能没有安装，而是退出了安装界面，用户退出了我们的APP，
     * 在某个时候，又再次使用了我们的APP，这个时候我们不应该去下载新版本，
     * 而是使用已经下载已经存在本地的APK。
     */
    private void checkDownloadStatus() {
        //第一次下载的 downloadManager.enqueue(req)会返回一个downloadId，把downloadId保存到本地，
        // 用户下次进来的时候，取出保存的downloadId，然后通过downloadId来获取下载的状态信息
        downloadId = SharedPreferencesUtils.getLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
        if (downloadId == -1L) {
            AppHttpServer.getInstance().post(HCHttpRequestParam.checkVersion(DeviceInfoUtil.getAppCurrVersion()), new CheckVersionCallBack(), 0);
            HCLogUtil.e(tag, "checkDownloadStatus downloadId -1 http request==========================");
            return;
        }
        mDownloadManager = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        int status = FileDownloadUtil.getDownloadStatus(downloadId);
        switch (status) {
            case DownloadManager.STATUS_SUCCESSFUL:
                Uri uri = FileDownloadUtil.getDownloadUri(downloadId);
                if (uri == null) {
                    reDownload();
                    return;
                }
                PackageInfo downApkInfo = FileDownloadUtil.getApkInfo(uri.getPath());
                if (FileDownloadUtil.isNewAPK(downApkInfo)) {
                    FileDownloadUtil.installApk(uri);
                    stopSelf();
                } else {
                    reDownload();
                }
                break;
            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
            case -1://暂停时，将之前的id删掉，重新下载
                reDownload();
                break;
            default:
                int type = SharedPreferencesUtils.getInt(TaskConstants.SP_UPDATE_TYPE, TaskConstants.UPDATE_TYPE_NORMAL);
                if (type != TaskConstants.UPDATE_TYPE_NORMAL) {
                    openDownloadDialog();
                }
                HCLogUtil.e("apk is already downloading");
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateServiceEntity updateService) {
        if (mVersionEntity == null) {
            return;
        }
        if (mVersionEntity.getType() == TaskConstants.UPDATE_TYPE_NORMAL) {
            ToastUtil.showInfo(getString(R.string.downloading));
        } else {
            openDownloadDialog();
        }
        SharedPreferencesUtils.saveInt(TaskConstants.SP_UPDATE_TYPE, mVersionEntity.getType());
        FileDownloadUtil.startDownload(mVersionEntity.getUrl(), System.currentTimeMillis() + ".apk", "卖车神器", "版本更新中");
    }


    /**
     * 开启下载对话框
     */
    private void openDownloadDialog() {
        Intent intent = new Intent(this, DownloadDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * 当下载任务失败、暂停时，将之前的downloadId删掉，
     * 重新开启新的下载任务
     */
    private void reDownload() {
        mDownloadManager.remove(downloadId);
        SharedPreferencesUtils.saveLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
        AppHttpServer.getInstance().post(HCHttpRequestParam.checkVersion(DeviceInfoUtil.getAppCurrVersion()), new CheckVersionCallBack(), 0);
        HCLogUtil.e(tag, "checkDownloadStatus http request==========================");
    }


    /**
     * 解析更新版本的json串
     *
     * @param json
     */
    private void parseUpdateVersion(String json) {
        mVersionEntity = new HCJsonParse().parseUpdateVersion(json);
//        mVersionEntity = JsonParseUtil.fromJsonObject(json, UpdateVersionEntity.class);
        //0：不是最新包，需要更新 1：不需要更新
        if (mVersionEntity != null && mVersionEntity.getIs_new() == 0) {
//            EventBus.getDefault().post(new UpdateVersionEntity(TaskConstants.OP_DIS_DOWN_DIALOG));
            Intent intent = new Intent(this, UpdateConfirmDialogActivity.class);
            intent.putExtra(TaskConstants.BINDLE_VERSION_NAME, mVersionEntity.getVersion_name());
            intent.putExtra(TaskConstants.BINDLE_UPDATE_CONTENT, mVersionEntity.getBrief());
            intent.putExtra(TaskConstants.SP_UPDATE_TYPE, mVersionEntity.getType());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().post(new UpdateVersionEntity(TaskConstants.OP_DIS_DOWN_DIALOG));
        EventBus.getDefault().unregister(this);
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        HCLogUtil.e(tag, "onDestroy  unregister==========================");
        super.onDestroy();
    }

    private class CheckVersionCallBack implements HCHttpCallback {

        @Override
        public void onHttpStart(String action, int requestId) {


        }

        @Override
        public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
            if (HttpConstants.ACTION_CHECK_VERSION.equals(action)) {
                switch (response.getErrno()) {
                    case 0:
                        parseUpdateVersion(response.getData());
                        break;
                    default:
                        ToastUtil.showInfo(response.getErrmsg());
                        break;
                }
            }
        }

        @Override
        public void onHttpProgress(String action, int requestId, long bytesWritten, long totalSize) {

        }

        @Override
        public void onHttpRetry(String action, int requestId, int retryNo) {

        }

        @Override
        public void onHttpFinish(String action, int requestId) {

        }
    }

}
