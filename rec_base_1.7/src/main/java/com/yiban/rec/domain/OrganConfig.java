package com.yiban.rec.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "t_organization_configuration")
public class OrganConfig extends BaseEntityEx{
	
	private static final long serialVersionUID = 1217210909936921734L;
	
	//机构编码
	private String orgNo;
	
	//网络状态
	private String networkState = "0079";
	
	//连接ip
	private String ip;
	
	//端口
	private String port;
	
	//对账相差时间
	private Integer recTime;
	
	//机构属性
	private String orgPro;
	
	//对账类型 2、两方对账  3、三方对账
	private String recType;
	
	@Transient
	private String recTypeName;
	
	@Transient
	private String isCashRecName;
	
	//是否现金对账
	private String isCashRec;
	//是否使用巨鼎支付平台支付（1 是 2否）
	private String payModel;
	
	//是否区分门诊住院（ true:区分；false：不区分）
	private String isPatType;


	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getNetworkState() {
		return networkState;
	}

	public void setNetworkState(String networkState) {
		this.networkState = networkState;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Integer getRecTime() {
		return recTime;
	}

	public void setRecTime(Integer recTime) {
		this.recTime = recTime;
	}

	
	
	public String getOrgPro() {
		return orgPro;
	}

	public void setOrgPro(String orgPro) {
		this.orgPro = orgPro;
	}

	public String getRecType() {
		return recType;
	}

	public void setRecType(String recType) {
		this.recType = recType;
	}

	public String getIsCashRec() {
		return isCashRec;
	}

	public void setIsCashRec(String isCashRec) {
		this.isCashRec = isCashRec;
	}

	public String getRecTypeName() {
		return recTypeName;
	}

	public void setRecTypeName(String recTypeName) {
		this.recTypeName = recTypeName;
	}

	public String getIsCashRecName() {
		return isCashRecName;
	}

	public void setIsCashRecName(String isCashRecName) {
		this.isCashRecName = isCashRecName;
	}

	public String getPayModel() {
		return payModel;
	}

	public void setPayModel(String payModel) {
		this.payModel = payModel;
	}

	public String getIsPatType() {
		return isPatType;
	}

	public void setIsPatType(String isPatType) {
		this.isPatType = isPatType;
	}

	
	
}
