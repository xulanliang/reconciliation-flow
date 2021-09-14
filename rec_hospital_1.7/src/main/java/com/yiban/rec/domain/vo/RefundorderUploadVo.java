package com.yiban.rec.domain.vo;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;

/**
 * describe: paycenter V2.0 退费订单上送实体
 *
 * @author xll
 * @date 2020/07/16
 */
public class RefundorderUploadVo {

    // 退款订单号
    @Length(max = 64, message = "退款订单号长度最大为64")
    @NotBlank(message = "退款订单号不能为空")
    private String refundId;
    // 平台支付订单号
    @Length(max = 64, message = "平台支付订单号长度最大为64")
    @NotBlank(message = "平台支付订单号不能为空")
    private String payId;
    // 机构编码
    @Length(max = 32, message = "机构编码长度最大为32")
    @NotBlank(message = "机构编码不能为空")
    private String orgCode;
    // 商户退款订单号
    @Length(max = 64, message = "商户退款订单号长度最大为64")
    @NotBlank(message = "商户退款订单号不能为空")
    private String mchOrderId;
    // 退费金额（元）
    @DecimalMin(value = "0.01", message = "退费金额不能小于0.01")
    private String refundAmt;
    // 退费订单状态
    @Length(max = 32, message = "退费订单状态长度最大为32")
    @NotBlank(message = "退费订单状态不能为空")
    private String status;
    // 渠道订单号
    @Length(max = 64, message = "渠道订单号长度最大为64")
    @NotBlank(message = "渠道订单号不能为空")
    private String channelOrderId;
    // 退费完成时间
    @Length(max = 19, message = "退费完成时间长度最大为19")
    @NotBlank(message = "退费完成时间不能为空")
    private String refundSuccessTime;
    // 最后更新时间
    @Length(max = 19, message = "最后更新时间长度最大为19")
    @NotBlank(message = "退费完成时间不能为空")
    private String updatedTime;
    // 外部订单号(微信/支付宝/.)
    @Length(max = 64, message = "外部订单号长度最大为64")
    private String outTradeNo;
    // 微信/支付宝支付单号
    @Length(max = 64, message = "微信/支付宝支付单号长度最大为64")
    private String outOrderNo;
    // 支付渠道编码(支付方式)
    @Length(max = 32, message = "支付渠道编码长度最大为32")
    private String channelCode;
    // 支付订单总金额（元）
    @DecimalMin(value = "0.01", message = "支付订单总金额不能小于0.01")
    private String payTotalFee;
    // 商户APPID
    @Length(max = 64, message = "商户APPID长度最大为64")
    private String mchAppid;
    // 备注
    @Length(max = 512, message = "备注长度最大为512")
    private String remark;
    // 退款原因
    @Length(max = 512, message = "备注长度最大为512")
    private String refundReason;
    // 扩展参数
    @Length(max = 512, message = "扩展参数长度最大为512")
    private String extendParams;

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

    public String getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(String refundAmt) {
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

    public String getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(String payTotalFee) {
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

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getRefundSuccessTime() {
        return refundSuccessTime;
    }

    public void setRefundSuccessTime(String refundSuccessTime) {
        this.refundSuccessTime = refundSuccessTime;
    }

    @Override
    public String toString() {
        return "RefundorderUploadVo{" +
                "refundId='" + refundId + '\'' +
                ", payId='" + payId + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", mchOrderId='" + mchOrderId + '\'' +
                ", refundAmt='" + refundAmt + '\'' +
                ", status='" + status + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", outOrderNo='" + outOrderNo + '\'' +
                ", channelCode='" + channelCode + '\'' +
                ", payTotalFee='" + payTotalFee + '\'' +
                ", mchAppid='" + mchAppid + '\'' +
                ", remark='" + remark + '\'' +
                ", refundReason='" + refundReason + '\'' +
                ", extendParams='" + extendParams + '\'' +
                '}';
    }
}
