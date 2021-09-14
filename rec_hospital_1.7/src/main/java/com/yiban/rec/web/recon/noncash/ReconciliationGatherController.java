package com.yiban.rec.web.recon.noncash;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.util.DateUtil;
import com.yiban.rec.util.ExportExcel;
import com.yiban.rec.util.StringUtil;

/**
 * 
 * <p>
 * 文件名称:ReconciliationController.java
 * <p>
 * <p>
 * 文件描述:本类描述
 * <p>
 * 版权所有:深圳市依伴数字科技有限公司版权所有(C)2017
 * </p>
 * <p>
 * 内容摘要:对账管理--->对账汇总查询
 * </p>
 * <p>
 * 其他说明:其它内容的说明
 * </p>
 * <p>
 * 完成日期:2017年3月21日下午2:40:16
 * </p>
 * <p>
 * 
 * @author fangzuxing
 */
@Controller
@RequestMapping("/admin/reconciliation/gather")
public class ReconciliationGatherController extends CurrentUserContoller {

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private MetaDataService metaDataService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;

	@RequestMapping("")
	public String index(ModelMap model) {
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
		model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		return autoView("reconciliation/gather");
	}

	/**
	 * @date：2017年3月21日
	 * @Description：查询对账汇总信息 
	 * @param orgcode 机构编码 
	 * @param payChannelId 渠道id 
	 * @param recDate 对账日期 
	 * @return: 返回结果描述 
	 * @return ResponseResult: 返回值类型 
	 * @throws
	 */
	@Logable(operation = "获取汇总账单")
	@RequestMapping(value = "/collect") 
	@ResponseBody
	public ResponseResult recGatherQuery(@RequestParam(value = "orgNo", required = false) String orgNo, @RequestParam(value = "payDate", required = false) String payDate) {
		ResponseResult rs = ResponseResult.success();
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//		hConfig.setOrgCode(orgNo);
		if(hConfig == null || hConfig.getRecType() == null || hConfig.getRecType().length() <= 0){
			return ResponseResult.failure("请配置需要对账类型！");
		}
		Map<String,Object> platMap = gatherService.getGatherData(orgNo, payDate,hConfig);
		rs.data(platMap);
		return rs;
	}
	
	
	/**
	* @throws IOException 
	* @date：2017年4月14日 
	* @Description：每日对账汇总导出至exl
	* @param orgNo
	* @param payDate
	* @return: 返回结果描述
	* @throws
	 */ 
	@Logable(operation = "导出对账明细")
	@RequestMapping(value = "/api/dcExcel", method = RequestMethod.GET)
	public ResponseResult recGatherExport(@RequestParam( value = "orgNo", required = false) String orgNo,@RequestParam("payDate") String payDate,@RequestParam( value = "orgName", required = false ) String orgName,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
//		hConfig.setOrgCode(orgNo);
		if(hConfig == null || hConfig.getRecType() == null || hConfig.getRecType().length() <= 0){
			return ResponseResult.failure("请配置需要对账类型！");
		}
		Map<String,Object> platMap = gatherService.getGatherData(orgNo, payDate,hConfig);
		response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/octet-stream;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		String fileName = payDate+orgName+"对账汇总查询";
		response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
		
		
		ExportExcel ee = new ExportExcel();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = ee.getSheet(wb, payDate+orgName+"对账汇总查询");
		
		//设置列的背景颜色
		CellStyle cellStyleContent_BG = wb.createCellStyle();
		cellStyleContent_BG.setFillForegroundColor(HSSFColor.LIME.index);		
		cellStyleContent_BG.setFillPattern(CellStyle.SOLID_FOREGROUND);	
		cellStyleContent_BG.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyleContent_BG.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyleContent_BG.setBorderBottom((short)1);
		cellStyleContent_BG.setBorderLeft((short)1);
		cellStyleContent_BG.setBorderRight((short)1);
		cellStyleContent_BG.setBorderTop((short)1);
		cellStyleContent_BG.setFont(ee.getFont(wb, "Courier New", (short)12, false, false));
		
		//标题样式
		CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)14, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
		Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
		
