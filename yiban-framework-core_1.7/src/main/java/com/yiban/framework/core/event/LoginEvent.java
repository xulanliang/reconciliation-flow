package com.yiban.framework.core.event;

import java.util.Date;

import com.yiban.framework.core.domain.SessionUser;

public final class LoginEvent implements EventData {

	public static final String TOPIC = "web.login.topic";

	private final Date date;
	private final SessionUser user;
	private String loginIp;

	

	public LoginEvent(SessionUser user, Date date, String loginIp) {
		this.user = user;
		this.date = date;
		this.loginIp = loginIp;
	}

	public Date getDate() {
		return date;
	}

	public SessionUser getUser() {
		return user;
	}

	public String getLoginIp() {
		return loginIp;
	}
}
