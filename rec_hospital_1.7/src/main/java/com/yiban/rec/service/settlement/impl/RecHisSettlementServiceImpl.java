package com.yiban.rec.service.settlement.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.dao.settlement.RecHisSettlementDao;
import com.yiban.rec.dao.settlement.RecLogSettlementDao;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.settlement.RecLogSettlement;
import com.yiban.rec.domain.vo.RecHisSettlementVo;
import com.yiban.rec.domain.vo.RecHisSettlementVo.RecHisSettlementDetails;
import com.yiban.rec.service.settlement.RecHisSettlementResultService;
import com.yiban.rec.service.settlement.RecHisSettlementService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.LogCons;

@Service
public class RecHisSettlementServiceImpl implements RecHisSettlementService{
    /** 日志对象 */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /** gson对象 */
    private Gson gson = 
            new GsonBuilder().enableComplexMapKeySerialization().create();
    
    @Autowired
    private RecHisSettlementDao recHisSettlementDao;
    
    @Autowired
    private EntityManager entityManager;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
    
    @Autowired
    private RecLogSettlementDao recLogSettlementDao;
    
    @Autowired
    private RecHisSettlementResultService recHisSettlementResultService;
    
    
    @Override
    @Transactional
    public void getAndSaveHisOrders(String hisBillDate) throws Exception {
        // 获取结算明细
        List<RecHisSettlementDetails> voList = getHisOrderList(hisBillDate);
        // 设置结算日期
        List<RecHisSettlement> list = setSettlementDate(voList);
        // 批量保存
        batchInsertRecHisSettlement(list);
        //汇总数据
        try {
        	recHisSettlementResultService.summary(list,hisBillDate);
        } catch (Exception e) {
			logger.info(e.getMessage());
		}
    }
    
    /*public void summary(String hisBillDate,List<RecHisSettlement> list) throws Exception {
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	
    	//汇总之前先清除
    	recHisSettlementResultDao.deleteBySettleDate(sdf.parse(hisBillDate));
    	for(MetaData v:billList) {
    		recHisSettlementResultService.summary(list, hisBillDate,v.getValue());
    	}
    }*/
    
    
    /**
     * 格式化数据
     * @param voList
     * @return
     * List<RecHisSettlement>
     */
    private List<RecHisSettlement> setSettlementDate(List<RecHisSettlementDetails> voList) {
        if(null == voList || voList.size() == 0) {
            return null;
        }
        List<RecHisSettlement> list = new ArrayList<>();
        for (RecHisSettlementDetails rhsd : voList) {
            RecHisSettlement r = new RecHisSettlement();
            BeanUtils.copyProperties(rhsd, r);
            /** 结账时间（yyyy-MM-dd HH:mm:ss） */
            String settlementTime = rhsd.getSettlementTime();
            if(StringUtils.isNotBlank(settlementTime)) {
                r.setSettlementTime(DateUtil.stringLineToDateTime(settlementTime));
                
                /** 结账日期（yyyy-MM-dd） */
                String settlementDate = settlementTime.substring(0, "yyyy-MM-dd".length());
                r.setSettlementDate(DateUtil.stringLineToDate(settlementDate));
            }
            
            /** 交易时间（yyyy-MM-dd HH:mm:ss） */
            String payTime = rhsd.getPayTime();
            if(StringUtils.isNotBlank(payTime)) {
                r.setPayTime(DateUtil.stringLineToDateTime(payTime));
            }
            list.add(r);
        }
        return list;
    }
    
    /**
     * 删除历史数据
     * @param hisBillDate
     * void
     */
    private void deleteHistoryData(String hisBillDate) {
        // 删除历史数据
        Date settlementDate = DateUtil.stringLineToDate(hisBillDate);
        recHisSettlementDao.deleteBySettlementDate(settlementDate);
    }

    /**
     * 获取HIS订单
     * @param hisBillDate
     * @return
     * List<RecHisSettlement>
     */
    private List<RecHisSettlementDetails> getHisOrderList(String hisBillDate) {
        String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		RecLogSettlement log = recLogSettlementDao.findByOrgCodeAndOrderDate(orgCode , hisBillDate);
        if(null == log) {
            log = new RecLogSettlement();
        }
        log.setRecResult(LogCons.REC_SUCCESS);
        log.setCreatedDate(DateUtil.getCurrentDateTime());
        log.setOrgCode(orgCode);
        log.setOrderDate(hisBillDate);
        String tangduHisSettlementOrderUrl = propertiesConfigService.findValueByPkey(ProConstants.tangduHisSettlementOrderUrl);
		try {
            if(StringUtils.isBlank(hisBillDate) || 
                    StringUtils.isBlank(tangduHisSettlementOrderUrl)) {
                log.setRecResult(LogCons.REC_FAIL);
                log.setResultInfo("日期或HIS结算URL配置为空");
                logger.info("日期或HIS结算URL配置为空");
                return null;
            }
            Map<String, String> param = new HashMap<>();
            param.put("hisBillDate", hisBillDate);
            logger.info("调用HIS结算账单明细接口，请求URL：{}", tangduHisSettlementOrderUrl);
            logger.info("调用HIS结算账单明细接口，请求入参：{}", gson.toJson(param));
            String result = "";
            Map<String, String> headParamMap = new HashMap<>();
            headParamMap.put("Content-Type", "application/json");
            result = HttpClientUtil.doPostJson(tangduHisSettlementOrderUrl, gson.toJson(param));
            logger.info("调用HIS结算账单明细接口，请求出参：{}", result);
            RecHisSettlementVo vo = null;
            try {
                vo = gson.fromJson(result, RecHisSettlementVo.class);
                String resultCode = vo.getResultCode();
                String msg = vo.getResultMsg();
                if(StringUtils.equalsIgnoreCase(resultCode, "SUCCESS")) {
                    log.setResultInfo("成功");
                    // 有数据则删除历史数据
                    List<RecHisSettlementDetails> list = vo.getData();
                    if(null != list && list.size()>0) {
                        deleteHistoryData(hisBillDate);
                    }else {
                        log.setResultInfo("暂无数据");
                    }
                    return list;
                }
                log.setRecResult(LogCons.REC_FAIL);
                log.setResultInfo(msg == null ?"接口返回失败":msg);
            } catch (Exception e) {
                logger.error("调用接口-{}，发生异常：{}", tangduHisSettlementOrderUrl, e.getMessage());
                log.setRecResult(LogCons.REC_FAIL);
                log.setResultInfo(e.getMessage());
            }
        } catch (Exception e) {
            logger.error("调用接口-{}，发生异常：{}", tangduHisSettlementOrderUrl, "连接超时");
            log.setRecResult(LogCons.REC_FAIL);
            log.setResultInfo("连接超时");
        }finally {
            recLogSettlementDao.saveAndFlush(log);
        }
        return null;
    }

    /**
     * 批量保存
     * @param list
     * void
     */
    private void batchInsertRecHisSettlement(List<RecHisSettlement> list) {
        if(null == list) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            entityManager.persist(list.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
    
    /**
     * 批量删除
     */
    @Override
    @Transactional
    public Long deleteBySettlementDate(Date settlementDate) {
        return recHisSettlementDao.deleteBySettlementDate(settlementDate);
    }
}
