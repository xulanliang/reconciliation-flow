package com.yiban.rec.domain.basicInfo;

/**
 * 支付宝账号信息管理
 */
public class AlipayAccount {


	private int id;
	// 合作伙伴id
	private String partner;

	// 应用私钥
	private String privateKey;

	// 支付宝公钥
	private String publicKey;

	// 字符编码
	private String inputCharset;

	// 签名类型
	private String signType;

	// 售卖者账号
	private String seller;

	// MD5密钥
	private String aliKey;

	// 应用id
	private String appId;
	
	//二维码超时时间（分钟）
	private String qrCodeTimeout;
	
	//渠道接口地址
	private String channelUrl;
	
	//账单路径
	private String billPath;
	
	//公司支付宝协议ID
	private String alipayId;

	// 系统编码
	private String systemCode;

	// 机构编码
	private String orgCode;
	
	//机构编码
	private String orgName;
	
	//账号类型
	private String type;

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getInputCharset() {
		return inputCharset;
	}

	public void setInputCharset(String inputCharset) {
		this.inputCharset = inputCharset;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getAliKey() {
		return aliKey;
	}

	public void setAliKey(String aliKey) {
		this.aliKey = aliKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getAlipayId() {
		return alipayId;
	}

	public void setAlipayId(String alipayId) {
		this.alipayId = alipayId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
