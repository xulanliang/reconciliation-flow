package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * <p>
 * 文件名称:FollowRecResult.java
 * <p>
 * <p>
 * 文件描述:隔日对账结果表
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年6月16日上午11:03:05
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
@Entity
@Table(name = "t_follow_rec_result")
public class FollowRecResult extends IdEntity {

	private static final long serialVersionUID = 977119549097313578L;

	// 机构编码
	private String orgNo;

	// 账单日起
	private String tradeDate;

	// his交易金额
	private BigDecimal hisAllAmount;

	// 支付系统交易金额
	private BigDecimal payAllAmount;
	// 平台交易金额
	private BigDecimal recPayAllAmount;

	// 交易差额
	private BigDecimal tradeDiffAmount;

	// 处理金额
	private BigDecimal handlerDiffAmount;

	// 处理结果
	private String exceptionResult;

	// 创建时间
	private Date createDate;

	// 门诊/住院
	private String patType;

	// 账单来源
	private String billSource;

	// 财务结算金额
	private BigDecimal settlementAmount;

	// 支付宝交易金额
	private BigDecimal alipayAllAmount;

	// 微信交易金额
	private BigDecimal wechatAllAmount;

	// 银行交易金额
	private BigDecimal bankAllAmount;

	// 现金交易金额
	private BigDecimal cashAllAmount;

	// 医保交易金额
	private BigDecimal socialInsuranceAmount;

	// his应收 笔数
	@Transient
	private Integer hisPayAcount;
	// his退款笔数
	@Transient
	private Integer hisRefundAcount;
	// 实收 笔数
	@Transient
	private Integer payAcount;
	// 实收 退款笔数
	@Transient
	private Integer refundAcount;
	// his实收 笔数
	@Transient
	private Integer settlementPayAcount;
	// 差异 笔数
	@Transient
	private Integer tradeDiffPayAcount;
	// 单边账长款笔数
	@Transient
	private Integer untreatedThirdAcount;
	// 单边账短款笔数
	@Transient
	private Integer untreatedHisAcount;
	@Transient
	private BigDecimal untreatedThirdAmount;
	// 单边账短款金额
	@Transient
	private BigDecimal untreatedHisAmount;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public BigDecimal getHisAllAmount() {
		return hisAllAmount;
	}

	public void setHisAllAmount(BigDecimal hisAllAmount) {
		this.hisAllAmount = hisAllAmount;
	}

	public BigDecimal getPayAllAmount() {
		return payAllAmount;
	}

	public void setPayAllAmount(BigDecimal payAllAmount) {
		this.payAllAmount = payAllAmount;
	}

	public BigDecimal getTradeDiffAmount() {
		return tradeDiffAmount;
	}

	public void setTradeDiffAmount(BigDecimal tradeDiffAmount) {
		this.tradeDiffAmount = tradeDiffAmount;
	}

	public BigDecimal getHandlerDiffAmount() {
		return handlerDiffAmount;
	}

	public void setHandlerDiffAmount(BigDecimal handlerDiffAmount) {
		this.handlerDiffAmount = handlerDiffAmount;
	}

	public String getExceptionResult() {
		return exceptionResult;
	}

	public void setExceptionResult(String exceptionResult) {
		this.exceptionResult = exceptionResult;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public BigDecimal getRecPayAllAmount() {
		return recPayAllAmount;
	}

	public void setRecPayAllAmount(BigDecimal recPayAllAmount) {
		this.recPayAllAmount = recPayAllAmount;
	}

	public BigDecimal getSettlementAmount() {
		return settlementAmount;
	}

	public void setSettlementAmount(BigDecimal settlementAmount) {
		this.settlementAmount = settlementAmount;
	}

	public BigDecimal getWechatAllAmount() {
		return wechatAllAmount;
	}

	public void setWechatAllAmount(BigDecimal wechatAllAmount) {
		this.wechatAllAmount = wechatAllAmount;
	}

	public BigDecimal getBankAllAmount() {
		return bankAllAmount;
	}

	public void setBankAllAmount(BigDecimal bankAllAmount) {
		this.bankAllAmount = bankAllAmount;
	}

	public BigDecimal getCashAllAmount() {
		return cashAllAmount;
	}

	public void setCashAllAmount(BigDecimal cashAllAmount) {
		this.cashAllAmount = cashAllAmount;
	}

	public BigDecimal getSocialInsuranceAmount() {
		return socialInsuranceAmount;
	}

	public void setSocialInsuranceAmount(BigDecimal socialInsuranceAmount) {
		this.socialInsuranceAmount = socialInsuranceAmount;
	}

	public BigDecimal getAlipayAllAmount() {
		return alipayAllAmount;
	}

	public void setAlipayAllAmount(BigDecimal alipayAllAmount) {
		this.alipayAllAmount = alipayAllAmount;
	}

	public Integer getPayAcount() {
		return payAcount;
	}

	public void setPayAcount(Integer payAcount) {
		this.payAcount = payAcount;
	}

	public Integer getHisPayAcount() {
		return hisPayAcount;
	}

	public void setHisPayAcount(Integer hisPayAcount) {
		this.hisPayAcount = hisPayAcount;
	}

	public Integer getSettlementPayAcount() {
		return settlementPayAcount;
	}

	public void setSettlementPayAcount(Integer settlementPayAcount) {
		this.settlementPayAcount = settlementPayAcount;
	}

	public Integer getTradeDiffPayAcount() {
		return tradeDiffPayAcount;
	}

	public void setTradeDiffPayAcount(Integer tradeDiffPayAcount) {
		this.tradeDiffPayAcount = tradeDiffPayAcount;
	}

	public Integer getHisRefundAcount() {
		return hisRefundAcount;
	}

	public void setHisRefundAcount(Integer hisRefundAcount) {
		this.hisRefundAcount = hisRefundAcount;
	}

	public Integer getRefundAcount() {
		return refundAcount;
	}

	public void setRefundAcount(Integer refundAcount) {
		this.refundAcount = refundAcount;
	}

	public Integer getUntreatedHisAcount() {
		return untreatedHisAcount;
	}

	public void setUntreatedHisAcount(Integer untreatedHisAcount) {
		this.untreatedHisAcount = untreatedHisAcount;
	}

	public BigDecimal getUntreatedHisAmount() {
		return untreatedHisAmount;
	}

	public void setUntreatedHisAmount(BigDecimal untreatedHisAmount) {
		this.untreatedHisAmount = untreatedHisAmount;
	}

	public Integer getUntreatedThirdAcount() {
		return untreatedThirdAcount;
	}

	public void setUntreatedThirdAcount(Integer untreatedThirdAcount) {
		this.untreatedThirdAcount = untreatedThirdAcount;
	}

	public BigDecimal getUntreatedThirdAmount() {
		return untreatedThirdAmount;
	}

	public void setUntreatedThirdAmount(BigDecimal untreatedThirdAmount) {
		this.untreatedThirdAmount = untreatedThirdAmount;
	}
}
