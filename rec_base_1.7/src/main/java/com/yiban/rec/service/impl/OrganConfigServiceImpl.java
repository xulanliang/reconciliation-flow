package com.yiban.rec.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yiban.framework.account.dao.OrganizationDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.OrganConfigDao;
import com.yiban.rec.domain.OrganConfig;
import com.yiban.rec.service.OrganConfigService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly = true)
public class OrganConfigServiceImpl extends BaseOprService implements OrganConfigService {

	@Autowired
	private OrganConfigDao organConfigDao;
	@Autowired
	private AccountService accoutService;
	@Autowired
	private OrganizationDao organizationDao;

	@Override
	public OrganConfig getOrganConfigById(Long id) {

		return organConfigDao.findOne(id);
	}

	@Override
	@Transactional
	public ResponseResult save(OrganConfig organConfig) {
		try {
			User user = accoutService.getCurrentUser();
			organConfig.setLastModifiedById(user.getId());
			organConfig.setCreatedById(user.getId());
			organConfig.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			organConfigDao.save(organConfig);
		} catch (Exception e) {
			logger.error("保存机构配置信息异常," + e.getMessage());
			return ResponseResult.failure("保存机构配置信息异常," + e.getMessage());
		}
		return ResponseResult.success("保存机构配置信息成功");
	}

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		organConfigDao.delete(id);
		return ResponseResult.success("删除机构配置信息成功");
	}

	@Override
	@Transactional
	public ResponseResult update(OrganConfig organConfig) {
		try {
			User user = accoutService.getCurrentUser();
			Long id = organConfig.getId();
			if (!StringUtil.isNullOrEmpty(id)) {
				OrganConfig organConfigDb = organConfigDao.findOne(id);
				if (organConfigDb != null) {
					// 存在则更新
					organConfigDb.setOrgNo(organConfig.getOrgNo());
					organConfigDb.setNetworkState(organConfig.getNetworkState());
					organConfigDb.setIp(organConfig.getIp());
					organConfigDb.setPort(organConfig.getPort());
					organConfigDb.setRecTime(organConfig.getRecTime());
					organConfigDb.setOrgPro(organConfig.getOrgPro());
					organConfigDb.setLastModifiedById(user.getId());
					organConfigDb.setRecType(organConfig.getRecType());
					organConfigDb.setIsCashRec(organConfig.getIsCashRec());
					organConfigDb.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
					organConfigDb.setPayModel(organConfig.getPayModel());
					organConfigDao.save(organConfigDb);
					return ResponseResult.success("更新机构配置信息成功");
				}
			}
			// 新增
			organConfigDao.save(organConfig);
		} catch (Exception e) {
			logger.error("更新机构配置信息,"+e.getMessage());
			return ResponseResult.failure("更新机构配置信息,"+e.getMessage());
		}
		return ResponseResult.success("更新机构配置信息成功");
	}

	@Override
	public Page<OrganConfig> getOrganConfigList(PageRequest pagerequest, String orgNo) {
		Specification<OrganConfig> specification = new Specification<OrganConfig>() {
			@Override
			public Predicate toPredicate(Root<OrganConfig> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch( orgNo,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return organConfigDao.findAll(specification, pagerequest);
	}

	protected List<Predicate> converSearch( String orgNo, Root<OrganConfig> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtil.isNullOrEmpty(orgNo)) {
			Path<Long> orgNoExp = root.get("orgNo");
			predicates.add(cb.equal(orgNoExp, orgNo));
		}
		return predicates;
	}

	@Override
	public OrganConfig getOrganConfigByOrgNo(String orgNo) {
		return organConfigDao.getOrganConfigByOrgNo(orgNo);
	}

	@Override
	public List<OrganConfig> getOrganConfigByIsDeletedAndIsActived(Integer isDeleted, Integer isActived) {
		
		return organConfigDao.findOrganConfigByIsDeletedAndIsActived(isDeleted, isActived);
	}
	
	@Override
	public OrganConfig getOrganConfigByOrgNo(Integer isDeleted, Integer isActived,String orgNo) {
		return organConfigDao.findOrganConfigByIsDeletedAndIsActivedAndOrgNo(isDeleted,isActived,orgNo);
	}

	@Override
	public List<Organization> getAllOrgnatzation() {
		return organizationDao.findAll();
	}

	@Override
	public List<OrganConfig> findByOrgNo(String orgNo) {
		return organConfigDao.findByOrgNo(orgNo);
	}

	@Override
	public List<OrganConfig> getAllOrganConfig() {
		return organConfigDao.findAll();
	}

}
