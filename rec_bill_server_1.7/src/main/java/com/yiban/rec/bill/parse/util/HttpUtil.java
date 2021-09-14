package com.yiban.rec.bill.parse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * @title http工具类
 * @description http工具类
 * 
 *              利用java原生 沒有使用第三方包 支持簡單get post請求
 * 
 * @author liyunqing
 * @date 2015-3-17上午11:36:17
 */
public class HttpUtil {

	// private static final String DEFAULT_CHARSET = "UTF-8";

	private static final String _GET = "GET"; // GET
	private static final String _POST = "POST";// POST
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;

	private static HttpUtil instance = null;

	public static HttpUtil getInstance() {
		if (instance == null)
			return new HttpUtil();
		return instance;
	}

	/**
	 * get請求
	 * 
	 * @param url
	 *            請求地址 如：http://api.caregg.cn/api
	 * @param requestPropertyMap
	 *            http请求头属性列表 map形式
	 * 
	 * 
	 *            调用例子： Map map = new HashMap<String, String>();
	 *            map.put("Content-Type", "application/json;charset=UTF-8");
	 *            String res = HttpUtil.sendGet(url,map,"utf-8");
	 * @return 返回服務端數據
	 * @throws IOException
	 */
	public static String sendGet(String url, Map<String, String> requestPropertyMap) {
		return sendGet(url, requestPropertyMap, "UTF-8");
	}

	/**
	 * get請求
	 * 
	 * @param url
	 *            請求地址 如：http://api.caregg.cn/api
	 * @return 返回服務端數據
	 * @throws IOException
	 */
	public static String sendGet(String url) {
		return sendGet(url, null, "UTF-8");
	}

	/**
	 * get請求
	 * 
	 * @param url
	 *            請求地址 如：http://api.caregg.cn/api
	 * @param charSet
	 *            接受服务端编码方式 如：UTF-8 GBK GB2312
	 * @param requestPropertyMap
	 *            http请求头属性列表 map形式
	 * 
	 * 
	 *            调用例子： Map map = new HashMap<String, String>();
	 *            map.put("Content-Type", "application/json;charset=UTF-8");
	 *            String res = HttpUtil.sendPost(url, param,"utf-8",map);
	 * 
	 * @return 返回服務端數據
	 * @throws IOException
	 */
	public static String sendGet(String url, Map<String, String> requestPropertyMap, String charSet) {
		String result = "";
		System.out.println("com.yiban.biz.util.HttpUtil.sendGet url:" + url);
		if ((url == null) || ("".equals(url)))
			return result;
		String urlName = url;
		URL U = null;
		try {
			U = new URL(urlName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "error";
		}
		URLConnection connection = null;
		try {
			connection = U.openConnection();

			if (requestPropertyMap != null)
				for (Map.Entry<String, String> entry : requestPropertyMap.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}

			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charSet));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
		return result;
	}

	/**
	 * post請求
	 * 
	 * @param url
	 *            請求地址 如：http://api.caregg.cn/api
	 * @param param
	 *            發送參數 如：param=1&param2=2
	 * @param charSet
	 *            接受服务端编码方式 如：UTF-8 GBK GB2312
	 * @return 返回服務端數據
	 * @throws IOException
	 */
	public static String sendPost(String url, String param, String charSet) {

		return sendPost(url, param, charSet, null);
	}

