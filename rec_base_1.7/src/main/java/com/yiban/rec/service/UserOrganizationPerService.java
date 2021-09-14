package com.yiban.rec.service;

import java.util.List;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.rec.domain.PayType;

public interface UserOrganizationPerService {
	
	
	public List<Organization> orgTempList(User user);
	
	public List<String> filtersOrg2List(List<Organization> orgListTemp);
	
	List<ValueTextable<String>> asNameToValue();
	
	List<ValueTextable<String>> asValueToName();
	
	public List<PayType> getPayTypeByType(String type);
	
	public List<PayType> getPayTypeByTypes(String[] type);
	
}
