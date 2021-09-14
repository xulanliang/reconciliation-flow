package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class YyAliTradeBillVo {
	private String tsnOrderNo; //支付宝交易流水号
	private String outTradeNo;//商户订单号（云医）
	private String hisOrderNo;//his订单号
	private String orderState; //交易状态  交易/退款
	private BigDecimal payAmount; //支付金额
	private BigDecimal payTotalAmount; //交易总金额
	private BigDecimal ybPayAmount; //医保金额
	private String tradeDateTime;   //交易时间
	
	private String payType;   //支付类型
	private String billSource;   //数据来源
	private String patientCardNo;   //患者卡号
	private String patientName;   //患者姓名
	public String getTsnOrderNo() {
		return tsnOrderNo;
	}
	public void setTsnOrderNo(String tsnOrderNo) {
		this.tsnOrderNo = tsnOrderNo;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getHisOrderNo() {
		return hisOrderNo;
	}
	public void setHisOrderNo(String hisOrderNo) {
		this.hisOrderNo = hisOrderNo;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public BigDecimal getPayTotalAmount() {
		return payTotalAmount;
	}
	public void setPayTotalAmount(BigDecimal payTotalAmount) {
		this.payTotalAmount = payTotalAmount;
	}
	public BigDecimal getYbPayAmount() {
		return ybPayAmount;
	}
	public void setYbPayAmount(BigDecimal ybPayAmount) {
		this.ybPayAmount = ybPayAmount;
	}
	public String getTradeDateTime() {
		return tradeDateTime;
	}
	public void setTradeDateTime(String tradeDateTime) {
		this.tradeDateTime = tradeDateTime;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getPatientCardNo() {
		return patientCardNo;
	}
	public void setPatientCardNo(String patientCardNo) {
		this.patientCardNo = patientCardNo;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
	

}
