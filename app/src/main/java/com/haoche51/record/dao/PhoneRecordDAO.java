package com.haoche51.record.dao;

import android.content.ContentValues;
import android.database.Cursor;


import com.haoche51.record.entity.BaseEntity;
import com.haoche51.record.entity.PhoneRecordEntity;

import java.util.List;

public class PhoneRecordDAO extends BaseDAO {
	private static PhoneRecordDAO dao = new PhoneRecordDAO();

	public static PhoneRecordDAO getInstance() {
		return dao;
	}

	public static final String TABLE_NAME = "phone_record";
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "saler_phone text not null default ''," // 本机号码
			+ "customer_phone text not null default ''," // 客户号码
			+ "create_time text not null default 0," // 来电或者去电时间 用unixtime 存储
			+ "call_duration text not null default '',"// 通话时间
			+ "call_type integer not null default 0," // 电话类型。0 呼入 1 呼出
			+ "employee_number integer not null default 0" // 销售员工id
			+ ");";

	private static final String[] COLUMNS = { "id", "saler_phone", "customer_phone", "create_time", "call_duration", "call_type", "employee_number" };
	private static final String DEFAULT_ORDER_BY = "create_time desc";

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
	protected ContentValues getContentValues(BaseEntity entity) {
		// VehicleBrandEntity vehicleBrand = (VehicleBrandEntity) entity;
		PhoneRecordEntity record = (PhoneRecordEntity) entity;
		ContentValues mValues = new ContentValues();
		// mValues.put(COLUMNS[0], record.getId());
		mValues.put(COLUMNS[1], record.getSaler_phone());
		mValues.put(COLUMNS[2], record.getCustomer_phone());
		mValues.put(COLUMNS[3], record.getCreate_time());
		mValues.put(COLUMNS[4], record.getCall_duration());
		mValues.put(COLUMNS[5], record.getCall_type());
		mValues.put(COLUMNS[6], record.getEmployee_number());
		return mValues;
	}

	@Override
	protected BaseEntity getEntityFromCursor(Cursor mCursor) {
		PhoneRecordEntity record = new PhoneRecordEntity();
		record.setId(mCursor.getInt(0));
		record.setSaler_phone(mCursor.getString(1));
		record.setCustomer_phone(mCursor.getString(2));
		record.setCreate_time(mCursor.getLong(3));
		record.setCall_duration(mCursor.getInt(4));
		record.setCall_type(mCursor.getInt(5));
		record.setEmployee_number(mCursor.getInt(6));
		return record;
	}

	@Override
	public PhoneRecordEntity get(int id) {
		return (PhoneRecordEntity) super.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PhoneRecordEntity> get(int offset, int limit) {
		return (List<PhoneRecordEntity>) super.get(offset, limit);
	}

	@SuppressWarnings("unchecked")
	public List<PhoneRecordEntity> get(String where) {
		return (List<PhoneRecordEntity>) super.get(where);
	}

	/***
	 * 获取最新的一条录音记录
	 * @return
	 */
	public PhoneRecordEntity getLastRecord() {
		PhoneRecordEntity result = null;
		StringBuilder sb = new StringBuilder(30);
		sb.append("select * from ").append(getTableName()).append(" where id=(select MAX(id) from ").append(getTableName() +")");
		android.util.Log.d("sqliteth", sb.toString());
		Cursor cursor = mDb.rawQuery(sb.toString(), null);
		android.util.Log.d("sqliteth", cursor.getCount()+"");
		if(cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			result = (PhoneRecordEntity) getEntityFromCursor(cursor);
			cursor.close();
		}
		return result;
	}
}
