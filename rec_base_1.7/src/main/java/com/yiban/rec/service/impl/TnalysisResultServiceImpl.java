package com.yiban.rec.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueText;
import com.yiban.framework.core.domain.base.ValueTextable;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.service.samba.SambaService;
import com.yiban.framework.dict.dao.MetaDataDao;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.rec.dao.HisPayResultDao;
import com.yiban.rec.dao.PayTypeDao;
import com.yiban.rec.dao.ThirdBillDao;
import com.yiban.rec.dao.TnalysisResultDao;
import com.yiban.rec.domain.HisPayResult;
import com.yiban.rec.domain.PayType;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.domain.log.TnalysisResult;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.TnalysisResultService;
import com.yiban.rec.service.base.BaseOprService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.RestUtil;
import com.yiban.rec.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Transactional(readOnly=true)
public class TnalysisResultServiceImpl extends BaseOprService implements TnalysisResultService {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private TnalysisResultDao tnalysisResultDao;
	@Autowired
	private PayTypeDao payTypeDao;
	
	@Autowired
	private SambaService sambaService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private ThirdBillDao thirdBillDao;
	
	@Autowired
	private HisPayResultDao hisPayResultDao;
	
	@Autowired
	private MetaDataDao metaDataDao;

	@Override
	public Page<TnalysisResult> getTnalysisResultList(PageRequest pagerequest, String orgCode) {
		Specification<TnalysisResult> specification = new Specification<TnalysisResult>() {
			@Override
			public Predicate toPredicate(Root<TnalysisResult> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch( orgCode,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tnalysisResultDao.findAll(specification, pagerequest);
	}

	protected List<Predicate> converSearch( String orgCode, Root<TnalysisResult> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		if (!StringUtil.isNullOrEmpty(orgCode)) {
			Path<String> orgCodeExp = root.get("orgCode");
			predicates.add(cb.equal(orgCodeExp, orgCode));
		}
		return predicates;
	}

/*	ExecutorService pool = Executors.newFixedThreadPool(10);
	@Override
	@Transactional
	public void tnalysisThread(JSONArray jsonArray,String beforeDate,Integer sourceType) {
		if(!StringUtil.isNullOrEmpty(jsonArray)){
			String id=saveLog(beforeDate,sourceType);
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			
				if(sourceType==1) {
					pool.execute(new Runnable() {
						@Override
						public void run() {
							tanlysisFile(jsonObject,id);
						}
					});
				}else {
					tanlysisFile(jsonObject,id);
				}
				
			}
		}
		
	}*/
	
	//写入主日志
	private String saveLog(String beforeDate ,Integer sourceType) {
		String url = Configure.getPropertyBykey("pay.center.url");
		String urlThill = url+"/pay/billLog/saveLog";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("logType", 2);
		map.put("sourceType", sourceType);
		map.put("billTime",beforeDate);
		String id=null;
		try {
			id=new RestUtil().doPost(urlThill, map, CommonConstant.CODING_FORMAT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	//写入明细日志
	private String saveAnalysisLog(String parentId,Integer channelType,String information,String orgCode,Integer analysisCount) {
		String url = Configure.getPropertyBykey("pay.center.url");
		String urlThill = url+"/pay/billLog/saveAnalysisLog";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("parentId", parentId);
		map.put("channelType", channelType);
		map.put("information", information);
		map.put("orgCode", orgCode);
		map.put("analysisCount", analysisCount);
		String id=null;
		try {
			id=new RestUtil().doPost(urlThill, map, CommonConstant.CODING_FORMAT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 
	* @author： fangzuxing
	* @date：2017年6月17日 
	* @Description：根据上传服务的状态，以及文件id解析文件保存至对账表中
	* @param jsonObject: 返回结果描述
	* @return void: 返回值类型
	* @throws
	 */
	@Transactional
	private void tanlysisFile(JSONObject jsonObject,String id){
		try {
			/*TnalysisResult tsr = new TnalysisResult();
			TnalysisResult trdb = tnalysisResultDao.findByOrgCodeAndPayChannelAndOrderDate(jsonObject.getString("orgCode"),jsonObject.getString("payChannel"),DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));
			tnalysisResultDao.deleteResult(jsonObject.getString("orgCode"),jsonObject.getString("payChannel"),DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));*/
			if(EnumTypeOfInt.HANDLE_SUCCESS.getId().toString().equals(jsonObject.get("uploadState").toString())){
				List<ThirdBill> list = tnalysisFileResult(jsonObject,id);
				thirdBillDao.save(list);
				/*//文件解析 ,返回解析结果
				if(!StringUtil.isNullOrEmpty(trdb)){
					trdb.setSystemCode(jsonObject.getString("systemCode"));
					trdb.setOrgCode(jsonObject.getString("orgCode"));
					trdb.setPayChannel(jsonObject.getString("payChannel"));
					trdb.setState(list.size()>0?EnumTypeOfInt.HANDLE_SUCCESS.getId():EnumTypeOfInt.HANDLE_FAIL.getId());//解析结果
					trdb.setCreateDate(new Date());
					trdb.setOrderDate(DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));
					trdb.setFileId(StringUtil.isNullOrEmpty(jsonObject.get("fileId"))?"":jsonObject.getString("fileId"));
					tnalysisResultDao.save(trdb);
				}else{
					List<ThirdBill> list = tnalysisFileResult(jsonObject,id);
					thirdBillDao.save(list);
					tsr.setSystemCode(jsonObject.getString("systemCode"));
					tsr.setOrgCode(jsonObject.getString("orgCode"));
					tsr.setPayChannel(jsonObject.getString("payChannel"));
					tsr.setState(list.size()>0?EnumTypeOfInt.HANDLE_SUCCESS.getId():EnumTypeOfInt.HANDLE_FAIL.getId());//解析结果
					tsr.setCreateDate(new Date());
					tsr.setOrderDate(DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));
					tsr.setFileId(StringUtil.isNullOrEmpty(jsonObject.get("fileId"))?"":jsonObject.getString("fileId"));
					tnalysisResultDao.save(tsr);
				}*/
			}else{
				Integer type=null;
				if(EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue().equals(jsonObject.getString("payChannel")))type=1;
				else type=2;
				saveAnalysisLog(id,type,"上传账单失败或者没有账单数据，无账单解析",jsonObject.getString("orgCode"),0);
			}/*else{
				Integer type=null;
				if(!StringUtil.isNullOrEmpty(trdb)){
					trdb.setSystemCode(jsonObject.getString("systemCode"));
					trdb.setOrgCode(jsonObject.getString("orgCode"));
					trdb.setPayChannel(jsonObject.getString("payChannel"));
					trdb.setState(jsonObject.getInt("uploadState"));//解析结果
					trdb.setCreateDate(new Date());
					trdb.setOrderDate(DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));
					trdb.setFileId(StringUtil.isNullOrEmpty(jsonObject.get("fileId"))?"":jsonObject.getString("fileId"));
					if(EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue().equals(jsonObject.getString("payChannel")))type=1;
					else type=2;
					saveAnalysisLog(id,type,"上传账单失败或者没有账单数据，无账单解析",jsonObject.getString("orgCode"),0);
					tnalysisResultDao.save(trdb);
				}else{
					tsr.setSystemCode(jsonObject.getString("systemCode"));
					tsr.setOrgCode(jsonObject.getString("orgCode"));
					tsr.setPayChannel(jsonObject.getString("payChannel"));
					tsr.setState(jsonObject.getInt("uploadState"));
					tsr.setCreateDate(new Date());
					tsr.setOrderDate(DateUtil.transferStringToDate("yyyy-MM-dd",jsonObject.getString("orderDate")));
					tsr.setFileId(StringUtil.isNullOrEmpty(jsonObject.get("fileId"))?"":jsonObject.getString("fileId"));
					if(EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue().equals(jsonObject.getString("payChannel")))type=1;
					else type=2;
					saveAnalysisLog(id,type,"上传账单失败或者没有账单数据，无账单解析",jsonObject.getString("orgCode"),0);
					tnalysisResultDao.save(tsr);
				}
			}*/
		} catch (Exception e) {
			saveAnalysisLog(id,null,e.getMessage(),null,0);
			logger.error("保存账单解析状态失败！"+e.getMessage());
		}
	}
	
	/**
	* @date：2017年6月17日 
	* @Description：根据文件id解析文件
	* @param fileId
	* @return: 返回结果描述
	* @return int: 账单上传状态  71：成功   72：失败 150:没有账单数据
	* @throws
	 */
	private List<ThirdBill> tnalysisFileResult(JSONObject jsonObject,String id){
		String fileUser = Configure.getPropertyBykey("file_user");
		String filepassword = Configure.getPropertyBykey("file_password");
		String fileIp = Configure.getPropertyBykey("file_ip");
		List<Object> list = new ArrayList<Object>();
		List<ThirdBill> listT=new ArrayList<ThirdBill>();
		try {
			InputStream in = sambaService.getFileDefault(jsonObject.getString("fileId"), fileUser, filepassword, fileIp);
			if(EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue().equals(jsonObject.getString("payChannel"))){//微信
				String str = IOUtils.toString(in);  
			   String[] arr = str.split("\n");
			   if(!StringUtil.isNullOrEmpty(arr)) {
					for (int i = 0; i < arr.length; i++) {
						list.add(arr[i]);
					}
				}
				listT = tnalysisWeiChatToDb(list, jsonObject);
				saveAnalysisLog(id, 1, EnumTypeOfInt.HANDLE_SUCCESS.getValue(), jsonObject.getString("orgCode"),
						listT.size());
			} else if (EnumTypeOfInt.PAY_CHANNEL_ALIPAY.getValue().equals(jsonObject.getString("payChannel"))) {// 支付宝
				String str = IOUtils.toString(in, "GBK");
				String[] arr = str.split("\n");
				if (!StringUtil.isNullOrEmpty(arr)) {
					for (int i = 0; i < arr.length; i++) {
						list.add(arr[i]);
					}
				}
				listT = tnalysisAlipayToDb(list, jsonObject);
				saveAnalysisLog(id, 2, EnumTypeOfInt.HANDLE_SUCCESS.getValue(), jsonObject.getString("orgCode"),
						listT.size());
			}
		} catch (Exception e) {
			Integer type = null;
			if (EnumTypeOfInt.PAY_CHANNEL_WECHAT.getValue().equals(jsonObject.getString("payChannel")))
				type = 1;
			else
				type = 2;
			logger.error("账单解析失败" + e.getMessage());
			e.printStackTrace();
			saveAnalysisLog(id, type, e.getMessage(), jsonObject.getString("orgCode"), 0);
			return null;
		}
		return listT;
	}

	@Transactional
	private List<ThirdBill> tnalysisWeiChatToDb(List<Object> list, JSONObject jsonObject) throws Exception {
		String systemCode = jsonObject.getString("systemCode");
		String orgCode = jsonObject.getString("orgCode");
		// 得到payType对应map集合
		Map<String, String> payMap = ValueTexts.asMap(getPayCodeMap());
		Map<String, String> typeMap = ValueTexts.asMap(getTypeCodeMap());
		List<ThirdBill> listBill = new ArrayList<ThirdBill>();
		try {
			if (!StringUtil.isNullOrEmpty(list)) {
				Set<String> tsn = new HashSet<>();
				int num = 0;
				for (int i = 1; i < list.size() - 3; i++) {// 根据解析出来文件的数据格式
					ThirdBill tb = new ThirdBill();
					Object o = list.get(i);
					String[] arr = String.valueOf(o).split(",`");
					if (tsn.contains(String.valueOf(arr[5]))) {// 判断是否重复
						if (String.valueOf(arr[9]).equals("REFUND")) {// 判断是否退费
							listBill.remove(num - 1);
							num = num - 1;
						} else {
							continue;
						}
					} else {
						tsn.add(String.valueOf(arr[5]));
					}
					String payFlowNo = arr[5];
					// 得到订单表里面的一些字段
					JSONObject order = getOrder(payFlowNo);
					if (order != null) {
						String payCode = order.getString("payCode");
						String refundState = order.getString("refundState");
						String sellerAccount = order.getString("sellerAccount");
						tb.setOrgNo(order.getString("orgCode"));
						if (StringUtils.isNotBlank(sellerAccount))
							tb.setPayAccount(sellerAccount);
						if (StringUtils.isNotBlank(payCode))
							tb.setPayType(payMap.get(payCode));
						if (StringUtils.isNotBlank(payCode))
							tb.setRecPayType(typeMap.get(payCode));
						if (StringUtils.isNotBlank(refundState))
							tb.setRefundState(refundState);
					} else {
						if (String.valueOf(arr[9]).equals("REFUND")) {// 判断是否退费
							if (String.valueOf(arr[19]).equals("SUCCESS")) {
								tb.setOrderState(EnumTypeOfInt.TRADE_CODE_REFUND.getValue());
							}
						}
						// 订单中无法找到
						tb.setPayType(EnumType.PAY_TYPE_WECHAT_REFUND.getValue());
						// 订单中无法找到
						tb.setRecPayType(EnumType.PAY_TYPE_WECHAT_REFUND.getValue());
					}
					// 判断org是否为空
					if (StringUtils.isBlank(tb.getOrgNo())) {
						tb.setOrgNo(orgCode);
					}
					// 注入金额
					if (String.valueOf(arr[9]).equals("REFUND")) {// 判断是否退费
						tb.setPayAmount(new BigDecimal(arr[16]));
					} else {
						tb.setPayAmount(new BigDecimal(arr[12]));
					}
					tb.setShopFlowNo(String.valueOf(arr[6]));
					tb.setPayFlowNo(payFlowNo);
					tb.setPaySource(payMap.get(systemCode));
					tb.setPayShopNo(arr[2]);
					tb.setTradeDatatime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arr[0].replace("`", "")));
					tb.setCreatedDate(new Date());
					tb.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
					tb.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
					listBill.add(tb);
					num++;
				}
			}
			return listBill;
		} catch (Exception e) {
			logger.error("微信账单入库解析失败" + e.getMessage());
			throw new Exception("微信账单入库解析失败！" + e.getMessage());
		}
	}

	private JSONObject getOrder(String payFlowNo) {
		String PAY_HOST = propertiesConfigService.findValueByPkey(ProConstants.payCenterUrl,
				ProConstants.DEFAULT.get(ProConstants.payCenterUrl));
		String url = PAY_HOST + "/pay/order/getOrder";
		String retStr = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("payFlowNo", payFlowNo);
		JSONObject jsonArray = null;
		try {
			retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
			JSONArray list = JSONArray.fromObject(retStr);
			if (list.size() > 0) {
				jsonArray = (JSONObject) JSONArray.fromObject(retStr).get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return jsonArray;
	}

	public List<ValueTextable<String>> getPayCodeMap() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<PayType> orgs = payTypeDao.findAll();
		if (orgs != null && orgs.size() > 0) {
			for (PayType data : orgs) {
				result.add(new ValueText<>(data.getOrderType(), data.getCode()));
			}
		}
		return result;
	}

	public List<ValueTextable<String>> getTypeCodeMap() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<PayType> orgs = payTypeDao.findAll();
		if (orgs != null && orgs.size() > 0) {
			for (PayType data : orgs) {
				result.add(new ValueText<>(data.getOrderType(), data.getType()));
			}
		}
		return result;
	}

	public String[] getTypeAndCodeMap(String type) {
		List<PayType> orgs = payTypeDao.findByType(type);
		List<String> str = new ArrayList<String>();
		String[] strings = null;
		if (orgs != null && orgs.size() > 0) {
			for (PayType data : orgs) {
				str.add(data.getCode());
			}
			strings = new String[str.size()];
			str.toArray(strings);
		}
		return strings;
	}

	@Transactional
	private List<ThirdBill> tnalysisAlipayToDb(List<Object> list, JSONObject jsonObject) throws Exception {
		String systemCode = jsonObject.getString("systemCode");
		String orgCode = jsonObject.getString("orgCode");
		// 得到payType对应map集合
		Map<String, String> payMap = ValueTexts.asMap(getPayCodeMap());
		Map<String, String> typeMap = ValueTexts.asMap(getTypeCodeMap());
		List<ThirdBill> listBill = new ArrayList<ThirdBill>();
		try {
			if (!StringUtil.isNullOrEmpty(list)) {
				Set<String> tsn = new HashSet<>();
				int num = 0;
				for (int i = 5; i < list.size() - 5; i++) {// 根据解析出来文件的数据格式
					Object o = list.get(i);
					ThirdBill tb = new ThirdBill();
					String[] arr = String.valueOf(o).split(",");
					String payFlowNo = arr[0].trim();
					if (tsn.contains(payFlowNo)) {// 判断是否重复
						if (String.valueOf(arr[2]).equals("退款\t")) {// 判断是否退费
							listBill.remove(num - 1);
							num = num - 1;
						} else {
							continue;
						}
					} else {
						tsn.add(payFlowNo);
					}
					tb.setPayFlowNo(payFlowNo);
					// 得到订单表里面的一些字段
					// TODO 以后放入机构及配置里面看会否是走我们支付来判断是否查询
					JSONObject order = getOrder(payFlowNo);
					if (order != null) {
						String payCode = order.getString("payCode");
						String refundState = order.getString("refundState");
						String sellerAccount = order.getString("sellerAccount");
						tb.setOrgNo(order.getString("orgCode"));
						if (StringUtils.isNotBlank(sellerAccount))
							tb.setPayAccount(sellerAccount);
						if (StringUtils.isNotBlank(payCode))
							tb.setPayType(payMap.get(payCode));
						if (StringUtils.isNotBlank(payCode))
							tb.setRecPayType(typeMap.get(payCode));
						if (StringUtils.isNotBlank(refundState))
							tb.setRefundState(refundState);
					} else {
						if (String.valueOf(arr[2]).equals("退款\t")) {// 判断是否退费
							tb.setOrderState(EnumTypeOfInt.TRADE_CODE_REFUND.getValue());
						}
						// 无法拿到订单类型统一变为支付宝支付
						tb.setPayType(EnumType.PAY_TYPE_ALIPAY_REFUND.getValue());
						// 无法拿到订单类型统一变为支付宝支付
						tb.setRecPayType(EnumType.PAY_TYPE_ALIPAY_REFUND.getValue());
					}
					// 注入金额
					if (String.valueOf(arr[2]).equals("退款\t")) {// 判断是否退费
						String amount = String.valueOf(arr[11]);
						if (StringUtils.isNotBlank(amount) && !amount.equals("0.00")) {
							String[] amountArr = String.valueOf(arr[11]).split("-");
							tb.setPayAmount(new BigDecimal(amountArr[1]));
						}
					} else {
						tb.setPayAmount(new BigDecimal(String.valueOf(arr[11])));
					}
					// 判断org是否为空
					if (StringUtils.isBlank(tb.getOrgNo())) {
						tb.setOrgNo(orgCode);
					}
					tb.setShopFlowNo(String.valueOf(arr[1].trim()));
					tb.setPaySource(payMap.get(systemCode));
					tb.setPayTermNo(arr[9].trim());
					tb.setTradeDatatime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arr[4]));
					tb.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
					tb.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
					tb.setCreatedDate(new Date());
					listBill.add(tb);
					num++;
				}
			}
			return listBill;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝账单入库解析失败！" + e.getMessage());
			throw new Exception("支付宝账单入库解析失败！" + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ResponseResult repeatTnalysis(Integer id, String orgCode, String payChannel, String fileId,
			String systemCode, String orderDate) {
		try {
			int resutl;
			JSONObject jsonObject = null;
			if (StringUtil.isNullOrEmpty(fileId)) {
				String url = Configure.getPropertyBykey("pay.center.url");
				url = url + "/pay/billupload/getbillInfo";
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("orgCode", orgCode);
				map.put("systemCode", systemCode);
				map.put("payChannel", payChannel);
				map.put("orderDate", orderDate);
				String retStr = new RestUtil().doPost(url, map, CommonConstant.CODING_FORMAT);
				logger.info("调用支付服务账单返回" + retStr);
				System.out.println("调用支付服务账单返回" + retStr);
				jsonObject = JSONObject.fromObject(retStr);
				resutl = 0;
			} else {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orgCode", orgCode);
				map.put("payChannel", systemCode);
				map.put("fileId", payChannel);
				map.put("orderDate", orderDate);
				jsonObject = JSONObject.fromObject(map);
				resutl = 0;
			}
			TnalysisResult tnr = tnalysisResultDao.findOne(id);
			tnr.setState(resutl);
			tnr.setFileId(StringUtil.isNullOrEmpty(fileId) ? jsonObject.getString("fileId") : fileId);
			tnalysisResultDao.save(tnr);
			if (resutl == EnumTypeOfInt.HANDLE_SUCCESS.getId()) {
				return ResponseResult.success().code(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ResponseResult.failure("服务异常");
		}
		return ResponseResult.failure("文件解析失败").code(-1);
	}

	@Transactional
	@Override
	public void tnalysisPlatThread(JSONArray jsonArray) {
		Map<String, Object> map = gatherService.getOrgIdFromCode();
		Map<String, String> metaMap = ValueTexts.asMap(asNameToId());
		if (!StringUtil.isNullOrEmpty(jsonArray)) {
			List<HisPayResult> hpList = new ArrayList<HisPayResult>();
			for (int i = 0; i < jsonArray.size(); i++) {
				HisPayResult hp = new HisPayResult();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				hp.setOrgNo(map.get(jsonObject.getString("org_code")).toString());
				hp.setPaySource(metaMap.get(jsonObject.getString("system_code")).toString());
				if (jsonObject.getInt("pay_code") == 151 || jsonObject.getInt("pay_code") == 153
						|| jsonObject.getInt("pay_code") == 155 || jsonObject.getInt("pay_code") == 157
						|| jsonObject.getInt("pay_code") == 159) {
					hp.setPayType(EnumType.PAY_TYPE_WEICHAT.getValue());
				} else {
					hp.setPayType(EnumType.PAY_TYPE_ALIPAY.getValue());
				}
				if ("payOrder".equals(jsonObject.getString("tradeType"))) {
					hp.setTradeCode(EnumTypeOfInt.TRADE_CODE_PAY.getValue());
				} else {
					hp.setTradeCode(EnumTypeOfInt.TRADE_CODE_REFUND.getValue());
				}
				hp.setPayFlowNo(jsonObject.getString("tsn"));// 支付流水号
				hp.setPayAccount(jsonObject.getString("buyer_account"));
				BigDecimal bd = new BigDecimal(jsonObject.getString("order_amount"));
				hp.setPayAmount(bd.setScale(2, BigDecimal.ROUND_HALF_UP));
				hp.setTradeDatatime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",
						DateUtil.getNormalFormatTime("yyyy-MM-dd HH:mm:ss", jsonObject.getLong("order_time"))));
				hp.setOrderState(EnumTypeOfInt.ORDER_STATE_SUCCUSS.getValue());
				hp.setBusinessFlowNo(jsonObject.getString("out_trade_no"));
//				hp.setPatientName(jsonObject.getString("contact_name"));
//				hp.setCustName(jsonObject.getString("contact_name"));
				hp.setFlowNo(jsonObject.getString("order_no"));
				hp.setIsActived(CommonEnum.IsActive.ISACTIVED.getValue());
				hp.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				hp.setCreatedDate(new Date());
				hp.setLastModifiedDate(new Date());
				hp.setCreatedById(1L);
				hp.setLastModifiedById(1L);
				hpList.add(hp);
			}
			hisPayResultDao.save(hpList);
		}

	}

	private List<ValueTextable<String>> asNameToId() {
		List<ValueTextable<String>> result = Lists.newArrayList();
		List<MetaData> metaDatas = metaDataDao.findAll();
		if (metaDatas != null && metaDatas.size() > 0) {
			for (MetaData metaData : metaDatas) {
				result.add(new ValueText<>(metaData.getName(), metaData.getId().toString()));
			}
		}
		return result;
	}

}
