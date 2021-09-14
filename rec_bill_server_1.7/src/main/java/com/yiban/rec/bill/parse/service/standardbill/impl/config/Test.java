package com.yiban.rec.bill.parse.service.standardbill.impl.config;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.rec.bill.parse.util.BeanUtil;
import com.yiban.rec.bill.parse.util.XmlUtil;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.WebServiceFieldMappingEntity;

public class Test {
	static String xml = "\r\n" + "<soap:Envelope\r\n"
			+ "    xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\n"
			+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
			+ "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n" + "    <soap:Body>\r\n"
			+ "        <Wh_YYPTResponse\r\n" + "            xmlns=\"http://tempuri.org/\">\r\n"
			+ "            <Wh_YYPTResult>\r\n" + "                <![CDATA[\r\n"
			+ "						<XMLTABLE>\r\n" + "							<XMLREC>\r\n"
			+ "								<REFRESULT>0</REFRESULT>\r\n"
			+ "								<ResultValues>\r\n" + "									<XMLTABLE>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T17:39:31.380</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>0.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000278201902286017508449</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>挂号</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>何东武  </patName>\r\n"
			+ "											<patCode>000020190228000573</patCode>\r\n"
			+ "											<invoiceNo>1902282319</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:36:28.833</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>18.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000278201902287304252199</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>挂号</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>何东武  </patName>\r\n"
			+ "											<patCode>000020190228000573</patCode>\r\n"
			+ "											<invoiceNo>1902282330</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:41:54</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>9.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000267201902286215799118</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>挂号</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>测试    </patName>\r\n"
			+ "											<invoiceNo>0000001150</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:43:22.017</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>8.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000278201902287612475466</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>缴费</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>测试    </patName>\r\n"
			+ "											<invoiceNo>0000001152</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:08:19</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>0.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000287201902287752124684</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>缴费</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>何东武  </patName>\r\n"
			+ "											<patCode>000020190228000573</patCode>\r\n"
			+ "											<invoiceNo>1902282328</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:15:32</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>0.00</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000273201902288417550099</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>缴费</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>何东武  </patName>\r\n"
			+ "											<patCode>000020190228000573</patCode>\r\n"
			+ "											<invoiceNo>1902282329</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n"
			+ "										<XMLREC>\r\n"
			+ "											<transDate>2019-02-28T18:37:14</transDate>\r\n"
			+ "											<payType>微信</payType>\r\n"
			+ "											<payAmount>250.38</payAmount>\r\n"
			+ "											<agtCode>53871</agtCode>\r\n"
			+ "											<agtOrdSerialNo>4200000267201902281569233412</agtOrdSerialNo>\r\n"
			+ "											<terminalName />\r\n"
			+ "											<terminalIp />\r\n"
			+ "											<terminalNo>001</terminalNo>\r\n"
			+ "											<billsType>缴费</billsType>\r\n"
			+ "											<patType>门诊</patType>\r\n"
			+ "											<patName>何东武  </patName>\r\n"
			+ "											<patCode>000020190228000573</patCode>\r\n"
			+ "											<invoiceNo>1902282331</invoiceNo>\r\n"
			+ "										</XMLREC>\r\n" + "									</XMLTABLE>\r\n"
			+ "								</ResultValues>\r\n"
			+ "								<ERRORINFO>缴费明细信息</ERRORINFO>\r\n"
			+ "							</XMLREC>\r\n" + "						</XMLTABLE>\r\n"
			+ "						]]>\r\n" + "            </Wh_YYPTResult>\r\n" + "        </Wh_YYPTResponse>\r\n"
			+ "    </soap:Body>\r\n" + "</soap:Envelope>";

