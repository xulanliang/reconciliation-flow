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



@Entity
@Table(name = "t_mix_refund_details",
    indexes= {
        @Index(name="refundOrderNoIndex", columnList = "refundOrderNo", unique=false)
})
public class MixRefundDetails implements Serializable {

	private static final long serialVersionUID = -1648427215222922488L;

	
	 /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=20)
    private Long id;
    
    /** 第三方业务系统流水号（微信支付宝、银行等支付成功返回的订单号）*/
    @Column(length=64)
    private String tsnOrderNo;
    
    /** 退费单号 */
    @Column(length=64)
    private String refundOrderNo;
    
    /** 医院His系统订单号 */
    @Column(length=64)
    private String hisOrderNo;
    
    
    /** 支付订单金额（单位元，保留2位小数） */
    @Column(length=12)
    private BigDecimal payAmount;
    
    /** 退款金额（单位元，保留2位小数） */
    @Column(length=12)
    private BigDecimal refundAmount;
    
    
    /** 支付时间，格式yyyy-MM-dd HH:mm:ss */
    @Column(length=20)
    private String payDateTime;
    
    
    /** 支付类型：0049-现金 ，0149-银行卡，0249-微信 ，0349-支付宝 ，0449-医保，0549-网银，0649-聚合支付，0749-支票，9949-其他 */
    @Column(length=8)
    private String payType;
    
    
    /** 账单来源：0030-建行自助（巨鼎-柯丽尔），0130-PAJK（平安好医生），0230-掌上医院（医享网-微信公众号）
     * ，0330-医保支付（云医支付宝-支付宝生活号）
     * ，0430-自助挂号（中行自助机），0530-宁远科技（就医160），0630-健康深圳
     * ，0830-HIS窗口' */
    @Column(length=8)
    private String billSource;
    
    
    /** 退款状态：0-未退费，1-退费成功，2-退费失败 */
    @Column(length=8)
    private int refundState;
    
    
    /** 退款状态描述（失败原因） */
    @Column(length=2048)
    private String refundStateInfo;
    
    /** 重试次数 */
    @Column(length=4)
    private int retryTimes;
    
    /** 下次重试时间 */
    @Column(length=20)
    private String nextTime;
    
    /** 退费次数 */
    @Column(length=2)
    private int refundCount;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public String getTsnOrderNo() {
		return tsnOrderNo;
	}


	public void setTsnOrderNo(String tsnOrderNo) {
		this.tsnOrderNo = tsnOrderNo;
	}


	public String getRefundOrderNo() {
		return refundOrderNo;
	}


	public void setRefundOrderNo(String refundOrderNo) {
		this.refundOrderNo = refundOrderNo;
	}


	public String getHisOrderNo() {
		return hisOrderNo;
	}


	public void setHisOrderNo(String hisOrderNo) {
		this.hisOrderNo = hisOrderNo;
	}


	public BigDecimal getPayAmount() {
		return payAmount;
	}


	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}


	public String getPayDateTime() {
		return payDateTime;
	}


	public void setPayDateTime(String payDateTime) {
		this.payDateTime = payDateTime;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}


	public String getBillSource() {
		return billSource;
	}


	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}


	public int getRefundState() {
		return refundState;
	}


	public void setRefundState(int refundState) {
		this.refundState = refundState;
	}


	public String getRefundStateInfo() {
		return refundStateInfo;
	}


	public void setRefundStateInfo(String refundStateInfo) {
		this.refundStateInfo = refundStateInfo;
	}


	public BigDecimal getRefundAmount() {
		return refundAmount;
	}


	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}


	public int getRetryTimes() {
		return retryTimes;
	}


	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}


	public String getNextTime() {
		return nextTime;
	}


	public void setNextTime(String nextTime) {
		this.nextTime = nextTime;
	}


	public int getRefundCount() {
		return refundCount;
	}


	public void setRefundCount(int refundCount) {
		this.refundCount = refundCount;
	}

    
}
