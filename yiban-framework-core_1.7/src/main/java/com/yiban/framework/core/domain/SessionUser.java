package com.yiban.framework.core.domain;

import java.util.Date;

/**
 * 当前登录用户基本信息接口
 * 
 */
public interface SessionUser {

    Long getId();

    String getLoginName();

    String getName();

    Date getLastLoginTime();
}
