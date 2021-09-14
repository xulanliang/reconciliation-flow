package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

public class SZDRHisVo {
	private String psOrdNum;//公共平台订单号
	private String hisOrdNum;//医院订单号
	private String hisOrdJournalNum;//医院交易流水号
	private String agtOrdNum;//收单机构流水号
	private String patCardType;//诊疗卡类型
	private String patCardNo;//卡号
	private String patName;//患者姓名
	private String orderType;//订单类型
	private String payMode;//支付渠道
	private String collectors;//收费员
	private String payStatus;//支付状态
	private BigDecimal payAmount;//自费金额
	private BigDecimal accountAmount;//个人账户结算金额
	private String medicareAmount;//统筹基金结算金额
	private String insuranceAmount;//记账合计
	private String totalAmount;//总金额
	private String payTime;//支付时间
	private String agtCode;//收单机构代码
	
	
	public BigDecimal getAccountAmount() {
		return accountAmount;
	}
	public void setAccountAmount(BigDecimal accountAmount) {
		this.accountAmount = accountAmount;
	}
	
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public String getPsOrdNum() {
		return psOrdNum;
	}
	public void setPsOrdNum(String psOrdNum) {
		this.psOrdNum = psOrdNum;
	}
	public String getHisOrdNum() {
		return hisOrdNum;
	}
	public void setHisOrdNum(String hisOrdNum) {
		this.hisOrdNum = hisOrdNum;
	}
	public String getHisOrdJournalNum() {
		return hisOrdJournalNum;
	}
	public void setHisOrdJournalNum(String hisOrdJournalNum) {
		this.hisOrdJournalNum = hisOrdJournalNum;
	}
	public String getAgtOrdNum() {
		return agtOrdNum;
	}
	public void setAgtOrdNum(String agtOrdNum) {
		this.agtOrdNum = agtOrdNum;
	}
	public String getPatCardType() {
		return patCardType;
	}
	public void setPatCardType(String patCardType) {
		this.patCardType = patCardType;
	}
	public String getPatCardNo() {
		return patCardNo;
	}
	public void setPatCardNo(String patCardNo) {
		this.patCardNo = patCardNo;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getCollectors() {
		return collectors;
	}
	public void setCollectors(String collectors) {
		this.collectors = collectors;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	
	
	public String getMedicareAmount() {
		return medicareAmount;
	}
	public void setMedicareAmount(String medicareAmount) {
		this.medicareAmount = medicareAmount;
	}
	public String getInsuranceAmount() {
		return insuranceAmount;
	}
	public void setInsuranceAmount(String insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public String getAgtCode() {
		return agtCode;
	}
	public void setAgtCode(String agtCode) {
		this.agtCode = agtCode;
	}
	
	
	

}
