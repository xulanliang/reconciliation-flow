package com.yiban.rec.domain.vo;

public class EmailConfig {
	private String host;
	private int port;
	private String type;
	private String userName;
	private String password;
	private String from;
	private String orgCode;

	public EmailConfig(String host, int port, String type, String userName, String password, String from,String orgCode) {
		super();
		this.host = host;
		this.port = port;
		this.type = type;
		this.userName = userName;
		this.password = password;
		this.from = from;
		this.orgCode=orgCode;
	}

	public EmailConfig() {
	}

	public String getHost() {
		return host;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
