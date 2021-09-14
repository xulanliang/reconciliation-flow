package com.yiban.rec.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** 
* @ClassName: SqlUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author tuchun@clearofchina.com 
* @date 2017年4月16日 上午11:39:46 
* @version V1.0 
*  
*/
public class SqlUtil {
	public static String getSetInConditionStr(String[] strs){
		//org.code in ('1.12.','1.3.')
		StringBuffer sb=new StringBuffer("(");
		int i=0;
		for(String str:strs){
			if(i==strs.length-1){
				sb.append("'");
				sb.append(str);
				sb.append("'");
			}else{
				sb.append("'");
				sb.append(str);
				sb.append("'");
				sb.append(",");
			}
			i++;
		}
		sb.append(")");
		return sb.toString();
	}
	public static String getSetInConditionStr(Set<String> set){
		//org.code in ('1.12.','1.3.')
		StringBuffer sb=new StringBuffer("(");
		int i=0;
		for( Iterator<String> it = set.iterator();  it.hasNext();){
			if(i==set.size()-1){
				sb.append("'");
				sb.append(it.next());
				sb.append("'");
			}else{
				sb.append("'");
				sb.append(it.next());
				sb.append("'");
				sb.append(",");
			}
			i++;
		}
		sb.append(")");
		return sb.toString();
	}
	public static String getSetInConditionInt(Set set){
		StringBuffer sb=new StringBuffer("(");
		int i=0;
		for(Iterator it=set.iterator();it.hasNext();){
			if(i==set.size()-1){
				sb.append(it.next());
			}else{
				sb.append(it.next());
				sb.append(",");
			}
			i++;
		}
		sb.append(")");
		return sb.toString();
	}
	public static String getSetInConditionStrCom(Set<String> set){
		//'1.12.','1.3.'
		StringBuffer sb=new StringBuffer();
		int i=0;
		for( Iterator<String> it = set.iterator();  it.hasNext();){
			if(i==set.size()-1){
				sb.append(it.next());
			}else{
				sb.append(it.next());
				sb.append(",");
			}
			i++;
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		Set<String> setStr=new HashSet<String>();
		setStr.add("1.2");
		setStr.add("1.3");
		setStr.add("1.2");
		
		String sb=getSetInConditionStr(setStr);
		System.out.println(sb);
	}
	
	
	
}
