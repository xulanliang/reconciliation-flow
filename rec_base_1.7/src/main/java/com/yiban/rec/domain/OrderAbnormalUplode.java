package com.yiban.rec.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;

/**
 * 异常订单上送表
 * @author Administrator
 *
 */

@Entity
@Table(name = "t_order_abnormal_uplode",
    indexes= {
        @Index(name="orgCodeIndex", columnList = "orgCode", unique=false),
        @Index(name="tsnIndex", columnList = "tsn", unique=false)
})
public class OrderAbnormalUplode extends IdEntity{

	private static final long serialVersionUID = 1L;
	
	private String orgCode;
	
	private String outTradeNo;
	
	private String payAmount;
	
	//数据时间
	private String tradeDateTime;
	
	private String payType;
	
	private String billSource;
	
	private String visitNumber;
	
	private String custName;
	
	private String terminalNumber;
	
	private String cashier;
	
	private String orderStateRemark;
	
	private String hisOrderNo;
	
	private String tsn;
	
	private Date createTime;

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
