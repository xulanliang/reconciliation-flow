package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;


/**
*<p>文件名称:ThirdTradeFlow.java
*<p>
*<p>文件描述:交易明细校验
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
@Table(name = "t_trade_check")
public class TradeCheck extends IdEntity{

	private static final long serialVersionUID = 2385763595070122975L;

	//机构编码
	private String orgNo;
	
	//支付商户流水号
	private String businessNo;
	
	//支付系统流水号
	private String payNo;
	
	//支付商户号
	private String shopNo;
	
	//支付应用id
	private String applyId;
	
	//his流水号
	private String hisFlowNo;
	
	//支付渠道名称
	private String payName;
	
	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeTime;
	
	//交易日期
	private String tradeDate;
	
	//付款方账号
	private String paymentAccount;
	
	//交易结果
	private String tradeResult;
	
	//交易名称
	private String tradeName;
	
	//交易金额
	private BigDecimal tradeAmount;
	
	//支付商户退费流水号
	private String paymentRefundFlow;
	
	//设备编码
	private String equipmentNo;
	
	//患者编码
	private String patientNo;
	
	//患者名称
	private String patientName;
	
	//是否删除
	private Integer isDeleted;
	
	//创建时间
	private Date createdDate;
	
	//状态
	private Integer checkState;
	
	@Transient
	private String checkStateValue;
	
	//用户id
	private Long userId;
	
	

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getPayNo() {
		return payNo;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getPaymentAccount() {
		return paymentAccount;
	}

	public void setPaymentAccount(String paymentAccount) {
		this.paymentAccount = paymentAccount;
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

	public String getEquipmentNo() {
		return equipmentNo;
	}

	public void setEquipmentNo(String equipmentNo) {
		this.equipmentNo = equipmentNo;
	}

	public String getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
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

	public String getHisFlowNo() {
		return hisFlowNo;
	}

	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}

	public Integer getCheckState() {
		return checkState;
	}

	public void setCheckState(Integer checkState) {
		this.checkState = checkState;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getCheckStateValue() {
		return checkStateValue;
	}

	public void setCheckStateValue(String checkStateValue) {
		this.checkStateValue = checkStateValue;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
}
