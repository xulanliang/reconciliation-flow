package com.yiban.rec.domain.vo;

import java.util.Date;

public class TradeCheckFollowQueryVo {
	
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
	//开始时间
	private Date startDate;
	//结束时间
	private Date endDate;
	//开始时间字符串
	private String startTime;
	//结束时间字符串
	private String endTime;
	// 对账状态
	private Integer checkState;
	
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

	public Integer getCheckState() {
		return checkState;
	}

	public void setCheckState(Integer checkState) {
		this.checkState = checkState;
	}

}
