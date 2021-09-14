package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.rec.domain.task.ChannelScheduleInfo;

public interface ChannelScheduleInfoService {
	 ChannelScheduleInfo getChannelScheduleInfoById(Long id);
	 ChannelScheduleInfo getChannelScheduleInfoByName(String jobName);
	 
	 ResponseResult save(ChannelScheduleInfo channelScheduleInfo);
	 
	 ResponseResult delete(Long id);
	 
	 ResponseResult update(ChannelScheduleInfo channelScheduleInfo);
	 
	 Page<Map<String,Object>> getChannelScheduleInfoList(OprPageRequest pagerequest,Long orgId);
	 
	 ResponseResult updateStatus(Integer status,Long id);
	 
	 List<ChannelScheduleInfo> getChannelScheduleInfosOfYesterday();
	 
	 List<ChannelScheduleInfo> getChannelScheduleInfosOfBeforeTwoDay();
}
