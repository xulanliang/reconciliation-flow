//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yiban.rec.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_citizen_card")
public class CitizenCardBill extends IdEntity {
    private static final long serialVersionUID = -7600438508540298503L;
    private String orgNo;
    private String payShopNo;
    private String payTermNo;
    private String payFlowNo;
    private BigDecimal payAmount;
    private String payAccount;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+08:00"
    )
    private Date tradeDatatime;
    private String orderState;
    @Transient
    private String orderStateName;
    private String updatedBy;
    private Date updatedTime;
    private Date createdDate;
    private Integer isDeleted = 0;
    private Integer isActived = 1;
    private String shopFlowNo;
    private String orderNo;
    private String outTradeNo;
    private Byte cardType;

    public CitizenCardBill() {
    }

    public String getOrgNo() {
        return this.orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getPayShopNo() {
        return this.payShopNo;
    }

    public void setPayShopNo(String payShopNo) {
        this.payShopNo = payShopNo;
    }

    public String getPayTermNo() {
        return this.payTermNo;
    }

    public void setPayTermNo(String payTermNo) {
        this.payTermNo = payTermNo;
    }

    public String getPayFlowNo() {
        return this.payFlowNo;
    }

    public void setPayFlowNo(String payFlowNo) {
        this.payFlowNo = payFlowNo;
    }

    public BigDecimal getPayAmount() {
        return this.payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Date getTradeDatatime() {
        return this.tradeDatatime;
    }

    public void setTradeDatatime(Date tradeDatatime) {
        this.tradeDatatime = tradeDatatime;
    }

    public String getOrderState() {
        return this.orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsActived() {
        return this.isActived;
    }

    public void setIsActived(Integer isActived) {
        this.isActived = isActived;
    }

    public String getShopFlowNo() {
        return this.shopFlowNo;
    }

    public void setShopFlowNo(String shopFlowNo) {
        this.shopFlowNo = shopFlowNo;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOrderStateName() {
        if (this.orderState != null) {
            if ("10011".equals(this.orderState)) {
                return "充值";
            }

            if ("10021".equals(this.orderState)) {
                return "消费";
            }

            if ("10031".equals(this.orderState)) {
                return "取现";
            }

            if ("30011".equals(this.orderState)) {
                return "退款";
            }

            if ("30031".equals(this.orderState)) {
                return "充值撤销";
            }
        }

        return this.orderStateName;
    }

    public void setOrderStateName(String orderStateName) {
        this.orderStateName = orderStateName;
    }

    public String getPayAccount() {
        return this.payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public Byte getCardType() {
        return this.cardType;
    }

    public void setCardType(Byte cardType) {
        this.cardType = cardType;
    }
}
