package com.yiban.rec.domain.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecQueryVo {
	
	/**
	 * 第几页
	 */
	private int page;

	/**
	 * 每页的条数
	 */

	private int rows;
	
	//机构编码
	private String orgNo;

	//交易类型
	private String tradeType;
	
	//系统流水号
	private String sysSerial;
	
	//交易状态
	private String tradeState;
	
	//支付渠道
	private String payType;
	
	//终端号
	private String payTermNo;
	
	//商户号
	private List<String> payShopNoList = new ArrayList<String>();
	
	//投资银行
	private String bankTypeId;
	
	//业务类型
	private String businessType;
	
	//设备编码
	private String deviceNo;
	
	//开始时间
	private Date startDate;
	
	//结束时间
	private Date endDate;
	private String startTime;
	private String endTime;
	
	//是否存在单边账
	private String isDifferent;
	//平台流水号
	private String flowNo;
	//支付流水号
	private String payFlowNo;
	
	private String orgName;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getSysSerial() {
		return sysSerial;
	}

	public void setSysSerial(String sysSerial) {
		this.sysSerial = sysSerial;
	}

	public String getTradeState() {
		return tradeState;
	}

	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getIsDifferent() {
		return isDifferent;
	}

	public void setIsDifferent(String isDifferent) {
		this.isDifferent = isDifferent;
	}
	public List<String> getPayShopNoList() {
		return payShopNoList;
	}

	public void setPayShopNoList(List<String> payShopNoList) {
		this.payShopNoList = payShopNoList;
	}

	public String getBankTypeId() {
		return bankTypeId;
	}

	public void setBankTypeId(String bankTypeId) {
		this.bankTypeId = bankTypeId;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getPayTermNo() {
		return payTermNo;
	}

	public void setPayTermNo(String payTermNo) {
		this.payTermNo = payTermNo;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getPayFlowNo() {
		return payFlowNo;
	}

	public void setPayFlowNo(String payFlowNo) {
		this.payFlowNo = payFlowNo;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
}