	/**
	 * post請求
	 * 
	 * @param url
	 *            請求地址 如：http://api.caregg.cn/api
	 * @param param
	 *            發送參數 如：param=1&param2=2
	 * @param charSet
	 *            接受服务端编码方式 如：UTF-8 GBK GB2312
	 * @param requestPropertyMap
	 *            http请求头属性列表 map形式
	 * 
	 * 
	 *            调用例子： Map map = new HashMap<String, String>();
	 *            map.put("Content-Type", "application/json;charset=UTF-8");
	 *            String res = HttpUtil.sendPost(url, param,"utf-8",map);
	 * 
	 * @return 返回服務端數據
	 * @throws IOException
	 */
	public static String sendPost(String url, String param, String charSet, Map<String, String> requestPropertyMap) {
		String result = "";
		System.out.println("com.yiban.biz.util.HttpUtil.sendPost param:" + param);
		System.out.println("com.yiban.biz.util.HttpUtil.sendPost url:" + url);
		if ((url == null) || ("".equals(url)))
			return result;
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			if (requestPropertyMap != null)
				for (Map.Entry<String, String> entry : requestPropertyMap.entrySet()) {
					httpConn.setRequestProperty(entry.getKey(), entry.getValue());
				}

			httpConn.setRequestMethod("POST");
			PrintWriter out = new PrintWriter(httpConn.getOutputStream());
			out.print(param);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), charSet));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("no result!" + e);
		}
		return result;
	}

	/**
	 * 初始化https请求参数
	 * 
	 * @param url
	 * @param method
	 * @return
	 * @throws Exception
	 */
	private static HttpsURLConnection initHttps(String url, String method, Map<String, String> headers)
			throws Exception {
		TrustManager[] tm = { new MyX509TrustManager() };
		System.setProperty("https.protocols", "TLSv1");
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, tm, new java.security.SecureRandom());
		// 从上述SSLContext对象中得到SSLSocketFactory对象
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		URL _url = new URL(url);
		HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
		// 设置域名校验
		http.setHostnameVerifier(new HttpUtil().new TrustAnyHostnameVerifier());
		// 连接超时
		http.setConnectTimeout(DEF_CONN_TIMEOUT);
		// 读取超时 --服务器响应比较慢，增大时间
		http.setReadTimeout(DEF_READ_TIMEOUT);
		http.setUseCaches(false);
		http.setRequestMethod(method);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		http.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
		if (null != headers && !headers.isEmpty()) {
			for (Entry<String, String> entry : headers.entrySet()) {
				http.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		http.setSSLSocketFactory(ssf);
		http.setDoOutput(true);
		http.setDoInput(true);
		http.connect();
		return http;
	}

	/**
	 * 
	 * @description 功能描述: get 请求
	 * @return 返回类型:
	 * @throws Exception
	 * 用于https请求
	 */
	public static String httpsGet(String url, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		HttpURLConnection http = null;
		// if (isHttps(url)) {
		http = initHttps(url, _GET, headers);
		// } else {
		// http = initHttp(initParams(url, params), _GET, headers);
		// }
		InputStream in = http.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String valueString = null;
		StringBuffer bufferRes = new StringBuffer();
		while ((valueString = read.readLine()) != null) {
			bufferRes.append(valueString);
		}
		in.close();
		if (http != null) {
			http.disconnect();// 关闭连接
		}
		return bufferRes.toString();
	}

	/**
	 * 
	 * @description 功能描述: post 请求
	 * @return 返回类型:
	 * @throws Exception
	 * 
	 * 用于https请求
	 */
	public static String httpsPost(String url, String params, String charSet) throws Exception {
		HttpURLConnection http = null;
		// if (isHttps(url)) {
		http = initHttps(url, _POST, null);
		// } else {
		// http = initHttp(url, _POST, null);
		// }
		OutputStream out = http.getOutputStream();
		out.write(params.getBytes(charSet));
		out.flush();
		out.close();

		InputStream in = http.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(in, charSet));
		String valueString = null;
		StringBuffer bufferRes = new StringBuffer();
		while ((valueString = read.readLine()) != null) {
			bufferRes.append(valueString);
		}
		in.close();
		if (http != null) {
			http.disconnect();// 关闭连接
		}
		return bufferRes.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String param = "";
		Map map = new HashMap<String, String>();
		map.put("Content-Type", "application/json;charset=UTF-8");
		String res = HttpUtil.sendPost("http://send", param, "utf-8", map);

	}

	/**
	 * https 域名校验
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;// 直接返回true
		}
	}
}
