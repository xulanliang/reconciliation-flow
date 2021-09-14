package com.yiban.rec.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.yiban.rec.domain.MixRefundDetails;


public class AllRefundVo implements Serializable {
	
	private static final long serialVersionUID = -8137289935342647727L;

	@NotBlank(message="机构编码不能为空")
    @Column(length=32, nullable=false)
	private String orgCode;
	
	@Transient
	private List<MixRefundDetails> orderItems;
    
    @NotBlank(message="结算方式不能为空")
    @Column(length=4)
    private String settlementType;
    
    @NotBlank(message="退款业务订单号不能为空")
    @Column(length=64)
    private String refundOrderNo;
    
    @NotBlank(message="退费时间不能为空")
    @Column(length=20)
    private String refundDateTime;
    
    @NotNull(message="退款金额不能为空")
    @Column(length=12)
    private BigDecimal refundAmount;
    
    
    @Column(length=12)
    private BigDecimal ybPayAmount;
    
    
    @Column(length=64)
    private String ybSerialNo;
    
    
    @Column(length=64)
    private String ybBillNo;
    
    @NotBlank(message="业务类型不能为空")
    @Column(length=4)
    private String payBusinessType;
    
    @NotBlank(message="患者类型不能为空")
    @Column(length=8)
    private String patType;
    
    @NotBlank(message="收费员/设备编码不能为空")
    @Column(length=64)
    private String cashier;
    
    
    @NotBlank(message="退款策略不能为空")
    @Column(length=2)
    private String refundStrategy;


    @Column(length=512)
    private String refundReason;


	public String getOrgCode() {
		return orgCode;
	}


	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}



	public List<MixRefundDetails> getOrderItems() {
		return orderItems;
	}


	public void setOrderItems(List<MixRefundDetails> orderItems) {
		this.orderItems = orderItems;
	}


	public String getSettlementType() {
		return settlementType;
	}


	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}


	public String getRefundOrderNo() {
		return refundOrderNo;
	}


	public void setRefundOrderNo(String refundOrderNo) {
		this.refundOrderNo = refundOrderNo;
	}


	public String getRefundDateTime() {
		return refundDateTime;
	}


	public void setRefundDateTime(String refundDateTime) {
		this.refundDateTime = refundDateTime;
	}


	public BigDecimal getRefundAmount() {
		return refundAmount;
	}


	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}


	public BigDecimal getYbPayAmount() {
		return ybPayAmount;
	}


	public void setYbPayAmount(BigDecimal ybPayAmount) {
		this.ybPayAmount = ybPayAmount;
	}


	public String getYbSerialNo() {
		return ybSerialNo;
	}


	public void setYbSerialNo(String ybSerialNo) {
		this.ybSerialNo = ybSerialNo;
	}


	public String getYbBillNo() {
		return ybBillNo;
	}


	public void setYbBillNo(String ybBillNo) {
		this.ybBillNo = ybBillNo;
	}


	public String getPayBusinessType() {
		return payBusinessType;
	}


	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
	}


	public String getPatType() {
		return patType;
	}


	public void setPatType(String patType) {
		this.patType = patType;
	}


	public String getCashier() {
		return cashier;
	}


	public void setCashier(String cashier) {
		this.cashier = cashier;
	}


	public String getRefundStrategy() {
		return refundStrategy;
	}


	public void setRefundStrategy(String refundStrategy) {
		this.refundStrategy = refundStrategy;
	}


	public String getRefundReason() {
		return refundReason;
	}


	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
    
    
}
