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
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.rec.service.DataSummaryService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * describe:
 *
 * @author xll 数据汇总报表  包含（His、渠道）
 * @date 2019/09/11
 */
@Controller
@RequestMapping(value = "admin/datasummary/report")
public class DataSummaryReportController {

    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private GatherService gatherService;
    @Autowired
    private DataSummaryService dataSummaryService;

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

    /**
     * 进入渠道汇总报表页面
     */
    @RequestMapping(value = "index")
    public String index(ModelMap model) {
        String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
        model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
        model.put("orgCode", orgCode);
        model.put("beginTime", DateUtil.getCurrentDateString());
        model.put("endTime", DateUtil.getCurrentDateString());
        return "reports/ThridDataSummaryReport";
    }
    /**
     * 进入渠道汇总报表页面
     */
    @RequestMapping(value = "hisIndex")
    public String hisIndex(ModelMap model) {
        String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
        model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
        model.put("beginTime", DateUtil.getCurrentDateString());
        model.put("orgCode", orgCode);
        model.put("endTime", DateUtil.getCurrentDateString());
        return "reports/HisDataSummaryReport";
    }

    @RequestMapping(value = "data")
    @ResponseBody
    public WebUiPage<Map<String, Object>> data(DataSummaryService.SummaryQuery query) {
        if (StringUtil.isEmpty(query.getBeginTime())) {
            query.setBeginTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 0));
        }
        if (StringUtil.isEmpty(query.getEndTime())) {
            query.setEndTime(DateUtil.getSpecifiedDayBeforeDay(new Date(), 0));
        }
        if (StringUtil.isEmpty(query.getOrgCode())) {
            String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            query.setOrgCode(orgCode);
        }
        List<Map<String, Object>> res = dataSummaryService.findAllSummaryByDate(query);
        return new WebUiPage<>(res.size(), res);
    }

    @RequestMapping(value = "dcExcel")
    @ResponseBody
    public void dcExcel(DataSummaryService.SummaryQuery query, String fileName, String workSheetName,
                        HttpServletRequest request, HttpServletResponse response) {
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
        List<Map<String, Object>> res = dataSummaryService.findAllSummaryByDate(query);
        try {
            commonExportExcel(fileName, workSheetName, request, response, res);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
                                  HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
        setResponseAdRequest(request, response, fileName);
        ExportExcel ee = new ExportExcel();
        // workbook对应一个Excel
        HSSFWorkbook wb = new HSSFWorkbook();
        // 定义一个统一字体样式:、居中,边框
        HSSFCellStyle fontStyle = wb.createCellStyle();
        fontStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
        fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        setBorderStyle(fontStyle);

        HSSFCellStyle borderStyle = wb.createCellStyle();
        setBorderStyle(borderStyle);

        // 创建一个sheet
        Sheet sheet = ee.getSheet(wb, workSheetName);
        // 第一行标题, 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
        Row titleRow = ee.getRow(sheet, 0, null, height);
        ee.getCell(titleRow, 0, fontStyle, fileName);
        // 创建等多的单元格，解决合并单元格的问题
        for (int i = 1; i < 11; i++) {
            ee.getCell(titleRow, i, fontStyle, "");
        }
        // 第二行标题、支付类型
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 10));

        Row secondTitleRow = ee.getRow(sheet, 1, null, height);
        ee.getCell(secondTitleRow, 0, fontStyle, "院区");
        ee.getCell(secondTitleRow, 1, fontStyle, "渠道");
        ee.getCell(secondTitleRow, 2, fontStyle, "数据来源");
        ee.getCell(secondTitleRow, 3, fontStyle, "支付宝");
        ee.getCell(secondTitleRow, 5, fontStyle, "微信");
        ee.getCell(secondTitleRow, 7, fontStyle, "银联");
        ee.getCell(secondTitleRow, 8, fontStyle, "");
        ee.getCell(secondTitleRow, 9, fontStyle, "合计");
        ee.getCell(secondTitleRow, 10, fontStyle, "");

        Row thridTitleRow = ee.getRow(sheet, 2, null, height);
        ee.getCell(thridTitleRow, 3, fontStyle, "金额");
        ee.getCell(thridTitleRow, 4, fontStyle, "笔数");
        ee.getCell(thridTitleRow, 5, fontStyle, "金额");
        ee.getCell(thridTitleRow, 6, fontStyle, "笔数");
        ee.getCell(thridTitleRow, 7, fontStyle, "金额");
        ee.getCell(thridTitleRow, 8, fontStyle, "笔数");
        ee.getCell(thridTitleRow, 9, fontStyle, "金额");
        ee.getCell(thridTitleRow, 10, fontStyle, "笔数");

        if (dataList.size() > 0) {
            Map<String, Object> colMap = null;
            CellStyle doubleStyle = wb.createCellStyle();
            DataFormat df = wb.createDataFormat();
            doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
            setBorderStyle(doubleStyle);
            for (int i = 0, len = dataList.size(); i < len; i++) {
                colMap = dataList.get(i);
                Row dataRow = ee.getRow(sheet, i + 3, null, height);
                // 院区
                ee.getCell(dataRow, 0, borderStyle, colMap.get("orgNo").toString());
                Cell cell1 = dataRow.createCell(1);
                cell1.setCellStyle(borderStyle);
                cell1.setCellValue(String.valueOf(colMap.get("name")));
                // 数据来源  住院 门诊
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellStyle(borderStyle);
                cell2.setCellValue(String.valueOf(colMap.get("patType")));
                // 支付宝金额
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellStyle(doubleStyle);
                cell3.setCellValue(Double.parseDouble(String.valueOf(
                        colMap.get("aliPay") == null ? "0.00" : colMap.get("aliPay"))));
                // 支付宝笔数
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellStyle(borderStyle);
                cell4.setCellValue(String.valueOf(colMap.get("aliCount")));

                // 微信金额
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellStyle(doubleStyle);
                cell5.setCellValue(Double.parseDouble(String.valueOf(
                        colMap.get("weCheat") == null ? "0.00" : colMap.get("weCheat"))));
                // 微信笔数
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellStyle(borderStyle);
                cell6.setCellValue(String.valueOf(colMap.get("weCheatCount")));

                // 银联金额
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellStyle(doubleStyle);
                cell7.setCellValue(Double.parseDouble(String.valueOf(
                        colMap.get("bank") == null ? "0.00" : colMap.get("bank"))));
                // 银联笔数
                Cell cell8 = dataRow.createCell(8);
                cell8.setCellStyle(borderStyle);
                cell8.setCellValue(String.valueOf(colMap.get("bankCount")));

                // 银联金额
                Cell cell9 = dataRow.createCell(9);
                cell9.setCellStyle(doubleStyle);
                cell9.setCellValue(Double.parseDouble(String.valueOf(
                        colMap.get("sumPay") == null ? "0.00" : colMap.get("sumPay"))));
                // 银联笔数
                Cell cell10 = dataRow.createCell(10);
                cell10.setCellStyle(borderStyle);
                cell10.setCellValue(String.valueOf(colMap.get("sumCount")));


            }
        }
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }

    public void setBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    }
}
