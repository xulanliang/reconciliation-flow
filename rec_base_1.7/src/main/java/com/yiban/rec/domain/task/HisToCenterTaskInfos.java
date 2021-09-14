package com.yiban.rec.domain.task;

import java.util.List;

/** 
* @ClassName: HisToCenterTaskInfos 
* @Description: HisToCenterTaskInfos
* @author tuchun@clearofchina.com 
* @date 2017年4月13日 下午9:09:38 
* @version V1.0 
*  
*/
public class HisToCenterTaskInfos<T> {
	private String orgNo;;
	/**
	 * 1 平台账单 2 his账单 3 第三方账单
	 */
	private Integer billType;
	private Integer startPosition;
	private Integer endPosition;
	private Integer total;
	private String billDate;
	
	//订单发送状态
	private String sendState;
	List<T> dataList;


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

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getSendState() {
		return sendState;
	}

	public void setSendState(String sendState) {
		this.sendState = sendState;
	}
	
}
