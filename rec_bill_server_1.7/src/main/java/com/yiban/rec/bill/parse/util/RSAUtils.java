package com.yiban.rec.bill.parse.util;


import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class RSAUtils {

	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";


    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
    public static void main(String[] args) {
    	Map<String, String> keyMap = RSAUtils.createKeys(1024);
        String  publicKey = keyMap.get("publicKey");
        publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6v5CO6av98q5CFxOh7wckytdtijoYb34VNEr3ZQ2I6LrUbhatCfUzqOFSc+VTDt6+soG9w29UI8SckfaFkw7+ee09e1voCu03XOwecck1JFePsbtZe/swJdZvi/fGfLICT4IwslGCx8ndg1Y3Oj2Z0zK7NyQht7UHdOG1y5ObdwIDAQAB";
        String  privateKey = keyMap.get("privateKey");
        privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALq/kI7pq/3yrkIXE6HvByTK122KOhhvfhU0SvdlDYjoutRuFq0J9TOo4VJz5VMO3r6ygb3Db1QjxJyR9oWTDv557T17W+gK7Tdc7B5xyTUkV4+xu1l7+zAl1m+L98Z8sgJPgjCyUYLHyd2DVjc6PZnTMrs3JCG3tQd04bXLk5t3AgMBAAECgYEAluPb0K1by8e/JyVwNoJk2kSPBjIOuNm1HTrwa66Z3gp1ktkUt2H5XnFRBgcgvxifv6OKEyMLHAf+f6escccd7/m33tFIfxsZcoPBi+NCFYPxJFLVu+qpnRKarZSUcPOAz0VI6rDPL732KqzPzGWYzButQrDUJHpwpsCVsI/CJykCQQD4H205OlwHms0OkywvZggP6vahibqMWgHb7vpzFpb8xtbILBFJhcQq5IJfItE5ZjSHrhiOq4+P2KUvSF1X1bIrAkEAwK1T/MTwzxUvA9pLYQYLvHybwFevElVwu5BZU6NDVLqEV6S2SPYhbwWjN/2nDxUzd/WsXsA4oMj1QNg2W3Mx5QJBAMSVgaAZ0NhlmZm2mQdku3aBHq6VxKt2lIqAKQYOG8pib8FgyMGLrgSdz304xLPJek0Vbnd7Dd9WMmnn16zDrskCQA8rlnlIaE1ltZXwEg9JkpL8nKbAQKCI6Z9a9i2HpT1+kfz0kwWbm3ZKU1eNOSIO4UaIqVGsgMjoCadZXq5Em6ECQHjkhuD69BGTo9W8rI8+f9Kmn/RBnBHaUGbfkrWsbliye4ajyZ1fQ8MIG5wHwkwK1T9ulSE4i7rIy7yV/b1Kqek=";
        System.out.println("公钥: \n\r" + publicKey);
        System.out.println("私钥： \n\r" + privateKey);

        System.out.println("公钥加密——私钥解密");
        String str = "站在大明门前守卫的禁卫军，事先没有接到\n" +
                "有关的命令，但看到大批盛装的官员来临，也就\n" +
                "以为确系举行大典，因而未加询问。进大明门即\n" +
                "为皇城。文武百官看到端门午门之前气氛平静，\n" +
                "城楼上下也无朝会的迹象，既无几案，站队点名\n" +
                "的御史和御前侍卫“大汉将军”也不见踪影，不免\n" +
                "心中揣测，互相询问：所谓午朝是否讹传？";
        System.out.println("\r明文：\r\n" + str);
        System.out.println("\r明文大小：\r\n" + str.getBytes().length);
        try {
        	 String encodedData = RSAUtils.publicEncrypt(str, RSAUtils.getPublicKey(publicKey));
        	 encodedData = "iDXZDJmJy3jkdBmvJJYoewKhWw6a9rn-szwHTVVU6VXETCOBo_23uE8m02NwaguuZMk3YeOmgnRWiu6sNnWuAlB6i5PSvJ5wfRmM2o0azObUobNY6Nf10SoOIWKsW-jfzrMmCPBTTmuz_F5dxIaPqfph-2lHuMH1WKSRMGeUZ0qONohQjkEfo30hiL06NzicPT02OiBwrxDqq1MKiT6iD5fCb8Faa-_zCEPKc-SccDtteK3khMKZU9W3tGdZGovM-m61jXBXo5OMb0LLCedqGJRindBziHLiWB7thEthqkbfoVGUdSD2IyNPMreIbijH4hoZwJfFjofikNKLgapFbQ";
             System.out.println("密文：\r\n" + encodedData);
             String decodedData = RSAUtils.privateDecrypt(encodedData, RSAUtils.getPrivateKey(privateKey));
             System.out.println("解密后文字: \r\n" + decodedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
