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
 * 支付结果上送
 * @Author WY
 * @Date 2018年10月25日
 */
@Entity
@Table(name = "t_order_upload",
    indexes= {
        @Index(name="orgCodeIndex", columnList = "orgCode", unique=false),
        @Index(name="hisOrderNoIndex", columnList = "hisOrderNo", unique=false)
})
public class OrderUpload implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4322671030095753359L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length=20)
    private Long id; 
    
    /**
     * 机构编码
     */
    @NotBlank(message="机构编码不能为空")
    @Column(length=32)
    private String orgCode;
    
    /**
     * 业务系统订单号
     */
    @NotBlank(message="业务系统订单号不能为空")
    @Column(length=64)
    private String outTradeNo;
    
    /**
     * 第三方订单号（微信支付宝）
     */
    @NotBlank(message="第三方订单号不能为空")
    @Column(length=32)
    private String tsnOrderNo;
    
    /**
     * 医院His系统订单号
     */
    @Column(length=64)
    private String hisOrderNo;
    
    /**
     * 订单号【支付平台】
     */
    @Column(length=64)
    private String orderNo;
	
	/**
	 * 医保流水号
	 * 结算方式非自费时节点必传
	 */
	@Column(length=64)
	private String ybSerialNo;
	
	/**
	 * 医保结算单据号
	 * 结算方式非自费时节点必传
	 */
	@Column(length=64)
	private String ybBillNo;
	
	/**
	 * 医疗总费用
	 * 医疗总费用=记账金额+自费支付金额
	 */
	@NotNull(message="订单总金额不能为空")
	@Column(length=12)
	private BigDecimal payTotalAmount;
	
	/**
	 * 自费支付金额（单位元，保留2位小数）
	 */
	@Column(length=12)
	private BigDecimal payAmount;
	
	/**
	 * 记账合计金额（单位元，保留2位小数）
	 * 结算方式非自费时节点必传
	 */
	@Column(length=12)
	private BigDecimal ybPayAmount;
	   
    /**
     * 订单状态
     * 已驳回 1809305
     * 已退款  1809304
     * 支付完成 1809302
     * 审核中  1809303
     * 交易异常 1809300
     */
    @Column(length=20)
    private String orderState;
    
    /**
     * 订单状态描述
     */
    @Column(length=512)
    private String orderStateRemark;
	
	/**
	 * 交易时间 
	 */
	@NotNull(message="交易时间不能为空")
	@Column(length=20)
	private String tradeDateTime;
	
	/**
	 * 结算方式 
	 * 0031自费
	 * 0131医保
	 * 0231公费
	 * 0331农村合作医疗
	 */
	@Column(length=12)
	@NotNull(message="结算方式不能为空")
	private String settlementType;
	
	/**
	 * 交易日期
	 */
	@Column(length=10)
	private String tradeDate;
	
	
	/**
	 * 支付类型
	 * 0049 现金 、0149 银行卡、0249 微信 、0349 支付宝 、0449 医保、0549 网银、0649 聚合支付、0749 支票、9949 其他
	 */
	@NotBlank(message="支付类型不能为空")
	@Column(length=8)
	private String payType;
	
	/**
	 * 支付业务类型
	 * 0051 未知 、0151 充值 、0251 办卡 、0351 补卡 、0451 挂号 、0551 缴费 
	 */
	@NotBlank(message="支付业务类型不能为空")
	@Column(length=8)
	private String payBusinessType;
	
	/**
	 * 患者类型（mz:门诊，zy:住院，qt:其他）
	 */
	@NotBlank(message="患者类型不能为空")
	@Column(length=8)
	private String patType;
	
	/**
	 * 账单来源self:银医,self_jd:巨鼎,third:第三方
	 */
	@NotBlank(message="账单来源不能为空")
	@Column(length=16)
	private String billSource;
	
	/**
	 * 患者就诊卡号
	 */
	@NotBlank(message="患者就诊卡号不能为空")
	@Column(length=64)
	private String patientCardNo;
	   
    /**
     * 患者姓名
     */
    @NotBlank(message="患者姓名不能为空")
    @Column(length=128)
    private String patientName;
    
    /**
     * 收费员/设备编码 HIS系统分配
     */
    @NotBlank(message="收费员/设备编码不能为空")
    @Column(length=64)
    private String cashier;
    
    /**
     * 设备编码
     */
    @Column(length=2048)
    private String goodInfo;

    /**
     * 发票号
     */
    @Column(length=50)
    private String invoiceNo;
    
    /**
     * 支付位置：0001 自助机，0002窗口
     */
    @Column(length=20, columnDefinition="varchar(20) default '0001'")
    private String payLocation = "0001";
    
    /**
     * 退款状态
     */
    @Column(length=20)
    private String refundOrderState;
    
    /**
     * 扩展字段
     */
    @Column(length=1024)
    private String extendArea;
    
    /**
     * 就诊记录号
     * @return
     */
    @Column(length=64)
    private String recordNumber;
    
    /**
     * 终端号
     * @return
     */
    @Column(length=64)
    private String terminalNumber;
    
    /**
     * 可退款金额
     * @return
     */
    @Column(length=12)
    private BigDecimal returnableAmount;

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTsnOrderNo() {
        return tsnOrderNo;
    }

    public void setTsnOrderNo(String tsnOrderNo) {
        this.tsnOrderNo = tsnOrderNo;
    }

    public String getHisOrderNo() {
        return hisOrderNo;
    }

    public void setHisOrderNo(String hisOrderNo) {
        this.hisOrderNo = hisOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public BigDecimal getPayTotalAmount() {
        return payTotalAmount;
    }

    public void setPayTotalAmount(BigDecimal payTotalAmount) {
        this.payTotalAmount = payTotalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getYbPayAmount() {
        return ybPayAmount;
    }

    public void setYbPayAmount(BigDecimal ybPayAmount) {
        this.ybPayAmount = ybPayAmount;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getOrderStateRemark() {
        return orderStateRemark;
    }

    public void setOrderStateRemark(String orderStateRemark) {
        this.orderStateRemark = orderStateRemark;
    }

    public String getTradeDateTime() {
        return tradeDateTime;
    }

    public void setTradeDateTime(String tradeDateTime) {
        this.tradeDateTime = tradeDateTime;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

    public String getPatientCardNo() {
        return patientCardNo;
    }

    public void setPatientCardNo(String patientCardNo) {
        this.patientCardNo = patientCardNo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getGoodInfo() {
        return goodInfo;
    }

    public void setGoodInfo(String goodInfo) {
        this.goodInfo = goodInfo;
    }

    public String getPayLocation() {
        return payLocation;
    }

    public void setPayLocation(String payLocation) {
        this.payLocation = payLocation;
    }

    public String getRefundOrderState() {
        return refundOrderState;
    }

    public void setRefundOrderState(String refundOrderState) {
        this.refundOrderState = refundOrderState;
    }

    public String getExtendArea() {
        return extendArea;
    }

    public void setExtendArea(String extendArea) {
        this.extendArea = extendArea;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

	public String getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getTerminalNumber() {
		return terminalNumber;
	}

	public void setTerminalNumber(String terminalNumber) {
		this.terminalNumber = terminalNumber;
	}

	public BigDecimal getReturnableAmount() {
		return returnableAmount;
	}

	public void setReturnableAmount(BigDecimal returnableAmount) {
		this.returnableAmount = returnableAmount;
	}
}
