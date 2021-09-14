package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.OrganConfig;

public interface OrganConfigDao extends JpaRepository<OrganConfig, Long>, JpaSpecificationExecutor<OrganConfig> {
	
	OrganConfig getOrganConfigByOrgNo(String orgNo);
	
	List<OrganConfig> findOrganConfigByIsDeletedAndIsActived(Integer isDeleted,Integer isActived);
	
	OrganConfig findOrganConfigByIsDeletedAndIsActivedAndOrgNo(Integer isDeleted,Integer isActived,String orgNo);
	
	List<OrganConfig> findByOrgNo(String orgNo);
	
}
