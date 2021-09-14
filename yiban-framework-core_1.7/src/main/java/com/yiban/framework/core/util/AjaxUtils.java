package com.yiban.framework.core.util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author swing
 * @date 2018年1月23日 下午2:49:48 类说明
 */
public class AjaxUtils {
	private static Logger logger = LoggerFactory.getLogger(AjaxUtils.class);
	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * 验证是否是ajax请求
	 * 
	 * @param webRequest
	 * @return
	 */
	public static boolean isAjaxRequest(WebRequest webRequest) {
		String requestedWith = webRequest.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	public static boolean isAjaxUploadRequest(WebRequest webRequest) {
		return webRequest.getParameter("ajaxUpload") != null;
	}

	public static void writeJson(Object value, HttpServletResponse response) {
		JsonGenerator jsonGenerator = null;
		try {
			jsonGenerator = mapper.getFactory().createGenerator(response.getOutputStream(), JsonEncoding.UTF8);
			if (jsonGenerator != null) {
				jsonGenerator.writeObject(value);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private AjaxUtils() {
	}
}
