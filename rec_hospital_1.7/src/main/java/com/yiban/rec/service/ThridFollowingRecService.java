package com.yiban.rec.service;

import java.util.List;

import com.yiban.rec.domain.FollowRecResult;
import com.yiban.rec.domain.vo.AppRuntimeConfig;

public interface ThridFollowingRecService {
	List<FollowRecResult> getFollowRecMap(String startDate,String endDate,AppRuntimeConfig hConfig);
}
