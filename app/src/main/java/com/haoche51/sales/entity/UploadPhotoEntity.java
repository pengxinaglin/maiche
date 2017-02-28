package com.haoche51.sales.entity;

/**
 * 需要上传的图片
 * Created by wufx on 2015/12/22.
 */
public class UploadPhotoEntity {
    private PhotoEntity photoEntity;
    /**
     * 在图片组中的位置
     */
    private int position;

    public PhotoEntity getPhotoEntity() {
        return photoEntity;
    }

    public void setPhotoEntity(PhotoEntity photoEntity) {
        this.photoEntity = photoEntity;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
