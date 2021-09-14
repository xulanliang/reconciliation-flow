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
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 退款结果上送
 * @Author WY
 * @Date 2018年10月25日
 */
@Entity
@Table(name = "t_refund_upload",
    indexes= {
        @Index(name="refundOrderNoIndex", columnList = "refundOrderNo", unique=true),
        @Index(name="oriTsnOrderNoIndex", columnList = "oriTsnOrderNo", unique=false)
})
public class RefundUpload implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6186350908288039297L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=20)
    private Long id; 
    
    /**
     * 机构编码
     */
    @Column(length=32)
    @NotBlank(message="机构编码不能为空")
    private String orgCode;
    
    /**
     * 退款流水号
     */
    @Column(length=64)
    @NotBlank(message="退款流水号不能为空")
    private String refundOrderNo;
    
    /**
     * 原第三方业务系统流水号
     */
    @NotBlank(message="原第三方业务系统流水号不能为空")
    @Column(length=32)
    private String oriTsnOrderNo;
	
	/**
	 * 退款金额
	 */
	@Column(length=12)
	private BigDecimal refundAmount;
	
	/**
	 * 退款日期时间
	 */
	@NotNull(message="退款日期时间不能为空")
	@Column(length=20)
	private String refundDateTime;
	
	/**
	 * 退款日期时间
	 */
	@Column(length=10)
	private String refundDate;
    
    /**
     * 操作员
     */
    @NotBlank(message="操作员不能为空")
    @Column(length=64)
    private String cashier;

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

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public void setRefundOrderNo(String refundOrderNo) {
        this.refundOrderNo = refundOrderNo;
    }

    public String getOriTsnOrderNo() {
        return oriTsnOrderNo;
    }

    public void setOriTsnOrderNo(String oriTsnOrderNo) {
        this.oriTsnOrderNo = oriTsnOrderNo;
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

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }
}
