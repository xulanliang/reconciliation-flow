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

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * HIS结算账单明细
 * @Author WY
 * @Date 2019年1月10日
 */
@Entity
@Table(name = "t_rec_his_settlement")
public class RecHisSettlement implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6520690300772617574L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 机构编码 */
    @Column(length=64)
    private String orgCode;
    
    /** HIS流水号 */
    @Column(length=64)
    private String hisOrderNo;
    
    /** 病人ID */
    @Column(length=64)
    private String patientId;
    
    /** 金额（2位小数）退费为负数  缴费为正数 */
    @Column(length=12)
    private BigDecimal amount;
    
    /** 支付类型  微信（0249）或者支付宝（0349） */
    @Column(length=16)
    private String payType;
    
    /** 交易类型  缴费（0156）或者退费（0256） */
    @Column(length=16)
    private String orderType;
    
    /** 交易时间（yyyy-MM-dd HH:mm:ss） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date payTime;
    
    /** 结账时间（yyyy-MM-dd HH:mm:ss） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date settlementTime;
    
    /** 结账日期（yyyy-MM-dd） */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
    private Date settlementDate;
    
    /** 结算人编号 */
    @Column(length=64)
    private String settlementorNum;
    
    /** 第三方业务系统流水号（微信支付宝、银行等支付成功返回的订单号） */
    @Column(length=64)
    private String tnsOrderNo;
    
    /** 账单来源  金蝶（self_td_jd）巨鼎（self）*/
    @Column(length=64)
    private String billSource;
    
    /** 结算批次号 */
    @Column(length=64)
    private String settlementNumber;
    
    // 患者姓名
	@Column(name = "patient_name", length = 64)
	private String patientName;
	
	// 结账序号
	@Column(name = "settlement_serial_no", length = 64)
	private String settlementSerialNo;
	
	// 商户流水号
	@Column(name = "out_trade_no", length = 64)
	private String outTradeNo;

	//支付业务类型
	@Column(name = "pay_business_type", length = 20)
	private String payBusinessType;
	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHisOrderNo() {
        return hisOrderNo;
    }

    public void setHisOrderNo(String hisOrderNo) {
        this.hisOrderNo = hisOrderNo;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettlementorNum() {
        return settlementorNum;
    }

    public void setSettlementorNum(String settlementorNum) {
        this.settlementorNum = settlementorNum;
    }

    public String getTnsOrderNo() {
        return tnsOrderNo;
    }

    public void setTnsOrderNo(String tnsOrderNo) {
        this.tnsOrderNo = tnsOrderNo;
    }

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

    public String getSettlementNumber() {
        return settlementNumber;
    }

    public void setSettlementNumber(String settlementNumber) {
        this.settlementNumber = settlementNumber;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getSettlementSerialNo() {
		return settlementSerialNo;
	}

	public void setSettlementSerialNo(String settlementSerialNo) {
		this.settlementSerialNo = settlementSerialNo;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getPayBusinessType() {
		return payBusinessType;
	}

	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
	}
}
