package com.yiban.rec.util;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ParameterMode;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;

public class WebServiceClientUtilExtend {

	private static Logger logger = Logger.getLogger(WebServiceClientUtilExtend.class);

	public static String himapwsCallService(String endpoint, String orgCode, String method, String reqXml, String userName,
			String password) {
		try {
			// String endpoint = "http://192.168.27.167:8080/himapws/CallServicePort?wsdl";
			// 直接引用远程的wsdl文件
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName("http://server.soap.himap.business.com/", "callService"));// WSDL里面描述的接口名称
			call.addParameter("arg0", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg1", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg2", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg3", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.addParameter("arg4", XMLType.XSD_DATE, ParameterMode.IN);// 接口的参数
			call.setReturnType(XMLType.XSD_STRING);// 设置返回类型
			// String result = (String) call.invoke(new Object[] { orgCode,
			// "PROC_GETCURRENTDATE", "<request></request>", "admin",
			// "fr4t3t1y6s6s1e0502c4d5" });
			// 给方法传递参数，并且调用方法
			String result = (String) call.invoke(new Object[] { orgCode, method, reqXml, userName, password });
			return result;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return null;
	}

	/**
	 *  将Map转换为XML格式的字符串
	 *
	 * @param data Map类型数据
	 * @return XML格式的字符串
	 * @throws Exception
	 */
	public static String mapToXml(Map<String, String> data) throws Exception {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element root = document.createElement("Req");
		document.appendChild(root);
		for (String key : data.keySet()) {
			String value = data.get(key);
			if (value == null) {
				value = "";
			}
			value = value.trim();
			Element filed = document.createElement(key);
			filed.appendChild(document.createTextNode(value));
			root.appendChild(filed);
		}
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		String output = writer.getBuffer().toString(); // .replaceAll("\n|\r", "");
		try {
			writer.close();
		} catch (Exception ex) {
		}
		return output;
	}
	
	/*public static void main(String[] args) {
		Map<String, String> data = new HashMap<>();
		data.put("tradeDate", "111");
		data.put("payMode", "111");
		
		String reqXml = null;
		try {
			reqXml = WebServiceClientUtil.mapToXml(data);
		} catch (Exception e) {
			return;
		}
		reqXml = reqXml.substring(reqXml.indexOf(">")+1, reqXml.length());
		System.out.println(reqXml);
	}*/

}
