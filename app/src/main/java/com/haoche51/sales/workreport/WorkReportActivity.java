package com.haoche51.sales.workreport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkReportActivity extends CommonStateFragmentActivity {

    public static final int REQUEST_CODE_CAMERA = 1;
    public static final int REQUEST_CODE_SEND_WORK_REPORT = 2;

    // 拍照按钮
    @ViewInject(R.id.right_image_1)
    private ImageView right_image_1;

    private WorkReportFragment mWorkReportFragment;
    public Uri mImageUri;

    @Override
    protected int getTitleView() {
        return R.layout.title_bar;
    }

    @Override
    protected int getContentView() {
        return R.layout.workreport_activity_main;
    }

    /**
     * 初始化界面
     */
    @Override
    protected void initView() {
        super.initView();
        ViewUtils.inject(this);
        registerTitleBack();
        setScreenTitle(R.string.hc_right_work_report);
        // 设置拍照按钮
        right_image_1.setImageResource(R.drawable.work_report_photo);
        right_image_1.setVisibility(View.VISIBLE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mWorkReportFragment = new WorkReportFragment();
        transaction.add(R.id.work_report_container, mWorkReportFragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 点击拍照
     */
    @OnClick(R.id.right_image_1)
    private void onAddClick(View view) {
        try {
            // 调用系统相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
            File imageDir = new File(path);
            // 创建图片保存目录
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            // 根据时间来命名
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            File imagFile = File.createTempFile("" + time.format(new Date()), ".jpg", imageDir);
            mImageUri = Uri.fromFile(imagFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAMERA) {
            // 拍照返回
            if (resultCode == Activity.RESULT_OK) {
                if (mImageUri != null) {
                    String path = mImageUri.getPath();
                    Intent intent = new Intent(WorkReportActivity.this, FullscreenImageActivity.class);
                    intent.putExtra(FullscreenImageActivity.KEY_NOT_SHOW_RIGHT_TITLE, true);
                    intent.putExtra(FullscreenImageActivity.KEY_IMAGE_FILE_PATH, path);
                    startActivityForResult(intent, REQUEST_CODE_SEND_WORK_REPORT);
                }
            }
        } else if (requestCode == REQUEST_CODE_SEND_WORK_REPORT) {
            // 发送工作汇报返回
            if (mWorkReportFragment != null) {
                mWorkReportFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
