package com.yiban.framework.account.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.RolePermissions;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.SessionUser;

public interface AccountService {

    /**
     * 通过登录名查询用户.
     * 
     * @param loginname
     *            登录名
     */
    User findUserByLoginName(String loginname);
    
    /**
     * 通过登录名查询没有被删除的有效用户
     * @param loginName
     * @return
     */
    User findValideUserByLoginName(String loginName);
    User findValideUserByPhone(String mobilePhone);
    
    /**
     * 通过邮箱查询没有被删除的有效用户
     * @param loginName
     * @return
     */
    Boolean findValideUserByEmail(String email,Long id);
    
    
    /**
     * 通过手机查询没有被删除的有效用户
     * @param loginName
     * @return
     */
    Boolean findValideUserByPhone(String phone,Long id);

    /**
     * 通过登录标识查询用户.
     * 
     * @param name
     *            登录名、注册邮箱或手机号
     */
    User findUserByLoginID(String loginID);

    /**
     * 是当前用户的密码?
     * 
     * @param plainPassword
     *            未加密前的密码.
     */
    
    boolean isCurrentUserPassword(String plainPassword);

  
    SessionUser getCurrentSessionUser();

   
    User getCurrentUser();
    
    User forceCurrentUser();
    

    /**
     * 是超级管理员(系统第一个用户)?
     * 
     * @param id
     *            用户id
     */
    boolean isSupervisor(Long id);

    /**
     * 更新用户最近一次登录的时间.
     * 
     */
    Future<Boolean> updateUserLastLoginTime(String loginId, Date time);

    /**
     * 删除用户.
     */
    void deleteUser(Long id);

    /**
     * 更新用户.
     */
    void updateUser(User user);

    /**
     * 注册用户.
     */
    void registerUser(User user);

    /**
     * 获取用户.
     * 
     * @param id
     *            用户id
     */
    User getUser(Long id);

    /**
     * 获取所有用户.
     */
    List<User> getAllUser();

    /**
     * 分配角色到用户.
     * 
     */
    void assignRolesToUser(User user, Role... roles);

    /**
     * 获取用户所有角色的列表.
     * 
     */
    List<Role> getUserAllRoles(User user);
    
    List<Role> findUserAllRoles(User user);

    /**
     * 创建角色.
     * 
     */
    void createRole(Role role);

    /**
     * 通过name查找角色.
     * 
     */
    Role findRoleByName(String name);

    /**
     * 通过name和realm查找角色.
     * 
     */
    Role findRoleByNameAndRealm(String name, String realm);

    /**
     * 创建权限.
     */
    
    void createPermissions(Permissions permissions);

    
    List<Permissions> getUserAllPermissions(User user);

    /**
     * 查找所有用户.
     */
    Page<User> findAllUsers(Collection<SearchFilter> searchFilters,Pageable pageable,String name);


    /**
     * 通过Id找用户.
     */
    User findUserById(Long id);

    
    Permissions findPermissionsById(Long id);

    
    void updatePermissions(Permissions entity);

    
    List<Permissions> findAllPermissions();
    
    Page<Permissions> findAllPermissions(Pageable pageable);

    /**
     * 删除权限.
     */
    
    void deletePermissions(Long id);

    /**
     * 通过过滤条件查找所有的权限.
     */
    
	Page<Permissions> findAllPermissions(Collection<SearchFilter> filters, Pageable pageable);
    
    /**
     * 通过Id找角色.
     */
    Role findRoleById(Long id);
    
    /**
     * 通过名称找角色.
     */
    Role findByRoleName(String name);

    /**
     * 更新角色.
     */
    void updateRole(Role entity);

    /**
     * 删除角色.
     */
    void deleteRole(Long id);

    /**
     * 通过过滤条件查所有的角色.
     */
    Page<Role> findAllRoles(Collection<SearchFilter> searchFilters, Pageable pageable);

    List<Role> findAllRoles(Collection<SearchFilter> searchFilters);
    /**
     * 从角色中移除权限.
     */
    
    void removeRolePermissionsByRoleId(Long roleId);

    /**
     * 查找所有的角色列表.
     */
    Iterable<Role> findAllRoles();

    /**
     * 改变用户状态.
     */
    void updateStatus(Long id);

    /**
     * 获取用户的直接管理的角色.
     */
    Iterable<Role> getUserRoles(Long userId);
	Page<Role> getRolesByUserId(Long id,Pageable pageable);

    /**
     * 将角色分配给用户.
     */
    void addRoleToUser(Long userId, Long roleId);

    /**
     * 取消分配用户的某个角色.
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * 通过组织机构查找其直接关联的所有用户.
     */
    Page<User> findUsersByOrganization(Long orgId, Pageable pageable);

    /**
     * 通过组织机构查找其直接关联的所有用户.
     */
    List<User> findUsersByOrganization(Long orgId);

    /**
     * 通过组织机构查找其关联所有用户（包括各个下属部门的）.
     */
    Page<User> findAllUsersByOrganization(Long orgId, Pageable pageable);

    /**
     * 通过组织机构查找其关联所有用户（包括各个下属部门的）.
     */
    List<User> findAllUsersByOrganization(Long orgId);

    /**
     * 注册用户和并分配组织机构.
     */
    void registerUserAndAddToOrganization(User user, Long orgId);

    /**
     * 将用户添关联到某个组织机构.
     */
    void addUserToOrganization(Long orgId, Long userId);

    /**
     * 获取用户关联的组织机构Id.
     */
    Long getUserOrganizationId(Long userId);

    /**
     * 获取用户管理的组织机构.
     */
    Organization getUserOrganization(Long userId);

    /**
     * 更新用户信息和修改其关联的组织机构.
     */
    void updateUserAndChangeOrganization(User user, Long orgId);

    /**
     * 获取未关联组织机构的所有用户.
     */
    Page<User> findAllUsersNotInOrganizations(Pageable pageable);

    /**
     * 修改用户密码.
     */
    void changeUserPassword(Long id, String newPassword);



    /**
     * 获取拥有某个角色的所有用户.
     */
    List<User> getUsersByRole(Role role);

    /**
     * 获取角色直属的权限列表.
     */

	List<Permissions> findPermissionsByRole(Long roleId, Sort sort);
	
	List<Permissions> findPermissionsByRoleId(Long roleId, Sort sort);
	
	List<Long> findPermissionsByRoles(Long roleId, Sort sort);

	void addPermissionsToRole(List<RolePermissions> rolePermsList);

	Role findRolesById(Long roleId);
	Permissions findByPermissionName(String name);
	/**
	 * 编辑用户角色
	 */
	public int editUserRole(Long userId,String roleIds);

}
