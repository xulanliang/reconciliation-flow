package com.yiban.framework.core.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 方便在容器外通过静态方法获取获取容器对象
 * 
 * @author swing
 *
 * @date 2016年7月28日 下午5:36:01
 */
@Configuration
public class SpringBeanUtil implements BeanFactoryAware {
	private static BeanFactory beanFactory;
	private static DefaultListableBeanFactory listtableBeanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		SpringBeanUtil.beanFactory = beanFactory;
		SpringBeanUtil.listtableBeanFactory=(DefaultListableBeanFactory)beanFactory;
	}
	public static Object getBean(String name) throws BeansException {
		return beanFactory.getBean(name);
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return beanFactory.getBean(requiredType);
	}
	public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return beanFactory.getBean(name, requiredType);
		
	}
	public static <T> Map<String, T> getBeansOfType(Class<T> requiredType)throws BeansException{
		return listtableBeanFactory.getBeansOfType(requiredType);
	}
}
