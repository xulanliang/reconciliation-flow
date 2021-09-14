 package com.yiban.rec.web.reports;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReportSummaryServiceForXiNingSanYuan;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;

/**
 * 收费员汇总
 * 
 * @author clearofchina
 *
 */
@Controller
@RequestMapping("")
public class CashierSummaryControllerForXiNingSanYuan {
	@Autowired
	private GatherService gatherService;
	@Autowired
	private MetaDataService metaDataService;
	@Autowired
	private ReportSummaryServiceForXiNingSanYuan reportSummaryServiceForXiNingSanYuan;
	@Autowired
	private PropertiesConfigService propertiesConfigService;

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

	/**
	 * 支付方式、业务类型二合一汇总报表controller
	 */
	@Controller
	@RequestMapping(value = "admin/cashier/payTypeAndBussinessType")
	public class PayTypeAndBussinessTypeSumaryReportsController extends CurrentUserContoller {

		/**
		 * 进入支付方式、业务类型二合一汇总页面
		 */
		@RequestMapping(value = "index")
		public String index(ModelMap model) {
			String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("orgCode", orgCode);
			model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
			model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));
			return "reports/cashier/payTypeAndBussinessTypeCashierReports";
		}

		/**
		 * 支付方式、业务类型二合一汇总数据
		 * 
		 * @param orgNo
		 * @param startDate
		 * @param endDate
		 * @return
		 */
		@RequestMapping(value = "data")
		@ResponseBody
		public WebUiPage<Map<String, Object>> data(ReportSummaryServiceForXiNingSanYuan.SummaryQuery query) {
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

			query.setColumnSql(" Cashier ");

			List<Map<String, Object>> res = reportSummaryServiceForXiNingSanYuan
					.findPayTypeAndBussinessTypeSummary(query);

			return new WebUiPage<>(res.size(), res);
		}

		@RequestMapping(value = "dcExcel")
		public void exportData(ReportSummaryServiceForXiNingSanYuan.SummaryQuery query, String fileName,
				String workSheetName, HttpServletRequest request, HttpServletResponse response) throws Exception {
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

			query.setColumnSql(" Cashier ");

			List<Map<String, Object>> res = reportSummaryServiceForXiNingSanYuan
					.findPayTypeAndBussinessTypeSummary(query);

			commonExportExcel(fileName, workSheetName, request, response, res);
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

			HSSFCellStyle borderStyle = wb.createCellStyle();
			setBorderStyle(borderStyle);

			// 创建一个sheet
			Sheet sheet = ee.getSheet(wb, workSheetName);
			// 第一行标题, 合并单元格
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 43));
			Row titleRow = ee.getRow(sheet, 0, null, height);
			ee.getCell(titleRow, 0, fontStyle, fileName);
			// 创建等多的单元格，解决合并单元格的问题
			for (int i = 1; i < 43; i++) {
				ee.getCell(titleRow, i, fontStyle, "");
			}

			// 第二行标题、支付类型
			for (int i = 1; i < 43; i = i + 2) {
				sheet.addMergedRegion(new CellRangeAddress(1, 1, i, i + 1));
			}

			Row secondTitleRow = ee.getRow(sheet, 1, null, height);
			ee.getCell(secondTitleRow, 0, fontStyle, "");
			ee.getCell(secondTitleRow, 1, fontStyle, "挂号 （微信）");
			ee.getCell(secondTitleRow, 3, fontStyle, "挂号 （支付宝）");
			ee.getCell(secondTitleRow, 5, fontStyle, "挂号 （银联）");
			ee.getCell(secondTitleRow, 7, fontStyle, "挂号 （现金）");
			ee.getCell(secondTitleRow, 9, fontStyle, "预约挂号 （微信）");
			ee.getCell(secondTitleRow, 11, fontStyle, "预约挂号 （支付宝）");
			ee.getCell(secondTitleRow, 13, fontStyle, "预约挂号 （银联）");
			ee.getCell(secondTitleRow, 15, fontStyle, "预约挂号（现金）");
			ee.getCell(secondTitleRow, 17, fontStyle, "缴费 （微信）");
			ee.getCell(secondTitleRow, 19, fontStyle, "缴费 （支付宝）");
			ee.getCell(secondTitleRow, 21, fontStyle, "缴费 （银联）");
			ee.getCell(secondTitleRow, 23, fontStyle, "缴费 （现金）");
			ee.getCell(secondTitleRow, 25, fontStyle, "住院预交金（微信）");
			ee.getCell(secondTitleRow, 27, fontStyle, "住院预交金 （支付宝）");
			ee.getCell(secondTitleRow, 29, fontStyle, "住院预交金 （银联）");
			ee.getCell(secondTitleRow, 31, fontStyle, "住院预交金 （现金）");
			ee.getCell(secondTitleRow, 33, fontStyle, "出院结算 （微信）");
			ee.getCell(secondTitleRow, 35, fontStyle, "出院结算 （支付宝）");
			ee.getCell(secondTitleRow, 37, fontStyle, "出院结算 （银联）");
			ee.getCell(secondTitleRow, 39, fontStyle, "出院结算 （现金）");
			ee.getCell(secondTitleRow, 41, fontStyle, "其他");

