package com.yiban.framework.account.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.framework.account.domain.Permissions;

public interface PermissionsDao extends JpaRepository<Permissions, Long>, JpaSpecificationExecutor<Permissions> {
	List<Permissions> findByParentIsNull(Sort sort);

	@Modifying
    @Query("update Permissions u set u.isDeleted = 1 where u.id = ?1")
    void deletePermissions(Long id);

	@Query("SELECT p FROM Permissions p WHERE id IN (select p.id FROM Role r JOIN r.permissions p where r.id = ?1)")
	List<Permissions> findPermissionsByRole(Long roleId, Sort sort);

	@Query("select p.id FROM Role r JOIN r.permissions p where r.id = ?1")
	List<Long> findPermissionsByRoles(Long roleId, Sort sort);

	@Query("SELECT p FROM Permissions p WHERE ((p.isDeleted =0 and p.isActived=1) or id IN (select p.id FROM Role r JOIN r.permissions p where r.id = ?1)) and p.parent is null")
	List<Permissions> findPermissionsById(Long roleId, Sort sort);
	
	@Query("SELECT p FROM Permissions p WHERE p.name=?1 and p.isDeleted =0 and p.isActived=1")
	Permissions findByPermissionName(String name);
	
}
