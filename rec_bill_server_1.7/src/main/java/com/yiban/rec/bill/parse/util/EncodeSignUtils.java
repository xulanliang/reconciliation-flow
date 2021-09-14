package com.yiban.rec.bill.parse.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class EncodeSignUtils {

    private static final String ENCODING = "UTF-8";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * SHA256WithRSA 签名
     */
    public static String sign256(String data, String privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(getPrivateKey(privateKey));
            signature.update(data.getBytes(ENCODING));
            return Base64.encodeBase64String(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SHA256WithRSA 验证签名
     */
    public static boolean verify256(String data, String sign, String publicKey) {
        if (data == null || StringUtils.isBlank(sign) || StringUtils.isBlank(publicKey)) {
            return false;
        }
        try {
            Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
            signetcheck.initVerify(getPublicKey(publicKey));
            signetcheck.update(data.getBytes(ENCODING));
            return signetcheck.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicKey getPublicKey(String key) {
        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            return keyFactory.generatePublic(bobPubKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static PrivateKey getPrivateKey(String key) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            // 指定加密算法
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // 取私钥匙对象
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成3DES密钥
     *
     * @param length
     * @return
     */
    public static String generateString(int length) {
        final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(generateString(24));
    }

}
