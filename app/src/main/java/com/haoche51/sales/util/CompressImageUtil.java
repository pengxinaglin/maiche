package com.haoche51.sales.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 压缩图片工具类
 * Created by wufx on 2016/1/7.
 */
public class CompressImageUtil {

  /**
   * 最小100
   */
  public static final int WIDTH_MIN_100 = 100;

  /**
   * 标准600
   */
  public static final int WIDTH_NORMAL_600 = 600;

  /**
   * 标准800
   */
  public static final int WIDTH_NORMAL_800 = 800;

  /**
   * 标准1200
   */
  public static final int WIDTH_NORMAL_1200 = 1200;

  /**
   * 最大3200
   */
  public static final int WIDTH_MAX_3200 = 3200;

  /**
   * 计算采样率
   *
   * @param options   选项
   * @param reqWidth  期望的宽
   * @param reqHeight 期望的高
   * @return
   */
  private static int calculateInSampleSize(BitmapFactory.Options options,
                                           int reqWidth, int reqHeight) {
    // Raw height and width of image
    int height = options.outHeight;
    int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

      // Calculate ratios of height and width to requested height and
      // width
      int heightRatio = Math.round((float) height / (float) reqHeight);
      int widthRatio = Math.round((float) width / (float) reqWidth);

      // Choose the smallest ratio as inSampleSize value, this will
      // guarantee
      // a final image with both dimensions larger than or equal to the
      // requested height and width.
      inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
    }
    return inSampleSize;
  }

  /**
   * 处理图片旋转
   *
   * @param srcFilePath 图片路径
   * @return
   */
  private static int readPictureDegree(String srcFilePath) {
    int degree = 0;
    try {
      ExifInterface exifInterface = new ExifInterface(srcFilePath);
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;
  }

  /**
   * 旋转bitmap
   *
   * @param bitmap
   * @param rotate 旋转度数
   * @return
   */
  private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
    if (bitmap == null)
      return null;

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    // Setting post rotate to 90
    Matrix mtx = new Matrix();
    mtx.postRotate(rotate);
    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
  }
//private static final String TAG="CompressImageUtil";

  /**
   * 压缩指定路径的图片，并获取bitmap
   *
   * @param srcFilePath 原始图片路径
   * @param reqWidth    期望的宽
   * @param reqHeight   期望的高
   * @return
   */
  public static Bitmap compressImage(String srcFilePath, int reqWidth, int reqHeight) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(srcFilePath, options);

    // 计算采样率
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;

    Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, options);
    if (bitmap == null) {
      return null;
    }
    //读取图片旋转的度数
    int degree = readPictureDegree(srcFilePath);
    //旋转图片到指定的度数
    bitmap = rotateBitmap(bitmap, degree);
    //将bitmap赋值给ImageView
    //如果给定的目录不存在，则创建
    return bitmap;
  }

  /**
   * 在指定的目录下创建图片
   *
   * @param bitmap
   * @param destFilePath
   * @param destFileName
   * @param quality
   */
  public static void createImage(Bitmap bitmap, String destFilePath, String destFileName, int quality) {
    File destDir = new File(destFilePath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    File destFile = new File(destDir, destFileName);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(destFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
      fos.flush();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      //关闭文件输出流
      try {
        if (fos != null)
          fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      //回收bitmap
      if (!bitmap.isRecycled()) {
        bitmap.recycle();
      }
    }
  }


  /**
   * 压缩指定路径的图片，并获取bitmap
   *
   * @param srcFilePath  原始图片路径
   * @param destFilePath 目标图片路径
   * @param destFileName 目标图片名称
   * @param reqWidth     期望的宽
   * @param reqHeight    期望的高
   * @param quality      图片质量 0-100，最大为100
   * @return
   */
  public static boolean compressImage(String srcFilePath, String destFilePath, String destFileName, int reqWidth, int reqHeight, int quality) {
    boolean result = true;
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(srcFilePath, options);

    // 计算采样率
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;

    Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, options);
    if (bitmap == null) {
      return false;
    }
    //读取图片旋转的度数
//    int degree = readPictureDegree(srcFilePath);
//    //旋转图片到指定的度数
//    bitmap = rotateBitmap(bitmap,degree) ;
    //如果给定的目录不存在，则创建
    File destDir = new File(destFilePath);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }
    File destFile = new File(destDir, destFileName);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(destFile);
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
      fos.flush();
    } catch (FileNotFoundException e) {
      result = false;
      e.printStackTrace();
    } catch (IOException e) {
      result = false;
      e.printStackTrace();
    } finally {
      //关闭文件输出流
      try {
        if (fos != null)
          fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      //回收bitmap
      if (!bitmap.isRecycled()) {
        bitmap.recycle();
      }
      return result;
    }
  }

}
