package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ChannelSrandardBillParser extends AbstractBillParser<ThirdBill>{

	@Override
	protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		getHisList(date,orgCode);
		return null;
	}

	private List<ThirdBill> getHisList(String time, String orgCode) throws BillParseException {
		try {
			//获取参数
			String url=ProConfigManager.getValueByPkey(entityManager, ProConstants.channelUrl);
			if(StringUtils.isBlank(url)) {
				logger.info("请求渠道拉取账单的url:" + "配置文件中请求渠道接口未配置");
				throw new BillParseException("配置文件中请求渠道接口未配置");
			}else {
				logger.info("请求渠道拉取账单的url:" + url);
			}
			Map<String,Object> map = new HashMap<String,Object>(10);
			map.put("startTime ", time+" 00:00:00");
			map.put("endTime", time+" 23:59:59");
			// 调用渠道接口，获取返回值
			JSONObject jsonObject = JSONObject.fromObject(map);  
			String retStr=null;
			retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
			JSONObject json=JSONObject.fromObject(retStr);
			if(!json.getBoolean("success")) {
				throw new BillParseException("调用渠道接口失败");
			}
			logger.info("渠道返回的结果:" + json.toString());
			// 将返回list
			return saveData(json.getJSONArray("data"));
		} catch (BillParseException e) {
			logger.info("渠道标准化拉取账单异常:" + e.getMessage());
			throw e;
		} catch (Exception e) {
			throw new BillParseException("请求渠道接口异常："+e.getMessage());
		}
	}
	/**
	 * 解析账单
	 * @param response
	 * @param orgCode
	 * @return
	 * @throws Exception 
	 */
	private List<ThirdBill> saveData(JSONArray json) throws BillParseException {
		List<ThirdBill> channelList = new ArrayList<ThirdBill>();
		for(int i=0;i<json.size();i++) {
			JSONObject jsonObject =  json.getJSONObject(i);
			ResponseResult result = checkData(jsonObject);
			if(!result.isSuccess()) {
				throw new BillParseException(result.getMessage());
			} 
			ThirdBill   vo = new ThirdBill();
			vo.setPayType(jsonObject.getString("payType"));
			vo.setPayFlowNo(jsonObject.getString("payFlowNo"));
			vo.setPayAmount(new BigDecimal(jsonObject.getDouble("payAmount")));
			vo.setTradeDatatime(DateUtil.getBeginDayOfTomorrow(jsonObject.getString("tradeDatatime"),"yyyy-MM-dd HH:mm:ss"));
			vo.setOrderState(jsonObject.getString("orderState"));
			vo.setPayBusinessType(jsonObject.getString("payBusinessType"));
			vo.setPatType(jsonObject.getString("patType"));
			vo.setBillSource(EnumTypeOfInt.BILL_SOURCE_SELF.getValue());
			vo.setOutTradeNo(jsonObject.getString("outTradeNo"));
			channelList.add(vo);
		}
		return channelList;
	}
	
	private ResponseResult checkData(JSONObject jsonObject) {
		if(StringUtils.isBlank(jsonObject.getString("payType"))) {
			ResponseResult.failure("上传数据机payType：支付类型不能为空");
		}
		if(StringUtils.isBlank(jsonObject.getString("payFlowNo"))) {
			ResponseResult.failure("上传数据payFlowNo：支付流水号不能为空");
		}
		if(StringUtils.isBlank(jsonObject.getString("payAmount"))) {
			ResponseResult.failure("上传数据payAmount：支付金额不能为空");
		}
		if(StringUtils.isBlank(jsonObject.getString("tradeDatatime"))) {
			ResponseResult.failure("上传数据tradeDatatime：交易时间不能为空");
		}
		if(StringUtils.isBlank(jsonObject.getString("orderState"))) {
			ResponseResult.failure("上传数据orderState：订单状态不能为空");
		}
		return ResponseResult.success();
	}
	
}
