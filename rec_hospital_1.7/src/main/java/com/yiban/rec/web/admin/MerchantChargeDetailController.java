package com.yiban.rec.web.admin;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.bill.parse.util.EnumTypeOfInt;
import com.yiban.rec.domain.ThirdBill;
import com.yiban.rec.service.MerchantChargeDetailService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;

/**
 * 商户收费明细controller
 */
@Controller
@RequestMapping(value = "merchant/bill")
public class MerchantChargeDetailController extends CurrentUserContoller {

	@Autowired
	private MerchantChargeDetailService chargeDetailService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private MetaDataService metaDataService;

	/**
	 * 
	 * @param orgCode
	 * @param billSource
	 * @param date
	 * @param tsnOrderNo
	 * @return
	 */
	@GetMapping("page")
	@ResponseBody
	public WebUiPage<ThirdBill> queryList(String orgCode, @RequestParam(required = false) String billSource,
			String date, @RequestParam(required = false) String tsnOrderNo,String tradeType) {

		PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, "tradeDatatime"));

		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}
		Page<ThirdBill> page = chargeDetailService.queryThirdBillPage(billSource, date, tsnOrderNo, orgList,tradeType, pageable);
		return this.toWebUIPage(page);
	}

	/**
	 * 商户收款明细页面总金额汇总
	 * 
	 * @param orgCode
	 * @param billSource
	 * @param date
	 * @param tsnOrderNo
	 * @return
	 */
	@PostMapping("sum")
	@ResponseBody
	public ResponseResult querySum(String orgCode, String billSource, String date, String tsnOrderNo,String tradeType) {
		ResponseResult result = ResponseResult.success();
		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}
		BigDecimal amount = chargeDetailService.querySum(billSource, date, tsnOrderNo, orgList,tradeType);
		return result.data(amount);
	}

	@GetMapping("dcExcel")
	@ResponseBody
	public void exportExcel(String orgCode, String billSource, String date, String tsnOrderNo,String tradeType,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}
		List<ThirdBill> billList = chargeDetailService.queryThirdBillNoPage(billSource, date, tsnOrderNo, orgList,tradeType,
				new Sort(Direction.DESC, "tradeDatatime"));
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

		BigDecimal amount = chargeDetailService.querySum(billSource, date, tsnOrderNo, orgList,tradeType);

		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map = null;
		for (ThirdBill th : billList) {
			map = new HashMap<>();
			map.put("payFlowNo", th.getPayFlowNo());
			map.put("payTypeName", metaMap.get(th.getPayType()));

			if (EnumTypeOfInt.REFUND_CODE.getValue().equals(th.getOrderState())) {
				// 退费的设置为负数
				map.put("payAmount", th.getPayAmount().abs().negate());
			}else {
				map.put("payAmount", th.getPayAmount().abs());
			}

			map.put("tradeDatetime", th.getTradeDatatime());
			map.put("billSourceName", metaMap.get(th.getBillSource()));
			mapList.add(map);
		}

		String[] titleArray = { "支付方流水号", "支付类型", "交易金额(元)", "交易时间", "渠道名称" };
		String fileName = date + " 商户收款明细";
		excel(fileName, "商户收款明细", request, response, mapList, titleArray, amount);
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
			ee.getCell(titleRow0, i, borderStyle, "");
		}
		// 第二行标题
		Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
		for (int i = 0, len = titleArray.length; i < len; i++) {
			ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
			sheet.setColumnWidth(i, titleArray[i].getBytes().length*256);
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
				ee.getCell(dataRow, 0, borderStyle, colMap.get("payFlowNo").toString());
				ee.getCell(dataRow, 1, borderStyle, colMap.get("payTypeName").toString());
				// double
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double.parseDouble(colMap.get("payAmount").toString()));

				ee.getCell(dataRow, 3, borderStyle,
						DateUtil.transferDateToDateFormat(dateFormat, (Date) colMap.get("tradeDatetime")));
				ee.getCell(dataRow, 4, borderStyle, colMap.get("billSourceName").toString());
			}
		}
		
		// 汇总金额, 合并单元格
		int countColumnIndex = dataList.size() + 3;
		sheet.addMergedRegion(new CellRangeAddress(countColumnIndex, countColumnIndex, 0, titleArray.length - 1));
		Row titleRow = ee.getRow(sheet, countColumnIndex, null, height);
//		HSSFCellStyle amountStyle = wb.createCellStyle();
//		amountStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//		Font font = wb.createFont();
//		font.setFontHeightInPoints((short) 20);
//		amountStyle.setFont(font);
//		setBorderStyle(amountStyle);
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
