package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.vo.HisPayQueryVo;
import com.yiban.rec.service.HisTransactionFlowService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum.DeleteStatus;
import com.yiban.rec.util.CommonEnum.IsActive;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.StringUtil;
@Service
public class HisTransactionFlowServiceImpl extends BaseOprService implements HisTransactionFlowService {

	@Autowired
	private HisTransactionFlowDao histransactionFlowDao;
	@Autowired
    private MetaDataService metaDataService;
	
	@Override
	public Page<HisTransactionFlow> getHisPayPage(HisPayQueryVo vo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification, pageable);
	}
	
	@Override
	public List<HisTransactionFlow> getHisPayNoPage(HisPayQueryVo vo, List<Organization> orgListTemp, Sort sort) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification, sort);
	}
	
	@Override
	public Long getHisBillCount(HisPayQueryVo vo, List<Organization> orgListTemp) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};

		return histransactionFlowDao.count(specification);
	}
	
	public Map<String,Object> getTradeCollect(HisPayQueryVo vo,List<Organization> orgList){
		StringBuffer sql=new StringBuffer("SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_rec_histransactionflow where 1=1 and is_deleted=0 and is_actived=1 ");
		if(org.apache.commons.lang.StringUtils.isNotBlank(vo.getOrgNo())) {
			String strOrg=" and org_no in(\'"+vo.getOrgNo()+"\'";
			for(Organization v:orgList) {
				strOrg=strOrg+",\'"+v.getCode()+"\'";
			}
			strOrg=strOrg+")";
			sql.append(strOrg);
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getPayFlowNo())) {
				String payFlowNo=" and pay_flow_no=\'"+vo.getPayFlowNo()+"\'";
				sql.append(payFlowNo);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisFlowNo())) {
				String flowNo=" and his_flow_no=\'"+vo.getHisFlowNo()+"\'";
				sql.append(flowNo);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getPayType())) {
				String payType=" and pay_type=\'"+vo.getPayType()+"\'";
				sql.append(payType);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getPatType())) {
				String patType=" and pat_type=\'"+vo.getPatType()+"\'";
				sql.append(patType);
			}
			if (!StringUtils.isEmpty(vo.getStartTime())) {
				String tradeDatatime=" and trade_datatime>=\'"+vo.getStartTime()+" 00:00:00\'";
				sql.append(tradeDatatime);
				
			}
			if (!StringUtils.isEmpty(vo.getEndTime())) {
				String tradeDatatime=" and trade_datatime<=\'"+vo.getEndTime()+" 23:59:59\'";
				sql.append(tradeDatatime);
			}
			
			if (!StringUtils.isEmpty(vo.getHisOrderState())) {
				String orderState=" and order_state=\'"+vo.getHisOrderState()+"\'";
				sql.append(orderState);
			}
//			if (!StringUtils.isEmpty(vo.getTradeType())) {
//				String orderState=" and order_state=\'"+vo.getTradeType()+"\'";
//				sql.append(orderState);
//			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisCredentialsNo())) {
				String credentialsNo=" and credentials_no=\'"+vo.getHisCredentialsNo()+"\'";
				sql.append(credentialsNo);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getCashier())) {
				String cashier=" and cashier=\'"+vo.getCashier()+"\'";
				sql.append(cashier);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisOriPayFlowNo())) {
				String oriPayFlowNo=" and Ori_Pay_Flow_No=\'"+vo.getHisOriPayFlowNo()+"\'";
				sql.append(oriPayFlowNo);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisInvoiceNo())) {
				String invoiceNo=" and invoice_no=\'"+vo.getHisInvoiceNo()+"\'";
				sql.append(invoiceNo);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisPatCode())) {
				String patCode=" and pat_code=\'"+vo.getHisPatCode()+"\'";
				sql.append(patCode);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getHisMzCode())) {
				String mzCode=" and mz_code=\'"+vo.getHisMzCode()+"\'";
				sql.append(mzCode);
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getBillSource())) {
				String billSource=" and bill_source=\'"+vo.getBillSource()+"\'";
				sql.append(billSource);
			}
			if(org.apache.commons.lang.StringUtils.isNotBlank(vo.getCustName())) {
				String custName = " AND `Cust_Name` ='"+vo.getCustName()+"'";
				sql.append(custName);
			}
			if(org.apache.commons.lang.StringUtils.isNotBlank(vo.getBusinessFlowNo())) {
				String businessFlowNo = " AND `Business_Flow_No` ='"+vo.getBusinessFlowNo()+"'";
				sql.append(businessFlowNo);
			}
