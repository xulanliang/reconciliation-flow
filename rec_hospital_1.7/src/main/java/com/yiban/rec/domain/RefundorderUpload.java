package com.yiban.rec.domain;

import com.yiban.framework.core.domain.base.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * describe: paycenter V2.0 退费订单上送实体
 *
 * @author xll
 * @date 2020/07/16
 */
@Entity
@Table(name = "t_refundorder_upload")
public class RefundorderUpload extends IdEntity {

    private static final long serialVersionUID = -1259836967637211668L;
    // 退款订单号
    private String refundId;
    // 平台支付订单号
    private String payId;
    // 机构编码
    private String orgCode;
    // 商户退款订单号
    private String mchOrderId;
    // 退费金额（元）
    private BigDecimal refundAmt;
    // 退费订单状态
    private String status;
    // 渠道订单号
    private String channelOrderId;
    // 退费完成时间
    private String refundSuccessTime;
    // 外部订单号(微信/支付宝/.)
    private String outTradeNo;
    // 微信/支付宝 支付单号
    private String outOrderNo;
    // 支付渠道编码(支付方式)
    private String channelCode;
    // 支付订单总金额（元）
    private BigDecimal payTotalFee;
    // 商户APPID
    private String mchAppid;
    // 备注
    private String remark;
    // 退款原因
    private String refundReason;
    // 扩展参数
    private String extendParams;
    private Date createDate;

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getMchOrderId() {
        return mchOrderId;
    }

    public void setMchOrderId(String mchOrderId) {
        this.mchOrderId = mchOrderId;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOutOrderNo() {
        return outOrderNo;
    }

    public void setOutOrderNo(String outOrderNo) {
        this.outOrderNo = outOrderNo;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public BigDecimal getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(BigDecimal payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    public String getMchAppid() {
        return mchAppid;
    }

    public void setMchAppid(String mchAppid) {
        this.mchAppid = mchAppid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRefundSuccessTime() {
        return refundSuccessTime;
    }

    public void setRefundSuccessTime(String refundSuccessTime) {
        this.refundSuccessTime = refundSuccessTime;
    }
}
