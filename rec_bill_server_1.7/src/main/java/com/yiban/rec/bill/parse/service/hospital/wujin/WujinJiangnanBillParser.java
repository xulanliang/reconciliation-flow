package com.yiban.rec.bill.parse.service.hospital.wujin;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.DateUtil;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.domain.ThirdBill;

public class WujinJiangnanBillParser extends AbstractBillParser<ThirdBill> {

	
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		logger.info("江南银行账单解析");
		String serverUrl = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String url = serverUrl + "/order/jnbank/downloadbill";
		Gson gson = new Gson();
		Map<String,String> map = new HashMap<>();
		map.put("systemCode", "50");
		map.put("orgCode", orgCode);
		map.put("txnDate", date);
		logger.info("江南银行请求url："+url+"--入参：orgCode:"+orgCode +"date" + date);
		List<ThirdBill> list = new ArrayList<ThirdBill>();
		String response = null;
		try {
			response = HttpClientUtil.doPostJson(url, gson.toJson(map).toString());
			logger.info("江南银行请求返回结果："+response);
			Map<String, String> rmap = gson.fromJson(response, Map.class);
			if(rmap != null){
			    String result = rmap.get("returnCode");
				if(result.equals("0000")){
					parseBill(list,rmap.get("data"),orgCode);
				}else {
				    throw new BillParseException(rmap.get("returnMsg"));
				}
			}else {
			    throw new BillParseException("服务器暂无响应");
			}
		} catch (Exception e) {
		    throw new BillParseException(e.getMessage());
		}
		logger.info("江南银行聚合支付解析数据行数："+list.size());
		return list;
	}
	
	//机构名称|$|商户名称|$|商户号|$|交易本金|$|交易手续费|$|交易日期|$|交易时间|$|交易类型|$|支付路径|$|流水号|$|外部订单号
	protected void parseBill(List<ThirdBill> list, String billData ,String orgCode) throws ParseException {
		// 账单来源
        Set<String> billSourceSet = new HashSet<>();
		//窗口和自助机的判断条件
		String cashierList = ProConfigManager.getValueByPkey(entityManager, ProConstants.WUJIN_CASHIER_LIST);
		String[] rows = billData.split(",");// 获取每一行
		for(int i=1;i<rows.length;i++) {
			String[] rows2 = rows[i].split("\\|\\$\\|");
			//商户号
			String payShopNo=rows2[2];
			//支付金额
			String payAmount=rows2[3];
			//交易时间
			String tradeDatatime = DateUtil.transferStringToDateFormat("yyyy-MM-dd HH:mm:ss","yyyyMMddHHmmss",rows2[4]+rows2[5]);
			//交易类型
			String orderState=rows2[7].equals("消费")?EnumTypeOfInt.TRADE_TYPE_PAY.getValue():EnumTypeOfInt.TRADE_TYPE_REFUND.getValue();
			//支付类型
			String payType = rows2[8].equals("微信")?EnumTypeOfInt.PAY_TYPE_WECHAT.getValue():rows2[8].equals("支付宝")?EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue():EnumTypeOfInt.PAY_TYPE_BANK.getValue();
			//江南银行流水号
			String shopFlowNo=rows2[9];
			//支付流水号
			String payFlowNo =rows2[10];
			//获取流水号规则：首先是否退费（约定规则退费流水号是在原下单流水号之前添加're'）
			if(payFlowNo.contains("re")) {
				payFlowNo=payFlowNo.substring("re".length());
			}
			//判断渠道是窗口和自助机
			String billSource="";
			if(StringUtils.isNotBlank(cashierList)) {
				String[] cashier = cashierList.split(",");
				for(String v:cashier) {
					//如果单号中包含自助机的收费员号,订单就是自助机来的
					if(payFlowNo.contains(v)) {
						billSource=com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_ZZJ.getValue();
					}else {
						billSource=com.yiban.rec.util.EnumTypeOfInt.BILL_SOURCE_CK.getValue();
					}
				}
			}
			ThirdBill tb = new ThirdBill();
			tb.setShopFlowNo(shopFlowNo);
			tb.setPayFlowNo(payFlowNo);
			tb.setPayShopNo(payShopNo);
			tb.setTradeDatatime(DateUtil.transferStringToDateFormat(tradeDatatime));
			tb.setOrgNo(orgCode);
	        tb.setPayType(payType);
	        tb.setRecPayType(EnumTypeOfInt.PAY_TYPE_AGGREGATE.getValue());
	        tb.setOrderState(orderState);
	        tb.setPayAmount(new BigDecimal(payAmount));
	        tb.setBillSource(billSource);
	        billSourceSet.add(tb.getBillSource());
			list.add(tb);
		}
		super.setBillSource(billSourceSet);
	}
	
	/**
	 * 清除已有的账单数据
	 *
	 * @param orgCode
	 * @param date
	 */
	@Override
	protected void clearBill(String orgCode, String date, EntityManager entityManager, String payType) {
		Map<String, String> map = new HashMap<>();
		map.put("tableName" , "t_thrid_bill");
		//get parameters
		String tableName = map.get("tableName");
		if(StringUtils.isNotBlank(tableName)) {
			String sDate = date + " 00:00:00";
			String eDate = date + " 23:59:59";
			String payTypeSql = getPayTypeSql(payType,tableName);
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE FROM " + tableName);
			sb.append(" WHERE ");
			sb.append(payTypeSql);
			sb.append(" Trade_datatime >= '"+ sDate +"'");
			sb.append(" AND Trade_datatime <= '"+ eDate +"'");
			if(null == billSource || billSource.isEmpty()) {
				sb.append(" AND bill_source = '" + EnumTypeOfInt.BILL_SOURCE_SELF.getValue() + "'");
			}else {
				sb.append(getBillSourceSql(billSource));
			}
			String sql = sb.toString();
			logger.info("clearBill sql = " + sql);
			// get session
			Session session = entityManager.unwrap(org.hibernate.Session.class);
			SQLQuery query = session.createSQLQuery(sql);
			int count = query.executeUpdate();
			logger.info("clearBill count = " + count);
		}
	}

}
