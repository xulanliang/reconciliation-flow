package com.yiban.rec.util;

import java.security.MessageDigest;

import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import com.google.common.base.Charsets;

public class Md5Util {
	
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;
	
	/**
	 * 返回md5加密的字节数组

	 * @param b 需要加密的字节数组
	 * @param len 需要加密的字节数组的长度

	 * @return 
	 * byte[]
	 */
	public static byte[] getMD5(byte[] b, int len) {
		MessageDigest digest;
		String algorithm = "MD5";
		try {
			digest = MessageDigest.getInstance(algorithm);
			digest.update(b, 0, len);
			return digest.digest();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 * 返回md5加密的HEX型字符串
	 * 
	 * @param src
	 * @return String
	 */
	public static String getMD5(String src) {
		byte[] b = src.getBytes();
		byte[] ret = getMD5(b, b.length);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ret.length; i++) {
			int k = (ret[i] + 128) % 128;
			sb.append(Integer.toHexString(k));
		}
		return sb.toString();
	}
	
	public static String entryptPassword(String plainPassword) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(Charsets.UTF_8), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(hashPassword);
	}
	
	public static void main(String[] args) {
		System.out.println(Md5Util.entryptPassword("fzx123456"));
		System.out.println(Md5Util.getMD5("fzx123456"));
	}

}
