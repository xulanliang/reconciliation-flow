package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 支付渠道类型关系表
 */
@Entity
@Table(name = "t_channel_type")
public class ChannelType extends BaseEntityEx {

	private static final long serialVersionUID = -2385421860055674933L;

	//机构编码
	private String orgNo;;
	
	// 支付渠道
	private Long payChannelId;

	// 支付类型
	private Long payTypeId;
	
	//备注
	private String remarks;

	
	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public Long getPayChannelId() {
		return payChannelId;
	}

	public void setPayChannelId(Long payChannelId) {
		this.payChannelId = payChannelId;
	}

	public Long getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(Long payTypeId) {
		this.payTypeId = payTypeId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

}
