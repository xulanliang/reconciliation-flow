package com.yiban.rec.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * his交易流水
 */
@Entity
@Table(name = "t_rec_histransactionflow")
public class HisTransactionFlow extends BaseEntityEx {

		private static final long serialVersionUID = 1500731789649529936L;

		// 机构编码
		private String orgNo;

		//机构名称
		@Transient
		private String orgName;

		// 支付类型
		private String payType;

		//支付类型名称
		@Transient
	private String payTypeName;

	// 支付商户流水号
	private String payFlowNo;

	// 支付账号
	private String payAccount;

	// 支付金额
	private BigDecimal payAmount;

	//交易时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDatatime;
	
	//结算时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date settlementDate;
	
	@Transient
	private String tradeDatetimeStr;

	// 支付系统号
	private String paySystemNo;

	// 原支付系统号
	private String oriPayFlowNo;

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

	// 客户标识类型
	private String custIdentifyType;
	
	//客户标记名称
	@Transient 
	private String custIdentifyTypeName;  

	// 患者名称
	private String custName;

	// 科室编码
	private String deptNo;

	// 科室名称
	private String deptName;

	// 收费员
	private String cashier;

	// 设备编码
	private String deviceNo;

	// 流水号
	private String hisFlowNo;
	
	// 证件号
	private String credentialsNo;
	
	// 发票号
	private String invoiceNo;
	
	//交易机构代码
	private String agtCode;
	
	//终端机器名称
	private String terminalName;
	
	//终端机器IP
	private String terminalIp;
	
	//终端编号
	private String terminalNo;
	
	//患者类型（门诊/住院）
	private String patType;
	
	//患者类型（门诊/住院）
	@Transient
	private String patTypeName;
	
	//住院号/卡号
	private String patCode;
	
	//门诊号
	private String mzCode;
	
	//厂家编号
	private String wsCode;
	
	//账单来源
	private String billSource;
	
	//就诊卡号
	private String visitNumber;
	
	// 支付位置：0001 自助机，0002窗口
	private String payLocation;
	
	// 扩展字段
	private String extendArea;
	// 商户号
	private String payShopNo;
	// 商户号订单号
	private String shopFlowNo;
	// 参考号
	private String referenceNum;
	//现金账单来源
	private String cashBillSource;

	private Integer requireRefund=1; //是否能退款，0否，1是
	
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

	
	public String getOriPayFlowNo() {
		return oriPayFlowNo;
	}

	public void setOriPayFlowNo(String oriPayFlowNo) {
		this.oriPayFlowNo = oriPayFlowNo;
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

	public String getHisFlowNo() {
		return hisFlowNo;
	}

	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
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

	public String getOrderStateName() {
		return orderStateName;
	}

	public void setOrderStateName(String orderStateName) {
		this.orderStateName = orderStateName;
	}

	public String getTradeDatetimeStr() {
		return tradeDatetimeStr;
	}

	public void setTradeDatetimeStr(String tradeDatetimeStr) {
		this.tradeDatetimeStr = tradeDatetimeStr;
	}

	public String getCredentialsNo() {
		return credentialsNo;
	}

	public void setCredentialsNo(String credentialsNo) {
		this.credentialsNo = credentialsNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getPatCode() {
		return patCode;
	}

	public void setPatCode(String patCode) {
		this.patCode = patCode;
	}

	public String getAgtCode() {
		return agtCode;
	}

	public void setAgtCode(String agtCode) {
		this.agtCode = agtCode;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String getTerminalIp() {
		return terminalIp;
	}

	public void setTerminalIp(String terminalIp) {
		this.terminalIp = terminalIp;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getPatType() {
		return patType;
	}

	public void setPatType(String patType) {
		this.patType = patType;
	}

	public String getWsCode() {
		return wsCode;
	}

	public void setWsCode(String wsCode) {
		this.wsCode = wsCode;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
	}

	public String getPayLocation() {
		return payLocation;
	}

	public void setPayLocation(String payLocation) {
		this.payLocation = payLocation;
	}

	public String getMzCode() {
		return mzCode;
	}

	public void setMzCode(String mzCode) {
		this.mzCode = mzCode;
	}

	public String getPatTypeName() {
		if(null != patType && patType.equals(EnumTypeOfInt.PAT_TYPE_MZ.getValue())){
			patTypeName = EnumTypeOfInt.PAT_TYPE_MZ.getCode();
		}else if(null != patType && patType.equals(EnumTypeOfInt.PAT_TYPE_ZY.getValue())) {
			patTypeName = EnumTypeOfInt.PAT_TYPE_ZY.getCode();
		}else if(null != patType && patType.equals(EnumTypeOfInt.PAT_TYPE_ZY.getValue())) {
			patTypeName = EnumTypeOfInt.PAT_TYPE_QT.getCode();
		}
		return patTypeName;
	}

    public String getExtendArea() {
        return extendArea;
    }

    public void setExtendArea(String extendArea) {
        this.extendArea = extendArea;
    }

    public void setPatTypeName(String patTypeName) {
        this.patTypeName = patTypeName;
    }

	public String getPayShopNo() {
		return payShopNo;
	}

	public void setPayShopNo(String payShopNo) {
		this.payShopNo = payShopNo;
	}

	public String getShopFlowNo() {
		return shopFlowNo;
	}

	public void setShopFlowNo(String shopFlowNo) {
		this.shopFlowNo = shopFlowNo;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getCashBillSource() {
		return cashBillSource;
	}

	public void setCashBillSource(String cashBillSource) {
		this.cashBillSource = cashBillSource;
	}

	public Integer getRequireRefund() {
		return requireRefund;
	}

	public void setRequireRefund(Integer requireRefund) {
		this.requireRefund = requireRefund;
	}

	@Override
	public String toString() {
		return "HisTransactionFlow [orgNo=" + orgNo + ", orgName=" + orgName + ", payType=" + payType + ", payFlowNo="
				+ payFlowNo + ", payAmount=" + payAmount + ", tradeDatatime=" + tradeDatatime + ", orderState="
				+ orderState + ", payBusinessType=" + payBusinessType + ", businessFlowNo=" + businessFlowNo
				+ ", custName=" + custName + ", cashier=" + cashier + ", hisFlowNo=" + hisFlowNo + ", patType="
				+ patType + ", patCode=" + patCode + ", mzCode=" + mzCode + ", billSource=" + billSource
				+ ", payShopNo=" + payShopNo + ", shopFlowNo=" + shopFlowNo + "]";
	}

	
	
}
