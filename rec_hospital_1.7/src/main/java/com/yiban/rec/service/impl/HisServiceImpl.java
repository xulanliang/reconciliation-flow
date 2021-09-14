package com.yiban.rec.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.client.Call;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.service.ServiceException;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.domain.vo.DataSource;
import com.yiban.rec.domain.vo.HisRequestVo;
import com.yiban.rec.domain.vo.HisResponseVo;
import com.yiban.rec.domain.vo.TradeHISDetailVo;
import com.yiban.rec.service.HisService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.HisInterfaceType;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.WebServiceClientUtilExtend;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class HisServiceImpl implements HisService {
    /** 日志对象 */
    private static final Logger logger = 
            LoggerFactory.getLogger(HisServiceImpl.class);
    /** gson对象 */
    private Gson gson = 
            new GsonBuilder().enableComplexMapKeySerialization().create();
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
    @Override
    public HisResponseVo service(HisRequestVo vo) throws Exception {
        logger.info("调用his接口入参:{}", gson.toJson(vo));
        HisInterfaceType type = vo.getType();
        // HTTP类型请求
        if(HisInterfaceType.HTTP.equals(type)) {
            return serviceHttp(vo.getServerUrl(), 
                    vo.getHttpType(), vo.getParams());
        }
        // WEBAPI类型请求
        if(HisInterfaceType.WEBAPI.equals(type)) {
            return serviceWebApi(vo.getServerUrl(), vo.getParams());
        }
        // WCF类型请求
        if(HisInterfaceType.WCF.equals(type)) {
            return serviceWcf(vo.getServerUrl(), vo.getParams());
        }
        // WEBSERVICE类型请求
        if(HisInterfaceType.WEBSERVICE.equals(type)) {
            return serviceWebService(vo.getServerUrl(), vo.getFunctionName(),vo.getParams());
        }
        // EXE类型请求
        if(HisInterfaceType.EXE.equals(type)) {
            return serviceExe(vo.getCommand());
        }
        // DATASOURCE类型请求
        if(HisInterfaceType.DATASOURCE.equals(type)) {
            return serviceDatasource(vo.getDataSource(), vo.getSql(), vo.getParams());
        }
        return HisResponseVo.failure("接口类型非法");
    }
    
    /**
     * 请求http类型的接口
     * @param serverUrl
     * @param httpType
     * @param params
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceHttp(String serverUrl, 
            String httpType, Map<String, String> params) {
        String result = "";
        try {
        	if(StringUtils.equalsIgnoreCase(httpType, "post")) {
                result = new RestUtil().doPostJson(serverUrl, JSONObject.fromObject(params).toString(), CommonConstant.CODING_FORMAT);
            }else if(StringUtils.equalsIgnoreCase(httpType, "get")) {
                result = HttpClientUtil.doGet(serverUrl, params);
            }
		} catch (Exception e) {
			logger.error("异常", e.getMessage());
		}
        logger.info("调用his接口出参:{}", result);
        if(StringUtils.isBlank(result)) {
            return HisResponseVo.failure("服务器暂无响应");
        }
        result=result.replaceAll("null", "\"\"");
        JSONObject json=JSONObject.fromObject(result);
        JSONArray newList=new JSONArray();
        try {
        	//字段匹配
            JSONArray list = json.getJSONArray("orderItems");
            if(list==null||list.size()<=0) {
            	return HisResponseVo.failure("无数据");
            }
            for(int i=0;i<list.size();i++) {
            	JSONObject jsonObject=(JSONObject) list.get(i);
            	JSONObject newOn=new JSONObject();
            	newOn.put("payType", jsonObject.getString("payType"));
            	newOn.put("payNo", jsonObject.getString("tsnOrderNo"));
            	newOn.put("tradeAmount", new BigDecimal(jsonObject.getDouble("payAmount")));
            	newOn.put("tradeTime", jsonObject.getString("tradeDateTime"));
            	newOn.put("patientName", jsonObject.getString("patientName"));
            	newOn.put("patientType", jsonObject.getString("patType"));
            	newOn.put("hisNo", jsonObject.getString("hisOrderNo"));
            	newOn.put("orderState", jsonObject.getString("orderState"));
            	newOn.put("visitNumber", jsonObject.getString("patientCardNo"));
            	newList.add(newOn);
            }
		} catch (Exception e) {
			return HisResponseVo.failure("字段匹配异常");
		}
        
        return HisResponseVo.success(newList);
    }
    
    /**
     * WebApi方式调用
     * @param serverUrl
     * @param param
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceWebApi(String serverUrl, 
            Map<String, String> params){
        return HisResponseVo.failure("WebApi方式调用未实现");
    }

    /**
     * Wcf方式调用
     * @param serverUrl
     * @param param
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceWcf(String serverUrl, 
            Map<String, String> params) {
        return HisResponseVo.failure("Wcf方式调用未实现");
    }
    
    /**
     * WebService方式调用
     * @param serverUrl
     * @param param
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceWebService(String serverUrl, String functionName,
            Map<String, String> param) {
        Map<String,String> parms = new HashMap<>();
        logger.info("###### WebService 接口参数：{}",param.toString());
        parms.put("OrderId",param.get("businessNo"));
        parms.put("BeginDate",param.get("tradeTime"));
        parms.put("EndDate",param.get("tradeTime"));

        String xmlData = "";
        try {
            xmlData = WebServiceClientUtilExtend.mapToXml(parms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("--xmlData 为:"+xmlData);
        TradeHISDetailVo tradeHISDetailVo = new TradeHISDetailVo();
        String result="";
        try{
            //调用webservice服务的地址，调用之前可以先用浏览器访问下，看是否能够正常访问
            org.apache.axis.client.Service service = new org.apache.axis.client.Service();
            //通过service创建call对象
            Call call = (Call)service.createCall();
            logger.info("His接口地址为：" + serverUrl);
            call.setTargetEndpointAddress(new java.net.URL(serverUrl));
            //设置调用方法
            call.setOperationName(functionName);
            call.setUseSOAPAction(true);
            //添加方法的参数，需要几个添加几个，
            //例：resultString：参数名   XSD_STRING：参数类型    IN：代表传入
            call.addParameter("Input", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
            //设置返回类型
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            try{
                //使用invoke方法
                result = (String) call.invoke(new Object[] {xmlData});
                //输出SOAP响应报文
                logger.info("--SOAP Response 响应: " + result);
            }catch(Exception e){
                logger.error("HIS详情接口调用错误：" + e.getMessage());
                logger.info("--SOAP Request 请求2: " + call.getMessageContext().getRequestMessage().getSOAPPartAsString());
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        logger.info("His接口返回数据为："+result);
        tradeHISDetailVo =parseXMLData(result,tradeHISDetailVo);
        if(tradeHISDetailVo == null){
            return HisResponseVo.failure("HIS账单拉取错误：没有对应交易记录");
        }
        return HisResponseVo.success(tradeHISDetailVo);
    }

    private TradeHISDetailVo parseXMLData(String dataStr, TradeHISDetailVo hisDetailVo){
        try {
            // 将字符串转为XML
            Document doc = DocumentHelper.parseText(dataStr);
            Element rootElt = doc.getRootElement(); // 获取根节点
            String respCode = rootElt.elementTextTrim("RespCode");
            if(!"10000".equals(respCode)){
                //HIS账单拉取错误
                logger.error("HIS账单拉取错误："+rootElt.elementTextTrim("RespMessage"));
                hisDetailVo = null;
            }else {
                Iterator iter = rootElt.element("Datas").elementIterator("Data");
                //遍历Data子节点
                while (iter.hasNext()) {
                    //当前数据节点
                    Element recordEle = (Element) iter.next();
                    String orderId = recordEle.elementTextTrim("OrderId");	// 全流程的支付订单
                    String refundOrderId = recordEle.elementTextTrim("RefundOrderId");	// 全流程的退款订单号
                    String wxOrderNo = recordEle.elementTextTrim("WxOrderNo");	// 微信的订单号
                    String hisOrderId = recordEle.elementTextTrim("OrderId");	// HIS的订单号
                    String payMoney = recordEle.elementTextTrim("PayMoney");	// 患者应付金额(单位：分)
                    String totalMoney = recordEle.elementTextTrim("TotalMoney");	// 缴费单应收金额(单位：分)
                    String tradeType = recordEle.elementTextTrim("TradeType");	// 交易类型（IPINV：住院交押金，OPINV：门诊窗口扫码付费，OPACC：门诊预交金充退）
                    String hisOrderState = recordEle.elementTextTrim("HisOrderState");	// His的订单状态
                    String hisBizState = recordEle.elementTextTrim("HisBizState");	// His的业务状态 P为交款，R为退款
                    String isAllowRefund = recordEle.elementTextTrim("IsAllowRefund");	// 是否允许退费
                    String tradeDate = recordEle.elementTextTrim("TradeDate");	// 交易日期
                    String tradeTime = recordEle.elementTextTrim("TradeTime");	// 交易时间
                    String patName = recordEle.elementTextTrim("PatName");	// 患者姓名
                    String patNo = recordEle.elementTextTrim("PatNo");	// 病人号
                    logger.info("### 患者姓名为：{}，病人号为：{}",patName,patNo);

                    hisDetailVo.setHisNo(hisOrderId);	//his流水号
                    hisDetailVo.setOrderState(hisOrderState);	//His的订单状态
                    hisDetailVo.setPatientNo(patNo);	//病人号
                    hisDetailVo.setPatientName(patName);	//病人姓名
                    hisDetailVo.setPatientType("OPACC".equalsIgnoreCase(tradeType) ? EnumTypeOfInt.PAT_TYPE_MZ.getValue() : "IPINV".equalsIgnoreCase(tradeType)
                            ? EnumTypeOfInt.PAT_TYPE_ZY.getValue() :EnumTypeOfInt.PAT_TYPE_QT.getValue());	//患者类型
                    hisDetailVo.setPayNo("");	//支付流水号
                    hisDetailVo.setPayType("P".equalsIgnoreCase(hisBizState) ? EnumTypeOfInt.PAY_CODE.getValue() : EnumTypeOfInt.REFUND_CODE.getValue());	//支付类型
                    hisDetailVo.setTradeAmount(payMoney);	//交易金额
                    hisDetailVo.setTradeTime(tradeDate+" " +tradeTime);	//交易时间
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hisDetailVo;
    }
    
    /**
     * Exe方式调用
     * @param exePath
     * @param param
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceExe(String command) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line = null;
            StringBuilder build = new StringBuilder();
            while ((line = br.readLine()) != null) {  
                build.append(line);  
            }
            return HisResponseVo.success(build.toString());
        } catch (IOException e) {
            logger.error("读取exe文件发生异常：", e);
            throw new ServiceException("读取exe文件发生异常" + e.getMessage());
        }
    }
    
    /**
     * Datasource方式调用
     * @param dataSource
     * @param sql
     * @param params
     * @return
     * HisResponseVo
     */
    private HisResponseVo serviceDatasource(DataSource dataSource, 
            String sql, Map<String, String> params) {
    	String hisAdd = propertiesConfigService.findValueByPkey(ProConstants.hisAdd);
    	String hisUser = propertiesConfigService.findValueByPkey(ProConstants.hisUser);
    	String hisPass = propertiesConfigService.findValueByPkey(ProConstants.hisPass);
    	String hisDriverName = propertiesConfigService.findValueByPkey(ProConstants.hisDriverName);
        logger.info("######## 准备请求His视图，sql:{},{}",sql,params.toString());
        DriverManagerDataSource driverManagerDataSource=new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(hisDriverName);
        driverManagerDataSource.setUrl(hisAdd);
        driverManagerDataSource.setUsername(hisUser);
        driverManagerDataSource.setPassword(hisPass);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        sql = String.format(sql,params.get("businessNo"));
        logger.info("######## 执行sql为：{}",sql);
        Map<String,Object> map = jdbcTemplate.queryForMap(sql);
        logger.info("######## His视图返回结果为，res:{}",map);
        map.put("tradeTime",String.valueOf(map.get("tradeTime")));
        List resultList = new ArrayList();
        resultList.add(map);
        return HisResponseVo.success(resultList);
    }
}
