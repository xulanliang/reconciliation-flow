package com.yiban.rec.web.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.domain.vo.TradeCheckVo;
import com.yiban.rec.service.ElectronicRecService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.impl.ElectronicRecServiceImpl;
import com.yiban.rec.service.impl.WuHuExcelDatSum;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.Configure;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportDataUtil;

/**
 * 电子对账controller
 *
 * @author clearofchina
 */
@Controller
@RequestMapping(value = "admin/electronic")
public class ElectronicRecController extends CurrentUserContoller {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private ElectronicRecService electronicRecService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private GatherService gatherService;
	@Autowired
	private ReconciliationService reconciliationService;
	@Autowired
	private HospitalConfigService hospitalConfigService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private WuHuExcelDatSum wuHuExcelDatSum;


	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(ModelMap model, String orgNo, String date) {
		// 保存是否是其他页面入口进来 0不是 1是其他页面进来
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		if (orgNo != null || date != null) {
			model.put("date", date);
			model.put("orgNo", orgNo);
		} else {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("orgNo", orgCode);
			model.put("date", DateUtil.getSpecifiedDayBefore(new Date()));
		}
		model.put("isRefundExamine", hConfig.getIsRefundExamine());
		String electronicRecDetailButtonOnly = propertiesConfigService.findValueByPkey(
				ProConstants.electronicRecDetailButtonOnly,
				ProConstants.DEFAULT.get(ProConstants.electronicRecDetailButtonOnly));
		String electronicRecPatTypeDisplay = propertiesConfigService.findValueByPkey(
				ProConstants.electronicRecPatTypeDisplay,
				ProConstants.DEFAULT.get(ProConstants.electronicRecPatTypeDisplay));
		model.put("electronicRecDetailButtonOnly",
				StringUtils.isNotBlank(electronicRecDetailButtonOnly) && "true".equals(electronicRecDetailButtonOnly) ? electronicRecDetailButtonOnly : false);
		model.put("electronicRecPatTypeDisplay",
				"true".equals(electronicRecPatTypeDisplay) ? electronicRecPatTypeDisplay : false);
		return autoView("reconciliation/electronicRec");
	}

	/**
	 * 电子对账汇总数据
	 *
	 * @param orgNo
	 * @param date
	 * @return
	 */
	@RequestMapping(value = "data", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult data(@RequestParam("orgNo") String orgNo, @RequestParam("startDate") String date,
							   @RequestParam("patType") String patType) {
		String startDate = null;
		String endDate = null;
		if (StringUtils.isBlank(date)) {
			startDate = DateUtil.getSpecifiedDayBefore(new Date());
			endDate = DateUtil.getSpecifiedDayBefore(new Date());
		} else {
			startDate = date;
			endDate = date;
		}
		if (StringUtil.isEmpty(orgNo)) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			orgNo = orgCode;
		}
		ResponseResult result = electronicRecService.getFollowRecMap(orgNo, startDate, endDate, patType);
		Log.info("电子对账查询返回结果：" + result);
		return result;
	}


	/**
	 * 电子对账数据汇总
	 */
	@RequestMapping(value = "newData", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult newData(@RequestParam("orgNo") String orgNo, @RequestParam("startDate") String date,
								  @RequestParam("billSource") String billSource,
								  @RequestParam("patType") String patType) {
		ResponseResult result = ResponseResult.success();
		String startDate = null;
		String endDate = null;
		if (StringUtils.isBlank(date)) {
			startDate = DateUtil.getSpecifiedDayBefore(new Date());
			endDate = DateUtil.getSpecifiedDayBefore(new Date());
		} else {
			startDate = date;
			endDate = date;
		}
		if (StringUtil.isEmpty(orgNo)) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			orgNo = orgCode;
		}
		result = electronicRecService.payDetails(orgNo, startDate, endDate, billSource, patType);
		Log.info("电子对账查询返回结果：" + result);
		return result;
	}



