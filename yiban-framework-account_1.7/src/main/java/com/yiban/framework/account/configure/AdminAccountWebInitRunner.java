package com.yiban.framework.account.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yiban.framework.account.common.CommonContents;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.service.impl.DefaultWebInitRunner;

/**
 * @author swing
 * @date 2018年1月10日 下午1:54:33 类说明
 */
@Component
public class AdminAccountWebInitRunner extends DefaultWebInitRunner {
	@Autowired 
	private AccountService accountService;

	@Override
	public void excute() throws Exception {
		User user = accountService.findUserByLoginName(CommonContents.DEFAULT_ADMIN_LOGIN_NAME);
		if (user == null) {
			// 默认新增一个超级管理员账号
			User u = new User();
			u.setName(CommonContents.DEFAULT_ADMIN_NAME);
			u.setLoginName(CommonContents.DEFAULT_ADMIN_LOGIN_NAME);
			u.setPlainPassword(CommonContents.DEFAULT_ADMIN_LOGIN_PASSWORD);
			accountService.registerUser(u);
		}
	}
}
