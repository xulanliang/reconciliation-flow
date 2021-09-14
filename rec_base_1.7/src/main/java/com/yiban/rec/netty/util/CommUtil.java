package com.yiban.rec.netty.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

public class CommUtil {
	private static final String DEFAULT_ENCODE = "UTF-8";
	private static Logger log = Logger.getLogger(CommUtil.class);

	public static String getIdArrFieldString(long[] idArr) {
		String arrStr = "";
		if (null == idArr) {
			return arrStr;
		}
		for (long obj : idArr) {
			arrStr += obj + "\n";
		}
		return arrStr;
	}

	public static long[] getIdArrField(ByteBuffer bytBuff) {
		long[] rslt = null;
		int len = bytBuff.getShort() & 0xffff;
		rslt = new long[len];
		for (int i = 0; i < len; i++) {
			rslt[i] = bytBuff.getLong();
		}
		return rslt;
	}

	

	public static void putArrTypeField(byte[] arr, ByteBuffer bytBuff) {
		if (null == arr) {
			//log.warn("params is null.");
			bytBuff.putShort((short) 0);
			return;
		}
		short len = (short) arr.length;
		bytBuff.putShort(len);
		bytBuff.put(arr);
	}

	public static void putArrTypeField(int[] arr, ByteBuffer bytBuff) {
		if (null == arr) {
			//log.warn("params is null.");
			bytBuff.putShort((short) 0);
			return;
		}
		short len = (short) arr.length;
		bytBuff.putShort(len);
		for (int i = 0; i < len; i++) {
			bytBuff.putInt(arr[i]);
		}
	}

