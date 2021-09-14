package com.yiban.rec.service.customized.refundorder;

import java.util.HashMap;
import java.util.Map;

import com.yiban.framework.core.util.PropertyUtil;
import com.yiban.framework.core.util.SpringBeanUtil;
import com.yiban.rec.bill.parse.util.RefundEnumType;
import com.yiban.rec.dao.MixRefundDetailsDao;
import com.yiban.rec.domain.MixRefundDetails;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONObject;

public class ClearRefundOrder {

	private MixRefundDetailsDao mixRefundDetailsDao;
	
	public ClearRefundOrder(MixRefundDetails vo) throws Exception {
		mixRefundDetailsDao=SpringBeanUtil.getBean(MixRefundDetailsDao.class);
		query(vo);
	}
	
	
	private void query(MixRefundDetails vo) throws Exception {
		try {
			String payCenterUrl = PropertyUtil.getProperty("pay.center.url", "");
			String url=payCenterUrl+"order/refund/query";
			//String url="http://192.168.24.52:8090/order/refund/query";
			Map<String,Object> map = new HashMap<String,Object>(10);
			//微信或者支付宝流水号
			map.put("tsn", vo.getTsnOrderNo());
			//部分退款
			map.put("batchRefundNo", mixRefundDetailsDao.refundCount(vo.getTsnOrderNo(),RefundEnumType.REFUND_SUCCESS.getId())+1);
			JSONObject jsonObject = JSONObject.fromObject(map);
			String retStr=null;
			retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
			JSONObject json =JSONObject.fromObject(retStr);
			if(!json.getBoolean("success")) {
				Exception exception=new Exception(json.getString("message"));
				throw exception;
			}else {
				if(!json.getString("data").equals("SUCCESS")&&!json.getString("data").equals("PROCESSING")) {//退款失败
					Exception exception=new Exception("退款结果未知查询中");
					vo.setRefundState(RefundEnumType.REFUND_NO_EXCEPTION.getId());
					throw exception;
				}
			}
		} catch (Exception e) {//异常退费失败
			throw e;
		}
	}
}
