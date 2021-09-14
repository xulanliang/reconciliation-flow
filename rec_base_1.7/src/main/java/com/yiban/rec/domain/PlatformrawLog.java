package com.yiban.rec.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 银医流水日志
 */
@Entity
@Table(name = "t_platformraw_log")
public class PlatformrawLog implements Serializable{

	private static final long serialVersionUID = -5169048091267751402L;

	@Id
	private Integer flowRawId;
	// 机构编码
	private String orgNo;;
	
	// 系统流水号
	private String flowNo;

	// 交易来源
	private String tradeFrom;
	
	//交易来源名称
	@Transient
	private String tradeFromName;

	// 交易目的
	private String tradeTo;
	
	@Transient
	private String tradeToName;


	// 支付来源
	private String paySource;

	// 支付类型
	private String payType;
	
	// 支付终端号
	private String rawData;
	
	// 更新人
	private String updatedBy;

	// 更新时间
	private Date updatedTime;

	// 响应码
	private String responseCode;
	
	//响应编码值
	@Transient
	private String responseValue;
	
	//交易日期
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date tradeDate;


	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getTradeFrom() {
		return tradeFrom;
	}

	public void setTradeFrom(String tradeFrom) {
		this.tradeFrom = tradeFrom;
	}

	public String getTradeTo() {
		return tradeTo;
	}

	public void setTradeTo(String tradeTo) {
		this.tradeTo = tradeTo;
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

	public Integer getFlowRawId() {
		return flowRawId;
	}

	public void setFlowRawId(Integer flowRawId) {
		this.flowRawId = flowRawId;
	}

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseValue() {
		return responseValue;
	}

	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getTradeFromName() {
		return tradeFromName;
	}

	public void setTradeFromName(String tradeFromName) {
		this.tradeFromName = tradeFromName;
	}

	public String getTradeToName() {
		return tradeToName;
	}

	public void setTradeToName(String tradeToName) {
		this.tradeToName = tradeToName;
	}
	
	
	
}
