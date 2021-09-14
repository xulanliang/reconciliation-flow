package com.yiban.rec.bill.parse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;

public class ParseXmlUtil {
	private static Logger logger = LoggerFactory.getLogger(XmlUtil.class);
	public static void main(String[] args) {
		String xml = "<Result>"
  +"<StatusCode>SUCCEED</StatusCode>"
  +"<StatusText>ExecutionSucceed</StatusText>"
  +"<Responses>"
  +"<Responsesaa>sssssss</Responsesaa>"
  +"  <response>"
  +"    <RECORD_COUNT>3</RECORD_COUNT>"
  +"    <PB_FLAG>2536||1015</PB_FLAG>"
  +"    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
  +"    <WEEK_DAY>4</WEEK_DAY>"
  +"    <SHIFT_ID>04</SHIFT_ID>"
  +"    <SHIFT_NAME>全天</SHIFT_NAME>"
  +"    <START_TIME>00:01</START_TIME>"
  +"    <END_TIME>23:59</END_TIME>"
  +"    <DEPT_CODE>3</DEPT_CODE>"
  +"    <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
 +"     <DOC_CODE>2213</DOC_CODE>"
 +"     <DOC_NAME>呼吸三科方便号</DOC_NAME>"
  +"    <DOC_TITILE_CODE/>"
 +"     <DOC_TITLE_NAME>方便门诊</DOC_TITLE_NAME>"
  +"    <VISIT_LEVEL_CODE>55</VISIT_LEVEL_CODE>"
  +"    <VISIT_LEVEL_NAME>方便门诊</VISIT_LEVEL_NAME>"
  +"    <SUM_FEE>1</SUM_FEE>"
  +"    <REG_FEE>0</REG_FEE>"
  +"    <CHECKUP_FEE>1</CHECKUP_FEE>"
  +"    <OTHER_FEE>0</OTHER_FEE>"
 +"     <VISIT_STATUS>1</VISIT_STATUS>"
 +"     <COUNT>999</COUNT>"
 +"     <REMAIN_COUNT>993</REMAIN_COUNT>"
  +"    <TIME_FLAG>0</TIME_FLAG>"
  +"  </response>"
  +"  <response>"
  +"    <RECORD_COUNT>3</RECORD_COUNT>"
  +"    <PB_FLAG>2537||1014</PB_FLAG>"
  +"    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
  +"    <WEEK_DAY>4</WEEK_DAY>"
  +"    <SHIFT_ID>04</SHIFT_ID>"
  +"    <SHIFT_NAME>全天</SHIFT_NAME>"
 +"     <START_TIME>00:01</START_TIME>"
  +"    <END_TIME>23:59</END_TIME>"
  +"    <DEPT_CODE>3</DEPT_CODE>"
   +"   <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
   +"   <DOC_CODE>2214</DOC_CODE>"
   +"   <DOC_NAME>呼吸三科老年号</DOC_NAME>"
   +"   <DOC_TITILE_CODE/>"
  +"    <DOC_TITLE_NAME>老年门诊</DOC_TITLE_NAME>"
  +"    <VISIT_LEVEL_CODE>56</VISIT_LEVEL_CODE>"
  +"    <VISIT_LEVEL_NAME>老年门诊</VISIT_LEVEL_NAME>"
  +"    <SUM_FEE>5</SUM_FEE>"
  +"    <REG_FEE>0</REG_FEE>"
   +"   <CHECKUP_FEE>5</CHECKUP_FEE>"
  +"    <OTHER_FEE>0</OTHER_FEE>"
  +"    <VISIT_STATUS>1</VISIT_STATUS>"
  +"    <COUNT>999</COUNT>"
  +"    <REMAIN_COUNT>999</REMAIN_COUNT>"
  +"    <TIME_FLAG>0</TIME_FLAG>"
  +"  </response>"
  +"  <response>"
  +"    <RECORD_COUNT>3</RECORD_COUNT>"
  +"    <PB_FLAG>2538||1014</PB_FLAG>"
  +"    <SCHEDULE_DATE>2018-01-18</SCHEDULE_DATE>"
  +"    <WEEK_DAY>4</WEEK_DAY>"
  +"    <SHIFT_ID>04</SHIFT_ID>"
 +"     <SHIFT_NAME>全天</SHIFT_NAME>"
  +"    <START_TIME>00:01</START_TIME>"
  +"    <END_TIME>23:59</END_TIME>"
  +"    <DEPT_CODE>3</DEPT_CODE>"
  +"    <DEPT_NAME>呼吸三科(北区)</DEPT_NAME>"
 +"     <DOC_CODE>2215</DOC_CODE>"
 +"     <DOC_NAME>呼吸三科急诊号</DOC_NAME>"
 +"     <DOC_TITILE_CODE/>"
  +"    <DOC_TITLE_NAME>急诊</DOC_TITLE_NAME>"
  +"    <VISIT_LEVEL_CODE>57</VISIT_LEVEL_CODE>"
  +"    <VISIT_LEVEL_NAME>急诊</VISIT_LEVEL_NAME>"
  +"    <SUM_FEE>10</SUM_FEE>"
  +"    <REG_FEE>0</REG_FEE>"
  +"    <CHECKUP_FEE>10</CHECKUP_FEE>"
  +"    <OTHER_FEE>0</OTHER_FEE>"
  +"    <VISIT_STATUS>1</VISIT_STATUS>"
  +"    <COUNT>999</COUNT>"
  +"    <REMAIN_COUNT>997</REMAIN_COUNT>"
  +"    <TIME_FLAG>0</TIME_FLAG>"
  +"  </response>"
  +"</Responses>"
+"</Result>";


		String xml2 = "<a><response>"
+"<SUM_FEE>1.51</SUM_FEE>"
		+"<PRES_NO/>"
		+"<PRES_TYPE>1</PRES_TYPE>"
		+"<PRES_NAME>西药</PRES_NAME>"
		+"<PRES_FEE>1.51</PRES_FEE>"
		+"<ITEM_NAME>(GJ)蒙托石散</ITEM_NAME>"
		+"<ITEM_SIZES>3g/包</ITEM_SIZES>"
		+"<ITEM_NUM>2</ITEM_NUM>"
		+"<ITEM_UNIT>包</ITEM_UNIT>"
		+"<ITEM_PRICE>0.76</ITEM_PRICE>"
		+"<ITEM_FEE>1.51</ITEM_FEE>"
		+"<PAY_DTIME>2018-01-18 09:40:49</PAY_DTIME>"
		+"</response>"
		+"<response>"
		+"<SUM_FEE>1.51</SUM_FEE>"
				+"<PRES_NO>1801001631</PRES_NO>"
				+"<PRES_TYPE>1</PRES_TYPE>"
				+"<PRES_NAME>西药</PRES_NAME>"
				+"<PRES_FEE>1.51</PRES_FEE>"
				+"<ITEM_NAME>(GJ)蒙托石散</ITEM_NAME>"
				+"<ITEM_SIZES>3g222222222/包</ITEM_SIZES>"
				+"<ITEM_NUM>2</ITEM_NUM>"
				+"<ITEM_UNIT>包</ITEM_UNIT>"
				+"<ITEM_PRICE>0.76</ITEM_PRICE>"
				+"<ITEM_FEE>1.51</ITEM_FEE>"
				+"<PAY_DTIME>2018-01-18 09:40:49</PAY_DTIME>"
				+"</response>"
				+ "</a>";



		String xml3="<Response>"
		+ "<ResultCode>0</ResultCode>"
		 + " <RecordCount>1</RecordCount>"
		 + " <Departments>"
		 + "   <Department>"
		 + "     <DepartmentCode>433</DepartmentCode>"
		+ "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
		+ "      <ParentId>-1</ParentId>"
		+ "      <DepartmentAddress>4楼</DepartmentAddress>"
		+ "    </Department>"
		+ "    <Department>"
		+ "      <DepartmentCode>433ssssssssss</DepartmentCode>"
		+ "      <DepartmentName>呼吸内一科门诊(南区)</DepartmentName>"
		+ "      <ParentId>-1</ParentId>"
		+ "      <DepartmentAddress>4楼</DepartmentAddress>"
		+ "    </Department>"
		+ "  </Departments>"
		+ "</Response>";
		Map<String, Object> map = null;
		map = xml2map(xml3);
		Map<String, Object> rootmap = (Map<String, Object>) map.get("Response");
		Map<String, Object> Departmentsmap = (Map<String, Object>) rootmap.get("Departments");
        if(Departmentsmap.get("Department") instanceof Map) //因为xml不能表现出是数组还是 单个对象 所以需要判断
        {
        	Map<String, Object> Departmentmap = (Map<String, Object>) Departmentsmap.get("Department");

        	logger.debug(Departmentmap.get("DepartmentCode").toString());
        }else if(Departmentsmap.get("Department") instanceof List)
        {
        	List<Map<String,Object>> lll =  (List<Map<String, Object>>) Departmentsmap.get("Department");
        	for(Map<String,Object> m: lll)
        	{
        		logger.debug(m.get("DepartmentCode") == null? "": m.get("DepartmentCode").toString());
        	}
        }
		/*
        map = xml2map(xml);
        //获取第一个map
        Map<String, Object> Resultmap  = (Map<String, Object>) map.get("Result");
        System.out.println(Resultmap.get("StatusText"));
        Map<String, Object> Responsesmap = (Map<String, Object>) Resultmap.get("Responses");//获取Responses
        System.out.println(Responsesmap.get("Responsesaa"));
        List<Map<String, Object>> ll = (List<Map<String, Object>>) Responsesmap.get("response");
        System.out.println(ll.size());



        //测试中
        Map<String, Object> map2 = null;
        map2 = xml2map(xml2);
        Map<String, Object> rootmap = (Map<String, Object>) map2.get("a");//xml的特性 必须有且只有一个根节点  所以不用判断是map还是 list<map>
        if(rootmap.get("response") instanceof Map) //因为xml不能表现出是数组还是 单个对象 所以需要判断
        {
        	Map<String, Object> responsemap = (Map<String, Object>) rootmap.get("response");
        	System.out.println(responsemap.get("ITEM_SIZES"));
        }else if(rootmap.get("response") instanceof List)
        {
        	List<Map<String,Object>> lll =  (List<Map<String, Object>>) rootmap.get("response");
        	for(Map<String,Object> m: lll)
        	{
        		System.out.println(m.get("ITEM_SIZES") == null? "": m.get("ITEM_SIZES"));
        	}
        }*/
	}

