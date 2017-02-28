package com.haoche51.sales.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.haoche51.sales.entity.BaseEntity;
import com.haoche51.sales.hctransaction.VehicleSeriesEntity;

import java.util.List;

public class VehicleSeriesDAO extends BaseDAO {
    private static VehicleSeriesDAO dao = new VehicleSeriesDAO();
    public static VehicleSeriesDAO getInstance() {
        return dao;
    }
	
    public static final String TABLE_NAME = "vehicle_series";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + "id integer not null default 0,"
            + "name text not null default '',"
            + "brand_id integer not null default 0,"
            + "brand_name text not null default '',"
            + "subbrand_id integer not null default 0,"
            + "subbrand_name text not null default ''"
            + ");";
    
    private static final String[] COLUMNS = {
      "id",
      "name",
      "brand_id",
      "brand_name",
      "subbrand_id",
      "subbrand_name"
    };
    private static final String DEFAULT_ORDER_BY = null;

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
		VehicleSeriesEntity vehicleClass = (VehicleSeriesEntity) entity;
        ContentValues mValues = new ContentValues();
        mValues.put(COLUMNS[0], vehicleClass.getId());
        mValues.put(COLUMNS[1], vehicleClass.getName());
        mValues.put(COLUMNS[2], vehicleClass.getBrand_id());
        mValues.put(COLUMNS[3], vehicleClass.getBrand_name());
        mValues.put(COLUMNS[4], vehicleClass.getSubbrand_id());
        mValues.put(COLUMNS[5], vehicleClass.getSubbrand_name());
        return mValues;
    }

    @Override
    protected BaseEntity getEntityFromCursor(Cursor mCursor) {
			  VehicleSeriesEntity vehicleClass = new VehicleSeriesEntity();
        vehicleClass.setId(mCursor.getInt(0));
        vehicleClass.setName(mCursor.getString(1));
        vehicleClass.setBrand_id(mCursor.getInt(2));
        vehicleClass.setBrand_name(mCursor.getString(3));
        vehicleClass.setSubbrand_id(mCursor.getInt(4));
        vehicleClass.setSubbrand_name(mCursor.getString(5));
        return vehicleClass;
    }

    @Override
    public VehicleSeriesEntity get(int id) {
        return (VehicleSeriesEntity) super.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VehicleSeriesEntity> get(int offset, int limit) {
        return (List<VehicleSeriesEntity>) super.get(offset, limit);
    }
    
    @SuppressWarnings("unchecked")
	public List<VehicleSeriesEntity> get(String where) {
    	return (List<VehicleSeriesEntity>) super.get(where);
    }
    
    @SuppressWarnings("unchecked")
	public List<VehicleSeriesEntity> getByBrand(int brandId, int subbrandId) {
    	String where = "brand_id=" + brandId + " and subbrand_id=" + subbrandId;
    	return (List<VehicleSeriesEntity>) query(where, null, null, getDefaultOrderby(), null);
    }
}
