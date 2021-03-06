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
 * 病历本报表
 */
@Controller
@RequestMapping("")
public class BlbSummaryController {

	private final int height = 18;
	@Autowired
	private GatherService gatherService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private ReportSummaryService reportSummaryService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

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

	public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
			HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
		setResponseAdRequest(request, response, fileName);
		ExportExcel ee = new ExportExcel();
		// workbook对应一个Excel
		HSSFWorkbook wb = new HSSFWorkbook();

		// 定义一个统一字体样式:、居中,边框
		HSSFCellStyle fontStyle = wb.createCellStyle();
		fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		setBorderStyle(fontStyle);
//		HSSFFont font = wb.createFont();
//		font.setFontName("Arial");
//		fontStyle.setFont(font);

		HSSFCellStyle borderStyle = wb.createCellStyle();
		setBorderStyle(borderStyle);

		// 创建一个sheet
		Sheet sheet = ee.getSheet(wb, workSheetName);
		int col = 10;
		// 第一行标题, 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, col));
		Row titleRow = ee.getRow(sheet, 0, null, height);
		ee.getCell(titleRow, 0, fontStyle, fileName);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < col + 1; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}

		// 第二行标题、支付类型
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 2));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));
		Row secondTitleRow = ee.getRow(sheet, 1, null, height);
		ee.getCell(secondTitleRow, 0, fontStyle, "机器编码");
		ee.getCell(secondTitleRow, 1, fontStyle, "SDP-7M-001");
		ee.getCell(secondTitleRow, 3, fontStyle, "SDP-7M-002");
		ee.getCell(secondTitleRow, 5, fontStyle, "SDP-7M-003");
		ee.getCell(secondTitleRow, 7, fontStyle, "SDP-7M-004");
		ee.getCell(secondTitleRow, 9, fontStyle, "SDP-7M-005");
		ee.getCell(secondTitleRow, 10, fontStyle, "");
		// 第三行标题、业务类型
		Row thirdTitleRow = ee.getRow(sheet, 2, null, height);
		String[] titleArray = { "支付方式", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)" };
		for (int i = 0, len = titleArray.length; i < len; i++) {
			ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
		}

		if (dataList.size() > 0) {
			Map<String, String> dataMap = ValueTexts.asMap(metaDataService.NameAsList());
			Map<String, Object> colMap = null;
			CellStyle doubleStyle = wb.createCellStyle();
			DataFormat df = wb.createDataFormat();
			doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
			setBorderStyle(doubleStyle);

			for (int i = 0, len = dataList.size(); i < len; i++) {

				Row dataRow = ee.getRow(sheet, i + 3, null, height);
				colMap = dataList.get(i);

				ee.getCell(dataRow, 0, borderStyle,
						dataMap.containsKey(colMap.get("businessType")) ? dataMap.get(colMap.get("businessType"))
								: colMap.get("businessType").toString());

				Cell cell1 = dataRow.createCell(1);
				cell1.setCellStyle(borderStyle);
				cell1.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("sdp001Acount"))));
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("sdp001PayAmount"))));

				Cell cell3 = dataRow.createCell(3);
				cell3.setCellStyle(borderStyle);
				cell3.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("sdp002Acount"))));
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellStyle(doubleStyle);
				cell4.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("sdp002PayAmount"))));

				Cell cell5 = dataRow.createCell(5);
				cell5.setCellStyle(borderStyle);
				cell5.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("sdp003Acount"))));
				Cell cell6 = dataRow.createCell(6);
				cell6.setCellStyle(doubleStyle);
				cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("sdp003PayAmount"))));

				Cell cell7 = dataRow.createCell(7);
				cell7.setCellStyle(borderStyle);
				cell7.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("sdp004Acount"))));
				Cell cell8 = dataRow.createCell(8);
				cell8.setCellStyle(doubleStyle);
				cell8.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("sdp004PayAmount"))));

				Cell cell9 = dataRow.createCell(9);
				cell9.setCellStyle(borderStyle);
				cell9.setCellValue(Integer.parseInt(getNotNullStr(colMap.get("sdp005Acount"))));
				Cell cell10 = dataRow.createCell(10);
				cell10.setCellStyle(doubleStyle);
				cell10.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("sdp005PayAmount"))));
			}
		}
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}

	/**
	 * 病历本汇总报表
	 */
	@Controller
	@RequestMapping(value = "admin/blb/blbSummary")
	public class hospitalizationReportsController extends CurrentUserContoller {

		@RequestMapping(value = "index")
		public String index(ModelMap model) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("orgCode", orgCode);
			model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
			model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));

			return "reports/blb/blbMachineReports";
		}

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
			List<Map<String, Object>> res = reportSummaryService.findBlbSummary(query);
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
			List<Map<String, Object>> res = reportSummaryService.findBlbSummary(query);
			commonExportExcel(fileName, workSheetName, request, response, res);
		}

	}
}
