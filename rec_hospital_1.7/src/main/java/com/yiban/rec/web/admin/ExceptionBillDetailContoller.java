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
import org.springframework.web.bind.annotation.ResponseBody;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.Organization;
import com.yiban.framework.account.service.OrganizationService;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.service.ExceptionBillDetailService;
import com.yiban.rec.util.CommonEnum;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;

/**
 * 异常账单明细controller
 */
@Controller
@RequestMapping("admin/exceptionbill")
public class ExceptionBillDetailContoller extends CurrentUserContoller {

	@Autowired
	private ExceptionBillDetailService exceptionBillDetailService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private MetaDataService metaDataService;

	@GetMapping(value = "page")
	@ResponseBody
	public WebUiPage<TradeCheckFollow> page(String orgCode, String billSource, String date,String tradeType) {
		PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, "tradeTime"));

		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}
		Page<TradeCheckFollow> page = exceptionBillDetailService.queryPage(orgCode, billSource, date,tradeType, orgList,
				pageable);
		return super.toWebUIPage(page);
	}

	// TODO 此方法暂时不用
	@PostMapping(value = "sum")
	@ResponseBody
	public ResponseResult querySum(String orgCode, String billSource, String date) {
		ResponseResult result = ResponseResult.success();

		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}

		return result.data(null);
	}

	@GetMapping(value = "dcExcel")
	public void exportExcel(String orgCode, String billSource, String date, BigDecimal amount,String tradeType,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		List<Organization> orgList = null;
		if (null != orgCode) {
			orgList = organizationService.findByParentCode(orgCode);
		}

		List<TradeCheckFollow> ts = exceptionBillDetailService.queryNoPage(orgCode, billSource, date,tradeType, orgList,
				new Sort(Direction.DESC, "tradeTime"));
		Map<String, String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());

		HashMap<Integer, String> checkStateName = new HashMap<>();
		checkStateName.put(CommonEnum.BillBalance.HISDC.getValue(), "短款");
		checkStateName.put(CommonEnum.BillBalance.HEALTHCAREHIS.getValue(), "短款");
		checkStateName.put(CommonEnum.BillBalance.THIRDDC.getValue(), "长款");
		checkStateName.put(CommonEnum.BillBalance.HEALTHCAREOFFI.getValue(), "长款");

		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map = null;
		for (TradeCheckFollow t : ts) {
			map = new HashMap<>();
			map.put("hisFlowNo", t.getHisFlowNo());
			map.put("payFlowNo", t.getBusinessNo());
			map.put("payTypeName", metaMap.get(t.getPayName()));

			if ("短款".equals(checkStateName.get(t.getCheckState()))) {
				map.put("payAmount", t.getTradeAmount().abs().negate());
			} else {
				map.put("payAmount", t.getTradeAmount().abs());
			}
			map.put("orderStateName", metaMap.get(t.getTradeName()));
			map.put("billSourceName", metaMap.get(t.getBillSource()));
			map.put("tradeDatetime", t.getTradeTime());
			map.put("checkStateName", checkStateName.get(t.getCheckState()));
			map.put("remark", remarkformatter(t.getCheckState(), t.getTradeName()));
			mapList.add(map);
		}
		String[] titleArray = { "HIS流水号", "支付方流水号", "支付类型", "交易金额(元)", "交易类型", "渠道名称", "交易时间", "状态", "备注" };
		String fileName = date + " 异常账单明细";
		this.excel(fileName, "异常账单明细", request, response, mapList, titleArray, amount);
	}

	public String remarkformatter(Integer checkState, String orderState) {
		String remark = "";
		if (CommonEnum.BillBalance.THIRDDC.getValue().equals(checkState)
				|| CommonEnum.BillBalance.HEALTHCAREOFFI.getValue().equals(checkState)) {
			if ("0156".equals(orderState)) {
				remark = "建议：HIS补数据";
			} else {
				remark = "建议：商户退给患者";
			}
		} else if (CommonEnum.BillBalance.HISDC.getValue().equals(checkState)
				|| CommonEnum.BillBalance.HEALTHCAREHIS.getValue().equals(checkState)) {
			if ("0156".equals(orderState)) {
				remark = "建议：商户补金额";
			} else {
				remark = "建议：HIS减数据";
			}
		}
		return remark;
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
			if (i == titleArray.length - 1) {
				sheet.setColumnWidth(i, titleArray[i].getBytes().length * 3 * 256);
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

			String dateFormat = "yyyy-MM-dd HH:mm:ss";
			for (int i = 0, len = dataList.size(); i < len; i++) {
				Row dataRow = ee.getRow(sheet, i + 2, null, height);
				colMap = dataList.get(i);

				// 字符串
				ee.getCell(dataRow, 0, borderStyle,
						colMap.get("hisFlowNo") == null ? "" : colMap.get("hisFlowNo").toString());
				ee.getCell(dataRow, 1, borderStyle, colMap.get("payFlowNo")==null?"":colMap.get("payFlowNo").toString());
				ee.getCell(dataRow, 2, borderStyle, colMap.get("payTypeName")==null?"":colMap.get("payTypeName").toString());
				// double
				Cell cell2 = dataRow.createCell(3);
				cell2.setCellStyle(doubleStyle);
				cell2.setCellValue(Double.parseDouble(colMap.get("payAmount")==null?"0.00":colMap.get("payAmount").toString()));

				ee.getCell(dataRow, 4, borderStyle, colMap.get("orderStateName")==null?"":colMap.get("orderStateName").toString());
				ee.getCell(dataRow, 5, borderStyle, colMap.get("billSourceName")==null?"":colMap.get("billSourceName").toString());
				ee.getCell(dataRow, 6, borderStyle,
						DateUtil.transferDateToDateFormat(dateFormat, (Date) colMap.get("tradeDatetime")));
				ee.getCell(dataRow, 7, borderStyle, colMap.get("checkStateName")==null?"":colMap.get("checkStateName").toString());
				ee.getCell(dataRow, 8, borderStyle, colMap.get("remark")==null?"":colMap.get("remark").toString());
			}
		}

		// 汇总金额, 合并单元格
		int countColumnIndex = dataList.size() + 3;
		sheet.addMergedRegion(new CellRangeAddress(countColumnIndex, countColumnIndex, 0, titleArray.length - 1));
		Row titleRow = ee.getRow(sheet, countColumnIndex, null, height);
		ee.getCell(titleRow, 0, fontStyle, "(长款-短款)差异总金额(元)：" + amount);
		
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
