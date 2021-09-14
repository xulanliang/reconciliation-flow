package com.yiban.rec.domain.vo;


public class TradeCheckFollowVo {
	
	//机构
	private String orgNo; 
	//开始时间
	private String startDate;
	//结束时间
	private String endDate; 
	//门诊住院
	private String patType; 
	//账单来源
	private String billSource;
	//冲正
	private String correction;
	//多出方
	private String dataSourceType;
	//支付方流水号
	private String businessNo;
	//HIS流水号
	private String hisFlowNo;
	
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
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
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getCorrection() {
		return correction;
	}
	public void setCorrection(String correction) {
		this.correction = correction;
	}
	public String getDataSourceType() {
		return dataSourceType;
	}
	public void setDataSourceType(String dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public String getHisFlowNo() {
		return hisFlowNo;
	}
	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}
}
