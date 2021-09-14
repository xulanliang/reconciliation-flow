package com.yiban.rec.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;


/**
 * @author su.jf
 */
public class CodingUtil {

    public static byte[] base64Decode(String str) {
        return Base64.decode(str);
    }

    public static String base64Encode(byte[] data) {
        return Base64.encode(data);
    }

    private static final int BIT_SIZE   = 0x10;
    private static final int BIZ_ZERO   = 0X00;

    private static char[][]  charArrays = new char[256][];

    static {
        int v;
        char[] ds;
        String temp;
        for (int i = 0; i < charArrays.length; i++) {
            ds = new char[2];
            v = i & 0xFF;
            temp = Integer.toHexString(v);
            if (v < BIT_SIZE) {
                ds[0] = '0';
                ds[1] = temp.charAt(0);
            } else {
                ds[0] = temp.charAt(0);
                ds[1] = temp.charAt(1);
            }
            charArrays[i] = ds;
        }
    }

    public static String bytesToHexString(byte[] src) {
        return bytesToHexString(src, 0, src.length);
    }
    
    
    public static String bytesToHexString(byte[] src,int posction,int length) {
        HexAppender helper = new HexAppender(src.length * 2);
        if (src == null || src.length <= BIZ_ZERO) {
            return null;
        }
        int v;
        int lengthR = src.length > length ?length:src.length;
        char[] temp;
        for (int i = posction; i < lengthR; i++) {
            v = src[i] & 0xFF;
            temp = charArrays[v];
            helper.append(temp[0], temp[1]);
        }
        return helper.toString();
    }

    public static String bytesToHexStringSub(byte[] src, int length) {
       return bytesToHexString(src, 0, length);
    }

    
    public static String bytesToHexString(byte[] src, int startWith) {
    	return bytesToHexString(src, startWith, src.length);
    }
    
    /**
     * Convert hex string to byte[]
     * 
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if ( hexString ==null) {
            return null;
        }
        int length = hexString.length() / 2;
        byte[] d = new byte[length];
        int pos;
        for (int i = 0; i < length; i++) {
            pos = i * 2;
            d[i] = (byte) (charToByte(hexString.charAt(pos)) << 4 | charToByte(hexString.charAt(pos + 1)));
        }
        return d;
    }

    /**
     * Convert char to byte
     * 
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) (c < 58 ? c - 48 : c < 71 ? c - 55 : c - 87);
    }
    
    
    private static final char[] DIS_256  = {'Ḁ','ḁ','Ḃ','ḃ','Ḅ','ḅ','Ḇ','ḇ','Ḉ','ḉ','Ḋ','ḋ','Ḍ','ḍ','Ḏ','ḏ','Ḑ','ḑ','Ḓ','ḓ','Ḕ','ḕ','Ḗ','ḗ','Ḙ','ḙ','Ḛ','ḛ','Ḝ','ḝ','Ḟ','ḟ','Ḡ','ḡ','Ḣ','ḣ','Ḥ','ḥ','Ḧ','ḧ','Ḩ','ḩ','Ḫ','ḫ','Ḭ','ḭ','Ḯ','ḯ','Ḱ','ḱ','Ḳ','ḳ','Ḵ','ḵ','Ḷ','ḷ','Ḹ','ḹ','Ḻ','ḻ','Ḽ','ḽ','Ḿ','ḿ','Ṁ','ṁ','Ṃ','ṃ','Ṅ','ṅ','Ṇ','ṇ','Ṉ','ṉ','Ṋ','ṋ','Ṍ','ṍ','Ṏ','ṏ','Ṑ','ṑ','Ṓ','ṓ','Ṕ','ṕ','Ṗ','ṗ','Ṙ','ṙ','Ṛ','ṛ','Ṝ','ṝ','Ṟ','ṟ','Ṡ','ṡ','Ṣ','ṣ','Ṥ','ṥ','Ṧ','ṧ','Ṩ','ṩ','Ṫ','ṫ','Ṭ','ṭ','Ṯ','ṯ','Ṱ','ṱ','Ṳ','ṳ','Ṵ','ṵ','Ṷ','ṷ','Ṹ','ṹ','Ṻ','ṻ','Ṽ','ṽ','Ṿ','ṿ',
    		'Ẁ','ẁ','Ẃ','ẃ','Ẅ','ẅ','Ẇ','ẇ','Ẉ','ẉ','Ẋ','ẋ','Ẍ','ẍ','Ẏ','ẏ','Ẑ','ẑ','Ẓ','ẓ','Ẕ','ẕ','ẖ','ẗ','ẘ','ẙ','ẚ','ẛ','ẜ','ẝ','ẞ','ẟ','Ạ','ạ','Ả','ả','Ấ','ấ','Ầ','ầ','Ẩ','ẩ','Ẫ','ẫ','Ậ','ậ','Ắ','ắ','Ằ','ằ','Ẳ','ẳ','Ẵ','ẵ','Ặ','ặ','Ẹ','ẹ','Ẻ','ẻ','Ẽ','ẽ','Ế','ế','Ề','ề','Ể','ể','Ễ','ễ','Ệ','ệ','Ỉ','ỉ','Ị','ị','Ọ','ọ','Ỏ','ỏ','Ố','ố','Ồ','ồ','Ổ','ổ','Ỗ','ỗ','Ộ','ộ','Ớ','ớ','Ờ','ờ','Ở','ở','Ỡ','ỡ','Ợ','ợ','Ụ','ụ','Ủ','ủ','Ứ','ứ','Ừ','ừ','Ử','ử','Ữ','ữ','Ự','ự','Ỳ','ỳ','Ỵ','ỵ','Ỷ','ỷ','Ỹ','ỹ','Ỻ','ỻ','Ỽ','ỽ','Ỿ','ỿ'};
    
    private static final int DIS_START = 'Ḁ';
    
    
    public static String bytesToByteString(byte[]datas){
    	char[]dataC = new char[datas.length];
    	for (int i = 0; i < datas.length; i++) {
    		dataC[i] = DIS_256[datas[i]+127];
		}
    	return new String(dataC);
    }
    
    public static byte[] byteStringToByte(String byteString){
    	char[]datas = byteString.toCharArray();
    	byte[]result = new byte[datas.length];
    	for (int i = 0; i < datas.length; i++) {
    		result[i] = (byte) (datas[i]-DIS_START-127);
		} 
    	return result;
    }
    
    
    
    public static long byte2long(byte[]value){
    	long temp = 0;  
        long res = 0;  
        for (int i=0;i<8;i++) {  
            res <<= 8;  
            temp = value[i] & 0xff;  
            res |= temp;  
        }  
        return res;
    }
    
   
    private static class HexAppender {

        private int    offerSet = 0;
        private char[] charData;

        public HexAppender(int size) {
            charData = new char[size];
        }

        public void append(char a, char b) {
            charData[offerSet++] = a;
            charData[offerSet++] = b;
        }

        @Override
        public String toString() {
            return new String(charData, 0, offerSet);
        }
    }

    /**
	 * 判断文件的编码格式
	 * @param fileName :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static String codeString(File fileName) throws Exception{
		BufferedInputStream bin = new BufferedInputStream(
		new FileInputStream(fileName));
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		return code;
	}
    
}
