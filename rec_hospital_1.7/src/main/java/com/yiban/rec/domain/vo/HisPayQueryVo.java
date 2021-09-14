package com.yiban.rec.domain.vo;

import java.util.Date;

public class HisPayQueryVo {
	
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
	//支付流水号
	private String payFlowNo;
	//支付类型
	private String payType;
	//门诊住院
	private String patType;
	// 交易状态
	private String hisOrderState;
	// 证件号
	private String hisCredentialsNo;
	// 订单号
	private String hisOriPayFlowNo;
	// 订单号
	private String hisFlowNo;
	// 发票号
	private String hisInvoiceNo;
	// 住院号
	private String hisPatCode;
	//门诊号
	private String hisMzCode;
	
	//开始时间
	private Date startDate;
	//结束时间
	private Date endDate;
	//开始时间字符串
	private String startTime;
	//结束时间字符串
	private String endTime;
	// 支付账号
	private String payAccount;
	//收费员
	private String cashier;
	private String orgName;
	
	//账单来源
	private String billSource;
	// 患者名称
	private String custName;
	// 日期 yyyy-MM-dd
	private String date;
	//商户流水号
	private String businessFlowNo;
	private String tradeType;
	//现金账单来源
		private String cashBillSource;
	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

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

	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
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

	public String getHisOrderState() {
		return hisOrderState;
	}

	public void setHisOrderState(String hisOrderState) {
		this.hisOrderState = hisOrderState;
	}

	public String getHisCredentialsNo() {
		return hisCredentialsNo;
	}

	public void setHisCredentialsNo(String hisCredentialsNo) {
		this.hisCredentialsNo = hisCredentialsNo;
	}

	public String getHisOriPayFlowNo() {
		return hisOriPayFlowNo;
	}

	public void setHisOriPayFlowNo(String hisOriPayFlowNo) {
		this.hisOriPayFlowNo = hisOriPayFlowNo;
	}

	public String getHisInvoiceNo() {
		return hisInvoiceNo;
	}

	public void setHisInvoiceNo(String hisInvoiceNo) {
		this.hisInvoiceNo = hisInvoiceNo;
	}

	public String getHisPatCode() {
		return hisPatCode;
	}

	public void setHisPatCode(String hisPatCode) {
		this.hisPatCode = hisPatCode;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getHisFlowNo() {
		return hisFlowNo;
	}

	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}

	public String getHisMzCode() {
		return hisMzCode;
	}

	public void setHisMzCode(String hisMzCode) {
		this.hisMzCode = hisMzCode;
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

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBusinessFlowNo() {
		return businessFlowNo;
	}

	public void setBusinessFlowNo(String businessFlowNo) {
		this.businessFlowNo = businessFlowNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}

	public String getCashBillSource() {
		return cashBillSource;
	}

	public void setCashBillSource(String cashBillSource) {
		this.cashBillSource = cashBillSource;
	}
}
