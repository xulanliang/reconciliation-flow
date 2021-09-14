package com.yiban.rec.service.base;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.mysql.jdbc.PreparedStatement;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.runtimecfg.service.RuntimePropertyService;

/**
 * 
 * @title 处理createNativeQuery 事项
 * @description 包括分页处理 以及nativesql查询出对象数组转map
 * 
 * @author liyunqing
 * @date 2015-12-22下午3:29:53
 */
public class BaseOprService extends BaseService {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private DataSource dataSource;

	@Autowired
	private RuntimePropertyService runtimePropertyService;

	/**
	 * 返回sql查询的所有集合
	 * 
	 * @param sql
	 * @param resultClass
	 * @return
	 * @author lihangang
	 */
	protected <T> List<T> handleNativeSql(String sql, Class<T> resultClass) {
		javax.persistence.Query query = entityManager.createNativeQuery(sql, resultClass);
		List<T> list = (List<T>) query.getResultList();
		return list;
	}

	/**
	 * 返回sql查询的分页集合
	 * 
	 * @param sql
	 * @param pageable
	 * @param resultClass
	 * @return
	 * @author lihangang
	 */
	protected <T> Page<T> handleNativeSql(String sql, Pageable pageable, Class<T> resultClass) {
		List<T> list = new ArrayList<T>();
		int count = getTotalElements(sql);
		if (count > 0) {
			javax.persistence.Query query = entityManager.createNativeQuery(sql, resultClass);
			if (pageable != null) {
				query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
				query.setMaxResults(pageable.getPageSize());
			}

			list = (List<T>) query.getResultList();
		}
		PageImpl<T> page = new PageImpl<T>(list, pageable, count);
		return page;
	}

	/**
	 * 返回sql查询的分页集合 元素为map key值为columns
	 * 
	 * @param sql
	 *            具体执行sql 查询sql包含多列
	 * @param pageRequest
	 *            pageRequest对象，分页使用
	 * @param columns
	 *            nativesql查询出为对象数组，将其转换为map，columns为列名数组，顺序和sql语句中列一致 例如{"列名1","列名2"}
	 * @return
	 */
	protected Page<Map<String, Object>> handleNativeSql(String sql, PageRequest pageRequest, String[] columns) {
		List<Map<String, Object>> list = handleSql(sql, pageRequest, columns);

		PageImpl<Map<String, Object>> page = new PageImpl<Map<String, Object>>(list, pageRequest, getTotalElements(sql));
		return page;
	}

	protected List<Map<String, Object>> handleNativeSqlList(String sql, PageRequest pageRequest, String[] columns) {
		List<Map<String, Object>> list = handleSql(sql, pageRequest, columns);
		return list;
	}

	protected Page<Map<String, Object>> handleNativeSql(List<Map<String, Object>> list, PageRequest pageRequest, int count) {
		PageImpl<Map<String, Object>> page = new PageImpl<Map<String, Object>>(list, pageRequest, count);
		return page;
	}

	/**
	 * 返回sql查询的所有集合 元素为map key值为columns
	 * 
	 * @param sql
	 *            具体执行sql 查询sql包含多列
	 * @param columns
	 *            columns nativesql查询出为对象数组，将其转换为map，columns为列名数组，顺序和sql语句中列一致 例如{"列名1","列名2"}
	 * @return
	 */
	protected List<Map<String, Object>> handleNativeSql(String sql, String[] columns) {
		List<Map<String, Object>> list = handleSql(sql, null, columns);
		return list;
	}

	/**
	 * 单列结果集查询
	 * 
	 * @param sql
	 * @param pageRequest
	 *            分页对象
	 * @return
	 */
	protected List<Object> handleNativeSql4SingleCol(String sql, PageRequest pageRequest) {
		javax.persistence.Query query = entityManager.createNativeQuery(sql);

		if (pageRequest != null) {
			query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
			query.setMaxResults(pageRequest.getPageSize());
		}
		List<Object> list = query.getResultList();
		return list;
	}

