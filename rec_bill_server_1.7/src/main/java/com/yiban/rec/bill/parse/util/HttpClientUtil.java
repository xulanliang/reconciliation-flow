package com.yiban.rec.bill.parse.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	private static final String ENCODING = "utf-8";
	public static final String splash = "\\JHZF\\";
	public static final String root = "D:";
	public static final int cache = 10 * 1024;

	/**
	 * 根据url下载文件，文件名从response header头中获取
	 *
	 * @param url
	 * @return
	 */
	public static String download(String url) {
		return download(url, null);
	}

	/**
	 * 根据url下载文件，保存到filepath中
	 *
	 * @param url
	 * @param filepath
	 * @return
	 */
	public static String download(String url, String filepath) {
		try {
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = client.execute(httpget);

			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			if (filepath == null) {
				filepath = getFilePath(response);
			}
			File file = new File(filepath);
			file.getParentFile().mkdirs();
			FileOutputStream fileout = new FileOutputStream(file);
			/**
			 * 根据实际运行效果 设置缓冲区大小
			 */
			byte[] buffer = new byte[cache];
			int ch = 0;
			while ((ch = is.read(buffer)) != -1) {
				fileout.write(buffer, 0, ch);
			}
			is.close();
			fileout.flush();
			fileout.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return filepath;
	}

	/**
	 * 获取response要下载的文件的默认路径
	 *
	 * @param response
	 * @return
	 */
	public static String getFilePath(HttpResponse response) {
		String filepath = root + splash;
		String filename = getFileName(response);

		if (filename != null) {
			filepath += filename;
		} else {
			filepath += getRandomFileName();
		}
		return filepath;
	}

	/**
	 * 获取response header中Content-Disposition中的filename值
	 *
	 * @param response
	 * @return
	 */
	public static String getFileName(HttpResponse response) {
		Header contentHeader = response.getFirstHeader("Content-Disposition");
		String filename = null;
		if (contentHeader != null) {
			HeaderElement[] values = contentHeader.getElements();
			if (values.length == 1) {
				NameValuePair param = values[0].getParameterByName("filename");
				if (param != null) {
					try {
						// filename = new String(param.getValue().toString().getBytes(), "utf-8");
						// filename=URLDecoder.decode(param.getValue(),"utf-8");
						filename = param.getValue();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return filename;
	}

	/**
	 * 获取随机文件名
	 *
	 * @return
	 */
	public static String getRandomFileName() {
		return String.valueOf(System.currentTimeMillis());
	}

	public static void outHeaders(HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}
	}

	/*
	 * public static void download(String urlString, String filename) { // 构造URL URL
	 * url; try { url = new URL(urlString); // 打开连接 URLConnection con =
	 * url.openConnection(); // 输入流 InputStream is = con.getInputStream(); //
	 * 1K的数据缓冲 byte[] bs = new byte[1024]; // 读取到的数据长度 int len; // 输出的文件流s
	 * OutputStream os = new FileOutputStream(filename); // 开始读取 while ((len =
	 * is.read(bs)) != -1) { os.write(bs, 0, len); } // 完毕，关闭所有链接 os.close();
	 * is.close(); } catch (MalformedURLException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */

	public static void getZipFile(String url, Map<String, String> param) throws IOException, URISyntaxException {
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		// 创建uri
		URIBuilder builder = new URIBuilder(url);
		if (param != null) {
			for (String key : param.keySet()) {
				builder.addParameter(key, param.get(key));
			}
		}
		URI uri = builder.build();

		// 创建http GET请求
		HttpGet httpGet = new HttpGet(uri);

		// 执行请求
		response = httpclient.execute(httpGet);
		// 从服务器请求文件流，具体代码就不贴了
		InputStream inputStream = response.getEntity().getContent();
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		byte[] buff = new byte[1024 * 1024]; // 如果是稍微大的文件，这里配置的大一些
		int len = 0;
		while ((len = inputStream.read(buff)) > 0) {
			// 把从服务端读取的文件流保存到ByteArrayOutputSteam中
			byteArray.write(buff, 0, len);
			byteArray.flush();
		}
		inputStream.close();
		response.close();

		// GZIPInputstream解压文件，然后读取文件
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new ZipInputStream(new ByteArrayInputStream(byteArray.toByteArray())), "utf-8"));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
		}
	}

	public static String doGet(String url, Map<String, String> param) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), ENCODING);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
		return resultString;
	}

	public static String doGet(String url) throws Exception {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, ENCODING);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null);
	}

	public static String doPostJson(String url, String json) {
		return doPostJson(url, null, json);
	}

	// 这个要弄长点
    public static final  int TIMEOUT = 600;
    public static final String ACCEPT_NAME = "Accept";
    public static final String ACCEPT = "application/json;charset=UTF-8";

    /**
	 * 请求的参数类型为json
	 *
	 * @param url
	 * @param json
	 * @return {username:"",pass:""}
	 */
	public static String doPostJson(String url, Map<String, String> headerMap, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {

            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT * 1000)
                    .setConnectTimeout(TIMEOUT * 1000)
                    .setConnectionRequestTimeout(TIMEOUT * 1000)
                    .build();

			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);

            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            httpPost.addHeader(ACCEPT_NAME, ACCEPT);
            httpPost.setConfig(defaultRequestConfig);

            if (headerMap != null) {
				Iterator headerIterator = headerMap.entrySet().iterator(); // 循环增加header
				while (headerIterator.hasNext()) {
					Entry<String, String> elem = (Entry<String, String>) headerIterator.next();
					httpPost.addHeader(elem.getKey(), elem.getValue());
				}
			}

			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	/**
	 * 请求的参数类型为xml,支持SOAP1.2
	 *
	 * @param url
	 * @param xml
	 *
	 * @return {username:"",pass:""}
	 */
	public static String doPostXml(String url, String xml, String soapname) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 设置SOAPAction，支持soap1.2
			httpPost.setHeader("SOAPAction", soapname);
			// 创建请求内容
			StringEntity entity = new StringEntity(xml, ContentType.TEXT_XML);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	/**
	 * 请求的参数类型为xml
	 *
	 * @param url
	 * @param xml
	 * @return {username:"",pass:""}
	 */
	public static String doPostXml(String url, String xml) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(xml, ContentType.TEXT_XML);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
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
	
	/**
	 *该方法适用于调用：使用SOAP1.1协议的webService服务
	 *
	 */
	public static String doPostWebserviceForSoap11(String url, String xml,String SOAPAction) throws Exception {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			httpPost.setHeader("SOAPAction",SOAPAction);
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
	//form格式
	public static String doPostform(String url, org.apache.commons.httpclient.NameValuePair[] data) {

		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		PostMethod postMethod=null;
		try {
			postMethod = new PostMethod(url);
			postMethod.setRequestBody(data);
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
			int i = httpClient.executeMethod(postMethod);
			String result = postMethod.getResponseBodyAsString() ;
			return result;
		} catch (Exception e) {
			// logger.info("请求异常"+e.getMessage(),e);
			throw new RuntimeException(e.getMessage());
		}

	}
	
	public static String doPostRequest(String url,String xml) {
		return doPostRequest(url,xml,"");
	}
	
	public static String doPostRequest(String url,String xml,String SOAPAction) {
		String result = "";
		Map<String, String> requestPropertyMap = new HashMap<>();
		requestPropertyMap.put("Content-Type", "text/xml;charset=UTF-8");
		requestPropertyMap.put("SOAPAction", SOAPAction);
		if ((url == null) || ("".equals(url)))
			return result;
		try {
			URL httpurl = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) httpurl
					.openConnection();
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			for(Map.Entry<String, String> entry : requestPropertyMap.entrySet()) {
				httpConn.setRequestProperty(entry.getKey(), entry.getValue());
			}

			httpConn.setRequestMethod("POST");
			PrintWriter out = new PrintWriter(httpConn.getOutputStream());
			out.print(xml);
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result = result + line;
			}
			in.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return result;
	}
}