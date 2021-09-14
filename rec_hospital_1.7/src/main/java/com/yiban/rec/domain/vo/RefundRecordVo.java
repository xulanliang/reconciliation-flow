package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;


public class RefundRecordVo {

	//开始时间
	private Date startDate;
	
	//结束时间
	private Date endDate;
	private String startTime;
	private String endTime;
	
	//是否存在单边账
	private String isDifferent;
	//机构编码
	private String orgNo;
	
	//his流水
	private String hisFlow;

	//支付请求流水号
	private String paymentRequestFlow;
	
	//支付系统响应流水号
	private String paymentFlow;
	
	//支付渠道名称
	private String payName;
	
	//业务类型
	private String businessType;
	
	//交易金额
	private BigDecimal tradeAmount;
	
	//设备编码
	private String equipmentNo;
	
	private String orgName;
	
	private String type;
	
	private String state;

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

	public String getIsDifferent() {
		return isDifferent;
	}

	public void setIsDifferent(String isDifferent) {
		this.isDifferent = isDifferent;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getHisFlow() {
		return hisFlow;
	}

	public void setHisFlow(String hisFlow) {
		this.hisFlow = hisFlow;
	}

	public String getPaymentRequestFlow() {
		return paymentRequestFlow;
	}

	public void setPaymentRequestFlow(String paymentRequestFlow) {
		this.paymentRequestFlow = paymentRequestFlow;
	}

	public String getPaymentFlow() {
		return paymentFlow;
	}

	public void setPaymentFlow(String paymentFlow) {
		this.paymentFlow = paymentFlow;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getEquipmentNo() {
		return equipmentNo;
	}

	public void setEquipmentNo(String equipmentNo) {
		this.equipmentNo = equipmentNo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