	/**
	 * 单列结果集查询
	 * 
	 * @param sql
	 * @return
	 */
	protected List<Object> handleNativeSql4SingleCol(String sql) {
		return handleNativeSql4SingleCol(sql, null);
	}

	/**
	 * 返回sql查询的所有集合 元素为map key值为从0以此开始
	 * 
	 * @param sql
	 *            具体执行sql 查询sql包含多列
	 * @return
	 */
	protected List<Map<String, Object>> handleNativeSql(String sql) {
		List<Map<String, Object>> list = handleSql(sql, null, null);
		return list;
	}
	
	/**
	 * 返回sql查询的所有集合 元素为map key值为从0以此开始
	 * 
	 * @param sql
	 *            具体执行sql 查询sql包含多列
	 * @return
	 */
	protected List<Map<String, Object>> handleNativeSqlColumns(String sql,String[] columns) {
		List<Map<String, Object>> list = handleSql(sql, null, columns);
		return list;
	}

	/**
	 * 根据sql 只查询一列时调用
	 * 
	 * @param sql
	 *            只查询一列数据并且以为一行数据时使用
	 * @return
	 */
	protected Object handleNativeSql4SingleRes(String sql) {
		javax.persistence.Query query = entityManager.createNativeQuery(sql);
		return query.getSingleResult();
	}

	/**
	 * 执行原生sql 采用PreparedStatement
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int executeUpdate(String sql, Object... params) {
		int r = 0;
		java.sql.Connection conn = null;
		try {
			conn=dataSource.getConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		PreparedStatement prest = null;
		try {
			prest = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 1; i <= params.length; i++) {
				prest.setObject(i, params[i - 1]);
			}
			r = prest.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (prest != null) {
				System.out.println("Hibernate: /* dynamic native SQL query */  " + prest);
				try {
					prest.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}

