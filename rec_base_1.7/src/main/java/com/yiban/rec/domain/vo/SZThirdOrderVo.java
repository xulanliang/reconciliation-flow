package com.yiban.rec.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

public class SZThirdOrderVo {
	
	private String outTradeNo;//业务系统订单号
	private String tsnOrderNo;//
	private String hisOrderNo;//医院His系统订单号 
	private String orderStateRemark;//HIS失败原因信息
	private String orderNo;//支付平台订单号
	private String orderState;//订单状态
	private String ybSerialNo;//医保流水号
	private String ybBillNo;//医保结算单据号
	private BigDecimal payTotalAmount;//医疗总费用=记账金额+自费支付金额
	private BigDecimal payAmount;//自费支付金额
	private BigDecimal ybPayAmount;//记账合计金额
	private String settlementType;//结算方式
	private Date tradeDateTime;//交易日期时间
	private String payType;//自费部分支付类型
	private String payBusinessType;//业务类型
	private String patType;//患者类型
	private String billSource;//账单来源
	private String patientCardNo;//患者就诊卡号
	private String patientName;//患者姓名
	private String cashier;//收费员
	private String goodInfo;//商品明细
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getTsnOrderNo() {
		return tsnOrderNo;
	}
	public void setTsnOrderNo(String tsnOrderNo) {
		this.tsnOrderNo = tsnOrderNo;
	}
	public String getHisOrderNo() {
		return hisOrderNo;
	}
	public void setHisOrderNo(String hisOrderNo) {
		this.hisOrderNo = hisOrderNo;
	}
	public String getOrderStateRemark() {
		return orderStateRemark;
	}
	public void setOrderStateRemark(String orderStateRemark) {
		this.orderStateRemark = orderStateRemark;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getYbSerialNo() {
		return ybSerialNo;
	}
	public void setYbSerialNo(String ybSerialNo) {
		this.ybSerialNo = ybSerialNo;
	}
	public String getYbBillNo() {
		return ybBillNo;
	}
	public void setYbBillNo(String ybBillNo) {
		this.ybBillNo = ybBillNo;
	}
	public BigDecimal getPayTotalAmount() {
		return payTotalAmount;
	}
	public void setPayTotalAmount(BigDecimal payTotalAmount) {
		this.payTotalAmount = payTotalAmount;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public BigDecimal getYbPayAmount() {
		return ybPayAmount;
	}
	public void setYbPayAmount(BigDecimal ybPayAmount) {
		this.ybPayAmount = ybPayAmount;
	}
	public String getSettlementType() {
		return settlementType;
	}
	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}
	public Date getTradeDateTime() {
		return tradeDateTime;
	}
	public void setTradeDateTime(Date tradeDateTime) {
		this.tradeDateTime = tradeDateTime;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayBusinessType() {
		return payBusinessType;
	}
	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
	}
	public String getPatType() {
		return patType;
	}
	public void setPatType(String patType) {
		this.patType = patType;
	}
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getPatientCardNo() {
		return patientCardNo;
	}
	public void setPatientCardNo(String patientCardNo) {
		this.patientCardNo = patientCardNo;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public String getCashier() {
		return cashier;
	}
	public void setCashier(String cashier) {
		this.cashier = cashier;
	}
	public String getGoodInfo() {
		return goodInfo;
	}
	public void setGoodInfo(String goodInfo) {
		this.goodInfo = goodInfo;
	}
	
	


}
