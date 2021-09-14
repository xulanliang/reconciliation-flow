package com.yiban.rec.domain.baseinfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.yiban.rec.domain.BaseEntityEx;

/**
 * 
 * @ClassName: ShopInfo
 * @Description: 商户信息【t_shop_info】
 * @author tuchun@clearofchina.com
 * @date 2017年3月28日 下午7:49:03
 * @version V1.0
 *
 */
@Entity
@Table(name = ShopInfo.TABLE_NAME)
public class ShopInfo extends BaseEntityEx {

	private static final long serialVersionUID = -3344272788523971301L;
	public static final String TABLE_NAME = "t_shop_info";

	// 支付渠道名称
	private String payName;
	// 商户号
	private String payShopNo;
	//设备编号 串
	@Transient
	private String deviceNos;

	// 机构编码
	@NotNull
	private String orgNo;

	// 应用ID
	private String applyId;
	// 描述
	private String description;
	// 业务系统简称
	private String bussShortname;

	// 连接地址
	private String serviceUrl;

	// pin算法
	private String pinAlgorithm;

	// 支付Tpdu
	private String payTpdu;
	// mac算法
	private String macAlgorithm;
	// 支付PIN密钥索引
	@Column(name = "pay_pinkey_id")
	private Integer payPinkeyId;
	// 支付Mac密钥索引
	@Column(name = "pay_mackey_id")
	private Integer payMackeyId;
	// 支付主密钥索引
	private Integer payPkeyId;
	// 支付终端号
	private String payTermNo;
	// 支付渠道id
	private Integer metaDataPayId;
	
	//银行id
	private Integer metaDataBankId;
	
	@Transient
	private String metaDataBankName;
	
	//二维码有效时间
	private String qrcodeTimeout;
	
	//订单有效时间
	private String orderTimeout;
	
	//服务器地址
	private String serviceAddress;
	
	//账单下载地址
	private String billFilePath;
	
	//公司支付宝协议ID
	private String companyPid;
	
	//微信商户支付密钥
	private String wxPayKey;
	
	//微信证书密码
	private String wxSslcertPassword;
	
	
	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}

	public String getPayShopNo() {
		return payShopNo;
	}

	public void setPayShopNo(String payShopNo) {
		this.payShopNo = payShopNo;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBussShortname() {
		return bussShortname;
	}

	public void setBussShortname(String bussShortname) {
		this.bussShortname = bussShortname;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public String getDeviceNos() {
		return deviceNos;
	}

	public void setDeviceNos(String deviceNos) {
		this.deviceNos = deviceNos;
	}

	public String getPinAlgorithm() {
		return pinAlgorithm;
	}

	public void setPinAlgorithm(String pinAlgorithm) {
		this.pinAlgorithm = pinAlgorithm;
	}

	public String getPayTpdu() {
		return payTpdu;
	}

	public void setPayTpdu(String payTpdu) {
		this.payTpdu = payTpdu;
	}

	public String getMacAlgorithm() {
		return macAlgorithm;
	}

	public void setMacAlgorithm(String macAlgorithm) {
		this.macAlgorithm = macAlgorithm;
	}


	public Integer getPayPinkeyId() {
		return payPinkeyId;
	}

	public void setPayPinkeyId(Integer payPinkeyId) {
		this.payPinkeyId = payPinkeyId;
	}

	public Integer getPayMackeyId() {
		return payMackeyId;
	}

	public void setPayMackeyId(Integer payMackeyId) {
		this.payMackeyId = payMackeyId;
	}

	public Integer getPayPkeyId() {
		return payPkeyId;
	}

	public void setPayPkeyId(Integer payPkeyId) {
		this.payPkeyId = payPkeyId;
	}

	public String getPayTermNo() {
		return payTermNo;
	}

	public void setPayTermNo(String payTermNo) {
		this.payTermNo = payTermNo;
	}


	public Integer getMetaDataPayId() {
		return metaDataPayId;
	}

	public void setMetaDataPayId(Integer metaDataPayId) {
		this.metaDataPayId = metaDataPayId;
	}

	public Integer getMetaDataBankId() {
		return metaDataBankId;
	}

	public void setMetaDataBankId(Integer metaDataBankId) {
		this.metaDataBankId = metaDataBankId;
	}

	public String getMetaDataBankName() {
		return metaDataBankName;
	}

	public void setMetaDataBankName(String metaDataBankName) {
		this.metaDataBankName = metaDataBankName;
	}

	public String getQrcodeTimeout() {
		return qrcodeTimeout;
	}

	public void setQrcodeTimeout(String qrcodeTimeout) {
		this.qrcodeTimeout = qrcodeTimeout;
	}

	public String getOrderTimeout() {
		return orderTimeout;
	}

	public void setOrderTimeout(String orderTimeout) {
		this.orderTimeout = orderTimeout;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public String getBillFilePath() {
		return billFilePath;
	}

	public void setBillFilePath(String billFilePath) {
		this.billFilePath = billFilePath;
	}

	public String getCompanyPid() {
		return companyPid;
	}

	public void setCompanyPid(String companyPid) {
		this.companyPid = companyPid;
	}

	public String getWxPayKey() {
		return wxPayKey;
	}

	public void setWxPayKey(String wxPayKey) {
		this.wxPayKey = wxPayKey;
	}

	public String getWxSslcertPassword() {
		return wxSslcertPassword;
	}

	public void setWxSslcertPassword(String wxSslcertPassword) {
		this.wxSslcertPassword = wxSslcertPassword;
	}
	
}
