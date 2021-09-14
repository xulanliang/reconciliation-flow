package com.yiban.rec.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import com.yiban.rec.domain.log.RecLogDetails;

/**
 * 对账日志详情
 * @Author WY
 * @Date 2018年9月28日
 */
public interface RecLogDetailsDao extends JpaRepository<RecLogDetails, Long>, 
    JpaSpecificationExecutor<RecLogDetails> {

    /**
     * 通过账单日期和机构编码查询对账日志明细
     * @param orderDate
     * @param orgCode
     * @return List<ResLogDetails>
     */
    List<RecLogDetails> findByOrderDateAndOrgCode(String orderDate, String orgCode);
    
    /**
     * 删除通过机构和账单日期
     * @param orderDate
     * @param orgCode
     * @return
     * Long
     */
    @Transactional
    Long deleteByOrderDateAndOrgCode(String orderDate, String orgCode);
    
    /**
     * 通过机构、账单日期、日志类型、对账类型删除
     * @param orderDate
     * @param orgCode
     * @param logType
     * @param payType
     * @return
     * Long
     */
    @Transactional
    Long deleteByOrderDateAndOrgCodeAndLogTypeAndPayType(String orderDate, 
            String orgCode, String logType, String payType);
    
    
    /**
     * 通过账单日期和机构编码和日志类型查询对账日志明细
     * @param orderDate
     * @param orgCode
     * @return List<ResLogDetails>
     */
    List<RecLogDetails> findByOrderDateAndOrgCodeAndLogType(String orderDate, String orgCode,String logType);
    
    /**
     * 通过机构编码和日期查询日志明细
     * @param orderDate
     * @param orgCodes
     * @return
     * List<RecLogDetails>
     */
    List<RecLogDetails> findByOrderDateAndLogTypeAndOrgCodeIn(String orderDate, String logType, Set<String> orgCodes);
}
