package com.yiban.framework.shiro.service.impl;

import org.apache.shiro.authc.UsernamePasswordToken;

public class SubSystemToken extends UsernamePasswordToken {

	private static final long serialVersionUID = -2564928913725078138L;

	private LoginType type;

	public SubSystemToken() {
		super();
	}

	public SubSystemToken(String username, String password, LoginType type, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
		this.type = type;
	}

	/** 免密登录 */
	public SubSystemToken(String username) {
		super(username, "", false, null);
		this.type = LoginType.NOPASSWD;
	}

	/** 账号密码登录 */
	public SubSystemToken(String username, String password) {
		super(username, password, false, null);
		this.type = LoginType.PASSWORD;
	}

	public LoginType getType() {
		return type;
	}

	public void setType(LoginType type) {
		this.type = type;
	}

	public enum LoginType {
		PASSWORD("password"), // 密码登录
		NOPASSWD("nopassword"); // 免密登录

		private String code;// 状态值

		private LoginType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}

}
