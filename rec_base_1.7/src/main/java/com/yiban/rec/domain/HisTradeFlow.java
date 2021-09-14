package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
*<p>文件名称:HisTradeFlow.java
*<p>
*<p>文件描述:his交易流水
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:his交易流水
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年6月16日上午11:03:05</p>
*<p>
*@author fangzuxing
 */
@Entity
@Table(name = "t_his_trade_flow")
public class HisTradeFlow extends IdEntity{

	private static final long serialVersionUID = 6085992428918210513L;
	
	//机构编码
	private String orgNo;
	
	//his流水号
	private String hisFlow;
	
	//支付请求流水号
	private String paymentRequestFlow;

	//支付系统响应流水号
	private String paymentFlow;
	
	//支付渠道名称
	private Integer payName;
	
	//业务类型:挂号，缴费
	private String businessType;
	
	//交易金额
	private BigDecimal tradeAmount;
	
	//交易时间
	private Date tradeTime;
	
	//交易日期
	private String tradeDate;
	
	//设备编号
	private String equipmentNo;
	
	//病人id
	private String patientNo;
	
	//病人名称
	private String patientName;
	
	//用户id
	private Long userId;
	
	//是否删除
	private Integer isDeleted;
	
	//创建时间
	private Date createdDate;
	
	private String extend1;
	
	private String extend2;
	
	private String extend3;
	
	private String extend4;

	
	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getHisFlow() {
		return hisFlow;
	}

	public void setHisFlow(String hisFlow) {
		this.hisFlow = hisFlow;
	}

	public String getPaymentRequestFlow() {
		return paymentRequestFlow;
	}

	public void setPaymentRequestFlow(String paymentRequestFlow) {
		this.paymentRequestFlow = paymentRequestFlow;
	}

	public String getPaymentFlow() {
		return paymentFlow;
	}

	public void setPaymentFlow(String paymentFlow) {
		this.paymentFlow = paymentFlow;
	}

	public Integer getPayName() {
		return payName;
	}

	public void setPayName(Integer payName) {
		this.payName = payName;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getEquipmentNo() {
		return equipmentNo;
	}

	public void setEquipmentNo(String equipmentNo) {
		this.equipmentNo = equipmentNo;
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

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}

	public String getExtend4() {
		return extend4;
	}

	public void setExtend4(String extend4) {
		this.extend4 = extend4;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	
}
