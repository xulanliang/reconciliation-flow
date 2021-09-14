package com.yiban.rec.domain.vo;

public class TradeHISDetailVo {

	// 患者姓名
	private String patientName;
	// 患者类型
	private String patientType;
	// 交易时间
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private String tradeTime;
	// 病人号
	private String patientNo;
	// his流水号
	private String hisNo;
	// 支付流水号
	private String payNo;
	// 支付类型
	private String payType;
	// 订单状态
	private String orderState;
	// 交易金额
	private String tradeAmount;
	//对接his接口状态，0：未接his详情接口，1：已接his详情接口没数据，2：已接his详情接口数据正常
	private String titleState;
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getPatientType() {
		return patientType;
	}
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getPatientNo() {
		return patientNo;
	}
	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}
	public String getHisNo() {
		return hisNo;
	}
	public void setHisNo(String hisNo) {
		this.hisNo = hisNo;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getTitleState() {
		return titleState;
	}
	public void setTitleState(String titleState) {
		this.titleState = titleState;
	}
	
	
	
}
