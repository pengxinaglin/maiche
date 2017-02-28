package com.haoche51.sales.hcmessage;

import android.support.v4.app.FragmentTransaction;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateFragmentActivity;

/**
 * Created by yangming on 2015/12/1.
 */
public class MessageMainActivity extends CommonStateFragmentActivity {

    @Override
    protected int getTitleView() {
        return R.layout.layout_common_titlebar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_message_list;
    }

    @Override
    protected void initView() {
        super.initView();
        registerTitleBack();
        setScreenTitle(getString(R.string.hc_message_activity_title));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_activity_message_list_container, new MessageListFragment());
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
