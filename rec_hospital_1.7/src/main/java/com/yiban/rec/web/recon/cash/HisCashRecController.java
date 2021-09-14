package com.yiban.rec.web.recon.cash;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.HisPayQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HisTransactionFlowService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportDataUtil;

/**
 * his现金对账
 * @author xiaoweijie
 */
@Controller
@RequestMapping("/admin/hisCashRec")
public class HisCashRecController extends CurrentUserContoller {
	@Autowired
	private HisTransactionFlowService hisTransactionFlowService;

	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;
	
	
	@RequestMapping("")
	public String index(ModelMap model, 
	        @RequestParam(value="payDate", required=false) String payDate) {
		if(payDate != null) {
			model.put("accountDate", payDate);
		}else {
			model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		AppRuntimeConfig runtimeConfig = hospitalConfigService.loadConfig();
		model.put("isDisplay", StringUtils.isNotBlank(runtimeConfig.getIsDisplay())?runtimeConfig.getIsDisplay():0);
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		return autoView("reconciliation/hisCashRec");
	}
	@RestController
	@RequestMapping({"/admin/hisCashRec/data"})
	class HisPayDedailedDataController extends BaseController {
		@Autowired
		private HisTransactionFlowService hisTransactionFlowService;
		
		@Autowired
		private OrganizationService organizationService;
		
		@GetMapping
		public WebUiPage<HisTransactionFlow> recThridtradeQuery(HisPayQueryVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if(null != vo.getOrgNo()){
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			try {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(StringUtils.isNotBlank(vo.getStartTime())) {
					vo.setStartDate(sdf.parse(vo.getStartTime().trim()));
				} 
				if(StringUtils.isNotBlank(vo.getEndTime())) {
					vo.setEndDate(sdf.parse(vo.getEndTime().trim()));
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isEmpty(vo.getCashBillSource())) {
				vo.setCashBillSource("0000");
			}
			Page<HisTransactionFlow> platPage =hisTransactionFlowService.getHisPayPage(vo,orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}
		@GetMapping("/searchSumary")
		public ResponseResult searchSumary(HisPayQueryVo vo) {
			List<Organization> orgList = null;
			if (StringUtils.isNotBlank(vo.getOrgNo())) {
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			Map<String, Object> map = hisTransactionFlowService.searchSumary(vo, orgList);
			return ResponseResult.success().data(map);
		}
		
		//统计笔数与金额
		@GetMapping("/countSum")
		public ResponseResult getCollect(HisPayQueryVo vo) {
			List<Organization> orgList = null;
			if (StringUtils.isNotBlank(vo.getOrgNo())) {
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			Map<String, Object> map = hisTransactionFlowService.getTradeCollect(vo, orgList);
			return ResponseResult.success().data(map);
		}
	}
	
	@GetMapping(value="/export/count")
	public ResponseResult getHisBillCount(HisPayQueryVo vo) {
		ResponseResult result = ResponseResult.success();
		
		List<Organization> orgList = null;
		if(null != vo.getOrgNo()){
			orgList = organizationService.findByParentCode(vo.getOrgNo());
		}
		try {
			Date date = DateUtil.getInputDateOnlyDay(new Date());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isNotBlank(vo.getStartTime())) {
				vo.setStartDate(sdf.parse(vo.getStartTime().trim()));
			} else if(null == vo.getStartTime()) {
				vo.setStartDate(date);
			}
			if(StringUtils.isNotBlank(vo.getEndTime())) {
				vo.setEndDate(sdf.parse(vo.getEndTime().trim()));
			} else if(null == vo.getEndTime()) {
				vo.setEndDate(date);
			}
			result.data(hisTransactionFlowService.getHisBillCount(vo, orgList));
		} catch (Exception e) {
			logger.error("查询his账单总数异常：" + e);
			result = ResponseResult.failure("查询异常");
		}
		return result;
	}
	
	
	
	@Logable(operation = "导出对账明细")
	@RequestMapping(value = "/exportData", method = RequestMethod.GET)
	public void exportData(HisPayQueryVo vo, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Sort sort = new Sort(Direction.DESC, "tradeDatatime");
		List<Organization> orgList = null;
		if(null != vo.getOrgNo()){
			orgList = organizationService.findByParentCode(vo.getOrgNo());
		}
		try {
			Date date = DateUtil.getInputDateOnlyDay(new Date());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isNotBlank(vo.getStartTime())) {
				vo.setStartDate(sdf.parse(vo.getStartTime().trim()));
			} else if(null == vo.getStartTime()) {
				vo.setStartDate(date);
			}
			if(StringUtils.isNotBlank(vo.getEndTime())) {
				vo.setEndDate(sdf.parse(vo.getEndTime().trim()));
			} else if(null == vo.getEndTime()) {
				vo.setEndDate(date);
			}
			if (StringUtils.isEmpty(vo.getCashBillSource())) {
				vo.setCashBillSource("0000");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<HisTransactionFlow> list = hisTransactionFlowService.getHisPayNoPage(vo, orgList, sort);
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		Map<String,Object> orgMap = gatherService.getOrgMap();
		
		BigDecimal orgAmount = new BigDecimal(0);
		List<HisTransactionFlow> listVo = new ArrayList<HisTransactionFlow>();
		for(HisTransactionFlow flow : list) {
			flow.setOrgName(String.valueOf(orgMap.get(String.valueOf(flow.getOrgNo()))));
			flow.setPayBusinessTypeName(metaMap.get(String.valueOf(flow.getPayBusinessType())));
			flow.setPayTypeName(metaMap.get(String.valueOf(flow.getPayType())));
			flow.setCustIdentifyTypeName(metaMap.get(String.valueOf(flow.getCustIdentifyType())));
			flow.setOrderStateName(metaMap.get(String.valueOf(flow.getOrderState())));
			flow.setBillSource(metaMap.get(String.valueOf(flow.getBillSource())));
			flow.setCashBillSource(metaMap.get(String.valueOf(flow.getCashBillSource())));
			if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(flow.getOrderState())) {// 缴费
				orgAmount = orgAmount
						.add(flow.getPayAmount() == null ? new BigDecimal(0) : flow.getPayAmount().abs().negate());
			} else {
				orgAmount = orgAmount.add(flow.getPayAmount() == null ? BigDecimal.ZERO : flow.getPayAmount());
			}
			listVo.add(flow);
		}
		List<Map<String, Object>> hisMapList = new ArrayList<>();
		for (HisTransactionFlow hisTransactionFlow : listVo) {
			Map<String, Object> hisMap = new HashMap<>();
			hisMap.put("tradeDatatime", DateUtil.transferDateToString("yyyy-MM-dd HH:mm:ss", hisTransactionFlow.getTradeDatatime()));
			hisMap.put("billSource", hisTransactionFlow.getBillSource());
			hisMap.put("patTypeName", hisTransactionFlow.getPatTypeName());
			hisMap.put("patId", org.apache.commons.lang3.StringUtils.equals(hisTransactionFlow.getPatType(), EnumTypeOfInt.PAT_TYPE_MZ.getValue())
					? hisTransactionFlow.getMzCode():hisTransactionFlow.getPatCode());
			hisMap.put("custName", hisTransactionFlow.getCustName());
			hisMap.put("hisFlowNo", hisTransactionFlow.getHisFlowNo());
			hisMap.put("businessFlowNo", hisTransactionFlow.getBusinessFlowNo());
			hisMap.put("payFlowNo", hisTransactionFlow.getPayFlowNo());
			hisMap.put("payTypeName", hisTransactionFlow.getPayTypeName());
			hisMap.put("cashier", hisTransactionFlow.getCashier());
			hisMap.put("orderStateName", hisTransactionFlow.getOrderStateName());
			hisMap.put("payAmount", hisTransactionFlow.getPayAmount()==null?"0":hisTransactionFlow.getPayAmount().toString());
			hisMap.put("invoiceNo", hisTransactionFlow.getInvoiceNo());
			hisMap.put("cashBillSource", hisTransactionFlow.getCashBillSource());
			hisMapList.add(hisMap);
		}
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		String[] thirdTitleArray = { "交易时间", "渠道名称", "患者类型", "患者ID", "患者姓名", "收费员", "his流水号","商户流水号", "支付方流水号", "发票号","支付类型", "订单状态", "金额(元)"};
		String[] cellValue = { "tradeDatatime","cashBillSource","patTypeName", "patId", "custName","cashier","hisFlowNo","businessFlowNo","payFlowNo","invoiceNo","payTypeName","orderStateName","payAmount"};
		ExportDataUtil exportDataUtil = new ExportDataUtil(18,12,thirdTitleArray,cellValue);
		String fileName = sdf2.format(sdf2.parse(vo.getStartTime()))+"至"+sdf2.format(sdf2.parse(vo.getEndTime()))+vo.getOrgName()+"现金对账";
		exportDataUtil.commonExportExcel(fileName, "现金对账", request, response, hisMapList);
	}
}