	/**
	 * 异常账单查询
	 *
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "exceptionTrade", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<TradeCheckFollow> findByOrgNoAndTradeDate(TradeCheckFollowVo vo) {
		PageRequest pageable = this.getRequestPageabledWithInitSort(getSortFromDatagridOrElse(new Sort(Direction.DESC, "businessNo")));
		if (StringUtils.isBlank(vo.getOrgNo())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgNo(orgCode);
		}
		Page<TradeCheckFollow> data = electronicRecService.findByOrgNoAndTradeDate(vo, pageable);
		return this.toWebUIPage(data);
	}

	/**
	 * 修改过的异常账单查询
	 *
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "exceptionTrade/new", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<Map<String, Object>> findByOrgNoAndTradeDateNew(TradeCheckFollowVo vo) {
		PageRequest pageable = this.getRequestPageabledWithInitSort(this.getIdDescSort());
		if (StringUtils.isBlank(vo.getOrgNo())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgNo(orgCode);
		}
		Page<Map<String, Object>> data = electronicRecService.findByOrgNoAndTradeDateModify(vo, pageable);
		return this.toWebUIPage(data);
	}

	/**
	 * 电子对账-异常账单详情接口
	 *
	 * @param businessNo
	 * @param orgNo
	 * @param orderState
	 * @param tradeTime
	 * @return
	 */
	@RequestMapping(value = "exceptionTrade/detail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult exceptionTradeDetail(
			@RequestParam(value = "recHisId") String recHisId,
			@RequestParam(value = "recThirdId") String recThirdId,
			@RequestParam(value = "businessNo") String businessNo,
			@RequestParam(value = "orgNo") String orgNo,
			@RequestParam(value = "billSource") String billSource,
			String orderState, String tradeTime) {
		/*if (StringUtil.isEmpty(businessNo)) {
			return ResponseResult.failure("支付方流水号不能为空");
		}*/
		return electronicRecService.getExceptionTradeDetail(recHisId, recThirdId, businessNo, orgNo, orderState, tradeTime,billSource);
	}

