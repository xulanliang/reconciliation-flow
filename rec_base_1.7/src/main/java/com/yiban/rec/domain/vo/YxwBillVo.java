package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class YxwBillVo {
	private String tradeTime;//交易时间
	private String tradeState;//订单交易类型
	private String agtPayOrdNum;//第三方支付订单号
	private String agtRefundOrdNum;//第三方退费流水号
	private String psPayOrdNum;//医享网支付流水号
	private String psRefundOrdNum;//医享网退费流水号
    private BigDecimal payTotalFee;//支付金额
	private BigDecimal refundTotalFee;//退费金额
	private String tradeType;//交易类型
	private String tradeMode;//支付类型
	private String orderMode;
	private String title;
	

	public String getTradeTime() {
		return tradeTime;
	}
	public BigDecimal getPayTotalFee() {
		return payTotalFee;
	}
	public void setPayTotalFee(BigDecimal payTotalFee) {
		this.payTotalFee = payTotalFee;
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

	public BigDecimal getRefundTotalFee() {
		return refundTotalFee;
	}

	public void setRefundTotalFee(BigDecimal refundTotalFee) {
		this.refundTotalFee = refundTotalFee;
	}

	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeMode() {
		return tradeMode;
	}
	public void setTradeMode(String tradeMode) {
		this.tradeMode = tradeMode;
	}
	
	
	
	
}
