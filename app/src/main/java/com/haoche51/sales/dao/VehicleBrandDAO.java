package com.haoche51.sales.dao;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.haoche51.sales.entity.BaseEntity;

public class VehicleBrandDAO extends BaseDAO {
    private static VehicleBrandDAO dao = new VehicleBrandDAO();
    public static VehicleBrandDAO getInstance() {
        return dao;
    }
	
    public static final String TABLE_NAME = "vehicle_brand";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + "id integer not null default 0,"
            + "name text not null default '',"
            + "pinyin text not null default '',"
            + "first_char text not null default '',"
            + "img_url text not null default ''"
            + ");";
    
    private static final String[] COLUMNS = {
      "id",
      "name",
      "pinyin",
      "first_char",
      "img_url"
    };
    private static final String DEFAULT_ORDER_BY = "first_char asc";

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
			  VehicleBrandEntity vehicleBrand = (VehicleBrandEntity) entity;
        ContentValues mValues = new ContentValues();
        mValues.put(COLUMNS[0], vehicleBrand.getId());
        mValues.put(COLUMNS[1], vehicleBrand.getName());
        mValues.put(COLUMNS[2], vehicleBrand.getPinyin());
        mValues.put(COLUMNS[3], vehicleBrand.getFirst_char());
        mValues.put(COLUMNS[4], vehicleBrand.getImg_url());
        return mValues;
    }

    @Override
    protected BaseEntity getEntityFromCursor(Cursor mCursor) {
			  VehicleBrandEntity vehicleBrand = new VehicleBrandEntity();
        vehicleBrand.setId(mCursor.getInt(0));
        vehicleBrand.setName(mCursor.getString(1));
        vehicleBrand.setPinyin(mCursor.getString(2));
        vehicleBrand.setFirst_char(mCursor.getString(3));
        vehicleBrand.setImg_url(mCursor.getString(4));
        return vehicleBrand;
    }

    @Override
    public VehicleBrandEntity get(int id) {
        return (VehicleBrandEntity) super.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VehicleBrandEntity> get(int offset, int limit) {
        return (List<VehicleBrandEntity>) super.get(offset, limit);
    }
    
    @SuppressWarnings("unchecked")
	public List<VehicleBrandEntity> get(String where) {
    	return (List<VehicleBrandEntity>) super.get(where);
    }
    
    @SuppressWarnings("unchecked")
	public List<VehicleBrandEntity> getByFirstChar(char firstChar) {
    	String where = "first_char='" + firstChar + "'";
    	return (List<VehicleBrandEntity>) query(where, null, null, getDefaultOrderby(), null);
    }
}
