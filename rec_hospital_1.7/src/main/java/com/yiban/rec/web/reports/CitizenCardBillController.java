package com.yiban.rec.web.reports;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.CitizenCardBill;
import com.yiban.rec.domain.vo.CitizenCardBillVo;
import com.yiban.rec.service.CitizenCardBillService;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一账通账单
 * @author admin
 *
 */
@Controller
@RequestMapping("/admin/citizenCardBill")
public class CitizenCardBillController extends CurrentUserContoller {

	@Autowired
    private GatherService gatherService;
	@Autowired
    private MetaDataService metaDataService;
	@Autowired
	private CitizenCardBillService citizenCardBillService;
	
	
	private final int height = 18;
	/**
     * 菜单页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/index")
    public String index(ModelMap model) {
        model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.NameAsList())));
        model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
        model.put("startDate", DateUtil.getSpecifiedDayBefore(new Date()));
        model.put("endDate", DateUtil.getSpecifiedDayBefore(new Date()));
        return "reconciliation/citizenCardBill";
    }
    /**
     * 一账通账单查询
     * @param citizenCardBillVo
     * @return
     */
    @RequestMapping(value = "/data")
    @ResponseBody
    public WebUiPage<CitizenCardBill> data(CitizenCardBillVo citizenCardBillVo) {
        PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Sort.Direction.DESC, "id"));
        Page<CitizenCardBill> data = citizenCardBillService.findPage(citizenCardBillVo, pageable);
        return this.toWebUIPage(data);
    }
    /**
     * 一账通账单汇总
     * @param citizenCardBillVo
     * @return
     */
    @RequestMapping(value = "/summary")
    @ResponseBody
    public List<Map<String, Object>> summary(CitizenCardBillVo citizenCardBillVo){
    	String[] orderStates = new String[]{"10011","10021","30011","10031"};
		List<Map<String, Object>> data = citizenCardBillService.summary(citizenCardBillVo);
		int flag = 0;
		for (String status : orderStates) {
			flag=0;
			for (Map<String, Object> map : data) {
				if(status.equals(map.get("orderState"))){
					flag=1;
					break;
				}
			}
			if(flag==0){
				Map<String, Object> _map = new HashMap<>();
				_map.put("orderState", status);
				_map.put("count", "0");
				_map.put("money", "0");
				data.add(_map);
			}
		}
		return data;
    }
    
    /**
     * 导出一账通账单数据
     * @param citizenCardBillVo
     */
    @RequestMapping(value = "/exportData")
    @ResponseBody
    public void exportData(CitizenCardBillVo citizenCardBillVo, HttpServletRequest request, HttpServletResponse response){
    	String[] orderStates = new String[]{"10011","10021","30011","10031"};
		List<Map<String, Object>> data = citizenCardBillService.summary(citizenCardBillVo);
		int flag = 0;
		for (String status : orderStates) {
			flag=0;
			for (Map<String, Object> map : data) {
				if(status.equals(map.get("orderState"))){
					flag=1;
					break;
				}
			}
			if(flag==0){
				Map<String, Object> _map = new HashMap<>();
				_map.put("orderState", status);
				_map.put("count", "0");
				_map.put("money", "0");
				data.add(_map);
			}
		}
		
		
		PageRequest pageable = this.getRequestPageabledWithInitSort(new Sort(Sort.Direction.DESC, "id"));
        Page<CitizenCardBill> list = citizenCardBillService.findPage(citizenCardBillVo, pageable);
        
        String fileName = citizenCardBillVo.getStartDate()+"-"+citizenCardBillVo.getEndDate()+"市民卡账单";
        String sheetName = "市民卡账单";
        try {
			commonExportExcel(fileName,sheetName,request,response,list.getContent(),data);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void commonExportExcel(String fileName, String workSheetName, HttpServletRequest request,
                                  HttpServletResponse response, List<CitizenCardBill> dataList, List<Map<String, Object>> summary) throws Exception{
    	setResponseAdRequest(request, response, fileName);
		ExportExcel ee = new ExportExcel();
		// workbook对应一个Excel
		HSSFWorkbook wb = new HSSFWorkbook();
		
		// 定义一个统一字体样式:、居中,边框
		HSSFCellStyle fontStyle = wb.createCellStyle();
		fontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		setBorderStyle(fontStyle);
		
		HSSFCellStyle titleFontStyle = wb.createCellStyle();
		titleFontStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		titleFontStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成一个字体
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 20);
		titleFontStyle.setFont(font);
		// 把字体 应用到当前样式
		setBorderStyle(titleFontStyle);
		
		HSSFCellStyle borderStyle = wb.createCellStyle();
		setBorderStyle(borderStyle);
		
		CellStyle doubleStyle = wb.createCellStyle();
		DataFormat df = wb.createDataFormat();
		doubleStyle.setDataFormat(df.getFormat("#,#0.00"));
		setBorderStyle(doubleStyle);
		
		String[] titleArray = new String[]{"平台流水号","终端流水号","终端号","卡号","交易类型","交易金额(元)","交易时间"};
		String[] subTitle = new String[]{"交易类型","总金额","笔数"};
		// 创建一个sheet
		Sheet sheet = ee.getSheet(wb, workSheetName);
		// 第一行标题, 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleArray.length-1));
		Row titleRow = ee.getRow(sheet, 0, null, 25);
		ee.getCell(titleRow, 0, titleFontStyle, fileName);
		// 创建等多的单元格，解决合并单元格的问题
		for (int i = 1; i < titleArray.length; i++) {
			ee.getCell(titleRow, i, fontStyle, "");
		}
		
		Map<String, Object> map = null;
		Row subTitleRow = ee.getRow(sheet, 2, null, height);
		for (int i = 0, len = subTitle.length; i < len; i++) {
			ee.getCell(subTitleRow, i, fontStyle, subTitle[i]);
		}
		
		for(int i=0;i<summary.size();i++){
			Row dataRow = ee.getRow(sheet, i + 3, null, height);
			map = summary.get(i);
			ee.getCell(dataRow, 0, borderStyle, getOrderStateName(map.get("orderState").toString()));
			ee.getCell(dataRow, 1, doubleStyle, map.get("money").toString());
			ee.getCell(dataRow, 2, borderStyle, map.get("count").toString());
		}
		
		// 第三行标题、业务类型
		Row thirdTitleRow = ee.getRow(sheet, summary.size()+4, null, height);
		for (int i = 0, len = titleArray.length; i < len; i++) {
			ee.getCell(thirdTitleRow, i, fontStyle, titleArray[i]);
		}
		
		
		if (dataList.size() >= 1) {
			CitizenCardBill colMap = null;
			
			for (int i = 0, len = dataList.size(); i < len; i++) {
				Row dataRow = ee.getRow(sheet, i + summary.size()+5, null, height);
				colMap = dataList.get(i);
				// 平台流水号
				ee.getCell(dataRow, 0, borderStyle, colMap.getOrderNo());
				// 终端流水号
				ee.getCell(dataRow, 1, borderStyle, colMap.getPayFlowNo());
				// 终端号
				ee.getCell(dataRow, 2, borderStyle, colMap.getPayTermNo());
				// 卡号
				ee.getCell(dataRow, 3, borderStyle, colMap.getPayAccount());
				// 交易类型
				ee.getCell(dataRow, 4, borderStyle, colMap.getOrderStateName());
				// 交易金额
				Cell cell5 = dataRow.createCell(5);
				cell5.setCellStyle(doubleStyle);
				cell5.setCellValue(Double.parseDouble(getNotNullStr(colMap.getPayAmount())));
				
				// 交易时间
				Cell cell6 = dataRow.createCell(6);
				cell6.setCellStyle(doubleStyle);
				SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				cell6.setCellValue(f.format(colMap.getTradeDatatime()));
			}
			
			for (int i=0;i<titleArray.length;i++) {
				sheet.autoSizeColumn((short)i,true); //调整列宽度自适应
			}
			
		}
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
    }
    
    private String getOrderStateName(String orderState){
    	if(orderState!=null){
			if("10011".equals(orderState)){
				return "充值";
			}else if("10021".equals(orderState)){
				return "消费";
			}else if("10031".equals(orderState)){
				return "取现";
			}else if("30011".equals(orderState)){
				return "退款";
			}else if("30031".equals(orderState)){
				return "充值撤销";
			}
		}
		return orderState;
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
    public String getNotNullStr(Object str) {
        return (str == null ? "0" : String.valueOf(str).trim());
    }
}
