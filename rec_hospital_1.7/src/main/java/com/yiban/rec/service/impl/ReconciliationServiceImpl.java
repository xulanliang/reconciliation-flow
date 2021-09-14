package com.yiban.rec.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.dao.HisPayResultDao;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.OrderUploadDao;
import com.yiban.rec.dao.PlatformFlowDao;
import com.yiban.rec.dao.PlatformFlowLogDao;
import com.yiban.rec.dao.RecCashDao;
import com.yiban.rec.dao.ReconcilitationDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.OrderUpload;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.PlatformrawLog;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.CashQueryVo;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.domain.vo.RefundRequestVo;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.domain.vo.TradeDetailQueryVo;
import com.yiban.rec.domain.vo.TradeDetailVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.RefundService;
import com.yiban.rec.service.ShopInfoService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.CommonEnum.DeleteStatus;
import com.yiban.rec.util.CommonEnum.IsActive;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.JsonChangeVo;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.util.WebServiceClientUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("reconciliationService")
public class ReconciliationServiceImpl extends BaseOprService implements ReconciliationService {

	@Autowired
	private ReconcilitationDao reconciliationDao;

	@Autowired
	private PlatformFlowDao platformFlowDao;
	@Autowired
	private RecCashDao recCashDao;

	@Autowired
	private HisPayResultDao hisPayResultDao;

	@Autowired
	private PlatformFlowLogDao platformFlowLogDao;

	@Autowired
	private ThirdBillDao thirdBillDao;

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;

	@Autowired
	private ShopInfoService shopInfoService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;

	@Autowired
	private ExcepHandingRecordDao excepHandingRecordDao;

	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;

	@Autowired
	private RefundService refundService;

	@Autowired
	private OrderUploadDao orderUploadDao;

	@Autowired
	private HisTransactionFlowDao hisTransactionFlowDao;

	@Autowired
	private PropertiesConfigService propertiesConfigService;

