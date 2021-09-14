package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

/**
*<p>文件名称:HisTradeFlow.java
*<p>
*<p>文件描述:异常处理记录
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
@Table(name = "t_exception_handling_record")
public class ExcepHandingRecord extends IdEntity{

	private static final long serialVersionUID = -8610367984251626836L;
	
	//机构编码
	private String orgNo;
	
	//his流水
	private String hisFlow;

	//支付请求流水号
	private String paymentRequestFlow;
	
	//支付系统响应流水号(退款流水号)
	private String paymentFlow;
	
	//支付渠道名称
	private String payName;
	
	//业务类型
	private String businessType;
	
	//交易金额
	private BigDecimal tradeAmount;
	
	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeTime;
	
	//设备编码
	private String equipmentNo;
	
	//病人名称
	private String patientName;
	
	//描述
	private String handleRemark;
	
	//处理时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date handleDateTime;
	
	private String imgUrl;
	
	private Long operationUserId;
	
	private String userName;
	
	private String state;
	
	private String patientNo;
	
	private Long fatherId;
	
	private String billSource;
	
	private String extendArea;
	
	private String refundType;

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

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getHandleRemark() {
		return handleRemark;
	}

	public void setHandleRemark(String handleRemark) {
		this.handleRemark = handleRemark;
	}

	public Date getHandleDateTime() {
		return handleDateTime;
	}

	public void setHandleDateTime(Date handleDateTime) {
		this.handleDateTime = handleDateTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Long getOperationUserId() {
		return operationUserId;
	}

	public void setOperationUserId(Long operationUserId) {
		this.operationUserId = operationUserId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPatientNo() {
		return patientNo;
	}

	public void setPatientNo(String patientNo) {
		this.patientNo = patientNo;
	}

	public Long getFatherId() {
		return fatherId;
	}

	public void setFatherId(Long fatherId) {
		this.fatherId = fatherId;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getExtendArea() {
		return extendArea;
	}

	public void setExtendArea(String extendArea) {
		this.extendArea = extendArea;
	}

	public String getRefundType() {
		return refundType;
	}

	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
}
