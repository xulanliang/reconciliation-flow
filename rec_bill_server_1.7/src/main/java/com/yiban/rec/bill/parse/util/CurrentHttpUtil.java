package com.yiban.rec.bill.parse.util;

/**
 * describe:
 *
 * @author xll
 * @date 2020/04/29
 */

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


public class CurrentHttpUtil {


    private final static Logger logger = Logger.getLogger(CurrentHttpUtil.class);

    public static final String CHARSET = "UTF-8";

    private static CloseableHttpClient httpClient = createSSLInsecureClient();

    public static final Integer HTTP_OK = 200;

    public static final String CONTENT_TYPE_NAME = "Content-Type";

    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    public static final String CONTENT_TYPE_XML = "text/xml;charset=UTF-8";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=UTF-8";

    public static final String ACCEPT_NAME = "Accept";
    public static final String ACCEPT = "application/json;charset=UTF-8";

    public static final int TIMEOUT = 60;//这个要弄长点


    public static String postUrl(String url, Map<String, Object> params) {
        String result = null;
        CloseableHttpResponse response = null;       //返回结果,释放链接
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT * 1000)
                    .setConnectTimeout(TIMEOUT * 1000)
                    .setConnectionRequestTimeout(TIMEOUT * 1000)
                    .build();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            httpPost.addHeader(CONTENT_TYPE_NAME, CONTENT_TYPE_JSON);
            httpPost.addHeader(ACCEPT_NAME, ACCEPT);
            httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            httpPost.setConfig(defaultRequestConfig);

            /*httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));*/
            Gson gson = new Gson();
            String myParams = gson.toJson(params).toString();
            httpPost.setEntity(new StringEntity(myParams, CHARSET));
            response = httpClient.execute(httpPost);     //建立链接得到返回结果
            int statusCode = response.getStatusLine().getStatusCode();      //返回的结果码
            if (statusCode != 200) {
                httpPost.abort();
                logger.error("===httphelper==httpclient===请求异常");
                return null;
            }
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                logger.error("===httphelper==httpclient===返回结果异常");
                return null;
            } else {
                result = EntityUtils.toString(httpEntity, CHARSET);
            }
            EntityUtils.consume(httpEntity);        //按照官方文档的说法：二者都释放了才可以正常的释放链接
            response.close();
            return result;
        } catch (Exception e) {
            logger.error("===httphelper==httpclient===请求错误" + e.getMessage() + ",错误信息" + e);
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("===httphelper==httpclient===关闭流异常" + e.getMessage() + ",错误信息" + e);
                }
            }
        }
    }


    public static CloseableHttpClient createSSLInsecureClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    new TrustStrategy() {

                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setMaxConnTotal(100).setMaxConnPerRoute(10)
                    .setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }


}
