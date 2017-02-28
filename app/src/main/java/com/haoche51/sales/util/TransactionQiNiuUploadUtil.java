package com.haoche51.sales.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.constants.PictureConstants;
import com.haoche51.sales.dialog.AlertDialog;
import com.haoche51.sales.entity.PhotoEntity;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpCallback;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TransactionQiNiuUploadUtil implements HCHttpCallback {
  CountDownLatch signal = new CountDownLatch(1);
  //图片图片列表
  private List<PhotoEntity> pictures;
  // 上传成功图片数量
  private int UPLOAD_IMAGE_COUNT = 0;
  //待上传图片数量
  private int imageCount = 0;
  //七牛uploadToken
  private String uploadToken;
  //七牛上传
  private UploadManager uploadManager;
  // 压缩图片异步线程
  private Thread mCopmressThread;
  CompressImageRunnable mRunnable;
  private Activity mActivity;

  private QiniuUploadListener mQiniuUploadListener;

  /**
   * 上传完成回调
   */
  public interface QiniuUploadListener {
    void onSuccess(List<String> keys);

    void onFailed();
  }

  /**
   * 当前处理事件handler
   */
  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case PictureConstants.GET_QINIU_TOKEN_FAILED:// 获取七牛token 失败
          Toast.makeText(mActivity, "连接服务器失败，请稍后再试！", Toast.LENGTH_SHORT).show();
          enableUpload(false);
          interrupt();
          break;
        case PictureConstants.GET_QINIU_TOKEN_SUCCESS:// 获取七牛token 成功。
          // 启动压缩上传
          mRunnable = new CompressImageRunnable();
          mCopmressThread = new Thread(mRunnable);
          mCopmressThread.start();
          break;
        case PictureConstants.UPLOAD_IMAGE: // 压缩后的图片可以直接上传
          uploadImage((PhotoEntity) msg.obj, msg.arg1);
          break;
        case PictureConstants.UPLOAD_IMAGE_SUCCESS:// 上传图片成功。刷新progress
          ProgressDialogUtil.setProgress(UPLOAD_IMAGE_COUNT);
          break;
        case PictureConstants.UPLOAD_IMAGE_FAILED://；
          enableUpload(false);
          ToastUtil.showInfo("未知异常");
          interrupt();
          break;
        case PictureConstants.UPLOAD_NETWORK_ERROR://网络错误
          enableUpload(false);
          ToastUtil.showInfo("网络连接超时！");
          interrupt();
          break;
        case PictureConstants.QINIU_UPLOAD_RETURN_ERROR://七牛服务器返回错误
          enableUpload(false);
          ToastUtil.showInfo("图片服务器返回链接错误，请稍后重试！");
          interrupt();
          break;
        case PictureConstants.QINIU_UPLOAD_SERVER_ERROR://七牛服务器错误
          enableUpload(false);
          ToastUtil.showInfo("图片服务器服务器繁忙，请稍后重试！");
          interrupt();
          break;
        case PictureConstants.COMPRESS_IMAGE_FAILED:// 压缩图片错误时返回
          enableUpload(false);
          ToastUtil.showInfo("图片压缩错误，请确认图片正确性!");
          interrupt();
          break;
        case PictureConstants.UPLOAD_COMPLETE: // 上传完成了
          if (!mActivity.isFinishing())
            ProgressDialogUtil.closeProgressDialog();//关闭上传图片进度条
          finish();
          break;
      }
      super.handleMessage(msg);
    }
  };


  public TransactionQiNiuUploadUtil(Activity mActivity, List<String> images) {
    this.mActivity = mActivity;
    if (images != null && images.size() > 0) {
      this.pictures = new ArrayList<>();
      for (int i = 0; i < images.size(); i++) {
        PhotoEntity entity = new PhotoEntity();
        entity.setIndex(i);
        entity.setPath(images.get(i));
        this.pictures.add(entity);
      }
    }
    //初始化准备上传
    initUploadManager();
  }

  private void initUploadManager() {
    try {
//			String recorderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//			Recorder recorder = new FileRecorder(recorderPath);
      String dirPath = Environment.getExternalStorageDirectory() + File.separator + "HCHaochebang" + File.separator + "Images" + File.separator + "recorder";
      Recorder recorder = new FileRecorder(dirPath);

      //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
      //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
      KeyGenerator keyGen = new KeyGenerator() {
        public String gen(String key, File file) {
          // 不必使用url_safe_base64转换，uploadManager内部会处理
          // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
          return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
        }
      };
      // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
      //UploadManager uploadManager = new UploadManager(recorder);  // 1
      //UploadManager uploadManager = new UploadManager(recorder, keyGen); // 2
      // 或 在初始化时指定：
      Configuration config = new Configuration.Builder()
        // recorder 分片上传时，已上传片记录器
        // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
        .recorder(recorder, keyGen)
        .build();
      uploadManager = new UploadManager(config);
    } catch (IOException e) { // 异常情况适用默认普通上传
      uploadManager = new UploadManager();
    }
  }


  /**
   * 启动上传
   */
  public void startNewUpload(QiniuUploadListener mQiniuUploadListener) {
    // 设置回调
    setmQiniuUploadListener(mQiniuUploadListener);
    startToNewWork();
  }

  /**
   * 开始工作
   */
  private void startToNewWork() {
    //检测是否用图片可传
    if (!checkPhoto()) {
      return;
    }
    //检测网络是否可用
    if (!NetInfoUtil.isNetConnected(mActivity)) {
      AlertDialog.createDialog(this.mActivity, "网络连接不可用,是否设置?", new AlertDialog.OnClickYesListener() {
        public void onClickYes() {
          //Caused by: android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.settings.WIRELESS_SETTINGS }
          //捕获异常，不做跳转
          try {
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            mActivity.startActivity(intent);
          } catch (Exception e) {
          }

        }
      });
      return;
    }
    //上传
    enableUpload(true);
    UPLOAD_IMAGE_COUNT = 0;//成功上传数清零
    imageCount = getUploadImagesCount();//获得上传总数
    ProgressDialogUtil.showProgressDialogWithCancel(this.mActivity, "正在上传中...", imageCount);
    // 获取上传七牛的token
    getQiniuUploadToken();
  }

  /**
   * 启动上传
   */
  public void startUpload(QiniuUploadListener mQiniuUploadListener) {
    //设置回调
    setmQiniuUploadListener(mQiniuUploadListener);
    startToWork();
  }

  /**
   * 开始工作
   */
  private void startToWork() {
    //检测是否用图片可传
    if (!checkPhoto()) {
      return;
    }
    //检测网络是否可用
    if (!NetInfoUtil.isNetConnected(mActivity)) {
      AlertDialog.createDialog(this.mActivity, "网络连接不可用,是否设置?", new AlertDialog.OnClickYesListener() {
        public void onClickYes() {
          //Caused by: android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.settings.WIRELESS_SETTINGS }
          //捕获异常，不做跳转
          try {
            Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
            mActivity.startActivity(intent);
          } catch (Exception e) {
          }

        }
      });
      return;
    }
    //上传
    enableUpload(true);
    UPLOAD_IMAGE_COUNT = 0;//成功上传数清零
    imageCount = getUploadImagesCount();//获得上传总数
    ProgressDialogUtil.showProgressDialogWithProgress(this.mActivity, "正在上传中...", imageCount);
    // 获取上传七牛的token
    getQiniuUploadToken();
  }

  /**
   * 获取七牛上传token
   */
  private void getQiniuUploadToken() {
    AppHttpServer.getInstance().post(HCHttpRequestParam.getUploadToken(), this, 0);
  }

  /**
   * 上传图片
   */
  private synchronized void uploadImage(final PhotoEntity image, int position) {

    final ImageResponseHandler mImageResponseHandler = new ImageResponseHandler(image, position);
    //设置uuid，在七牛中唯一
    if (image.getUnid() == null || TextUtils.isEmpty(image.getUnid())) { // uuid 为空。
      //设置一个新的uuid
      final StringBuilder sb = new StringBuilder();
      sb.append(GlobalData.userDataHelper.getUser().getId()).append(java.util.UUID.randomUUID()).append(".jpg");
      image.setUnid(sb.toString());
    } //不空的时候使用image.getUnid;
    if (signal.getCount() == 0) {
      signal = new CountDownLatch(1);
    }
    //上传
    uploadManager.put(image.getTemp_path(), image.getUnid(), uploadToken, mImageResponseHandler, null);
    try {
      signal.await(500, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param enable true 。上传 false,移出线程结束
   */
  private void enableUpload(boolean enable) {
    if (!enable) {
      if (mCopmressThread != null) {
        mCopmressThread.interrupt();
        mCopmressThread = null;
        mHandler.removeCallbacks(mRunnable);
      }
      if (!mActivity.isFinishing())
        ProgressDialogUtil.closeProgressDialog();
    }
  }

  private boolean checkPhoto() {
    return this.pictures == null ? false : this.pictures.size() > 0 ? true : false;
  }

  /**
   * 上传结束
   */
  private void finish() {
    //将上传完成的结果key回调返回
    if (this.mQiniuUploadListener != null) {
      List<String> keys = new ArrayList<>();
      for (PhotoEntity entity : this.pictures) {
        HCLogUtil.e("", "http://image1.haoche51.com/" + entity.getUrl());
        keys.add(entity.getUrl());
      }
      this.mQiniuUploadListener.onSuccess(keys);
    }
  }

  /**
   * 上传失败 出现中断
   */
  private void interrupt() {
    if (this.mQiniuUploadListener != null) {
      this.mQiniuUploadListener.onFailed();
    }
  }

  /**
   * 获取待上传图片数量
   */
  private final String IMAGTAG = "UploadImageHelper";

  private int getUploadImagesCount() {
    int count = 0;
    if (this.pictures != null)
      for (PhotoEntity image : pictures) {
        if (image.getPath() != null && (image.getUrl() == null || TextUtils.isEmpty(image.getUrl()))) {
          HCLogUtil.d(IMAGTAG, "pictures" + count);
          count++;
        }
      }
    return count;
  }

  /**
   * 图片处理压缩线程
   */
  private class CompressImageRunnable implements Runnable {
    @Override
    public void run() {
      int size = pictures.size();
      for (int i = 0; i < size; i++) {
        compressImages(pictures.get(i), i);
      }
    }
  }

  /**
   * 压缩图片
   */
  private void compressImages(PhotoEntity image, int position) {
    if (image.getPath() != null && (image.getUrl() == null || TextUtils.isEmpty(image.getUrl()))) { // 路径存在，且未上传过
      Map<String, Object> map = BitmapUtil.readCorrectBitMapFromFile(image.getPath(), BitmapUtil.ResolutionLevel.kResolutionThumb);

      switch (Integer.valueOf(map.get("code").toString())) {
        case 0: // 成功
          Bitmap bitmap = (Bitmap) map.get("bitmap");
          if (bitmap == null) {
            break;
          }
          StringBuilder sb = new StringBuilder();
          sb.append(Environment.getExternalStorageDirectory()).append("/").append(System.currentTimeMillis()).append(".jpg");
          boolean ret = BitmapUtil.writeCorrectBitmapToFile(bitmap, sb.toString(), 50);
          if (!ret) {
            new File(sb.toString()).delete();
            break;
          }
          image.setTemp_path(sb.toString());
          if (!bitmap.isRecycled()) { // 回收bitmap
            bitmap.recycle();
          }
          Message msg = mHandler.obtainMessage();
          msg.what = PictureConstants.UPLOAD_IMAGE;
          msg.obj = image;
          msg.arg1 = position;
          uploadImage((PhotoEntity) msg.obj, msg.arg1);
          break;
        default: // 失败
          mHandler.sendEmptyMessage(PictureConstants.COMPRESS_IMAGE_FAILED);
          break;
      }

    }
  }

  /**
   * 返回结果处理
   */
  private class ImageResponseHandler implements UpCompletionHandler {
    private PhotoEntity image;
    private int position;

    public ImageResponseHandler(PhotoEntity image, int position) {
      this.image = image;
      this.position = position;
    }

    /**
     * 上传完成回调
     */
    @Override
    public void complete(String arg0, ResponseInfo arg1, JSONObject arg2) {
      if (arg1.isOK()) {
        if (arg2 != null && !TextUtils.isEmpty(arg2.optString("key"))) {
          uploadSuccess(arg2.optString("key"));
        } else {
          uploadFailed(PictureConstants.QINIU_UPLOAD_RETURN_ERROR); //QINIU 返回键值对为空
        }
      } else {
        if (arg1.isServerError()) {
          uploadFailed(PictureConstants.QINIU_UPLOAD_SERVER_ERROR);//七牛服务器错误
        } else if (arg1.isNetworkBroken()) {
          uploadFailed(PictureConstants.UPLOAD_NETWORK_ERROR);//网络错误
        } else {
          uploadFailed(PictureConstants.UPLOAD_IMAGE_FAILED); //未知异常错误
        }
      }
      // 如果上传数 == 上传总数 认为上传完成
      if (imageCount == UPLOAD_IMAGE_COUNT) {
        mHandler.sendEmptyMessage(PictureConstants.UPLOAD_COMPLETE);
      }
      signal.countDown();
    }

    /**
     * 删除临时文件
     */
    public void removeTempImage() {
      if (new File(image.getTemp_path()).exists()) {
        new File(image.getTemp_path()).delete();
      }
    }

    /**
     * 上传成功
     */
    public void uploadSuccess(String url) {
      pictures.get(position).setUrl(url);
      UPLOAD_IMAGE_COUNT++; // 上传成功数
      //发送消息刷新上传进度
      mHandler.sendEmptyMessage(PictureConstants.UPLOAD_IMAGE_SUCCESS);
      removeTempImage();
    }

    /**
     * 上传失败
     */
    public void uploadFailed(int errno) {
      Message msg = mHandler.obtainMessage();
      msg.obj = image;
      msg.what = errno;
      mHandler.sendMessage(msg);
      removeTempImage();
    }
  }

  @Override
  public void onHttpStart(String action, int requestId) {

  }

  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
    //获得七牛token
    if (action.equals(HttpConstants.ACTION_GET_QINIU_TOKEN)) {
      switch (response.getErrno()) {
        case 0:
          //成功获得token
          uploadToken = response.getData();
          //发送消息开始压缩图片
          mHandler.sendEmptyMessage(PictureConstants.GET_QINIU_TOKEN_SUCCESS);
          break;
        default:
          mHandler.sendEmptyMessage(PictureConstants.GET_QINIU_TOKEN_FAILED);
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

  public void setmQiniuUploadListener(QiniuUploadListener mQiniuUploadListener) {
    this.mQiniuUploadListener = mQiniuUploadListener;
  }
}
