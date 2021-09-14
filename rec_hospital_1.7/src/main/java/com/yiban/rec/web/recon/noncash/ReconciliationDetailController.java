package com.yiban.rec.web.recon.noncash;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springside.modules.mapper.JsonMapper;

import com.yiban.framework.account.common.ProConstants;
import com.yiban.framework.account.controller.CurrentUserContoller;
import com.yiban.framework.account.domain.User;
import com.yiban.framework.account.service.PropertiesConfigService;
import com.yiban.framework.core.annotation.Logable;
import com.yiban.framework.core.domain.BusinessException;
import com.yiban.framework.core.domain.ResponseResult;
import com.yiban.framework.core.domain.WebUiPage;
import com.yiban.framework.core.domain.base.ValueTexts;
import com.yiban.framework.dict.service.MetaDataService;
import com.yiban.framework.export.view.excel.ExcelDecoratedEntry;
import com.yiban.framework.export.view.excel.ExcelResult;
import com.yiban.rec.domain.Reconciliation;
import com.yiban.rec.domain.TradeCheckFollow;
import com.yiban.rec.domain.baseinfo.ShopInfo;
import com.yiban.rec.domain.vo.AppRuntimeConfig;
import com.yiban.rec.domain.vo.RecQueryVo;
import com.yiban.rec.service.GatherService;
import com.yiban.rec.service.HospitalConfigService;
import com.yiban.rec.service.ReconciliationService;
import com.yiban.rec.service.ShopInfoService;
import com.yiban.rec.util.CommonConstant;
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
 * 内容摘要:对账管理--->对账明细查询
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
@RequestMapping("/admin/reconciliation/detail")
public class ReconciliationDetailController extends CurrentUserContoller {

	@Autowired
	private ReconciliationService reconciliationService;
	
	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private GatherService gatherService;
	
	@Autowired
	private ShopInfoService shopInfoService;
	@Autowired
	private PropertiesConfigService propertiesConfigService;
	
	@Autowired
	private HospitalConfigService hospitalConfigService;

