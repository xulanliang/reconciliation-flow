package com.yiban.rec.domain.basicInfo;

import java.util.Date;


public class ServiceMonitorInfo{

	//机构编码
	private String orgNo;;
	
	//通知方式
	private String noticeWay;
	
	private String noticeWayValue;
	
	//联系人
	private String contacts;
	
	//邮箱
	private String email;
	
	//监测间隔时间
	private String intervalTime;
	
	//是否开启
	private Integer isOpen;
	
	private Date createdDate;
	
	private Long createdById;
	
	private Date lastModifiedDate;
	
	private Long lastModifiedById;

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getNoticeWay() {
		return noticeWay;
	}

	public void setNoticeWay(String noticeWay) {
		this.noticeWay = noticeWay;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(String intervalTime) {
		this.intervalTime = intervalTime;
	}

	public Integer getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(Integer isOpen) {
		this.isOpen = isOpen;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(Long lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNoticeWayValue() {
		return noticeWayValue;
	}

	public void setNoticeWayValue(String noticeWayValue) {
		this.noticeWayValue = noticeWayValue;
	}

}
