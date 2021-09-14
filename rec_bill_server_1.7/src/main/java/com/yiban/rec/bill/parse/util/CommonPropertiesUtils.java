package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

/**
 * 属性文件工具类
 * 
 * @author houguiqiang
 * @2017年3月23日
 */
public class CommonPropertiesUtils {
	
	protected static final Logger LOG = LoggerFactory.getLogger(CommonPropertiesUtils.class);

	// 外部的配置文件所在的文件夹名称
	private static String PATH = "config";
	private static Properties pro;
	private static final Map<String, String> MAP = new HashMap<>();

	static {
		try {
			String resource = "application.properties";
			InputStream inputStream = configPathFile(resource);
			if (inputStream == null) {
				// 外部文件不存在则读项目内的
				inputStream = resourceFile(resource);
			}
			init(inputStream);
			// 获取active属性
			String active = pro.getProperty("spring.profiles.active");
			LOG.info("CommonPropertiesUtils 获取active属性 = " + active);
			// 配置文件名称
			String activeFileName = "application-@ACTIVE.properties";
			activeFileName = activeFileName.replace("@ACTIVE", active);

			// 缓存数据
			init(resourceFile(resource), true);
			init(configPathFile(resource), true);
			init(resourceFile(activeFileName), true);
			init(configPathFile(activeFileName), true);
			LOG.info("CommonPropertiesUtils 获取配置文件MAP= " + MAP);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private CommonPropertiesUtils() {
	}

	private static void init(InputStream inputStream) {
		init(inputStream, false);
	}

	/**
	 * 加载properties文件
	 * 
	 * @param inputStream properties文件流
	 * @param isSave      是否将properties缓存到map
	 */
	@SuppressWarnings("unchecked")
	private static void init(InputStream inputStream, boolean isSave) {
		if (inputStream == null) {
			return;
		}
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			pro = new Properties();
			pro.load(inputStreamReader);

			if (isSave) {
				MAP.putAll((Map) pro);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 项目外部的文件夹读取文件
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	private static InputStream configPathFile(String fileName) throws FileNotFoundException {
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		String pathStr = path.getAbsolutePath();
		File config = new File(pathStr + File.separator + PATH + File.separator + fileName);

		return config.exists() ? new FileInputStream(config) : null;
	}

	/**
	 * 项目内读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	private static InputStream resourceFile(String fileName) {
		return CommonPropertiesUtils.class.getResourceAsStream("/" + fileName);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getValue(String key, String defaultValue) {
		String value = MAP.get(key);
		if (value != null && value.indexOf("${") > -1) {

			try {
				value = formatStr(value);
				MAP.put(key, value);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}
	
	public static String getValue(String key) {
		String value = MAP.get(key);
		if (value != null && value.indexOf("${") > -1) {
			
			try {
				value = formatStr(value);
				MAP.put(key, value);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
	
	/**
	 *  递归替换占位符${}内的内容
	 * @param value
	 * @return
	 */
	private static String formatStr(String value) {
		if (value != null && value.indexOf("${") > -1) {
			String s = "${";
			String e = "}";
			String keyTemp = value.substring(value.indexOf(s) + s.length(), value.indexOf(e));
			value = value.replaceAll("\\$\\{" + keyTemp + "\\}", MAP.get(keyTemp).toString());
			value = formatStr(value);
		}
		return value;
	}
}
