package com.yiban.rec.domain.basicInfo;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.yiban.rec.domain.BaseEntityEx;

/**
 * 
 * @ClassName: RetryOfTaskFailInfo
 * @Description: hisToCenter任务失败重试
 * @author tuchun@clearofchina.com
 * @date 2017年3月28日 下午7:47:38
 * @version V1.0
 *
 */

@Entity
@Table(name = RetryOfHisToCenterTaskFailInfo.TABLE_NAME)
public class RetryOfHisToCenterTaskFailInfo extends BaseEntityEx {

	private static final long serialVersionUID = -1876845371347092880L;
	public static final String TABLE_NAME = "t_rec_retry_task_fail";
	/**
	 * 机构编码
	 */
	private String orgNo;;
	/**
	 * 1 平台账单 2 his账单 3 第三方账单
	 */
	private Integer billType;
	/**
	 * 开始位置
	 */
	private Integer startPosition;
	/**
	 * 结束位置
	 */
	private Integer endPosition;
	/**
	 * 总数
	 */
	private Integer total;
	/**
	 * 账单日期
	 */
	private String billDate;
	/**
	 * 上传状态 1成功 0失败 2 已经签收
	 */
	private Integer status;
	/**
	 * 发送次数
	 */
	private Integer sendAmount;
	/**
	 * 备注信息
	 */
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
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
