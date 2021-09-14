package com.yiban.rec.web.admin;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springside.modules.mapper.JsonMapper;

import com.google.gson.Gson;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.controller.BaseController;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.HisTransactionFlow;
import com.yiban.rec.domain.settlement.RecHisSettlement;
import com.yiban.rec.domain.vo.HisSettlementDetailVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HisSettlementDetailService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * his结算明细controller
 */
@Controller
@RequestMapping(value = "admin/hissettlement")
public class HisSettlementDetailController extends CurrentUserContoller {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Gson gson = new Gson();

	@Autowired
	private HisSettlementDetailService hisSettlementDetailService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private GatherService gatherService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMapFromCode()));
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("settlement/hisSettlementDetail");
	}

	@GetMapping(value = "page")
	@ResponseBody
	public WebUiPage<RecHisSettlement> page(HisSettlementDetailVo vo) {
		logger.info(" vo = " + gson.toJson(vo));
		PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, "payTime"));
		List<Organization> orgList = null;
		if (StringUtils.isEmpty(vo.getOrgCode())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgCode(orgCode);
		}
		orgList = organizationService.findByParentCode(vo.getOrgCode());
		if (StringUtils.isEmpty(vo.getSettlementDate()) && StringUtils.isEmpty(vo.getSettlementStartDate())) {
			vo.setSettlementStartDate(DateUtil.getSpecifiedDayBefore(new Date()));
			vo.setSettlementEndDate(DateUtil.getSpecifiedDayBefore(new Date()));
		}
		Page<RecHisSettlement> page = hisSettlementDetailService.queryPage(vo, orgList, pageable);
		return super.toWebUIPage(page);
	}

	@PostMapping(value = "sum")
	@ResponseBody
	public ResponseResult querySum(HisSettlementDetailVo vo) {
		ResponseResult result = ResponseResult.success();
		List<Organization> orgList = null;
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		if (StringUtils.isEmpty(vo.getOrgCode())) {
			vo.setOrgCode(orgCode);
		}
		orgList = organizationService.findByParentCode(orgCode);
		Map<String, Object> map = this.hisSettlementDetailService.querySum(vo, orgList);
		return result.data(map);
	}

	@PostMapping("datelist")
	@ResponseBody
	public ResponseResult queryTradeDateList(HisSettlementDetailVo vo) {
		ResponseResult result = ResponseResult.success();
		Map<String, List<Map<String, Object>>> map = hisSettlementDetailService.queryTradeDateList(vo);
		return result.data(map);
	}

	@PostMapping(value = "select")
	@ResponseBody
	public List<Map<String, String>> querySelect(HisSettlementDetailVo vo) {
		List<Map<String, Object>> selectList = hisSettlementDetailService.querySelect(vo.getValue());
		if (selectList != null) {
			Map<String, Object> allMap = new HashMap<>();
			allMap.put("value", "全部");
			allMap.put("id", "");
			selectList.add(0, allMap);
		}
		// 转成String类型返回，后面优化
		List<Map<String, String>> res = new ArrayList<>();
		for (Map<String, Object> map : selectList) {
			HashMap<String, String> valueMap = new HashMap<>();
			valueMap.put("value", map.get("value").toString());
			valueMap.put("id", map.get("id").toString());
			res.add(valueMap);
		}
		return res;
	}

	@GetMapping(value = "dcExcel")
	public void exportExcel(HisSettlementDetailVo vo, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Organization> orgList = null;
		if (StringUtils.isEmpty(vo.getOrgCode())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgCode(orgCode);
		}
		orgList = organizationService.findByParentCode(vo.getOrgCode());
		List<RecHisSettlement> hisList = hisSettlementDetailService.queryNoPage(vo, orgList,
				new Sort(Direction.DESC, "payTime"));
		Map<String, Object> sumMap = this.hisSettlementDetailService.querySum(vo, orgList);
		BigDecimal amount = BigDecimal.ZERO;
		if (sumMap.containsKey("amountSum")) {
			amount = new BigDecimal(sumMap.get("amountSum").toString());
		}

		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map = null;
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		for (RecHisSettlement his : hisList) {
			map = new HashMap<>();
			map.put("hisFlowNo", his.getHisOrderNo());
			map.put("payTypeName", metaMap.get(his.getPayType()));

			if (EnumTypeOfInt.REFUND_CODE.getValue().equals(his.getOrderType())) {
				// 退费的设置为负数
				map.put("payAmount", his.getAmount().abs().negate());
			} else {
				map.put("payAmount", his.getAmount().abs());
			}
			map.put("orderStateName", metaMap.get(his.getOrderType()));
			map.put("settlementorNum", his.getSettlementorNum());
			map.put("tradeDatetime", his.getPayTime());
			map.put("settlementTime", DateUtil.transferDateToDateFormat(dateFormat, his.getSettlementTime()));
			mapList.add(map);
		}

		String[] titleArray = { "HIS流水号", "交易金额(元)", "交易类型", "结账人员", "交易时间", "结账时间", "支付类型" };
		String fileName = vo.getSettlementDate() + " HIS结算明细";
		this.excel(fileName, fileName, request, response, mapList, titleArray, amount);
	}

	@GetMapping(value = "dcHisSettlementDetailExcel")
	public void dcHisSettlementDetailExcel(HisSettlementDetailVo vo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Organization> orgList = null;
		if (StringUtils.isEmpty(vo.getOrgCode())) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			vo.setOrgCode(orgCode);
		}
		orgList = organizationService.findByParentCode(vo.getOrgCode());
		List<RecHisSettlement> hisList = hisSettlementDetailService.queryNoPage(vo, orgList,
				getSortFromDatagridOrElse(new Sort(Direction.DESC, "payTime")));
		Map<String, Object> sumMap = this.hisSettlementDetailService.querySum(vo, orgList);
		BigDecimal amount = BigDecimal.ZERO;
		int billsCount = 0;
		if (sumMap.containsKey("amountSum")) {
			amount = new BigDecimal(sumMap.get("amountSum").toString());
			billsCount = Integer.parseInt(sumMap.get("billsCount").toString());
		}

		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map = null;
		for (RecHisSettlement his : hisList) {
			map = new HashMap<>();
			map.put("settlementDate", DateUtil.getDateToString(his.getSettlementDate()));
			map.put("patientId", his.getPatientId());
			map.put("patientName", his.getPatientName());
			map.put("outTradeNo", his.getOutTradeNo());
			map.put("orderStateName", metaMap.get(his.getOrderType()));
			if (EnumTypeOfInt.REFUND_CODE.getValue().equals(his.getOrderType())) {
				// 退费的设置为负数
				map.put("payAmount", his.getAmount()==null?"0.00":his.getAmount().abs().negate());
			} else {
				map.put("payAmount", his.getAmount()==null?"0.00":his.getAmount().abs());
			}
			map.put("settlementSerialNo", his.getSettlementSerialNo());
			map.put("payTypeName", metaMap.get(his.getPayType()));
			map.put("billSourceName", metaMap.get(his.getBillSource()));
			map.put("tnsOrderNo", his.getTnsOrderNo());
			mapList.add(map);
		}
		String[] titleArray = { "结账日期", "患者ID", "姓名", "商户流水号", "交易类型", "金额(元)", "结账序号", "支付类型", "渠道名称", "支付方流水号" };
		String fileName = vo.getSettlementStartDate() + "至" + vo.getSettlementEndDate() + vo.getOrgName() + "结账单明细";
		this.excelHisSettlementDetail(fileName, fileName, request, response, mapList, titleArray, amount, billsCount);
	}

	public void excelHisSettlementDetail(String fileName, String workSheetName, HttpServletRequest request,
			HttpServletResponse response, List<Map<String, Object>> dataList, String[] titleArray, BigDecimal amount,
			int billsCount) throws Exception {
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
			if (i == len - 1) {
				sheet.setColumnWidth(i, titleArray[i].getBytes().length * 400);
			} else {
				sheet.setColumnWidth(i, titleArray[i].getBytes().length * 256);
			}
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
				ee.getCell(dataRow, 0, borderStyle,
						colMap.get("settlementDate") == null ? "" : colMap.get("settlementDate").toString());
				ee.getCell(dataRow, 1, borderStyle,
						colMap.get("patientId") == null ? "" : colMap.get("patientId").toString());
				ee.getCell(dataRow, 2, borderStyle,
						colMap.get("patientName") == null ? "" : colMap.get("patientName").toString());
				ee.getCell(dataRow, 3, borderStyle,
						colMap.get("outTradeNo") == null ? "" : colMap.get("outTradeNo").toString());
				ee.getCell(dataRow, 4, borderStyle,
						colMap.get("orderStateName") == null ? "" : colMap.get("orderStateName").toString());
				// double
				Cell cell2 = dataRow.createCell(5);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double
						.parseDouble(colMap.get("payAmount") == null ? "0.00" : colMap.get("payAmount").toString()));

				ee.getCell(dataRow, 6, borderStyle,
						colMap.get("settlementSerialNo") == null ? "" : colMap.get("settlementSerialNo").toString());
				ee.getCell(dataRow, 7, borderStyle,
						colMap.get("payTypeName") == null ? "" : colMap.get("payTypeName").toString());
				ee.getCell(dataRow, 8, borderStyle,
						colMap.get("billSourceName") == null ? "" : colMap.get("billSourceName").toString());
				ee.getCell(dataRow, 9, borderStyle,
						colMap.get("tnsOrderNo") == null ? "" : colMap.get("tnsOrderNo").toString());
			}
		}

		// 汇总金额, 合并单元格
		int countColumnIndex = dataList.size() + 3;
		sheet.addMergedRegion(new CellRangeAddress(countColumnIndex, countColumnIndex, 0, titleArray.length - 1));
		Row titleRow = ee.getRow(sheet, countColumnIndex, null, height);
		ee.getCell(titleRow, 0, fontStyle, "总金额(元)：" + amount + " 交易总笔数：" + billsCount);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < titleArray.length; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}

		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
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

			String dateFormat = "yyyy-MM-dd HH:mm:ss";
			for (int i = 0, len = dataList.size(); i < len; i++) {
				Row dataRow = ee.getRow(sheet, i + 2, null, height);
				colMap = dataList.get(i);

				// 字符串
				ee.getCell(dataRow, 0, borderStyle,
						colMap.get("hisFlowNo") == null ? "" : colMap.get("hisFlowNo").toString());
				// double
				Cell cell2 = dataRow.createCell(1);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double
						.parseDouble(colMap.get("payAmount") == null ? "0.00" : colMap.get("payAmount").toString()));

				ee.getCell(dataRow, 2, borderStyle,
						colMap.get("orderStateName") == null ? "" : colMap.get("orderStateName").toString());
				ee.getCell(dataRow, 3, borderStyle,
						colMap.get("settlementorNum") == null ? "" : colMap.get("settlementorNum").toString());

				ee.getCell(dataRow, 4, borderStyle,
						DateUtil.transferDateToDateFormat(dateFormat, (Date) colMap.get("tradeDatetime")));
				ee.getCell(dataRow, 5, borderStyle,
						DateUtil.transferStringToDateFormat(dateFormat, colMap.get("settlementTime").toString()));
				ee.getCell(dataRow, 6, borderStyle,
						colMap.get("payTypeName") == null ? "" : colMap.get("payTypeName").toString());
			}
		}

		// 汇总金额, 合并单元格
		int countColumnIndex = dataList.size() + 3;
		sheet.addMergedRegion(new CellRangeAddress(countColumnIndex, countColumnIndex, 0, titleArray.length - 1));
		Row titleRow = ee.getRow(sheet, countColumnIndex, null, height);
		ee.getCell(titleRow, 0, fontStyle, "总金额(元)：" + (amount==null?"0.00":amount));
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

	/**
	 * 漏结金额账单详情
	 * @author clearofchina
	 *
	 */
	@RestController
	@RequestMapping(value="/admin/omissionAmount")
	class OmissionAmountController extends BaseController {

		@Autowired
		private OrganizationService organizationService;
		@Autowired
		private MetaDataService metaDataService;
		
		/**
		 * 结账日页面-漏结金额交易明细
		 * 
		 * @param orgCode, billSource, tradeDate（交易日期）, outTradeNo,hisOrderNo, patientName
		 * @return
		 */
		@GetMapping("/page")
		public WebUiPage<HisTransactionFlow> recHisTradeQuery(HisSettlementDetailVo vo) {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getTradeDate())) {
				vo.setTradeDate(DateUtil.getCurrentDateString());
			}
			Page<HisTransactionFlow> platPage = hisSettlementDetailService.getOmissionAmountPage(vo, orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}

		// 统计笔数与金额
		@PostMapping("/sum")
		public ResponseResult getCollect(HisSettlementDetailVo vo) {
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getTradeDate())) {
				vo.setTradeDate(DateUtil.getCurrentDateString());
			}
			Map<String, Object> map = hisSettlementDetailService.getOmissionAmountCollect(vo, orgList);
			return ResponseResult.success().data(map);
		}

		@Logable(operation = "导出漏结金额明细账单")
		@RequestMapping(value = "/dcExcel", method = RequestMethod.GET)
		public void exportDataAccount(HisSettlementDetailVo vo, ModelMap model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Sort sort = new Sort(Direction.DESC, "tradeDatatime");
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getTradeDate())) {
				vo.setTradeDate(DateUtil.getCurrentDateString());
			}

			Page<HisTransactionFlow> list = hisSettlementDetailService.getOmissionAmountPage(vo, orgList,
					this.getRequestPageabledWithInitSort(sort));
			Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

			// 总金额
			BigDecimal orgAmount = new BigDecimal(0);
			List<Map<String, Object>> hisMapList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (HisTransactionFlow his : list) {
				Map<String, Object> hisMap = new HashMap<>();
				hisMap.put("hisFlowNo", StringUtil.isNullOrEmpty(his.getHisFlowNo())?"":his.getHisFlowNo());
				hisMap.put("payFlowNo", StringUtil.isNullOrEmpty(his.getPayFlowNo())?"":his.getPayFlowNo());
				hisMap.put("payTypeName", metaMap.get(his.getPayType()));
				hisMap.put("tradeDatatime", sdf.format(his.getTradeDatatime()));
				hisMap.put("billSource", metaMap.get(StringUtil.isNullOrEmpty(his.getBillSource())?"":his.getBillSource()));
				hisMap.put("custName", StringUtil.isNullOrEmpty(his.getCustName())?"":his.getCustName());
				
				
				if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(his.getOrderState())) {// 退费
					hisMap.put("payAmount", his.getPayAmount()==null?new BigDecimal(0):his.getPayAmount().abs().negate());
					orgAmount = orgAmount.add(his.getPayAmount()==null?new BigDecimal(0):his.getPayAmount().abs().negate());
				} else {
					hisMap.put("payAmount", his.getPayAmount()==null?"0.00":his.getPayAmount().abs());
					orgAmount = orgAmount.add(his.getPayAmount()==null?new BigDecimal(0):his.getPayAmount());
				}
				hisMapList.add(hisMap);
			}
			String[] titleArray = { "his流水号", "支付方流水号", "支付类型", "金额(元)", "交易时间", "渠道名称", "患者姓名" };
			String fileName = vo.getTradeDate() + "漏结金额交易明细";
			this.excel(fileName, "漏结金额交易明细", request, response, hisMapList, titleArray, orgAmount);
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
	
	
	/**
	 * 结算前账单详情
	 * @author clearofchina
	 *
	 */
	@RestController
	@RequestMapping(value="/admin/beforeSettlement")
	class BeforeSettlementController extends BaseController {

		@Autowired
		private OrganizationService organizationService;
		@Autowired
		private MetaDataService metaDataService;
		
		/**
		 * 结账日页面-结算前金额交易明细
		 * 
		 * @param orgCode, billSource, tradeDate（交易日期）, outTradeNo,hisOrderNo, patientName
		 * @return
		 */
		@GetMapping("/page")
		public WebUiPage<RecHisSettlement> recHisTradeQuery(HisSettlementDetailVo vo) {
			Sort sort = new Sort(Direction.DESC, "payTime");
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getSettlementDate())) {
				vo.setSettlementDate(DateUtil.getCurrentDateString());
			}

			Page<RecHisSettlement> platPage = hisSettlementDetailService.getBeforeSettlementPage(vo, orgList,
					this.getRequestPageabledWithInitSort(sort));
			return toWebUIPage(platPage);
		}

		// 统计笔数与金额
		@PostMapping("/sum")
		public ResponseResult getCollect(HisSettlementDetailVo vo) {
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getSettlementDate())) {
				vo.setSettlementDate(DateUtil.getCurrentDateString());
			}
			Map<String, Object> map = hisSettlementDetailService.getBeforeSettlementCollect(vo, orgList);
			return ResponseResult.success().data(map);
		}
		@PostMapping("datelist")
		@ResponseBody
		public ResponseResult queryTradeDateList(HisSettlementDetailVo vo) {
			ResponseResult result = ResponseResult.success();
			Map<String, List<Map<String, Object>>> map = hisSettlementDetailService.queryBeforeTradeDateList(vo);
			return result.data(map);
		}
		@Logable(operation = "导出结算前金额明细账单")
		@RequestMapping(value = "/dcExcel", method = RequestMethod.GET)
		public void exportDataAccount(HisSettlementDetailVo vo, ModelMap model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			Sort sort = new Sort(Direction.DESC, "payTime");
			List<Organization> orgList = null;
			if (null != vo.getOrgCode()) {
				orgList = organizationService.findByParentCode(vo.getOrgCode());
			}
			if (StringUtils.isBlank(vo.getSettlementDate())) {
				vo.setSettlementDate(DateUtil.getCurrentDateString());
			}

			Page<RecHisSettlement> list = hisSettlementDetailService.getBeforeSettlementPage(vo, orgList,
					this.getRequestPageabledWithInitSort(sort));
			Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

			// 总金额
			BigDecimal orgAmount = new BigDecimal(0);
			List<Map<String, Object>> hisMapList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (RecHisSettlement his : list) {
				Map<String, Object> hisMap = new HashMap<>();
				hisMap.put("hisFlowNo", StringUtil.isNullOrEmpty(his.getHisOrderNo())?"":his.getHisOrderNo());
//				hisMap.put("payFlowNo", StringUtil.isNullOrEmpty(his.getOutTradeNo())?"":his.getOutTradeNo());
				hisMap.put("payTypeName", metaMap.get(StringUtil.isNullOrEmpty(his.getPayType())?"":his.getPayType()));
				hisMap.put("orderTypeName", metaMap.get(StringUtil.isNullOrEmpty(his.getOrderType())?"":his.getOrderType()));
				hisMap.put("tradeDatatime", sdf.format(his.getPayTime()));
				hisMap.put("billSource", metaMap.get(StringUtil.isNullOrEmpty(his.getBillSource())?"":his.getBillSource()));
				hisMap.put("settlementorNum", StringUtil.isNullOrEmpty(his.getSettlementorNum())?"":his.getSettlementorNum());
				hisMap.put("settlementTime", sdf.format(his.getSettlementTime()));
				
				if (!EnumTypeOfInt.TRADE_TYPE_PAY.getValue().equals(his.getOrderType())) {// 退费
					hisMap.put("payAmount", his.getAmount()==null?"0.00":his.getAmount().abs().negate());
					orgAmount = orgAmount.add(his.getAmount()==null?new BigDecimal(0):his.getAmount().abs().negate());
				} else {
					hisMap.put("payAmount", his.getAmount()==null?"0.00":his.getAmount().abs());
					orgAmount = orgAmount.add(his.getAmount()==null?new BigDecimal(0):his.getAmount());
				}
				hisMapList.add(hisMap);
			}
			String[] titleArray = { "his流水号","交易金额(元)","交易类型", "结账人员", "交易时间", "结账时间",  "支付类型" };
			String fileName = vo.getSettlementDate() + "结算前金额交易明细";
			this.excel(fileName, "结算前金额交易明细", request, response, hisMapList, titleArray, orgAmount);
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
					// double
					Cell cell1 = dataRow.createCell(1);
					cell1.setCellStyle(doubleStyle);
					cell1.setCellValue(Double.parseDouble(colMap.get("payAmount")==null?"0.00":colMap.get("payAmount").toString()));
					ee.getCell(dataRow, 2, doubleStyle, colMap.get("orderTypeName")==null?"":colMap.get("orderTypeName").toString());
					ee.getCell(dataRow, 3, borderStyle, colMap.get("settlementorNum") == null ? "" : colMap.get("settlementorNum").toString());
					ee.getCell(dataRow, 4, borderStyle, colMap.get("tradeDatatime")==null?"":colMap.get("tradeDatatime").toString());
					ee.getCell(dataRow, 5, borderStyle, colMap.get("settlementTime")==null?"":colMap.get("settlementTime").toString());
					ee.getCell(dataRow, 6, borderStyle, colMap.get("payTypeName")==null?"":colMap.get("payTypeName").toString());
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
	
}
