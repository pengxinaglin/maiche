package com.haoche51.sales.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.haoche51.sales.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

public class VehicleModelDAO extends BaseDAO {
    private static VehicleModelDAO dao = new VehicleModelDAO();
    public static VehicleModelDAO getInstance() {
        return dao;
    }
	
    public static final String TABLE_NAME = "vehicle_model";
    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "("
            + "id integer default 0,"
            + "series_id integer not null default 0,"
            + "model_name text not null default '',"
            + "model_short_name text not null default '',"
            + "quoto_price text not null default '',"
            + "roof text not null default '',"
            + "panoramic_roof text not null default '',"
            + "memory_seat text not null default '',"
            + "year text not null default 0,"
            + "heated_seat text not null default 0,"
            + "electric_variable_rear_seat text not null,"
            + "electric_variable_seat text not null default '',"
            + "ventilated_seat text not null default '',"
            + "leather_seat text not null default '',"
            + "multi_function_wheel text not null default '',"
            + "start_keyless text not null default '',"
            + "ccs text not null default '',"
            + "engine_model text not null default '',"
            + "emissions_l text not null default '',"
            + "transmission text not null default ''"
            + ");";
    
    private static final String[] COLUMNS = {
      "id", //id
      "series_id",//车系Id
      "model_name",// 名称
      "model_short_name",// 简称
      "quoto_price",//厂商指导价
      "roof",//外部配置-电动天窗',
      "panoramic_roof",//外部配置-全景天窗’
      "memory_seat",// '座椅配置-电动座椅记忆’,
      "year", //车型年份’,
      "heated_seat", //'座椅配置-前/后排座椅加热’,
      "electric_variable_rear_seat",//'座椅配置-后排座椅电动调节’,
      "electric_variable_seat",// '座椅配置-主/副驾驶座电动调节',
      "ventilated_seat", //座椅配置-前/后排座椅通风
      "leather_seat", //座椅配置-真皮/仿皮座椅
      "multi_function_wheel", //内部配置-多功能方向盘
      "start_keyless", //安全装备-无钥匙启动系统
      "ccs", //'内部配置-定速巡航’
      "engine_model",// '发动机-发动机型号’,
      "emissions_l", //排量
      "transmission" //基本参数-变速箱
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
			  VehicleModelEntity vehicle = (VehicleModelEntity) entity;
        ContentValues mValues = new ContentValues();
        mValues.put(COLUMNS[0], vehicle.getId());
        mValues.put(COLUMNS[1], vehicle.getSeries_id());
        mValues.put(COLUMNS[2], vehicle.getModel_name());
        mValues.put(COLUMNS[3], vehicle.getModel_short_name());
        mValues.put(COLUMNS[4], vehicle.getQuoto_price());
        mValues.put(COLUMNS[5], vehicle.getRoof());
        mValues.put(COLUMNS[6], vehicle.getPanoramic_roof());
        mValues.put(COLUMNS[7], vehicle.getMemory_seat());
        mValues.put(COLUMNS[8], vehicle.getYear());
        mValues.put(COLUMNS[9], vehicle.getHeated_seat());
        mValues.put(COLUMNS[10], vehicle.getElectric_variable_rear_seat());
        mValues.put(COLUMNS[11], vehicle.getElectric_variable_seat());
        mValues.put(COLUMNS[12], vehicle.getVentilated_seat());
        mValues.put(COLUMNS[13], vehicle.getLeather_seat());
        mValues.put(COLUMNS[14], vehicle.getMulti_function_wheel());
        mValues.put(COLUMNS[15], vehicle.getStart_keyless());
        mValues.put(COLUMNS[16], vehicle.getCcs());
        mValues.put(COLUMNS[17], vehicle.getEngine_model());
        mValues.put(COLUMNS[18], vehicle.getEmissions_l());
        mValues.put(COLUMNS[19], vehicle.getTransmission());
        
        return mValues;
    }

    @Override
    protected BaseEntity getEntityFromCursor(Cursor mCursor) {
			  VehicleModelEntity vehicle = new VehicleModelEntity();
        vehicle.setId(mCursor.getInt(0));
        vehicle.setSeries_id(mCursor.getInt(1));
        vehicle.setModel_name(mCursor.getString(2));
        vehicle.setModel_short_name(mCursor.getString(3));
        vehicle.setQuoto_price(mCursor.getString(4));
        vehicle.setRoof(mCursor.getString(5));
        vehicle.setPanoramic_roof(mCursor.getString(6));
        vehicle.setMemory_seat(mCursor.getString(7));
        vehicle.setYear(mCursor.getString(8));
        vehicle.setHeated_seat(mCursor.getString(9));
        vehicle.setElectric_variable_rear_seat(mCursor.getString(10));
        vehicle.setElectric_variable_seat(mCursor.getString(11));
        vehicle.setVentilated_seat(mCursor.getString(12));
        vehicle.setLeather_seat(mCursor.getString(13));
        vehicle.setMulti_function_wheel(mCursor.getString(14));
        vehicle.setStart_keyless(mCursor.getString(15));
        vehicle.setCcs(mCursor.getString(16));
        vehicle.setEngine_model(mCursor.getString(17));
        vehicle.setEmissions_l(mCursor.getString(18));
        vehicle.setTransmission(mCursor.getString(19));
        return vehicle;
    }

