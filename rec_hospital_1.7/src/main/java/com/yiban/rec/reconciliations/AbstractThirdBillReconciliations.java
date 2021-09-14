package com.yiban.rec.reconciliations;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.dao.HisTransactionFlowDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.ThirdBill;

/**
 * 微信支付宝公共方法抽象类
 * @Author WY
 * @Date 2018年7月20日
 */
public abstract class AbstractThirdBillReconciliations
    extends AbstractReconciliations<ThirdBill, HisTransactionFlow> {
    
    /** 第三方渠道Dao */
    private ThirdBillDao thirdBillDao;
    
    private final String zero="0";
    
    /** His数据对象Dao */
    private HisTransactionFlowDao hisTransactionFlowDao;

    public AbstractThirdBillReconciliations(String orgCode, String date) {
        super(orgCode, date);
        thirdBillDao = SpringBeanUtil.getBean(ThirdBillDao.class);
        hisTransactionFlowDao = SpringBeanUtil.getBean(HisTransactionFlowDao.class);
    }

    @Override
    protected boolean isEqual(ThirdBill tb, HisTransactionFlow htf) {
        if(tb == null || htf == null) {
            return false;
        }
        // 院区对比器（对账单号可能是业务单号）
        boolean orgNoComparetor = StringUtils.equals(tb.getOrgNo(), htf.getOrgNo());
        if(!orgNoComparetor) {
            return false;
        }
        // his订单号为空或者为0，直接返回异常
        String hisPayFlowNo = htf.getPayFlowNo();
        if(StringUtils.isBlank(hisPayFlowNo)||hisPayFlowNo.equals(zero)) {
            return false;
        }
        // his订单状态为空或者为0，直接返回异常
        String orderState = htf.getOrderState();
        if(StringUtils.isBlank(orderState)||orderState.equals(zero)) {
            return false;
        }
        // 订单号对比器
        boolean orderNoComparetor = StringUtils.equals(tb.getPayFlowNo(), htf.getPayFlowNo())
                || StringUtils.equals(tb.getShopFlowNo(), htf.getPayFlowNo())
                || StringUtils.equals(tb.getOutTradeNo(), htf.getPayFlowNo())
                || StringUtils.equals(tb.getOrderNo(), htf.getPayFlowNo());
        if(!orderNoComparetor) {
            return false;
        }
        // 订单状态对比器
        boolean orderStateComparetor = StringUtils.equals(tb.getOrderState(), htf.getOrderState());
        if(!orderStateComparetor) {
            return false;
        }
        // 订单金额对比器
        boolean orderAmountComparetor = tb.getPayAmount().abs().compareTo(htf.getPayAmount().abs())==0;
        if(!orderAmountComparetor) {
            return false;
        }
        // 支付类型对比器
        boolean payTypeComparetor = StringUtils.equals(tb.getRecPayType(), htf.getPayType());
        if(!payTypeComparetor) {
            return false;
        }
        return true;
    }

    protected List<ThirdBill> sourceList(String payType) {
        return thirdBillDao.findByOrgNoInAndRecPayTypeAndTradeDatatimeBetween(orgCodes, 
                payType, beginDate, endDate);
    }

    protected List<HisTransactionFlow> targetList(String payType) {
        return hisTransactionFlowDao.findByOrgNoInAndPayTypeAndTradeDatatimeBetween(
                orgCodes, payType, beginDate, endDate);
    }
}
