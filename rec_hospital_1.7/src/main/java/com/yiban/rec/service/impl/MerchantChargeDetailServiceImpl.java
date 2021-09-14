package com.yiban.rec.service.impl;

import java.math.BigDecimal;
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
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.service.MerchantChargeDetailService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum.DeleteStatus;
import com.yiban.rec.util.CommonEnum.IsActive;
import com.yiban.rec.util.DateUtil;

@Service
public class MerchantChargeDetailServiceImpl extends BaseOprService implements MerchantChargeDetailService {
	@Autowired
	private ThirdBillDao thirdBillDao;

	@Override
	public Page<ThirdBill> queryThirdBillPage(String billSource, String date, String tsnOrderNo,
			List<Organization> orgList,String tradeType, Pageable pageable) {

		Specification<ThirdBill> specification = new Specification<ThirdBill>() {
			@Override
			public Predicate toPredicate(Root<ThirdBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(billSource, date, tsnOrderNo,tradeType, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return thirdBillDao.findAll(specification, pageable);
	}
	
	@Override
	public List<ThirdBill> queryThirdBillNoPage(String billSource, String date, String tsnOrderNo,
			List<Organization> orgList,String tradeType, Sort sort) {

		Specification<ThirdBill> specification = new Specification<ThirdBill>() {
			@Override
			public Predicate toPredicate(Root<ThirdBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(billSource, date, tsnOrderNo,tradeType, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return thirdBillDao.findAll(specification, sort);
	}
	
	protected List<Predicate> filterCondition(String billSource, String date, String tsnOrderNo,String tradeType,
			List<Organization> orgList, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));

		if (orgList != null && orgList.size() > 0) {
			In<String> in = cb.in(root.get("orgNo").as(String.class));
			for (int i = 0; i < orgList.size(); i++) {
				in.value(orgList.get(i).getCode());
			}
			predicates.add(in);
		}
		if (StringUtils.isNotEmpty(tsnOrderNo)) {
			Path<String> paySystemNoExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySystemNoExp, tsnOrderNo));
		}
		if (StringUtils.isNotEmpty(billSource)) {
			Path<String> billSourceExp = root.get("billSource");
			predicates.add(cb.equal(billSourceExp, billSource));
		}
		if (StringUtils.isNotEmpty(date)) {
			String startDate = date + " 00:00:00";
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp,
					DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", startDate)));
		}
		if(StringUtils.isNotEmpty(tradeType)){
			Path<String> orderStatusExp = root.get("orderState");
			predicates.add(cb.equal(orderStatusExp, tradeType));
		}
		if (StringUtils.isNotEmpty(date)) {
			String endDate = date + " 23:59:59";
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(
					cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate)));
		}
		return predicates;
	}

	@Override
	public BigDecimal querySum(String billSource, String date, String tsnOrderNo, List<Organization> orgList,String tradeType) {
		StringBuilder sb = new StringBuilder();
		sb.append(
		" SELECT " + 
		"  IFNULL(SUM(CASE WHEN t.`Order_State`='0156' THEN ABS(t.`Pay_Amount`) ELSE -ABS(t.`Pay_Amount`) END), 0.00) " + 
		" FROM t_thrid_bill t" + 
		" WHERE 1=1 ");
		if (orgList != null && orgList.size() > 0) {
			sb.append(" AND t.`org_no` IN (");
			for (Organization org : orgList) {
				sb.append("'").append(org.getCode()).append("'");
			}
			sb.append(" ) ");
		}
		if (StringUtils.isNotBlank(date)) {
			String sDate = date + " 00:00:00";
			String eDate = date + " 23:59:59";
			sb.append(" AND t.`Trade_datatime` >= ").append("'" + sDate + "'");
			sb.append(" AND t.`Trade_datatime` <= ").append("'" + eDate + "'");
		}
		if (StringUtils.isNotBlank(tsnOrderNo)) {
			sb.append(" AND t.`Pay_Flow_No` = ").append("'" + tsnOrderNo + "'");
		}
		if (StringUtils.isNotBlank(billSource)) {
			sb.append(" AND t.`bill_source` = ").append("'" + billSource + "'");
		}
		if (StringUtils.isNotBlank(tradeType)) {
			sb.append(" AND t.`Order_State` = ").append("'" + tradeType + "'");
		}
		logger.info("统计商户收款明细总金额 sql = " + sb.toString());
		
		BigDecimal amount = new BigDecimal(super.handleNativeSql4SingleRes(sb.toString()).toString());
		logger.info("amount = " + amount);
		return amount;
	}
	
	
}
