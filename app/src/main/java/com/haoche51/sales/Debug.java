package com.haoche51.sales;

public class Debug {

	// App接口地址
	public static String APP_SERVER;

	// Wap地址
	public static String WAP_SERVER;

	// 门店地址
	public static String STORE_SERVER;

	// 开发环境
	public static int EVIROMENT = 0;

	static {

		
		// 测试环境
		APP_SERVER = "http://192.168.50.183:10089/";
		WAP_SERVER = "http://192.168.50.183:1002";
		STORE_SERVER = "http://192.168.50.183:12306";
	}
}
