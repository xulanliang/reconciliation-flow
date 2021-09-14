package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HealthExceptionVo {
	/**
	 * 第几页
	 */
	private int page;

	/**
	 * 每页的条数
	 */

	private int rows;
	
	//医保流水号
	private Long id;

	//医保流水号
	private String payFlowNo;
	//机构编码
	private String orgNo;
	
	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDataTime;
	//状态
	private String orderState;
	//医保卡号
	private String healthCode;
	//患者名称
	private String patientName;
	//统筹支付金额
	private BigDecimal costWhole;
	//个人账户支付金额
	private BigDecimal costAccount;
	//门诊住院
	private String patType;
	//医保类型
	private String healthType;
	//数据来源  mz-门诊   zy-住院
	private String dataSource;
	//异常类型  5 his多出   6中心多出
	private Integer checkState;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createdDate;
	
	private String billCostWhole;
	private String billCostAccount;
	private String hisCostWhole;
	private String hisCostAccount;
	
	private String startTime;
	private String endTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getPayFlowNo() {
		return payFlowNo;
	}
	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public Date getTradeDataTime() {
		return tradeDataTime;
	}
	public void setTradeDataTime(Date tradeDataTime) {
		this.tradeDataTime = tradeDataTime;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getHealthCode() {
		return healthCode;
	}
	public void setHealthCode(String healthCode) {
		this.healthCode = healthCode;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public BigDecimal getCostWhole() {
		return costWhole;
	}
	public void setCostWhole(BigDecimal costWhole) {
		this.costWhole = costWhole;
	}
	public BigDecimal getCostAccount() {
		return costAccount;
	}
	public void setCostAccount(BigDecimal costAccount) {
		this.costAccount = costAccount;
	}
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}
	public String getHealthType() {
		return healthType;
	}
	public void setHealthType(String healthType) {
		this.healthType = healthType;
	}
	public Integer getCheckState() {
		return checkState;
	}
	public void setCheckState(Integer checkState) {
		this.checkState = checkState;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getBillCostWhole() {
		return billCostWhole;
	}
	public void setBillCostWhole(String billCostWhole) {
		this.billCostWhole = billCostWhole;
	}
	public String getBillCostAccount() {
		return billCostAccount;
	}
	public void setBillCostAccount(String billCostAccount) {
		this.billCostAccount = billCostAccount;
	}
	public String getHisCostWhole() {
		return hisCostWhole;
	}
	public void setHisCostWhole(String hisCostWhole) {
		this.hisCostWhole = hisCostWhole;
	}
	public String getHisCostAccount() {
		return hisCostAccount;
	}
	public void setHisCostAccount(String hisCostAccount) {
		this.hisCostAccount = hisCostAccount;
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}
