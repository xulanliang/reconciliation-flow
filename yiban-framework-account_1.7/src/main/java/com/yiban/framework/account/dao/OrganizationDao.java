package com.yiban.framework.account.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.framework.account.domain.Organization;

public interface OrganizationDao extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {

	//查询一级组织机构
    //List<Organization> findByParentIsNull(Sort sort);
    List<Organization> findByParentIsNull();

    //查询一级组织个数
    long countByCodeIsNull();
    
    @Query("select u from Organization u where u.name like %?1% and u.isDeleted=0")
    List<Organization> findByNameLike(String name);
    
    //根据名称获取组织机构
    @Query("select u from Organization u where u.name = ?1 and u.isDeleted=0")
    Organization findByName(String name);
    
    @Query("select u from Organization u where u.code = ?1 and u.isDeleted=0")
    Organization findByCode(String code);
    
    //逻辑删除组织机构
    @Modifying
    @Query("update Organization u set u.isDeleted = 1 where u.id = ?1")
    void deleteOrganization(Long id);
    
    List<Organization> findByParentId(Long parentId);
}
