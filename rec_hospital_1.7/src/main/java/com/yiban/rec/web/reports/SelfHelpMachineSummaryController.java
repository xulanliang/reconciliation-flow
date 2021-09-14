package com.yiban.rec.web.reports;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.ibm.icu.math.BigDecimal;
import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.StringUtil;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;

/**
 * 自助机汇总
 */
@Controller
@RequestMapping("")
public class SelfHelpMachineSummaryController {
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	@Autowired
	private GatherService gatherService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private ReportSummaryService reportSummaryService;

	private final int height = 18;

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

	public String getNotNullStr(Object str) {
		return (str == null ? "0" : String.valueOf(str).trim());
	}
	
	public BigDecimal sumAmount(Map<String, Object> colMap) {
		BigDecimal wechatAmount = new BigDecimal(
				String.valueOf(colMap.get("wechatAmount") == null ? "0.00" : colMap.get("wechatAmount")));
		BigDecimal zfbAmount = new BigDecimal(
				String.valueOf(colMap.get("zfbAmount") == null ? "0.00" : colMap.get("zfbAmount")));
		BigDecimal bankAmount = new BigDecimal(
				String.valueOf(colMap.get("bankAmount") == null ? "0.00" : colMap.get("bankAmount")));
		BigDecimal cashAmount = new BigDecimal(
				String.valueOf(colMap.get("cashAmount") == null ? "0.00" : colMap.get("cashAmount")));
		BigDecimal unionAmount = new BigDecimal(
				String.valueOf(colMap.get("unionAmount") == null ? "0.00" : colMap.get("unionAmount")));
		BigDecimal yibaoAmount = new BigDecimal(
				String.valueOf(colMap.get("yibaoAmount") == null ? "0.00" : colMap.get("yibaoAmount")));

		BigDecimal amountSum = wechatAmount.add(zfbAmount).add(bankAmount).add(cashAmount).add(unionAmount)
				.add(yibaoAmount);

		return amountSum.setScale(2);
	}
	
	public int otherAcount(Map<String, Object> colMap) {
		int allAcount = Integer
				.parseInt(String.valueOf(colMap.get("allAcount") == null ? "0" : colMap.get("allAcount")));
		int registerAcount = Integer
				.parseInt(String.valueOf(colMap.get("registerAcount") == null ? "0" : colMap.get("registerAcount")));
		int makeAppointmentAcount = Integer.parseInt(String
				.valueOf(colMap.get("makeAppointmentAcount") == null ? "0" : colMap.get("makeAppointmentAcount")));
		int payAcount = Integer
				.parseInt(String.valueOf(colMap.get("payAcount") == null ? "0" : colMap.get("payAcount")));
		int clinicAcount = Integer
				.parseInt(String.valueOf(colMap.get("clinicAcount") == null ? "0" : colMap.get("clinicAcount")));
		int prepaymentForHospitalizationAcount = Integer
				.parseInt(String.valueOf(colMap.get("prepaymentForHospitalizationAcount") == null ? "0"
						: colMap.get("prepaymentForHospitalizationAcount")));

		int otherAcount = allAcount - registerAcount - makeAppointmentAcount - payAcount - clinicAcount
				- prepaymentForHospitalizationAcount;

		return otherAcount;
	}

	public BigDecimal otherAmount(Map<String, Object> colMap) {
		BigDecimal allAmount = new BigDecimal(
				String.valueOf(colMap.get("allAmount") == null ? "0.00" : colMap.get("allAmount")));
		BigDecimal registerAmount = new BigDecimal(
				String.valueOf(colMap.get("registerAmount") == null ? "0.00" : colMap.get("registerAmount")));
		BigDecimal makeAppointmentAmount = new BigDecimal(String
				.valueOf(colMap.get("makeAppointmentAmount") == null ? "0.00" : colMap.get("makeAppointmentAmount")));
		BigDecimal payAmount = new BigDecimal(
				String.valueOf(colMap.get("payAmount") == null ? "0.00" : colMap.get("payAmount")));
		BigDecimal clinicAmount = new BigDecimal(
				String.valueOf(colMap.get("clinicAmount") == null ? "0.00" : colMap.get("clinicAmount")));
		BigDecimal prepaymentForHospitalizationAmount = new BigDecimal(
				String.valueOf(colMap.get("prepaymentForHospitalizationAmount") == null ? "0.00"
						: colMap.get("prepaymentForHospitalizationAmount")));

		BigDecimal otherAmount = allAmount.subtract(registerAmount).subtract(makeAppointmentAmount).subtract(payAmount)
				.subtract(clinicAmount).subtract(prepaymentForHospitalizationAmount);

		return otherAmount.setScale(2);
	}

