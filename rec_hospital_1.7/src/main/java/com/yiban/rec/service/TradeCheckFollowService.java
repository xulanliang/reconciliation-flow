package com.yiban.rec.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yiban.framework.account.domain.Organization;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.TradeCheckFollowQueryVo;

/**
 * 
 *
 * 项目名称：rec_hospital
 * 类名称：TradeCheckFollowService
 * 类描述：对账信息
 * 创建人：huangguojie
 * 创建时间：2018年5月18日 下午4:32:31
 * 修改人：huangguojie
 * 修改时间：2018年5月18日 下午4:32:31
 * 修改备注：
 * @version
 *
 */
public interface TradeCheckFollowService {

	/**
	 * 
	 * findAllHisPayPageByNotZP  获取对账差错明细
	 * 根据check_state查询对账差错明细
	 * @param TradeCheckFollowQueryVo
	 * @param List<Organization>
	 * @param PageRequest
	 * @return Page<TradeCheckFollow>
	 * @since CodingExample Ver(编码范例查看) 1.1
	 */
	Page<TradeCheckFollow> findAllHisPayPageByNotZP(TradeCheckFollowQueryVo vo, List<Organization> orgListTemp, Pageable pageable);

	/**
	 * 
	 * findAllHisPayPageByNotZP 导出对账差错明细
	 * 根据check_state查询对账差错明细
	 * @param TradeCheckFollowQueryVo
	 * @param List<Organization>
	 * @param PageRequest
	 * @return Page<TradeCheckFollow>
	 * @since CodingExample Ver(编码范例查看) 1.1
	 */
	List<TradeCheckFollow> findAllHisPayPageByNotZP(TradeCheckFollowQueryVo vo, List<Organization> orgListTemp, Sort sort);
	
	/**
	 * 得出异常账单的冲正数据
	 */
	public String getTradeCheckFollow(String satarTime,String endTime,String surface);
}
