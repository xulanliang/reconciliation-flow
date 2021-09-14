package com.yiban.framework.core.util;

import org.springside.modules.security.utils.Digests;
import org.springside.modules.utils.Encodes;

import com.google.common.base.Charsets;

/**
 * @author swing
 * @date 2018年1月22日 下午1:56:16 类说明
 */
public class PasswordUtil {
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	public final static byte[] createSalt() {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		return salt;
	}
	public final static String encodeSalt(byte[] salt){
		return Encodes.encodeHex(salt);
	}
	
	public final static byte[] decodeSalt(String salt){
		return Encodes.decodeHex(salt);
	}

	public final static String entryptPassword(String plainPassword){
		byte[] salt =PasswordUtil.createSalt();
		return PasswordUtil.entryptPassword(plainPassword, salt);
		
	}
	public final static String entryptPassword(String plainPassword, byte[] salt) {
		byte[] hashPassword = Digests.sha1(plainPassword.getBytes(Charsets.UTF_8), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(hashPassword);
	}
}
