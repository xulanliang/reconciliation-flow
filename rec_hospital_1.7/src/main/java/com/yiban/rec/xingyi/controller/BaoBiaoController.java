package com.yiban.rec.xingyi.controller;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;
import com.yiban.rec.xingyi.service.BaoBiaoService;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/baobiao"})
public class BaoBiaoController extends CurrentUserContoller {
  private final int height = 18;
  
  @Autowired
  private BaoBiaoService baoBiaoService;
  
  @RequestMapping({"/tobaobiao"})
  public String toRefundTableUI(ModelMap model) {
    model.put("nowDate", DateUtil.getSpecifiedDayBefore(new Date()));
    return autoView("admin/baobiao");
  }
  
  @GetMapping({"/getTableList"})
  @ResponseBody
  public WebUiPage<Map<String, Object>> getTableList(String rangeTime) {
    if (StringUtil.isEmpty(rangeTime)) {
      rangeTime = DateUtil.getSpecifiedDayBefore(new Date()) + "~" + DateUtil.getSpecifiedDayBefore(new Date());
    } else {
      rangeTime = rangeTime.split("~")[0].trim() + "~" + rangeTime.split("~")[1].trim();
    } 
    String startTime = rangeTime.split("~")[0].trim();
    String endTime = rangeTime.split("~")[1].trim();
    PageRequest pagerequest = getRequestPageable();
    List<Map<String, Object>> data = this.baoBiaoService.getList(pagerequest, startTime, endTime);
    return new WebUiPage(data.size(), data);
  }
  
  @RequestMapping({"/dcExcel"})
  public void dcExcel(String rangeTime, String fileName, String workSheetName, HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (StringUtil.isEmpty(rangeTime)) {
      rangeTime = DateUtil.getSpecifiedDayBefore(new Date()) + "~" + DateUtil.getSpecifiedDayBefore(new Date());
    } else {
      rangeTime = rangeTime.split("~")[0].trim() + "~" + rangeTime.split("~")[1].trim();
    } 
    String startTime = rangeTime.split("~")[0].trim();
    String endTime = rangeTime.split("~")[1].trim();
    PageRequest pagerequest = getRequestPageable();
    List<Map<String, Object>> data = this.baoBiaoService.getList(pagerequest, startTime, endTime);
    commonExportExcel(fileName, workSheetName, request, response, data);
  }
  
