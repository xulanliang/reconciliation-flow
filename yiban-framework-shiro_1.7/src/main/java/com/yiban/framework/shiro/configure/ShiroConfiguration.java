package com.yiban.framework.shiro.configure;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Maps;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.util.PasswordUtil;
import com.yiban.framework.shiro.service.impl.ShiroDbRealm;
import com.yiban.framework.shiro.service.impl.SpringCacheManager;
import com.yiban.framework.shiro.service.impl.SubSystemCredentialsMatcher;

/**
 * 
 * @author swing
 *
 * @date 2018年1月8日 下午4:57:21 说明: spring shrio权限管配置
 */
@Configuration
@ConditionalOnWebApplication
public class ShiroConfiguration {

	@Configuration
	@ConditionalOnWebApplication
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	public static class DefaultShiroConfiguration {
		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

		@Bean
		public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
			return new LifecycleBeanPostProcessor();
		}

		@Bean
		// @ConditionalOnMissingBean
		public ShiroDbRealm shiroDbRealm(AccountService accountService) {
			return new ShiroDbRealm(accountService);
		}
		
		@Bean
		public org.apache.shiro.cache.CacheManager shrioCacheManager(CacheManager springCacheManager) {
			return new SpringCacheManager(springCacheManager);
		}

		// 默认哈希密码验证器(子系统定义后，该配置失效)
//		@Bean
//		@ConditionalOnMissingBean
//		public CredentialsMatcher credentialsMatcher() {
//			HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(PasswordUtil.HASH_ALGORITHM);
//			matcher.setHashIterations(PasswordUtil.HASH_INTERATIONS);
//			return matcher;
//		}
		@Bean
		@ConditionalOnMissingBean
		public CredentialsMatcher credentialsMatcher() {
			SubSystemCredentialsMatcher matcher = new SubSystemCredentialsMatcher(PasswordUtil.HASH_ALGORITHM);
			matcher.setHashIterations(PasswordUtil.HASH_INTERATIONS);
			return matcher;
		}

		// shiro安全管理器
		@Bean
		@ConditionalOnMissingBean
		public DefaultWebSecurityManager createWebSecurityManager(ShiroDbRealm shiroDbRealm,
				org.apache.shiro.cache.CacheManager cacheManager) {
			LOGGER.debug("初始化shiro权限安全管理器");
			// shiro 缓存配置，使用net.sf.ehcache.CacheManager管理器
			// EhCacheManagerFactoryBean ehCacheManagerFactoryBean
			// key :
			// com.yiban.framework.shiro.service.impl.ShiroDbRealm.authorizationCache
			// EhCacheManager ehCacheManager = new EhCacheManager();
			// ehCacheManager.setCacheManager(ehCacheManagerFactoryBean.getObject());

			SimpleCookie cookie = new SimpleCookie("rememberMe");
			cookie.setHttpOnly(true);
			cookie.setMaxAge(3600 * 24 * 7); // 7 天
			CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
			rememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode("4AvVhmFLUsseD0KTA3Kprsdag=="));
			rememberMeManager.setCookie(cookie);
			DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
			webSecurityManager.setRealm(shiroDbRealm);
			webSecurityManager.setCacheManager(cacheManager);
			webSecurityManager.setRememberMeManager(rememberMeManager);
			return webSecurityManager;
		}
	}

	@Configuration
	@ConditionalOnWebApplication
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	public static class Filters {
		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		// shrio过滤器配置
		@Bean
		@ConditionalOnMissingBean
		public AbstractShiroFilter shiroFilter(WebSecurityManager webSecurityManager) throws Exception {
			LOGGER.debug("初始化shiro权限过滤器");
			ShiroFilterFactoryBean ret = new ShiroFilterFactoryBean();
			ret.setSecurityManager(webSecurityManager);
			ret.setLoginUrl("/passport/login");
			ret.setSuccessUrl("/passport/dispatch");
			ret.setUnauthorizedUrl("/passport/unauthorized");
			setFilterChainDefinitionMap(ret);
			Map<String, Filter> map = Maps.newLinkedHashMap();
			map.put("user", new UserFilter() {
				@Override
				protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
					HttpServletRequest rq = (HttpServletRequest) request;
					HttpServletResponse rp = (HttpServletResponse) response;
					if ("XMLHttpRequest".equalsIgnoreCase(rq.getHeader("X-Requested-With"))) {
						rp.setStatus(528);
						rp.sendError(528, "session timeout");
						return false;
					} else {
						return super.onAccessDenied(request, response);
					}
				}
			});

			ret.setFilters(map);
			return (AbstractShiroFilter) ret.getObject();
		}

		// 过滤器拦截规则配置
		private void setFilterChainDefinitionMap(ShiroFilterFactoryBean ret) {
			Map<String, String> filterChainDefinitionMap = Maps.newLinkedHashMap();
			filterChainDefinitionMap.put("/", "anon");
			filterChainDefinitionMap.put("/assets/**", "anon");
			filterChainDefinitionMap.put("/static/**", "anon");
			filterChainDefinitionMap.put("/imgFile/**", "anon");
			filterChainDefinitionMap.put("/public/**", "anon");
			filterChainDefinitionMap.put("/favicon.ico", "anon");
			filterChainDefinitionMap.put("/usercode/**", "anon");
			filterChainDefinitionMap.put("/passport/register/**", "anon");
			filterChainDefinitionMap.put("/forgetpass/**", "anon");
			filterChainDefinitionMap.put("/passport/logout", "logout");
			filterChainDefinitionMap.put("/passport/dispatch", "user");
			filterChainDefinitionMap.put("/api/**", "anon");
			filterChainDefinitionMap.put("/admin/**", "user");
			final Resource resource = new ClassPathResource("conf/acl.properties");

			if (resource != null && resource.exists()) {
				try {
					loadFromConfig(resource, filterChainDefinitionMap);
				} catch (Exception e) {
					throw new RuntimeException("读取acl.properties出错!", e);
				}
			} else {
				LOGGER.warn("classpath:/conf/acl.properties don't exists. use defaults");
			}
			ret.setFilterChainDefinitionMap(filterChainDefinitionMap);
		}

		// 加载自定义拦截规则
		private void loadFromConfig(Resource resource, Map<String, String> filterChainDefinitionMap)
				throws IOException {
			Properties pro = PropertiesLoaderUtils.loadProperties(resource);
			Map<String, String> cutomerShiroAcl = (Map) pro;
			{
				if (!cutomerShiroAcl.isEmpty()) {
					filterChainDefinitionMap.putAll(cutomerShiroAcl);
				}
			}
		}
	}

}
