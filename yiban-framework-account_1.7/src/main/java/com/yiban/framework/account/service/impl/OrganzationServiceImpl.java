package com.yiban.framework.account.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.collect.Lists;
import com.yiban.framework.account.dao.OrganizationDao;
import com.yiban.framework.account.dao.UserOrganizationDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.UserOrganization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.SessionUser;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.service.BaseService;
import com.yiban.framework.core.service.SessionUserService;

@Service
@Transactional(readOnly = true)
public class OrganzationServiceImpl extends BaseService implements OrganizationService {
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private UserOrganizationDao userOrganizationDao;
	
	@Autowired
    private SessionUserService sessionUserService;

	@Override
	@Transactional
	public void createOrganization(Organization organization) {
		/*
		 * if(organization.getId() != null){ throw new
		 * RuntimeException("机构ID非空"); }
		 */
		organizationDao.save(organization);
	}

	@Override
	public List<Organization> findAllTopOrganizations() {
		return organizationDao.findByParentIsNull();
	}

	@Override
	public List<Organization> findAllOrganizations() {
		return organizationDao.findAll();
	}

	@Override
	@Transactional
	public void updateOrganization(Organization organization) {
		organizationDao.save(organization);
	}

	@Override
	@Transactional
	public void deleteOrganization(Long id) {
		//organizationDao.deleteOrganization(id);
		//物理删除
		userOrganizationDao.removeByOrganizationId(id);
		organizationDao.delete(id);
	}

	@Override
	public boolean existsEmptyOrganizationCode() {
		return (organizationDao.countByCodeIsNull() > 0);
	}

	@Override
	public Organization findOrganizationById(Long id) {
		return organizationDao.findOne(id);
	}

	@Override
	public Organization findByCode(String code) {
		return organizationDao.findByCode(code);
	}