	static String xmlJson = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\r\n"
			+ "   <soap:Body>\r\n" + "      <GetPayInfoResponse>\r\n" + "         <GetPayInfoResult>\r\n"
			+ "		 {\"resultCode\":\"SUCCESS\",\"resultMsg\":\"\",\"orderItems\":		 [{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"064200648692\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:00:10\",\"payType\":\"0049\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291186\",\"patientName\":\"董鸿忠\",\"cashier\":\"2173\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"064200648693\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:07:28\",\"payType\":\"0049\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291175\",\"patientName\":\"王芳\",\"cashier\":\"2173\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"082600063308\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"4.50\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:07:01\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291188\",\"patientName\":\"范永忠\",\"cashier\":\"0702\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"227600011416\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"54.06\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:05:19\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291185\",\"patientName\":\"杨明方\",\"cashier\":\"4316\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"090600114298\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"3.00\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:04:14\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291180\",\"patientName\":\"李晓蝶\",\"cashier\":\"1113\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"103000028748\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"28.00\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:09:09\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012291180\",\"patientName\":\"李晓蝶\",\"cashier\":\"3048\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"103000028747\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"12.43\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:01:33\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1012075383\",\"patientName\":\"郭子轩\",\"cashier\":\"3048\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"150400045977\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"70.00\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:06:22\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"mz\",\"billSource\":\"\",\"patientCardNo\":\"1011998616\",\"patientName\":\"申俊梅\",\"cashier\":\"4135\",\"goodInfo\":\"\"},{\"outTradeNo\":\"\",\"tsnOrderNo\":\"\",\"hisOrderNo\":\"51010193\",\"orderStateRemark\":\"\",\"orderNo\":\"\",\"orderState\":\"\",\"ybSerialNo\":\"\",\"ybBillNo\":\"\",\"payTotalAmount\":\"3000.00\",\"payAmount\":\"0.00\",\"ybPayAmount\":\"0.00\",\"settlementType\":\"0031\",\"tradeDateTime\":\"2018/5/5 0:02:12\",\"payType\":\"9949\",\"payBusinessType\":\"0051\",\"patType\":\"zy\",\"billSource\":\"\",\"patientCardNo\":\"\",\"patientName\":\"\",\"cashier\":\"\",\"goodInfo\":\"\"}]\r\n"
			+ "		 }\r\n" + "		 \r\n" + "		 </GetPayInfoResult>\r\n" + "      </GetPayInfoResponse>\r\n"
			+ "   </soap:Body>\r\n" + "</soap:Envelope>";

	static String json = "{\r\n" + "		    \"result_code\": 0,\r\n" + "		    \"result_data\": [\r\n"
			+ "		        {\r\n" + "		            \"billSource\": \"\",\r\n"
			+ "		            \"cashier\": \"1209\",\r\n" + "		            \"goodInfo\": \"\",\r\n"
			+ "		            \"hisOrderNo\": \"52002206\",\r\n" + "		            \"orderNo\": \"\",\r\n"
			+ "		            \"orderState\": \"0\",\r\n" + "		            \"orderStateRemark\": \"\",\r\n"
			+ "		            \"outTradeNo\": \"\",\r\n" + "		            \"patType\": \"门诊\",\r\n"
			+ "		            \"patientCardNo\": \"\",\r\n" + "		            \"patientName\": \"张玫福\",\r\n"
			+ "		            \"payAmount\": \"\",\r\n" + "		            \"payBusinessType\": \"0051\",\r\n"
			+ "		            \"payTotalAmount\": \"\",\r\n" + "		            \"payType\": \"0049\",\r\n"
			+ "		            \"settlementType\": \"自费\",\r\n"
			+ "		            \"tradeDateTime\": \"2008/2/27 12:21:50\",\r\n"
			+ "		            \"tsnOrderNo\": \"\",\r\n" + "		            \"ybBillNo\": \"\",\r\n"
			+ "		            \"ybPayAmount\": \"\",\r\n" + "		            \"ybSerialNo\": \"\"\r\n"
			+ "		        }\r\n" + "		    ],\r\n" + "		    \"result_msg\": \"查询支付记录成功！\"\r\n" + "		}";

