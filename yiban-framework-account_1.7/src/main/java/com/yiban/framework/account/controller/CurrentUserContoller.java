package com.yiban.framework.account.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.service.SessionUserService;

/**
 * @author swing
 * @date 2018年4月9日 下午3:54:50 类说明
 */
public class CurrentUserContoller extends FrameworkController {

	@Autowired
	private SessionUserService sessionUserService;
	protected User currentUser(){
		SessionUser sessionUser =sessionUserService.forceCurrentSessionUser();
		if(sessionUser != null){
			return (User)sessionUser;
		}
		return null;
	}
}
