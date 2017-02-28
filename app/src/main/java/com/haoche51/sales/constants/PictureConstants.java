package com.haoche51.sales.constants;

public class PictureConstants {

	/** 外观图标识*/
	public static final int OUTER_PICTURE_LIMIT = 11;
	public static final String OUTER_PICTURE_TYPE = "outer_pics";
	public static final int OUTER_PICTURE_CHOSE = 0x123 ;
	/** 内饰图标识*/
	public static final int INNER_PICTURE_LIMIT = 16;
	public static final String INNER_PICTURE_TYPE = "inner_pics";
	public static final int INNER_PICTURE_CHOSE = 0x124;
	/** 细节图标识*/
	public static final String DETAIL_PICTURE_TYPE = "detail_pics";
	public static final int DETAIL_PICTURE_CHOSE = 0x125;
	/** 瑕疵图标识*/
	public static final int SINGLE_PICTURE_LIMIT = 1; //瑕疵每次只能选一张。需要标瑕疵位置
	public static final String DEFECT_PICTURE_TYPE = "defect_pics";
	public static final int DEFECT_PICTURE_CHOSE = 0x126;
	/** 选择单张照片**/
	public static final int SINGLE_PICTURE_CHOSE = 0x127;

	/**选择内置相机拍摄的整组照片*/
	public static final int ALBUM_PICTURE_CHOSE = 0x128;

  /** 选择汽车图片 */
  public static final int SELECT_CAR_PICTURE = 20;
  /** 选择瑕疵图片 */
  public static final int SELECT_DEFECT_PICTURE = 30;
  public static final int DELETESTATUS_DELTE = 1;//删除
  public static final int DELETESTATUS_FINISH = 2;//完成
  public static final int DELETESTATUS_CANCEL = 3;//取消
  public static final int DELETESTATUS_NULL = 4;//没有图片

	public static final int UPLOAD_IMAGE_SUCCESS = 0x201;//上传图片成功

	public static final int UPLOAD_IMAGE_FAILED = 0x202; //上传图片失败

	public static final int QINIU_UPLOAD_SERVER_ERROR = 0x301;//QINIU 服务器异常

	public static final int QINIU_UPLOAD_RETURN_ERROR = 0x302;//QINIU 返回键值对为空

	public static final  int UPLOAD_NETWORK_ERROR = 0x303; //网络上传错误

	public static final int UPLOAD_IMAGE_TYPE_ERROR = 0x304; //图片类型错误

	public static final int UPLOAD_WRITE_URL_ERROR = 0x305; //重写URL出错

	public static final int UPLOAD_REPORT_SUCCESS = 0x203;

	public static final int UPLOAD_REPORT_FAILED = 0x204;

	public static final int UPLOAD_REPORT = 0x205;

	public static final int GET_QINIU_TOKEN_SUCCESS = 0x206;

	public static final int GET_QINIU_TOKEN_FAILED = 0x207;

	/**
	 * 找不到图片
	 */
	public static final int PHOTO_NOT_FOUND = 0x208;
	/**
	 * 报告上传失败，请求响应结果为空
	 */
	public static final int UPLOAD_REPORT_FAILED_OTHER = 0x209;

	/**
	 * 图片没有URL
	 */
	public static final int PHOTO_NO_URL = 0x300;

	/**
	 * 当前上传照片线程尚未执行完
	 */
	public static final int UPLOAD_PHOTO_NOT_FINISH = 0x210;
	public static final int UPLOAD_IMAGE = 1000;
	public static final int UPLOAD_COMPLETE = 1005;
	/**
	 * 压缩图片
	 */
	public static final int COMPRESS_IMAGE_SUCCESS = 1;//压缩单张图片成功
	public static final int COMPRESS_IMAGE_FAILED = 2;//压缩单张图片失败
	public static final int COMPRESS_ALL_COMPLETE = 3;//压缩图片完成

}