	static String xml1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + "<response>\r\n" + "  <result>\r\n"
			+ "    <orderRecord>\r\n" + "      <deptName>门诊西医(普通)</deptName>\r\n"
			+ "      <refundTotalFee>0</refundTotalFee>\r\n" + "      <tradeType>1</tradeType>\r\n"
			+ "      <hisOrderNo>3319881</hisOrderNo>\r\n" + "      <patIdType>1</patIdType>\r\n"
			+ "      <payTotalFee>600</payTotalFee>\r\n" + "      <refundTime>2019-04-01 21:17:39</refundTime>\r\n"
			+ "      <tradeCode>1277063101</tradeCode>\r\n"
			+ "      <refundOrderNo>RefundMZ_7D_20120190401211739</refundOrderNo>\r\n"
			+ "      <bookDate>2019-04-03 00:00:00</bookDate>\r\n" + "      <agtRefundTraceNo/>\r\n"
			+ "      <patName>冯少鹏</patName>\r\n" + "      <tradeMode>3</tradeMode>\r\n"
			+ "      <patCardType>8</patCardType>\r\n" + "      <doctorName>丁度军</doctorName>\r\n"
			+ "      <hisRefundOrderNo>3319881</hisRefundOrderNo>\r\n" + "      <orderMode>1</orderMode>\r\n"
			+ "      <doctorCode>0266</doctorCode>\r\n"
			+ "      <agtRefundOrderNo>e817f1f5-bc5c-4d59-9b6c-e1b7505a2f22</agtRefundOrderNo>\r\n"
			+ "      <tradeTime>2019-04-01 18:57:33</tradeTime>\r\n"
			+ "      <agtOrderNo>10388441600007541161979122223153</agtOrderNo>\r\n"
			+ "      <patIdNo>420626199308106019</patIdNo>\r\n" + "      <patCardNo>74000003</patCardNo>\r\n"
			+ "      <branchCode/>\r\n" + "      <orderNo>MZ_7D_20120190401185539</orderNo>\r\n"
			+ "      <branchName/>\r\n" + "      <agtTraceNo/>\r\n" + "      <deptCode>103</deptCode>\r\n"
			+ "    </orderRecord>\r\n" + "    <orderRecord>\r\n" + "      <deptName>门诊西医(普通)</deptName>\r\n"
			+ "      <refundTotalFee>600</refundTotalFee>\r\n" + "      <tradeType>2</tradeType>\r\n"
			+ "      <hisOrderNo>3319881</hisOrderNo>\r\n" + "      <patIdType>1</patIdType>\r\n"
			+ "      <payTotalFee>0</payTotalFee>\r\n" + "      <refundTime>2019-04-01 21:17:39</refundTime>\r\n"
			+ "      <tradeCode>1277063101</tradeCode>\r\n"
			+ "      <refundOrderNo>RefundMZ_7D_20120190401211739</refundOrderNo>\r\n"
			+ "      <bookDate>2019-04-03 00:00:00</bookDate>\r\n" + "      <agtRefundTraceNo/>\r\n"
			+ "      <patName>冯少鹏</patName>\r\n" + "      <tradeMode>3</tradeMode>\r\n"
			+ "      <patCardType>8</patCardType>\r\n" + "      <doctorName>丁度军</doctorName>\r\n"
			+ "      <hisRefundOrderNo>3319881</hisRefundOrderNo>\r\n" + "      <orderMode>1</orderMode>\r\n"
			+ "      <doctorCode>0266</doctorCode>\r\n"
			+ "      <agtRefundOrderNo>e817f1f5-bc5c-4d59-9b6c-e1b7505a2f22</agtRefundOrderNo>\r\n"
			+ "      <tradeTime>2019-04-01 18:57:33</tradeTime>\r\n"
			+ "      <agtOrderNo>10388441600007541161979122223153</agtOrderNo>\r\n"
			+ "      <patIdNo>420626199308106019</patIdNo>\r\n" + "      <patCardNo>74000003</patCardNo>\r\n"
			+ "      <branchCode/>\r\n" + "      <orderNo>MZ_7D_20120190401185539</orderNo>\r\n"
			+ "      <branchName/>\r\n" + "      <agtTraceNo/>\r\n" + "      <deptCode>103</deptCode>\r\n"
			+ "    </orderRecord>\r\n" + "  </result>\r\n" + "  <resultCode>0</resultCode>\r\n" + "  <resultDesc/>\r\n"
			+ "</response>\r\n" + "";

	public static void testXML() {
		String keys = "soap:Envelope,soap:Body,Wh_YYPTResponse,Wh_YYPTResult,XMLTABLE,XMLREC,ResultValues,XMLTABLE,XMLREC";

		System.out.println(XmlUtil.parse2JSONArray(xml, keys));
	}

