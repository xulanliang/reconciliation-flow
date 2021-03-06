package com.yiban.rec.reconciliations;

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

import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.dao.HealthCareHisDao;
import com.yiban.rec.dao.HealthCareOfficialDao;
import com.yiban.rec.dao.HealthExceptionDao;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.RecLogDetailsDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.dao.TradeCheckFollowDao;
import com.yiban.rec.dao.recon.TradeCheckFollowDealDao;
import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.HealthException;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.domain.recon.TradeCheckFollowDeal;
import com.yiban.rec.reconciliations.impl.AggregateReconciliations;
import com.yiban.rec.reconciliations.impl.AliPayReconciliations;
import com.yiban.rec.reconciliations.impl.BankReconciliations;
import com.yiban.rec.reconciliations.impl.CashReconciliations;
import com.yiban.rec.reconciliations.impl.HealthCareBankReconciliations;
import com.yiban.rec.reconciliations.impl.HealthCareOfficiaReconciliations;
import com.yiban.rec.reconciliations.impl.HealthTypeReconciliations;
import com.yiban.rec.reconciliations.impl.OnlineBankReconciliations;
import com.yiban.rec.reconciliations.impl.UnionPayReconciliations;
import com.yiban.rec.reconciliations.impl.WJYZTReconciliations;
import com.yiban.rec.reconciliations.impl.WechatPayReconciliations;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.LogCons;
import com.yiban.rec.util.RandomCodeUtil;

/**
 * @author swing
 * @date 2018???7???19??? ??????11:13:06 ?????????: ??????????????????????????????????????????????????????
 */
public abstract class AbstractReconciliations<T, K> implements Reconciliationsable {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Logger????????????
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * ??????????????????
     */
    private RecLogDetailsDao recLogDetailsDao = SpringBeanUtil.getBean(RecLogDetailsDao.class);

    /**
     * ????????????Service
     */
    private OrganizationService organizationService = SpringBeanUtil.getBean(OrganizationService.class);


    private TradeCheckFollowDao tradeCheckFollowDao = SpringBeanUtil.getBean(TradeCheckFollowDao.class);

    private TradeCheckFollowDealDao tradeCheckFollowDealDao = SpringBeanUtil.getBean(TradeCheckFollowDealDao.class);

    private HisTransactionFlowDao hisTransactionFlowDao = SpringBeanUtil.getBean(HisTransactionFlowDao.class);

    // ??????
    private ThirdBillDao thirdBillDao = SpringBeanUtil.getBean(ThirdBillDao.class);
    // ????????????
    private HealthCareOfficialDao healthCareOfficialDao = SpringBeanUtil.getBean(HealthCareOfficialDao.class);
    // ??????His
    private HealthCareHisDao healthCareHisDao = SpringBeanUtil.getBean(HealthCareHisDao.class);

    private HealthExceptionDao healthExceptionDao = SpringBeanUtil.getBean(HealthExceptionDao.class);

    private PropertiesConfigService propertiesConfigService = SpringBeanUtil.getBean(PropertiesConfigService.class);

    /**
     * ????????????
     */
    protected Set<String> orgCodes;

    /**
     * ????????????????????????
     */
    private String orgCode;

    /**
     * ??????
     */
    protected String date;

    /**
     * ????????????
     */
    protected Date beginDate;

    /**
     * ????????????
     */
    protected Date endDate;

    /**
     * ????????????
     *
     * @param orgCode
     * @param date
     */
    public AbstractReconciliations(String orgCode, String date) {
        this.orgCode = orgCode;
        this.date = date;
        this.orgCodes = initOrgCodeSet(orgCode);
        getBeginDateAndEndDate();
    }


    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     */
    @SuppressWarnings("rawtypes")
    @Override
    public final List<TradeCheckFollow> compareBill() throws Exception {
        List<TradeCheckFollow> resultList = new ArrayList<>();
        List<T> sourceList = sourceList();
        List<K> targetList = targetList();
        //????????????????????????????????????????????????????????????,?????????????????????????????????????????????????????????
        Class cls = this.getClass();
        String msgSource = "";
        String msgTarget = "";
        String sucMsg = "";
        String payType = "";

        if (cls.equals(AliPayReconciliations.class)) {
            msgSource = "??????????????????????????????";
            msgTarget = "his?????????????????????";
            sucMsg = "?????????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue();
        } else if (cls.equals(WechatPayReconciliations.class)) {
            msgSource = "???????????????????????????";
            msgTarget = "his?????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_WECHAT.getValue();
        } else if (cls.equals(BankReconciliations.class)) {
            msgSource = "???????????????????????????";
            msgTarget = "his?????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_BANK.getValue();
        } else if (cls.equals(AggregateReconciliations.class)) {
            msgSource = "?????????????????????????????????";
            msgTarget = "his?????????????????????";
            sucMsg = "????????????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue();
        } else if (cls.equals(CashReconciliations.class)) {
            msgSource = "???????????????????????????";
            msgTarget = "his???????????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.CASH_PAYTYPE.getValue();
        } else if (cls.equals(HealthTypeReconciliations.class)) {
            msgSource = "???????????????????????????";
            msgTarget = "his???????????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        } else if (cls.equals(WJYZTReconciliations.class)) {
            msgSource = "??????????????????????????????";
            msgTarget = "his??????????????????????????????";
            sucMsg = "?????????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_WJYZT.getValue();
        } else if (cls.equals(UnionPayReconciliations.class)) {
            msgSource = "??????????????????????????????";
            msgTarget = "his??????????????????????????????";
            sucMsg = "?????????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue();
        } else if(cls.equals(HealthCareBankReconciliations.class)){
        	msgSource = "???????????????????????????????????????";
            msgTarget = "his???????????????????????????????????????";
            sucMsg = "??????????????????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        } else if(cls.equals(OnlineBankReconciliations.class)){
        	msgSource = "???????????????????????????";
            msgTarget = "???????????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue();
        }

        logger.info("????????????:{},????????????:{},???????????????:{},??????????????????:{}", orgCodes, date, sourceList.size(), targetList.size());
        if (sourceList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgSource, payType);
        }

