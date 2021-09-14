package com.yiban.rec.bill.parse.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 簡單aes加解密工具類
 *
 */
public class AESUtils {
	// 算法/模式/填充
	private static final String CipherMode = "AES/CBC/PKCS5Padding";
	private static final String DEFAULST_SECURITY_KEY = "XXX-123-!@#$%^&*()_+=,./";
	private static final String encodFormat = "UTF-8";

	/**
	 * 创建密钥
	 * 
	 * @param key
	 * @return
	 */
	private static SecretKeySpec createKey(String key) {
		key = key == null ? "" : key;
		StringBuilder sb = new StringBuilder(16);
		sb.append(key);
		while (sb.length() < 16) {
			sb.append("0");
		}
		if (sb.length() > 16) {
			sb.setLength(16);
		}
		byte[] data = null;
		try {
			data = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new SecretKeySpec(data, "AES");
	}

	/**
	 * 创建初始化向量
	 * 
	 * @param password
	 * @return
	 */
	private static IvParameterSpec createIV(String password) {
		password = password == null ? "" : password;
		StringBuilder sb = new StringBuilder(16);
		sb.append(password);
		while (sb.length() < 16) {
			sb.append("0");
		}
		if (sb.length() > 16) {
			sb.setLength(16);
		}
		byte[] data = null;
		try {
			data = sb.toString().getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new IvParameterSpec(data);
	}

	/**
	 * AES-128/CBC算法加密字节数据
	 * 
	 * @param content  内容
	 * @param password 密码
	 * @param iv       向量
	 * @return
	 */
	public static byte[] aes128CBCEncrypt(byte[] content, String password, String iv) {
		try {
			Cipher cipher = Cipher.getInstance(CipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, createKey(password), createIV(iv));
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * AES-128/CBC算法解密字节数组 *
	 * 
	 * @param content *
	 * @param password
	 * @param iv       初始化向量 * @return byte[]
	 */
	public static byte[] aes128CBCDecrypt(byte[] content, String password, String iv) {
		try {
			Cipher cipher = Cipher.getInstance(CipherMode);
			cipher.init(Cipher.DECRYPT_MODE, createKey(password), createIV(iv));
			byte[] result = cipher.doFinal(content);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * AES-128加密字符串 * *
	 * 
	 * @param content *
	 * @return
	 */
	public static String encrypt(String content) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(DEFAULST_SECURITY_KEY.getBytes(encodFormat)));
			byte[] bytes = kgen.generateKey().getEncoded();
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"));
			byte[] result = cipher.doFinal(content.getBytes(encodFormat));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				String hex = Integer.toHexString(result[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append(hex.toUpperCase());
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * AES-128 CBC加密方式， 加密后使用Base64转码 * *
	 * 
	 * @param content        待加密内容 *
	 * @param encodingFormat *
	 * @param key            密钥 *
	 * @param initVector     初始化向量 *
	 * @return *
	 * @throws Exception
	 */
	public static String aesCBCEncrypt(String content, String encodingFormat, String key, String initVector) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(encodingFormat), "AES");
			// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			IvParameterSpec vector = new IvParameterSpec(initVector.getBytes(encodingFormat));
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, vector);
			byte[] encrypted = cipher.doFinal(content.getBytes(encodingFormat));
			// 此处使用BASE64做转码。
			String result = Base64.getEncoder().encodeToString(encrypted);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * AES-128 CBC解密方式 * *
	 * 
	 * @param content        待解密的Base64字符串 *
	 * @param encodingFormat *
	 * @param key            密钥 *
	 * @param initVector     初始化向量 *
	 * @return
	 */
	public static String aesCBCDecrypt(String content, String encodingFormat, String key, String initVector) {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(encodingFormat), "AES");
			IvParameterSpec vector = new IvParameterSpec(initVector.getBytes());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, vector);
			// 先用base64编码，因为对应的加密使用Base64解码
			byte[] base64Bytes = Base64.getDecoder().decode(content);
			byte[] original = cipher.doFinal(base64Bytes);
			String result = new String(original, encodingFormat);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加密
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String encrypt(String content, String key) {
		if (isEmpty(key)) {
			return encrypt(content);
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes(encodFormat)));
			byte[] bytes = kgen.generateKey().getEncoded();
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(bytes, "AES"));
			byte[] result = cipher.doFinal(content.getBytes(encodFormat));
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < result.length; i++) {
				String hex = Integer.toHexString(result[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}
				sb.append(hex.toUpperCase());
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * 解密 * *
	 * 
	 * @param content *
	 * @return
	 */
	public static String decrypt(String content) {
		if (isEmpty(content)) {
			return null;
		}
		byte[] bytes = new byte[content.length() / 2];
		for (int i = 0; i < content.length() / 2; i++) {
			int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2), 16);
			bytes[i] = (byte) (high * 16 + low);
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(DEFAULST_SECURITY_KEY.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(bytes);
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * * 解密 * *
	 * 
	 * @param content *
	 * @param securityKey *
	 * @return
	 */
	public static String decrypt(String content, String securityKey) {
		if (isEmpty(securityKey)) {
			return decrypt(content);
		}
		byte[] bytes = new byte[content.length() / 2];
		for (int i = 0; i < content.length() / 2; i++) {
			int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2), 16);
			bytes[i] = (byte) (high * 16 + low);
		}
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(securityKey.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(bytes);
			return new String(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean equal(byte[] a1, byte[] a2) {
		if (a1 != null && a2 != null && a1.length == a2.length) {
			for (int i = 0; i < a1.length; i++) {
				if (a1[i] != a2[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String extParam = "HOO4xWeZZuGZQ/ldq5+RBUHF3x8LQKrJsxRjy3Ef3J6cAKhTQ0dWMLww3AB0hJ9qcKovSeu83ifX9pa+0l7QomHvc5Cs4iuYzvpH7+VPhk/sf0Kopai5dZ5ClFw8fgz4uINBMth3jS9OpXPv++B+6jqDEn3kb91Bzj1HDRNnY4KLNn6IaflfpaHj7Qy7yq/h6WbwgzA5pvlvQGt+G7nPns6JAjR6AsgmFIQ3kQ12aaBtyZcP6DhAL874vpow8b6VQs4ffedxOgsOmkQ01gXGsBMkL9CiiQsawNs4UPPNQJ8GZlMKsWI8rSn5quv1ErkNJx/2LdRfMCWg7oqYJybdKtcmZfivPYka+2TWFpx4m0cP/LJYduFq6W+Pt7NWTW1pMbZ6hgZi8tKolmcPVdsIjy3mkq4eQbCi+YyBmh7+f0ayG1NKOmnZgVZTCyP1tVLUlH2E9PNQllbvQHVzfPMyZMgC/YE8qQlOPWtJFboXepvCSTgwSpMotuD76Fy8BU4K3e5MkVvp5b9clNQJysNMRKblzmUkjWGqhwwAvQA90549A31hjmJrLf2H8A9wkJXRBtVEgQAHZRCQ1dvvXzjq6Rdv24QPj+ZEQWSt3+Q1jN6xFsfXl+Cy7ViIo7SM2xNQkMcLmGfLSAxCIaKzUX+7CVNyLZNF00NHETzYPTprhIiN72Ac7fuA4TkJ+r3qnHY0mOzZ97wmVuja31IY2Q2KMzyJw1k6nZyhySvsjq/hdXww6TXV9YVRe2zoqyHRd0GP3k2ftz8DAYHxlS43yz4hxVml55w3UQbd4akxpMGtrg/N9ZNfUkTGcg+aLLrvzu/lA11Rn6htfxAT2hljLGJpa7KyHk+4Q1od4GPIABnKbuIvINkumCWtJyKkmg/Ak02Ap1bzxA47v5UXSDmAC+cHPGNT4XC5eqZ+LrVxiRGR+dm7GvoADmLvw4Jhl647J147y2ADtuVfwunbmqE/68cjdZzBmUKeUt3JQ71Eq9y/zJ/QnzRjHWckg684/tc0tFk6AIqIw0J2n3/kNIIB+M1EvFNQQOfwS2fy4/RhzQ7sphJhlINojIXsFch8+QLaa0oaKrSLvvWukaebLvM+I+ywCxz7DYDQWgk1SwItbSaPo3sN7ZJfDTcO511qyC1Jm0hIYTSqXHwx+vZz/iR2xTVN2xY9I12WtYMOxnBslKNjw250KixJzDjGg0pwVjic6G7M1yYKIegR9tmoscxz87wV+SQQPB0MPgPHalP4UqoVImwHgZ1Nwc4H2rxUeYtY0BJc7ExKCh8YaviMv3pb158JTP+YuUBElqJP6c5ffCF1032Ryt7GKbKf1nccW8ZXqnhyLMSJS9SFwIfqJtc4ln23rg==";
		String initVector = "AaBbCcDd1234!@#$";
		String key = "WwXxYyZz1234!@#$";
		System.out.println(aesCBCEncrypt("{\"a\":1}", "utf-8", initVector, key));
		System.out.println(aesCBCDecrypt("U9yneHo/7YV+a1CRmNyjlg==", "utf-8", initVector, key));
		System.out.println(aesCBCDecrypt(extParam, "utf-8", initVector, key));

	}

	public static void main2(String[] args) throws UnsupportedEncodingException {
		String initVector = "AaBbCcDd1234!@#$";
		String key = "WwXxYyZz1234!@#$";
		int total = 100000;
		int run = 0;
		int count = 0;
		String[] strArr = new String[] { "//////\\\\+++、、", "+++、、//////\\++！", "++！@#%￥……，。、……&*（", "【】）&（\\\" + \"中国",
				"事实上", "&（\\\" + \"中国谁哟的事实上122你的", "12343567" };
		Random random = new Random(System.currentTimeMillis());
		for (int i = 0; i < total; i++) {
			run++;
			StringBuilder builder = new StringBuilder();
			builder.append(random.nextInt(strArr.length));
			builder.append(random.nextInt(strArr.length));
			builder.append(random.nextInt(strArr.length));
			builder.append(random.nextInt(strArr.length));
			builder.append(random.nextInt(strArr.length));
			byte[] original = builder.toString().getBytes("UTF-8");
			byte[] encrypted = aes128CBCEncrypt(original, key, initVector);
			byte[] decrypted = aes128CBCDecrypt(encrypted, key, initVector);

			if (equal(original, decrypted)) {
				count++;
			} else {
				System.out.println("字符串加解密后内容不一致");
				System.out.println("原始字符串： " + Arrays.toString(encrypted));
				System.out.println("解密字符串： " + Arrays.toString(decrypted));
			}
		}

		if (total == count) {
			System.out.println("测试全部通过");
		} else {
			System.out.println("加解密出现缺陷数据");
		}
		System.out.println("运行总次数是： " + run);

	}
}
