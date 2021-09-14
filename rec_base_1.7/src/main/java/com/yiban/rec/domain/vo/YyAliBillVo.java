package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class YyAliBillVo {
	private String agtNo; //支付宝交易流水号
	private String orderNo;//商户订单号（云医）
	private String payStatus; //支付状态  交易/退款
	private BigDecimal payAmount; //交易金额
	private String payTime;   //交易时间
	public String getAgtNo() {
		return agtNo;
	}
	public void setAgtNo(String agtNo) {
		this.agtNo = agtNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	

}
