package com.haoche51.settlement.net;

/**
 * Created by xuhaibo on 15/8/26.
 */
public interface HCHttpCallback {
	/**
	 * 网络请求开始
	 * @param action 当前请求action
	 */
	void onHttpStart(String action,int requestId);

	/**
	 * 网络请求结束,请求服务器成功，请求服务器失败
	 * @param action 当前请求action
	 * @param response hc 请求结果
	 * @param error 网络问题造成failed 的error
	 */
	void onHttpComplete(String action,int requestId,HCHttpResponse response,Throwable error);
	/**
	 * 网络请求进度
	 * @param action 当前请求action
	 * @param bytesWritten
	 * @param totalSize
	 */
	void onHttpProgress(String action,int requestId,long bytesWritten, long totalSize);
	/***
	 * 重试次数回调
	 * @param action 当前请求action
	 * @param retryNo 重试次数
	 */
	void onHttpRetry(String action,int requestId,int retryNo);

	void onHttpFinish(String action, int requestId);
}
