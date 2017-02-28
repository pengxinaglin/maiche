package com.haoche51.sales.hctransfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonStateActivity;
import com.haoche51.sales.custom.time.ScreenInfo;
import com.haoche51.sales.custom.time.WheelMain;
import com.haoche51.sales.dialog.HCDialog;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.net.HttpConstants;
import com.haoche51.sales.util.ToastUtil;
import com.haoche51.sales.util.UnixTimeUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Calendar;

/**
 * 修改过户时间和过户地点
 * Created by yangming on 2016/1/20.
 */
public class TransferModifyActivity extends CommonStateActivity {

    public static final String KEY_INTENT_EXTRA_ID = "transfer_id";
    public static final String KEY_INTENT_EXTRA_TIME = "time";
    public static final String KEY_INTENT_EXTRA_ADDRESS = "address";

    @ViewInject(R.id.tv_transfer_modify_time)
    private TextView tv_transfer_modify_time;//过户时间

    @ViewInject(R.id.et_transfer_modify_address)
    private EditText et_transfer_modify_address;//过户地点

    private String transferId;
    private int transferTime;
    private String transferAddress;


    @Override
    protected int getContentView() {
        return R.layout.activity_transfer_modify;
    }

    @Override
    protected void initView() {
        super.initView();
        setScreenTitle(R.string.hc_modify_transfer);
        transferId = getIntent().getStringExtra(KEY_INTENT_EXTRA_ID);
        transferTime = getIntent().getIntExtra(KEY_INTENT_EXTRA_TIME, -1);
        transferAddress = getIntent().getStringExtra(KEY_INTENT_EXTRA_ADDRESS);
        if (TextUtils.isEmpty(transferId)) {
            ToastUtil.showText(getString(R.string.parameters_error));
            finish();
        }
        if (transferTime == -1) {
            ToastUtil.showText(getString(R.string.parameters_error));
            finish();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        tv_transfer_modify_time.setText(UnixTimeUtil.formatTransferTime(transferTime));
        et_transfer_modify_address.setText(transferAddress);
    }

    @OnClick(R.id.tv_transfer_modify_time)
    private void onTimeClick(View view) {
        initTimeWhell(TransferModifyActivity.this);
    }

    @OnClick(R.id.tv_transfer_modify_save)
    private void onSaveClick(View view) {
        HCDialog.showProgressDialog(TransferModifyActivity.this);
        AppHttpServer.getInstance().post(HCHttpRequestParam.modifyTransferInfo(transferId, et_transfer_modify_address.getText().toString(), transferTime), this, 0);
    }

    private void initTimeWhell(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransferModifyActivity.this);
        builder.setTitle(getString(R.string.select_transfer_time));
        final View timerView = LayoutInflater.from(TransferModifyActivity.this).inflate(R.layout.timepicker, null);
        ScreenInfo screenInfo = new ScreenInfo(activity);
        final WheelMain wheelMain = new WheelMain(timerView, true);
        wheelMain.screenheight = screenInfo.getHeight();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        wheelMain.initDateTimePicker(year, month, day, hour, min);
        builder.setView(timerView);
        builder.setNegativeButton(getString(R.string.soft_update_cancel), null);
        builder.setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                transferTime = UnixTimeUtil.getUnixTime(wheelMain.getTime());
                if (transferTime < System.currentTimeMillis() / 1000) {
                    ToastUtil.showInfo(getString(R.string.no_choose));
                } else {
                    tv_transfer_modify_time.setText(UnixTimeUtil.format(transferTime));
                }
            }
        });
        builder.show();
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (isFinishing()) {
            return;
        }
        if (action.equals(HttpConstants.ACTION_MODIFY_TRANSFER_INFO)) {
            onModifyTransferInfoResult(response);
        }
    }

    private void onModifyTransferInfoResult(HCHttpResponse response) {
        switch (response.getErrno()) {
            case 0:
                ToastUtil.showText(R.string.hc_modify_succ);
                Intent intent = new Intent();
                intent.putExtra(KEY_INTENT_EXTRA_TIME, transferTime);
                intent.putExtra(KEY_INTENT_EXTRA_ADDRESS, et_transfer_modify_address.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                ToastUtil.showInfo(response.getErrmsg());
                break;
        }
        HCDialog.dismissProgressDialog();
    }

}
