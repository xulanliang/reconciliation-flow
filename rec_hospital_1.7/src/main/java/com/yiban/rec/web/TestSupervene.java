package com.yiban.rec.web;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yiban.framework.core.controller.FrameworkController;
import com.yiban.rec.dao.HealthCareOfficialDao;
import com.yiban.rec.domain.HealthCareOfficial;
import com.yiban.rec.domain.vo.SZYBBill;
import com.yiban.rec.service.settlement.RecHisSettlementService;
//import com.yiban.rec.service.settlement.RecHisSettlementService;
import com.yiban.rec.util.DateUtil;

import net.sf.json.JsonConfig;

@RestController
@RequestMapping("/api/test")
public class TestSupervene extends FrameworkController{
	
    @Autowired
    private RecHisSettlementService recHisSettlementService;
    
    @Autowired
    private HealthCareOfficialDao healthCareOfficialDao;
	
	@RequestMapping(value = "/settlement", method = RequestMethod.GET)
	public String testSettlementHisOrder(String date) {
	    String hisBillDate = DateUtil.getSpecifiedDayBeforeDay(new Date(), 1);
        try {
            recHisSettlementService.getAndSaveHisOrders(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return "SUCCESS";
	}
	
	@Transactional
    @RequestMapping(value = "/szyb", method = RequestMethod.GET)
    public String testszyb() {
        List<HealthCareOfficial> hisList = new ArrayList<>();
        // String testStr = FileUtil.readAsString(new
        // File("C:\\Users\\admin\\Desktop\\医保接口返回的响应.txt"));
        // net.sf.json.JSONObject testJson = gson.fromJson(testStr,
        // net.sf.json.JSONObject.class);
        String testStr = "[\r\n" + "  {\r\n" + "    \"akc190\": \"H0320KKP48009179\",\r\n"
                + "    \"bke384\": \"H0320201811221707253\",\r\n" + "    \"ckc618\": \"A4403001811221721706\",\r\n"
                + "    \"aka130\": \"01\",\r\n" + "    \"aaz500\": \"5002404163\",\r\n" + "    \"alc005\": \"\",\r\n"
                + "    \"aac003\": \"\",\r\n" + "    \"aka018\": \"1\",\r\n" + "    \"cke555\": \"10\",\r\n"
                + "    \"akc264\": 0,\r\n" + "    \"akb068\": 0,\r\n" + "    \"akb066\": 0,\r\n"
                + "    \"akb067\": 0,\r\n" + "    \"aae011\": \"1791\"\r\n" + "  },\r\n" + "  {\r\n"
                + "    \"akc190\": \"H0320KKP48009179\",\r\n" + "    \"bke384\": \"H0320201811221707256\",\r\n"
                + "    \"ckc618\": \"A4403001811221721706\",\r\n" + "    \"aka130\": \"12\",\r\n"
                + "    \"aaz500\": \"5002404163\",\r\n" + "    \"alc005\": \"\",\r\n" + "    \"aac003\": \"郭芸含\",\r\n"
                + "    \"aka018\": \"1\",\r\n" + "    \"cke555\": \"10\",\r\n" + "    \"akc264\": 15.61,\r\n"
                + "    \"akb068\": 0,\r\n" + "    \"akb066\": 15.61,\r\n" + "    \"akb067\": 0,\r\n"
                + "    \"aae011\": \"1791\"\r\n" + "  },\r\n" + "  {\r\n" + "    \"akc190\": \"H0320KP48045276\",\r\n"
                + "    \"bke384\": \"H0320201811221707467\",\r\n" + "    \"ckc618\": \"A4403001811221721906\",\r\n"
                + "    \"aka130\": \"01\",\r\n" + "    \"aaz500\": \"6055054382\",\r\n" + "    \"alc005\": \"\",\r\n"
                + "    \"aac003\": \"邱建均\",\r\n" + "    \"aka018\": \"1\",\r\n" + "    \"cke555\": \"10\",\r\n"
                + "    \"akc264\": 31,\r\n" + "    \"akb068\": 0,\r\n" + "    \"akb066\": 31,\r\n"
                + "    \"akb067\": 0,\r\n" + "    \"aae011\": \"1791\"\r\n" + "  }\r\n" + "]";
        List<SZYBBill> list = net.sf.json.JSONArray.toList(net.sf.json.JSONArray.fromObject(testStr), new SZYBBill(),
                new JsonConfig());
        for (SZYBBill bill : list) {
            if (null == bill || StringUtils.isBlank(bill.getAkc190())) {
                continue;
            }
            HealthCareOfficial flow = new HealthCareOfficial();
            flow.setOrgNo("pppppppppp");
            flow.setPayFlowNo(bill.getBke384());
            flow.setOperationType("dddd");
            flow.setCostAll(bill.getAkc264());
            //flow.setTradeDatatime(new Date());
            flow.setTradeDatatime(formaterTime(bill.getBke384()));
            flow.setBillSource("8392");
            flow.setCreatedDate(new Date());
            hisList.add(flow);
        }
        healthCareOfficialDao.save(hisList);
        return "SUCCESS";
    }
	
	/*@ResponseBody
	@RequestMapping("szyb")
	@Transactional
	public String testSZYB(){
		HealthCareOfficial flow = new HealthCareOfficial();
		flow.setOrgNo("999999");
		flow.setPayFlowNo("000000000000000");
		flow.setOperationType("");
		flow.setCostAll(new BigDecimal("100"));
		flow.setTradeDatatime(new Date());
		flow.setBillSource("8392");
		flow.setCreatedDate(new Date());
		entityManager.persist(flow);
        entityManager.flush();
        entityManager.clear();
//        healthCareOfficialDao.save(flow);
		return "SUCCESS";
	}*/
	
	private Date formaterTime(String date){
		String dateformat=date;
		StringBuffer buffer = new StringBuffer();
		String result = dateformat.substring(5, 13);
		buffer.append(result.substring(0,4));
		buffer.append("-");
		buffer.append(result.substring(4,6));
		buffer.append("-");
		buffer.append(result.substring(6,8));
		buffer.append(" ");
		buffer.append("00:00:00");
		Date data=null;
		try {
			data = DateUtil.transferStringToDateFormat(buffer.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return data;
		
	}
}
