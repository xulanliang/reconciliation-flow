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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.dao.FollowRecResultDao;
import com.yiban.rec.dao.HealthCareHisDao;
import com.yiban.rec.dao.HealthCareOfficialDao;
import com.yiban.rec.dao.HisPayResultDao;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.PlatformFlowDao;
import com.yiban.rec.dao.RecCashDao;
import com.yiban.rec.dao.RecLogDao;
import com.yiban.rec.dao.RecLogDetailsDao;
import com.yiban.rec.dao.ReconcilitationDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.AutoReconciliationService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.CommonEnum.DeleteStatus;
import com.yiban.rec.util.CommonEnum.IsActive;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.StringUtil;

@Service
public class AutoReconciliationServiceImpl extends BaseOprService implements AutoReconciliationService{
	
	static Logger logger_info = LoggerFactory.getLogger(AutoReconciliationServiceImpl.class);
	 
	@Autowired
	private ReconcilitationDao reconcilitationDao;
	
	@Autowired
	private HisPayResultDao hisPayResultDao;
	@Autowired
	private RecCashDao recCashDao;
	
	@Autowired
	private PlatformFlowDao platformFlowDao;
	
	@Autowired
	private ThirdBillDao thirdBillDao;
	
	@Autowired
	private RecLogDao recLogDao;
	
	@Autowired
	private RecLogDetailsDao recLogDetailsDao;
	
	@Autowired
	private HisTransactionFlowDao histransactionFlowDao;
	
	@Autowired
	private FollowRecResultDao followRecResultDao;
	
	
	@Autowired
	private HealthCareOfficialDao healthCareOfficialDao;
	
	@Autowired
	private HealthCareHisDao healthCareHisDao;
	
	@Autowired
	private OrganizationService organizationService;
	
	
//	private void countThrid(String orgNo,String payDate,AppRuntimeConfig config) {
		// ????????????????????????????????????
//		String isOutpatient = config.getIsOutpatient();
		/*// ??????????????????????????????
		String isBillsSources = config.getIsBillsSources();*/
	
