package com.yiban.rec.domain.vo;

import java.util.Date;


public class HealthCareDetailQueryVo {
	
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

	private String orgName;

	// 操作类型，收费，退费，撤销
	private String operationType;
	
	// 支付商户流水号
	private String payFlowNo;
	
	// 医保类型编码
	private String healthcareTypeCode;

	// 业务周期号
	private String businessCycleNo;
	
	//开始时间
	private Date startDate;
	
	//结束时间
	private Date endDate;
	
	//开始时间字符串
	private String startTime;
	
	//结束时间字符串
	private String endTime;

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

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	public String getHealthcareTypeCode() {
		return healthcareTypeCode;
	}

	public void setHealthcareTypeCode(String healthcareTypeCode) {
		this.healthcareTypeCode = healthcareTypeCode;
	}

	public String getBusinessCycleNo() {
		return businessCycleNo;
	}

	public void setBusinessCycleNo(String businessCycleNo) {
		this.businessCycleNo = businessCycleNo;
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

	
}
