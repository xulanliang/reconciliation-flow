package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
 */
@Entity
@Table(name = "t_rec_retry_task_fail")
public class RecRetryTask extends BaseEntityEx {

	private static final long serialVersionUID = 1L;

	// 机构编码
	private String orgNo;;

	// 账单类型
	private Integer billType;

	private Integer startPosition;

	private Integer endPosition;

	// 总数
	private Integer total;

	// 账单时间
//	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String billDate;

	// 上传状态
	private Integer status;

	// 发送次数
	private Integer sendAmount;

	// 描述
	private String remarks;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public Integer getBillType() {
		return billType;
	}

	public void setBillType(Integer billType) {
		this.billType = billType;
	}

	public Integer getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Integer startPosition) {
		this.startPosition = startPosition;
	}

	public Integer getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Integer endPosition) {
		this.endPosition = endPosition;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSendAmount() {
		return sendAmount;
	}

	public void setSendAmount(Integer sendAmount) {
		this.sendAmount = sendAmount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
