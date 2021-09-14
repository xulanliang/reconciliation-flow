package com.yiban.rec.bill.parse.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.util.IOUtils;

import net.sf.json.JSONObject;

public class MD5Util {

	private static MessageDigest MD5;
	static {
		try {
			MD5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String getMd5(File file){
		byte[]buffer = new byte[4096];
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
			while((postion = inputStream.read(buffer))> 0){
				md5.update(buffer, 0, postion);
			}
			return CodingUtil.bytesToHexString(md5.digest());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			IOUtils.close(inputStream);
		}
		return "";
	}
	
	public static String getMd5(InputStream input){
		byte[]buffer = new byte[4096];
		int postion = -1;
		try {
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
			while((postion = input.read(buffer))> 0){
				md5.update(buffer, 0, postion);
			}
			return CodingUtil.bytesToHexString(md5.digest());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			IOUtils.close(input);
		}
		return "";
	}
	
	public static synchronized String getMd5(String msg) {
		return getMd5(msg.getBytes());
	}

	public static String getMd5(String source, String key){
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
		
		
		Arrays.fill(k_ipad, keyb.length, 64, (byte)0x36);
		Arrays.fill(k_opad, keyb.length, 64, (byte)0x5c);
		
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
		byte[]md5Byte = MD5.digest(msg);
		byte[]result = new byte[8];
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
	
	public static void main(String[] args) throws Exception {
		/*<?xml version="1.0" encoding="UTF-8"?>
		<request>
			<sign></sign>
			<cmd>2000004</cmd>
			<fixId>457</fixId>
			<dealID>202103181022060085688</dealID>
			<jbr>ZZJ006</jbr>
			<serialID>20210318101942ZZJ006</serialID>
			<cardID>A61313142</cardID>
			<perCardID>412702198506306011</perCardID>
			<perName>朱雪葵</perName>
			<sendFlag>0</sendFlag>
		</request>*/
		String tt="{\n" + 
				"		\"Tolls\": {\n" + 
				"			\"mztoll\": {\n" + 
				"				\"dealID\": \"202103181022060085688\",\n" + 
				"				\"jbr\": \"ZZJ006\",\n" + 
				"				\"serialID\": \"20210318101942ZZJ006\"\n" + 
				"			}\n" + 
				"		},\n" + 
				"		\"cardID\": \"A61313142\",\n" + 
				"		\"perCardID\": \"412702198506306011\",\n" + 
				"		\"perName\": \"朱雪葵\"\n" + 
				"	}";
		String bb="{\n" + 
				"		\"sendFlag\": \"0\"\n" + 
				"	}";
		JSONObject aa = JSONObject.fromObject(tt);
		JSONObject cc = JSONObject.fromObject(bb);
		Map<String, String> jsonMap=new HashMap<String, String>();
		jsonMap.put("cmd", "2000004");
		jsonMap.put("fixId", "457");
		jsonMap.put("param", aa.toString());
		jsonMap.put("extendparams", cc.toString());
		System.out.println(generateSignature(jsonMap,null));
	}
	
	/**
     * 生成签名
     *
     * @param data 待签名数据
     * @param key API密钥
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key) throws Exception {
        return generateSignature(data, key, "MD5");
    }

    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data 待签名数据
     * @param key API密钥
     * @param signType 签名方式
     * @return 签名
     */
    public static String generateSignature(final Map<String, String> data, String key, String signType) throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals("sign")) {
                continue;
            }
            if(sb.length()==0) {
            	sb.append(k).append("=").append(data.get(k).trim());
            }else if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append("&").append(k).append("=").append(data.get(k).trim());
        }
        if(StringUtils.isNotBlank(key)) {
        	sb.append("key=").append(key);
        }
        System.out.println(sb.toString());
        if ("MD5".equals(signType)) {
            return MD5(sb.toString()).toUpperCase();
        }
        else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }
    
    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
	
	/*public  static void main(String[] args){
		System.out.println(MD5Util.getMd5("cmbc510623").toUpperCase());
	}*/
}
