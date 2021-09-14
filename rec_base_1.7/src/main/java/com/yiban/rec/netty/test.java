package com.yiban.rec.netty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** 
* @ClassName: test 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author tuchun@clearofchina.com 
* @date 2017年4月6日 下午7:37:44 
* @version V1.0 
*  
*/
public class test {
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		BigDecimal b = 	(BigDecimal) (map.get("pay_amount") == null ? "0" : map.get("pay_amount"));
		System.out.println(b);
	}
}