		ee.getCell(titleRow, 0, cellStyle, orgName+"对账汇总查询"+payDate);
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));
		
		//头部样式
		CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		Row headRow = ee.getRow(sheet, 1, null, 20);
		
		ee.getCell(headRow, 0, cellStyleHead, "数据来源");
		ee.getCell(headRow, 1, cellStyleHead, "支付笔数");
		ee.getCell(headRow, 2, cellStyleHead, "支付金额(单位：元)");
		ee.getCell(headRow, 3, cellStyleHead, "退费笔数");
		ee.getCell(headRow, 4, cellStyleHead, "退费金额(单位：元)");
		ee.getCell(headRow, 5, cellStyleHead, "净收笔数");
		ee.getCell(headRow, 6, cellStyleHead, "净收金额(单位：元)");

		CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> gatherList =  (List<Map<String, Object>>) platMap.get("gatherList");
		Map<String,String> metaMap = ValueTexts.asMap(metaDataService.valueAsList());
		if(!StringUtil.isNullOrEmpty(gatherList)){
			for(int i=0;i<gatherList.size();i++){
				 Map<String,Object> map = gatherList.get(i);
				 Row platRow = ee.getRow(sheet, i+2, null, 20);
				 
				 String paySum = String.valueOf(map.get("paySum")==null?"0":map.get("paySum"));
				 String refundSum = String.valueOf(map.get("refundSum")==null?"0":map.get("refundSum"));
				 String payAmount = String.valueOf(map.get("payAmount")==null?"0":map.get("payAmount"));
				 String refundAmount = String.valueOf(map.get("refundAmount")==null?"0":map.get("refundAmount"));
				 String receiptSum = String.valueOf(Integer.parseInt(paySum) - Integer.parseInt(refundSum));
				 String receiptAmount = String.valueOf(Double.parseDouble(payAmount) - Double.parseDouble(refundAmount));
				 String dataSource = String.valueOf(map.get("dataSource"));
				 String dataSourceValue = metaMap.get(dataSource);
				 String isParent =  String.valueOf(map.get("parent")==null?"false":map.get("parent"));
				 
				 if(null == dataSourceValue && dataSource.length() >4 ){
					 dataSourceValue = metaMap.get(dataSource.substring(0,4)) + " - " + metaMap.get(dataSource.substring(4));
				 }
				 if("root".equals(isParent)){
					 ee.getCell(platRow, 0, cellStyleContent_BG, dataSourceValue);
					 ee.getCell(platRow, 1, cellStyleContent_BG, paySum);
					 ee.getCell(platRow, 2, cellStyleContent_BG, payAmount);
					 ee.getCell(platRow, 3, cellStyleContent_BG, refundSum);
					 ee.getCell(platRow, 4, cellStyleContent_BG, refundAmount);
					 ee.getCell(platRow, 5, cellStyleContent_BG, receiptSum);
					 ee.getCell(platRow, 6, cellStyleContent_BG, receiptAmount);
				 }else{
					 ee.getCell(platRow, 0, cellStyleContent, dataSourceValue);
					 ee.getCell(platRow, 1, cellStyleContent, paySum);
					 ee.getCell(platRow, 2, cellStyleContent, payAmount);
					 ee.getCell(platRow, 3, cellStyleContent, refundSum);
					 ee.getCell(platRow, 4, cellStyleContent, refundAmount);
					 ee.getCell(platRow, 5, cellStyleContent, receiptSum);
					 ee.getCell(platRow, 6, cellStyleContent, receiptAmount);
				 }
			}
		}
		
		//单边账
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> singleList = (List<Map<String, Object>>) platMap.get("singleList");
		Row brRow2 = ee.getRow(sheet, gatherList.size() + 3, null, 35);
		ee.getCell(brRow2, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)14, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "单边账");
		sheet.addMergedRegion(new CellRangeAddress(gatherList.size() + 3,gatherList.size() + 3,0,1));
		String count = "",platformAmount = "";
		if(!StringUtil.isNullOrEmpty(singleList)){
			for(Map<String,Object> map : singleList){
				count = String.valueOf(map.get("count"));
				platformAmount = StringUtil.isNullOrEmpty(map.get("platformAmount"))?"0":String.valueOf(map.get("platformAmount"));
			}
		}
		Row hjRow2 = ee.getRow(sheet, gatherList.size() + 4, null, 20);
		ee.getCell(hjRow2, 0, cellStyleHead, "笔数");
		ee.getCell(hjRow2, 1, cellStyleHead, "金额");

		Row hjMisRow2 = ee.getRow(sheet, gatherList.size() + 5, null, 20);
		ee.getCell(hjMisRow2, 0, cellStyleHead, count);
		ee.getCell(hjMisRow2, 1, cellStyleHead, platformAmount);
		