  public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request, HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
    setResponseAdRequest(request, response, fileName);
    ExportExcel ee = new ExportExcel();
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFCellStyle fontStyle = wb.createCellStyle();
    fontStyle.setAlignment((short)2);
    setBorderStyle((CellStyle)fontStyle);
    HSSFCellStyle borderStyle = wb.createCellStyle();
    setBorderStyle((CellStyle)borderStyle);
    Sheet sheet = ee.getSheet((Workbook)wb, workSheetName);
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 14));
    Row titleRow = ee.getRow(sheet, 0, null, 18);
    ee.getCell(titleRow, 0, (CellStyle)fontStyle, fileName);
    for (int i = 1; i < 14; i++)
      ee.getCell(titleRow, i, (CellStyle)fontStyle, ""); 
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 4));
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 7));
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 8, 10));
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 13));
    Row secondTitleRow = ee.getRow(sheet, 1, null, 18);
    ee.getCell(secondTitleRow, 0, (CellStyle)fontStyle, "");
    ee.getCell(secondTitleRow, 1, (CellStyle)fontStyle, "");
    ee.getCell(secondTitleRow, 2, (CellStyle)fontStyle, "挂号");
    ee.getCell(secondTitleRow, 5, (CellStyle)fontStyle, "缴费");
    ee.getCell(secondTitleRow, 8, (CellStyle)fontStyle, "住院预交金");
    ee.getCell(secondTitleRow, 11, (CellStyle)fontStyle, "合计");
    Row fourTitleRow = ee.getRow(sheet, 2, null, 18);
    String[] titleArray = { 
        "日期", "收费员", "微信", "支付宝", "银行卡", "微信", "支付宝", "银行卡", "微信", "支付宝", 
        "银行卡", "微信", "支付宝", "银行卡"};
    for (int j = 0, len = titleArray.length; j < len; j++)
      ee.getCell(fourTitleRow, j, (CellStyle)fontStyle, titleArray[j]); 
    if (dataList.size() > 1) {
      Map<String, Object> colMap = null;
      HSSFCellStyle hSSFCellStyle = wb.createCellStyle();
      HSSFDataFormat hSSFDataFormat = wb.createDataFormat();
      hSSFCellStyle.setDataFormat(hSSFDataFormat.getFormat("#,#0.00"));
      setBorderStyle((CellStyle)hSSFCellStyle);
      for (int k = 0, m = dataList.size(); k < m; k++) {
        Row dataRow = ee.getRow(sheet, k + 3, null, 18);
        colMap = dataList.get(k);
        ee.getCell(dataRow, 0, (CellStyle)borderStyle, 
            (colMap.get("date") != null) ? colMap.get("date").toString() : "");
        Cell cell1 = dataRow.createCell(1);
        cell1.setCellStyle((CellStyle)borderStyle);
        cell1.setCellValue(colMap.get("cashier").toString());
        Cell cell2 = dataRow.createCell(2);
        cell2.setCellStyle((CellStyle)hSSFCellStyle);
        cell2.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("guahaoweixinPayAmount"))));
        Cell cell3 = dataRow.createCell(3);
        cell3.setCellStyle((CellStyle)borderStyle);
        cell3.setCellValue(Double.parseDouble(colMap.get("guahaozhifubaoPayAmount").toString()));
        Cell cell4 = dataRow.createCell(4);
        cell4.setCellStyle((CellStyle)hSSFCellStyle);
        cell4.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("guahaobankPayAmount"))));
        Cell cell5 = dataRow.createCell(5);
        cell5.setCellStyle((CellStyle)borderStyle);
        cell5.setCellValue(Double.parseDouble(colMap.get("jiaofeiweixinPayAmount").toString()));
        Cell cell6 = dataRow.createCell(6);
        cell6.setCellStyle((CellStyle)hSSFCellStyle);
        cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("jiaofeizhifubaoPayAmount"))));
        Cell cell7 = dataRow.createCell(7);
        cell7.setCellStyle((CellStyle)borderStyle);
        cell7.setCellValue(Double.parseDouble(colMap.get("jiaofeibankPayAmount").toString()));
        Cell cell8 = dataRow.createCell(8);
        cell8.setCellStyle((CellStyle)hSSFCellStyle);
        cell8.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjweixinPayAmount"))));
        Cell cell9 = dataRow.createCell(9);
        cell9.setCellStyle((CellStyle)hSSFCellStyle);
        cell9.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjzhifubaoPayAmount"))));
        Cell cell10 = dataRow.createCell(10);
        cell10.setCellStyle((CellStyle)hSSFCellStyle);
        cell10.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjbankPayAmount"))));
        Cell cell11 = dataRow.createCell(11);
        cell11.setCellStyle((CellStyle)hSSFCellStyle);
        cell11.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zjweixinPayAmount"))));
        Cell cell12 = dataRow.createCell(12);
        cell12.setCellStyle((CellStyle)hSSFCellStyle);
        cell12.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zjzhifubaoPayAmount"))));
        Cell cell13 = dataRow.createCell(13);
        cell13.setCellStyle((CellStyle)hSSFCellStyle);
        cell13.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zjbankPayAmount"))));
      } 
    } 
    ServletOutputStream servletOutputStream = response.getOutputStream();
    wb.write((OutputStream)servletOutputStream);
    servletOutputStream.flush();
    servletOutputStream.close();
  }
  
  public void setResponseAdRequest(HttpServletRequest request, HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
    response.setContentType("application/vnd.ms-excel;charset=utf-8");
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    request.setCharacterEncoding("utf-8");
    response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName
          .getBytes(), "iso-8859-1") + ".xls");
  }
  
  public void setBorderStyle(CellStyle cellStyle) {
    cellStyle.setBorderBottom((short)1);
    cellStyle.setBorderLeft((short)1);
    cellStyle.setBorderRight((short)1);
    cellStyle.setBorderTop((short)1);
  }
  
  public String getNotNullStr(Object str) {
    return (str == null) ? "0" : String.valueOf(str).trim();
  }
}