	public static void testJson() {
		String keys = "soap:Envelope,soap:Body,GetPayInfoResponse,GetPayInfoResult,resultCode,orderItems";

		System.out.println(XmlUtil.parse2JSONArray(xmlJson, keys));
	}

	public static void testJSON1() {
		String keys = "result_data";

		System.out.println(XmlUtil.parse2JSONArray(json, keys));
	}

	public static void xml1() {
		String keys = "response,result,orderRecord";
		System.out.println(XmlUtil.parse2JSONArray(xml1, keys));
	}

	public static ArrayList<HisTransactionFlow> wrapBean() throws Exception {
		String keys = "soap:Envelope,soap:Body,Wh_YYPTResponse,Wh_YYPTResult,XMLTABLE,XMLREC,ResultValues,XMLTABLE,XMLREC";

		ArrayList<HisTransactionFlow> hisList = new ArrayList<>();
		JSONArray jsonArray = XmlUtil.parse2JSONArray(xml, keys);
		if (jsonArray == null || jsonArray.length() == 0) {
			return hisList;
		}

		// 初始化映射关系
		ArrayList<WebServiceFieldMappingEntity> mappings = new ArrayList<>();
		WebServiceFieldMappingEntity mapping = new WebServiceFieldMappingEntity();
		mapping.setDataFieldName("agtOrdSerialNo");
		mapping.setClassFieldName("payFlowNo");
		mappings.add(mapping);

		WebServiceFieldMappingEntity f1 = new WebServiceFieldMappingEntity();
		f1.setDataFieldName("patName");
		f1.setClassFieldName("custName");
		mappings.add(f1);

		WebServiceFieldMappingEntity f2 = new WebServiceFieldMappingEntity();
		f2.setDataFieldName("payType");
		f2.setClassFieldName("payType");
		mappings.add(f2);

		WebServiceFieldMappingEntity f3 = new WebServiceFieldMappingEntity();
		f3.setDataFieldName("transDate");
		f3.setClassFieldName("tradeDatatime");
		mappings.add(f3);

		WebServiceFieldMappingEntity f4 = new WebServiceFieldMappingEntity();
		f4.setDataFieldName("payAmount");
		f4.setClassFieldName("payAmount");
		mappings.add(f4);

		WebServiceFieldMappingEntity f5 = new WebServiceFieldMappingEntity();
		f5.setDataFieldName("orgNo");
		f5.setClassFieldName("orgNo");
		f5.setDefaultValue("150150");
		mappings.add(f5);

		WebServiceFieldMappingEntity f6 = new WebServiceFieldMappingEntity();
		f6.setDataFieldName("billSource");
		f6.setClassFieldName("billSource");
		f6.setDefaultValue("self");
		mappings.add(f6);

		WebServiceFieldMappingEntity f7 = new WebServiceFieldMappingEntity();
		f7.setDataFieldName("patCode");
		f7.setClassFieldName("patCode");
		mappings.add(f7);

		for (int i = 0, len = jsonArray.length(); i < len; i++) {
			JSONObject obj = jsonArray.getJSONObject(i);
			HisTransactionFlow his = new HisTransactionFlow();

			// 遍历映射关系
			for (WebServiceFieldMappingEntity entity : mappings) {

				String dataFieldName = entity.getDataFieldName();
				String classFieldName = entity.getClassFieldName();
				String defaultValue = entity.getDefaultValue();

				String value = !obj.has(dataFieldName) || StringUtil.isNullOrEmpty(obj.get(dataFieldName))
						? defaultValue
						: obj.get(dataFieldName).toString();

				if (StringUtil.isEmpty(value)) {
					continue;
				}

				BeanUtil.setFieldValue(his, classFieldName, value);
			}

			hisList.add(his);
		}

		System.out.println("解析his数量：" + hisList.size());
		System.out.println(new Gson().toJson(hisList));
		return hisList;
	}

	public static void main(String[] args) throws Exception {
//		testJson();
//		testJSON1();
//		xml1();

		wrapBean();
	}
}
