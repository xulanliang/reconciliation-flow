package com.yiban.framework.core.api.oss.vo;

import java.io.Serializable;

public class UploadResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String STATUC_SUC="00000";
	public static final String STATUC_FAIL="10000";
	
	private String fileName="file";
	private String statusCode=UploadResult.STATUC_FAIL;
	private String resultMsg="error";
	public UploadResult(){
		
	}
	public UploadResult(String statusCode){
		this.statusCode=statusCode;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	@Override
	public String toString() {
		return String.format("{fileName:%s,statusCode:%s,resultMsg:%s}",fileName,statusCode,resultMsg);
	}
}
