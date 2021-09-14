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
 * @date 2018年7月19日 上午11:13:06 类说明: 对账基础实现，控制对账逻辑及对账算法
 */
public abstract class AbstractReconciliations<T, K> implements Reconciliationsable {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Logger日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 对账日志明细
     */
    private RecLogDetailsDao recLogDetailsDao = SpringBeanUtil.getBean(RecLogDetailsDao.class);

    /**
     * 机构管理Service
     */
    private OrganizationService organizationService = SpringBeanUtil.getBean(OrganizationService.class);


    private TradeCheckFollowDao tradeCheckFollowDao = SpringBeanUtil.getBean(TradeCheckFollowDao.class);

    private TradeCheckFollowDealDao tradeCheckFollowDealDao = SpringBeanUtil.getBean(TradeCheckFollowDealDao.class);

    private HisTransactionFlowDao hisTransactionFlowDao = SpringBeanUtil.getBean(HisTransactionFlowDao.class);

    // 渠道
    private ThirdBillDao thirdBillDao = SpringBeanUtil.getBean(ThirdBillDao.class);
    // 医保中心
    private HealthCareOfficialDao healthCareOfficialDao = SpringBeanUtil.getBean(HealthCareOfficialDao.class);
    // 医保His
    private HealthCareHisDao healthCareHisDao = SpringBeanUtil.getBean(HealthCareHisDao.class);

    private HealthExceptionDao healthExceptionDao = SpringBeanUtil.getBean(HealthExceptionDao.class);

    private PropertiesConfigService propertiesConfigService = SpringBeanUtil.getBean(PropertiesConfigService.class);

    /**
     * 机构编码
     */
    protected Set<String> orgCodes;

    /**
     * 日志只记录父机构
     */
    private String orgCode;

    /**
     * 日期
     */
    protected String date;

    /**
     * 开始日期
     */
    protected Date beginDate;

    /**
     * 结束日期
     */
    protected Date endDate;

    /**
     * 构造方法
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
     * 对账核心算法器，暂时采用列表实现，如有性能问题考虑其他方式
     */
    @SuppressWarnings("rawtypes")
    @Override
    public final List<TradeCheckFollow> compareBill() throws Exception {
        List<TradeCheckFollow> resultList = new ArrayList<>();
        List<T> sourceList = sourceList();
        List<K> targetList = targetList();
        //这个地方比较鸡肋，单纯的为了记录异常类型,可扩展性差，目前暂时这样，日后有待优化
        Class cls = this.getClass();
        String msgSource = "";
        String msgTarget = "";
        String sucMsg = "";
        String payType = "";

        if (cls.equals(AliPayReconciliations.class)) {
            msgSource = "支付宝账单数据未上传";
            msgTarget = "his账单数据未上传";
            sucMsg = "支付宝对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue();
        } else if (cls.equals(WechatPayReconciliations.class)) {
            msgSource = "微信账单数据未上传";
            msgTarget = "his账单数据未上传";
            sucMsg = "微信对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_WECHAT.getValue();
        } else if (cls.equals(BankReconciliations.class)) {
            msgSource = "银行账单数据未上传";
            msgTarget = "his账单数据未上传";
            sucMsg = "银行对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_BANK.getValue();
        } else if (cls.equals(AggregateReconciliations.class)) {
            msgSource = "聚合支付账单数据未上传";
            msgTarget = "his账单数据未上传";
            sucMsg = "聚合支付对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue();
        } else if (cls.equals(CashReconciliations.class)) {
            msgSource = "现金账单数据未上传";
            msgTarget = "his现金账单数据未上传";
            sucMsg = "现金对账成功";
            payType = EnumTypeOfInt.CASH_PAYTYPE.getValue();
        } else if (cls.equals(HealthTypeReconciliations.class)) {
            msgSource = "医保账单数据未上传";
            msgTarget = "his中心账单数据未上传";
            sucMsg = "医保对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        } else if (cls.equals(WJYZTReconciliations.class)) {
            msgSource = "一账通账单数据未上传";
            msgTarget = "his一账通账单数据未上传";
            sucMsg = "一账通对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_WJYZT.getValue();
        } else if (cls.equals(UnionPayReconciliations.class)) {
            msgSource = "云闪付账单数据未上传";
            msgTarget = "his云闪付账单数据未上传";
            sucMsg = "云闪付对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_UNIONPAY.getValue();
        } else if(cls.equals(HealthCareBankReconciliations.class)){
        	msgSource = "社保卡银行卡账单数据未上传";
            msgTarget = "his社保卡银行卡账单数据未上传";
            sucMsg = "社保卡银行卡对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        } else if(cls.equals(OnlineBankReconciliations.class)){
        	msgSource = "网银账单数据未上传";
            msgTarget = "网银账单数据未上传";
            sucMsg = "网银对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_ONLINE_BANK.getValue();
        }

        logger.info("机构编码:{},对账日期:{},源数据总数:{},目标数据总数:{}", orgCodes, date, sourceList.size(), targetList.size());
        if (sourceList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgSource, payType);
        }

