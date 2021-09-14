package com.yiban.rec.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 窗口现金核对表
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-21 9:58
 */
@Entity
@Table(name = "t_window_cash")
public class WindowCash extends IdEntity {


//    private Long id;
    private String orgCode;
    // 存款时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date cashDate;
    // 收费员账号
    private String cashierAccount;
    // 收费员名称
    private String cashierName;
    // 业务类型
    private String businessType;
    // 银行类型
    private String bankType;
    // 状态：异常、正常、已通过
    private String cashStatus;
    // HIS 汇总金额（应收金额）
    private BigDecimal hisAmount;
    // 渠道到账金额（实收金额）
    private BigDecimal channelAmount;
    // 垫付金额
    private BigDecimal exceptionalAmount;
    // 垫付原因
    private String exceptionalReason;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createdDateTime;
    // 通过操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date checkDateTime;
    // 通过操作账号
    private String checkCashierAcount;
    // 通过操作人
    private String checkCashierName;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Date getCashDate() {
        return cashDate;
    }

    public void setCashDate(Date cashDate) {
        this.cashDate = cashDate;
    }

    public String getCashierAccount() {
        return cashierAccount;
    }

    public void setCashierAccount(String cashierAccount) {
        this.cashierAccount = cashierAccount;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCashStatus() {
        return cashStatus;
    }

    public void setCashStatus(String cashStatus) {
        this.cashStatus = cashStatus;
    }

    public BigDecimal getHisAmount() {
        return hisAmount;
    }

    public void setHisAmount(BigDecimal hisAmount) {
        this.hisAmount = hisAmount;
    }

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public BigDecimal getExceptionalAmount() {
        return exceptionalAmount;
    }

    public void setExceptionalAmount(BigDecimal exceptionalAmount) {
        this.exceptionalAmount = exceptionalAmount;
    }

    public String getExceptionalReason() {
        return exceptionalReason;
    }

    public void setExceptionalReason(String exceptionalReason) {
        this.exceptionalReason = exceptionalReason;
    }


    public Date getCheckDateTime() {
        return checkDateTime;
    }

    public void setCheckDateTime(Date checkDateTime) {
        this.checkDateTime = checkDateTime;
    }

    public String getCheckCashierAcount() {
        return checkCashierAcount;
    }

    public void setCheckCashierAcount(String checkCashierAcount) {
        this.checkCashierAcount = checkCashierAcount;
    }

    public String getCheckCashierName() {
        return checkCashierName;
    }

    public void setCheckCashierName(String checkCashierName) {
        this.checkCashierName = checkCashierName;
    }
}
