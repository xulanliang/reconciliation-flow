package com.yiban.rec.bill.parse.service.standardbill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.rec.bill.parse.service.standardbill.impl.order.QueryPayCenterOrder;
import com.yiban.rec.bill.parse.service.standardbill.impl.order.QueryUploadOrder;
import com.yiban.rec.bill.parse.util.HttpClientUtil;
import com.yiban.rec.bill.parse.util.ProConfigManager;
import com.yiban.rec.bill.parse.util.ReflectUtils;
import com.yiban.rec.bill.parse.vo.ExtraParamVo;
import com.yiban.rec.bill.parse.vo.PayOrder;
import com.yiban.rec.domain.BillSourceTermNo;
import com.yiban.rec.domain.SystemMetadata;
import com.yiban.rec.domain.log.RecLogDetails;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.LogCons;
import com.yiban.rec.util.RestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author swing
 * @date 2018年7月26日 上午10:26:17 类说明 账单解析抽象父类 将账单数据持久化到数据库中
 */
public abstract class AbstractBillParser<T> implements BillParserable {
	/**
	 * 给子类统一使用日志框架
	 */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	   
    /** GSON对象 */
    protected Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
	
	/**
	 * 持久化实体管理器
	 */
	protected EntityManager entityManager;

	protected List<ValueTextable<String>> systemList;
	
	protected String payCenterHost;
	
	protected Map<String,PayOrder> orderMap=new HashMap<>(1024);
	
	protected HashMap<String, ExtraParamVo> extraParamVoMap = new HashMap<>();

	// 放在billSource的set里面，用来删除账单
	protected Set<String> billSource = new HashSet<>();
	
	protected Map<String, BillSourceTermNo> billSourceTermNoMap = new HashMap<>();

