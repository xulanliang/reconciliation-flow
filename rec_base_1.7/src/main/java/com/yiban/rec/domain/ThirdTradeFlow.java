package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
*<p>文件名称:ThirdTradeFlow.java
*<p>
*<p>文件描述:第三方交易流水
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年6月16日上午11:03:05</p>
*<p>
*@author fangzuxing
 */
@Entity
@Table(name = "t_third_trade_flow")
public class ThirdTradeFlow extends IdEntity{

	private static final long serialVersionUID = 3751208273116741958L;

	//机构编码
	private String orgNo;
	
	//支付商户号
	private String shopNo;
	
	//支付应用id
	private String applyId;
	
	//支付系统响应流水号
	private String paymentFlow;
	
	//支付请求流水号
	private String paymentRequestFlow;
	
	//付款方账号
	private String paymentAccount;
	
	//交易时间
	private Date tradeTime;
	
	//账单日期
	private String tradeDate;
	
	//交易结果
	private String tradeResult;
	
	//交易名称
	private String tradeName;
	
	//交易金额
	private BigDecimal tradeAmount;
	
	//支付系统退费流水号
	private String paymentRefundFlow;
	
	//手续费
	private BigDecimal counterFee;
	
	//支付渠道名称
	private String payName;
	
	//用户id
	private Long userId;
	
	//是否删除
	private Integer isDeleted;
	
	//创建时间
	private Date createdDate;
	

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getPaymentFlow() {
		return paymentFlow;
	}

	public void setPaymentFlow(String paymentFlow) {
		this.paymentFlow = paymentFlow;
	}

	public String getPaymentRequestFlow() {
		return paymentRequestFlow;
	}

	public void setPaymentRequestFlow(String paymentRequestFlow) {
		this.paymentRequestFlow = paymentRequestFlow;
	}

	public String getPaymentAccount() {
		return paymentAccount;
	}

	public void setPaymentAccount(String paymentAccount) {
		this.paymentAccount = paymentAccount;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getTradeResult() {
		return tradeResult;
	}

	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getPaymentRefundFlow() {
		return paymentRefundFlow;
	}

	public void setPaymentRefundFlow(String paymentRefundFlow) {
		this.paymentRefundFlow = paymentRefundFlow;
	}

	public BigDecimal getCounterFee() {
		return counterFee;
	}

	public void setCounterFee(BigDecimal counterFee) {
		this.counterFee = counterFee;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}
	
}
