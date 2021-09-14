package com.yiban.framework.core.configure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 
 * @author swing
 *
 * @date 2018年1月11日 下午2:13:21
 * 缓存管理器配置
 */
@Configuration
@EnableCaching
public class EhCacheCacheManagerConfiguration {
	
	//如果存在其他的CacheManager实现，则此配置无效
	@ConditionalOnMissingBean(CacheManager.class)
	@Bean
	public CacheManager cacheManager(EhCacheManagerFactoryBean ehCacheManagerFactoryBean) {
		return new EhCacheCacheManager(ehCacheManagerFactoryBean.getObject());
	}
	

	//ehcache管理器配置:后面shiro，或者spring cache缓存使用
	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean(){
		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("conf/ehcache/ehcache.xml"));
		//一个jvm共享一个实例
		cacheManagerFactoryBean.setShared(true);
		return cacheManagerFactoryBean;
	}
}
