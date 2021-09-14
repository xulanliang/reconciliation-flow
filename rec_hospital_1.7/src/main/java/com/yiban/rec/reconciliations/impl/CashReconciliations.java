package com.yiban.rec.reconciliations.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.RecCashDao;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.reconciliations.AbstractReconciliations;
import com.yiban.rec.util.EnumTypeOfInt;

/**
 * 现金对账
 * @author swing
 * @date 2018年7月19日 下午2:17:13 类说明
 */
public class CashReconciliations extends AbstractReconciliations<RecCash, 
    HisTransactionFlow> {
    
    /** His账单操作Dao */ 
    private HisTransactionFlowDao hisTransactionFlowDao;
    
    /** 现金账单操作Dao */
    private RecCashDao recCashDao;
    
    /**
     * 构造函数
     * @param orgCode
     * @param date
     */
	public CashReconciliations(String orgCode, String date) {
		super(orgCode, date);
		hisTransactionFlowDao = SpringBeanUtil.getBean(HisTransactionFlowDao.class);
		recCashDao = SpringBeanUtil.getBean(RecCashDao.class);
	}

	@Override
	public boolean isEqual(RecCash rc, HisTransactionFlow htf) {
		if(rc == null || htf == null) {
		    return false;
		}
		 // 院区对比器（对账单号可能是业务单号）
        boolean orgNoComparetor = StringUtils.equals(rc.getOrgNo(), 
                htf.getOrgNo());
        if(!orgNoComparetor) {
            return false;
        }
        // 订单号对比器
        boolean orderNoComparetor = StringUtils.equals(rc.getPayFlowNo(), 
                htf.getPayFlowNo());
        if(!orderNoComparetor) {
            return false;
        }
        // 订单状态对比器
        boolean orderStateComparetor = StringUtils.equals(rc.getOrderState(), 
                htf.getOrderState());
        if(!orderStateComparetor) {
            return false;
        }
        // 订单金额对比器
        boolean orderAmountComparetor = rc.getPayAmount().abs().compareTo(
                htf.getPayAmount().abs())==0;
        if(!orderAmountComparetor) {
            return false;
        }
        // 支付类型对比器
        boolean payTypeComparetor = StringUtils.equals(rc.getPayType(), 
                htf.getPayType());
        if(!payTypeComparetor) {
            return false;
        }
		return true;
	}

	@Override
	public List<RecCash> sourceList() {
	    return recCashDao.findByOrgNoInAndTradeDatatimeBetween(orgCodes, 
	            beginDate, endDate);
	}

	@Override
	public List<HisTransactionFlow> targetList() {
	    return hisTransactionFlowDao.findByOrgNoInAndPayTypeAndTradeDatatimeBetween(
	            orgCodes, EnumTypeOfInt.CASH_PAYTYPE.getValue(), 
	            beginDate, endDate);
	}

}
