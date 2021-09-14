package com.yiban.rec.domain.vo;

import com.google.gson.Gson;
import com.yiban.rec.util.FieldMetaGroupEnum;
import com.yiban.rec.util.FieldMetaTypeEnum;

/**
 * @author swing
 * @date 2018年7月13日 下午3:35:36 类说明 应用所有字段配置
 */
public class AppRuntimeConfig {

	//通用属性
//	@FieldMeta(name = "机构编码", type = FieldMetaTypeEnum.TEXT)
//	private String orgCode;

	//账单解析专用
	@FieldMeta(name = "地址", type = FieldMetaTypeEnum.TEXT, group = FieldMetaGroupEnum.BILL_EMAIL)
	private String host;
	@FieldMeta(name = "账号", type = FieldMetaTypeEnum.TEXT, group = FieldMetaGroupEnum.BILL_EMAIL)
	private String userName;
	@FieldMeta(name = "密码", type = FieldMetaTypeEnum.TEXT, group = FieldMetaGroupEnum.BILL_EMAIL)
	private String password;
	@FieldMeta(name = "端口号", type = FieldMetaTypeEnum.TEXT, group = FieldMetaGroupEnum.BILL_EMAIL)
	private Integer port;
	@FieldMeta(name = "发送者", type = FieldMetaTypeEnum.TEXT, group = FieldMetaGroupEnum.BILL_EMAIL)
	private String from;
	@FieldMeta(name = "协议类型", type = FieldMetaTypeEnum.SELECT, group = FieldMetaGroupEnum.BILL_EMAIL,
			options={"pop3:POP3","smtp:SMTP","imap:IMAP"})
	private String type;


	//对账专用
//	@FieldMeta(name = "是否区分账单来源", type = FieldMetaTypeEnum.RADIO, options={"1:是","0:否"},defaultValue="0")
//	private String isBillsSources;
//	@FieldMeta(name = "是否区分门诊", type = FieldMetaTypeEnum.RADIO, options={"1:是","0:否"},defaultValue="0")
//	private String isOutpatient;
//	@FieldMeta(name = "是否医保对账", type = FieldMetaTypeEnum.RADIO, options={"1:是","0:否"},defaultValue="0")
//	private String isHealthAccount;
//	@FieldMeta(name = "医保对账类型", type = FieldMetaTypeEnum.RADIO, 
//	        options = {"two_rec:两方对账", "three_rec:三方对账" }, defaultValue = "two_rec")
//	private String healthCheckWays;
//	@FieldMeta(name = "对账方式", type = FieldMetaTypeEnum.RADIO, 
//	        options={"two_rec:两方对账","three_rec:三方对账"},defaultValue="two_rec")
//	private String checkWays;
	@FieldMeta(name = "对账类型", type = FieldMetaTypeEnum.CHECKBOX,
			options={"0249:微信","0349:支付宝","0559:医保","0149:银行","1649:聚合支付","2649:武进一卡通","0049:现金","3649:云闪付","jdwechat:金蝶微信","ccbbank:建设银行卡","pfbank:浦发银行卡","0449:社保卡银行卡","0549:网银"},defaultValue="0249")
	private String recType;
	@FieldMeta(name = "对账周期", type = FieldMetaTypeEnum.SELECT,
			options={"0:T+0","1:T+1","2:T+2","3:T+3"},defaultValue="1")
	private String checkTime;
	@FieldMeta(name = "医保对账金额类型", type = FieldMetaTypeEnum.CHECKBOX,
			options={"costAll:医疗总费用","costBasic:基本医疗费用","costAccount:账户支付金额","costCash:现金支付金额","costWhole:统筹支付金额","costRescue:大病救助基金支付","costSubsidy:公务员补助支付"},
			defaultValue="costAll,costBasic,costAccount,costCash,costWhole,costRescue,costSubsidy")
	private String healthAmountType;
	@FieldMeta(name = "退款是否审核", type = FieldMetaTypeEnum.RADIO, options={"1:是","0:否"},defaultValue="0")
	private String isRefundExamine="0";

	@FieldMeta(name = "是否显示各个页面的统计", type = FieldMetaTypeEnum.RADIO, options={"1:是","0:否"},defaultValue="0")
	private String isDisplay;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

//	public String getOrgCode() {
//		return orgCode;
//	}
//
//	public void setOrgCode(String orgCode) {
//		this.orgCode = orgCode;
//	}

//	public String getIsBillsSources() {
//		return isBillsSources;
//	}
//
//	public void setIsBillsSources(String isBillsSources) {
//		this.isBillsSources = isBillsSources;
//	}
//
//	public String getIsOutpatient() {
//		return isOutpatient;
//	}
//
//	public void setIsOutpatient(String isOutpatient) {
//		this.isOutpatient = isOutpatient;
//	}

	public String getRecType() {
		return recType;
	}

	public void setRecType(String recType) {
		this.recType = recType;
	}

//	public String getCheckWays() {
//		return checkWays;
//	}
//
//	public void setCheckWays(String checkWays) {
//		this.checkWays = checkWays;
//	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

//	public String getIsHealthAccount() {
//		return isHealthAccount;
//	}
//
//	public void setIsHealthAccount(String isHealthAccount) {
//		this.isHealthAccount = isHealthAccount;
//	}
//
//	public String getHealthCheckWays() {
//		return healthCheckWays;
//	}
//
//	public void setHealthCheckWays(String healthCheckWays) {
//		this.healthCheckWays = healthCheckWays;
//	}

	public String getHealthAmountType() {
		return healthAmountType;
	}

	public void setHealthAmountType(String healthAmountType) {
		this.healthAmountType = healthAmountType;
	}

	public String getIsRefundExamine() {
		return isRefundExamine;
	}

	public void setIsRefundExamine(String isRefundExamine) {
		this.isRefundExamine = isRefundExamine;
	}

	public String getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String isDisplay) {
		this.isDisplay = isDisplay;
	}

	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

}
