package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PatTypeSummaryVo {

	private String source;
	private String patType;
	//微信
	private BigInteger wechatCount=new BigInteger("0");
	private BigDecimal wechatPayAmount=new BigDecimal(0);
	
	//支付宝
	private BigInteger aliPayCount=new BigInteger("0");
	private BigDecimal aliPayPayAmount=new BigDecimal(0);
	
	//支付宝
	private BigInteger bankCount=new BigInteger("0");
	private BigDecimal bankPayAmount=new BigDecimal(0);
	
	//现金
	private BigInteger cashCount=new BigInteger("0");
	private BigDecimal cashPayAmount=new BigDecimal(0);
	
	//聚合支付
	private BigInteger polyCount=new BigInteger("0");
	private BigDecimal polyPayAmount=new BigDecimal(0);
	
	//其他
	private BigInteger otherCount=new BigInteger("0");
	private BigDecimal otherPayAmount=new BigDecimal(0);
	
	//行合计
	private BigInteger count;
	private BigDecimal payAmount;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}
	public BigInteger getWechatCount() {
		return wechatCount;
	}
	public void setWechatCount(BigInteger wechatCount) {
		this.wechatCount = wechatCount;
	}
	public BigDecimal getWechatPayAmount() {
		return wechatPayAmount;
	}
	public void setWechatPayAmount(BigDecimal wechatPayAmount) {
		this.wechatPayAmount = wechatPayAmount;
	}
	public BigInteger getAliPayCount() {
		return aliPayCount;
	}
	public void setAliPayCount(BigInteger aliPayCount) {
		this.aliPayCount = aliPayCount;
	}
	public BigDecimal getAliPayPayAmount() {
		return aliPayPayAmount;
	}
	public void setAliPayPayAmount(BigDecimal aliPayPayAmount) {
		this.aliPayPayAmount = aliPayPayAmount;
	}
	public BigInteger getBankCount() {
		return bankCount;
	}
	public void setBankCount(BigInteger bankCount) {
		this.bankCount = bankCount;
	}
	public BigDecimal getBankPayAmount() {
		return bankPayAmount;
	}
	public void setBankPayAmount(BigDecimal bankPayAmount) {
		this.bankPayAmount = bankPayAmount;
	}
	public BigInteger getCashCount() {
		return cashCount;
	}
	public void setCashCount(BigInteger cashCount) {
		this.cashCount = cashCount;
	}
	public BigDecimal getCashPayAmount() {
		return cashPayAmount;
	}
	public void setCashPayAmount(BigDecimal cashPayAmount) {
		this.cashPayAmount = cashPayAmount;
	}
	public BigInteger getPolyCount() {
		return polyCount;
	}
	public void setPolyCount(BigInteger polyCount) {
		this.polyCount = polyCount;
	}
	public BigDecimal getPolyPayAmount() {
		return polyPayAmount;
	}
	public void setPolyPayAmount(BigDecimal polyPayAmount) {
		this.polyPayAmount = polyPayAmount;
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
