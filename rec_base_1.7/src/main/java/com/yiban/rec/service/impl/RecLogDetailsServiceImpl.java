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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.dao.RecLogDetailsDao;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.service.RecLogDetailsService;
import com.yiban.rec.util.StringUtil;

@Service
public class RecLogDetailsServiceImpl implements RecLogDetailsService {
	
	@Autowired
	private RecLogDetailsDao recLogDetailsDao;
	
	protected List<Predicate> converSearch(Long orgNo, Date date,List<Organization> orgListTemp, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtils.isEmpty(orgNo)) {
			Path<Long> orgCodeExp = root.get("orgNo");
			predicates.add(cb.equal(orgCodeExp, orgNo));
		}else{
			if(StringUtil.isNullOrEmpty(orgListTemp)){
				Path<Long> orgCodeExp = root.get("orgNo");
				predicates.add(cb.equal(orgCodeExp, 0));
			}else{
				In<String> in = cb.in(root.get("orgNo").as(String.class));
			    for (int i = 0; i < orgListTemp.size(); i++) {
			        in.value(orgListTemp.get(i).getCode());
			        List<Organization> childList = orgListTemp.get(i).getChildren();
			        if(!StringUtil.isNullOrEmpty(childList)){
			        	for(Organization org : childList){
			        		in.value(org.getCode());
			        		if(!StringUtil.isNullOrEmpty(org.getChildren())){
			        			for(Organization orgCl : org.getChildren()){
			        				in.value(orgCl.getCode());
			        			}
			        		}
			        	}
			        }
			    }
			    predicates.add(in);
			}
		}
		if (!StringUtils.isEmpty(date)) {
			Path<Date> orderDateExp = root.get("orderDate");
			predicates.add(cb.equal(orderDateExp, date));
		}
		return predicates;
	}

	@Override
	public Page<RecLogDetails> getRecLogData(Long orgNo, Date date, List<Organization> orgListTemp,Pageable pageable) {
//		Specification<ResLogDetails> specification = new Specification<RecLogState>() {
//			@Override
//			public Predicate toPredicate(Root<RecLogState> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				List<Predicate> predicates = converSearch(orgNo,date,orgListTemp, root, query, cb);
//				if (predicates.size() > 0) {
//					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//				}
//				return cb.conjunction();
//			}
//		};
//		return recLogDao.findAll(specification, pageable);
	    return null;
	}

    @Override
    @Transactional
    public RecLogDetails save(RecLogDetails recLogDetails) {
        return recLogDetailsDao.save(recLogDetails);
    }

    @Override
    public List<RecLogDetails> findByOrderDateAndOrgCode(String orderDate, String orgCode) {
        return recLogDetailsDao.findByOrderDateAndOrgCode(orderDate, orgCode);
    }

    @Override
    public Long deleteByOrderDateAndOrgCode(String orderDate, String orgCode) {
        return recLogDetailsDao.deleteByOrderDateAndOrgCode(orderDate, orgCode);
    }

}
