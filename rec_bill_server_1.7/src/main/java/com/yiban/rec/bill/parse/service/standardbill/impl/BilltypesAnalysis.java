package com.yiban.rec.bill.parse.service.standardbill.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.rec.bill.parse.service.getfilefunction.AbstractNewEmailBilParser;
import com.yiban.rec.bill.parse.service.standardbill.AbstractBillParser;
import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.ExcelUtil;
import com.yiban.rec.bill.parse.vo.EmailConfig;
import com.yiban.rec.domain.ThirdBill;

/**
 * 多类型解析
 * @author Administrator
 *
 */
public class BilltypesAnalysis extends AbstractBillParser<ThirdBill> {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//创建公共解析配置实体
	
	
	/**
	 * 构造器
	 */
	public BilltypesAnalysis(){
		//获取解析配置数据，赋值公共实体
		getConfig();
	}

	/**
	 * 获取解析配置数据
	 * @return
	 */
	public String getConfig() {
		return null;
	}
	
	/**
	 * 获取需要解析的file
	 * @return
	 */
	public File getFile() {
		return null;
	}

	/**
	 * 主方法块
	 */
	@Override
	protected List<ThirdBill> doParse(String orgCode, String date) throws BillParseException {
		//得到需要的file
		//解析file
		//返回
		return null;
	}
	
	/**
	 * @param json  映射的对象
	 * @param file
	 */
	private static void analysisFile(JSONObject json,File file) {
		//配置中字段后需要添加到数据库字段里面
		//缴费字段
		String pay="消费";
		//支付类型0249：微信，0349：支付宝，0449：银行
		String payType="0249";
		
		
		
		
		
		//解析统一返回的list集合
		List<Map<String, String>> listMap=new ArrayList<>();
		List<ThirdBill> billList=new ArrayList<>();
		String fileName=file.getName();
		String filePath = file.getPath();
		fileName = fileName.toLowerCase();
		//分配解析的工具类
		if(fileName.endsWith(".txt")){
			
		}else if(fileName.endsWith(".pdf")){
			
		}else if(fileName.endsWith(".csv")) {
			
		}else if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
			//解析excel
			ExcelUtil excel = new ExcelUtil();
			listMap = excel.analysis(filePath, 2, 2, "POS编号：");
		}
		//
		Set<String> list = json.keySet();
		//循环解析的集合
		for(Map<String, String> v: listMap) {
			ThirdBill vo =new ThirdBill();
			JSONObject jsonVo=new JSONObject();
			for(String z:list) {
				//需要转义的映射体的值
				String val = v.get(json.getString(z));
				//转义订单状态
				if(z.equals("orderState")&&StringUtils.isNotBlank(val)) {//退费
					if(!val.equals(pay)) {
						val=EnumTypeOfInt.REFUND_CODE.getValue();
					}else {
						val=EnumTypeOfInt.PAY_CODE.getValue();
					}
					
				}
				jsonVo.put(z, val);
			}
			//将json转为实体
			vo = jsonVo.toJavaObject(ThirdBill.class);
			//支付类型
			vo.setPayType(payType);
			vo.setRecPayType(payType);
			billList.add(vo);
		}
	}
	
	public static void main(String[] args) {
		JSONObject json=new JSONObject();
		json.put("payType", "0");
		json.put("payFlowNo", "20");
		json.put("orderState", "6");
		AbstractNewEmailBilParser email = new AbstractNewEmailBilParser(new EmailConfig("pop3.163.com", 110, "pop3", "xwj130066@163.com", "xwjtms130066", "xwj130066@163.com", "110",true,null));
		//File file = email.downloadAttachment("2019-05-09");
		analysisFile(json,new File("D:\\billfile\\xwj130066@163.com\\2019\\05\\09\\test.xlsx"));
	}
}
