package com.yiban.rec.domain.basicInfo;

import java.math.BigDecimal;

//线上退费实体
public class OnlineRefundInfo {
	
	private Long id;

	// 交易编码
	private String Trade_Code;

	// 机构编码
	private String Org_No;

	// 支付商户号
	private String Pay_Shop_No;

	// 支付应用ID
	private String Pay_App_ID;

	// 支付来源
	private String Pay_Source;

	// 支付类型
	private String Pay_Type;

	// 支付商户流水号
	private String Pay_Flow_No;

	// 支付金额
	private BigDecimal Pay_Amount;

	// 退款金额
	private BigDecimal Pay_Round;

	// 原支付商户流水号
	private String Ori_Pay_Flow_No;

	// 设备编码
	private String Device_No;

	// 平台校验码
	private String Chk;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrade_Code() {
		return Trade_Code;
	}

	public void setTrade_Code(String trade_Code) {
		Trade_Code = trade_Code;
	}

	public String getOrg_No() {
		return Org_No;
	}

	public void setOrg_No(String org_No) {
		Org_No = org_No;
	}

	public String getPay_Shop_No() {
		return Pay_Shop_No;
	}

	public void setPay_Shop_No(String pay_Shop_No) {
		Pay_Shop_No = pay_Shop_No;
	}

	public String getPay_App_ID() {
		return Pay_App_ID;
	}

	public void setPay_App_ID(String pay_App_ID) {
		Pay_App_ID = pay_App_ID;
	}

	public String getPay_Source() {
		return Pay_Source;
	}

	public void setPay_Source(String pay_Source) {
		Pay_Source = pay_Source;
	}

	public String getPay_Type() {
		return Pay_Type;
	}

	public void setPay_Type(String pay_Type) {
		Pay_Type = pay_Type;
	}

	public String getPay_Flow_No() {
		return Pay_Flow_No;
	}

	public void setPay_Flow_No(String pay_Flow_No) {
		Pay_Flow_No = pay_Flow_No;
	}

	public BigDecimal getPay_Amount() {
		return Pay_Amount;
	}

	public void setPay_Amount(BigDecimal pay_Amount) {
		Pay_Amount = pay_Amount;
	}

	public BigDecimal getPay_Round() {
		return Pay_Round;
	}

	public void setPay_Round(BigDecimal pay_Round) {
		Pay_Round = pay_Round;
	}

	public String getOri_Pay_Flow_No() {
		return Ori_Pay_Flow_No;
	}

	public void setOri_Pay_Flow_No(String ori_Pay_Flow_No) {
		Ori_Pay_Flow_No = ori_Pay_Flow_No;
	}

	public String getDevice_No() {
		return Device_No;
	}

	public void setDevice_No(String device_No) {
		Device_No = device_No;
	}

	public String getChk() {
		return Chk;
	}

	public void setChk(String chk) {
		Chk = chk;
	}

	@Override
	public String toString() {
		return "OnlineRefundInfo [Trade_Code=" + Trade_Code + ", Org_No=" + Org_No + ", Pay_Shop_No=" + Pay_Shop_No
				+ ", Pay_App_ID=" + Pay_App_ID + ", Pay_Source=" + Pay_Source + ", Pay_Type=" + Pay_Type
				+ ", Pay_Flow_No=" + Pay_Flow_No + ", Pay_Amount=" + Pay_Amount + ", Pay_Round=" + Pay_Round
				+ ", Ori_Pay_Flow_No=" + Ori_Pay_Flow_No + ", Device_No=" + Device_No + ", Chk=" + Chk + ", getId()="
				+ getId() + ", getTrade_Code()=" + getTrade_Code() + ", getOrg_No()=" + getOrg_No()
				+ ", getPay_Shop_No()=" + getPay_Shop_No() + ", getPay_App_ID()=" + getPay_App_ID()
				+ ", getPay_Source()=" + getPay_Source() + ", getPay_Type()=" + getPay_Type() + ", getPay_Flow_No()="
				+ getPay_Flow_No() + ", getPay_Amount()=" + getPay_Amount() + ", getPay_Round()=" + getPay_Round()
				+ ", getOri_Pay_Flow_No()=" + getOri_Pay_Flow_No() + ", getDevice_No()=" + getDevice_No()
				+ ", getChk()=" + getChk() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

}
