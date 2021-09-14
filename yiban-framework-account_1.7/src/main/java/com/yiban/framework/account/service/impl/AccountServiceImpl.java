package com.yiban.framework.account.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;
import org.springside.modules.utils.Clock;
import org.springside.modules.utils.Encodes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yiban.framework.account.common.CommonContents;
import com.yiban.framework.account.dao.PermissionsDao;
import com.yiban.framework.account.dao.RoleDao;
import com.yiban.framework.account.dao.RolePermissionsDao;
import com.yiban.framework.account.dao.UserDao;
import com.yiban.framework.account.dao.UserOrganizationDao;
import com.yiban.framework.account.dao.UserRoleDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.RolePermissions;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.domain.UserOrganization;
import com.yiban.framework.account.domain.UserRole;
import com.yiban.framework.account.domain.UserStatus;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.eum.ActiveEnum;
import com.yiban.framework.core.eum.DeleteEnum;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.core.util.PasswordUtil;
import com.yiban.framework.core.util.StringUtil;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl extends BaseService implements AccountService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private PermissionsDao permissionsDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private RolePermissionsDao rolePermissionsDao;
	@Autowired
	private UserRoleDao userRoleDao;
	@Autowired
	private UserOrganizationDao userOrganizationDao;
	
	@Autowired
	private OrganizationService organizationService;
	private Clock clock = Clock.DEFAULT;

	@Override
	public User findUserByLoginName(String loginName) {
		return userDao.findByLoginName(loginName);
	}

	@Override
	public User findUserByLoginID(String loginID) {
		return userDao.findUserByLoginID(loginID);
	}

	@Override
	public List<User> getAllUser() {
		return (List<User>) userDao.findAll();
	}

	@Override
	public User getUser(Long id) {
		return userDao.findOne(id);
	}

	@Override
	@Transactional
	public void registerUser(User user) {
		//生成加密盐值
		byte[] salt =PasswordUtil.createSalt();
		//根据盐值生成密码
		String entryptPassword=PasswordUtil.entryptPassword(user.getPlainPassword(), salt);
		user.setPassword(entryptPassword);
		user.setSalt(PasswordUtil.encodeSalt(salt));
		user.setRegisterDate(clock.getCurrentDate());
		userDao.save(user);
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		if (StringUtils.isNotBlank(user.getPlainPassword())) {
			String plainPassword = user.getPlainPassword();
			byte[] salt = PasswordUtil.createSalt();
			String hashPassword = PasswordUtil.entryptPassword(plainPassword, salt);
			user.setSalt(Encodes.encodeHex(salt));
			user.setPassword(hashPassword);
		}
		userDao.save(user);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		if (isSupervisor(id)) {
			throw new ServiceException("不能删除超级管理员用户");
		}
		//userDao.deleteUser(id);
		//物理删除用户
		userOrganizationDao.removeByUserId(id);
		userDao.delete(id);
	}

	/**
	 * 判断是否超级管理员.
	 */
	@Override
	public boolean isSupervisor(Long id) {
		User user =findUserById(id);
		return user!=null && user.getLoginName().equals(CommonContents.DEFAULT_ADMIN_LOGIN_NAME);
	}

	
	@Transactional
	@Override
	public void assignRolesToUser(User user, Role... roles) {
		Preconditions.checkNotNull(user);
		Preconditions.checkArgument(roles != null && roles.length > 0);

		for (Role role : roles) {
			UserRole userRole = new UserRole(user, role);
			userRoleDao.save(userRole);
		}
	}

	@Override
	public List<Role> getUserAllRoles(User user) {
		// 直接的.
		List<Role> immedRoles = userRoleDao.findAllRolesByUserId(user.getId());

		List<Role> result = Lists.newArrayList(immedRoles);
		for (Role role : immedRoles) {
			List<Role> roleParents = getRoleParents(role.getParent(), Lists.<Role> newArrayList());
			result.addAll(roleParents);
		}

		return Lists.newArrayList(Sets.newHashSet(result));
	}

	@Override
	public List<Role> findUserAllRoles(User user) {
		// 直接的.
		List<Role> immedRoles = userRoleDao.getAllRolesByUserId(user.getId());

		List<Role> result = Lists.newArrayList(immedRoles);
		for (Role role : immedRoles) {
			List<Role> roleParents = findRoleParents(role.getParent(), Lists.<Role> newArrayList());
			result.addAll(roleParents);
		}

		return Lists.newArrayList(Sets.newHashSet(result));
	}

	// 获取
	private List<Role> getRoleParents(Role role, List<Role> result) {
		if (role == null) {
			return result;
		}

		result.add(role);
		return getRoleParents(role.getParent(), result);
	}

	private List<Role> findRoleParents(Role role, List<Role> result) {
		if (role == null) {
			return result;
		}

		result.add(role);
		return findRoleParents(role.getParent(), result);
	}

	@Override
	@Transactional
	public void createRole(Role role) {
		roleDao.save(role);
	}

	@Override
	public Role findRoleByName(String name) {
		return roleDao.findByName(name);
	}

	

	@Override
	public Role findRoleByNameAndRealm(String name, String realm) {
		return roleDao.findRoleByNameAndRealm(name, realm);
	}

	@Override
	@Transactional
	public void createPermissions(Permissions permissions) {
		permissionsDao.save(permissions);
	}

	@Override
	public List<Permissions> getUserAllPermissions(User user) {
		List<Role> roles = this.findUserAllRoles(user);
		List<Permissions> result = Lists.newArrayList();

		for (Role role : roles) {
			result.addAll(role.getPermissions());
		}
		return Lists.newArrayList(Sets.newHashSet(result));
	}

	@Override
	public User findUserById(Long id) {
		return userDao.findOne(id);
	}

	@Override
	public Permissions findPermissionsById(Long id) {
		return permissionsDao.findOne(id);
	}

	@Override
	@Transactional
	public void updatePermissions(Permissions entity) {
		permissionsDao.save(entity);
	}

	@Override
	public List<Permissions> findAllPermissions() {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		return permissionsDao.findByParentIsNull(sort);
	}

	@Override
	public Page<Permissions> findAllPermissions(Pageable pageable) {
		return permissionsDao.findAll(pageable);
	}

	@Override
	@Transactional
	public void deletePermissions(Long id) {
		permissionsDao.deletePermissions(id);
	}

	@Override
	public Page<Permissions> findAllPermissions(Collection<SearchFilter> filters, Pageable pageable) {
		Specifications<Permissions> spec = Specifications.where(bySearchFilter(filters, Permissions.class))
				.and(builtinSpecs.<Permissions> parentIsNull());
		return permissionsDao.findAll(spec, pageable);
	}

	@Override
	public Role findRoleById(Long id) {
		return roleDao.findOne(id);
	}

	@Override
	@Transactional
	public void updateRole(Role role) {
		roleDao.save(role);
	}

	@Override
	@Transactional
	public void deleteRole(Long id) {
		roleDao.removeRolePermissionsByRoleId(id);
		//roleDao.deleteRole(id);
		//物理删除
		roleDao.delete(id);
	}

	@Override
	public Page<Role> findAllRoles(Collection<SearchFilter> searchFilters, Pageable pageable) {
		Specifications<Role> spec = Specifications.where(bySearchFilter(searchFilters, Role.class)).and(isDeleted());
		return roleDao.findAll(spec, pageable);
	}
	
	@Override
	public List<Role> findAllRoles(Collection<SearchFilter> searchFilters) {
		Specifications<Role> spec = Specifications.where(bySearchFilter(searchFilters, Role.class)).and(isDeleted());
		return roleDao.findAll(spec);
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

	@Override
	@Transactional
	public void addPermissionsToRole(List<RolePermissions> rolePermsList) {
		Role roles = this.findRolesById(rolePermsList.get(0).getRoleId());
		if (roles == null) {
			throw new ServiceException("id为" + rolePermsList.get(0).getRoleId() + "的角色不存在");
		}
		try {
			rolePermissionsDao.save(rolePermsList);
		} catch (Exception e) {
			logger.error("rolePermissionsDao.save(rolePermsList)", e);
			throw new ServiceException("权限不存在, ");
		}
	}

	public Role findRolesById(Long roleId) {
		return roleDao.findOne(roleId);
	}

	@Override
	public Iterable<Role> findAllRoles() {
		return roleDao.findAll();
	}


	

	@Override
	@Transactional
	public void updateStatus(Long id) {
		User user = this.findUserById(id);
		if (user == null) {
			throw new ServiceException("id为" + id + "用户不存在");
		}

		if (Objects.equal(user.getStatus(), UserStatus.active.getValue())) {
			user.setStatus(UserStatus.disabled.getValue());
		} else {
			user.setStatus(UserStatus.active.getValue());
		}
		userDao.updateStatus(user.getStatus(), id);
	}

	@Override
	public Iterable<Role> getUserRoles(Long userId) {
		List<Role> roleList = userRoleDao.getAllRolesByUserId(userId);
		Iterator<Role> ite = roleList.iterator();
		// 过滤已经删除的角色
		while (ite.hasNext()) {
			Role role = ite.next();
			if (role.getIsActived() == ActiveEnum.NO.getValue() || role.getIsDeleted() == DeleteEnum.YES.getValue()) {
				ite.remove();
			}
		}
		return roleList;
	}

	@Override
	@Transactional
	public void removeRoleFromUser(Long userId, Long roleId) {
		User user = this.findUserById(userId);
		if (user == null) {
			throw new ServiceException("id为" + userId + "用户不存在");
		}
		Role role = this.findRoleById(roleId);
		if (role == null) {
			throw new ServiceException("id为" + roleId + "的角色不存在");
		}

		UserRole userRole = userRoleDao.findByUserIdAndRoleId(userId, roleId);

		userRoleDao.delete(userRole);
	}

	@Override
	@Transactional
	public void removeRolePermissionsByRoleId(Long roleId) {
		roleDao.removeRolePermissionsByRoleId(roleId);
	}

	@Override
	@Transactional
	public void addRoleToUser(final Long userId, final Long roleId) {
		User user = this.findUserById(userId);
		if (user == null) {
			throw new ServiceException("id为" + userId + "用户不存在");
		}

		boolean exists = Iterables.any(this.getUserRoles(userId), new com.google.common.base.Predicate<Role>() {
			@Override
			public boolean apply(Role input) {
				return input.getId().equals(roleId);
			}
		});

		if (exists) {
			throw new ServiceException("该角色已经添加");
		}

		Role role = this.findRoleById(roleId);
		if (role == null) {
			throw new ServiceException("id为" + roleId + "的角色不存在");
		}

		UserRole userRole = new UserRole(user, role);
		userRoleDao.save(userRole);
	}

	@Override
	public Page<User> findUsersByOrganization(Long orgId, Pageable pageable) {
		return userOrganizationDao.findUsersByOrganizationId(orgId, pageable);
	}

	@Override
	public Page<User> findAllUsersByOrganization(Long orgId, Pageable pageable) {
		Organization org = organizationService.findOrganizationById(orgId);
		Preconditions.checkNotNull(org);

		return userOrganizationDao.findAllUsersByOrganizationCode(org.getCode(), pageable);
	}

	@Override
	public List<User> findUsersByOrganization(Long orgId) {
		return userOrganizationDao.findUsersByOrganizationId(orgId);
	}

	@Override
	public List<User> findAllUsersByOrganization(Long orgId) {
		Organization org = organizationService.findOrganizationById(orgId);
		Preconditions.checkNotNull(org);

		return userOrganizationDao.findAllUsersByOrganizationCode(org.getCode());
	}

	@Override
	@Transactional
	public void registerUserAndAddToOrganization(User user, Long orgId) {
		this.registerUser(user);
		this.addUserToOrganization(orgId, user.getId());
	}

	@Override
	@Transactional
	public void addUserToOrganization(Long orgId, Long userId) {
		Preconditions.checkNotNull(orgId, "机构Id不能为空");
		Preconditions.checkNotNull(userId, "用户Id不能为空");

		Organization org = organizationService.findOrganizationById(orgId);
		Preconditions.checkNotNull(org, "机构不存在");

		User user = this.findUserById(userId);
		Preconditions.checkNotNull(user, "用户不存在");

		UserOrganization uo = new UserOrganization(user, org);
		userOrganizationDao.save(uo);
	}

	@Override
	@Transactional
	public void updateUserAndChangeOrganization(User user, Long orgId) {
		this.updateUser(user);
		if (orgId != null) {
			// 先清除所有归属的组织机构
			// userOrganizationDao.deleteUserOrganization(user.getId());
			userOrganizationDao.deleByUserId(user.getId());
			if (orgId > 0) {
				this.addUserToOrganization(orgId, user.getId());
			}
		}
	}

	@Override
	public Long getUserOrganizationId(Long userId) {
		Organization org = getUserOrganization(userId);
		if (org == null) {
			return null;
		}
		return org.getId();
	}

	@Override
	public Organization getUserOrganization(Long userId) {
		List<Organization> orgs = userOrganizationDao.findOrganizationsByUserId(userId);
		if (orgs.isEmpty()) {
			return null;
		}
		return orgs.get(0);
	}

	@Override
	public Page<User> findAllUsersNotInOrganizations(Pageable pageable) {
		return userOrganizationDao.findUsersNotInOrganizations(pageable);
	}

	@Override
	@Transactional
	public void changeUserPassword(Long id, String newPassword) {
		User user = this.findUserById(id);
		if (user == null) {
			throw new ServiceException("id为" + id + "用户不存在");
		}
		user.setPlainPassword(newPassword);
		this.updateUser(user);
	}

	@Override
	public List<User> getUsersByRole(Role role) {
		return roleDao.findUsersByRoleId(role.getId());
	}

	@Override
	public List<Permissions> findPermissionsByRole(Long roleId, Sort sort) {
		return permissionsDao.findPermissionsByRole(roleId, sort);
	}

	@Override
	public List<Permissions> findPermissionsByRoleId(Long roleId, Sort sort) {
		return permissionsDao.findPermissionsById(roleId, sort);
	}

	@Override
	public List<Long> findPermissionsByRoles(Long roleId, Sort sort) {
		return permissionsDao.findPermissionsByRoles(roleId, sort);
	}

	@Transactional
	@Override
	public Future<Boolean> updateUserLastLoginTime(String loginID, Date time) {
		User user = this.findUserByLoginID(loginID);
		if (user == null) {
			return new AsyncResult<Boolean>(false);
		}

		user.setLastLoginTime(time);
		userDao.save(user);

		return new AsyncResult<Boolean>(true);
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	@Override
	public Role findByRoleName(String name) {
		return roleDao.findByRoleName(name);
	}

	@Override
	public User findValideUserByLoginName(String loginName) {
		return userDao.findValideUserByLoginName(loginName);
	}

	@Override
	public Boolean findValideUserByEmail(String email, Long id) {
		if (email == null || email.equals(""))
			return true;
		if (id == null)
			return userDao.findValideUserByEmail(email) == null ? true : false;
		else
			return userDao.findValideUserByEmail(email, id) == null ? true : false;
	}

	@Override
	public Boolean findValideUserByPhone(String mobilePhone, Long id) {
		if (mobilePhone == null || mobilePhone.equals(""))
			return true;
		if (id == null)
			return userDao.findValideUserByPhone(mobilePhone) == null ? true : false;
		else
			return userDao.findValideUserByPhone(mobilePhone, id) == null ? true : false;
	}

	@Override
	public Permissions findByPermissionName(String name) {
		return permissionsDao.findByPermissionName(name);
	}

	@Override
	public User findValideUserByPhone(String mobilePhone) {
		return userDao.findValideUserByPhone(mobilePhone);
	}

	@Override
	public Page<Role> getRolesByUserId(Long id, Pageable pageable) {
		return userRoleDao.getRolesByUserId(id, pageable);
	}

	@Override
	public Page<User> findAllUsers(Collection<SearchFilter> searchFilters, Pageable pageable,String name) {
		Specifications<User> spec = Specifications.where(bySearchFilter(searchFilters, User.class));
		Specification<User> specification = new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = Lists.newArrayList();
				Path<String> loginNameExp = root.get("name");
				//排除系统管理员
				predicates.add(cb.notEqual(loginNameExp, CommonContents.DEFAULT_ADMIN_LOGIN_NAME));
				//排除当前登录用户
				predicates.add(cb.notEqual(root.get("id"), getCurrentSessionUser().getId()));
				
				if(StringUtils.isNotBlank(name)) {
					predicates.add(cb.like(loginNameExp, "%"+name+"%"));
				}
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		spec = spec.and(Specifications.where(specification));
		return userDao.findAll(spec, pageable);
	}

	@Override
	public boolean isCurrentUserPassword(String plainPassword) {
		return false;
		
	}

	@Override
	public SessionUser getCurrentSessionUser() {
		try {
			final org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
			if (subject == null) {
				return null;
			}
			return (SessionUser) subject.getPrincipal();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public User getCurrentUser() {
		final SessionUser sessionUser = getCurrentSessionUser();
		if (sessionUser == null) {
			return null;
		}
		if (!(sessionUser instanceof User)) {
			throw new RuntimeException("getCurrentUser, sessionUser not instanceof User");
		}
		return (User) sessionUser;
	}

	@Override
	public User forceCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public int editUserRole(Long userId, String roleIds) {
		try {
			userRoleDao.deleteUserIdRole(userId);
			String[] roleIdList = roleIds.split(",");
			Long roleId;
			for(String r:roleIdList){
				if(StringUtil.isNullOrEmpty(r)){
					continue;
				}
				try {
					roleId=Long.parseLong(r);
					this.addRoleToUser(userId, roleId);
				} catch (NumberFormatException e) {
					return 2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		}	
		return 1;
	}	
}
