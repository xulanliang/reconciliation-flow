package com.yiban.rec.domain.vo;

public class BillConfigVo {

	private int  billType;
	
	private String billDelimiter;
	
	//映射配置集合
	private String billMapper;
	
	private int billPayType;
	
	private String passWord;

	//pdf的地址
	private String url;

	private String userName;
	
	private String configHost;
	
	private String configProt;
	
	//协议
	private String configAgreement;
	
	//保存本地地址
	private String configAdd;
	
	private String configRowStart;
	
	private String configLineStart;
	
	private String configFlter;
	
	private String configPay;
	
	private String configSplit;

	public int getBillType() {
		return billType;
	}

	public void setBillType(int billType) {
		this.billType = billType;
	}

	public String getBillDelimiter() {
		return billDelimiter;
	}

	public void setBillDelimiter(String billDelimiter) {
		this.billDelimiter = billDelimiter;
	}

	public String getBillMapper() {
		return billMapper;
	}

	public void setBillMapper(String billMapper) {
		this.billMapper = billMapper;
	}

	public int getBillPayType() {
		return billPayType;
	}

	public void setBillPayType(int billPayType) {
		this.billPayType = billPayType;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConfigHost() {
		return configHost;
	}

	public void setConfigHost(String configHost) {
		this.configHost = configHost;
	}

	public String getConfigProt() {
		return configProt;
	}

	public void setConfigProt(String configProt) {
		this.configProt = configProt;
	}

	public String getConfigAgreement() {
		return configAgreement;
	}

	public void setConfigAgreement(String configAgreement) {
		this.configAgreement = configAgreement;
	}

	public String getConfigAdd() {
		return configAdd;
	}

	public void setConfigAdd(String configAdd) {
		this.configAdd = configAdd;
	}

	public String getConfigRowStart() {
		return configRowStart;
	}

	public void setConfigRowStart(String configRowStart) {
		this.configRowStart = configRowStart;
	}

	public String getConfigLineStart() {
		return configLineStart;
	}

	public void setConfigLineStart(String configLineStart) {
		this.configLineStart = configLineStart;
	}

	public String getConfigFlter() {
		return configFlter;
	}

	public void setConfigFlter(String configFlter) {
		this.configFlter = configFlter;
	}

	public String getConfigPay() {
		return configPay;
	}

	public void setConfigPay(String configPay) {
		this.configPay = configPay;
	}

	public String getConfigSplit() {
		return configSplit;
	}

	public void setConfigSplit(String configSplit) {
		this.configSplit = configSplit;
	}
	
}
