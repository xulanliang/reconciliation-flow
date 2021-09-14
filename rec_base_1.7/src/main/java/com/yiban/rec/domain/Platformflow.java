package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 银医流水信息
 */
@Entity
@Table(name = "t_rec_platformflow_log")
public class Platformflow extends BaseEntityEx {

	private static final long serialVersionUID = 8612495072627181881L;

	// 平台流水号
	private String flowNo;

	// 订单状态
	private String orderState;
	
	//订单状态值
	@Transient
	private String orderStateName;

	// 交易编码
	private Integer tradeCode;
	
	//交易编码名称
	@Transient
	private String tradeCodeName;

	// 交易来源
	private Integer tradeFrom;
	
	//交易来源值
	@Transient
	private String tradeFromName;
	
	//交易目的
	private Integer tradeTo;
	
	//交易目的名称
	@Transient
	private String tradeToName;

	// 机构编码
	private String orgNo;;
	
	//机构名称
	@Transient
	private String orgName;

	// 支付来源
	private String paySource;
	
	//支付来源名称
	@Transient
	private String paySourceName;

	// 支付类型
	private String payType;
	
	//支付类型名称
	@Transient
	private String payTypeName;

	// 支付商户号
	private String payShopNo;

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

	// 支付日期(YYYYMMDD)
	private String payDate;

	// 支付时间(HHMMSS)
	private String payTime;
	
	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDatatime;

	// 支付系统号
	private String paySystemNo;

	// 原支付商户批次号
	private String oriPayBatchNo;

	// 原支付商户流水号
	private String oriPayFlowNo;

	// 原支付日期
	private String oriPayDate;

	// 原支付时间
	private String oriPayTime;

	// 原支付系统号
	private String oriPaySystemNo;

	// 支付业务类型
	private String payBusinessType;
	
	//业务类型名称
	@Transient
	private String payBusinessTypeName;

	// 客户标识类型
	private String custIdentifyType;
	
	//客户标记名称
	@Transient
	private String custIdentifyTypeName;

	// 客户名称
	private String custName;

	// 收费员
	private String cashier;

	// 响应编码
	private String responseCode;
	
	//相应编码值
	@Transient
	private String responseCodeName;

	// 设备编码
	private String deviceNo;

	//机构id
	private String orgCode;
	//支付编码
	private String payCode;

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public Integer getTradeCode() {
		return tradeCode;
	}

	public String getTradeToName() {
		return tradeToName;
	}

	public void setTradeToName(String tradeToName) {
		this.tradeToName = tradeToName;
	}

	public String getPaySourceName() {
		return paySourceName;
	}

	public String getCustIdentifyTypeName() {
		return custIdentifyTypeName;
	}

	public void setCustIdentifyTypeName(String custIdentifyTypeName) {
		this.custIdentifyTypeName = custIdentifyTypeName;
	}

	public void setPaySourceName(String paySourceName) {
		this.paySourceName = paySourceName;
	}

	public void setTradeCode(Integer tradeCode) {
		this.tradeCode = tradeCode;
	}

	public Integer getTradeFrom() {
		return tradeFrom;
	}

	public void setTradeFrom(Integer tradeFrom) {
		this.tradeFrom = tradeFrom;
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

	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
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

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getPaySystemNo() {
		return paySystemNo;
	}

	public void setPaySystemNo(String paySystemNo) {
		this.paySystemNo = paySystemNo;
	}

	public String getOriPayBatchNo() {
		return oriPayBatchNo;
	}

	public void setOriPayBatchNo(String oriPayBatchNo) {
		this.oriPayBatchNo = oriPayBatchNo;
	}

	public String getOriPayFlowNo() {
		return oriPayFlowNo;
	}

	public void setOriPayFlowNo(String oriPayFlowNo) {
		this.oriPayFlowNo = oriPayFlowNo;
	}

	public String getOriPayDate() {
		return oriPayDate;
	}

	public void setOriPayDate(String oriPayDate) {
		this.oriPayDate = oriPayDate;
	}

	public String getOriPayTime() {
		return oriPayTime;
	}

	public void setOriPayTime(String oriPayTime) {
		this.oriPayTime = oriPayTime;
	}

	public String getOriPaySystemNo() {
		return oriPaySystemNo;
	}

	public void setOriPaySystemNo(String oriPaySystemNo) {
		this.oriPaySystemNo = oriPaySystemNo;
	}

	public String getPayBusinessType() {
		return payBusinessType;
	}

	public String getCustIdentifyType() {
		return custIdentifyType;
	}

	public void setCustIdentifyType(String custIdentifyType) {
		this.custIdentifyType = custIdentifyType;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}


	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getTradeDatatime() {
		return tradeDatatime;
	}

	public void setTradeDatatime(Date tradeDatatime) {
		this.tradeDatatime = tradeDatatime;
	}

	

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getTradeCodeName() {
		return tradeCodeName;
	}

	public void setTradeCodeName(String tradeCodeName) {
		this.tradeCodeName = tradeCodeName;
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

	public String getPayBusinessTypeName() {
		return payBusinessTypeName;
	}

	public void setPayBusinessTypeName(String payBusinessTypeName) {
		this.payBusinessTypeName = payBusinessTypeName;
	}

	public Integer getTradeTo() {
		return tradeTo;
	}

	public void setTradeTo(Integer tradeTo) {
		this.tradeTo = tradeTo;
	}

	public String getOrderStateName() {
		return orderStateName;
	}

	public void setOrderStateName(String orderStateName) {
		this.orderStateName = orderStateName;
	}

	public String getTradeFromName() {
		return tradeFromName;
	}

	public void setTradeFromName(String tradeFromName) {
		this.tradeFromName = tradeFromName;
	}

	public String getResponseCodeName() {
		return responseCodeName;
	}

	public void setResponseCodeName(String responseCodeName) {
		this.responseCodeName = responseCodeName;
	}
	

}
