package com.yiban.rec.bill.parse.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 * ftp工具类
 * 
 * @author clearofchina
 *
 */
public class XmlUtil {

	// xml形式的字符串转换为map集合
	public static Map<String, Object> xmlStr2Map(String xmlStr) {
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc;
		try {
			doc = DocumentHelper.parseText(xmlStr);
			Element root = doc.getRootElement();
			List children = root.elements();
			if (children != null && children.size() > 0) {
				for (int i = 0; i < children.size(); i++) {
					Element child = (Element) children.get(i);
					map.put(child.getName(), child.getTextTrim());
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}

	public static JSONArray parse2JSONArray(String context, String keys) {
		if (StringUtils.isEmpty(context)) {
			return null;
		}

		JSONArray jsonArray = null;

		JSONObject jsonObject = null;
		if (context.trim().startsWith("{")) {
			jsonObject = new JSONObject(context);

		} else if (context.trim().startsWith("<")) {
			jsonObject = XML.toJSONObject(context);
		}

		String[] keyArr = keys.split(",");
		for (int i = 0, len = keyArr.length; i < len; i++) {
			Object val = jsonObject.get(keyArr[i]);
			if (val == null) {
				return null;
			}

			String text = val.toString().trim();
			if (i == len - 1) {
				if (text.startsWith("[")) {
					jsonArray = new JSONArray(val.toString());
				} else if (text.startsWith("<")) {
					jsonArray = XML.toJSONObject(val.toString()).getJSONArray(keyArr[i]);
				}
			} else {
				if (text.startsWith("{")) {
					jsonObject = new JSONObject(val.toString());
				} else if (text.startsWith("<")) {
					jsonObject = XML.toJSONObject(val.toString());
				}
			}
		}

		return jsonArray;
	}
}
