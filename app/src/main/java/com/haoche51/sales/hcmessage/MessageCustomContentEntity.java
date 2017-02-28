package com.haoche51.sales.hcmessage;

/**
 * Created by xuhaibo on 15/9/14.
 */
public class MessageCustomContentEntity {
  private int task_id;
  private int msg_type = -1; // 11 审批成功   12 审批失败
  private String phone;

  public void setTask_id(int task_id) {
    this.task_id = task_id;
  }

  public int getTask_id() {

    return task_id;
  }

  public int getMsg_type() {
    return msg_type;
  }

  public void setMsg_type(int msg_type) {
    this.msg_type = msg_type;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}
