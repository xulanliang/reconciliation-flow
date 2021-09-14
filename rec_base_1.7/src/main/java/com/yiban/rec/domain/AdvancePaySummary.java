package com.yiban.rec.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 *  预收款汇总表
 */
@Entity
@Table(name = "advance_pay_summary")
public class AdvancePaySummary extends IdEntity {

	private static final long serialVersionUID = 1500731789649529936L;

	// 机构编码
	private String orgNo;
	//机构名称
	@Transient
	private String orgName;
	// 渠道
	private String billSource;
	// 患者名称
	private String patientName;
	// 交易金额
	private BigDecimal amount;
	// 支付时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date payTime;
	// 业务产生时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date serverDate;
	// 支付类型
	private String payType;
	// 账单拉取时间
	private Date reconDate;
	// 账单类型 0:预约支付 1:预约已就诊 2:预约已退费
	private String orderType;
	// 流水号
	private String payFlowNo;


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

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getServerDate() {
		return serverDate;
	}

	public void setServerDate(Date serverDate) {
		this.serverDate = serverDate;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Date getReconDate() {
		return reconDate;
	}

	public void setReconDate(Date reconDate) {
		this.reconDate = reconDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
}
