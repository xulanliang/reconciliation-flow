package com.yiban.framework.core.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * @author swing
 * @date 2018年4月9日 下午5:10:55 类说明
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE-5)
public class PropertyUtil implements EnvironmentAware {
	private static Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		PropertyUtil.environment = environment;
	}

	public static String getProperty(String key, String defaultValue) {
		return environment.getProperty(key, defaultValue);
	}
}
