package com.yiban.rec.service;

import java.util.List;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.basicInfo.RetryOfHisToCenterTaskFailInfo;

public interface RetryOfHisToCenterTaskFailInfoService {

	ResponseResult save(RetryOfHisToCenterTaskFailInfo retryOfTaskFailInfo);
	
	ResponseResult save(List<RetryOfHisToCenterTaskFailInfo> channelOrderTaskInfos);

	ResponseResult delete(Long id);

	ResponseResult updateStatus(Integer status, String orgNo,Integer billType,Integer startPosition,Integer endPosition,String billDate);

	List<RetryOfHisToCenterTaskFailInfo> listOfFail();
	
}
