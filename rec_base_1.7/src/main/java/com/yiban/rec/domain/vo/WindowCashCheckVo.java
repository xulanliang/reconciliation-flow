package com.yiban.rec.domain.vo;

/**
 * @Description
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-02-21 15:09
 */
public class WindowCashCheckVo {
    private String id;
    private String channelAmount;
    private String exceptionalAmount;
    private String cashDate;
    private String exceptionalReason;
    private String orgCode;
    private String startDate;
    private String endDate;
    private String cashierName;
    private String bankType;
    private String businessType;


    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(String channelAmount) {
        this.channelAmount = channelAmount;
    }

    public String getExceptionalAmount() {
        return exceptionalAmount;
    }

    public void setExceptionalAmount(String exceptionalAmount) {
        this.exceptionalAmount = exceptionalAmount;
    }

    public String getCashDate() {
        return cashDate;
    }

    public void setCashDate(String cashDate) {
        this.cashDate = cashDate;
    }

    public String getExceptionalReason() {
        return exceptionalReason;
    }

    public void setExceptionalReason(String exceptionalReason) {
        this.exceptionalReason = exceptionalReason;
    }
}