	/**
	 * xml转map
	 *
	 * 调用参考
	 *
	 * Map<String, Object> map = null;
        map = xml2map(xml);
        //获取第一个map
        Map<String, Object> Resultmap  = (Map<String, Object>) map.get("Result");
        System.out.println(Resultmap.get("StatusText"));
        Map<String, Object> Responsesmap = (Map<String, Object>) Resultmap.get("Responses");//获取Responses
        System.out.println(Responsesmap.get("Responsesaa"));
        List<Map<String, Object>> ll = (List<Map<String, Object>>) Responsesmap.get("response");
        System.out.println(ll.size());



        //测试中
        Map<String, Object> map2 = null;
        map2 = xml2map(xml2);
        Map<String, Object> rootmap = (Map<String, Object>) map2.get("a");//xml的特性 必须有且只有一个根节点  所以不用判断是map还是 list<map>
        if(rootmap.get("response") instanceof Map) //因为xml不能表现出是数组还是 单个对象 所以需要判断
        {
        	Map<String, Object> responsemap = (Map<String, Object>) rootmap.get("response");
        	System.out.println(responsemap.get("ITEM_SIZES"));
        }else if(rootmap.get("response") instanceof List)
        {
        	List<Map<String,Object>> lll =  (List<Map<String, Object>>) rootmap.get("response");
        	for(Map<String,Object> m: lll)
        	{
        		System.out.println(m.get("ITEM_SIZES") == null? "": m.get("ITEM_SIZES"));
        	}
        }
	 * @param xml
	 * @return
	 */
	public static Map<String, Object> xml2map(String xml) {
		logger.debug("入参:"+xml);
		Map<String, Object> map = new HashMap<String, Object>();
		Document doc = null;
		if(StringUtils.isEmpty(xml))
			return map;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            e.printStackTrace();
            return map;
        }

