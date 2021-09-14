package com.yiban.rec.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
*<p>文件名称:TnalysisResult.java
*<p>
*<p>文件描述:本类描述
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:账单解析结果表
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年6月16日上午11:03:05</p>
*<p>
*@author fangzuxing
 */
public class BillUpload{
	
	private int id;
	//系统编码
	private String systemCode;
	
	//机构编码
	private String orgCode; 
	
	//支付渠道
	private String payChannel;
	
	//订单时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date orderDate;
	
	//状态
	private Integer uploadState;
	
	//文件id
	private String fileId;
	private String payCode;
	private String payState;
	private String refundState;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	private Date createDate;

	
	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}


	public Integer getUploadState() {
		return uploadState;
	}

	public void setUploadState(Integer uploadState) {
		this.uploadState = uploadState;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPayCode() {
		return payCode;
	}

	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}

	public String getPayState() {
		return payState;
	}

	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getRefundState() {
		return refundState;
	}

	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}

}
