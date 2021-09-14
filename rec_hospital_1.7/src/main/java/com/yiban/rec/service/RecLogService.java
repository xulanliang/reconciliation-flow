package com.yiban.rec.service;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springside.modules.persistence.SearchFilter;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.log.RecLog;

public interface RecLogService {
	
	/**
	* @date：2017年3月27日 
	* @Description：对账管理---获取历史数据状态查询
	* @return: 返回结果描述
	* @return Page<HistoryState>: 返回值类型
	* @throws
	 */
	Page<RecLog> getHistoryData(String orgNo, Date StartTime, Date endTime,List<Organization> orgListTemp, Pageable pageable);
	
	/**
	* @date：2018年4月27日 
	* @Description：对账管理---记录对账日志  
	* @throws
	 */
	Page<RecLog> getRecLogData(String orgNo,Date startDate,Date endDate,List<Organization> orgListTemp,int recState,Pageable pageable);
	
	/**
	 * 保存
	 * @param recLog
	 * @return
	 * RecLog
	 */
	RecLog save(RecLog recLog);
	
	/**
	 * 通过机构编码和拉取日期查询拉取日志
	 * @param orgNo
	 * @param orderDate
	 * @return RecLog
	 */
	RecLog findByOrderDateAndOrgCode(String orgNo, String orderDate);
	
	/**
	 * 查询分页数据
	 * @param filters
	 * @param pageable
	 * @return
	 * Page<RecLog>
	 */
	Page<RecLog> findPageByQueryParameters(List<SearchFilter> filters, 
            Pageable pageable);
}
