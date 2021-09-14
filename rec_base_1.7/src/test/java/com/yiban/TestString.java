package com.yiban;

/** 
* @ClassName: TestString 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author tuchun@clearofchina.com 
* @date 2017年5月4日 下午2:57:35 
* @version V1.0 
*  
*/
public class TestString {

	public static void main(String[] args) {
		//String str="/LQGWeb/accessAppInterface/indexrefresh";
		String str="http://localhost:8080/LQGWeb/accessAppInterface/indexrefresh";
		String  str2=str.substring(str.indexOf('/', 4));
		//str.indexOf
		System.out.println(str2);
	}

}
