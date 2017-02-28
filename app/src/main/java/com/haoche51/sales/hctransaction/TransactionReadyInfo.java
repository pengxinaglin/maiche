package com.haoche51.sales.hctransaction;

import com.haoche51.sales.entity.BaseEntity;

import java.io.Serializable;

/**
 * 地销监控记录实体
 * Created by yangming on 2016/2/23.
 */
public class TransactionReadyInfo extends BaseEntity implements Serializable {
    /**
     * id : 185
     * name : 杨明
     */

    private UserInfoEntity user_info;
    /**
     * type : 1
     * network_env : 1
     * operate_time : 1456207883069
     */

    private OperationEntity operation;
    /**
     * user_info : {"id":185,"name":"杨明"}
     * operation : {"type":1,"network_env":1,"operate_time":1456207883069}
     * transaction_id : 1940
     */

    private int transaction_id;

    private int id;
    private String upload_status;//状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpload_status() {
        return upload_status;
    }

    public void setUpload_status(String upload_status) {
        this.upload_status = upload_status;
    }

    public void setUser_info(UserInfoEntity user_info) {
        this.user_info = user_info;
    }

    public void setOperation(OperationEntity operation) {
        this.operation = operation;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public UserInfoEntity getUser_info() {
        return user_info;
    }

    public OperationEntity getOperation() {
        return operation;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public static class UserInfoEntity {
        private int id;
        private String name;

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class OperationEntity {
        private int type;
        private int network_env;
        private long operate_time;

        public void setType(int type) {
            this.type = type;
        }

        public void setNetwork_env(int network_env) {
            this.network_env = network_env;
        }

        public void setOperate_time(long operate_time) {
            this.operate_time = operate_time;
        }

        public int getType() {
            return type;
        }

        public int getNetwork_env() {
            return network_env;
        }

        public long getOperate_time() {
            return operate_time;
        }
    }

//  private int id;
//  private int task_id;
//  private int type;
//  private int network_env;
//  private long operate_time;
//  private String upload_status;
//
//  public int getId() {
//    return id;
//  }
//
//  public void setId(int id) {
//    this.id = id;
//  }
//
//  public int getTask_id() {
//    return task_id;
//  }
//
//  public void setTask_id(int task_id) {
//    this.task_id = task_id;
//  }
//
//  public int getType() {
//    return type;
//  }
//
//  public void setType(int type) {
//    this.type = type;
//  }
//
//  public int getNetwork_env() {
//    return network_env;
//  }
//
//  public void setNetwork_env(int network_env) {
//    this.network_env = network_env;
//  }
//
//  public long getOperate_time() {
//    return operate_time;
//  }
//
//  public void setOperate_time(long operate_time) {
//    this.operate_time = operate_time;
//  }
//
//  public String getUpload_status() {
//    return upload_status;
//  }
//
//  public void setUpload_status(String upload_status) {
//    this.upload_status = upload_status;
//  }


}
