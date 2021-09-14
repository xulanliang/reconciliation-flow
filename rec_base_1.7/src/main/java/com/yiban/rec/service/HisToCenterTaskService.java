package com.yiban.rec.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.basicInfo.RetryOfHisToCenterTaskFailInfo;
import com.yiban.rec.domain.task.HisToCenterTaskInfo;

public interface HisToCenterTaskService {
	HisToCenterTaskInfo getHisToCenterTaskInfoById(Long id);
	HisToCenterTaskInfo getHisToCenterTaskInfoByName(String jobName);

	ResponseResult save(HisToCenterTaskInfo hisToCenterTaskInfo);

	ResponseResult delete(Long id);

	ResponseResult update(HisToCenterTaskInfo hisToCenterTaskInfo);

	Page<Map<String, Object>> getHisToCenterTaskInfoList(OprPageRequest pagerequest, Long orgId);

	ResponseResult updateStatus(Integer status, Long id);

	List<HisToCenterTaskInfo> getHisToCenterTaskInfo();

	List<HisPayResult> getHisPayResultOfYesterday(Date startDate,Date endDate, List<Long> orgIds);

	List<Platformflow> getPlatformflowsOfYesterday(Date startDate,Date endDate, List<Long> orgIds);

	List<ThirdBill> getThirdBillsOfYesterday(Date startDate,Date endDate, List<Long> orgIds);
	
	List<HisPayResult> getHisPayResultOfYesterdayById(Date startDate,Date endDate, String orgId);

	List<HisTransactionFlow> getPlatformflowsOfYesterdayById(Date startDate,Date endDate, String orgId);

	List<ThirdBill> getThirdBillsOfYesterdayById(Date startDate,Date endDate, String orgId);

	List<HisPayResult> getHisPayResultOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition, Integer endPosition);

	List<HisTransactionFlow> getPlatformflowsOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition, Integer endPosition);

	List<ThirdBill> getThirdBillsOfFail(Date startDate,Date endDate, String orgNo, Integer startPosition, Integer endPosition);

	List<Map<String, Object>> getOrgSimpleInfosByOrgCode(Set<String> orgCodes);
	Map<String, Object> getOrgSimpleInfosByOrgCode(String orgCode);

	List<Map<String, Object>> getOrgSimpleInfosByOrgNo(Set<Long> orgNos);
	Map<String, Object> getOrgSimpleInfosByOrgNo(Long orgNo);

	List<Map<String, Object>> getSimpleMetaDatasByPayType(Set<Integer> payTypes);

	List<Map<String, Object>> getSimpleMetaDatasByPayCode(Set<String> payCodes);

	Page<RetryOfHisToCenterTaskFailInfo> getetryOfHisToCenterTaskFailInfo(String orgNo, Integer billType,
			String billDate, Integer startPosition, Integer endPosition);

	List<HisPayResult> getHisPayResultOfYesterday2();

	List<Platformflow> getPlatformflowsOfYesterday2();

	List<ThirdBill> getThirdBillsOfYesterday2();
	
    HisToCenterTaskInfo getHisToCenterInfo(Long orgNo);
}
