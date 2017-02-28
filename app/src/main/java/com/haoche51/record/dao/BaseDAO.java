package com.haoche51.record.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.haoche51.record.entity.BaseEntity;
import com.haoche51.sales.GlobalData;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {
    private List<DataObserver> mObList = null;
    protected SQLiteDatabase mDb = null;
    
    protected BaseDAO() {
        mDb = GlobalData.dbHelper.getWritableDatabase();
    }

	public void registerDataObserver(DataObserver mDataObserver) {
        if (mObList == null) mObList = new ArrayList<DataObserver>();
        mObList.add(mDataObserver);
    }
    
    protected void invokeDataChanged() {
    	if (mObList == null) return;
    	for (DataObserver observer : mObList) {
			observer.onChanged();
		}
    }
    
    protected abstract String getTableName();
    protected abstract ContentValues getContentValues(BaseEntity entity);
    protected abstract BaseEntity getEntityFromCursor(Cursor mCursor);
    protected abstract String[] getColumns();
    protected abstract String getDefaultOrderby();
    
    public long insert(BaseEntity entity) {
        if (mDb == null) return 0;
        ContentValues mValues = getContentValues(entity);
        long ret = mDb.insert(getTableName(), null, mValues);
        if (ret != -1) invokeDataChanged();
        return ret;
    }
    
    public void insert(List<? extends BaseEntity> entityList) {
    	if (mDb == null) return;
    	mDb.beginTransaction();
    	for (BaseEntity entity : entityList) {
    		insert(entity);
    	}
    	mDb.setTransactionSuccessful();
    	mDb.endTransaction();
    }
    
    public int delete(int id) {
        if (mDb == null) return 0;
        int ret = mDb.delete(getTableName(), "id="+id, null);
        if (ret > 0) invokeDataChanged();
        return ret;
    }
    
    public int clear() {
    	if (mDb == null) return 0;
    	int ret = mDb.delete(getTableName(), null, null);
    	if (ret > 0) invokeDataChanged();
    	return ret;
    }
    
    public int update(int id, BaseEntity entity) {
        if (mDb == null) return 0;
        ContentValues mValues = getContentValues(entity);
        int ret = update(mValues, "id="+id);
        if (ret > 0) invokeDataChanged();
        return ret;
    }
    
    public BaseEntity get(int id) {
    	return get("id="+id, null, null, null);
    }
    
    public boolean exists(int id) {
    	if (mDb == null) return false;
    	int count = getCount("id="+id);
    	return count > 0;
    }
    
    public int getCount() {
    	return getCount(null);
    }
    
    public int getCount(String where) {
    	if (mDb == null) return 0;
    	String whereClause = "";
    	if (where != null) {
    		whereClause = " where " + where;
    	}
    	String sql = "select count(id) from " + getTableName() + whereClause;
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getInt(0);
    	mCursor.close();
    	return count;
    }
    
    @SuppressLint("DefaultLocale")
	public List<? extends BaseEntity> get(int offset, int limit) {
        if (mDb == null) return null;
        String limitClause = String.format("%d,%d", offset,limit);
        return query(null, null, null, getDefaultOrderby(), limitClause);
    }

    public List<? extends BaseEntity> get() {
    	if (mDb == null) return null;
    	return query(null, null, null,getDefaultOrderby(),null);
    }
    protected List<? extends BaseEntity> query(String where, String groupby, String having, String orderby, String limit) {
    	if (mDb == null) return null;
    	List<BaseEntity> mList = new ArrayList<BaseEntity>();
    	Cursor mCursor = mDb.query(getTableName(), getColumns(), where, null, groupby, having, orderby, limit);
        BaseEntity entity;
        mCursor.moveToFirst();
        int count = mCursor.getCount();
        for (int i = 0; i < count; i++) {
            entity = getEntityFromCursor(mCursor);
            mList.add(entity);
            mCursor.moveToNext();
        }
        mCursor.close();
    	return mList;
    }
    
    protected BaseEntity get(String where, String groupby, String having, String orderby) {
    	if (mDb == null) return null;
    	BaseEntity entity = null;
    	Cursor mCursor = mDb.query(getTableName(), getColumns(), where, null, groupby, having, orderby);
    	if (mCursor.getCount() > 0) {
    		mCursor.moveToFirst();
    		entity = getEntityFromCursor(mCursor);
    	}
    	mCursor.close();
    	return entity;
    }
    
    protected int update(ContentValues values, String where) {
    	if (mDb == null) return 0;
    	return mDb.update(getTableName(), values, where, null);
    }
    
    public List<? extends BaseEntity> get(String where) {
    	if (mDb == null) return null;
    	return query(where, null, null, getDefaultOrderby(), null);
    }
    
    // TODO use DROP-CREATE instead of DELETE
    public BaseDAO truncate() {
    	if (mDb == null) return this;
    	String sql = "delete from " + getTableName();
    	mDb.execSQL(sql);
    	return this;
    	
    }
    
}
