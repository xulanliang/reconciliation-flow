package com.yiban.rec.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoapUiUtil {
	
	@SuppressWarnings("serial")
	private static HashMap<String, String> stringweek=new HashMap<String, String>(){{
		put("一", "1");
		put("二", "2");
		put("三", "3");
		put("四", "4");
		put("五", "5");
		put("六", "6");
		put("日", "7");
		put("七", "7");
	}};

	//外联平台请求
	public static String getSoapUiHead(String serviceName,String content,String name,String token) {
		String soapui="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server.soap.himap.business.com/\">\r\n" + 
				"   <soapenv:Header/>\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <ser:callService>\r\n" + 
				"         <arg1>%s</arg1>\r\n" + 
				"         <arg2><![CDATA[%s]]></arg2>\r\n" + 
				"         <arg3>%s</arg3>\r\n" + 
				"         <arg4>%s</arg4>\r\n" + 
				"      </ser:callService>\r\n" + 
				"   </soapenv:Body>\r\n" + 
				"</soapenv:Envelope>";
		return String.format(soapui, serviceName,content,name,token);
	}
	
	
	/**
	 * 截取外联平台soapui返回的xml，其他soap的xml不支持
	 */
	public static String analysisSoapXml(String xml){
		xml = xml.replaceAll("&lt;", "<");
		xml = xml.replaceAll("&gt;", ">");
		String str1=xml.substring(0, xml.indexOf("<root>"));
		String str2=xml.substring(str1.length(), xml.length());
		String str3 = str2.substring(0, str2.indexOf("</root>")+"</root>".length());
		return str3;
	}
	
	public static String transformation(String str){
		String regEx="[^一二三四五六七日]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String week=m.replaceAll("").trim();
		if(stringweek.containsKey(week)) {
			return stringweek.get(week);
		}
		return str;
	}
	
	 /**
     * 获取午别
     *
     * @param timeStr 08:18:00
     * @return
     */
	public static String getTimeInterval(String timeStr) {
        String[] timeArr = timeStr.split(":");
        Integer timeInterval = Integer.valueOf(timeArr[0]);
        return timeInterval < 12 ? "1" : "2";
    }
	
}
