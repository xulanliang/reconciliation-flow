package com.yiban.rec.bill.parse.service.standardbill.impl.his;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.JsonUtil;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HisStandardHttpBillParser<T> extends AbstractHisBillParser<HisTransactionFlow>{
	
	protected enum RequestMtechodEnum{
		POST,GET;
	}
	
	@Override
	protected List<HisTransactionFlow> getHisList(String startTime, String endTime, String orgCode)
			throws BillParseException {
		String url = getUrl();
		logger.info("--->his账单请求URL：{}",url);
		Map<String, String> params = getParams(startTime, endTime);
		logger.info("--->his账单请求参数：{}",params);
		String response = loadResponseData(url, params);
		logger.info("--->his账单请求结果：{}",response);
		return parseBill(response, orgCode);
	}
	
	protected List<HisTransactionFlow> parseBill(String response,String orgCode) throws BillParseException {
		List<HisTransactionFlow> hisList = new ArrayList<HisTransactionFlow>();
		if(StringUtil.isEmpty(response)){
			return hisList;
		}
		JSONObject json = JSONObject.fromObject(response);
		if (!"SUCCESS".equals(json.getString("resultCode"))) {
			logger.info("调用HIS接口失败：{}",json.getString("resultMsg"));
			return hisList;
		}
		JSONArray jsonArray = json.getJSONArray("orderItems");
		for (int i = 0; i < jsonArray.size(); i++) {
			HisTransactionFlow his = parseByItem(jsonArray.getJSONObject(i));
			if(his!=null){
				if(StringUtil.isEmpty(his.getOrgNo())){
					his.setOrgNo(orgCode);
				}
				if(his.getCreatedDate()==null){
					his.setCreatedDate(new Date());
				}
				hisList.add(his);
			}
		}
		return hisList;
	}
	
	protected HisTransactionFlow parseByItem(JSONObject jsonObject) {
		HisTransactionFlow vo = new HisTransactionFlow();
		vo.setPayType(getPayType(jsonObject));
		vo.setPayFlowNo(getPayFlowNo(jsonObject));
		vo.setHisFlowNo(jsonObject.getString("hisOrderNo"));
		vo.setPayAmount(new BigDecimal(jsonObject.getString("payAmount").replace(",", "")).abs());
		vo.setTradeDatatime(
				DateUtil.getBeginDayOfTomorrow(jsonObject.getString("tradeDateTime"), "yyyy-MM-dd HH:mm:ss"));
		vo.setOrderState("交易正常".equals(jsonObject.getString("orderState"))?EnumTypeOfInt.PAY_CODE.getValue():EnumTypeOfInt.REFUND_CODE.getValue());
		vo.setPayBusinessType(jsonObject.getString("payBusinessType"));
		vo.setPatType(jsonObject.getString("patType"));
		vo.setCustName(jsonObject.getString("patientName"));
		vo.setCashier(jsonObject.getString("cashier"));
		vo.setMzCode(jsonObject.getString("patientCardNo"));
		vo.setPatCode(jsonObject.getString("patientCardNo"));
		vo.setVisitNumber(jsonObject.getString("patientCardNo"));
		vo.setBillSource(getBillSource(jsonObject));
		vo.setRequireRefund(loadRequireRefund(vo));
		super.addBillSource(vo.getBillSource());
		return vo;
	}

	protected Integer loadRequireRefund(HisTransactionFlow vo) {
		return 1;
	}

	protected String getBillSource(JSONObject jsonObject) {
		return getBillSource(jsonObject.getString("billSource"));
	}

	protected String getBillSource(String billSource) {
		return billSource;
	}

	protected String getPayFlowNo(JSONObject jsonObject) {
		String payFlowNo = jsonObject.getString("tsnOrderNo");
		if(StringUtil.isEmpty(payFlowNo)){
			payFlowNo = jsonObject.getString("orderNo");
		}
		if(StringUtil.isEmpty(payFlowNo)){
			payFlowNo = jsonObject.getString("outTradeNo");
		}
		return payFlowNo;
	}

	protected String getPayType(JSONObject jsonObject) {
		return getPayType(jsonObject.getString("payType"));
	}

	protected String getPayType(String payType) {
		return payType;
	}

	protected String loadResponseData(String url,Map<String, String> params){
		String response = "";
		if(RequestMtechodEnum.POST == getRequestMtehod()){
			Map<String, String> header = loadHeaderMap();
			response = HttpClientUtil.doPostJson(url,header, JsonUtil.map2json(params));
		}else{
			try {
				response = HttpClientUtil.doGet(url, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	protected Map<String, String> loadHeaderMap() {
		Map<String, String> header = new HashMap<>();
		header.put("Accept", "*/*");
		return header;
	}

	/**
	 * 获取请求方式
	 * @return
	 * @author jxl
	 * @Time 2020年11月10日下午1:58:44
	 */
	protected RequestMtechodEnum getRequestMtehod(){
		return RequestMtechodEnum.POST;
	}
	
	/**
	 * 获取请求地址URL
	 * @return
	 * @author jxl
	 * @Time 2020年11月10日上午11:56:14
	 */
	protected String getUrl(){
		String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisUrl);
		return url;
	}
	
	protected Map<String, String> getParams(String startTime,String endTime){
		Map<String, String> map = new HashMap<String, String>();
		map.put("startDateTime", startTime + " 00:00:00");
		map.put("EndDateTime", endTime + " 23:59:59");
		return map;
	}
	
}
