package com.yiban.rec.domain.vo;

import java.util.Date;

import com.ibm.icu.math.BigDecimal;
public class TradeDetailVo {

	// 机构编码
	private String orgCode;
	// 系统来源
	private String systemFrom;
	// 交易时间
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDataTime;
	// 患者名称
	private String custName;
	// 金额
	private BigDecimal payAmount;
	// 支付系统流水号
	private String hisOrderNO;
	// 支付系统流水号
	private String paySystemNo;
	// 就诊卡号
	private String visitNumber;
	// 设备编码
	private String deviceNo;
	// 订单状态
	private String orderState;
	// 患者类型
	private String patType;
	// 支付业务类型
	private String payBussinessType;
	
	private String startDate;
	private String endDate;
	// 支付类型
	private String payType;
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getSystemFrom() {
		return systemFrom;
	}
	public void setSystemFrom(String systemFrom) {
		this.systemFrom = systemFrom;
	}
	public Date getTradeDataTime() {
		return tradeDataTime;
	}
	public void setTradeDataTime(Date tradeDataTime) {
		this.tradeDataTime = tradeDataTime;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public String getPaySystemNo() {
		return paySystemNo;
	}
	public void setPaySystemNo(String paySystemNo) {
		this.paySystemNo = paySystemNo;
	}
	public String getVisitNumber() {
		return visitNumber;
	}
	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
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
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}
	public String getPayBussinessType() {
		return payBussinessType;
	}
	public void setPayBussinessType(String payBussinessType) {
		this.payBussinessType = payBussinessType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getHisOrderNO() {
		return hisOrderNO;
	}
	public void setHisOrderNO(String hisOrderNO) {
		this.hisOrderNO = hisOrderNO;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
}
