package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

public class YxwYBBillVo {
	private String tradeTime;
	private String tradeState;
	private String agtPayOrdNum;
	private String agtRefundOrdNum;
	private String agtInsuranceNum;
	private String agtRefundInsuranceNum;
	private String psPayOrdNum;
	private String psRefundOrdNum;
	private BigDecimal payTotalFee;
	private BigDecimal payInsuranceFee;
	private BigDecimal payCashFee;
	private String orderMode;
	private String title;
	private String tradeMode;
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getTradeState() {
		return tradeState;
	}
	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	public String getAgtPayOrdNum() {
		return agtPayOrdNum;
	}
	public void setAgtPayOrdNum(String agtPayOrdNum) {
		this.agtPayOrdNum = agtPayOrdNum;
	}
	public String getAgtRefundOrdNum() {
		return agtRefundOrdNum;
	}
	public void setAgtRefundOrdNum(String agtRefundOrdNum) {
		this.agtRefundOrdNum = agtRefundOrdNum;
	}
	public String getAgtInsuranceNum() {
		return agtInsuranceNum;
	}
	public void setAgtInsuranceNum(String agtInsuranceNum) {
		this.agtInsuranceNum = agtInsuranceNum;
	}
	public String getAgtRefundInsuranceNum() {
		return agtRefundInsuranceNum;
	}
	public void setAgtRefundInsuranceNum(String agtRefundInsuranceNum) {
		this.agtRefundInsuranceNum = agtRefundInsuranceNum;
	}
	public String getPsPayOrdNum() {
		return psPayOrdNum;
	}
	public void setPsPayOrdNum(String psPayOrdNum) {
		this.psPayOrdNum = psPayOrdNum;
	}
	public String getPsRefundOrdNum() {
		return psRefundOrdNum;
	}
	public void setPsRefundOrdNum(String psRefundOrdNum) {
		this.psRefundOrdNum = psRefundOrdNum;
	}
	public BigDecimal getPayTotalFee() {
		return payTotalFee;
	}
	public void setPayTotalFee(BigDecimal payTotalFee) {
		this.payTotalFee = payTotalFee;
	}
	public BigDecimal getPayInsuranceFee() {
		return payInsuranceFee;
	}
	public void setPayInsuranceFee(BigDecimal payInsuranceFee) {
		this.payInsuranceFee = payInsuranceFee;
	}
	public BigDecimal getPayCashFee() {
		return payCashFee;
	}
	public void setPayCashFee(BigDecimal payCashFee) {
		this.payCashFee = payCashFee;
	}
	public String getOrderMode() {
		return orderMode;
	}
	public void setOrderMode(String orderMode) {
		this.orderMode = orderMode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTradeMode() {
		return tradeMode;
	}
	public void setTradeMode(String tradeMode) {
		this.tradeMode = tradeMode;
	}
	
	

}
