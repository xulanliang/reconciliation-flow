package com.yiban.rec.domain.vo;

import java.math.BigDecimal;

public class JKSZTradeBillVo {
	private String tranType;//交易类型； 1：预约挂号，2：门诊缴费
	private String orderSerial;//业务系统订单号
	private String tranTime;//交易时间；格式：yyyyMMddHHmmss
	private String hisOrderId;//HIS系统订单号
	private String paySerial;//支付交易流水号，由应用支付平台产生，用于对账
	
	private String hisPatientSerial;//HIS系统就诊流水号
	private String pname;//姓名
	private String idcard;//身份证号码
	private String patientId;//就诊人唯一ID号
	private String ehealthCardId;//电子健康卡ID
	
	private String orderStatus;//预约状态（只针对预约挂号订单）； 1：预约成功，2：已取消，3：待取号， 4：已完成，5：已停诊，6：已爽约，7：已分诊，8：已就诊，9：处理中
	private String payStatus;//支付状态； 1：待支付，2：已支付，3：已退款，4：退款中，5：部分退款
	private String payFlag;//支付标识；1：医保全额支付，2：自费全额支付，3：医保自费混合支付
	private String cashPayType;//自费支付方式； 1：微信，2：支付宝，3：银联，4：平安付
	private String notifyFlag;//支付通知医院；0：无需通知，1：待通知，2：已通知
	
	private BigDecimal payAmount;//订单金额（分）
	private BigDecimal cashAmount;//现金自费支付金额（分）
	private BigDecimal insurAmount;//医保支付金额（分）
	private BigDecimal insurSelfAmt;//医保个账支付金额（分）
	private BigDecimal insurPubAmt;//医保统筹支付金额（分）
	
	private String hisSerialNo;//医药机构门诊流水号（akc190）
	private String hisBillNo;//医药机构结算业务序列号（bke384）
	private String insurBillNo;//医保结算业务号（ckc618）
	private String recipeIds;//处方编号列表，多个通过“,”分隔
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getOrderSerial() {
		return orderSerial;
	}
	public void setOrderSerial(String orderSerial) {
		this.orderSerial = orderSerial;
	}
	public String getTranTime() {
		return tranTime;
	}
	public void setTranTime(String tranTime) {
		this.tranTime = tranTime;
	}
	public String getHisOrderId() {
		return hisOrderId;
	}
	public void setHisOrderId(String hisOrderId) {
		this.hisOrderId = hisOrderId;
	}
	public String getPaySerial() {
		return paySerial;
	}
	public void setPaySerial(String paySerial) {
		this.paySerial = paySerial;
	}
	public String getHisPatientSerial() {
		return hisPatientSerial;
	}
	public void setHisPatientSerial(String hisPatientSerial) {
		this.hisPatientSerial = hisPatientSerial;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getEhealthCardId() {
		return ehealthCardId;
	}
	public void setEhealthCardId(String ehealthCardId) {
		this.ehealthCardId = ehealthCardId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayFlag() {
		return payFlag;
	}
	public void setPayFlag(String payFlag) {
		this.payFlag = payFlag;
	}
	public String getCashPayType() {
		return cashPayType;
	}
	public void setCashPayType(String cashPayType) {
		this.cashPayType = cashPayType;
	}
	public String getNotifyFlag() {
		return notifyFlag;
	}
	public void setNotifyFlag(String notifyFlag) {
		this.notifyFlag = notifyFlag;
	}
	public BigDecimal getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}
	public BigDecimal getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}
	public BigDecimal getInsurAmount() {
		return insurAmount;
	}
	public void setInsurAmount(BigDecimal insurAmount) {
		this.insurAmount = insurAmount;
	}
	public BigDecimal getInsurSelfAmt() {
		return insurSelfAmt;
	}
	public void setInsurSelfAmt(BigDecimal insurSelfAmt) {
		this.insurSelfAmt = insurSelfAmt;
	}
	public BigDecimal getInsurPubAmt() {
		return insurPubAmt;
	}
	public void setInsurPubAmt(BigDecimal insurPubAmt) {
		this.insurPubAmt = insurPubAmt;
	}
	public String getHisSerialNo() {
		return hisSerialNo;
	}
	public void setHisSerialNo(String hisSerialNo) {
		this.hisSerialNo = hisSerialNo;
	}
	public String getHisBillNo() {
		return hisBillNo;
	}
	public void setHisBillNo(String hisBillNo) {
		this.hisBillNo = hisBillNo;
	}
	public String getInsurBillNo() {
		return insurBillNo;
	}
	public void setInsurBillNo(String insurBillNo) {
		this.insurBillNo = insurBillNo;
	}
	public String getRecipeIds() {
		return recipeIds;
	}
	public void setRecipeIds(String recipeIds) {
		this.recipeIds = recipeIds;
	}
	
	
}