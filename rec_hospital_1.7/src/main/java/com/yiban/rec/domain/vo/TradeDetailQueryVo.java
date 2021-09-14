package com.yiban.rec.domain.vo;

public class TradeDetailQueryVo {
	
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
	private String startDate;
	
	//结束时间
	private String endDate;
	//设备编码
	private String deviceNo;
	//收费员
	private String cashier;
	
	private String orgName;

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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPayTermNo() {
		return payTermNo;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
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

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
}
