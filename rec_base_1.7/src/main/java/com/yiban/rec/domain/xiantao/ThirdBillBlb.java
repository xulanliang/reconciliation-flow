package com.yiban.rec.domain.xiantao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.rec.domain.ThirdBill;

/**
 * 第三方支付渠道流水
 */
@Entity
@Table(name = "t_thrid_bill_blb")
public class ThirdBillBlb extends IdEntity {

    private static final long serialVersionUID = -1259836967637211668L;
    // 机构编码
    private String orgNo;

    // 机构名称
    @Transient
    private String orgName;

    // 支付来源
    private String paySource;

    // 支付类型
    private String payType;

    // 对账支付类型
    private String recPayType;

    // 支付类型名称
    @Transient
    private String payTypeName;

    // 支付商户号
    private String payShopNo;

    // 支付来源名称
    @Transient
    private String paySourceName;

    // 客户名称
    private String custName;
    // 患者就诊卡号
    private String patientCardNo;
    // 卡类型
    private String cardType;
    // 客户标识
    @Transient
    private String custIdentifyType;
    // 患者就诊号
    @Transient
    private String visitNumbe;

    // 支付终端号
    private String payTermNo;

    // 支付商户批次号
    private String payBatchNo;

    // 支付商户流水号
    private String payFlowNo;

    // 支付账号
    private String payAccount;

    // 支付金额
    private BigDecimal payAmount;

    // 交易时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date tradeDatatime;

    // 银行卡-授权码
    private String authoriCode;

    // 订单状态
    private String orderState;

    // 订单状态值
    @Transient
    private String orderStateName;

    // 原支付流水号
    private String oriPayFlowNo;

    // 更新人
    private String updatedBy;

    // 更新时间
    private Date updatedTime;

    // 创建时间
    private Date createdDate = new Date();

    // 是否删除
    private Integer isDeleted = DeleteEnum.NO.getValue();

    // 是否有效
    private Integer isActived = ActiveEnum.YES.getValue();

    //支付业务类型
    private String PayBusinessType;

    //退款状态
    private String refundState;

    //商户订单号
    private String shopFlowNo;

    //账单来源
    private String billSource = "self";

    //平台订单号
    private String orderNo;

    //业务系统订单号
    private String outTradeNo;

    //账单文件id
    private String fileId;
    //门诊/住院
    private String patType;
    // 发票号
    private String invoiceNo;
    //参考号
    private String referenceNum;
    //商户流水号
//	private String businessFlowNo;
    private Integer requireRefund=1; //是否能退款，0否，1是

    // 线上支付- 支付类型
    private String unionPayType;
    // 线上支付-聚合支付标识
    private String unionPayCode;
    // 线上支付-系统编码
    private String unionSystemCode;

    public ThirdBillBlb() {
    }

