package com.yiban.rec.domain.basicInfo;

/** 
* @ClassName: ChannelOrderTaskInfo
* @Description: 获取his渠道数据任务调度信息
* @author tuchun@clearofchina.com 
* @date 2017年4月5日 下午2:59:22 
* @version V1.0 
*  
*/
public class ChannelOrderTaskInfo {
	/**
	 * 机构ID
	 */
	private String orgNo;;
	/**
	 * 支付渠道ID
	 */
	private Long payType;
	/**
	 * 数据来源 1机构 2第三方支付平台 3 HisToCenter 4(机构和第三方支付平台)
	 */
	private Integer platformType;
	/**
	 * 开始日期
	 */
	private String startAt;
	/**
	 * 回调时状态 1成功 0失败 
	 */
	private Integer state;
	
	
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
	public Long getPayType() {
		return payType;
	}
	public void setPayType(Long payType) {
		this.payType = payType;
	}
	public String getStartAt() {
		return startAt;
	}
	public void setStartAt(String startAt) {
		this.startAt = startAt;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Integer getPlatformType() {
		return platformType;
	}
	public void setPlatformType(Integer platformType) {
		this.platformType = platformType;
	}
	
	
}
