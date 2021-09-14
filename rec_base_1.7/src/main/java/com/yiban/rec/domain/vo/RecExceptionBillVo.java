package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 电子对账异常账单VO
 * 
 * @author clearofchina
 *
 */
public class RecExceptionBillVo {
	private Integer id;
	// 支付系统流水号
	private String businessNo;
	// his流水号
	private String hisFlowNo;
	// 交易名称 缴费 退款
	private String tradeName;
	// 支付类型
	private String payName;
	// 交易金额
	private BigDecimal tradeAmount;
	// 患者名称
	private String patientName;

	private String checkStateValue;
	// 交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeTime;
	// 业务类型
	private String businessType;
	// 患者类型
	private String patType;

	private String exceptionType;

	private String IsCorrection;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getCheckStateValue() {
		return checkStateValue;
	}

	public void setCheckStateValue(String checkStateValue) {
		this.checkStateValue = checkStateValue;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getIsCorrection() {
		return IsCorrection;
	}

	public void setIsCorrection(String isCorrection) {
		IsCorrection = isCorrection;
	}
}
