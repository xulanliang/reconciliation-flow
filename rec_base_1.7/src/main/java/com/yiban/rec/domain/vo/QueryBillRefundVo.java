package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class QueryBillRefundVo {
	//退款金额
	private BigDecimal tradeAmount;
	//流水号
	private String payNo;
	//支付类型
	private String payCode;
	//用户密码
	private String passWord;
	
	//支付金额
	private BigDecimal payAmount;
	
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	
}
