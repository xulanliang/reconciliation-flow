package com.yiban.rec.bill.parse.util;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @Author: XuYang
 * @Description: 江苏省区域支付平台3des加密工具
 * @Date: Create in 9:26 2018/10/16
 */
public class Des3Utils {

    /**
     * 密钥进行MD5加密取前24位(进行MD5加密的好处：
     * 1、密钥长度不够会自动填充为32位
     * 2、如果密钥相同，进行MD5以后得到的密钥也不会相同)
     *
     * @param key
     */
    private static byte[] hex(String key) throws UnsupportedEncodingException {
        return Arrays.copyOfRange(DigestUtils.md5Hex(key).getBytes("UTF-8"), 0, 24);
    }

    /**
     * 3DES加密
     *
     * @param key  密钥，24位
     * @param data 将加密的字符串
     */
    public static String encode3Des(String key, String data) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(hex(key), "DESede");
            // 加密
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return Base64.encodeBase64String(c1.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 3DES解密
     *
     * @param key  加密密钥，长度为24字节
     * @param data 解密后的字符串
     */
    public static String decode3Des(String key, String data) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(hex(key), "DESede");
            // 解密
            Cipher c1 = Cipher.getInstance("DESede");
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return new String(c1.doFinal(Base64.decodeBase64(data)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