        if (targetList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgTarget, payType);
        }
        if (sourceList.size() > 0 && targetList.size() > 0) {
            saveRecLog(orgCode, date, LogCons.REC_SUCCESS, sucMsg, payType);
        }

        //??????????????????
        for (int i = sourceList.size() - 1; i >= 0; i--) {
            T o1 = sourceList.get(i);
            for (int j = targetList.size() - 1; j >= 0; j--) {
                K o2 = targetList.get(j);
                if (isEqual(o1, o2)) {
                    sourceList.remove(i);
                    targetList.remove(j);
                    break;
                }
            }
        }

        String crossDayReconciliation = propertiesConfigService.findValueByPkey(ProConstants.crossDayReconciliation,
                ProConstants.DEFAULT.get(ProConstants.crossDayReconciliation));
        //??????????????????????????????
        if (crossDayReconciliation.trim().equalsIgnoreCase("true")) {
            String stepNum = propertiesConfigService.findValueByPkey(ProConstants.stepNum,
                    ProConstants.DEFAULT.get(ProConstants.stepNum));
            //????????????????????????his??????????????????
            Date startTime = DateUtil.stringLineToDateTime(DateUtil.getNDayBefore(beginDate, Integer.valueOf(stepNum)));
            Date endTime = DateUtil.stringLineToDateTime(DateUtil.getNDayBefore(endDate, -Integer.valueOf(stepNum)));
            Iterator<ThirdBill> dataIterator = (Iterator<ThirdBill>) sourceList.iterator();
            while (dataIterator.hasNext()) {
                ThirdBill thirdBill = dataIterator.next();
                List<HisTransactionFlow> listData = hisTransactionFlowDao.findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(thirdBill.getOrgNo(), thirdBill.getPayFlowNo(),
                        thirdBill.getOrderState(), startTime, endTime);
                if (listData.size() > 0) {
                    if (isEqua(thirdBill, listData.get(0))) {
                        dataIterator.remove();
                    }
                }
            }
            // His??????????????????????????????????????????
            Iterator<HisTransactionFlow> dataHisIterator = (Iterator<HisTransactionFlow>) targetList.iterator();
            while (dataHisIterator.hasNext()) {
                HisTransactionFlow hisTransactionFlow = dataHisIterator.next();
                List<ThirdBill> listData = thirdBillDao.findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(hisTransactionFlow.getOrgNo(), hisTransactionFlow.getPayFlowNo(),
                        hisTransactionFlow.getOrderState(), startTime, endTime);
                if (listData.size() > 0) {
                    if (isEqua(hisTransactionFlow, listData.get(0))) {
                        dataHisIterator.remove();
                    }
                }
            }
        }

        //?????????????????????????????????
        for (T t : sourceList) {
            TradeCheckFollow rec = convertToTradeCheckFollow(t);
            resultList.add(rec);

        }
        for (K k : targetList) {
            TradeCheckFollow rec = convertToTradeCheckFollow(k);
            resultList.add(rec);
        }

        resultList = this.filterResult(resultList);
        resultList = this.filterAmountZero(resultList);
        // ????????????????????????????????????
//		resultList = filterSingleRefundBillResult(resultList);
        // ??????????????????
        // TODO modify by myz 2019???1???14???15:15:22 ????????? ??????????????????????????????
//		resultList = filterMergeBillResult(resultList);
//		logger.info(" filterResultModify -- > " + resultList.size()+", " + resultList);
        //???????????????????????????????????????(???????????????????????????)
        //TODO ????????????????????????????????? ???????????????????????????????????????
        // TODO modify by myz 2019???1???14???15:15:22 ????????? ????????????????????????
//		resultList=filterResultModifyAll(resultList);
        //logger.info(" filterResultModifyAll -- > "+resultList.size()+", " + resultList);
        logger.info(this.getClass() + "????????????:{}", resultList.size());
        return resultList;
    }

    /**
     * ??????????????????????????????
     *
     * @param t1
     * @param t2
     * @return false ?????????  true  ??????
     */
    private boolean isEqua(Object t1, Object t2) {
        ThirdBill thirdBill = null;
        HisTransactionFlow hisTransactionFlow = null;
        HealthCareOfficial healthCareOfficial = null;
        HealthCareHis healthCareHis = null;
        // ????????????
        if (t1 instanceof ThirdBill || t1 instanceof HisTransactionFlow) {
            if (t1 instanceof ThirdBill) {
                thirdBill = (ThirdBill) t1;
                hisTransactionFlow = (HisTransactionFlow) t2;
            } else if (t1 instanceof HisTransactionFlow) {
                thirdBill = (ThirdBill) t2;
                hisTransactionFlow = (HisTransactionFlow) t1;
            }
            if (!thirdBill.getOrgNo().equals(hisTransactionFlow.getOrgNo())) {
                return false;
            }
            if (!thirdBill.getOrderState().equals(hisTransactionFlow.getOrderState())) {
                return false;
            }
            if (thirdBill.getPayAmount().compareTo(hisTransactionFlow.getPayAmount()) != 0) {
                return false;
            }
        }
        // ????????????  HealthCareOfficial hco, HealthCareHis hch
        if (t1 instanceof HealthCareOfficial || t1 instanceof HealthCareHis) {
            if (t1 instanceof HealthCareOfficial) {
                healthCareOfficial = (HealthCareOfficial) t1;
                healthCareHis = (HealthCareHis) t2;
            } else if (t1 instanceof HealthCareHis) {
                healthCareOfficial = (HealthCareOfficial) t2;
                healthCareHis = (HealthCareHis) t1;
            }
            if (!healthCareOfficial.getOrgNo().equals(healthCareHis.getOrgNo())) {
                return false;
            }
            if (!healthCareOfficial.getOrderState().equals(healthCareHis.getOrderState())) {
                return false;
            }
            if (healthCareOfficial.getCostTotalInsurance().compareTo(healthCareHis.getCostTotalInsurance()) != 0) {
                return false;
            }
        }
        return true;
    }
