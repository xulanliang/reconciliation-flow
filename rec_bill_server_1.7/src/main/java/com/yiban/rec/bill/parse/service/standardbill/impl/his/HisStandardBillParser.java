package com.yiban.rec.bill.parse.service.standardbill.impl.his;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HisStandardBillParser extends AbstractHisBillParser<HisTransactionFlow> {

	@Override
	protected List<HisTransactionFlow> getHisList(String startTime, String endTime, String orgCode)
			throws BillParseException {
		try {
			// 获取参数
			String url = ProConfigManager.getValueByPkey(entityManager, ProConstants.hisUrl);
			if (StringUtils.isBlank(url)) {
				logger.info("请求his的url:" + "配置文件中请求his接口未配置");
				throw new BillParseException("配置文件中请求his接口未配置");
			} else {
				logger.info("请求his的url:" + url);
			}
			Map<String, Object> map = new HashMap<String, Object>(10);
			map.put("startTime", startTime + " 00:00:00");
			map.put("endTime", endTime + " 23:59:59");
			// 调用his接口，获取返回值
			JSONObject jsonObject = JSONObject.fromObject(map);
			String retStr = null;
			retStr = new RestUtil().doPostJson(url, jsonObject.toString(), CommonConstant.CODING_FORMAT);
			JSONObject json = JSONObject.fromObject(retStr);
			if (!json.getBoolean("success")) {
				throw new BillParseException("调用his接口失败");
			}
			logger.info("his返回的结果:" + json.toString());
			// 将返回list
			return formatRestRet(json);
		} catch (BillParseException e) {
			logger.info("his标准化拉取账单异常:" + e.getMessage());
			throw e;
		} catch (Exception e) {
			throw new BillParseException("请求his接口异常：" + e.getMessage());
		}
	}

	public List<HisTransactionFlow> formatRestRet(JSONObject ret) throws BillParseException {
		return saveData(ret.getJSONArray("data"));
	}

	/**
	 * 解析账单
	 * 
	 * @param response
	 * @param orgCode
	 * @return
	 * @throws Exception
	 */
	private List<HisTransactionFlow> saveData(JSONArray json) throws BillParseException {
		List<HisTransactionFlow> hisList = new ArrayList<HisTransactionFlow>();
		for (int i = 0; i < json.size(); i++) {
			JSONObject jsonObject = json.getJSONObject(i);
			ResponseResult result = checkData(jsonObject);
			if (!result.isSuccess()) {
				throw new BillParseException(result.getMessage());
			}
			HisTransactionFlow vo = new HisTransactionFlow();
			vo.setOrgNo(ProConfigManager.getValueByPkey(entityManager, ProConstants.yibanProjectid));
			vo.setPayType(jsonObject.getString("payType"));
			if (StringUtils.isNotBlank(jsonObject.getString("settlementDate")))
				vo.setSettlementDate(
						DateUtil.getBeginDayOfTomorrow(jsonObject.getString("settlementDate"), "yyyy-MM-dd"));
			vo.setPayFlowNo(jsonObject.getString("payFlowNo"));
			vo.setPayAmount(new BigDecimal(jsonObject.getDouble("payAmount")));
			vo.setTradeDatatime(
					DateUtil.getBeginDayOfTomorrow(jsonObject.getString("tradeDatatime"), "yyyy-MM-dd HH:mm:ss"));
			vo.setOrderState(jsonObject.getString("orderState"));
			vo.setPayBusinessType(jsonObject.getString("payBusinessType"));
			vo.setPatType(jsonObject.getString("patType"));
			vo.setCustName(jsonObject.getString("custName"));
			vo.setDeptNo(jsonObject.getString("deptNo"));
			vo.setDeptName(jsonObject.getString("deptName"));
			vo.setCashier(jsonObject.getString("cashier"));
			vo.setHisFlowNo(jsonObject.getString("hisFlowNo"));
			vo.setMzCode(jsonObject.getString("mzCode"));
			vo.setPatCode(jsonObject.getString("patCode"));
			vo.setInvoiceNo(jsonObject.getString("invoiceNo"));
			vo.setVisitNumber(jsonObject.getString("visitNumber"));
			vo.setBillSource(jsonObject.getString("billSource"));
			hisList.add(vo);
		}
		return hisList;
	}

	private ResponseResult checkData(JSONObject jsonObject) {
		if (StringUtils.isBlank(jsonObject.getString("payType"))) {
			ResponseResult.failure("上传数据机payType：支付类型不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("payFlowNo"))) {
			ResponseResult.failure("上传数据payFlowNo：支付流水号不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("payAmount"))) {
			ResponseResult.failure("上传数据payAmount：支付金额不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("tradeDatatime"))) {
			ResponseResult.failure("上传数据tradeDatatime：交易时间不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("orderState"))) {
			ResponseResult.failure("上传数据orderState：订单状态不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("payBusinessType"))) {
			ResponseResult.failure("上传数据payBusinessType：业务类型不能为空");
		}
		if (StringUtils.isBlank(jsonObject.getString("patType"))) {
			ResponseResult.failure("上传数据patType：住院（zy）/门诊（mz）不能为空");
		}
		return ResponseResult.success();
	}

}
