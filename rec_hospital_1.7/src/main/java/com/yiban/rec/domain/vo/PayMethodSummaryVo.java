package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PayMethodSummaryVo {

	private String source;
	private String payType;
	
	//挂号
	private BigInteger regCount=new BigInteger("0");
	private BigDecimal regPayAmount=new BigDecimal(0);
	
	//预约挂号
	private BigInteger appointmentCount=new BigInteger("0");
	private BigDecimal appointmentPayAmount=new BigDecimal(0);
	
	//缴费
	private BigInteger payCount=new BigInteger("0");
	private BigDecimal payPayAmount=new BigDecimal(0);
	
	//门诊充值
	private BigInteger rechargeCount=new BigInteger("0");
	private BigDecimal rechargePayAmount=new BigDecimal("0");
	
	//住院预交金
	private BigInteger prePaymentCount=new BigInteger("0");
	private BigDecimal prePaymentPayAmount=new BigDecimal("0");
	
	//其他
	private BigInteger otherCount=new BigInteger("0");
	private BigDecimal otherPayAmount=new BigDecimal(0);
	
	//列合计
	private BigInteger count=new BigInteger("0");
	private BigDecimal payAmount=new BigDecimal(0);
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public BigInteger getRegCount() {
		return regCount;
	}
	public void setRegCount(BigInteger regCount) {
		this.regCount = regCount;
	}
	public BigDecimal getRegPayAmount() {
		return regPayAmount;
	}
	public void setRegPayAmount(BigDecimal regPayAmount) {
		this.regPayAmount = regPayAmount;
	}
	public BigInteger getAppointmentCount() {
		return appointmentCount;
	}
	public void setAppointmentCount(BigInteger appointmentCount) {
		this.appointmentCount = appointmentCount;
	}
	public BigDecimal getAppointmentPayAmount() {
		return appointmentPayAmount;
	}
	public void setAppointmentPayAmount(BigDecimal appointmentPayAmount) {
		this.appointmentPayAmount = appointmentPayAmount;
	}
	public BigInteger getPayCount() {
		return payCount;
	}
	public void setPayCount(BigInteger payCount) {
		this.payCount = payCount;
	}
	public BigDecimal getPayPayAmount() {
		return payPayAmount;
	}
	public void setPayPayAmount(BigDecimal payPayAmount) {
		this.payPayAmount = payPayAmount;
	}
	public BigInteger getRechargeCount() {
		return rechargeCount;
	}
	public void setRechargeCount(BigInteger rechargeCount) {
		this.rechargeCount = rechargeCount;
	}
	public BigDecimal getRechargePayAmount() {
		return rechargePayAmount;
	}
	public void setRechargePayAmount(BigDecimal rechargePayAmount) {
		this.rechargePayAmount = rechargePayAmount;
	}
	public BigInteger getPrePaymentCount() {
		return prePaymentCount;
	}
	public void setPrePaymentCount(BigInteger prePaymentCount) {
		this.prePaymentCount = prePaymentCount;
	}
	public BigDecimal getPrePaymentPayAmount() {
		return prePaymentPayAmount;
	}
	public void setPrePaymentPayAmount(BigDecimal prePaymentPayAmount) {
		this.prePaymentPayAmount = prePaymentPayAmount;
	}
	public BigInteger getOtherCount() {
		return otherCount;
	}
	public void setOtherCount(BigInteger otherCount) {
		this.otherCount = otherCount;
	}
	public BigDecimal getOtherPayAmount() {
		return otherPayAmount;
	}
	public void setOtherPayAmount(BigDecimal otherPayAmount) {
		this.otherPayAmount = otherPayAmount;
	}
	public BigInteger getCount() {
		return count;
	}
	public void setCount(BigInteger count) {
		this.count = count;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
}
