package com.yiban.rec.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiban.framework.core.domain.base.IdEntity;

/**
*<p>文件名称:ServiceMonitor.java
*<p>
*<p>文件描述:服务监测显示表
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:
</p>
*@author fangzuxing
 */
@Entity
@Table(name = "t_center_service_monitor")
public class CenterServiceMonitor extends IdEntity{

	private static final long serialVersionUID = -762766590177860716L;

	//机构编码
	private String orgNo;;
	
	//通知方式
	private String noticeWay;
	
	@Transient
	private String noticeWayValue;
	
	//联系人
	private String contacts;
	
	//邮箱
	private String email;
	
	//监测间隔时间
	private String intervalTime;
	
	//状态
	private int state;
	
	@Transient
	private String stateValue;
	
	//节点信息
	private String nodeMsg;
	
	private Date createdDate;
	
    //最后监测时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date lastMonitorTime;
	
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getLastMonitorTime() {
		return lastMonitorTime;
	}

	public void setLastMonitorTime(Date lastMonitorTime) {
		this.lastMonitorTime = lastMonitorTime;
	}

	public String getNodeMsg() {
		return nodeMsg;
	}

	public void setNodeMsg(String nodeMsg) {
		this.nodeMsg = nodeMsg;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getNoticeWayValue() {
		return noticeWayValue;
	}

	public void setNoticeWayValue(String noticeWayValue) {
		this.noticeWayValue = noticeWayValue;
	}

	public String getStateValue() {
		return stateValue;
	}

	public void setStateValue(String stateValue) {
		this.stateValue = stateValue;
	}
}
