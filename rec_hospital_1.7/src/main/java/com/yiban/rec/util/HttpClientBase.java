package com.yiban.rec.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author 李韩刚
 */
public class HttpClientBase {

	private static Logger logger = Logger.getLogger(HttpClientBase.class);

	public static final String charSet_encode = "UTF-8";
	
	// 连接管理器
    private static PoolingHttpClientConnectionManager pool;

    // 请求配置
    private static RequestConfig requestConfig;
    
    // utf-8字符编码
    public static final String CHARSET_UTF_8 = "UTF-8";
    
    // HTTP内容类型。相当于form表单的形式，提交数据
    public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";
    
    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            // 配置同时支持 HTTP 和 HTPPS
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
            // 初始化连接管理器
            pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            pool.setMaxTotal(200);
            // 设置最大路由
            pool.setDefaultMaxPerRoute(2);
            // 根据默认超时限制初始化requestConfig
            int socketTimeout = 10000;
            int connectTimeout = 10000;
            int connectionRequestTimeout = 10000;
            requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("依赖缺失： ", e);
        } catch (KeyStoreException e) {
            logger.error("证书存贮： ", e);
        } catch (KeyManagementException e) {
            logger.error("证书管理： ", e);
        }
        // 设置请求超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000)
                .setConnectionRequestTimeout(50000).build();
    }
    
	/**
	 * 设置请求头部
	 * 
	 * @param httpRequest
	 * @param headParamMap
	 * @throws Exception
	 */
	public static void buildHeadInfo(HttpRequestBase httpRequest, Map<String, String> headParamMap) throws Exception {
		try {
			if (httpRequest == null) {
				return;
			}
			if (!(headParamMap != null && headParamMap.size() > 0)) {
				return;
			}
			for (String key : headParamMap.keySet()) {
				httpRequest.setHeader(key, headParamMap.get(key));
			}
		} catch (Exception e) {
			logger.error("设置请求头部异常  " + e.getMessage(), e);
			throw new Exception("设置请求头部异常", e);
		}

	}

	/**
	 * 设置请求参数
	 */
	public static HttpEntity buildHttpEntity(HttpContentTypes contentTypes, String bodyString) throws Exception {
		try {
			StringEntity stringEntity = new StringEntity(bodyString);
			stringEntity.setContentEncoding(charSet_encode);
			stringEntity.setContentType(contentTypes.getValue());
			return stringEntity;
		} catch (Exception e) {
			logger.error("设置请求参数异常  " + e.getMessage(), e);
			throw new Exception("设置请求参数异常", e);
		}

	}

	private static List<NameValuePair> buildPrams(Map<String, String> params) throws Exception {
		List<NameValuePair> tl = new ArrayList<NameValuePair>();
		if (params == null || params.size() < 1)
			return null;
		for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, String> param = iterator.next();
			tl.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		}
		return tl;
	}

	/**
	 * 设置请求参数
	 */
	public static HttpEntity buildHttpEntity(Map<String, String> params) throws Exception {
		if (params == null || params.size() == 0) {
			return null;
		}
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(buildPrams(params), charSet_encode);
			return httpEntity;
		} catch (Exception e) {
			logger.error("设置请求参数异常   " + e.getMessage(), e);
			throw new Exception("设置请求参数异常", e);
		}
	}

	/**
	 * 执行调用URL
	 */
	public static CloseableHttpResponse execute(CloseableHttpClient httpClient, HttpRequestBase httpRequest)
			throws Exception {
		try {
			return httpClient.execute(httpRequest);
		} catch (Exception e) {
			logger.error("请求异常  " + e.getMessage(), e);
			throw new Exception("请求异常", e);
		}
	}

	/**
	 * 读取响应
	 * 
	 * @param httpResponse
	 * @return
	 * @throws Exception
	 */
	public static String parseToString(CloseableHttpResponse httpResponse) throws Exception {
		try {
			String responseContent = EntityUtils.toString(httpResponse.getEntity(), charSet_encode);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return responseContent;
			} else {
				logger.info("请求返回消息:" + responseContent);
				return null;
			}
		} catch (Exception e) {
			logger.error("读取响应异常  " + e.getMessage(), e);
			throw new Exception("读取响应异常", e);
		}
	}

	public static CloseableHttpClient buildHttpClient() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		return httpClientBuilder.build();
	}

	/**
	 * 关闭响应和请求
	 * 
	 * @param httpResponse
	 * @param httpClient
	 */
	public static void close(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) {
		try {
			if (httpResponse != null) {
				httpResponse.close();
			}
			if (httpClient != null) {
				httpClient.close();
			}
		} catch (Exception e) {
			logger.error("关闭连接异常  " + e.getMessage(), e);
		}
	}
	
	 /**
     * 发送 post请求
     * @param httpUrl 地址
     * @param params 参数(格式:key1=value1&key2=value2)
     */
    public static String sendHttpPost(String httpUrl, String params) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            // 设置参数
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, Consts.UTF_8);
                stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            logger.error("执行http post 请求["+httpUrl+"]，参数["+params+"]，发生异常：", e);
        }
        return sendHttpPost(httpPost);
    }

    /**
     * 发送 post请求
     * @param params 参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> params) {
        String parem = convertStringParamter(params);
        return sendHttpPost(httpUrl, parem);
    }
    
    /**
     * 发送Post请求
     * @param httpPost
     * @return
     */
    private static String sendHttpPost(HttpPost httpPost) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = getHttpClient();
            // 配置请求信息
            httpPost.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpPost);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 可以获得响应头
            // Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
            // for (Header header : headers) {
            // logger.info(header.getName());
            // }

            // 得到响应类型
            // logger.info(ContentType.getOrDefault(response.getEntity()).getMimeType());

            // 判断响应状态
            if (response.getStatusLine().getStatusCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.error("http post method exception:", e);
        } finally {
            try {
                // 释放资源
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("关闭http client 发生异常：", e);
            }
        }
        return responseContent;
    }
    
    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(pool)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
        return httpClient;
    }
    
    /**
     * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
     * @param params 需要转化的键值对集合
     * @return 字符串
     */
    public static String convertStringParamter(Map<String, String> params) {
        StringBuffer parameterBuffer = new StringBuffer();
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            String key = null;
            String value = null;
            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (params.get(key) != null) {
                    value = (String) params.get(key);
                } else {
                    value = "";
                }
                parameterBuffer.append(key).append("=").append(value);
                if (iterator.hasNext()) {
                    parameterBuffer.append("&");
                }
            }
        }
        return parameterBuffer.toString();
    }
}