	/**
	 * 各种账单类型统一批量写入数据库
	 */
	@Override
	public final void parse(String orgCode, String date, 
	        EntityManager entityManager, String payType) throws BillParseException {
		this.payCenterHost = ProConfigManager.getValueByPkey(entityManager, ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		this.entityManager = entityManager;
		int state = LogCons.REC_SUCCESS;
		String msg = "正常";
		
		// 得到系统来源配置数据
		findSystemCodes();
		
		Boolean findOnlineOrderFlag = Boolean
				.valueOf(ProConfigManager.getValueByPkey(entityManager, ProConstants.findOnlineOrderFlag,
						ProConstants.DEFAULT.get(ProConstants.findOnlineOrderFlag)));
		if (findOnlineOrderFlag) {
			if (EnumTypeOfInt.PAY_TYPE_WECHAT.getValue().equals(payType)
					|| EnumTypeOfInt.PAY_TYPE_ALIPAY.getValue().equals(payType)) {
				// 得到线上订单集合
				try {
					initPayOrders(orgCode, payType, date);
				} catch (Exception e) {
					logger.error("获取线上订单集合发生异常：", e.getMessage());
				}
				
				extraParamOrder(orgCode, date);
			}
		}
		
		loadBillSourceTermNoMap(orgCode);
		
	    List<T> list = null;
		// 再解析账单
		try{
			list = doParse(orgCode, date);
		}catch(Exception e){
		    msg = e.getMessage();
		    state = LogCons.REC_FAIL;
		    logger.error("拉取解析账单发生异常：", e);
			throw e;
		}finally {
            saveRecLog(orgCode, date, state, msg, payType);
        }
		// 账单入库
		if (list != null && list.size() > 0) {
			// 先清除数据
			clearBill(orgCode,date,entityManager,payType);
			logger.info("渠道入库条数："+list.size());
			batchInsert(list);
		}
	}
	/**
	 * 加载渠道设备号映射map
	 * @param orgCode
	 * @author jxl
	 * @Time 2020年11月11日下午7:24:30
	 */
	@SuppressWarnings("unchecked")
	private void loadBillSourceTermNoMap(String orgCode) {
		Session session = entityManager.unwrap(org.hibernate.Session.class);
		StringBuffer sb = new StringBuffer("select * from t_billsource_termno");
        Query query = session.createSQLQuery(sb.toString()).addEntity(BillSourceTermNo.class);
        List<BillSourceTermNo> list = (List<BillSourceTermNo>)query.list();
        Map<String, BillSourceTermNo> map = new HashMap<>();
		if (list != null && list.size() > 0) {
			for (BillSourceTermNo v : list) {
				map.put(v.getTermNo(), v);
			}
		}
		billSourceTermNoMap = map;
	}

	private void extraParamOrder(String orgCode, String date) {
		// 获取线上账单的患者名称，卡号等信息
		String beginTime = date.trim() + " 00:00:00";
		String endTime = date.trim() + " 23:59:59";
		HashMap<String, ExtraParamVo> map = new QueryPayCenterOrder(payCenterHost + "/pay/billLog/query/list", orgCode,
				beginTime, endTime).query();
		extraParamVoMap = map != null ? map : new HashMap<>();
		HashMap<String, ExtraParamVo> uploadMap = new QueryUploadOrder(orgCode, date, entityManager).query();
		extraParamVoMap.putAll(uploadMap);
		logger.info("扩展字段信息翻译订单量：{}", extraParamVoMap.size());
	}
	
	@SuppressWarnings("serial")
	private void initPayOrders(String orgCode,String payChannel,String date) throws Exception{
		long t = System.currentTimeMillis();
		if(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue().equals(payChannel)) {//微信
			payChannel=EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue();
		}else {//支付宝
			payChannel=EnumTypeOfInt.PAY_CHANNEL_ALIPAY.getValue();
		}
		Map<String, Object> map = new HashMap<String, Object>(200);
		Set<String> orgCodeSet=new HashSet<String>();
		//设置结构
		if(StringUtils.isNotBlank(orgCode) && !orgCode.equals("0")){
			orgCodeSet.add(orgCode);
			Session session = entityManager.unwrap(org.hibernate.Session.class);
			StringBuffer sb = new StringBuffer(" select * from t_organization t where t.code="+orgCode);
	        Query query = session.createSQLQuery(sb.toString()).addEntity(Organization.class);
	        Organization org = (Organization) query.uniqueResult();
			if(org !=null){
				for(Organization child:org.getChildren()){
					orgCodeSet.add(child.getCode());
				}
			}
		}
		map.put("orgCode",	StringUtils.join(orgCodeSet, ","));
		map.put("payChannel", payChannel);
		map.put("date", date);
		String retStr = new RestUtil().doPost(payCenterHost+"/pay/billupload/orgCodes", map, CommonConstant.CODING_FORMAT);
		if(StringUtils.isNotEmpty(retStr)){
			Map<String,Object> jsonMap=gson.fromJson(retStr, new TypeToken<Map<String,Object>>() {}.getType());
			List<PayOrder> orderList =gson.fromJson(JSON.toJSONString(jsonMap.get("orders")), new TypeToken<List<PayOrder>>() {}.getType());
			if(orderList !=null){
				for (PayOrder order : orderList) {
					orderMap.put(order.getPayFlowNo(), order);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("同步订单,请求参数:{},返回订单数{},耗时{}毫秒",map,orderMap.size(), (System.currentTimeMillis() - t));
		}
	}
	
	protected String initOnePayOrders(String tsn){
//		Boolean findOnlineOrderFlag = Boolean.valueOf(CommonPropertiesUtils.getValue("find.online.order.flag", "true"));
		Boolean findOnlineOrderFlag = Boolean
				.valueOf(ProConfigManager.getValueByPkey(entityManager, ProConstants.findOnlineOrderFlag,
						ProConstants.DEFAULT.get(ProConstants.findOnlineOrderFlag)));
		if (findOnlineOrderFlag) {
			Map<String, Object> vo = new HashMap<String, Object>(200);
			vo.put("tsn",	tsn);
			vo.put("outTradeNo",	tsn);
			String retStr =null;
			try {
				retStr =HttpClientUtil.doPostJson(payCenterHost+"/pay/billLog/query/list", gson.toJson(vo).toString());
				if(StringUtils.isNotBlank(retStr)&&!retStr.equals("[]")&&retStr.length()<=64) {
					JSONArray jsons = JSONArray.fromObject(retStr);
					if(jsons!=null&&jsons.size()>0) {
						JSONObject json = jsons.getJSONObject(0);
						retStr=json.containsKey("out_trade_no")?json.getString("out_trade_no"):null;
					}else {
						retStr=null;
					}
				}else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("线上拉取单笔出错：tsn="+tsn);
				return null;
			}
			return retStr;
		}else {
			return null;
		}
		
	}

	/*private List<BillUpload> findBillUpload(String benginDate, String endDate, String orgCode) {
		Set<String> fileSet=new HashSet<>();
		Set<String> orgCodeSet=new HashSet<String>();
		List<BillUpload> resultList=new ArrayList<>();
		String url=payCenterHost+"/pay/billupload/getUploadBill";
		//设置结构
		if(StringUtils.isNotBlank(orgCode) && !orgCode.equals("0")){
			orgCodeSet.add(orgCode);
			Session session = entityManager.unwrap(org.hibernate.Session.class);
			StringBuffer sb = new StringBuffer(" select * from t_organization t where t.code="+orgCode);
	        Query query = session.createSQLQuery(sb.toString()).addEntity(Organization.class);
	        Organization org = (Organization) query.uniqueResult();
			if(org !=null){
					for(Organization child:org.getChildren()){
						orgCodeSet.add(child.getCode());
					}
			}
		}
		
		Map<String,Object> map = new HashMap<String,Object>(10);
		map.put("benginDate", benginDate);
		map.put("endDate", endDate);
		if(orgCodeSet.size() >0){
			map.put("orgCodes",	StringUtils.join(orgCodeSet, ","));
		}
		
		String retStr=null;
		try {
			retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
		
		}catch(ConnectException ex){
			if (logger.isInfoEnabled()) {
				logger.info("连接超时,请检查网络:{}",url);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isNotEmpty(retStr)){
			JSONArray jsonArray = JSONArray.fromObject(retStr);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				String fileId=json.getString("fileId");
				Integer stat=json.getInt("uploadState");
				if(StringUtils.isNotEmpty(fileId) && stat != null && EnumTypeOfInt.HANDLE_SUCCESS.getId()==stat.intValue()){
					String fileName=Md5Util.getMD5(fileId.trim());
					if(!fileSet.contains(fileName)){
						fileSet.add(fileName);
						String[] dateFormats = new String[] { "yyyy-MM-dd" };
						JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(dateFormats));
						BillUpload upload = (BillUpload) JSONObject.toBean(json, BillUpload.class);
						resultList.add(upload);
					}
				}
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("查找账单上传记录,开始日期:{},结束日期:{},机构:{},记录总数:{}",benginDate,endDate,orgCodeSet,resultList.size());
		}
	    return resultList;
	}*/
	
	/**
	 * 保存日志明细
	 * @param orgCode
	 * @param date
	 * @param state
	 * @param msg
	 * @param payType
	 * void
	 */
	private void saveRecLog(String orgCode, String date, 
            int state, String msg ,String payType) {
        RecLogDetails recLogDetails = new RecLogDetails();
        recLogDetails.setOrgCode(orgCode);
        recLogDetails.setRecState(state);
        recLogDetails.setExceptionRemark(msg);
        recLogDetails.setOrderDate(date);
        String logType = LogCons.LOG_TYPE_BILL;
        recLogDetails.setLogType(logType);
        recLogDetails.setPayType(payType);
        recLogDetails.setCreatedDate(DateUtil.getCurrentDateTime());
        StringBuffer sb = new StringBuffer("DELETE FROM t_rec_log_details");
        sb.append(" WHERE 1=1");
        sb.append(" AND org_code='"+orgCode+"'");
        sb.append(" AND order_date = '"+date+"'");
        sb.append(" AND log_type = '"+logType+"'");
        sb.append(" AND pay_type = '"+payType+"'");
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        SQLQuery query = session.createSQLQuery(sb.toString());
        query.executeUpdate();
        entityManager.persist(recLogDetails);
        entityManager.flush();
        entityManager.clear();
    }
	
	
	//得到字典与系统来源对应配置
	@SuppressWarnings("unchecked")
	private void findSystemCodes() {
		 Session session = entityManager.unwrap(org.hibernate.Session.class);
		 StringBuffer sb = new StringBuffer("select * from t_system_metadata");
         Query query = session.createSQLQuery(sb.toString()).addEntity(SystemMetadata.class);
         List<SystemMetadata> list = (List<SystemMetadata>)query.list();
         List<ValueTextable<String>> result = Lists.newArrayList();
 		 if (list != null && list.size() > 0) {
 			for (SystemMetadata v : list) {
 				result.add(new ValueText<>(v.getSystemCode(), v.getMetaDataCode()));
 			}
 		 }
 		systemList=result;
	}

	/**
	 * 具体的账单解析逻辑由子类根据实际情况实现
	 * 
	 * @return
	 */
	protected abstract List<T> doParse(String orgCode, String date) throws BillParseException;

	/**
	 * 清除已有的账单数据
	 * 
	 * @param orgCode
	 * @param date
	 */
	protected void clearBill(String orgCode, String date,EntityManager entityManager,String payType) {
    	
    	// get class
    	Class<?> tClass = ReflectUtils.getActualTypeArgument(this.getClass());
    	Map<String, String> map = ReflectUtils.getTableName(tClass);
    	
    	//get parameters 
    	String tableName = map.get("tableName");
    	if(StringUtils.isNotBlank(tableName)) {
    		String sDate = date + " 00:00:00";
    		String eDate = date + " 23:59:59";
    		String payTypeSql = getPayTypeSql(payType,tableName);
    		StringBuffer sb = new StringBuffer();
    		sb.append("DELETE FROM " + tableName);
    		sb.append(" WHERE org_no = '" + orgCode +"'");
    		sb.append(" AND Trade_datatime >= '"+ sDate +"'");
    		sb.append(" AND Trade_datatime <= '"+ eDate +"'");
    		sb.append(payTypeSql);
    		if(null == billSource || billSource.isEmpty()) {
    			sb.append(" AND bill_source = '" + EnumTypeOfInt.BILL_SOURCE_SELF.getValue() + "'");
    		}else {
    		    sb.append(getBillSourceSql(billSource));
    		}
			String sql = sb.toString();
			logger.info("clearBill sql = " + sql);
			// get session
			Session session = entityManager.unwrap(org.hibernate.Session.class);
			SQLQuery query = session.createSQLQuery(sql);
			int count = query.executeUpdate();
			logger.info("clearBill count = " + count);
    	}
	}
	
	/**
	 * 获取billSource类型
	 * @param billSource
	 * @return
	 * String
	 */
	protected String getBillSourceSql(Set<String> billSource) {
	    if(null == billSource || billSource.isEmpty()) {
	        return "";
	    }
	    StringBuffer sb = new StringBuffer(" AND bill_source in(");
	    for (String b : billSource) {
	    	sb.append("'"+ b +"',");
		}
	    if(sb != null && sb.length()>0){
	    	sb.deleteCharAt(sb.length()-1);
	    }
	    sb.append(")");
	    return sb.toString();
	}
	
	/**
	 * 根据机构编码返回机构和此机构的子机构
	 * 
	 * @param orgCode
	 * @return
	 */
	protected Set<String> getOrgCodeSet(String orgCode) {
		return null;
	}
	
	protected void setBillSource(Set<String> billSource2) {
		this.billSource.addAll(billSource2);
    }

	protected void addBillSource(String billSource){
		if(this.billSource==null){
			this.billSource = new HashSet<>();
		}
		this.billSource.add(billSource);
	}
	
	/**
	 * 批量提交
	 * 
	 * @param list
	 */
	protected final void batchInsert(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            entityManager.persist(list.get(i));
            if (i % 30 == 0) {
                entityManager.flush();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
	
	protected String getPayTypeSql(String payType,String tableName){
    	if(StringUtil.isNullOrEmpty(payType)){
    		return "";
    	}
    	if(StringUtils.isNotBlank(tableName)&&tableName.equals("t_rec_histransactionflow")) {
//    		return " AND pay_type = '"+payType+"' ";
    		return "";
    	}
    	return " AND rec_pay_type = '"+payType+"' ";
    }
	
	/**
	 * 通过终端号获取渠道
	 * @param termNo
	 * @return
	 * @author jxl
	 * @Time 2020年11月11日下午7:33:15
	 */
	protected String getBillSourceByTermNo(String termNo){
		BillSourceTermNo vo = billSourceTermNoMap.get(termNo);
		if(vo!=null){
			return vo.getBillSource();
		}
		return "";
	}
	/**
	 * 通过终端号获取支付类型
	 * @param termNo
	 * @return
	 * @author jxl
	 * @Time 2020年11月11日下午7:34:18
	 */
	protected String getPayTypeByTermNo(String termNo){
		BillSourceTermNo vo = billSourceTermNoMap.get(termNo);
		if(vo!=null){
			return vo.getPayType();
		}
		return "";
	}
}
