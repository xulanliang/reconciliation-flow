package com.yiban.rec.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomCodeUtil {
	/**
	 * 随机生成4位字母或数字
	 * @return
	 */
	public static  String generateWord(int fillSeat) {  
        String[] beforeShuffle = new String[] { "0","1","2", "3", "4", "5", "6", "7",  
                "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",  
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",  
                "W", "X", "Y", "Z","a", "b", "c", "d", "e", "f", "g", "h", "i", "j",  
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",  
                "w", "x", "y", "z" };  
        List<?> list = Arrays.asList(beforeShuffle);  
        Collections.shuffle(list);  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < list.size(); i++) {  
            sb.append(list.get(i));  
        }  
        String afterShuffle = sb.toString();  
        if(fillSeat > 0){
        	return afterShuffle.substring(0,fillSeat);
        }else{
        	return "";
        }
    }
	
	public static void main(String[] args) {
        System.err.println(generateWord(16));
    }
}
