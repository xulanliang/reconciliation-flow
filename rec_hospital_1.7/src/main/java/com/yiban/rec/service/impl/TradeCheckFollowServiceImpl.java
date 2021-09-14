package com.yiban.rec.service.impl;

import java.util.Date;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.TradeCheckFollowQueryVo;
import com.yiban.rec.service.TradeCheckFollowService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;

@Service("tradeCheckFollowService")
public class TradeCheckFollowServiceImpl extends BaseOprService implements TradeCheckFollowService {
	
	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;
	@Autowired
	private OrganizationService organizationService;
	
	

	@Override
	public Page<TradeCheckFollow> findAllHisPayPageByNotZP(TradeCheckFollowQueryVo vo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tradeCheckFollowDao.findAll(specification, pageable);
	}

	protected List<Predicate> converSearchCash(TradeCheckFollowQueryVo vo, List<Organization> orgListTemp, Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> checkStateExp = root.get("checkState");
		predicates.add(cb.notEqual(checkStateExp, vo.getCheckState()));
		
		if (!StringUtils.isEmpty(vo.getOrgNo())) {
			In<String> in = cb.in(root.get("orgNo").as(String.class));
			//获取所有的机构编码
			List<Organization> orgList = organizationService.findByParentCode(vo.getOrgNo());
			Organization o = new Organization();
			o.setCode(vo.getOrgNo());
			orgList.add(o);
			if (orgList != null && orgList.size() > 0) {
				for(Organization org : orgList){
					if(org != null){
						in.value(org.getCode());
					}
				}
			}
			predicates.add(in);
		}
		
		if (!StringUtils.isEmpty(vo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeTime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", DateFormatUtils.format(vo.getStartDate(), "yyyy-MM-dd 00:00:00"))));
		}
		if (!StringUtils.isEmpty(vo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeTime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", DateFormatUtils.format(vo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		return predicates;
	}

	@Override
	public List<TradeCheckFollow> findAllHisPayPageByNotZP(TradeCheckFollowQueryVo vo, List<Organization> orgListTemp, Sort sort) {
		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(vo, orgListTemp,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tradeCheckFollowDao.findAll(specification, sort);
	}
	//渠道减去自动冲正数据
	private String substractAutoRefundNum(String payDate, String endTime,String surface){
		String endPayDate = endTime+" 23:59:59";
		String sql ="";
		if("t_order_upload".equals(surface)) {
			sql = " SELECT tsn_order_no Pay_Flow_No FROM t_order_upload f "+
					" WHERE trade_date_time >= '"+payDate+"' AND trade_date_time <= '"+endPayDate+"' "+
					" GROUP BY tsn_order_no,out_trade_no,order_no,org_code,ABS(Pay_Amount) "+
					" HAVING COUNT(*) = 2 "+
					" AND  NOT EXISTS "+
					" ( SELECT 1 FROM t_rec_histransactionflow t "+
					" WHERE t.Trade_datatime >= '"+payDate+"' AND t.Trade_datatime <= '"+endPayDate+"' "+
					" AND  f.org_code = t.org_no " +
					" AND (f.tsn_order_no = t.Pay_Flow_No OR f.out_trade_no = t.Pay_Flow_No OR f.order_no = t.Pay_Flow_No) "+
					" GROUP BY t.Pay_Flow_No, ABS(t.Pay_Amount) "+
					" HAVING COUNT(*) = 2 ) ";
		}else {
			sql = " SELECT Pay_Flow_No FROM t_thrid_bill f "+
					" WHERE Trade_datatime >= '"+payDate+"' AND Trade_datatime <= '"+endPayDate+"' "+
					" GROUP BY Pay_Flow_No, shop_flow_no, order_no, out_trade_no,org_no, ABS(Pay_Amount) "+
					" HAVING COUNT(*) = 2 "+
					" AND  NOT EXISTS "+
					" ( SELECT 1 FROM t_rec_histransactionflow t "+
					" WHERE t.Trade_datatime >= '"+payDate+"' AND t.Trade_datatime <= '"+endPayDate+"' "+
					" AND  f.org_no = t.org_no " +
					" AND (f.Pay_Flow_No = t.Pay_Flow_No OR f.shop_flow_no = t.Pay_Flow_No OR f.order_no = t.Pay_Flow_No OR f.out_trade_no = t.Pay_Flow_No) "+
					" GROUP BY t.Pay_Flow_No, ABS(t.Pay_Amount) "+
					" HAVING COUNT(*) = 2 ) ";
		}
		
		return sql;
	}

	@Override
	public String getTradeCheckFollow(String startTime, String endTime,String surface) {
		List<Map<String, Object>> list=null;
		String ret="";
		String sql="";
		sql=substractAutoRefundNum(startTime,endTime,surface);
		try {
			String[] fields = "Pay_Flow_No,Order_State".split(",");
			list = handleNativeSqlColumns(sql,fields);
			if(list!=null&&list.size()>0) {
				for(Map<String, Object> v:list) {
					String payNo=(String) v.get("Pay_Flow_No");
					if(org.apache.commons.lang.StringUtils.isBlank(ret)) {
						ret="\'"+payNo+"\'";
					}else {
						ret=ret+",\'"+payNo+"\'";
					}	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ret;
		}
		return ret;
	}

}
