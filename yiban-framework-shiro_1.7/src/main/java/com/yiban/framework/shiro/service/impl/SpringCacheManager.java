package com.yiban.framework.shiro.service.impl;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * @author swing
 * @date 2018年1月24日 上午11:47:56 类说明
 * 扩展org.apache.shiro.cache.CacheManager 接口，
 * 使用org.springframework.cache.CacheManager接口实现
 */
public class SpringCacheManager implements org.apache.shiro.cache.CacheManager {
	private org.springframework.cache.CacheManager cacheManager;

	/**
	 * 设置spring cache manager
	 *
	 * @param cacheManager
	 *            spring cache
	 */
	public SpringCacheManager() {

	}

	public SpringCacheManager(org.springframework.cache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void setCacheManager(org.springframework.cache.CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		org.springframework.cache.Cache springCache = cacheManager.getCache(name);
		return new SpringCacheWrapper(springCache);
	}

	 class SpringCacheWrapper implements Cache {
		private org.springframework.cache.Cache springCache;

		SpringCacheWrapper(org.springframework.cache.Cache springCache) {
			this.springCache = springCache;
		}

		@Override
		public Object get(Object key) throws CacheException {
			Object value = springCache.get(key);
			if (value instanceof SimpleValueWrapper) {
				return ((SimpleValueWrapper) value).get();
			}
			return value;
		}

		@Override
		public Object put(Object key, Object value) throws CacheException {
			springCache.put(key, value);
			return value;
		}

		@Override
		public Object remove(Object key) throws CacheException {
			springCache.evict(key);
			return null;
		}

		@Override
		public void clear() throws CacheException {
			springCache.clear();
		}

		@Override
		public int size() {
			throw new UnsupportedOperationException("invoke spring cache abstract size method not supported");
		}

		@Override
		public Set keys() {
			throw new UnsupportedOperationException("invoke spring cache abstract keys method not supported");
		}

		@Override
		public Collection values() {
			throw new UnsupportedOperationException("invoke spring cache abstract values method not supported");
		}
	}

}
