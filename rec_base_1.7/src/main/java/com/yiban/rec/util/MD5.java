package com.yiban.rec.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.alibaba.fastjson.util.IOUtils;

public class MD5 {

	private static MessageDigest MD5;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String getMd5(File file) {
		byte[] buffer = new byte[4096];
		InputStream inputStream = null;
		int postion = -1;
		try {
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
			inputStream = new FileInputStream(file);
			while ((postion = inputStream.read(buffer)) > 0) {
				md5.update(buffer, 0, postion);
			}
			return CodingUtil.bytesToHexString(md5.digest());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(inputStream);
		}
		return "";
	}

	public static String getMd5(InputStream input) {
		byte[] buffer = new byte[4096];
		int postion = -1;
		try {
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
			while ((postion = input.read(buffer)) > 0) {
				md5.update(buffer, 0, postion);
			}
			return CodingUtil.bytesToHexString(md5.digest());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(input);
		}
		return "";
	}

	public static synchronized String getMd5(String msg) {
		return getMd5(msg.getBytes());
	}

	public static String getMd5(String source, String key) {
		byte[] k_ipad = new byte[64];
		byte[] k_opad = new byte[64];
		byte[] keyb = null;
		byte[] value = null;
		try {
			keyb = key.getBytes("UTF8");
			value = source.getBytes("UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		Arrays.fill(k_ipad, keyb.length, 64, (byte) 0x36);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 0x5c);

		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5C);
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte[] dg = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return CodingUtil.bytesToHexString(dg);
	}

	public static synchronized byte[] getMd5Byte(String msg) {
		return getMd5Byte(msg.getBytes());
	}

	public static synchronized byte[] getMd5Byte16(String msg) {
		return getMd5Byte16(msg.getBytes());
	}

	public static synchronized String getMd5_16(String msg) {
		return CodingUtil.bytesToHexString(getMd5Byte(msg), 4, 12);
	}

	public static synchronized byte[] getMd5Byte16(byte[] msg) {
		byte[] md5Byte = MD5.digest(msg);
		byte[] result = new byte[8];
		System.arraycopy(md5Byte, 4, result, 0, 8);
		return result;
	}

	public static synchronized byte[] getMd5Byte(byte[] msg) {
		return MD5.digest(msg);
	}

	public static synchronized String getMd5(byte[] msg) {
		MD5.update(msg);
		return CodingUtil.bytesToHexString(MD5.digest());
	}
}
