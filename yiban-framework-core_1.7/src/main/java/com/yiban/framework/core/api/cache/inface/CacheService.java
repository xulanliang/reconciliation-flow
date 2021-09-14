package com.yiban.framework.core.api.cache.inface;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存服务接口
 * 
 * @author swing
 *
 * @date 2016年7月19日 上午11:08:22
 */
public interface CacheService {
	/**
	 * 
	 * @param key
	 * @param value
	 */
    Set<String> keys(String pa);
	void set(String key, Object value);

	/**
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 */

	void set(String key, Object value, long timeout);

	/**
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param timeout
	 *            缓存失效时间
	 * @param unit
	 *            缓存失效时间单位
	 */
	void set(String key, Object value, long timeout, TimeUnit unit);

	Object get(String key);

	Object get(String arg0, long arg1, long arg2);

	/**
	 * 根据key 删除缓存
	 * 
	 * @param key
	 */
	void del(String key);

	/**
	 * 根据key数组删除缓存
	 * 
	 * @param keys
	 */

	void del(String... keys);

	void del(Collection<String> keys);

	/**
	 * 是否存在某个key
	 * 
	 * @param key
	 * @return
	 */

	boolean hasKey(String key);

	/**
	 * 从列表左边添加一个对象
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Long leftPush(String key, Object value);

	Long leftPush(String key, Object pivot, Object value);

	/**
	 * 从列表左边添加一组对象
	 * 
	 * @param key
	 * @param values
	 * @return
	 */

	Long leftPushAll(String key, Collection<Object> values);

	/**
	 * 从列表左边添加一组对象
	 * 
	 * @param key
	 * @param values
	 * @return
	 */

	Long leftPushAll(String key, Object... values);

	/**
	 * 从列表左边取出一个对象
	 * 
	 * @param key
	 * @return
	 */

	Object leftPop(String key);

	/**
	 * 从列表左边取出一个对象
	 * 
	 * @param key
	 * @param timeout
	 *            失效时间
	 * @param unit
	 *            失效单位
	 * @return
	 */

	Object leftPop(String key, long timeout, TimeUnit unit);

	/**
	 * 范围检索
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @retu*
	 */
	List<Object> range(String key, int start, int end);

	/**
	 * 移除
	 * 
	 * @param key
	 * @param i
	 * @param value
	 */
	public void remove(String key, long i, String value);

	/**
	 * 置值
	 * 
	 * @param key
	 * @param index
	 * @param value
	 */
	public void set(String key, long index, Object value);

	/**
	 * 数据存储容量
	 * 
	 * @return
	 */
	long dbSize();
}
