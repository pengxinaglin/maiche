package com.haoche51.sales.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BitmapUtil {
  private static final int UPLOAD_WIDTH = 1200;
  private static final int UPLOAD_HEIGHT = 900;
  private static final int UPLOAD_QUALITY = 100;
  private static int UPLOAD_HALF_QUALITY = 80;
  private static final float[] SCALE_RANGE = {0.74f, 0.76f};
  public static final int PICTURE_OUTSIZE = 0;
  public static final int PICTURE_INNER = 1;
  public static final int PICTURE_DETAIL = 2;
  public static final int PICTURE_DEFECT = 3;

  public enum ResolutionLevel {
    kResolutionNormal, kResolutionThumb, kResolutionFile, kResolutionMax
  }

  private static int getThreshold(ResolutionLevel level) {
    switch (level) {
      case kResolutionThumb:
        return 600;
      case kResolutionNormal:
        return 800;
      case kResolutionFile:
        return 1200;
      case kResolutionMax:
        return 3200;
      default:
        return 100;
    }
  }

  private static String errorMessage = null;

  public static String getErrorMessage() {
    return errorMessage;
  }

  public static boolean matchScale(String filePath) {
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(new File(filePath)));
    } catch (FileNotFoundException e) {
      return false;
    }
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(in, null, options);
    int width = options.outWidth;
    int height = options.outHeight;
    float scale = ((float) height) / width;
    return scale >= SCALE_RANGE[0] && scale <= SCALE_RANGE[1];
  }

	/*
     * public static Bitmap readBitmapFromFile(String filePath) { return
	 * (Bitmap)readBitmapFromFile(filePath,
	 * ResolutionLevel.kResolutionNormal).get("bitmap"); } 压缩图片
	 */

  public static Map<String, Object> readBitmapFromFile(String filePath, ResolutionLevel level) {
    Map<String, Object> map = new HashMap<String, Object>();

    Bitmap bitmap = null;
    try {
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(in, null, options);
      in.close();
      int i = 0;
      int threshold = getThreshold(level);
      while (true) {
        if (options.outWidth >> i < threshold) {
          in = new BufferedInputStream(new FileInputStream(new File(filePath)));
          options.inSampleSize = (int) Math.pow(2.0D, i - 1);
          options.inJustDecodeBounds = false;
          bitmap = BitmapFactory.decodeStream(in, null, options);
          break;
        }
        i += 1;
      }
      map.put("code", 0);
    } catch (FileNotFoundException e) {
      errorMessage = "catch FileNotFoundException: " + e.getMessage();
      map.put("code", -1);
    } catch (IOException e) {
      errorMessage = "catch IOException: " + e.getMessage();
      map.put("code", -2);
    } catch (OutOfMemoryError e) {
      errorMessage = "catch OutOfMemoryError: " + e.getMessage();
      map.put("code", -3);
    }
    map.put("bitmap", bitmap);
    return map;
  }

  private static float originWidth, originHeight;

  public static Map<String, Object> readCorrectBitMapFromFile(String filePath, ResolutionLevel level) {
    Map<String, Object> map = new HashMap<String, Object>();

    Bitmap bitmap = null;
    try {
      File file = new File(filePath);
      map.put("sd_photo_name", file.getName());
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(in, null, options);

      originWidth = options.outWidth;
      originHeight = options.outHeight;

      Log.d("thDebugPic", "origin =" + originWidth + ", " + originHeight);

      in.close();
      int i = 0;
      int threshold = getThreshold(level);
      while (true) {
        int optionWidth = options.outWidth;
        if (optionWidth >> i < threshold) {
          in = new BufferedInputStream(new FileInputStream(new File(filePath)));
          int sampleSize = (int) Math.pow(2.0D, i - 1);
          options.inSampleSize = sampleSize;
          options.inJustDecodeBounds = false;
          bitmap = BitmapFactory.decodeStream(in, null, options);
          Log.d("thDebugPic", "sampleSizez = " + sampleSize + ", optionWidth = " + optionWidth);

          break;
        }
        i += 1;
      }
      map.put("code", 0);
      in.close();
    } catch (FileNotFoundException e) {
      errorMessage = "catch FileNotFoundException: " + e.getMessage();
      map.put("code", -1);
    } catch (IOException e) {
      errorMessage = "catch IOException: " + e.getMessage();
      map.put("code", -2);
    } catch (OutOfMemoryError e) {
      errorMessage = "catch OutOfMemoryError: " + e.getMessage();
      map.put("code", -3);
    }
    map.put("bitmap", bitmap);
    return map;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  public static boolean writeCorrectBitmapToFile(Bitmap bitmap, String filePath, int quality) {
    File file = new File(filePath);
    boolean ret = false;
    try {
      file.createNewFile();
      if (!file.canWrite()) {
        errorMessage = "cannot write file: " + filePath;
        return false;
      }
      if (bitmap != null) {
        FileOutputStream outputStream = new FileOutputStream(file);

//        int resetHeight = (int) (UPLOAD_WIDTH * (originHeight / originWidth));

        HCLogUtil.log("origin " + originWidth + "," + originHeight + ",scale=" + (originWidth / originHeight) + "reset " + bitmap.getWidth() + ", " + bitmap.getHeight());

        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);//为避免CreateBitmap操作时OOM，预先缩放
        ret = bitmap2.compress(CompressFormat.JPEG, UPLOAD_HALF_QUALITY, outputStream);

        outputStream.flush();
        outputStream.close();

        if (!bitmap.isRecycled()) {
          bitmap.recycle();
        }
        if (!bitmap2.isRecycled()) {
          bitmap2.recycle();
        }
      } else {
        return false;
      }
    } catch (IOException e) {
      errorMessage = "catch IOException: " + e.getMessage();
      return false;
    }
    return ret;
  }


  public static boolean writeCorrectBitmapToFile(Bitmap bitmap, String filePath) {
    File file = new File(filePath);
    boolean ret = false;
    try {
      file.createNewFile();
      if (!file.canWrite()) {
        errorMessage = "cannot write file: " + filePath;
        return false;
      }
      if (bitmap != null) {
        FileOutputStream outputStream = new FileOutputStream(file);

        int resetHeight = (int) (UPLOAD_WIDTH * (originHeight / originWidth));

        HCLogUtil.log("origin " + originWidth + "," + originHeight + ",scale=" + (originWidth / originHeight) + "reset " + UPLOAD_WIDTH + ", " + resetHeight);

        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, UPLOAD_WIDTH, resetHeight, true);//为避免CreateBitmap操作时OOM，预先缩放
        ret = bitmap2.compress(CompressFormat.JPEG, UPLOAD_QUALITY, outputStream);

        outputStream.flush();
        outputStream.close();

        if (!bitmap.isRecycled()) {
          bitmap.recycle();
        }
        if (!bitmap2.isRecycled()) {
          bitmap2.recycle();
        }
      } else {
        return false;
      }
    } catch (IOException e) {
      errorMessage = "catch IOException: " + e.getMessage();
      return false;
    }
    return ret;
  }

  public static boolean writeBitmapToFile(Bitmap bitmap, String filePath) {
    File file = new File(filePath);
    boolean ret = false;
    try {
      file.createNewFile();
      if (!file.canWrite()) {
        errorMessage = "cannot write file: " + filePath;
        return false;
      }
      if (bitmap != null) {
        FileOutputStream outputStream = new FileOutputStream(file);
        Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, UPLOAD_WIDTH, UPLOAD_HEIGHT, false);
        ret = bitmap2.compress(CompressFormat.JPEG, UPLOAD_QUALITY, outputStream);
        outputStream.flush();
        outputStream.close();
        if (!bitmap.isRecycled()) {
          bitmap.recycle();
        }
        if (!bitmap2.isRecycled()) {
          bitmap2.recycle();
        }
      } else {
        return false;
      }
    } catch (IOException e) {
      errorMessage = "catch IOException: " + e.getMessage();
      return false;
    }
    return ret;
  }

  /**
   * 根据path 解析图片类型
   */
  public static int getPictureType(String path) {
    if (Pattern.matches(".*standard_out_[0-9]+.*", path)) {
      return PICTURE_OUTSIZE;
    }
    if (Pattern.matches(".*standard_iner_[0-9]+.*", path)) {
      return PICTURE_INNER;
    }
    if (Pattern.matches(".*standard_detail_[0-9]+.*", path) || Pattern.matches(".*free_detail_[0-9]+.*", path)) {
      return PICTURE_DETAIL;
    }
    if (Pattern.matches(".*scratch_[0-9]+_[0-9]+_[0-9]+.*", path)) {
      return PICTURE_DEFECT;
    }
    return PICTURE_DETAIL;// 默认放到细节图里
  }

  /**
   * @param picList
   * @return
   */
  public static ArrayList<String> getSortPicutre(ArrayList<String> picList) {

    return null;
  }

  /**
   * 检查图片是否存在
   *
   * @return
   */
  public static boolean pictureExit(String path) {
    if (path == null || "".equals(path)) {
      return false;
    }
    File f = new File(path);
    return f.exists();
  }

  /**
   * 保存Bitmap到文件中
   *
   * @param bitmap
   * @param path
   */
  public static void saveBitmapToFile(Bitmap bitmap, String path) {
    File f = new File(path);
    if (f.exists()) {
      f.delete();
    }
    try {
      FileOutputStream os = new FileOutputStream(f);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
      os.flush();
      os.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private static int calculateInSampleSize(BitmapFactory.Options options,
                                           int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;
      while ((halfHeight / inSampleSize) > reqHeight
        && (halfWidth / inSampleSize) > reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
  private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
    if (src == null) {
      return null;
    }
    Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    if (src != dst) { // 如果没有缩放，那么不回收
      src.recycle(); // 释放Bitmap的native像素数组
    }
    return dst;
  }

  // 从Resources中加载图片
  public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                       int resId, int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
    options.inSampleSize = calculateInSampleSize(options, reqWidth,
      reqHeight); // 计算inSampleSize
    options.inJustDecodeBounds = false;
    Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
    return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
  }

  // 从sd卡上加载图片
  public static Bitmap decodeSampledBitmapFromFd(String pathName,
                                                 int reqWidth, int reqHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(pathName, options);
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    options.inJustDecodeBounds = false;
    Bitmap src = BitmapFactory.decodeFile(pathName, options);
    return createScaleBitmap(src, reqWidth, reqHeight);
  }

}
