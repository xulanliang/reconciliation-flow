package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

/**
 * 医保his表
 */
@Entity
@Table(name = "t_healthcare_his")
public class HealthCareHis extends IdEntity {

	private static final long serialVersionUID = -1259836967637211668L;
	// 机构编码
	private String orgNo;

	// 机构名称
	@Transient
	private String orgName;

	// 操作类型，收费，退费，撤销
	private String operationType;

	// 操作名称
	@Transient
	private String operationTypeName;

	// 支付商户流水号
	private String payFlowNo;
	// 社保电脑号
	private String socialComputerNumber;

	// 交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDatatime;

	// 医保中心编码（医保类型）
	private String healthcareTypeCode;

	// 医保中心名称（医保类型）
	@Transient
	private String healthcareTypeName;

	// 业务周期号
	@Column(length = 64)
	private String businessCycleNo;

	// 医疗费总额
	private BigDecimal costAll;

	// 基本医疗费用
	private BigDecimal costBasic;

	// 账户支付金额
	private BigDecimal costAccount;

	// 现金支付金额
	private BigDecimal costCash;

	// 统筹支付金额
	private BigDecimal costWhole;

	// 大病救助基金支付
	private BigDecimal costRescue;

	// 公务员补助支付
	private BigDecimal costSubsidy;

	// 医保合计金额
	private BigDecimal costTotalInsurance;
	// 收费员
	private String cashier;

	// 创建时间
	private Date createdDate;

	// 是否删除
	private Integer isDeleted;

	// 是否有效
	private Integer isActived;

	// 账单来源
	private String billSource;

	// 订单状态
	private String orderState;

	// 患者类型
	private String patType;
	private String patientName;
	// 业务业务类型（挂号/缴费）
	private String busnessType;

	// 隔日平账标志  true：隔日账平   false：未平
	private String crossDayRec;

	public BigDecimal getCostTotalInsurance() {
		return costTotalInsurance;
	}

	public void setCostTotalInsurance(BigDecimal costTotalInsurance) {
		this.costTotalInsurance = costTotalInsurance;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
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

	public Date getTradeDatatime() {
		return tradeDatatime;
	}

	public void setTradeDatatime(Date tradeDatatime) {
		this.tradeDatatime = tradeDatatime;
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

	public BigDecimal getCostAll() {
		return costAll;
	}

	public void setCostAll(BigDecimal costAll) {
		this.costAll = costAll;
	}

	public BigDecimal getCostBasic() {
		return costBasic;
	}

	public void setCostBasic(BigDecimal costBasic) {
		this.costBasic = costBasic;
	}

	public BigDecimal getCostAccount() {
		return costAccount;
	}

	public void setCostAccount(BigDecimal costAccount) {
		this.costAccount = costAccount;
	}

	public BigDecimal getCostCash() {
		return costCash;
	}

	public void setCostCash(BigDecimal costCash) {
		this.costCash = costCash;
	}

	public BigDecimal getCostWhole() {
		return costWhole;
	}

	public void setCostWhole(BigDecimal costWhole) {
		this.costWhole = costWhole;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getOperationTypeName() {
		return operationTypeName;
	}

	public void setOperationTypeName(String operationTypeName) {
		this.operationTypeName = operationTypeName;
	}

	public String getHealthcareTypeName() {
		return healthcareTypeName;
	}

	public void setHealthcareTypeName(String healthcareTypeName) {
		this.healthcareTypeName = healthcareTypeName;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getIsActived() {
		return isActived;
	}

	public void setIsActived(Integer isActived) {
		this.isActived = isActived;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public BigDecimal getCostRescue() {
		return costRescue;
	}

	public void setCostRescue(BigDecimal costRescue) {
		this.costRescue = costRescue;
	}

	public BigDecimal getCostSubsidy() {
		return costSubsidy;
	}

	public void setCostSubsidy(BigDecimal costSubsidy) {
		this.costSubsidy = costSubsidy;
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

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getCrossDayRec() {
		return crossDayRec;
	}

	public void setCrossDayRec(String crossDayRec) {
		this.crossDayRec = crossDayRec;
	}
}
