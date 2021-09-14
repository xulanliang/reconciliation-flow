package com.yiban.rec.util;

import com.yiban.framework.core.util.PropertyUtil;

public class Configure {
	private Configure() {

	}

	private static Configure configure;

	public synchronized static Configure getInstance() {
		if (configure == null) {
			configure = new Configure();
		}
		return configure;
	}

	public String getProperties(String sign) {
	/*	InputStream in = getClass().getResourceAsStream(
				"/application.properties");
		Properties prop = new Properties();
		String properties = "";
		try {
			prop.load(in);
			properties = prop.getProperty(sign).toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return properties;*/
		return PropertyUtil.getProperty(sign, "");
	}

	public static String getPropertyBykey(String key) {
		Configure configure = Configure.getInstance();
		return configure.getProperties(key);
	}

	/*public static void main(String[] args) {
		System.out.println("listen_ip:" + getPropertyBykey("listen_ip"));
	}*/
}