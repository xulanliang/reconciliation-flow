package com.yiban.framework.core.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.DynamicSpecifications;

import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;



@Transactional(readOnly = true)
public abstract class BaseService extends DynamicSpecifications {
	protected final Specs builtinSpecs = new Specs();
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
    protected List<Map<String, Object>> query(String sql){
	    List<Map<String, Object>> list = new ArrayList<>();
	    Session session = entityManager.unwrap(Session.class);
	    SQLQuery query = session.createSQLQuery(sql);
	    list = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	    return list;
	}

	public static class Specs {
		//父节点为null
		public <T> Specification<T> parentIsNull() {
			return new Specification<T>() {
				@Override
				public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Path<Object> path = root.get("parent");
					return cb.isNull(path);
				}
			};
		}

		//父节点为0
		public <T> Specification<T> parentIsZero() {
			return new Specification<T>() {
				@Override
				public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Path<Object> path = root.get("pid");
					return cb.equal(path, 0);
				}
			};
		}
		
		//未删除
		public <T> Specification<T> notDelete() {
			return new Specification<T>() {
				@Override
				public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Path<String> exp = root.get("isDeleted");
					return cb.notEqual(exp, DeleteEnum.YES.getValue());
				}
			};
		}
		//已激活
		public <T> Specification<T> isActived() {
			return new Specification<T>() {
				@Override
				public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Path<String> exp = root.get("isActived");
					return cb.equal(exp,ActiveEnum.YES.getValue());
				}
			};
		}
	}
 
}
