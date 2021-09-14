package com.yiban.rec.bill.parse.util;

import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.yiban.rec.bill.parse.service.standardbill.BillParseException;
import com.yiban.rec.domain.HisTransactionFlow;

/**
* @author lyx
* @version 创建时间：2019年3月17日 下午5:27:48
* 类说明
*/
public class RSAEncrypt {
	private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
	public static void main(String[] args) throws Exception {
		//生成公钥和私钥
		genKeyPair();
		//加密字符串
		String message = "<request><branchCode></branchCode><orderMode>0</orderMode><startTime>2019-03-11 00:00:00</startTime><endTime>2019-03-11 23:59:59</endTime><regMode>11</regMode><orderNo></orderNo></request>";
		String  publicKey = keyMap.get("publicKey");
        publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6v5CO6av98q5CFxOh7wckytdtijoYb34VNEr3ZQ2I6LrUbhatCfUzqOFSc+VTDt6+soG9w29UI8SckfaFkw7+ee09e1voCu03XOwecck1JFePsbtZe/swJdZvi/fGfLICT4IwslGCx8ndg1Y3Oj2Z0zK7NyQht7UHdOG1y5ObdwIDAQAB";
        String  privateKey = keyMap.get("privateKey");
        privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALq/kI7pq/3yrkIXE6HvByTK122KOhhvfhU0SvdlDYjoutRuFq0J9TOo4VJz5VMO3r6ygb3Db1QjxJyR9oWTDv557T17W+gK7Tdc7B5xyTUkV4+xu1l7+zAl1m+L98Z8sgJPgjCyUYLHyd2DVjc6PZnTMrs3JCG3tQd04bXLk5t3AgMBAAECgYEAluPb0K1by8e/JyVwNoJk2kSPBjIOuNm1HTrwa66Z3gp1ktkUt2H5XnFRBgcgvxifv6OKEyMLHAf+f6escccd7/m33tFIfxsZcoPBi+NCFYPxJFLVu+qpnRKarZSUcPOAz0VI6rDPL732KqzPzGWYzButQrDUJHpwpsCVsI/CJykCQQD4H205OlwHms0OkywvZggP6vahibqMWgHb7vpzFpb8xtbILBFJhcQq5IJfItE5ZjSHrhiOq4+P2KUvSF1X1bIrAkEAwK1T/MTwzxUvA9pLYQYLvHybwFevElVwu5BZU6NDVLqEV6S2SPYhbwWjN/2nDxUzd/WsXsA4oMj1QNg2W3Mx5QJBAMSVgaAZ0NhlmZm2mQdku3aBHq6VxKt2lIqAKQYOG8pib8FgyMGLrgSdz304xLPJek0Vbnd7Dd9WMmnn16zDrskCQA8rlnlIaE1ltZXwEg9JkpL8nKbAQKCI6Z9a9i2HpT1+kfz0kwWbm3ZKU1eNOSIO4UaIqVGsgMjoCadZXq5Em6ECQHjkhuD69BGTo9W8rI8+f9Kmn/RBnBHaUGbfkrWsbliye4ajyZ1fQ8MIG5wHwkwK1T9ulSE4i7rIy7yV/b1Kqek=";
		System.out.println("随机生成的公钥为:" + publicKey);
		System.out.println("随机生成的私钥为:" + privateKey);
		String messageEn = encrypt(message,publicKey);
		
		messageEn = "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";
		
		System.out.println(message);
		System.out.println("加密后的字符串为:" + messageEn);
		String messageDe = decrypt(messageEn,privateKey);
		System.out.println("还原后的字符串为:" + messageDe);
		String result = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soap:Body><ns1:getOrderRecordsResponse xmlns:ns1=\"http://service.ws.bsoft.com\"><ns1:out>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</ns1:out></ns1:getOrderRecordsResponse></soap:Body></soap:Envelope>";
		result = result.substring(result.indexOf("<ns1:out>")+9, result.indexOf("</ns1:out>"));
		System.out.println(result);
		result = RSAEncrypt.decrypt(result, privateKey);
		result = result.replaceAll("&lt;", "<");
		result = result.replaceAll("&gt;", ">");
		result = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + result;
		System.out.println("返回参数：" + result);
		List<HisTransactionFlow> list = analysisBill(result);
		System.out.println(list.size());
	}
	