	@Override
	public Organization findByName(String name) {
		return organizationDao.findByName(name);
	}


	
	@Override
	public List<Organization> findAllOrganization(Collection<SearchFilter> searchFilters, String name) {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		Specifications<Organization> spec = Specifications.where(bySearchFilter(new HashSet<>(), Organization.class))
				.and(builtinSpecs.notDelete())
				.and(builtinSpecs.isActived())
				;
		if(StringUtils.isNotEmpty(name)){
			spec.and(new Specification<Organization>() {
				@Override
				public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					Path<String> exp = root.get("name");
					return cb.like(exp, "%"+name+"%");
				}
			});
		}
		return organizationDao.findAll(spec, sort);
	}

	@Override
	public List<Organization> findAllOrganization(Collection<SearchFilter> searchFilters) {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		Specifications<Organization> spec = Specifications.where(bySearchFilter(searchFilters, Organization.class)).and(builtinSpecs.notDelete()).and(builtinSpecs.isActived());
		return organizationDao.findAll(spec, sort);
	}

	
	@Override
	public List<Organization> findAllOrganization(String name) {
		Sort sort = new Sort(new Order(Direction.ASC, "sort"), new Order(Direction.ASC, "id"));
		Specifications<Organization> spec = Specifications.where(bySearchFilter(new HashSet<>(), Organization.class))
				.and(builtinSpecs.notDelete())
				.and(builtinSpecs.isActived())
				.and(new Specification<Organization>() {
					@Override
					public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
							Path<String> exp = root.get("name");
							return cb.like(exp, "%"+name+"%");
					}
				});
		return organizationDao.findAll(spec, sort);
	}

	
	@Override
	public boolean existsOrganization(Organization organization) {
		Organization orgCheck = this.organizationDao.findByName(organization.getName());
		if (orgCheck != null && orgCheck.getId() != organization.getId() && orgCheck.getIsDeleted() == 0) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public boolean existsUser(Long organizationId) {
		return true;
	}

	@Override
	public List<ValueTextable<String>> getOrgMap() {
		List<ValueTextable<String>> result = Lists.newArrayList();
	List<Organization> orgs = organizationDao.findAll();
		if (orgs != null && orgs.size() > 0) {
			for (Organization data : orgs) {
				result.add(new ValueText<>(data.getCode(), data.getName()));
			}
		}
		return result;
	}
	
	@Override
	public List<ValueTextable<String>> getOrgCodeMap() {
		List<ValueTextable<String>> result = Lists.newArrayList();
	List<Organization> orgs = organizationDao.findAll();
		if (orgs != null && orgs.size() > 0) {
			for (Organization data : orgs) {
				result.add(new ValueText<>(data.getId().toString(), data.getCode()));
			}
		}
		return result;
	}
	
	public List<Organization> findByParentCode(String parentCode){
		Organization o = organizationDao.findByCode(parentCode);
		if(null != o){
			Long parentId = o.getId();
			List<Organization> list = organizationDao.findByParentId(parentId);
			if(list==null||list.size()<=0) {
				list.add(o);
			}
			return list;
		}
		return null;
	}
	
	public String findByCodeId(String code) {
		Organization o = organizationDao.findByCode(code);
		Long parentId=0L;
		if(o!=null) {
			parentId= o.getId();
		}
		Organization orgVo = organizationDao.findOne(parentId);
		if(orgVo!=null) {
			return orgVo.getCode();
		}
		return null;
	}

    @Override
    public List<Organization> findByNameLike(String name) {
        return organizationDao.findByNameLike(name);
    }

    @Override
    public List<Map<String, Object>> findAllCodeAndName() {
        List<Map<String, Object>> list = new ArrayList<>();
        SessionUser sessionUser = sessionUserService.getCurrentSessionUser();
        if(null != sessionUser) {
            String loginName = sessionUser.getLoginName();
            Long userId = sessionUser.getId();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT");
            sb.append(" o.id as id,");
            sb.append(" o.name as name,");
            sb.append(" o.code as code,");
            sb.append(" o.parent_id as parent");
            sb.append(" FROM t_organization o");
            if(!StringUtils.equalsIgnoreCase("admin", loginName)) {
                sb.append(getOrgsSql(userId));
            }
            sb.append(" ORDER BY parent_id");
            
            list = query(sb.toString());
        }
        return list;
    }
    
    /**
     * 构造查询机构的SQL脚本
     * @param userId
     * @return
     * String
     */
    private String getOrgsSql(Long userId) {
        Set<Long> sets = new HashSet<>();
        List<UserOrganization> list = userOrganizationDao.findByUserId(userId);
        for (UserOrganization uo : list) {
            Long orgId = uo.getOrganization().getId();
            List<Organization> orgList = new ArrayList<>();
            findChildrens(orgList, orgId);
            if(orgList.isEmpty()) {
                /*continue;*/
				sets.add(orgId);
            } else {
				for (Organization o : orgList) {
					sets.add(o.getId());
				}
				sets.add(orgId);
			}
        }
        return getSql(sets);
    }
    
    /**
     * 查询子机构
     * @param id
     * @return
     * List<Organization>
     */
    private void findChildrens(List<Organization> list, Long pid) {
        List<Organization> parentList = organizationDao.findByParentId(pid);
        if(!parentList.isEmpty()) {
            for (Organization o : parentList) {
                Organization org = o.getParent();
                if(null != org) {
                    findChildrens(list, o.getId());
                }
            }
        }
        list.addAll(parentList);
    }
    
    /**
     * 构造SQL
     * @param sets
     * @return
     * String
     */
    private String getSql(Set<Long> sets) {
        if(sets.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if(!sets.isEmpty()) {
            sb.append(" WHERE o.id IN ( ");
            for (Long id : sets) {
                sb.append("'");
                sb.append(id);
                sb.append("',");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(" )");
        }
        return sb.toString();
    }

    @Override
    public List<Map<String, Object>> findAllData() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT");
        sb.append(" o.id as id,");
        sb.append(" o.name as name,");
        sb.append(" o.code as code,");
        sb.append(" o.parent_id as parent");
        sb.append(" FROM t_organization o");
        return query(sb.toString());
    }
    
    public String getOrgCodes(String orgName) {
    	String codes=null;
    	if(StringUtils.isNotBlank(orgName)) {
    		orgName=orgName.trim();
    	}
    	List<Organization> list = organizationDao.findByNameLike(orgName);
    	for(Organization v:list) {
    		if(StringUtils.isBlank(v.getCode()))continue;
    		if(StringUtils.isBlank(codes)) {
    			codes=v.getCode();
    		}else {
    			codes=codes+","+v.getCode();
    		}
    	}
		return codes;
    }
    
}
