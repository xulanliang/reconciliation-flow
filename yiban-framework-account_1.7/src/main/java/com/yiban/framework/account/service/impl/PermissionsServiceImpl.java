package com.yiban.framework.account.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yiban.framework.account.dao.PermissionsDao;
import com.yiban.framework.account.dao.UserRoleDao;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PermissionsService;
import com.yiban.framework.core.service.BaseService;

/**
 * @author swing
 * @date 2018年1月25日 下午3:54:33 类说明
 */
@Service
public class PermissionsServiceImpl extends BaseService implements PermissionsService {
	@Autowired
	private PermissionsDao permissionsDao;
	@Autowired
	private UserRoleDao userRoleDao;

	@Override
	@Transactional
	public void saveOrUpdate(Permissions permission) {
		permissionsDao.save(permission);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		 //permissionsDao.deletePermissions(id);
		// 直接物理删除
		permissionsDao.delete(id);

	}

	@Override
	public Permissions findByPermissionName(String name) {
		return permissionsDao.findByPermissionName(name);
	}

	@Override
	public Permissions findById(Long id) {
		return permissionsDao.findOne(id);
	}

	@Override
	public List<Permissions> getUserPermissions(User user) {
        //获取角色
		List<Role> roles = findUserAllRoles(user);
		List<Permissions> result = Lists.newArrayList();
		for (Role role : roles) {
			//根据角色获取权限
			result.addAll(role.getPermissions());
		}
		return Lists.newArrayList(Sets.newHashSet(result));

	}

	private List<Role> findUserAllRoles(User user) {
		// 直接的.
		List<Role> immedRoles = userRoleDao.getAllRolesByUserId(user.getId());

		List<Role> result = Lists.newArrayList(immedRoles);
		for (Role role : immedRoles) {
			List<Role> roleParents = findRoleParents(role.getParent(), Lists.<Role> newArrayList());
			result.addAll(roleParents);
		}

		return Lists.newArrayList(Sets.newHashSet(result));
	}

	private List<Role> findRoleParents(Role role, List<Role> result) {
		if (role == null) {
			return result;
		}

		result.add(role);
		return findRoleParents(role.getParent(), result);
	}


	@Override
	public List<Permissions> findAll(Collection<SearchFilter> searchFilters) {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		Specifications<Permissions> spec = Specifications.where(bySearchFilter(searchFilters, Permissions.class)).and(builtinSpecs.notDelete()).and(builtinSpecs.isActived());
		return permissionsDao.findAll(spec, sort);
	}
	
}
