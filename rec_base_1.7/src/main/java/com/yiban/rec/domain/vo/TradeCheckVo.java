package com.yiban.rec.domain.vo;
/**
* @author lyx
* @version 创建时间：2019年3月13日 下午5:25:45
* 类说明
*/
public class TradeCheckVo {
	private String patId;
	private String payFlowNo;
	private String custName;
	private String hisFlowNo;
	public String getPatId() {
		return patId;
	}
	public void setPatId(String patId) {
		this.patId = patId;
	}
	public String getPayFlowNo() {
		return payFlowNo;
	}
	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getHisFlowNo() {
		return hisFlowNo;
	}
	public void setHisFlowNo(String hisFlowNo) {
		this.hisFlowNo = hisFlowNo;
	}
}
