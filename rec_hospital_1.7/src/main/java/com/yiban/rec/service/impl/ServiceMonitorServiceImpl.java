package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.AccountService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.ServiceMonitorDao;
import com.yiban.rec.domain.ServiceMonitor;
import com.yiban.rec.service.ServiceMonitorService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.StringUtil;

@Service
@Transactional(readOnly = true) 
public class ServiceMonitorServiceImpl  implements ServiceMonitorService {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ServiceMonitorDao serviceMonitorDao;
	@Autowired
	private AccountService accoutService;

	@Override
	public ServiceMonitor getServiceMonitorById(Long id) {

		return serviceMonitorDao.findOne(id);
	}

	@Override
	@Transactional
	public ResponseResult save(ServiceMonitor serviceMonitor) {
		try {
			User user = accoutService.getCurrentUser();
			serviceMonitor.setCreatedDate(new Date());
			serviceMonitor.setCreatedById(user.getId());
			if(serviceMonitor.getIsOpen()==null){
				serviceMonitor.setIsOpen(CommonEnum.IsActive.NOTACTIVE.getValue());
			}
			serviceMonitorDao.save(serviceMonitor);
		} catch (Exception e) {
			logger.error("保存服务监测信息异常," + e.getMessage());
			return ResponseResult.failure("保存服务监测信息异常," + e.getMessage());
		}
		return ResponseResult.success("保存服务监测信息成功");
	}

	@Override
	@Transactional
	public ResponseResult delete(Long id) {
		serviceMonitorDao.delete(id);
		return ResponseResult.success("删除服务监测信息成功");
	}

	@Override
	@Transactional
	public ResponseResult update(ServiceMonitor serviceMonitor) {
		try {
			User user = accoutService.getCurrentUser();     
			Long id = serviceMonitor.getId();
			if (!StringUtil.isNullOrEmpty(id)) {
				ServiceMonitor serviceMonitorDb = serviceMonitorDao.findOne(id);
				if (serviceMonitorDb != null) {
					// 存在则更新
					if(serviceMonitor.getIsOpen()==null){
						serviceMonitor.setIsOpen(CommonEnum.IsActive.NOTACTIVE.getValue());
					}
					serviceMonitorDb.setOrgNo(serviceMonitor.getOrgNo());
					serviceMonitorDb.setNoticeWay(serviceMonitor.getNoticeWay());
					serviceMonitorDb.setIntervalTime(serviceMonitor.getIntervalTime());
					serviceMonitorDb.setContacts(serviceMonitor.getContacts());
					serviceMonitorDb.setIsOpen(serviceMonitor.getIsOpen());
					serviceMonitorDb.setLastModifiedDate(new Date());
					serviceMonitorDb.setLastModifiedById(user.getId());
					serviceMonitorDao.save(serviceMonitorDb);
					return ResponseResult.success("更新服务监测信息成功");
				}
			}
			// 新增
			serviceMonitorDao.save(serviceMonitor);
		} catch (Exception e) {
			logger.error("更新服务监测信息,"+e.getMessage());
			return ResponseResult.failure("更新服务监测信息,"+e.getMessage());
		}
		return ResponseResult.success("更新服务监测信息成功");
	}

	@Override
	public Page<ServiceMonitor> getServiceMonitorList(PageRequest pagerequest, Long orgNo) {
		Specification<ServiceMonitor> specification = new Specification<ServiceMonitor>() {
			@Override
			public Predicate toPredicate(Root<ServiceMonitor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch( orgNo,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return findAll(serviceMonitorDao.findAll(specification, pagerequest));
	}
	
	private Page<ServiceMonitor> findAll(Page<ServiceMonitor> page){
		List<ServiceMonitor> list = page.getContent();
		for(ServiceMonitor serviceMonitor : list){
			if(serviceMonitor.getNoticeWay().indexOf(",")>-1){
				serviceMonitor.setNoticeWayValue("短信,邮件");
			}else if(EnumType.NOTICE_WAY_MESSAGE.getValue().equals(serviceMonitor.getNoticeWay())){
				serviceMonitor.setNoticeWayValue(EnumType.NOTICE_WAY_MESSAGE.getName());
			}else{
				serviceMonitor.setNoticeWayValue(EnumType.NOTICE_WAY_EMAIL.getName());
			}
		}
		return page;
	}

	protected List<Predicate> converSearch( Long orgNo, Root<ServiceMonitor> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtil.isNullOrEmpty(orgNo)) {
			Path<Long> orgNoExp = root.get("orgNo");
			predicates.add(cb.equal(orgNoExp, orgNo));
		}
		return predicates;
	}

	@Override
	public ServiceMonitor getServiceMonitorByOrgNo(String orgNo) {
		return serviceMonitorDao.getServiceMonitorByOrgNo(orgNo);
	}

	@Override
	public List<ServiceMonitor> getServiceMonitorByIsDeletedAndIsActived(Integer isDeleted, Integer isActived) {
		
		return serviceMonitorDao.findAll();
	}
	
}
