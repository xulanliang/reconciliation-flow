package com.yiban.rec.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.domain.vo.RefundVo;
import com.yiban.rec.service.ThirdRefundService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.WebServiceClientUtil;

@Service
public class ThirdRefundServiceImpl implements ThirdRefundService {

	@Autowired
	private ThirdBillDao thirdBillDao;
	
	/**
	 * 唐都金蝶退费
	 * @param reason
	 * @param orgNo
	 * @param endpoint
	 * @param username
	 * @param password
	 * @param method
	 * @param bill
	 * @throws Exception
	 */
	public void tangDuJd(RefundVo vo) throws Exception {
		/*ThirdBill bill=new ThirdBill();
		//查找出支付渠道记录
		List<ThirdBill> list = thirdBillDao.findByOrderNoAndOrderState(vo.getOrderNo());
		//微信支付宝订单号
		if(list.size()>0) {
			String orderNo=list.get(0).getShopFlowNo();
			if(org.apache.commons.lang.StringUtils.isBlank(orderNo)) {
				Exception exception=new Exception("微信/支付宝订单号为空");
				throw exception;
			}
		}*/
		// 调金蝶退费接口用
		String endpoint = PropertyUtil.getProperty("tangdu.endpoint", "");
		String username = PropertyUtil.getProperty("tangdu.username", "");
		String password = PropertyUtil.getProperty("tangdu.password", "");
		String method = PropertyUtil.getProperty("tangdu.refund.method", "");
		Map<String, String> data = new HashMap<>();
		data.put("orderId", vo.getTsn());
		data.put("refundTime", DateUtil.getCurrentTimeString());
		data.put("refundFee", String.valueOf(new BigDecimal(vo.getTradeAmount()).multiply(new BigDecimal(100)).intValue()));
		data.put("refundNo", vo.getTsn());
		data.put("reason", vo.getReason());
		String reqXml = null;
		try {
			reqXml = WebServiceClientUtil.mapToXml(data);
		} catch (Exception e) {
			Exception exception=new Exception("mapToXml error: "+e);
			throw exception;
		}
		reqXml = reqXml.substring(reqXml.indexOf(">") + 1, reqXml.length());
		WebServiceClientUtil.himapwsCallService(endpoint, vo.getOrgCode(), method, reqXml, username, password);
	}
}
