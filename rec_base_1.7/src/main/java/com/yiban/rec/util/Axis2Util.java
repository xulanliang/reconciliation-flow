package com.yiban.rec.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * 请求WebService
 * @Author WY
 * @Date 2018年11月8日
 */
public class Axis2Util {
    private static Logger logger = Logger.getLogger(WebServiceClientUtil.class);
    
    public static void main(String[] args) {
        String url = "http://218.18.109.234:30001/services/com.yxw.interfaces.service.YxwOpenService";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("orderMode", "2");
        paramMap.put("appId", "wx95ac8639496452b8");
        paramMap.put("tradeMode", "1");
        paramMap.put("startTime", "2019-01-24 00:00:00");
        paramMap.put("endTime", "2019-01-25 00:00:00");
        String nameSpace = "http://service.interfaces.yxw.com/";
        String ser = "openService";
        String methodCode = "billQuery";
        String responseType = "0";
        String result = invoke(url, paramMap, nameSpace, ser, methodCode, responseType);
        System.err.println(result);
    }
    
    /**
     * 调用webService接口
     * @param url 请求地址
     * @param params Map类型请求参数
     * @param nameSpace 命名空间
     * @param ser 命名空间方法
     * @param methodCode 请求方法
     * @param responseType 响应类型
     * @return
     * String
     */
    public static String invoke(String url, Map<String, String> params, String nameSpace, String ser, 
            String methodCode, String responseType) {
        String xmlParams = mapToXMLWithoutHeader(params);
        return invoke(url, xmlParams, nameSpace, ser, methodCode, responseType);
    }
    
    /**
     * 调用webService接口
     * @param url 请求地址
     * @param params XML字符类型请求参数
     * @param nameSpace 命名空间
     * @param ser 命名空间方法
     * @param methodCode 请求方法
     * @param responseType 响应类型
     * @return
     * String
     */
    public static String invoke(String url, String xmlParams, String nameSpace, String ser, 
            String methodCode, String responseType) {
        try {
            // 使用RPC方式调用WebService
            RPCServiceClient serviceClient = new RPCServiceClient();
            EndpointReference targetEPR = new EndpointReference(url);
            Options options = serviceClient.getOptions();
            options.setTo(targetEPR);
            // 创建服务名称
            QName qname = new QName(nameSpace, ser);
            // 调用方法一 传递参数，调用服务，获取服务返回结果集
            Class<?>[] returnType = new Class[] {Response.class};
            // 请求入参
            Object[] parameters = new Object[] {new Request(methodCode, xmlParams, responseType)};
            // 响应参数
            Object[] objs = serviceClient.invokeBlocking(qname, parameters, returnType);
            StringBuffer sb = new StringBuffer();
            for (Object o : objs) {
                Response r = (Response) o;
                sb.append(r.toString());
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }
    
    /**
     * 将Map转换为xml
     *
     * @param map
     * @return
     */
    private static String mapToXMLWithoutHeader(Map<String, String> map) {
        StringBuffer buffer = new StringBuffer("");
        Object[] keys = map.keySet().toArray();
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].toString();
            Object value = map.get(key);
            if (!StringUtils.isEmpty(value.toString())) {
                buffer.append("<" + key + ">" + value + "</" + key + ">");
            }
        }
        buffer.append("");
        return buffer.toString();
    }
    
    /**
     * 请求对象
     */
    public static class Request {
        private String methodCode;
        private String params;
        private String responseType;
        
        public String getMethodCode() {
            return methodCode;
        }

        public void setMethodCode(String methodCode) {
            this.methodCode = methodCode;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public String getResponseType() {
            return responseType;
        }

        public void setResponseType(String responseType) {
            this.responseType = responseType;
        }

        public Request() {
            super();
        }

        public Request(String methodCode, String params, String responseType) {
            this.methodCode = methodCode;
            this.params = params;
            this.responseType = responseType;
        }
    }
    
    /**
     * 响应对象
     */
    public static class Response {
        private String result;
        private String resultCode;
        private String resultMessage;
        public String getResult() {
            return result;
        }
        public void setResult(String result) {
            this.result = result;
        }
        public String getResultCode() {
            return resultCode;
        }
        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }
        public String getResultMessage() {
            return resultMessage;
        }
        public void setResultMessage(String resultMessage) {
            this.resultMessage = resultMessage;
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("<result>");
            sb.append(result);
            sb.append("</result>");
            sb.append("<resultCode>");
            sb.append(resultCode);
            sb.append("</resultCode>");
            sb.append("<resultMessage>");
            sb.append(resultMessage);
            sb.append("</resultMessage>");
            return sb.toString();
        }
    } 
}
