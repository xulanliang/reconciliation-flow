package com.yiban.rec.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 国际化当前语言
 * 
 * @author 7
 * 
 */
public class CurrentEnvironment {
	/** HTTP中请求头当前语言环境参数 **/
	private static final String LANGUAGE = "language";
	/** HTTP中请求头当前语言环境参数值为中文 **/
	private static final String LANGUAGE_ZH = "zh";
	/** HTTP中请求头当前语言环境参数值为英文 **/
	private static final String LANGUAGE_EN = "en";
	/** HTTP中请求头当前语言环境参数值默认为中文 **/
	private static final String LANGUAGE_DEFAULT = "default";

	private static final String PROJECT_ID = "projectId";

	// public static Locale getLanguage(){
	// HttpServletRequest request =
	// ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	// switch
	// (StringUtils.isNotBlank(request.getHeader(LANGUAGE))?request.getHeader(LANGUAGE):LANGUAGE_DEFAULT)
	// {
	// case LANGUAGE_ZH:
	// return Locale.SIMPLIFIED_CHINESE;
	// case LANGUAGE_EN:
	// return Locale.US;
	// default:
	// return Locale.SIMPLIFIED_CHINESE;
	// }
	// }
	public static String getProjectId() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		return request.getHeader(PROJECT_ID);
	}

}
