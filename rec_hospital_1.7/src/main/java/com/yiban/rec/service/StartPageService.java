package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import com.yiban.framework.account.domain.User;
import com.yiban.rec.domain.log.RecLog;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.domain.vo.AppRuntimeConfig;

/**
 * 首页Service
 * @Author WY
 * @Date 2018年11月13日
 */
public interface StartPageService {
	Map<String, Object> getRecInfo(String orgNo,User user);
	Map<String, Object> getRecInfoByDay(String date , String orgNo);
	List<RecLog> getExceptionDate(String startDate, String endDate, AppRuntimeConfig hConfig);
	List<RecLogDetails> getExceptionInfo(String startDate, String endDate, AppRuntimeConfig hConfig);
}
