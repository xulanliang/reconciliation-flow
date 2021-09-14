package com.yiban.rec.util;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 利用反射和JDOM解析xml成bean/List
 * 
 * @author clearofchina
 *
 */
public class Xml2BeanUtil {
	/**
	 * xml转换成bean
	 * 
	 * @param xml
	 * @param obj
	 * @return
	 */
	public static Object xmlToBean(String xml, Class obj) {
		SAXBuilder builder = new SAXBuilder();
		Field[] fields = obj.getDeclaredFields();
		String beanName = obj.getSimpleName();
		try {
			Object object = Class.forName(obj.getName()).newInstance();
			Document doc = builder.build(new StringReader(xml));
			Element books = doc.getRootElement();
			List booklist = books.getChildren(beanName);
			for (Iterator iter = booklist.iterator(); iter.hasNext();) {
				Element book = (Element) iter.next();
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					if (!fields[j].toString().contains("final")) {
						fields[j].set(object, book.getChildTextTrim(fields[j].getName()) == null ? ""
								: book.getChildTextTrim(fields[j].getName()));
					}
				}
			}
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * xml转换成list
	 * 
	 * @param xml
	 * @param obj
	 * @return
	 */
	public static List<Object> xmlToList(String xml, Class obj) {
		SAXBuilder builder = new SAXBuilder();
		Field[] fields = obj.getDeclaredFields();
		String beanName = obj.getSimpleName();
		try {
			List<Object> list = new ArrayList<Object>();
			Document doc = builder.build(new StringReader(xml));
			Element books = doc.getRootElement();
			List booklist = books.getChildren(beanName);
			if (booklist == null || booklist.isEmpty()) {
				beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
				booklist = books.getChildren(beanName);
			}
			for (Iterator iter = booklist.iterator(); iter.hasNext();) {
				Object object = Class.forName(obj.getName()).newInstance();
				
				Element book = (Element) iter.next();
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					if (!fields[j].toString().contains("final")) {
						fields[j].set(object, book.getChildTextTrim(fields[j].getName()) == null ? ""
								: book.getChildTextTrim(fields[j].getName()));
					}
				}
				list.add(object);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * xml转换成list
	 * 
	 * @param xml
	 * @param obj
	 * @return
	 */
	public static List<Object> xmlToList(String xml, String itemName, Class obj) {
		SAXBuilder builder = new SAXBuilder();
		Field[] fields = obj.getDeclaredFields();
		try {
			List<Object> list = new ArrayList<Object>();
			Document doc = builder.build(new StringReader(xml));
			Element books = doc.getRootElement();
			List booklist = books.getChildren(itemName);
			for (Iterator iter = booklist.iterator(); iter.hasNext();) {
				Object object = Class.forName(obj.getName()).newInstance();
				
				Element book = (Element) iter.next();
				for (int j = 0; j < fields.length; j++) {
					fields[j].setAccessible(true);
					if (!fields[j].toString().contains("final")) {
						fields[j].set(object, book.getChildTextTrim(fields[j].getName()) == null ? ""
								: book.getChildTextTrim(fields[j].getName()));
					}
				}
				list.add(object);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * bean转换成xml
	 */
	public static String beanToXmls(Object obj) {
		Class t = (Class) obj.getClass();
		Field[] fields = t.getDeclaredFields();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<Data><Vehicle>");
		try {
			Object object = Class.forName(obj.getClass().getName()).newInstance();
			for (int j = 0; j < fields.length; j++) {
				fields[j].setAccessible(true);
				String val = fields[j].get(obj) == null ? "" : fields[j].get(obj) + "";
				buffer.append("<" + fields[j].getName() + ">" + val + "</" + fields[j].getName() + ">\n");
			}
			buffer.append("</Data></Vehicle>");
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