		// ?????????????????????????????????????????????????????????
//		if("1".equals(isOutpatient)){//??????????????????
			// ??????
//			collectCountThrid(orgNo,payDate,null,config);
			// ????????????
//			collectCountThrid(orgNo,payDate,EnumTypeOfInt.PAT_TYPE_MZ.getValue(),config);
//			// ????????????
//			collectCountThrid(orgNo,payDate,EnumTypeOfInt.PAT_TYPE_ZY.getValue(),config);
//			// ????????????
//			collectCountThrid(orgNo,payDate,EnumTypeOfInt.PAT_TYPE_QT.getValue(),config);
////		}else{
//			collectCountThrid(orgNo,payDate,null,config);
//		}
//	}
	
	
	@Transactional
	@Override
	public ResponseResult isAutoRecSuccess(String orgNo,String payDate,AppRuntimeConfig config) {
		//????????????????????????????????????????????????
		String healthCheckWays="";
		//????????????????????????
		String recType=config.getRecType();
		
		Map<String,String> mapVo = assemblyPayTypesThrid(recType,"search");
		//?????????????????????????????????????????????
		List<ThirdBill> thirdList = getFlowThird(orgNo,payDate,payDate,mapVo.get("third"));//??????,???????????????
		List<HisTransactionFlow> hisList = getHisFlowThrid(orgNo,payDate,payDate,mapVo.get("his"));//his ???????????????
		List<HisPayResult> hisPayResultList=getHisPayResultFlow(orgNo,payDate,recType,"");//????????????????????????
		
		
		List<HisPayResult> hisPayResultListAll=null;//????????????????????????
		List<HealthCareOfficial> healthCareOfficialList = null;//??????????????????
		List<HealthCareHis> healthCareHisList = null;//??????his??????

		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())>-1) {//??????????????????
			healthCareOfficialList = getHealthCareOfficial(orgNo,payDate,payDate);//??????????????????
			healthCareHisList = getHealthCareHis(orgNo,payDate,payDate);//??????his??????
			hisPayResultListAll=getHisPayResultFlow(orgNo,payDate,null,EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());//????????????????????????
		}
		
		if(StringUtil.isNullOrEmpty(thirdList)&&StringUtil.isNullOrEmpty(hisList)){
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"???????????????his??????????????????");
			return ResponseResult.failure("???????????????his??????????????????");
		}
		if(StringUtil.isNullOrEmpty(thirdList)){
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"??????????????????????????????");
			return ResponseResult.failure("??????????????????????????????");
		}
		if(StringUtil.isNullOrEmpty(hisList)){
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"his??????????????????");
			return ResponseResult.failure("his??????????????????");
		}
		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())>-1&&StringUtil.isNullOrEmpty(healthCareOfficialList)) {
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"???????????????????????????his??????????????????");
			return ResponseResult.failure("???????????????????????????his???????????????");
		}
		try {
			List<Organization> orgList = organizationService.findByParentCode(orgNo);
			followRecResultDao.deleteFollowRec(payDate,payDate, orgNo);
			for (Organization organization : orgList) {
				if(null != organization){
					followRecResultDao.deleteFollowRec(payDate,payDate, organization.getCode());
				}
			}
			
			// ????????????(?????????????????????????????????)
//			countThrid(orgNo,payDate,config);
			
			List<Reconciliation> recResultList = getReconciliation(orgNo, payDate);
			if(!StringUtil.isNullOrEmpty(recResultList)){
				Long tradeDateStart = DateUtil.transferStringToDate("yyyy-MM-dd", payDate).getTime()/1000;
				Long tradeDateEnd = DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59").getTime()/1000;
				List<Organization> list = organizationService.findByParentCode(orgNo);
				List<String> orgs=new ArrayList<>();
				for(Organization v:list) {
					orgs.add(v.getCode());
				}
				reconcilitationDao.updateIsactivedAndIsDeleted(orgs,tradeDateStart,tradeDateEnd);
			}
			// ??????????????????????????????????????????
			List<Reconciliation> recList = new ArrayList<Reconciliation>();
			
			if(!StringUtil.isNullOrEmpty(hisList)){//?????????????????????????????????
				//????????????
				filterData(hisList,thirdList,hisPayResultList);
				// ??????????????????
				if(!StringUtil.isNullOrEmpty(thirdList)){
					handlThirdData(recList,thirdList);
				}
				// ??????his??????
				if(!StringUtil.isNullOrEmpty(hisList)){
					handlHisData(recList,hisList);
				}
				// ????????????
				if(!StringUtil.isNullOrEmpty(hisPayResultList)){
					handlHisPayResultData(recList,hisPayResultList);
				}
			}
			if(!StringUtil.isNullOrEmpty(healthCareHisList)){//???????????????????????????
				//????????????
				filterDataYibao(healthCareHisList,healthCareOfficialList,hisPayResultListAll,healthCheckWays);
				//????????????his?????????
				if(!StringUtil.isNullOrEmpty(healthCareHisList)){
					healthCareHisData(recList,healthCareHisList);
				}
				//???????????????????????????
				if(!StringUtil.isNullOrEmpty(healthCareOfficialList)){
					healthCareOfficialData(recList,healthCareOfficialList);
				}
				if((org.apache.commons.lang.StringUtils.isNotBlank(healthCheckWays)&&EnumType.THIRD_HANDLE_TYPE.getValue().equals(healthCheckWays))||org.apache.commons.lang.StringUtils.isBlank(healthCheckWays)) {
					//??????????????????
					if(!StringUtil.isNullOrEmpty(hisPayResultListAll)){
						handlHisPayResultData(recList,hisPayResultListAll);
					}
				}
			}
			reconcilitationDao.save(recList);
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_SUCCESS.getValue()),"???????????????");
		} catch (Exception e) {
			e.printStackTrace();
			logger_info.error("????????????"+e.getMessage());
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.NOCASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"????????????"+e.getMessage());
			return ResponseResult.failure("????????????!");
		}
		return ResponseResult.success("????????????");
	}
	public List<ThirdBill> getFlowThird(String orgNo,String startDate,String endDate,String payTypeSql){
		Specification<ThirdBill> specification = new Specification<ThirdBill>() {
			@Override
			public Predicate toPredicate(Root<ThirdBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			
				List<Predicate> predicates = converSearch(orgNo,startDate,endDate ,payTypeSql,"third",root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return thirdBillDao.findAll(specification);
	}
	
	public List<HisTransactionFlow> getHisFlowThrid(String orgNo,String startDate,String endDate,String hisPayTypeSql){
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(orgNo,startDate,endDate,hisPayTypeSql,null,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification);
	}
		
	// ??????????????????????????????
	private void filterData(List<HisTransactionFlow> hisList,List<ThirdBill> thirdList,List<HisPayResult> hisPayResultList) {
		//his???????????????
		for(int i=hisList.size()-1;i>=0;i--){
			HisTransactionFlow histr=hisList.get(i);
			String hisFlowNo = histr.getPayFlowNo();
			if(org.apache.commons.lang.StringUtils.isBlank(hisFlowNo))continue;
			//??????????????????
			for(int z=hisPayResultList.size()-1;z>=0;z--){
				HisPayResult hisPayResult=hisPayResultList.get(z);
				//??????his??????????????????
				if(hisFlowNo.equals(hisPayResult.getPayFlowNo())
						&&(hisPayResult.getPayAmount().abs()).compareTo(histr.getPayAmount().abs())==0
						&&histr.getPayType().equals(hisPayResult.getPayType())
						&&histr.getOrderState().equals(hisPayResult.getOrderState())
						&&histr.getOrgNo().equals(hisPayResult.getOrgNo())){//????????????????????????????????????
					for(int j=thirdList.size()-1;j>=0;j--){
						ThirdBill thirdBill = thirdList.get(j);
						if(hisPayResult.getPayFlowNo().equals(thirdBill.getPayFlowNo())
								&&hisPayResult.getPayType().equals(thirdBill.getRecPayType())
								&&(hisPayResult.getPayAmount().abs()).equals(thirdBill.getPayAmount().abs())
								&&thirdBill.getOrderState().equals(hisPayResult.getOrderState())
								&&hisPayResult.getOrgNo().equals(thirdBill.getOrgNo())){
							thirdList.remove(j);
							hisList.remove(i);
							hisPayResultList.remove(z);
							break;
						}
					}
					break;
				}
			}
		}
	}
	
	private void filterDataYibao(List<HealthCareHis> healthCareHisList,List<HealthCareOfficial> healthCareOfficialList,List<HisPayResult> hisPayResultListAll,String healthCheckWays) {
		if(org.apache.commons.lang.StringUtils.isNotBlank(healthCheckWays)&&EnumType.SECOND_HANDLE_TYPE.getValue().equals(healthCheckWays)) {//???2?????????
			//his???????????????
			for(int i=healthCareHisList.size()-1;i>=0;i--){
				HealthCareHis histr = healthCareHisList.get(i);
				String hisFlowNo = histr.getPayFlowNo();
				if(org.apache.commons.lang.StringUtils.isBlank(hisFlowNo))continue;
				//??????his????????????????????????
				for(int j=healthCareOfficialList.size()-1;j>=0;j--){
					HealthCareOfficial thirdBill = healthCareOfficialList.get(j);
					if(histr.getPayFlowNo().equals(thirdBill.getPayFlowNo())
							&&(histr.getCostAccount().abs()).equals(thirdBill.getCostAccount().abs())
							&&thirdBill.getOrderState().equals(histr.getOrderState())
							&&histr.getOrgNo().equals(thirdBill.getOrgNo())){
						healthCareOfficialList.remove(j);
						healthCareHisList.remove(i);
						break;
					}
				}
			}
		}else {
			//his???????????????
			for(int i=healthCareHisList.size()-1;i>=0;i--){
				HealthCareHis histr = healthCareHisList.get(i);
				String hisFlowNo = histr.getPayFlowNo();
				if(org.apache.commons.lang.StringUtils.isBlank(hisFlowNo))continue;
				//??????????????????
				for(int z=hisPayResultListAll.size()-1;z>=0;z--){
					HisPayResult hisPayResult=hisPayResultListAll.get(z);
					//??????his??????????????????
					if(hisFlowNo.equals(hisPayResult.getPayFlowNo())
							&&(hisPayResult.getPayAmount().abs()).compareTo(histr.getCostAccount().abs())==0
							&&histr.getOrderState().equals(hisPayResult.getOrderState())
							&&histr.getOrgNo().equals(hisPayResult.getOrgNo())){//????????????????????????????????????,????????????
						for(int j=healthCareOfficialList.size()-1;j>=0;j--){
							HealthCareOfficial thirdBill = healthCareOfficialList.get(j);
							if(hisPayResult.getPayFlowNo().equals(thirdBill.getPayFlowNo())
									&&(hisPayResult.getPayAmount().abs()).equals(thirdBill.getCostAccount().abs())
									&&thirdBill.getOrderState().equals(hisPayResult.getOrderState())
									&&hisPayResult.getOrgNo().equals(thirdBill.getOrgNo())){
								healthCareOfficialList.remove(j);
								healthCareHisList.remove(i);
								hisPayResultListAll.remove(z);
								break;
							}
						}
						break;
					}
				}
			}
		}
		
	}
	
	private void healthCareHisData(List<Reconciliation> recList,List<HealthCareHis> healthCareHisList){
		for(HealthCareHis thirdBill : healthCareHisList){
			Reconciliation rec = new Reconciliation();
			rec.setOrgNo(thirdBill.getOrgNo());
			rec.setPayDateStam(thirdBill.getTradeDatatime().getTime()/1000);
			rec.setPayType(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
			rec.setTradeFrom(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
			rec.setPayFlowNo(thirdBill.getPayFlowNo());
			rec.setOrgAmount(thirdBill.getCostAccount());
			rec.setPlatformAmount(new BigDecimal(0));
			rec.setThirdAmount(new BigDecimal(0));
			rec.setOrderState(thirdBill.getOrderState());
			rec.setReconciliationDate(thirdBill.getTradeDatatime());
			rec.setPayBusinessType(thirdBill.getOperationType());
			rec.setIsDifferent(CommonEnum.BillBalance.HEALTHCAREHIS.getValue());
			rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
			recList.add(rec);
		}
	}
	private void healthCareOfficialData(List<Reconciliation> recList,List<HealthCareOfficial> healthCareOfficialList){
		for(HealthCareOfficial thirdBill : healthCareOfficialList){
			Reconciliation rec = new Reconciliation();
			rec.setOrgNo(thirdBill.getOrgNo());
			rec.setPayDateStam(thirdBill.getTradeDatatime().getTime()/1000);
			rec.setPayType(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
			rec.setTradeFrom(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
			rec.setPayFlowNo(thirdBill.getPayFlowNo());
			rec.setOrgAmount(new BigDecimal(0));
			rec.setPlatformAmount(new BigDecimal(0));
			rec.setThirdAmount(thirdBill.getCostAccount());
			rec.setOrderState(thirdBill.getOrderState());
			rec.setReconciliationDate(thirdBill.getTradeDatatime());
			rec.setPayBusinessType(thirdBill.getOperationType());
			rec.setIsDifferent(CommonEnum.BillBalance.HEALTHCAREOFFI.getValue());
			rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
			recList.add(rec);
		}
	}
	
	//??????????????????????????????
	private void handlThirdData(List<Reconciliation> recList,List<ThirdBill> thirdList){
		for(ThirdBill thirdBill : thirdList){
			Reconciliation rec = new Reconciliation();
			rec.setOrgNo(thirdBill.getOrgNo());
			rec.setPayDateStam(thirdBill.getTradeDatatime().getTime()/1000);
			rec.setPayType(thirdBill.getPayType());
			rec.setTradeFrom(thirdBill.getPaySource());
			rec.setCustName(thirdBill.getCustName());
			rec.setCustIdentify(thirdBill.getCustIdentifyType());
			rec.setPayBatchNo(thirdBill.getPayBatchNo());
			rec.setPayFlowNo(thirdBill.getPayFlowNo());
			rec.setOrgAmount(new BigDecimal(0));
			rec.setPlatformAmount(new BigDecimal(0));
			rec.setThirdAmount(thirdBill.getPayAmount());
			rec.setPayTermNo(thirdBill.getPayTermNo());
			rec.setPayAccount(thirdBill.getPayAccount());
			rec.setOrderState(thirdBill.getOrderState());
			rec.setReconciliationDate(thirdBill.getTradeDatatime());
			rec.setPayBusinessType(thirdBill.getPayBusinessType());
			rec.setIsDifferent(CommonEnum.BillBalance.THIRDDC.getValue());
			rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
			recList.add(rec);
		}
	}
	
	private void handlHisData(List<Reconciliation> recList,List<HisTransactionFlow> htList) {
		for(HisTransactionFlow his : htList){
			Reconciliation rec = new Reconciliation();
			rec.setOrgNo(his.getOrgNo());
			rec.setPayDateStam(his.getTradeDatatime().getTime()/1000);
			rec.setPayType(his.getPayType());
			rec.setCustName(his.getCustName());
			rec.setCustIdentify(his.getCustIdentifyType());
			rec.setPayFlowNo(his.getPayFlowNo());
			rec.setOrgAmount(his.getPayAmount());
			rec.setPlatformAmount(new BigDecimal(0));
			rec.setThirdAmount(new BigDecimal(0));
			rec.setPayAccount(his.getPayAccount());
			rec.setOrderState(his.getOrderState());
			rec.setReconciliationDate(his.getTradeDatatime());
			rec.setPayBusinessType(his.getPayBusinessType());
			rec.setIsDifferent(CommonEnum.BillBalance.HISDC.getValue());
			rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
			recList.add(rec);
		}
	}
	
	private void  handlHisPayResultData(List<Reconciliation> recList,List<HisPayResult> hisPayResultList) {
		for(HisPayResult hisPayResult : hisPayResultList){
			Reconciliation rec = new Reconciliation();
			rec.setOrgNo(hisPayResult.getOrgNo());
			rec.setPayDateStam(hisPayResult.getTradeDatatime().getTime()/1000);
			rec.setPayType(hisPayResult.getPayType());
			rec.setTradeFrom(hisPayResult.getPaySource());
			rec.setCustName(hisPayResult.getCustName());
			rec.setCustIdentify(hisPayResult.getCustIdentifyType());
			rec.setPayFlowNo(hisPayResult.getPayFlowNo());
			rec.setOrgAmount(new BigDecimal(0));
			rec.setPlatformAmount(hisPayResult.getPayAmount());
			rec.setThirdAmount(new BigDecimal(0));
			rec.setPayAccount(hisPayResult.getPayAccount());
			rec.setOrderState(hisPayResult.getOrderState());
			rec.setReconciliationDate(hisPayResult.getTradeDatatime());
			rec.setPayBusinessType(hisPayResult.getPayBusinessType());
			rec.setIsDifferent(CommonEnum.BillBalance.PLATDC.getValue());
			rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
			rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
			recList.add(rec);
		}
	}
	
	/**
	 * ????????????????????????
	 * @param orgCode
	 * @param orderDate
	 * @param recState
	 * @param logType
	 * @param payType
	 * @param exceptionRemark
	 * @param dealWay
	 */
	private void recordRecLog(String orgCode, String orderDate, Integer recState,
	       String logType, String payType, String exceptionRemark, String dealWay) {
	    RecLogDetails rd = new RecLogDetails();
	    rd.setCreatedDate(DateUtil.getCurrentDateTime());
	    rd.setDealWay(dealWay);
	    rd.setExceptionRemark(exceptionRemark);
	    rd.setLogType(logType);
	    rd.setOrderDate(orderDate);
	    rd.setOrgCode(orgCode);
	    rd.setRecState(recState);
	    recLogDetailsDao.save(rd);
	}
	
	
	/**
	 * ????????????
	 * @param orgNo
	 * @param payDate
	 * @param recState
	 * void
	 */
	private void recordRecLog(String orgNo, String payDate, Integer recState) {
	    try {
            RecLog recLog = recLogDao.findByOrderDateAndOrgCode(payDate, orgNo);
            if(null == recLog) {
                recLog = new RecLog();
                recLog.setCreatedDate(DateUtil.getCurrentDateTime());
            }
            recLog.setOrgCode(orgNo);
            recLog.setOrderDate(payDate);
            recLog.setRecResult(recState);
            recLogDao.save(recLog);
        } catch (Exception e) {
            logger.error("???????????????????????????????????????", e);
        }
//	    try {
//			RecLogState reclogState = recLogDao.findByOrgNoAndOrderDate(orgNo, DateUtil.transferStringToDate("yyyy-MM-dd", payDate));
//			if(reclogState!=null){
//				reclogState.setRecState(recState);
//				reclogState.setExceptionRemark(exceptionRemark);
//				reclogState.setLastModifiedDate(new Date());
//				recLogDao.save(reclogState);
//			}else{
//				RecLogState rls = new RecLogState();
//				rls.setOrgNo(orgNo);
//				rls.setRecState(recState);
//				rls.setIsCash(isCash);
//				rls.setExceptionRemark(exceptionRemark);
//				rls.setOrderDate(DateUtil.transferStringToDate("yyyy-MM-dd", payDate));
//				recLogDao.save(rls);
//			}
//		} catch (Exception e) {
//			logger_info.error("??????????????????"+e.getMessage());
//		} 
	}
	
	/**
	* @date???2017???4???25??? 
	* @Description?????????????????????????????????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<Platformflow>: ???????????????
	* @throws
	 */
	public List<ThirdBill> getThirdFlow(String orgNo,String startDate,String endDate,String payTypeSql){
		Specification<ThirdBill> specification = new Specification<ThirdBill>() {
			@Override
			public Predicate toPredicate(Root<ThirdBill> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			
				List<Predicate> predicates = converSearch(orgNo,startDate,endDate ,payTypeSql,"third",root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return thirdBillDao.findAll(specification);
	}
	/**
	* @date???2017???4???25??? 
	* @Description?????????????????????????????????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<Platformflow>: ???????????????
	* @throws
	 */
	public List<HisPayResult> getHisPayResultFlow(String orgNo,String payDate,String sql,String isHealthAccount){
		Specification<HisPayResult> specification = new Specification<HisPayResult>() {
			@Override
			public Predicate toPredicate(Root<HisPayResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchNoYiBao(orgNo,payDate,root, query, cb,sql,isHealthAccount);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return hisPayResultDao.findAll(specification);
	}
	
	
	/**
	* @date???2017???5???25??? 
	* @Description?????????his????????????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<HisTransactionFlow>: ???????????????
	* @throws
	 */
	public List<HisTransactionFlow> getHisFlow(String orgNo,String startDate,String endDate,String hisPayTypeSql){
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(orgNo,startDate,endDate,hisPayTypeSql,null,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification);
	}
	/**
	* @date???2017???4???25??? 
	* @Description???????????????????????????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<Platformflow>: ???????????????
	* @throws
	 */
	public List<Platformflow> getPlatformFlow(String orgNo,String payDate){
		Specification<Platformflow> specification = new Specification<Platformflow>() {
			@Override
			public Predicate toPredicate(Root<Platformflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(orgNo,payDate,payDate,null,null,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return platformFlowDao.findAll(specification);
	}
	
	/*
	 * ??????????????????
	 */
	public List<Reconciliation> getReconciliation(String orgNo,String payDate){
		Specification<Reconciliation> specification = new Specification<Reconciliation>() {
			@Override
			public Predicate toPredicate(Root<Reconciliation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchRec(orgNo,payDate,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return reconcilitationDao.findAll(specification);
	}
	
	protected List<Predicate> converSearchRec(String orgNo, String payDate,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		// ???????????????????????????
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		// ??????
		In<String> in = cb.in(root.get("orgNo"));
		if (orgList != null && orgList.size() > 0) {
			for(Organization org : orgList){
				if(org != null){
					in.value(org.getCode());
				}
			}
		}
		predicates.add(in);
		if (!StringUtils.isEmpty(payDate)) {
			Path<Long> orderDateExp = root.get("payDateStam");
			predicates.add(cb.greaterThanOrEqualTo(orderDateExp, DateUtil.transferStringToDate("yyyy-MM-dd", payDate).getTime()/1000));
		}
		if (!StringUtils.isEmpty(payDate)) {
			Path<Long> orderDateExp = root.get("payDateStam");
			predicates.add(cb.lessThanOrEqualTo(orderDateExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59").getTime()/1000));
		}
// 		In<Long> in = cb.in(root.get("tradeCode").as(Long.class));
// 		in.value(Long.valueOf(EnumTypeOfInt.TRADE_CODE_BAR.getValue()));
// 		in.value(Long.valueOf(EnumTypeOfInt.TRADE_CODE_PAY.getValue()));
// 		in.value(Long.valueOf(EnumTypeOfInt.REfUND_CODE.getValue()));
// 		in.value(Long.valueOf(EnumTypeOfInt.TRADE_CODE_RETURN.getValue()));
// 		in.value(Long.valueOf(EnumTypeOfInt.TRADE_CODE_REVOKE.getValue()));
// 		predicates.add(in);
		
		return predicates;
	}
	
	protected List<Predicate> converSearch(String orgNo, String startDate,String endDate,String payTypeSql,String payTypeFlag,Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		// ???????????????????????????
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		List<Predicate> predicates = Lists.newArrayList();
		// ??????
		In<String> in = cb.in(root.get("orgNo"));
		in.value(orgNo);
		if (orgList != null && orgList.size() > 0) {
			for(Organization org : orgList){
				if(org != null){
					in.value(org.getCode());
				}
			}
		}
		predicates.add(in);
		if(null != payTypeSql){
			In<String> payTypeIn = null;
			if(null != payTypeFlag && "third".equals(payTypeFlag)){
				payTypeIn = cb.in(root.get("recPayType"));
			}else{
				payTypeIn = cb.in(root.get("payType"));
			}
					
			String[] payTypes = payTypeSql.split(",");
			for (String payType : payTypes) {
				payTypeIn.value(payType);
			}
			predicates.add(payTypeIn);
		}
		
		// ??????
		if (!StringUtils.isEmpty(startDate)) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, DateUtil.transferStringToDate("yyyy-MM-dd",startDate)));
		}
		if (!StringUtils.isEmpty(endDate)) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate+" 23:59:59")));
		}
		// ????????????
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		return predicates;
	}
	
	protected List<Predicate> converSearchNoYiBao(String orgNo, String payDate,Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,String sql,String isHealthAccount) {
		// ???????????????????????????
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		if(null == orgList ){
			orgList = new ArrayList<Organization>();
		}
		Organization o = new Organization();
		o.setCode(orgNo);
		orgList.add(o);
		
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		In<String> in = cb.in(root.get("orgNo"));
		if (orgList != null && orgList.size() > 0) {
			for(Organization org : orgList){
				if(org != null){
					in.value(org.getCode());
				}
			}
		}
		in.value(orgNo);
		predicates.add(in);
		
		if(null != sql){
			In<String> payTypeIn = cb.in(root.get("payType"));
			String[] payTypes = sql.split(",");
			for (String payType : payTypes) {
				payTypeIn.value(payType);
			}
			predicates.add(payTypeIn);
		}
		
		
		if (!StringUtils.isEmpty(payDate)) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, DateUtil.transferStringToDate("yyyy-MM-dd",payDate)));
		}
		if (!StringUtils.isEmpty(payDate)) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59")));
		}
		Path<Integer> payTypeExp = root.get("payType");
		predicates.add(cb.notEqual(payTypeExp, EnumTypeOfInt.CASH_PAYTYPE.getValue()));
		if(org.apache.commons.lang.StringUtils.isBlank(isHealthAccount)||isHealthAccount.indexOf(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())==-1) {
			Path<Integer> yibao = root.get("payType");
			predicates.add(cb.notEqual(yibao, EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue()));
		}else {
			Path<Integer> yibao = root.get("payType");
			predicates.add(cb.equal(yibao, EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue()));
		}
		return predicates;
	}
	
	protected List<Predicate> converSearchHealthCare(String orgNo, String startDate,String endDate,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		
		// ???????????????????????????
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		List<Predicate> predicates = Lists.newArrayList();
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		In<String> in = cb.in(root.get("orgNo"));
		if (orgList != null && orgList.size() > 0) {
			for(Organization org : orgList){
				if(org != null){
					in.value(org.getCode());
				}
			}
		}
		in.value(orgNo);
		predicates.add(in);
		if (!StringUtils.isEmpty(startDate)) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, DateUtil.transferStringToDate("yyyy-MM-dd",startDate)));
		}
		if (!StringUtils.isEmpty(endDate)) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate+" 23:59:59")));
		}
		return predicates;
	}

	/**
	 * ??????????????????
	 */
	@Transactional
	@Override
	public ResponseResult isAutoRecCashSuccess(String orgNo, String payDate) {
		List<HisTransactionFlow> hisCahsList = getHisCashFlow(orgNo,payDate,payDate);
		List<RecCash> hispayResultCashList = getHisPayCashResult(orgNo,payDate,payDate);
		if(StringUtil.isNullOrEmpty(hisCahsList)){
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.CASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"his??????????????????");
			return ResponseResult.failure("his??????????????????");
		}
		List<Reconciliation> recList = new ArrayList<Reconciliation>();
		try {
			for(int i = hisCahsList.size()-1;i>=0;i--){
				HisTransactionFlow hisTransactionFlow = hisCahsList.get(i);
				String hisFlowNo = hisTransactionFlow.getPayFlowNo();
				for(int j = hispayResultCashList.size()-1;j>=0;j--){
					RecCash hisPayResult = hispayResultCashList.get(j);
					Reconciliation rec =  new Reconciliation();
					if(hisFlowNo.equals(hisPayResult.getPayFlowNo())&&
							hisPayResult.getPayAmount().compareTo(hisTransactionFlow.getPayAmount())==0){
						rec.setOrgNo(hisPayResult.getOrgNo());
// 						rec.setPayDateStam(hisPayResult.getTradeDatatime().getTime()/1000);
						rec.setPayType(hisPayResult.getPayType());
						rec.setTradeCode(hisPayResult.getTradeCode());
						rec.setPayBusinessType(hisPayResult.getPayBusinessType());
						rec.setTradeFrom(hisPayResult.getPaySource());
						rec.setCustName(hisPayResult.getCustName());
						rec.setPayShopNo(hisPayResult.getPayShopNo());
						rec.setCustIdentify(hisPayResult.getCustIdentifyType());
						rec.setFlowNo(hisPayResult.getFlowNo());	
						rec.setPaySystemNo(hisPayResult.getPaySystemNo());
						rec.setHisFlowNo(hisTransactionFlow.getPayFlowNo());
						rec.setBusinessFlowNo(hisTransactionFlow.getBusinessFlowNo());
						rec.setPayBatchNo(hisPayResult.getPayBatchNo());
						rec.setPayFlowNo(hisPayResult.getPayFlowNo());
						rec.setOrgAmount(hisTransactionFlow.getPayAmount());
						rec.setPlatformAmount(hisPayResult.getPayAmount());
						rec.setThirdAmount(new BigDecimal(0));
						rec.setPayAccount(hisPayResult.getPayAccount());
						rec.setPayTermNo(hisPayResult.getPayTermNo());
						rec.setDeviceNo(hisPayResult.getDeviceNo());
						rec.setOrderState(hisPayResult.getOrderState());
						rec.setReconciliationDate(hisPayResult.getTradeDatatime());
						rec.setIsDifferent(CommonEnum.BillBalance.zp.getValue());
						rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
						rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
						rec.setCustName(hisPayResult.getPatientName() == null ? hisTransactionFlow.getCustName() : hisPayResult.getPatientName());
						recList.add(rec);
						hisCahsList.remove(i);
						hispayResultCashList.remove(j);
						break;
					}
				}
			}
// 			Long tradeDateStart = DateUtil.transferStringToDate("yyyy-MM-dd",payDate).getTime()/1000;
// 			Long tradeDateEnd = DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59").getTime()/1000;
			handlerHisCashDcData(recList,hisCahsList,orgNo);// ??????his??????????????????
			handlerPlatCashDcData(recList,hispayResultCashList,orgNo);// ????????????????????????
			// ????????????
			List<Organization> orgList = organizationService.findByParentCode(orgNo);
			reconcilitationDao.deleteCashByDate(orgNo, DateUtil.transferStringToDate("yyyy-MM-dd",payDate), DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59"));
			for (Organization organization : orgList) {
				if(null != organization){
					reconcilitationDao.deleteCashByDate(orgNo, DateUtil.transferStringToDate("yyyy-MM-dd",payDate), DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", payDate+" 23:59:59"));
				}
			}
			reconcilitationDao.save(recList);
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.CASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_SUCCESS.getValue()),"???????????????");
		} catch (Exception e) {
			logger_info.error("??????????????????"+e.getMessage());
//			recordRecLog(orgNo,payDate,EnumTypeOfInt.CASH_PAYTYPE.getValue(),Integer.parseInt(EnumType.HANDLE_FAIL.getValue()),"????????????"+e.getMessage());
			return ResponseResult.failure("??????????????????!");
		}
		
		return ResponseResult.success("?????????????????????");
	}
	
	private void handlerHisCashDcData(List<Reconciliation> recList,List<HisTransactionFlow> hisCahsList,String orgNo){
		if(!StringUtil.isNullOrEmpty(hisCahsList)){
			for(HisTransactionFlow hisTransactionFlow : hisCahsList){
				Reconciliation rec = new Reconciliation();
				rec.setOrgNo(hisTransactionFlow.getOrgNo());
				rec.setPayDateStam(hisTransactionFlow.getTradeDatatime().getTime()/1000);
				rec.setPayType(hisTransactionFlow.getPayType());
				rec.setPayBusinessType(hisTransactionFlow.getPayBusinessType());
				rec.setCustName(hisTransactionFlow.getCustName());
				rec.setFlowNo(hisTransactionFlow.getHisFlowNo());
				rec.setHisFlowNo(hisTransactionFlow.getPaySystemNo());
				rec.setBusinessFlowNo(hisTransactionFlow.getBusinessFlowNo());
				rec.setPayFlowNo(hisTransactionFlow.getPayFlowNo());
				rec.setOrgAmount(hisTransactionFlow.getPayAmount());
				rec.setPlatformAmount(new BigDecimal(0));
				rec.setThirdAmount(new BigDecimal(0));
				rec.setPayAccount(hisTransactionFlow.getPayAccount());
				rec.setDeviceNo(hisTransactionFlow.getDeviceNo());
				rec.setOrderState(hisTransactionFlow.getOrderState());
				rec.setReconciliationDate(hisTransactionFlow.getTradeDatatime());
				rec.setIsDifferent(CommonEnum.BillBalance.HISDC.getValue());
				rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
				rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				rec.setCustName(hisTransactionFlow.getCustName());
				recList.add(rec);
			}
		}
	}
	
	private void handlerPlatCashDcData(List<Reconciliation> recList,List<RecCash> hispayResultCashList,String orgNo){
		if(!StringUtil.isNullOrEmpty(hispayResultCashList)){
			for(RecCash hisPayResult : hispayResultCashList){
				Reconciliation rec = new Reconciliation();
				rec.setOrgNo(orgNo);
				rec.setPayDateStam(hisPayResult.getTradeDatatime().getTime()/1000);
				rec.setPayType(hisPayResult.getPayType());
				rec.setTradeCode(hisPayResult.getTradeCode());
				rec.setPayBusinessType(hisPayResult.getPayBusinessType());
				rec.setTradeFrom(hisPayResult.getPaySource());
				rec.setCustName(hisPayResult.getCustName());
				rec.setPayShopNo(hisPayResult.getPayShopNo());
				rec.setCustIdentify(hisPayResult.getCustIdentifyType());
				rec.setFlowNo(hisPayResult.getFlowNo());
				rec.setPaySystemNo(hisPayResult.getPaySystemNo());
				rec.setHisFlowNo(hisPayResult.getBusinessFlowNo());
				rec.setBusinessFlowNo(hisPayResult.getBusinessFlowNo());
				rec.setPayBatchNo(hisPayResult.getPayBatchNo());
				rec.setPayFlowNo(hisPayResult.getPayFlowNo());
				rec.setOrgAmount(new BigDecimal(0));
				rec.setPlatformAmount(hisPayResult.getPayAmount());
				rec.setThirdAmount(new BigDecimal(0));
				rec.setPayAccount(hisPayResult.getPayAccount());
				rec.setPayTermNo(hisPayResult.getPayTermNo());
				rec.setDeviceNo(hisPayResult.getDeviceNo());
				rec.setOrderState(hisPayResult.getOrderState());
				rec.setReconciliationDate(hisPayResult.getTradeDatatime());
				rec.setIsDifferent(CommonEnum.BillBalance.PLATDC.getValue());
				rec.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
				rec.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				rec.setCustName(hisPayResult.getPatientName());
				recList.add(rec);
			}
		}
	}
	
	public List<RecCash> getHisPayCashResult(String orgNo,String startDate,String endDate){
		Specification<RecCash> specification = new Specification<RecCash>() {
			@Override
			public Predicate toPredicate(Root<RecCash> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(orgNo,startDate,endDate,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return recCashDao.findAll(specification);
	}
	
	public List<HisTransactionFlow> getHisCashFlow(String orgNo,String startDate,String endDate){
		Specification<HisTransactionFlow> specification = new Specification<HisTransactionFlow>() {
			@Override
			public Predicate toPredicate(Root<HisTransactionFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchCash(orgNo,startDate,endDate,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return histransactionFlowDao.findAll(specification);
	}
	
	protected List<Predicate> converSearchCash(String orgNo, String startDate,String endDate,Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		
		// ???????????????????????????
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		List<Predicate> predicates = Lists.newArrayList();
		In<String> in = cb.in(root.get("orgNo"));
		if (orgList != null && orgList.size() > 0) {
			for(Organization org : orgList){
				if(org != null){
					in.value(org.getCode());
				}
			}
		}
		in.value(orgNo);
		predicates.add(in);
		Path<Integer> isDeletedExp = root.get("isDeleted");
		predicates.add(cb.equal(isDeletedExp, DeleteStatus.UNDELETE.getValue()));
		Path<Integer> isActivedExp = root.get("isActived");
		predicates.add(cb.equal(isActivedExp, IsActive.ISACTIVED.getValue()));
		if (!StringUtils.isEmpty(startDate)) {
			Path<Date> payDateStartExp = root.get("tradeDatatime");
			predicates.add(cb.greaterThanOrEqualTo(payDateStartExp, DateUtil.transferStringToDate("yyyy-MM-dd",startDate)));
		}
		if (!StringUtils.isEmpty(endDate)) {
			Path<Date> payDateEndExp = root.get("tradeDatatime");
			predicates.add(cb.lessThanOrEqualTo(payDateEndExp, DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", endDate+" 23:59:59")));
		}
		Path<Integer> payTypeExp = root.get("payType");
		predicates.add(cb.equal(payTypeExp, EnumTypeOfInt.CASH_PAYTYPE.getValue()));
		
		return predicates;
	}
	
private Map<String,String> assemblyPayTypesThrid(String payType,String flag){
		
		Map<String,String> map = new HashMap<String,String>();
		StringBuilder payTypeSql = new StringBuilder();
		StringBuilder hisPayTypeSql = new StringBuilder();
		if("compare".equals(flag)){
			payTypeSql.append(" and rec_pay_type in ( ");
			hisPayTypeSql.append(" and pay_type in ( ");
		}else{
			payTypeSql.append("");
			hisPayTypeSql.append("");
		}
		
		if(payType == null || payType.length() ==0 ){
			payTypeSql.append("");
			hisPayTypeSql.append("");
		}else{
			boolean contain = false;
			boolean hisContain = false;
			if("compare".equals(flag)){
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())){// ??????
					payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'" + ",");
					hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + "'"  + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())){// ?????????
					payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
					hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + "'" + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())){// ??????
					payTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
					hisPayTypeSql.append("'" + EnumTypeOfInt.PAY_TYPE_BANK.getValue() + "'" + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())){// ??????
					payTypeSql.append("'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "'" + ",");
					hisPayTypeSql.append("'" + EnumTypeOfInt.CASH_PAYTYPE.getValue() + "'" + ",");
					hisContain = true;
				}
			}else {
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())){// ??????
					payTypeSql.append(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + ",");
					hisPayTypeSql.append(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue() + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())){// ?????????
					payTypeSql.append( EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue()+ ",");
					hisPayTypeSql.append(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue() + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.PAY_TYPE_BANK.getValue())){// ??????
					payTypeSql.append( EnumTypeOfInt.PAY_TYPE_BANK.getValue() + ",");
					hisPayTypeSql.append( EnumTypeOfInt.PAY_TYPE_BANK.getValue() + ",");
					contain = true;
					hisContain = true;
				}
				if(payType.contains(EnumTypeOfInt.CASH_PAYTYPE.getValue())){// ??????
					hisPayTypeSql.append( EnumTypeOfInt.CASH_PAYTYPE.getValue() + ",");
					hisContain = true;
				}
			}
			
			if(contain){
				payTypeSql.deleteCharAt(payTypeSql.length()-1);
				if("compare".equals(flag)){
					payTypeSql.append( " ) ");
				}
			}else{
				payTypeSql.delete(0, payTypeSql.length());
			}
			if(hisContain){
				hisPayTypeSql.deleteCharAt(hisPayTypeSql.length()-1);
				if("compare".equals(flag)){
					hisPayTypeSql.append(" ) ");
				}
			}else{
				hisPayTypeSql.delete(0, hisPayTypeSql.length());
			}
		}
		map.put("his", hisPayTypeSql.toString());
		map.put("third", payTypeSql.toString());
		return map;
		
	}

	private String generateAllOrgSql(String orgNo) {
		String orgSql = "";
		List<Organization> orgList = organizationService.findByParentCode(orgNo);
		if(null == orgList){
			orgSql = " and org_no= '" + orgNo + "'";
		}else{
			orgSql = " and org_no IN (";
			for (Organization organization : orgList) {
				orgSql =orgSql + "'"+ organization.getCode() + "',";
			}
			orgSql = orgSql + "'"+orgNo+"')";
		}
		return orgSql;
	}
	/**
	* @date???2018???5???13??? 
	* @Description???????????????????????????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<HealthCareOfficial>: ???????????????
	* @throws
	 */
	private List<HealthCareOfficial> getHealthCareOfficial(String orgNo,String startDate,String endDate){
		Specification<HealthCareOfficial> specification = new Specification<HealthCareOfficial>() {
			@Override
			public Predicate toPredicate(Root<HealthCareOfficial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchHealthCare(orgNo,startDate,endDate,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return healthCareOfficialDao.findAll(specification);
	}
	
	/**
	* @date???2018???5???13??? 
	* @Description???????????????his??????
	* @param orgNo
	* @param payDate
	* @return: ??????????????????
	* @return List<HealthCareOfficial>: ???????????????
	* @throws
	 */
	private List<HealthCareHis> getHealthCareHis(String orgNo,String startDate,String endDate){
		Specification<HealthCareHis> specification = new Specification<HealthCareHis>() {
			@Override
			public Predicate toPredicate(Root<HealthCareHis> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearchHealthCare(orgNo,startDate,endDate,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return healthCareHisDao.findAll(specification);
	}
	
	private void collectCountThrid(String orgNo, String payDate, String patType,AppRuntimeConfig config){
		//String recType="'"+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()+"','"+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue()+"','"+EnumTypeOfInt.PAY_TYPE_BANK.getValue()+"'";
		String recType=config.getRecType();
//		String healthCheckWays=config.getHealthCheckWays();
	
		// ????????????/????????????
		String patTypeSql = "";
		if(null != patType){
			patTypeSql = " and pat_type = '" + patType +"' ";
		}
		// ????????????
		String orgSql = generateAllOrgSql(orgNo);
		
		FollowRecResult followRecResult = new FollowRecResult();
		String startDate = payDate;
		String endDate = payDate+" 23:59:59";
		followRecResult.setOrgNo(orgNo);
		followRecResult.setTradeDate(payDate);
		
		String sql="";
		// ?????????????????????
//		if(EnumTypeOfInt.REC_TYPE_TWO.getValue().equals(healthCheckWays)) {//??????2?????????
			sql="SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_rec_pay_result WHERE "+ "Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' "
					+" and pay_type!="+EnumTypeOfInt.CASH_PAYTYPE.getValue()
					+" and pay_type!="+EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue()
					+" and pay_type in "
							+ "("+recType+")"
							+ patTypeSql + orgSql;
//		}
//		else{
//			sql="SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_rec_pay_result WHERE "+ "Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' "
//					+" and pay_type!="+EnumTypeOfInt.CASH_PAYTYPE.getValue()
//					+" and pay_type in "
//							+ "("+recType+")"
//							+ patTypeSql + orgSql;
//		}
		
		Object objRecPay = super.handleNativeSql4SingleRes(sql);
		if(objRecPay==null) {
			objRecPay="0";
		}
		BigDecimal recPayBd = new BigDecimal(objRecPay.toString());
		followRecResult.setRecPayAllAmount(recPayBd);
		
		
		// his????????????
		sql="SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_rec_histransactionflow WHERE "+ "Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' "
				+" and pay_type in "+ "("+recType+")"
				+ patTypeSql + orgSql;
		Object objOne = super.handleNativeSql4SingleRes(sql);
		if(objOne==null) {
			objOne="0";
		}
		// his????????????
		BigDecimal hisBd = new BigDecimal(objOne.toString());
		followRecResult.setHisAllAmount(hisBd);
		// ?????????????????????
		sql="SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_thrid_bill WHERE "
				+ " Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"'" 
				+" and rec_pay_type in "+ "("+recType+") "
				+ patTypeSql + orgSql;
		Object objTwo = super.handleNativeSql4SingleRes(sql);
		if(objTwo==null) {
			objTwo="0";
		}
		BigDecimal payBd = new BigDecimal(objTwo.toString());
		followRecResult.setPayAllAmount(payBd);
		
		// ?????????????????????
		BigDecimal wechatBd = new BigDecimal(0);
		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue())>-1) {
			sql = "SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_thrid_bill WHERE "
				+ " rec_pay_type='"+EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()+"' "
						+ "AND Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' " 
						+ patTypeSql + orgSql;
			
			Object objThree = super.handleNativeSql4SingleRes(sql);
			wechatBd = new BigDecimal(StringUtil.isNullOrEmpty(objThree)?"0.00":objThree.toString());
		}
		
		followRecResult.setWechatAllAmount(wechatBd);
		
		// ????????????????????????
		BigDecimal aliPayBd = new BigDecimal(0);
		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue())>-1) {
			sql = "SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_thrid_bill WHERE "
					+" rec_pay_type='"+EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue()+"' "
							+ " AND Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' " 
							+ patTypeSql + orgSql;
			
			Object objFour = super.handleNativeSql4SingleRes(sql);
			aliPayBd = new BigDecimal(StringUtil.isNullOrEmpty(objFour)?"0.00":objFour.toString());
		}
		
		followRecResult.setAlipayAllAmount(aliPayBd);
		// ??????????????????
		BigDecimal bankBd = new BigDecimal(0);
		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_BANK.getValue())>-1) {
			sql = "SELECT SUM(IF(Order_State=0256,-ABS(pay_amount),pay_amount)) AS Pay_Amount FROM t_thrid_bill WHERE "
					+" rec_pay_type='"+EnumTypeOfInt.PAY_TYPE_BANK.getValue()+"' "   
							+ " AND Trade_datatime>='"+startDate+"' AND  Trade_datatime<='"+endDate+"' " 
							+ patTypeSql + orgSql;
			
			Object objFive = super.handleNativeSql4SingleRes(sql);
			bankBd = new BigDecimal(StringUtil.isNullOrEmpty(objFive)?"0.00":objFive.toString());
		}
		
		followRecResult.setBankAllAmount(bankBd);
		
		
		BigDecimal healthcareOffBd = new BigDecimal(0);
		BigDecimal healthcareHisBd = new BigDecimal(0);
		if(org.apache.commons.lang.StringUtils.isNotBlank(recType)&&recType.indexOf(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue())>-1) {
			// ??????????????????
			sql="SELECT SUM(IF(Order_State=0256,-ABS(cost_account),cost_account)) AS Pay_Amount FROM t_healthcare_official WHERE "
					+ " trade_datatime>='"+startDate+"' AND  trade_datatime<='"+endDate+"' " 
					+ patTypeSql + orgSql;
			Object objHealthcareOff = super.handleNativeSql4SingleRes(sql);
			if(objHealthcareOff==null) {
				objHealthcareOff="0";
			}
			healthcareOffBd = new BigDecimal(objHealthcareOff.toString());
			
			
			// ??????his??????
			sql="SELECT SUM(IF(Order_State=0256,-ABS(cost_account),cost_account)) AS Pay_Amount FROM t_healthcare_his WHERE "
					+ " trade_datatime>='"+startDate+"' AND  trade_datatime<='"+endDate+"' "
					+ patTypeSql + orgSql;
			Object objHealthcareHis = super.handleNativeSql4SingleRes(sql);
			if(objHealthcareHis==null) {
				objHealthcareHis="0";
			}
			healthcareHisBd = new BigDecimal(objHealthcareHis.toString());
		}
		followRecResult.setSocialInsuranceAmount(healthcareOffBd);
		// his??????????????????????????????his??????????????????????????? + ????????????
		followRecResult.setHisAllAmount(hisBd.add(healthcareHisBd));
		// ????????????????????????????????????????????????????????????????????? + ????????????
		followRecResult.setPayAllAmount(payBd.add(healthcareOffBd));
		if(org.apache.commons.lang.StringUtils.isBlank(patType)){
			followRecResult.setPatType(EnumTypeOfInt.PAT_TYPE_ZYMZ.getValue());
			followRecResult.setTradeDiffAmount(hisBd.subtract(payBd));// ??????
			followRecResult.setHandlerDiffAmount(hisBd.subtract(payBd));
			if(followRecResult.getHisAllAmount().compareTo(followRecResult.getPayAllAmount())==0&&followRecResult.getHisAllAmount().compareTo(followRecResult.getRecPayAllAmount())==0&&followRecResult.getPayAllAmount().compareTo(followRecResult.getRecPayAllAmount())==0){
				followRecResult.setExceptionResult("?????????");
			}else{
				followRecResult.setExceptionResult("?????????");
			}
		}
		followRecResult.setCreateDate(new Date());
		if(org.apache.commons.lang.StringUtils.isNotBlank(patType)){
			followRecResult.setPatType(patType);
		}
		followRecResultDao.save(followRecResult);
	}
}
