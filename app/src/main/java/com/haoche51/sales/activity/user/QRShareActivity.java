package com.haoche51.sales.activity.user;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.User;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.helper.ImageLoaderHelper;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.BitmapUtil;
import com.haoche51.sales.util.CodeCreatorUtil;
import com.haoche51.sales.util.SharedPreferencesUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;

/**
 * Created by yangming on 2015/11/25.
 */
public class QRShareActivity extends CommonBaseActivity {

    /**
     * 二维码
     */
    @ViewInject(R.id.iv_qrcode_app_download)
    private ImageView iv_qrcode_app_download;
    @ViewInject(R.id.iv_qrcode_introduction)
    private ImageView iv_qrcode_introduction;
    private String myQRCodePath = Environment.getExternalStorageDirectory() + "/myqrcode.png";


    @Override
    protected void initView() {
        super.initView();
        setContentView(R.layout.activity_qrcode_share);
        registerTitleBack();
        setScreenTitle(R.string.hc_qrcode_share_title);
        ViewUtils.inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        //    setUpUserShareStat();
        showQRCode();
    }


    private void setUpUserShareStat() {

        // TODO:设置分享次数和排名信息,和排名前三的评估师姓名

        /**
         * 获取个人排名
         *
         */
        int checker_id = GlobalData.userDataHelper.getUser().getId();
        AppHttpServer.getInstance().post(HCHttpRequestParam.getSelfShareStat(checker_id), this, 0);
        /**
         * 获取前三名
         */
        AppHttpServer.getInstance().post(HCHttpRequestParam.getShareStat(), this, 0);
    }

    private void showQRCode() {
        if (isCanUse()) {
            ImageLoaderHelper.displayImage("file://" + myQRCodePath, iv_qrcode_app_download);
            ImageLoaderHelper.displayImage("file://" + myQRCodePath, iv_qrcode_introduction);
        } else {
            User user = GlobalData.userDataHelper.getUser();
            String url = GlobalData.resourceHelper.getString(R.string.myqrcode) + user.getId();
            try {
                Bitmap bitmap = CodeCreatorUtil.createQRCode(url);
                iv_qrcode_app_download.setImageBitmap(bitmap);
                iv_qrcode_introduction.setImageBitmap(bitmap);
                BitmapUtil.saveBitmapToFile(bitmap, myQRCodePath);
                SharedPreferencesUtils.saveBoolean("hc_share_qrcode", true);
            } catch (Exception e) {
                iv_qrcode_app_download.setVisibility(View.GONE);
                iv_qrcode_introduction.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    private boolean isCanUse() {
        return SharedPreferencesUtils.getBoolean("hc_share_qrcode", false) && new File(myQRCodePath).exists();
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        super.onHttpComplete(action, requestId, response, error);
    }


}
