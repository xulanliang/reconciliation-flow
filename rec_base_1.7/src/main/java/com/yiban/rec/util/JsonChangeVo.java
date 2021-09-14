package com.yiban.rec.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

public class JsonChangeVo {

	//实体变为JSON
	public static<T> String getJson(T obj) throws IOException {
		ObjectMapper mapper = new ObjectMapper();  
		// Convert object to JSON string  
		String jsonStr = "";
		try {
			jsonStr =  mapper.writeValueAsString(obj);
		} catch (IOException e) {
		   throw e;
		}
		//return JSONObject.fromObject(jsonStr).toString();
		return jsonStr;
	}
	
	//JSON变为实体
	public static <T> Object getVo(String str,Class<T> bClass) throws IOException {
		return JSONObject.toBean(JSONObject.fromObject(str), bClass);
	}
}
