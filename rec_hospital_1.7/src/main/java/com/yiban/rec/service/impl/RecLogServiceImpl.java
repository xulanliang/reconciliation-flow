package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springside.modules.persistence.DynamicSpecifications;
import org.springside.modules.persistence.SearchFilter;

import com.google.common.collect.Lists;
import com.yiban.framework.account.dao.OrganizationDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.service.RecLogService;

@Service
public class RecLogServiceImpl extends DynamicSpecifications implements RecLogService {
	
	@Autowired
	private RecLogDao recLogDao;
	@Autowired
	private OrganizationDao organizationDao;
	
	@Override
	public Page<RecLog> getHistoryData(String orgNo,Date startTime, Date endTime, List<Organization> orgListTemp, Pageable pageable) {
		
//		Specification<RecLog> specification = new Specification<RecLog>() {
//			@Override
//			public Predicate toPredicate(Root<RecLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				List<Predicate> predicates = converSearch(orgNo,startTime,endTime, orgListTemp,0,root, query, cb);
//				if (predicates.size() > 0) {
//					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//				}
//				return cb.conjunction();
//			}
//		};
//		return historyDataDao.findAll(specification, pageable);
		
		return null;
	}
	
	protected List<Predicate> converSearch(String orgNo, Date startTime, Date endTime,List<Organization> orgListTemp,int recState, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		List<Organization> orgList = organizationDao.findByParentId(organizationDao.findByCode(orgNo).getId());
		In<String> in = cb.in(root.get("orgNo").as(String.class));
		in.value(orgNo);
		if(orgList != null ){
			for (int i = 0; i < orgList.size(); i++) {
		    	Organization org = orgList.get(i);
		    	in.value(org.getCode());
		    }
			predicates.add(in);
		}
		
		if (recState > 0) {
			Path<Integer> recStateExp = root.get("recState");
			predicates.add(cb.equal(recStateExp, recState));
		}
	
		if (!StringUtils.isEmpty(startTime)) {
			Path<Date> orderDateExp = root.get("orderDate");
			predicates.add(cb.greaterThanOrEqualTo(orderDateExp, startTime));
		}
		
		if (!StringUtils.isEmpty(endTime)) {
			Path<Date> orderDateExp = root.get("orderDate");
			predicates.add(cb.lessThanOrEqualTo(orderDateExp, endTime));
		}
		
		return predicates;
	}

	@Override
	public Page<RecLog> getRecLogData(String orgNo, Date startDate, Date endDate,List<Organization> orgListTemp,int recState,Pageable pageable) {
		Specification<RecLog> specification = new Specification<RecLog>() {
			@Override
			public Predicate toPredicate(Root<RecLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(orgNo,startDate,endDate,orgListTemp,recState, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return recLogDao.findAll(specification, pageable);
	}
	
	@Override
	@Transactional
	public RecLog save(RecLog recLog) {
//		if(null != hs) {
//			RecLog data = historyDataDao.findByOrgNoAndOrderDate(hs.getOrgCode(),hs.getOrderDate());
//			if(null == data) {
//				data = new RecLog();
//				BeanUtils.copyProperties(hs, data);
//			}
//			data.setHistoryState(hs.getHistoryState());
//			data.setExceptionRemark(hs.getExceptionRemark());
//			data.setLastModifiedDate(new Date());
//			if(data.getSendAmount() == null) {
//				data.setSendAmount(1);
//			}else {
//				data.setSendAmount(data.getSendAmount() + 1);
//			}
//			Long id = historyDataDao.save(data).getId();
//		}
	    return recLogDao.save(recLog);
	}

    @Override
    public RecLog findByOrderDateAndOrgCode(String orgCode, String orderDate) {
        return recLogDao.findByOrderDateAndOrgCode(orderDate, orgCode);
    }

    @Override
    public Page<RecLog> findPageByQueryParameters(List<SearchFilter> filters, Pageable pageable) {
        return recLogDao.findAll(Specifications.where(bySearchFilter(filters, 
                RecLog.class)), pageable);
    }
}
