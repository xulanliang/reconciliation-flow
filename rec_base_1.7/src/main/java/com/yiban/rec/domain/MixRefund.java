package com.yiban.rec.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;


@Entity
@Table(name = "t_mix_refund",
    indexes= {
        @Index(name="refundOrderNoUniqueIndex", columnList = "refundOrderNo", unique=true),
        @Index(name="orgCodeIndex", columnList = "orgCode", unique=false),
        @Index(name="refundDateIndex", columnList = "refundDateTime", unique=false)
})
public class MixRefund implements Serializable {

	private static final long serialVersionUID = -6978056281539335614L;

	
	 /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=20)
    private Long id; 
    
    /** 机构编码 */
    @Column(length=32)
    @NotBlank(message="机构编码不能为空")
    private String orgCode;
    
    /** 结算方式:0031-自费,0131-医保,0231-公费,0331-农村合作医疗 */
    @Column(length=8)
    private String settlementType;
    
    /** 退费单号 */
    @Column(length=64)
    private String refundOrderNo;
    
    /**
	 * 退费金额
	 */
	@Column(length=12)
	private BigDecimal refundAmount;
	
	/**
	 * 退费时间(格式yyyy-MM-dd HH:mm:ss)
	 */
	@Column(length=12)
	private String refundDateTime;
	
	
	/**
	 * 记账合计金额（单位元，保留2位小数）
	 */
	@Column(length=12)
	private BigDecimal ybPayAmount;
	
	
	/**
	 * 医保流水号
	 */
	@Column(length=64)
	private String ybSerialNo;
	
	/**
	 * 医保结算单据号
	 */
	@Column(length=64)
	private String ybBillNo;
	
	
	/**
	 * 业务类型:0051-未知,0151-充值,0251-办卡,0351-补卡,0451-挂号,0551-缴费
	 */
	@Column(length=8)
	private String payBusinessType;
	
	
	/**
	 * 患者类型（mz:门诊，zy:住院，qt:其他）
	 */
	@Column(length=8)
	private String patType;
	
	
	/**
	 * 收费员/设备编码
	 */
	@Column(length=64)
	private String cashier; 
	
	/**
	 * 退款策略：01先进先出  02先大后小(默认)
	 */
	@Column(length=64)
	private String refundStrategy; 
	
	
	/**
	 * 退费原因
	 */
	@Column(length=64)
	private String refundReason;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getOrgCode() {
		return orgCode;
	}


	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
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


	public BigDecimal getRefundAmount() {
		return refundAmount;
	}


	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}


	public String getRefundDateTime() {
		return refundDateTime;
	}


	public void setRefundDateTime(String refundDateTime) {
		this.refundDateTime = refundDateTime;
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
