package com.yiban.rec.domain.basicInfo;

/**
 * <p>
 * 文件名称:PayConfig.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:支付配置管理实体
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年6月13日下午3:13:03
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
public class PayConfig {

	private int id;

	// 系统编码
	private String systemCode;

	// 机构编码
	private String orgCode;

	private String orgName;

	// 加密密钥
	private String payKey;
	// 回调地址. 用户支付成功、关闭订单、异常支付退款回调业务系统地址
	private String notifyUrl;
	// 支付宝网页支付时，必传。页面跳转同步通知页面路径 支付宝处理完请求后，当前页面自动跳转到商户网站里指定页面的http路径
	private String returnAlih5url;
	// 支付宝网页支付时，必传。商品展示网址 用户付款中途退出返回商户网站的地址
	private String showAlih5url;
	// 支付超时时间
	private Long payTimeOut;
	// 支付模式：1.普通商户模式，2.服务商模式
	private Integer serviceModel;

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

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReturnAlih5url() {
		return returnAlih5url;
	}

	public void setReturnAlih5url(String returnAlih5url) {
		this.returnAlih5url = returnAlih5url;
	}

	public String getShowAlih5url() {
		return showAlih5url;
	}

	public void setShowAlih5url(String showAlih5url) {
		this.showAlih5url = showAlih5url;
	}

	public Long getPayTimeOut() {
		return payTimeOut;
	}

	public void setPayTimeOut(Long payTimeOut) {
		this.payTimeOut = payTimeOut;
	}

	public Integer getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(Integer serviceModel) {
		this.serviceModel = serviceModel;
	}

}
