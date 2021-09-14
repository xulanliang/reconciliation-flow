package com.yiban.rec.service.customized.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.dao.MixRefundDetailsDao;
import com.yiban.rec.domain.MixRefund;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.service.customized.AbstractRefundClass;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONObject;

public class ClearRefundClass extends AbstractRefundClass{

	
	private MixRefundDetailsDao mixRefundDetailsDao;
	
	public ClearRefundClass(MixRefundDetails vo,MixRefund mixRefund,BigDecimal sourceAmount) throws Exception {
		super(vo,sourceAmount);
		mixRefundDetailsDao=SpringBeanUtil.getBean(MixRefundDetailsDao.class);
		refund(vo,mixRefund,sourceAmount);
	}
	
	protected boolean isAllAmount() {
		return super.isAllAmount();
	}
	
	private void refund(MixRefundDetails vo,MixRefund mixRefund,BigDecimal sourceAmount) throws Exception {
		//url="http://192.168.19.207:8068/order/refund"; 
		try {
			String url=PropertyUtil.getProperty("pay.center.url", "")+"order/refund";
			//String url="http://192.168.24.52:8090/order/refund";
			Map<String,Object> map = new HashMap<String,Object>(10);
			//退款原因
			map.put("reason", mixRefund.getRefundReason());
			//医院His系统订单号
			map.put("orderNo", vo.getHisOrderNo());
			//微信或者支付宝流水号
			map.put("tsn", vo.getTsnOrderNo());
			//机构编码
			map.put("orgCode", mixRefund.getOrgCode());
			//支付渠道
			map.put("payCode", vo.getPayType());
			//退款金额
			if(!isAllAmount()) {//部分退款
				vo.setRefundAmount(sourceAmount);
				map.put("refundAmount", sourceAmount);
				map.put("batchRefundNo", mixRefundDetailsDao.refundCount(vo.getTsnOrderNo(),RefundEnumType.REFUND_SUCCESS.getId())+1);
			}else {
				vo.setRefundAmount(vo.getPayAmount());
			}
			JSONObject jsonObject = JSONObject.fromObject(map);
			String retStr=null;
			retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
			JSONObject json =JSONObject.fromObject(retStr);
			if(!json.getBoolean("success")) {//正常退费失败
				Exception exception=new Exception(json.getString("message"));
				vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
				throw exception;
			}else {
				if(!json.getString("data").equals("SUCCESS")&&!json.getString("data").equals("PROCESSING")) {//退款失败
					Exception exception=new Exception("退款结果未知查询中");
					vo.setRefundState(RefundEnumType.REFUND_NO_EXCEPTION.getId());
					throw exception;
				}else {
					vo.setRefundState(RefundEnumType.REFUND_SUCCESS.getId());
				}
			}
		} catch (Exception e) {//程序异常也属于正常退费失败
			if(vo.getRefundState()!=RefundEnumType.REFUND_NO_EXCEPTION.getId()) {
				vo.setRefundState(RefundEnumType.REFUND_FAILURE.getId());
			}
			throw e;
		}
	}
	
}