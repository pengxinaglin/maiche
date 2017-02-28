package com.haoche51.sales;

import android.content.Context;

import com.haoche51.sales.helper.BrandLogoHelper;
import com.haoche51.sales.helper.DatabaseHelper;
import com.haoche51.sales.helper.ResourceHelper;
import com.haoche51.sales.helper.UserDataHelper;
import com.haoche51.sales.util.HCLogUtil;

public class GlobalData {

    public static Context mContext = null;
    public static DatabaseHelper dbHelper = null;
    public static UserDataHelper userDataHelper = null;
    public static ResourceHelper resourceHelper = null;
    public static BrandLogoHelper brandLogo = null;

    public static void init(Context mContext) {
        HCLogUtil.e("push", "init global data");
        GlobalData.mContext = mContext;
        userDataHelper = new UserDataHelper(mContext);
        resourceHelper = new ResourceHelper(mContext);
        dbHelper = new DatabaseHelper(mContext);
        brandLogo = new BrandLogoHelper(mContext);
    }
}
