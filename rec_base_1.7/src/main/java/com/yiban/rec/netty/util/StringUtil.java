package com.yiban.rec.netty.util;

import com.yiban.rec.netty.common.GlobalConstValue;

/**
 * 
 * @ClassName: StringUtil
 * @Description: 字符串长度计算工具类
 * @author chuntu tuchun168@163.com
 * @date 2016年5月3日 上午11:28:06
 *
 */
public class StringUtil {

	/** 得到字符串长度 */
	public static int getStringProtocolLen(String str, StringEncode encode) {
		int len = 0;
		if (str == null)
			return GlobalConstValue.SHORT_LEN;
		try {
			len += str.getBytes(encode.getEncoder()).length
					+ GlobalConstValue.SHORT_LEN;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return len;
	}
	/** 得到字符串长度 长度是int*/
	public static int getStringProtocolLenINT(String str, StringEncode encode) {
		int len = 0;
		if (str == null)
			return GlobalConstValue.INT_LEN;
		try {
			len += str.getBytes(encode.getEncoder()).length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return len;
	}
	
	/**
	 * 字符串截取字节。（截取位置为多字节，舍弃）
	 * 
	 * @param str
	 * @param byteLen
	 * @param appendStr
	 *            字符串过长截取后拼接字符串。如：... 可为null
	 * @return
	 */
	public static String subStr(String str, int byteLen, String appendStr) {
		String retStr = "";
		if (str == null)
			return retStr;
		int len = str.getBytes().length;
		if (len <= byteLen)
			return str;

		char[] cs = str.toCharArray();
		int indexChar = 0;
		int indexByteLen = 0;
		for (char c : cs) {
			int clen = Character.toString(c).getBytes().length;
			indexByteLen = indexByteLen + clen;
			if (indexByteLen < byteLen) {
				indexChar++;
				continue;
			} else if (indexByteLen == byteLen) {
				indexChar++;
				return appendStr == null ? str.substring(0, indexChar) : str
						.substring(0, indexChar) + appendStr;
			} else if (indexByteLen > byteLen) {
				return appendStr == null ? str.substring(0, indexChar) : str
						.substring(0, indexChar) + appendStr;
			}
		}
		return str;
	}
}
