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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.reports.AdvancePaySummaryDetailListVo;
import com.yiban.rec.domain.reports.AdvancePaySummaryListVo;
import com.yiban.rec.service.AdvancePaySummaryService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * @Description 市二预收款汇总报表
 * @Author xll
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2019-06-1 10:45
 */
@Controller
@RequestMapping("")
public class AdvancePaySummaryController {

    @Autowired
    private GatherService gatherService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private AdvancePaySummaryService advancePaySummaryService;

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
     * 渠道数据报表
     */
    @Controller
    @RequestMapping(value = "admin/advance_pay/")
    public class BillDataReportsController {
        /**
         * 进入业务类型汇总页面
         */
        @RequestMapping(value = "index")
        public String index(ModelMap model) {
            String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
            model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
            model.put("orgCode", orgCode);
            model.put("beginTime", DateUtil.getLastMonth("yyyy-MM", new Date()));

            return "reports/advancePaySummaryReports";
        }

        @RequestMapping(value = "data")
        @ResponseBody
        public WebUiPage<Map<String, Object>> data(AdvancePaySummaryListVo queryListVo) {
            if (StringUtil.isEmpty(queryListVo.getDate())) {
                queryListVo.setDate(DateUtil.getLastMonth("yyyy-MM", new Date()));
            }
            if (StringUtil.isEmpty(queryListVo.getOrgCode())) {
                String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
                queryListVo.setOrgCode(orgCode);
            }
            List<Map<String, Object>> res = advancePaySummaryService.findAdvancePaySummaryByParams(queryListVo);
            return new WebUiPage<>(res.size(), res);
        }

        /**
         * 增加款、减少款详情列表
         *
         * @param paySummaryDetailListVo
         * @return
         */
        @RequestMapping(value = "detail/list")
        @ResponseBody
        public WebUiPage<Map<String, Object>> detailList(AdvancePaySummaryDetailListVo paySummaryDetailListVo) {
            List<Map<String, Object>> res = advancePaySummaryService.findAdvancePaySummaryDetailListByParams(paySummaryDetailListVo);
            return new WebUiPage<>(res.size(), res);
        }

        /**
         * 获取渠道下拉列表
         *
         * @return
         */
        @GetMapping(value = "bill-source/down/list")
        @ResponseBody
        public WebUiPage<Map<String, Object>> getBillSourceDataList() {
            List<Map<String, Object>> res = advancePaySummaryService.getBillSourceDataList();
            return new WebUiPage<>(res.size(), res);
        }

        /**
         * 导出增加款、减少款详情列表
         *
         * @param paySummaryDetailListVo
         * @return
         */
        @RequestMapping(value = "export")
        @ResponseBody
        public void export(AdvancePaySummaryDetailListVo paySummaryDetailListVo, HttpServletRequest request, HttpServletResponse response) {
            List<Map<String, Object>> res = advancePaySummaryService.findAdvancePaySummaryDetailListByParams(paySummaryDetailListVo);
            try {
                commonExportExcel(paySummaryDetailListVo.getFileName(), paySummaryDetailListVo.getWorkSheetName(), request, response, res);
            } catch (Exception e) {
                e.printStackTrace();
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
            fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            setBorderStyle(fontStyle);
            HSSFCellStyle borderStyle = wb.createCellStyle();
            setBorderStyle(borderStyle);
            // 创建一个sheet
            Sheet sheet = ee.getSheet(wb, workSheetName);
            // 第一行标题, 合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            Row titleRow = ee.getRow(sheet, 0, null, height);
            ee.getCell(titleRow, 0, fontStyle, fileName);
            // 创建等多的单元格，解决合并单元格的问题
            for (int i = 1; i < 6; i++) {
                ee.getCell(titleRow, i, fontStyle, "");
            }
            // 第三行标题、业务类型
            Row thirdTitleRow = ee.getRow(sheet, 1, null, height);
            String[] titleArray = {"院区", "渠道", "支付方流水号", "支付方式", "支付金额（元）", "支付时间", "HIS就诊时间"};
            for (int i = 0, len = titleArray.length; i < len; i++) {
                ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
            }

            // 只有合计的话就不导出了
            /*if (dataList.size() > 1) {*/
            Map<String, Object> colMap = null;
            CellStyle doubleStyle = wb.createCellStyle();
            DataFormat df = wb.createDataFormat();
            doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
            setBorderStyle(doubleStyle);
            for (int i = 0, len = dataList.size(); i < len; i++) {
                Row dataRow = ee.getRow(sheet, i + 2, null, height);
                colMap = dataList.get(i);
                // 院区
                Cell cell1 = dataRow.createCell(0);
                cell1.setCellStyle(borderStyle);

                String orgNoName = colMap.get("orgNoName") == null ? "" : colMap.get("orgNoName").toString();
                cell1.setCellValue(orgNoName);
                // 渠道
                Cell cell2 = dataRow.createCell(1);
                cell2.setCellStyle(doubleStyle);
                String billSourceName = formatterData(colMap.get("billSourceName")).equals("") ? "0" : formatterData(colMap.get("billSourceName"));
                cell2.setCellValue(billSourceName);
                // 支付方流水号
                Cell cell3 = dataRow.createCell(2);
                cell3.setCellStyle(doubleStyle);
                cell3.setCellValue(formatterData(colMap.get("payFlowNo")));
                // 支付方式
                Cell cell4 = dataRow.createCell(3);
                cell4.setCellStyle(doubleStyle);
                cell4.setCellValue(formatterData(colMap.get("payTypeName")));
                // 支付金额
                Cell cell5 = dataRow.createCell(4);
                cell5.setCellStyle(doubleStyle);
                cell5.setCellValue(formatterData(colMap.get("amount")));
                // 支付时间
                Cell cell6 = dataRow.createCell(5);
                cell6.setCellStyle(doubleStyle);
                cell6.setCellValue(formatterData(colMap.get("payTime")));
                // His就诊时间
                Cell cell7 = dataRow.createCell(6);
                cell7.setCellStyle(doubleStyle);
                cell7.setCellValue(formatterData(colMap.get("serverDate")));
            }
            /*}*/
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
