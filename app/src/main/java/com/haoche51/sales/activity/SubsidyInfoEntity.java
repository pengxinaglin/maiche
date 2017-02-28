package com.haoche51.sales.activity;

/**
 * Created by PengXianglin on 16/8/31.
 */
public class SubsidyInfoEntity {
	private int trans_fee;     // 服务费
	private int fin_loan;      // 贷款
	private int fin_insurance; // 保险
	private int oil_fee;       // 油补
	private int bonus;         // 奖金
	private int current_expect_income; // 预期收入
	private int last_total_income;     // 上月收入
	private int level;                 // 本月级别
	private String level_desc;         // 宝马级
	private String prev_month_url;// 上月薪资详情
	private String cur_month_url;// 本月薪资详情
	private int task_count;//看车任务数

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public int getCurrent_expect_income() {
		return current_expect_income;
	}

	public void setCurrent_expect_income(int current_expect_income) {
		this.current_expect_income = current_expect_income;
	}

	public int getFin_insurance() {
		return fin_insurance;
	}

	public void setFin_insurance(int fin_insurance) {
		this.fin_insurance = fin_insurance;
	}

	public int getFin_loan() {
		return fin_loan;
	}

	public void setFin_loan(int fin_loan) {
		this.fin_loan = fin_loan;
	}

	public int getLast_total_income() {
		return last_total_income;
	}

	public void setLast_total_income(int last_total_income) {
		this.last_total_income = last_total_income;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getLevel_desc() {
		return level_desc;
	}

	public void setLevel_desc(String level_desc) {
		this.level_desc = level_desc;
	}

	public int getOil_fee() {
		return oil_fee;
	}

	public void setOil_fee(int oil_fee) {
		this.oil_fee = oil_fee;
	}

	public int getTrans_fee() {
		return trans_fee;
	}

	public void setTrans_fee(int trans_fee) {
		this.trans_fee = trans_fee;
	}

	public String getCur_month_url() {
		return cur_month_url;
	}

	public void setCur_month_url(String cur_month_url) {
		this.cur_month_url = cur_month_url;
	}

	public String getPrev_month_url() {
		return prev_month_url;
	}

	public void setPrev_month_url(String prev_month_url) {
		this.prev_month_url = prev_month_url;
	}

	public int getTask_count() {
		return task_count;
	}

	public void setTask_count(int task_count) {
		this.task_count = task_count;
	}
}
