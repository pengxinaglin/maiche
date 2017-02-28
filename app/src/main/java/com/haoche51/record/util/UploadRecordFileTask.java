package com.haoche51.record.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;

import com.haoche51.record.dao.PhoneRecordDAO;
import com.haoche51.record.entity.PhoneRecordEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.HCLogUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.Zone;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/***
 * 上传录音文件
 */
public class UploadRecordFileTask implements HCHttpCallback {
    private final static String TAG = "UploadRecordFile";

    private String mRecordDir = "";

    private static final String MRECORDDIR_NAME = "HCSalesCallRecord";//保存本地录音文件的文件夹名
    private static final String OLD_MRECORDDIR_NAME = "HCSalerCallRecord";//老的保存本地录音文件的文件夹名

    private Context mContext;

    /**
     * 标识 是否正在上传文件
     */
    private boolean isUploadingFile = false;

    public void setIsUploadingFile(boolean isUploadingFile) {
        this.isUploadingFile = isUploadingFile;
    }

    public boolean isUploadingFile() {
        return isUploadingFile;
    }

    public UploadRecordFileTask(Context context) {
        this.mContext = context;

        initDir();
        //检查之前是否有录音文件
        if (checkOldDir())
            moveOldrecordFileToNewDir();
    }

    public String getRecordDir() {
        return this.mRecordDir;
    }

    /**
     * 初始化存储根目录
     */
    private void initDir() {
        mRecordDir = Environment.getExternalStorageDirectory().getPath() + File.separator + MRECORDDIR_NAME + File.separator;
        File mDir = new File(mRecordDir);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
    }

    /**
     * 检测是否有原来的录音文件夹
     */
    private boolean checkOldDir() {
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + OLD_MRECORDDIR_NAME + File.separator;
        return new File(path).exists() && new File(path).isDirectory();
    }

    /**
     * 移动原来的录音文件到新文件夹
     */
    private void moveOldrecordFileToNewDir() {
        //原来路径
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + OLD_MRECORDDIR_NAME + File.separator;
        //获得文件夹
        File dir = new File(path);
        //获得文件夹下地文件
        File[] files = dir.listFiles();
        //遍历
        if (files != null && files.length > 0) {
            Collections.shuffle(Arrays.asList(files));
            for (File file : files) {
                //移动到目标文件夹下
                file.renameTo(new File(mRecordDir + File.separator + file.getName()));
            }
        }
        //删除文件夹
        dir.delete();
    }


    // 1.
    public synchronized void getUploadToken(final File uploadFile) {
        if (isUploadingFile) {
            HCLogUtil.d(TAG, "isUploadingFile now   return it " + uploadFile);
            return;
        } else {

            isUploadingFile = true;

            new AsyncTask<Void, Void, File>() {
                @Override
                protected File doInBackground(Void... params) {
                    return uploadFile == null ? getUploadFile() : uploadFile;
                }

                @Override
                protected void onPostExecute(final File file) {
                    if (file != null && !RecordUtil.getRecordingFile(mContext).equals(file.getName())) {
                        AppHttpServer.getInstance().post(HCHttpRequestParam.getFileToken(), UploadRecordFileTask.this, 0);
                    } else {
                        isUploadingFile = false;
                    }
                }
            }.execute();
        }
    }