        if (doc == null)
            return map;
        Element rootElement = doc.getRootElement();
        element2map(rootElement,map);
//        logger.debug("最终生成的map如下:"+map);
        return map;
    }

	/**
     *
     * @param elmt 当前元素
     * @param map 主键为当前元素的节点名,值为当前元素的所有直接子元素
     */
    private static void element2map(Element elmt, Map<String, Object> map) {
        if (null == elmt) {
            return;
        }
        String name = elmt.getName();
        if (elmt.isTextOnly()) {
            map.put(name, elmt.getText());
        } else {
            Map<String, Object> mapSub = new HashMap<String, Object>();
            List<Element> elements = (List<Element>) elmt.elements();
            for (Element elmtSub : elements) {
                element2map(elmtSub, mapSub);
            }
            Object first = map.get(name);
            if (null == first) {
                map.put(name, mapSub);
            } else {
                if (first instanceof List<?>) {
                    ((List) first).add(mapSub);
                } else {
                    List<Object> listSub = new ArrayList<Object>();
                    listSub.add(first);
                    listSub.add(mapSub);
                    map.put(name, listSub);
                }
            }
        }
    }
    
    public static JSONObject stringXMLToJSONObject(String data) {
        JSONObject obj = new JSONObject();
        if (StringUtils.isBlank(data)) {
            return null;
        }
        try {
            Document doc = DocumentHelper.parseText(data);
            Element root = doc.getRootElement();
            obj.put(root.getName(), iterateElement(root));
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("stringXMLToJSONObject error: {}", e);
            return null;
        }
    }
    
    /**
     * 一个迭代方法
     *
     * @param element
     * @return java.util.Map 实例
     */
    private static Map iterateElement(Element element) {
        List elements = element.elements();
        Element et;
        Map obj = new HashMap();
        Object temp;
        List list;
        for (int i = 0; i < elements.size(); i++) {
            list = new LinkedList();
            et = (Element) elements.get(i);
            if (et.getTextTrim().equals("")) {
                if (et.elements().size() == 0) {
                    obj.put(et.getName(), "");
                    continue;
                }
                if (obj.containsKey(et.getName())) {
                    temp = obj.get(et.getName());
                    if (temp instanceof List) {
                        list = (List) temp;
                        list.add(iterateElement(et));
                    } else if (temp instanceof Map) {
                        list.add(temp);
                        list.add(iterateElement(et));
                    } else {
                        list.add(temp);
                        list.add(iterateElement(et));
                    }
                    obj.put(et.getName(), list);
                } else {
                    obj.put(et.getName(), iterateElement(et));
                }
            } else {
                if (obj.containsKey(et.getName())) {
                    temp = obj.get(et.getName());
                    if (temp instanceof List) {
                        list = (List) temp;
                        list.add(et.getTextTrim());
                    } else if (temp instanceof Map) {
                        list.add(temp);
                        list.add(iterateElement(et));
                    } else {
                        list.add(temp);
                        list.add(et.getTextTrim());
                    }
                    obj.put(et.getName(), list);
                } else {
                    obj.put(et.getName(), et.getTextTrim());
                }
            }
        }
        return obj;
    }
}
