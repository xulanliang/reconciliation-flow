package com.yiban.rec.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
 
/**
 * 交易流水号
 * @author B7
 *
 */
public class SerialNumberGenerater {
 
    private static final int MAX_LENGTH = 4; // 流水号格式
    private static SerialNumberGenerater serialNumberGenerater = null;
    /** 流水号当前值 */
    private static int serialNumber = 0;
    /** yyyyMMddHHhhmmss */
    private static String dateFormat = "";  
    private SerialNumberGenerater() {
    }
 
    /**
     * 获取流水号对象实例
     * 
     * @return
     */
    public static SerialNumberGenerater getInstance() {
        if (serialNumberGenerater == null) {
            synchronized (SerialNumberGenerater.class) {
                if (serialNumberGenerater == null) {
                	serialNumberGenerater = new SerialNumberGenerater();
                }
            }
        }
        return serialNumberGenerater;
    }
 
    /**
     * 生成下一个编号
     */
    public synchronized String generaterNextNumber() {
    	String id = null;
    	String newDateFormat = new SimpleDateFormat("yyyyMMddHHhhmmss").format(new Date());
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < MAX_LENGTH; i++) {
    		sb.append("0");
    	}
    	DecimalFormat df = new DecimalFormat("0000");
    	if(dateFormat.equals(newDateFormat)){
    		++serialNumber;
    	}else{
    		serialNumber = 0;
    	}
    	id = newDateFormat+ df.format(1 + serialNumber);
    	dateFormat = newDateFormat;
    	return id;
    }
}