	@Logable(operation = "手动平账")
	@RequestMapping(value = "/dealFollow", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult dealFollow(String payFlowNo, String description,
									 @RequestParam(required = false) MultipartFile file, String checkState, String tradeAmount,
									 String orgCode, String tradeDatetime, String payType, String billSource, String patType,
									 String recHisId, String recThridId) {
		if ((description == null || description.trim().equals("")) && file == null) {
			return ResponseResult.failure("请输入原因");
		}
		if (null != description && description.length() >= 200) {
			return ResponseResult.failure("请输入文字少于200个");
		}
		User user = currentUser();
		return reconciliationService.newDealFollow(user.getName(), payFlowNo, description, file, checkState, tradeAmount,
				orgCode, tradeDatetime,payType,billSource, patType, recHisId, recThridId);
	}

	@Logable(operation = "已处理差额")
	@RequestMapping(value = "/updateDifferenceAmount", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateDifferenceAmount(String orgNo, String startDate) {
		return reconciliationService.updateDifferenceAmount(startDate, orgNo);
	}

	@Logable(operation = "查询图片")
	@RequestMapping("/readImage")
	public void readImage(@RequestParam(required = true) String adress, HttpServletRequest request,
						  HttpServletResponse response) {
		String fileLocation = Configure.getPropertyBykey("file.location");
		fileLocation = fileLocation + adress;
		File file = new File(fileLocation);
		if (file != null && file.exists()) {
			try {
				response.getOutputStream().write(FileUtils.readFileToByteArray(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 页面显示的是合并后的账单，导出的是没合并之前的
	 */
	@Logable(operation = "导出电子对账异常账单")
//	@GetMapping("/api/dcExcel")
	public void toDcExcel1(TradeCheckFollowVo cqvo, String orgName, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Map<String, Object>> tradeCheckMapList = electronicRecService.getExportExceptionDetailBill(cqvo);
		String[] thirdTitleArray = {"支付方流水号", "HIS流水号", "支付渠道名称", "交易类型", "金额(元)",
				"患者姓名", "交易时间", "渠道名称", "状态", "处理后状态"};
		String[] cellValue = {"businessNo", "hisFlowNo", "payName", "tradeName", "tradeAmount",
				"patientName", "tradeTime", "billSource", "oriCheckState", "checkStateValue"};

		ExportDataUtil exportDataUtil = new ExportDataUtil(18, 9, thirdTitleArray, cellValue);
		String fileName = cqvo.getStartDate() + "至" + cqvo.getStartDate() + orgName + "异常账单明细";
		exportDataUtil.commonExportExcel(fileName, "异常账单明细", request, response, tradeCheckMapList);
	}

	/**
	 * 导出的Excel跟页面显示的一致
	 */
	@Logable(operation = "导出电子对账异常账单")
	@GetMapping("/api/dcExcel")
	public void toDcExcel(TradeCheckFollowVo cqvo, String orgName, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PageRequest pageable = new PageRequest(0, 200000, getSortFromDatagridOrElse(new Sort(Direction.DESC, "businessNo")));
		List<Organization> orgList = null;
		if (StringUtils.isNotBlank(cqvo.getOrgNo())) {
			orgList = organizationService.findByParentCode(cqvo.getOrgNo());
		}
		List<TradeCheckFollow> hisList = electronicRecService.exportToDcExcel(cqvo, orgList, pageable);
		//Map<String, TradeCheckVo> patIdMap = new HashMap<>();
		Map<String, TradeCheckVo> patIdMap = electronicRecService.getPatIdMap(hisList);
		//将code 转换为名称
		String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
		Map<String, String> typesMap = ValueTexts.asMap(metaDataService.NameAsList());
		Map<String, Object> orgMap = gatherService.getOrgMap();
		List<Map<String, Object>> tradeCheckMapList = new ArrayList<>();
		List<String> repeatList = new ArrayList<>();//用于存放重复的元素的list
		List<TradeCheckFollow> tmpHisList = new ArrayList<>();
		tmpHisList.addAll(hisList);
		for (int i = 0; i < tmpHisList.size() - 1; i++) {
			for (int j = tmpHisList.size() - 1; j > i; j--) {
				if (tmpHisList.get(j).getBusinessNo().equals(tmpHisList.get(i).getBusinessNo())
						&& tmpHisList.get(j).getTradeAmount().equals(tmpHisList.get(i).getTradeAmount())) {
					if ((hisCheck.contains(tmpHisList.get(j).getOriCheckState()) ? "短款" : "长款").equals("短款")
							&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState()) ? "短款" : "长款").equals("长款")) {
						if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
							repeatList.add(tmpHisList.get(j).getBusinessNo());//把相同元素加入list(找出相同的)
							tmpHisList.remove(j);//删除重复元素
						}
					} else if ((hisCheck.contains(tmpHisList.get(j).getOriCheckState()) ? "短款" : "长款").equals("长款")
							&& (hisCheck.contains(tmpHisList.get(i).getOriCheckState()) ? "短款" : "长款").equals("短款")) {
						if (!"".equals(tmpHisList.get(j).getBusinessNo())) {
							repeatList.add(tmpHisList.get(j).getBusinessNo());//把相同元素加入list(找出相同的)
							tmpHisList.remove(j);//删除重复元素
						}
					}
				}
			}
		}
		for (TradeCheckFollow tradeCheckFollow : hisList) {
			tradeCheckFollow.setPayName(typesMap.get(tradeCheckFollow.getPayName()));
			tradeCheckFollow.setBillSource(typesMap.get(tradeCheckFollow.getBillSource()));
			tradeCheckFollow.setOrgNo((String) orgMap.get(cqvo.getOrgNo()));
			if(hisCheck.contains(tradeCheckFollow.getOriCheckState())) {
				tradeCheckFollow.setTradeAmount(tradeCheckFollow.getTradeAmount().setScale(2).abs());
			}
			tradeCheckFollow.setTradeName(typesMap.get(tradeCheckFollow.getTradeName()));
			HashMap<String, Object> tradeCheckMap = new HashMap<>();
			String businessNo = tradeCheckFollow.getBusinessNo();
			tradeCheckMap.put("businessNo", businessNo);
			tradeCheckMap.put("hisFlowNo", patIdMap.get(businessNo) == null ? tradeCheckFollow.getHisFlowNo() : patIdMap.get(businessNo).getHisFlowNo());
			tradeCheckMap.put("payName", tradeCheckFollow.getPayName());
			tradeCheckMap.put("tradeName", tradeCheckFollow.getTradeName());
			tradeCheckMap.put("tradeAmount", tradeCheckFollow.getTradeAmount());
			tradeCheckMap.put("patientName", patIdMap.get(businessNo) == null ? tradeCheckFollow.getPatientName() : patIdMap.get(businessNo).getCustName());
			tradeCheckMap.put("tradeTime", DateUtil.transferDateToDateFormat("yyyy-MM-dd HH:mm:ss", tradeCheckFollow.getTradeTime()));
			tradeCheckMap.put("channelName", tradeCheckFollow.getBillSource());
			tradeCheckMap.put("checkStateValue", tradeCheckFollow.getCheckStateValue());
			tradeCheckMap.put("oriCheckState", hisCheck.contains(tradeCheckFollow.getOriCheckState()) ? "短款" : "长款");
			tradeCheckMap.put("patId", patIdMap.get(businessNo) == null ? null : patIdMap.get(businessNo).getPatId());
			tradeCheckMap.put("businessFlowNo", tradeCheckFollow.getShopFlowNo());
			tradeCheckMap.put("billSource", getBillSource(tradeCheckFollow.getTradeName(), hisCheck.contains(tradeCheckFollow.getOriCheckState()) ? "短款" : "长款", tradeCheckFollow.getBillSource()));
			tradeCheckMap.put("remark", getRemark(tradeCheckFollow, repeatList));
			tradeCheckMapList.add(tradeCheckMap);
		}
		String[] thirdTitleArray = {"支付方流水号", "商户流水号", "HIS流水号", "患者ID", "患者姓名", "交易类型", "交易金额(元)",
				"交易时间", "支付类型", "渠道名称", "异常类型","当前状态", "            原因            "};
		String[] cellValue = {"businessNo", "businessFlowNo", "hisFlowNo", "patId", "patientName", "tradeName", "tradeAmount",
				"tradeTime", "payName", "channelName", "oriCheckState","checkStateValue", "remark"};
		ExportDataUtil exportDataUtil = new ExportDataUtil(18, thirdTitleArray.length - 1, thirdTitleArray, cellValue);
		String fileName = cqvo.getStartDate() + "至" + cqvo.getStartDate() + orgName + "异常账单明细";
		exportDataUtil.commonExportExcel(fileName, "异常账单明细", request, response, tradeCheckMapList);
	}

	/**
	 * 获取账单来源
	 *
	 * @param tradeName     交易类型
	 * @param oriCheckState 长短款状态
	 * @param channelName   渠道名称
	 * @return
	 */
	private String getBillSource(String tradeName, String oriCheckState, String channelName) {
		if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
			return channelName;
		}
		if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
			return "HIS";
		}
		if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
			return "HIS";
		}
		if (tradeName.equals("缴费") && oriCheckState.equals("长款")) {
			return channelName;
		}
		return null;
	}

	/**
	 * 获取备注
	 *
	 * @param repeatList
	 * @param tradeCheckFollow
	 * @return
	 */
	private String getRemark(TradeCheckFollow tradeCheckFollow, List<String> repeatList) {
		String hisCheck = CommonEnum.BillBalance.HISDC.getValue() + "," + CommonEnum.BillBalance.HEALTHCAREHIS.getValue();
		String tradeName = tradeCheckFollow.getTradeName();
		String oriCheckState = hisCheck.contains(tradeCheckFollow.getOriCheckState()) ? "短款" : "长款";
		String remark = null;
		if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
			remark = "商户已退，HIS未退";
		}
		if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
			remark = "商户未退，HIS已退";
		}
		if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
			remark = "商户未缴，HIS已缴";
		}
		if (tradeName.equals("缴费") && oriCheckState.equals("长款")) {
			remark = "商户已缴，HIS未缴";
		}
		if (repeatList.contains(tradeCheckFollow.getBusinessNo()) && StringUtils.isNotBlank(tradeCheckFollow.getBusinessNo())) {
			return remark + "，已对冲";
		}
		return remark;
	}

	public List<Map<String, Object>> getSumData(String startDate, String endDate, String orgNo) {
//		ResponseResult result  = electronicRecService.getFollowRecMap(orgNo, startDate, endDate);
//		Map<String,Object> dataMap = (Map<String, Object>) result.getData();
//		// 获取微信、支付宝、银行卡支付统计
//		// 支付渠道数量
//		Map<String,Object> payData = (Map<String, Object>) dataMap.get("payDetailMap");
//		for(Map.Entry<String, Object> entry : payData.entrySet()){
//			// 支付方式
//		}
//		List<Map<String,Object>> data = new LinkedList<>();

		// 根据配置信息查询结果
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		String payTypeSql = ElectronicRecServiceImpl.combinationPayTypeSql(hConfig.getRecType());
		List<Map<String, Object>> list = wuHuExcelDatSum.getFollowCount(startDate, endDate, payTypeSql, orgNo);
		return list;
	}

	public String concatOrgNoSql(String orgNo) {
		List<Organization> orgList = null;
		if (null != orgNo) {
			orgList = organizationService.findByParentCode(orgNo);
		}
		String strOrg = "\'" + orgNo + "\'";
		if (orgList != null) {
			for (Organization v : orgList) {
				strOrg = strOrg + ",\'" + v.getCode() + "\'";
			}

		}
		return strOrg;
	}

	@GetMapping("/payStep")
	@ResponseBody
	public ResponseResult payStep(String businessNo, String orgCode, String billSource, String recHisId, String recThirdId) {
		if (StringUtils.isBlank(businessNo)) {
			return ResponseResult.failure();
		}
		if (StringUtil.isEmpty(orgCode)) {
			orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		}
		//String orgCodes = concatOrgNoSql(orgCode);
		List<Map<String, Object>> data = electronicRecService.getPayStep(businessNo,orgCode,billSource,recHisId,recThirdId);

		return ResponseResult.success().data(data);
	}

}