        if (targetList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgTarget, payType);
        }
        if (sourceList.size() > 0 && targetList.size() > 0) {
            saveRecLog(orgCode, date, LogCons.REC_SUCCESS, sucMsg, payType);
        }

        //过滤异常账单
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
        //异常账单隔天对账逻辑
        if (crossDayReconciliation.trim().equalsIgnoreCase("true")) {
            String stepNum = propertiesConfigService.findValueByPkey(ProConstants.stepNum,
                    ProConstants.DEFAULT.get(ProConstants.stepNum));
            //渠道异常账单，从his获取账单对比
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
            // His异常账单，从渠道获取账单对比
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

        //异常账单转换成统一实体
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
        // 只处理退款状态的单边账单
//		resultList = filterSingleRefundBillResult(resultList);
        // 进行合并处理
        // TODO modify by myz 2019年1月14日15:15:22 注释， 唐都对账不用合并账单
//		resultList = filterMergeBillResult(resultList);
//		logger.info(" filterResultModify -- > " + resultList.size()+", " + resultList);
        //全表数据处理一正一负的账单(不纳入异常账单列表)
        //TODO 当有多条不同状态的账单 支付流水号为空时会删错数据
        // TODO modify by myz 2019年1月14日15:15:22 注释， 唐都对账不用冲正
//		resultList=filterResultModifyAll(resultList);
        //logger.info(" filterResultModifyAll -- > "+resultList.size()+", " + resultList);
        logger.info(this.getClass() + "异常数据:{}", resultList.size());
        return resultList;
    }

    /**
     * 判断两笔订单是否相等
     *
     * @param t1
     * @param t2
     * @return false 不相等  true  相等
     */
    private boolean isEqua(Object t1, Object t2) {
        ThirdBill thirdBill = null;
        HisTransactionFlow hisTransactionFlow = null;
        HealthCareOfficial healthCareOfficial = null;
        HealthCareHis healthCareHis = null;
        // 电子对账
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
        // 医保对账  HealthCareOfficial hco, HealthCareHis hch
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
//	 *  判断两笔订单是否相等
//	 * @param t1
//	 * @param t2
//	 * @return false 不相等  true  相等
//	 */
//	private boolean isEqua(Object t1, Object t2){
//		ThirdBill thirdBill = null;
//		HisTransactionFlow hisTransactionFlow = null;
//		HealthCareOfficial healthCareOfficial = null;
//		HealthCareHis healthCareHis = null;
//		// 电子对账
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
//		// 医保对账  HealthCareOfficial hco, HealthCareHis hch
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
     * 过滤金额为0的异常账单
     *
     * @param resultList
     * @return
     */
    private List<TradeCheckFollow> filterAmountZero(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        Long now = System.currentTimeMillis();
        logger.info(this.getClass() + "过滤金额为0的异常账单");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            String timestampStr = UUID.randomUUID().toString();
            String businessNo = tf.getBusinessNo() == null ? timestampStr : tf.getBusinessNo();
            String key = tf.getOrgNo().concat(businessNo).concat(String.valueOf(tf.getTradeAmount().doubleValue()))
                    .concat(tf.getPayName()).concat(timestampStr);
            //金额不为0
            if (tf.getTradeAmount() != null && tf.getTradeAmount().doubleValue() != 0) {
                tfMap.put(key, tf);
            }
        }
        logger.info(this.getClass() + "过滤金额为0的异常账单 end " + (now - System.currentTimeMillis()) / 1000);

        return new ArrayList<TradeCheckFollow>(tfMap.values());
    }


    /**
     * 过滤掉一正一负的记录
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
        logger.info(this.getClass() + "过滤正负账单");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            int countHis = hisTransactionFlowDao.countByOrgCodesAndPaytypeAndDateAndPayflowno(orgCodes, tf.getPayName(),
                    tf.getTradeDate(), tf.getBusinessNo());
            int countThridBill = thirdBillDao.countByOrgCodesAndPaytypeAndDateAndPayflowno(orgCodes, tf.getPayName(),
                    tf.getTradeDate(), tf.getBusinessNo());
            String timestampStr = "";
            // his表有数据并且渠道表有数据，就过滤掉不参与冲正
            if (countHis > 0 && countThridBill > 0) {
                timestampStr = String.valueOf(System.currentTimeMillis());
            }
            String businessNo = tf.getBusinessNo();
            // 流水号为空，也不用冲正
            if (StringUtils.isEmpty(businessNo)) {
                businessNo = String.valueOf(System.currentTimeMillis());
            }
            String key = tf.getOrgNo().concat(businessNo).concat(String.valueOf(tf.getTradeAmount().doubleValue()))
                    .concat(tf.getPayName()).concat(timestampStr);
            TradeCheckFollow mapObj = tfMap.get(key);
            if (mapObj == null) {
                tfMap.put(key, tf);
                // 是否需要隔日账单 一正一负 账单冲正
                if (offsetStepNum > 0 && countHis == 0) {
                    // 实行隔日账单 一正一负账单 冲正逻辑，从渠道表中查看  是否有退费订单
                    Date billDate = DateUtil.getStringToDateTime(resultList.get(0).getTradeDate());
                    String startDate = DateUtil.getSpecifiedDayBeforeDay(billDate, offsetStepNum);
                    String endDate = DateUtil.getSpecifiedDayAfter(resultList.get(0).getTradeDate(), offsetStepNum);
                    List<ThirdBill> thirdBillList = thirdBillDao.findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(tf.getOrgNo(), tf.getBusinessNo(), tf.getTradeAmount(), DateUtil.getStringToDateTime(startDate), DateUtil.getStringToDateTime(endDate));
                    List<HisTransactionFlow> hisBillList = hisTransactionFlowDao.findByOrgNoAndPayFlowNoAndPayAmountAndTradeDatatimeBetween(tf.getOrgNo(), tf.getBusinessNo(), tf.getTradeAmount(), DateUtil.getStringToDateTime(startDate), DateUtil.getStringToDateTime(endDate));
                    for (ThirdBill thirdBill : thirdBillList) {
                        if (!tf.getTradeName().equals(thirdBill.getOrderState())) {
                            tfMap.remove(key);
                            logger.info("此渠道账单符合隔日冲正条件，删除账单，" + tf.getBusinessNo());
                        }
                    }
                    for (HisTransactionFlow hisTransactionFlow : hisBillList) {
                        if (!tf.getTradeName().equals(hisTransactionFlow.getOrderState())) {
                            tfMap.remove(key);
                            logger.info("此His账单符合隔日冲正条件，删除账单，" + tf.getBusinessNo());
                        }
                    }
                }
            } else {
                // 比较支付状态是否正负(出现正负则删除)
                if (!tf.getTradeName().equals(mapObj.getTradeName()) && !"0".equals(mapObj.getBusinessNo())) {
                    tfMap.remove(key);
                    logger.error("此账单符合冲正条件，删除账单，" + mapObj.getBusinessNo());
                } else {
                    // 兼容异常账单重复的情况
                    key += UUID.randomUUID().toString();
                    tfMap.put(key, tf);
                }
            }
        }
        logger.info(this.getClass() + "过滤正负账单 end " + (now - System.currentTimeMillis()) / 1000);

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
     * 过滤不是当天的数据可能存在的一正一负数据
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
        logger.info("全表过滤正负账单--------------------start -----------");
        //历史数据预处理map
        Map<String, List<TradeCheckFollow>> map = new HashMap<>();
        //历史数据当天map预处理map
        Map<String, TradeCheckFollow> maping = new HashMap<>();
        //抹平预处理记录map
        //抹平所有记录map
        Map<String, TradeCheckFollowDeal> dealAllMap = new HashMap<>();
        //抹平预删除的集合
        List<TradeCheckFollowDeal> dealDeleteList = new ArrayList<>();
        //当天抹平预保存的集合
        List<TradeCheckFollowDeal> dealSaveList = new ArrayList<>();
        //查询异常处理表数据（抹平表）
        List<TradeCheckFollowDeal> dealList = tradeCheckFollowDealDao.findAll();
        //查询历史异常表所有数据
        List<TradeCheckFollow> list = tradeCheckFollowDao.findAll();
        //循环抹平记录数据存入抹平map集合中
        for (TradeCheckFollowDeal v : dealList) {
            //得到所有的map集合
            dealAllMap.put(v.getOrgCode() + "," + v.getPayFlowNo() + "," + v.getTradeDatetime(), v);
        }
        //将历史数据存入map集合中（key为单号，值为金额）
        for (TradeCheckFollow v : list) {
            //得到当天需要删除前的历史数据map集合
            if (v.getTradeDate().equals(nowTime)) {
                maping.put(v.getBusinessNo(), v);
                continue;
            }
            //如果存在则添加多条现金数据
            List<TradeCheckFollow> voList = map.get(v.getBusinessNo());
            if (voList != null && voList.size() > 0) {
                //如果是短款,值为负数,长款则是正数
                if (v.getCheckState() == CommonEnum.BillBalance.HISDC.getValue()) {
                    v.setTradeAmount(v.getTradeAmount().negate());
                }
                voList.add(v);
                map.put(v.getBusinessNo(), voList);
            } else {
                List<TradeCheckFollow> newList = new ArrayList<TradeCheckFollow>();
                //如果是短款,值为负数,长款则是正数
                if (v.getCheckState() == CommonEnum.BillBalance.HISDC.getValue()) {
                    v.setTradeAmount(v.getTradeAmount().negate());
                }
                newList.add(v);
                map.put(v.getBusinessNo(), newList);
            }
        }
        //循环当天异常账单集合
        for (TradeCheckFollow v : resultList) {
            //得到历史数据中单号相同的map
            List<TradeCheckFollow> voList = map.get(v.getBusinessNo());
            //map中存在
            if (voList != null && voList.size() > 0) {
                for (TradeCheckFollow z : voList) {
                    //看是否已经处理过,如果处理过则跳过不抹平，为处理则抹平处理跳出整个循环
                    if (dealAllMap.get(z.getOrgNo() + "," + z.getBusinessNo() + "," + z.getTradeDate()) != null) {
                        continue;
                    }
                    //判断金额是否冲正,冲正则抹平
                    if (z.getTradeAmount().add(v.getTradeAmount()).compareTo(new BigDecimal(0)) == 0) {
                        // 保存处理单当天
                        saveDeal(v, dealAllMap, dealSaveList);
                        // 保存处理单隔天
                        saveDeal(z, dealAllMap, dealSaveList);
                    }
                    break;
                }
            }
            //map中不存在加入抹平预删除集合
            else {
                TradeCheckFollowDeal deal = dealAllMap.get(v.getOrgNo() + "," + v.getBusinessNo() + "," + v.getTradeDate());
                if (deal != null) {
                    dealDeleteList.add(deal);
                }
            }
        }
        //删除抹平集合
        deleteDeal(dealDeleteList);
        //保存抹平记录
        saveDeal(dealSaveList);
		
		/*for (int i = resultList.size() - 1; i >= 0; i--) {
			TradeCheckFollow tf = resultList.get(i);
			if(StringUtils.isBlank(tf.getBusinessNo()))continue;
			Integer checkState=tf.getCheckState();
			//短款
			if(checkState==CommonEnum.BillBalance.HISDC.getValue()) {
				checkState=CommonEnum.BillBalance.THIRDDC.getValue();
			}else {//长款
				checkState=CommonEnum.BillBalance.HISDC.getValue();
			}
			//查询his表以及渠道表中和该条单冲正的数据
			List<TradeCheckFollow> list = tradeCheckFollowDao.findByBusinessNoAndTradeDateNotAndCheckState(tf.getBusinessNo(),tf.getTradeDate(),checkState);
			if (list != null && list.size() > 0) {// 有冲正数据，则修改该条记录为抹平
				//有多条单,如果全部被处理，当天该条异常单则不需要处理
				for(TradeCheckFollow v:list) {
					if(tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime
							(v.getBusinessNo(),v.getOrgNo(),v.getTradeDate())==null) {
						if(tf.getTradeAmount().abs().compareTo(v.getTradeAmount().abs())==0) {//抹平
							// 保存处理单当天
							saveDeal(tf);
							// 保存处理单隔天
							saveDeal(v);
							logger.info("有冲正数据，抹平该条记录 + " + resultList.get(i) + ", 异常表的数据 = " + list);
						}
						break;
					}
				}
			}else {//如果不存在则证明不需要抹平，不管抹平记录表里面有没有，直接删除
				TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
				if(deal!=null) {
					tradeCheckFollowDealDao.delete(deal);
					//tradeCheckFollowDealDao.deleteByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
				}
			}
		}*/

        logger.info("全表过滤正负账单完毕--------------------end -----------, 耗时：" + (System.currentTimeMillis() - start));
        return resultList;
    }
	
	/*private void saveDeal(TradeCheckFollow tf) {
		TradeCheckFollowDeal deal = tradeCheckFollowDealDao.findByPayFlowNoAndOrgCodeAndTradeDatetime(tf.getBusinessNo(),tf.getOrgNo(),tf.getTradeDate());
		if(deal==null) {
			TradeCheckFollowDeal vo =new TradeCheckFollowDeal();
			vo.setPayFlowNo(tf.getBusinessNo());
			vo.setDescription("自动抹平处理");
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
            vo.setDescription("自动抹平处理");
            vo.setCreatedDate(new Date());
            vo.setExceptionState(String.valueOf(tf.getCheckState()));
            vo.setDealAmount(tf.getTradeAmount());
            vo.setOrgCode(tf.getOrgNo());
            vo.setTradeDatetime(tf.getTradeDate());
            dealSaveList.add(vo);
        }
    }

    //批量保存
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

    //批量删除
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
     * 对账核心算法器，暂时采用列表实现，如有性能问题考虑其他方式
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
            msgSource = "医保账单数据未上传";
            msgTarget = "his中心账单数据未上传";
            sucMsg = "医保对账成功";
            payType = EnumTypeOfInt.PAY_TYPE_HEALTHCARE.getValue();
        }

        logger.info("机构编码:{},对账日期:{},源数据总数:{},目标数据总数:{}", orgCodes, date, sourceList.size(), targetList.size());
        if (sourceList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgSource, payType);
        }

        if (targetList.size() == 0) {
            saveRecLog(orgCode, date, LogCons.REC_FAIL, msgTarget, payType);
        }
        if (sourceList.size() > 0 && targetList.size() > 0) {
            saveRecLog(orgCode, date, LogCons.REC_SUCCESS, sucMsg, payType);
        }

        //过滤异常账单
        for (int i = sourceList.size() - 1; i >= 0; i--) {
            // 医保中心
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

        //异常账单隔天对账逻辑  HealthCareOfficial hco, HealthCareHis hch
        String crossDayReconciliation = propertiesConfigService.findValueByPkey(ProConstants.crossDayReconciliation,
                ProConstants.DEFAULT.get(ProConstants.crossDayReconciliation));
        if (crossDayReconciliation.trim().equalsIgnoreCase("true")) {
            // 隔天平账账单流水号
            List<String> crossDayRecFlowNoList = new ArrayList<>();
            //医保渠道异常账单，从his获取账单对比
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
                        // 2019-11-27 仅过滤非当天账单
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
            // 医保His异常账单，从渠道获取账单对比
            Iterator<HealthCareHis> dataHisIterator = (Iterator<HealthCareHis>) targetList.iterator();
            while (dataHisIterator.hasNext()) {
                HealthCareHis healthCareHis = dataHisIterator.next();
                List<HealthCareOfficial> listData = healthCareOfficialDao.findByOrgNoAndPayFlowNoAndOrderStateAndTradeDatatimeBetween(healthCareHis.getOrgNo(), healthCareHis.getPayFlowNo(),
                        healthCareHis.getOrderState(), startTime, endTime);
                if (listData.size() > 0) {
                    if (isEqua(healthCareHis, listData.get(0))) {
                        // 2019-11-27 仅过滤非当天账单
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

        //异常账单转换成统一实体
        for (K k : targetList) {
            HealthException rec = convertToHealthException(k);
            resultList.add(rec);
        }
        for (T t : sourceList) {
            HealthException rec = convertToHealthException(t);
            resultList.add(rec);
        }
        //处理一正一负的账单(不纳入异常账单列表)
        resultList = filterHealthResult(resultList);
        logger.info("异常数据:{}", resultList.size());
        return resultList;
    }


    /**
     * 只处理退款状态的单边账
     */
    private List<TradeCheckFollow> filterSingleRefundBillResult(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        // 单边账map集合
        Map<String, List<TradeCheckFollow>> singleRefundMap = new HashMap<>(resultList.size());
        // 可以合并的账单（不包含单边账）
        Map<String, TradeCheckFollow> tfmap = new HashMap<>();

        for (TradeCheckFollow tf : resultList) {
            // 区分支付流水号为空的情况
            String key = "";
            if (StringUtils.isBlank(tf.getBusinessNo())) {
                key = tf.getOrgNo().concat(System.currentTimeMillis() + RandomCodeUtil.generateWord(4))
                        .concat(tf.getPayName());
            } else {
                key = tf.getOrgNo().concat(tf.getBusinessNo()).concat(tf.getPayName());
            }
            TradeCheckFollow mapObj = tfmap.get(key);
            if (mapObj == null) {
                // 退款的单边账要分开处理
                String tradeName = tf.getTradeName();
                if (StringUtils.isNotBlank(tradeName) && EnumTypeOfInt.TRADE_TYPE_REFUND.getValue().equals(tradeName)) {
                    List<TradeCheckFollow> refundFollows = new ArrayList<>();
                    refundFollows.add(tf);
                    singleRefundMap.put(key, refundFollows);
                } else {
                    // 随机生成不重复的key
                    tfmap.put(System.currentTimeMillis() + RandomCodeUtil.generateWord(4), tf);
                }
            } else {
                // checkState 不等，则不是单边账。判断这个主要是为了防止多笔退款的也被当成不是单边账
                if (!tf.getCheckState().equals(mapObj.getCheckState())) {
                    singleRefundMap.remove(key);

                    // 随机生成不重复的key
                    tfmap.put(System.currentTimeMillis() + RandomCodeUtil.generateWord(4), tf);
                } else {
                    List<TradeCheckFollow> refundFollows = singleRefundMap.get(key);
                    refundFollows.add(mapObj);
                    singleRefundMap.put(key, refundFollows);
                }

            }
        }

        // 处理单边账的情况
        ArrayList<TradeCheckFollow> follows = dealSingleRefundListBill(singleRefundMap);

        ArrayList<TradeCheckFollow> result = new ArrayList<TradeCheckFollow>(tfmap.values());
        result.addAll(follows);
        return result;
    }

    // 对所有的账单进行合并
    private List<TradeCheckFollow> filterMergeBillResult(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        //
        List<TradeCheckFollow> result = new ArrayList<>();
        // 可以合并的账单（不包含单边账）
        Map<String, List<TradeCheckFollow>> tfmap = new HashMap<>();

        for (TradeCheckFollow tf : resultList) {
            // 区分支付流水号为空的情况
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

        // 合并账单
        List<TradeCheckFollow> follows = mergeAllExceptionBill(tfmap);
        result.addAll(follows);
        return result;
    }

    private List<TradeCheckFollow> filterResultModify1(List<TradeCheckFollow> resultList) {
        if (resultList.size() == 0) {
            return resultList;
        }
        Log.info("过滤正负账单");
        Map<String, TradeCheckFollow> tfMap = new HashMap<>(resultList.size());
        //单边账map集合
        Map<String, TradeCheckFollow> singleRefundMap = new HashMap<>(resultList.size());
        for (TradeCheckFollow tf : resultList) {
            // 区分支付流水号为空的情况
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
                // 退款的单边账要分开处理
                if (StringUtils.isNotBlank(tradeName) && EnumTypeOfInt.TRADE_TYPE_REFUND.getValue().equals(tradeName)) {
                    singleRefundMap.put(key, tf);
                }
            } else {
                singleRefundMap.remove(key);
                BigDecimal tradeAmount1 = tf.getTradeAmount();
                BigDecimal tradeAmount2 = mapObj.getTradeAmount();
                String tradeName1 = tf.getTradeName();
                String tradeName2 = mapObj.getTradeName();

                // 判断金额、状态是否一致(出现正负则删除),冲正
                if (!tradeName1.equals(tradeName2) && tradeAmount1.abs().compareTo(tradeAmount2.abs()) == 0) {
                    tfMap.remove(key);
                } else {
                    // 不一致的账单进行合并
                    if (!tf.getCheckState().equals(mapObj.getCheckState())) {
                        TradeCheckFollow tradeCheckFollow = mergeExceptionBill(tf, mapObj);
                        if (tradeCheckFollow != null) {
                            // 用合并后的账单覆盖掉原先的账单
                            tfMap.put(key, tradeCheckFollow);
                        }
                    }
                }
            }
        }

        // 处理单边账的情况
        ArrayList<TradeCheckFollow> follows = dealSingleRefundBill(singleRefundMap);

        ArrayList<TradeCheckFollow> result = new ArrayList<TradeCheckFollow>(tfMap.values());
        result.addAll(follows);
        return result;
    }

    // 兼容多次退款的情况
    public ArrayList<TradeCheckFollow> dealSingleRefundListBill(Map<String, List<TradeCheckFollow>> singleRefundMap) {
        if (singleRefundMap.isEmpty()) {
            return new ArrayList<TradeCheckFollow>();
        }
        // 3 5长款 2 6短款
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        ArrayList<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>();
        for (List<TradeCheckFollow> list : singleRefundMap.values()) {
            follows.addAll(list);
        }
        for (TradeCheckFollow tradeCheckFollow : follows) {
            // 短款
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
        logger.info("合并差异账单。。。");
        List<TradeCheckFollow> follows = new ArrayList<>();
        // 3 5长款 2 6短款
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
            // 短款 2
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                t.setCheckState(CommonEnum.BillBalance.HISDC.getValue());
            }
            // 长款
            else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                t.setCheckState(CommonEnum.BillBalance.THIRDDC.getValue());
                // 冲正
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
        // 3 5长款 2 6短款
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();
        ArrayList<TradeCheckFollow> follows = new ArrayList<TradeCheckFollow>(singleRefundMap.values());
        for (TradeCheckFollow tradeCheckFollow : follows) {
            // 短款
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
        logger.info("合并差异账单。。。");
        // 3 5长款 2 6短款
        String shotCheckStates = CommonEnum.BillBalance.HISDC.getValue() + ","
                + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
//		String longCheckStates = CommonEnum.BillBalance.THIRDDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREOFFI.getValue();

        // 区分长短款账单
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

        // 支付状态不一致则不合并
        if (!tradeNameShot.equals(tradeNameLong)) {
            logger.error("支付状态不一样，不合并异常账单， " + tfShot.getBusinessNo());
            return null;
        }
        // 算出差异金额
        BigDecimal diffAmount = tradeAmountShot.subtract(tradeAmountLong);

        Integer checkStateShot = tfShot.getCheckState();
        Integer checkStateLong = tfLong.getCheckState();
        Integer checkState = null;
        // 支付类型
        if (tradeNameShot.equals(EnumTypeOfInt.TRADE_TYPE_PAY.getValue())) {
            // 短款
            if (diffAmount.compareTo(BigDecimal.ZERO) > 0) {
                checkState = checkStateShot;
                // 长款
            } else {
                checkState = checkStateLong;
            }

            // 退款类型
        } else if (tradeNameShot.equals(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())) {
            // 长款
            if (diffAmount.compareTo(BigDecimal.ZERO) > 0) {
                checkState = checkStateLong;
                // 短款
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
     * 医保对账过滤掉一正一负的记录
     *
     * @param resultList
     */
    private List<HealthException> filterHealthResult(List<HealthException> resultList) {
        if (resultList == null || resultList.size() == 0) {
            return resultList;
        }
        //记录HIS一正一负的集合
        Set<String> hisSet = new HashSet<String>();
        Log.info("过滤正负账单");
        Map<String, HealthException> tfMap = new HashMap<>(resultList.size());
        for (HealthException tf : resultList) {
            //过滤医保中心里面HIS已经冲正的单号
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
                //比较是否正负(出现正负则删除)
                if (!tf.getOrderState().equals(mapObj.getOrderState())) {
                    //如果是HIS的冲正，记录下来
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
     * 将当个机构转换成父子结构列表
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
     * 记录日志
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
     * 由子类实现两条账单记录是否相等
     *
     * @param o1
     * @param o2
     * @return
     */
    protected abstract boolean isEqual(T o1, K o2);

    /**
     * 获取源数据，由子类实现
     *
     * @return
     */
    protected abstract List<T> sourceList();

    /**
     * 获取要比较的数据,由子类实现
     *
     * @return
     */

    protected abstract List<K> targetList();


    /**
     * 将对账后数据对象统一转化为Reconciliation对象
     *
     * @param o
     * @return
     */
    private final HealthException convertToHealthException(Object o) {
        HealthException vo = new HealthException();
        vo.setCreatedDate(new Date());
        if (o instanceof HealthCareHis) {
            //医保his
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
            // 医保电脑号
            vo.setSocialComputerNumber(hisBill.getSocialComputerNumber());
            // 业务类型（挂号/缴费）
            vo.setBusnessType(hisBill.getBusnessType());
            String crossDayRec = hisBill.getCrossDayRec();
            if (crossDayRec != null && !crossDayRec.trim().equals("")) {
                vo.setCrossDayRec(crossDayRec);
            }
        } else if (o instanceof HealthCareOfficial) {
            HealthCareOfficial officialBill = (HealthCareOfficial) o;
            //医保中心
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
            // 医保卡号
            vo.setHealthCode(officialBill.getSocialInsuranceNo());
            // 商户流水号
            vo.setShopFlowNo(officialBill.getShopFlowNo());
            String crossDayRec = officialBill.getCrossDayRec();
            if (crossDayRec != null && !crossDayRec.trim().equals("")) {
                vo.setCrossDayRec(crossDayRec);
            }
        }
        return vo;
    }


    /**
     * 将对账后数据对象统一转化为Reconciliation对象
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
            //渠道
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
            // 商户流水号
            tradeCheckFollow.setShopFlowNo(thirdBill.getShopFlowNo());
            // 商户号
            tradeCheckFollow.setShopNo(thirdBill.getPayShopNo());
            tradeCheckFollow.setRecThridId(thirdBill.getId());
            // 终端号
            tradeCheckFollow.setTerminalNo(thirdBill.getPayTermNo());
            // 参考号
            tradeCheckFollow.setReferenceNum(thirdBill.getReferenceNum());
            // 渠道是否能够退款
            tradeCheckFollow.setRequireRefund(thirdBill.getRequireRefund());
            // 银行卡号
            tradeCheckFollow.setPaymentAccount(thirdBill.getPayAccount());
            // 发票号
            tradeCheckFollow.setInvoiceNo(thirdBill.getInvoiceNo());
        } else if (o instanceof HealthCareHis) {
            //医保his
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
            //医保中心
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
            //现金
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
            // 终端号
            tradeCheckFollow.setTerminalNo(hisTransactionFlow.getTerminalNo());
            tradeCheckFollow.setHisFlowNo(hisTransactionFlow.getHisFlowNo());
            tradeCheckFollow.setRecHisId(hisTransactionFlow.getId());
            // 商户号
            tradeCheckFollow.setShopNo(hisTransactionFlow.getPayShopNo());
            // 商户流水号
            tradeCheckFollow.setShopFlowNo(hisTransactionFlow.getBusinessFlowNo());
            // 参考号
            tradeCheckFollow.setReferenceNum(hisTransactionFlow.getReferenceNum());
            // 门诊号
            tradeCheckFollow.setMzCode(hisTransactionFlow.getMzCode());
            // 发票号
            tradeCheckFollow.setInvoiceNo(hisTransactionFlow.getInvoiceNo());
            // 渠道是否能够退款
            tradeCheckFollow.setRequireRefund(hisTransactionFlow.getRequireRefund());
        }
        return tradeCheckFollow;
    }


    /**
     * 获取账单时间
     *
     * @return List<Date>
     */
    private void getBeginDateAndEndDate() {
        try {
            beginDate = DateUtil.transferStringToDateFormat(date + " 00:00:00");
            endDate = DateUtil.transferStringToDateFormat(date + " 23:59:59");
        } catch (ParseException e) {
            logger.error("日志转换异常：", e);
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
