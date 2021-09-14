package com.yiban.rec.domain.basicInfo;

import javax.persistence.Transient;


/**
 * 微信账号信息管理
 */
public class WeichatAccount{

	private String id;
	
	@Transient
	private String hid;
	//API密钥  key  签名用 商户号中设置 加密
	private String wxKey;
	
	//appId
	private String appId;
	
	//加密 Appsecret
	private String securityKey;
	
	//二维码超时时间（分钟）
	private String qrCodeTimeout;
	
	//渠道接口地址
	private String channelUrl;
	
	//账单路径
	private String billPath;
	
	//商户号
	private String mchId;
	
	//sub_mch_id
	private String subMchId;
	
	//证书
	private byte[] certData;
	
	//加密 证书密码 默认商户号
	private String certPasswd;
	
	//open_id
	private String openId;
	
	//H5 / APP
	private String type;
	
	// 系统编码
	private String systemCode;
	
	//机构编码
	private String orgCode;
	
	//机构名称
	private String orgName;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWxKey() {
		return wxKey;
	}

	public void setWxKey(String wxKey) {
		this.wxKey = wxKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getSubMchId() {
		return subMchId;
	}

	public void setSubMchId(String subMchId) {
		this.subMchId = subMchId;
	}

	public byte[] getCertData() {
		return certData;
	}

	public void setCertData(byte[] certData) {
		this.certData = certData;
	}

	public String getCertPasswd() {
		return certPasswd;
	}

	public void setCertPasswd(String certPasswd) {
		this.certPasswd = certPasswd;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getQrCodeTimeout() {
		return qrCodeTimeout;
	}

	public void setQrCodeTimeout(String qrCodeTimeout) {
		this.qrCodeTimeout = qrCodeTimeout;
	}

	public String getChannelUrl() {
		return channelUrl;
	}

	public void setChannelUrl(String channelUrl) {
		this.channelUrl = channelUrl;
	}

	public String getBillPath() {
		return billPath;
	}

	public void setBillPath(String billPath) {
		this.billPath = billPath;
	}
	
}
