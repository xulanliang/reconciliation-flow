package com.yiban.rec.bill.parse.vo;

public class FTPConfig {
	
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String ftpPath;
	private String savePath;
	private boolean isFtps;
	
	public FTPConfig(String host, Integer port, String username, String password, String ftpPath, String savePath) {
		this(host,port,username,password,ftpPath,savePath,false);
	}
	
	public FTPConfig(String host, Integer port, String username, String password, String ftpPath, String savePath,boolean isFtps) {
		super();
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.ftpPath = ftpPath;
		this.savePath = savePath;
		this.isFtps = isFtps;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFtpPath() {
		return ftpPath;
	}
	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public boolean isFtps() {
		return isFtps;
	}

	public void setFtps(boolean isFtps) {
		this.isFtps = isFtps;
	}
}
