package com.yiban.rec.web.reports;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.yiban.rec.pizhouservice.PiZhouBaoBiaoService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.ReportSummaryService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

@Controller
@RequestMapping("")
public class PiZhouController {
	
	@Autowired
    private GatherService gatherService;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private PropertiesConfigService propertiesConfigService;
    @Autowired
    private PiZhouBaoBiaoService piZhouBaoBiaoService;

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
    
    @Controller
    @RequestMapping("admin/pizhoubaobiao")
    public class PiZhouBaoBiaoController {
    	/**
         * 进入邳州报表汇总页面
         */
        @RequestMapping(value = "/index")
        public String index(ModelMap model) {
            String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
            model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
            model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
            model.put("orgCode", orgCode);
            model.put("beginTime", DateUtil.getSpecifiedDayBefore(new Date()));
            model.put("endTime", DateUtil.getSpecifiedDayBefore(new Date()));
            return "reports/pizhou/pizhou";
        }
        
        @RequestMapping(value = "/data")
        @ResponseBody
        public WebUiPage<Map<String, Object>> data(ReportSummaryService.SummaryQuery query,String selectType) {
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
            
            formatDate(query);
            
//            query.setColumnSql(" Cashier ");
            List<Map<String, Object>> res = piZhouBaoBiaoService.findPiZhouBaoBiao(query,selectType);
            return new WebUiPage<>(res.size(), res);
        }
        
        @RequestMapping(value="/data/summary")
        @ResponseBody
        public List<Map<String,Object>> dataSummary(ReportSummaryService.SummaryQuery query,String selectType) {
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
             
             formatDate(query);
             
             List<Map<String,Object>> object=piZhouBaoBiaoService.findPiZhouBaoBiaoSummary(query, selectType);
             return object;
        }
        
        @RequestMapping(value = "/dcExcel")
		public void exportData(ReportSummaryService.SummaryQuery query, String fileName,String selectType, String workSheetName,
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
			
			List<Map<String, Object>> res = piZhouBaoBiaoService.findPiZhouBaoBiao(query,selectType);
			formatDate(query);
			
			commonExportExcel(fileName, workSheetName, request, response, res);
			
		}
        
        public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
				HttpServletResponse response, List<Map<String, Object>> dataList) throws Exception{
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
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
			Row titleRow = ee.getRow(sheet, 0, null, height);
			ee.getCell(titleRow, 0, fontStyle, fileName);
			// 创建等多的单元格，解决合并单元格的问题
			for (int i = 1; i < 10; i++) {
				ee.getCell(titleRow, i, fontStyle, "");
			}
			// 第二行业务类型
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 8));
			Row secondTitleRow = ee.getRow(sheet, 1, null, height);
			ee.getCell(secondTitleRow, 0, fontStyle, "");
			ee.getCell(secondTitleRow, 1, fontStyle, "门诊业务");
			ee.getCell(secondTitleRow, 7, fontStyle, "住院业务");
			
			// 第三行业务类型
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 2));
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, 4));
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 8));
			Row thridTitleRow = ee.getRow(sheet, 2, null, height);
			ee.getCell(thridTitleRow, 0, fontStyle, "");
			ee.getCell(thridTitleRow, 1, fontStyle, "挂号");
			ee.getCell(thridTitleRow, 3, fontStyle, "缴费");
			ee.getCell(thridTitleRow, 5, fontStyle, "门诊充值");
			ee.getCell(thridTitleRow, 7, fontStyle, "住院预交金");
			
			// 第四行标题、业务类型
			Row fourTitleRow = ee.getRow(sheet, 3, null, height);
			String[] titleArray = { "渠道", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)", "笔数", "金额(元)"};
			for (int i = 0, len = titleArray.length; i < len; i++) {
				ee.getCell(fourTitleRow, i, fontStyle, titleArray[i]);
			}
			if(dataList.size()>1) {
				Map<String, Object> colMap = null;
				CellStyle doubleStyle = wb.createCellStyle();
				DataFormat df = wb.createDataFormat();
				doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
				setBorderStyle(doubleStyle);
				for(int i=0,len= dataList.size();i < len; i++) {
					Row dataRow = ee.getRow(sheet, i + 4, null, height);
					colMap = dataList.get(i);
					ee.getCell(dataRow, 0, borderStyle,
							colMap.get("ly") != null ? colMap.get("ly").toString() : "");
					Cell cell1 = dataRow.createCell(1);
					cell1.setCellStyle(borderStyle);
					cell1.setCellValue(Integer.parseInt(colMap.get("registrationNum").toString()));
					Cell cell2 = dataRow.createCell(2);
					cell2.setCellStyle(doubleStyle);
					cell2.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("registrationAmount"))));

					Cell cell3 = dataRow.createCell(3);
					cell3.setCellStyle(borderStyle);
					cell3.setCellValue(Integer.parseInt(colMap.get("payNum").toString()));
					Cell cell4 = dataRow.createCell(4);
					cell4.setCellStyle(doubleStyle);
					cell4.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("payAmount"))));

					Cell cell5 = dataRow.createCell(5);
					cell5.setCellStyle(borderStyle);
					cell5.setCellValue(Integer.parseInt(colMap.get("outpatientRechargeNum").toString()));
					Cell cell6 = dataRow.createCell(6);
					cell6.setCellStyle(doubleStyle);
					cell6.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("outpatientRechargeAmount"))));

					Cell cell7 = dataRow.createCell(7);
					cell7.setCellStyle(borderStyle);
					cell7.setCellValue(Integer.parseInt(colMap.get("zyyjjNum").toString()));
					Cell cell8 = dataRow.createCell(8);
					cell8.setCellStyle(doubleStyle);
					cell8.setCellValue(Double.parseDouble(getNotNullStr(colMap.get("zyyjjAmount"))));
				}
			}
			
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
			
        }
        
    }
    
    public String getNotNullStr(Object str) {
		return (str == null ? "0" : String.valueOf(str).trim());
	}
    
    
    public static void formatDate(ReportSummaryService.SummaryQuery query) {
		String beginTime = query.getBeginTime();
		String endTime = query.getEndTime();

		// 按日统计，要转义
		String format = "%%Y-%%m-%%d";
		try {
			// 按月统计
			if ("months".equals(query.getCollectType())) {
				format = "%%Y-%%m";

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				beginTime = sdf.format(sdf.parse(beginTime)) + "-01";
				endTime = sdf.format(sdf.parse(endTime)) + "-31";

				// 按年统计
			} else if ("years".equals(query.getCollectType())) {
				format = "%%Y";

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				beginTime = sdf.format(sdf.parse(beginTime)) + "-01-01";
				endTime = sdf.format(sdf.parse(endTime)) + "-12-31";
			}
		} catch (ParseException e) {
			//logger.error("formatDate exception：" + e);
		}
		query.setBeginTime(beginTime);
		query.setEndTime(endTime);
		query.setColumnSql(" DATE_FORMAT(trade_date, '" + format + "') ");
	}
    
    

}
