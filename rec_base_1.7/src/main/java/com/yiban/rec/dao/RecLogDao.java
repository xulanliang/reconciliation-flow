package com.yiban.rec.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.yiban.rec.domain.log.RecLog;

/**
 * 日志汇总Dao
 * @Author WY
 * @Date 2018年9月28日
 */
public interface RecLogDao extends JpaRepository<RecLog, Long>, JpaSpecificationExecutor<RecLog> {

    /**
     * 通过账单日期和机构查询
     * @param orderDate
     * @param orgCode
     * @return
     * RecLog
     */
    RecLog findByOrderDateAndOrgCode(String orderDate, String orgCode);
    
    /**
     * 通过账单日期和机构删除
     * @param orderDate
     * @param orgCode
     * @return
     * Long
     */
	Long deleteByOrderDateAndOrgCode(String orderDate, String orgCode);
	
	/**
	 * 通过日志状态查询对账结果
	 * @param recResult
	 * @return
	 * List<RecLog>
	 */
	List<RecLog> findByRecResult(Integer recResult);
	
	/**
     * 通过账单日期和机构查询
     * @param orderDate
     * @param orgCode
     * @return
     * RecLog
     */
    List<RecLog> findByOrderDateBetweenAndOrgCode(String startDate,String endDate, String orgCode);
    
}
