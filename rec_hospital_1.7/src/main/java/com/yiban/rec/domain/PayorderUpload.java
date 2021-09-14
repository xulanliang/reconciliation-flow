package com.yiban.rec.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * describe: paycenter V2.0 支付订单上送实体
 *
 * @author xll
 * @date 2020/07/16
 */
@Entity
@Table(name = "t_payorder_upload")
public class PayorderUpload implements Serializable {

    private static final long serialVersionUID = -1259836967637211668L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 支付订单号
    private String payId;
    // 机构编码
    private String orgCode;
    // 商户订单号
    private String mchOrderId;
    // 订单金额（元）
    private BigDecimal orderAmt;
    // 支付类型(条码支付/APP支付/..)
    private String payType;
    // 支付方式
    private String payOption;
    // 支付状态  1001：支付成功  1002：支付异常
    private String status;
    // 外部订单号(微信/支付宝/.)
    private String outTradeNo;
    // 支付完成时间
    private String payTimeEnd;
    // 关闭订单号
    private String closeId;
    // 撤销订单号
    private String cancelId;
    // 总的已退金额（元）
    private BigDecimal totalRefundedAmt;
    // 终端编号
    private String terminalId;
    // 业务类型
    private String businessType;
    // 患者名称
    private String patientName;
    // 患者ID
    private String patientId;
    // 患者类型
    private String patientType;
    // 商户APPID
    private String mchAppid;
    // 商户回调通知地址
    private String mchNotifyUrl;
    // 订单主题/商品名称
    private String orderSubject;
    // 订单说明/商品明细
    private String orderDetail;
    // 条码付bar_code/声波付wave_code/人脸付face_code，默认bar_code
    private String payScene;
    // 用户终端IP
    private String userClientIp;
    // 条码支付授权码
    private String authCode;
    // 公众号
    private String openId;
    // 备注
    private String remark;
    // 订单失效时间
    private String timeExpire;
    // 渠道订单号
    private String channelOrderId;
    // 扩展信息
    private String extendParams;
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
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

    public BigDecimal getTotalRefundedAmt() {
        return totalRefundedAmt;
    }

    public void setTotalRefundedAmt(BigDecimal totalRefundedAmt) {
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPayTimeEnd() {
        return payTimeEnd;
    }

    public void setPayTimeEnd(String payTimeEnd) {
        this.payTimeEnd = payTimeEnd;
    }
}