	@RequestMapping("")
	public String index(ModelMap model,HttpServletRequest request,String orgNo,String payDate) {
		String orgCode = propertiesConfigService.findValueByPkey(ProConstants.yibanProjectid);
		//判断是否从其他页面点击链接进入，默认为双方对账，非现金对账
		int fag = 0;
		if(orgNo != null || payDate != null) {
			fag = 1;
			model.put("accountDate", payDate);
			model.put("accountOrgNo", orgNo);
		}else {
			model.put("accountDate", DateUtil.getSpecifiedDayBefore(new Date()));
		}
		//根据机构属性配置，判断该机构为三方对账/两方对账，现金对账/非现金对账
		String recType = getPayType();
		model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.getNameValueAsList())));
		model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
		model.put("deviceNo", CommonConstant.DEVICE_NO);
		model.put("orgNo", orgCode);
		model.put("flag",fag);
		model.put("recType", recType);
		
		return autoView("reconciliation/detail");
	}
	
	@RequestMapping("/check")
	public String indexDetail(@RequestParam("orgNo") Long orgNo,@RequestParam("payDate") String payDate,
			@RequestParam("isDifferent") Integer isDifferent,ModelMap model) {
			model.put("typesJSON", JsonMapper.nonEmptyMapper().toJson(ValueTexts.asMap(metaDataService.valueAsList())));
			model.put("orgJSON", JsonMapper.nonEmptyMapper().toJson(gatherService.getOrgMap()));
			model.put("accountOrgNo", orgNo);
			model.put("accountDate", payDate);
			model.put("isDifferent", isDifferent);
			return autoView("reconciliation/detail");
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<Reconciliation> recDetailQuery(RecQueryVo rqvo) {
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) {
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!StringUtil.isNullOrEmpty(rqvo.getBankTypeId())){
			List<ShopInfo> list = shopInfoService.getAllShopInfo();
			for(ShopInfo shopInfo : list){
				if(shopInfo.getMetaDataBankId() != null && rqvo.getBankTypeId().equals(shopInfo.getMetaDataBankId().toString())){
					rqvo.getPayShopNoList().add(shopInfo.getPayShopNo());
				}
			}
			if(StringUtil.isNullOrEmpty(rqvo.getPayShopNoList())){
				rqvo.getPayShopNoList().add("0");
			}
		}
		Sort sort = new Sort(Direction.DESC, "isDifferent");
		PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
		try {
			Page<Reconciliation> recPage = reconciliationService.getRecpage(rqvo, pageable);
			return toWebUIPage(recPage);
		} catch (Exception e) {
			e.printStackTrace();
			return toWebUIPage(null);
		}
	}
	
	@RequestMapping(value = "/detailTwo", method = RequestMethod.GET)
	@ResponseBody
	public WebUiPage<TradeCheckFollow> recTwoDetailQuery(RecQueryVo rqvo) {
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) {
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) {
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!StringUtil.isNullOrEmpty(rqvo.getBankTypeId())){
			List<ShopInfo> list = shopInfoService.getAllShopInfo();
			for(ShopInfo shopInfo : list){
				if(shopInfo.getMetaDataBankId() != null && rqvo.getBankTypeId().equals(shopInfo.getMetaDataBankId().toString())){
					rqvo.getPayShopNoList().add(shopInfo.getPayShopNo());
				}
			}
			if(StringUtil.isNullOrEmpty(rqvo.getPayShopNoList())){
				rqvo.getPayShopNoList().add("0");
			}
		}
		Sort sort = new Sort(Direction.ASC, "tradeTime");
		PageRequest pageable = this.getRequestPageabledWithInitSort(sort);
		try {
//			List<Organization> orgListTemp = userOrganizationPerService.orgTempList(user);
			Page<TradeCheckFollow> recPage = reconciliationService.getTwoRecpage(rqvo, pageable);
			
			return toWebUIPage(recPage);
		} catch (Exception e) {
			e.printStackTrace();
			return toWebUIPage(null);
		}
	}

	/**
	 * 新增处理
	 * @param detail
	 * @param result
	 * @return
	 */
	@Logable( operation = "处理单边账信息")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult add(@RequestParam("detailId") Long detailId, @RequestParam("platformAmount") BigDecimal platformAmount,
			@RequestParam("remarkInfo") String remarkInfo,@RequestParam("handleCode") Integer handleCode) throws BusinessException {
		User user = currentUser();
		ResponseResult res = reconciliationService.edit(detailId, platformAmount, remarkInfo,handleCode,user.getLoginName());
		return res;
	}

	/**
	* @date：2017年4月16日 
	* @Description：查看交易流水
	* @param flowNo
	* @return
	* @throws BusinessException: 返回结果描述
	* @return ResponseResult: 返回值类型
	* @throws
	 */
	@RequestMapping(value = "platlog", method = RequestMethod.GET)
	@ResponseBody
	public ResponseResult getPlatformflowLog(@RequestParam("flowNo") String flowNo) throws BusinessException {
		ResponseResult rs = ResponseResult.success();
		Map<String,Object> map = reconciliationService.getPlatformflowLog(flowNo);
		rs.data(map);
		return rs;
	}
	
	/**
	* @date：2017年4月7日 
	* @Description：导出
	* @param model
	* @param request
	* @return: 返回结果描述
	* @return ModelAndView: 返回值类型
	* @throws
	 */
	@Logable(operation = "导出对账明细")
	@RequestMapping(value="/api/dcExcel",method=RequestMethod.GET) 
	public ModelAndView toDcExcel(RecQueryVo rqvo,ModelMap model, HttpServletRequest request) throws BusinessException{ 
		
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) { 
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<ExcelDecoratedEntry> ops = new ArrayList<ExcelDecoratedEntry>();
		
		ops.add(new ExcelDecoratedEntry("payDateStamName", "账单日期"));
		ops.add(new ExcelDecoratedEntry("orgName", "机构名称"));
		ops.add(new ExcelDecoratedEntry("tradeCodeName", "交易类型"));
		ops.add(new ExcelDecoratedEntry("payBusinessTypeName", "业务类型"));
		ops.add(new ExcelDecoratedEntry("custName", "客户名称"));
		ops.add(new ExcelDecoratedEntry("custIdentifyName", "客户标识"));
		ops.add(new ExcelDecoratedEntry("flowNo", "平台流水号"));
		ops.add(new ExcelDecoratedEntry("thirdAmount", "支付金额(元)","#.##"));
		ops.add(new ExcelDecoratedEntry("payTypeName", "支付类型"));
		ops.add(new ExcelDecoratedEntry("orgAmount", "his支付金额(元)","#.##"));
		ops.add(new ExcelDecoratedEntry("deviceNo", "设备编码"));
		ops.add(new ExcelDecoratedEntry("platformAmount", "平台金额(元)","#.##"));
		ops.add(new ExcelDecoratedEntry("payTermNo", "支付终端号"));
		ops.add(new ExcelDecoratedEntry("payAccount", "支付账号"));
		ops.add(new ExcelDecoratedEntry("orderStateName", "订单状态"));
		ops.add(new ExcelDecoratedEntry("reconciliationDate", "对账时间"));
		ops.add(new ExcelDecoratedEntry("remarkInfo", "备注"));
		Sort sort = new Sort(Direction.DESC, "isDifferent");
		Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
		List<Reconciliation> detailList = reconciliationService.getRecDetailList(rqvo,page);
		ExcelResult viewExcel = new ExcelResult(detailList, ops,rqvo.getStartTime()+"至"+rqvo.getEndTime()+rqvo.getOrgName()+"对账明细",17);
		return new ModelAndView(viewExcel); 
	}
	
	/**
	* @date：2017年4月7日 
	* @Description：导出
	* @param model
	* @param request
	* @return: 返回结果描述
	* @return ModelAndView: 返回值类型
	* @throws
	 */
	@Logable(operation = "导出对账明细")
	@RequestMapping(value="/api/dcTwoExcel",method=RequestMethod.GET) 
	public void toDcTwoExcel(RecQueryVo rqvo,ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException{ 
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd"); 
		if(null != rqvo.getStartTime()) { 
			try {
				rqvo.setStartDate(formatter.parse(rqvo.getStartTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(null != rqvo.getEndTime()) { 
			
			try {
				rqvo.setEndDate(formatter.parse(rqvo.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Sort sort = new Sort(Direction.ASC, "tradeTime");
		Pageable page = this.getPageRequest(CommonConstant.exportPageSize, sort);
		
		response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/octet-stream;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		String fileName = rqvo.getStartTime()+"至"+rqvo.getEndTime()+rqvo.getOrgName()+"对账明细";
		response.addHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"iso-8859-1") + ".xls");
		
		
		ExportExcel ee = new ExportExcel();
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = ee.getSheet(wb, rqvo.getStartTime()+"至"+rqvo.getEndTime()+rqvo.getOrgName()+"对账明细");
		
		//标题样式
		CellStyle cellStyle = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)14, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)0,(short)0,(short)0,(short)0);
		Row titleRow = ee.getRow(sheet, 0, cellStyle, 35);
		
		ee.getCell(titleRow, 0, cellStyle, rqvo.getStartTime()+"至"+rqvo.getEndTime()+rqvo.getOrgName()+"对账明细");
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,7));
		
		//头部样式
		CellStyle cellStyleHead = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		Row headRow = ee.getRow(sheet, 1, null, 20);
		
		ee.getCell(headRow, 0, cellStyleHead, "账单日期" );
		ee.getCell(headRow, 1, cellStyleHead, "渠道名称" );
		ee.getCell(headRow, 2, cellStyleHead, "机构名称" );
		ee.getCell(headRow, 3, cellStyleHead, "患者类型" );
		ee.getCell(headRow, 4, cellStyleHead, "业务类型" );
		ee.getCell(headRow, 5, cellStyleHead, "客户名称" );
		ee.getCell(headRow, 6, cellStyleHead, "对账流水号" );
		ee.getCell(headRow, 7, cellStyleHead, "支付金额(元)" );
		ee.getCell(headRow, 8, cellStyleHead, "支付类型" );
		ee.getCell(headRow, 9, cellStyleHead, "对账时间" );

		CellStyle cellStyleContent = ee.getCellStyle(wb, ee.getFont(wb, "Courier New", (short)12, false, false),CellStyle.ALIGN_CENTER,CellStyle.VERTICAL_CENTER,(short)1,(short)1,(short)1,(short)1);
		@SuppressWarnings("unchecked")
		List<TradeCheckFollow> detailList = reconciliationService.getRecTwoDetailList(rqvo,page);
		if(!StringUtil.isNullOrEmpty(detailList)){
			for(int i=0;i<detailList.size();i++){
				 Row platRow = ee.getRow(sheet, i+2, null, 20);
				 
				 String tradeDate = detailList.get(i).getTradeDate()==null?"":detailList.get(i).getTradeDate().toString();
				 String billSource = detailList.get(i).getBillSource();
				 String orgNo = detailList.get(i).getOrgNo();
				 String patType = detailList.get(i).getPatType();
				 String tradeName = detailList.get(i).getTradeName();
				 String custName = detailList.get(i).getPatientName();
				 String businessNo = detailList.get(i).getBusinessNo();
				 String tradeAmount = String.valueOf(detailList.get(i).getTradeAmount());
				 String payName = detailList.get(i).getPayName();
				 String tradeTime = detailList.get(i).getTradeTime()==null?"-":detailList.get(i).getTradeTime().toString();

				 ee.getCell(platRow, 0, cellStyleContent, tradeDate);
				 ee.getCell(platRow, 1, cellStyleContent, billSource);
				 ee.getCell(platRow, 2, cellStyleContent, orgNo);
				 ee.getCell(platRow, 3, cellStyleContent, patType);
				 ee.getCell(platRow, 4, cellStyleContent, tradeName);
				 ee.getCell(platRow, 5, cellStyleContent, custName);
				 ee.getCell(platRow, 6, cellStyleContent, businessNo);
				 ee.getCell(platRow, 7, cellStyleContent, tradeAmount);
				 ee.getCell(platRow, 8, cellStyleContent, payName);
				 ee.getCell(platRow, 9, cellStyleContent, tradeTime);
			}
		}
		
		sheet.setColumnWidth(0, 5000);
		sheet.setColumnWidth(1, 5000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 5000);
		sheet.setColumnWidth(5, 5000);
		sheet.setColumnWidth(6, 9000);
		sheet.setColumnWidth(7, 5000);
		sheet.setColumnWidth(8, 5000);
		sheet.setColumnWidth(9, 9000);
		
		OutputStream out = response.getOutputStream();
		wb.write(out);
		out.flush();
		out.close();
	}
	
	private String getPayType(){
		String str = null;
		AppRuntimeConfig hConfig = hospitalConfigService.loadConfig();
		if(null != hConfig){
//			str = hConfig.getCheckWays();
		}
		return str;
	}

}