	public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
			HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
		setResponseAdRequest(request, response, fileName);
		ExportExcel ee = new ExportExcel();
		// workbook对应一个Excel
		HSSFWorkbook wb = new HSSFWorkbook();

		// 定义一个统一字体样式:、居中,边框
		HSSFCellStyle fontStyle = wb.createCellStyle();
		fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		fontStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
		setBorderStyle(fontStyle);

		HSSFCellStyle borderStyle = wb.createCellStyle();
		setBorderStyle(borderStyle);

		// 创建一个sheet
		Sheet sheet = ee.getSheet(wb, workSheetName);
		// 第一行标题, 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
		Row titleRow = ee.getRow(sheet, 0, null, height);
		ee.getCell(titleRow, 0, fontStyle, fileName);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < 13; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}

		// 第二行标题、支付类型
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));

		Row secondTitleRow = ee.getRow(sheet, 1, null, height);
		ee.getCell(secondTitleRow, 0, fontStyle, "");
		ee.getCell(secondTitleRow, 1, fontStyle, "挂号");
		ee.getCell(secondTitleRow, 3, fontStyle, "预约挂号");
		ee.getCell(secondTitleRow, 5, fontStyle, "缴费");
		ee.getCell(secondTitleRow, 7, fontStyle, "门诊充值");
		ee.getCell(secondTitleRow, 9, fontStyle, "住院预交金");
		ee.getCell(secondTitleRow, 11, fontStyle, "其他");
		ee.getCell(secondTitleRow, 12, fontStyle, "");

		// 第三行标题、业务类型
		Row thirdTitleRow = ee.getRow(sheet, 2, null, height);
		String[] titleArray = { "设备名称", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数",
				"金额(元)" };
		for (int i = 0, len = titleArray.length; i < len; i++) {
			ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
		}

		if (dataList.size() > 0) {
			Map<String, Object> colMap = null;
			CellStyle doubleStyle = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
			setBorderStyle(doubleStyle);

			for (int i = 0, len = dataList.size(); i < len; i++) {
				colMap = dataList.get(i);

				Row dataRow = ee.getRow(sheet, i + 3, null, height);

				ee.getCell(dataRow, 0, borderStyle, colMap.get("businessType") != null ? colMap.get("businessType").toString() : "");

				Cell cell1 = dataRow.createCell(1);
				cell1.setCellStyle(borderStyle);
				cell1.setCellValue(Integer.parseInt(
						colMap.get("registerAcount") == null ? "0" : colMap.get("registerAcount").toString()));
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double.parseDouble(
						colMap.get("registerAmount") == null ? "0.00" : colMap.get("registerAmount").toString()));

				Cell cell3 = dataRow.createCell(3);
				cell3.setCellStyle(borderStyle);
				cell3.setCellValue(Integer.parseInt(colMap.get("makeAppointmentAcount") == null ? "0"
						: colMap.get("makeAppointmentAcount").toString()));
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellStyle(doubleStyle);
				cell4.setCellValue(Double.parseDouble(String.valueOf(
						colMap.get("makeAppointmentAmount") == null ? "0.00" : colMap.get("makeAppointmentAmount"))));

				Cell cell5 = dataRow.createCell(5);
				cell5.setCellStyle(borderStyle);
				cell5.setCellValue(
						Integer.parseInt(colMap.get("payAcount") == null ? "0" : colMap.get("payAcount").toString()));
				Cell cell6 = dataRow.createCell(6);
				cell6.setCellStyle(doubleStyle);
				cell6.setCellValue(Double
						.parseDouble(colMap.get("payAmount") == null ? "0.00" : colMap.get("payAmount").toString()));

				Cell cell7 = dataRow.createCell(7);
				cell7.setCellStyle(borderStyle);
				cell7.setCellValue(Integer
						.parseInt(colMap.get("clinicAcount") == null ? "0" : colMap.get("clinicAcount").toString()));
				Cell cell8 = dataRow.createCell(8);
				cell8.setCellStyle(doubleStyle);
				cell8.setCellValue(Double.parseDouble(
						colMap.get("clinicAmount") == null ? "0.00" : colMap.get("clinicAmount").toString()));

				Cell cell9 = dataRow.createCell(9);
				cell9.setCellStyle(borderStyle);
				cell9.setCellValue(Integer.parseInt(colMap.get("prepaymentForHospitalizationAcount") == null ? "0"
						: colMap.get("prepaymentForHospitalizationAcount").toString()));
				Cell cell10 = dataRow.createCell(10);
				cell10.setCellStyle(doubleStyle);
				cell10.setCellValue(Double
						.parseDouble(String.valueOf(colMap.get("prepaymentForHospitalizationAmount") == null ? "0.00"
								: colMap.get("prepaymentForHospitalizationAmount"))));

				Cell cell11 = dataRow.createCell(11);
				cell11.setCellStyle(borderStyle);
				cell11.setCellValue(otherAcount(colMap));
				Cell cell12 = dataRow.createCell(12);
				cell12.setCellStyle(doubleStyle);
				cell12.setCellValue(otherAmount(colMap).doubleValue());
			}
		}
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}

	public void payTitleExportExcel(String fileName, String workSheetName, HttpServletRequest request,
			HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
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
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
		Row titleRow = ee.getRow(sheet, 0, null, height);
		ee.getCell(titleRow, 0, fontStyle, fileName);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < 14; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}

		// 第二行标题、支付类型
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 12));
		Row secondTitleRow = ee.getRow(sheet, 1, null, height);
		ee.getCell(secondTitleRow, 0, fontStyle, "");
		ee.getCell(secondTitleRow, 1, fontStyle, "微信");
		ee.getCell(secondTitleRow, 3, fontStyle, "支付宝");
		ee.getCell(secondTitleRow, 5, fontStyle, "银联");
		ee.getCell(secondTitleRow, 7, fontStyle, "现金");
		ee.getCell(secondTitleRow, 9, fontStyle, "聚合支付");
		ee.getCell(secondTitleRow, 11, fontStyle, "医保");
		ee.getCell(secondTitleRow, 12, fontStyle, "");
		ee.getCell(secondTitleRow, 13, fontStyle, "");

		// 第三行标题、业务类型
		Row thirdTitleRow = ee.getRow(sheet, 2, null, height);
		String[] titleArray = { "设备名称", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数",
				"金额(元)", "总金额(元)" };
		for (int i = 0, len = titleArray.length; i < len; i++) {
			ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
		}

		if (dataList.size() > 0) {
			Map<String, Object> colMap = null;
			CellStyle doubleStyle = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
			setBorderStyle(doubleStyle);

			for (int i = 0, len = dataList.size(); i < len; i++) {

				Row dataRow = ee.getRow(sheet, i + 3, null, height);
				colMap = dataList.get(i);

				ee.getCell(dataRow, 0, borderStyle, colMap.get("businessType") != null ? colMap.get("businessType").toString() : "");

				Cell cell1 = dataRow.createCell(1);
				cell1.setCellStyle(borderStyle);
				cell1.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("wechatAcount"))));
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("wechatAmount"))));

				Cell cell3 = dataRow.createCell(3);
				cell3.setCellStyle(borderStyle);
				cell3.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("zfbAcount"))));
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellStyle(doubleStyle);
				cell4.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zfbAmount"))));

				Cell cell5 = dataRow.createCell(5);
				cell5.setCellStyle(borderStyle);
				cell5.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("bankAcount"))));
				Cell cell6 = dataRow.createCell(6);
				cell6.setCellStyle(doubleStyle);
				cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("bankAmount"))));

				Cell cell7 = dataRow.createCell(7);
				cell7.setCellStyle(borderStyle);
				cell7.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("cashAcount"))));
				Cell cell8 = dataRow.createCell(8);
				cell8.setCellStyle(doubleStyle);
				cell8.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("cashAmount"))));

				Cell cell9 = dataRow.createCell(9);
				cell9.setCellStyle(borderStyle);
				cell9.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("unionAcount"))));
				Cell cell10 = dataRow.createCell(10);
				cell10.setCellStyle(doubleStyle);
				cell10.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("unionAmount"))));

				Cell cell11 = dataRow.createCell(11);
				cell11.setCellStyle(borderStyle);
				cell11.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("yibaoAcount"))));
				Cell cell12 = dataRow.createCell(12);
				cell12.setCellStyle(doubleStyle);
				cell12.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("yibaoAmount"))));
				// 总金额
				Cell cell13 = dataRow.createCell(13);
				cell13.setCellStyle(doubleStyle);
				cell13.setCellValue(sumAmount(colMap).doubleValue());
			}
		}
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}

	/**
	 * 业务类型汇总报表controller
	 */
	@Controller
	@RequestMapping(value = "admin/selfhelpmachine/businessType")
	public class BusinessTypeSumaryReportsController extends CurrentUserContoller {
		/**
		 * 进入业务类型汇总页面
		 */
		@RequestMapping(value = "index")
		public String index(ModelMap model) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("orgCode", orgCode);
			model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
			model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));

			return "reports/selfhelpmachine/businessTypeMachineReports";
		}

		/**
		 * 业务类型汇总数据
		 * 
		 * @param orgNo
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		@RequestMapping(value = "data")
		@ResponseBody
		public WebUiPage<Map<String, Object>> data(ReportSummaryService.SummaryQuery query) {
			if (StringUtil.isEmpty(query.getBeginTime())) {
				query.setBeginTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getEndTime())) {
				query.setEndTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getOrgCode())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
//			List<Map<String, Object>> res = reportSummaryService.findSelfHelpMachineSummaryByBusinessType(query);

			// 这里用Device_No表示，内部逻辑实际查询的是cashier字段
			query.setColumnSql(" Device_No ");
			List<Map<String, Object>> res = reportSummaryService.findAllOfSummaryByBusinessType(query);
			return new WebUiPage<>(res.size(), res);
		}
		
		@RequestMapping(value = "dcExcel")
		public void exportData(ReportSummaryService.SummaryQuery query, String fileName, String workSheetName,
				HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (StringUtil.isEmpty(query.getBeginTime())) {
				query.setBeginTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getEndTime())) {
				query.setEndTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getOrgCode())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
//			List<Map<String, Object>> res = reportSummaryService.findSelfHelpMachineSummaryByBusinessType(query);

			// 这里用Device_No表示，内部逻辑实际查询的是cashier字段
			query.setColumnSql(" Device_No ");
			List<Map<String, Object>> res = reportSummaryService.findAllOfSummaryByBusinessType(query);
			commonExportExcel(fileName, workSheetName, request, response, res);
		}
	}

	/**
	 * 支付方式汇总报表controller
	 */
	@Controller
	@RequestMapping(value = "admin/selfhelpmachine/payType")
	public class PayTypeSumaryReportsController extends CurrentUserContoller {

		/**
		 * 进入支付方式汇总页面
		 */
		@RequestMapping(value = "index")
		public String index(ModelMap model) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("orgCode", orgCode);
			model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
			model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));
			return "reports/selfhelpmachine/payTypeMachineReports";
		}

		/**
		 * 支付方式汇总数据
		 * 
		 * @param orgNo
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		@RequestMapping(value = "data")
		@ResponseBody
		public WebUiPage<Map<String, Object>> data(ReportSummaryService.SummaryQuery query) {
			if (StringUtil.isEmpty(query.getBeginTime())) {
				query.setBeginTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getEndTime())) {
				query.setEndTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getOrgCode())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
//			List<Map<String, Object>> res = reportSummaryService.findSelfHelpMachineSummaryByPayType(query);

			// 这里用Device_No表示，内部逻辑实际查询的是cashier字段
			query.setColumnSql(" Device_No ");
			List<Map<String, Object>> res = reportSummaryService.findAllOfSummaryByPayType(query);
			return new WebUiPage<>(res.size(), res);
		}
		
		@RequestMapping(value = "dcExcel")
		public void exportData(ReportSummaryService.SummaryQuery query, String fileName, String workSheetName,
				HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (StringUtil.isEmpty(query.getBeginTime())) {
				query.setBeginTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getEndTime())) {
				query.setEndTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 1));
			}
			if (StringUtil.isEmpty(query.getOrgCode())) {
				String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
				query.setOrgCode(orgCode);
			}
			// 这里用Device_No表示，内部逻辑实际查询的是cashier字段
			query.setColumnSql(" Device_No ");
			List<Map<String, Object>> res = reportSummaryService.findAllOfSummaryByPayType(query);
			payTitleExportExcel(fileName, workSheetName, request, response, res);
		}
	}
}
