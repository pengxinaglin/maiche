package com.haoche51.sales.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 线程池 不用每次都重新创建新线程
 */
public class HCThreadUtil {

	/**
	 * 初始化线程池线程数
	 */
	private static final int INIT_THREAD_COUNTS = 3;

	private static ExecutorService mService = Executors.newFixedThreadPool(INIT_THREAD_COUNTS);

	public static void execute(Runnable command) {
		mService.execute(command);
	}
}
