package com.yiban.rec.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 财务汇总
 * @Author WY
 * @Date 2018年11月14日
 */
@Entity
@Table(name = "t_follow_summary")
public class FollowSummary implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7316266058393504365L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=11)
    private Long id;
	
    /**
	 * 机构编码
	 */
    @Column(length=20)
	private String orgNo;

    /**
     * 账单日期
     */
    @Column(length=20)
	private String tradeDate;
	
	/**
	 * 账单来源   self ：自己      third   第三方
	 */
    @Column(length=20)
	private String billSource;
	
	/**
	 * 数据来源 platform:平台  his:his third:渠道
	 */
    @Column(length=20)
	private String dataSource;
    
    /**
     * 患者类型 mz：门诊 zy：住院
     */
    @Column(length=20)
    private String patType;
    
    /**
     * 支付位置：终端/人工窗口
     */
    @Column(length=20)
    private String payLocation;
    
    /**
     * 支付类型 0049：现金 0149：银行卡，参考支付类型枚举
     */
    @Column(length=20)
    private String recPayType;
    
    /**
     * 详细支付类型 0649：微信扫码支付 参考支付类型枚举
     */
    @Column(length=20)
    private String payType;
    
    /**
     * 金额
     */
    @Column(length=12)
    private BigDecimal payAmount;
    
    /**
     * 支付次数
     */
    @Column(length=12)
    private Integer payAcount;
    
    /**
     * 结算金额
     */
    @Column(length=12)
    private BigDecimal settlementAmount;
    
    
    /**
     * 订单状态:0156-支付，0256-退费
     */
    @Column(length=12)
    private String orderState;
    
    /**
     * 创建时间
     */
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getPatType() {
        return patType;
    }

    public void setPatType(String patType) {
        this.patType = patType;
    }

    public String getPayLocation() {
        return payLocation;
    }

    public void setPayLocation(String payLocation) {
        this.payLocation = payLocation;
    }

    public String getRecPayType() {
        return recPayType;
    }

    public void setRecPayType(String recPayType) {
        this.recPayType = recPayType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getPayAcount() {
        return payAcount;
    }

    public void setPayAcount(Integer payAcount) {
        this.payAcount = payAcount;
    }

    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
