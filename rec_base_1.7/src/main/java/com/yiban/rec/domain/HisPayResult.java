package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.rec.domain.vo.GoodInfoVo;

/**
 * 业务系统支付结果上送表
 */
@Entity
@Table(name = "t_rec_pay_result")
public class HisPayResult extends BaseEntityEx {
	private static final long serialVersionUID = 5796752437191180470L;

	// 机构编码
	private String orgNo;
	
	//机构名称
	@Transient
	private String orgName;

	// 支付来源
	private String paySource;
	
	//支付来源名称
	@Transient
	private String paySourceName;
	
	//交易目的名称
	@Transient
	private String tradeToName;

	// 支付类型
	private String payType;
	
	//支付类型名称
	@Transient
	private String payTypeName;

	// 交易编码
	private String tradeCode;
	
	//交易编码名称
	@Transient
	private String tradeCodeName;

	// 支付商户号
	private String payShopNo;

	// 终端号
	private String payTermNo;
	
	//支付终端号
	@Transient
	private String paySerNo;

	// 支付商户批次号
	private String payBatchNo;

	// 支付商户流水号
	private String payFlowNo;

	// 支付账号
	private String payAccount;

	// 支付金额
	private BigDecimal payAmount;

	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDatatime;

	// 支付系统号
	private String paySystemNo;

	// 订单状态
	private String orderState;
	
	//订单状态值
	@Transient
	private String orderStateName;

	// 支付业务类型
	private String payBusinessType;
	
	//支付业务类型名称
	@Transient
	private String payBusinessTypeName;

	// 业务系统流水号
	private String businessFlowNo;

	//患者姓名
	private String patientName;
	
	//患者卡号
	private String patientCardNo;
	
	//Patient_Id_Card
	private String patientIdCard;	
	
	// 客户标识类型
	private String custIdentifyType;
	
	//商品信息
	private String goodInfoList;
	
	// 客户名称
	private String custName;
	
	//客户标记名称
	@Transient 
	private String custIdentifyTypeName;  
	
	//商品信息
	@Transient
	private List<GoodInfoVo> goodInfo;
	
	// 科室编号
	private String deptNo;

	// 科室名称
	private String deptName;
	
	// 收费员
	private String cashier;
	
	// 设备编码
	private String deviceNo;

	// 流水号
	private String flowNo;

	private String refundState;
	
	//账单来源
	private String billSource;
	
	private String patType;

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

	public String getPaySystemNo() {
		return paySystemNo;
	}

	public void setPaySystemNo(String paySystemNo) {
		this.paySystemNo = paySystemNo;
	}


	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getBusinessFlowNo() {
		return businessFlowNo;
	}

	public void setBusinessFlowNo(String businessFlowNo) {
		this.businessFlowNo = businessFlowNo;
	}

	public String getCustIdentifyType() {
		return custIdentifyType;
	}

	public void setCustIdentifyType(String custIdentifyType) {
		this.custIdentifyType = custIdentifyType;
	}

	public String getCustIdentifyTypeName() {
		return custIdentifyTypeName;
	}

	public void setCustIdentifyTypeName(String custIdentifyTypeName) {
		this.custIdentifyTypeName = custIdentifyTypeName;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public Date getTradeDatatime() {
		return tradeDatatime;
	}

	public void setTradeDatatime(Date tradeDatatime) {
		this.tradeDatatime = tradeDatatime;
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

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getPayBusinessType() {
		return payBusinessType;
	}

	public void setPayBusinessType(String payBusinessType) {
		this.payBusinessType = payBusinessType;
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

	public String getTradeCodeName() {
		return tradeCodeName;
	}

	public void setTradeCodeName(String tradeCodeName) {
		this.tradeCodeName = tradeCodeName;
	}

	public String getPayBusinessTypeName() {
		return payBusinessTypeName;
	}

	public void setPayBusinessTypeName(String payBusinessTypeName) {
		this.payBusinessTypeName = payBusinessTypeName;
	}

	public String getPaySourceName() {
		return paySourceName;
	}

	public void setPaySourceName(String paySourceName) {
		this.paySourceName = paySourceName;
	}

	public String getTradeToName() {
		return tradeToName;
	}

	public void setTradeToName(String tradeToName) {
		this.tradeToName = tradeToName;
	}

	public String getOrderStateName() {
		return orderStateName;
	}

	public void setOrderStateName(String orderStateName) {
		this.orderStateName = orderStateName;
	}

	public String getGoodInfoList() {
		return goodInfoList;
	}

	public void setGoodInfoList(String goodInfoList) {
		this.goodInfoList = goodInfoList;
	}

	public List<GoodInfoVo> getGoodInfo() {
		return goodInfo;
	}

	public void setGoodInfo(List<GoodInfoVo> goodInfo) {
		this.goodInfo = goodInfo;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientCardNo() {
		return patientCardNo;
	}

	public void setPatientCardNo(String patientCardNo) {
		this.patientCardNo = patientCardNo;
	}

	public String getPatientIdCard() {
		return patientIdCard;
	}

	public void setPatientIdCard(String patientIdCard) {
		this.patientIdCard = patientIdCard;
	}

	public String getHisState() {
		return refundState;
	}

	public void setHisState(String hisState) {
		this.refundState = hisState;
	}

	public String getPaySerNo() {
		return paySerNo;
	}

	public void setPaySerNo(String paySerNo) {
		this.paySerNo = paySerNo;
	}

	public String getRefundState() {
		return refundState;
	}

	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

}
