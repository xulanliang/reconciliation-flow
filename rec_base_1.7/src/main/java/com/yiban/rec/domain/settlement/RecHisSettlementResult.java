package com.yiban.rec.domain.settlement;

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
 * HIS 结算账单汇总
 * @Author WY
 * @Date 2019年1月10日
 */
@Entity
@Table(name = "t_rec_his_settlement_result")
public class RecHisSettlementResult implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6665793207623838138L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 结账日期（yyyy-MM-dd） */
    @Column(length=64)
    private Date settleDate;
   
    /** 渠道汇总金额 */
    @Column(length=12)
    private BigDecimal channelAmount;
    
    /** HIS汇总金额 */
    @Column(length=12)
    private BigDecimal hisAmount;
    
    /** HIS结算金额 */
    @Column(length=12)
    private BigDecimal hisSettlementAmount;
    
    /** 前一日金额 */
    @Column(length=12)
    private BigDecimal yesterdayAmount;
    
    /** 前一日金额 */
    @Column(length=12)
    private BigDecimal todayUnsettleAmount;
    
    /** 订单来源 */
    @Column(length=16)
    private String billSource;

    /** 机构编码 */
    @Column(length=64)
    private String orgCode;
    
    private BigDecimal beforeSettlementAmount;
    
    private BigDecimal omissionAmount;
    
    public BigDecimal getOmissionAmount() {
		return omissionAmount;
	}

	public void setOmissionAmount(BigDecimal omissionAmount) {
		this.omissionAmount = omissionAmount;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public BigDecimal getHisAmount() {
        return hisAmount;
    }

    public void setHisAmount(BigDecimal hisAmount) {
        this.hisAmount = hisAmount;
    }

    public BigDecimal getHisSettlementAmount() {
        return hisSettlementAmount;
    }

    public void setHisSettlementAmount(BigDecimal hisSettlementAmount) {
        this.hisSettlementAmount = hisSettlementAmount;
    }

    public BigDecimal getYesterdayAmount() {
        return yesterdayAmount;
    }

    public void setYesterdayAmount(BigDecimal yesterdayAmount) {
        this.yesterdayAmount = yesterdayAmount;
    }

    public BigDecimal getTodayUnsettleAmount() {
        return todayUnsettleAmount;
    }

    public void setTodayUnsettleAmount(BigDecimal todayUnsettleAmount) {
        this.todayUnsettleAmount = todayUnsettleAmount;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public BigDecimal getBeforeSettlementAmount() {
		return beforeSettlementAmount;
	}

	public void setBeforeSettlementAmount(BigDecimal beforeSettlementAmount) {
		this.beforeSettlementAmount = beforeSettlementAmount;
	}
}
