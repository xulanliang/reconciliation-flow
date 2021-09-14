package com.yiban.rec.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.Platformflow;
import com.yiban.rec.domain.RecCash;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.CashQueryVo;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.domain.vo.RefundRequestVo;
import com.yiban.rec.domain.vo.TradeDetailQueryVo;
import com.yiban.rec.domain.vo.TradeDetailVo;

public interface ReconciliationService {


	/**
	 * @date：2017年3月27日
	 * @Description：获取对账结果详细信息
	 * @param vo
	 * @return: 返回结果描述
	 * @return Page<Reconciliation>: 返回值类型
	 * @throws
	 */
	public Page<Reconciliation> getRecpage(RecQueryVo rqvo,Pageable pageable);
	public List<Reconciliation> getRecpageNopage(RecQueryVo rqvo,Sort sort);

	//双方对账
	public Page<TradeCheckFollow> getTwoRecpage(RecQueryVo rqvo,Pageable pageable);

	/**
	 * @date：2017年3月27日
	 * @Description：现金交易查询-查询平台现金记录
	 * @param vo
	 * @return: 返回结果描述
	 * @return Page<Platformflow>: 返回值类型
	 * @throws
	 */
	public Page<Platformflow> getPlatPage(TradeDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);

	/**
	 * @date：2017年3月27日
	 * @Description：现金交易查询-查询his现金记录
	 * @param vo
	 * @return: 返回结果描述
	 * @return Page<HisPayResult>: 返回值类型
	 * @throws
	 */
	public Page<HisPayResult> getHisPage(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);

	/**
	 * @date：2017年3月27日
	 * @Description：现金交易查询-查询his现金记录
	 * @param vo
	 * @return: 返回结果描述
	 * @return Page<HisPayResult>: 返回值类型
	 * @throws
	 */
	public Page<ThirdBill> getThridPage(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);

	public Page<Map<String, Object>> getThridAllPage(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);

	public List<Map<String, Object>> getThridAllNoPage(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);


	public ResponseResult edit(Long id,BigDecimal platformAmount,String remarkInfo,Integer handleCode,String loginName) throws BusinessException;

	/**
	 * @date：2017年3月27日
	 * @Description：查询交易流水
	 * @param vo
	 * @return: 返回结果描述
	 * @return Page<PlatformflowLog>: 返回值类型
	 * @throws
	 */
	public Map<String,Object> getPlatformflowLog(String flowNo);

	/**
	 * @date：2017年4月7日
	 * @Description：导出条件查询数据
	 * @param rqvo
	 * @return: 返回结果描述
	 * @return List<Reconciliation>: 返回值类型
	 * @throws
	 */
	public List<Reconciliation> getRecDetailList(RecQueryVo rqvo,Pageable page);

	public List<TradeCheckFollow> getRecTwoDetailList(RecQueryVo rqvo,Pageable page);

	/**
	 * @date：2017年4月7日
	 * @Description：现金管理-导出平台条件查询数据
	 * @param rqvo
	 * @return: 返回结果描述
	 * @return List<Reconciliation>: 返回值类型
	 * @throws
	 */
	public List<Platformflow> getRecCashPlatList(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable page);

	/**
	 * @date：2017年4月7日
	 * @Description：现金管理-导出his条件查询数据
	 * @param rqvo
	 * @return: 返回结果描述
	 * @return List<Reconciliation>: 返回值类型
	 * @throws
	 */
	public List<RecCash> getRecCashHisList(CashQueryVo cqvo,List<Organization> orgListTemp,Pageable page);

	public List<HisPayResult> getRecCashHisList(TradeDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable page);


	public List<Platformflow> getPlatformflow();
	public Page<Platformflow> getPagePlatformflow(Pageable pageable);
	public RecCash findByPayFlowNo(String payFlowNo);

	public ResponseResult updateSingle(Long id,User user);
	//现金的
	public Page<RecCash> getTradeDetailPage(TradeDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);
	//电子明细的
	public Page<HisPayResult> getTradeDetailPageHis(TradeDetailQueryVo cqvo,List<Organization> orgListTemp,Pageable pageable);


	Map<String,Object> getTradeCollect(TradeDetailQueryVo cqvo,List<Organization> orgList);

	List<Reconciliation> getRecMap(String orgNo, String tradeDate,String endDate);

	public String updateThirdFollowing(String type,Long id,String orderNo,String reason,String url,String orgNo,
									   String payCode,String tradeAmount,String batchRefundNo,
									   String billSource,String imgUrl,Long userId)throws Exception;
	public Page<RecCash> getCash(Integer id,Pageable pageable);
	public Page<HealthCareOfficial> getYiBao(Integer id,Pageable pageable);
	public Page<ThirdBill> getBill(Integer id,Pageable pageable);
	public ResponseResult dealFollow(String userName, String payFlowNo, String description,MultipartFile file) ;
	public ResponseResult newDealFollow(String userName, String payFlowNo, String description,MultipartFile file, String checkState,
										String tradeAmount,String orgCode, String tradeDatetime,String payName,String billSource, String patType,
										String recHisId, String recThridId) ;

	public Page<Map<String, Object>> getTradeDetailPage(TradeDetailVo tdv,List<Organization> orgList,PageRequest pageRequest);
	public List<Map<String, Object>> exportTradeDetail(TradeDetailVo cqvo,List<Organization> orgListTemp, PageRequest pageRequest);
	public Map<String, Object> getTradeRefundDetail(String id);
	ResponseResult updateDetailById(Long id, User user);
	public Map<String, Object> getExceptionHandlingRecord(String paymentRequestFlow);

	public ResponseResult electronicRecRefund(RefundRequestVo requestVo,  User user) throws Exception;

	public Map<String, Object> updateDifferenceAmount(String tradeTime, String orgNo);
	public List<Map<String, Object>> getRefundInfo(String orgNo, String payFlowNo);
	public List<Map<String, Object>> getTradeDetailCollect(TradeDetailVo tdv, List<Organization> orgList);
}
