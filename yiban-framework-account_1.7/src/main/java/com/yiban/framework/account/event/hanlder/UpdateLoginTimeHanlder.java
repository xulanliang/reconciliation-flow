package com.yiban.framework.account.event.hanlder;



import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.event.LoginEvent;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;

/**
 * @author swing
 * @date 2018年1月22日 下午2:48:01 更新用户登录时间 类说明
 */
@Consumer
public class UpdateLoginTimeHanlder{
	@Autowired
	private Reactor reactor;
	
	@Autowired
	private AccountService accountService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// 处理用户登录事件
	@Selector(LoginEvent.TOPIC)
	public void updateLoginTime(Event<LoginEvent> evt) {
		logger.info("==更新用户登录时间==");
		LoginEvent data = evt.getData();
		SessionUser user = data.getUser();
		Date date = data.getDate();
		if(null != user) {
		    accountService.updateUserLastLoginTime(user.getLoginName(), date);
		}
	}
}
