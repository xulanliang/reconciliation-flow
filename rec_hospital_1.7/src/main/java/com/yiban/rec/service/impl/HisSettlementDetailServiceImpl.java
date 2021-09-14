package com.yiban.rec.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.settlement.RecHisSettlementDao;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.vo.HisSettlementDetailVo;
import com.yiban.rec.service.HisSettlementDetailService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;

@Service
public class HisSettlementDetailServiceImpl extends BaseOprService implements HisSettlementDetailService {

	@Autowired
	private RecHisSettlementDao recHisSettlementDao;

	@Autowired
	private HisTransactionFlowDao hisTransactionFlowDao;
	
	@Override
	public Page<RecHisSettlement> queryPage(HisSettlementDetailVo vo, List<Organization> orgList, Pageable pageable) {

		Specification<RecHisSettlement> specification = new Specification<RecHisSettlement>() {
			@Override
			public Predicate toPredicate(Root<RecHisSettlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(vo, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return recHisSettlementDao.findAll(specification, pageable);
	}

	@Override
	public List<RecHisSettlement> queryNoPage(HisSettlementDetailVo vo, List<Organization> orgList, Sort sort) {

		Specification<RecHisSettlement> specification = new Specification<RecHisSettlement>() {
			@Override
			public Predicate toPredicate(Root<RecHisSettlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = filterCondition(vo, orgList, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return recHisSettlementDao.findAll(specification, sort);
	}

	@Override
	public Map<String, Object> querySum(HisSettlementDetailVo vo, List<Organization> orgList) {
		StringBuilder sb = new StringBuilder();
		// 总金额：(缴费 减 退费)，笔数：(缴费 加 退费 )
		sb.append("SELECT "
//				+ " IFNULL(SUM(CASE WHEN t.`order_type`='0156' THEN ABS(t.`amount`) ELSE -ABS(t.`amount`) END), 0.00) amountSum,"
				+ " IFNULL(SUM(t.amount), 0.00) amountSum,"
				+ " COUNT(1) billsCount FROM t_rec_his_settlement t WHERE 1=1 ");

		if (StringUtils.isNotBlank(vo.getSettlementDate())) {
			sb.append(" AND t.`settlement_date` = ").append("'" + vo.getSettlementDate() + "'");
		}
		if (StringUtils.isNotBlank(vo.getOrderState())) {
			sb.append(" AND t.`order_type` = ").append("'" + vo.getOrderState() + "'");
		}
		if (StringUtils.isNotBlank(vo.getTradeDate())) {
			sb.append(" AND DATE(t.`pay_time`) = ").append("'" + vo.getTradeDate() + "'");
		}
		if (StringUtils.isNotBlank(vo.getBillSource())) {
			sb.append(" AND t.`bill_source`= ").append("'" + vo.getBillSource() + "'");
		}
		if (StringUtils.isNotBlank(vo.getSettlementStartDate()) && StringUtils.isNotBlank(vo.getSettlementEndDate())) {
			String startDate = vo.getSettlementStartDate() + " 00:00:00";
			String endDate = vo.getSettlementEndDate() + " 23:59:59";
			sb.append(" AND t.`settlement_time` <= '" + endDate + "' AND t.`settlement_time` >= '"
					+ startDate + "'");
		}
		if (StringUtils.isNotBlank(vo.getSettlementSerialNo())) {
			sb.append(" AND t.`settlement_serial_no` = '" + vo.getSettlementSerialNo() + "'");
		}
		if (StringUtils.isNotBlank(vo.getOutTradeNo())) {
			sb.append(" AND t.`out_trade_no` = '" + vo.getOutTradeNo() + "'");
		}
		if (StringUtils.isNotBlank(vo.getPayType())) {
			sb.append(" AND t.`pay_type` = '" + vo.getPayType() + "'");
		}
		if (StringUtils.isNotBlank(vo.getTnsOrderNo())) {
			sb.append(" AND t.`tns_order_no` = '" + vo.getTnsOrderNo() + "'");
		}
		if (StringUtils.isNotBlank(vo.getHisOrderNo())) {
			sb.append(" AND t.`his_order_no` ='" + vo.getHisOrderNo() + "'");
		}
		if (StringUtils.isNotBlank(vo.getTradeType())) {
			sb.append(" AND t.`order_type` ='" + vo.getTradeType() + "'");
		}
		if(StringUtils.isNotBlank(vo.getPayBusinessType())){
			sb.append(" AND t.`pay_business_type` ='" + vo.getPayBusinessType() + "'");
		}
		logger.info("查询his结算明细汇总金额和笔数 sql = " + sb.toString());

		List<Map<String, Object>> mapList = super.handleNativeSql(sb.toString(),
				new String[] { "amountSum", "billsCount" });

		logger.info(" mapList = " + mapList);
		return mapList.get(0);
	}

	@Override
	public Map<String, List<Map<String, Object>>> queryTradeDateList(HisSettlementDetailVo vo) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DATE_FORMAT(t.`pay_time`, '%Y-%m-%d') payDate  FROM t_rec_his_settlement t where 1=1 ");

		if (StringUtils.isNotBlank(vo.getSettlementDate())) {
			sb.append(" AND t.`settlement_date` = ").append("'" + vo.getSettlementDate() + "'");
		}
		if (StringUtils.isNotBlank(vo.getBillSource())) {
			sb.append(" AND t.`bill_source`= ").append("'" + vo.getBillSource() + "'");
		}
		sb.append(" GROUP BY payDate  ORDER BY payDate ");
		logger.info("查询his结算明细交易时间查询条件 sql = " + sb.toString());

		List<Map<String, Object>> dateList = super.queryList(sb.toString(), null, null);
		HashMap<String, List<Map<String, Object>>> res = new HashMap<>();
		res.put("dateList", dateList);

		logger.info("dateList = " + dateList);
		return res;
	}

	protected List<Predicate> filterCondition(HisSettlementDetailVo vo, List<Organization> orgList, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();

//		if (orgList != null && orgList.size() > 0) {
//			In<String> in = cb.in(root.get("orgNo").as(String.class));
//			for (int i = 0; i < orgList.size(); i++) {
//				in.value(orgList.get(i).getCode());
//			}
//			predicates.add(in);
//		}
		// 结账日期
		if (StringUtils.isNotBlank(vo.getSettlementDate())) {
			Path<Date> exp = root.get("settlementDate");
			predicates.add(cb.equal(exp, DateUtil.transferStringToDate("yyyy-MM-dd", vo.getSettlementDate())));
		}

		// 结算时间
		if (StringUtils.isNotEmpty(vo.getSettlementStartDate()) && StringUtils.isNotBlank(vo.getSettlementEndDate())) {
			String startDate = vo.getSettlementStartDate() + " 00:00:00";
			String endDate = vo.getSettlementEndDate() + " 23:59:59";

			Path<Date> exp = root.get("settlementTime");
			predicates
					.add(cb.greaterThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", startDate)));
			predicates.add(cb.lessThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate)));
		}
		// 交易时间
		if (StringUtils.isNotEmpty(vo.getTradeDate())) {
			String startDate = vo.getTradeDate() + " 00:00:00";
			String endDate = vo.getTradeDate() + " 23:59:59";

			Path<Date> exp = root.get("payTime");
			predicates
					.add(cb.greaterThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", startDate)));
			predicates.add(cb.lessThanOrEqualTo(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate)));
		}

		if (StringUtils.isNotEmpty(vo.getOrderState())) {
			Path<String> exp = root.get("orderType");
			predicates.add(cb.equal(exp, vo.getOrderState()));
		}

		if (StringUtils.isNotBlank(vo.getBillSource())) {
			Path<String> exp = root.get("billSource");
			predicates.add(cb.equal(exp, vo.getBillSource()));
		}

		if (StringUtils.isNotBlank(vo.getSettlementSerialNo())) {
			Path<String> exp = root.get("settlementSerialNo");
			predicates.add(cb.equal(exp, vo.getSettlementSerialNo()));
		}
		if (StringUtils.isNotBlank(vo.getHisOrderNo())) {
			Path<String> exp = root.get("hisOrderNo");
			predicates.add(cb.equal(exp, vo.getHisOrderNo()));
		}
		if (StringUtils.isNotBlank(vo.getTnsOrderNo())) {
			Path<String> exp = root.get("tnsOrderNo");
			predicates.add(cb.equal(exp, vo.getTnsOrderNo()));
		}
		if (StringUtils.isNotBlank(vo.getPayType())) {
			Path<String> exp = root.get("payType");
			predicates.add(cb.equal(exp, vo.getPayType()));
		}
		if (StringUtils.isNotBlank(vo.getOutTradeNo())) {
			Path<String> exp = root.get("outTradeNo");
			predicates.add(cb.equal(exp, vo.getOutTradeNo()));
		}
		if (StringUtils.isNotBlank(vo.getTradeType())) {
			Path<String> exp = root.get("orderType");
			predicates.add(cb.equal(exp, vo.getTradeType()));
		}
		if (StringUtils.isNotBlank(vo.getPayBusinessType())){
			Path<String> exp = root.get("payBusinessType");
			predicates.add(cb.equal(exp, vo.getPayBusinessType()));
		}
		return predicates;
	}

	public List<Map<String, Object>> querySelect(String value) {
		String sql = String.format("SELECT" + 
				"  t.name `value`," + 
				"  t.value `id`" + 
				"FROM" + 
				"  t_meta_data t " + 
				" WHERE t.type_id =" + 
				"  (SELECT" + 
				"    id" + 
				"  FROM" + 
				"    `t_meta_data_type`" + 
				"  WHERE VALUE = '%s')", value);
		return super.handleNativeSqlColumns(sql, new String[] {"value", "id"});
	}
	
	private List<String> getOmissNotNo(String date,String billSourece){
		String nextDate = DateUtil.transferDateToDateFormat("yyyy-MM-dd",DateUtil.getBeginDayOfTomorrow(DateUtil.transferStringToDate("yyyy-MM-dd", date)));
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("SELECT t.his_order_no FROM t_rec_his_settlement t WHERE ");
		sbuf.append(" DATE_FORMAT(t.pay_time,'%Y-%m-%d') = '").append(date).append("' ");
		sbuf.append(" AND( DATE_FORMAT(t.settlement_date,'%Y-%m-%d') = '").append(nextDate).append("' ");
		sbuf.append(" OR DATE_FORMAT(t.settlement_date,'%Y-%m-%d') = '").append(date).append("') ");
		sbuf.append(" AND t.bill_source = '").append(billSourece).append("' ");
		List<String> data = (List)super.handleNativeSql4SingleCol(sbuf.toString());
		return data;
	}
	
	@Override
	public Page<HisTransactionFlow> getOmissionAmountPage(HisSettlementDetailVo vo, List<Organization> orgList,
			PageRequest pageable) {
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = Lists.newArrayList();

				if (orgList != null && orgList.size() > 0) {
					In<String> in = cb.in(root.get("orgNo").as(String.class));
					for (int i = 0; i < orgList.size(); i++) {
						in.value(orgList.get(i).getCode());
					}
					predicates.add(in);
				}
				// 结账日期
				if (StringUtils.isNotBlank(vo.getTradeDate())) {
					Path<Date> exp = root.get("tradeDatatime");
					predicates.add(cb.between(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getTradeDate()+" 00:00:00"),DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getTradeDate()+" 23:59:59")));
//					predicates.add(cb.or(cb.greaterThan(root.get("settlementDate"), DateUtil.getBeginDayOfTomorrow(DateUtil.transferStringToDate("yyyy-MM-dd", vo.getTradeDate()))),cb.isNull(root.get("settlementDate"))));
					List<String> omissNotNo = getOmissNotNo(vo.getTradeDate(),vo.getBillSource());
//					if(omissNotNo!=null&&omissNotNo.size()>0){
//						for (String no : omissNotNo) {
//	                        if (no != null) {
//	                            predicates.add(cb.notEqual(root.get("hisFlowNo"), no));
//	                        }
//	                    }
//					}
					In<String> in = cb.in(root.get("hisFlowNo"));
	                if (omissNotNo != null && omissNotNo.size()>0) {
	                    for (String no : omissNotNo) {
	                        if (StringUtils.isNotBlank(no)) {
	                            in.value(no);
	                        }
	                    }
	                    predicates.add(cb.not(in));
	                }
				}
				if (StringUtils.isNotBlank(vo.getOutTradeNo())) {
					Path<String> exp = root.get("payFlowNo");
					predicates.add(cb.equal(exp, vo.getOutTradeNo()));
				}
				if (StringUtils.isNotBlank(vo.getHisOrderNo())) {
					Path<String> exp = root.get("hisFlowNo");
					predicates.add(cb.equal(exp, vo.getHisOrderNo()));
				}
				if (StringUtils.isNotBlank(vo.getPatientName())) {
					Path<String> exp = root.get("custName");
					predicates.add(cb.equal(exp, vo.getPatientName()));
				}
				if(StringUtils.isNotBlank(vo.getTradeType())){
					Path<String> exp = root.get("orderState");
					predicates.add(cb.equal(exp, vo.getTradeType()));
				}
				if(StringUtils.isNotBlank(vo.getBillSource())){
					Path<String> exp = root.get("billSource");
					predicates.add(cb.equal(exp, vo.getBillSource()));
				}
				predicates.add(cb.notEqual(root.get("payType"), "0049"));
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return hisTransactionFlowDao.findAll(specification, pageable);
	}

	@Override
	public Map<String, Object> getOmissionAmountCollect(HisSettlementDetailVo vo, List<Organization> orgList) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = DateUtil.getBeginDayOfTomorrow(new Date());
		try {
			date = DateUtil.getBeginDayOfTomorrow(fmt.parse(vo.getTradeDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("SELECT COUNT(1),IFNULL(SUM(CASE WHEN t.Order_State = '0256' THEN -ABS(t.Pay_Amount) ELSE ABS(t.Pay_Amount) END),0) FROM t_rec_histransactionflow t ");
		sbuf.append(" WHERE 1=1 ");
		sbuf.append(" AND DATE_FORMAT(t.Trade_datatime,'%Y-%m-%d') = '").append(vo.getTradeDate()).append("' ");
		sbuf.append(" AND his_flow_no not in (");
		sbuf.append(" select his_order_no from t_rec_his_settlement a where DATE_FORMAT(a.pay_time,'%Y-%m-%d') = '").append(vo.getTradeDate()).append("' ");
		sbuf.append(" AND (DATE_FORMAT(a.settlement_date,'%Y-%m-%d') = '").append(fmt.format(date)).append("' ");
		sbuf.append(" OR DATE_FORMAT(a.settlement_date,'%Y-%m-%d') = '").append(vo.getTradeDate()).append("') ");
		sbuf.append(" AND a.bill_source = '").append(vo.getBillSource()).append("') ");
//		sbuf.append(" AND DATE_FORMAT(t.settlement_date,'%Y-%m-%d') > '").append(fmt.format(date)).append("' ");
		if(StringUtils.isNotBlank(vo.getOutTradeNo())){
			sbuf.append(" AND t.Pay_Flow_No = '").append(vo.getOutTradeNo()).append("' ");
		}
		if(StringUtils.isNotBlank(vo.getHisOrderNo())){
			sbuf.append(" AND t.his_flow_no = '").append(vo.getHisOrderNo()).append("' ");
		}
		if(StringUtils.isNotBlank(vo.getPatientName())){
			sbuf.append(" AND t.Cust_Name = '").append(vo.getPatientName()).append("' ");
		}
		if (StringUtils.isNotBlank(vo.getTradeType())) {
			sbuf.append(" AND t.`Order_State` ='" + vo.getTradeType() + "'");
		}
		if(StringUtils.isNotBlank(vo.getBillSource())){
			sbuf.append(" AND t.bill_source = '").append(vo.getBillSource()).append("' ");
		}
		
		sbuf.append(" AND t.pay_type!='0049' ");
		List<Map<String, Object>> data = super.handleNativeSql(sbuf.toString(),new String[]{"allCount","allAmount"});
		logger.info(" 漏结账单金额明细总金额 sql ==  "+sbuf.toString());
		return data.get(0);
	}

	@Override
	public Page<RecHisSettlement> getBeforeSettlementPage(HisSettlementDetailVo vo, List<Organization> orgList,
			PageRequest pageable) {
		Specification<RecHisSettlement> specification = new Specification<RecHisSettlement>() {
			@Override
			public Predicate toPredicate(Root<RecHisSettlement> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = Lists.newArrayList();

				if (orgList != null && orgList.size() > 0) {
					In<String> in = cb.in(root.get("orgCode").as(String.class));
					for (int i = 0; i < orgList.size(); i++) {
						in.value(orgList.get(i).getCode());
					}
					predicates.add(in);
				}
				// 结账日期
				if (StringUtils.isNotBlank(vo.getSettlementDate())) {
					Path<Date> exp = root.get("settlementDate");
					predicates.add(cb.between(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getSettlementDate()+" 00:00:00"),DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getSettlementDate()+" 23:59:59")));
					predicates.add(cb.lessThan(root.get("payTime"), DateUtil.addDay(DateUtil.transferStringToDate("yyyy-MM-dd", vo.getSettlementDate()),-1)));
				}
				if (StringUtils.isNotBlank(vo.getTradeDate())){
					Path<Date> exp = root.get("payTime");
					predicates.add(cb.between(exp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getTradeDate()+" 00:00:00"),DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", vo.getTradeDate()+" 23:59:59")));
				}
				if (StringUtils.isNotBlank(vo.getOrderState())) {
					Path<String> exp = root.get("orderType");
					predicates.add(cb.equal(exp, vo.getOrderState()));
				}
				if (StringUtils.isNotBlank(vo.getTradeType())) {
					Path<String> exp = root.get("orderType");
					predicates.add(cb.equal(exp, vo.getTradeType()));
				}
				if(StringUtils.isNotBlank(vo.getBillSource())){
					Path<String> exp = root.get("billSource");
					predicates.add(cb.equal(exp, vo.getBillSource()));
				}
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return recHisSettlementDao.findAll(specification, pageable);
	}

	@Override
	public Map<String, Object> getBeforeSettlementCollect(HisSettlementDetailVo vo, List<Organization> orgList) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = DateUtil.getBeginDayOfTomorrow(new Date());
		try {
			date = DateUtil.addDay(fmt.parse(vo.getSettlementDate()),-1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("SELECT COUNT(1),IFNULL(SUM(t.amount),0) FROM t_rec_his_settlement t ");
		sbuf.append(" WHERE 1=1 ");
		sbuf.append(" AND DATE_FORMAT(t.pay_time,'%Y-%m-%d') < '").append(fmt.format(date)).append("' ");
		sbuf.append(" AND DATE_FORMAT(t.settlement_date,'%Y-%m-%d') = '").append(vo.getSettlementDate()).append("' ");
		if(StringUtils.isNotBlank(vo.getTradeDate())){
			sbuf.append(" AND DATE_FORMAT(t.pay_time,'%Y-%m-%d') = '").append(vo.getTradeDate()).append("' ");
		}
		if(StringUtils.isNotBlank(vo.getOrderState())){
			sbuf.append(" AND t.order_type = '").append(vo.getOrderState()).append("' ");
		}
		if (StringUtils.isNotBlank(vo.getTradeType())) {
			sbuf.append(" AND t.`order_type` ='" + vo.getTradeType() + "'");
		}
		if(StringUtils.isNotBlank(vo.getBillSource())){
			sbuf.append(" AND t.bill_source = '").append(vo.getBillSource()).append("' ");
		}
		List<Map<String, Object>> data = super.handleNativeSql(sbuf.toString(),new String[]{"allCount","allAmount"});
		logger.info(" 结账以前金额明细总金额 sql ==  "+sbuf.toString());
		return data.get(0);
	}

	@Override
	public Map<String, List<Map<String, Object>>> queryBeforeTradeDateList(HisSettlementDetailVo vo) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		Date date = DateUtil.getBeginDayOfTomorrow(new Date());
		try {
			date = DateUtil.addDay(fmt.parse(vo.getSettlementDate()),-1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DATE_FORMAT(t.`pay_time`, '%Y-%m-%d') payDate  FROM t_rec_his_settlement t where 1=1 ");

		if (StringUtils.isNotBlank(vo.getSettlementDate())) {
			sb.append(" AND t.`settlement_date` = ").append("'" + vo.getSettlementDate() + "'");
			sb.append(" AND DATE_FORMAT(t.pay_time,'%Y-%m-%d') < '").append(fmt.format(date)).append("' ");
		}
		if (StringUtils.isNotBlank(vo.getBillSource())) {
			sb.append(" AND t.`bill_source`= ").append("'" + vo.getBillSource() + "'");
		}
		sb.append(" GROUP BY payDate  ORDER BY payDate ");
		logger.info("查询his结算明细交易时间查询条件 sql = " + sb.toString());

		List<Map<String, Object>> dateList = super.queryList(sb.toString(), null, null);
		HashMap<String, List<Map<String, Object>>> res = new HashMap<>();
		res.put("dateList", dateList);

		logger.info("dateList = " + dateList);
		return res;
	}
}
