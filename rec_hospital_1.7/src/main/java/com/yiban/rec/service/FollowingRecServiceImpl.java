package com.yiban.rec.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.dao.baseinfo.ShopInfoDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.service.impl.TradeCheckServiceImpl;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONObject;

@Service
public class FollowingRecServiceImpl extends BaseOprService implements FollowingRecService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeCheckServiceImpl.class);
	
	@Autowired
	private TradeCheckFollowDao tradeCheckFollowDao;
	
	@Autowired
	private ShopInfoDao shopInfoDao;
	
	@Autowired
	private ExcepHandingRecordDao excepHandingRecordDao;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private TradeCheckFollowDealDao tradeCheckFollowDealDao;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@Override
	public List<FollowRecResult> getFollowRecMap(String startDate,String endDate,AppRuntimeConfig hConfig) {
		
		//??????????????????????????????
		String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		List<Map<String, Object>> listMap = getFollowCount(startDate, endDate,payTypeSql,orgCode );
		
		//???????????????
		List<FollowRecResult> followList = new ArrayList<FollowRecResult>();
		FollowRecResult f = new FollowRecResult();
		
		//??????
		if(null != listMap){
			for(Map<String, Object> map : listMap){
				if("his".equals((String)map.get("data_source"))){
					f.setHisAllAmount((BigDecimal)map.get("pay_amount"));
					continue;
				}
				if("third".equals((String)map.get("data_source"))){
					f.setPayAllAmount((BigDecimal)map.get("pay_amount"));
					continue;
				}
				if("settlement".equals((String)map.get("data_source"))){
					f.setSettlementAmount((BigDecimal)map.get("pay_amount"));
					continue;
				}
			}
			if(null != f.getHisAllAmount()&& f.getPayAllAmount() != null){
				f.setTradeDiffAmount(f.getHisAllAmount().subtract(f.getPayAllAmount()));
			}
			f.setOrgNo(orgCode);
			f.setTradeDate(startDate + " - " + endDate);
		}
		
		followList.add(f);
		return followList;
	}
	
	@SuppressWarnings("unused")
    private StringBuilder getSplicingSql(AppRuntimeConfig hConfig){
		StringBuilder searchSql = new StringBuilder();
//		if("1".equals(hConfig.getIsBillsSources())){//??????????????????
			searchSql.append(" ,bill_source ");
//		}
//		if("1".equals(hConfig.getIsOutpatient())){//????????????/????????????
			searchSql.append(" ,pat_type ");
//		}
		return searchSql ;
	}
	
	@SuppressWarnings("unchecked")
    public List<Map<String, Object>> getFollowCount(String startDate, String endDate,String payTypeSql,String orgNo) {
		payTypeSql=payTypeSql.replace("0049", "");
		final String sql = String.format(
				" SELECT data_source,SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary  WHERE data_source = 'his' " + 
				" AND org_no = '%s'"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' "+
				" AND pay_type in (%s)" +
				" UNION"+
				" SELECT data_source,SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary  WHERE data_source = 'third' " +
				" AND org_no = '%s'"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' "+
				" AND rec_pay_type in (%s)" +
				" UNION"+
				" SELECT 'settlement' as data_source,SUM(CASE order_state WHEN '0156' THEN settlement_amount ELSE -ABS(settlement_amount) END) pay_amount FROM t_follow_summary  WHERE data_source = 'his' " +
				" AND org_no = '%s' "+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' "+
				" AND pay_type in (%s)" 
				,orgNo,startDate,endDate,payTypeSql,orgNo,startDate,endDate,payTypeSql,orgNo,startDate,endDate,payTypeSql);
		LOGGER.info(" getFollowCount sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFollowCountByType(String startDate, String endDate,String payTypeSql,String orgNo,String billSource) {
		final String sql = String.format(
				"SELECT %s rec_pay_type,SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary " + 
				" WHERE data_source = 'third' "+
				" AND org_no = '%s'"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' " + 
				" AND rec_pay_type in (%s)" +
				" GROUP BY %s rec_pay_type "
				,billSource,orgNo,startDate,endDate,payTypeSql,billSource);
		LOGGER.info(" getFollowCountByType sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
    private List<Map<String, Object>> getFollowCountOfDayByType(String startDate, String endDate,String payTypeSql,String orgNo,String billSource) {
		final String sql = String.format(
				"SELECT %s rec_pay_type,Trade_Date , SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary " + 
				" WHERE data_source = 'third' "+
				" AND org_no = '%s'"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' " + 
				" AND rec_pay_type in (%s)" +
				" GROUP BY %s rec_pay_type ,Trade_Date "
				,billSource,orgNo,startDate,endDate,payTypeSql,billSource);
		LOGGER.info(" getFollowCountByType sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfHis(String startDate, String endDate,String payTypeSql,String orgNo) {
		final String sql = String.format(
				" SELECT order_state, SUM(ABS(pay_amount)) pay_amount ,SUM(pay_acount) pay_acount FROM t_follow_summary  WHERE pay_amount <> 0 AND data_source = 'his' " + 
				" AND org_no = '%s'"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' "+
				" AND pay_type in (%s) " + 
				" group by order_state "
				,orgNo,startDate,endDate,payTypeSql);
		LOGGER.info(" getFollowCountOfHis sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getFollowCountOfExp(String startDate, String endDate,String orgCodeSql) {
		final String sql = String.format(
				" SELECT " + 
				" SUM(CASE WHEN check_state='"+CommonEnum.BillBalance.THIRDDC.getValue()+"' OR check_state='"+CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()+"' THEN ABS(Trade_Amount) ELSE 0 END) thrid_trade_amount ,  " +
				" SUM(CASE WHEN check_state='"+CommonEnum.BillBalance.THIRDDC.getValue()+"' OR check_state='"+CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()+"' THEN 1 ELSE 0 END) thrid_trade_acount ,  " +
				" SUM(CASE WHEN check_state='"+CommonEnum.BillBalance.HISDC.getValue()+"' OR check_state='"+CommonEnum.BillBalance.HEALTHCAREHIS.getValue()+"' THEN ABS(Trade_Amount) ELSE 0 END) his_trade_amount ,   " +
				" SUM(CASE WHEN check_state='"+CommonEnum.BillBalance.HISDC.getValue()+"' OR check_state='"+CommonEnum.BillBalance.HEALTHCAREHIS.getValue()+"' THEN 1 ELSE 0 END) his_trade_acount   " +
				" FROM t_trade_check_follow  " +
				" where org_no IN (%s)"+
				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' AND Pay_Name IN('0149', '0249', '0349', '1649')"
				,orgCodeSql,startDate,endDate);
		LOGGER.info(" getFollowCountOfExp sql============" + sql) ;
		return super.queryList(sql, null, null);
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Map<String, Object>> getFollowCountOfRealMoney(String startDate, String endDate,String orgNo) {
//		final String sql = String.format(
//				"SELECT rec_pay_type,SUM(CASE order_state WHEN '0156' THEN pay_amount ELSE -ABS(pay_amount) END) pay_amount FROM t_follow_summary " + 
//				" WHERE data_source = 'third' "+
//				" AND org_no = '%s' "+
//				" AND Trade_Date >= '%s' AND Trade_Date <= '%s' "
//				,orgNo,startDate,endDate);
//		LOGGER.info(" getFollowCountOfBusiness sql============" + sql) ;
//		return super.queryList(sql, null, null);
//	}
	
	public Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable){
		List<Organization> orgList = organizationService.findByParentCode(vo.getOrgNo());
		String orgNo = vo.getOrgNo();
		String patType = vo.getPatType();
		String billSource = vo.getBillSource();
		String correction = vo.getCorrection();
		String startDate = vo.getStartDate();
		String endDate = vo.getEndDate();
		String dataSourceType = vo.getDataSourceType();
		String[] orgs = null;
		
		StringBuffer sql=new StringBuffer();
		if(orgList != null &&orgList.size() > 0){
			orgs = new String[orgList.size()+1];
			orgs[0] = orgNo;
			sql.append(orgNo);
			for (int i=0; i<orgList.size(); i++) {
				orgs[i+1] = orgList.get(i).getCode();
				sql.append(","+orgList.get(i).getCode());
			}
		}else{
			orgs = new String[1];
			orgs[0] = orgNo;
			sql.append(orgNo);
		}
		Page<TradeCheckFollow> tcf = null;
		if(dataSourceType != null){
			String checkStates = "";
			String correctionSql = "";
			if("his".equals(dataSourceType)){
				checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
			}else if("third".equals(dataSourceType)){
				checkStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
			}else{
				checkStates = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue()+ "," +CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
			}
			if("1".equals(correction)) {
				correctionSql = " GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1" ;
			}
			tcf = findByOrgNoAndTradeDateAndCheckState(sql.toString(), startDate, endDate,checkStates,correctionSql,pageable);
		}else{
			//TODO ???????????????????????????????????? ????????????
			if(patType != null && !patType.trim().equals("") && (patType.equals(EnumTypeOfInt.PAT_TYPE_ZY.getValue()) || patType.equals(EnumTypeOfInt.PAT_TYPE_MZ.getValue()))){
				if(billSource != null && !billSource.trim().equals("")){
					if("1".equals(correction)) {
						tcf = findByOrgNoAndTradeDateAndPatTypeAndBillSourceCorrection(sql.toString(), startDate, endDate,CommonEnum.BillBalance.zp.getValue(),patType,billSource,pageable);
					}else {
						tcf = tradeCheckFollowDao.findByOrgNoAndTradeDateAndPatTypeAndBillSource(orgs, startDate, endDate,CommonEnum.BillBalance.zp.getValue(),patType,billSource,pageable);
					}
				}else{
					if("1".equals(correction)) {
						tcf = findByOrgNoAndTradeDateAndPatTypeCorrection(sql.toString(), startDate, endDate,CommonEnum.BillBalance.zp.getValue(),patType,pageable);
					}else {
						tcf = tradeCheckFollowDao.findByOrgNoAndTradeDateAndPatType(orgs, startDate, endDate,CommonEnum.BillBalance.zp.getValue(),patType,pageable);
					}
				}
			}else{
				if(billSource != null && !billSource.trim().equals("")){
					if("1".equals(correction)) {
						tcf = findByOrgNoAndTradeDateAndBillSourceCorrection(sql.toString(), startDate, endDate,CommonEnum.BillBalance.zp.getValue(),billSource,pageable);
					}else {
						tcf = tradeCheckFollowDao.findByOrgNoAndTradeDateAndBillSource(orgs, startDate, endDate,CommonEnum.BillBalance.zp.getValue(),billSource,pageable);
					}
				}else{
					if("1".equals(correction)) {
						tcf = findByOrgNoAndTradeDateCorrection(sql.toString(), startDate, endDate,CommonEnum.BillBalance.zp.getValue(),pageable);
					}else {
						tcf = tradeCheckFollowDao.findByOrgNoAndTradeDate(orgs, startDate, endDate,CommonEnum.BillBalance.zp.getValue(),pageable);
					}
				}
			}
		}
		List<TradeCheckFollow> list = tcf.getContent();
		
		//??????????????????
		if(!StringUtil.isNullOrEmpty(list)){
			for(TradeCheckFollow tradeCheckFollow : list){
				String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
				String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
				if(tradeCheckFollow.getCheckState() != null && thirdCheck.contains(tradeCheckFollow.getCheckState().toString())){
					tradeCheckFollow.setExceptionType("????????????????????????");
				}else if(tradeCheckFollow.getCheckState() != null && hisCheck.contains(tradeCheckFollow.getCheckState().toString())){
					tradeCheckFollow.setExceptionType("?????????HIS?????????"); 
				}
			}
		}
		
		//???????????????????????????
		handDealFollow(list);
		
		//???????????????????????????
		handExceptionFollow(list);
		
		//???code ???????????????
		if(!StringUtil.isNullOrEmpty(list)){
			Map<Integer,String> maptwo = CommonEnum.BillBalance.asMap();
			for(TradeCheckFollow tradeCheckFollow : list){
				tradeCheckFollow.setCheckStateValue(maptwo.get(tradeCheckFollow.getCheckState()));
			}
		}
		return tcf;
	}
	
	//????????????????????????
	private void handExceptionFollow(List<TradeCheckFollow> list){
		List<ExcepHandingRecord> excepHandingRecordList =  excepHandingRecordDao.findAll();
		if(excepHandingRecordList != null && list != null){
			for (int j = list.size()-1; j>=0;j--) {
				String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
				if(list.get(j).getCheckState() != null && thirdCheck.contains(list.get(j).getCheckState().toString())){
					for(int i = excepHandingRecordList.size()-1;i>=0;i--){
						if(list.get(j).getBusinessNo().equals(excepHandingRecordList.get(i).getPaymentRequestFlow())){
							list.get(j).setCheckState(CommonEnum.BillBalance.HANDLER.getValue());
							list.get(j).setDescription(excepHandingRecordList.get(i).getHandleRemark());
							list.get(j).setFileLocation(excepHandingRecordList.get(i).getImgUrl());
							list.get(j).setTradeName(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue());
						}
					}
				}
			}
		}
	}
	//?????????????????????????????????
	private void handDealFollow(List<TradeCheckFollow> list) {
		List<TradeCheckFollowDeal> dealFollowList =  tradeCheckFollowDealDao.findAll();
		if(dealFollowList != null && list != null){
			for (TradeCheckFollow tradeCheckFollow : list) {
				String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
				if(tradeCheckFollow.getCheckState() != null && hisCheck.contains(tradeCheckFollow.getCheckState().toString())){
					for(int i = dealFollowList.size()-1;i>=0;i--){
						if(tradeCheckFollow.getBusinessNo().equals(dealFollowList.get(i).getPayFlowNo())){
							tradeCheckFollow.setCheckState(CommonEnum.BillBalance.HANDLER.getValue());
							tradeCheckFollow.setDescription(dealFollowList.get(i).getDescription());
							tradeCheckFollow.setFileLocation(dealFollowList.get(i).getFileLocation());
						}
					}
				}
			}
		}
	}
	
	private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndCheckState(String orgNo,String startDate,String endDate,String checkStates,String correctionSql,Pageable pageable) {
		String sql="SELECT * FROM ("+
			       "SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' " + correctionSql
		  + " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND ( ISNULL(t.business_no) OR t.business_no = '' )" 
		  + " )s WHERE s.check_state in ("+checkStates+")" ;
		return handleNativeSql(sql,pageable,TradeCheckFollow.class);
	}
	private Page<TradeCheckFollow> findByOrgNoAndTradeDateCorrection(String orgNo,String startDate,String endDate,Integer checkState,Pageable pageable) {
		String sql="SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		return handleNativeSql(sql,pageable,TradeCheckFollow.class);
	}
	private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPatTypeAndBillSourceCorrection(String orgNo,String startDate,String endDate,Integer checkState, String patType ,String billSource,Pageable pageable) {
		String sql="SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" and pat_type='"+patType+"' and bill_source='"+billSource+"' AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		return handleNativeSql(sql,pageable,TradeCheckFollow.class);
	}
	private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndPatTypeCorrection(String orgNo,String startDate,String endDate,Integer checkState, String patType,Pageable pageable) {
		String sql="SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" and pat_type='"+patType+"' AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" and pat_type='"+patType+"' AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		return handleNativeSql(sql,pageable,TradeCheckFollow.class);
	}
	private Page<TradeCheckFollow> findByOrgNoAndTradeDateAndBillSourceCorrection(String orgNo,String startDate,String endDate,Integer checkState,String billSource,Pageable pageable) {
		String sql="SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" and bill_source='"+billSource+"' AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" and bill_source='"+billSource+"' AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		return handleNativeSql(sql,pageable,TradeCheckFollow.class);
	}
	
	private List<TradeCheckFollow> findByOrgNoAndTradeDateAndCorrectionNoPage(String orgNo,String startDate,String endDate,Integer checkState) {
		String sql="SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" AND t.business_no IS NOT NULL AND t.business_no <> '' GROUP BY t.business_no, abs(t.trade_amount) HAVING count(*) = 1"
				+ " UNION SELECT * FROM t_trade_check_follow t WHERE t.org_no IN ("+orgNo+") AND t.trade_date >= '"+startDate+"' AND t.trade_date <= '"+endDate+"' AND t.check_state <> "+checkState+" AND ( ISNULL(t.business_no) OR t.business_no = '' )";
		return handleNativeSql(sql,TradeCheckFollow.class);
	}
	
	
	public List<TradeCheckFollow> findByOrgNoAndTradeDateNoPage(String orgNo, String startDate,String endDate,String correction){
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		Organization organization = organizationService.findByCode(orgNo);
		orgList.add(organization);
		String[] orgs = null;
		StringBuffer sql=new StringBuffer();
		if(orgList != null &&orgList.size() > 0){
			orgs = new String[orgList.size()+1];
			orgs[0] = orgNo;
			sql.append(orgNo);
			for (int i=0; i<orgList.size(); i++) {
				orgs[i+1] = orgList.get(i).getCode();
				sql.append(","+orgList.get(i).getCode());
			}
		}else{
			orgs = new String[1];
			orgs[0] = orgNo;
			sql.append(orgNo);
		}
		List<TradeCheckFollow> list = null;
		if("1".equals(correction)){
			list = findByOrgNoAndTradeDateAndCorrectionNoPage(sql.toString(),startDate, endDate,CommonEnum.BillBalance.zp.getValue());
		}else{
			list = tradeCheckFollowDao.findByOrgNoAndTradeDateNoPage(orgs,startDate, endDate,CommonEnum.BillBalance.zp.getValue());
		}
		if(!StringUtil.isNullOrEmpty(list)){
			Map<Integer,String> maptwo = CommonEnum.BillBalance.asMap();
			List<MetaData> metaList = metaDataService.findAllMetaData();
			for(TradeCheckFollow tradeCheckFollow : list){
				tradeCheckFollow.setCheckStateValue(maptwo.get(tradeCheckFollow.getCheckState()));
				for(MetaData m : metaList){
					if(tradeCheckFollow.getBillSource()!= null && tradeCheckFollow.getBillSource().equals(m.getValue())){
						tradeCheckFollow.setBillSource(m.getName());
					}
					
					if(tradeCheckFollow.getPatType()!= null && tradeCheckFollow.getPatType().equals(m.getValue())){
						tradeCheckFollow.setPatType(m.getName());
					}
					
					if(tradeCheckFollow.getPayName()!= null && tradeCheckFollow.getPayName().equals(m.getValue())){
						tradeCheckFollow.setPayName(m.getName());
					}
					
					if(tradeCheckFollow.getTradeName()!= null && tradeCheckFollow.getTradeName().equals(m.getValue())){
						tradeCheckFollow.setTradeName(m.getName());
					}
					String thirdCheck = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
					if(tradeCheckFollow.getCheckState() != null && thirdCheck.contains(tradeCheckFollow.getCheckState().toString())){
						tradeCheckFollow.setExceptionType("????????????????????????");
					}else{
						tradeCheckFollow.setExceptionType("?????????HIS?????????"); 
					}
				}
				if(orgList.size()>0){
					for(Organization o :orgList){
						if(tradeCheckFollow.getOrgNo()!= null && tradeCheckFollow.getOrgNo().equals(o.getCode())){
							tradeCheckFollow.setOrgNo(o.getName());
						}
					}
				}
			}
			
		}
		
		return list;
	}
	
	@Transactional
	@Override
	public String checkRefund(Long id, User user) throws Exception {
			String resultStr = "";
		try {
			TradeCheckFollow tradeCheckFollow = tradeCheckFollowDao.findOne(id);
			ShopInfo shopInfo = shopInfoDao.findByOrgNoAndMetaDataPayId(tradeCheckFollow.getOrgNo(), tradeCheckFollow.getPayName());
			JSONObject jb = new JSONObject();
			jb.put("Trade_Code", EnumType.TRADE_CODE_REFUND.getValue());
			jb.put("Org_No", tradeCheckFollow.getOrgNo().toString());
			jb.put("Pay_Shop_No", shopInfo.getPayShopNo());
			jb.put("Pay_App_ID", shopInfo.getApplyId());
			jb.put("Pay_Source", EnumType.PAY_SOURCE_REFUND.getValue());
			if(tradeCheckFollow.getPayName()==EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()){
				jb.put("Pay_Type", EnumType.PAY_TYPE_WECHAT_REFUND.getValue());
			}else{
				jb.put("Pay_Type", EnumType.PAY_TYPE_ALIPAY_REFUND.getValue());
			}
			jb.put("Pay_Flow_No", CommonConstant.REFUND_FLAG+new Date().getTime());
			jb.put("Pay_Amount", tradeCheckFollow.getTradeAmount());
			jb.put("Pay_Round", tradeCheckFollow.getTradeAmount());
			jb.put("Device_No", "ZHZFPT");
			jb.put("Ori_Pay_Flow_No", tradeCheckFollow.getBusinessNo());
			jb.put("Chk", "");
			LOGGER.info("??????????????????????????????????????????====???"+jb.toString());
			System.out.println("??????????????????????????????????????????====???"+jb.toString());
			IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
			String result = iPaymentService.entrance(jb.toString());
			JSONObject jsonObject = JSONObject.fromObject(result);
			LOGGER.info("??????????????????????????????????????????====???"+result);
			System.out.println("??????????????????????????????????????????====???"+result);
			if(CommonConstant.TRADE_CODE_SUCCESS.equals(jsonObject.getString("Response_Code"))){
				tradeCheckFollow.setCheckState(CommonEnum.BillBalance.zp.getValue());
				tradeCheckFollowDao.save(tradeCheckFollow);
				refundSeting(tradeCheckFollow,"??????????????????-?????????");//?????????????????????
				resultStr = "????????????";
			}else{
				resultStr = "???????????????";
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultStr = "???????????????";
			LOGGER.error("???????????????");
		}
		return resultStr;
	}
	
	private void refundSeting(TradeCheckFollow tradeCheckFollow,String handleRemark){
		ExcepHandingRecord ehr = new ExcepHandingRecord();
		ehr.setOrgNo(tradeCheckFollow.getOrgNo());
		ehr.setPaymentRequestFlow(tradeCheckFollow.getBusinessNo());
		ehr.setPaymentFlow(tradeCheckFollow.getPayNo());
		ehr.setPayName(tradeCheckFollow.getPayName());
		ehr.setTradeAmount(tradeCheckFollow.getTradeAmount());
		ehr.setTradeTime(tradeCheckFollow.getTradeTime());
		ehr.setHandleRemark(handleRemark);
		ehr.setHandleDateTime(new Date());
		excepHandingRecordDao.save(ehr);
	}
	
	private String combinationPayTypeSql(String payType){
		StringBuilder sql = new StringBuilder();
		
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
	
	private String combinationBillSource(String billSource){
		if("0".equals(billSource)){
			return "";
		}
		return " bill_source, ";
	}

	@Override
	public List<Map<String, Object>> getFollowRecMapDetail(String startDate, String endDate, AppRuntimeConfig hConfig) {
		//??????????????????????????????
		String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
		String billSource = combinationBillSource(null);
		
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		List<Map<String, Object>> typeListMap = getFollowCountByType(startDate, endDate,payTypeSql,orgCode,billSource);
		
		return typeListMap;
	}
	
	@Override
	public List<Map<String, Object>> getFollowRecMapDetailOf7Day(String startDate, String endDate, AppRuntimeConfig hConfig) {
		//??????????????????????????????
		String payTypeSql = combinationPayTypeSql(hConfig.getRecType());
		String billSource = combinationBillSource(null);
		
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		List<Map<String, Object>> typeListMap = getFollowCountOfDayByType(startDate, endDate,payTypeSql,orgCode,billSource);
		
		return typeListMap;
	}
}
