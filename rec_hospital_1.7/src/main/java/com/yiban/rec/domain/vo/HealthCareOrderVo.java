package com.yiban.rec.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 医保账单上送vo
 */
public class HealthCareOrderVo implements Serializable {

	public static String BILL_TYPE_HIS = "0";
	public static String BILL_TYPE_OFFICIAL = "1";

	@NotBlank(message = "医保账单类型不能为空(0:his医保账单，1:医保中心账单)")
	private String billType;

	// 机构编码
	@NotBlank(message = "机构编码不能为空")
	private String orgNo;

	// 操作类型，收费，退费，撤销
	@NotBlank(message = "操作类型不能为空")
	private String operationType;

	// 支付商户流水号
	@NotBlank(message = "支付流水号不能为空")
	private String payFlowNo;

	// 交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	@NotNull(message = "交易时间不能为空(yyyy-MM-dd HH:mm:ss)")
	private Date tradeDatatime;

	// 医保中心编码（医保类型）
	private String healthcareTypeCode;

	// 业务周期号
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

	// 账单来源
	private String billSource = "self";

	// 订单状态
	@NotBlank(message = "订单状态不能为空")
	private String orderState;

	// 患者类型
	private String patientName;

	// 门诊住院
	private String patType;

	// 创建时间
	private Date createdDate = new Date();

	// 是否删除
	private Integer isDeleted = 0;

	// 是否有效
	private Integer isActived = 1;

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
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

	public BigDecimal getCostTotalInsurance() {
		return costTotalInsurance;
	}

	public void setCostTotalInsurance(BigDecimal costTotalInsurance) {
		this.costTotalInsurance = costTotalInsurance;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public Integer getIsActived() {
		return isActived;
	}

}
