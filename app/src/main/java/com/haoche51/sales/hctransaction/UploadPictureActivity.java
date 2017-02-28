package com.haoche51.sales.hctransaction;


import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.hctransfer.TransferPhotoViewAdapter;
import com.haoche51.sales.constants.TaskConstants;
import com.haoche51.sales.listener.HCTransactionSuccessWatched;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.PopupWindowUtil;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.TransactionQiNiuUploadUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;

/**
 * Created by mingzheng on 2015/12/28.
 */
public class UploadPictureActivity extends CommonBaseActivity implements TransferPhotoViewAdapter.OnPhotoListChangedListener {
    @ViewInject(R.id.tv_upload_transfer_picture)
    private TextView tv_upload_transfer_picture;//点击上传
    @ViewInject(R.id.iv_append_photo)
    private ImageView iv_append_photo;//点击添加图片
    @ViewInject(R.id.ll_append_photo)
    private LinearLayout ll_append_photo;
    public RecyclerView recycler;
    public TransferPhotoViewAdapter mAdapter;
    //  public RecyclerItemClickListener itemClickListener;
    public List<String> photos = new ArrayList<String>();
    private TransactionTaskEntity taskEntity;

    protected void initView() {
        setContentView(R.layout.activity_transaction_upload_picture);
        ViewUtils.inject(this);
        setScreenTitle(R.string.hc_loading_photo);
        registerTitleBack();
        this.recycler = (RecyclerView) findViewById(R.id.recycler_upload);
        initRecyclerView();

        taskEntity = (TransactionTaskEntity) getIntent().getSerializableExtra("mTaskEntity");

    }

    /**
     * 初始化recycler view
     */
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(UploadPictureActivity.this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recycler.setLayoutManager(layoutManager);
        this.recycler.setHasFixedSize(true);
//    this.itemClickListener = new RecyclerItemClickListener(UploadPictureActivity.this, this);
        mAdapter = new TransferPhotoViewAdapter(UploadPictureActivity.this, photos, this);
        recycler.setAdapter(mAdapter);
//    this.recycler.addOnItemTouchListener(this.itemClickListener);
        recycler.setVisibility(View.GONE);
    }

    /**
     * 添加照片按钮点击
     *
     * @param v
     */
    @OnClick(R.id.iv_append_photo)
    private void topopuwindow(View v) {
        PopupWindowUtil.UploadPicturePopup(UploadPictureActivity.this, v);
    }

    /**
     * 提交
     *
     * @param v
     */
    @OnClick(R.id.ll_transaction_upload)
    private void commit(View v) {
        uploadImage();
    }


    private void uploadImage() {
        TransactionQiNiuUploadUtil qiniuUploadImageUtil = new TransactionQiNiuUploadUtil(this, this.photos);
        qiniuUploadImageUtil.startUpload(new TransactionQiNiuUploadUtil.QiniuUploadListener() {
            @Override
            public void onSuccess(List<String> keys) {
                //上传完成，调用取消任务
                taskEntity.setPrepay_transfer_pic((ArrayList<String>) keys);
                taskEntity.setContract_pic(null);
                AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferMessage(taskEntity), UploadPictureActivity.this, 0);
            }

            @Override
            public void onFailed() {
                //取消上传，重新上传
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == TaskConstants.PREPAY_SELECT_PHOTO) { // 选择照片界面
            if (data != null) {
                List<String> temps = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                photos.addAll(temps);
            }
        }
        if (requestCode == TaskConstants.PREPAY_TAKE_PHOTO) { //系统相机拍摄
            if (PopupWindowUtil.photo_path != null) {
                photos.add(PopupWindowUtil.photo_path);
            }
        }
        recycler.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
        ll_append_photo.setVisibility(View.GONE);
    }


    @Override
    public void onSelectPhoto(View view) {
        PopupWindowUtil.UploadPicturePopup(UploadPictureActivity.this, view);
    }

    @Override
    public void onViewPhoto(int position) {

    }

    @Override
    public void onDeletePhoto(int position, String tag) {
        photos.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        super.onHttpComplete(action, requestId, response, error);
        ProgressDialogUtil.closeProgressDialog();
        switch (response.getErrno()) {
            case 0:
                ProgressDialogUtil.closeProgressDialog();
                ToastUtil.showInfo(getString(R.string.hc_modify_succ));
                //成功
                HCTransactionSuccessWatched.getInstance().notifyWatchers(null);
                setResult(RESULT_OK);
                finish();
                break;
            default:
                ToastUtil.showInfo(response.getErrmsg());
                break;
        }
    }
}