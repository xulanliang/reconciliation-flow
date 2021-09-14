package com.yiban.rec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;

public interface CashRecService {

	Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable);

	ResponseResult getFollowRecMap(String orgNo, String startDate, String endDate);

	public ResponseResult deal(Long id, String description, MultipartFile file, String userName);

	ResponseResult getExceptionTradeDetail(String recHisId, String recThirdId, String businessNo, String orgNo,
			String tradeTime);

}