		return r;
	}

	private List<Map<String, Object>> handleSql(String sql, PageRequest pageRequest, String[] columns) {
		javax.persistence.Query query = entityManager.createNativeQuery(sql);

		if (pageRequest != null) {
			query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
			query.setMaxResults(pageRequest.getPageSize());
		}
		List<Object[]> list = query.getResultList();

		List<Map<String, Object>> list_ = Lists.newArrayList();
		Map<String, Object> map = null;

		String key = "0";
		for (Object[] arrs : list) {
			map = new HashMap<String, Object>();
			for (int i = 0; i < arrs.length; i++) {
				key = columns == null ? i + "" : columns[i];
				map.put(key, arrs[i]);
			}
			list_.add(map);
		}

		return list_;
	}

	/**
	 * 调用sql查询返回map对象
	 * 
	 * @param sql
	 * @param pageRequest
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> createSQLQuery(String sql, PageRequest pageRequest, Object... params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		if (pageRequest != null) {
			query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
			query.setMaxResults(pageRequest.getPageSize());
		}
		if (params != null && params.length != 0) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	protected Page<Map<String, Object>> findPage(String sql, PageRequest pageRequest, Object... params) {
		List<Map<String, Object>> list = createSQLQuery(sql, pageRequest, params);
		StringBuffer sbuf = new StringBuffer("select count(*) total from ( " + sql + ") t");
		List<Map<String, Object>> dblist = createSQLQuery(sbuf.toString(), params);
		long total = 0;
		if (dblist != null && dblist.size() != 0) {
			total = Long.parseLong(dblist.get(0).get("total").toString());
		}
		PageImpl<Map<String, Object>> page = new PageImpl<Map<String, Object>>(list, pageRequest, total);
		return page;
	}

	/**
	 * 调用sql查询返回map对象
	 * 
	 * @param sql
	 * @param pageRequest
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> createSQLQuery(String sql, Object... params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		if (params != null && params.length != 0) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}

		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	/**
	 * 更新数据库操作
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int execute(String sql, Object... params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		if (params != null && params.length != 0) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.executeUpdate();
	}

	/**
	 * 获取总记录数
	 * 
	 * @param sql
	 * @return
	 */
	private Integer getTotalElements(String sql) {
		StringBuffer sbuf = new StringBuffer("select count(*) from ( ");
		sbuf.append(sql);
		sbuf.append(") t");
		javax.persistence.Query query = entityManager.createNativeQuery(sbuf.toString());
		List<Object> result = query.getResultList();

		entityManager.clear();
		return Integer.parseInt(result.get(0).toString());
	}
	
	public String getSum(String sql) {
		javax.persistence.Query query = entityManager.createNativeQuery(sql);
		List<Object> result = query.getResultList();

		entityManager.clear();
		return result.get(0).toString();
	}

	/**
	 * 获取Session对象
	 * 
	 * @return
	 */
	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	/**
	 * 判断是否admin登录
	 * 
	 * @param loginName
	 * @return
	 */
	public Boolean isAdminLogin(String loginName) {
		if ("admin".equals(loginName))
			return true;

		String administrators = runtimePropertyService.getPropertyByKey("application.administrators");
		if (StringUtils.isNotEmpty(administrators) && administrators.indexOf(loginName) != -1)
			return true;

		return false;
	}
	
	/**
	 * 查询数据集合
	 * @param sql 查询sql sql中的参数用:name格式
	 * @param params 查询参数map格式，key对应参数中的:name
	 * @param clazz 实体类型为空则直接转换为map格式
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected List queryList(String sql, Map<String, Object> params, Class<?> clazz) {
		Session session = entityManager.unwrap(org.hibernate.Session.class);
		SQLQuery query = session.createSQLQuery(sql);
		if (params != null) {
			for (String key : params.keySet()) {
			    Object values = params.get(key);
			    if(values instanceof Collection) {
			        query.setParameterList(key, (Collection)values);
			    }else {
			        query.setParameter(key, params.get(key));
			    }
			}
		}
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = query.list();
		if(null == clazz) {
			return result;
		}
		List<Object> entityList = convert(clazz, result);
		return convertClass(entityList, clazz);
	}
	
	/**
	 * 结果转换
	 * @param clazz
	 * @param list
	 * @return
	 */
	private List<Object> convert(Class<?> clazz, List<Map<String, Object>> list) {
		List<Object> result;
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		result = new ArrayList<Object>();
		try {
			PropertyDescriptor[] props = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
			for (Map<String, Object> map : list) {
				Object obj = clazz.newInstance();
				for (String key : map.keySet()) {
					String attrName = key.toLowerCase();
					for (PropertyDescriptor prop : props) {
						attrName = removeUnderLine(attrName);
						if (!attrName.equals(prop.getName())) {
							continue;
						}
						Method method = prop.getWriteMethod();
						Object value = map.get(key);
						if (value != null) {
							value = ConvertUtils.convert(value, prop.getPropertyType());
						}
						if(null != method && null != value) {
							method.invoke(obj, value);
						}
					}
				}
				result.add(obj);
			}
		} catch (Exception e) {
			throw new RuntimeException("数据转换错误");
		}
		return result;
	}

	/**
	 * 将object转为泛型
	 * @param entityList
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> convertClass(List<Object> entityList,Class<T> clazz){
		List<T> ls = new ArrayList<>();
		for (Object object : entityList) {
			T ts = (T) object;
			ls.add(ts);
		}
		return ls;
	}
	
	/**
	 * 去除下划线
	 * @param attrName
	 * @return
	 */
	private String removeUnderLine(String attrName) {
		// 去掉数据库字段的下划线
		if (attrName.contains("_")) {
			String[] names = attrName.split("_");
			String firstPart = names[0];
			String otherPart = "";
			for (int i = 1; i < names.length; i++) {
				String word = names[i].replaceFirst(names[i].substring(0, 1), 
				        names[i].substring(0, 1).toUpperCase());
				otherPart += word;
			}
			attrName = firstPart + otherPart;
		}
		return attrName;
	}
}