	@Override
	public Page<Reconciliation> getRecpage(RecQueryVo rqvo, Pageable pageable) {
		Specification<Reconciliation> specification = new Specification<Reconciliation>() {
			@Override
			public Predicate toPredicate(Root<Reconciliation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_cash(rqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return payDateToStr(specification, pageable);
	}

	public List<Reconciliation> getRecpageNopage(RecQueryVo rqvo, Sort sort) {
		Specification<Reconciliation> specification = new Specification<Reconciliation>() {
			@Override
			public Predicate toPredicate(Root<Reconciliation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_cash(rqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return reconciliationDao.findAll(specification, sort);
	}

	public Page<TradeCheckFollow> getTwoRecpage(RecQueryVo rqvo, Pageable pageable) {
		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_twocash(rqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tradeCheckFollowDao.findAll(specification, pageable);
	}

	private Page<Reconciliation> payDateToStr(Specification<Reconciliation> specification, Pageable pageable) {
		Page<Reconciliation> page = reconciliationDao.findAll(specification, pageable);
		Map<Integer, String> map = CommonEnum.BillBalance.asMap();
		List<Reconciliation> recList = page.getContent();
		if (!StringUtil.isNullOrEmpty(recList)) {
			for (Reconciliation rec : recList) {
				if (null != rec.getPayDateStam()) {
					rec.setPayDateStamName(DateUtil.getNormalTime(rec.getPayDateStam() * 1000));
				}
				rec.setIsDifferentValue(map.get(rec.getIsDifferent()));
			}
		}
		return page;
	}

	private List<Predicate> converSearch_twocash(RecQueryVo rqvo, Root<?> root, CriteriaQuery<?> query,
												 CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		if (!StringUtils.isEmpty(rqvo.getOrgNo())) {
			// 获取所有的机构编码
			List<Organization> orgList = organizationService.findByParentCode(rqvo.getOrgNo());
			In<String> in = cb.in(root.get("orgNo"));
			in.value(rqvo.getOrgNo());
			if (orgList != null && orgList.size() > 0) {
				for (Organization org : orgList) {
					if (org != null) {
						in.value(org.getCode());
					}
				}
			}
			predicates.add(in);
		}
		if (!StringUtils.isEmpty(rqvo.getPayType())) {
			In<String> payTypeIn = cb.in(root.get("payName"));
			String[] payTypes = rqvo.getPayType().split(",");
			for (String payType : payTypes) {
				payTypeIn.value(payType);
			}
			predicates.add(payTypeIn);
		}
		if (!StringUtils.isEmpty(rqvo.getFlowNo())) {
			Path<String> paySystemNoExp = root.get("businessNo");
			predicates.add(cb.equal(paySystemNoExp, rqvo.getFlowNo()));
		}
		if (!StringUtils.isEmpty(rqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeTime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, rqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(rqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeTime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
					DateFormatUtils.format(rqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		return predicates;
	}

	protected List<Predicate> converSearch_cash(RecQueryVo rqvo, Root<?> root, CriteriaQuery<?> query,
												CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		if (!StringUtils.isEmpty(rqvo.getOrgNo())) {
			// 获取所有的机构编码
			List<Organization> orgList = organizationService.findByParentCode(rqvo.getOrgNo());
			In<String> in = cb.in(root.get("orgNo"));
			in.value(rqvo.getOrgNo());
			if (orgList != null && orgList.size() > 0) {
				for (Organization org : orgList) {
					if (org != null) {
						in.value(org.getCode());
					}
				}
			}
			predicates.add(in);
		}
		if (!StringUtils.isEmpty(rqvo.getDeviceNo())) {
			Path<String> deviceNoExp = root.get("deviceNo");
			predicates.add(cb.equal(deviceNoExp, rqvo.getDeviceNo()));
		}
		if (!StringUtils.isEmpty(rqvo.getPayType())) {
			In<String> payTypeIn = cb.in(root.get("payType"));
			String[] payTypes = rqvo.getPayType().split(",");
			for (String payType : payTypes) {
				payTypeIn.value(payType);
			}
			predicates.add(payTypeIn);
		}
		if (!StringUtils.isEmpty(rqvo.getTradeType())) {
			Path<String> tradeType = root.get("tradeCode");
			predicates.add(cb.equal(tradeType, rqvo.getTradeType()));
		}
		if (!StringUtils.isEmpty(rqvo.getSysSerial())) {
			Path<Integer> businessFlowNo = root.get("businessFlowNo");
			predicates.add(cb.equal(businessFlowNo, rqvo.getSysSerial()));
		}
		if (!StringUtils.isEmpty(rqvo.getBusinessType())) {
			Path<Integer> payBusinessTypeExp = root.get("payBusinessType");
			predicates.add(cb.equal(payBusinessTypeExp, rqvo.getBusinessType()));
		}
		if (!StringUtils.isEmpty(rqvo.getFlowNo())) {
			Path<String> paySystemNoExp = root.get("flowNo");
			predicates.add(cb.equal(paySystemNoExp, rqvo.getFlowNo()));
		}
		if (!StringUtils.isEmpty(rqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("reconciliationDate");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, rqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(rqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("reconciliationDate");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
					DateFormatUtils.format(rqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		Path<Integer> isDifferent = root.get("isDifferent");
		predicates.add(cb.greaterThan(isDifferent, CommonEnum.BillBalance.zp.getValue()));
		return predicates;
	}

	@Override
	public Page<Platformflow> getPlatPage(TradeDetailQueryVo cqvo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<Platformflow> specification = new Specification<Platformflow>() {
			@Override
			public Predicate toPredicate(Root<Platformflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchTradeDetail(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return platformFlowDao.findAll(specification, pageable);
	}

	public List<Map<String, Object>> getThridAllNoPage(CashQueryVo cqvo, List<Organization> orgListTemp,
													   Pageable pageable) {
		String sql = sqlCount(cqvo, orgListTemp, pageable);
//		List<ThirdBillVo> list = handleNativeSql(sql, ThirdBillVo.class);
		List<Map<String, Object>> data = handleNativeSql(sql, new String[] { "id","payAccount", "payFlowNo", "orgNo", "orderState",
				"payType", "payAmount", "tradeDatatime", "billSource", "businessFlowNo" });

		List<Map<String, Object>> listVo = changeThirdMoIdToName(data, cqvo, orgListTemp);
		return listVo;
	}

	public Page<Map<String, Object>> getThridAllPage(CashQueryVo cqvo, List<Organization> orgListTemp,
													 Pageable pageable) {
		String sql = sqlCount(cqvo, orgListTemp, pageable);
		return handleNativeSql(sql, new PageRequest(pageable.getPageNumber(), pageable.getPageSize()),
				new String[] { "id","payAccount",  "payFlowNo", "orgNo", "orderState", "payType", "payAmount", "tradeDatatime",
						"billSource", "businessFlowNo" });
	}

	private String sqlCount(CashQueryVo cqvo, List<Organization> orgListTemp, Pageable pageable) {
		// 机构
		String orgNos = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getOrgNo())) {
			if (orgListTemp != null && orgListTemp.size() > 0) {
				orgNos = " and t.org_no in ('" + cqvo.getOrgNo() + "'";
				for (Organization v : orgListTemp) {
					orgNos = orgNos + ",'" + v.getCode() + "'";
				}
				orgNos = orgNos + ")";
			} else {
				orgNos = " and t.org_no='" + cqvo.getOrgNo() + "'";
			}
		}
		// 流水号
		String businessFlowNo = "";
		String shopFlowNo = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getBusinessFlowNo())) {
			businessFlowNo = " and t.Business_Flow_No='" + cqvo.getBusinessFlowNo() + "' ";
			shopFlowNo = " and t.shop_flow_no='" + cqvo.getBusinessFlowNo() + "' ";
		}
		// 流水号
		String payFlowNo = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getSysSerial())) {
			payFlowNo = " and ( t.Pay_Flow_No='" + cqvo.getSysSerial() + "' or t.shop_flow_no='" + cqvo.getSysSerial()
					+ "' or t.order_no='" + cqvo.getSysSerial() + "' " + " or t.out_trade_no='" + cqvo.getSysSerial()
					+ "')";
		}
		// 支付类型
		String payType = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getPayType())) {
			payType = " and t.rec_pay_type='" + cqvo.getPayType() + "'";
		}
		// 账单来源
		String billSource = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getBillSource())) {
			billSource = " and t.bill_source='" + cqvo.getBillSource() + "'";
		}
		// 订单状态
		String orderState = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getOrderState())) {
			orderState = " and t.Order_State='" + cqvo.getOrderState() + "'";
		}
		// 支付账号
		String payAccount = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getPayAccount())) {
			payAccount = " and t.Pay_Account like '%" + cqvo.getPayAccount() + "%' ";
		}
		// 时间
		String startTime = "";
		String endTime = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getStartTime())) {
			startTime = " and t.Trade_datatime>='" + cqvo.getStartTime() + " 00:00:00'";
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getStartTime())) {
			endTime = " and t.Trade_datatime<='" + cqvo.getEndTime() + " 23:59:59'";
		}

		String billSql = "select a.id,a.payAccount,";
		if (pageable == null) {
			billSql += "a.Pay_Flow_No, a.org_no, a.Order_State, a.pay_type, a.Pay_Amount, a.Trade_datatime,a.bill_source,a.shop_flow_no as businessFlowNo ";
		} else {
			billSql += "a.Pay_Flow_No as payFlowNo, a.org_no as orgNo, a.Order_State as orderState, a.pay_type as payType, a.Pay_Amount as payAmount, DATE_FORMAT(a.Trade_datatime,'%Y-%m-%d %T') as tradeDatatime,a.bill_source as billSource,a.shop_flow_no as businessFlowNo ";
		}
		billSql = billSql
				+ " from ((SELECT CONCAT(t.id,'00') id,t.Pay_Account payAccount,t.Pay_Flow_No, t.org_no, t.Order_State, t.pay_type, t.Pay_Amount, t.Trade_datatime,t.bill_source,t.shop_flow_no FROM t_thrid_bill t"
				+ " where 1=1 " + orgNos + shopFlowNo + payFlowNo + payType + billSource + startTime + endTime + payAccount
				+ orderState + ")";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getSysSerial())) {
			payFlowNo = " and t.Pay_Flow_No='" + cqvo.getSysSerial() + "'";
		}
		String yibaoSql = " UNION ALL (SELECT CONCAT(t.id,'11') id,'' payAccount,t.Pay_Flow_No, t.org_no, t.Order_State,IFNULL('0449', '0449')  pay_type, t.cost_account  Pay_Amount, t.trade_datatime,t.bill_source,t.shop_flow_no FROM t_healthcare_official t"
				+ " where 1=1 " + orgNos + shopFlowNo + payFlowNo + billSource + startTime + endTime + orderState + ")";
		String cashSql = " UNION ALL (SELECT CONCAT(t.id,'22') id,'' payAccount,t.Pay_Flow_No, t.org_no, t.Order_State, t.pay_type, t.Pay_Amount  payAmount, t.trade_datatime,t.bill_source,t.Business_Flow_No as shop_flow_no FROM t_rec_cash t"
				+ " where 1=1 " + orgNos + businessFlowNo + payFlowNo + billSource + startTime + endTime + orderState
				+ ")";
		String endSql = ") a";
		String orderSql = concatOrderBySql(pageable);
		String sql = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getPayType())) {
			if (EnumTypeOfInt.CASH_PAYTYPE.getValue().equals(cqvo.getPayType())) {// 现金
				sql = billSql + cashSql + endSql;
			} else if (EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue().equals(cqvo.getPayType())) {// 医保
				sql = billSql + yibaoSql + endSql;
			} else {
				sql = billSql + endSql;
			}
		} else {
			sql = billSql + yibaoSql + cashSql + endSql;
		}
		if (pageable != null) {
			sql += " order by " + orderSql;
		}
		logger.info("支付渠道交易明细查询 SQL = " + sql);
		return sql;
	}

	private String concatOrderBySql(Pageable pageable) {
		if (pageable == null) {
			return "";
		}
		Sort sort = pageable.getSort();
		if (sort == null) {
			return " Trade_datatime DESC ";
		}
		String orderSql = "";
		Iterator<Order> iterator = sort.iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			orderSql += order.getProperty() + " " + order.getDirection() + ",";
		}
		if (StringUtil.isEmpty(orderSql)) {
			orderSql = " Trade_datatime DESC ";
		} else {
			orderSql = orderSql.substring(0, orderSql.lastIndexOf(","));
		}
		return orderSql;
	}

	@Override
	public Page<HisPayResult> getHisPage(CashQueryVo cqvo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<HisPayResult> specification = new Specification<HisPayResult>() {
			@Override
			public Predicate toPredicate(Root<HisPayResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return hisPayResultDao.findAll(specification, pageable);
	}

	@Override
	public Page<ThirdBill> getThridPage(CashQueryVo cqvo, List<Organization> orgListTemp, Pageable pageable) {
		Specification<ThirdBill> specification = new Specification<ThirdBill>() {
			@Override
			public Predicate toPredicate(Root<ThirdBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_thrid(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return thirdBillDao.findAll(specification, pageable);
	}

	private List<Predicate> converSearch_thrid(CashQueryVo cqvo, List<Organization> orgListTemp, Root<?> root,
											   CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtils.isEmpty(cqvo.getOrgNo())) {
			In<String> orgCodeIn = cb.in(root.get("orgNo"));
			orgCodeIn.value(cqvo.getOrgNo());
			if (null != orgListTemp) {
				for (Organization org : orgListTemp) {
					orgCodeIn.value(org.getCode());
				}
			}
			predicates.add(orgCodeIn);
		}
		if (!StringUtils.isEmpty(cqvo.getPayType())) {
			Path<String> payTypeArrExp = root.get("payType");
			predicates.add(cb.equal(payTypeArrExp, cqvo.getPayType()));
		}
		if (!StringUtils.isEmpty(cqvo.getSysSerial())) {
			Path<String> paySystemNoExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getSysSerial()));
		}
		if (!StringUtils.isEmpty(cqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, cqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(cqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
					DateFormatUtils.format(cqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		return predicates;
	}

	protected List<Predicate> converSearchCash(CashQueryVo cqvo, List<Organization> orgListTemp, Root<?> root,
											   CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		if (!StringUtils.isEmpty(cqvo.getOrgNo())) {
			Path<String> orgCodeExp = root.get("orgNo");
			predicates.add(cb.equal(orgCodeExp, cqvo.getOrgNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getTradeCode())) {
			if (cqvo.getTradeCode().indexOf(",") > -1) {
				String[] tradeTypeArr = cqvo.getTradeCode().split(",");
				In<String> in = cb.in(root.get("tradeCode").as(String.class));
				for (int i = 0; i < tradeTypeArr.length; i++) {
					in.value(tradeTypeArr[i]);
				}
				predicates.add(in);
			} else {
				Path<String> tradeCodeExp = root.get("tradeCode");
				predicates.add(cb.equal(tradeCodeExp, cqvo.getTradeCode()));
			}
		}
		if (!StringUtils.isEmpty(cqvo.getPayType())) {
			Path<String> payTypeArrExp = root.get("payType");
			predicates.add(cb.equal(payTypeArrExp, cqvo.getPayType()));
		}
//		}else{
//			Path<String> payTypeArrExp = root.get("payType");
//			predicates.add(cb.notEqual(payTypeArrExp, EnumType.PAY_CODE_CASH.getValue()));
//		}
		if (!StringUtils.isEmpty(cqvo.getPaySource())) {
			Path<Integer> paySourceExp = root.get("paySource");
			predicates.add(cb.equal(paySourceExp, cqvo.getPaySource()));
		}
		if (!StringUtils.isEmpty(cqvo.getSysSerial())) {
			Path<String> paySystemNoExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getSysSerial()));
		}
		if (!StringUtils.isEmpty(cqvo.getPayTermNo())) {
			Path<String> payTermNoExp = root.get("payTermNo");
			predicates.add(cb.equal(payTermNoExp, cqvo.getPayTermNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getCashier())) {
			Path<String> payTermNoExp = root.get("cashier");
			predicates.add(cb.equal(payTermNoExp, cqvo.getCashier()));
		}
		if (!StringUtils.isEmpty(cqvo.getBusinessType())) {
			Path<Integer> payBusinessTypeExp = root.get("payBusinessType");
			predicates.add(cb.equal(payBusinessTypeExp, cqvo.getBusinessType()));
		}
		if (!StringUtils.isEmpty(cqvo.getStartDate())) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, cqvo.getStartDate()));
		}
		if (!StringUtils.isEmpty(cqvo.getEndDate())) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
					DateFormatUtils.format(cqvo.getEndDate(), "yyyy-MM-dd 23:59:59"))));
		}
		return predicates;
	}

	@Override
	public ResponseResult edit(Long id, BigDecimal platformAmount, String remarkInfo, Integer handleCode,
							   String loginName) throws BusinessException {
		Reconciliation rec = reconciliationDao.findOne(id);
		if (null == rec) {
			return ResponseResult.failure("记录不存在或已被删除");
		} else {
			rec.setPlatformAmount(platformAmount);
			rec.setThirdAmount(platformAmount);
			rec.setOrgAmount(platformAmount);
			rec.setRemarkInfo(remarkInfo);
			rec.setHandleCode(handleCode);
			rec.setOperationUserName(loginName);
			rec.setIsDifferent(0);
			reconciliationDao.save(rec);
			return ResponseResult.success();
		}
	}

	@Override
	public ResponseResult updateSingle(Long id, User user) {
		Reconciliation rec = reconciliationDao.findOne(id);
		if (null == rec) {
			return ResponseResult.failure("记录不存在或已被删除");
		} else {
			rec.setLastModifiedById(user.getId());
			rec.setIsDifferent(EnumTypeOfInt.REC_SINGLE_NO.getId());
			reconciliationDao.save(rec);
			return ResponseResult.success();
		}
	}

	@Override
	public Map<String, Object> getPlatformflowLog(String flowNo) {
		List<HisPayResult> hispayResult = hisPayResultDao.findByFlowNo(flowNo);
		Map<String, Object> map = new HashMap<String, Object>();
		HisPayResult hisPayInfo = null;
		JSONArray jsonArray = null;
		if (!StringUtil.isNullOrEmpty(hispayResult)) {
			for (int i = 0; i < hispayResult.size(); i++) {
				if (hispayResult.get(i).getIsActived() == 1) {
					hisPayInfo = hispayResult.get(i);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(hisPayInfo) && !StringUtil.isNullOrEmpty(hisPayInfo.getGoodInfoList())) {
			jsonArray = JSONArray.fromObject(hisPayInfo.getGoodInfoList());
		}
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		List<PlatformrawLog> platLogList = platformFlowLogDao.findByFlowNo(flowNo);
		if (!StringUtil.isNullOrEmpty(platLogList)) {
			for (PlatformrawLog platformrawLog : platLogList) {
				platformrawLog.setResponseValue(
						StringUtil.isNullOrEmpty(metaMap.get(platformrawLog.getResponseCode())) == true ? "无"
								: metaMap.get(platformrawLog.getResponseCode()));
				platformrawLog.setTradeFromName(
						StringUtil.isNullOrEmpty(metaMap.get(platformrawLog.getTradeFrom())) == true ? "无"
								: metaMap.get(platformrawLog.getTradeTo()));
				platformrawLog
						.setTradeToName(StringUtil.isNullOrEmpty(metaMap.get(platformrawLog.getTradeTo())) == true ? "无"
								: metaMap.get(platformrawLog.getTradeTo()));
			}
		}
		map.put("goodInfo", jsonArray);
		map.put("platLogList", platLogList);
		return map;
	}

	@Override
	public List<Reconciliation> getRecDetailList(RecQueryVo rqvo, Pageable page) {
		Specification<Reconciliation> specification = new Specification<Reconciliation>() {
			@Override
			public Predicate toPredicate(Root<Reconciliation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_cash(rqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return changeDetailMoIdToName(specification, page);
	}

	public List<TradeCheckFollow> getRecTwoDetailList(RecQueryVo rqvo, Pageable page) {
		Specification<TradeCheckFollow> specification = new Specification<TradeCheckFollow>() {
			@Override
			public Predicate toPredicate(Root<TradeCheckFollow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch_twocash(rqvo, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return changeTwoDetailMoIdToName(specification, page);
	}

	private List<Reconciliation> changeDetailMoIdToName(Specification<Reconciliation> specification, Pageable page) {
		Page<Reconciliation> recPage = reconciliationDao.findAll(specification, page);
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<Reconciliation> recList = new ArrayList<Reconciliation>();
		Map<Integer, String> map = CommonEnum.BillBalance.asMap();
		if (!StringUtil.isNullOrEmpty(recPage)) {
			List<Reconciliation> recPageList = recPage.getContent();
			BigDecimal orgAmount = new BigDecimal(0);
			BigDecimal thirdAmount = new BigDecimal(0);
			BigDecimal platAmount = new BigDecimal(0);
			for (Reconciliation rec : recPageList) {
				rec.setOrgName(String.valueOf(orgMap.get(String.valueOf(rec.getOrgNo()))));
				rec.setPayDateStamName(DateUtil.getNormalTime(rec.getPayDateStam() * 1000));
				rec.setIsDifferentValue(map.get(rec.getIsDifferent()));
				rec.setTradeCodeName(metaMap.get(String.valueOf(rec.getTradeCode())));
				rec.setPayBusinessTypeName(metaMap.get(String.valueOf(rec.getPayBusinessType())));
				rec.setPayTypeName(metaMap.get(String.valueOf(rec.getPayType())));
				rec.setCustIdentifyName(metaMap.get(String.valueOf(rec.getCustIdentify())));
				rec.setOrderStateName(metaMap.get(String.valueOf(rec.getOrderState())));
				orgAmount = orgAmount.add(rec.getOrgAmount());
				thirdAmount = thirdAmount.add(rec.getThirdAmount());
				platAmount = platAmount.add(rec.getPlatformAmount());
				recList.add(rec);
			}
			Reconciliation reconciliation = new Reconciliation();
			reconciliation.setOrgAmount(orgAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			reconciliation.setThirdAmount(thirdAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			reconciliation.setPlatformAmount(platAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			reconciliation.setOrgName("合计:");
			recList.add(reconciliation);
		}
		return recList;
	}

	private List<TradeCheckFollow> changeTwoDetailMoIdToName(Specification<TradeCheckFollow> specification,
															 Pageable page) {
		Page<TradeCheckFollow> recPage = tradeCheckFollowDao.findAll(specification, page);
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<TradeCheckFollow> recList = new ArrayList<TradeCheckFollow>();
		if (!StringUtil.isNullOrEmpty(recPage)) {
			List<TradeCheckFollow> recPageList = recPage.getContent();
			BigDecimal thirdAmount = new BigDecimal(0);
			for (TradeCheckFollow rec : recPageList) {
				rec.setOrgNo(String.valueOf(orgMap.get(String.valueOf(rec.getOrgNo()))));
				rec.setPayName(metaMap.get(String.valueOf(rec.getPayName())));
				rec.setTradeName(metaMap.get(String.valueOf(rec.getTradeName())));
				rec.setBillSource(metaMap.get(String.valueOf(rec.getBillSource())));
				rec.setPatType(metaMap.get(String.valueOf(rec.getPatType())));
				thirdAmount = thirdAmount.add(rec.getTradeAmount());
				recList.add(rec);
			}
			TradeCheckFollow tradeCheckFollow = new TradeCheckFollow();
			tradeCheckFollow.setOrgNo("合计");
			tradeCheckFollow.setTradeAmount(thirdAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			recList.add(tradeCheckFollow);
		}
		return recList;
	}

	@Override
	public List<Platformflow> getRecCashPlatList(CashQueryVo cqvo, List<Organization> orgListTemp, Pageable page) {
		Specification<Platformflow> specification = new Specification<Platformflow>() {
			@Override
			public Predicate toPredicate(Root<Platformflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return changePlatMoIdToName(specification, page);
	}

	private List<Platformflow> changePlatMoIdToName(Specification<Platformflow> specification, Pageable page) {
		List<Platformflow> recList = platformFlowDao.findAll(specification, page).getContent();
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<Platformflow> pffList = new ArrayList<Platformflow>();
		if (!StringUtil.isNullOrEmpty(recList)) {
			Platformflow plat = new Platformflow();
			BigDecimal platAmount = new BigDecimal(0);
			for (Platformflow rec : recList) {
				rec.setOrgName(String.valueOf(orgMap.get(String.valueOf(rec.getOrgNo()))));
				rec.setTradeCodeName(metaMap.get(String.valueOf(rec.getTradeCode())));
				rec.setPayBusinessTypeName(metaMap.get(String.valueOf(rec.getPayBusinessType())));
				rec.setPayTypeName(metaMap.get(String.valueOf(rec.getPayType())));
				rec.setCustIdentifyTypeName(metaMap.get(String.valueOf(rec.getCustIdentifyType())));
				rec.setPaySourceName(metaMap.get(String.valueOf(rec.getPaySource())));
				rec.setTradeToName(metaMap.get(String.valueOf(rec.getTradeTo())));
				rec.setOrderStateName(metaMap.get(String.valueOf(rec.getOrderState())));
				platAmount.add(rec.getPayAmount() == null ? new BigDecimal(0.00) : rec.getPayAmount());
				pffList.add(rec);
			}
			plat.setOrgName("合计:");
			plat.setPayAmount(platAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			pffList.add(plat);
		}
		return pffList;
	}

	@Override
	public List<RecCash> getRecCashHisList(CashQueryVo cqvo, List<Organization> orgListTemp, Pageable page) {
		Specification<RecCash> specification = new Specification<RecCash>() {
			@Override
			public Predicate toPredicate(Root<RecCash> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return changeHisMoIdToName(specification, page);
	}

	public List<HisPayResult> getRecCashHisList(TradeDetailQueryVo cqvo, List<Organization> orgListTemp,
												Pageable page) {
		Specification<HisPayResult> specification = new Specification<HisPayResult>() {
			@Override
			public Predicate toPredicate(Root<HisPayResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchTradeDetail(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return changeHisMoIdToNameTrade(specification, page);
	}

	private List<RecCash> changeHisMoIdToName(Specification<RecCash> specification, Pageable page) {
		Page<RecCash> recPage = recCashDao.findAll(specification, page);
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<RecCash> hisPayResultList = new ArrayList<RecCash>();
		if (!StringUtil.isNullOrEmpty(recPage)) {
			RecCash hisResult = new RecCash();
			BigDecimal orgAmount = new BigDecimal(0);
			List<RecCash> recPageList = recPage.getContent();
			for (RecCash rec : recPageList) {
				rec.setOrgName(String.valueOf(orgMap.get(String.valueOf(rec.getOrgNo()))));
				rec.setTradeCodeName(metaMap.get(String.valueOf(rec.getTradeCode())));
				rec.setPayBusinessTypeName(metaMap.get(String.valueOf(rec.getPayBusinessType())));
				rec.setPayTypeName(metaMap.get(String.valueOf(rec.getPayType())));
				rec.setCustIdentifyTypeName(metaMap.get(String.valueOf(rec.getCustIdentifyType())));
				rec.setPaySourceName(metaMap.get(String.valueOf(rec.getPaySource())));
				rec.setOrderStateName(metaMap.get(String.valueOf(rec.getOrderState())));
				orgAmount = orgAmount.add(rec.getPayAmount());
				hisPayResultList.add(rec);
			}
			hisResult.setOrgName("合计:");
			hisResult.setPayAmount(orgAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisPayResultList.add(hisResult);
		}
		return hisPayResultList;
	}

	private List<HisPayResult> changeHisMoIdToNameTrade(Specification<HisPayResult> specification, Pageable page) {
		Page<HisPayResult> recPage = hisPayResultDao.findAll(specification, page);
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<HisPayResult> hisPayResultList = new ArrayList<HisPayResult>();
		if (!StringUtil.isNullOrEmpty(recPage)) {
			HisPayResult hisResult = new HisPayResult();
			BigDecimal orgAmount = new BigDecimal(0);
			List<HisPayResult> recPageList = recPage.getContent();
			for (HisPayResult rec : recPageList) {
				rec.setOrgName(String.valueOf(orgMap.get(String.valueOf(rec.getOrgNo()))));
				rec.setTradeCodeName(metaMap.get(String.valueOf(rec.getTradeCode())));
				rec.setPayBusinessTypeName(metaMap.get(String.valueOf(rec.getPayBusinessType())));
				rec.setPayTypeName(metaMap.get(String.valueOf(rec.getPayType())));
				rec.setCustIdentifyTypeName(metaMap.get(String.valueOf(rec.getCustIdentifyType())));
				rec.setPaySourceName(metaMap.get(String.valueOf(rec.getPaySource())));
				rec.setOrderStateName(metaMap.get(String.valueOf(rec.getOrderState())));
				if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(rec.getOrderState())) {// 缴费
					orgAmount = orgAmount.add(rec.getPayAmount().abs().negate());
				} else {
					orgAmount = orgAmount.add(rec.getPayAmount());
				}
				hisPayResultList.add(rec);
			}
			hisResult.setOrgName("合计:");
			hisResult.setPayAmount(orgAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
			hisPayResultList.add(hisResult);
		}
		return hisPayResultList;
	}

	private List<Map<String, Object>> changeThirdMoIdToName(List<Map<String, Object>> recList, CashQueryVo cqvo,
															List<Organization> orgListTemp) {
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<Map<String, Object>> thList = new ArrayList<Map<String, Object>>();
		if (!StringUtil.isNullOrEmpty(recList)) {
//			ThirdBillVo thirdBill = new ThirdBillVo();
			BigDecimal thirdAmount = new BigDecimal(0);
			for (Map<String, Object> rec : recList) {
				rec.put("orgName", String.valueOf(orgMap.get(String.valueOf(rec.get("orgNo")))));
				rec.put("payTypeName", metaMap.get(String.valueOf(rec.get("payType"))));
				rec.put("orderStateName", metaMap.get(rec.get("orderState")));
				rec.put("billSourceName", metaMap.get(rec.get("billSource")));

				if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(rec.get("orderState"))) {// 缴费
					thirdAmount = thirdAmount.add(new BigDecimal(rec.get("payAmount").toString()).abs().negate());
				} else {
					thirdAmount = thirdAmount.add(new BigDecimal(rec.get("payAmount").toString()));
				}
				thList.add(rec);
			}
			/*
			 * thirdBill.setOrgName("总金额:");
			 * thirdBill.setPayTypeName(thirdAmount.setScale(2,
			 * BigDecimal.ROUND_HALF_UP).toString()); thirdBill.setPayFlowNo("总笔数:");
			 * thirdBill.setOrderStateName(String.valueOf(recList.size()));
			 * thList.add(thirdBill); String startTime = null; String endTime = null; if
			 * (cqvo.getStartDate() != null) startTime =
			 * DateFormatUtils.format(cqvo.getStartDate(), "yyyy-MM-dd"); if
			 * (cqvo.getEndDate() != null) endTime =
			 * DateFormatUtils.format(cqvo.getEndDate(), "yyyy-MM-dd 23:59:59"); Map<String,
			 * Object> map = thirdTradeService.getTradeCollect(cqvo.getOrgNo(), startTime,
			 * endTime, cqvo.getPayType(), cqvo.getSysSerial(), orgListTemp);
			 *
			 * ThirdBillVo thirdBillTwo = new ThirdBillVo();
			 * thirdBillTwo.setOrgName("微信总金额:");
			 * thirdBillTwo.setPayTypeName(String.valueOf(map.get("wechatAllAmount")));
			 * thirdBillTwo.setPayFlowNo("微信总笔数:");
			 * thirdBillTwo.setOrderStateName(String.valueOf(map.get("wechatAllNum")));
			 * thList.add(thirdBillTwo);
			 *
			 * ThirdBillVo thirdBillThree = new ThirdBillVo();
			 * thirdBillThree.setOrgName("支付宝总金额:");
			 * thirdBillThree.setPayTypeName(String.valueOf(map.get("aliAllAmount")));
			 * thirdBillThree.setPayFlowNo("支付宝总笔数:");
			 * thirdBillThree.setOrderStateName(String.valueOf(map.get("aliAllNum")));
			 * thList.add(thirdBillThree);
			 *
			 * ThirdBillVo thirdBillFour = new ThirdBillVo();
			 * thirdBillFour.setOrgName("银行总金额:");
			 * thirdBillFour.setPayTypeName(String.valueOf(((BigDecimal)
			 * map.get("jdBankAllAmount")).add((BigDecimal)
			 * map.get("thridBankAllAmount")))); thirdBillFour.setPayFlowNo("银行总笔数:");
			 * thirdBillFour.setOrderStateName(String.valueOf(((BigInteger)map.get(
			 * "jdBankAllNum")).add((BigInteger)map.get("thridBankAllNum"))));
			 * thList.add(thirdBillFour);
			 *
			 * ThirdBillVo thirdBill5 = new ThirdBillVo(); thirdBill5.setOrgName("现金总金额:");
			 * thirdBill5.setPayTypeName(String.valueOf(map.get("cashAllAmount")));
			 * thirdBill5.setPayFlowNo("现金总笔数:");
			 * thirdBill5.setOrderStateName(String.valueOf(map.get("cashAllNum")));
			 * thList.add(thirdBill5);
			 *
			 * ThirdBillVo thirdBill6 = new ThirdBillVo();
			 * thirdBill6.setOrgName("医保账户支付总金额:");
			 * thirdBill6.setPayTypeName(String.valueOf(map.get("yibaoAllAmount")));
			 * thirdBill6.setPayFlowNo("医保账户支付总笔数:");
			 * thirdBill6.setOrderStateName(String.valueOf(map.get("yibaoAllNum")));
			 * thList.add(thirdBill6);
			 */
		}
		return thList;
	}

	@Override
	public List<Platformflow> getPlatformflow() {
		return platformFlowDao.findAll();
	}

	@Override
	public Page<Platformflow> getPagePlatformflow(Pageable pageable) {
		return platformFlowDao.findAll(pageable);
	}

	@Override
	public RecCash findByPayFlowNo(String payFlowNo) {
		return recCashDao.findByPayFlowNo(payFlowNo);
	}

	@Override
	public Page<RecCash> getTradeDetailPage(TradeDetailQueryVo cqvo, List<Organization> orgListTemp,
											Pageable pageable) {
		Specification<RecCash> specification = new Specification<RecCash>() {
			@Override
			public Predicate toPredicate(Root<RecCash> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchTradeDetail(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return spRecCash(recCashDao.findAll(specification, pageable));
	}

	@Override
	public Page<HisPayResult> getTradeDetailPageHis(TradeDetailQueryVo cqvo, List<Organization> orgListTemp,
													Pageable pageable) {
		Specification<HisPayResult> specification = new Specification<HisPayResult>() {
			@Override
			public Predicate toPredicate(Root<HisPayResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchTradeDetail(cqvo, orgListTemp, root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return hisPayResultDao.findAll(specification, pageable);
	}

	public List<Map<String, Object>> getTradeDetailCollect(TradeDetailVo tdv, List<Organization> orgList) {
		StringBuffer sql = new StringBuffer(
				"select tmp.pay_type payType,if(tmp.pay_type='0449',SUM(IF(tmp.refund_order_state='1809304' or tmp.order_state = '1809304',-tmp.yb_pay_amount,tmp.yb_pay_amount)),"
						+ "SUM(IF(tmp.refund_order_state='1809304' or tmp.order_state = '1809304',-tmp.pay_amount,tmp.pay_amount))) amount,SUM(IF(tmp.order_state != '1809304',1,0)) payNum,"
						+ " SUM(IF(tmp.refund_order_state = '1809304' or tmp.order_state = '1809304',1,0)) reFundNum from t_order_upload tmp where 1=1  ");
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getOrgCode())) {
			String strOrg = " and tmp.org_code in(\'" + tdv.getOrgCode() + "\'";
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}
			strOrg = strOrg + ")";
			sql.append(strOrg);
			// HIS系统订单号（HIS流水号）
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getHisOrderNO())) {
				sql.append(" and (tmp.his_order_no='" + tdv.getHisOrderNO() + "'");
			}
			// 业务系统订单号（支付方流水号）
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPaySystemNo())) {
				sql.append(" or tmp.tsn_order_no='" + tdv.getPaySystemNo() + "')");
			}
			// 业务类型
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPayBussinessType())) {
				sql.append(" and tmp.pay_business_type='" + tdv.getPayBussinessType() + "'");
			}
			// 柜员号
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getDeviceNo())) {
				sql.append(" and tmp.cashier='" + tdv.getDeviceNo() + "'");
			}
			// 订单状态
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getOrderState())) {
				sql.append(" and ((order_state='" + tdv.getOrderState() + "' and refund_order_state is null )"
						+ " or refund_order_state= '" + tdv.getOrderState() + "')");
			}
			// 就诊卡号
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getVisitNumber())) {
				sql.append(" and tmp.patient_card_no='" + tdv.getVisitNumber() + "'");
			}
			// 患者名称
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getCustName())) {
				sql.append(" and tmp.patient_name='" + tdv.getCustName() + "'");
			}
			// 患者类型
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPatType())) {
				sql.append(" and tmp.pat_type='" + tdv.getPatType() + "'");
			}
			// 支付类型
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPayType())) {
				sql.append(" and tmp.pay_type='" + tdv.getPayType() + "'");
			}
			// 系统来源
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getSystemFrom())) {
				sql.append(" and tmp.bill_source='" + tdv.getSystemFrom() + "'");
			}
			// 时间
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getStartDate())) {
				sql.append(" and tmp.trade_date_time>='" + tdv.getStartDate().trim() + "'");
			}
			if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getEndDate())) {
				sql.append(" and tmp.trade_date_time<='" + tdv.getEndDate().trim() + "'");
			}
			sql.append(" GROUP BY tmp.pay_type");
		}
		List<Map<String, Object>> list = handleNativeSql(sql.toString(),
				new String[] { "payType", "amount", "payNum", "refundNum" });
		/*
		 * list.get(0).put("allAmount",
		 * StringUtil.isNullOrEmpty(list.get(0).get("allAmount")) ? new BigDecimal(0) :
		 * list.get(0).get("allAmount"));
		 */
		return list;
	}

	public Map<String, Object> getTradeCollect(TradeDetailQueryVo cqvo, List<Organization> orgList) {
		StringBuffer sql = new StringBuffer(
				"SELECT COUNT(1), SUM(IF(Order_State=0256,-ABS(Pay_Amount),Pay_Amount)) Pay_Amount  FROM t_rec_pay_result where 1=1 and is_deleted=0 and is_actived=1 ");
		if (org.apache.commons.lang.StringUtils.isNotBlank(cqvo.getOrgNo())) {
			String strOrg = " and org_no in(\'" + cqvo.getOrgNo() + "\'";
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}
			strOrg = strOrg + ")";
			sql.append(strOrg);

			if (!StringUtils.isEmpty(cqvo.getTradeCode())) {
				if (cqvo.getTradeCode().indexOf(",") > -1) {
					String[] tradeTypeArr = cqvo.getTradeCode().split(",");
					String tradeCode = "";
					for (int i = 0; i < tradeTypeArr.length; i++) {
						if (org.apache.commons.lang.StringUtils.isBlank(tradeCode)) {
							tradeCode = " and trade_code in (\'" + tradeTypeArr[i] + "\'";
						} else {
							tradeCode = tradeCode + ",\'" + tradeTypeArr[i] + "\'";
						}
					}
					sql.append(tradeCode);
				} else {
					String tradeCode = " and trade_code=\'" + cqvo.getTradeCode() + "\'";
					sql.append(tradeCode);
				}
			}
			if (!StringUtils.isEmpty(cqvo.getPayType())) {
				if (cqvo.getPayType().indexOf(",") > -1) {
					String[] payTypeArr = cqvo.getPayType().split(",");
					String payType = "";
					for (int i = 0; i < payTypeArr.length; i++) {
						if (org.apache.commons.lang.StringUtils.isBlank(payType)) {
							payType = " and pay_type in (\'" + payTypeArr[i] + "\'";
						} else {
							payType = payType + ",\'" + payTypeArr[i] + "\'";
						}
					}
					sql.append(payType);
				} else {
					String payType = " and pay_type=\'" + cqvo.getPayType() + "\'";
					sql.append(payType);
				}
			}
			if (!StringUtils.isEmpty(cqvo.getPaySource())) {
				String paySource = " and pay_source=\'" + cqvo.getPaySource() + "\'";
				sql.append(paySource);
			}
			if (!StringUtils.isEmpty(cqvo.getSysSerial())) {
				String payFlowNo = " and pay_flow_no=\'" + cqvo.getSysSerial() + "\'";
				sql.append(payFlowNo);
			}
			if (!StringUtils.isEmpty(cqvo.getPayTermNo())) {
				String payTermNo = " and pay_term_no=\'" + cqvo.getPayTermNo() + "\'";
				sql.append(payTermNo);
			}
			if (!StringUtils.isEmpty(cqvo.getCashier())) {
				String cashier = " and cashier=\'" + cqvo.getCashier() + "\'";
				sql.append(cashier);
			}
			if (!StringUtils.isEmpty(cqvo.getDeviceNo())) {
				String deviceNo = " and device_no=\'" + cqvo.getDeviceNo() + "\'";
				sql.append(deviceNo);
			}
			if (!StringUtils.isEmpty(cqvo.getBusinessType())) {
				String payBusinessType = " and pay_business_type=\'" + cqvo.getBusinessType() + "\'";
				sql.append(payBusinessType);
			}
			if (!StringUtils.isEmpty(cqvo.getStartDate())) {
				String tradeDatatime = " and trade_datatime>=\'" + cqvo.getStartDate() + "\'";
				sql.append(tradeDatatime);

			}
			if (!StringUtils.isEmpty(cqvo.getEndDate())) {
				String tradeDatatime = " and trade_datatime<=\'" + cqvo.getEndDate() + "\'";
				sql.append(tradeDatatime);
			}
		}
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> list = handleNativeSql(sql.toString(), new String[] { "allNum", "allAmount" });
		list.get(0).put("allAmount", StringUtil.isNullOrEmpty(list.get(0).get("allAmount")) ? new BigDecimal(0)
				: list.get(0).get("allAmount"));
		map.putAll(list.get(0));
		return map;
	}

	// 现金
	private Page<RecCash> spRecCash(Page<RecCash> page) {
		List<RecCash> list = page.getContent();
		Map<String, String> map = shopInfoService.getTerm2PayTerm();
		if (!StringUtil.isNullOrEmpty(list)) {
			for (RecCash hisPayResult : list) {
				hisPayResult.setPaySerNo(map.get(hisPayResult.getPayTermNo()));
			}
		}
		return page;
	}
	// 电子明细
	/*
	 * private Page<HisPayResult> spHisResult(Page<HisPayResult> page){
	 * List<HisPayResult> list = page.getContent(); Map<String,String> map =
	 * shopInfoService.getTerm2PayTerm(); if(!StringUtil.isNullOrEmpty(list)){
	 * for(HisPayResult hisPayResult : list){
	 * hisPayResult.setPaySerNo(map.get(hisPayResult.getPayTermNo())); } } return
	 * page; }
	 */

	protected List<Predicate> converSearchTradeDetail(TradeDetailQueryVo cqvo, List<Organization> orgListTemp,
													  Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtils.isEmpty(cqvo.getOrgNo())) {
			In<String> in = cb.in(root.get("orgNo").as(String.class));
			in.value(cqvo.getOrgNo());
			for (int i = 0; i < orgListTemp.size(); i++) {
				in.value(orgListTemp.get(i).getCode());
			}
			predicates.add(in);
		}
		if (!StringUtils.isEmpty(cqvo.getTradeCode())) {
			if (cqvo.getTradeCode().indexOf(",") > -1) {
				String[] tradeTypeArr = cqvo.getTradeCode().split(",");
				In<Integer> in = cb.in(root.get("tradeCode").as(Integer.class));
				for (int i = 0; i < tradeTypeArr.length; i++) {
					in.value(Integer.parseInt(tradeTypeArr[i]));
				}
				predicates.add(in);
			} else {
				Path<Integer> tradeCodeExp = root.get("tradeCode");
				predicates.add(cb.equal(tradeCodeExp, cqvo.getTradeCode()));
			}
		}
		if (!StringUtils.isEmpty(cqvo.getPayType())) {
			if (cqvo.getPayType().indexOf(",") > -1) {
				String[] payTypeArr = cqvo.getPayType().split(",");
				In<Integer> in = cb.in(root.get("payType").as(Integer.class));
				for (int i = 0; i < payTypeArr.length; i++) {
					in.value(Integer.parseInt(payTypeArr[i]));
				}
				predicates.add(in);
			} else {
				Path<Integer> payTypeArrExp = root.get("payType");
				predicates.add(cb.equal(payTypeArrExp, cqvo.getPayType()));
			}
		}
		if (!StringUtils.isEmpty(cqvo.getPaySource())) {
			Path<Integer> paySourceExp = root.get("paySource");
			predicates.add(cb.equal(paySourceExp, cqvo.getPaySource()));
		}
		if (!StringUtils.isEmpty(cqvo.getSysSerial())) {
			Path<String> paySystemNoExp = root.get("payFlowNo");
			predicates.add(cb.equal(paySystemNoExp, cqvo.getSysSerial()));
		}
		if (!StringUtils.isEmpty(cqvo.getPayTermNo())) {
			Path<String> payTermNoExp = root.get("payTermNo");
			predicates.add(cb.equal(payTermNoExp, cqvo.getPayTermNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getCashier())) {
			Path<String> payTermNoExp = root.get("cashier");
			predicates.add(cb.equal(payTermNoExp, cqvo.getCashier()));
		}
		if (!StringUtils.isEmpty(cqvo.getDeviceNo())) {
			Path<String> deviceNoExp = root.get("deviceNo");
			predicates.add(cb.equal(deviceNoExp, cqvo.getDeviceNo()));
		}
		if (!StringUtils.isEmpty(cqvo.getBusinessType())) {
			Path<Integer> payBusinessTypeExp = root.get("payBusinessType");
			predicates.add(cb.equal(payBusinessTypeExp, cqvo.getBusinessType()));
		}
		try {
			if (!StringUtils.isEmpty(cqvo.getStartDate())) {
				Path<Date> payDateStartExp = root.get("tradeDatatime");
				predicates.add(cb.greaterThanOrEqualTo(payDateStartExp,
						DateUtil.transferStringToDateFormat(cqvo.getStartDate())));
			}
			if (!StringUtils.isEmpty(cqvo.getEndDate())) {
				Path<Date> payDateEndExp = root.get("tradeDatatime");
				predicates.add(
						cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDateFormat(cqvo.getEndDate())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return predicates;
	}

	@Override
	public List<Reconciliation> getRecMap(String orgNo, String tradeDate, String endDate) {
		try {
			if (org.apache.commons.lang.StringUtils.isNotBlank(endDate)) {
				return reconciliationDao.findByOrgNoAndReconciliationDate(orgNo,
						DateUtil.transferStringToDate("yyyy-MM-dd", tradeDate),
						DateUtil.transferStringToDateFormat(endDate + " 23:59:59"));
			} else {
				return reconciliationDao.findByOrgNoAndReconciliationDate(orgNo,
						DateUtil.transferStringToDate("yyyy-MM-dd", tradeDate),
						DateUtil.transferStringToDateFormat(tradeDate + " 23:59:59"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new ArrayList<Reconciliation>();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String updateThirdFollowing(String type, Long id, String orderNo, String reason, String url, String orgNo,
									   String payCode, String tradeAmount, String batchRefundNo, String billSource, String imgUrl, Long userId)
			throws Exception {
		// 调金蝶退费接口用
		String endpoint = PropertyUtil.getProperty("tangdu.endpoint", "");
		String username = PropertyUtil.getProperty("tangdu.username", "");
		String password = PropertyUtil.getProperty("tangdu.password", "");
		String method = PropertyUtil.getProperty("tangdu.refund.method", "");

		// 查找出支付渠道记录
		List<ThirdBill> list = thirdBillDao.findByOrderNoAndOrderState(orderNo);
		// 微信支付宝订单号
		String tsn = null;
		if (list.size() > 0) {
			orderNo = list.get(0).getShopFlowNo();
			tsn = list.get(0).getPayFlowNo();
			if (org.apache.commons.lang.StringUtils.isBlank(orderNo)) {
				Exception exception = new Exception("微信/支付宝订单号为空");
				throw exception;
			}
		}
		// 删除支付渠道表记录
		thirdBillDao.delete(orderNo);
		if (EnumTypeOfInt.REC_TYPE_THREE.getValue().equals(type)) {// 三方对账
			Reconciliation reconciliation = reconciliationDao.findOne(id);
			// 删除异常记录
			reconciliationDao.delete(id);
			// 写入退费记录表
			refundSeting(null, reconciliation, reason, imgUrl, userId);
		} else {
			TradeCheckFollow tradeCheckFollow = tradeCheckFollowDao.findOne(id);
			// 删除异常记录
			tradeCheckFollowDao.delete(id);
			refundSeting(tradeCheckFollow, null, reason, imgUrl, userId);// 记录退费记录表
		}
		// 判断支付的厂家来调用退费接口
		if (billSource.equals(EnumTypeOfInt.BILL_SOURCE_SELF_JD.getValue())) {
			refund(url, tsn, orderNo, reason, orgNo, payCode, tradeAmount, batchRefundNo);
		}
		if (billSource.equals(EnumTypeOfInt.BILL_SOURCE_SELF_TD_JD.getValue())) {// 唐都金蝶退费
			refundForTangDuJd(reason, orgNo, endpoint, username, password, method, list.get(0));
		} else {
			// 调用其他退费接口
			refundThird(url, tsn, orderNo, reason, orgNo, payCode, tradeAmount);
		}

		return "退费成功";
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseResult electronicRecRefund(RefundRequestVo requestVo, User user) throws Exception {

		String PayFlowNo = "";
		String payType = "";
		String hisFlowNo = "";
		String cardNo = "";
		// 线上支付- 支付类型
		String unionPayType = "";
		// 线上支付-聚合支付标识
		String unionPayCode = "";
		// 线上支付-系统编码
		String unionSystemCode = "";
		String outTradeNo="";
		
		List<ThirdBill> thirdBillList = thirdBillDao.findByOrderNoAndOrderState(requestVo.getOrderNo());
		RefundVo vo = new RefundVo();
		if (thirdBillList.size() <= 0) {
			List<HisTransactionFlow> hisTransactionFlows = hisTransactionFlowDao
					.findByOrderNoAndOrderState(requestVo.getOrderNo());

			if (hisTransactionFlows.size() <= 0) {
				return ResponseResult.failure("退费失败, 订单号对应订单为空");
			} else {
				PayFlowNo = hisTransactionFlows.get(0).getPayFlowNo();
				payType = hisTransactionFlows.get(0).getPayType();
				hisFlowNo = hisTransactionFlows.get(0).getHisFlowNo();
			}

		} else {
			List<HisTransactionFlow> hisTransactionFlows = hisTransactionFlowDao.findByOrderNoAndOrderState(requestVo.getOrderNo());
			if (hisTransactionFlows.size() > 0) {
				hisFlowNo = hisTransactionFlows.get(0).getHisFlowNo();
			}
			ThirdBill tb = thirdBillList.get(0); 
			PayFlowNo = tb.getPayFlowNo();
			payType = tb.getPayType();
			cardNo = tb.getPayAccount();
            outTradeNo=tb.getOutTradeNo();
			unionPayType = tb.getUnionPayType();
			unionPayCode = tb.getUnionPayCode();
			unionSystemCode = tb.getUnionSystemCode();
			vo.setShopNo(tb.getShopFlowNo());
		}
		vo.setThirdOutTradeNo(outTradeNo);
		vo.setOutTradeNo(requestVo.getSysNo());
		vo.setImgUrl(requestVo.getImgUrl());
		vo.setSqm(requestVo.getSqm());
		vo.setPjh(requestVo.getPjh());
		vo.setCashier(requestVo.getCashier());
		vo.setCounterNo(requestVo.getCounterNo());
		vo.setBocNo(requestVo.getBocNo());
		// 需要退款的订单号：tsn
		vo.setOrderNo(requestVo.getOrderNo());
		vo.setTsn(PayFlowNo);
		// 医院编码：orgCode
		vo.setOrgCode(requestVo.getOrgCode());
		// 支付类型：payType
		vo.setPayType(payType);
		// 支付渠道：payCode
		vo.setPayCode(requestVo.getPayCode());
		// 操作人信息：user
		vo.setUser(user);
		// 退款原因：reason
		vo.setReason(requestVo.getReason());
		// 订单来源：billSource（"self_jd","巨鼎" "self","银医" "third","第三方"）
		vo.setBillSource(requestVo.getBillSource());
		vo.setTradeAmount(requestVo.getTradeAmount());
		// 部分退款金额
		vo.setBatchRefundNo(requestVo.getBatchRefundNo());
		// 订单总金额
		vo.setPayAmount(requestVo.getPayAmount());
		vo.setBusinessType(requestVo.getBusinessType());
		vo.setTime(requestVo.getTime());
		vo.setTradetime(requestVo.getTradetime());
		// 商户号
		vo.setMerId(requestVo.getShopNo());
		// 终端号
		vo.setTermId(requestVo.getTerminalNo());
		// 发票号
		vo.setInvoiceNo(requestVo.getInvoiceNo());
		// 患者类型
		vo.setPatType(requestVo.getPatType());
		vo.setHisFlowNo(hisFlowNo);
		vo.setCardNo(cardNo);
		vo.setRefundType(requestVo.getRefundType());
		vo.setUnionPayType(unionPayType);
		vo.setUnionPayCode(unionPayCode);
		vo.setUnionSystemCode(unionSystemCode);
		
		vo.setExtendArea(JsonChangeVo.getJson(vo));
		ResponseResult result = refundService.refundAll(vo);
		logger.error("退款结果：{}", result.getMessage());
		/*if (result.isSuccess() && result.getData().toString().equals(RefundStateEnum.refund.getValue())) {
//			handleRefund(orderNo);
		}*/
		return result;
	}

	@Transactional(rollbackFor = Exception.class)
	public ResponseResult handleRefund(String orderNo) {
		// 删除支付渠道表记录
		thirdBillDao.delete(orderNo);
		// 删除异常记录
		tradeCheckFollowDao.deleteByBusinessNo(orderNo);
		return ResponseResult.success();
	}

	/**
	 * 唐都金蝶退费
	 *
	 * @param reason
	 * @param orgNo
	 * @param endpoint
	 * @param username
	 * @param password
	 * @param method
	 * @param bill
	 * @throws Exception
	 */
	private void refundForTangDuJd(String reason, String orgNo, String endpoint, String username, String password,
								   String method, ThirdBill bill) throws Exception {
		Map<String, String> data = new HashMap<>();
		data.put("orderId", bill.getOriPayFlowNo());
		data.put("refundTime", DateUtil.getCurrentTimeString());
		data.put("refundFee", String.valueOf(bill.getPayAmount().multiply(new BigDecimal(100)).intValue()));
		data.put("refundNo", bill.getPayFlowNo());
		data.put("reason", reason);
		String reqXml = null;
		try {
			reqXml = WebServiceClientUtil.mapToXml(data);
		} catch (Exception e) {
			Exception exception = new Exception("mapToXml error: " + e);
			throw exception;
		}
		reqXml = reqXml.substring(reqXml.indexOf(">") + 1, reqXml.length());
		System.out.println("金蝶退费入参：" + reqXml);
		String refMsg = WebServiceClientUtil.himapwsCallService(endpoint, orgNo, method, reqXml, username, password);
		System.out.println("金蝶退费信息：" + refMsg);
	}

	public void refundSeting(TradeCheckFollow tradeCheckFollow, Reconciliation reconciliation, String handleRemark,
							 String imgUrl, Long id) {
		ExcepHandingRecord ehr = new ExcepHandingRecord();
		ehr.setHandleDateTime(new Date());
		ehr.setHandleRemark(handleRemark);
		ehr.setOperationUserId(id);
		BigDecimal count = new BigDecimal(0);
		if (tradeCheckFollow != null) {
			ehr.setOrgNo(tradeCheckFollow.getOrgNo());
			ehr.setPaymentRequestFlow(tradeCheckFollow.getBusinessNo());
			ehr.setImgUrl(imgUrl);
			// ehr.setPaymentFlow(tradeCheckFollow.getPayNo());
			ehr.setBusinessType(tradeCheckFollow.getPayName());
			ehr.setPayName(tradeCheckFollow.getPayName());
			ehr.setPatientName(tradeCheckFollow.getPatientName());
			ehr.setTradeAmount(tradeCheckFollow.getTradeAmount());
			ehr.setTradeTime(tradeCheckFollow.getTradeTime());
		} else {
			ehr.setOrgNo(reconciliation.getOrgNo());
			ehr.setPaymentRequestFlow(reconciliation.getPayFlowNo());
			ehr.setImgUrl(imgUrl);
			// ehr.setPaymentFlow(reconciliation.getPayNo());
			ehr.setPayName(reconciliation.getPayType());
			ehr.setPatientName(reconciliation.getCustName());
			ehr.setBusinessType(reconciliation.getPayType());
			ehr.setTradeAmount(reconciliation.getOrgAmount().compareTo(count) == 0
					? reconciliation.getPlatformAmount().compareTo(count) == 0 ? reconciliation.getThirdAmount()
					: reconciliation.getPlatformAmount()
					: reconciliation.getOrgAmount());
			ehr.setTradeTime(reconciliation.getReconciliationDate());
		}
		excepHandingRecordDao.save(ehr);
	}

	public void refund(String url, String tsn, String orderNo, String reason, String orgNo, String payCode,
					   String tradeAmount, String batchRefundNo) throws Exception {
		// url="http://192.168.19.207:8068/order/refund";
		url = url + "order/refund";
		Map<String, Object> map = new HashMap<String, Object>(10);
		map.put("reason", reason);
		map.put("orderNo", orderNo);
		map.put("tsn", tsn);
		map.put("orgCode", orgNo);
		if (org.apache.commons.lang.StringUtils.isNotBlank(payCode)) {
			map.put("payCode", payCode);
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(batchRefundNo)) {
			map.put("batchRefundNo", batchRefundNo);
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(tradeAmount)) {
			map.put("refundAmount", tradeAmount);
		}
		JSONObject jsonObject = JSONObject.fromObject(map);
		String retStr = null;
		retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
		JSONObject json = JSONObject.fromObject(retStr);
		if (!json.getBoolean("success")) {
			Exception exception = new Exception(json.getString("message"));
			throw exception;
		}
	}

	public void refundThird(String url, String tsn, String orderNo, String reason, String orgNo, String payCode,
							String tradeAmount) throws Exception {
		url = url + "order/hospitalRefund";
		Map<String, Object> map = new HashMap<String, Object>(10);
		map.put("reason", reason);
		map.put("orderNo", orderNo);
		map.put("tsn", tsn);
		map.put("orgCode", orgNo);
		if (org.apache.commons.lang.StringUtils.isNotBlank(payCode)) {
			map.put("payCode", payCode);
		}
		map.put("refundAmount", tradeAmount);
		JSONObject jsonObject = JSONObject.fromObject(map);
		String retStr = null;
		retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
		JSONObject json = JSONObject.fromObject(retStr);
		if (!json.getBoolean("success")) {
			Exception exception = new Exception(json.getString("message"));
			throw exception;
		}
	}

	@Override
	public Page<RecCash> getCash(Integer id, Pageable pageable) {
		String sql = "select * from t_rec_cash t where t.id=" + id;
		return handleNativeSql(sql, pageable, RecCash.class);
	}

	@Override
	public Page<HealthCareOfficial> getYiBao(Integer id, Pageable pageable) {
		String sql = "select * from t_healthcare_official t where t.id=" + id;
		return handleNativeSql(sql, pageable, HealthCareOfficial.class);
	}

	@Override
	public Page<ThirdBill> getBill(Integer id, Pageable pageable) {
		String sql = "select * from t_thrid_bill t where t.id=" + id;
		Page<ThirdBill> page = handleNativeSql(sql, pageable, ThirdBill.class);
		List<ThirdBill> thirdBills = page.getContent();
		// 从线上订单中获取患者就诊号
		String visitNumbe = "";
		String getPayCenterOrderInfo = propertiesConfigService.findValueByPkey(ProConstants.payCenterOrderInfo,
				ProConstants.DEFAULT.get(ProConstants.payCenterOrderInfo));
		if (Boolean.valueOf(getPayCenterOrderInfo)) {
			if (thirdBills.size() > 0) {
				ThirdBill thirdBill = thirdBills.get(0);
				// 获取患者就诊号
				visitNumbe = getVisitNumber(thirdBill.getPayFlowNo(), thirdBill.getOrgNo(),DateUtil.transferDateToDateFormat("yyyy-MM-dd",thirdBills.get(0).getTradeDatatime()));
			}
		}
		if (thirdBills.size() > 0) {
			// 查询订单上送表, 新增'患者类型'、'就诊卡号'、'患者名称'三个字段
			OrderUpload orderUpload = orderUploadDao.findByTsnOrderNo(thirdBills.get(0).getPayFlowNo());
			if (orderUpload != null) {
				for (ThirdBill thirdBill : thirdBills) {
					thirdBill.setPatType(orderUpload.getPatType());
                    thirdBill.setCustName(orderUpload.getPatientName());
                    thirdBill.setPatientCardNo(orderUpload.getPatientCardNo());
					// 患者就诊号
					thirdBill.setVisitNumbe(visitNumbe);
				}
			} else {
				for (ThirdBill thirdBill : thirdBills) {
					// 患者就诊号
					thirdBill.setVisitNumbe(visitNumbe);
				}
			}
		}
		return page;
	}

	@Override
	public Map<String, Object> updateDifferenceAmount(String tradeTime, String orgNo) {
		String startTime = tradeTime + " 00:00:00";
		String endTime = tradeTime + " 23:59:59";
		String sql = "SELECT "
				+ "IFNULL(sum(case when tt.orderState=2 OR tt.orderState=6 OR tt.orderState=10 then tt.deal_amount end),0.00)"
				+ " - " + "IFNULL(sum(case when tt.orderState=3 OR tt.orderState=5 then tt.deal_amount end),0.00) "
				+ "AS 'differenceAmout', "
				+ "IFNULL(SUM(case when tt.orderState=2 OR tt.orderState=6 OR tt.orderState=3 OR tt.orderState=5 OR tt.orderState=10 then 1 end),0.00) "
				+ "AS 'differenceCount'  " + "from "
				+ "(SELECT trcd.*,CASE tehr.state WHEN '1' THEN '7' WHEN '2' THEN '8' WHEN '3' THEN '3' ELSE trcd.exception_state END "
				+ "AS orderState" + " FROM  " + "(SELECT * FROM t_trade_check_follow_deal WHERE pay_flow_no in"
				+ "(SELECT business_no from t_trade_check_follow" + " WHERE" + " trade_time >='" + startTime
				+ "' AND trade_time<='" + endTime + "' AND org_no = '" + orgNo + "')) trcd " + " LEFT JOIN "
				+ "t_exception_handling_record  tehr" + " ON " + "tehr.Payment_Request_Flow = trcd.pay_flow_no) tt";
		return handleNativeSql(sql).get(0);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult dealFollow(String userName, String payFlowNo, String description, MultipartFile file) {

		// 上传图片
		String saveFileLocation = "";
		if (file != null) {
			saveFileLocation = saveImage(file);
		}

		// 保存处理单
		tradeCheckFollowDealDao
				.save(new TradeCheckFollowDeal(userName, payFlowNo, description, saveFileLocation, new Date()));

		return ResponseResult.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult newDealFollow(String userName, String payFlowNo, String description, MultipartFile file,
										String checkState, String tradeAmount, String orgCode, String tradeDatetime,
										String payName, String billSource, String patType, String recHisId, String recThridId) {
		// 上传图片
		String saveFileLocation = "";
		if (file != null) {
			saveFileLocation = saveImage(file);
		}
		// 保存处理单
		tradeCheckFollowDealDao.save(new TradeCheckFollowDeal(userName, payFlowNo, description, saveFileLocation, new Date(),
				checkState, new BigDecimal(tradeAmount), orgCode, tradeDatetime, payName, billSource, patType, recHisId, recThridId));
		return ResponseResult.success();
	}

	private String saveImage(MultipartFile file) {
		String fileName = file.getOriginalFilename();
		String fileLocation = Configure.getPropertyBykey("file.location");
		if (fileName.indexOf(".") >= 0) {
			fileName = UUID.randomUUID().toString().replace("-", "").toLowerCase()
					+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
		}
		try {
			uploadFile(file.getBytes(), fileLocation, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	@Override
	public Page<Map<String, Object>> getTradeDetailPage(TradeDetailVo tdv, List<Organization> orgList,
														PageRequest pageRequest) {
		String orderBySql = concatOrderBySql(pageRequest);
		String sql = tradeDetailSql(tdv, orgList, orderBySql);
		return handleNativeSql(sql, pageRequest,
				new String[] { "orderSourceState","invoiceNo", "stateRemark", "tsn", "orderNo", "id", "orgCode", "systemFrom",
						"tradeDataTime", "custName", "payAmount", "paySystemNo", "hisOrderNO", "visitNumber",
//						"deviceNo",
						"orderState", "patType", "payBussinessType", "cashier", "payType", "ybPayAmount", "billSource",
						"extendArea", "outTradeNo" });
	}

	private String concatOrderBySql(PageRequest pageRequest) {
		Sort sort = pageRequest.getSort();
		if (sort == null) {
			return " order_state DESC ";
		}
		String orderSql = "";
		Iterator<Order> iterator = sort.iterator();
		while (iterator.hasNext()) {
			Order order = iterator.next();
			orderSql += order.getProperty() + " " + order.getDirection() + ",";
		}
		if (StringUtil.isEmpty(orderSql)) {
			orderSql = " order_state DESC ";
		} else {
			orderSql = orderSql.substring(0, orderSql.lastIndexOf(","));
		}
		return orderSql;
	}

	@Override
	public List<Map<String, Object>> exportTradeDetail(TradeDetailVo tdv, List<Organization> orgList,
													   PageRequest pageRequest) {
		String orderSql = this.concatOrderBySql(pageRequest);
		String sql = tradeDetailSql(tdv, orgList, orderSql);
		List<Map<String, Object>> tradeDetailList = super.handleNativeSql(sql,
				new String[] { "orderSourceState","patType","invoiceNo", "stateRemark", "tsn", "orderNo", "id", "orgCode", "systemFrom",
						"tradeDataTime", "custName", "payAmount", "paySystemNo", "hisOrderNO", "visitNumber",
//						"deviceNo",
						"orderState", "patType", "payBussinessType", "cashier", "payType", "ybPayAmount", "billSource",
						"extendArea", "outTradeNo" });
		return changeFinanceMoIdToName(tradeDetailList);
	}

	private List<Map<String, Object>> changeFinanceMoIdToName(List<Map<String, Object>> list) {
		Map<String, Object> orgMap = gatherService.getOrgMap();
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		BigDecimal ybAmount = new BigDecimal(0);
		BigDecimal wxAmount = new BigDecimal(0);
		BigDecimal zfbAmount = new BigDecimal(0);
		BigDecimal bankAmount = new BigDecimal(0);
		int wxCount = 0;
		int zfbCount = 0;
		int bankCount = 0;
		int ybCount = 0;
		if (!StringUtil.isNullOrEmpty(list)) {
			Map<String, Object> mapAllAmount = new HashMap<String, Object>();
			Map<String, Object> mapAllCount = new HashMap<String, Object>();
			for (Map<String, Object> map : list) {
				map.put("tsn", orgMap.get(String.valueOf(map.get("tsn"))));
				map.put("orgCode", orgMap.get(String.valueOf(map.get("orgCode"))));
				map.put("systemFrom", metaMap.get(String.valueOf(map.get("systemFrom"))));
				map.put("tradeDataTime", String.valueOf(map.get("tradeDataTime")));
				map.put("custName", String.valueOf(map.get("custName")));
				map.put("payAmount", String.valueOf(map.get("payAmount")));
				if (org.apache.commons.lang3.StringUtils.isNotBlank(String.valueOf(map.get("payType")))) {
					BigDecimal amount = new BigDecimal(0);
					if (map.get("payAmount") != null)
						amount = new BigDecimal(String.valueOf(map.get("payAmount")));
					if (String.valueOf(map.get("payType")).equals(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())) {// 医保金额
						map.put("payAmount", String.valueOf(map.get("ybPayAmount")));
						BigDecimal ybPayAmount = new BigDecimal(0);
						if (map.get("ybPayAmount") != null)
							ybPayAmount = new BigDecimal(String.valueOf(map.get("ybPayAmount")));
						ybCount++;
						ybAmount = ybAmount.add(ybPayAmount);
					}
					if (String.valueOf(map.get("payType")).equals(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())) {// 微信金额
						wxCount++;
						wxAmount = wxAmount.add(amount);
					}
					if (String.valueOf(map.get("payType")).equals(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())) {// 支付宝金额
						zfbCount++;
						zfbAmount = zfbAmount.add(amount);
					}
					if (String.valueOf(map.get("payType")).equals(EnumTypeOfInt.PAY_TYPE_BANK.getValue())) {// 银行金额
						bankCount++;
						bankAmount = bankAmount.add(amount);
					}
				}
				map.put("paySystemNo", String.valueOf(map.get("paySystemNo")));
				map.put("visitNumber", String.valueOf(map.get("visitNumber")));
				map.put("cashier", String.valueOf(map.get("cashier")));
				map.put("payType", metaMap.get(String.valueOf(map.get("payType"))));
				map.put("orderState", metaMap.get(String.valueOf(map.get("orderState"))));
			}
			mapAllAmount.put("allAmount", "支付交易总额：" + ybAmount.add(zfbAmount).add(wxAmount).add(bankAmount) + "(元)");
			mapAllAmount.put("wxAmount", "微信支付总额：" + wxAmount + "(元)");
			mapAllAmount.put("zfbAmount", "支付宝支付总额：" + zfbAmount + "(元)");
			mapAllAmount.put("bankAmount", "银行支付总额：" + bankAmount + "(元)");
			mapAllAmount.put("ybAmount", "医保支付总额：" + ybAmount + "(元)");

			mapAllCount.put("allCount", "支付交易总笔数：" + (wxCount + zfbCount + bankCount + ybCount));
			mapAllCount.put("wxCount", "微信支付总笔数：" + wxCount);
			mapAllCount.put("zfbCount", "支付宝支付总笔数：" + zfbCount);
			mapAllCount.put("bankCount", "银行支付总笔数：" + bankCount);
			mapAllCount.put("ybCount", "医保支付总笔数：" + ybCount);
			list.add(mapAllAmount);
			list.add(mapAllCount);
		}
		return list;
	}

	/**
	 * 交易明细查询公用sql,导出sql
	 *
	 * @param tdv
	 * @param orgList
	 * @param orderBySql
	 * @return
	 */
	private String tradeDetailSql(TradeDetailVo tdv, List<Organization> orgList, String orderBySql) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT order_state AS orderSourceState,");
		sb.append(" invoice_no AS invoiceNo,");
		sb.append(" order_state_remark AS stateRemark,");
		sb.append(" tsn_order_no AS tsn,");
		sb.append(" order_no AS orderNo,");
		sb.append(" id AS id,");
		sb.append(" org_code AS orgCode,");
		sb.append(" bill_source AS systemFrom,");
		sb.append(" trade_date_time as tradeDataTime,");
		sb.append(" patient_name AS custName,");
		sb.append(" pay_amount AS payAmount,");
		sb.append(" tsn_order_no AS paySystemNo,");
		sb.append(" his_order_no AS hisOrderNO,");
		sb.append(" patient_card_no AS visitNumber,");
		sb.append(
				" CASE WHEN (ISNULL(refund_order_state) or refund_order_state='') THEN tou.order_state ELSE refund_order_state END AS orderState,");
		sb.append(" pat_type AS patType,");
		sb.append(" Pay_Business_Type AS payBussinessType,");
		sb.append(" cashier AS cashier,");
		sb.append(" pay_type AS payType,");
		sb.append(" yb_pay_amount AS ybPayAmount, ");
		sb.append("	bill_source AS billSource,");
		sb.append(" extend_area AS extendArea,");
		sb.append(" out_trade_no AS outTradeNo ");
		sb.append(" FROM t_order_upload tou ");
		sb.append(" WHERE 1=1 ");
		sql = sb.toString();
		// 机构
		String orgCode = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getOrgCode())) {
			if (orgList != null && orgList.size() > 0) {
				Set<String> orgSets = new HashSet<>();
				orgCode = " and org_code in ('" + tdv.getOrgCode() + "'";
				orgSets.add(orgCode);
				for (Organization v : orgList) {
					String org = v.getCode();
					if (orgSets.contains(org)) {
						continue;
					}
					orgCode += ",'" + org + "'";
					orgSets.add(org);
				}
				orgCode += ")";
			} else {
				orgCode = " and org_code='" + tdv.getOrgCode() + "'";
			}
		}
		// HIS系统订单号（HIS流水号）
		String hisOrderNO = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getHisOrderNO())) {
			hisOrderNO = " and (his_order_no='" + tdv.getHisOrderNO() + "'";
		}
		// 业务系统订单号（支付方流水号）
		String payFlowNo = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPaySystemNo())) {
			payFlowNo = " or tsn_order_no='" + tdv.getPaySystemNo() + "')";
		}
		// 业务类型
		String payBussinessType = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPayBussinessType())) {
			payBussinessType = " and Pay_Business_Type='" + tdv.getPayBussinessType() + "'";
		}
		// 柜员号
		String payDeviceNo = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getDeviceNo())) {
			payDeviceNo = " and cashier='" + tdv.getDeviceNo() + "'";
		}
		// 订单状态
		String orderState = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getOrderState())) {
			orderState = " and ((order_state='" + tdv.getOrderState() + "' and refund_order_state is null )"
					+ " or refund_order_state= '" + tdv.getOrderState() + "')";
		}
		// 就诊卡号
		String visitNum = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getVisitNumber())) {
			visitNum = " and patient_card_no='" + tdv.getVisitNumber() + "'";
		}
		// 患者名称
		String custName = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getCustName())) {
			custName = " and patient_name='" + tdv.getCustName() + "'";
		}
		// 患者类型
		String patType = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPatType())) {
			patType = " and pat_type='" + tdv.getPatType() + "'";
		}
		// 支付类型
		String payType = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getPayType())) {
			payType = " and pay_type='" + tdv.getPayType() + "'";
		}
		// 系统来源
		String systemFrom = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getSystemFrom())) {
			systemFrom = " and bill_source='" + tdv.getSystemFrom() + "'";
		}
		// 时间
		String startTime = "";
		String endTime = "";
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getStartDate())) {
			startTime = " and trade_date_time>='" + tdv.getStartDate() + "'";
		}
		if (org.apache.commons.lang.StringUtils.isNotBlank(tdv.getEndDate())) {
			endTime = " and trade_date_time<='" + tdv.getEndDate() + "'";
		}
		String whereSql = orgCode + payBussinessType + payDeviceNo + orderState + visitNum + custName + hisOrderNO
				+ payFlowNo + patType + payType + systemFrom + startTime + endTime;
		if (StringUtil.checkNotNull(orderBySql) && "id DESC".equalsIgnoreCase(orderBySql)) {
			sql += whereSql + " ORDER BY order_state";
		} else {
			sql += whereSql + " ORDER BY " + orderBySql;
		}
		logger.info(sql);
		return sql;
	}

	@Override
	public Map<String, Object> getTradeRefundDetail(String id) {
		String Sql = "select id as id,tsn_order_no as tsn,org_code as orgCode,pay_type as payType,pay_type as payCode,"
				+ "bill_source as billSource,pay_amount as tradeAmount,out_trade_no as paySystemNo from t_order_upload tmp where 1=1 and id='"
				+ id + "'";
		List<Map<String, Object>> resList = handleNativeSql(Sql);
		Map<String, Object> map = resList.get(0);
		Map<String, Object> newMap = new HashMap<>();
		newMap.put("id", StringUtil.getNotNullStr(map.get("0")));
		newMap.put("tsn", StringUtil.getNotNullStr(map.get("1")));
		newMap.put("orgCode", StringUtil.getNotNullStr(map.get("2")));
		newMap.put("payType", StringUtil.getNotNullStr(map.get("3")));
		newMap.put("payCode", StringUtil.getNotNullStr(map.get("4")));
		newMap.put("billSource", StringUtil.getNotNullStr(map.get("5")));
		newMap.put("tradeAmount", StringUtil.getNotNullStr(map.get("6")));
		newMap.put("paySystemNo", StringUtil.getNotNullStr(map.get("7")));
		return newMap;
	}

	@Override
	public ResponseResult updateDetailById(Long id, User user) {
		Reconciliation rec = reconciliationDao.findOne(id);
		if (null == rec) {
			return ResponseResult.failure("记录不存在或已被删除");
		} else {
			rec.setLastModifiedById(user.getId());
			rec.setIsDifferent(EnumTypeOfInt.TRADE_TYPE_REFUND.getId());
			reconciliationDao.save(rec);
			return ResponseResult.success();
		}
	}

	@Override
	public Map<String, Object> getExceptionHandlingRecord(String temp) {
		String paymentRequestFlow = " and Payment_Request_Flow in ( " + temp + ")";
		String Sql = "SELECT Payment_Request_Flow AS paymentRequestFlow,state AS orderState "
				+ "FROM `t_exception_handling_record` WHERE  (father_id = 0 OR father_id IS NULL) "
				+ paymentRequestFlow;
		List<Map<String, Object>> excHandRecordList = handleNativeSql(Sql);
		logger.info(Sql);
		Map<String, Object> flowMap = new HashMap<>();
		for (Map<String, Object> map : excHandRecordList) {
			flowMap.put(String.valueOf(map.get("0")), getOrderState(map.get("1")));
		}
		return flowMap;
	}

	private String getOrderState(Object obj) {
		if ("1".equals(String.valueOf(obj))) {
			// 审核中
			return "1809303";
		} else if ("2".equals(String.valueOf(obj))) {
			// 被驳回
			return "1809305";
		} else if ("3".equals(String.valueOf(obj))) {
			// 已退款
			return "1809304";
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getRefundInfo(String orgNo, String payFlowNo) {
		String refund_upload_SQL = "SELECT  tru.`refund_order_no` refundOrderNo, tru.`refund_amount` refundAmount, tru.`refund_date_time` refundDateTime"
				+ " FROM t_refund_upload tru WHERE tru.ori_tsn_order_no = '" + payFlowNo + "' and org_code = '" + orgNo
				+ "'";
		String refund_exception_hadding_SQL = "SELECT Payment_Flow AS refundOrderNo,trade_Amount as refundAmount,Handle_Date_Time as refundDateTime "
				+ "FROM `t_exception_handling_record` WHERE  (father_id = 0 OR father_id IS NULL) "
				+ "And Payment_Request_Flow ='" + payFlowNo + "' And org_no ='" + orgNo + "'";
		List<Map<String, Object>> refundUploadList = handleNativeSql(refund_upload_SQL);
		List<Map<String, Object>> excHandRecordList = handleNativeSql(refund_exception_hadding_SQL);
		List<Map<String, Object>> returnList = new ArrayList<>();
		logger.info("refund_upload_SQL:" + refund_upload_SQL);
		logger.info("refund_exception_hadding_SQL:" + refund_exception_hadding_SQL);
		Map<String, Object> refundMap = new HashMap<>();
		if (refundUploadList.size() > 0) {
			for (Map<String, Object> map : refundUploadList) {
				refundMap.put("refundOrderNo", String.valueOf(map.get("0")));
				refundMap.put("refundAmount", String.valueOf(map.get("1")));
				refundMap.put("refundDateTime",
						DateUtil.transferStringToDateFormat("yyyy-MM-dd HH:mm:ss", String.valueOf(map.get("2"))));
				returnList.add(refundMap);
			}

		} else {
			for (Map<String, Object> map : excHandRecordList) {
				refundMap.put("refundOrderNo", String.valueOf(map.get("0")));
				refundMap.put("refundAmount", String.valueOf(map.get("1")));
				refundMap.put("refundDateTime",
						DateUtil.transferStringToDateFormat("yyyy-MM-dd HH:mm:ss", String.valueOf(map.get("2"))));
				returnList.add(refundMap);
			}
		}
		return returnList;

	}
	/**
	 * 通过机构编码、交易流水号查询paycenter中订单信息
	 * @param payFlowNo 交易流水号
	 * @param orgNo 机构编码
	 * @param date  账单日期
	 * @return
	 */
	private String getVisitNumber(String payFlowNo ,String orgNo, String date){
		String beginTime = date + " 00:00:00";
		String endTime = date + " 23:59:59";
		String paramsStr = "{\"beginTime\":\"" + beginTime + "\",\"endTime\":\"" + endTime + "\",\"orgCode\":\"" + orgNo + "\"}";
		String extStr = "";
		// 就诊卡号
		String visitNumber = "";
		String retStrs="";
		String payCenterHost = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl, ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		try {
			retStrs = com.yiban.rec.bill.parse.util.HttpClientUtil.doPostJson(payCenterHost  + "/pay/billLog/query/list", paramsStr);
		} catch (Exception e) {
		}
		if (!StringUtil.isNullOrEmpty(retStrs) && retStrs.startsWith("[")) {
			net.sf.json.JSONArray array = JSONArray.fromObject(retStrs);
			for (int i = 0; i < array.size() - 1; i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String tsnNo = "";
				tsnNo = jsonObject.containsKey("tsn") ? jsonObject.getString("tsn") : "";
				if (payFlowNo.equalsIgnoreCase(tsnNo)){
					extStr = jsonObject.containsKey("extra_param") ? jsonObject.getString("extra_param") : "";
					// 校验是否json字符串
					if(!StringUtil.isNullOrEmpty(extStr) && extStr.startsWith("{")){
						JSONObject extraParamJsonObj = JSONObject.fromObject(extStr);
						visitNumber = extraParamJsonObj.containsKey("visitNumber") ? extraParamJsonObj.getString("visitNumber") : "";
					}
					break;
				}
			}
		}
		return visitNumber;
	}
}
