//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yiban.rec.web.reports;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.core.util.OprPageRequest;
import com.yiban.framework.dict.domain.MetaData;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.vo.TradeCheckFollowVo;
import com.yiban.rec.domain.vo.TradeCheckVo;
import com.yiban.rec.service.ElectronicRecService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.RecSummaryReportService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.EnumTypeOfInt;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.ExportMergedUtils;
import com.yiban.rec.util.PayTypeEnum;
import com.yiban.rec.util.CommonEnum.BillBalance;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

@Controller
@RequestMapping({"/admin/recSummaryReport"})
public class RecSummaryReportController extends CurrentUserContoller {
    @Value("${yiban.projectid}")
    private String orgCode;
    @Autowired
    private GatherService gatherService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private RecSummaryReportService recSummaryReportService;
    @Autowired
    private ElectronicRecService electronicRecService;

    public RecSummaryReportController() {
    }

    @RequestMapping({"/index"})
    public String index(ModelMap model) {
        List<MetaData> billSources = this.metaDataService.findMetaDataByDataTypeValue("bill_source");
        model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(this.metaDataService.NameAsList())));
        model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(this.gatherService.getOrgMap()));
        model.put("orgCode", this.orgCode);
        model.put("startDate", DateUtil.getSpecifiedDayBeforeDay(new Date(), 7));
        model.put("endDate", DateUtil.getSpecifiedDayBefore(new Date()));
        model.put("billSources", billSources);
        return "reconciliation/recSummaryReport";
    }

    @RequestMapping({"/summary"})
    @ResponseBody
    public WebUiPage<Map<String, Object>> summary(Map<String, String> vo) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var4) {
            var4.printStackTrace();
        }

        PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, new String[]{"id"}));
        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Page<Map<String, Object>> data = this.recSummaryReportService.summary(vo, pageable);
        return this.toWebUIPage(data);
    }

    @RequestMapping({"/summaryAmount"})
    @ResponseBody
    public List<Map<String, Object>> summaryAmount(Map<String, String> vo) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var6) {
            var6.printStackTrace();
        }

        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Map<String, String> asMap = ValueTexts.asMap(this.metaDataService.NameAsList());
        List<Map<String, Object>> summaryAmount = this.recSummaryReportService.summaryAmount(vo);
        Iterator var4 = summaryAmount.iterator();

        while(var4.hasNext()) {
            Map<String, Object> map = (Map)var4.next();
            map.put("tradeDate", StringUtils.isEmpty((String)asMap.get(map.get("tradeDate"))) ? map.get("tradeDate") : asMap.get(map.get("tradeDate")));
        }

        return summaryAmount;
    }

    @RequestMapping({"/exceptionSummary"})
    @ResponseBody
    public WebUiPage<Map<String, Object>> exceptionSummary(Map<String, String> vo) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var4) {
            var4.printStackTrace();
        }

        PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, new String[]{"id"}));
        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Page<Map<String, Object>> data = this.recSummaryReportService.exceptionSummary(vo, pageable);
        return this.toWebUIPage(data);
    }

    @RequestMapping({"/shortDetail"})
    @ResponseBody
    public WebUiPage<Map<String, Object>> shortDetail(Map<String, String> vo) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var4) {
            var4.printStackTrace();
        }

        PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, new String[]{"id"}));
        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Page<Map<String, Object>> data = this.recSummaryReportService.shortDetail(vo, pageable);
        return this.toWebUIPage(data);
    }

    @RequestMapping({"/shortSummary"})
    @ResponseBody
    public WebUiPage<Map<String, Object>> shortSummary(Map<String, String> vo) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var4) {
            var4.printStackTrace();
        }

        PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Direction.DESC, new String[]{"id"}));
        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Page<Map<String, Object>> data = this.recSummaryReportService.shortSummary(vo, pageable);
        return this.toWebUIPage(data);
    }

    @RequestMapping({"/exportData"})
    public void exportData(Map<String, String> vo, HttpServletRequest request, HttpServletResponse response) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var28) {
            var28.printStackTrace();
        }

        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Map<String, String> asMap = ValueTexts.asMap(this.metaDataService.NameAsList());
        PageRequest pageable = new PageRequest(0, 9999999);
        List<MetaData> billSources = this.metaDataService.findMetaDataByDataTypeValue("bill_source");
        String startDate = (String)vo.get("startDate");
        String endDate = (String)vo.get("endDate");

        try {
            this.setResponseAdRequest(request, response, startDate + "至 " + endDate + "对账数据汇总");
        } catch (UnsupportedEncodingException var27) {
            var27.printStackTrace();
        }

        ExportExcel ee = new ExportExcel();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle titleFontStyle = wb.createCellStyle();
        titleFontStyle.setAlignment((short)2);
        titleFontStyle.setVerticalAlignment((short)1);
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short)20);
        titleFontStyle.setFont(font);
        this.setBorderStyle(titleFontStyle);
        HSSFCellStyle fontStyle = wb.createCellStyle();
        fontStyle.setAlignment((short)2);
        fontStyle.setVerticalAlignment((short)1);
        this.setBorderStyle(fontStyle);
        HSSFCellStyle doubleStyle = wb.createCellStyle();
        HSSFDataFormat hSSFDataFormat = wb.createDataFormat();
        doubleStyle.setVerticalAlignment((short)1);
        doubleStyle.setDataFormat(hSSFDataFormat.getFormat("#,#0.00"));
        this.setBorderStyle(doubleStyle);
        Iterator var16 = billSources.iterator();

        while(var16.hasNext()) {
            MetaData meta = (MetaData)var16.next();
            vo.put("billSource", meta.getValue());
            Sheet summary = ee.getSheet(wb, meta.getName() + "汇总单");
            Page<Map<String, Object>> data = this.recSummaryReportService.summary(vo, pageable);
            List<Map<String, Object>> summaryAmount = this.recSummaryReportService.summaryAmount(vo);
            List<Map<String, Object>> summaryData = new ArrayList();
            List<Map<String, Object>> summaryContent = data.getContent();
            Iterator var23 = summaryContent.iterator();

            while(var23.hasNext()) {
                Map<String, Object> map = (Map)var23.next();
                map.put("billSource", asMap.get(map.get("billSource")));
                map.put("payType", asMap.get(map.get("payType")));
                summaryData.add(map);
            }

            summaryData.addAll(summaryAmount);

            try {
                this.exportSummartData(ee, titleFontStyle, fontStyle, doubleStyle, summaryData, summary, startDate + " 至 " + endDate + "昆明市延安医院" + meta.getName() + "汇总单");
            } catch (IOException var26) {
                var26.printStackTrace();
            }
        }

        try {
            ServletOutputStream servletOutputStream = response.getOutputStream();
            wb.write(servletOutputStream);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (IOException var25) {
            var25.printStackTrace();
        }

    }

    @RequestMapping({"/exportData2"})
    public void exportData2(Map<String, String> vo, HttpServletRequest request, HttpServletResponse response) {
        try {
            OprPageRequest oprPageRequest = this.URL2PageRequest();
            vo = oprPageRequest.getWhere();
        } catch (BusinessException var17) {
            var17.printStackTrace();
        }

        if (StringUtils.isBlank((String)vo.get("orgCode"))) {
            vo.put("orgCode", this.orgCode);
        }

        Map<String, String> asMap = ValueTexts.asMap(this.metaDataService.NameAsList());
        PageRequest pageable = new PageRequest(0, 200000);
        Page<Map<String, Object>> summaryData = this.recSummaryReportService.summary(vo, pageable);
        Page<Map<String, Object>> exceptionSummaryData = this.recSummaryReportService.exceptionSummary(vo, pageable);
        List<Map<String, Object>> summaryAmount = this.recSummaryReportService.summaryAmount(vo);
        Iterator var9 = summaryAmount.iterator();

        while(var9.hasNext()) {
            Map<String, Object> map = (Map)var9.next();
            map.put("tradeDate", StringUtils.isEmpty((String)asMap.get(map.get("tradeDate"))) ? map.get("tradeDate") : asMap.get(map.get("tradeDate")));
        }

        TradeCheckFollowVo unusualBillVo = new TradeCheckFollowVo();
        unusualBillVo.setOrgNo((String)vo.get("orgCode"));
        unusualBillVo.setStartDate((String)vo.get("startDate"));
        unusualBillVo.setEndDate((String)vo.get("endDate"));
        Page<TradeCheckFollow> exceptionData = this.electronicRecService.findDataByOrgNoAndTradeDate(unusualBillVo, pageable);
        List<Map<String, Object>> summaryContent = new ArrayList();
        List<Map<String, Object>> exceptionSummaryContent = exceptionSummaryData.getContent();
        List<TradeCheckFollow> exceptionContent = exceptionData.getContent();
        summaryContent.addAll(summaryData.getContent());
        summaryContent.addAll(summaryAmount);
        Iterator var14 = summaryContent.iterator();

        Map map;
        while(var14.hasNext()) {
            map = (Map)var14.next();
            map.put("billSource", asMap.get(map.get("billSource")));
            map.put("payType", asMap.get(map.get("payType")));
        }

        var14 = exceptionSummaryContent.iterator();

        while(var14.hasNext()) {
            map = (Map)var14.next();
            map.put("billSource", asMap.get(map.get("billSource")));
            map.put("payType", asMap.get(map.get("payType")));
        }

        try {
            this.exportData(summaryContent, exceptionSummaryContent, exceptionContent, vo, request, response);
        } catch (IOException var16) {
            var16.printStackTrace();
        }

    }

    private void exportData(List<Map<String, Object>> summaryData, List<Map<String, Object>> exceptionSummaryData, List<TradeCheckFollow> exceptionContent, Map<String, String> vo, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String startDate = (String)vo.get("startDate");
        String endDate = (String)vo.get("endDate");
        this.setResponseAdRequest(request, response, startDate + "-" + endDate + "对账数据汇总");
        ExportExcel ee = new ExportExcel();
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFCellStyle titleFontStyle = wb.createCellStyle();
        titleFontStyle.setAlignment((short)2);
        titleFontStyle.setVerticalAlignment((short)1);
        HSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short)20);
        titleFontStyle.setFont(font);
        this.setBorderStyle(titleFontStyle);
        HSSFCellStyle fontStyle = wb.createCellStyle();
        fontStyle.setAlignment((short)2);
        fontStyle.setVerticalAlignment((short)1);
        this.setBorderStyle(fontStyle);
        HSSFCellStyle doubleStyle = wb.createCellStyle();
        HSSFDataFormat hSSFDataFormat = wb.createDataFormat();
        doubleStyle.setVerticalAlignment((short)1);
        doubleStyle.setDataFormat(hSSFDataFormat.getFormat("#,#0.00"));
        this.setBorderStyle(doubleStyle);
        Sheet summary = ee.getSheet(wb, "医院汇总");
        Sheet exceptionSummary = ee.getSheet(wb, "异常账单汇总");
        Sheet exception = ee.getSheet(wb, "异常账单明细");
        this.exportSummartData(ee, titleFontStyle, fontStyle, doubleStyle, summaryData, summary, startDate + "-" + endDate + "昆明市延安医院汇总表");
        this.exportExceptionSummartData(ee, titleFontStyle, fontStyle, doubleStyle, exceptionSummaryData, exceptionSummary, startDate + "-" + endDate + "昆明市延安医院异常账单汇总");
        this.exportExceptionData(ee, titleFontStyle, fontStyle, doubleStyle, exceptionContent, exception, startDate + "-" + endDate + "昆明市延安医院异常账单明细");
        ServletOutputStream servletOutputStream = response.getOutputStream();
        wb.write(servletOutputStream);
        servletOutputStream.flush();
        servletOutputStream.close();
    }

    private void exportExceptionData(ExportExcel ee, HSSFCellStyle titleFontStyle, HSSFCellStyle fontStyle, HSSFCellStyle doubleStyle, List<TradeCheckFollow> dataList, Sheet sheet, String title) {
        List<MetaData> metaDataList = this.metaDataService.findMetaDataByDataTypeValue("bill_source");
        String[] titleArray = new String[]{"支付方流水号", "商户流水号", "HIS流水号", "患者ID", "患者姓名", "交易类型", "交易金额(元)", "交易时间", "支付渠道名称", "账单来源", "渠道名称", "状态", "备注"};
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length - 1));
        Row titleRow = ee.getRow(sheet, 0, (CellStyle)null, 28);
        ee.getCell(titleRow, 0, titleFontStyle, title);

        for(int i = 1; i < titleArray.length; ++i) {
            ee.getCell(titleRow, i, fontStyle, "");
        }

        Row thirdTitleRow = ee.getRow(sheet, 1, (CellStyle)null, 18);
        int j = 0;

        for(int len = titleArray.length; j < len; ++j) {
            ee.getCell(thirdTitleRow, j, fontStyle, titleArray[j]);
        }

        if (dataList.size() >= 1) {
            TradeCheckFollow colMap = null;
            Map<String, TradeCheckVo> patIdMap = this.electronicRecService.getPatIdMap(dataList);
            String hisCheck = BillBalance.HISDC.getValue() + "," + BillBalance.HEALTHCAREHIS.getValue();
            List<String> repeatList = new ArrayList();
            List<TradeCheckFollow> tmpHisList = new ArrayList();
            tmpHisList.addAll(dataList);

            int k;
            int n;
            for(k = 0; k < tmpHisList.size() - 1; ++k) {
                for(n = tmpHisList.size() - 1; n > k; --n) {
                    if (((TradeCheckFollow)tmpHisList.get(n)).getBusinessNo().equals(((TradeCheckFollow)tmpHisList.get(k)).getBusinessNo()) && ((TradeCheckFollow)tmpHisList.get(n)).getTradeAmount().equals(((TradeCheckFollow)tmpHisList.get(k)).getTradeAmount())) {
                        if ((hisCheck.contains(((TradeCheckFollow)tmpHisList.get(n)).getOriCheckState()) ? "短款" : "长款").equals("短款") && (hisCheck.contains(((TradeCheckFollow)tmpHisList.get(k)).getOriCheckState()) ? "短款" : "长款").equals("长款")) {
                            if (!"".equals(((TradeCheckFollow)tmpHisList.get(n)).getBusinessNo())) {
                                repeatList.add(((TradeCheckFollow)tmpHisList.get(n)).getBusinessNo());
                                tmpHisList.remove(n);
                            }
                        } else if ((hisCheck.contains(((TradeCheckFollow)tmpHisList.get(n)).getOriCheckState()) ? "短款" : "长款").equals("长款") && (hisCheck.contains(((TradeCheckFollow)tmpHisList.get(k)).getOriCheckState()) ? "短款" : "长款").equals("短款") && !"".equals(((TradeCheckFollow)tmpHisList.get(n)).getBusinessNo())) {
                            repeatList.add(((TradeCheckFollow)tmpHisList.get(n)).getBusinessNo());
                            tmpHisList.remove(n);
                        }
                    }
                }
            }

            k = 0;

            for(n = dataList.size(); k < n; ++k) {
                Row dataRow = ee.getRow(sheet, k + 2, (CellStyle)null, 18);
                colMap = (TradeCheckFollow)dataList.get(k);
                ee.getCell(dataRow, 0, fontStyle, colMap.getBusinessNo());
                Cell cell1 = dataRow.createCell(1);
                cell1.setCellStyle(fontStyle);
                cell1.setCellValue(colMap.getShopFlowNo());
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellStyle(fontStyle);
                cell2.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getHisFlowNo() : ((TradeCheckVo)patIdMap.get(colMap.getBusinessNo())).getHisFlowNo());
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellStyle(fontStyle);
                cell3.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? null : ((TradeCheckVo)patIdMap.get(colMap.getBusinessNo())).getPatId());
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellStyle(fontStyle);
                cell4.setCellValue(patIdMap.get(colMap.getBusinessNo()) == null ? colMap.getPatientName() : ((TradeCheckVo)patIdMap.get(colMap.getBusinessNo())).getCustName());
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellStyle(fontStyle);
                cell5.setCellValue(EnumTypeOfInt.getByCode(colMap.getTradeName()).getCode());
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellStyle(doubleStyle);
                cell6.setCellValue(this.getDouble(colMap.getTradeAmount()));
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellStyle(fontStyle);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cell7.setCellValue(f.format(colMap.getTradeTime()));
                Cell cell8 = dataRow.createCell(8);
                cell8.setCellStyle(fontStyle);
                cell8.setCellValue(PayTypeEnum.getByCode(colMap.getPayName()).getName());
                Cell cell9 = dataRow.createCell(9);
                cell9.setCellStyle(fontStyle);
                String billSource = this.getBillSource(EnumTypeOfInt.getByCode(colMap.getTradeName()).getCode(), hisCheck.contains(colMap.getOriCheckState()) ? "短款" : "长款", colMap.getBillSource());
                Iterator var33 = metaDataList.iterator();

                while(var33.hasNext()) {
                    MetaData metaData = (MetaData)var33.next();
                    if (metaData != null && metaData.getValue().equals(billSource)) {
                        billSource = metaData.getName();
                        break;
                    }
                }

                cell9.setCellValue(billSource);
                Cell cell10 = dataRow.createCell(10);
                cell10.setCellStyle(fontStyle);
                String channelName = colMap.getBillSource();
                Iterator var35 = metaDataList.iterator();

                while(var35.hasNext()) {
                    MetaData metaData = (MetaData)var35.next();
                    if (metaData != null && metaData.getValue().equals(channelName)) {
                        channelName = metaData.getName();
                        break;
                    }
                }

                cell10.setCellValue(channelName);
                Cell cell11 = dataRow.createCell(11);
                cell11.setCellStyle(fontStyle);
                cell11.setCellValue(colMap.getCheckStateValue());
                Cell cell12 = dataRow.createCell(12);
                cell12.setCellStyle(fontStyle);
                cell12.setCellValue(this.getRemark(colMap, repeatList));
            }
        }

        for(j = 0; j < titleArray.length; ++j) {
            if (j <= 2) {
                sheet.setColumnWidth(j, 8000);
            } else if (j <= 6) {
                sheet.setColumnWidth(j, 3000);
            } else if (j == 7) {
                sheet.setColumnWidth(j, 5000);
            } else if (j <= 11) {
                sheet.setColumnWidth(j, 3000);
            } else {
                sheet.setColumnWidth(j, 10000);
            }
        }

    }

    private String getBillSource(String tradeName, String oriCheckState, String channelName) {
        if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
            return channelName;
        } else if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
            return "HIS";
        } else if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
            return "HIS";
        } else {
            return tradeName.equals("缴费") && oriCheckState.equals("长款") ? channelName : null;
        }
    }

    private String getRemark(TradeCheckFollow tradeCheckFollow, List<String> repeatList) {
        String hisCheck = BillBalance.HISDC.getValue() + "," + BillBalance.HEALTHCAREHIS.getValue();
        String tradeName = EnumTypeOfInt.getByCode(tradeCheckFollow.getTradeName()).getCode();
        String oriCheckState = hisCheck.contains(tradeCheckFollow.getOriCheckState()) ? "短款" : "长款";
        String remark = null;
        if (tradeName.equals("退费") && oriCheckState.equals("短款")) {
            remark = "商户已退，his未退";
        }

        if (tradeName.equals("退费") && oriCheckState.equals("长款")) {
            remark = "商户未退，his已退";
        }

        if (tradeName.equals("缴费") && oriCheckState.equals("短款")) {
            remark = "商户未缴，his已缴";
        }

        if (tradeName.equals("缴费") && oriCheckState.equals("长款")) {
            remark = "商户已缴，his未缴";
        }

        return repeatList.contains(tradeCheckFollow.getBusinessNo()) && StringUtils.isNotBlank(tradeCheckFollow.getBusinessNo()) ? remark + ",已对冲" : remark;
    }

    private void exportExceptionSummartData(ExportExcel ee, HSSFCellStyle titleFontStyle, HSSFCellStyle fontStyle, HSSFCellStyle doubleStyle, List<Map<String, Object>> list, Sheet sheet, String title) {
        String[] secondTitle = new String[]{"类别", "日期", "HIS应收(元)", "渠道实收(元)", "差异金额(元)", "", "", "当天处理金额(元)", "", "", "历史待处理金额(元)", "", "", "当天处理历史金额(元)", "", ""};
        String[] titleArray = new String[]{"", "", "", "", "总计", "长款金额", "短款金额", "总计", "长款金额", "短款金额", "总计", "长款金额", "短款金额", "总计", "长款金额", "短款金额"};
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length - 1));
        Row titleRow = ee.getRow(sheet, 0, (CellStyle)null, 28);
        ee.getCell(titleRow, 0, titleFontStyle, title);

        for(int i = 1; i < titleArray.length; ++i) {
            ee.getCell(titleRow, i, fontStyle, "");
        }

        Row secondTitleRow = ee.getRow(sheet, 1, (CellStyle)null, 18);
        int j = 0;

        int k;
        for(k = secondTitle.length; j < k; ++j) {
            ee.getCell(secondTitleRow, j, fontStyle, secondTitle[j]);
        }

        Row thirdTitleRow = ee.getRow(sheet, 2, (CellStyle)null, 18);
        k = 0;

        for(int n = titleArray.length; k < n; ++k) {
            ee.getCell(thirdTitleRow, k, fontStyle, titleArray[k]);
        }

        for(k = 0; k < 4; ++k) {
            sheet.addMergedRegion(new CellRangeAddress(1, 2, k, k));
        }

        for(k = 4; k < secondTitle.length; k += 3) {
            sheet.addMergedRegion(new CellRangeAddress(1, 1, k, k + 2));
        }

        Map<String, Object> map = null;

        int m;
        for(m = 0; m < list.size(); ++m) {
            map = (Map)list.get(m);
            Row row = ee.getRow(sheet, m + 2, (CellStyle)null, 18);
            ee.getCell(row, 0, fontStyle, map.get("billSource").toString() + "-" + map.get("payType").toString());
            ee.getCell(row, 1, fontStyle, map.get("tradeDate").toString());
            ee.getCell(row, 2, doubleStyle, this.getDouble(map.get("hisPayAmount")).toString());
            ee.getCell(row, 3, doubleStyle, this.getDouble(map.get("thridPayAmount")).toString());
            ee.getCell(row, 4, doubleStyle, this.getDouble(map.get("allAmount")).toString());
            ee.getCell(row, 5, doubleStyle, this.getDouble(map.get("longAmount")).toString());
            ee.getCell(row, 6, doubleStyle, this.getDouble(map.get("shortAmount")).toString());
            ee.getCell(row, 7, doubleStyle, this.getDouble(map.get("todayAmount")).toString());
            ee.getCell(row, 8, doubleStyle, this.getDouble(map.get("todayLongAmount")).toString());
            ee.getCell(row, 9, doubleStyle, this.getDouble(map.get("todayShortAmount")).toString());
            ee.getCell(row, 10, doubleStyle, this.getDouble(map.get("historyUnHandleAmount")).toString());
            ee.getCell(row, 11, doubleStyle, this.getDouble(map.get("historyUnHandleLongAmount")).toString());
            ee.getCell(row, 12, doubleStyle, this.getDouble(map.get("historyUnHandleShortAmount")).toString());
            ee.getCell(row, 13, doubleStyle, this.getDouble(map.get("todayHistoryAmount")).toString());
            ee.getCell(row, 14, doubleStyle, this.getDouble(map.get("todayHistoryLongAmount")).toString());
            ee.getCell(row, 15, doubleStyle, this.getDouble(map.get("todayHistoryShortAmount")).toString());
        }

        for(m = 0; m < titleArray.length; ++m) {
            if (m <= 1) {
                sheet.setColumnWidth(m, 5000);
            } else {
                sheet.setColumnWidth(m, 3000);
            }
        }

    }

    private void exportSummartData(ExportExcel ee, HSSFCellStyle titleFontStyle, HSSFCellStyle fontStyle, HSSFCellStyle doubleStyle, List<Map<String, Object>> list, Sheet sheet, String title) throws IOException {
        String[] titleArray = new String[]{"日期", "HIS应收(元)", "渠道名称", "渠道实收(元)", "支付方式", "应收金额(元)", "实收金额(元)", "长款金额(元)", "短款金额(元)"};
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length - 1));
        Row titleRow = ee.getRow(sheet, 0, (CellStyle)null, 28);
        ee.getCell(titleRow, 0, titleFontStyle, title);

        for(int i = 1; i < titleArray.length; ++i) {
            ee.getCell(titleRow, i, fontStyle, "");
        }

        Map<String, Object> map = null;
        Row thirdTitleRow = ee.getRow(sheet, 1, (CellStyle)null, 18);
        int j = 0;

        for(int len = titleArray.length; j < len; ++j) {
            ee.getCell(thirdTitleRow, j, fontStyle, titleArray[j]);
        }

        Font redFont = sheet.getWorkbook().createFont();
        redFont.setColor((short)10);
        Font greenFont = sheet.getWorkbook().createFont();
        greenFont.setColor((short)50);
        Font blodFont = sheet.getWorkbook().createFont();
        blodFont.setBold(true);
        CellStyle shortStyle = sheet.getWorkbook().createCellStyle();
        DataFormat df = sheet.getWorkbook().createDataFormat();
        shortStyle.setVerticalAlignment((short)1);
        shortStyle.setDataFormat(df.getFormat("#,#0.00"));
        shortStyle.setFont(redFont);
        this.setBorderStyle(shortStyle);
        CellStyle longStyle = sheet.getWorkbook().createCellStyle();
        longStyle.setVerticalAlignment((short)1);
        longStyle.setDataFormat(df.getFormat("#,#0.00"));
        longStyle.setFont(greenFont);
        this.setBorderStyle(longStyle);
        CellStyle blodDoubleStyle = sheet.getWorkbook().createCellStyle();
        blodDoubleStyle.setVerticalAlignment((short)1);
        blodDoubleStyle.setDataFormat(df.getFormat("#,#0.00"));
        blodDoubleStyle.setFont(blodFont);
        this.setBorderStyle(blodDoubleStyle);
        CellStyle blodFontStyle = sheet.getWorkbook().createCellStyle();
        blodFontStyle.setVerticalAlignment((short)1);
        blodFontStyle.setAlignment((short)2);
        blodFontStyle.setFont(blodFont);
        this.setBorderStyle(blodFontStyle);

        int k;
        for(k = 0; k < list.size(); ++k) {
            map = (Map)list.get(k);
            Row row = ee.getRow(sheet, k + 2, (CellStyle)null, 18);
            if (k == list.size() - 1) {
                ee.getCell(row, 0, blodFontStyle, map.get("tradeDate").toString());
                ee.getCell(row, 1, blodDoubleStyle, this.getDouble(map.get("hisAmount")).toString());
                ee.getCell(row, 2, blodFontStyle, map.get("billSource") == null ? "" : map.get("billSource").toString());
                ee.getCell(row, 3, blodDoubleStyle, this.getDouble(map.get("thirdAmount")).toString());
                ee.getCell(row, 4, blodFontStyle, map.get("payType") == null ? "" : map.get("payType").toString());
                ee.getCell(row, 5, blodDoubleStyle, this.getDouble(map.get("hisPayAmount")).toString());
                ee.getCell(row, 6, blodDoubleStyle, this.getDouble(map.get("thridPayAmount")).toString());
                ee.getCell(row, 7, blodDoubleStyle, this.getDouble(map.get("longAmount")).toString());
                ee.getCell(row, 8, blodDoubleStyle, this.getDouble(map.get("shortAmount")).toString());
            } else {
                ee.getCell(row, 0, fontStyle, map.get("tradeDate").toString());
                ee.getCell(row, 1, doubleStyle, this.getDouble(map.get("hisAmount")).toString());
                ee.getCell(row, 2, fontStyle, map.get("billSource") == null ? "" : map.get("billSource").toString());
                ee.getCell(row, 3, doubleStyle, this.getDouble(map.get("thirdAmount")).toString());
                ee.getCell(row, 4, fontStyle, map.get("payType") == null ? "" : map.get("payType").toString());
                ee.getCell(row, 5, doubleStyle, this.getDouble(map.get("hisPayAmount")).toString());
                ee.getCell(row, 6, doubleStyle, this.getDouble(map.get("thridPayAmount")).toString());
                if (this.getDouble(map.get("longAmount")) != 0.0D && k != list.size() - 1) {
                    ee.getCell(row, 7, longStyle, this.getDouble(map.get("longAmount")).toString());
                } else {
                    ee.getCell(row, 7, doubleStyle, this.getDouble(map.get("longAmount")).toString());
                }

                if (this.getDouble(map.get("shortAmount")) != 0.0D && k != list.size() - 1) {
                    ee.getCell(row, 8, shortStyle, this.getDouble(map.get("shortAmount")).toString());
                } else {
                    ee.getCell(row, 8, doubleStyle, this.getDouble(map.get("shortAmount")).toString());
                }
            }
        }

        (new ExportMergedUtils()).addMergedRegion(sheet, new int[]{0, 1, 2, 3, 4});

        for(k = 0; k < titleArray.length; ++k) {
            sheet.setColumnWidth(k, 5000);
        }

    }

    public void setResponseAdRequest(HttpServletRequest request, HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        request.setCharacterEncoding("utf-8");
        response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(), "iso-8859-1") + ".xls");
    }

    public void setBorderStyle(CellStyle cellStyle) {
        cellStyle.setBorderBottom((short)1);
        cellStyle.setBorderLeft((short)1);
        cellStyle.setBorderRight((short)1);
        cellStyle.setBorderTop((short)1);
    }

    public Double getDouble(Object str) {
        String doubleVal = this.getNotNullStr(str);
        return Double.parseDouble(doubleVal);
    }

    public String getNotNullStr(Object str) {
        return str == null ? "0" : ("".equals(String.valueOf(str).trim()) ? "0" : String.valueOf(str).trim());
    }
}
