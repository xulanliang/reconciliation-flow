package com.yiban.rec.netty.util;

import java.io.UnsupportedEncodingException;

public class FrameUtil {
    // 8字节IP + 5字节端口
    // private static final int EXTRA_DATA_LENGTH = BasicDataSize.SIZE_OF_LONG +
    // BasicDataSize.SIZE_OF_INT;
    
    // 截取有效的字节
    public static byte[] getValidStr(byte[] byts, int limitLen) {
        int j = 0;
        for (int i = 0; i < byts.length; i++) {
            if (byts[i] < 0 || byts[i] > 127) {
                if (((byts[i] & 0xff) >> 4) == 0x0E) {
                    i += 2;
                }
            }
            if (i < limitLen) {
                j = i + 1;
            }
        }
        byte[] retByts = new byte[j];
        System.arraycopy(byts, 0, retByts, 0, j);
        return retByts;
    }
    
    public static void main(String[] args) {
        try {
            String str = "ss行可是对方理论";
            byte[] b = str.getBytes("UTF-8");
            System.out.println(new String(getValidStr(b, 20)));
            
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
