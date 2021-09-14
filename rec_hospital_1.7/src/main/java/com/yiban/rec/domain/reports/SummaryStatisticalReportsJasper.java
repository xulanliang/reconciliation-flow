package com.yiban.rec.domain.reports;
/**
* @author swing
* @date 2018年5月16日 上午10:47:11
* 类说明
*/
public class SummaryStatisticalReportsJasper {
	private String hosName;
	private Double cashRecharge;
	private Double bankRecharge;
	private Double wxRecharge;
	private Double aliRecharge;
	
	public String getHosName() {
		return hosName;
	}

	public void setHosName(String hosName) {
		this.hosName = hosName;
	}

	public Double getCashRecharge() {
		return cashRecharge;
	}

	public void setCashRecharge(Double cashRecharge) {
		this.cashRecharge = cashRecharge;
	}

	public Double getBankRecharge() {
		return bankRecharge;
	}

	public void setBankRecharge(Double bankRecharge) {
		this.bankRecharge = bankRecharge;
	}

	public Double getWxRecharge() {
		return wxRecharge;
	}

	public void setWxRecharge(Double wxRecharge) {
		this.wxRecharge = wxRecharge;
	}

	public Double getAliRecharge() {
		return aliRecharge;
	}

	public void setAliRecharge(Double aliRecharge) {
		this.aliRecharge = aliRecharge;
	}

	public SummaryStatisticalReportsJasper() {
	}

	public SummaryStatisticalReportsJasper(String hosName, Double cashRecharge, Double bankRecharge, Double wxRecharge, Double aliRecharge) {
		this.hosName = hosName;
		this.cashRecharge = cashRecharge;
		this.bankRecharge = bankRecharge;
		this.wxRecharge = wxRecharge;
		this.aliRecharge = aliRecharge;
	}
	
}
