package com.yiban.rec.bill.parse.util;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author Administrator
 *
 */
public class AES {

    // 加密
    public static String Encrypt(String sSrc, String sKey) {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw;
		try {
			raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

	        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "22ADB1992144D4D3";
        // 需要加密的字串
        String cSrc = "<ZFPT>银行卡</ZFPT>\n<PAYTYPE>1</PAYTYPE>\n<KSSJ>2019-03-01</KSSJ>\n<JSSJ>2019-03-19</JSSJ>";
        System.out.println(cSrc);
        // 加密
        String enString = AES.Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);

        // 解密
        String DeString = AES.Decrypt("6ZQP+DOk/7vd65/XqDmJmtgR5R+fciKctSNVfhU80JSyM43OgmR8UVFnz+KAsX0VQDNsNmHQXM97ItLHcM10qADs1aevCdPyfVa98FtKLHZrMpbJ1g87a5VuvTDyYQDMmp6/2gNkrTirDvjRJUdcmto93RFIHx3gcg8Y1i5zWIPstKsyHi1fyzt8ZYtbcyN608A9cFotJ3odgxfRgd5QwuosoTZ4G3s574a5ky+1gW5GngQ1Gge/60n5Bj/aw6+eootYU0wc9/XPoU+/RU6jRpjVCY81qR7dQ11Fu3aZz5TkQWSeJTdKVybH0YsQP9GyekRnDoYJLbVT6+rFN9I6dFNDCglh4MfVArr0zUVO6MjhEKuuhPlZZofrXWtdC+D+apIgkDswFFwYfpgOY90vBj9LR2QGSmhL0bdHRLRnBwfJNRIQCpQYf62TA37dZ2vBDTsoIV78hhlgtux6JUdBDI+1ohQeIk1nEgivLSl+SQuAZ4sZZtBuaC1ceZ5qdaTv3L3j0mlzg+fCBUBFG+alO49ET5RdoML9LdH8XMeifk8xdoV1UR0Gdfsyz3nRftqXPBC7G3SHJDc9mJdWkN3zWks9WPGsYoGqEYQtQUU6+a8SupfGO+/TS45MzEZld33mQdNOwJC3Q6qjN19LtW8ap1w5HW/iP/N4tKdyXZeVeLUMvRQBPp42KnR4u5X1cNallVGaOrjQUs/IMFHqliy3OQ==", cKey);
        System.out.println("解密后的字串是：" + DeString);
    }
}

//源代码片段来自云代码http://yuncode.net