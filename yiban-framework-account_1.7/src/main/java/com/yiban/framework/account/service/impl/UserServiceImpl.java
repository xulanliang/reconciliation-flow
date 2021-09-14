package com.yiban.framework.account.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.dao.UserDao;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.UserService;
import com.yiban.framework.core.service.BaseService;

/**
 * 用户管理service实现类
 * @author tantian
 * @date   2018-01-23
 */
@Service
@Transactional
public class UserServiceImpl extends BaseService implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	AccountService accountService;

	/**
	 * 分页查询
	 * @param searchFilters
	 * @param pageable
	 * @return
	 */
	@Override
	public Page<User> findList(Collection<SearchFilter> searchFilters, Pageable pageable) {
		Specifications<User> spec = Specifications.where(bySearchFilter(searchFilters, User.class)).and(isDeleted());
		return userDao.findAll(spec, pageable);
	}

	/**
	 * 去除已删除用户
	 * @param <T>
	 * @return
	 */
	public <T> Specification<T> isDeleted() {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path<Object> path = root.get("isDeleted");
				return cb.equal(path, 0);
			}
		};
	}

	/**
	 * 新增用户
	 * @param user
	 * @return
	 */
	@Override
	public void saveUser(User user) {
		accountService.registerUser(user);
	}

	/**
	 * 修改用户
	 * @param user
	 */
	@Override
	public void updateUser(User user) {
		accountService.updateUser(user);
	}

	/**
	 * 删除用户
	 * @param user
	 */
	@Override
	public void deleteUser(User user) {
		accountService.deleteUser(user.getId());
	}

	/**
	 * 查询条件转换
	 * @param q
	 * @return
	 */
	private Specification<User> convertToSpecitication(userQuery q) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				if (!StringUtils.isEmpty(q.getName())) {
					Path<String> exp = root.get("name");
					predicates.add(cb.equal(exp, q.getName().trim()));
				}

				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
	}

	public User queryUserByUsername(String username) {
		return userDao.findByLoginName(username);
	}
}