//	/**
//	 *  ??????????????????????????????
//	 * @param t1
//	 * @param t2
//	 * @return false ?????????  true  ??????
//	 */
//	private boolean isEqua(Object t1, Object t2){
//		ThirdBill thirdBill = null;
//		HisTransactionFlow hisTransactionFlow = null;
//		HealthCareOfficial healthCareOfficial = null;
//		HealthCareHis healthCareHis = null;
//		// ????????????
//		if(t1 instanceof ThirdBill || t1 instanceof HisTransactionFlow){
//			if(t1 instanceof ThirdBill){
//				thirdBill = (ThirdBill) t1;
//				hisTransactionFlow = (HisTransactionFlow) t2;
//			} else if (t1 instanceof HisTransactionFlow) {
//				thirdBill = (ThirdBill) t2;
//				hisTransactionFlow = (HisTransactionFlow) t1;
//			}
//			if (!thirdBill.getOrgNo().equals(hisTransactionFlow.getOrgNo())){
//				return false;
//			}
//			if (!thirdBill.getOrderState().equals(hisTransactionFlow.getOrderState())){
//				return false;
//			}
//			if (thirdBill.getPayAmount().compareTo(hisTransactionFlow.getPayAmount()) != 0){
//				return false;
//			}
//		}
//		// ????????????  HealthCareOfficial hco, HealthCareHis hch
//		if(t1 instanceof HealthCareOfficial || t1 instanceof HealthCareHis){
//			if(t1 instanceof HealthCareOfficial){
//				healthCareOfficial = (HealthCareOfficial) t1;
//				healthCareHis = (HealthCareHis) t2;
//			} else if (t1 instanceof HealthCareHis) {
//				healthCareOfficial = (HealthCareOfficial) t2;
//				healthCareHis = (HealthCareHis) t1;
//			}
//			if (!healthCareOfficial.getOrgNo().equals(healthCareHis.getOrgNo())){
//				return false;
//			}
//			if (!healthCareOfficial.getOrderState().equals(healthCareHis.getOrderState())){
//				return false;
//			}
//			if (healthCareOfficial.getCostTotalInsurance().compareTo(healthCareHis.getCostTotalInsurance()) != 0){
//				return false;
//			}
//		}
//		return true;
//	}

    /**
     * ???????????????0???????????????
     *
     * @param resultList
     * @return
     */
    private List<TradeCheckFollow> filterAmountZero(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        Long now = System.currentTimeMillis();
        logger.info(this.getClass() + "???????????????0???????????????");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            String timestampStr = UUID.randomUUID().toString();
            String businessNo = tf.getBusinessNo() == null ? timestampStr : tf.getBusinessNo();
            String key = tf.getOrgNo().concat(businessNo).concat(String.valueOf(tf.getTradeAmount().doubleValue()))
                    .concat(tf.getPayName()).concat(timestampStr);
            //????????????0
            if (tf.getTradeAmount() != null && tf.getTradeAmount().doubleValue() != 0) {
                tfMap.put(key, tf);
            }
        }
        logger.info(this.getClass() + "???????????????0??????????????? end " + (now - System.currentTimeMillis()) / 1000);

        return new ArrayList<TradeCheckFollow>(tfMap.values());
    }


    /**
     * ??????????????????????????????
     *
     * @param resultList
     */
    private List<TradeCheckFollow> filterResult(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        Integer offsetStepNum = Integer.valueOf(propertiesConfigService.findValueByPkey(ProConstants.offsetStepNum,
                ProConstants.DEFAULT.get(ProConstants.offsetStepNum)));
        Long now = System.currentTimeMillis();
        logger.info(this.getClass() + "??????????????????");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            int countHis = hisTransactionFlowDao.countByOrgCodesAndPaytypeAndDateAndPayflowno(orgCodes, tf.getPayName(),
                    tf.getTradeDate(), tf.getBusinessNo());
            int countThridBill = thirdBillDao.countByOrgCodesAndPaytypeAndDateAndPayflowno(orgCodes, tf.getPayName(),
                    tf.getTradeDate(), tf.getBusinessNo());
            String timestampStr = "";
            // his??????????????????????????????????????????????????????????????????
            if (countHis > 0 && countThridBill > 0) {
                timestampStr = String.valueOf(System.currentTimeMillis());
            }
            String businessNo = tf.getBusinessNo();
            // ?????????????????????????????????
            if (StringUtils.isEmpty(businessNo)) {
                businessNo = String.valueOf(System.currentTimeMillis());
            }
            String key = tf.getOrgNo().concat(businessNo).concat(String.valueOf(tf.getTradeAmount().doubleValue()))
                    .concat(tf.getPayName()).concat(timestampStr);
            TradeCheckFollow mapObj = tfMap.get(key);
            if (mapObj == null) {
                tfMap.put(key, tf);
                // ???????????????????????? ???????????? ????????????
                if (offsetStepNum > 0 && countHis == 0) {
                    // ?????????????????? ?????????????????? ????????????????????????????????????  ?????????????????????
                    Date billDate = DateUtil.getStringToDateTime(resultList.get(0).getTradeDate());
                    String startDate = DateUtil.getSpecifiedDayBeforeDay(billDate, offsetStepNum);
                    String endDate = DateUtil.getSpecifiedDayAfter(resultList.get(0).getTradeDate(), offsetStepNum);
                    List<ThirdBill> thirdBillList = thirdBillDao.findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(tf.getOrgNo(), tf.getBusinessNo(), tf.getTradeAmount(), DateUtil.getStringToDateTime(startDate), DateUtil.getStringToDateTime(endDate));
                    List<HisTransactionFlow> hisBillList = hisTransactionFlowDao.findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(tf.getOrgNo(), tf.getBusinessNo(), tf.getTradeAmount(), DateUtil.getStringToDateTime(startDate), DateUtil.getStringToDateTime(endDate));
                    for (ThirdBill thirdBill : thirdBillList) {
                        if (!tf.getTradeName().equals(thirdBill.getOrderState())) {
                            tfMap.remove(key);
                            logger.info("?????????????????????????????????????????????????????????" + tf.getBusinessNo());
                        }
                    }
                    for (HisTransactionFlow hisTransactionFlow : hisBillList) {
                        if (!tf.getTradeName().equals(hisTransactionFlow.getOrderState())) {
                            tfMap.remove(key);
                            logger.info("???His????????????????????????????????????????????????" + tf.getBusinessNo());
                        }
                    }
                }
            } else {
                // ??????????????????????????????(?????????????????????)
                if (!tf.getTradeName().equals(mapObj.getTradeName()) && !"0".equals(mapObj.getBusinessNo())) {
                    tfMap.remove(key);
                    logger.error("?????????????????????????????????????????????" + mapObj.getBusinessNo());
                } else {
                    // ?????????????????????????????????
                    key += UUID.randomUUID().toString();
                    tfMap.put(key, tf);
                }
            }
        }
        logger.info(this.getClass() + "?????????????????? end " + (now - System.currentTimeMillis()) / 1000);

        return new ArrayList<TradeCheckFollow>(tfMap.values());
    }

    private String[] getOrgNoList(String orgCode) {
        List<Organization> orgList = organizationService.findByParentCode(orgCode);
        String[] orgs = null;
        if (orgList != null && orgList.size() > 0) {
            orgs = new String[orgList.size() + 1];
            orgs[0] = orgCode;
            for (int i = 0; i < orgList.size(); i++) {
                orgs[i + 1] = orgList.get(i).getCode();
            }
        } else {
            orgs = new String[1];
            orgs[0] = orgCode;
        }
        return orgs;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param resultList
     * @return
     */
    private List<TradeCheckFollow> filterResultModifyAll(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        String nowTime = resultList.get(0).getTradeDate();
        Long start = System.currentTimeMillis();
        logger.info("????????????????????????--------------------start -----------");
        //?????????????????????map
        Map<String, List<TradeCheckFollow>> map = new HashMap<>();
        //??????????????????map?????????map
        Map<String, TradeCheckFollow> maping = new HashMap<>();
        //?????????????????????map
        //??????????????????map
        Map<String, TradeCheckFollowDeal> dealAllMap = new HashMap<>();
        //????????????????????????
        List<TradeCheckFollowDeal> dealDeleteList = new ArrayList<>();
        //??????????????????????????????
        List<TradeCheckFollowDeal> dealSaveList = new ArrayList<>();
        //??????????????????????????????????????????
        List<TradeCheckFollowDeal> dealList = tradeCheckFollowDealDao.findAll();
        //?????????????????????????????????
        List<TradeCheckFollow> list = tradeCheckFollowDao.findAll();
        //????????????????????????????????????map?????????
        for (TradeCheckFollowDeal v : dealList) {
            //???????????????map??????
            dealAllMap.put(v.getOrgCode() + "," + v.getPayFlowNo() + "," + v.getTradeDatetime(), v);
        }
        //?????????????????????map????????????key???????????????????????????
        for (TradeCheckFollow v : list) {
            //??????????????????????????????????????????map??????
            if (v.getTradeDate().equals(nowTime)) {
                maping.put(v.getBusinessNo(), v);
                continue;
            }
            //???????????????????????????????????????
            List<TradeCheckFollow> voList = map.get(v.getBusinessNo());
            if (voList != null && voList.size() > 0) {
                //???????????????,????????????,??????????????????
                if (v.getCheckState() == CommonEnum.BillBalance.HISDC.getValue()) {
                    v.setTradeAmount(v.getTradeAmount().negate());
                }
                voList.add(v);
                map.put(v.getBusinessNo(), voList);
            } else {
                List<TradeCheckFollow> newList = new ArrayList<TradeCheckFollow>();
                //???????????????,????????????,??????????????????
                if (v.getCheckState() == CommonEnum.BillBalance.HISDC.getValue()) {
                    v.setTradeAmount(v.getTradeAmount().negate());
                }
                newList.add(v);
                map.put(v.getBusinessNo(), newList);
            }
        }
        //??????????????????????????????
        for (TradeCheckFollow v : resultList) {
            //????????????????????????????????????map
            List<TradeCheckFollow> voList = map.get(v.getBusinessNo());
            //map?????????
            if (voList != null && voList.size() > 0) {
                for (TradeCheckFollow z : voList) {
                    //????????????????????????,??????????????????????????????????????????????????????????????????????????????
                    if (dealAllMap.get(z.getOrgNo() + "," + z.getBusinessNo() + "," + z.getTradeDate()) != null) {
                        continue;
                    }
                    //????????????????????????,???????????????
                    if (z.getTradeAmount().add(v.getTradeAmount()).compareTo(new BigDecimal(0)) == 0) {
                        // ?????????????????????
                        saveDeal(v, dealAllMap, dealSaveList);
                        // ?????????????????????
                        saveDeal(z, dealAllMap, dealSaveList);
                    }
                    break;
                }
            }
            //map???????????????????????????????????????
            else {
                TradeCheckFollowDeal deal = dealAllMap.get(v.getOrgNo() + "," + v.getBusinessNo() + "," + v.getTradeDate());
                if (deal != null) {
                    dealDeleteList.add(deal);
                }
            }
        }
        //??????????????????
        deleteDeal(dealDeleteList);
        //??????????????????
        saveDeal(dealSaveList);
		
		/*for (int i = resultList.size() - 1; i >= 0; i--) {
			TradeCheckFollow tf = resultList.get(i);
			if(StringUtils.isBlank(tf.getBusinessNo()))continue;
			Integer checkState=tf.getCheckState();
			//??????
			if(checkState==CommonEnum.BillBalance.HISDC.getValue()) {
				checkState=CommonEnum.BillBalance.THIRDDC.getValue();
			}else {//??????
				checkState=CommonEnum.BillBalance.HISDC.getValue();
			}
			//??????his????????????????????????????????????????????????
			List<TradeCheckFollow> list = tradeCheckFollowDao.findByBusinessNoAndTradeDateNotAndCheckState(tf.getBusinessNo(),tf.getTradeDate(),checkState);
			if (list != null && list.size() > 0) {// ????????????????????????????????????????????????
				//????????????,???????????????????????????????????????????????????????????????
				for(TradeCheckFollow v:list) {
					if(tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime
							(v.getBusinessNo(),v.getOrgNo(),v.getTradeDate())==null) {
						if(tf.getTradeAmount().abs().compareTo(v.getTradeAmount().abs())==0) {//??????
							// ?????????????????????
							saveDeal(tf);
							// ?????????????????????
							saveDeal(v);
							logger.info("???????????????????????????????????? + " + resultList.get(i) + ", ?????????????????? = " + list);
						}
						break;
					}
				}
			}else {//?????????????????????????????????????????????????????????????????????????????????????????????
				TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
				if(deal!=null) {
					tradeCheckFollowDealDao.delete(deal);
					//tradeCheckFollowDealDao.deleteByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
				}
			}
		}*/

        logger.info("??????????????????????????????--------------------end -----------, ?????????" + (System.currentTimeMillis() - start));
        return resultList;
    }
	
	/*private void saveDeal(TradeCheckFollow tf) {
		TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
		if(deal==null) {
			TradeCheckFollowDeal vo =new TradeCheckFollowDeal();
			vo.setPayFlowNo(tf.getBusinessNo());
			vo.setDescription("??????????????????");
			vo.setCreatedDate(new Date());
			vo.setExceptionState(String.valueOf(tf.getCheckState()));
			vo.setDealAmount(tf.getTradeAmount());
			vo.setOrgCode(tf.getOrgNo());
			vo.setTradeDatetime(tf.getTradeDate());
			tradeCheckFollowDealDao.save(vo);
		}
	}*/

    private void saveDeal(TradeCheckFollow tf, Map<String, TradeCheckFollowDeal> dealAllMap, List<TradeCheckFollowDeal> dealSaveList) {
        TradeCheckFollowDeal deal = dealAllMap.get(tf.getOrgNo() + "," + tf.getBusinessNo() + "," + tf.getTradeDate());
        if (deal == null) {
            TradeCheckFollowDeal vo = new TradeCheckFollowDeal();
            vo.setPayFlowNo(tf.getBusinessNo());
            vo.setDescription("??????????????????");
            vo.setCreatedDate(new Date());
            vo.setExceptionState(String.valueOf(tf.getCheckState()));
            vo.setDealAmount(tf.getTradeAmount());
            vo.setOrgCode(tf.getOrgNo());
            vo.setTradeDatetime(tf.getTradeDate());
            dealSaveList.add(vo);
        }
    }

    //????????????
    private void saveDeal(List<TradeCheckFollowDeal> dealSaveList) {
		 /*for (int i = 0; i < dealSaveList.size(); i++) {
            entityManager.persist(dealSaveList.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }*/
        tradeCheckFollowDealDao.save(dealSaveList);
    }

    //????????????
    private void deleteDeal(List<TradeCheckFollowDeal> dealDeleteList) {
		/*for (int i = 0; i < dealDeleteList.size(); i++) {
            entityManager.remove(dealDeleteList.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }*/
        tradeCheckFollowDealDao.delete(dealDeleteList);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     */
    @SuppressWarnings("rawtypes")
    @Override
    public final List<HealthException> compareHealthBill() throws Exception {

        List<HealthException> resultList = new ArrayList<>();
        List<T> sourceList = sourceList();
        List<K> targetList = targetList();
        Class cls = this.getClass();
        String msgSource = "";
        String msgTarget = "";
        String sucMsg = "";
        String payType = "";

        if (cls.equals(HealthCareOfficiaReconciliations.class)) {
            msgSource = "???????????????????????????";
            msgTarget = "his???????????????????????????";
            sucMsg = "??????????????????";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        }

        logger.info("????????????:{},????????????:{},???????????????:{},??????????????????:{}", orgCodes, date, sourceList.size(), targetList.size());
        if (sourceList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgSource, payType);
        }

        if (targetList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgTarget, payType);
        }
        if (sourceList.size() > 0 && targetList.size() > 0) {
            saveRecLog(orgCode, date, LogCons.REC_SUCCESS, sucMsg, payType);
        }

        //??????????????????
        for (int i = sourceList.size() - 1; i >= 0; i--) {
            // ????????????
            T o1 = sourceList.get(i);
            for (int j = targetList.size() - 1; j >= 0; j--) {
                // His
                K o2 = targetList.get(j);
                if (isEqual(o1, o2)) {
                    sourceList.remove(i);
                    targetList.remove(j);
                    break;
                }
            }
        }

        //??????????????????????????????  HealthCareOfficial hco, HealthCareHis hch
        String crossDayReconciliation = propertiesConfigService.findValueByPkey(ProConstants.crossDayReconciliation,
                ProConstants.DEFAULT.get(ProConstants.crossDayReconciliation));
        if (crossDayReconciliation.trim().equalsIgnoreCase("true")) {
            // ???????????????????????????
            List<String> crossDayRecFlowNoList = new ArrayList<>();
            //??????????????????????????????his??????????????????
            String stepNum = propertiesConfigService.findValueByPkey(ProConstants.stepNum,
                    ProConstants.DEFAULT.get(ProConstants.stepNum));
            Date startTime = DateUtil.stringLineToDateTime(DateUtil.getNDayBefore(beginDate, Integer.valueOf(stepNum)));
            Date endTime = DateUtil.addDay(endDate, Integer.valueOf(stepNum));
            Iterator<HealthCareOfficial> dataBillIterator = (Iterator<HealthCareOfficial>) sourceList.iterator();
            while (dataBillIterator.hasNext()) {
                HealthCareOfficial healthCareOfficial = dataBillIterator.next();
                List<HealthCareHis> listData = healthCareHisDao.findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(healthCareOfficial.getOrgNo(), healthCareOfficial.getPayFlowNo(),
                        healthCareOfficial.getOrderState(), startTime, endTime);
                if (listData.size() > 0) {
                    if (isEqua(healthCareOfficial, listData.get(0))) {
                        // 2019-11-27 ????????????????????????
                        String officialDateStr = DateUtil.transferDateToDateFormat("yyyy-MM-dd", healthCareOfficial.getTradeDatatime());
                        String hisDateStr = DateUtil.transferDateToDateFormat("yyyy-MM-dd", listData.get(0).getTradeDatatime());
                        if (!officialDateStr.equals(hisDateStr)) {
                            /*dataBillIterator.remove();*/
                            healthCareOfficial.setCrossDayRec("true");
                            crossDayRecFlowNoList.add(healthCareOfficial.getPayFlowNo());
                        }
                    }
                }
            }
            // ??????His??????????????????????????????????????????
            Iterator<HealthCareHis> dataHisIterator = (Iterator<HealthCareHis>) targetList.iterator();
            while (dataHisIterator.hasNext()) {
                HealthCareHis healthCareHis = dataHisIterator.next();
                List<HealthCareOfficial> listData = healthCareOfficialDao.findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(healthCareHis.getOrgNo(), healthCareHis.getPayFlowNo(),
                        healthCareHis.getOrderState(), startTime, endTime);
                if (listData.size() > 0) {
                    if (isEqua(healthCareHis, listData.get(0))) {
                        // 2019-11-27 ????????????????????????
                        String hisDateStr = DateUtil.transferDateToDateFormat("yyyy-MM-dd", healthCareHis.getTradeDatatime());
                        String officialDateStr = DateUtil.transferDateToDateFormat("yyyy-MM-dd", listData.get(0).getTradeDatatime());
                        if (!officialDateStr.equals(hisDateStr)) {
                            /*dataHisIterator.remove();*/
                            healthCareHis.setCrossDayRec("true");
                            crossDayRecFlowNoList.add(healthCareHis.getPayFlowNo());
                        }
                    }
                }
            }
            if (crossDayRecFlowNoList.size() > 0) {
                healthExceptionDao.updateExceptionByPayFlowNoIn(crossDayRecFlowNoList);
            }
        }

        //?????????????????????????????????
        for (K k : targetList) {
            HealthException rec = convertToHealthException(k);
            resultList.add(rec);
        }
        for (T t : sourceList) {
            HealthException rec = convertToHealthException(t);
            resultList.add(rec);
        }
        //???????????????????????????(???????????????????????????)
        resultList = filterHealthResult(resultList);
        logger.info("????????????:{}", resultList.size());
        return resultList;
    }


    /**
     * ?????????????????????????????????
     */
    private List<TradeCheckFollow> filterSingleRefundBillResult(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        // ?????????map??????
        Map<String, List<TradeCheckFollow>> singleRefundMap = new HashMap<>(resultList.size());
        // ?????????????????????????????????????????????
        Map<String, TradeCheckFollow> tfmap = new HashMap<>();

        for (TradeCheckFollow tf : resultList) {
            // ????????????????????????????????????
            String key = "";
            if (StringUtils.isBlank(tf.getBusinessNo())) {
                key = tf.getOrgNo().concat(System.currentTimeMillis() + RandomCodeUtil.generateWord(4))
                        .concat(tf.getPayName());
            } else {
                key = tf.getOrgNo().concat(tf.getBusinessNo()).concat(tf.getPayName());
            }
            TradeCheckFollow mapObj = tfmap.get(key);
            if (mapObj == null) {
                // ?????????????????????????????????
                String tradeName = tf.getTradeName();
                if (StringUtils.isNotBlank(tradeName) && EnumTypeOfInt.TRADE_TYPE_REFUND.getValue().equals(tradeName)) {
                    List<TradeCheckFollow> refundFollows = new ArrayList<>();
                    refundFollows.add(tf);
                    singleRefundMap.put(key, refundFollows);
                } else {
                    // ????????????????????????key
                    tfmap.put(System.currentTimeMillis() + RandomCodeUtil.generateWord(4), tf);
                }
            } else {
                // checkState ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (!tf.getCheckState().equals(mapObj.getCheckState())) {
                    singleRefundMap.remove(key);

                    // ????????????????????????key
                    tfmap.put(System.currentTimeMillis() + RandomCodeUtil.generateWord(4), tf);
                } else {
                    List<TradeCheckFollow> refundFollows = singleRefundMap.get(key);
                    refundFollows.add(mapObj);
                    singleRefundMap.put(key, refundFollows);
                }

            }
        }

        // ????????????????????????
        ArrayList<TradeCheckFollow> follows = dealSingleRefundListBill(singleRefundMap);

        ArrayList<TradeCheckFollow> result = new ArrayList<TradeCheckFollow>(tfmap.values());
        result.addAll(follows);
        return result;
    }

    // ??????????????????????????????
    private List<TradeCheckFollow> filterMergeBillResult(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        //
        List<TradeCheckFollow> result = new ArrayList<>();
        // ?????????????????????????????????????????????
        Map<String, List<TradeCheckFollow>> tfmap = new HashMap<>();

        for (TradeCheckFollow tf : resultList) {
            // ????????????????????????????????????
            String key = "";
            if (StringUtils.isBlank(tf.getBusinessNo())) {
                result.add(tf);
                continue;
            }
            key = tf.getOrgNo().concat(tf.getBusinessNo());
            if (tfmap.containsKey(key)) {
                List<TradeCheckFollow> tcfList = tfmap.get(key);
                tcfList.add(tf);
                tfmap.put(key, tcfList);
            } else {
                List<TradeCheckFollow> ts = new ArrayList<>();
                ts.add(tf);
                tfmap.put(key, ts);
            }
        }

        // ????????????
        List<TradeCheckFollow> follows = mergeAllExceptionBill(tfmap);
        result.addAll(follows);
        return result;
    }

    private List<TradeCheckFollow> filterResultModify1(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        Log.info("??????????????????");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        //?????????map??????
        Map<String, TradeCheckFollow> singleRefundMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            // ????????????????????????????????????
            String key = "";
            if (StringUtils.isBlank(tf.getBusinessNo())) {
                key = tf.getOrgNo().concat(System.currentTimeMillis() + RandomCodeUtil.generateWord(4)).concat(tf.getPayName());
            } else {
                key = tf.getOrgNo().concat(tf.getBusinessNo()).concat(tf.getPayName());
            }
//			String key = tf.getOrgNo().concat(tf.getBusinessNo()).concat(tf.getPayName());;
//			String key = tf.getOrgNo().concat(tf.getBusinessNo()).concat(tf.getPayName()).concat(String.valueOf(tf.getCheckState()));
            TradeCheckFollow mapObj = tfMap.get(key);
            if (mapObj == null) {
                tfMap.put(key, tf);
                String tradeName = tf.getTradeName();
                // ?????????????????????????????????
                if (StringUtils.isNotBlank(tradeName) && EnumTypeOfInt.TRADE_TYPE_REFUND.getValue().equals(tradeName)) {
                    singleRefundMap.put(key, tf);
                }
            } else {
                singleRefundMap.remove(key);
                BigDecimal tradeAmount1 = tf.getTradeAmount();
                BigDecimal tradeAmount2 = mapObj.getTradeAmount();
                String tradeName1 = tf.getTradeName();
                String tradeName2 = mapObj.getTradeName();

                // ?????????????????????????????????(?????????????????????),??????
                if (!tradeName1.equals(tradeName2) && tradeAmount1.abs().compareTo(tradeAmount2.abs()) == 0) {
                    tfMap.remove(key);
                } else {
                    // ??????????????????????????????
                    if (!tf.getCheckState().equals(mapObj.getCheckState())) {
                        TradeCheckFollow tradeCheckFollow = mergeExceptionBill(tf, mapObj);
                        if (tradeCheckFollow != null) {
                            // ?????????????????????????????????????????????
                            tfMap.put(key, tradeCheckFollow);
                        }
                    }
                }
            }
        }

        // ????????????????????????
        ArrayList<TradeCheckFollow> follows = dealSingleRefundBill(singleRefundMap);

        ArrayList<TradeCheckFollow> result = new ArrayList<TradeCheckFollow>(tfMap.values());
        result.addAll(follows);
        return result;
    }

    // ???????????????????????????
    public ArrayList<TradeCheckFollow> dealSingleRefundListBill(Map<String, List<TradeCheckFollow>> singleRefundMap) {
        if (singleRefundMap.isEmpty()) {
            return new ArrayList<TradeCheckFollow>();
        }
        // 3 5?????? 2 6??????
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        ArrayList<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>();
        for (List<TradeCheckFollow> list : singleRefundMap.values()) {
            follows.addAll(list);
        }
        for (TradeCheckFollow tradeCheckFollow : follows) {
            // ??????
            if (shotCheckStates.contains(String.valueOf(tradeCheckFollow.getCheckState()))) {
                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.THIRDDC.getValue());
            } else {
                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.HISDC.getValue());
            }
            BigDecimal tradeAmount = tradeCheckFollow.getTradeAmount();
            if (tradeAmount != null) {
                tradeCheckFollow.setTradeAmount(tradeAmount.abs());
            }
        }
        return follows;
    }

    public List<TradeCheckFollow> mergeAllExceptionBill(Map<String, List<TradeCheckFollow>> map) {
        logger.info("???????????????????????????");
        List<TradeCheckFollow> follows = new ArrayList<>();
        // 3 5?????? 2 6??????
        String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        for (List<TradeCheckFollow> list : map.values()) {
            if (list.size() == 0) {
                continue;
            }
            BigDecimal amount = new BigDecimal(0);
            for (TradeCheckFollow t : list) {
                Integer checkState = t.getCheckState();
                if (null != checkState && longCheckStates.contains(checkState.toString())) {
                    amount = amount.add(t.getTradeAmount().abs());
                } else {
                    amount = amount.subtract(t.getTradeAmount().abs());
                }
            }
            TradeCheckFollow t = list.get(0);
            t.setTradeAmount(amount.abs());
            // ?????? 2
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                t.setCheckState(CommonEnum.BillBalance.HISDC.getValue());
            }
            // ??????
            else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                t.setCheckState(CommonEnum.BillBalance.THIRDDC.getValue());
                // ??????
            } else {
                continue;
            }
            follows.add(t);
        }
        return follows;
    }

    public ArrayList<TradeCheckFollow> dealSingleRefundBill(Map<String, TradeCheckFollow> singleRefundMap) {
        if (singleRefundMap.isEmpty()) {
            return new ArrayList<TradeCheckFollow>();
        }
        // 3 5?????? 2 6??????
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        ArrayList<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>(singleRefundMap.values());
        for (TradeCheckFollow tradeCheckFollow : follows) {
            // ??????
            if (shotCheckStates.contains(String.valueOf(tradeCheckFollow.getCheckState()))) {
                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.THIRDDC.getValue());
            } else {
                tradeCheckFollow.setCheckState(CommonEnum.BillBalance.HISDC.getValue());
            }
            BigDecimal tradeAmount = tradeCheckFollow.getTradeAmount();
            if (tradeAmount != null) {
                tradeCheckFollow.setTradeAmount(tradeAmount.abs());
            }
        }
        return follows;
    }

    public TradeCheckFollow mergeExceptionBill(TradeCheckFollow tFollow1, TradeCheckFollow tFollow2) {
        logger.info("???????????????????????????");
        // 3 5?????? 2 6??????
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();

        // ?????????????????????
        TradeCheckFollow tfShot = null, tfLong = null;
        if (shotCheckStates.contains(String.valueOf(tFollow1.getCheckState()))) {
            tfShot = tFollow1;
            tfLong = tFollow2;
        } else {
            tfShot = tFollow2;
            tfLong = tFollow1;
        }

        TradeCheckFollow tradeCheckFollow = new TradeCheckFollow();
        BigDecimal tradeAmountShot = tfShot.getTradeAmount().abs();
        BigDecimal tradeAmountLong = tfLong.getTradeAmount().abs();
        String tradeNameShot = tfShot.getTradeName();
        String tradeNameLong = tfLong.getTradeName();

        // ?????????????????????????????????
        if (!tradeNameShot.equals(tradeNameLong)) {
            logger.error("???????????????????????????????????????????????? " + tfShot.getBusinessNo());
            return null;
        }
        // ??????????????????
        BigDecimal diffAmount = tradeAmountShot.subtract(tradeAmountLong);

        Integer checkStateShot = tfShot.getCheckState();
        Integer checkStateLong = tfLong.getCheckState();
        Integer checkState = null;
        // ????????????
        if (tradeNameShot.equals(EnumTypeOfInt.TRADE_TYPE_PAY.getValue())) {
            // ??????
            if (diffAmount.compareTo(BigDecimal.ZERO) > 0) {
                checkState = checkStateShot;
                // ??????
            } else {
                checkState = checkStateLong;
            }

            // ????????????
        } else if (tradeNameShot.equals(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())) {
            // ??????
            if (diffAmount.compareTo(BigDecimal.ZERO) > 0) {
                checkState = checkStateLong;
                // ??????
            } else {
                checkState = checkStateShot;
            }
        }

        BeanUtils.copyProperties(tfLong, tradeCheckFollow);
        tradeCheckFollow.setCheckState(checkState);
        tradeCheckFollow.setTradeAmount(diffAmount.abs());
        return tradeCheckFollow;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param resultList
     */
    private List<HealthException> filterHealthResult(List<HealthException> resultList) {
        if (resultList == null || resultList.size() == 0) {
            return resultList;
        }
        //??????HIS?????????????????????
        Set<String> hisSet = new HashSet<String>();
        Log.info("??????????????????");
        Map<String, HealthException> tfMap = new HashMap<>(resultList.size());
        for (HealthException tf : resultList) {
            //????????????????????????HIS?????????????????????
            if (CommonEnum.BillBalance.HEALTHCAREOFFI.getValue().equals(tf.getCheckState()) &&
                    hisSet.contains(tf.getPayFlowNo())) {
                String key = tf.getOrgNo().concat(tf.getPayFlowNo()).concat(tf.getCheckState().toString());
                tfMap.remove(key);
                continue;
            }
            String key = tf.getOrgNo().concat(tf.getPayFlowNo()).concat(tf.getCheckState().toString());
            HealthException mapObj = tfMap.get(key);
            if (mapObj == null) {
                tfMap.put(key, tf);
            } else {
                //??????????????????(?????????????????????)
                if (!tf.getOrderState().equals(mapObj.getOrderState())) {
                    //?????????HIS????????????????????????
                    if (CommonEnum.BillBalance.HEALTHCAREHIS.getValue().equals(tf.getCheckState())) {
                        hisSet.add(tf.getPayFlowNo());
                    }
                    tfMap.remove(key);
                }
            }
        }
        return new ArrayList<HealthException>(tfMap.values());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param orgCode
     * @return
     */
    private Set<String> initOrgCodeSet(String orgCode) {
        Set<String> orgCodeSet = new HashSet<>();
        orgCodeSet.add(orgCode);
        Organization org = organizationService.findByCode(orgCode);
        if (org != null) {
            for (Organization child : org.getChildren()) {
                orgCodeSet.add(child.getCode());
            }
        }
        return orgCodeSet;
    }

    /**
     * ????????????
     *
     * @param orgCode
     * @param date
     * @param state
     * @param msg
     */
    private void saveRecLog(String orgCode, String date,
                            int state, String msg, String payType) {
        RecLogDetails recLogDetails = new RecLogDetails();
        recLogDetails.setOrgCode(orgCode);
        recLogDetails.setRecState(state);
        recLogDetails.setExceptionRemark(msg);
        recLogDetails.setOrderDate(date);
        String logType = LogCons.LOG_TYPE_REC;
        recLogDetails.setLogType(logType);
        recLogDetails.setPayType(payType);
        recLogDetails.setCreatedDate(DateUtil.getCurrentDateTime());
        recLogDetailsDao.deleteByOrderDateAndOrgCodeAndLogTypeAndPayType(date,
                orgCode, logType, payType);
        recLogDetailsDao.save(recLogDetails);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param o1
     * @param o2
     * @return
     */
    protected abstract boolean isEqual(T o1, K o2);

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    protected abstract List<T> sourceList();

    /**
     * ????????????????????????,???????????????
     *
     * @return
     */

    protected abstract List<K> targetList();


    /**
     * ???????????????????????????????????????Reconciliation??????
     *
     * @param o
     * @return
     */
    private final HealthException convertToHealthException(Object o) {
        HealthException vo = new HealthException();
        vo.setCreatedDate(new Date());
        if (o instanceof HealthCareHis) {
            //??????his
            HealthCareHis hisBill = (HealthCareHis) o;
            vo.setOrgNo(hisBill.getOrgNo());
            vo.setPayFlowNo(hisBill.getPayFlowNo());
            vo.setTradeDataTime(hisBill.getTradeDatatime());
            vo.setOrderState(hisBill.getOrderState());
            vo.setCostWhole(hisBill.getCostWhole());
            vo.setPatientName(hisBill.getPatientName());
            vo.setCostAccount(hisBill.getCostAccount());
            vo.setHealthType(hisBill.getHealthcareTypeCode());
            vo.setCheckState(CommonEnum.BillBalance.HEALTHCAREHIS.getValue());
            vo.setPatType(hisBill.getPatType());
            vo.setCostAll(hisBill.getCostAll());
            vo.setCostTotalInsuranceHis(hisBill.getCostTotalInsurance());
            // ???????????????
            vo.setSocialComputerNumber(hisBill.getSocialComputerNumber());
            // ?????????????????????/?????????
            vo.setBusnessType(hisBill.getBusnessType());
            String crossDayRec = hisBill.getCrossDayRec();
            if (crossDayRec != null && !crossDayRec.trim().equals("")) {
                vo.setCrossDayRec(crossDayRec);
            }
        } else if (o instanceof HealthCareOfficial) {
            HealthCareOfficial officialBill = (HealthCareOfficial) o;
            //????????????
            vo.setOrgNo(officialBill.getOrgNo());
            vo.setPayFlowNo(officialBill.getPayFlowNo());
            vo.setTradeDataTime(officialBill.getTradeDatatime());
            vo.setPatientName(officialBill.getPatientName());
            vo.setOrderState(officialBill.getOrderState());
            vo.setCostWhole(officialBill.getCostWhole());
            vo.setCostAccount(officialBill.getCostAccount());
            vo.setHealthType(officialBill.getHealthcareTypeCode());
            vo.setCheckState(CommonEnum.BillBalance.HEALTHCAREOFFI.getValue());
            vo.setCostAll(officialBill.getCostAll());
            vo.setCostTotalInsurance(officialBill.getCostTotalInsurance());
            vo.setPatType(officialBill.getPatType());
            // ????????????
            vo.setHealthCode(officialBill.getSocialInsuranceNo());
            // ???????????????
            vo.setShopFlowNo(officialBill.getShopFlowNo());
            String crossDayRec = officialBill.getCrossDayRec();
            if (crossDayRec != null && !crossDayRec.trim().equals("")) {
                vo.setCrossDayRec(crossDayRec);
            }
        }
        return vo;
    }


    /**
     * ???????????????????????????????????????Reconciliation??????
     *
     * @param o
     * @return
     */
    private final TradeCheckFollow convertToTradeCheckFollow(Object o) {
        TradeCheckFollow tradeCheckFollow = new TradeCheckFollow();
        tradeCheckFollow.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
        tradeCheckFollow.setCreatedDate(new Date());
        tradeCheckFollow.setUserId(0L);
        if (o instanceof ThirdBill) {
            //??????
            ThirdBill thirdBill = (ThirdBill) o;
            tradeCheckFollow.setOrgNo(thirdBill.getOrgNo());
            tradeCheckFollow.setBusinessNo(thirdBill.getPayFlowNo() == null ? thirdBill.getShopFlowNo() : thirdBill.getPayFlowNo());
            tradeCheckFollow.setPayName(thirdBill.getRecPayType());
            tradeCheckFollow.setTradeTime(thirdBill.getTradeDatatime());
            tradeCheckFollow.setTradeDate(sdf.format(thirdBill.getTradeDatatime()));
            tradeCheckFollow.setPaymentAccount(thirdBill.getPayAccount());
            tradeCheckFollow.setTradeAmount(thirdBill.getPayAmount() == null ? BigDecimal.ZERO : thirdBill.getPayAmount().abs());
            tradeCheckFollow.setTradeName(thirdBill.getOrderState());
            tradeCheckFollow.setPaymentRefundFlow(thirdBill.getOriPayFlowNo());
            Integer checkState = StringUtils.equals(thirdBill.getOrderState(), EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? CommonEnum.BillBalance.HISDC.getValue()
                    : CommonEnum.BillBalance.THIRDDC.getValue();
            tradeCheckFollow.setCheckState(checkState);
            tradeCheckFollow.setBillSource(thirdBill.getBillSource());
            tradeCheckFollow.setPayNo(thirdBill.getOrderNo());
            tradeCheckFollow.setHisFlowNo(thirdBill.getOutTradeNo());
            tradeCheckFollow.setBusinessType(thirdBill.getPayBusinessType());
            String patType = thirdBill.getPatType();
            tradeCheckFollow.setPatType("zy".equalsIgnoreCase(patType) ? com.yiban.rec.bill.parse.util.EnumTypeOfInt.PAT_TYPE_ZY.getValue()
                    : com.yiban.rec.bill.parse.util.EnumTypeOfInt.PAT_TYPE_MZ.getValue());
            // ???????????????
            tradeCheckFollow.setShopFlowNo(thirdBill.getShopFlowNo());
            // ?????????
            tradeCheckFollow.setShopNo(thirdBill.getPayShopNo());
            tradeCheckFollow.setRecThridId(thirdBill.getId());
            // ?????????
            tradeCheckFollow.setTerminalNo(thirdBill.getPayTermNo());
            // ?????????
            tradeCheckFollow.setReferenceNum(thirdBill.getReferenceNum());
            // ????????????????????????
            tradeCheckFollow.setRequireRefund(thirdBill.getRequireRefund());
            // ????????????
            tradeCheckFollow.setPaymentAccount(thirdBill.getPayAccount());
            // ?????????
            tradeCheckFollow.setInvoiceNo(thirdBill.getInvoiceNo());
        } else if (o instanceof HealthCareHis) {
            //??????his
            HealthCareHis hisBill = (HealthCareHis) o;
            tradeCheckFollow.setOrgNo(hisBill.getOrgNo());
            tradeCheckFollow.setBusinessNo(hisBill.getPayFlowNo());
            tradeCheckFollow.setPayName(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
            tradeCheckFollow.setTradeTime(hisBill.getTradeDatatime());
            tradeCheckFollow.setTradeDate(sdf.format(hisBill.getTradeDatatime()));
            tradeCheckFollow.setTradeAmount(hisBill.getCostAccount() == null ? BigDecimal.ZERO : hisBill.getCostAccount().abs());
            tradeCheckFollow.setTradeName(hisBill.getOrderState());
            Integer checkState = StringUtils.equals(hisBill.getOrderState(), EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? CommonEnum.BillBalance.HEALTHCAREOFFI.getValue()
                    : CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
            tradeCheckFollow.setCheckState(checkState);
            tradeCheckFollow.setBillSource(hisBill.getBillSource());
            tradeCheckFollow.setRecHisId(hisBill.getId());
        } else if (o instanceof HealthCareOfficial) {
            HealthCareOfficial officialBill = (HealthCareOfficial) o;
            //????????????
            tradeCheckFollow.setOrgNo(officialBill.getOrgNo());
            tradeCheckFollow.setBusinessNo(officialBill.getPayFlowNo());
            tradeCheckFollow.setPayName(EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue());
            tradeCheckFollow.setTradeTime(officialBill.getTradeDatatime());
            tradeCheckFollow.setTradeDate(sdf.format(officialBill.getTradeDatatime()));
            tradeCheckFollow.setTradeAmount(officialBill.getCostAccount() == null ? BigDecimal.ZERO : officialBill.getCostAccount().abs());
            tradeCheckFollow.setTradeName(officialBill.getOrderState());
            tradeCheckFollow.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
            Integer checkState = StringUtils.equals(officialBill.getOrderState(), EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? CommonEnum.BillBalance.HEALTHCAREHIS.getValue()
                    : CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
            tradeCheckFollow.setCheckState(checkState);
            tradeCheckFollow.setBillSource(officialBill.getBillSource());
            tradeCheckFollow.setRecThridId(officialBill.getId());
        } else if (o instanceof RecCash) {
            //??????
            RecCash cashFlow = (RecCash) o;
            tradeCheckFollow.setOrgNo(cashFlow.getOrgNo());
            tradeCheckFollow.setBusinessNo(cashFlow.getPayFlowNo());
            tradeCheckFollow.setPayName(cashFlow.getPayType());
            tradeCheckFollow.setTradeTime(cashFlow.getTradeDatatime());
            tradeCheckFollow.setTradeDate(sdf.format(cashFlow.getTradeDatatime()));
            tradeCheckFollow.setPaymentAccount(cashFlow.getPayAccount());
            tradeCheckFollow.setBusinessType(cashFlow.getPayBusinessType());
            tradeCheckFollow.setTradeAmount(cashFlow.getPayAmount() == null ? BigDecimal.ZERO : cashFlow.getPayAmount().abs());
            tradeCheckFollow.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
            tradeCheckFollow.setTradeName(cashFlow.getOrderState());
            Integer checkState = StringUtils.equals(cashFlow.getOrderState(), EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? CommonEnum.BillBalance.HISDC.getValue()
                    : CommonEnum.BillBalance.THIRDDC.getValue();
            tradeCheckFollow.setCheckState(checkState);
            tradeCheckFollow.setPatientName(cashFlow.getPatientName());
            tradeCheckFollow.setBillSource(cashFlow.getBillSource());
            tradeCheckFollow.setPatType(cashFlow.getPatType());
            tradeCheckFollow.setRecThridId(cashFlow.getId());
        } else if (o instanceof HisTransactionFlow) {
            //his
            HisTransactionFlow hisTransactionFlow = (HisTransactionFlow) o;
            tradeCheckFollow.setOrgNo(hisTransactionFlow.getOrgNo());
            tradeCheckFollow.setBusinessNo(hisTransactionFlow.getPayFlowNo());
            tradeCheckFollow.setPayName(hisTransactionFlow.getPayType());
            tradeCheckFollow.setTradeTime(hisTransactionFlow.getTradeDatatime());
            tradeCheckFollow.setTradeDate(sdf.format(hisTransactionFlow.getTradeDatatime()));
            String patType = hisTransactionFlow.getPatType();
            tradeCheckFollow.setPatType("zy".equalsIgnoreCase(patType) ? "zy" : "mz");
            tradeCheckFollow.setPaymentAccount(hisTransactionFlow.getPayAccount());
            tradeCheckFollow.setBusinessType(hisTransactionFlow.getPayBusinessType());
            tradeCheckFollow.setTradeAmount(hisTransactionFlow.getPayAmount() == null ? BigDecimal.ZERO : hisTransactionFlow.getPayAmount().abs());
            tradeCheckFollow.setPaymentRefundFlow(hisTransactionFlow.getOriPayFlowNo());
            tradeCheckFollow.setTradeName(hisTransactionFlow.getOrderState());
            tradeCheckFollow.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
            Integer checkState = StringUtils.equals(hisTransactionFlow.getOrderState(), EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())
                    ? CommonEnum.BillBalance.THIRDDC.getValue()
                    : CommonEnum.BillBalance.HISDC.getValue();
            tradeCheckFollow.setCheckState(checkState);
            tradeCheckFollow.setPatientName(hisTransactionFlow.getCustName());
            tradeCheckFollow.setBillSource(hisTransactionFlow.getBillSource());
            tradeCheckFollow.setPatientName(hisTransactionFlow.getCustName());
            // ?????????
            tradeCheckFollow.setTerminalNo(hisTransactionFlow.getTerminalNo());
            tradeCheckFollow.setHisFlowNo(hisTransactionFlow.getHisFlowNo());
            tradeCheckFollow.setRecHisId(hisTransactionFlow.getId());
            // ?????????
            tradeCheckFollow.setShopNo(hisTransactionFlow.getPayShopNo());
            // ???????????????
            tradeCheckFollow.setShopFlowNo(hisTransactionFlow.getBusinessFlowNo());
            // ?????????
            tradeCheckFollow.setReferenceNum(hisTransactionFlow.getReferenceNum());
            // ?????????
            tradeCheckFollow.setMzCode(hisTransactionFlow.getMzCode());
            // ?????????
            tradeCheckFollow.setInvoiceNo(hisTransactionFlow.getInvoiceNo());
            // ????????????????????????
            tradeCheckFollow.setRequireRefund(hisTransactionFlow.getRequireRefund());
        }
        return tradeCheckFollow;
    }


    /**
     * ??????????????????
     *
     * @return List<Date>
     */
    private void getBeginDateAndEndDate() {
        try {
            beginDate = DateUtil.transferStringToDateFormat(date + " 00:00:00");
            endDate = DateUtil.transferStringToDateFormat(date + " 23:59:59");
        } catch (ParseException e) {
            logger.error("?????????????????????", e);
        }
    }

    private String concatOrgCodeSql() {
        if (orgCodes != null && orgCodes.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String orgCode : orgCodes) {
                sb.append("'" + orgCode + "',");
            }
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }
}
