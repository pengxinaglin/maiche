package com.haoche51.sales.workreport;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.HCApplication;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.haoche51.sales.dialog.AlertDialog;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ImageUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.TransactionQiNiuUploadUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WorkReportSendActivity extends CommonStateFragmentActivity {

    @ViewInject(R.id.right_text_view)
    private TextView right_text_view;

    @ViewInject(R.id.input_text)
    private EditText input_text;

    @ViewInject(R.id.photo_image)
    private ImageView photo_image;

    @ViewInject(R.id.place_text)
    private TextView place_text;

    private String mImagePath;
    public List<String> mPhotoPaths = new ArrayList<String>();
    private double mLatitude;
    private double mLongitude;
    private String mAddress;

    @Override
    protected int getTitleView() {
        return R.layout.title_bar;
    }

    @Override
    protected int getContentView() {
        return R.layout.workreport_activity_send;
    }

    @Override
    protected void initView() {
        super.initView();
        ViewUtils.inject(this);
        registerTitleBack();
        setScreenTitle("");

        right_text_view.setText(getString(R.string.publish));
        right_text_view.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        super.initData();
        startLocation();

        mImagePath = getIntent().getStringExtra(FullscreenImageActivity.KEY_IMAGE_FILE_PATH);
        mPhotoPaths.add(mImagePath);
        Bitmap bitmap = ImageUtil.getBitmap(mImagePath);
        bitmap = ImageUtil.extractThumbnail(bitmap, DisplayUtils.dip2px(this, 80), DisplayUtils.dip2px(this, 80));
        photo_image.setImageBitmap(bitmap);
    }

    /**
     * 启动定位
     */
    private void startLocation() {
        LocationClient locationClient = HCApplication.mLocationClient;
        if (locationClient == null) {
            locationClient = new LocationClient(HCApplication.getContext());
        }

        LocationClientOption option = new LocationClientOption();
        option.setProdName(GlobalData.mContext.getPackageName());
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度
        option.setScanSpan(100); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认为无地址
        locationClient.setLocOption(option);
        HaocheLocationListener locationListener = new HaocheLocationListener();
        locationClient.registerLocationListener(locationListener);
        locationClient.requestLocation();
        locationClient.start();
    }

    /**
     * 实现实位回调监听
     */
    public class HaocheLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (isFinishing()) {
                return;
            }

            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mAddress = location.getAddrStr();
            if (!TextUtils.isEmpty(mAddress)) {
                place_text.setText(mAddress);
            }
        }
    }

    /**
     * 点击图片
     */
    @OnClick(R.id.photo_image)
    private void onClickImage(View view) {
        Intent intent = new Intent(WorkReportSendActivity.this, FullscreenImageActivity.class);
        intent.putExtra(FullscreenImageActivity.KEY_IMAGE_FILE_PATH, mImagePath);
        startActivity(intent);
    }

    /**
     * 点击发表
     */
    @OnClick(R.id.right_text_view)
    private void onClickSend(View view) {
        if (TextUtils.isEmpty(input_text.getText())) {
            ToastUtil.showInfo(getString(R.string.content_not_null));
            return;
        }

        if (TextUtils.isEmpty(mAddress)) {
            ToastUtil.showInfo(getString(R.string.locationing));
            return;
        }

        uploadImage();
    }

    /**
     * 上传图片完成后发表工作汇报
     */
    private void uploadImage() {
        TransactionQiNiuUploadUtil uploadUtil = new TransactionQiNiuUploadUtil(this, mPhotoPaths);
        uploadUtil.startNewUpload(new TransactionQiNiuUploadUtil.QiniuUploadListener() {
            @Override
            public void onSuccess(List<String> keys) {
                if (isFinishing()) {
                    return;
                }
                // 上传完成，调用取消任务
                if (keys == null || keys.size() == 0) {
                    ToastUtil.showInfo(getString(R.string.image_upload_fail));
                }

                String imageUrl = keys.get(0);
                ProgressDialogUtil.showProgressDialog(WorkReportSendActivity.this, getString(R.string.sending));
                long timeMillis = System.currentTimeMillis();
                long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
                Map<String, Object> params = HCHttpRequestParam.addReport(timeSeconds, input_text.getText().toString(),
                        mAddress, "" + mLongitude, "" + mLatitude, imageUrl);
                AppHttpServer.getInstance().post(params, WorkReportSendActivity.this, HttpConstants.GET_LIST_REFRESH);
            }

            @Override
            public void onFailed() {
                if (isFinishing()) {
                    return;
                }
                // 取消上传，重新上传
                ToastUtil.showInfo(getString(R.string.image_upload_fail));
            }
        });
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }
        ProgressDialogUtil.closeProgressDialog();
        switch (response.getErrno()) {
            case 0:
                setResult(Activity.RESULT_OK);
                finish();
                break;
            default:
                ToastUtil.showInfo(response.getErrmsg());
                break;
        }
    }

    /**
     * 注册back功能
     */
    protected void registerTitleBack() {
        View backBtn = findViewById(R.id.tv_common_back);
        if (backBtn != null) {
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showExitDialog();
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return true;
        }
        return false;
    }

    /**
     * 显示退出Dialog
     */
    private void showExitDialog() {
        AlertDialog.showStandardTitleMessageDialog(WorkReportSendActivity.this,
                getString(R.string.tips), getString(R.string.exit_edit), getString(R.string.soft_update_cancel), getString(R.string.exit),
                new AlertDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Bundle data) {
                        finish();
                    }
                });
    }
}
