package com.haoche51.settlement;

public class PayDebug {

    public static String VCODE_SERVER = "http://cashier.app.haoche51.com";

    // 开发环境
    public static int EVIROMENT = 0;

    /**
     * 初始化环境
     */
    public static void initEviroment(int eviroment) {
        EVIROMENT = eviroment;
        switch (EVIROMENT) {
            case 0:
                // 正式环境
//                VCODE_SERVER = "http://cashier.app.haoche51.com";
                VCODE_SERVER = "https://casher.app.haoche51.com";
                break;
            case 1:
                // Beta环境，线上数据
                VCODE_SERVER = "http://182.92.242.166:10099/";
                break;
            case 2:
                // 测试环境
                VCODE_SERVER = "http://192.168.50.183:10099/";
                break;
            case 3:
                // 测试环境外网地址
                VCODE_SERVER = "http://103.19.65.58:10099/";
                break;
        }
    }
}
