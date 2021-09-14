package com.yiban.framework.core.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author swing
 * @date 2018年1月11日 上午10:24:06 类说明
 */
@Component
@ConfigurationProperties(prefix = "app.config")
public class AppConfig {
	private String prefix;
	private String name;
	private String version;
	private String startYear;
	// 默认为远程模式
	private Boolean remoteUi = true;
	//默认服务器地址
	private String uiHost="http://120.76.244.19:9090";

	//ui资源远程地址
	public Boolean getRemoteUi() {
		return remoteUi;
	}

	public void setRemoteUi(Boolean remoteUi) {
		this.remoteUi = remoteUi;
	}

    //如果是本地模式,则服务器地址为空字符
	public String getUiHost() {
		if(! this.getRemoteUi()){
			return "";
		}else{
			return uiHost;
		}
	}

	public void setUiHost(String uiHost) {
		this.uiHost = uiHost;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

}
