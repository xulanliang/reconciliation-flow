package com.yiban.rec.domain.vo;



public class SZYBVo {
	/*
	 {
		    "transTime": "20181128144959:000",
		    "transType": "DZ002",
		    "transReturnCode": "00000000",
		    "transReturnMessage": "",
		    "transVersion": "V0.2",
		    "serialNumber": "H0320201811280017790",
		    "cardArea": "440300",
		    "hospitalCode": "H0320",
		    "operatorCode": "0",
		    "operatorName": "HIS管理员",
		    "operatorPass": "",
		    "transBody": {
		    	"bad766":"20181128",
		        "pageno": 1,
		        "endpage": "1",
		        "currentpagesize": 0,
		        "totalsize": 0,
		        "totalpagecount": 0,
		        "outputlist": ""
		    },
		    "verifyCode": "000000|81711013f058d656a136f06bbac1b961",
		    "extendDeviceId": "",
		    "transChannel": "10",
		    "extendUserId": "",
		    "extendSerialNumber": "",
		    "caz055": "",
		    "aae501": ""}"
		}
		*/
	
	private String transTime;
	private String transType;
	private String transReturnCode;
	private String transReturnMessage;
	private String transVersion;
	private String serialNumber;
	private String cardArea;
	private String hospitalCode;
	private String operatorCode;
	private String operatorName;
	private String operatorPass;
	private BodyVo transBody;
	private String verifyCode;
	private String extendDeviceId;
	private String transChannel;
	private String extendUserId;
	private String extendSerialNumber;
	private String caz055;
	private String aae501;
	
	public String getOperatorCode() {
		return operatorCode;
	}
	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTransReturnCode() {
		return transReturnCode;
	}
	public void setTransReturnCode(String transReturnCode) {
		this.transReturnCode = transReturnCode;
	}
	public String getTransReturnMessage() {
		return transReturnMessage;
	}
	public void setTransReturnMessage(String transReturnMessage) {
		this.transReturnMessage = transReturnMessage;
	}
	public String getTransVersion() {
		return transVersion;
	}
	public void setTransVersion(String transVersion) {
		this.transVersion = transVersion;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getCardArea() {
		return cardArea;
	}
	public void setCardArea(String cardArea) {
		this.cardArea = cardArea;
	}
	public String getHospitalCode() {
		return hospitalCode;
	}
	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorPass() {
		return operatorPass;
	}
	public void setOperatorPass(String operatorPass) {
		this.operatorPass = operatorPass;
	}
	
	public BodyVo getTransBody() {
		return transBody;
	}
	public void setTransBody(BodyVo transBody) {
		this.transBody = transBody;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public String getExtendDeviceId() {
		return extendDeviceId;
	}
	public void setExtendDeviceId(String extendDeviceId) {
		this.extendDeviceId = extendDeviceId;
	}
	public String getTransChannel() {
		return transChannel;
	}
	public void setTransChannel(String transChannel) {
		this.transChannel = transChannel;
	}
	public String getExtendUserId() {
		return extendUserId;
	}
	public void setExtendUserId(String extendUserId) {
		this.extendUserId = extendUserId;
	}
	public String getExtendSerialNumber() {
		return extendSerialNumber;
	}
	public void setExtendSerialNumber(String extendSerialNumber) {
		this.extendSerialNumber = extendSerialNumber;
	}
	public String getCaz055() {
		return caz055;
	}
	public void setCaz055(String caz055) {
		this.caz055 = caz055;
	}
	public String getAae501() {
		return aae501;
	}
	public void setAae501(String aae501) {
		this.aae501 = aae501;
	}
	
	
	
	
	
	

}
