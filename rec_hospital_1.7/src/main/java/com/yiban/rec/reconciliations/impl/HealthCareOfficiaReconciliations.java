package com.yiban.rec.reconciliations.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.dao.HealthCareHisDao;
import com.yiban.rec.dao.HealthCareOfficialDao;
import com.yiban.rec.domain.HealthCareHis;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.reconciliations.AbstractReconciliations;

/**
 * 医保对账
 * @author swing
 * @date 2018年7月19日 下午2:22:53 类说明
 */
public class HealthCareOfficiaReconciliations 
    extends AbstractReconciliations<HealthCareOfficial, HealthCareHis> {

    /** 医保账单DAO */
    private HealthCareOfficialDao healthCareOfficialDao;
    
    /** His医保账单DAO */
    private HealthCareHisDao healthCareHisDao;
    
    /**
	 * 医保对账金额维度
	 */
    private String healthAmountType;
    
	public HealthCareOfficiaReconciliations(String orgCode, String date,String healthAmountType) {
		super(orgCode, date);
		healthCareOfficialDao = SpringBeanUtil.getBean(HealthCareOfficialDao.class);
		healthCareHisDao = SpringBeanUtil.getBean(HealthCareHisDao.class);
		this.healthAmountType=healthAmountType;
	}

	@Override
	protected boolean isEqual(HealthCareOfficial hco, HealthCareHis hch) {
	    if(hco == null || hch == null) {
	        return false;
	    }
	    // 院区对比器（对账单号可能是业务单号）
		boolean orgNoComparetor = StringUtils.equals(hco.getOrgNo(),
				hch.getOrgNo());
		if (!orgNoComparetor) {
			return false;
		}
		// 订单号对比器
		boolean orderNoComparetor = StringUtils.equals(hco.getPayFlowNo(),
				hch.getPayFlowNo());
		if (!orderNoComparetor) {
			return false;
		}
        // 订单状态对比器
        boolean orderStateComparetor = StringUtils.equals(hco.getOrderState(), 
                hch.getOrderState());
        if(!orderStateComparetor) {
            return false;
        }
        // 订单金额对比器
        if(StringUtils.isNotBlank(healthAmountType)) {
        	String[] types = healthAmountType.split(",");
        	for(String v:types) {
    			try {
    				Field f1=hco.getClass().getDeclaredField(v);
    				Field f2=hch.getClass().getDeclaredField(v);
    				//设置对象的访问权限，保证对private的属性的访问
    				f1.setAccessible(true);
    				f2.setAccessible(true);
    				BigDecimal vo1=(BigDecimal) f1.get(hco)==null?new BigDecimal(0):(BigDecimal) f1.get(hco);
    				BigDecimal vo2=(BigDecimal) f2.get(hch)==null?new BigDecimal(0):(BigDecimal) f2.get(hch);
    				boolean orderAmountComparetor = vo1.abs().compareTo(
    						vo2.abs())==0;
    	            if(!orderAmountComparetor) {
    	                return false;
    	            }
    			} catch (Exception e) {
    				e.printStackTrace();
    				return false;
    			}
        	}
        }else {
        	return false;
        }
        return true;
	}

	@Override
	protected List<HealthCareOfficial> sourceList() {
		List<HealthCareOfficial> list = healthCareOfficialDao.findByOrgNoInAndTradeDatatimeBetween(orgCodes, 
		        beginDate, endDate);
		/*Map<String, HealthCareOfficial> tfMap = new HashMap<>(list.size());
		for(HealthCareOfficial tf:list){
			String key =tf.getPayFlowNo();
			HealthCareOfficial mapObj =tfMap.get(key);
			if( mapObj == null){
				tfMap.put(key, tf);
			}else{
				//比较是否正负(出现正负则删除)
	            if( !tf.getOrderState().equals(mapObj.getOrderState()) && tf.getCostTotalInsurance().abs().compareTo(mapObj.getCostTotalInsurance().abs()) == 0){
	            	tfMap.remove(key);
				}
			}
		}*/
		return  new ArrayList<HealthCareOfficial>(list);
	}

	@Override
	protected List<HealthCareHis> targetList() {
		List<HealthCareHis> list = healthCareHisDao.findByOrgNoInAndTradeDatatimeBetween(orgCodes, 
		        beginDate, endDate);
		/*Map<String, HealthCareHis> tfMap = new HashMap<>(list.size());
		for(HealthCareHis tf:list){
			String key =tf.getPayFlowNo();
			HealthCareHis mapObj =tfMap.get(key);
			if( mapObj == null){
				tfMap.put(key, tf);
			}else{
				//比较是否正负(出现正负则删除)
				if( !tf.getOrderState().equals(mapObj.getOrderState()) && tf.getCostTotalInsurance().abs().compareTo(mapObj.getCostTotalInsurance().abs()) == 0){
	            	tfMap.remove(key);
				}
			}
		}*/
		return  new ArrayList<HealthCareHis>(list);
	}
}