//		//合计行
//		Row brRow = ee.getRow(sheet, gatherList.size() + 7, null, 35);
//		ee.getCell(brRow, 0, ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)14, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0), "合计");
//		sheet.addMergedRegion(new CellRangeAddress(gatherList.size() + 7,gatherList.size() + 7,0,4));
//		
//		Row hjRow = ee.getRow(sheet, gatherList.size() + 8, null, 20);
//		ee.getCell(hjRow, 0, cellStyleHead, "数据来源");
//		ee.getCell(hjRow, 1, cellStyleHead, "实收笔数");
//		ee.getCell(hjRow, 2, cellStyleHead, "实收金额(单位:元)");
//		ee.getCell(hjRow, 3, cellStyleHead, "净收笔数");
//		ee.getCell(hjRow, 4, cellStyleHead, "净收金额(单位:元)");
//		
//		List<Map<String, Object>> acountList = new ArrayList<Map<String, Object>>();
//		if(gatherList != null && gatherList.size()>0){
//			for(int i=0;i<gatherList.size();i++){
//				Map<String, Object>  obj = gatherList.get(i);
//				if(obj.get("parent")=="root"){
//					acountList.add(obj);
//				}
//			}
//		}
//		
//		if(!StringUtil.isNullOrEmpty(acountList)){
//			for(int i=0;i<acountList.size();i++){
//				 Map<String,Object> map = acountList.get(i);
//				 Row hjMisRow = ee.getRow(sheet, i+(gatherList.size() + 9), null, 20);
//					ee.getCell(hjMisRow, 0, cellStyleHead, String.valueOf(metaMap.get(map.get("dataSource"))));
//					ee.getCell(hjMisRow, 1, cellStyleHead, String.valueOf(map.get("paySum")==null?"0":map.get("paySum")));
//					ee.getCell(hjMisRow, 2, cellStyleHead, String.valueOf(map.get("payAmount")==null?"0":map.get("payAmount")));
//					BigInteger paySum = (BigInteger)map.get("paySum");
//					ee.getCell(hjMisRow, 3, cellStyleHead, String.valueOf(paySum.subtract((BigInteger)map.get("refundSum"))));
//					BigDecimal payAmount = (BigDecimal)map.get("payAmount");
//					ee.getCell(hjMisRow, 4, cellStyleHead, String.valueOf(payAmount.subtract((BigDecimal)map.get("refundAmount")).setScale(3, BigDecimal.ROUND_HALF_UP)));
//			}
//		}
//
//		sheet.setColumnWidth(0, "数据来源".getBytes().length * 2 * 256);// 调整第一列宽度
//		sheet.setColumnWidth(1, "支付笔数".getBytes().length * 2 * 256);// 调整第一列宽度
//		sheet.setColumnWidth(2, "支付金额(单位：元)".getBytes().length * 2 * 128);// 调整第一列宽度
//		sheet.setColumnWidth(3, "退费笔数".getBytes().length * 2 * 256);// 调整第一列宽度
//		sheet.setColumnWidth(4, "退费金额(单位：元)".getBytes().length * 2 * 128);// 调整第一列宽度
		
		sheet.setColumnWidth(0, 9500);
		sheet.setColumnWidth(1, 8000);
		sheet.setColumnWidth(2, 6500);
		sheet.setColumnWidth(3, 6500);
		sheet.setColumnWidth(4, 6500);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 5000);
		sheet.setColumnWidth(7, 7000);
		
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
		return null;
	}
	
}
