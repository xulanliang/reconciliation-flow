package com.yiban.rec.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.RecCashDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.service.CashRecService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

/**
 * 现金对账service
 * 
 * @author clearofchina
 *
 */
@Service
public class CashRecServiceImpl extends BaseOprService implements CashRecService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;
	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;
	@Autowired
	private RecCashDao cashDao;

	@Override
	public ResponseResult getFollowRecMap(String orgNo, String startDate, String endDate) {
		ResponseResult result = ResponseResult.success();
		// 根据配置信息查询结果
		String payTypeSql = "'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "' ";
		String orgNoSql = this.concatOrgNoSql(orgNo);
		List<Map<String, Object>> listMap = getFollowCount(startDate, endDate, payTypeSql, orgNoSql);

		// 1. 获取 总计
		FollowRecResult f = new FollowRecResult();
		if (null != listMap) {
			for (Map<String, Object> map : listMap) {
				// his应收
				if ("his".equals((String) map.get("data_source"))) {
					f.setHisAllAmount(new BigDecimal(map.get("pay_amount").toString()));
					f.setHisPayAcount((new BigDecimal(map.get("payAcount").toString())).intValue());
					f.setHisRefundAcount(new BigDecimal(map.get("refundAcount").toString()).intValue());
					continue;
				}
				// 实收
				if ("third".equals((String) map.get("data_source"))) {
					f.setPayAllAmount(new BigDecimal(map.get("pay_amount").toString()));
					f.setPayAcount((new BigDecimal(map.get("payAcount").toString())).intValue());
					f.setRefundAcount(new BigDecimal(map.get("refundAcount").toString()).intValue());
					continue;
				}
			}
			// 差异
			if (null != f.getHisAllAmount() && f.getPayAllAmount() != null) {
				f.setTradeDiffAmount(f.getPayAllAmount().subtract(f.getHisAllAmount()));
				f.setTradeDiffPayAcount(Math.abs(f.getHisPayAcount() - f.getPayAcount()));
			}
			f.setOrgNo(orgNo);
		}

		// 2. 获取支付方式数据
		List<Map<String, Object>> list = this.getFollowPayInfoDetail(startDate, endDate, payTypeSql, orgNoSql);
		Map<String, List<Map<String, Object>>> payDetailMap = this.dealData(list);

		HashMap<String, Object> resMap = new HashMap<>();
		resMap.put("recResult", f);
		resMap.put("payDetailMap", payDetailMap);

		return result.data(resMap);
	}

	public Map<String, List<Map<String, Object>>> dealData(List<Map<String, Object>> list) {
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

		Map<String, Integer> indexMap = new HashMap<>();
		indexMap.put(EnumTypeOfInt.PAT_TYPE_MZ.getValue(), 1);
		indexMap.put(EnumTypeOfInt.PAT_TYPE_ZY.getValue(), 2);

		Map<String, List<Map<String, Object>>> dataMap = new HashMap<>();
		for (Map<String, Object> map : list) {
			// payLocation： 0001 自助机， 0002 窗口
			String payLocation = (String) map.get("payLocation");
			if (StringUtil.isEmpty(payLocation))
				continue;

			List<Map<String, Object>> listMap = null;
			if (dataMap.containsKey(payLocation)) {
				listMap = dataMap.get(payLocation);
			} else {
				listMap = initData(metaMap);
			}
			String patType = (String) map.get("patType");
			if (StringUtil.isEmpty(patType))
				continue;

			HashMap<String, Object> valueMap = new HashMap<>();
			valueMap.put("patType", metaMap.get(patType));
			valueMap.put("payAmount", map.get("payAmount"));
			valueMap.put("realPayAcount", map.get("realPayAcount"));
			valueMap.put("refundAcount", map.get("refundAcount"));
			listMap.set(indexMap.get(patType), valueMap);
			dataMap.put(payLocation, listMap);
		}

		// 总计
		for (String payLocation : dataMap.keySet()) {
			// 总计的实际总金额
			BigDecimal totalPayAmount = new BigDecimal(0);
			// 总计的支付笔数
			BigDecimal totalPayAcount = new BigDecimal(0);
			// 总计的支出笔数
			BigDecimal totalRefundAcount = new BigDecimal(0);
			List<Map<String, Object>> obj = dataMap.get(payLocation);
			// 从第二个(门诊)开始循环
			for (int i = 1; i < obj.size(); i++) {
				Map<String, Object> detailMap = obj.get(i);
				totalPayAmount = totalPayAmount.add((BigDecimal) detailMap.get("payAmount"));
				totalPayAcount = totalPayAcount.add((BigDecimal) detailMap.get("realPayAcount"));
				totalRefundAcount = totalRefundAcount.add((BigDecimal) detailMap.get("refundAcount"));
			}
			HashMap<String, Object> totalMap = new HashMap<>();
			totalMap.put("payAmount", totalPayAmount);
			totalMap.put("realPayAcount", totalPayAcount);
			totalMap.put("refundAcount", totalRefundAcount);
			totalMap.put("patType", "总计");
			obj.set(0, totalMap);
		}
		return dataMap;
	}

	public ArrayList<Map<String, Object>> initData(Map<String, String> metaMap) {

		HashMap<String, Object> valueMap = new HashMap<>();
		valueMap.put("payAmount", BigDecimal.ZERO);
		valueMap.put("realPayAcount", BigDecimal.ZERO);
		valueMap.put("refundAcount", BigDecimal.ZERO);

		ArrayList<Map<String, Object>> list = new ArrayList<>();
		list.add(new HashMap<>());

		valueMap = new HashMap<>();
		valueMap.put("payAmount", BigDecimal.ZERO);
		valueMap.put("realPayAcount", BigDecimal.ZERO);
		valueMap.put("refundAcount", BigDecimal.ZERO);
		valueMap.put("patType", metaMap.get(EnumTypeOfInt.PAT_TYPE_MZ.getValue()));
		list.add(valueMap);

		valueMap = new HashMap<>();
		valueMap.put("payAmount", BigDecimal.ZERO);
		valueMap.put("realPayAcount", BigDecimal.ZERO);
		valueMap.put("refundAcount", BigDecimal.ZERO);
		valueMap.put("patType", metaMap.get(EnumTypeOfInt.PAT_TYPE_ZY.getValue()));
		list.add(valueMap);
		return list;
	}

	/**
	 * 补充和格式化没有的数据
	 * 
	 * @param dataMap
	 */
	public void formatData(Map<String, List<Map<String, Object>>> dataMap, Map<String, Integer> indexMap) {
		for (String payLocation : dataMap.keySet()) {
			List<Map<String, Object>> list = dataMap.get(payLocation);
			for (Map<String, Object> map : list) {
				if (map.isEmpty()) {
					// 存在住院,则补充门诊的
					String patType = list.get(0).get("patType").toString().equals(EnumTypeOfInt.PAT_TYPE_ZY.getValue())
							? EnumTypeOfInt.PAT_TYPE_MZ.getValue()
							: EnumTypeOfInt.PAT_TYPE_ZY.getValue();

					HashMap<String, Object> mzMap = new HashMap<>();
					mzMap.put("patType", patType);
					mzMap.put("payAmount", BigDecimal.ZERO);
					mzMap.put("realPayAcount", BigDecimal.ZERO);
					mzMap.put("refundAcount", BigDecimal.ZERO);
					list.add(indexMap.get(patType), mzMap);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getFollowCount(String startDate, String endDate, String payTypeSql,
			String orgNo) {
		final String sql = String.format(" SELECT 'his' data_source,"
				+ "	IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END), 0.00) pay_amount,"
				+ " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_acount END), 0) payAcount, "
				+ " IFNULL(SUM(CASE order_state WHEN '0256' THEN pay_acount ELSE 0 END), 0) refundAcount "
				+ " FROM t_follow_summary  WHERE data_source = 'his' "
				+ " AND  org_no in (%s) AND Trade_Date >= '%s' AND Trade_Date <= '%s' AND settlement_amount<=0 "
				+ " AND rec_pay_type in (%s)" + " UNION" + " SELECT 'third' data_source,"
				+ " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END), 0.00) pay_amount,"
				+ " IFNULL(SUM(CASE order_state WHEN '0156' THEN pay_acount END), 0) payAcount, "
				+ " IFNULL(SUM(CASE order_state WHEN '0256' THEN pay_acount ELSE 0 END), 0) refundAcount "
				+ " FROM t_follow_summary  WHERE data_source = 'third' "
				+ " AND  org_no in (%s) AND Trade_Date >= '%s' AND Trade_Date <= '%s' " + " AND rec_pay_type in (%s)",
				orgNo, startDate, endDate, payTypeSql, orgNo, startDate, endDate, payTypeSql);
		logger.info(" getFollowCount sql ============" + sql);
		return super.queryList(sql, null, null);
	}

	/**
	 * 查询支付类型的详情
	 * 
	 * @param startDate
	 * @param endDate
	 * @param payTypeSql
	 * @param orgNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getFollowPayInfoDetail(String startDate, String endDate, String payTypeSql,
			String orgNo) {
		final String sql = String.format("SELECT tfs.`pay_location` payLocation, tfs.`pat_type` patType,"
				+ " SUM(CASE order_state WHEN '0156' THEN tfs.`pay_amount` ELSE - ABS(tfs.`pay_amount`) END) payAmount,"
				+ " SUM(CASE tfs.`order_state` WHEN '0256' THEN 0 ELSE IFNULL(tfs.`pay_acount`, 0) END) realPayAcount,"
				+ " SUM(CASE tfs.`order_state` WHEN '0256' THEN tfs.`pay_acount` ELSE 0 END) refundAcount "
				+ " FROM t_follow_summary tfs " + "  WHERE org_no in (%s)" + "  AND Trade_Date >= '%s'"
				+ "  AND Trade_Date <= '%s'" + "  AND rec_pay_type IN (%s)" + "  AND data_source='third' "
				+ "GROUP BY tfs.pay_location , tfs.pat_type ", orgNo, startDate, endDate, payTypeSql);
		logger.info(" getFollowPayInfoDetail sql============" + sql);
		return super.queryList(sql, null, null);
	}

	/**
	 * 查询异常账单
	 */
	@Override
	public Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable) {
		List<Organization> orgList = organizationService.findByParentCode(vo.getOrgNo());
		String orgNo = vo.getOrgNo();
		String startDate = vo.getStartDate().trim();
		String endDate = vo.getEndDate().trim();
		String dataSourceType = vo.getDataSourceType();
		String[] orgs = null;

		String payTypeSql = "'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "' ";

		StringBuffer sql = new StringBuffer();
		if (orgList != null && orgList.size() > 0) {
			orgs = new String[orgList.size() + 1];
			orgs[0] = orgNo;
			sql.append(orgNo);
			for (int i = 0; i < orgList.size(); i++) {
				orgs[i + 1] = orgList.get(i).getCode();
				sql.append("," + orgList.get(i).getCode());
			}
		} else {
			orgs = new String[1];
			orgs[0] = orgNo;
			sql.append(orgNo);
		}
		Page<TradeCheckFollow> tcf = null;
		// 首页进来或者 点击查询按钮查询的情况， dataSourceType 为空
		if (dataSourceType != null) {
			startDate = startDate + " 00:00:00";
			endDate = endDate + " 23:59:59";
			String checkStates = "";
			String correctionSql = "";
			if ("his".equals(dataSourceType)) {
				checkStates = CommonEnum.BillBalance.HISDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
			} else if ("third".equals(dataSourceType)) {
				checkStates = CommonEnum.BillBalance.THIRDDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
			} else {
				checkStates = CommonEnum.BillBalance.HISDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREHIS.getValue() + ","
						+ CommonEnum.BillBalance.THIRDDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
			}
			tcf = findByOrgNoAndTradeDateAndCheckState(sql.toString(), startDate, endDate, checkStates, correctionSql,
					payTypeSql, pageable);
		} else {
//			Date sDate = new Date();
//			Date eDate = new Date();
//			try {
//				sDate = DateUtil.transferStringToDateFormat(startDate);
//				eDate = DateUtil.transferStringToDateFormat(endDate);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			tcf = tradeCheckFollowDao.findByOrgNoAndTradeDateAndPayName(orgs, startDate, endDate,
					CommonEnum.BillBalance.zp.getValue(), payTypeSql.replaceAll("'", "").split(","), pageable);
		}
		List<TradeCheckFollow> list = tcf.getContent();

		// 添加异常类型
		if (!StringUtil.isNullOrEmpty(list)) {
			for (TradeCheckFollow tradeCheckFollow : list) {
				String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
				String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + ","
						+ CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
				if (tradeCheckFollow.getCheckState() != null
						&& thirdCheck.contains(tradeCheckFollow.getCheckState().toString())) {
					tradeCheckFollow.setExceptionType("长款");
				} else if (tradeCheckFollow.getCheckState() != null
						&& hisCheck.contains(tradeCheckFollow.getCheckState().toString())) {
					tradeCheckFollow.setExceptionType("短款");
				}
			}
		}

		// 短款手动处理的账单
		handDealFollow(list);

		// 长款手动处理的账单
//		handExceptionFollow(list);

		// 将code 转换为名称
		String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + ","
				+ CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
		String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + ","
				+ CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
		if (!StringUtil.isNullOrEmpty(list)) {
//			Map<Integer,String> maptwo = CommonEnum.BillBalance.asMap();
			for (TradeCheckFollow tradeCheckFollow : list) {
//				tradeCheckFollow.setCheckStateValue(maptwo.get(checkState));
				Integer checkState = tradeCheckFollow.getCheckState();
				if (hisCheck.contains(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("短款");
				} else if (thirdCheck.contains(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("长款");
				} else if (CommonEnum.BillBalance.WAITEXAMINE.getValue().toString().equals(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("待审核");
				} else if (CommonEnum.BillBalance.REJECT.getValue().toString().equals(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("已驳回");
				} else if (CommonEnum.BillBalance.REFUND.getValue().toString().equals(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("已退费");
				} else if (CommonEnum.BillBalance.HANDLER.getValue().toString().equals(checkState.toString())) {
					tradeCheckFollow.setCheckStateValue("已处理");
				}
			}
		}
		return tcf;
	}

	// 短款手动处理的账单处理
	private void handDealFollow(List<TradeCheckFollow> list) {
		List<TradeCheckFollowDeal> dealFollowList = tradeCheckFollowDealDao.findAll();
		if (dealFollowList != null && list != null) {
			for (TradeCheckFollow tradeCheckFollow : list) {
				if (tradeCheckFollow.getCheckState() != null) {
					for (int i = dealFollowList.size() - 1; i >= 0; i--) {
						if (tradeCheckFollow.getBusinessNo().equals(dealFollowList.get(i).getPayFlowNo())) {
							tradeCheckFollow.setCheckState(CommonEnum.BillBalance.HANDLER.getValue());
							tradeCheckFollow.setDescription(dealFollowList.get(i).getDescription());
							tradeCheckFollow.setFileLocation(dealFollowList.get(i).getFileLocation());
						}
					}
				}
			}
		}
	}

	private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndCheckState(String orgNo, String startDate, String endDate,
			String checkStates, String correctionSql, String payTypeSql, Pageable pageable) {
		String sql = "SELECT * FROM (" + "SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo
				+ ") AND t.trade_time >= '" + startDate + "' AND t.trade_time <= '" + endDate + "' " + correctionSql
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo + ") AND t.trade_time >= '"
				+ startDate + "' AND t.trade_time <= '" + endDate
				+ "' AND ( ISNULL(t.business_no) OR t.business_no = '' )" + " )s WHERE s.check_state in (" + checkStates
				+ ") AND s.Pay_Name IN( " + payTypeSql + " )  ORDER BY s.id DESC";
		logger.info("查询现金对账异常账单 findByOrgNoAndTradeDateAndCheckState = " + sql);
		return handleNativeSql(sql, pageable, TradeCheckFollow.class);
	}

	private List<TradeCheckFollow> findByOrgNoAndTradeDateAndCorrectionNoPage(String orgNo, String startDate,
			String endDate, Integer checkState) {
		String sql = "SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo + ") AND t.trade_date >= '"
				+ startDate + "' AND t.trade_date <= '" + endDate + "' AND t.check_state <> " + checkState
				+ " AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN (" + orgNo + ") AND t.trade_date >= '"
				+ startDate + "' AND t.trade_date <= '" + endDate + "' AND t.check_state <> " + checkState
				+ " AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		logger.info("findByOrgNoAndTradeDateAndCorrectionNoPage = " + sql);
		return handleNativeSql(sql, TradeCheckFollow.class);
	}

	public List<TradeCheckFollow> findByOrgNoAndTradeDateNoPage(String orgNo, String startDate, String endDate,
			String correction) {
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		Organization organization = organizationService.findByCode(orgNo);
		orgList.add(organization);
		String[] orgs = null;
		StringBuffer sql = new StringBuffer();
		if (orgList != null && orgList.size() > 0) {
			orgs = new String[orgList.size() + 1];
			orgs[0] = orgNo;
			sql.append(orgNo);
			for (int i = 0; i < orgList.size(); i++) {
				orgs[i + 1] = orgList.get(i).getCode();
				sql.append("," + orgList.get(i).getCode());
			}
		} else {
			orgs = new String[1];
			orgs[0] = orgNo;
			sql.append(orgNo);
		}
		List<TradeCheckFollow> list = null;
		if ("1".equals(correction)) {
			list = findByOrgNoAndTradeDateAndCorrectionNoPage(sql.toString(), startDate, endDate,
					CommonEnum.BillBalance.zp.getValue());
		} else {
			list = tradeCheckFollowDao.findByOrgNoAndTradeDateNoPage(orgs, startDate, endDate,
					CommonEnum.BillBalance.zp.getValue());
		}
		if (!StringUtil.isNullOrEmpty(list)) {
			Map<Integer, String> maptwo = CommonEnum.BillBalance.asMap();
			List<MetaData> metaList = metaDataService.findAllMetaData();
			for (TradeCheckFollow tradeCheckFollow : list) {
				tradeCheckFollow.setCheckStateValue(maptwo.get(tradeCheckFollow.getCheckState()));
				for (MetaData m : metaList) {
					if (tradeCheckFollow.getBillSource() != null
							&& tradeCheckFollow.getBillSource().equals(m.getValue())) {
						tradeCheckFollow.setBillSource(m.getName());
					}

					if (tradeCheckFollow.getPatType() != null && tradeCheckFollow.getPatType().equals(m.getValue())) {
						tradeCheckFollow.setPatType(m.getName());
					}

					if (tradeCheckFollow.getPayName() != null && tradeCheckFollow.getPayName().equals(m.getValue())) {
						tradeCheckFollow.setPayName(m.getName());
					}

					if (tradeCheckFollow.getTradeName() != null
							&& tradeCheckFollow.getTradeName().equals(m.getValue())) {
						tradeCheckFollow.setTradeName(m.getName());
					}
					String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + ","
							+ CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
					if (tradeCheckFollow.getCheckState() != null
							&& thirdCheck.contains(tradeCheckFollow.getCheckState().toString())) {
						tradeCheckFollow.setExceptionType("长款（渠道多出）");
					} else {
						tradeCheckFollow.setExceptionType("短款（HIS多出）");
					}
				}
				if (orgList.size() > 0) {
					for (Organization o : orgList) {
						if (tradeCheckFollow.getOrgNo() != null && tradeCheckFollow.getOrgNo().equals(o.getCode())) {
							tradeCheckFollow.setOrgNo(o.getName());
						}
					}
				}
			}
		}
		return list;
	}

	public String concatOrgNoSql(String orgNo) {
		List<Organization> orgList = null;
		if (null != orgNo) {
			orgList = organizationService.findByParentCode(orgNo);
		}
		String strOrg = "\'" + orgNo + "\'";
		if (orgList != null) {
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}

		}
		return strOrg;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseResult getExceptionTradeDetail(String recHisId, String recThirdId, String businessNo, String orgNo,
			String tradeTime) {
		ResponseResult result = ResponseResult.success();
		HashMap<String, Object> map = new HashMap<>();

		// 查询此异常账单的处理（平账|追回）记录
		TradeCheckFollowDeal deal = null;
		if (StringUtils.isNotBlank(businessNo)) {
			deal = tradeCheckFollowDealDao.findFirstByPayFlowNoAndOrgCodeAndTradeDatetimeOrderByCreatedDateDesc(businessNo, orgNo,
					DateUtil.formatStringDate("yyyy-MM-dd", tradeTime));
			if (deal != null) {
				map.put("dealDetail", deal);
			}
		}

		// 查询现金渠道账单
		List<RecCash> cashBills = new ArrayList<>();
		// 流水号不为空就按照流水号查询
		RecCash cash = null;
		if (StringUtils.isNotEmpty(businessNo)) {
			cash = cashDao.findByPayFlowNo(businessNo);

		} else if (StringUtils.isNotEmpty(recThirdId)) {
			cash = cashDao.findOne(Long.valueOf(recThirdId));
		}
		if (cash != null) {
			cashBills.add(cash);
			map.put("thirdOrder", cashBills);
		}

		// HIS订单信息优先获取本地HIS表记录进行展示,本地不存在再实时获取
		String conditionSql = "";
		if (StringUtils.isNotEmpty(businessNo)) {
			conditionSql += String.format(" And t.`Pay_Flow_No`='%s'  AND t.`org_no`='%s' ", businessNo, orgNo);
		} else if (StringUtils.isNotEmpty(recHisId)) {
			conditionSql += String.format(" And t.`id`='%s' ", recHisId);
		}
		String sql = String.format("SELECT "
				+ " t.`Cust_Name` patientName, t.pat_type patientType, t.`his_flow_no` hisNo,"
				+ " t.`Pay_Flow_No` payNo, t.`pay_type` payType, t.`Order_State` orderState,"
				+ " DATE_FORMAT(t.`Trade_datatime`, '%%Y-%%m-%%d %%h:%%i:%%s') tradeTime, t.`Pay_Amount` tradeAmount, t.mz_code mzCode,t.invoice_no invoiceNo,"
				+ " IF(pat_type='mz',mz_code,pat_code) patientNo, shop_flow_no shopFlowNo, pay_Shop_No payShopNo, terminal_no terminalNo, reference_num referenceNum "
				+ " FROM t_rec_histransactionflow t WHERE 1=1 %s", conditionSql);

		List<Map<String, Object>> hisOrders = null;
		if (StringUtils.isNotBlank(conditionSql)) {
			hisOrders = super.queryList(sql, null, null);
		}
		map.put("hisOrder", hisOrders);
		return result.data(map);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseResult deal(Long id, String description, MultipartFile file, String userName) {
		// 上传图片
		String fileLocation = "";
		if (file != null) {
			fileLocation = saveImage(file);
		}

		TradeCheckFollow follow = tradeCheckFollowDao.getOne(id);
		TradeCheckFollowDeal followDeal = new TradeCheckFollowDeal();
		followDeal.setUserName(userName);
		followDeal.setPayFlowNo(follow.getBusinessNo());
		followDeal.setExceptionState(String.valueOf(follow.getCheckState()));
		followDeal.setDescription(description);
		followDeal.setCreatedDate(new Date());
		followDeal.setDealAmount(follow.getTradeAmount().abs());
		followDeal.setFileLocation(fileLocation);
		followDeal.setOrgCode(follow.getOrgNo());
		followDeal.setTradeDatetime(DateUtil.transferDateToDateFormat("yyyy-MM-dd", follow.getTradeTime()));
		// 保存处理单
		tradeCheckFollowDealDao.save(followDeal);

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
}