    /***
     * 获取录音文件
     *
     * @return
     */
    // 2.
    private synchronized File getUploadFile() {
        File dir = new File(mRecordDir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                Collections.shuffle(Arrays.asList(files));

                for (File file : files) {
                    if (file != null && file.isFile()) {
                        String fileName = file.getName();

                        // 如果当前文件为正在录音的文件 跳过
                        if (RecordUtil.getRecordingFile(mContext).equals(fileName)) {
                            HCLogUtil.d(TAG, fileName + "  is current recording ");
                            continue;
                        }

                        boolean hasUploaded = RecordUtil.getFileUploadStatus(mContext, file);
                        if (fileName.contains(".")) {
                            if (0 == file.length()) {
                                file.delete();
                                HCLogUtil.d(TAG, "delete 0kb file " + file);
                                continue;
                            }
                            String searchKey = fileName.split("\\.")[0];
                            if (!hasUploaded && 10 == searchKey.length()) {
                                HCLogUtil.d(TAG, "getUPloadFile " + fileName + ", hasUploaded= " + hasUploaded);
                                return file;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 上传录音文件到七牛服务器
     *
     * @param token
     * @param file
     */
    // 3.
    private synchronized void uploadToQiniuServer(final File file, String token) {
        if (TextUtils.isEmpty(token) || file == null || RecordUtil.getRecordingFile(mContext).equals(file.getName())) {
            isUploadingFile = false;
            return;
        }
        String key = null;
        UploadOptions options = null;
        UploadManager uploadManager;
        try {
            //设置断点记录文件保存的文件夹位置
            String recorderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Recorder recorder = new FileRecorder(recorderPath);
            //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
            //避免记录文件冲突（当key指定为null时），也可自定义文件名(下方为默认实现)：
            KeyGenerator keyGen = new KeyGenerator() {
                public String gen(String key, File file) {
                    // 不必使用url_safe_base64转换，uploadManager内部会处理
                    // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                    return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
                }
            };
            //设置分片上传
            Configuration config = new Configuration.Builder()
                    .chunkSize(128 * 1024)  //分片上传时，每片的大小。 默认 256K
                    .putThreshhold(256 * 1024)  // 启用分片上传阀值。默认 512K
                    .connectTimeout(10) // 链接超时。默认 10秒
                    .responseTimeout(60) // 服务器响应超时。默认 60秒
                    .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
                    .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                    .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
                    .build();
            //使用断点记录上传
            uploadManager = new UploadManager(config);
        } catch (Exception e) {
            e.printStackTrace();
            //如果有异常，改用使用普通上传
            uploadManager = new UploadManager();
        }
        HCLogUtil.e(TAG, "uploadToQiniuServer token = " + token);
        uploadManager.put(file, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String arg0, ResponseInfo arg1, JSONObject json) {
                HCLogUtil.e(TAG, "uploadToQiniuServer json = " + json);
                if (json != null) {
                    String key = json.optString("key");
                    if (!TextUtils.isEmpty(key)) {
                        HCLogUtil.d(TAG, file + "  uploaded to qiniu  return key = " + key);
                        uploadToHaoCheServer(file, key);
                    } else {
                        isUploadingFile = false;
                    }
                } else {
                    isUploadingFile = false;
                }
            }
        }, options);
    }

    /***
     * 上传文件标识到好车服务器
     */
    // 4.
    private synchronized void uploadToHaoCheServer(final File file, String record_url) {
        String fileName = file.getName();
        String searchKey = fileName.split("\\.")[0];
        StringBuffer where = new StringBuffer("create_time=").append(searchKey);
        List<PhoneRecordEntity> results = PhoneRecordDAO.getInstance().get(where.toString());

        HCLogUtil.d(TAG, "upload to haoche searchkey= " + searchKey + ", results =" + results);

        if (results == null || results.isEmpty()) {
            if (!RecordUtil.getRecordingFile(mContext).equals(fileName)) {
                file.delete();
            }
            isUploadingFile = false;
            //TODO 当前网络为WiFi/3G/4G并且已经连接
            if (SupportUtil.isCurrentNetWifiOr3gOr4g(mContext)) {
                getUploadToken(null);
            }
            return;
        }

        PhoneRecordEntity entity = results.get(0);
        AppHttpServer.getInstance().post(HCHttpRequestParam.uploadToServer(entity, record_url), UploadRecordFileTask.this, 1);
    }


    @Override
    public void onHttpStart(String action, int requestId) {

    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (action.equals(HttpConstants.ACTION_GET_UPLOAD_RECORD_TOKEN)) {
            doRequestGetUploadToken(response);
        } else if (action.equals(HttpConstants.ACTION_UPLOADTOSERVER)) {
            doRequestUploadToServer(response);
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

    private void doRequestGetUploadToken(HCHttpResponse response) {
        String token = response.getData();
        uploadToQiniuServer(getUploadFile(), token);
    }

    private void doRequestUploadToServer(HCHttpResponse response) {
        if (0 == response.getErrno()) {
            File file = getUploadFile();
            RecordUtil.saveFileUploaded(mContext, file);
            boolean hasDel = false;
            hasDel = file.delete();
            isUploadingFile = false;
            HCLogUtil.d(TAG, "uploaded  haoche server delete file " + " " + file + " del: " + hasDel);
            //TODO 当前网络为WiFi/3G/4G并且已经连接
            if (SupportUtil.isCurrentNetWifiOr3gOr4g(mContext)) {
                getUploadToken(null);
            }
        }
    }
}
