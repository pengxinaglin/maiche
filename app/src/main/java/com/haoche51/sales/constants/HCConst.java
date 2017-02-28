package com.haoche51.sales.constants;

/***
 * 管理所有intent 传递key 广播action等
 */
public class HCConst {

    /**
     * 过户状态改变action
     */
    public static final String ACTION_TRANSFER_CHANGED = "actionTransferChanged";

    public class Transfer {
        //列表接口tab
        public static final int HTTP_POST_TAB_TRANFER = 1;      //过户列表接口tab--待过户
        public static final int HTTP_POST_TAB_TRANFER_AUDIT = 2;//过户列表接口tab--审核中
        public static final int HTTP_POST_TAB_TRANFER_FINSH = 3;//过户列表接口tab--已过户
    }

    public class ReadyInfo {

        public static final int READYINFO_TYPE_NONE = 0;                       //没操作
        public static final int READYINFO_TYPE_CUSTOMER_PHONE = 1;          //客户电话
        public static final int READYINFO_TYPE_OWNER_PHONE = 2;             //车主电话
        public static final int READYINFO_TYPE_SALER_PHONE = 3;             //电销电话
        public static final int READYINFO_TYPE_CHECKER_PHONE = 4;           //评估师电话
        public static final int READYINFO_TYPE_VEHICLE_INFO = 5;            //车源详情
        public static final int READYINFO_TYPE_VEHICLE_INFO_CHECK_PARAM = 6;//车源详情-查看车辆参数
        public static final int READYINFO_TYPE_CHECKER_REPORT = 7;          //车检报告
        public static final int READYINFO_TYPE_CHECKER_REPORT_DOWNLOAD = 8; //车检报告-下载
        public static final int READYINFO_TYPE_CJD_REPORT = 9;              //4S 店保养记录
        public static final int READYINFO_TYPE_CJD_REPORT_DOWNLOADD = 10;   //4S 店保养记录-下载
        public static final int READYINFO_TYPE_VEHICLE_VISIT_RECORD = 11;   //车源回访记录
        public static final int READYINFO_TYPE_BUYER_VISIT_RECORD = 12;     //买家回访记录
        public static final int READYINFO_TYPE_VOICE = 13;                  //听录音
        public static final int READYINFO_TYPE_VOICE_BS = 14;               //听录音-车主买家录音


        public static final int READYINFO_NET_TYPE_NONE = 0;
        public static final int READYINFO_NET_TYPE_WIFI = 1;
        public static final int READYINFO_NET_TYPE_MOBILE = 2;
        public static final int READYINFO_NET_TYPE_EXCEPTION = 3;

    }

    public class SharePreferences {
        public static final String SP_USER_FACE = "sp_user_face";
    }

}
