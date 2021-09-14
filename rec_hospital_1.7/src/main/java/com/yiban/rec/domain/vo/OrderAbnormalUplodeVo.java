package com.yiban.rec.domain.vo;

import java.io.Serializable;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.yiban.rec.service.GroupBy.GroupA;
import com.yiban.rec.service.GroupBy.GroupB;
import com.yiban.rec.service.GroupBy.GroupC;
import com.yiban.rec.service.GroupBy.GroupD;


public class OrderAbnormalUplodeVo implements Serializable {

	private static final long serialVersionUID = 300269234040851399L;

	@Length(max=32,message="机构编码长度不能超过32")
	private String orgCode;
	
	@Length(max=64,message="业务系统订单号长度不能超过32")
	private String outTradeNo;
	
	@NotBlank(message = "交易金额不能为空",groups=GroupA.class)
	@Length(max=12,message="交易金额长度不能超过11",groups=GroupB.class)
	@DecimalMin(value="0.01",message="交易金额不能小于0.01",groups=GroupC.class)
	@Digits(fraction=2,message="交易金额最小单位为分", integer = 8,groups=GroupD.class)
	private String payAmount;
	
	@NotBlank(message = "交易时间不能为空")
	@Length(max=20,message="交易时间长度不能超过20")
	private String tradeDateTime;
	
	@NotBlank(message = "支付类型不能为空")
	@Length(max=4,message="支付类型长度不能超过4")
	private String payType;
	
	@NotBlank(message = "账单来源不能为空")
	@Length(max=8,message="账单来源长度不能超过8")
	private String billSource;
	
	@Length(max=64,message="诊卡号长度不能超过64")
	private String visitNumber;
	
	@NotBlank(message = "患者姓名不能为空")
	@Length(max=20,message="患者姓名长度不能超过20")
	private String custName;
	
	@Length(max=64,message="终端号长度不能超过64")
	private String terminalNumber;
	
	@Length(max=64,message="收费员/设备编码长度不能超过64")
	private String cashier;
	
	@Length(max=512,message="异常原因信息长度不能超过512")
	private String orderStateRemark;
	
	@Length(max=64,message="HIS流水号长度不能超过64")
	private String hisOrderNo;
	
	@Length(max=64,message="支付流水号长度不能超过64")
	private String tsn;

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getTradeDateTime() {
		return tradeDateTime;
	}

	public void setTradeDateTime(String tradeDateTime) {
		this.tradeDateTime = tradeDateTime;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBillSource() {
		return billSource;
	}

	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}

	public String getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getTerminalNumber() {
		return terminalNumber;
	}

	public void setTerminalNumber(String terminalNumber) {
		this.terminalNumber = terminalNumber;
	}

	public String getCashier() {
		return cashier;
	}

	public void setCashier(String cashier) {
		this.cashier = cashier;
	}

	public String getOrderStateRemark() {
		return orderStateRemark;
	}

	public void setOrderStateRemark(String orderStateRemark) {
		this.orderStateRemark = orderStateRemark;
	}

	public String getHisOrderNo() {
		return hisOrderNo;
	}

	public void setHisOrderNo(String hisOrderNo) {
		this.hisOrderNo = hisOrderNo;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	
}
