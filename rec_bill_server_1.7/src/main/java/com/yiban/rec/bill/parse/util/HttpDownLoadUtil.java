package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import  com.yiban.rec.bill.parse.util.SymbolConstant;

/**
 * 基于httpClient的http工具类
 * 
 * @author Zhouych
 */
public class HttpDownLoadUtil {

	/**
	 * 默认超时时间
	 */
	private static final int defaultConnectionTimeout = 3000;

	/**
	 * 默认接收数据类型为json
	 */
	private static Header defaultHeader = new BasicHeader("Accept", "application/json");

	/**
	 * Description: 发起一个get请求
	 *
	 * @param url
	 *            请求地址
	 * @return 请求相应得到的String数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String get(String url) throws ClientProtocolException, IOException {
		return Request.Get(url).setHeaders(defaultHeader).connectTimeout(defaultConnectionTimeout).execute()
				.returnContent().asString();
	}
	
	
	
	/**
	 * Description: 发起一个get请求
	 *
	 * @param url
	 *            请求地址
	 * @return 请求相应得到的String数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String getFile(String url,String dirDestroy) throws ClientProtocolException, IOException {
		//return 
		HttpResponse response = Request.Get(url).execute().returnResponse();
		String fileName = response.getFirstHeader("Content-Disposition").getValue();
		fileName = fileName.substring(fileName.lastIndexOf("=") + 1, fileName.length());
		String file = dirDestroy + "\\" + fileName;
		final FileOutputStream out = new FileOutputStream(file);
		HttpEntity entity = null;
        try {
            entity = response.getEntity();
            if (entity != null) {
                entity.writeTo(out);
            }
        } finally {
        	if(null != entity)
        	EntityUtils.consume(entity);
        	if(null != out)
            out.close();
        }
        return file;
//		Request.Get(url).execute().saveContent(new File(file));
	}
	
	/**
	 * Description: 发起一个get请求
	 *
	 * @param url
	 *            请求地址
	 * @return 请求相应得到的String数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String getFile(String url) throws ClientProtocolException, IOException {
		return Request.Get(url).execute().returnResponse().getFirstHeader("Content-Disposition").getValue();
		//Request.Get(url).execute().saveContent(new File());
	}
	
	/**
	 * 下载文件并保存到destPath
	 * 
	 * @param url
	 * @param destPath
	 * @throws IOException
	 */
	public static void transferFileTo(String url, String destPath) throws IOException {
		Request.Get(url).execute().saveContent(new File(destPath));
	}

	

	/**
	 * Description: 发起一个get请求
	 *
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @return 请求相应得到的String数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String get(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		return get(url, params, null, defaultConnectionTimeout);
	}

	/**
	 * Description: 发起一个get请求
	 *
	 * @param url
	 *            请求地址
	 * @param params
	 *            参数
	 * @param header
	 *            请求头
	 * @param connTimeout
	 *            连接超时毫秒数
	 * @return 请求相应得到的String数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String get(String url, Map<String, String> params, Map<String, String> header, int connTimeout)
			throws ClientProtocolException, IOException {
		String excuteUrl = trans2Url(url, params);
		Header[] headers = null;
		if (null != header && header.size() > 0) {
			headers = new Header[header.size()];
			int i = 0;
			for (Iterator<String> it = header.keySet().iterator(); it.hasNext();) {
				String headerName = it.next();
				headers[i++] = new BasicHeader(headerName, header.get(headerName));
			}
		} else {
			headers = new Header[] { defaultHeader };
		}
		return Request.Get(excuteUrl).setHeaders(headers).connectTimeout(connTimeout).execute().returnContent()
				.asString();
	}

	
	

	/**
	 * Description: 执行post请求
	 *
	 * @param url
	 *            目标地址
	 * @param params
	 *            请求参数
	 * @return 返回数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String post(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		final Header header = null;
		return post(url, params, header);
	}

	/**
	 * Description: 执行post请求
	 *
	 * @param url
	 *            目标地址
	 * @param params
	 *            请求参数
	 * @param header
	 *            header信息
	 * @return 返回数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String post(String url, Map<String, String> params, Header header)
			throws ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new LinkedList<>();
		if (null != params) {
			for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
				String paramName = it.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, params.get(paramName)));
			}
		}
		return Request.Post(url).setHeader(header).connectTimeout(defaultConnectionTimeout)
				.bodyForm(nameValuePairs, Consts.UTF_8).execute().returnContent().asString();
	}

	
	/**
	 * Description: 执行put请求
	 *
	 * @param url
	 *            目标地址
	 * @param params
	 *            请求参数
	 * @return 返回数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String put(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new LinkedList<>();
		if (null != params) {
			for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
				String paramName = it.next();
				nameValuePairs.add(new BasicNameValuePair(paramName, params.get(paramName)));
			}
		}
		return Request.Put(url).setHeader(defaultHeader).connectTimeout(defaultConnectionTimeout)
				.bodyForm(nameValuePairs, Consts.UTF_8).execute().returnContent().asString();
	}

	

	/**
	 * Description: 发起一个delete请求
	 *
	 * @param url
	 *            请求地址
	 * @return 返回数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String delete(String url) throws ClientProtocolException, IOException {
		return Request.Delete(url).setHeaders(defaultHeader).connectTimeout(defaultConnectionTimeout).execute()
				.returnContent().asString();
	}

	/**
	 * Description: 发起一个delete请求
	 *
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 返回数据
	 * @throws ClientProtocolException
	 *             协议支持异常
	 * @throws IOException
	 *             网络等异常信息
	 */
	public static String delete(String url, Map<String, String> params) throws ClientProtocolException, IOException {
		String excuteUrl = trans2Url(url, params);
		return delete(excuteUrl);
	}

	


	
	
