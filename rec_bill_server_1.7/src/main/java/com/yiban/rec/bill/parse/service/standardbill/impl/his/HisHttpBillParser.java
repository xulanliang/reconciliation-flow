package com.yiban.rec.bill.parse.service.standardbill.impl.his;


import java.util.List;

import org.apache.log4j.Logger;

import com.yiban.rec.bill.parse.service.getfilefunction.AbstractHisBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.HttpClientUtil;

/**
 * his接口账单解析器
 */
public abstract class HisHttpBillParser<T> extends AbstractHisBillParser<T> {
	private static Logger logger = Logger.getLogger(HisHttpBillParser.class);
	
	@Override
	protected List<T> getHisList(String startTime, String endTime,String orgCode) throws BillParseException {
		
		//获取参数
		String url = getUrl(startTime, endTime,orgCode);
		logger.info("请求his的url:" + url);
		String response ="";
		try {
			// 调用his接口，获取返回值
			response = HttpClientUtil.doGet(url, null);
			/*logger.info("his返回的结果:" + response);*/

		} catch (Exception e) {
			throw new BillParseException(e.getMessage());
		}
		// 将返回值保存
		return saveData(response,orgCode);
		
	}
	
	public String getUrl(String startDate, String endDate,String orgCode){
	    // 暂时交给子类实现
		return "";
	}
	
	public abstract List<T> saveData(String response,String orgCode);

}
