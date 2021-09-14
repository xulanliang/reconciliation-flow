package com.yiban.rec.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.yiban.rec.util.CommonConstant;


/**
 * @author swing
 * @date 2018年1月19日 下午4:20:11 类说明
 */
@Component
@ConfigurationProperties(prefix = "activemq")
public class YiBanActiveMqConfig {
	private String user;
	private String password;
	private String address;
	private String hospitalId;
	private String isconnect=CommonConstant.MQ_ISCONNECT;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getIsconnect() {
		return isconnect;
	}
	public void setIsconnect(String isconnect) {
		this.isconnect = isconnect;
	}

}