	/**
	 * 获取文件流
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpEntity getFileInputStream(String url) throws IOException {
		return Request.Get(url).execute().returnResponse().getEntity();
	}

	/**
	 * 拼接URL
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String trans2Url(String url, Map<String, String> params) {
		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("the url should be not empty");
		}
		if (null == params || params.isEmpty()) {
			return url;
		}

		StringBuffer buf = new StringBuffer(url);
		boolean constainsAsk = false;
		for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String value = params.get(key);
			if (constainsAsk || url.indexOf(SymbolConstant.ASK) != -1) {
				buf.append(SymbolConstant.AND).append(key).append(SymbolConstant.EQUAL).append(value);
			} else {
				buf.append(SymbolConstant.ASK).append(key).append(SymbolConstant.EQUAL).append(value);
				constainsAsk = true;
			}
		}
		return buf.toString();
	}

	

	/**
	 * 根据contentType获取文件后缀
	 * 
	 * @param contentType
	 * @return
	 */
	public static String getExtensionByContentType(String contentType) {
		if (StringUtils.isEmpty(contentType)) {
			return StringUtils.EMPTY;
		}
		return contentTypeExtensionMap.get(contentType);
	}

	private static Map<String, String> contentTypeExtensionMap = new HashMap<>();

	static {
		contentTypeExtensionMap.put("image/jpeg", "jpg");
		contentTypeExtensionMap.put("application/x-jpg", "jpg");
		contentTypeExtensionMap.put("image/png", "png");
		contentTypeExtensionMap.put("application/x-png", "png");
		contentTypeExtensionMap.put("image/bmp", "bmp");
		contentTypeExtensionMap.put("application/x-bmp", "bmp");
		contentTypeExtensionMap.put("image/x-icon", "ico");
		contentTypeExtensionMap.put("application/x-ico", "ico");
		contentTypeExtensionMap.put("image/gif", "gif");
		contentTypeExtensionMap.put("application/x-jpe", "jpe");
		contentTypeExtensionMap.put("audio/mp3", "mp3");
		contentTypeExtensionMap.put("application/msword", "doc");
		contentTypeExtensionMap.put("application/vnd.ms-excel", "xls");
		contentTypeExtensionMap.put("audio/amr", "amr");
		contentTypeExtensionMap.put("audio/amr-wb", "amr");
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		/*String name = Request.Get(
				"https://uat.pyhtech.com/pay/portal/down?downloadFlag=1&merchFlag=1&tradeTime=20181227155857&agentComyId=18101710BCA7FA1D&sign=b1a6ed096e81a175280f104dd70cd8ac&settleDate=20181219&version=1.0.0&merchId=18101714047FE228")
				.execute().returnResponse().getFirstHeader("Content-Disposition").getValue();
		System.out.println(name);*/
		String url = "https://uat.pyhtech.com/pay/portal/down?downloadFlag=1&merchFlag=1&tradeTime=20181227171848&agentComyId=18101710BCA7FA1D&sign=02a904b0349b946aad3646debb24a90d&settleDate=20181219&version=1.0.0&merchId=18101714047FE228";
		
		String file = HttpDownLoadUtil.getFile(url,"d:\\test");
		System.out.println(file);
		
	}
}
