package com.yiban.framework.account.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.UserRole;

public interface UserRoleDao extends PagingAndSortingRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

    @Query("select ur.role from UserRole ur where ur.user.id = ?1 ")
    List<Role> findAllRolesByUserId(Long id);

    UserRole findByUserIdAndRoleId(Long userId, Long roleId);
    
    @Modifying
    @Query("update UserRole u set u.isDeleted = 1 where u.id = ?1")
    void deleteUserRole(Long id);
    
    @Query("select ur.role from UserRoles ur where ur.user.id = ?1 and ur.role.isDeleted=0 and ur.role.isActived=1")
	List<Role> getAllRolesByUserId(Long id);
    
    @Query("select ur.role from UserRoles ur where ur.user.id = ?1 and ur.role.isDeleted=0 and ur.role.isActived=1")
   	Page<Role> getRolesByUserId(Long id,Pageable pageable);
    @Modifying
    @Query("delete from UserRole u where u.user.id = :userId")
    void deleteUserIdRole(@Param("userId") Long userId);
}