//			ee.getCell(secondTitleRow, 12, fontStyle, "");

			// 第三行标题、业务类型
			Row thirdTitleRow = ee.getRow(sheet, 2, null, height);

			String[] titleArray = { "收费员", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)",
					"笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数",
					"金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)",
					"笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)" };

			for (int i = 0, len = titleArray.length; i < len; i++) {
				ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
			}

			// 只有合计的话就不导出了
			if (dataList.size() > 1) {
				Map<String, Object> colMap = null;
				CellStyle doubleStyle = wb.createCellStyle();
				DataFormat df = wb.createDataFormat();
				doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
				setBorderStyle(doubleStyle);

				for (int i = 0, len = dataList.size(); i < len; i++) {

					Row dataRow = ee.getRow(sheet, i + 3, null, height);
					colMap = dataList.get(i);

//					ee.getCell(dataRow, 0, borderStyle,
//							colMap.get("businessType") != null ? colMap.get("businessType").toString() : "");
					MetaData cashierName = metaDataService
							.findMetaDataByValueAndTypeValue((String) colMap.get("businessType"), "cashier_type");

					Cell cell0 = dataRow.createCell(0);
					cell0.setCellStyle(borderStyle);
					if (cashierName != null) {
						cell0.setCellValue(cashierName.getName());
					} else {
						Object businessTypse = colMap.get("businessType");
						if (businessTypse != null) {
							cell0.setCellValue(businessTypse.toString());
						} else {
							cell0.setCellValue("");
						}
					}

					Cell cell1 = dataRow.createCell(1);
					cell1.setCellStyle(borderStyle);
					cell1.setCellValue(Integer.parseInt(colMap.get("drghWechatAcount").toString()));
					Cell cell2 = dataRow.createCell(2);
					cell2.setCellStyle(doubleStyle);
//					cell2.setCellValue(1234.12);
					cell2.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("drghWechatAmount"))));

					Cell cell3 = dataRow.createCell(3);
					cell3.setCellStyle(borderStyle);
					cell3.setCellValue(Integer.parseInt(colMap.get("drghZfbAcount").toString()));
					Cell cell4 = dataRow.createCell(4);
					cell4.setCellStyle(doubleStyle);
					cell4.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("drghZfbAmount"))));

					Cell cell5 = dataRow.createCell(5);
					cell5.setCellStyle(borderStyle);
					cell5.setCellValue(Integer.parseInt(colMap.get("drghBankAcount").toString()));
					Cell cell6 = dataRow.createCell(6);
					cell6.setCellStyle(doubleStyle);
					cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("drghBankAmount"))));

					Cell cell7 = dataRow.createCell(7);
					cell7.setCellStyle(borderStyle);
					cell7.setCellValue(Integer.parseInt(colMap.get("drghCashAcount").toString()));
					Cell cell8 = dataRow.createCell(8);
					cell8.setCellStyle(doubleStyle);
					cell8.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("drghCashAmount"))));

					Cell cell9 = dataRow.createCell(9);
					cell9.setCellStyle(borderStyle);
					cell9.setCellValue(Integer.parseInt(colMap.get("yyghWechatAcount").toString()));
					Cell cell10 = dataRow.createCell(10);
					cell10.setCellStyle(doubleStyle);
					cell10.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("yyghWechatAmount"))));

					Cell cell11 = dataRow.createCell(11);
					cell11.setCellStyle(borderStyle);
					cell11.setCellValue(Integer.parseInt(String.valueOf(colMap.get("yyghZfbAcount"))));
					Cell cell12 = dataRow.createCell(12);
					cell12.setCellStyle(doubleStyle);
					cell12.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("yyghZfbAmount"))));

					Cell cell13 = dataRow.createCell(13);
					cell13.setCellStyle(borderStyle);
					cell13.setCellValue(Integer.parseInt(String.valueOf(colMap.get("yyghBankAcount"))));
					Cell cell14 = dataRow.createCell(14);
					cell14.setCellStyle(doubleStyle);
					cell14.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("yyghBankAmount"))));

					Cell cell15 = dataRow.createCell(15);
					cell15.setCellStyle(borderStyle);
					cell15.setCellValue(Integer.parseInt(String.valueOf(colMap.get("yyghCashAcount"))));

					Cell cell16 = dataRow.createCell(16);
					cell16.setCellStyle(doubleStyle);
					cell16.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("yyghCashAmount"))));

					Cell cell17 = dataRow.createCell(17);
					cell17.setCellStyle(borderStyle);
					cell17.setCellValue(Integer.parseInt(colMap.get("jfWechatAcount").toString()));
					Cell cell18 = dataRow.createCell(18);
					cell18.setCellStyle(doubleStyle);
					cell18.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("jfWechatAmount"))));

					Cell cell19 = dataRow.createCell(19);
					cell19.setCellStyle(borderStyle);
					cell19.setCellValue(Integer.parseInt(String.valueOf(colMap.get("jfZfbAcount"))));
					Cell cell20 = dataRow.createCell(20);
					cell20.setCellStyle(doubleStyle);
					cell20.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("jfZfbAmount"))));

					Cell cell21 = dataRow.createCell(21);
					cell21.setCellStyle(borderStyle);
					cell21.setCellValue(Integer.parseInt(String.valueOf(colMap.get("jfBankAcount"))));
					Cell cell22 = dataRow.createCell(22);
					cell22.setCellStyle(doubleStyle);
					cell22.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("jfBankAmount"))));

					Cell cell23 = dataRow.createCell(23);
					cell23.setCellStyle(borderStyle);
					cell23.setCellValue(Integer.parseInt(String.valueOf(colMap.get("jfCashAcount"))));

					Cell cell24 = dataRow.createCell(24);
					cell24.setCellStyle(doubleStyle);
					cell24.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("jfCashAmount"))));

					Cell cell25 = dataRow.createCell(25);
					cell25.setCellStyle(borderStyle);
					cell25.setCellValue(Integer.parseInt(colMap.get("zyyjjWechatAcount").toString()));
					Cell cell26 = dataRow.createCell(26);
					cell26.setCellStyle(doubleStyle);
					cell26.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjWechatAmount"))));

					Cell cell27 = dataRow.createCell(27);
					cell27.setCellStyle(borderStyle);
					cell27.setCellValue(Integer.parseInt(String.valueOf(colMap.get("zyyjjZfbAcount"))));
					Cell cell28 = dataRow.createCell(28);
					cell28.setCellStyle(doubleStyle);
					cell28.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjZfbAmount"))));

					Cell cell29 = dataRow.createCell(29);
					cell29.setCellStyle(borderStyle);
					cell29.setCellValue(Integer.parseInt(String.valueOf(colMap.get("zyyjjBankAcount"))));
					Cell cell30 = dataRow.createCell(30);
					cell30.setCellStyle(doubleStyle);
					cell30.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjBankAmount"))));

					Cell cell31 = dataRow.createCell(31);
					cell31.setCellStyle(borderStyle);
					cell31.setCellValue(Integer.parseInt(String.valueOf(colMap.get("zyyjjCashAcount"))));
					Cell cell32 = dataRow.createCell(32);
					cell32.setCellStyle(doubleStyle);
					cell32.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjCashAmount"))));

					Cell cell33 = dataRow.createCell(33);
					cell33.setCellStyle(borderStyle);
					cell33.setCellValue(Integer.parseInt(String.valueOf(colMap.get("cyjsWechatAcount"))));
					Cell cell34 = dataRow.createCell(34);
					cell34.setCellStyle(doubleStyle);
					cell34.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("cyjsWechatAmount"))));

					Cell cell35 = dataRow.createCell(35);
					cell35.setCellStyle(borderStyle);
					cell35.setCellValue(Integer.parseInt(String.valueOf(colMap.get("cyjsZfbAcount"))));
					Cell cell36 = dataRow.createCell(36);
					cell36.setCellStyle(doubleStyle);
					cell36.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("cyjsZfbAmount"))));

					Cell cell37 = dataRow.createCell(37);
					cell37.setCellStyle(borderStyle);
					cell37.setCellValue(Integer.parseInt(String.valueOf(colMap.get("cyjsBankAcount"))));
					Cell cell38 = dataRow.createCell(38);
					cell38.setCellStyle(doubleStyle);
					cell38.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("cyjsBankAmount"))));

					Cell cell39 = dataRow.createCell(39);
					cell39.setCellStyle(borderStyle);
					cell39.setCellValue(Integer.parseInt(String.valueOf(colMap.get("cyjsCashAcount"))));
					Cell cell40 = dataRow.createCell(40);
					cell40.setCellStyle(doubleStyle);
					cell40.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("cyjsCashAmount"))));

					BigDecimal allAcount = (BigDecimal) colMap.get("allAcount");
					BigDecimal drghAcount = (BigDecimal) colMap.get("drghAcount");
					BigDecimal jfAcount = (BigDecimal) colMap.get("jfAcount");
					BigDecimal yyghAcount = (BigDecimal) colMap.get("yyghAcount");
					BigDecimal zyyjjAcount = (BigDecimal) colMap.get("zyyjjAcount");
					BigDecimal cyjsAcount = (BigDecimal) colMap.get("cyjsAcount");
					BigDecimal otherAcount = allAcount.subtract(drghAcount).subtract(jfAcount).subtract(yyghAcount)
							.subtract(zyyjjAcount).subtract(cyjsAcount);

					Cell cell41 = dataRow.createCell(41);
					cell41.setCellStyle(borderStyle);
					cell41.setCellValue(otherAcount.doubleValue());

					BigDecimal allAmount = (BigDecimal) colMap.get("allAmount");
					BigDecimal drghAmount = (BigDecimal) colMap.get("drghAmount");
					BigDecimal jfAmount = (BigDecimal) colMap.get("jfAmount");
					BigDecimal yyghAmount = (BigDecimal) colMap.get("yyghAmount");
					BigDecimal zyyjjAmount = (BigDecimal) colMap.get("zyyjjAmount");
					BigDecimal cyjsAmount = (BigDecimal) colMap.get("cyjsAmount");
					BigDecimal otherAmount = allAmount.subtract(drghAmount).subtract(jfAmount).subtract(yyghAmount)
							.subtract(zyyjjAmount).subtract(cyjsAmount);

					Cell cell42 = dataRow.createCell(42);
					cell42.setCellStyle(doubleStyle);
					cell42.setCellValue(otherAmount.doubleValue());

				}
			}
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		}
	}
}
