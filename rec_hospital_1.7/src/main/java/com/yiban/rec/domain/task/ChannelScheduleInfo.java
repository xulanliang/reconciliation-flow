package com.yiban.rec.domain.task;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yiban.rec.domain.BaseEntityEx;

/**
 * 
 * @ClassName: ChannelScheduleInfo
 * @Description: 获取渠道数据任务实体类【t_rec_channel_schedule_cfg】
 * @author tuchun@clearofchina.com
 * @date 2017年3月28日 下午7:44:42
 * @version V1.0
 *
 */

@Entity
@Table(name = ChannelScheduleInfo.TABLE_NAME)
public class ChannelScheduleInfo extends BaseEntityEx {

	private static final long serialVersionUID = -8761182278910863179L;
	public static final String TABLE_NAME = "t_rec_channel_schedule_cfg";

	/**
	 * job执行类
	 */
	private String jobClass;
	/**
	 * con表达式
	 */
	private String jobCorn;
	/**
	 * job描述
	 */
	private String jobDesc;
	/**
	 * job执行前拦截接口
	 */
	private String jobInterface;
	/**
	 * 任务名称
	 */
	private String jobName;
	/**
	 * job执行参数
	 */
	private String jobParam;
	/**
	 * 1:运行 0:停止
	 */
	private Integer jobsStatus;
	/**
	 * job开始时间
	 */
	private String startat;

	/**
	 * 机构ID
	 */
	private String orgNo;
	/**
	 * 支付渠道ID
	 */
	private Long metaPayId;
	@Transient
	private Integer cycleType1;
	@Transient
	private Integer cycleType2;
	@Transient
	private Integer cycleType3;
	@Transient
	private Integer cycleType4;

	public Integer getCycleType1() {
		return cycleType1;
	}

	public void setCycleType1(Integer cycleType1) {
		this.cycleType1 = cycleType1;
	}

	public Integer getCycleType2() {
		return cycleType2;
	}

	public void setCycleType2(Integer cycleType2) {
		this.cycleType2 = cycleType2;
	}

	public Integer getCycleType3() {
		return cycleType3;
	}

	public void setCycleType3(Integer cycleType3) {
		this.cycleType3 = cycleType3;
	}

	public Integer getCycleType4() {
		return cycleType4;
	}

	public void setCycleType4(Integer cycleType4) {
		this.cycleType4 = cycleType4;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getJobCorn() {
		return jobCorn;
	}

	public void setJobCorn(String jobCorn) {
		this.jobCorn = jobCorn;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public String getJobInterface() {
		return jobInterface;
	}

	public void setJobInterface(String jobInterface) {
		this.jobInterface = jobInterface;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobParam() {
		return jobParam;
	}

	public void setJobParam(String jobParam) {
		this.jobParam = jobParam;
	}

	public Integer getJobsStatus() {
		return jobsStatus;
	}

	public void setJobsStatus(Integer jobsStatus) {
		this.jobsStatus = jobsStatus;
	}
	

	public String getStartat() {
		return startat;
	}

	public void setStartat(String startat) {
		this.startat = startat;
	}

	public Long getMetaPayId() {
		return metaPayId;
	}

	public void setMetaPayId(Long metaPayId) {
		this.metaPayId = metaPayId;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}


}
