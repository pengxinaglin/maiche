package com.haoche51.sales.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.haoche51.sales.entity.BaseEntity;
import com.haoche51.sales.hctransaction.TransactionReadyInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 地销操作监控待上传实体类
 */
public class TransactionReadyInfoDAO extends BaseDAO {
  private Gson mGson = new Gson();
  public static final String TABLE_NAME = "transaction_ready_info";
  public static final String CREATE_TABLE = "create table " + TABLE_NAME
    + "(" + "id integer primary key autoincrement,"
    + "task_id integer  not null,"
    + "type integer  not null,"
    + "network_env integer not null default '',"
    + "operate_time long not null default '',"
    + "upload_status text not null default '')";
  private static final String[] COLUMNS = {
    "id",
    "task_id",
    "type",
    "network_env",
    "operate_time",
    "upload_status"
  };
  private static final String DEFAULT_ORDER_BY = "operate_time asc";
  private static TransactionReadyInfoDAO dao = new TransactionReadyInfoDAO();
  private Type type;
  //  private List<PhotoEntity> photoEntities;
  private ArrayList<TransactionReadyInfo> transactionReadyInfos;

  private TransactionReadyInfoDAO() {
  }

  public static TransactionReadyInfoDAO getInstance() {
    return dao;
  }

  @Override
  protected ContentValues getContentValues(BaseEntity entity) {
    TransactionReadyInfo readyInfo = (TransactionReadyInfo) entity;
    ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMNS[0], uploadCheck.getId());
    contentValues.put(COLUMNS[1], readyInfo.getTransaction_id());
    contentValues.put(COLUMNS[2], readyInfo.getOperation().getType());
    contentValues.put(COLUMNS[3], readyInfo.getOperation().getNetwork_env());
    contentValues.put(COLUMNS[4], readyInfo.getOperation().getOperate_time());
    contentValues.put(COLUMNS[5], readyInfo.getUpload_status());

    return contentValues;
  }

  @Override
  protected BaseEntity getEntityFromCursor(Cursor mCursor) {
    TransactionReadyInfo readyInfo = new TransactionReadyInfo();
    readyInfo.setId(mCursor.getInt(0));
    readyInfo.setTransaction_id(mCursor.getInt(1));

    TransactionReadyInfo.OperationEntity operationEntity = new TransactionReadyInfo.OperationEntity();
    operationEntity.setType(mCursor.getInt(2));
    operationEntity.setNetwork_env(mCursor.getInt(3));
    operationEntity.setOperate_time(mCursor.getInt(4));
    readyInfo.setOperation(operationEntity);

    readyInfo.setUpload_status(mCursor.getString(5));

    return readyInfo;
  }

  @Override
  protected String getTableName() {
    return TABLE_NAME;
  }

  @Override
  protected String[] getColumns() {
    return COLUMNS;
  }

  @Override
  protected String getDefaultOrderby() {
    return DEFAULT_ORDER_BY;
  }

  @Override
  public TransactionReadyInfo get(int id) {
    return (TransactionReadyInfo) super.get(id);
  }


  /**
   * 根据条件查询
   *
   * @param where 查询条件：null表示查询全部
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<TransactionReadyInfo> get(String where) {
    return (List<TransactionReadyInfo>) super.get(where);
  }


  /**
   * 根据任务ID更新
   *
   * @param entity
   * @return
   */
  public int updateByTaskId(TransactionReadyInfo entity) {
    if (mDb == null) return 0;
    ContentValues mValues = getContentValues(entity);
    int ret = update(mValues, "task_id=" + entity.getTransaction_id());
    return ret;
  }

  /**
   * 根据ID删除
   *
   * @param taskId 任务ID
   * @return
   */
  public void deleteByTaskId(int taskId) {
    mDb.execSQL("delete from transaction_ready_info where id=" + taskId);
  }
}
