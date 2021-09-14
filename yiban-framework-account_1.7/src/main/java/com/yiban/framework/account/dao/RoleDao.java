package com.yiban.framework.account.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.User;

public interface RoleDao extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Role findByName(String name);
    
    @Query("select role from  Role role where role.name= ?1 and role.isDeleted = 0")
    Role findByRoleName(String name);

    Role findRoleByNameAndRealm(String name, String realm);

    @Query("select ur.user from  UserRole ur where ur.role.id= :roleId")
    List<User> findUsersByRoleId(@Param("roleId") Long roleId);
    
    @Modifying
    @Query("update Role u set u.isDeleted = 1 where u.id = ?1")
    void deleteRole(Long id);

    @Modifying
    @Query(value = "delete from t_role_permissions where role_id = :roleId", nativeQuery = true)
	void removeRolePermissionsByRoleId(@Param("roleId") Long roleId);

}
