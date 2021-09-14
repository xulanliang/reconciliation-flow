package com.yiban.framework.account.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.framework.account.domain.RolePermissions;

public interface RolePermissionsDao extends JpaRepository<RolePermissions, Long>, JpaSpecificationExecutor<RolePermissions> {

}
