package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.service.standardbill.impl.his.HisWebServiceBillParser;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.util.StringUtil;

/**
 * 标准his webservice接口解析 将his的webservice接口转化为标准的restful类型的接口
 * 具体的转化功能由python实现,该功能实现了拉取账单和对账业务的分离,确保拉取账单失败后不会对对账业务造成影响
 * his接口经常变动，该功能能做到his接口变的情况下，只需要改变python代码而不用改java代码,极大的减少了java代码的发包次数
 * 
 * @author niujinlong
 *
 */
public class HisWebserviceStandardBillParser extends HisWebServiceBillParser<HisTransactionFlow> {

	@Override
	public List<HisTransactionFlow> getList() throws BillParseException {
		// TODO Auto-generated method stub
		List<HisTransactionFlow> list = new ArrayList<HisTransactionFlow>();
		String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisWebserviceUrl);
		Map<String, String> parm = new HashMap<String, String>();
		parm.put("startTime", startTime);
		parm.put("endTime", endTime);
		JSONObject jsonObj = null;
		String result = "";
		try {
			result = HttpClientUtil.doPost(url, parm);
			logger.info("返回原始结果：" + result);
			jsonObj = JSONObject.parseObject(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("请求异常：" + e.getMessage());
			throw new BillParseException("无法拉到his账单，请检查是否连到医院内网");
		}
		return analysisBill(jsonObj);
	}

	private List<HisTransactionFlow> analysisBill(JSONObject jsonObject) throws BillParseException {
		List<HisTransactionFlow> hisList = new ArrayList<HisTransactionFlow>();
		try {
			String code = jsonObject.getString("code");
			if (StringUtil.isEmpty(code) || !code.equals("200")) {
				logger.info("his账单为空");
				return null;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			JSONArray jsonArray = jsonObject.getJSONArray("data");

			for (int i = 0; i < jsonArray.size(); i++) {
				HisTransactionFlow his = new HisTransactionFlow();
				JSONObject js = jsonArray.getJSONObject(i);

				String payFlowNo = js.getString("payFlowNo");
				String shopFlowNo = js.getString("shopFlowNo");
				String hisFlowNo = js.getString("hisFlowNo");
				String payType = js.getString("payType");
				String billSource = js.getString("billSource");
				String payBusinessType = js.getString("payBusinessType");
				String patientCardNo = js.getString("patientCardNo");
				String patType = js.getString("patType");
				String cashier = js.getString("cashier");
				String patientName = js.getString("patientName");
				String orderState = js.getString("orderState");
				String payAmount = js.getString("payAmount");
				String tradeDateTime = js.getString("tradeDateTime");

				his.setPayFlowNo(payFlowNo);
				his.setShopFlowNo(shopFlowNo);
				his.setHisFlowNo(hisFlowNo);
				his.setPayType(payType);
				his.setBillSource(billSource);
				his.setPayBusinessType(payBusinessType);
				his.setPatType(patType);
				if (patType.equals("mz")) {
					his.setMzCode(patientCardNo);
				} else {
					his.setPatCode(patientCardNo);
				}
				his.setCashier(cashier);
				his.setCustName(patientName);
				his.setOrderState(orderState);
				if (StringUtils.isNotBlank(payAmount)) {
					BigDecimal bigDecimal = new BigDecimal(payAmount);
					his.setPayAmount(StringUtils.isNotBlank(payAmount) ? bigDecimal.abs() : new BigDecimal(0));
				}

				if (StringUtil.checkNotNull(tradeDateTime)) {
					Date date = sdf.parse(tradeDateTime);
					his.setTradeDatatime(date);
				} else {
					String pamentDate = startTime + " 00:00:00";
					his.setTradeDatatime(sdf.parse(pamentDate));
				}
				hisList.add(his);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new BillParseException("his接口有问题，请检查his接口是否正常");
		}
		return hisList;
	}

	public static void main(String[] args) {
		JSONObject js = new JSONObject();
		js.put("k", null);
		String res = js.getString("k");
		System.out.println(res);
	}

}
