package com.yiban.rec.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.domain.vo.TradeCheckVo;

public interface ElectronicRecService {

	ResponseResult getFollowRecMap(String orgNo, String startDate, String endDate, String patType);

	ResponseResult payDetails(String orgNo, String startDate, String endDate,String billSource,String patType);

	List<Map<String, Object>> getFollowRecMapDetail(String startDate, String endDate, AppRuntimeConfig hConfig);

	String checkRefund(Long id, User user) throws Exception;

	Page<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable);

	Page<TradeCheckFollow> findDataByOrgNoAndTradeDate(TradeCheckFollowVo vo, Pageable pageable);

	Page<Map<String, Object>> findByOrgNoAndTradeDateModify(TradeCheckFollowVo vo, PageRequest pageRequest);

	ResponseResult getExceptionTradeDetail(String recHisId, String recThirdId,String businessNo, String orgNo, String orderState, String tradeTime,String billSource);

	public List<Map<String, Object>> getExportExceptionDetailBill(TradeCheckFollowVo vo);

	public List<TradeCheckFollow> exportToDcExcel(TradeCheckFollowVo cqvo, List<Organization> orgList,PageRequest pageable);

	Map<String, Object> getDiffAmount(TradeCheckFollowVo unusualBillVo);

	List<Map<String, Object>> getPayStep(String businessNo, String orgCodes, String billSource, String recHisId, String recThirdId);

	Map<String, TradeCheckVo> getPatIdMap(List<TradeCheckFollow> hisList);

	String concatOrgNoSql(String orgNo);

	List<TradeCheckFollow> filterDealBill(List<TradeCheckFollow> list);
}
