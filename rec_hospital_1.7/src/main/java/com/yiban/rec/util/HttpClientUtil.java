package com.yiban.rec.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


/**
 * @author 李韩刚
 */
public class HttpClientUtil extends HttpClientBase {

	private static Logger logger = Logger.getLogger(HttpClientBase.class);
	
	private static final String ENCODING = "utf-8";
	
	private static HttpContext localContext = new BasicHttpContext();
	private static HttpClientContext context = HttpClientContext.adapt(localContext);
	/**
	 * 
	 * Content-Type: application/x-www-form-urlencoded; charset=UTF-8
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            头部信息
	 * @param bodyParamMap
	 *            发送参数
	 * @return
	 * @throws Exception
	 */
	public static String postMap(String url, Map<String, String> headParamMap, Map<String, String> bodyParamMap)
			throws Exception {
		CloseableHttpClient httpClient = buildHttpClient();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			buildHeadInfo(httpPost, headParamMap);
			HttpEntity httpEntity = buildHttpEntity(bodyParamMap);
			httpPost.setEntity(httpEntity);

			httpResponse = execute(httpClient, httpPost);
			String responseContent = parseToString(httpResponse);
			return responseContent;
		} catch (Exception e) {
			throw e;
		} finally {
			close(httpResponse, httpClient);
		}
	}

	/**
	 * Content-Type: application/json; charset=UTF-8
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            头部信息
	 * @param bodyString
	 *            发送参数
	 * @return
	 * @throws Exception
	 */
	public static String postJson(String url, Map<String, String> headParamMap, String bodyString) throws Exception {
		return postBody(url, headParamMap, HttpContentTypes.application_json, bodyString);
	}

	/**
	 * Content-Type: application/xml; charset=UTF-8
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            头部信息
	 * @param bodyString
	 *            发送参数
	 * @return
	 * @throws Exception
	 */
	public static String postXml(String url, Map<String, String> headParamMap, String bodyString) throws Exception {
		return postBody(url, headParamMap, HttpContentTypes.application_xml, bodyString);
	}

	/**
	 * Content-Type: HttpContentTypes
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            头部信息
	 * @param contentTypes
	 *            数据类型
	 * @param bodyString
	 *            发送数据
	 * @return
	 * @throws Exception
	 */
	public static String postBody(String url, Map<String, String> headParamMap, HttpContentTypes contentTypes,
			String bodyString) throws Exception {
		CloseableHttpClient httpClient = buildHttpClient();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			buildHeadInfo(httpPost, headParamMap);
			HttpEntity httpEntity = buildHttpEntity(contentTypes, bodyString);
			httpPost.setEntity(httpEntity);

			httpResponse = execute(httpClient, httpPost);
			String responseContent = parseToString(httpResponse);
			return responseContent;
		} catch (Exception e) {
			throw e;
		} finally {
			close(httpResponse, httpClient);
		}
	}

	/**
	 * GET 方式调用
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            请求头
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, Map<String, String> headParamMap) throws Exception {
		logger.debug("\n请求 servlet端 url地址为:" + url);
		CloseableHttpClient httpClient = buildHttpClient();
		CloseableHttpResponse httpResponse = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			buildHeadInfo(httpGet, headParamMap);
			
			httpResponse = httpClient.execute(httpGet,context);
			
//			httpResponse = execute(httpClient, httpGet);
			String responseContent = parseToString(httpResponse);
			return responseContent;
		} catch (Exception e) {
			logger.error("\n请求 servlet端 url地址为:" + url);
			throw e;
		} finally {
			close(httpResponse, httpClient);
		}
	}

	/**
	 * GET 方式调用
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            请求头
	 * @param paramMap
	 *            请参数
	 * @param charset
	 *            url 的参数编码
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, Map<String, String> headParamMap, Map<String, String> paramMap, String charset) throws Exception {
		if (paramMap == null || paramMap.size() <= 0) {
			return get(url, headParamMap);
		}

		StringBuffer sf = new StringBuffer(url);
		if (url.indexOf("?") < 0) {
			sf.append("?");
		}

		if (!url.endsWith("?") && !url.endsWith("&")) {
			sf.append("&");
		}

		for (String key : paramMap.keySet()) {
			sf.append(key).append("=").append(URLEncoder.encode(paramMap.get(key), charset));
			sf.append("&");
		}

		String zurl = sf.toString();
		if (zurl.endsWith("&")) {
			zurl = zurl.substring(0, zurl.length() - 1);
		}

		return get(zurl, headParamMap);
	}

	/**
	 * GET 方式调用
	 * 
	 * @param url
	 *            调用地址
	 * @param headParamMap
	 *            请求头
	 * @param paramMap
	 *            请参数 , 参数编码默认UTF-8
	 * @return
	 * @throws Exception
	 */
	public static String get(String url, Map<String, String> headParamMap, Map<String, String> paramMap) throws Exception {
		return get(url, headParamMap, paramMap, "UTF-8");
	}

	public static final String converMapToString(final Map<String, String> bodyMap) {
		if (bodyMap == null || bodyMap.isEmpty()) {
			return null;
		}
		StringBuilder bodySb = new StringBuilder(300);
		if (bodyMap != null && !bodyMap.isEmpty()) {
			Iterator<Map.Entry<String, String>> entryIte = bodyMap.entrySet().iterator();
			while (entryIte.hasNext()) {
				if (bodySb.length() > 0) {
					bodySb.append("&");
				}
				Map.Entry<String, String> en = entryIte.next();
				bodySb.append(en.getKey());
				bodySb.append("=");
				bodySb.append(en.getValue());
			}
		}
		return bodySb.toString();
	}
	
	public static String doPostWebservice(String url, String xml) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			StringEntity data = new StringEntity(xml, Charset.forName("UTF-8"));
			httpPost.setEntity(data);
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), ENCODING);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}
	
	  public  boolean doURl(String url, String charset){
			HttpClient httpClient = new HttpClient();
			httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(45000);
			GetMethod getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 45000);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			try {
				int statusCode = httpClient.executeMethod(getMethod);
				if(statusCode==200){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				getMethod.releaseConnection();
			}
			return true;
		}
	  
}
