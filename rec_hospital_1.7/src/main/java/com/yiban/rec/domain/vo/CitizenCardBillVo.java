package com.yiban.rec.domain.vo;

public class CitizenCardBillVo {
	private String orgCode;
    private String startDate;
    private String endDate;
    private String payFlowNo;
    private String orderState;
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getPayFlowNo() {
		return payFlowNo;
	}
	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
}
