package com.haoche51.settlement.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.haoche51.settlement.R;

public class AlertDialog {

    //确定Listener
    public interface OnClickYesListener {
        void onClickYes();
    }

    /**
     * 显示确认手机号码Dialog
     */
    public static void showConfirmPhoneDialog(final Activity context, String content, String phone,
                                              String leftButtonText, String rightButtonText, final OnDismissListener onDismissListener) {
        if (context == null || context.isFinishing()) {
            return;
        }

        final Dialog dialog = new Dialog(context, R.style.pay_shareDialog);
        View view = View.inflate(context, R.layout.pay_dialog_confirm_phone, null);
        // 内容
        TextView contentView = (TextView) view.findViewById(R.id.content);
        contentView.setText(content);
        // 电话
        TextView phoneView = (TextView) view.findViewById(R.id.phone);
        phoneView.setText(phone);
        // 左按钮
        Button leftButton = (Button) view.findViewById(R.id.left_button);
        leftButton.setText(leftButtonText);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // 右按钮
        Button rightButton = (Button) view.findViewById(R.id.right_button);
        rightButton.setText(rightButtonText);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(null);
                }
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        setDialogSize(context, dialog, (float) 0.85, 0);
        dialog.show();
    }


    public static void createNormalDialog(final Activity context, final String content, final OnDismissListener onDismissListener) {
        if (context == null || context.isFinishing()) return;
        final Dialog dialog = new Dialog(context, R.style.pay_shareDialog);
        View root = View.inflate(context, R.layout.pay_vcode_send_dialog, null);
        ((TextView) root.findViewById(R.id.tv_content)).setText(content);
        /** 取消 ，确定离开 */
        Button cancle = (Button) root.findViewById(R.id.cancel);
//        cancle.setText(cancelContent);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle data = new Bundle();
                data.putBoolean("determine", false);
                onDismissListener.onDismiss(data);

                dialog.dismiss();
            }
        });

        /** 确定 ，继续收款 */
        Button determine = (Button) root.findViewById(R.id.determine);
//        determine.setText(okContent);
        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDismissListener != null) {
                    Bundle data = new Bundle();
                    data.putBoolean("determine", true);
                    onDismissListener.onDismiss(data);
                }
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        setDialogSize(context, dialog, (float) 0.85, 0);
        dialog.show();
    }


    public static void createRuleDialog(final Activity context) {
        if (context == null || context.isFinishing()) return;
        final Dialog dialog = new Dialog(context, R.style.pay_shareDialog);
        View root = View.inflate(context, R.layout.pay_balance_rule_dialog, null);
        /** 取消 ，确定离开 */
        Button cancle = (Button) root.findViewById(R.id.cancel);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(root);
        setDialogSize(context, dialog, (float) 0.85, 0);
        dialog.show();
    }


    public static void setDialogSize(Activity context, Dialog dialog, float scaleW, float scaleH) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (scaleW > 0)
            lp.width = (int) (context.getWindowManager().getDefaultDisplay().getWidth() * scaleW);
        else
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;

        if (scaleH > 0)
            lp.height = (int) (context.getWindowManager().getDefaultDisplay().getHeight() * scaleH);
        else
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private static void setDialogBackground(Dialog dialog, int res) {
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(res);
    }

    //确定Listener
    public interface OnDismissListener {
        void onDismiss(Bundle data);
    }


    public interface OnDialogConfirmListener {
        void isClick(boolean confirm);
    }

    /**
     * 现金支付确认
     *
     * @param context                 context
     * @param price                   price
     * @param onDialogConfirmListener 回调
     */
    public static void pospayCahsConfirmDialog(Context context, String price, final OnDialogConfirmListener onDialogConfirmListener) {

        LayoutInflater flater = LayoutInflater.from(context);
        final View view = flater.inflate(R.layout.pay_detail_cash_confirm_dialog, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        final android.app.AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失

        TextView textViewTitle = (TextView) view.findViewById(R.id.tv_pospay_cash_confirm_price);
        String source = "";
        source = "请确保可当场收取<font color=\"#ff3333\">"
                + price + "元</font>现金！";

        textViewTitle.setText(Html.fromHtml(source));

        Button buttonUse = (Button) view.findViewById(R.id.btn_pospay_cash_confirm);
        Button buttonUnUse = (Button) view.findViewById(R.id.btn_pospay_cash_cancle);

        buttonUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onDialogConfirmListener.isClick(true);
            }
        });
        buttonUnUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                onDialogConfirmListener.isClick(false);
            }
        });

        alertDialog.show();

    }
}
