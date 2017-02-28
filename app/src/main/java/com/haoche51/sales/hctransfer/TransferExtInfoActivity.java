package com.haoche51.sales.hctransfer;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.entity.ExtInfo;
import com.haoche51.sales.net.HCJsonParse;
import com.haoche51.sales.util.ToastUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 展示附加信息页面（地销提交过户的时候填写的附加信息）
 * Created by yangming on 2016/1/21.
 */
public class TransferExtInfoActivity extends CommonStateActivity {

    /**
     * 本市户口
     */
    @ViewInject(R.id.tv_transfer_ext_resident)
    private TextView tv_transfer_ext_resident;

    /**
     * 占住证
     */
    @ViewInject(R.id.tv_transfer_ext_temp_resident)
    private TextView tv_transfer_ext_temp_resident;

    /**
     * 车主本人是否到场
     */
    @ViewInject(R.id.tv_transfer_ext_seller_present)
    private TextView tv_transfer_ext_seller_present;

    /**
     * 买家本人是否到场
     */
    @ViewInject(R.id.tv_transfer_ext_buyer_present)
    private TextView tv_transfer_ext_buyer_present;

    /**
     * 买家是否外迁
     */
    @ViewInject(R.id.tv_transfer_ext_emigrate)
    private TextView tv_transfer_ext_emigrate;

    /**
     * 车辆是否公户
     */
    @ViewInject(R.id.tv_transfer_ext_company_seller)
    private TextView tv_transfer_ext_company_seller;

    /**
     * 买家是否公户
     */
    @ViewInject(R.id.tv_transfer_ext_company_buyer)
    private TextView tv_transfer_ext_company_buyer;

    /**
     * 贷款
     */
    @ViewInject(R.id.tv_transfer_ext_loan)
    private TextView tv_transfer_ext_loan;

    public static final String KEY_INTENT_EXTRA_EXTINFO = "extinfo";
    private ExtInfo extInfo;

    @Override
    protected int getContentView() {
        return R.layout.activity_transfer_extinfo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        setScreenTitle("更多信息");
        String ext = getIntent().getStringExtra(KEY_INTENT_EXTRA_EXTINFO);
        extInfo = HCJsonParse.parseExtInfo(ext);
    }

    @Override
    protected void initData() {
        super.initData();
        if (extInfo == null) {
            ToastUtil.showText(getString(R.string.parameters_error));
            finish();
            return;
        }

        if (!TextUtils.isEmpty(String.valueOf(extInfo.getResident()))) {
            tv_transfer_ext_resident.setText(extInfo.getResident() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getEmigrate()))) {
            tv_transfer_ext_emigrate.setText(extInfo.getEmigrate() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getBuyer_present()))) {
            tv_transfer_ext_buyer_present.setText(extInfo.getBuyer_present() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_buyer()))) {
            tv_transfer_ext_company_buyer.setText(extInfo.getCompany_buyer() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getCompany_seller()))) {
            tv_transfer_ext_company_seller.setText(extInfo.getCompany_seller() == 1 ? "是" : "否");
        }

        if (!TextUtils.isEmpty(String.valueOf(extInfo.getSeller_present()))) {
            tv_transfer_ext_seller_present.setText(extInfo.getSeller_present() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getTemp_resident()))) {
            tv_transfer_ext_temp_resident.setText(extInfo.getTemp_resident() == 1 ? "是" : "否");
        }
        if (!TextUtils.isEmpty(String.valueOf(extInfo.getLoan()))) {
            tv_transfer_ext_loan.setText(extInfo.getLoan() == 1 ? "是" : "否");
        }
    }
}
