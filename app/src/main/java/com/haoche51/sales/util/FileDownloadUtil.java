package com.haoche51.sales.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.constants.TaskConstants;

import java.io.File;

import static com.haoche51.sales.GlobalData.mContext;

/**
 * 文件下载工具
 * Created by wfx on 2016/9/20.
 */
public class FileDownloadUtil {
    private static final String tag= "FileDownloadUtil";

    /**
     * 获取指定下载任务的状态
     *
     * @param downloadId 下载任务的id
     * @return
     */
    public static int getDownloadStatus(long downloadId) {
        DownloadManager dm = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = dm.query(query);
        if (cursor == null) {
            return -1;
        }
        int status = -1;
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            cursor.close();
        }
        return status;
    }

//    /**
//     * 获取下载完apk的地址
//     *
//     * @param downloadId
//     * @return
//     */
//    public static Uri getDownloadUri(long downloadId) {
//        DownloadManager dm = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        return dm.getUriForDownloadedFile(downloadId);
//    }


    /**
     * 获取下载完apk的Uri
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     * @param downloadId
     * @return
     */
    public static Uri getDownloadUri(long downloadId) {
        Uri uri = null;
        DownloadManager downloader = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
//        long downloadId = PreferencesUtils.getLong(context, DownloadActivity.DOWNLOAD_ID);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        uri = Uri.parse(uriString);
                    }
                }
                cur.close();
            }
        }
        return uri;
    }


    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     *
     * @param downloadId
     * @return
     */
    public static File getDownloadedApk(long downloadId) {
        File targetApkFile = null;
        HCLogUtil.e(tag, "接收到下载完成的广播======================");
        Uri uri = getDownloadUri(downloadId);
        if (uri != null) {
            targetApkFile = new File(uri.getPath());
        }
        return targetApkFile;
    }

    /**
     * 判断已下载的安装包是否是最新的apk，是最新的，返回true
     * 逻辑：将已下载的安装包的版本号与本地已安装程序的版本号进行比较，
     * 如果大于本地已安装的版本号，则说明是最新的，返回true
     *
     * @param apkInfo
     * @return
     */
    public static boolean isNewAPK(PackageInfo apkInfo) {
        if (apkInfo == null) {
            return false;
        }
        String localPackName = GlobalData.mContext.getPackageName();
        if (!localPackName.equals(apkInfo.packageName)) {
            HCLogUtil.e("apk's package not match app's package");
            return false;
        }

        try {
            PackageInfo currPackInfo = GlobalData.mContext.getPackageManager().getPackageInfo(localPackName, 0);
            if (apkInfo.versionCode > currPackInfo.versionCode) {
                return true;
            }
            HCLogUtil.e("apk's versionCode <= app's versionCode");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取已下载安装包的包信息
     *
     * @param path
     * @return
     */
    public static PackageInfo getApkInfo(String path) {
        return GlobalData.mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
    }

    /**
     * 判断下载服务是否可用
     *
     * @return
     */
    public static boolean isDownloadUsable() {
        try {
            int state = GlobalData.mContext.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据指定的Uri安装apk
     *
     * @param uri
     */
    public static void installApk(Uri uri) {
        try {
            HCLogUtil.e(tag, "开启安装界面==========================");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            GlobalData.mContext.startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showInfo("下载完成，开启安装界面失败！");
            e.printStackTrace();
            HCLogUtil.e(tag, "开启安装界面 出现异常==========================" + e.getMessage());
        }
    }


    /**
     * 开始下载文件
     *
     * @param downloadURL  下载地址
     * @param destFileName 目标文件名称
     * @param title        通知栏下载标题
     * @param description  通知栏下载描述
     */
    public static void startDownload(String downloadURL, String destFileName, String title, String description) {
        //创建下载请求对象，并且把下载的地址放进去
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(downloadURL));
        //漫游状态下不允许下载
        req.setAllowedOverRoaming(false);
        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadURL));
        req.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        req.setVisibleInDownloadsUi(true);

        //设置下载路径
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destFileName);

        //设置标题和描述信息
        req.setTitle(title);
        req.setDescription(description);

        //设置为可被媒体扫描器找到
        req.allowScanningByMediaScanner();

        DownloadManager downloadManager = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (FileDownloadUtil.isDownloadUsable()) {
            long downloadId = downloadManager.enqueue(req);
            SharedPreferencesUtils.saveLong(TaskConstants.KEY_DOWNLOAD_ID, downloadId);
        } else {
            enableDownload();
        }

    }

    /**
     * 启用下载服务
     */
    public static void enableDownload() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalData.mContext.startActivity(intent);
    }

    /**
     * 重置下载任务id为-1
     *
     * @param downloadId 下载任务id
     */
    public static void resetDownloadId(long downloadId) {
        DownloadManager dm = (DownloadManager) GlobalData.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        dm.remove(downloadId);
        SharedPreferencesUtils.saveLong(TaskConstants.KEY_DOWNLOAD_ID, -1L);
    }
}
