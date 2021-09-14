package com.yiban.rec.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;

/**
 * describe: paycenter V2.0 支付订单上送实体
 *
 * @author xll
 * @date 2020/07/16
 */

public class PayorderUploadVo {

    // 支付订单号
    @Length(max = 64, message = "支付订单号长度最大为64")
    @NotBlank(message = "支付订单号不能为空")
    private String payId;
    // 机构编码
    @Length(max = 64, message = "机构编码长度最大为64")
    @NotBlank(message = "机构编码不能为空")
    private String orgCode;
    // 商户订单号
    @Length(max = 64, message = "商户订单号长度最大为64")
    @NotBlank(message = "商户订单号不能为空")
    private String mchOrderId;
    // 订单金额（元）
    @DecimalMin(value = "0.01", message = "订单金额不能小于0.01")
    private String orderAmt;
    // 支付类型(条码支付/APP支付/..)
    @Length(max = 32, message = "支付类型长度最大为32")
    @NotBlank(message = "支付类型不能为空")
    private String payType;
    // 支付方式
    @Length(max = 32, message = "支付方式长度最大为32")
    @NotBlank(message = "支付方式不能为空")
    private String payOption;
    // 支付状态  1001：支付成功  1002：支付异常
    private String status;
    // 外部订单号(微信/支付宝/.)
    @Length(max = 64, message = "外部订单号长度最大为64")
    @NotBlank(message = "外部订单号不能为空")
    private String outTradeNo;
    // 支付完成时间
    @Length(max = 19, message = "支付完成时间长度最大为32")
    private String payTimeEnd;
    // 最后更新时间完成时间
    @Length(max = 19, message = "最后更新时间长度最大为32")
    @NotBlank(message = "最后更新时间不能为空")
    private String updatedTime;
    // 关闭订单号
    @Length(max = 64, message = "关闭订单号长度最大为64")
    private String closeId;
    // 撤销订单号
    @Length(max = 64, message = "撤销订单号长度最大为64")
    private String cancelId;
    // 总的已退金额（元）
    @DecimalMin(value = "0", message = "总的已退金额不能小于0")
    private String totalRefundedAmt;
    // 终端编号
    @Length(max = 64, message = "终端编号长度最大为64")
    private String terminalId;
    // 业务类型
    @Length(max = 32, message = "业务类型长度最大为32")
    private String businessType;
    // 患者名称
    @Length(max = 32, message = "患者名称长度最大为32")
    private String patientName;
    // 患者ID
    @Length(max = 32, message = "患者ID长度最大为32")
    private String patientId;
    // 患者ID类型
    @Length(max = 32, message = "患者ID类型长度最大为32")
    private String patientType;
    // 商户APPID
    @Length(max = 64, message = "商户APPID长度最大为64")
    private String mchAppid;
    // 商户回调通知地址
    @Length(max = 512, message = "商户回调通知地址长度最大为512")
    private String mchNotifyUrl;
    // 订单主题/商品名称
    @Length(max = 128, message = "订单主题/商品名称长度最大为128")
    private String orderSubject;
    // 订单说明/商品明细
    @Length(max = 1024, message = "订单说明/商品明细长度最大为1024")
    private String orderDetail;
    // 条码付bar_code/声波付wave_code/人脸付face_code，默认bar_code
    @Length(max = 32, message = "条码付长度最大为32")
    private String payScene;
    // 用户终端IP
    @Length(max = 32, message = "用户终端IP长度最大为32")
    private String userClientIp;
    // 条码支付授权码
    @Length(max = 128, message = "条码支付授权码长度最大为128")
    private String authCode;
    // 公众号
    @Length(max = 128, message = "公众号长度最大为128")
    private String openId;
    // 备注
    @Length(max = 255, message = "备注长度最大为255")
    private String remark;
    // 订单失效时间
    @Length(max = 14, message = "订单失效时间长度最大为14")
    private String timeExpire;
    // 渠道订单号
    @Length(max = 64, message = "渠道订单号长度最大为64")
    private String channelOrderId;
    // 扩展信息
    @Length(max = 512, message = "扩展信息长度最大为512")
    private String extendParams;

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

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getCloseId() {
        return closeId;
    }

    public void setCloseId(String closeId) {
        this.closeId = closeId;
    }

    public String getCancelId() {
        return cancelId;
    }

    public void setCancelId(String cancelId) {
        this.cancelId = cancelId;
    }

    public String getTotalRefundedAmt() {
        return totalRefundedAmt;
    }

    public void setTotalRefundedAmt(String totalRefundedAmt) {
        this.totalRefundedAmt = totalRefundedAmt;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getMchAppid() {
        return mchAppid;
    }

    public void setMchAppid(String mchAppid) {
        this.mchAppid = mchAppid;
    }

    public String getMchNotifyUrl() {
        return mchNotifyUrl;
    }

    public void setMchNotifyUrl(String mchNotifyUrl) {
        this.mchNotifyUrl = mchNotifyUrl;
    }

    public String getOrderSubject() {
        return orderSubject;
    }

    public void setOrderSubject(String orderSubject) {
        this.orderSubject = orderSubject;
    }

    public String getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getPayScene() {
        return payScene;
    }

    public void setPayScene(String payScene) {
        this.payScene = payScene;
    }

    public String getUserClientIp() {
        return userClientIp;
    }

    public void setUserClientIp(String userClientIp) {
        this.userClientIp = userClientIp;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getChannelOrderId() {
        return channelOrderId;
    }

    public void setChannelOrderId(String channelOrderId) {
        this.channelOrderId = channelOrderId;
    }

    public String getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

    public String getPayTimeEnd() {
        return payTimeEnd;
    }

    public void setPayTimeEnd(String payTimeEnd) {
        this.payTimeEnd = payTimeEnd;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "PayorderUploadVo{" +
                "payId='" + payId + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", mchOrderId='" + mchOrderId + '\'' +
                ", orderAmt='" + orderAmt + '\'' +
                ", payType='" + payType + '\'' +
                ", payOption='" + payOption + '\'' +
                ", status='" + status + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", closeId='" + closeId + '\'' +
                ", cancelId='" + cancelId + '\'' +
                ", totalRefundedAmt='" + totalRefundedAmt + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", businessType='" + businessType + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientId='" + patientId + '\'' +
                ", patientType='" + patientType + '\'' +
                ", mchAppid='" + mchAppid + '\'' +
                ", mchNotifyUrl='" + mchNotifyUrl + '\'' +
                ", orderSubject='" + orderSubject + '\'' +
                ", orderDetail='" + orderDetail + '\'' +
                ", payScene='" + payScene + '\'' +
                ", userClientIp='" + userClientIp + '\'' +
                ", authCode='" + authCode + '\'' +
                ", openId='" + openId + '\'' +
                ", remark='" + remark + '\'' +
                ", timeExpire='" + timeExpire + '\'' +
                ", channelOrderId='" + channelOrderId + '\'' +
                '}';
    }
}

