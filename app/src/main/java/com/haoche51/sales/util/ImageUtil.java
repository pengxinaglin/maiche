package com.haoche51.sales.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.View;

import com.haoche51.sales.GlobalData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil {

    /* Maximum pixels size for created bitmap. */
    private static final int MAX_NUM_PIXELS_THUMBNAIL = 512 * 384;

    private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 128 * 128;

    private static final int UNCONSTRAINED = -1;

    public static final int OPTIONS_NONE = 0x0;

    public static final int OPTIONS_SCALE_UP = 0x1;

    /**
     * Constant used to indicate we should recycle the input in
     * {@link #(Bitmap, int, int, int)} unless the output is the
     * input.
     */
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;

    /**
     * Constant used to indicate the dimension of mini thumbnail.
     *
     * @hide Only used by media framework and media provider internally.
     */
    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;

    /**
     * Constant used to indicate the dimension of micro thumbnail.
     *
     * @hide Only used by media framework and media provider internally.
     */
    public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;

    /**
     * 将 bitmap 转换成字节数组
     *
     * @param bitmap      要转换的 bitmap
     * @param needRecycle 转换后是否要回收 bitmap
     * @return 转换成功后的字节数组
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output);
        if (needRecycle) {
            bitmap.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 压缩图片并返回压缩后的图片文件
     *
     * @param filePath  源文件路径
     * @param maxWidth  输出图片的最大宽度
     * @param maxHeight 输出图片的最大高度
     */
    public static File compressImage(String filePath, int maxWidth, int maxHeight) {
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = decodeSampledBitmapFromResource(filePath, maxWidth, maxHeight);

            // 将压缩后的文件保存到 cache 目录
            File destFile = new File(GlobalData.mContext.getCacheDir(), "compressed.jpg");
            if (destFile.exists()) {
                destFile.delete();
            }
            fos = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return destFile;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            System.gc();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static Bitmap getBitmap(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            return BitmapFactory.decodeStream(new FileInputStream(filePath));
        } catch (OutOfMemoryError error) {
            System.gc();
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 从文件中读取图片，如果原图片过大，则缩放到最大宽度不超过 reqWidth，最大高度不超过 reqHeight。
     *
     * @param filePath  本地图片的文件路径
     * @param reqWidth  输出图片的最大宽度
     * @param reqHeight 输出图片的最大高度
     * @return 读取成功后的 bitmap，发生任何异常则返回 null。
     */
    public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            //options.inTempStorage = new byte[32 * 1024];
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            fis.close();
            fis = new FileInputStream(filePath);
            return BitmapFactory.decodeStream(new FileInputStream(filePath), null, options);
            //return BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
        } catch (OutOfMemoryError error) {
            System.gc();
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 截取屏幕
     *
     * @param rootView  要截屏的 view
     * @param maxWidth  输出图像的最大宽度
     * @param maxHeight 输出图像的最大高度
     * @return 截屏后的 bitmap 图片
     */
    public static Bitmap takeScreenshot(View rootView, int maxWidth, int maxHeight) {
        if (rootView == null) {
            return null;
        }

        Bitmap bitmap = null;
        OutputStream out = null;
        try {
            rootView.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

            int[] size = getScaleSize(bitmap.getWidth(), bitmap.getHeight(), maxWidth, maxHeight);
            bitmap = Bitmap.createScaledBitmap(bitmap, size[0], size[1], true);

            return bitmap;
        } catch (Exception e) {
            bitmap = null;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }

        return bitmap;
    }

    /**
     * 等比缩放 bitmap
     *
     * @param src       要进行缩放的 bitmap
     * @param maxWidth  缩放后的 bitmap 的最大宽度
     * @param maxHeight 缩放后的 bitmap 的最大高度
     * @return 缩放后的 bitmap
     */
    public static Bitmap getScaledBitmap(Bitmap src, int maxWidth, int maxHeight) {
        int[] size = getScaleSize(src.getWidth(), src.getHeight(), maxWidth, maxHeight);
        return Bitmap.createScaledBitmap(src, size[0], size[1], true);
    }

    /**
     * 在保持比例的情况下，返回获取图片的最大宽度和最大高度
     */
    private static int[] getScaleSize(int srcWidth, int srcHeight, int maxWidth, int maxHeight) {
        int w = srcWidth;
        int h = srcHeight;
        if (srcWidth > maxWidth || srcHeight > maxHeight) {
            float srcRatio = (float) srcWidth / srcHeight;
            float dstRatio = (float) maxWidth / maxHeight;
            if (dstRatio > srcRatio) {
                h = maxHeight;
                w = (int) (h * srcRatio);
            } else {
                w = maxWidth;
                h = (int) (w / srcRatio);
            }
        }

        return new int[]{w, h};
    }

    /**
     * 对位图进行剪切缩放
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height, OPTIONS_SCALE_UP);
        return thumbnail;
    }


    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }
}
