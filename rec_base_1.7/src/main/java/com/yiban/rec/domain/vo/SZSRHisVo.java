package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class SZSRHisVo {
	private String his_type;//订单的支付类型
    private String psOrdNum;//公共平台订单号
    private String hisOrdNum;//医院订单号
    private String agtOrdNum;//医院交易流水号
    private String patCardType;//诊疗卡类型
    private String patCardNo;//卡号
    private String patName;//患者姓名
    private String orderType;//订单类型
    private String payMode;//支付渠道
    private String collectors;//收费员
    private String payStatus;//支付状态
    private BigDecimal payAmount=new BigDecimal(0);//自费金额
    private BigDecimal accountAmount=new BigDecimal(0);//个人账户结算金额
    private BigDecimal medicareAmount=new BigDecimal(0);//统筹基金结算金额
    private BigDecimal insuranceAmount=new BigDecimal(0);//记账合计
    private BigDecimal totalAmount=new BigDecimal(0);//总金额
    private String payTime;//支付时间
    private String agtCode;//
    private String hisOrdJournalNum;//医院交易流水号
    private String SSBillNumber;
    
    private String billSource;
    private String posorderno;
    private String pjh;
    private String tsnOrderNo;
    private String orderNo;
    private String settlementType;
    private String outTradeNo;
    private String sqm;
    private String payType;
    private String shh;	//商户号

	public String getHisOrdJournalNum() {
		return hisOrdJournalNum;
	}
	public void setHisOrdJournalNum(String hisOrdJournalNum) {
		this.hisOrdJournalNum = hisOrdJournalNum;
	}
	public String getHis_type() {
		return his_type;
	}
	public void setHis_type(String his_type) {
		this.his_type = his_type;
	}
	public String getPsOrdNum() {
		return psOrdNum;
	}
	public void setPsOrdNum(String psOrdNum) {
		this.psOrdNum = psOrdNum;
	}
	public String getHisOrdNum() {
		return hisOrdNum;
	}
	public void setHisOrdNum(String hisOrdNum) {
		this.hisOrdNum = hisOrdNum;
	}
	public String getAgtOrdNum() {
		return agtOrdNum;
	}
	public void setAgtOrdNum(String agtOrdNum) {
		this.agtOrdNum = agtOrdNum;
	}
	public String getPatCardType() {
		return patCardType;
	}
	public void setPatCardType(String patCardType) {
		this.patCardType = patCardType;
	}
	public String getPatCardNo() {
		return patCardNo;
	}
	public void setPatCardNo(String patCardNo) {
		this.patCardNo = patCardNo;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getPayMode() {
		return payMode;
	}
	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
	public String getCollectors() {
		return collectors;
	}
	public void setCollectors(String collectors) {
		this.collectors = collectors;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public BigDecimal getAccountAmount() {
		return accountAmount;
	}
	public void setAccountAmount(BigDecimal accountAmount) {
		this.accountAmount = accountAmount;
	}
	public BigDecimal getMedicareAmount() {
		return medicareAmount;
	}
	public void setMedicareAmount(BigDecimal medicareAmount) {
		this.medicareAmount = medicareAmount;
	}
	public BigDecimal getInsuranceAmount() {
		return insuranceAmount;
	}
	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getAgtCode() {
		return agtCode;
	}
	public void setAgtCode(String agtCode) {
		this.agtCode = agtCode;
	}
    public String getSSBillNumber() {
        return SSBillNumber;
    }
    public void setSSBillNumber(String sSBillNumber) {
        SSBillNumber = sSBillNumber;
    }
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getPosorderno() {
		return posorderno;
	}
	public void setPosorderno(String posorderno) {
		this.posorderno = posorderno;
	}
	public String getPjh() {
		return pjh;
	}
	public void setPjh(String pjh) {
		this.pjh = pjh;
	}
	public String getTsnOrderNo() {
		return tsnOrderNo;
	}
	public void setTsnOrderNo(String tsnOrderNo) {
		this.tsnOrderNo = tsnOrderNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getSettlementType() {
		return settlementType;
	}
	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getSqm() {
		return sqm;
	}
	public void setSqm(String sqm) {
		this.sqm = sqm;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getShh() {
		return shh;
	}

	public void setShh(String shh) {
		this.shh = shh;
	}
}
