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
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.service.BillDataReportService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * @Description
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-06-1 10:45
 */
@Controller
@RequestMapping("")
public class BillDataController {

    @Autowired
    private GatherService gatherService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private BillDataReportService billDataReportService;

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

    /**
     * ??????????????????
     */
    @Controller
    @RequestMapping(value = "admin/billData/")
    public class BillDataReportsController {
        /**
         * ??????????????????????????????
         */
        @RequestMapping(value = "index")
        public String index(ModelMap model) {
            String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
            model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
            model.put("orgCode", orgCode);
            model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
            model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));
            return "reports/billDataReports";
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
//            query.setColumnSql(" Cashier ");
            List<Map<String, Object>> res = billDataReportService.findAllBillAndHisDataByDate(query);
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
//            query.setColumnSql(" Cashier ");
            List<Map<String, Object>> res = billDataReportService.findAllBillAndHisDataByDate(query);

            commonExportExcel(fileName, workSheetName, request, response, res);
        }

        public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
                                      HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception {
            setResponseAdRequest(request, response, fileName);
            ExportExcel ee = new ExportExcel();
            // workbook????????????Excel
            HSSFWorkbook wb = new HSSFWorkbook();
            // ??????????????????????????????:?????????,??????
            HSSFCellStyle fontStyle = wb.createCellStyle();
            fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            setBorderStyle(fontStyle);
            HSSFCellStyle borderStyle = wb.createCellStyle();
            setBorderStyle(borderStyle);
            // ????????????sheet
            Sheet sheet = ee.getSheet(wb, workSheetName);
            // ???????????????, ???????????????
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));
            Row titleRow = ee.getRow(sheet, 0, null, height);
            ee.getCell(titleRow, 0, fontStyle, fileName);
            // ?????????????????????????????????????????????????????????
            for (int i = 1; i < 12; i++) {
                ee.getCell(titleRow, i, fontStyle, "");
            }

            // ??????????????????????????????
            Row thirdTitleRow = ee.getRow(sheet, 2, null, height);
            String[] titleArray = {"?????????(??????)", "??????(??????)", "????????????(???)", "?????????(??????)???", "??????(??????)???",
                    "?????????(??????)???", "?????????(His)", "??????(His)", "His??????(???)", "?????????(His)???", "??????(His)???", "?????????(His)???"};
            for (int i = 0, len = titleArray.length; i < len; i++) {
                ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
            }

            // ?????????????????????????????????
            if (dataList.size() > 1) {
                Map<String, Object> colMap = null;
                CellStyle doubleStyle = wb.createCellStyle();
                DataFormat df = wb.createDataFormat();
                doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
                setBorderStyle(doubleStyle);
                for (int i = 0, len = dataList.size(); i < len; i++) {
                    Row dataRow = ee.getRow(sheet, i + 3, null, height);
                    colMap = dataList.get(i);
                    // ?????????
                    Cell cell1 = dataRow.createCell(0);
                    cell1.setCellStyle(borderStyle);
                    if (i == len - 1){
                        sheet.addMergedRegion(new CellRangeAddress(len + 2, len + 2, 0, 11));
                    }
                    String Cashiera = colMap.get("Cashiera") == null ? "" : colMap.get("Cashiera").toString();
                    cell1.setCellValue(Cashiera);
                    // ??????????????????
                    Cell cell2 = dataRow.createCell(1);
                    cell2.setCellStyle(doubleStyle);
                    String countBill = formatterData(colMap.get("countBill")).equals("") ? "0" : formatterData(colMap.get("countBill"));
                    cell2.setCellValue(Integer.valueOf(countBill));
                    // ??????????????????
                    Cell cell3 = dataRow.createCell(2);
                    cell3.setCellStyle(doubleStyle);
                    cell3.setCellValue(formatterData(colMap.get("payAmountBill")));
                    // ?????????(??????)
                    Cell cell4 = dataRow.createCell(3);
                    cell4.setCellStyle(doubleStyle);
                    cell4.setCellValue(formatterData(colMap.get("bankBill")));
                    // ??????(??????)
                    Cell cell5 = dataRow.createCell(4);
                    cell5.setCellStyle(doubleStyle);
                    cell5.setCellValue(formatterData(colMap.get("wechatBill")));
                    // ?????????(??????)
                    Cell cell6 = dataRow.createCell(5);
                    cell6.setCellStyle(doubleStyle);
                    cell6.setCellValue(formatterData(colMap.get("alipayBill")));
                    // ?????????(His)
                    Cell cell7 = dataRow.createCell(6);
                    cell7.setCellStyle(doubleStyle);
                    String Cashierb = colMap.get("Cashierb") == null ? "" : colMap.get("Cashierb").toString();
                    cell7.setCellValue(Cashierb);
                    // ?????????(His)
                    Cell cell8 = dataRow.createCell(7);
                    cell8.setCellStyle(doubleStyle);
                    String countHis = formatterData(colMap.get("countHis")).equals("") ? "0" : formatterData(colMap.get("countHis"));
                    cell8.setCellValue(Integer.valueOf(countHis));
                    // ?????????His???
                    Cell cell9 = dataRow.createCell(8);
                    cell9.setCellStyle(doubleStyle);
                    cell9.setCellValue(formatterData(colMap.get("payAmountHis")));
                    // ????????????His???
                    Cell cell10 = dataRow.createCell(9);
                    cell10.setCellStyle(doubleStyle);
                    cell10.setCellValue(formatterData(colMap.get("bankHis")));
                    // ?????????His???
                    Cell cell11 = dataRow.createCell(10);
                    cell11.setCellStyle(doubleStyle);
                    cell11.setCellValue(formatterData(colMap.get("wechatHis")));
                    // ?????????(His)
                    Cell cell12 = dataRow.createCell(11);
                    cell12.setCellStyle(doubleStyle);
                    cell12.setCellValue(formatterData(colMap.get("alipayHis")));
                }
            }
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.flush();
            out.close();
        }

        private String formatterData(Object obj) {
            return obj == null ? "" : obj.toString();
        }

    }

}
