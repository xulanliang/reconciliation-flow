package com.yiban.rec.web.recon.noncash;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportDataUtil;
import com.yiban.rec.util.ExportExcel;

/**
 * his交易明细查询
 * @author xiaoweijie
 */
@Controller
@RequestMapping("/admin/hisPayDedailed")
public class HisPayDedailedController extends CurrentUserContoller {
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
		return autoView("reconciliation/hisPayDetailed");
	}
	@RequestMapping("/weinan")
	public String weinanIndex(ModelMap model, 
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
		return autoView("reconciliation/weinanhisPayDetailed");
	}
	@RestController
	@RequestMapping({"/admin/hisPayDedailed/data"})
	class HisPayDedailedDataController extends BaseController {
		@Autowired
		private HisTransactionFlowService hisTransactionFlowService;
		
		@Autowired
		private OrganizationService organizationService;
		
		@Autowired
		private ReportSummaryService reportSummaryService;
		
		@GetMapping
		public WebUiPage<HisTransactionFlow> recThridtradeQuery(HisPayQueryVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if(null != vo.getOrgNo()){
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			try {
				//Date date = DateUtil.getInputDateOnlyDay(new Date());
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(StringUtils.isNotBlank(vo.getStartTime())) {
					vo.setStartDate(sdf.parse(vo.getStartTime().trim()));
				} /*else if(null == vo.getStartTime()) {
					vo.setStartDate(date);
				}*/
				if(StringUtils.isNotBlank(vo.getEndTime())) {
					vo.setEndDate(sdf.parse(vo.getEndTime().trim()));
				} /*else if(null == vo.getEndTime()) {
					vo.setEndDate(date);
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			Page<HisTransactionFlow> platPage =hisTransactionFlowService.getHisPayPage(vo,orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
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
		
		@GetMapping("/weinan/countSum")
		public ResponseResult getWeiNanCollect(HisPayQueryVo vo) {
			String orgNo = vo.getOrgNo();
			String startDate = vo.getStartTime().trim();
			String endDate = vo.getEndTime().trim();
			ReportSummaryService.SummaryQuery query = new ReportSummaryService.SummaryQuery();
			query.setBeginTime(startDate.substring(0,startDate.indexOf(" ")));
			query.setEndTime(endDate.substring(0,endDate.indexOf(" ")));
			query.setOrgCode(orgNo);
			List<Map<String, Object>> res = reportSummaryService.findAllOfSummaryByBusinessType(query);
			Map<String, Object> map = res.get(0);
			return ResponseResult.success().data(map);
		}
	}
	
	@GetMapping(value="export/count")
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
	
	/**
	 * his结账日当日账单详情
	 * @author clearofchina
	 *
	 */
	@RestController
	@RequestMapping(value="/admin/accountday")
	class HisAcountDayController extends BaseController {
		@Autowired
		private HisTransactionFlowService hisTransactionFlowService;
		@Autowired
		private OrganizationService organizationService;
		@Autowired
		private MetaDataService metaDataService;
		
		/**
		 * 结账日页面-his当日交易明细
		 * 
		 * @param orgNo, billSource, date（交易日期）, payFlowNo,hisFlowNo, custName
		 * @return
		 */
		@GetMapping("/page")
		public WebUiPage<HisTransactionFlow> recHisTradeQuery(HisPayQueryVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if (null != vo.getOrgNo()) {
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			if (StringUtils.isBlank(vo.getDate())) {
				vo.setDate(DateUtil.getCurrentDateString());
			}
			String sDate = vo.getDate() + " 00:00:00";
			String eDate = vo.getDate() + " 23:59:59";
			vo.setStartDate(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", sDate));
			vo.setEndDate(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", eDate));

			Page<HisTransactionFlow> platPage = hisTransactionFlowService.getHisPayPage(vo, orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}

		// 统计笔数与金额
		@PostMapping("/sum")
		public ResponseResult getCollect(HisPayQueryVo vo) {
			List<Organization> orgList = null;
			if (StringUtils.isNotBlank(vo.getOrgNo())) {
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}

			if (StringUtils.isNotEmpty(vo.getDate())) {
				vo.setStartTime(vo.getDate());
				vo.setEndTime(vo.getDate());
			}
			Map<String, Object> map = hisTransactionFlowService.getTradeCollect(vo, orgList);
			return ResponseResult.success().data(map);
		}

		@Logable(operation = "导出HIS当日交易明细账单")
		@RequestMapping(value = "/dcExcel", method = RequestMethod.GET)
		public void exportDataAccount(HisPayQueryVo vo, ModelMap model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if (null != vo.getOrgNo()) {
				orgList = organizationService.findByParentCode(vo.getOrgNo());
			}
			if (StringUtils.isBlank(vo.getDate())) {
				vo.setDate(DateUtil.getCurrentDateString());
			}
			String sDate = vo.getDate() + " 00:00:00";
			String eDate = vo.getDate() + " 23:59:59";
			vo.setStartDate(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", sDate));
			vo.setEndDate(DateUtil.transferStringToDate("yyyy-MM-dd HH:mm:ss", eDate));

			List<HisTransactionFlow> list = hisTransactionFlowService.getHisPayNoPage(vo, orgList, sort);
			Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

			// 总金额
			BigDecimal orgAmount = new BigDecimal(0);
			List<Map<String, Object>> hisMapList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (HisTransactionFlow hisTransactionFlow : list) {
				Map<String, Object> hisMap = new HashMap<>();
				hisMap.put("hisFlowNo", hisTransactionFlow.getHisFlowNo());
				hisMap.put("payFlowNo", hisTransactionFlow.getPayFlowNo());
				hisMap.put("payTypeName", metaMap.get(hisTransactionFlow.getPayType()));
				hisMap.put("tradeDatatime", sdf.format(hisTransactionFlow.getTradeDatatime()));
				hisMap.put("billSource", metaMap.get(hisTransactionFlow.getBillSource()));
				hisMap.put("custName", hisTransactionFlow.getCustName());
				
				
				if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(hisTransactionFlow.getOrderState())) {// 退费
					hisMap.put("payAmount", hisTransactionFlow.getPayAmount().abs().negate());
					orgAmount = orgAmount.add(hisTransactionFlow.getPayAmount().abs().negate());
				} else {
					hisMap.put("payAmount", hisTransactionFlow.getPayAmount().abs());
					orgAmount = orgAmount.add(hisTransactionFlow.getPayAmount());
				}
				hisMapList.add(hisMap);
			}
			String[] titleArray = { "his流水号", "支付方流水号", "支付类型", "金额(元)", "交易时间", "渠道名称", "患者姓名" };
			String fileName = vo.getDate() + " HIS当日交易明细";
			this.excel(fileName, "HIS当日交易明细", request, response, hisMapList, titleArray, orgAmount);
		}

		public void excel(String fileName, String workSheetName, HttpServletRequest request, HttpServletResponse response,
				List<Map<String, Object>> dataList, String[] titleArray, BigDecimal amount) throws Exception {
			final int height = 18;
			setResponseAdRequest(request, response, fileName);
			ExportExcel ee = new ExportExcel();
			// workbook对应一个Excel
			HSSFWorkbook wb = new HSSFWorkbook();

			// 定义一个统一字体样式:、居中,边框
			HSSFCellStyle fontStyle = wb.createCellStyle();
			fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			setBorderStyle(fontStyle);

			HSSFCellStyle borderStyle = wb.createCellStyle();
			setBorderStyle(borderStyle);

			// 创建一个sheet
			Sheet sheet = ee.getSheet(wb, workSheetName);
			// 第一行标题, 合并单元格
			// 标题样式
			HSSFCellStyle titleStyle = wb.createCellStyle();
			titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			Font font = wb.createFont();
			font.setFontHeightInPoints((short) 20);
			titleStyle.setFont(font);
			setBorderStyle(titleStyle);
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length - 1));
			Row titleRow0 = ee.getRow(sheet, 0, null, height);
			ee.getCell(titleRow0, 0, titleStyle, fileName);
			titleRow0.setHeightInPoints(25);
			// 创建等多的单元格，解决合并单元格的问题
			for (int i = 1; i < titleArray.length; i++) {
				ee.getCell(titleRow0, i, fontStyle, "");
			}
			
			// 第二行标题
			Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
			for (int i = 0, len = titleArray.length; i < len; i++) {
				ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
				sheet.setColumnWidth(i, titleArray[i].getBytes().length * 256);
			}

			if (dataList.size() > 0) {
				Map<String, Object> colMap = null;
				CellStyle doubleStyle = wb.createCellStyle();
				DataFormat df = wb.createDataFormat();
				doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
				setBorderStyle(doubleStyle);

				for (int i = 0, len = dataList.size(); i < len; i++) {
					Row dataRow = ee.getRow(sheet, i + 2, null, height);
					colMap = dataList.get(i);

					// 字符串
					ee.getCell(dataRow, 0, borderStyle, colMap.get("hisFlowNo") == null ? "" : colMap.get("hisFlowNo").toString());
					ee.getCell(dataRow, 1, borderStyle, colMap.get("payFlowNo")==null?"":colMap.get("payFlowNo").toString());
					ee.getCell(dataRow, 2, borderStyle, colMap.get("payTypeName")==null?"":colMap.get("payTypeName").toString());
					// double
					Cell cell2 = dataRow.createCell(3);
					cell2.setCellStyle(doubleStyle);
					cell2.setCellValue(Double.parseDouble(colMap.get("payAmount")==null?"0.00":colMap.get("payAmount").toString()));

					ee.getCell(dataRow, 4, borderStyle, colMap.get("tradeDatatime")==null?"":colMap.get("tradeDatatime").toString());
					ee.getCell(dataRow, 5, borderStyle, colMap.get("billSource")==null?"":colMap.get("billSource").toString());
					ee.getCell(dataRow, 6, borderStyle, colMap.get("custName") == null ? "" : colMap.get("custName").toString());
				}
			}

			// 汇总金额, 合并单元格
			int countColumnIndex = dataList.size() + 3;
			sheet.addMergedRegion(new CellRangeAddress(countColumnIndex, countColumnIndex, 0, titleArray.length - 1));
			Row titleRow = ee.getRow(sheet, countColumnIndex, null, height);
//			HSSFCellStyle amountStyle = wb.createCellStyle();
//			amountStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//			Font font = wb.createFont();
//			font.setFontHeightInPoints((short) 20);
//			amountStyle.setFont(font);
//			setBorderStyle(amountStyle);
			ee.getCell(titleRow, 0, fontStyle, "总金额(元)：" + amount);
			// 创建等多的单元格，解决合并单元格的问题
			for (int i = 1; i < titleArray.length; i++) {
				ee.getCell(titleRow, i, fontStyle, "");
			}

			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		}

		public void setResponseAdRequest(HttpServletRequest request, HttpServletResponse response, String fileName)
				throws UnsupportedEncodingException {
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			request.setCharacterEncoding("utf-8");
			response.addHeader("Content-disposition",
					"attachment; filename=" + new String(fileName.getBytes(), "iso-8859-1") + ".xls");
		}

		public void setBorderStyle(CellStyle cellStyle) {
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		}
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
			hisMapList.add(hisMap);
		}
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		String[] thirdTitleArray = { "交易时间", "渠道名称", "患者类型", "患者ID", "患者姓名", "收费员", "his流水号","商户流水号", "支付方流水号", "发票号","支付类型", "订单状态", "金额(元)"};
		String[] cellValue = { "tradeDatatime","billSource","patTypeName", "patId", "custName","cashier","hisFlowNo","businessFlowNo","payFlowNo","invoiceNo","payTypeName","orderStateName","payAmount"};
		ExportDataUtil exportDataUtil = new ExportDataUtil(18,12,thirdTitleArray,cellValue);
		String fileName = sdf2.format(sdf2.parse(vo.getStartTime()))+"至"+sdf2.format(sdf2.parse(vo.getEndTime()))+vo.getOrgName()+"his交易明细";
		exportDataUtil.commonExportExcel(fileName, "his交易明细", request, response, hisMapList);
	}
}
