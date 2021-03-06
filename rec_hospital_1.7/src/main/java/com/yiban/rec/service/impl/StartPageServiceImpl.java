package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.dao.UserRoleDao;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.Role;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.FollowSummaryDao;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.dao.RecLogDetailsDao;
import com.yiban.rec.domain.FollowSummary;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.FollowingRecService;
import com.yiban.rec.service.HisTransactionFlowService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.StartPageService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;

@Service
public class StartPageServiceImpl extends BaseOprService implements StartPageService{
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private UserRoleDao userRoleDao;
	
	@Autowired
	private FollowingRecService followingRecService;
	
	@Autowired
	private RecLogDao recLogDao;
	
	@Autowired
	private RecLogDetailsDao recLogDetailsDao;
//	
//	@Autowired
//	private OrderUploadDao orderUploadDao;
	
	@Autowired
	private HisTransactionFlowService hisTransactionFlowService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private FollowSummaryDao followSummaryDao;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	/**
	 * ????????????
	 * @param orgNo
	 * @return
	 * String
	 */
	private String combinationOrgCodeSql(String orgNo){
		StringBuilder sql = new StringBuilder();
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		sql.append("'" + orgNo + "'" + ",");
		if(orgList != null && orgList.size() > 0){
			for (Organization organization : orgList) {
				sql.append("'" + organization.getCode() + "'" + ",");
			}
		}
		sql.deleteCharAt(sql.length()-1);
		return sql.toString();
	}
	
	private String combinationPayTypeSql(String payType){
		StringBuilder sql = new StringBuilder();
		if(StringUtils.isBlank(payType)) {
		    return "";
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())){// ??????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())){// ?????????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())){// ??????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())){// ??????
			sql.append("'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())){// ??????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue())){// ????????????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue() + "'" + ",");
		}
		if(payType.contains(EnumTypeOfInt.PAY_TYPE_WJYZT.getValue())){//?????????
			sql.append("'" + EnumTypeOfInt.PAY_TYPE_WJYZT.getValue() + "'" + ",");
		}
        if(payType.contains(EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue())){//?????????
        	sql.append("'" + EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue() + "'" + ",");
        }
		sql.deleteCharAt(sql.length()-1);
		return sql.toString();
	}

