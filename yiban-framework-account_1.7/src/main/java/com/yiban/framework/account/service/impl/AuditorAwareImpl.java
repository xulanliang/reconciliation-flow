package com.yiban.framework.account.service.impl;

import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.annotation.Transactional;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.service.SessionUserService;
/**
 * 
 * @author swing
 *
 * @date 2018年1月26日 下午2:21:35
 * 实现jpa自动注入当前用户接口 作用于 @createBy注解
 */
public class AuditorAwareImpl implements AuditorAware<User> {

    private SessionUserService sessionUserService;

    public AuditorAwareImpl(SessionUserService sessionUserService) {
        this.sessionUserService = sessionUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentAuditor() {
    	SessionUser u =sessionUserService.getCurrentSessionUser();
        return (User)u;
    }
}
