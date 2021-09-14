package com.yiban.rec.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.export.util.CsvUtils;
import com.yiban.rec.dao.ExcepHandingRecordDao;
import com.yiban.rec.dao.HisTradeFlowDao;
import com.yiban.rec.dao.ThirdTradeFlowDao;
import com.yiban.rec.dao.TradeCheckDao;
import com.yiban.rec.domain.ExcepHandingRecord;
import com.yiban.rec.domain.HisTradeFlow;
import com.yiban.rec.domain.ThirdTradeFlow;
import com.yiban.rec.domain.TradeCheck;
import com.yiban.rec.service.TradeCheckService;
import com.yiban.rec.util.CommonConstant;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumType;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.XmltoObject;
import com.yiban.rec.util.ZipUtil;
import com.yiban.rec.ws.client.IPaymentService;
import com.yiban.rec.ws.client.PaymentServiceLocator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class TradeCheckServiceImpl implements TradeCheckService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TradeCheckServiceImpl.class);
	
	@Autowired
	private TradeCheckDao tradeCheckDao;
	
	@Autowired
	private ThirdTradeFlowDao thirdTradeFlowDao;
	
	@Autowired
	private HisTradeFlowDao hisTradeFlowDao;
	
	@Autowired
	private ExcepHandingRecordDao excepHandingRecordDao;
	

	@Override
	public Page<TradeCheck> getTradeCheckPage(TradeCheck tradeCheck, User user, Pageable pageable) {
		
		Specification<TradeCheck> specification = new Specification<TradeCheck>() {
			@Override
			public Predicate toPredicate(Root<TradeCheck> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = converSearch(tradeCheck,user,root, query, cb);
				if (predicates.size() > 0) {
					return cb.and(predicates.toArray(new Predicate[predicates.size()]));
				}
				return cb.conjunction();
			}
		};
		return tranData(tradeCheckDao.findAll(specification, pageable));
	}
	
	private Page<TradeCheck> tranData(Page<TradeCheck> page){
		List<TradeCheck> tradeCheckList = page.getContent();
		Map<Integer,String> map = CommonEnum.BillBalance.asMap();
		for(TradeCheck tradeCheck : tradeCheckList){
			tradeCheck.setCheckStateValue(map.get(tradeCheck.getCheckState()));
		}
		return page;
		
	}
	
	protected List<Predicate> converSearch(TradeCheck tradeCheck, User user, Root<?> root,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> predicates = Lists.newArrayList();
		
		if (!StringUtils.isEmpty(tradeCheck.getBusinessNo())) {
			Path<String> businessNoExp = root.get("businessNo");
			predicates.add(cb.equal(businessNoExp, tradeCheck.getBusinessNo()));
		}
		if (!StringUtils.isEmpty(tradeCheck.getPayNo())) {
			Path<String> payNoExp = root.get("payNo");
			predicates.add(cb.equal(payNoExp, tradeCheck.getPayNo()));
		}
		if (!StringUtils.isEmpty(tradeCheck.getOrgNo())) {
			Path<String> orgNoExp = root.get("orgNo");
			predicates.add(cb.equal(orgNoExp, tradeCheck.getOrgNo()));
		}
		if (!StringUtils.isEmpty(tradeCheck.getEquipmentNo())) {
			Path<String> equipmentNoExp = root.get("equipmentNo");
			predicates.add(cb.equal(equipmentNoExp, tradeCheck.getEquipmentNo()));
		}
		if (!StringUtils.isEmpty(tradeCheck.getHisFlowNo())) {
			Path<String> hisFlowNoExp = root.get("hisFlowNo");
			predicates.add(cb.equal(hisFlowNoExp, tradeCheck.getHisFlowNo()));
		}
		if (!StringUtils.isEmpty(tradeCheck.getTradeDate())) {
			Path<String> payDateStartExp = root.get("tradeDate");
			predicates.add(cb.equal(payDateStartExp, tradeCheck.getTradeDate()));
		}
		return predicates;
	}

	@Override
	@Transactional
	public void dataSwitch(List<MultipartFile> files,User user,String wechatPayShopNo,String wechatApplyId,
			String alipayPayShopNo,String alipayApplyId) throws Exception{
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(files)){
			try {
				String hospitalId = Configure.getPropertyBykey("yiban.projectid");
			 for(int i=0;i<files.size();i++){
				 MultipartFile mpFile = files.get(i);
				 if(!mpFile.isEmpty()){
					 //保存数据  微信交易账单有2种格式，分别两种判断方式
					 String fileName = mpFile.getOriginalFilename();
					 if(fileName.indexOf(CommonConstant.WEICHAT_PAY_FILE)>-1){
						 List<String> result = CsvUtils.getDataFromCsv(mpFile.getInputStream(),Charset.forName(CommonConstant.CODING_FORMAT_GBK));
						 weichatPay(result,user,hospitalId,wechatPayShopNo,wechatApplyId);
					 }else if(fileName.indexOf(CommonConstant.WEICHAT_PAY_FILE_MIN)>-1){
						 List<String> result = CsvUtils.getDataFromCsv(mpFile.getInputStream(),Charset.forName(CommonConstant.CODING_FORMAT));
						 weichatPayMin(result,user,hospitalId,wechatPayShopNo,wechatApplyId);
					 }else if(fileName.indexOf(CommonConstant.WEICHAT_REFUND_FILE)>-1){
						 List<String> result = CsvUtils.getDataFromCsv(mpFile.getInputStream(),Charset.forName(CommonConstant.CODING_FORMAT_GBK));
						 weichatRefund(result,user,hospitalId,wechatPayShopNo,wechatApplyId);
					 }else if(fileName.indexOf(CommonConstant.ALIPAY_PAY_FILE)>-1){
						 File file = new File(Configure.getPropertyBykey("application.upload")+mpFile.getOriginalFilename());
						 FileUtils.writeByteArrayToFile(file,IOUtils.toByteArray(mpFile.getInputStream()));
						 String str = ZipUtil.unZipFiles(Configure.getPropertyBykey("application.upload")+mpFile.getOriginalFilename(),
								 CommonConstant.CODING_FORMAT);
						 file.delete();
						 List<String> strList = XmltoObject.getObjectFromXml(str);
						 alipayPay(strList,user,hospitalId,alipayPayShopNo,alipayApplyId);
					 }else{
						 File file = new File(Configure.getPropertyBykey("application.upload")+mpFile.getOriginalFilename());
						 FileUtils.writeByteArrayToFile(file,IOUtils.toByteArray(mpFile.getInputStream()));
						 String str = ZipUtil.unZipFiles(Configure.getPropertyBykey("application.upload")+mpFile.getOriginalFilename(),
								 CommonConstant.CODING_FORMAT);
						 file.delete();
						 List<String> strList = XmltoObject.getObjectFromXml(str);
						 alipayRefund(strList,user,hospitalId,alipayPayShopNo,alipayApplyId);
					 }
					 }
				 }
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("账单保存失败"+e.getMessage());
				throw e;
			}
		 }
	}
	/**
	* @date：2017年10月30日 
	* @Description：支付宝退费账单解析
	* @param strList
	* @param user: 返回结果描述
	* @return void: 返回值类型
	* @throws
	 */
	private void alipayRefund(List<String> strList,User user,String hospitalId,String alipayPayShopNo,String alipayApplyId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(strList)){
			List<ThirdTradeFlow> ttList = new ArrayList<ThirdTradeFlow>();
			String currentDate = DateUtil.getCurrentDateString();
			for(int i=0;i<strList.size()-1;i++){
					ThirdTradeFlow tt = new ThirdTradeFlow();
					String str = strList.get(i);
					String[] arrStr = str.split(",");
					tt.setOrgNo(hospitalId);
					tt.setShopNo(alipayPayShopNo);
					tt.setApplyId(alipayApplyId);
					tt.setPaymentFlow(arrStr[11]);
					tt.setPaymentRequestFlow(arrStr[5]);
					tt.setPaymentAccount(arrStr[6]);
					tt.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[1]));
					String tradeDate = DateUtil.getNormalTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[1]).getTime());
					tt.setTradeDate(tradeDate);
					tt.setTradeResult(arrStr[10]);
					tt.setTradeName(CommonConstant.BUSSINESS_TYPE_REFUND);
					BigDecimal bd = new BigDecimal("-"+arrStr[9]);
					tt.setTradeAmount(bd);
					BigDecimal bdt = new BigDecimal("0.00");
					tt.setCounterFee(bdt);
					tt.setPaymentRefundFlow(arrStr[4]);
					tt.setPayName(EnumType.PAY_TYPE_ALIPAY.getValue());
					tt.setUserId(user.getId());
					tt.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
					tt.setCreatedDate(new Date());
					ttList.add(tt);
			}
			thirdTradeFlowDao.deleteThird(currentDate, Integer.valueOf(EnumType.PAY_TYPE_ALIPAY.getValue()), user.getId(),CommonConstant.BUSSINESS_TYPE_REFUND);
			thirdTradeFlowDao.save(ttList);
		}
	}
	
	/**
	* @date：2017年10月30日 
	* @Description：支付宝支付账单解析
	* @param strList
	* @param user: 返回结果描述
	* @return void: 返回值类型
	* @throws
	 */
	private void alipayPay(List<String> strList,User user,String hospitalId,String alipayPayShopNo,String alipayApplyId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(strList)){
			List<ThirdTradeFlow> ttList = new ArrayList<ThirdTradeFlow>();
			String currentDate = DateUtil.getCurrentDateString();
			for(int i=0;i<strList.size()-1;i++){
					ThirdTradeFlow tt = new ThirdTradeFlow();
					String str = strList.get(i);
					String[] arrStr = str.split(",");
					if(arrStr[10].toString().equals(CommonConstant.TRADE_VALUE_STATE)&&"0.00".equals(arrStr[8])){
						continue;
					}
					if(arrStr[10].toString().equals(CommonConstant.TRADE_VALUE_WAIT_PAY)){
						continue;
					}
					tt.setOrgNo(hospitalId);
					tt.setShopNo(alipayPayShopNo);
					tt.setApplyId(alipayApplyId);
					tt.setPaymentFlow(arrStr[4]);
					tt.setPaymentRequestFlow(arrStr[3]);
					tt.setPaymentAccount(arrStr[5]);
					tt.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[1]));
					String tradeDate = DateUtil.getNormalTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[1]).getTime());
					tt.setTradeDate(tradeDate);
					tt.setTradeResult(arrStr[10]);
					tt.setTradeName(CommonConstant.BUSSINESS_TYPE_PAY);
					BigDecimal bd = new BigDecimal(arrStr[7]);
					tt.setTradeAmount(bd);
					BigDecimal bdt = new BigDecimal(arrStr[9]);
					tt.setCounterFee(bdt);
					tt.setPayName(EnumType.PAY_TYPE_ALIPAY.getValue());
					tt.setUserId(user.getId());
					tt.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
					tt.setCreatedDate(new Date());
					ttList.add(tt);
				
			}
			thirdTradeFlowDao.deleteThird(currentDate, Integer.valueOf(EnumType.PAY_TYPE_ALIPAY.getValue()), user.getId(),CommonConstant.BUSSINESS_TYPE_PAY);
			thirdTradeFlowDao.save(ttList);
		}
	}
	
	/**
	* @date：2017年10月26日 
	* @Description：微信支付账单解析
	* @param listStr: 返回结果描述
	* @return void: 返回值类型
	* @throws
	 */
	private void weichatPay(List<String> listStr,User user,String hospitalId,String wechatPayShopNo,String wechatApplyId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(listStr)){
			List<ThirdTradeFlow> ttList = new ArrayList<ThirdTradeFlow>();
			String currentDate = DateUtil.getCurrentDateString();
			for(int i=3;i<listStr.size();i++){
				ThirdTradeFlow tt = new ThirdTradeFlow();
				String str = listStr.get(i);
				String[] arrStr = str.split(",");
				if(CommonConstant.TRADE_VALUE_WAITPAY.equals(arrStr[4])){
					continue;
				}
				tt.setOrgNo(hospitalId);
				tt.setShopNo(wechatPayShopNo);
				tt.setApplyId(wechatApplyId);
				tt.setPaymentFlow(arrStr[1].replace("`", ""));
				tt.setPaymentRequestFlow(arrStr[2].replace("`", ""));
				String tradeDate = DateUtil.getNormalTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")).getTime());
				tt.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")));
				tt.setTradeDate(tradeDate);
				tt.setTradeResult(arrStr[4]);
				tt.setTradeName(CommonConstant.BUSSINESS_TYPE_PAY);
				BigDecimal bd = new BigDecimal(arrStr[5]);
				tt.setTradeAmount(bd);
				BigDecimal bdt = new BigDecimal("0.00");
				tt.setCounterFee(bdt);
				tt.setPayName(EnumType.PAY_TYPE_WEICHAT.getValue());
				tt.setUserId(user.getId());
				tt.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				tt.setCreatedDate(new Date());
				ttList.add(tt);
			}
			thirdTradeFlowDao.deleteThird(currentDate, Integer.valueOf(EnumType.PAY_TYPE_WEICHAT.getValue()), user.getId(),CommonConstant.BUSSINESS_TYPE_PAY);
			thirdTradeFlowDao.save(ttList);
		}
	}
	
	private void weichatPayMin(List<String> listStr,User user,String hospitalId,String wechatPayShopNo,String wechatApplyId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(listStr)){
			List<ThirdTradeFlow> ttList = new ArrayList<ThirdTradeFlow>();
			String currentDate = DateUtil.getCurrentDateString();
			for(int i=3;i<listStr.size();i++){
				ThirdTradeFlow tt = new ThirdTradeFlow();
				String str = listStr.get(i);
				String[] arrStr = str.split(",");
				if(CommonConstant.TRADE_VALUE_WAITPAY.equals(arrStr[9])){
					continue;
				}
				tt.setOrgNo(hospitalId);
				tt.setShopNo(wechatPayShopNo);
				tt.setApplyId(wechatApplyId);
				tt.setPaymentFlow(arrStr[1].trim());
				tt.setPaymentRequestFlow(arrStr[2].trim());
				String tradeDate = DateUtil.getNormalTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")).getTime());
				tt.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")));
				tt.setTradeDate(tradeDate);
				tt.setTradeResult(arrStr[9]);
				tt.setTradeName(CommonConstant.BUSSINESS_TYPE_PAY);
				BigDecimal bd = new BigDecimal(arrStr[11]);
				tt.setTradeAmount(bd);
				BigDecimal bdt = new BigDecimal("0.00");
				tt.setCounterFee(bdt);
				tt.setPayName(EnumType.PAY_TYPE_WEICHAT.getValue());
				tt.setUserId(user.getId());
				tt.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				tt.setCreatedDate(new Date());
				ttList.add(tt);
			}
			thirdTradeFlowDao.deleteThird(currentDate, Integer.valueOf(EnumType.PAY_TYPE_WEICHAT.getValue()), user.getId(),CommonConstant.BUSSINESS_TYPE_PAY);
			thirdTradeFlowDao.save(ttList);
		}
	}
	/**
	* @date：2017年10月26日 
	* @Description：微信退费账单解析
	* @param listStr: 返回结果描述
	* @return void: 返回值类型
	* @throws
	 */
	private void weichatRefund(List<String> listStr,User user,String hospitalId,String wechatPayShopNo,String wechatApplyId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(listStr)){
			List<ThirdTradeFlow> ttList = new ArrayList<ThirdTradeFlow>();
			String currentDate = DateUtil.getCurrentDateString();
			for(int i=3;i<listStr.size();i++){
				ThirdTradeFlow tt = new ThirdTradeFlow();
				String str = listStr.get(i);
				String[] arrStr = str.split(",");
				tt.setOrgNo(hospitalId);
				tt.setShopNo(wechatPayShopNo);
				tt.setApplyId(wechatApplyId);
				tt.setPaymentFlow(arrStr[1].replace("`", ""));
				tt.setPaymentRequestFlow(arrStr[2].replace("`", ""));
				String tradeDate = DateUtil.getNormalTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")).getTime());
				tt.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", arrStr[0].length()>16?arrStr[0]:(arrStr[0]+":00").replace("/", "-")));
				tt.setTradeDate(tradeDate);
				tt.setTradeResult(arrStr[3]);
				tt.setTradeName(CommonConstant.BUSSINESS_TYPE_REFUND);
				BigDecimal bd = new BigDecimal("-"+arrStr[5]);
				tt.setTradeAmount(bd);
				tt.setPaymentRefundFlow(arrStr[8].replace("`", ""));
				BigDecimal bdt = new BigDecimal("0.00");
				tt.setCounterFee(bdt);
				tt.setPayName(EnumType.PAY_TYPE_WEICHAT.getValue());
				tt.setUserId(user.getId());
				tt.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				tt.setCreatedDate(new Date());
				ttList.add(tt);
			}
			thirdTradeFlowDao.deleteThird(currentDate, Integer.valueOf(EnumType.PAY_TYPE_WEICHAT.getValue()), user.getId(),CommonConstant.BUSSINESS_TYPE_REFUND);
			thirdTradeFlowDao.save(ttList);
		}
	}
	
	@Transactional
	@Override
	public String handleBill(String accountDate, String str,User user) throws Exception {
		//1、账单入库处理 2、账单比对处理
		try {
			String hospitalId = Configure.getPropertyBykey("yiban.projectid");
			JSONObject jsonObject = JSONObject.fromObject(str);
			LOGGER.info("获取his账单"+jsonObject.toString());
			if(!CommonConstant.TRADE_CODE_SUCCESS.equals(jsonObject.getString("Response_Code"))){
				return "获取his账单文件失败!";
			}
			JSONArray jsonArray = jsonObject.getJSONArray("HisData");
//			//同步处理插入his账单信息
			insertHisTradeFlow(accountDate,jsonArray,user,hospitalId);
			List<ThirdTradeFlow> ttList = thirdTradeFlowDao.findByTradeDateAndUserId(accountDate, user.getId());
			List<TradeCheck> tcList = new ArrayList<TradeCheck>();
			if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(ttList)){
				for(int i=ttList.size()-1;i>=0;i--){
					ThirdTradeFlow thirdTradeFlow = ttList.get(i);
					for(int j=jsonArray.size()-1;j>=0;j--){
						TradeCheck tradeCheck = new TradeCheck();
						JSONObject jb = jsonArray.getJSONObject(j);
						if(thirdTradeFlow.getPaymentRequestFlow().trim().equals(jb.getString("PayMent_Request_Flow"))&&
								thirdTradeFlow.getTradeAmount().compareTo(new BigDecimal(jb.getString("Trade_Amount")))==0){
							tradeCheck.setOrgNo(hospitalId);
							tradeCheck.setShopNo(thirdTradeFlow.getShopNo());
							tradeCheck.setApplyId(thirdTradeFlow.getApplyId());
							tradeCheck.setBusinessNo(thirdTradeFlow.getPaymentRequestFlow());
							tradeCheck.setPayNo(thirdTradeFlow.getPaymentFlow());
							tradeCheck.setHisFlowNo(jb.getString("His_Flow"));
							tradeCheck.setPayName(thirdTradeFlow.getPayName());
							tradeCheck.setTradeTime(thirdTradeFlow.getTradeTime());
							tradeCheck.setTradeDate(accountDate);
							tradeCheck.setPaymentAccount(thirdTradeFlow.getPaymentAccount());
							tradeCheck.setTradeResult(thirdTradeFlow.getTradeResult());
							tradeCheck.setTradeName(thirdTradeFlow.getTradeName());
							tradeCheck.setTradeAmount(thirdTradeFlow.getTradeAmount());
							tradeCheck.setPaymentRefundFlow(thirdTradeFlow.getPaymentRefundFlow());
							tradeCheck.setEquipmentNo(jb.getString("Equipment_No"));
							tradeCheck.setPatientNo(jb.getString("Patient_No"));
							tradeCheck.setPatientName(jb.getString("Patient_Name"));
							tradeCheck.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
							tradeCheck.setCreatedDate(new Date());
							tradeCheck.setCheckState(CommonEnum.BillBalance.zp.getValue());
							tradeCheck.setUserId(user.getId());
							tcList.add(tradeCheck);
							ttList.remove(i);
							jsonArray.remove(j);
							break;
						}
					}
				}
				handleDcThird(tcList,ttList,user,hospitalId,accountDate);
				handleDcHis(tcList,jsonArray,user,hospitalId,accountDate);
				tradeCheckDao.deleteTradeCheck(accountDate, user.getId());
				tradeCheckDao.save(tcList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("账单校验失败!"+e);
			throw new Exception("账单校验失败!"+e.getMessage());
		}
		return "账单校验成功!";
	}
	
	private void handleDcHis(List<TradeCheck> tcList,JSONArray jsonArray,User user,String hospitalId,String accountDate){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(jsonArray)){
			for(int i=0;i<=jsonArray.size()-1;i++){
				TradeCheck tradeCheck = new TradeCheck();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				tradeCheck.setOrgNo(hospitalId);
				tradeCheck.setBusinessNo(jsonObject.getString("PayMent_Request_Flow"));
				tradeCheck.setHisFlowNo(jsonObject.getString("His_Flow"));
				if(EnumType.PAY_TYPE_ALIPAY.getName().equals(jsonObject.getString("Pay_Name"))){
					tradeCheck.setPayName(EnumType.PAY_TYPE_ALIPAY.getValue());
				}else{
					tradeCheck.setPayName(EnumType.PAY_TYPE_WEICHAT.getValue());
				}
				tradeCheck.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",jsonObject.getString("Trade_Time")+":00"));
				tradeCheck.setTradeDate(accountDate);
				tradeCheck.setTradeName(jsonObject.getString("Business_Type"));
				BigDecimal bg = new BigDecimal(jsonObject.getString("Trade_Amount"));
				tradeCheck.setTradeAmount(bg);
				tradeCheck.setEquipmentNo(jsonObject.getString("Equipment_No"));
				tradeCheck.setPatientNo(jsonObject.getString("Patient_No"));
				tradeCheck.setPatientName(jsonObject.getString("Patient_Name"));
				tradeCheck.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				tradeCheck.setCreatedDate(new Date());
				tradeCheck.setCheckState(CommonEnum.BillBalance.HISDC.getValue());
				tradeCheck.setUserId(user.getId());
				tcList.add(tradeCheck);
			}
		}
		
	}
	private void handleDcThird(List<TradeCheck> tcList,List<ThirdTradeFlow> ttList,User user,String hospitalId,String accountDate){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(ttList)){
			List<ThirdTradeFlow> ttListTwoList = new ArrayList<ThirdTradeFlow>();
			for(ThirdTradeFlow tt:ttList){
				ThirdTradeFlow ttf = new ThirdTradeFlow();
				BeanUtils.copyProperties(tt, ttf);
				ttListTwoList.add(ttf);
			}
			List<String> listStr = new ArrayList<String>();
			for(int i=ttList.size()-1;i>=0;i--){
				for(int j = ttListTwoList.size()-1;j>=0;j--){
					if(ttList.get(i).getPaymentRequestFlow().equals(ttListTwoList.get(j).getPaymentRefundFlow())&&
							ttList.get(i).getTradeAmount().add(ttListTwoList.get(j).getTradeAmount()).equals(new BigDecimal("0.00"))){
						listStr.add(ttList.get(i).getPaymentRequestFlow());
					}
				}
			}
			for(int i=ttList.size()-1;i>=0;i--){
				ThirdTradeFlow thirdTradeFlow = ttList.get(i);
				for(String str : listStr){
					if(str.equals(thirdTradeFlow.getPaymentRequestFlow())||str.equals(thirdTradeFlow.getPaymentRefundFlow())){
						ttList.remove(i);
						break;
					}
				}
			}
			for(ThirdTradeFlow thirdTradeFlow :  ttList){
				TradeCheck tradeCheck = new TradeCheck();
				tradeCheck.setOrgNo(hospitalId);
				tradeCheck.setShopNo(thirdTradeFlow.getShopNo());
				tradeCheck.setApplyId(thirdTradeFlow.getApplyId());
				tradeCheck.setBusinessNo(thirdTradeFlow.getPaymentRequestFlow());
				tradeCheck.setPayNo(thirdTradeFlow.getPaymentFlow());
				tradeCheck.setHisFlowNo("");
				tradeCheck.setPayName(thirdTradeFlow.getPayName());
				tradeCheck.setTradeTime(thirdTradeFlow.getTradeTime());
				tradeCheck.setTradeDate(accountDate);
				tradeCheck.setPaymentAccount(thirdTradeFlow.getPaymentAccount());
				tradeCheck.setTradeResult(thirdTradeFlow.getTradeResult());
				tradeCheck.setTradeName(thirdTradeFlow.getTradeName());
				tradeCheck.setTradeAmount(thirdTradeFlow.getTradeAmount());
				tradeCheck.setPaymentRefundFlow(thirdTradeFlow.getPaymentRefundFlow());
				tradeCheck.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				tradeCheck.setCreatedDate(new Date());
				tradeCheck.setCheckState(CommonEnum.BillBalance.THIRDDC.getValue());
				tradeCheck.setUserId(user.getId());
				tcList.add(tradeCheck);
			}
		}
	}
	
	
	private void insertHisTradeFlow(String accountDate,JSONArray jsonArray,User user,String hospitalId){
		if(!com.yiban.rec.util.StringUtil.isNullOrEmpty(jsonArray)){
			List<HisTradeFlow> htList = new ArrayList<HisTradeFlow>();
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				HisTradeFlow histradeFlow = new HisTradeFlow();
				histradeFlow.setOrgNo(hospitalId);
				histradeFlow.setHisFlow(jsonObject.getString("His_Flow"));
				histradeFlow.setPaymentRequestFlow(jsonObject.getString("PayMent_Request_Flow"));
				histradeFlow.setPaymentFlow(jsonObject.getString("Payment_Flow"));
				if(EnumType.PAY_TYPE_ALIPAY.getName().equals(jsonObject.getString("Pay_Name"))){
					histradeFlow.setPayName(Integer.valueOf(EnumType.PAY_TYPE_ALIPAY.getValue()));
				}else{
					histradeFlow.setPayName(Integer.valueOf(EnumType.PAY_TYPE_WEICHAT.getValue()));
				}
				histradeFlow.setBusinessType(jsonObject.getString("Business_Type"));
				BigDecimal db = new BigDecimal(jsonObject.getString("Trade_Amount"));
				histradeFlow.setTradeAmount(db);
				histradeFlow.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss",jsonObject.getString("Trade_Time")+":00"));
//				histradeFlow.setTradeTime(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss","2017-11-06 02:05:45"));
				histradeFlow.setTradeDate(accountDate);
				histradeFlow.setEquipmentNo(jsonObject.getString("Equipment_No"));
				histradeFlow.setPatientNo(jsonObject.getString("Patient_No"));
				histradeFlow.setPatientName(jsonObject.getString("Patient_Name"));
				histradeFlow.setUserId(user.getId());
				histradeFlow.setIsDeleted(CommonEnum.DeleteStatus.UNDELETE.getValue());
				histradeFlow.setCreatedDate(new Date());
				htList.add(histradeFlow);
			}
			hisTradeFlowDao.deleteTradeFlow(accountDate, user.getId());
			hisTradeFlowDao.save(htList);
		}
	}
	
	@Transactional
	@Override
	public String checkRefund(Long id, User user,String handleRemark) throws Exception {
			String resultStr = "";
		try {
			TradeCheck tradeCheck = tradeCheckDao.findOne(id);
			JSONObject jb = new JSONObject();
			jb.put("Trade_Code", EnumType.TRADE_CODE_REFUND.getValue());
			jb.put("Org_No", tradeCheck.getOrgNo().toString());
			jb.put("Pay_Shop_No", tradeCheck.getShopNo());
			jb.put("Pay_App_ID", tradeCheck.getApplyId());
			jb.put("Pay_Source", EnumType.PAY_SOURCE_REFUND.getValue());
			if(EnumTypeOfInt.PAY_TYPE_WECHAT.getValue().equals(tradeCheck.getPayName())){
				jb.put("Pay_Type", EnumType.PAY_TYPE_WECHAT_REFUND.getValue());
			}else{
				jb.put("Pay_Type", EnumType.PAY_TYPE_ALIPAY_REFUND.getValue());
			}
			jb.put("Pay_Flow_No", CommonConstant.REFUND_FLAG+new Date().getTime());
			jb.put("Pay_Amount", tradeCheck.getTradeAmount());
			jb.put("Pay_Round", tradeCheck.getTradeAmount());
			jb.put("Device_No", "ZHZFPT");
			jb.put("Ori_Pay_Flow_No", tradeCheck.getBusinessNo());
			jb.put("Chk", "");
			IPaymentService iPaymentService = new PaymentServiceLocator().getBasicHttpBinding_IPaymentService();
			String result = iPaymentService.entrance(jb.toString());
			JSONObject jsonObject = JSONObject.fromObject(result);
			LOGGER.info("调用银医退费接口返回====》"+result);
			if(CommonConstant.TRADE_CODE_SUCCESS.equals(jsonObject.getString("Response_Code"))){
				tradeCheck.setCheckState(CommonEnum.BillBalance.zp.getValue());
				tradeCheckDao.save(tradeCheck);
				refundSeting(tradeCheck,handleRemark);//记录退费记录表
				resultStr = "退费成功";
			}else{
				resultStr = "退费失败！";
			}
		} catch (Exception e) {
			resultStr = "退费失败！";
			LOGGER.error("退费失败！");
			throw e;
		}
		return resultStr;
	}
	
	private void refundSeting(TradeCheck tradeCheck,String handleRemark){
		ExcepHandingRecord ehr = new ExcepHandingRecord();
		ehr.setOrgNo(tradeCheck.getOrgNo());
		ehr.setPaymentRequestFlow(tradeCheck.getBusinessNo());
		ehr.setPaymentFlow(tradeCheck.getPayNo());
		ehr.setPayName(tradeCheck.getPayName());
		ehr.setTradeAmount(tradeCheck.getTradeAmount());
		ehr.setTradeTime(tradeCheck.getTradeTime());
		ehr.setHandleRemark(handleRemark);
		ehr.setHandleDateTime(new Date());
		excepHandingRecordDao.save(ehr);
	}

	@Transactional
	@Override
	public void handlerZp(Long id) {
		tradeCheckDao.updateZp(id);
	}
}
	
