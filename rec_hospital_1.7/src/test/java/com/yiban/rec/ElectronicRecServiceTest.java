package com.yiban.rec;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.yiban.rec.bill.parse.util.HttpClientUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ElectronicRecServiceTest {
    private static Logger logger = LoggerFactory.getLogger(ElectronicRecServiceTest.class);

    public static void main(String[] args) {

        /*getOnLineOrderList("http://pay.clearofchina.com", "15290", "2020-02-24");*/
        /*String url = "http://pay.clearofchina.com" + "/pay/billLog/getWechatBill";*/
        String url = "http://pay.clearofchina.com" + "/pay/billLog/getAliBill";
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map.put("orgCode", "15290");
        map.put("time", "2020-03-31");
        String response = HttpClientUtil.doPostJson(url, gson.toJson(map).toString());
        System.out.println(response);

        //2、使用JSONArray
        /*String stu = "[{\"treatDate\":\"2020-04-03\",\"timeInterval\":\"1\"},{\"treatDate\":\"2020-04-04\",\"timeInterval\":\"1\"}]";
        JSONObject json = JSONObject.fromObject(stu);
        JSONArray array=JSONArray.fromObject(stu);
        System.out.println(array.size());*/




    }


    public static Map<String, JSONObject> getOnLineOrderList(String payCenterUrl, String orgCode, String beginDate) {
        Gson gson = new Gson();
        // 获取线上订单集合
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("orgCode", orgCode);
        paramsMap.put("beginTime", beginDate);
        /*paramsMap.put("endTime", DateUtil.getSpecifiedDayAfter(beginDate));*/
        paramsMap.put("endTime", beginDate);
        String responseStr = HttpClientUtil.doPostJson(payCenterUrl + "/pay/billLog/query/list", gson.toJson(paramsMap));
        JSONArray jsonArray = JSONArray.fromObject(responseStr);
        Map<String, JSONObject> keyMap = new HashMap<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            keyMap.put(jsonObject.getString("order_no"), jsonObject);
        }
        return keyMap;
    }

}



