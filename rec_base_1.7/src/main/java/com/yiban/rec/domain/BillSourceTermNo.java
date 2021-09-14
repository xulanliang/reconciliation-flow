package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.framework.core.domain.base.IdEntity;
/**
 * 渠道终端对应表
 * @author jxl
 * @Time 2020年11月11日下午7:18:23
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_his_report")
public class BillSourceTermNo extends IdEntity {

	private String orgCode;
	private String billSource;
	private String termNo;
	private String payType;
	
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getBillSource() {
		return billSource;
	}
	public void setBillSource(String billSource) {
		this.billSource = billSource;
	}
	public String getTermNo() {
		return termNo;
	}
	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
}
