package com.yiban.framework.account.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.impl.AuditorAwareImpl;
import com.yiban.framework.core.service.SessionUserService;

/**
 * @author swing
 * @date 2018年1月26日 下午2:32:12 类说明
 *  自动审计功能开启
 */
@Configuration
//开启自动审计功能
@EnableJpaAuditing
@EnableTransactionManagement
public class AuditorConfiguration {
	// 自动审计
	@Bean
	public AuditorAware<User> auditorProvider(SessionUserService sessionUserService) {
		return new AuditorAwareImpl(sessionUserService);
	}
}
