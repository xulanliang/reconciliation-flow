package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

@Entity
@Table(name = "t_health_exception")
public class HealthException extends IdEntity {

	private static final long serialVersionUID = 1L;
	//医保流水号
	private String payFlowNo;
	private String shopFlowNo;
	//机构编码
	private String orgNo;

	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDataTime;
	//状态
	private String orderState;
	//医保卡号
	private String healthCode;
	// 社保电脑号
	private String socialComputerNumber;
	//患者名称
	private String patientName;
	//统筹支付金额
	private BigDecimal costWhole;
	//个人账户支付金额
	private BigDecimal costAccount;

	// 医疗费总额
	private BigDecimal costAll;

	//医保中心 医保合计金额
	private BigDecimal costTotalInsurance;
	//医保His 医保合计金额
	private BigDecimal costTotalInsuranceHis;
	// 隔日平账标志  true：隔日账平   false：未平
	private String crossDayRec;

	//门诊住院
	private String patType;
	//业务类型（挂号/缴费）
	private String busnessType;
	//医保类型
	private String healthType;
	//异常类型  5 his多出   6中心多出
	private Integer checkState;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createdDate;


	public BigDecimal getCostAll() {
		return costAll;
	}
	public void setCostAll(BigDecimal costAll) {
		this.costAll = costAll;
	}
	public BigDecimal getCostTotalInsurance() {
		return costTotalInsurance;
	}
	public void setCostTotalInsurance(BigDecimal costTotalInsurance) {
		this.costTotalInsurance = costTotalInsurance;
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
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	public BigDecimal getCostTotalInsuranceHis() {
		return costTotalInsuranceHis;
	}

	public void setCostTotalInsuranceHis(BigDecimal costTotalInsuranceHis) {
		this.costTotalInsuranceHis = costTotalInsuranceHis;
	}

	public String getSocialComputerNumber() {
		return socialComputerNumber;
	}

	public void setSocialComputerNumber(String socialComputerNumber) {
		this.socialComputerNumber = socialComputerNumber;
	}
	public String getBusnessType() {
		return busnessType;
	}

	public void setBusnessType(String busnessType) {
		this.busnessType = busnessType;
	}

	public String getShopFlowNo() {
		return shopFlowNo;
	}

	public void setShopFlowNo(String shopFlowNo) {
		this.shopFlowNo = shopFlowNo;
	}

	public String getCrossDayRec() {
		return crossDayRec;
	}

	public void setCrossDayRec(String crossDayRec) {
		this.crossDayRec = crossDayRec;
	}
}
