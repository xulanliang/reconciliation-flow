package com.yiban.rec.domain.task;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.yiban.rec.domain.BaseEntityEx;

/**
 * 
 * @ClassName: HisToCenterTaskInfo
 * @Description: HIS上传数据到中心的任务实体类【t_schedule_cfg】
 * @author tuchun@clearofchina.com
 * @date 2017年3月28日 下午7:47:38
 * @version V1.0
 *
 */

@Entity
@Table(name = HisToCenterTaskInfo.TABLE_NAME)
public class HisToCenterTaskInfo extends BaseEntityEx {

	private static final long serialVersionUID = -5177773202502133202L;

	public static final String TABLE_NAME = "t_his_to_center_schedule";

	/**
	 * job执行类
	 */
	private String jobclass;
	/**
	 * com表达式
	 */
	private String jobcorn;
	/**
	 * job描述
	 */
	private String jobdesc;
	/**
	 * job执行前拦截接口
	 */
	private String jobinterface;
	/**
	 * 任务名称
	 */
	private String jobname;
	/**
	 * job执行参数
	 */
	private String jobparam;
	/**
	 * 1:运行 0:停止
	 */
	private Integer jobsstatus;
	/**
	 * job开始时间
	 */
	private String startat;
	/**
	 * 机构编码
	 */
	private String orgNo;;
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
	public String getJobclass() {
		return jobclass;
	}
	public void setJobclass(String jobclass) {
		this.jobclass = jobclass;
	}
	public String getJobcorn() {
		return jobcorn;
	}
	public void setJobcorn(String jobcorn) {
		this.jobcorn = jobcorn;
	}
	public String getJobdesc() {
		return jobdesc;
	}
	public void setJobdesc(String jobdesc) {
		this.jobdesc = jobdesc;
	}
	public String getJobinterface() {
		return jobinterface;
	}
	public void setJobinterface(String jobinterface) {
		this.jobinterface = jobinterface;
	}
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public String getJobparam() {
		return jobparam;
	}
	public void setJobparam(String jobparam) {
		this.jobparam = jobparam;
	}
	public Integer getJobsstatus() {
		return jobsstatus;
	}
	public void setJobsstatus(Integer jobsstatus) {
		this.jobsstatus = jobsstatus;
	}
	
	public String getStartat() {
		return startat;
	}
	public void setStartat(String startat) {
		this.startat = startat;
	}
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	
}
