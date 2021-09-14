package com.yiban.rec.bill.parse.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import net.sf.json.JSONObject;

public class RSA2SignUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSA2SignUtil.class);

	private static final String SIGNATURE_KEY = "sign";
	private static final String RSA = "SHA1WithRSA";
	private static final String RSA2 = "SHA256WithRSA";

	/**
	 * @description 拼接原始数据 将 bean 转化为 Map String String localDateTime yyyy-MM-dd
	 *              HH:mm:ss BigDecimal #0.00
	 */
	private static String link(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return StringUtils.EMPTY;
		}

		StringBuilder sb = new StringBuilder();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			Object obj = params.get(key);
			if (StringUtils.equalsIgnoreCase(SIGNATURE_KEY, key)) {
				continue;
			}
			if (obj == null) {
				continue;
			}
			String value = obj instanceof String ? (String) obj
					: JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss", SerializerFeature.MapSortField);

			if (StringUtils.isBlank(value)) {
				continue;
			}
			sb.append('&').append(key).append('=').append(value);
		}
		if (sb.length() <= 0) {
			return StringUtils.EMPTY;
		}

		return sb.deleteCharAt(0).toString();
	}

	/**
	 * @description 加签
	 */
	public static String signRsa(Map<String, Object> map, String privateKey) {
		String source = link(map);
		LOGGER.info("原始数据(加签)[{}]", source);
		if (StringUtils.isBlank(source)) {
			return StringUtils.EMPTY;
		}

		return sign(source, privateKey.getBytes(), "RSA2");
	}

	/**
	 * @description 验签
	 */
	public static boolean verifyRsa(Map<String, Object> map, String publicKey) {
		if (null == map) {
			return false;
		}

		String sign = map.get(SIGNATURE_KEY).toString();
		if (StringUtils.isBlank(sign)) {
			LOGGER.error("sign为空");
		}
		String content = link(map);
		LOGGER.info("原始数据(验签)[{}]", content);

		return verify(content, sign, publicKey, "RSA2");
	}

	/**
	 * RSA签名
	 */
	private static String sign(String content, byte[] privateKey, String encrypt) {
		Security.addProvider(new BouncyCastleProvider());
		InputStream ins = new ByteArrayInputStream(privateKey);
		Reader input = null;
		PKCS8EncodedKeySpec pkcs8KeySpec;
		Signature signature;
		try {
			input = new InputStreamReader(ins);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// 实例化Signature
			switch (encrypt.toUpperCase()) {
			case "RSA":
				pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(readText(ins).getBytes()));
				signature = Signature.getInstance(RSA);
				break;
			case "RSA2":
				pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(readText(ins).getBytes()));
				signature = Signature.getInstance(RSA2);
				break;
			default:
				throw new RuntimeException("加密算法枚举不存在");
			}
			// 生成私钥
			PrivateKey key = keyFactory.generatePrivate(pkcs8KeySpec);
			// 初始化Signature
			signature.initSign(key);
			// 更新
			signature.update(content.getBytes(StandardCharsets.UTF_8));
			byte[] signed = signature.sign();
			return Base64.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				ins.close();
				if (input != null) {
					input.close();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * RSA验签
	 */
	private static boolean verify(String content, String sign, String publicKey, String encrypt) {
		byte[] encodedKey;
		Signature signature;
		try {
			switch (encrypt.toUpperCase()) {
			case "RSA": {
				encodedKey = Base64.decodeBase64(publicKey);
				signature = Signature.getInstance(RSA);
				break;
			}
			case "RSA2": {
				encodedKey = Base64.decodeBase64(publicKey);
				signature = Signature.getInstance(RSA2);
				break;
			}
			default:
				throw new RuntimeException("加密算法枚举不存在");
			}
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			signature.initVerify(pubKey);
			signature.update(content.getBytes(StandardCharsets.UTF_8));

			return signature.verify(Base64.decodeBase64(sign));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static String readText(InputStream in) throws IOException {
		Reader reader = new InputStreamReader(in);
		StringWriter writer = new StringWriter();
		char[] buffer = new char[4096];
		int amount;
		while ((amount = reader.read(buffer)) >= 0) {
			writer.write(buffer, 0, amount);
		}
		return writer.toString();
	}

	public static void main(String[] args) {
		predownload();
//		download();

	}

	public static void download() {
		String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDAjuiakVFG0Zrc6awXV2D4Lod/I6nK5Qp9/VpymwWcQhTyFGkaZbwp42B3+zBh24tYZxZqifoJdtyN+3/Otqj/Cc/tnaMMllJP/G4cWjZyl9UIqy3PNEftIdF4ZS+sdc1xPb/caTZvvjM01Nf+n7OlMf3zCxYxm3mtQXy7qULtOGRAnQNtYZDRK74p5/iqXVn+tAcdL3AkXoioLhNU0iRNdtwEGRLRbNs18m1GnfcBwVlRwYjzGPsH305ODyyZvlngoNyxNi3y44c9FIq9+frSlUpJw8AHiD5xjielCkcpRlanAaWiUiD/73h4LlLkhQF/YquB+HBn6bagDmPJsawxAgMBAAECggEAds31f6BXKD2csd5OelxEDPwQR4ZGdVOZhKBzVLCG5qDy8WVcb5gDX0jVtVE6ybW+JL3925/jsEEw1T0t3uleHXT9YBrZhaOdh5I0kbOrUkqReva6ndQg6JLr/b9YiaxpJAw4OxKDLrCIQXltBsGcAjl0+KaHe/X0Pcxg3BwTme4ViYZ7j4KIM7yElfW2D+8/YNwLyk71hCuK853bwc2uiTcs5eHFqkbsGBgkEIK/VTevurw/uQfdTQWqBwMZBG9NZ1a7WprpgCniNQVQ8/TPu+OVQLhxxG9PTcPBEFnEt6O2yL4VOcNiVEj0ox+AXx9O7Fb+nzPPA3JRzaKwb9NW0QKBgQDs0W7bV0vgYo4LKrKzNjytAdFUVjJcbCTF8xzl21+p7T3zhLQHr50l3SDv9uHTk5ezOd0Zd53mW/mWXjlcpiBKW5TuyxFSXPg9frfvAFfXq7M5S2VqnEymNsqA1gUxiQJbVtMZbQwdSQrAhP0QknKTLgxLoNbweveEMunmJHoVZQKBgQDQJ7hyg+q6U3NpUxtJwtQEJeigY6+kUOAmEtuh+3rYfTIJxhckPbi/0Y1FNp7VHt1sDzGG5+RDrLU79/uLt6wNsTl4n//e2h2kx6CSywmyp9V6qjd+zM7W/v9zTDtnwegTeFDZnRtXjBlx4fVJi/wrmjDBTUh8TXMd2BQwCO8k3QKBgAZjgAIa0Mk033AdrsR26hwguGz2BlXH9RYCAP2SdXo4FDvFkLXEXxDubj0LA/yZNrZ3ESfmujPEt27/C70cHjLjVZk7kTQPngLKsFuaPsRqtWMOTaYaoVa6k6v2Gh3D/HRKW1eMQY9osQmQjOMtkDm4PEyuAh5qstK2LOkwgrqhAoGADXeEZ2RBe2yKuvW1fkAsl1gchMPuSSMyXUQ7EW/Dst3mrtsyBBY6La7RBPlTrVYh3SveeGvoSLiTwR16GL/5Ual1nmg7lZ9m65I+wUCBkB1rolQA4yM8/ovg2wTDkyLocNoH4QxZCID35HZJ+EgmOGvxW5A0cFZSbEvegn0Re8ECgYByECm645UvH0op2PgwEA2FVKzhxACO7mjSMOIjKhFAuFtmbm/eDHJvD7wDvGuAkn0Wgp3UG7p+8KSD/1BMdQfRvyB8JX3Z1EweNfj0KjyixPyVi8E7Oqb3X26BLuFTjrtSDverl6H6/T8CpQnrqkU0XZQe81O7WOcRT8AmM7bMWg==";

		Map<String, Object> jo = new HashMap<String, Object>();
		jo.put("billsNo", "1");
		jo.put("isvId", 20004);
		jo.put("partnerId", 100215);
		jo.put("version", "1.0.0");
		jo.put("timestamp", System.currentTimeMillis());
		String sign = RSA2SignUtil.signRsa(jo, privateKey);
		jo.put("sign", sign);

		System.out.println(JSONObject.fromObject(jo));
	}

	public static void predownload() {
		String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDAjuiakVFG0Zrc6awXV2D4Lod/I6nK5Qp9/VpymwWcQhTyFGkaZbwp42B3+zBh24tYZxZqifoJdtyN+3/Otqj/Cc/tnaMMllJP/G4cWjZyl9UIqy3PNEftIdF4ZS+sdc1xPb/caTZvvjM01Nf+n7OlMf3zCxYxm3mtQXy7qULtOGRAnQNtYZDRK74p5/iqXVn+tAcdL3AkXoioLhNU0iRNdtwEGRLRbNs18m1GnfcBwVlRwYjzGPsH305ODyyZvlngoNyxNi3y44c9FIq9+frSlUpJw8AHiD5xjielCkcpRlanAaWiUiD/73h4LlLkhQF/YquB+HBn6bagDmPJsawxAgMBAAECggEAds31f6BXKD2csd5OelxEDPwQR4ZGdVOZhKBzVLCG5qDy8WVcb5gDX0jVtVE6ybW+JL3925/jsEEw1T0t3uleHXT9YBrZhaOdh5I0kbOrUkqReva6ndQg6JLr/b9YiaxpJAw4OxKDLrCIQXltBsGcAjl0+KaHe/X0Pcxg3BwTme4ViYZ7j4KIM7yElfW2D+8/YNwLyk71hCuK853bwc2uiTcs5eHFqkbsGBgkEIK/VTevurw/uQfdTQWqBwMZBG9NZ1a7WprpgCniNQVQ8/TPu+OVQLhxxG9PTcPBEFnEt6O2yL4VOcNiVEj0ox+AXx9O7Fb+nzPPA3JRzaKwb9NW0QKBgQDs0W7bV0vgYo4LKrKzNjytAdFUVjJcbCTF8xzl21+p7T3zhLQHr50l3SDv9uHTk5ezOd0Zd53mW/mWXjlcpiBKW5TuyxFSXPg9frfvAFfXq7M5S2VqnEymNsqA1gUxiQJbVtMZbQwdSQrAhP0QknKTLgxLoNbweveEMunmJHoVZQKBgQDQJ7hyg+q6U3NpUxtJwtQEJeigY6+kUOAmEtuh+3rYfTIJxhckPbi/0Y1FNp7VHt1sDzGG5+RDrLU79/uLt6wNsTl4n//e2h2kx6CSywmyp9V6qjd+zM7W/v9zTDtnwegTeFDZnRtXjBlx4fVJi/wrmjDBTUh8TXMd2BQwCO8k3QKBgAZjgAIa0Mk033AdrsR26hwguGz2BlXH9RYCAP2SdXo4FDvFkLXEXxDubj0LA/yZNrZ3ESfmujPEt27/C70cHjLjVZk7kTQPngLKsFuaPsRqtWMOTaYaoVa6k6v2Gh3D/HRKW1eMQY9osQmQjOMtkDm4PEyuAh5qstK2LOkwgrqhAoGADXeEZ2RBe2yKuvW1fkAsl1gchMPuSSMyXUQ7EW/Dst3mrtsyBBY6La7RBPlTrVYh3SveeGvoSLiTwR16GL/5Ual1nmg7lZ9m65I+wUCBkB1rolQA4yM8/ovg2wTDkyLocNoH4QxZCID35HZJ+EgmOGvxW5A0cFZSbEvegn0Re8ECgYByECm645UvH0op2PgwEA2FVKzhxACO7mjSMOIjKhFAuFtmbm/eDHJvD7wDvGuAkn0Wgp3UG7p+8KSD/1BMdQfRvyB8JX3Z1EweNfj0KjyixPyVi8E7Oqb3X26BLuFTjrtSDverl6H6/T8CpQnrqkU0XZQe81O7WOcRT8AmM7bMWg==";
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjhMLNrIkzLfsMwUgBwtEukIwca7oU5lS58eHdOET1yt4gnFiL7uC2m4mLiQAhYkEFKk+0GicwXJ9aXp0YNcMMISXYwotcWYquoXvjpAqDvLTx4VvS1F/tgHgiQrIOXD8bUC2IOkiZPCa385Bbsp2OZFSzvxQ0x9uXmJaQgXXO4fe3Sy+Kq0PdKF9Dmex9JzI9nxTvaS63gcAAF/5pVCYLyW5zCzl6u2XBJtYuzEUcI9+MgYlZKHsvUFo//zVG6dQL7KxXL2LEP/ioXP/ClSx3b4nFClRjfHVJtgAgvC3k9wsQpp1cjRDKcHiNW39xn3rPbx59HgNDT2MSr10PH+EvwIDAQAB";

		Map<String, Object> jo = new HashMap<String, Object>();
		jo.put("billsNo", "1");
		jo.put("isvId", 20004);
		jo.put("startTime", "2020-05-08 00:00:00");
		jo.put("partnerId", 100215);
		jo.put("endTime", "2020-05-08 23:59:59");
		jo.put("version", "1.0.0");
		jo.put("timestamp", System.currentTimeMillis());
		String sign = signRsa(jo, privateKey);
		System.out.println(sign);
		jo.put("sign", sign);

		System.out.println(JSONObject.fromObject(jo));

		System.out.println(verifyRsa(jo,
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwI7ompFRRtGa3OmsF1dg+C6HfyOpyuUKff1acpsFnEIU8hRpGmW8KeNgd/swYduLWGcWaon6CXbcjft/zrao/wnP7Z2jDJZST/xuHFo2cpfVCKstzzRH7SHReGUvrHXNcT2/3Gk2b74zNNTX/p+zpTH98wsWMZt5rUF8u6lC7ThkQJ0DbWGQ0Su+Kef4ql1Z/rQHHS9wJF6IqC4TVNIkTXbcBBkS0WzbNfJtRp33AcFZUcGI8xj7B99OTg8smb5Z4KDcsTYt8uOHPRSKvfn60pVKScPAB4g+cY4npQpHKUZWpwGlolIg/+94eC5S5IUBf2KrgfhwZ+m2oA5jybGsMQIDAQAB"));

		System.out.println(verifyRsa(jo, publicKey));
	}

}