package com.yiban.rec.bill.parse.service.standardbill.impl.refund;


import com.yiban.rec.bill.parse.service.standardbill.RefundParent;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.domain.vo.RefundVo;

import net.sf.json.JSONObject;

public class CcbStandardRefund extends RefundParent {

	private RefundVo vo;
	
	public CcbStandardRefund(RefundVo vo,String url) {
		super(url);
		this.vo = vo;
	}

	@Override
	public String getRequestData() throws Exception {
		JSONObject params = new JSONObject();
		//params.put("requestSn", vo.getUnionPayCode());
		params.put("orderId", vo.getTsn());
		params.put("refundMoney", vo.getPayAmount());
		return params.toString();
	}

	@Override
	public String requestRefund(String data) {
		String result = HttpClientUtil.doPostJson(url, data);
		return result;
	}
	
	@Override
	public String analysisResult(String result) throws Exception {
		JSONObject ret = JSONObject.fromObject(result);
		String returnCode = ret.getString("returnCode");
		if("000000".equals(returnCode)) {
			return "退款成功";
		}else {
			throw new Exception("退款失败:"+ret.getString("returnMsg"));
		}
	}
}