	public static void putArrTypeField(String strField, ByteBuffer bytBuff,
			StringEncode encode) {
		byte[] arr;
		try {
			if (null == strField) {
				//log.warn("params is null.");
				bytBuff.putShort((short) 0);
				return;
			}
			arr = strField.getBytes(encode.getEncoder());
			short len = (short) arr.length;
			bytBuff.putShort(len);
			bytBuff.put(arr);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void putArrTypeFieldN(String strField, ChannelBuffer bytBuff,
			StringEncode encode) {
		byte[] arr;
		try {
			if (null == strField) {
				//log.warn("params is null.");
				bytBuff.writeShort((short) 0);
				return;
			}
			arr = strField.getBytes(encode.getEncoder());
			short len = (short) arr.length;
			bytBuff.writeShort(len);
			bytBuff.writeBytes(arr);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
	}
	public static void putArrTypeFieldNINT(String strField, ChannelBuffer bytBuff,
										StringEncode encode) {
		byte[] arr;
		try {
			if (null == strField) {
				//log.warn("params is null.");
				bytBuff.writeInt(0);
				return;
			}
			arr = strField.getBytes(encode.getEncoder());
			int len =  arr.length;
			bytBuff.writeInt(len);
			bytBuff.writeBytes(arr);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
	}
	public static void putStringToByteBuffer(String strField,
			ByteBuffer bytBuff, int len) {
		byte[] arr;
		try {
			if (null == strField) {
				//log.warn("params is null.");
				bytBuff.put(new byte[len]);
				return;
			}
			arr = strField.getBytes(DEFAULT_ENCODE);
			bytBuff.put(arr, 0, len);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void putArrTypeField(String[] strArr, ByteBuffer bytBuff,
			StringEncode encode) {
		try {
			if (null == strArr) {
				bytBuff.putShort((short) 0);
				return;
			}
			short len = (short) strArr.length;
			bytBuff.putShort(len);
			byte[] arr;
			for (int i = 0; i < len; i++) {
				arr = strArr[i].getBytes(encode.getEncoder());
				short strLen = (short) arr.length;
				bytBuff.putShort(strLen);
				bytBuff.put(arr);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void putArrTypeField(long[] idArr, ByteBuffer bytBuff) {
		if (null == idArr) {
			//log.warn("params is null.");
			bytBuff.putShort((short) 0);
			return;
		}
		short len = (short) idArr.length;
		bytBuff.putShort(len);
		for (int i = 0; i < len; i++) {
			bytBuff.putLong(idArr[i]);
		}
	}

	public static byte[] getByteArrField(ByteBuffer bytBuff) {
		byte[] rslt = null;
		int len = bytBuff.getShort() & 0xffff;
		if (log.isInfoEnabled()) {
			//log.info("len->" + len);
		}
		if (0 < len) {
			rslt = new byte[len];
			bytBuff.get(rslt);
		}
		return rslt;
	}

	public static byte[] getByteArrByByteBuffer(ByteBuffer bytBuff, int len) {
		byte[] rslt = null;
		if (log.isInfoEnabled()) {
			//log.info("len->" + len);
		}
		if (0 < len) {
			rslt = new byte[len];
			bytBuff.get(rslt);
		}
		return rslt;
	}

	

	public static String getStringField(ByteBuffer bytBuff, StringEncode encode) {
		String rslt = "";
		try {
			int len = bytBuff.getShort() & 0xffff;
			if (log.isInfoEnabled()) {
				//log.info("len->" + len);
			}
			if (0 < len) {
				byte[] arr = new byte[len];
				bytBuff.get(arr);
				rslt = new String(arr, encode.getEncoder());
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return rslt;
	}
	
	public static String getStringFieldN(ChannelBuffer bytBuff, StringEncode encode) {
		String rslt = "";
		try {
			int len = bytBuff.readShort() & 0xffff;
			if (log.isInfoEnabled()) {
				//log.info("len->" + len);
			}
            if (0 < len) {
				byte[] arr = new byte[len];
				bytBuff.readBytes(arr);
				rslt = new String(arr, encode.getEncoder());
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return rslt;
	}

	public static String[] getStringArrField(ByteBuffer bytBuff,
			StringEncode encode) {
		int len = bytBuff.getShort() & 0xffff;
		String[] rslt = new String[len];
		try {
			if (0 < len) {
				for (int i = 0; i < len; i++) {
					int slen = bytBuff.getShort() & 0xffff;
					if (0 < slen) {
						byte[] arr = new byte[slen];
						bytBuff.get(arr);
						String temp = new String(arr, encode.getEncoder());
						rslt[i] = temp;
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		return rslt;
	}

	

	public static String compositeStringValue(String initValue, char symbol,
			int totalLen) {
		String rslt = initValue;
		int initVLen = initValue.length();
		int padLen = totalLen - initVLen;
		for (int i = 0; i < padLen; i++) {
			rslt += symbol;
		}
		return rslt;
	}

	public static byte[] getValidBuffer(byte[] arr) {
		byte[] rslt = null;
		int limit = 0;
		for (int i = arr.length - 1; i >= 0; i--) {
			limit = i + 1;
			if (0 != arr[i]) {
				break;
			}
		}
		rslt = new byte[limit];
		System.arraycopy(arr, 0, rslt, 0, limit);
		return rslt;
	}

	

	public static String hex2StringForUUId(byte[] arr) {
		String rslt = "";
		for (byte elem : arr) {
			String tmp = Integer.toHexString(elem);
			if (tmp.length() == 0) {
				continue;
			}
			if (tmp.length() < 2) {
				tmp = '0' + tmp;
			} else if (tmp.length() > 2) {
				tmp = tmp.substring(tmp.length() - 2);
			}
			rslt += tmp;
		}
		return rslt;
	}

	public static short getCmdCode(byte mainCmd, byte subCmd) {
		return (short) (((mainCmd & 0xff) << 8) + (subCmd & 0xff));
	}

	public static String generateUUId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String generateDataString(ByteBuffer bytBuff) {
		String rslt = "";
		if (null == bytBuff) {
			return rslt;
		}
		int bytCounts = bytBuff.remaining();
		byte[] bytArr = new byte[bytCounts];
		bytBuff.get(bytArr);
		for (int i = 0; i < bytCounts; i++) {
			rslt = rslt + bytArr[i] + " ";
		}
		return rslt.trim();
	}

//	public static void closeChannel(Long userid, int ret) {
//		SocketChannelMng socketChannelMng = SocketChannelMng.getInstance();
//		synchronized (socketChannelMng) {
//			if (log.isInfoEnabled()) {
//				log.info("close client channel,delete client channel from map. ret->"
//						+ ret);
//			}
//			socketChannelMng.getUserSocketChannel(userid).getChannel().close();
//			socketChannelMng.deleteSocketChannel(userid);
//		}
//	}
//
//	public static void closeChannel(Long userid, Exception e) {
//		SocketChannelMng socketChannelMng = SocketChannelMng.getInstance();
//		synchronized (socketChannelMng) {
//			if (log.isInfoEnabled()) {
//				log.info("close client channel,delete client channel from map. "
//						+ e);
//			}
//			socketChannelMng.getUserSocketChannel(userid).getChannel().close();
//			socketChannelMng.deleteSocketChannel(userid);
//		}
//	}

	/**
	 * 获取UTF-8字符串
	 * 
	 * @param byteBuffer
	 * @param byteSize
	 * @return
	 */
	public static String getString(ByteBuffer byteBuffer, int byteSize) {
		return getString(byteBuffer, byteSize, DEFAULT_ENCODE);
	}

	/**
	 * 获取String
	 * 
	 * @param byteBuffer
	 * @param byteSize
	 * @param encode
	 * @return
	 */
	public static String getString(ByteBuffer byteBuffer, int byteSize,
			String encode) {
		byte[] bytes = new byte[byteSize];
		byteBuffer.get(bytes);
		try {
			return new String(bytes, encode).trim();
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**根据一个long(占据新数值的高四位,[0，无符号整型最大值]),int(占据新数值低四位) 合成一个long 数值，*/
	public static long enLong(long l,int i){
		return ((l&0xFFFFFFFF)<<32)|(i&0xFFFFFFFF);
	}
	
	/**得到long 数值的高四位，转换成long 值*/
	public static long getH32Bit(long l){
		return (l>>32)&0xFFFFFFFFl;
	}
	
	/**得到long 数值的低四位，转换成long 值*/
	public static long getL32Bit(long l){
		return l&0xFFFFFFFFl;
	}
	
	/**将单字节的数值转换成无符号整型*/
	public static int byteToInt(byte b){
		return b&0x0F|((b&0xF0));
	}
	
}