	@Override
	public Map<String, Object> getRecInfo(String orgNo, User user) {
	    AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
	    String recType = hConfig.getRecType();
	    String startDate = DateUtil.getSpecifiedDayBeforeDay(new Date(),7);
        String endDate = DateUtil.getSpecifiedDayBefore(new Date());
		// ??????????????????
		String payTypeSql = combinationPayTypeSql(recType);
		String orgCodeSql = combinationOrgCodeSql(orgNo);
		Map<String, Object> resultMap = new HashMap<>();
		final CountDownLatch countDownLatch = new CountDownLatch(3);
		
		// ???????????????????????????
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long payTypeOf7DayBeginTime = System.currentTimeMillis();
                    List<Map<String, Object>> payTypeOf7Day = 
                            hisTransactionFlowService.getFollowCountOfPayTypeOf7Day(startDate, 
                                    endDate, payTypeSql, orgCodeSql);
                    resultMap.put("payTypeOf7Day", payTypeOf7Day);
                    Long payTypeOf7DayEndTime = System.currentTimeMillis();
                    logger.info("??????????????????????????????????????? " + (payTypeOf7DayEndTime-payTypeOf7DayBeginTime) + " ??????");
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
		
		// ?????????????????????
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long hisBussinessCountOf7DayBeginTime = System.currentTimeMillis();
                    List<Map<String, Object>> hisBussinessCountOf7Day = 
                            hisTransactionFlowService.getFollowCountOfHisBusinessOf7Day(startDate, endDate, payTypeSql, orgCodeSql);
                    resultMap.put("hisBussinessCountOf7Day", hisBussinessCountOf7Day);
                    Long hisBussinessCountOf7DayEndTime = System.currentTimeMillis();
                    logger.info("????????????????????????????????? " + (hisBussinessCountOf7DayEndTime-hisBussinessCountOf7DayBeginTime) + " ??????");
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Long orderCountBeginTime = System.currentTimeMillis();
                    // ????????????
                    Map<String, Long> dealFollowCount = getDealFollowCount(orgNo,user);
                    resultMap.put("dealFollowCount", dealFollowCount);
                    Long orderCountEndTime = System.currentTimeMillis();
                    logger.info("???????????????????????? " + (orderCountEndTime-orderCountBeginTime) + " ??????");
                } finally {
                    countDownLatch.countDown();
                }
            }
        }).start();
		try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("???????????????????????????????????????????????????: ",e);
        }
		return resultMap;
	}
	
	@Override
	public Map<String, Object> getRecInfoByDay(String date, String orgNo) {
	    Long beginTime = System.currentTimeMillis();
	    Map<String, Object> resultMap = new HashMap<>();
	    final String orderStatePay="0156";
	    final String orderStateRefund="0256";
	    final String his="his";
	    final String third="third";
	    // ??????????????????
	    Double thirdSumAmount = 0d;
	    // HIS????????????
	    Double hisSumAmount = 0d;
	    // HIS????????????(?????????)
	    Double hisSettlementSumAmount = 0d;
	    // ??????????????????
	    Map<String, Double> payTypeSumMap = new HashMap<>();
	    
	    // ???????????????
	    Set<String> orgNoSets = getAllChildrenOrgs(orgNo);
	    // ????????????
	    List<FollowSummary> list = followSummaryDao.findByOrgNoInAndTradeDate(orgNoSets, date);
	    // ??????????????????
	    final List<String> elecTronicTypes = Arrays.asList(new String[] {
	            EnumTypeOfInt.PAY_TYPE_WECHAT.getValue(),
	            EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue(),
	            EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue(),
	            EnumTypeOfInt.PAY_TYPE_HEALTH.getValue(),
	            EnumTypeOfInt.PAY_TYPE_BANK.getValue()
	    });
	    for (FollowSummary f : list) {
	        String recPayType = f.getRecPayType();
	        if(!elecTronicTypes.contains(recPayType)) {
	            continue;
	        }
	        // ????????????????????????order_state:0156-?????????0256-??????????????????????????????
            String orderState = f.getOrderState();
            if(!StringUtils.equalsIgnoreCase(orderStatePay, orderState) &&
                    !StringUtils.equalsIgnoreCase(orderStateRefund, orderState)) {
                continue;
            }
            
            // ????????????
            double dPayAmount = 0d;
            BigDecimal payAmount =  f.getPayAmount();
            if(null != payAmount) {
                dPayAmount = payAmount.doubleValue();
                // ?????????????????????????????????
                if(StringUtils.equalsIgnoreCase(orderState, orderStateRefund)) {
                    dPayAmount = 0 - Math.abs(dPayAmount);
                }
            }
            
            // ?????????????????????
            double dSettlementAmount = 0d;
            BigDecimal settlementAmount =  f.getSettlementAmount();
            if(null != settlementAmount) {
                dSettlementAmount = settlementAmount.doubleValue();
                // ?????????????????????????????????
                if(StringUtils.equalsIgnoreCase(orderState, orderStateRefund)) {
                    dSettlementAmount = 0 - Math.abs(dSettlementAmount);
                }
            }

            // dataSource-????????????: platform-?????? ,his-his,third-??????
            String dataSource = f.getDataSource();
            if(StringUtils.equalsIgnoreCase(dataSource, his)) {
                hisSumAmount += dPayAmount;
                hisSettlementSumAmount += dSettlementAmount;
            }else if(StringUtils.equalsIgnoreCase(dataSource, third)) {
                thirdSumAmount += dPayAmount;
                // ????????????????????????
                if(payTypeSumMap.containsKey(recPayType)) {
                    Double sum = payTypeSumMap.get(recPayType);
                    sum += dPayAmount;
                    payTypeSumMap.put(recPayType, sum);
                }else {
                    payTypeSumMap.put(recPayType, dPayAmount);
                }
            }
        }
	    
	    // ??????????????????
	    thirdSumAmount = formateDouble(thirdSumAmount);
	    hisSumAmount = formateDouble(hisSumAmount);
	    hisSettlementSumAmount = formateDouble(hisSettlementSumAmount);
	    payTypeSumMap.forEach((key, value) -> {
	        payTypeSumMap.put(key, formateDouble(value));
	    });
	    
	    // ??????????????????
	    resultMap.put("thirdSumAmount", thirdSumAmount);
	    resultMap.put("hisSumAmount", hisSumAmount);
	    resultMap.put("hisSettlementSumAmount", hisSettlementSumAmount);
	    resultMap.put("payTypeSumMap", payTypeSumMap);
	    
	    Set<String> orgSets = getAllChildrenOrgs(orgNo);
	    // ??????
	    Double longTradeAmount = 0d;
	    Integer longTradeAccount = 0;
	    // ??????
	    Double shortTradeAmount = 0d;
	    Integer shortTradeAccount = 0;
	    
	    // check_state:2-?????????3-??????
	    final String checkStateLong = "3";
	    final String checkStateShort = "2";
	    StringBuffer sb = new StringBuffer();
	    sb.append("SELECT COUNT(1) cnt, SUM(Trade_Amount) amountSum, check_state checState");
	    sb.append(" FROM t_trade_check_follow");
	    sb.append(" WHERE 1=1");
	    sb.append(" AND trade_date = '" + date + "'");
	    if(!orgSets.isEmpty()) {
	        sb.append(" AND org_no IN(");
	        for (String org : orgSets) {
	            sb.append("'"+ org +"'");
                sb.append(",");
            }
	        sb.delete(sb.length()-1, sb.length());
	        sb.append(" )");
	    }
	    sb.append(" AND business_no NOT IN(");
	    sb.append(" SELECT Pay_Flow_No business_no FROM t_trade_check_follow_deal WHERE exception_state <> '11'");
	    sb.append(" UNION");
	    sb.append(" SELECT Payment_Request_Flow business_no FROM t_exception_handling_record WHERE 1=1");
	    sb.append(" AND state= '3'");
	    sb.append(" AND (father_id IS NULL OR father_id = 0)");
	    sb.append(")");
	    sb.append(" GROUP BY check_state");
	    List<Map<String, Object>> ycList = super.handleNativeSql(sb.toString(), new String[] {"cnt","amountSum","checState"});
        if(null != ycList && !ycList.isEmpty()) {
            for (Map<String, Object> map : ycList) {
                // check_state:2-?????????3-??????
                String checState = String.valueOf(map.get("checState"));
                String cnt = String.valueOf(map.get("cnt"));
                String amountSum = String.valueOf(map.get("amountSum"));
                if(StringUtils.equalsIgnoreCase(checState, checkStateLong)) {
                    longTradeAccount = Integer.valueOf(cnt);
                    longTradeAmount = Double.valueOf(amountSum);
                }else if(StringUtils.equalsIgnoreCase(checState, checkStateShort)) {
                    shortTradeAccount = Integer.valueOf(cnt);
                    shortTradeAmount = Double.valueOf(amountSum);
                }else {
                    logger.info("???????????????????????????{}", checState);
                    continue;
                }
            }
        }
        // ????????????
        resultMap.put("hisTradeAccount", shortTradeAccount);
        resultMap.put("hisTradeAmount", formateDouble(shortTradeAmount));
        resultMap.put("thridTradeAccount", longTradeAccount);
        resultMap.put("thridTradeAmount", formateDouble(longTradeAmount));
        // ???????????????01??????????????????02?????????
        final String yillParseLogType = "01";
        // ??????????????????
        List<RecLogDetails> billParseList = 
                recLogDetailsDao.findByOrderDateAndLogTypeAndOrgCodeIn(date, yillParseLogType, orgSets);
        resultMap.put("billParseList", billParseList);
        Long endTime = System.currentTimeMillis();
        logger.info("?????????????????????????????? " + (endTime-beginTime) + " ??????");
		return resultMap;
	}
	
	/**
	 * ?????????????????????
	 * @param orgNo
	 * @return
	 * Set<String>
	 */
	private Set<String> getAllChildrenOrgs(String orgNo){
	    Set<String> sets = new HashSet<>();
	    List<Organization> orgList = organizationService.findByParentCode(orgNo);
	    for (Organization o : orgList) {
	        sets.add(o.getCode());
        }
	    sets.add(orgNo);
	    return sets;
	}
	
	/**
	 * ??????????????????
	 * @param d
	 * @return
	 * double
	 */
	private double formateDouble(double d) {
	    BigDecimal bg = new BigDecimal(d);
	    return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	@Override
	public List<RecLog> getExceptionDate(String startDate, String endDate, AppRuntimeConfig hConfig) {
		
		String orgCode =propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		// ????????????3????????????????????????
		return recLogDao.findByOrderDateBetweenAndOrgCode(startDate, endDate, orgCode );
	}
	
	@Override
	public List<RecLogDetails> getExceptionInfo(String startDate, String endDate, AppRuntimeConfig hConfig) {
		// ??????????????????????????????
		String orgCode =propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		return recLogDetailsDao.findByOrderDateAndOrgCodeAndLogType(endDate, orgCode,"01");
	}
	
	/**
	 * ?????????????????????????????????????????????????????????????????????
	 * @param orgNo
	 * @param user
	 * @return
	 * Map<String,Long>
	 */
	private Map<String, Long> getDealFollowCount(String orgNo,User user) {
	    Map<String, Long> map = new HashMap<>();
	    final String cwzr = "????????????";
	    final String yygly = "???????????????";
//	    final String count1State = "1809300";
        Set<String> orgSets = getAllChildrenOrgs(orgNo);
	    // ????????????????????????
	    Long count1 = 0l;
	    // ?????????????????????
	    Long count2 = 0l;
	    // ??????????????????
	    Long count3 = 0l;
	    // ????????????????????????
	    Long userId = user.getId();
	    String loginName = user.getLoginName();
        List<Role> roles = userRoleDao.findAllRolesByUserId(userId);
        List<String> roleNames = new ArrayList<>();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(1) cnt FROM t_exception_handling_record");
        sb.append(" WHERE 1=1");
        sb.append(" AND state = '1'");
        sb.append(" AND Handle_Date_Time <= CURDATE()");
        sb.append(" AND DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= DATE(Handle_Date_Time)");
        sb.append(" AND (father_id = 0 OR father_id IS NULL)");
        if(!roleNames.contains(cwzr) && !roleNames.contains(yygly)) {
            sb.append(" AND user_name = '"+ loginName +"'");
        }
        if(!orgSets.isEmpty()) {
            sb.append(" AND org_no in (");
            for (String org : orgSets) {
                sb.append("'"+org+"'");
                sb.append(",");
            }
            sb.delete(sb.length()-1, sb.length());
            sb.append(")");
        }
        String sqlCount = sb.toString();
        logger.info("??????????????????:{}",sqlCount);
        List<Object> cntList = super.handleNativeSql4SingleCol(sqlCount);
        if(null != cntList && !cntList.isEmpty()) {
            Object cntObj = cntList.get(0);
            if(null != cntObj) {
                count2 = Long.valueOf(String.valueOf(cntObj));
            }
        }
        sqlCount = sqlCount.replace("state = '1'", "state = '2'");
        cntList = super.handleNativeSql4SingleCol(sqlCount);
        if(null != cntList && !cntList.isEmpty()) {
            Object cntObj = cntList.get(0);
            if(null != cntObj) {
                count3 = Long.valueOf(String.valueOf(cntObj));
            }
        }
        
        sb = new StringBuffer();
        sb.append("SELECT COUNT(1) cnt FROM t_order_upload");
        sb.append(" WHERE 1=1");
        sb.append(" AND ((order_state='1809300' AND refund_order_state IS NULL ) OR refund_order_state= '1809300') ");
        sb.append(" AND trade_date_time <= CURDATE()");
        sb.append(" AND DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= DATE(trade_date_time)");
        sb.append(" AND Order_State = '1809300'");
        if(!orgSets.isEmpty()) {
            sb.append(" AND org_code IN (");
            for (String org : orgSets) {
                sb.append("'"+org+"'");
                sb.append(",");
            }
            sb.delete(sb.length()-1, sb.length());
            sb.append(")");
        }
        sqlCount = sb.toString();
        logger.info("???????????????????????????{}", sqlCount);
        cntList = super.handleNativeSql4SingleCol(sqlCount);
        if(null != cntList && !cntList.isEmpty()) {
            Object cntObj = cntList.get(0);
            if(null != cntObj) {
                count1 = Long.valueOf(String.valueOf(cntObj));
            }
        }
        // t_order_upload??????????????????:????????????-1809300
//        count1 = orderUploadDao.countByOrgCodeInAndOrderStateAndRefundOrderStateIsNull(orgSets, count1State);
	    map.put("count1", count1);
	    map.put("count2", count2);
	    map.put("count3", count3);
		return map;
	}
	
	@SuppressWarnings("unused")
    private List<Map<String, Object>> getRecAlldetail(String startDate, String endDate, AppRuntimeConfig hConfig,String searchType){
		List<Map<String, Object>> recAlldetail = null;
		if("day".equals(searchType)){
			recAlldetail = getFollowRecMapDetailOf7Day(startDate, endDate, hConfig);
		}else{
			recAlldetail = followingRecService.getFollowRecMapDetail(startDate, endDate, hConfig);
		}
		
		if(null != recAlldetail && recAlldetail.size() > 0){
			List<MetaData> metaList = metaDataService.findAllMetaData();
			for (Map<String, Object> map : recAlldetail) {
				if(null != map.get("rec_pay_type")){
					StringBuilder name = new StringBuilder();
					String billSource = (String) map.get("bill_source");
					String recPayType = (String) map.get("rec_pay_type");
					for(MetaData m : metaList){
						
						if(billSource != null && billSource.equals(m.getValue())){
							name.append(m.getName());
						}
						if( recPayType.equals(m.getValue())){
							if(name.length()>0){
								name.append(" - ");
							}
							name.append(m.getName());
						}
					}
					map.put("name", name.toString());
				}
			}
		}
		
		return recAlldetail;
	}
	
	private List<Map<String, Object>> getFollowRecMapDetailOf7Day(String startDate, String endDate, AppRuntimeConfig hConfig){
		
		List<Map<String, Object>> searhResult = followingRecService.getFollowRecMapDetailOf7Day(startDate, endDate, hConfig);
		List<Map<String, Object>> recAlldetail = getCompletionDataOfDate(startDate, endDate,  searhResult);
		return recAlldetail;
		
	}
	
	private List<Map<String, Object>> getCompletionDataOfDate(String startDate, String endDate, List<Map<String, Object>> searhResult){
		List<String> allDates = getAllDateBeforeStartDateAndEndDate(startDate,endDate);
		for (Map<String, Object> map : searhResult) {
			String date = (String) map.get("Trade_Date");
			if(allDates.contains(date)) {
				allDates.remove(date);
			}
		}
		for(String date : allDates){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Trade_Date", date);
			map.put("pay_amount", 0);
			searhResult.add(map);
		}
		return searhResult;
	}
	
	private List<String> getAllDateBeforeStartDateAndEndDate(String startDate, String endDate){
		
		List<String> dates = new ArrayList<String>();
		
		int d = 0;
		while (true) {
			String date = DateUtil.getSpecifiedDayAfter(startDate, d);
			dates.add(date);
			d++;
			if (date.equals(endDate)) {
				break;
			}
		}
		return dates;
	}
}
