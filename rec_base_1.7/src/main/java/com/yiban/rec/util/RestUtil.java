package com.yiban.rec.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.yiban.framework.core.service.ServiceException;


/**
*<p>文件名称:RestUtil.java
*<p>
*<p>文件描述:提供restful的调用方式，支持get，post方式
*<p>版权所有:深圳市依伴数字科技有限公司版权所有(C)2017</p>
*<p>内容摘要:简要描述本文件的内容，包括主要模块、函数及能的说明
</p>
*<p>其他说明:其它内容的说明
</p>
*<p>完成日期:2017年7月24日下午2:50:04</p>
*<p>
*@author fangzuxing
 */
public class RestUtil {
	
	// 设置超时时间
	private static int CONNECTION_TIME_OUT = 180000;
	
	public String doGet(String url) throws Exception {
		return doGet(url, "utf-8");
	}
	
	public String doGet(String url, String charset) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, CONNECTION_TIME_OUT);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		StringBuffer response = new StringBuffer();
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				throw new ServiceException("请求返回状态码("+statusCode+")异常。");
			}
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getMethod.getResponseBodyAsStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
		} catch (Exception e) {
			throw e;
		} finally {
			getMethod.releaseConnection();
		}
		return response.toString();
	}
	
	
	public String doPost(String url, Object params) throws Exception {
		return doPost(url, params, "utf-8");
	}
	
	/**
	 * 通过POST的方式请求REST数据
	* @param url		请求地址
	* @param params		请求参数
	* 		当类型为String时将参数作为POST的BODY数据提交
	* 		当类型为Map时将MAP中的键值作为POST的键值提交
	* @param charset	编码方式
	* @return
	 */
	public String doPost(String url, Object params, String charset) throws Exception {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, CONNECTION_TIME_OUT);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		
		// 设置Http Post数据
		if (params != null) {
			if(params instanceof String) {
				method.setRequestBody((String)params);
				
			} else if (params instanceof Map) {
				
				for (Map.Entry<String, Object> entry : ((Map<String, Object>)params).entrySet()) {
					method.addParameter(entry.getKey(),
							entry.getValue() == null ? "" : entry.getValue()+"");
				}
				
			} else {
				throw new IllegalArgumentException("调用REST接口参数(params)设置错误");
			}
		}
		
		StringBuffer response = new StringBuffer();
		try {
			int statusCode = httpClient.executeMethod(method); // method.getStatusCode()
			if (statusCode != HttpStatus.SC_OK) {
				throw new ServiceException("请求返回状态码("+statusCode+")异常。");
			}
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
		}
		
		return response.toString();
	}
	
	public String doPostJson(String url, Object params, String charset) throws Exception {
		HttpClient httpClient = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, CONNECTION_TIME_OUT);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		
		method.setRequestHeader("Content-Type",
				"application/json");
		
		// 设置Http Post数据
		if (params != null) {
			if(params instanceof String) {
				method.setRequestBody((String)params);
				
			} else if (params instanceof Map) {
				
				for (Map.Entry<String, Object> entry : ((Map<String, Object>)params).entrySet()) {
					method.addParameter(entry.getKey(),
							entry.getValue() == null ? "" : entry.getValue()+"");
				}
				
			} else {
				throw new IllegalArgumentException("调用REST接口参数(params)设置错误");
			}
		}
		
		StringBuffer response = new StringBuffer();
		try {
			int statusCode = httpClient.executeMethod(method); // method.getStatusCode()
			if (statusCode != HttpStatus.SC_OK) {
				throw new ServiceException("请求返回状态码("+statusCode+")异常。");
			}
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(method.getResponseBodyAsStream(), charset));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			method.releaseConnection();
		}
		
		return response.toString();
	}
	
	/**
	 * 通过POST的方式请求REST数据,返回二进制数组
	* @param url		请求地址
	* @param params		请求参数
	* @param charset	编码方式
	* @return
	 */
	public byte[] doPost(String url, String charset, Object params) throws Exception {
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIME_OUT);
		
		PostMethod method = new PostMethod(url);
		method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, CONNECTION_TIME_OUT);
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
		
		// 设置Http Post数据
		if (params != null) {
			if(params instanceof String) {
				
				method.setRequestBody((String)params);
				
			} else if (params instanceof Map) {
				
				for (Map.Entry<String, String> entry : ((Map<String, String>)params).entrySet()) {
					method.addParameter(entry.getKey(),
							entry.getValue() == null ? "" : entry.getValue());
				}
				
			} else {
				throw new IllegalArgumentException("调用REST接口参数(params)设置错误");
			}
		}
		
		ByteArrayOutputStream baos = null;
		InputStream in =null;
		try {
			int statusCode = httpClient.executeMethod(method); // method.getStatusCode()
			if (statusCode != HttpStatus.SC_OK) {
				throw new ServiceException("请求返回状态码("+statusCode+")异常。");
			}
			in = method.getResponseBodyAsStream();
			baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int count = -1;
			while((count =  in.read(b, 0, b.length))!=-1){
				baos.write(b,0,count);
			}
			b=null;
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if(null!=baos){
				baos.close();
			}
			if(null!=in){
				in.close();
			}
			method.releaseConnection();
		}
	}
}