//			sql.append(" AND pay_type != '0049' ");
		}
		logger.info("--getTradeCollect :" + sql);
		Map<String, Object> map=new HashMap<>();
		List<Map<String, Object>> list = handleNativeSql(sql.toString(),new String[]{"allNum","allAmount"});
		list.get(0).put("allAmount", StringUtil.isNullOrEmpty(list.get(0).get("allAmount"))?new BigDecimal(0):list.get(0).get("allAmount"));
		map.putAll(list.get(0));
		return map;
	}
	
	protected List<Predicate> converSearchCash(HisPayQueryVo cqvo, List<Organization> orgListTemp,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
//		predicates.add(cb.notEqual(root.get("payType"), "0049"));
		if(org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getOrgNo())) {
			In<String> in = cb.in(root.get("orgNo").as(String.class));
			in.value(cqvo.getOrgNo());
			for (int i = 0; i < orgListTemp.size(); i++) {
		    	Organization org = orgListTemp.get(i);
		        if(!StringUtil.equals(cqvo.getOrgNo(), org.getCode())) {
		        	in.value(org.getCode());
		        }
		    }
			predicates.add(in);
		}
		if (!StringUtils.isEmpty(cqvo.getPayFlowNo())) {
			Path<String> paySystemNoExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getPayFlowNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getHisFlowNo())) {
			Path<String> paySystemNoExp = root.get("hisFlowNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getHisFlowNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getPayType())) {
			Path<Integer> payTypeArrExp = root.get("payType");
			predicates.add(cb.equal(payTypeArrExp, cqvo.getPayType()));
		}
		if (!StringUtils.isEmpty(cqvo.getPatType())) {
			Path<Integer> patTypeArrExp = root.get("patType");
			predicates.add(cb.equal(patTypeArrExp, cqvo.getPatType()));
		}
		if (!StringUtils.isEmpty(cqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, cqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(cqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
//			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", DateFormatUtils.format(cqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, cqvo.getEndDate()));
		}
		
		if (!StringUtils.isEmpty(cqvo.getHisOrderState())) {
			Path<Integer> orderStateExp = root.get("orderState");
			predicates.add(cb.equal(orderStateExp, cqvo.getHisOrderState()));
		}
		if (!StringUtils.isEmpty(cqvo.getHisCredentialsNo())) {
			Path<String> credentialsNoExp = root.get("credentialsNo");
			predicates.add(cb.equal(credentialsNoExp, cqvo.getHisCredentialsNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getCashier())) {
			Path<String> credentialsNoExp = root.get("cashier");
			predicates.add(cb.equal(credentialsNoExp, cqvo.getCashier()));
		}
		if (!StringUtils.isEmpty(cqvo.getHisOriPayFlowNo())) {
			Path<String> oriPayFlowNoExp = root.get("oriPayFlowNo");
			predicates.add(cb.equal(oriPayFlowNoExp, cqvo.getHisOriPayFlowNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getHisInvoiceNo())) {
			Path<String> invoiceNoExp = root.get("invoiceNo");
			predicates.add(cb.equal(invoiceNoExp, cqvo.getHisInvoiceNo()));
		}
		// 支付账号
		if (!StringUtils.isEmpty(cqvo.getPayAccount())) {
			Path<String> payAccountExp = root.get("payAccount");
//			predicates.add(cb.equal(payAccountExp, cqvo.getPayAccount()));
			predicates.add(cb.like(payAccountExp.as(String.class), "%" + cqvo.getPayAccount() + "%"));
		}
		if (!StringUtils.isEmpty(cqvo.getHisPatCode())) {
			Path<String> hospitalizationNoExp = root.get("patCode");
			predicates.add(cb.equal(hospitalizationNoExp, cqvo.getHisPatCode()));
		}
		if (!StringUtils.isEmpty(cqvo.getHisMzCode())) {
			Path<String> hospitalizationNoExp = root.get("mzCode");
			predicates.add(cb.equal(hospitalizationNoExp, cqvo.getHisMzCode()));
		}
		if (!StringUtils.isEmpty(cqvo.getBillSource())) {
			Path<String> hospitalizationNoExp = root.get("billSource");
			predicates.add(cb.equal(hospitalizationNoExp, cqvo.getBillSource()));
		}
		if (!StringUtils.isEmpty(cqvo.getCustName())) {
			Path<String> custNameExp = root.get("custName");
			predicates.add(cb.equal(custNameExp, cqvo.getCustName()));
		}if (!StringUtils.isEmpty(cqvo.getBusinessFlowNo())) {
			Path<String> businessFlowNoExp = root.get("businessFlowNo");
			predicates.add(cb.equal(businessFlowNoExp, cqvo.getBusinessFlowNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getTradeType())) {
			Path<String> orderStateExp = root.get("orderState");
			predicates.add(cb.equal(orderStateExp, cqvo.getTradeType()));
		}
		if (!StringUtils.isEmpty(cqvo.getCashBillSource())) {
			if (cqvo.getCashBillSource().equalsIgnoreCase("0000")) {
				Path<String> cashBillSourceExp = root.get("cashBillSource");
				predicates.add(cb.isNotNull(cashBillSourceExp));
			}else {
				Path<String> cashBillSourceExp = root.get("cashBillSource");
				predicates.add(cb.equal(cashBillSourceExp,cqvo.getCashBillSource()));
			}
			
		}

		return predicates;
	}

	@Override
	public Page<HisTransactionFlow> getHisPayPage(HisPayQueryVo vo, List<Organization> orgListTemp, List<String> payTypes, List<String> payBusinessTypes, PageRequest pageable) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp, payTypes, payBusinessTypes, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification, pageable);
	}

	protected List<Predicate> converSearchCash(HisPayQueryVo vo, List<Organization> orgListTemp, List<String> payTypes, List<String> payBusinessTypes, Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		In<String> in = cb.in(root.get("orgNo").as(String.class));
		for (int i = 0; i < orgListTemp.size(); i++) {
	    	Organization org = orgListTemp.get(i);
	        if(StringUtil.equals(vo.getOrgNo(), org.getCode())) {
	        	in.value(org.getCode());
		        if(!StringUtil.isNullOrEmpty(org.getChildren())) {
		        	List<Organization> childList = org.getChildren();
		        	for(Organization org1 : childList){
		        		in.value(org1.getCode());
		        		if(!StringUtil.isNullOrEmpty(org1.getChildren())){
		        			for(Organization orgCl : org1.getChildren()){
		        				in.value(orgCl.getCode());
		        			}
		        		}
		        	}
		        }
	        }
	    }
	    predicates.add(in);
		if (!StringUtils.isEmpty(vo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, vo.getStartDate()));
		}
		if (!StringUtils.isEmpty(vo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", DateFormatUtils.format(vo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		In<String> payTypeIn = cb.in(root.get("payType").as(String.class));
        for (String id : payTypes) {
        	payTypeIn.value(id);
        }
        predicates.add(payTypeIn);
        In<String> payBusinessTypeIn = cb.in(root.get("payBusinessType").as(String.class));
        for (String id : payBusinessTypes) {
        	payBusinessTypeIn.value(id);
        }
        predicates.add(payBusinessTypeIn);
		return predicates;
	}

	@Override
	public List<HisTransactionFlow> getHisPayList(HisPayQueryVo vo, List<Organization> orgListTemp, List<String> payTypes, List<String> payBusinessTypes, Sort sort) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp, payTypes, payBusinessTypes, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification, sort);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfHisBusiness(String startDate, String endDate,String payTypeSql,String orgCodeSql) {
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		String bussiness_7day = "'0451','0551','0151'";
		final String sql = String.format(
				" SELECT Pay_Business_Type, SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_rec_histransactionflow " + 
				" WHERE org_no IN (%s) "+
				" AND Trade_datatime >= '%s' AND Trade_datatime <= '%s' " + 
				" AND pay_type in (%s) " +
				" AND Pay_Business_Type IN (%s) "+
				" GROUP BY Pay_Business_Type "
				,orgCodeSql,startDate,endDate,payTypeSql,bussiness_7day);
		logger.info(" getFollowCountOfHisBusiness sql:{}", sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfHisBusinessOf7Day(String startDate, String endDate,String payTypeSql,String orgCodeSql) {
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		
		StringBuffer sb = new StringBuffer();
        sb.append("SELECT date_table.Trade_Date, dt.Pay_Business_Type, dt.pay_amount FROM");
        sb.append(" (");
        sb.append(" SELECT DATE_FORMAT(DATE_SUB(NOW(), INTERVAL xc DAY), '%Y-%m-%d') AS Trade_Date");
        sb.append(" FROM");
        sb.append(" (SELECT @xi\\:=@xi-1 AS xc FROM");
        sb.append(" (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) xc1");
        sb.append(" ,(SELECT @xi\\:=8) xc0 ");
        sb.append(" ) xcxc");
        sb.append(" ) date_table ");
        sb.append(" LEFT JOIN ");
        sb.append(" (");
        sb.append(" SELECT DATE_FORMAT(Trade_datatime,'%Y-%m-%d') Trade_Date,");
        sb.append(" Pay_Business_Type,");
        sb.append(" SUM(CASE order_state WHEN '0156' THEN pay_amount WHEN '0256' THEN -ABS(pay_amount) END) pay_amount ");
        sb.append(" FROM t_rec_histransactionflow ");
        sb.append(" WHERE 1=1");
        if(org.apache.commons.lang3.StringUtils.isNotBlank(orgCodeSql)) {
            sb.append(" AND org_no IN ("+orgCodeSql+") ");
        }
        sb.append(" AND Trade_datatime >= '"+startDate+"' AND Trade_datatime <= '"+endDate+"' ");
        if(org.apache.commons.lang3.StringUtils.isNotBlank(payTypeSql)) {
            sb.append(" AND pay_type in ("+payTypeSql+") ");
        }
        sb.append(" AND Pay_Business_Type IN('0451', '0551', '0151')");
        sb.append(" GROUP BY Trade_Date, Pay_Business_Type ");
        sb.append(" ) dt");
        sb.append(" ON date_table.Trade_Date = dt.Trade_Date");
		final String sql = sb.toString();
		logger.info(" getFollowCountOfHisBusiness sql:{}", sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfPayType(String startDate, String endDate,String payTypeSql,String orgCodeSql) {
		payTypeSql=payTypeSql.replace("0049", "");
		final String sql = 
				" SELECT rec_pay_type pay_type, SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary " + 
				" WHERE data_source = 'third' and  org_no IN ("+orgCodeSql+") "+
				" AND Trade_Date >= '"+startDate+"' AND Trade_Date <= '"+endDate+"' "+
				" AND rec_pay_type in ("+payTypeSql+") " +
				" GROUP BY rec_pay_type ";
		logger.info(" getFollowCountOfPayType sql:{}", sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfPayTypeOf7Day(String startDate, String endDate,String payTypeSql,String orgCodeSql) {
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT date_table.Trade_Date, dt.pay_type, dt.pay_amount FROM");
		sb.append(" (");
		sb.append(" SELECT DATE_FORMAT(DATE_SUB(NOW(), INTERVAL xc DAY), '%Y-%m-%d') AS Trade_Date");
		sb.append(" FROM");
		sb.append(" (SELECT @xi\\:=@xi-1 AS xc FROM");
		sb.append(" (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) xc1");
		sb.append(" ,(SELECT @xi\\:=8) xc0 ");
		sb.append(" ) xcxc");
		sb.append(" ) date_table ");
		sb.append(" LEFT JOIN ");
		sb.append(" (SELECT DATE_FORMAT(Trade_datatime,'%Y-%m-%d') Trade_Date,");
		sb.append(" rec_pay_type pay_type,");
		sb.append(" SUM(CASE order_state WHEN '0156' THEN pay_amount WHEN '0256' THEN -ABS(pay_amount) END) pay_amount ");
		sb.append(" FROM t_thrid_bill ");
		sb.append(" WHERE 1=1 ");
		if(org.apache.commons.lang3.StringUtils.isNotBlank(orgCodeSql)) {
		    sb.append(" AND org_no IN ("+orgCodeSql+") ");
		}
		sb.append(" AND Trade_datatime >= '"+startDate+"' AND Trade_datatime <= '"+endDate+"' ");
		if(org.apache.commons.lang3.StringUtils.isNotBlank(payTypeSql)) {
		    sb.append(" AND rec_pay_type in ("+payTypeSql+") ");
		}
		sb.append(" GROUP BY Trade_Date, rec_pay_type ");
		sb.append(" ) dt");
		sb.append(" ON date_table.Trade_Date = dt.Trade_Date");
		
		final String sql = sb.toString();
		logger.info(" getFollowCountOfPayTypeOf7Day sql:{}", sql) ;
		return super.queryList(sql, null, null);
	}

	//渭南市定制化现金对账汇总界面统计
		@Override
		public Map<String, Object> searchSumary(HisPayQueryVo vo, List<Organization> orgList) {
			String cashBillSource = vo.getCashBillSource();
			
			String billSourceSQL = "";
			if (StringUtil.checkNotNull(cashBillSource)) {
				billSourceSQL = " and cash_bill_Source = '"+cashBillSource+"'";
			}else {
				billSourceSQL = " and cash_bill_Source is NOT NULL";
			}
			Map<String, Object> map = new HashMap<>();
			List<Map<String, Object>> dataList=new ArrayList<>();
			List<Map<String, Object>> lineList = getDate();
			//门诊金额，门诊缴费笔数，门诊退费笔数
			String mzsql = "SELECT SUM(CASE order_state WHEN '0156' THEN pay_amount WHEN '0256' THEN -ABS(pay_amount) END) as mzAllAmount,SUM(CASE order_state WHEN '0156' THEN 1 WHEN '0256' THEN 0 END) as mzPayCount,SUM(CASE order_state WHEN '0156' THEN 0 WHEN '0256' THEN 1 END) as mzRefundCount FROM t_rec_histransactionflow where Trade_datatime >= '"+vo.getStartTime()+"' AND Trade_datatime <= '"+vo.getEndTime()+"' and pat_type ='mz'"+billSourceSQL;
			List<Map<String, Object>> mzList =queryList(mzsql,null,null);
			if (mzList.size()>0) {
				dataList.add(mzList.get(0));
			}
			//住院金额，住院缴费笔数，住院退费笔数
			String zysql = "SELECT SUM(CASE order_state WHEN '0156' THEN pay_amount WHEN '0256' THEN -ABS(pay_amount) END) as zyAllAmount,SUM(CASE order_state WHEN '0156' THEN 1 WHEN '0256' THEN 0 END) as zyPayCount,SUM(CASE order_state WHEN '0156' THEN 0 WHEN '0256' THEN 1 END) as zyRefundCount FROM t_rec_histransactionflow where Trade_datatime >= '"+vo.getStartTime()+"' AND Trade_datatime <= '"+vo.getEndTime()+"' and pat_type ='zy'"+billSourceSQL;
			List<Map<String, Object>> zyList =queryList(zysql,null,null);
			if (zyList.size()>0) {
				dataList.add(zyList.get(0));
			}
			//汇总金额，汇总缴费笔数，汇总退费笔数
			String hzsql = "SELECT SUM(CASE order_state WHEN '0156' THEN pay_amount WHEN '0256' THEN -ABS(pay_amount) END) as hzAllAmount,SUM(CASE order_state WHEN '0156' THEN 1 WHEN '0256' THEN 0 END) as hzPayCount,SUM(CASE order_state WHEN '0156' THEN 0 WHEN '0256' THEN 1 END) as hzRefundCount FROM t_rec_histransactionflow where Trade_datatime >= '"+vo.getStartTime()+"' AND Trade_datatime <= '"+vo.getEndTime()+"' "+billSourceSQL;
			List<Map<String, Object>> hzList =queryList(hzsql,null,null);
			if (hzList.size()>0) {
				dataList.add(hzList.get(0));
			}
			map.put("headLine", lineList);
			map.put("headData", dataList);
			return map;
		}
		
		/**
	     * 获取异常数目
	     * @param startDate
	     * @param endDate
	     * @param payType
	     * @param orgNo
	     * @param billSource
	     * @return
	     */
	    private List<Map<String, Object>> getDate() {
	    	List<Map<String, Object>> mapList= new ArrayList<>();
	    	Map<String, Object> mapAll= new HashMap<>();
	    	mapAll.put("name", "全部");
	    	mapAll.put("value", "");
	    	mapList.add(mapAll);
	    	//获取订单来源
	    	List<MetaData> metaDataList = metaDataService.findMetaDataByDataTypeValue("cash_bill_source");
	    	if (metaDataList != null) {
	    		for(MetaData v:metaDataList) {
	        		Map<String, Object> map= new HashMap<>();
	        		map.put("value", v.getValue());
	        		map.put("name", v.getName());
	        		mapList.add(map);
	        	}
			}
			return mapList;
	    }
    
    
	
}
