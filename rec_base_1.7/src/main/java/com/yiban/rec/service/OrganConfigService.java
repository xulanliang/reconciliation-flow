package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.OrganConfig;

public interface OrganConfigService {
	 OrganConfig getOrganConfigById(Long id);
	 
	 OrganConfig getOrganConfigByOrgNo(String orgNo);
	 
	 ResponseResult save(OrganConfig organConfig);
	 
	 ResponseResult delete(Long id);
	 
	 ResponseResult update(OrganConfig organConfig);
	 
	 List<Organization> getAllOrgnatzation();
	 
	 List<OrganConfig> getAllOrganConfig();
	 
	 List<OrganConfig> findByOrgNo(String orgNo);
	 
	 Page<OrganConfig> getOrganConfigList(PageRequest pagerequest,String orgNo);
	 
	 List<OrganConfig> getOrganConfigByIsDeletedAndIsActived(Integer isDeleted,Integer isActived);
	 
	 OrganConfig getOrganConfigByOrgNo(Integer isDeleted, Integer isActived,String orgNo);
}
