package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 对账结果记录
 */
@Entity
@Table(name = "t_rec_reconciliation")
public class Reconciliation extends BaseEntityEx {
	private static final long serialVersionUID = 6407236098598859539L;

	//支付时间（时间戳）
	private Long payDateStam;

	@Transient
	private String payDateStamName;
	// 支付类型
	private String payType;
	
	//支付类型名称
	@Transient
	private String payTypeName;

	// 机构编码
	private String orgNo;;
	
	//机构名称
	@Transient
	private String orgName;

	// 交易类型编码
	private String tradeCode;
	
	//交易编码名称
	@Transient
	private String tradeCodeName;
	
	//支付商户号
	private String payShopNo;
	
	//原支付流水号
	private String oriPayFlowNo;

	// 支付业务类型
	private String payBusinessType;
	
	//业务类型名称
	@Transient
	private String payBusinessTypeName;

	// 交易来源
	private String tradeFrom;

	// 客户名称
	private String custName;

	// 客户标识
	private String custIdentify;
	
	//客户标识名称
	@Transient
	private String custIdentifyName;

	// 支付系统号
	private String paySystemNo;
	
	//平台流水号
	private String flowNo;

	// 机构流水号
	private String hisFlowNo;

	// 业务系统流水号
	private String businessFlowNo;

	// 支付商户批次号
	private String payBatchNo;

	// 支付商户流水号
	private String payFlowNo;
	
	//支付账号
	private String payAccount;
	
	//订单状态
	private String orderState;
	
	//订单状态值
	@Transient
	private String orderStateName;
	
	//支付终端号
	private String payTermNo;

	//设备编码
	private String deviceNo;
	
	// 机构支付金额
	private BigDecimal orgAmount;

	// 平台支付金额
	private BigDecimal platformAmount;

	// 第三方支付金额
	private BigDecimal thirdAmount;

	// 是否存在异常
	private Integer isDifferent;
	
	@Transient
	private String isDifferentValue;
	
	//单边账处理方式
	private Integer handleCode;

	// 备注信息
	private String remarkInfo;

	// 操作用户id
	private Integer operationUserId;

	// 操作用户
	private String operationUserName;

	// 对账日期
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date reconciliationDate;

	
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
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

	public String getPayBusinessType() {
		return payBusinessType;
	}

	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
	}

	public String getTradeFrom() {
		return tradeFrom;
	}

	public void setTradeFrom(String tradeFrom) {
		this.tradeFrom = tradeFrom;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustIdentify() {
		return custIdentify;
	}

	public void setCustIdentify(String custIdentify) {
		this.custIdentify = custIdentify;
	}

	public String getPaySystemNo() {
		return paySystemNo;
	}

	public void setPaySystemNo(String paySystemNo) {
		this.paySystemNo = paySystemNo;
	}

	public String getHisFlowNo() {
		return hisFlowNo;
	}

	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}

	public String getBusinessFlowNo() {
		return businessFlowNo;
	}

	public void setBusinessFlowNo(String businessFlowNo) {
		this.businessFlowNo = businessFlowNo;
	}

	public String getPayBatchNo() {
		return payBatchNo;
	}

	public void setPayBatchNo(String payBatchNo) {
		this.payBatchNo = payBatchNo;
	}

	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}


	public BigDecimal getOrgAmount() {
		return orgAmount;
	}

	public void setOrgAmount(BigDecimal orgAmount) {
		this.orgAmount = orgAmount;
	}

	
	public BigDecimal getPlatformAmount() {
		return platformAmount;
	}

	public void setPlatformAmount(BigDecimal platformAmount) {
		this.platformAmount = platformAmount;
	}

	public BigDecimal getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(BigDecimal thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public Integer getIsDifferent() {
		return isDifferent;
	}

	public void setIsDifferent(Integer isDifferent) {
		this.isDifferent = isDifferent;
	}

	public String getRemarkInfo() {
		return remarkInfo;
	}

	public void setRemarkInfo(String remarkInfo) {
		this.remarkInfo = remarkInfo;
	}

	public Integer getOperationUserId() {
		return operationUserId;
	}

	public void setOperationUserId(Integer operationUserId) {
		this.operationUserId = operationUserId;
	}

	public String getOperationUserName() {
		return operationUserName;
	}

	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}
	
	public Date getReconciliationDate() {
		return reconciliationDate;
	}

	public void setReconciliationDate(Date reconciliationDate) {
		this.reconciliationDate = reconciliationDate;
	}

	public Integer getHandleCode() {
		return handleCode;
	}

	public void setHandleCode(Integer handleCode) {
		this.handleCode = handleCode;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getTradeCodeName() {
		return tradeCodeName;
	}

	public void setTradeCodeName(String tradeCodeName) {
		this.tradeCodeName = tradeCodeName;
	}

	public String getPayBusinessTypeName() {
		return payBusinessTypeName;
	}

	public void setPayBusinessTypeName(String payBusinessTypeName) {
		this.payBusinessTypeName = payBusinessTypeName;
	}

	public String getCustIdentifyName() {
		return custIdentifyName;
	}

	public void setCustIdentifyName(String custIdentifyName) {
		this.custIdentifyName = custIdentifyName;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}

	public String getPayTermNo() {
		return payTermNo;
	}

	public void setPayTermNo(String payTermNo) {
		this.payTermNo = payTermNo;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getPayShopNo() {
		return payShopNo;
	}

	public void setPayShopNo(String payShopNo) {
		this.payShopNo = payShopNo;
	}

	public String getOriPayFlowNo() {
		return oriPayFlowNo;
	}

	public void setOriPayFlowNo(String oriPayFlowNo) {
		this.oriPayFlowNo = oriPayFlowNo;
	}

	public String getOrderStateName() {
		return orderStateName;
	}

	public void setOrderStateName(String orderStateName) {
		this.orderStateName = orderStateName;
	}

	public Long getPayDateStam() {
		return payDateStam;
	}

	public void setPayDateStam(Long payDateStam) {
		this.payDateStam = payDateStam;
	}

	public String getPayDateStamName() {
		return payDateStamName;
	}

	public void setPayDateStamName(String payDateStamName) {
		this.payDateStamName = payDateStamName;
	}

	public String getIsDifferentValue() {
		return isDifferentValue;
	}

	public void setIsDifferentValue(String isDifferentValue) {
		this.isDifferentValue = isDifferentValue;
	}

}
