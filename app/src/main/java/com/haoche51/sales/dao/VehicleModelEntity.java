package com.haoche51.sales.dao;

import com.haoche51.sales.entity.BaseEntity;

public class VehicleModelEntity extends BaseEntity {
    private int id = 0;
    private int series_id = 0;
    private String model_name = "";
    private String model_short_name = "";
    private String quoto_price="";
    private String roof ="";
    private String panoramic_roof ="";
    private String memory_seat = "";
    private String year ="";
    private String heated_seat = "";
    private String electric_variable_rear_seat = "";
    private String electric_variable_seat="";
    private String ventilated_seat="";
    private String leather_seat="";
    private String multi_function_wheel="";
    private String start_keyless="";
    private String ccs="";
    private String engine_model ="";
    private String emissions_l ="";
    private String transmission ="";

    public static VehicleModelEntity parseFromJson(String jsonString) {
        return gson.fromJson(jsonString, VehicleModelEntity.class);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeries_id() {
		return series_id;
	}

	public void setSeries_id(int series_id) {
		this.series_id = series_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getModel_short_name() {
		return model_short_name;
	}

	public void setModel_short_name(String model_short_name) {
		this.model_short_name = model_short_name;
	}

	public String getQuoto_price() {
		return quoto_price;
	}

	public void setQuoto_price(String quoto_price) {
		this.quoto_price = quoto_price;
	}

	public String getRoof() {
		return roof;
	}

	public void setRoof(String roof) {
		this.roof = roof;
	}

	public String getPanoramic_roof() {
		return panoramic_roof;
	}

	public void setPanoramic_roof(String panoramic_roof) {
		this.panoramic_roof = panoramic_roof;
	}

	public String getMemory_seat() {
		return memory_seat;
	}

	public void setMemory_seat(String memory_seat) {
		this.memory_seat = memory_seat;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getHeated_seat() {
		return heated_seat;
	}

	public void setHeated_seat(String heated_seat) {
		this.heated_seat = heated_seat;
	}

	public String getElectric_variable_rear_seat() {
		return electric_variable_rear_seat;
	}

	public void setElectric_variable_rear_seat(String electric_variable_rear_seat) {
		this.electric_variable_rear_seat = electric_variable_rear_seat;
	}

	public String getElectric_variable_seat() {
		return electric_variable_seat;
	}

	public void setElectric_variable_seat(String electric_variable_seat) {
		this.electric_variable_seat = electric_variable_seat;
	}

	public String getVentilated_seat() {
		return ventilated_seat;
	}

	public void setVentilated_seat(String ventilated_seat) {
		this.ventilated_seat = ventilated_seat;
	}

	public String getLeather_seat() {
		return leather_seat;
	}

	public void setLeather_seat(String leather_seat) {
		this.leather_seat = leather_seat;
	}

	public String getMulti_function_wheel() {
		return multi_function_wheel;
	}

	public void setMulti_function_wheel(String multi_function_wheel) {
		this.multi_function_wheel = multi_function_wheel;
	}

	public String getStart_keyless() {
		return start_keyless;
	}

	public void setStart_keyless(String start_keyless) {
		this.start_keyless = start_keyless;
	}

	public String getCcs() {
		return ccs;
	}

	public void setCcs(String ccs) {
		this.ccs = ccs;
	}

	public String getEngine_model() {
		return engine_model;
	}

	public void setEngine_model(String engine_model) {
		this.engine_model = engine_model;
	}

	public String getEmissions_l() {
		return emissions_l;
	}

	public void setEmissions_l(String emissions_l) {
		this.emissions_l = emissions_l;
	}

	public String getTransmission() {
		return transmission;
	}

	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}

}
