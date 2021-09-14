package com.yiban.rec.netty.util;

import com.google.gson.Gson;

public class GsonUtil {
    
    public static String object2jsonString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }
    
    @SuppressWarnings("unchecked")
    public static Object jsonString2Object(String jsonString, Class clazz) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, clazz);
    }
}
