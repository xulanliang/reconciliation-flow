package com.yiban.rec.dao.task;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.task.HisToCenterTaskInfo;

public interface HisToCenterTaskDao
		extends JpaRepository<HisToCenterTaskInfo, Long>, JpaSpecificationExecutor<HisToCenterTaskInfo> {

	@Transient
	@Modifying
	@Query("update HisToCenterTaskInfo h set h.jobsstatus = ?1 where h.id = ?2")
	void updateStatus(Integer status,Long id);
	
	@Query("select h from HisToCenterTaskInfo h where h.jobsstatus =1 AND h.isDeleted=0 order by h.createdDate asc")
    List<HisToCenterTaskInfo> getHisToCenterTaskInfoOfYesterday();
	
	@Query("select h from HisToCenterTaskInfo h where h.jobsstatus =1 AND h.isDeleted=0 and h.orgNo=?1 order by h.createdDate asc")
    HisToCenterTaskInfo getHisToCenterInfoByOrgNo(Long orgNo);
	
	@Query("select h from HisToCenterTaskInfo h where h.jobsstatus =1 AND h.isDeleted=0 and h.jobname=?1 order by h.createdDate asc")
    HisToCenterTaskInfo getHisToCenterTaskInfoByName(String jobName);
	
	@Query("select h from HisPayResult h where h.tradeDatatime>=?1 AND h.tradeDatatime<?2 AND h.orgNo in ?3 order by h.id asc")
	List<HisPayResult> getHisPayResultOfYesterday(Date startDate,Date endDate,List<Long> orgIds);

	
	@Query("select p from Platformflow p where p.tradeDatatime>=?1 AND p.tradeDatatime<?2 AND p.orgNo in ?3 order by p.id asc")
	List<Platformflow> getPlatformflowsOfYesterday(Date startDate,Date endDate,List<Long> orgIds);
	
	@Query("select t from ThirdBill t where t.tradeDatatime>=?1 AND t.tradeDatatime<?2 AND t.orgNo in ?3 order by t.id asc")
	List<ThirdBill> getThirdBillsOfYesterday(Date startDate,Date endDate,List<Long> orgIds);
	
	
	@Query("select h from HisPayResult h where h.tradeDatatime>=?1 AND h.tradeDatatime<?2 AND h.orgNo = ?3 order by h.id asc")
	List<HisPayResult> getHisPayResultOfYesterdayById(Date StartDate,Date endDate,String orgId);

	
	@Query("select p from HisTransactionFlow p where p.tradeDatatime>=?1 AND p.tradeDatatime<?2 AND p.orgNo = ?3 order by p.id asc")
	List<HisTransactionFlow> getPlatformflowsOfYesterdayById(Date startDate,Date endDate,String orgId);
	
	@Query("select t from ThirdBill t where t.tradeDatatime>=?1 AND t.tradeDatatime<?2 AND t.orgNo = ?3 order by t.id asc")
	List<ThirdBill> getThirdBillsOfYesterdayById(Date startDate,Date endDate,String orgId);
	
	
	@Query("select h from HisPayResult h where h.tradeDatatime>=?1 AND h.tradeDatatime<?2 AND h.orgNo = ?3 order by h.id asc")
	List<HisPayResult> getHisPayResultOfFail(Date startDate,Date endDate,String orgNo);

	
	@Query("select p from HisTransactionFlow p where p.tradeDatatime>=?1 AND p.tradeDatatime<?2 AND p.orgNo = ?3 order by p.id asc")
	List<HisTransactionFlow> getPlatformflowsOfFail(Date startDate,Date endDate,String orgNo);
	
	@Query("select t from ThirdBill t where t.tradeDatatime>=?1 AND t.tradeDatatime<?2 AND t.orgNo = ?3 order by t.id asc")
	List<ThirdBill> getThirdBillsOfFail(Date startDate,Date endDate,String orgNo);
	
	@Query("select h from HisPayResult h  order by h.id asc")
	List<HisPayResult> getHisPayResultOfYesterday2();

	
	@Query("select p from Platformflow p  order by p.id asc")
	List<Platformflow> getPlatformflowsOfYesterday2();
	
	@Query("select t from ThirdBill t order by t.id asc")
	List<ThirdBill> getThirdBillsOfYesterday2();
}
