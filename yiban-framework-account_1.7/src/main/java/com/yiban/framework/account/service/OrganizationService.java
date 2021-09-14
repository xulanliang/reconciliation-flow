package com.yiban.framework.account.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.core.domain.base.ValueTextable;

public interface OrganizationService {

	/**
	 * 创建组织机构.
	 */
	void createOrganization(Organization organization);

	/**
	 * 更新组织机构.
	 */
	void updateOrganization(Organization organization);

	/**
	 * 通过Id找组织机构.
	 */
	Organization findOrganizationById(Long id);

	/**
	 * 根据code查找组织机构（用于code判重）
	 * 
	 * @param code
	 * @return
	 */
	Organization findByCode(String code);

	Organization findByName(String name);
	List<Organization> findByNameLike(String name);

	List<Organization> findAllOrganization(Collection<SearchFilter> searchFilters);
	List<Organization> findAllOrganization(Collection<SearchFilter> searchFilters,String name);
	List<Organization> findAllOrganization(String name);

	List<Organization> findAllTopOrganizations();

	List<Organization> findAllOrganizations();
	
	List<Organization> findByParentCode(String parentCode);
	String findByCodeId(String code);

	/**
	 * 删除组织机构.
	 */
	void deleteOrganization(Long id);

	/**
	 * 机构中存在未更新的空code?
	 */
	boolean existsEmptyOrganizationCode();

	/**
	 * 判断机构是否已经存在
	 * 
	 * @param name
	 * @return
	 */
	boolean existsOrganization(Organization organization);

	// 该机构是否存在用户
	boolean existsUser(Long organizationId);
	
	public List<ValueTextable<String>> getOrgMap();
	public List<ValueTextable<String>> getOrgCodeMap();
	
	/**
	 * 查询机构的code和name
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> findAllCodeAndName();
	
	/**
	 * 查询机构的code和name
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> findAllData();
	
	public String getOrgCodes(String orgName);

}
