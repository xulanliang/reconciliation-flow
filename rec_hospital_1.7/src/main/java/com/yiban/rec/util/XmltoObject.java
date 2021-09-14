package com.yiban.rec.util;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmltoObject {

	public static List<String> getObjectFromXml(String xmlStr) {
		List<String> strList = new ArrayList<String>();
		try {
			Document document = DocumentHelper.parseText(xmlStr);
			Element root = document.getRootElement();
			List<Element> rootList = root.elements();
			for (Element e : rootList) {
				// System.out.println(e.getName());
				if (e.getName().equals("Worksheet")) {
					List<Element> nodeList = e.element("Table").elements("Row");
					Element n0 = nodeList.get(0);
					// System.out.println(n0);
					Element n1 = nodeList.get(1);
					// 头部
					Element n2 = nodeList.get(2);
					List<Element> heads = n2.elements("Cell");
					for (Element h : heads) {
//						System.out.print(h.element("Data").getText());
//						System.out.print("\t");
					}
					for (int i = 3; i < nodeList.size(); i++) {
						List<Element> cellList = nodeList.get(i).elements("Cell");
						String str = "";
						System.out.println(cellList.size());
						for (Element c : cellList) {
							str+=c.element("Data").getText()+",";
						}
						strList.add(str);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strList;
	}
}
