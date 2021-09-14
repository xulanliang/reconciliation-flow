package com.yiban.rec.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class JDomXmlParseUtil {

	/**
	 *     *
	 * <p>
	 * Description:将字符串类型的XML 转化成Docunent文档结构
	 * </p>
	 *      * @param parseStrXml 待转换的xml 字符串      * @return Document      *     
	 * * @author  huoge     
	 */
	public static Document strXmlToDocument(String parseStrXml) {
		StringReader read = new StringReader(parseStrXml);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		// 创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder(); // 新建立构造器
		Document doc = null;
		try {
			doc = sb.build(source);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * <p>
	 * Description: 根据目标节点名获取值
	 * </p>
	 * 
	 * @param doc           文档结构
	 * @param finalNodeName 最终节点名
	 * @return
	 *
	 * @author huoge
	 */
	public static String getValueByElementName(Document doc, String finalNodeName) {
		String result = null;
		try {
			Element root = doc.getRootElement();
			HashMap<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> resultmap = getChildAllText(doc, root, map);
			result = (String) resultmap.get(finalNodeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * <p>
	 * Description: 递归获得子节点的值
	 * </p>
	 * 
	 * @param doc       文档结构
	 * @param e         节点元素
	 * @param resultmap 递归将值压入map中
	 * @return
	 *
	 * @author huoge
	 */
	public static Map<String, Object> getChildAllText(Document doc, Element e, HashMap<String, Object> resultmap) {
		if (e != null) {
			if (e.getChildren() != null) // 如果存在子节点
			{
				List<Element> list = e.getChildren();
				for (Element el : list) // 循环输出
				{
					if (el.getChildren().size() > 0) // 如果子节点还存在子节点，则递归获取
					{
						getChildAllText(doc, el, resultmap);
					} else {
						resultmap.put(el.getName(), el.getTextTrim()); // 将叶子节点值压入map
					}
				}
			}
		}
		return resultmap;
	}
}
