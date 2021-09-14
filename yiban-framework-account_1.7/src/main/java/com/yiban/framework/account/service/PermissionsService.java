package com.yiban.framework.account.service;

import java.util.Collection;
import java.util.List;
import org.springside.modules.persistence.SearchFilter;
import com.yiban.framework.account.domain.Permissions;
import com.yiban.framework.account.domain.User;

/**
* @author swing
* @date 2018年1月25日 下午3:49:26
* 类说明
*/
public interface PermissionsService {

	public void saveOrUpdate(Permissions permission);
	
	public void delete(Long id);
	
	public Permissions findById(Long id) ;
	public Permissions findByPermissionName(String name);
	
	//获取当前用户权限
	public List<Permissions> getUserPermissions(User user);

	public List<Permissions> findAll(Collection<SearchFilter> searchFilters);
}
