package com.haoche51.sales.activity.user;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.haoche51.sales.GlobalData;
import com.haoche51.sales.R;
import com.haoche51.sales.hcbaseactivity.CommonBaseActivity;
import com.haoche51.sales.constants.UserConstans;
import com.haoche51.sales.net.AppHttpServer;
import com.haoche51.sales.net.HCHttpRequestParam;
import com.haoche51.sales.net.HCHttpResponse;
import com.haoche51.sales.util.ProgressDialogUtil;
import com.haoche51.sales.util.ToastUtil;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.umeng.analytics.MobclickAgent;

public class ChangePwdActivity extends CommonBaseActivity {
  @Required(order = 1, message = "请输入原始密码")
  private EditText originPwdEditText;
  @Password(order = 2, message = "请输入新密码")
  private EditText newPwdEditText;
  @Required(order = 3, message = "确认密码不能为空")
  @ConfirmPassword(order = 4, message = "确认密码与新密码不一致，请重新输入")
  private EditText againNewPwdEditText;
  private Button okBtn;

  @Override
  protected void initView() {
    super.initView();
    setContentView(R.layout.activity_change_pwd);
    registerTitleBack();
    setScreenTitle(R.string.change_pwd);
    originPwdEditText = (EditText) findViewById(R.id.origin_pwd);
    newPwdEditText = (EditText) findViewById(R.id.new_pwd);
    againNewPwdEditText = (EditText) findViewById(R.id.new_pwd_again);
    okBtn = (Button) findViewById(R.id.btn_ok);
  }

  @Override
  protected void initData() {
    super.initData();
    initClick();
  }


  private void initClick() {
    okBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        validator.validate();
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onValidationSucceeded() {
    final String originPwd = originPwdEditText.getText().toString();
    final String newPwdAgagin = againNewPwdEditText.getText().toString();
    if (!originPwd.equals(GlobalData.userDataHelper.getLoginPwd())) {
      showErrorMsg(originPwdEditText, getString(R.string.old_pwd_error));
      return;
    }
    ProgressDialogUtil.showProgressDialog(ChangePwdActivity.this, null);
    AppHttpServer.getInstance().post(HCHttpRequestParam.changePwd(originPwd, newPwdAgagin), this, 0);

    super.onValidationSucceeded();
  }

  @Override
  public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
    super.onHttpComplete(action, requestId, response, error);
    final String newPwdAgagin = againNewPwdEditText.getText().toString();
    ProgressDialogUtil.closeProgressDialog();
    switch (response.getErrno()) {
      case 0:
        GlobalData.userDataHelper.setLoginPwd(newPwdAgagin)
          .commit();
        ToastUtil.showInfo(getString(R.string.hc_modify_succ));
        finish();
        break;
      case UserConstans.OLD_PWD_ERROR:
        showErrorMsg(originPwdEditText, response.getErrmsg());
        break;
      case UserConstans.PWD_FROMAT_ERROR:
      case UserConstans.NEWPWD_EQ_OLDPWD:
        showErrorMsg(newPwdEditText, response.getErrmsg());
        break;
      default:
        ToastUtil.showInfo(response.getErrmsg());
        break;
    }

  }

  @Override
  protected void onResume() {
    super.onResume();
    MobclickAgent.onPageStart("ChangePwdPage");
    MobclickAgent.onResume(getApplicationContext());
  }

  @Override
  protected void onPause() {
    super.onPause();
    MobclickAgent.onPageEnd("ChangePwdPage");
    MobclickAgent.onPause(getApplicationContext());
  }

}
