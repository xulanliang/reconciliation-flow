package com.yiban.rec.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yiban.framework.account.dao.UserOrganizationDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.domain.UserOrganization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.util.YiBanEntityUtils;
import com.yiban.framework.dict.dao.MetaDataDao;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.rec.dao.PayTypeDao;
import com.yiban.rec.domain.PayType;
import com.yiban.rec.service.UserOrganizationPerService;
import com.yiban.rec.util.EnumType;

@Service("userOrganizationPerService")
public class UserOrganizationPerServiceImpl implements UserOrganizationPerService {

	@Autowired
	private OrganizationService organizationService;
	

	
	@Autowired
	private MetaDataDao metaDataDao;
	
	@Autowired
	private UserOrganizationDao userOrganizationDao;
	
	@Autowired
	private PayTypeDao payTypeDao;
	
	@Override
	public List<Organization> orgTempList(User user) {
		Long userId = user.getId();
		boolean isAdmin = EnumType.ADMIN_LOGIN.getValue().equals(user.getLoginName());
		List<Organization> orgs = userOrganizationDao.findOrganizationsByUserId(userId);
		List<Organization> orgList=organizationService.findAllOrganizations();
		YiBanEntityUtils.filterEntity(orgList);
		if(isAdmin || orgs.size()<=0){
			return orgList;
		}
		//List<Organization> list = organizationService.findAllOrganizations();
		filterUserOrg(orgList,userId);
    	//List<Organization> orgTempList = filterOrg(filterOrg(list,userId));
        return orgList;
	}
	
	/*
	private List<Organization> filterOrg(List<Organization> orgList){
    	boolean braekFlag = false;
    	for(Organization organization : orgList){
    		if(organization.getIsActived() ==0 || organization.getIsDeleted() ==1){
    			int index = orgList.indexOf(organization);
    			orgList.remove(index);
    			braekFlag = true;
    			break;
    		}else{
    			filterOrg(organization.getChildren());
    		}
    	}
    	if(braekFlag){
    		braekFlag = false;
    		filterOrg(orgList);
    	}
    	return orgList;
		
    }
	**/
	/* private List<Organization> filterOrg(List<Organization> orgList, Long userId){
     	boolean braekFlag = false;
     	List<Organization> orgTempList = new ArrayList<Organization>();
     	Organization organizationtemp = null;
     	out:for(Organization organization : orgList){
     		if(organization.getIsActived() ==1 && organization.getIsDeleted() ==0){
     			for(UserOrganization userOrg : organization.getUserOrganizations()){
     				if(userOrg.getUser().getId()==userId&&userOrg.getOrganization().getIsActived()==1&&userOrg.getOrganization().getIsDeleted()==0){
     					braekFlag = true;
     					organizationtemp = organization;
     					orgTempList.add(organizationtemp);
     					break out;
     				}
     			}
     		}else{
     			continue;
     		}
     	}
     	if(braekFlag){
     		braekFlag = false;
     		filterOrg(organizationtemp.getChildren(),userId);
     	}
     	return orgTempList;
     }*/
	
	
	/**
	 * 过滤非给定用户的组织机构
	 * @param orgList
	 * @param userId
	 */
	@SuppressWarnings("unused")
	private void filterUserOrg(List<Organization> orgList, Long userId){
		Iterator<Organization> ite =orgList.iterator();
		while(ite.hasNext()){
			Organization org =ite.next();
			List<UserOrganization> userOrgList =org.getUserOrganizations();
			Iterator<UserOrganization> userOrgIte=userOrgList.iterator();
			while(userOrgIte.hasNext()){
				UserOrganization userOrg =userOrgIte.next();
				if(userOrg.getUser().getId().longValue() !=userId){
					userOrgIte.remove();
				}
			}
		}
	}
	
	 //因为这个 接口被多个地方使用，故接口保持不变，接口内部重新实现
	 public List<String> filtersOrg2List(List<Organization> list){
		// List<Long> orgList = new ArrayList<Long>();
		/* for (int i = 0; i < list.size(); i++) {
			 orgList.add(list.get(i).getId());
		        List<Organization> childList = list.get(i).getChildren();
		        if(!StringUtil.isNullOrEmpty(childList)){
		        	for(Organization org : childList){
		        		 orgList.add(org.getId());
		        		if(!StringUtil.isNullOrEmpty(org.getChildren())){
		        			for(Organization orgCl : org.getChildren()){
		        				 orgList.add(orgCl.getId());
		        			}
		        		}
		        	}
		        }
		    }
		 return orgList;*/
		 Set<String> set =listToIdSet(list);
		 return new ArrayList<>(set);
	 } 
	 
	 private Set<String> listToIdSet(List<Organization> list){
		 Set<String> idSet=new HashSet<>(list.size() * 2 );
		 for(Organization org:list){
			 idSet.add(org.getCode());
			 List<Organization> child=org.getChildren();
			 if(child !=null && !child.isEmpty()){
				 Set<String> childIdSet =listToIdSet(child);
				 idSet.addAll(childIdSet);
			 }
		 }
		 return idSet;
	 }
	 
		@Override
		public List<ValueTextable<String>> asNameToValue() {
			List<ValueTextable<String>> result = Lists.newArrayList();
			List<MetaData> metaDatas = metaDataDao.findAll();
			if (metaDatas != null && metaDatas.size() > 0) {
				for (MetaData metaData : metaDatas) {
					result.add(new ValueText<>(metaData.getName(), metaData.getValue()));
				}
			}
			return result;
		}
		
		public List<ValueTextable<String>> asValueToName() {
			List<ValueTextable<String>> result = Lists.newArrayList();
			List<MetaData> metaDatas = metaDataDao.findAll();
			if (metaDatas != null && metaDatas.size() > 0) {
				for (MetaData metaData : metaDatas) {
					result.add(new ValueText<>(metaData.getValue(), metaData.getName()));
				}
			}
			return result;
		}

		public List<PayType> getPayTypeByType(String type){
			return payTypeDao.findByType(type);
		}
		
		public List<PayType> getPayTypeByTypes(String[] types){
			return payTypeDao.findByTypes(types);
		}

}
