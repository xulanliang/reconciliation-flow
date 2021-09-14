package com.yiban.framework.core.api.email.vo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮件VO类
 * 
 * @author swing
 *
 * @date 2016年8月22日 上午10:05:36
 */
public class EmailVo implements Serializable{
	private String[] to;
	private String[] cc;
	//标题 必须参数
	private String subject;
	//内容 必须参数
	private String content;
	//编码
	private String encode = "UTF-8";
	//还发发送html
	private boolean isHtml = false;
	private List<File> attachList = new ArrayList<>();

	public EmailVo() {

	}

	public EmailVo(String to, String[] cc, String subject, String content){
		this(new String[]{to},cc,subject,content,false,"UTF-8");
	}
	
	public EmailVo(String[] to, String[] cc, String subject, String content){
		this(to,cc,subject,content,false,"UTF-8");
	}
	
	public EmailVo(String to, String[] cc, String subject, String content, boolean isHtml,String encode) {
		this(new String[]{to},cc,subject,content,isHtml,encode);
	}
	public EmailVo(String[] to, String[] cc, String subject, String content, boolean isHtml,String encode) {
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.content = content;
		this.isHtml = isHtml;
		this.encode=encode;
	}

	
	
	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}
	
	public void setCc(String cc) {
		this.cc = new String[]{cc};
	}
	

	

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public void setTo(String to) {
		this.to = new String[] { to };
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}

	public void addAttchment(File file) {
		attachList.add(file);
	}

	public List<File> geAattachList() {
		return this.attachList;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

}
