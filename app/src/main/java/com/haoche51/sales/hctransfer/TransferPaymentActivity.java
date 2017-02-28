package com.haoche51.sales.hctransfer;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.util.DisplayUtils;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangming on 2016/1/22.
 */
public class TransferPaymentActivity extends CommonStateActivity {

    /**
     * 剩余服务费
     */
    @ViewInject(R.id.tv_transfer_start_remaind_service)
    private TextView tv_transfer_start_remaind_service;

    /**
     * 剩余车价
     */
    @ViewInject(R.id.tv_transfer_start_remaind_price)
    private TextView tv_transfer_start_remaind_price;

    /**
     * 复检后车辆成交价
     */
    @ViewInject(R.id.tv_transfer_payment_price)
    private TextView tv_transfer_payment_price;

    /**
     * 复检减免服务费
     */
    @ViewInject(R.id.tv_transferpayment_reduce)
    private TextView tv_transferpayment_reduce;

    /**
     * 买家通过公司转账
     */
    @ViewInject(R.id.tv_transfer_payment_comtrans)
    private TextView tv_transfer_payment_comtrans;

    /**
     * 买家转给公司金额
     */
    @ViewInject(R.id.ll_transfer_payment_comtrans_price)
    private LinearLayout ll_transfer_payment_comtrans_price;
    @ViewInject(R.id.tv_transfer_payment_comtrans_price)
    private TextView tv_transfer_payment_comtrans_price;

    /**
     * 过户费用
     */
    @ViewInject(R.id.ll_transfer_payment_fee)
    private LinearLayout ll_transfer_payment_fee;
    @ViewInject(R.id.tv_transfer_payment_fee)
    private TextView tv_transfer_payment_fee;

    @ViewInject(R.id.et_transfer_start_comment)
    private TextView et_transfer_start_comment;


    /**
     * 合同及转账照片
     */
    @ViewInject(R.id.recyclerview_contract)
    private RecyclerView recyclerview_contract;

    private TransferEntity entity;

    List<String> transferPhotoPath = new ArrayList<>();//全部照片的list
    private TransferPhotoViewAdapter transferPhotoViewAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_transfer_payment;
    }

    @Override
    protected void initView() {
        super.initView();
        setScreenTitle(R.string.hc_transfer_payment_title);
        entity = (TransferEntity) getIntent().getSerializableExtra("transferEntity");
        if (entity == null) {
            ToastUtil.showText(getString(R.string.parameters_error));
            finish();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        tv_transfer_start_remaind_service.setText(entity.getRest_commisson() + "元");
        tv_transfer_start_remaind_price.setText(entity.getRest_vehicle_price() + "元");
        tv_transfer_payment_price.setText(entity.getTransfer_price() + "元");
        tv_transferpayment_reduce.setText(entity.getTransfer_remission() + "元");

        //买家转给公司金额
        if ("1".equals(entity.getTransfer_by_company())) {
            tv_transfer_payment_comtrans.setText("是");
        } else {
            tv_transfer_payment_comtrans.setText("否");
        }
        tv_transfer_payment_comtrans_price.setText(entity.getFinal_payment() + "元");

        //过户费用
        if ("0".equals(entity.getTransfer_free())) {//否
            ll_transfer_payment_fee.setVisibility(View.GONE);
        } else {//包过户
            ll_transfer_payment_fee.setVisibility(View.VISIBLE);
            tv_transfer_payment_fee.setText(entity.getTransfer_amount() + "元");
        }

        //备注
        et_transfer_start_comment.setText(entity.getTransfer_comment());

        // 照片
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_contract.setLayoutManager(layoutManager);
        recyclerview_contract.setHasFixedSize(true);

        transferPhotoPath = entity.getReceipt_pic();

        // 照片
        TransferPhotoViewAdapter photoViewAdapter = new TransferPhotoViewAdapter(TransferPaymentActivity.this, transferPhotoPath, null);
        photoViewAdapter.setView(true);
        recyclerview_contract.setAdapter(photoViewAdapter);
        DisplayUtils.reSizeRecyclerView(this, recyclerview_contract, transferPhotoPath, true);

    }

}
