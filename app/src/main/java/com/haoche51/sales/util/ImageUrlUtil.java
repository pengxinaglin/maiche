package com.haoche51.sales.util;

import android.text.TextUtils;

/**
 * 七牛图片剪切文档地址：
 * http://developer.qiniu.com/code/v6/api/dora-api/index.html#imageview2
 */
public class ImageUrlUtil {

    /**
     * 裁剪正中部分，等比缩小生成200x200缩略图
     * http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/1/w/200/h/200
     */
    public static String getNewUrl(String imageUrl, int width, int height) {
        if (TextUtils.isEmpty(imageUrl)) {
            return imageUrl;
        }
        if (width <= 0 || height <= 0) {
            return imageUrl;
        }

        String newImageUrl = imageUrl + "?imageView2/1/" + "w/" + width + "/h/" + height;
        return newImageUrl;
    }

    /**
     * 宽度固定为200px，高度等比缩小，生成200x133缩略图
     * http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/2/w/200
     */
    public static String getNewUrlByWidth(String imageUrl, int width) {
        if (TextUtils.isEmpty(imageUrl)) {
            return imageUrl;
        }
        if (width <= 0) {
            return imageUrl;
        }

        String newImageUrl = imageUrl + "?imageView2/2/" + "w/" + width;
        return newImageUrl;
    }

    /**
     * 高度固定为200px，宽度等比缩小，生成300x200缩略图
     * http://78re52.com1.z0.glb.clouddn.com/resource/gogopher.jpg?imageView2/2/h/200
     */
    public static String getNewUrlByHeight(String imageUrl, int height) {
        if (TextUtils.isEmpty(imageUrl)) {
            return imageUrl;
        }
        if (height <= 0) {
            return imageUrl;
        }

        String newImageUrl = imageUrl + "?imageView2/2/" + "h/" + height;
        return newImageUrl;
    }
}