    @Override
    public VehicleModelEntity get(int id) {
        return (VehicleModelEntity) super.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VehicleModelEntity> get(int offset, int limit) {
        return (List<VehicleModelEntity>) super.get(offset, limit);
    }
    
    @SuppressWarnings("unchecked")
	public List<VehicleModelEntity> get(String where) {
    	return (List<VehicleModelEntity>) super.query(where,null,null,"year asc", null);
    }
    /**
     * 获取车型年份
     * @param seriesId
     * @return
     */
    public List<String> getYear(int seriesId) {
    	if (mDb == null) return null;
    	String sql = "select year from "+TABLE_NAME+" where series_id="+seriesId+" group by year order by year asc";
    	List<String> year = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		year.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	
    	return year;
    }
   /**
    * 获取变数箱
    * @param seriesId
    * @return
    */
    public List<String> getTransmission(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select transmission from "+TABLE_NAME+" where series_id="+seriesId+" group by transmission order by transmission asc;";
    	List<String> transmission = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		transmission.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return transmission;
    }
    
    /**
     * 获取排量
     * @param seriesId
     * @return
     */
    public List<String> getEmission(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select emissions_l from "+TABLE_NAME+" where series_id="+ seriesId+" group by emissions_l order by emissions_l desc;";
    	List<String> emission = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		emission.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return emission;
    }
    /**
     * 获取发动机型号
     * @param seriesId
     * @return
     */
    public List<String> getEngineMode(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select engine_model from "+TABLE_NAME+" where series_id="+seriesId+" group by engine_model order by engine_model desc;";
    	List<String> engineMode = new ArrayList<String>();
    	
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i< count; i++){
    		engineMode.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return engineMode;
    }
    /**
     * 获取天窗
     * @param seriesId
     * @return
     */
    public List<String> getRoof(int seriesId) {
    	if (mDb == null) return null;
    	String sql = "select roof from "+TABLE_NAME+" where series_id="+seriesId+" group by roof order by panoramic_roof desc;";
    	List<String> roof = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count  = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		roof.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return roof;
    }
    /**
     * 获取定速巡航
     * @param seriesId
     * @return
     */
    public List<String> getCcS(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select ccs from "+TABLE_NAME+" where series_id="+seriesId+" group by ccs order by ccs desc;";
    	List<String> ccs = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		ccs.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return ccs;
    }
    /**
     * 获取无钥匙启动
     * @param seriesId
     * @return
     */
    public List<String> getKeyless(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select start_keyless from "+TABLE_NAME+" where series_id="+seriesId+" group by start_keyless order by start_keyless desc;";
    	List<String> startKeyless = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<mCursor.getCount(); i++){
    		startKeyless.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return startKeyless;
    } 
    /**
     * 获取多功能方向盘
     * @param seriesId
     * @return
     */
    public List<String> getMultiWheel(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select multi_function_wheel from "+TABLE_NAME+" where series_id="+seriesId+" group by multi_function_wheel order by multi_function_wheel desc;";
    	List<String> multiWheel = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		multiWheel.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return multiWheel;
    }
    /**
     * 获取座椅材质
     * @param seriesId
     * @return
     */
    public List<String> getLeatherSeat(int seriesId) {
    	if (mDb == null) return null;
    	String sql = "select leather_seat from "+TABLE_NAME+" where series_id="+seriesId+" group by leather_seat order by leather_seat desc;";
    	List<String> leatherSeat = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		leatherSeat.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return leatherSeat;
    }
    
    /**
     * 获取座椅电动调节
     * @param seriesId
     * @return
     */
    public List<String> getElectricSeat(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select electric_variable_seat from "+TABLE_NAME+" where series_id="+seriesId+" group by electric_variable_seat order by electric_variable_seat desc;";
    	List<String> electricSeat = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		electricSeat.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return electricSeat;
    }
    /**
     * 获取座椅记忆
     * @param seriesId
     * @return memory_seat
     */
    public List<String> getMemorySeat(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select memory_seat from "+TABLE_NAME+" where series_id="+seriesId+" group by memory_seat order by memory_seat desc;";
    	List<String> memorySeat = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		memorySeat.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return memorySeat;
    }
    /**
     * 获取座椅加热
     * @param seriesId
     * @return heated_seat
     */
    public List<String> getHeatSeat(int seriesId){
    	if (mDb == null) return null;
    	String sql = "select heated_seat from "+TABLE_NAME+" where series_id="+seriesId+" group by heated_seat order by heated_seat desc;";
    	List<String> heatSeat = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		heatSeat.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return heatSeat;
    }
    /**
     * 座椅通风
     * @param seriesId
     * @return ventilated_seat
     */
    
    public List<String> getVentilatedSeat(int seriesId) {
    	if (mDb == null) return null;
    	String sql = "select ventilated_seat from "+TABLE_NAME+" where series_id="+seriesId+" group by ventilated_seat order by ventilated_seat desc;";
    	List<String> ventiSeat = new ArrayList<String>();
    	Cursor mCursor = mDb.rawQuery(sql, null);
    	mCursor.moveToFirst();
    	int count = mCursor.getCount();
    	for (int i=0; i<count; i++){
    		ventiSeat.add(mCursor.getString(0));
    		mCursor.moveToNext();
    	}
    	return ventiSeat;
    }
    
}
