package com.yiban.framework.core.api.sms.vo;

import java.io.Serializable;

/**
 * 短信模板对象
 * 
 * @author swing
 *
 * @date 2016年9月21日 下午6:29:44
 */
public class SmsVo implements Serializable {
	private static final long serialVersionUID = 1L;
	// 融联云短信接口需要根据模板id发送短信
	private String templateId;
	// 融联云短信接口需要根据模板参数
	private String[] datas;
	// 短信接收人
	private String to;
	// 短信内容
	private String contents;



	public SmsVo() {

	}

	public SmsVo(String[] datas, String to) {
		this.datas = datas;
		this.to = to;
	}

	public SmsVo(String to, String[] datas) {
		this.datas = datas;
		this.to = to;
	}

	public SmsVo(String templateId, String[] datas, String to, String contents) {
		this.templateId = templateId;
		this.datas = datas;
		this.to = to;
		this.contents = contents;
		
	}

	public SmsVo(String to, String templateId, String[] datas) {
		this.to = to;
		this.templateId = templateId;
		this.datas = datas;

	}

	

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String[] getDatas() {
		return datas;
	}

	public void setDatas(String[] datas) {
		this.datas = datas;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getTo() {
		return to;
	}



	
	public void setTo(String to) {
		this.to = to;
	}

	

	

	@Override
	public String toString() {
	  String json= String.format("\"to\":\"%s\",\"templateId\":\"%s\","
	  		+ "\"contents\":\"%s\"", 
			  to,templateId,contents);
	  if(datas !=null && datas.length >0){
		  String t="";
		  for(String d:datas){
			  if(t.length() >0){
				  t+=",";
			  }
			  t+="\""+d+"\"";
		  }
		  t="["+t+"]";
		  json+=",\"datas\":"+t;
	  }
	  
	  return "{"+json+"}";
	}
	public static void main(String[] args) {
		String[] datas={"221","3"};
		SmsVo v=new SmsVo("135254581", "6525",datas);
		System.out.println(v);
	}
}