	/**
	 * 解析接口
	 */
	private static List<HisTransactionFlow> analysisBill(String xml) throws BillParseException {
		List<HisTransactionFlow> list = new ArrayList<HisTransactionFlow>(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement(); // 获取根节点
			System.out.println("返回参数根节点：" + rootElt.toString());
			String result = rootElt.elementTextTrim("resultCode");
			if (StringUtils.isNotBlank(result) && result.equals("0")) {
				// 请求成功
				Iterator<?> iter = rootElt.element("result").elementIterator("orderRecord");
				while (iter.hasNext()) {
					HisTransactionFlow hisVo = new HisTransactionFlow();
					Element itemEle = (Element) iter.next();
					// 支付业务类型
					String productType = itemEle.elementTextTrim("orderMode");
					hisVo.setPayBusinessType(
							StringUtils.isNotBlank(productType)
									? productType
											.equals("1")
													? "0451"
													: productType.equals("2") ? "0851"
															: productType.equals("3") ? "0151"
																	: productType.equals("4") ? "0751" : "0551"
									: productType);
					// 交易时间
					String pamentDate = itemEle.elementTextTrim("tradeTime");
					hisVo.setTradeDatatime(sdf.parse(pamentDate));
					// 机构
					String orgCode = itemEle.elementTextTrim("branchCode");
					hisVo.setOrgNo(orgCode);
					// 患者姓名
					String name = itemEle.elementTextTrim("patName");
					hisVo.setCustName(name);
					// 身份证号
					String cardNo = itemEle.elementTextTrim("patIdNo");
					hisVo.setCredentialsNo(cardNo);
					// 支付方式

					String payMethod = itemEle.elementTextTrim("tradeType");
					hisVo.setPayType(StringUtils.isNotBlank(payMethod)
							? payMethod.equals("2") ? EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue()
									: payMethod.equals("3") ? EnumTypeOfInt.PAY_TYPE_WECHAT.getValue()
											: payMethod.equals("1") ? EnumTypeOfInt.CASH_PAYTYPE.getValue()
													: EnumTypeOfInt.PAY_TYPE_BANK.getValue()
							: payMethod);

					// 支付流水号
					String payNumber = itemEle.elementTextTrim("hisOrderNo");
					hisVo.setPayFlowNo(payNumber);
					// 退费缴费状态
					String payStatus = itemEle.elementTextTrim("tradeType");
					hisVo.setOrderState(StringUtils.isNotBlank(payStatus)
							? payStatus.equals("1") ? EnumTypeOfInt.TRADE_TYPE_PAY.getValue()
									: payStatus.equals("2") ? EnumTypeOfInt.TRADE_TYPE_REFUND.getValue() : "0"
							: EnumTypeOfInt.TRADE_TYPE_PAY.getValue());
					// 金额
					String payAmount = "";
					if (StringUtils.isNotBlank(hisVo.getOrderState())
							&& hisVo.getOrderState().equals(EnumTypeOfInt.TRADE_TYPE_PAY.getValue())) {// 缴费
						payAmount = itemEle.elementTextTrim("payTotalFee");
					} else if (StringUtils.isNotBlank(hisVo.getOrderState())
							&& hisVo.getOrderState().equals(EnumTypeOfInt.TRADE_TYPE_REFUND.getValue())) {// 退费
						payAmount = itemEle.elementTextTrim("refundTotalFee");
					} else {
						payAmount = itemEle.elementTextTrim("payTotalFee");
					}
					hisVo.setPayAmount(
							StringUtils.isNotBlank(payAmount) ? new BigDecimal(payAmount) : new BigDecimal(0));
					// 账单来源
					hisVo.setBillSource("self");
					list.add(hisVo);
				}
			} else {// 请求失败
				throw new BillParseException("拉取his账单webservcie请求失败");
			}
		} catch (Exception e) {
			throw new BillParseException(e.getMessage());
		}
		return list;
	}
	
	

	/** 
	 * 随机生成密钥对 
	 * @throws NoSuchAlgorithmException 
	 */  
	public static void genKeyPair() throws NoSuchAlgorithmException {  
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
		// 初始化密钥对生成器，密钥大小为96-1024位  
		keyPairGen.initialize(1024,new SecureRandom());  
		// 生成一个密钥对，保存在keyPair中  
		KeyPair keyPair = keyPairGen.generateKeyPair();  
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥  
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥  
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));  
		// 得到私钥字符串  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));  
		// 将公钥和私钥保存到Map
		keyMap.put(0,publicKeyString);  //0表示公钥
		keyMap.put(1,privateKeyString);  //1表示私钥
	}  
	/** 
	 * RSA公钥加密 
	 *  
	 * @param str 
	 *            加密字符串
	 * @param publicKey 
	 *            公钥 
	 * @return 密文 
	 * @throws Exception 
	 *             加密过程中的异常信息 
	 */  
	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		String outStr = null;
		byte[] inputArray = str.getBytes("UTF-8");
		int inputLength = inputArray.length;
		System.out.println("加密字节数：" + inputLength);
		// 最大加密字节数，超出最大字节数需要分组加密
		int MAX_ENCRYPT_BLOCK = 117;
		// 标识
		int offSet = 0;
		byte[] resultBytes = {};
		byte[] cache = {};
		while (inputLength - offSet > 0) {
			if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
				offSet += MAX_ENCRYPT_BLOCK;
			} else {
				cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
				offSet = inputLength;
			}
			resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
			System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
		}
		outStr = Base64.encodeBase64String(resultBytes);
		return outStr;
	}

	/** 
	 * RSA私钥解密
	 *  
	 * @param str 
	 *            加密字符串
	 * @param privateKey 
	 *            私钥 
	 * @return 铭文
	 * @throws Exception 
	 *             解密过程中的异常信息 
	 */  
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
//		String outStr = new String(cipher.doFinal(inputByte));
		String outStr = null;
		byte[] inputArray = Base64.decodeBase64(str.getBytes("UTF-8"));
		int inputLength = inputArray.length;
		System.out.println("加密字节数：" + inputLength);
		// 最大加密字节数，超出最大字节数需要分组加密
		int MAX_ENCRYPT_BLOCK = 128;
		// 标识
		int offSet = 0;
		byte[] resultBytes = {};
		byte[] cache = {};
		while (inputLength - offSet > 0) {
			if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
				offSet += MAX_ENCRYPT_BLOCK;
			} else {
				cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
				offSet = inputLength;
			}
			resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
			System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
		}
		outStr = new String(resultBytes,"UTF8");
		return outStr;
	}

}
