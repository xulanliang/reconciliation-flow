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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.dao.MixRefundDao;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.vo.BlendRefundVo;
import com.yiban.rec.service.MixRefundService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;

@Service
public class MixRefundServiceImpl implements MixRefundService {
	
	@Autowired
	private MixRefundDao mixRefundDao;

	
	
	@Override
	public Page<MixRefund> fundData(BlendRefundVo vo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<MixRefund> specification = new Specification<MixRefund>() {
			@Override
			public Predicate toPredicate(Root<MixRefund> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return mixRefundDao.findAll(specification, pageable);
	}
	protected List<Predicate> converSearchCash(BlendRefundVo vo, List<Organization> orgListTemp,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if(org.apache.commons.lang.StringUtils.isNotBlank(vo.getOrgNo())) {
			In<String> in = cb.in(root.get("orgCode").as(String.class));
			in.value(vo.getOrgNo());
			for (int i = 0; i < orgListTemp.size(); i++) {
		    	Organization org = orgListTemp.get(i);
		        if(!StringUtil.equals(vo.getOrgNo(), org.getCode())) {
		        	in.value(org.getCode());
		        }
		    }
			predicates.add(in);
		}
		if (!StringUtils.isEmpty(vo.getRefundOrderNo())) {
			Path<String> refundOrderNo = root.get("refundOrderNo");
			predicates.add(cb.equal(refundOrderNo, vo.getRefundOrderNo()));
		}
		if (!StringUtils.isEmpty(vo.getCashier())) {
			Path<String> paySystemNoExp = root.get("cashier");
			predicates.add(cb.equal(paySystemNoExp, vo.getCashier()));
		}
		if (!StringUtils.isEmpty(vo.getPayBusinessType())) {
			Path<Integer> payTypeArrExp = root.get("payBusinessType");
			predicates.add(cb.equal(payTypeArrExp, vo.getPayBusinessType()));
		}
		if (!StringUtils.isEmpty(vo.getStartTime())) {
			Path<String> payDateStartExp = root.get("refundDateTime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, vo.getStartTime()));
		}
		if (!StringUtils.isEmpty(vo.getEndTime())) {
			Path<Date> payDateEndExp = root.get("refundDateTime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getEndTime())));
		}
		
		return predicates;
	}
}
