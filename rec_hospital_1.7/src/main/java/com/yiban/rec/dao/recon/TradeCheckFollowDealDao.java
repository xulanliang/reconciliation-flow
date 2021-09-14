package com.yiban.rec.dao.recon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.recon.TradeCheckFollowDeal;

/**
 * 异常处理记录Dao
 * @Author WY
 * @Date 2018年11月16日
 */
public interface TradeCheckFollowDealDao
        extends JpaRepository<TradeCheckFollowDeal, Long>, JpaSpecificationExecutor<TradeCheckFollowDeal> {
    TradeCheckFollowDeal findByPayFlowNo(boolean b);
    TradeCheckFollowDeal findByPayFlowNo(String payFlowNo);
//    TradeCheckFollowDeal findByPayFlowNoAndOrgCodeAndTradeDatetime(String payFlowNo,String orgCode,String tradeDatetime);
    TradeCheckFollowDeal findFirstByPayFlowNoAndOrgCodeAndTradeDatetimeOrderByCreatedDateDesc(String payFlowNo,String orgCode,String tradeDatetime);
    List<TradeCheckFollowDeal> findByTradeDatetime(String tradeDatetime);
    
    List<TradeCheckFollowDeal> findByTradeDatetimeBetween(String startTime,String endTime);
    List<TradeCheckFollowDeal> findByPayFlowNoAndOrgCode(String payFlowNo, String orgCode);
    //删除抹平记录
    void deleteByPayFlowNoAndOrgCodeAndTradeDatetimeAndExceptionState(String payFlowNo,String orgCode,String tradeDatetime, String exceptionState);
}