    public ThirdBillBlb(ThirdBill bill) {
        this.orgNo = bill.getOrgNo();
        this.orgName = bill.getOrgName();
        this.paySource = bill.getPaySource();
        this.payType = bill.getPayType();
        this.recPayType = bill.getRecPayType();
        this.payTypeName = bill.getPayTypeName();
        this.payShopNo = bill.getPayShopNo();
        this.paySourceName = bill.getPaySourceName();
        this.custName = bill.getCustName();
        this.patientCardNo = bill.getPatientCardNo();
        this.cardType = bill.getCardType();
        this.custIdentifyType = bill.getCustIdentifyType();
        this.visitNumbe = bill.getVisitNumbe();
        this.payTermNo = bill.getPayTermNo();
        this.payBatchNo = bill.getPayBatchNo();
        this.payFlowNo = bill.getPayFlowNo();
        this.payAccount = bill.getPayAccount();
        this.payAmount = bill.getPayAmount();
        this.tradeDatatime = bill.getTradeDatatime();
        this.authoriCode = bill.getAuthoriCode();
        this.orderState = bill.getOrderState();
        this.orderStateName = bill.getOrderStateName();
        this.oriPayFlowNo = bill.getOriPayFlowNo();
        this.updatedBy = bill.getUpdatedBy();
        this.updatedTime = bill.getUpdatedTime();
        this.createdDate = bill.getCreatedDate();
        this.isDeleted = bill.getIsDeleted();
        this.isActived = bill.getIsActived();
        this.PayBusinessType = bill.getPayBusinessType();
        this.refundState = bill.getRefundState();
        this.shopFlowNo = bill.getShopFlowNo();
        this.billSource = bill.getBillSource();
        this.orderNo = bill.getOrderNo();
        this.outTradeNo = bill.getOutTradeNo();
        this.fileId = bill.getFileId();
        this.patType = bill.getPatType();
        this.invoiceNo = bill.getInvoiceNo();
        this.referenceNum = bill.getReferenceNum();
        this.requireRefund = bill.getRequireRefund();
        this.unionPayType = bill.getUnionPayType();
        this.unionPayCode = bill.getUnionPayCode();
        this.unionSystemCode = bill.getUnionSystemCode();
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsActived() {
        return isActived;
    }

    public void setIsActived(Integer isActived) {
        this.isActived = isActived;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayShopNo() {
        return payShopNo;
    }

    public void setPayShopNo(String payShopNo) {
        this.payShopNo = payShopNo;
    }

    public String getPayTermNo() {
        return payTermNo;
    }

    public void setPayTermNo(String payTermNo) {
        this.payTermNo = payTermNo;
    }

    public String getPayBatchNo() {
        return payBatchNo;
    }

    public void setPayBatchNo(String payBatchNo) {
        this.payBatchNo = payBatchNo;
    }

    public String getPayFlowNo() {
        return payFlowNo;
    }

    public void setPayFlowNo(String payFlowNo) {
        this.payFlowNo = payFlowNo;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getOriPayFlowNo() {
        return oriPayFlowNo;
    }

    public void setOriPayFlowNo(String oriPayFlowNo) {
        this.oriPayFlowNo = oriPayFlowNo;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getTradeDatatime() {
        return tradeDatatime;
    }

    public void setTradeDatatime(Date tradeDatatime) {
        this.tradeDatatime = tradeDatatime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPayTypeName() {
        return payTypeName;
    }

    public void setPayTypeName(String payTypeName) {
        this.payTypeName = payTypeName;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getPaySourceName() {
        return paySourceName;
    }

    public void setPaySourceName(String paySourceName) {
        this.paySourceName = paySourceName;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustIdentifyType() {
        return custIdentifyType;
    }

    public void setCustIdentifyType(String custIdentifyType) {
        this.custIdentifyType = custIdentifyType;
    }

    public String getOrderStateName() {
        return orderStateName;
    }

    public void setOrderStateName(String orderStateName) {
        this.orderStateName = orderStateName;
    }

    public String getPayBusinessType() {
        return PayBusinessType;
    }

    public void setPayBusinessType(String payBusinessType) {
        PayBusinessType = payBusinessType;
    }

    public String getRefundState() {
        return refundState;
    }

    public void setRefundState(String refundState) {
        this.refundState = refundState;
    }

    public String getRecPayType() {
        return recPayType;
    }

    public void setRecPayType(String recPayType) {
        this.recPayType = recPayType;
    }

    public String getShopFlowNo() {
        return shopFlowNo;
    }

    public void setShopFlowNo(String shopFlowNo) {
        this.shopFlowNo = shopFlowNo;
    }

    public String getBillSource() {
        return billSource;
    }

    public void setBillSource(String billSource) {
        this.billSource = billSource;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getPatType() {
        return patType;
    }

    public void setPatType(String patType) {
        this.patType = patType;
    }

    public String getPatientCardNo() {
        return patientCardNo;
    }

    public void setPatientCardNo(String patientCardNo) {
        this.patientCardNo = patientCardNo;
    }

    public String getReferenceNum() {
        return referenceNum;
    }

    public void setReferenceNum(String referenceNum) {
        this.referenceNum = referenceNum;
    }

    public Integer getRequireRefund() {
        return requireRefund;
    }

    public void setRequireRefund(Integer requireRefund) {
        this.requireRefund = requireRefund;
    }

    public String getAuthoriCode() {
        return authoriCode;
    }

    public void setAuthoriCode(String authoriCode) {
        this.authoriCode = authoriCode;
    }

    public String getVisitNumbe() {
        return visitNumbe;
    }

    public void setVisitNumbe(String visitNumbe) {
        this.visitNumbe = visitNumbe;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getUnionPayType() {
        return unionPayType;
    }

    public void setUnionPayType(String unionPayType) {
        this.unionPayType = unionPayType;
    }

    public String getUnionPayCode() {
        return unionPayCode;
    }

    public void setUnionPayCode(String unionPayCode) {
        this.unionPayCode = unionPayCode;
    }

    public String getUnionSystemCode() {
        return unionSystemCode;
    }

    public void setUnionSystemCode(String unionSystemCode) {
        this.unionSystemCode = unionSystemCode;
    }

    @Override
    public String toString() {
        return "ThirdBillBlb [orgNo=" + orgNo + ", orgName=" + orgName + ", payType=" + payType + ", recPayType="
                + recPayType + ", payShopNo=" + payShopNo + ", custName=" + custName + ", patientCardNo="
                + patientCardNo + ", cardType=" + cardType + ", payFlowNo=" + payFlowNo + ", payAmount=" + payAmount
                + ", tradeDatatime=" + tradeDatatime + ", orderState=" + orderState + ", PayBusinessType="
                + PayBusinessType + ", refundState=" + refundState + ", shopFlowNo=" + shopFlowNo + ", billSource="
                + billSource + ", orderNo=" + orderNo + ", outTradeNo=" + outTradeNo + ", patType=" + patType + "]";
    }

}
