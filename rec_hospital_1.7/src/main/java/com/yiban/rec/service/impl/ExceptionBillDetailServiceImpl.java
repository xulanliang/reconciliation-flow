package com.yiban.rec.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.service.ExceptionBillDetailService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;

@Service
public class ExceptionBillDetailServiceImpl extends BaseOprService implements ExceptionBillDetailService {

	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;

	@Override
	public Page<TradeCheckFollow> queryPage(String orgCode, String billSource, String date,String tradeType,
			List<Organization> orgList, Pageable pageable) {

		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(billSource, date,tradeType, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tradeCheckFollowDao.findAll(specification, pageable);
	}

	@Override
	public List<TradeCheckFollow> queryNoPage(String orgCode, String billSource, String date,String tradeType,
			List<Organization> orgList, Sort sort) {

		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(billSource, date,tradeType, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tradeCheckFollowDao.findAll(specification, sort);
	}

	protected List<Predicate> filterCondition(String billSource, String date,String tradeType, List<Organization> orgList,
			Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();

		if (orgList != null && orgList.size() > 0) {
			In<String> in = cb.in(root.get("orgNo").as(String.class));
			for (int i = 0; i < orgList.size(); i++) {
				in.value(orgList.get(i).getCode());
			}
			predicates.add(in);
		}

		if (StringUtils.isNotEmpty(date)) {
			String startDate = date + " 00:00:00";
			String endDate = date + " 23:59:59";

			Path<Date> exp = root.get("tradeTime");
			predicates
					.add(cb.greaterThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", startDate)));
			predicates.add(cb.lessThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate)));
		}

		if (StringUtils.isNotBlank(billSource)) {
			Path<String> exp = root.get("billSource");
			predicates.add(cb.equal(exp, billSource));
		}
		if (StringUtils.isNotBlank(tradeType)) {
			Path<String> exp = root.get("tradeName");
			predicates.add(cb.equal(exp, tradeType));
		}

		return predicates;
	}
}
