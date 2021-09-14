package com.yiban.rec.domain.vo;

import java.util.Date;

public class CashQueryVo {
	
	/**
	 * 第几页
	 */
	private int page;

	/**
	 * 每页的条数
	 */

	private int rows;
	
	//机构编码
	private String orgNo;

	//交易类型编码
	private String tradeCode;
	
	//系统流水号
	private String sysSerial;
	
	//数据来源
	private String dataSource;
	
	//终端号
	private String payTermNo;
	
	//支付类型
	private String payType;
	
	//业务类型
	private String businessType;
	
	//支付来源
	private String paySource;
	
	//开始时间
	
	private Date startDate;
	
	//结束时间
	private Date endDate;
	//开始时间字符串
	private String startTime;
	
	//结束时间字符串
	private String endTime;
	//收费员
	private String cashier;
	
	private String orgName;
	
	//账单来源
	private String billSource;
	
	//商户流水号
	private String businessFlowNo;

	//商户流水号
	private String orderState;
	// 支付账号
	private String payAccount;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	
	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getSysSerial() {
		return sysSerial;
	}

	public void setSysSerial(String sysSerial) {
		this.sysSerial = sysSerial;
	}


	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}


	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPayTermNo() {
		return payTermNo;
	}

	public void setPayTermNo(String payTermNo) {
		this.payTermNo = payTermNo;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPaySource() {
		return paySource;
	}

	public void setPaySource(String paySource) {
		this.paySource = paySource;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getBusinessFlowNo() {
		return businessFlowNo;
	}

	public void setBusinessFlowNo(String businessFlowNo) {
		this.businessFlowNo = businessFlowNo;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}
}
